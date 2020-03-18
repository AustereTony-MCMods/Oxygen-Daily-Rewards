package austeretony.oxygen_dailyrewards.client.gui.menu;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_dailyrewards.client.RewardsMenuManager;
import austeretony.oxygen_dailyrewards.client.settings.EnumDailyRewardsClientSettings;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsPrivilege;

public class DailyRewardsMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return DailyRewardsMain.DAILY_REWARDS_MENU_SCREEN_ID;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.menu.title");
    }

    @Override
    public int getKeyCode() {
        return DailyRewardsConfig.DAILY_REWARDS_MENU_KEY.asInt();
    }

    @Override
    public boolean isValid() {
        return PrivilegesProviderClient.getAsBoolean(EnumDailyRewardsPrivilege.DAILY_REWARDS_ACCESS.id(), DailyRewardsConfig.DAILY_REWARDS_ACCESS.asBoolean()) 
                && EnumDailyRewardsClientSettings.ADD_DAILY_REWARDS_MENU.get().asBoolean();
    }

    @Override
    public void open() {
        RewardsMenuManager.openDailyRewardsMenu();
    }
}
