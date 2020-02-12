package austeretony.oxygen_dailyrewards.common.reward;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class RewardCommand implements Reward {

    private String description, tooltip, icon, commands;

    private int day;

    private boolean special;

    //server
    private byte[] iconRaw;

    //client
    private ResourceLocation iconTexture;

    @Override
    public EnumReward getType() {
        return EnumReward.COMMAND;
    }

    @Override
    public int getDay() {
        return this.day;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public long getAmount() {
        return 0L;
    }

    @Override
    public boolean isSpecial() {
        return this.special;
    }

    public static Reward fromJson(JsonObject jsonObject) {
        RewardCommand reward = new RewardCommand();
        reward.day = jsonObject.get("day").getAsInt();
        reward.description = jsonObject.get("description").getAsString();
        reward.special = jsonObject.get("special").getAsBoolean();
        reward.tooltip = jsonObject.get("tooltip").getAsString();
        reward.icon = jsonObject.get("icon").getAsString();
        reward.commands = jsonObject.get("commands").getAsString();

        reward.loadIconBytes();

        return reward;
    }

    private void loadIconBytes() {
        String pathStr = CommonReference.getGameFolder() + "/config/oxygen/data/server/daily rewards/icons/" + this.icon;
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(pathStr));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            this.iconRaw = baos.toByteArray();
        } catch (IOException exception) {
            OxygenMain.LOGGER.info("[Daily Rewards] Failed to load reward icon: {}", this.icon);
            exception.printStackTrace();
            this.iconRaw = new byte[0];
        }
    }

    @Override
    public void write(ByteBuf buffer) {  
        buffer.writeByte(this.day);
        ByteBufUtils.writeString(this.description, buffer);
        buffer.writeBoolean(this.special);
        ByteBufUtils.writeString(this.tooltip, buffer);

        buffer.writeInt(this.iconRaw.length);
        buffer.writeBytes(this.iconRaw);
        this.iconRaw = null;
    }

    public static Reward read(ByteBuf buffer) {
        RewardCommand reward = new RewardCommand();
        reward.day = buffer.readByte();
        reward.description = ByteBufUtils.readString(buffer);
        reward.special = buffer.readBoolean();
        reward.tooltip = ByteBufUtils.readString(buffer);

        reward.iconRaw = new byte[buffer.readInt()];
        buffer.readBytes(reward.iconRaw);

        return reward;
    }

    @Override
    public void rewardPlayer(EntityPlayerMP playerMP) {
        String[] commands = this.commands.split("[;]");
        for (String command : commands) {
            if (command.contains("@p"))
                command = command.replace("@p", CommonReference.getName(playerMP));

            if (command.contains("@pX"))
                command = command.replace("@pX", String.valueOf((int) playerMP.posX));
            if (command.contains("@pY"))
                command = command.replace("@pY", String.valueOf((int) playerMP.posY));
            if (command.contains("@pZ"))
                command = command.replace("@pZ", String.valueOf((int) playerMP.posZ));

            CommonReference.getServer().commandManager.executeCommand(CommonReference.getServer(), command);
        }

        DailyRewardsMain.DAILY_REWARDS_LOGGER.info("<{}/{}> [2]: player rewarded with COMMAND - {}.", 
                CommonReference.getName(playerMP), 
                CommonReference.getPersistentUUID(playerMP),
                this.commands);
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public ResourceLocation getIconTexture() {
        if (this.iconTexture == null) {
            if (this.iconRaw.length > 0) {
                ByteArrayInputStream baos = new ByteArrayInputStream(this.iconRaw);
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(baos);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                if (bufferedImage != null)
                    return this.iconTexture = ClientReference.getMinecraft().getTextureManager().getDynamicTextureLocation("reward_icon", new DynamicTexture(bufferedImage));
            }
            this.iconTexture = ClientReference.getMinecraft().getTextureManager().RESOURCE_LOCATION_EMPTY;
            this.iconRaw = null;
        }
        return this.iconTexture;
    }
}
