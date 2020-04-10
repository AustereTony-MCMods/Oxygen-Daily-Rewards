package austeretony.oxygen_dailyrewards.common.main;

import austeretony.oxygen_core.common.EnumValueType;
import austeretony.oxygen_core.common.privilege.PrivilegeRegistry;

public enum EnumDailyRewardsPrivilege {

    DAILY_REWARDS_ACCESS("dailyrewards:dailyRewardsAccess", 1400, EnumValueType.BOOLEAN),
    MAXIMUM_REWARDS_AMOUNT_PER_MONTH("dailyrewards:maximumRewardsAmountPerMonth", 1401, EnumValueType.INT);

    private final String name;

    private final int id;

    private final EnumValueType type;

    EnumDailyRewardsPrivilege(String name, int id, EnumValueType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public int id() {
        return id;
    }

    public static void register() {
        for (EnumDailyRewardsPrivilege privilege : values())
            PrivilegeRegistry.registerPrivilege(privilege.name, privilege.id, privilege.type);
    }
}
