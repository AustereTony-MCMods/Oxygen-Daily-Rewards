package austeretony.oxygen_daily_rewards.common.config;

import austeretony.oxygen_core.common.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class DailyRewardsConfig extends AbstractConfig {

    public static final ConfigValue
            ENABLE_DAILY_REWARDS_SCREEN_KEY = ConfigValueUtils.getBoolean("client", "enable_daily_rewards_screen_key", true),
            DAILY_REWARDS_SCREEN_KEY = ConfigValueUtils.getInt("client", "daily_rewards_screen_key", Keyboard.KEY_COMMA),

    REWARD_TIME_OFFSET_HOURS = ConfigValueUtils.getInt("server", "reward_time_offset_hours", 0, true);

    @Override
    public String getDomain() {
        return DailyRewardsMain.MOD_ID;
    }

    @Override
    public String getVersion() {
        return DailyRewardsMain.VERSION_CUSTOM;
    }

    @Override
    public String getFileName() {
        return "daily_rewards.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(ENABLE_DAILY_REWARDS_SCREEN_KEY);
        values.add(DAILY_REWARDS_SCREEN_KEY);

        values.add(REWARD_TIME_OFFSET_HOURS);
    }
}
