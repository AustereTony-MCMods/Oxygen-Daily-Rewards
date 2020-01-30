package austeretony.oxygen_dailyrewards.client.gui.menu;

import org.lwjgl.input.Keyboard;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_dailyrewards.client.settings.EnumDailyRewardsClientSettings;

public class DailyRewardsMenuEntry implements OxygenMenuEntry {

    @Override
    public int getId() {
        return 140;
    }

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.menu.title");
    }

    @Override
    public int getKeyCode() {
        return Keyboard.KEY_R;
    }

    @Override
    public boolean isValid() {
        return EnumDailyRewardsClientSettings.ADD_DAILY_REWARDS_MENU.get().asBoolean();
    }

    @Override
    public void open() {
        DailyRewardsManagerClient.instance().getMenuManager().openDailyRewardsMenu();
    }
}
