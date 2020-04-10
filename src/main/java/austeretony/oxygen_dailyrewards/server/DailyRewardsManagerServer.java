package austeretony.oxygen_dailyrewards.server;

import java.time.Duration;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.TimeHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import net.minecraft.entity.player.EntityPlayerMP;

public class DailyRewardsManagerServer {

    private static DailyRewardsManagerServer instance;

    private final RewardsDataContainerServer rewardsDataContainer;

    private final RewardsPlayerDataContainerServer playerDataContainer; 

    private final PlayersDataManagerServer playerDataManager;

    private DailyRewardsManagerServer() {      
        this.rewardsDataContainer = new RewardsDataContainerServer(this);
        this.playerDataContainer = new RewardsPlayerDataContainerServer(this);
        this.playerDataManager = new PlayersDataManagerServer(this);
    }

    private void registerPersistentData() {
        OxygenHelperServer.registerPersistentData(this.playerDataContainer::save);
    }

    private void scheduleRepeatableProcesses() {
        //scheduling rewards reloading when new month starts
        ZonedDateTime 
        currentTime = TimeHelperServer.getZonedDateTime(),
        reloadingTime = currentTime.withDayOfMonth(1).withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()).withMinute(0).withSecond(0).withNano(0);

        if (currentTime.compareTo(reloadingTime) > 0)
            reloadingTime = reloadingTime.plusMonths(1L);

        Month nextMonth = reloadingTime.getMonth();
        int nextMonthLength = reloadingTime.toLocalDate().isLeapYear() ? nextMonth.maxLength() : nextMonth.minLength();

        long initalDelay = Duration.between(currentTime, reloadingTime).getSeconds();

        OxygenHelperServer.getSchedulerExecutorService().scheduleAtFixedRate(
                this.rewardsDataContainer::reloadRewards, 
                initalDelay, 
                TimeUnit.DAYS.toSeconds(nextMonthLength), 
                TimeUnit.SECONDS);

        OxygenMain.LOGGER.info("[Daily Rewards] Scheduled rewards reloading for <{}> at: {}", 
                nextMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                OxygenMain.DEBUG_DATE_TIME_FORMATTER.format(reloadingTime));
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

    public RewardsDataContainerServer getRewardsDataContainer() {
        return this.rewardsDataContainer;
    }

    public RewardsPlayerDataContainerServer getPlayerDataContainer() {
        return this.playerDataContainer;
    }

    public PlayersDataManagerServer getPlayerDataManager() {
        return this.playerDataManager;
    }

    public void worldLoaded() {
        OxygenHelperServer.addIOTask(this.rewardsDataContainer::loadRewardsData);
    }

    public void playerLoaded(EntityPlayerMP playerMP) {
        this.rewardsDataContainer.syncRewardsData(playerMP);
        this.playerDataManager.playerLoaded(playerMP);
    }
}
