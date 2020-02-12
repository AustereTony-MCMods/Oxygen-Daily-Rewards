package austeretony.oxygen_dailyrewards.client;

import java.time.Duration;
import java.time.ZonedDateTime;

import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.api.TimeHelperClient;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsPrivilege;

public class RewardsPlayerDataClient {

    private int daysRewarded;

    private long lastRewardTimeMillis;

    public int getDaysRewarded() {
        return this.daysRewarded;
    }

    public long getLastRewardTimeMillis() {
        return this.lastRewardTimeMillis;
    }

    public void init(int daysRewarded, long lastRewardTimeMillis) {
        this.daysRewarded = daysRewarded;
        this.lastRewardTimeMillis = lastRewardTimeMillis;

        ZonedDateTime serverRewardTime = TimeHelperClient.getServerZonedDateTime(this.lastRewardTimeMillis);
        OxygenMain.LOGGER.info("[Daily Rewards] Player data synchronized - days rewarded this month: {}, last reward time: {}",  
                this.daysRewarded, 
                OxygenMain.DEBUG_DATE_TIME_FORMATTER.format(serverRewardTime));
    }

    public boolean isRewardAvailable() {      
        ZonedDateTime 
        currentServerTime = TimeHelperClient.getServerZonedDateTime(),
        lastTimePlayerRewarded = TimeHelperClient.getServerZonedDateTime(this.lastRewardTimeMillis),
        nextRewardTime = lastTimePlayerRewarded.plusDays(1L).withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()).withMinute(0).withSecond(0);

        if (lastTimePlayerRewarded.getMonthValue() != currentServerTime.getMonthValue()
                && currentServerTime.getHour() >= DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt())
            this.daysRewarded = 0;

        int maximumRewards = PrivilegesProviderClient.getAsInt(EnumDailyRewardsPrivilege.MAXIMUM_REWARDS_AMOUNT.id(), DailyRewardsConfig.MAXIMUM_REWARDS_AMOUNT.asInt());
        if (!PrivilegesProviderClient.getAsBoolean(EnumDailyRewardsPrivilege.DAILY_REWARDS_ACCESS.id(), true)
                || (maximumRewards != - 1 && this.daysRewarded >= maximumRewards))
            return false;

        return currentServerTime.compareTo(nextRewardTime) > 0;
    }

    public Duration getTimeLeftUntilNextReward() {
        ZonedDateTime 
        currentServerTime = TimeHelperClient.getServerZonedDateTime(),
        lastTimePlayerRewarded = TimeHelperClient.getServerZonedDateTime(this.lastRewardTimeMillis),
        nextRewardTime = lastTimePlayerRewarded.plusDays(1L).withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()).withMinute(0).withSecond(0);

        return Duration.between(currentServerTime, nextRewardTime);
    }
}
