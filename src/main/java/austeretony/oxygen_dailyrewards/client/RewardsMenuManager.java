package austeretony.oxygen_dailyrewards.client;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_dailyrewards.client.gui.rewards.DailyRewardsMenuScreen;

public class RewardsMenuManager {

    private final DailyRewardsManagerClient manager;

    protected RewardsMenuManager(DailyRewardsManagerClient manager) {
        this.manager = manager;
    }

    public void openDailyRewardsMenu() {
        ClientReference.displayGuiScreen(new DailyRewardsMenuScreen());
    }

    public void rewardClaimed() {
        ClientReference.delegateToClientThread(()->{
            if (isDailyRewardsMenuOpened())
                ((DailyRewardsMenuScreen) ClientReference.getCurrentScreen()).rewardClaimed();
        });
    }

    public static boolean isDailyRewardsMenuOpened() {
        return ClientReference.hasActiveGUI() && ClientReference.getCurrentScreen() instanceof DailyRewardsMenuScreen;
    }
}
