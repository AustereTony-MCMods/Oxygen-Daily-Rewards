package austeretony.oxygen_daily_rewards.common.network.client;

import austeretony.oxygen_core.client.util.MinecraftClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_daily_rewards.client.DailyRewardsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncRewards extends Packet {

    private byte[] rewardsRaw;

    public CPSyncRewards() {}

    public CPSyncRewards(byte[] rewardsRaw) {
        this.rewardsRaw = rewardsRaw;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeInt(rewardsRaw.length);
        buffer.writeBytes(rewardsRaw);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final byte[] rewardsRaw = new byte[buffer.readInt()];
        buffer.readBytes(rewardsRaw);
        MinecraftClient.delegateToClientThread(() -> DailyRewardsManagerClient.instance().rewardsSynchronized(rewardsRaw));
    }
}
