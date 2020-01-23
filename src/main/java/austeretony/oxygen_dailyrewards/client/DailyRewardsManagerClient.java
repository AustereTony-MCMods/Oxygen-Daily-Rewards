package austeretony.oxygen_dailyrewards.client;

import austeretony.oxygen_dailyrewards.client.test.time.TimeManagerClient;

public class DailyRewardsManagerClient {

    private static DailyRewardsManagerClient instance;

    private final RewardsDataContainerClient rewardsDataContainer;

    private final RewardsPlayerDataClient playerData = new RewardsPlayerDataClient();

    private final RewardsMenuManager menuManager;

    //TODO Move to Core
    private final TimeManagerClient timeManager;

    private DailyRewardsManagerClient() {
        this.rewardsDataContainer = new RewardsDataContainerClient(this);
        this.menuManager = new RewardsMenuManager(this);
        //TODO Move to Core
        this.timeManager = new TimeManagerClient(this);
    }

    public static void create() {
        if (instance == null)
            instance = new DailyRewardsManagerClient();
    }

    public static DailyRewardsManagerClient instance() {
        return instance;
    }

    public RewardsDataContainerClient getRewardsDataContainer() {
        return this.rewardsDataContainer;
    }

    public RewardsPlayerDataClient getPlayerData() {
        return this.playerData;
    }

    public RewardsMenuManager getMenuManager() {
        return this.menuManager;
    }

    //TODO Move to Core
    public TimeManagerClient getTimeManager() {
        return this.timeManager;
    }
}
