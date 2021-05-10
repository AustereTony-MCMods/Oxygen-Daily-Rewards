package austeretony.oxygen_daily_rewards.common.network.client;

import austeretony.oxygen_core.client.util.MinecraftClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_daily_rewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_daily_rewards.client.gui.daily_rewards.DailyRewardsScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPRewardClaimed extends Packet {

    private long lastRewardMillis;
    private int daysRewarded;

    public CPRewardClaimed() {}

    public CPRewardClaimed(long lastRewardMillis, int daysRewarded) {
        this.lastRewardMillis = lastRewardMillis;
        this.daysRewarded = daysRewarded;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeLong(lastRewardMillis);
        buffer.writeShort(daysRewarded);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final long lastRewardMillis = buffer.readLong();
        final int daysRewarded = buffer.readShort();
        MinecraftClient.delegateToClientThread(() -> {
            DailyRewardsManagerClient.instance().playerDataSynchronized(lastRewardMillis, daysRewarded);
            DailyRewardsScreen.rewardClaimed();
        });
    }
}
