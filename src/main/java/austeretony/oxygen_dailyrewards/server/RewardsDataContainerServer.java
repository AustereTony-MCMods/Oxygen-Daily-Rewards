package austeretony.oxygen_dailyrewards.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.JsonUtils;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.api.TimeHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncRewardsData;
import austeretony.oxygen_dailyrewards.common.reward.EnumReward;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RewardsDataContainerServer {

    private final DailyRewardsManagerServer manager;

    private final List<Reward> rewards = new ArrayList<>(31);

    private final ByteBuf compressedRewards = Unpooled.buffer();

    public RewardsDataContainerServer(DailyRewardsManagerServer manager) {
        this.manager = manager;
    }

    public void reloadRewards() {
        final Runnable reloadingTask = ()->{
            Future future = OxygenHelperServer.addIOTask(this::loadRewardsData);
            try {
                future.get();
            } catch (ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
            }
            OxygenHelperServer.getOnlinePlayersUUIDs().forEach(
                    (playerUUID)->this.syncRewardsData(CommonReference.playerByUUID(playerUUID)));
            OxygenMain.LOGGER.info("[Daily Rewards] Daily rewards reloaded.");
        };
        OxygenHelperServer.addRoutineTask(reloadingTask);
    }

    public void loadRewardsData() {
        String folder = null;
        if (DailyRewardsConfig.REWARD_MODE.asInt() == 0) {
            Month currentMonth = TimeHelperServer.getZonedDateTime().getMonth();

            String monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/rewards_" + monthName.toLowerCase() + ".json";
        } else {
            folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/rewards.json";
        }

        Path path = Paths.get(folder);
        if (!Files.exists(path))
            createDefaultRewardFiles();

        JsonArray rewardsArray;
        JsonObject rewardObject;
        EnumReward enumReward;
        this.rewards.clear();
        try {  
            rewardsArray = JsonUtils.getExternalJsonData(folder).getAsJsonArray();
            for (JsonElement rewardElement : rewardsArray) {
                rewardObject = rewardElement.getAsJsonObject();
                enumReward = EnumReward.valueOf(rewardObject.get("type").getAsString());
                this.rewards.add(enumReward.fromJson(rewardObject));
            }
            OxygenMain.LOGGER.info("[Daily Rewards] Successfuly loaded rewards from file <{}>.", folder);
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Rewards data file <" + folder + "> is damaged!", exception);
            exception.printStackTrace();
        }

        this.compressRewardsData();
    }

    private static void createDefaultRewardFiles() {
        String folder;
        Path path;
        JsonArray rewardsArray;
        JsonObject 
        rewardObject, 
        defaultReward = ItemStackWrapper.of(new ItemStack(Items.EMERALD)).toJson().getAsJsonObject();
        int i;

        if (DailyRewardsConfig.REWARD_MODE.asInt() == 0) {
            for (Month month : Month.values()) {
                folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/rewards_" + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase() + ".json";
                createDefaultRewardsFile(folder, month.maxLength());
            } 
        } else {
            folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/rewards.json";
            createDefaultRewardsFile(folder, 7);
        }
        OxygenMain.LOGGER.info("[Daily Rewards] Successfuly created default daily rewards data file(s).");
    }

    private static void createDefaultRewardsFile(String folder, int days) {
        Path path = Paths.get(folder);    
        try {  
            if (!Files.exists(path))
                Files.createDirectories(path.getParent());    

            JsonArray rewardsArray = new JsonArray();    
            JsonObject 
            rewardObject, 
            defaultReward = ItemStackWrapper.of(new ItemStack(Items.EMERALD)).toJson().getAsJsonObject();

            for (int i = 0; i < days; i++) {
                rewardObject = new JsonObject();
                rewardObject.add("day", new JsonPrimitive(i + 1));
                rewardObject.add("type", new JsonPrimitive(EnumReward.ITEM.toString()));
                rewardObject.add("description", new JsonPrimitive("oxygen_dailyrewards.description.item"));
                rewardObject.add("amount", new JsonPrimitive(1));
                rewardObject.add("special", new JsonPrimitive(false));
                rewardObject.add("itemstack", defaultReward);
                rewardsArray.add(rewardObject);
            }
            JsonUtils.createExternalJsonFile(folder, rewardsArray);
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Failed to create default reward data file! Path: {}", folder);
            exception.printStackTrace();
        } 
    }

    private void compressRewardsData() {
        synchronized (this.compressedRewards) {
            this.compressedRewards.clear();
            this.compressedRewards.writeByte(this.rewards.size());
            for (Reward reward : this.rewards) {
                this.compressedRewards.writeByte(reward.getType().ordinal());
                reward.write(this.compressedRewards);
            }
        }
    }

    public List<Reward> getRewards() {
        return this.rewards;
    }

    @Nullable
    public Reward getDailyReward(int day) {
        Reward reward = null;
        try {
            reward = this.rewards.get(day - 1);
        } catch (IndexOutOfBoundsException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Reward index <{}> out of bounds!", day);
            exception.printStackTrace();
        }
        return reward;
    }

    public void syncRewardsData(@Nullable EntityPlayerMP playerMP) {
        if (playerMP != null) {
            synchronized (this.compressedRewards) {
                byte[] compressed = new byte[this.compressedRewards.writerIndex()];
                this.compressedRewards.getBytes(0, compressed);
                OxygenMain.network().sendTo(new CPSyncRewardsData(compressed), playerMP);
            }
        }
    }
}
