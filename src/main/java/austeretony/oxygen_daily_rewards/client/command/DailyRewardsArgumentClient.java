package austeretony.oxygen_daily_rewards.client.command;

import austeretony.oxygen_core.client.api.OxygenClient;
import austeretony.oxygen_core.common.command.CommandArgument;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class DailyRewardsArgumentClient implements CommandArgument {

    @Override
    public String getName() {
        return "daily-rewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            OxygenClient.openScreenWithDelay(DailyRewardsMain.SCREEN_ID_DAILY_REWARDS);
        }
    }
}
