package austeretony.oxygen_dailyrewards.common.network.client;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.network.Packet;
import austeretony.oxygen_dailyrewards.client.RewardsMenuManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public class CPOpenDailyRewardsMenu extends Packet {

    public CPOpenDailyRewardsMenu() {}

    @Override
    public void write(ByteBuf buffer, INetHandler netHandler) {}

    @Override
    public void read(ByteBuf buffer, INetHandler netHandler) {
        OxygenHelperClient.addRoutineTask(RewardsMenuManager::openDailyRewardsMenuDelegated);
    }
}
