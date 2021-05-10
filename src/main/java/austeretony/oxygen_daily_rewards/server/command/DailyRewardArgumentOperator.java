package austeretony.oxygen_daily_rewards.server.command;

import austeretony.oxygen_core.common.command.CommandArgument;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.MinecraftCommon;
import austeretony.oxygen_core.common.util.objects.Pair;
import austeretony.oxygen_core.server.api.OxygenServer;
import austeretony.oxygen_daily_rewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_daily_rewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_daily_rewards.common.player.PlayerData;
import austeretony.oxygen_daily_rewards.server.DailyRewardsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DailyRewardArgumentOperator implements CommandArgument {

    @Override
    public String getName() {
        return "daily-rewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            if (args[1].equals("reload")) {
                Future<Boolean> future = OxygenServer.addTask(() -> {
                    DailyRewardsManagerServer.instance().loadRewards();
                    return true;
                });

                try {
                    Boolean result = future.get();
                    if (result != null && result) {
                        sender.sendMessage(new TextComponentString("Daily rewards successfully reloaded"));
                        OxygenMain.logInfo(1, "[Core] {} successfully reloaded rewards.", sender.getName());
                    } else {
                        sender.sendMessage(new TextComponentString("Failed to reload daily rewards"));
                        OxygenMain.logInfo(1, "[Core] {} failed to reload rewards.", sender.getName());
                    }
                } catch (InterruptedException | ExecutionException exception) {
                    exception.printStackTrace();
                }
            }
        } else if (args.length == 3) {
            if (args[1].equals("days-rewarded-get")) {
                Pair<UUID, EntityPlayerMP> pair = parsePlayer(server, sender, args[2]);
                UUID playerUUID = pair.getKey();
                @Nullable EntityPlayerMP playerMP = pair.getValue();

                Future<Integer> future = OxygenServer.addTask(() -> {
                    DailyRewardsManagerServer manager = DailyRewardsManagerServer.instance();
                    PlayerData playerData = manager.getPlayerData(playerUUID);
                    if (playerData == null) {
                        playerData = manager.createAndLoadData(playerUUID);
                    }
                    return playerData.getDaysRewarded();
                });

                try {
                    Integer result = future.get();
                    if (result != null) {
                        sender.sendMessage(new TextComponentString(String.format("%s/%s days rewarded this month: %s",
                                playerMP != null ? MinecraftCommon.getEntityName(playerMP) : "Offline", playerUUID, result)));
                    } else {
                        sender.sendMessage(new TextComponentString(String.format("Failed to get days rewarded for %s/%s",
                                playerMP != null ? MinecraftCommon.getEntityName(playerMP) : "Offline", playerUUID)));
                    }
                } catch (InterruptedException | ExecutionException exception) {
                    exception.printStackTrace();
                }
            }
        } else if (args.length == 4) {
            if (args[1].equals("days-rewarded-set")) {
                Pair<UUID, EntityPlayerMP> pair = parsePlayer(server, sender, args[2]);
                UUID playerUUID = pair.getKey();
                @Nullable EntityPlayerMP playerMP = pair.getValue();
                int daysRewarded = CommandBase.parseInt(args[3], 0, 31);

                Future<Boolean> future = OxygenServer.addTask(() -> {
                    DailyRewardsManagerServer manager = DailyRewardsManagerServer.instance();
                    PlayerData playerData = manager.getPlayerData(playerUUID);
                    if (playerData == null) {
                        playerData = manager.createAndLoadData(playerUUID);
                    }

                    playerData.setDaysRewarded(daysRewarded);
                    long lastRewardTimeMillis = 0L;
                    if (daysRewarded > 0) {
                        lastRewardTimeMillis = OxygenServer.getZonedDateTime()
                                .withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt())
                                .withMinute(0).withSecond(1).toInstant().toEpochMilli();
                    }
                    playerData.setLastTimeRewardedMillis(lastRewardTimeMillis);
                    playerData.markChanged();

                    if (playerMP != null) {
                        OxygenMain.network().sendTo(new CPSyncPlayerData(playerData.getLastTimeRewardedMillis(),
                                playerData.getDaysRewarded()), playerMP);
                    }
                    OxygenMain.logInfo(1, "[Daily Rewards] Changed 'days rewarded' value of {}/{} to: {}",
                            playerMP != null ? MinecraftCommon.getEntityName(playerMP) : "Offline", playerUUID, daysRewarded);

                    return true;
                });

                try {
                    Boolean result = future.get();
                    if (result != null && result) {
                        sender.sendMessage(new TextComponentString(String.format("Changed 'days rewarded' value of %s/%s to: %s",
                                playerMP != null ? MinecraftCommon.getEntityName(playerMP) : "Offline", playerUUID, args[3])));
                    } else {
                        sender.sendMessage(new TextComponentString(String.format("Failed to change 'days rewarded' of %s/%s",
                                playerMP != null ? MinecraftCommon.getEntityName(playerMP) : "Offline", playerUUID)));
                    }
                } catch (InterruptedException | ExecutionException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "reload, days-rewarded-get, days-rewarded-set");
        }
        return Collections.emptyList();
    }
}
