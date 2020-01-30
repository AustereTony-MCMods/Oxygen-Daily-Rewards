package austeretony.oxygen_dailyrewards.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncPlayerData extends Packet {

    private int daysRewarded;

    private long lastRewardTimeMillis;

    public CPSyncPlayerData() {}

    public CPSyncPlayerData(int daysRewarded, long lastRewardTimeEpochSeconds) {
        this.daysRewarded = daysRewarded;
        this.lastRewardTimeMillis = lastRewardTimeEpochSeconds;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeByte(this.daysRewarded);
        buffer.writeLong(this.lastRewardTimeMillis);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final int daysRewarded = buffer.readByte();
        final long lastRewardTimeMillis = buffer.readLong();
        OxygenHelperClient.addRoutineTask(()->DailyRewardsManagerClient.instance().getPlayerData().init(daysRewarded, lastRewardTimeMillis));
    }
}
