package austeretony.oxygen_dailyrewards.server.command;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_dailyrewards.server.DailyRewardsManagerServer;
import austeretony.oxygen_dailyrewards.server.RewardsPlayerDataServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class DailyRewardsArgumentOperator implements ArgumentExecutor {

    @Override
    public String getName() {
        return "dailyrewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 2) {
            EntityPlayerMP senderPlayerMP = null, targetPlayerMP;
            if (sender instanceof EntityPlayerMP)
                senderPlayerMP = CommandBase.getCommandSenderAsPlayer(sender);

            if (args.length == 2) {
                if (args[1].equals("-reload-rewards")) {
                    DailyRewardsManagerServer.instance().getRewardsDataContainer().reloadRewards();
                    OxygenHelperServer.sendStatusMessage(senderPlayerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARDS_RELOADED.ordinal());

                    if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Daily Rewards] (Operator/Console) {} reloaded daily rewards.",
                                sender.getName()); 
                }
            } else if (args.length == 3) {
                if (args[1].equals("-reset-latest-reward")) {
                    targetPlayerMP = CommandBase.getPlayer(server, sender, args[2]);

                    RewardsPlayerDataServer playerData = DailyRewardsManagerServer.instance().getPlayerDataContainer().getPlayerData(CommonReference.getPersistentUUID(targetPlayerMP));
                    if (playerData != null) {
                        playerData.setDaysRewarded(playerData.getDaysRewarded() - 1);
                        playerData.setRewardedDaysSeries(playerData.getRewardedDaysSeries() - 1);

                        playerData.setLastRewardTimeMillis(0L);
                        playerData.setChanged(true);

                        OxygenMain.network().sendTo(new CPSyncPlayerData(
                                playerData.getDaysRewarded(), 
                                playerData.getLastRewardTimeMillis()), targetPlayerMP);

                        OxygenHelperServer.sendStatusMessage(senderPlayerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.PLAYER_LATEST_REWARD_RESET.ordinal());

                        if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                            OxygenMain.LOGGER.info("[Daily Rewards] (Operator/Console) {} reset player {}/{} latest daily reward.",
                                    sender.getName(),
                                    CommonReference.getName(targetPlayerMP),
                                    CommonReference.getPersistentUUID(targetPlayerMP)); 
                    }
                }
            } else if (args.length == 4) {
                if (args[1].equals("-set-days-rewarded")) {
                    targetPlayerMP = CommandBase.getPlayer(server, sender, args[2]);
                    int amount = CommandBase.parseInt(args[3], 0, Short.MAX_VALUE);

                    RewardsPlayerDataServer playerData = DailyRewardsManagerServer.instance().getPlayerDataContainer().getPlayerData(CommonReference.getPersistentUUID(targetPlayerMP));
                    if (playerData != null) {
                        playerData.setDaysRewarded(amount);
                        playerData.setRewardedDaysSeries(amount);

                        playerData.setLastRewardTimeMillis(0L);
                        playerData.setChanged(true);

                        OxygenMain.network().sendTo(new CPSyncPlayerData(amount, 0L), targetPlayerMP);

                        OxygenHelperServer.sendStatusMessage(senderPlayerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.PLAYER_REWARDS_AMOUNT_SET.ordinal());

                        if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                            OxygenMain.LOGGER.info("[Daily Rewards] (Operator/Console) {} set player {}/{} claimed rewards amount to <{}>.",
                                    sender.getName(),
                                    CommonReference.getName(targetPlayerMP),
                                    CommonReference.getPersistentUUID(targetPlayerMP),
                                    amount); 
                    }
                }
            }
        }
    }
}
