package austeretony.oxygen_dailyrewards.client.test.time;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;

public class TimeManagerClient {

    private final DailyRewardsManagerClient manager;

    private final DateTimeFormatter dateTimeFormatter;

    private final ZoneId zoneId;

    private final Clock clock;

    private ZoneId serverZoneId;

    public TimeManagerClient(DailyRewardsManagerClient manager) {
        this.manager = manager;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(DailyRewardsConfig.DATE_TIME_FORMATTER_PATTERN.asString());
        this.zoneId = initZoneId();
        this.clock = Clock.system(this.zoneId);
    }

    private static ZoneId initZoneId() {
        ZoneId zoneId = ZoneId.systemDefault();
        if (!DailyRewardsConfig.CLIENT_REGION_ID.asString().isEmpty()) {
            try {
                zoneId = ZoneId.of(DailyRewardsConfig.CLIENT_REGION_ID.asString());
            } catch (DateTimeException exception) {
                OxygenMain.LOGGER.error("Client ZoneId parse failure! System default ZoneId will be used.", exception);
            }
        }
        return zoneId;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return this.dateTimeFormatter;
    }

    public ZoneId getZoneId() {
        return this.zoneId;
    }

    public Clock getClock() {
        return this.clock;
    }

    public Instant getInstant() {
        return this.clock.instant();
    }

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(this.clock);
    }

    public void initServerTime(String serverRegionId) {
        this.serverZoneId = ZoneId.of(serverRegionId);//an exception just impossible

        DailyRewardsMain.LOGGER.info("Server zone-time data: {}", DailyRewardsMain.DEBUG_DATE_TIME_FORMATTER.format(this.getServerZonedDateTime()));
    }

    public ZoneId getServerZoneId() {
        return this.serverZoneId;
    }

    public ZonedDateTime getServerZonedDateTime() {
        return ZonedDateTime.now(this.serverZoneId);
    }
}
