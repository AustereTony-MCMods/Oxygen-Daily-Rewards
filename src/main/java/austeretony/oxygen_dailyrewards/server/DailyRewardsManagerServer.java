package austeretony.oxygen_dailyrewards.server;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_dailyrewards.server.test.time.TimeHelperServer;
import austeretony.oxygen_dailyrewards.server.test.time.TimeManagerServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class DailyRewardsManagerServer {

    private static DailyRewardsManagerServer instance;

    private final RewardsDataContainerServer rewardsDataContainer;

    private final RewardsPlayerDataContainerServer playerDataContainer; 

    private final PlayersDataManagerServer playerDataManager;

    //TODO Move to Core
    private final TimeManagerServer timeManager;

    private int currentMonth;

    private DailyRewardsManagerServer() {      
        //TODO Move to Core
        this.timeManager = new TimeManagerServer(this);

        this.currentMonth = this.timeManager.getZonedDateTime().getMonthValue();

        this.rewardsDataContainer = new RewardsDataContainerServer(this);
        this.playerDataContainer = new RewardsPlayerDataContainerServer(this);
        this.playerDataManager = new PlayersDataManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(()->this.playerDataContainer.save());
    }

    private void scheduleRepeatableProcesses() {
        OxygenHelperServer.getSchedulerExecutorService().scheduleAtFixedRate(
                ()->this.updateRewards(), 1L, 1L, TimeUnit.HOURS);
    }

    private void updateRewards() {
        int month = TimeHelperServer.getZonedDateTime().getMonthValue();
        if (this.currentMonth != month) {
            this.rewardsDataContainer.loadRewardsData();
            OxygenHelperServer.getOnlinePlayersUUIDs().forEach(
                    (playerUUID)->this.rewardsDataContainer.syncRewardsData(CommonReference.playerByUUID(playerUUID)));
        }
        this.currentMonth = month;
    }

    public static void create() {
        if (instance == null) {
            instance = new DailyRewardsManagerServer();
            instance.registerPersistentData();
            instance.scheduleRepeatableProcesses();
        }
    }

    public static DailyRewardsManagerServer instance() {
        return instance;
    }

    public int getCurrentMonth() {
        return this.currentMonth;
    }

    public RewardsDataContainerServer getRewardsDataContainer() {
        return this.rewardsDataContainer;
    }

    public RewardsPlayerDataContainerServer getPlayerDataContainer() {
        return this.playerDataContainer;
    }

    public PlayersDataManagerServer getPlayerDataManager() {
        return this.playerDataManager;
    }

    //TODO Move to Core
    public TimeManagerServer getTimeManager() {
        return this.timeManager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {
        this.rewardsDataContainer.syncRewardsData(playerMP);
        this.playerDataManager.onPlayerLoaded(playerMP);
    }
}
