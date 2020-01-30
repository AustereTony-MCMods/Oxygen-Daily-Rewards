package austeretony.oxygen_dailyrewards.client;

public class DailyRewardsManagerClient {

    private static DailyRewardsManagerClient instance;

    private final RewardsDataContainerClient rewardsDataContainer;

    private final RewardsPlayerDataClient playerData = new RewardsPlayerDataClient();

    private final RewardsMenuManager menuManager;

    private DailyRewardsManagerClient() {
        this.rewardsDataContainer = new RewardsDataContainerClient(this);
        this.menuManager = new RewardsMenuManager(this);
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
}
