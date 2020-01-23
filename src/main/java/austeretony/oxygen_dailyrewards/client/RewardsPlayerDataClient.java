package austeretony.oxygen_dailyrewards.client;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.ZonedDateTime;

import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_dailyrewards.client.test.time.TimeHelperClient;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
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

        //TODO DEBUG
        ZonedDateTime serverRewardTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.lastRewardTimeMillis), TimeHelperClient.getServerZoneId());
        DailyRewardsMain.LOGGER.info("Player data synchronized - days rewarded this month: {}, last reward time: {}",  
                this.daysRewarded, 
                DailyRewardsMain.DEBUG_DATE_TIME_FORMATTER.format(serverRewardTime));
    }

    public boolean isRewardAvailable() {        
        int maximumRewards = PrivilegesProviderClient.getAsInt(EnumDailyRewardsPrivilege.MAXIMUM_REWARDS_AMOUNT.id(), DailyRewardsConfig.MAXIMUM_REWARDS_AMOUNT.asInt());
        if (!PrivilegesProviderClient.getAsBoolean(EnumDailyRewardsPrivilege.DAILY_REWARDS_ACCESS.id(), true)
                || (maximumRewards != - 1 && this.daysRewarded >= maximumRewards))
            return false;

        ZonedDateTime 
        currentServerTime = TimeHelperClient.getServerZonedDateTime(),
        lastTimePlayerRewarded = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.lastRewardTimeMillis), TimeHelperClient.getServerZoneId());

        int days = Period.between(lastTimePlayerRewarded.toLocalDate(), currentServerTime.toLocalDate()).getDays();
        if (days == 1)
            return currentServerTime.getHour() >= DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt();
            return days > 1;
    }

    public Duration getTimeLeftUntilNextReward() {
        ZonedDateTime 
        currentServerTime = TimeHelperClient.getServerZonedDateTime(),
        lastTimePlayerRewarded = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.lastRewardTimeMillis), TimeHelperClient.getServerZoneId());

        ZonedDateTime nextRewardTime = ZonedDateTime.of(currentServerTime.getYear(), currentServerTime.getMonthValue(), currentServerTime.getDayOfMonth(), DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt(), 0, 0, 0, TimeHelperClient.getServerZoneId());

        if (lastTimePlayerRewarded.isBefore(nextRewardTime))
            return Duration.between(currentServerTime, nextRewardTime);
        else
            return Duration.between(currentServerTime, nextRewardTime.plusDays(1L));
    }
}
