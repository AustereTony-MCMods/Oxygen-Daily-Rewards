package austeretony.oxygen_daily_rewards.server.command;

import austeretony.oxygen_core.common.command.CommandArgument;
import austeretony.oxygen_core.common.util.MinecraftCommon;
import austeretony.oxygen_core.server.api.OxygenServer;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import austeretony.oxygen_daily_rewards.server.DailyRewardsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class DailyRewardsArgumentServer implements CommandArgument {

    @Override
    public String getName() {
        return "daily-rewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            if (args[1].equals("claim")) {
                EntityPlayerMP playerMP = CommandBase.getCommandSenderAsPlayer(sender);
                if (OxygenServer.isNetworkRequestAvailable(DailyRewardsMain.NET_REQUEST_CLAIM_REWARD,
                        MinecraftCommon.getEntityUUID(playerMP))) {
                    OxygenServer.addTask(() -> DailyRewardsManagerServer.instance().claimReward(playerMP));
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "claim");
        }
        return Collections.emptyList();
    }
}
