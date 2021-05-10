package austeretony.oxygen_daily_rewards.common.network.server;

import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_core.common.util.MinecraftCommon;
import austeretony.oxygen_core.server.api.OxygenServer;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import austeretony.oxygen_daily_rewards.server.DailyRewardsManagerServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;

public class SPClaimReward extends Packet {

    public SPClaimReward() {}

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {}

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        final EntityPlayerMP playerMP = getEntityPlayerMP(netHandler);
        if (OxygenServer.isNetworkRequestAvailable(DailyRewardsMain.NET_REQUEST_CLAIM_REWARD,
                MinecraftCommon.getEntityUUID(playerMP))) {
            OxygenServer.addTask(() -> DailyRewardsManagerServer.instance().claimReward(playerMP));
        }
    }
}
