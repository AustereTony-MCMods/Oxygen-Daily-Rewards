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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.JsonUtils;
import austeretony.oxygen_core.server.api.TimeHelperServer;
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

    public void loadRewardsData() {
        Month currentMonth = TimeHelperServer.getZonedDateTime().getMonth();

        String 
        monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
        folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/rewards_" + monthName.toLowerCase() + ".json";

        Path path = Paths.get(folder);
        if (!Files.exists(path))
            createDefaultRewardsDataFiles();

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
            OxygenMain.LOGGER.info("[Daily Rewards] Successfuly loaded rewards for <{}>.", monthName);
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Rewards data file for <" + monthName + "> is damaged!", exception);
            exception.printStackTrace();
        }

        this.compressRewardsData();
    }

    private static void createDefaultRewardsDataFiles() {
        String folder;
        Path path;
        JsonArray rewardsArray;
        JsonObject 
        rewardObject, 
        defaultReward = ItemStackWrapper.getFromStack(new ItemStack(Items.EMERALD)).toJson().getAsJsonObject();
        int i;

        for (Month month : Month.values()) {
            folder = OxygenHelperCommon.getConfigFolder() + "data/server/daily rewards/rewards_" + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase() + ".json";
            path = Paths.get(folder);    
            try {  
                if (!Files.exists(path))
                    Files.createDirectories(path.getParent());    

                rewardsArray = new JsonArray();    
                for (i = 0; i < month.maxLength(); i++) {
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
        OxygenMain.LOGGER.info("[Daily Rewards] Successfuly created default daily rewards data files.");
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

    public Reward getDailyReward(int day) {
        return this.rewards.get(day - 1);
    }

    public void syncRewardsData(EntityPlayerMP playerMP) {
        synchronized (this.compressedRewards) {
            byte[] compressed = new byte[this.compressedRewards.writerIndex()];
            this.compressedRewards.getBytes(0, compressed);
            OxygenMain.network().sendTo(new CPSyncRewardsData(compressed), playerMP);
        }
    }
}
