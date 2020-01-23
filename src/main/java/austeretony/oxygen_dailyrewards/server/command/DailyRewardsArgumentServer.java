package austeretony.oxygen_dailyrewards.server.command;

import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_dailyrewards.server.DailyRewardsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class DailyRewardsArgumentServer implements ArgumentExecutor {

    @Override
    public String getName() {
        return "dailyrewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            if (args[1].equals("-claim"))
                DailyRewardsManagerServer.instance().getPlayerDataManager().claimReward(CommandBase.getCommandSenderAsPlayer(sender));
        }
    }
}
