package austeretony.oxygen_dailyrewards.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPSyncRewardsData extends Packet {

    private byte[] compressed;

    public CPSyncRewardsData() {}

    public CPSyncRewardsData(byte[] compressed) {
        this.compressed = compressed;
    }

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {
        buffer.writeBytes(this.compressed);
    }

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final ByteBuf buf = buffer.copy();
        OxygenHelperClient.addRoutineTask(()->DailyRewardsManagerClient.instance().getRewardsDataContainer().rewardsDataReceived(buffer));
    }
}
