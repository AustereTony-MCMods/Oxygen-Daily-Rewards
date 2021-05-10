package austeretony.oxygen_daily_rewards.common.player;

import austeretony.oxygen_core.common.persistent.AbstractPersistentData;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class PlayerData extends AbstractPersistentData {

    private UUID playerUUID;
    private long lastTimeRewardedMillis;
    private int daysRewarded;

    private String filePath;

    public PlayerData() {}

    public PlayerData(UUID playerUUID, String filePath) {
        this.playerUUID = playerUUID;
        this.filePath = filePath;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public long getLastTimeRewardedMillis() {
        return lastTimeRewardedMillis;
    }

    public void setLastTimeRewardedMillis(long value) {
        lastTimeRewardedMillis = value;
    }

    public int getDaysRewarded() {
        return daysRewarded;
    }

    public void setDaysRewarded(int value) {
        daysRewarded = value;
    }

    public void rewarded(long currentTimeMillis) {
        lastTimeRewardedMillis = currentTimeMillis;
        daysRewarded++;
    }

    @Override
    public String getName() {
        return "daily_rewards:player_data";
    }

    @Override
    public String getPath() {
        return filePath;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setLong("last_time_rewarded_millis", lastTimeRewardedMillis);
        tagCompound.setShort("days_rewarded", (short) daysRewarded);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        lastTimeRewardedMillis = tagCompound.getLong("last_time_rewarded_millis");
        daysRewarded = tagCompound.getShort("days_rewarded");
    }

    @Override
    public void reset() {
        lastTimeRewardedMillis = 0L;
        daysRewarded = 0;
    }
}
