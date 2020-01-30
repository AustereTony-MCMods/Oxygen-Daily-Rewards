package austeretony.oxygen_dailyrewards.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;

public class DailyRewardsConfig extends AbstractConfig {

    public static final ConfigValue
    REWARD_TIME_OFFSET_HOURS = ConfigValueUtils.getValue("server", "reward_time_offset_hours", 0, true),
    MAXIMUM_REWARDS_AMOUNT = ConfigValueUtils.getValue("server", "maximum_rewards_amount_per_month", - 1, true);

    @Override
    public String getDomain() {
        return DailyRewardsMain.MODID;
    }

    @Override
    public String getExternalPath() {
        return CommonReference.getGameFolder() + "/config/oxygen/daily-rewards.json";
    }

    @Override
    public void getValues(List<ConfigValue> values) {
        values.add(REWARD_TIME_OFFSET_HOURS);
        values.add(MAXIMUM_REWARDS_AMOUNT);
    }
}
