package austeretony.oxygen_daily_rewards.server;

import austeretony.oxygen_core.common.api.OxygenCommon;
import austeretony.oxygen_core.common.chat.StatusMessageType;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.JsonUtils;
import austeretony.oxygen_core.common.util.MinecraftCommon;
import austeretony.oxygen_core.server.api.OxygenServer;
import austeretony.oxygen_core.server.api.PrivilegesServer;
import austeretony.oxygen_core.server.operation.Operation;
import austeretony.oxygen_daily_rewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsPrivileges;
import austeretony.oxygen_daily_rewards.common.network.client.CPRewardClaimed;
import austeretony.oxygen_daily_rewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_daily_rewards.common.network.client.CPSyncRewards;
import austeretony.oxygen_daily_rewards.common.player.PlayerData;
import austeretony.oxygen_daily_rewards.common.reward.DailyReward;
import austeretony.oxygen_daily_rewards.common.reward.RewardType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class DailyRewardsManagerServer {

    private static DailyRewardsManagerServer instance;

    private final List<DailyReward> rewardList = new ArrayList<>();
    private final ByteBuf rewardsRaw = Unpooled.buffer();

    private final Map<UUID, PlayerData> playersMap = new HashMap<>();

    private DailyRewardsManagerServer() {
        OxygenServer.registerPersistentData(this::save);
        scheduleRewardsReload();
    }

    private void scheduleRewardsReload() {
        ZonedDateTime currentTime = OxygenServer.getZonedDateTime();
        ZonedDateTime reloadingTime = currentTime.withDayOfMonth(1)
                .withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()).withMinute(0).withSecond(0);

        if (currentTime.compareTo(reloadingTime) > 0) {
            reloadingTime = reloadingTime.plusMonths(1L);
        }

        Month nextMonth = reloadingTime.getMonth();
        long initialDelay = Duration.between(currentTime, reloadingTime).getSeconds();
        OxygenServer.addTask(this::loadRewards, initialDelay, TimeUnit.SECONDS);

        OxygenMain.logInfo(1, "[Daily Rewards] Scheduled rewards reloading for <{}> at: {}",
                nextMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                OxygenMain.DEBUG_DATE_TIME_FORMATTER.format(reloadingTime));
    }

    public static DailyRewardsManagerServer instance() {
        if (instance == null)
            instance = new DailyRewardsManagerServer();
        return instance;
    }

    public void serverStarting() {
        OxygenServer.addTask(this::loadRewards);
    }

    public void loadRewards() {
        Month currentMonth = OxygenServer.getZonedDateTime().getMonth();
        String monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String pathStr = OxygenCommon.getConfigFolder() + "/data/server/daily_rewards/rewards_" + monthName.toLowerCase() + ".json";

        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            createDefaultRewardsConfig(pathStr);
        }

        rewardList.clear();
        try {
            JsonArray rewardsArray = JsonUtils.getExternalJsonData(pathStr).getAsJsonArray();
            for (JsonElement rewardElement : rewardsArray) {
                DailyReward reward = DailyReward.fromJson(rewardElement.getAsJsonObject());
                rewardList.add(reward);
            }
            OxygenMain.logInfo(1, "[Daily Rewards] Successfully loaded rewards from file <{}>.", pathStr);
        } catch (IOException exception) {
            OxygenMain.logError(1, "[Daily Rewards] Rewards data file <" + pathStr + "> is damaged!", exception);
            exception.printStackTrace();
        }
        cacheRewardsData();
    }

    private void createDefaultRewardsConfig(String pathStr) {
        Path path = Paths.get(pathStr);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
            }

            JsonArray rewardsArray = new JsonArray();
            for (int i = 0; i < 31; i++) {
                boolean special = (i + 1) % 7 == 0;
                JsonObject rewardObject = new JsonObject();
                rewardObject.addProperty("type", RewardType.CURRENCY.toString());
                rewardObject.addProperty("day", i + 1);
                rewardObject.addProperty("is_special", special);

                JsonObject currencyRewardObject = new JsonObject();
                if (i < 28) {
                    currencyRewardObject.addProperty("currency_index", special ? OxygenMain.CURRENCY_SHARDS : OxygenMain.CURRENCY_COINS);
                    currencyRewardObject.addProperty("value", special ? 10 : 100);
                } else {
                    currencyRewardObject.addProperty("currency_index", OxygenMain.CURRENCY_VOUCHERS);
                    currencyRewardObject.addProperty("value", 5);
                }
                rewardObject.add("reward", currencyRewardObject);

                rewardsArray.add(rewardObject);
            }
            JsonUtils.createExternalJsonFile(pathStr, rewardsArray);
        } catch (IOException exception) {
            OxygenMain.logError(1, "[Daily Rewards] Failed to create default reward data file! Path: {}", pathStr);
            exception.printStackTrace();
        }
    }

    private void cacheRewardsData() {
        synchronized (rewardsRaw) {
            rewardsRaw.clear();
            rewardsRaw.writeShort(rewardList.size());
            for (DailyReward reward : rewardList) {
                reward.write(rewardsRaw);
            }
        }
    }

    public void playerLoggedIn(EntityPlayerMP playerMP) {
        final Runnable task = () -> {
            UUID playerUUID = MinecraftCommon.getEntityUUID(playerMP);
            PlayerData playerData = playersMap.get(playerUUID);
            if (playerData == null) {
                playerData = createAndLoadData(playerUUID);
            }
            syncRewards(playerMP);

            if (checkRewardAvailableAndReset(playerData)) {
                ITextComponent message = new TextComponentTranslation("oxygen_daily_rewards.status_message.daily_reward_available");
                ITextComponent command = new TextComponentTranslation("oxygen_daily_rewards.status_message.click_here");

                message.getStyle().setItalic(true);
                message.getStyle().setColor(TextFormatting.AQUA);
                command.getStyle().setItalic(true);
                command.getStyle().setUnderlined(true);
                command.getStyle().setColor(TextFormatting.WHITE);
                command.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/oxygenc core open-gui " + DailyRewardsMain.SCREEN_ID_DAILY_REWARDS));

                ITextComponent result = message.appendSibling(new TextComponentString(" ")).appendSibling(command);
                MinecraftCommon.sendChatMessage(playerMP, result);
            }

            OxygenMain.network().sendTo(new CPSyncPlayerData(playerData.getLastTimeRewardedMillis(),
                    playerData.getDaysRewarded()), playerMP);
        };
        OxygenServer.addTask(task);
    }

    public PlayerData createAndLoadData(UUID playerUUID) {
        PlayerData playerData = new PlayerData(playerUUID,
                OxygenServer.getDataFolder() + "/players/" + playerUUID + "/daily_rewards/player_data.dat");
        playersMap.put(playerUUID, playerData);
        OxygenServer.loadPersistentData(playerData);
        return playerData;
    }

    @Nullable
    public PlayerData getPlayerData(UUID playerUUID) {
        return playersMap.get(playerUUID);
    }

    private boolean checkRewardAvailableAndReset(PlayerData playerData) {
        ZonedDateTime currentTime = OxygenServer.getZonedDateTime();
        ZonedDateTime lastTimePlayerRewarded = OxygenServer.getZonedDateTime(playerData.getLastTimeRewardedMillis());
        ZonedDateTime nextRewardTime = lastTimePlayerRewarded.plusDays(1L)
                .withHour(DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()).withMinute(0).withSecond(0);

        boolean isNewMonth = lastTimePlayerRewarded.getMonthValue() != currentTime.getMonthValue();
        if (playerData.getLastTimeRewardedMillis() != 0L && isNewMonth
                && currentTime.getHour() >= DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt()) {
            playerData.setDaysRewarded(0);
            playerData.markChanged();
        }

        return PrivilegesServer.getBoolean(playerData.getPlayerUUID(), DailyRewardsPrivileges.DAILY_REWARDS_ACCESS.getId(), true)
                && currentTime.compareTo(nextRewardTime) > 0;
    }

    public void syncRewards(EntityPlayerMP playerMP) {
        synchronized (rewardsRaw) {
            byte[] rewardsRaw = new byte[this.rewardsRaw.writerIndex()];
            this.rewardsRaw.getBytes(0, rewardsRaw);
            OxygenMain.network().sendTo(new CPSyncRewards(rewardsRaw), playerMP);
        }
    }

    public void claimReward(EntityPlayerMP playerMP) {
        UUID playerUUID = MinecraftCommon.getEntityUUID(playerMP);
        PlayerData playerData = playersMap.get(playerUUID);
        if (playerData == null) return;

        if (checkRewardAvailableAndReset(playerData)) {
            int daysRewarded = playerData.getDaysRewarded();
            if (daysRewarded >= rewardList.size()) return;

            DailyReward reward = rewardList.get(daysRewarded);

            Operation operation = Operation.of(DailyRewardsMain.OPERATION_REWARD_GAIN, playerMP)
                    .withSuccessTask(() -> {
                        playerData.rewarded(OxygenServer.getCurrentTimeMillis());
                        playerData.markChanged();

                        OxygenMain.network().sendTo(new CPRewardClaimed(playerData.getLastTimeRewardedMillis(),
                                playerData.getDaysRewarded()), playerMP);

                        OxygenServer.sendStatusMessage(playerMP, DailyRewardsMain.MODULE_INDEX, StatusMessageType.COMMON,
                                "oxygen_daily_rewards.status_message.daily_reward_claimed");
                        OxygenMain.logInfo(2, "[Daily Rewards] Rewarded {}/{} for day {} with: {}",
                                MinecraftCommon.getEntityName(playerMP), playerUUID, playerData.getDaysRewarded(),
                                reward.getItem().toString());
                    })
                    .withFailTask(reason -> OxygenServer.sendMessageOnOperationFail(playerMP, reason, DailyRewardsMain.MODULE_INDEX));
            reward.getItem().reward(operation);
            operation
                    .process();
        } else {
            OxygenServer.sendStatusMessage(playerMP, DailyRewardsMain.MODULE_INDEX, StatusMessageType.ERROR,
                    "oxygen_daily_rewards.status_message.daily_reward_unavailable");
        }
    }

    public void save() {
        for (PlayerData playerData : playersMap.values()) {
            if (playerData.isChanged()) {
                playerData.resetChangedMark();
                OxygenServer.savePersistentData(playerData);
            }
        }
    }
}
