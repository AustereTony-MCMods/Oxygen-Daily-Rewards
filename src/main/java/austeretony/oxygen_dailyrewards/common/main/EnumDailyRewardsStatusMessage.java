package austeretony.oxygen_dailyrewards.common.main;

import austeretony.oxygen_core.client.api.ClientReference;

public enum EnumDailyRewardsStatusMessage {

    DAILY_REWARD_AVAILABLE("dailyRewardAvailable"),
    DAILY_REWARD_CLAIMED("dailyRewardClaimed"),
    DAILY_REWARD_UNAVAILABLE("dailyRewardUnavailable");

    private final String status;

    EnumDailyRewardsStatusMessage(String status) {
        this.status = "oxygen_dailyrewards.status." + status;
    }

    public String localized() {
        return ClientReference.localize(this.status);
    }
}
