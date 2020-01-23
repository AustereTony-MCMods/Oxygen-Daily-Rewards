package austeretony.oxygen_dailyrewards.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncPlayerData extends Packet {

    //TODO Move to Core to base info packet
    private String serverRegionId;

    private int daysRewarded;

    private long lastRewardTimeMillis;

    public CPSyncPlayerData() {}

    public CPSyncPlayerData(String serverRegionId, int daysRewarded, long lastRewardTimeEpochSeconds) {
        this.serverRegionId = serverRegionId;
        this.daysRewarded = daysRewarded;
        this.lastRewardTimeMillis = lastRewardTimeEpochSeconds;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        ByteBufUtils.writeString(this.serverRegionId, buffer);//TODO Move to Core to base info packet
        buffer.writeByte(this.daysRewarded);
        buffer.writeLong(this.lastRewardTimeMillis);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final String serverRegionId = ByteBufUtils.readString(buffer);//TODO Move to Core to base info packet
        final int daysRewarded = buffer.readByte();
        final long lastRewardTimeMillis = buffer.readLong();
        OxygenHelperClient.addRoutineTask(()->{
            DailyRewardsManagerClient.instance().getTimeManager().initServerTime(serverRegionId);
            DailyRewardsManagerClient.instance().getPlayerData().init(daysRewarded, lastRewardTimeMillis);
        });
    }
}
