package austeretony.oxygen_dailyrewards.client.test.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;

public class TimeHelperClient {

    //TODO Move to Core

    //client

    public static ZoneId getZoneId() {
        return DailyRewardsManagerClient.instance().getTimeManager().getZoneId();
    }

    public static Clock getClock() {
        return DailyRewardsManagerClient.instance().getTimeManager().getClock();
    }

    public static long getCurrentMillis() {
        return DailyRewardsManagerClient.instance().getTimeManager().getClock().millis();
    }

    public static Instant getInstant() {
        return DailyRewardsManagerClient.instance().getTimeManager().getInstant();
    }

    public static ZonedDateTime getZonedDateTime() {
        return DailyRewardsManagerClient.instance().getTimeManager().getZonedDateTime();
    }

    //server

    public static ZoneId getServerZoneId() {
        return DailyRewardsManagerClient.instance().getTimeManager().getServerZoneId();
    }

    public static ZonedDateTime getServerZonedDateTime() {
        return DailyRewardsManagerClient.instance().getTimeManager().getServerZonedDateTime();
    }
}
