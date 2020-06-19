package austeretony.oxygen_dailyrewards.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;

public class DailyRewardsConfig extends AbstractConfig {

    public static final ConfigValue
    DAILY_REWARDS_MENU_KEY = ConfigValueUtils.getValue("client", "daily_rewards_menu_key", 51),

    REWARD_MODE = ConfigValueUtils.getValue("server", "reward_mode", 0, true),
    REWARD_TIME_OFFSET_HOURS = ConfigValueUtils.getValue("server", "reward_time_offset_hours", 0, true),
    DAILY_REWARDS_ACCESS = ConfigValueUtils.getValue("server", "daily_reards_access", true, true),
    MAXIMUM_REWARDS_PER_MONTH = ConfigValueUtils.getValue("server", "maximum_rewards_per_month", 31, true),
    DEBUG_SCRIPTS = ConfigValueUtils.getValue("server", "debug_scripts", false),
    ADVANCED_LOGGING = ConfigValueUtils.getValue("server", "advanced_logging", false);

    @Override 
    public String getDomain() {
        return DailyRewardsMain.MODID;
    }

    @Override
    public String getVersion() {
        return DailyRewardsMain.VERSION_CUSTOM;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/daily-rewards.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(DAILY_REWARDS_MENU_KEY);

        values.add(REWARD_MODE);
        values.add(REWARD_TIME_OFFSET_HOURS);
        values.add(DAILY_REWARDS_ACCESS);
        values.add(MAXIMUM_REWARDS_PER_MONTH);
        values.add(DEBUG_SCRIPTS);
        values.add(ADVANCED_LOGGING);
    }
}
