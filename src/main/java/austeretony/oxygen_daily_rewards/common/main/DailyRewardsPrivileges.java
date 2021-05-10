package austeretony.oxygen_daily_rewards.common.main;

import austeretony.oxygen_core.common.privileges.PrivilegeRegistry;
import austeretony.oxygen_core.common.util.value.ValueType;

public final class DailyRewardsPrivileges {

    public static final PrivilegeRegistry.Entry DAILY_REWARDS_ACCESS =
            PrivilegeRegistry.register(700, "daily_rewards:daily_rewards_access", ValueType.BOOLEAN);

    private DailyRewardsPrivileges() {}

    public static void register() {}
}
