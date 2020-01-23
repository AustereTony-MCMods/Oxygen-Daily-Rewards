package austeretony.oxygen_dailyrewards.server.test.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import austeretony.oxygen_dailyrewards.server.DailyRewardsManagerServer;

public class TimeHelperServer {

    //TODO Move to Core

    public static ZoneId getZoneId() {
        return DailyRewardsManagerServer.instance().getTimeManager().getZoneId();
    }

    public static Clock getClock() {
        return DailyRewardsManagerServer.instance().getTimeManager().getClock();
    }

    public static long getCurrentMillis() {
        return DailyRewardsManagerServer.instance().getTimeManager().getClock().millis();
    }

    public static Instant getInstant() {
        return DailyRewardsManagerServer.instance().getTimeManager().getInstant();
    }

    public static ZonedDateTime getZonedDateTime() {
        return DailyRewardsManagerServer.instance().getTimeManager().getZonedDateTime();
    }
}
