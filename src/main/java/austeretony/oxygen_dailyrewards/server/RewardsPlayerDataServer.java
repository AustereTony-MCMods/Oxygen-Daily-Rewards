package austeretony.oxygen_dailyrewards.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.UUID;

import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import austeretony.oxygen_core.common.util.StreamUtils;
import austeretony.oxygen_core.server.api.PrivilegesProviderServer;
import austeretony.oxygen_core.server.api.TimeHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsPrivilege;
import io.netty.buffer.ByteBuf;

public class RewardsPlayerDataServer extends AbstractPersistentData {

    private int rewardedDaysSeries, daysRewarded;

    private long lastRewardTimeMillis;

    public final String dataPath;

    public RewardsPlayerDataServer(String dataPath) {
        this.dataPath = dataPath;
    }

    public boolean isRewardAvailable(UUID playerUUID) {
        ZonedDateTime 
        currentTime = TimeHelperServer.getZonedDateTime(),
        lastTimePlayerRewarded = TimeHelperServer.getZonedDateTime(this.lastRewardTimeMillis),
        nextRewardTime = lastTimePlayerRewarded.plusDays(1L).withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()).withMinute(0).withSecond(0);

        if (Period.between(lastTimePlayerRewarded.toLocalDate(), currentTime.toLocalDate()).getDays() >= 2)
            this.rewardedDaysSeries = 0;

        boolean resetRewards = DailyRewardsConfig.REWARD_MODE.asInt() == 0 ? lastTimePlayerRewarded.getMonthValue() != currentTime.getMonthValue() : this.daysRewarded >= DailyRewardsManagerServer.instance().getRewardsDataContainer().getRewards().size();

        if (this.lastRewardTimeMillis != 0L 
                && resetRewards
                && currentTime.getHour() >= DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt())
            this.daysRewarded = 0;

        int maximumRewards = PrivilegesProviderServer.getAsInt(playerUUID, EnumDailyRewardsPrivilege.MAXIMUM_REWARDS_AMOUNT_PER_MONTH.id(), DailyRewardsConfig.MAXIMUM_REWARDS_PER_MONTH.asInt());
        if (!PrivilegesProviderServer.getAsBoolean(playerUUID, EnumDailyRewardsPrivilege.DAILY_REWARDS_ACCESS.id(), DailyRewardsConfig.DAILY_REWARDS_ACCESS.asBoolean())
                || this.daysRewarded >= maximumRewards)
            return false;

        return currentTime.compareTo(nextRewardTime) > 0;
    }

    public void rewarded() {
        this.rewardedDaysSeries++;
        this.daysRewarded++;
        this.lastRewardTimeMillis = TimeHelperServer.getCurrentMillis();
    }

    public int getRewardedDaysSeries() {
        return this.rewardedDaysSeries;
    }

    public void setRewardedDaysSeries(int value) {
        this.rewardedDaysSeries = value;
    }

    public int getDaysRewarded() {
        return this.daysRewarded;
    }

    public void setDaysRewarded(int value) {
        this.daysRewarded = value;
    }

    public long getLastRewardTimeMillis() {
        return this.lastRewardTimeMillis;
    }

    public void setLastRewardTimeMillis(long millis) {
        this.lastRewardTimeMillis = millis;
    }

    @Override
    public String getDisplayName() {
        return "dailyrewards:player_data_server";
    }

    @Override
    public String getPath() {
        return this.dataPath;
    }

    @Override
    public void write(BufferedOutputStream bos) throws IOException {
        StreamUtils.write((short) this.rewardedDaysSeries, bos);
        StreamUtils.write((byte) 1, bos);//placeholder (if multitab rewards will be implemented it will provide compatibility)
        StreamUtils.write((byte) 0, bos);//placeholder
        StreamUtils.write((byte) this.daysRewarded, bos);
        StreamUtils.write(this.lastRewardTimeMillis, bos);
    }

    @Override
    public void read(BufferedInputStream bis) throws IOException {
        this.rewardedDaysSeries = StreamUtils.readShort(bis);
        int dummy;
        dummy = StreamUtils.readByte(bis);//placeholder
        dummy = StreamUtils.readByte(bis);//placeholder
        this.daysRewarded = StreamUtils.readByte(bis);
        this.lastRewardTimeMillis = StreamUtils.readLong(bis);
    }

    public void write(ByteBuf buffer) {
        buffer.writeShort(this.rewardedDaysSeries);
        buffer.writeByte(this.daysRewarded);
        buffer.writeLong(this.lastRewardTimeMillis);
    }

    @Override
    public void reset() {
        this.rewardedDaysSeries = 0;
        this.daysRewarded = 0;
        this.lastRewardTimeMillis = 0L;
    }
}
