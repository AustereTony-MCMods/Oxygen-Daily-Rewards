package austeretony.oxygen_daily_rewards.client.settings;

import austeretony.oxygen_core.client.api.OxygenClient;
import austeretony.oxygen_core.client.gui.base.Alignment;
import austeretony.oxygen_core.client.settings.SettingType;
import austeretony.oxygen_core.client.settings.SettingValue;
import austeretony.oxygen_core.client.settings.gui.SettingWidgets;
import austeretony.oxygen_core.common.util.value.ValueType;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;

public final class DailyRewardsSettings {

    public static final SettingValue
    DAILY_REWARDS_SCREEN_ALIGNMENT = OxygenClient.registerSetting(DailyRewardsMain.MOD_ID, SettingType.INTERFACE, "Daily Rewards", "alignment",
            ValueType.STRING, "daily_rewards_screen_alignment", Alignment.CENTER.toString(), SettingWidgets.screenAlignmentList()),

    ADD_DAILY_REWARDS_SCREEN_TO_OXYGEN_MENU = OxygenClient.registerSetting(DailyRewardsMain.MOD_ID, SettingType.COMMON, "Daily Rewards", "oxygen_menu",
            ValueType.BOOLEAN, "add_daily_rewards_screen", true, SettingWidgets.checkBox());

    private DailyRewardsSettings() {}

    public static void register() {}
}
