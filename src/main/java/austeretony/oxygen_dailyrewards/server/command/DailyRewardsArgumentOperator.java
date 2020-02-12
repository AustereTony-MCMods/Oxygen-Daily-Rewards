package austeretony.oxygen_dailyrewards.server.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonObject;

import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.JsonUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;
import austeretony.oxygen_dailyrewards.server.DailyRewardsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class DailyRewardsArgumentOperator implements ArgumentExecutor {

    @Override
    public String getName() {
        return "dailyrewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 2) {
            EntityPlayerMP senderPlayerMP = null;
            if (sender instanceof EntityPlayerMP)
                senderPlayerMP = CommandBase.getCommandSenderAsPlayer(sender);
            if (args[1].equals("-reload-rewards")) {
                DailyRewardsManagerServer.instance().reloadRewards();
                OxygenHelperServer.sendStatusMessage(senderPlayerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARDS_RELOADED.ordinal());
            } else if (args[1].equals("-write-stack")) {
                if (args.length == 3) {
                    if (!(sender instanceof EntityPlayerMP))
                        throw new WrongUsageException("Command available only for player!");
                    if (senderPlayerMP.getHeldItemMainhand() != ItemStack.EMPTY) {
                        String folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/itemstack/" + args[2] + ".json";
                        Path path = Paths.get(folder);
                        JsonObject stackObject = new JsonObject();
                        stackObject.add("itemstack", ItemStackWrapper.getFromStack(senderPlayerMP.getHeldItemMainhand()).toJson());
                        try {
                            if (!Files.exists(path))
                                Files.createDirectories(path.getParent());   
                            JsonUtils.createExternalJsonFile(folder, stackObject);
                        } catch (IOException exception) {
                            OxygenMain.LOGGER.error("[Daily Rewards] ItemStack writing failure!", exception);
                        }
                        OxygenHelperServer.sendStatusMessage(senderPlayerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.ITEMSTACK_SERIALIZED.ordinal());
                    } else
                        throw new WrongUsageException("Main hand is empty!"); 
                }
            }
        }
    }
}
