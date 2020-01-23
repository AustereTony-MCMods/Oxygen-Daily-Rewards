package austeretony.oxygen_dailyrewards.common.config;

import java.util.List;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.config.AbstractConfig;
import austeretony.oxygen_core.common.config.ConfigValue;
import austeretony.oxygen_core.common.config.ConfigValueUtils;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;

public class DailyRewardsConfig extends AbstractConfig {

    public static final ConfigValue
    CLIENT_REGION_ID = ConfigValueUtils.getValue("client", "region_id", ""),//TODO Move to Core
    DATE_TIME_FORMATTER_PATTERN = ConfigValueUtils.getValue("client", "date_time_formatter_pattern", "d MM yyyy"),//TODO Move to Core

    SERVER_REGION_ID = ConfigValueUtils.getValue("server", "region_id", ""),//TODO Move to Core
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
        values.add(CLIENT_REGION_ID);
        values.add(DATE_TIME_FORMATTER_PATTERN);

        values.add(SERVER_REGION_ID);
        values.add(REWARD_TIME_OFFSET_HOURS);
        values.add(MAXIMUM_REWARDS_AMOUNT);
    }
}
