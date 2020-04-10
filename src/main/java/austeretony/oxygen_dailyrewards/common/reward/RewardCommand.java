package austeretony.oxygen_dailyrewards.common.reward;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.InventoryProviderServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RewardCommand implements Reward {

    private String description, tooltip, commands;

    private int day, amount;

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
    public String getTooltip() {
        return this.tooltip;
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
        reward.amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsInt() : 0;
        reward.tooltip = jsonObject.get("tooltip").getAsString();
        reward.commands = jsonObject.get("commands").getAsString();

        reward.loadIconBytes(jsonObject.get("icon").getAsString());

        return reward;
    }

    private void loadIconBytes(String iconName) {
        String pathStr = CommonReference.getGameFolder() + "/config/oxygen/data/server/daily rewards/icons/" + iconName;
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(pathStr));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            this.iconRaw = baos.toByteArray();
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Failed to load reward icon: {}", iconName);
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
    public boolean rewardPlayer(EntityPlayerMP playerMP) {
        if (InventoryProviderServer.getPlayerInventory().getEmptySlotsAmount(playerMP) < this.amount) {
            OxygenManagerServer.instance().sendStatusMessage(playerMP, EnumOxygenStatusMessage.INVENTORY_FULL);
            return false;
        }       

        String[] commands = this.commands.split("[;]");
        int result;
        for (String command : commands) {
            if (command.contains("@p"))
                command = command.replace("@p", CommonReference.getName(playerMP));

            if (command.contains("@pX"))
                command = command.replace("@pX", String.valueOf((int) playerMP.posX));
            if (command.contains("@pY"))
                command = command.replace("@pY", String.valueOf((int) playerMP.posY));
            if (command.contains("@pZ"))
                command = command.replace("@pZ", String.valueOf((int) playerMP.posZ));
            if (command.contains("@dim"))
                command = command.replace("@dim", String.valueOf(playerMP.dimension));

            result = CommonReference.getServer().commandManager.executeCommand(CommonReference.getServer(), command);

            if (result == 0)
                OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> Failed to execute command: {}.", 
                        CommonReference.getName(playerMP), 
                        CommonReference.getPersistentUUID(playerMP),
                        command);
        }

        if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
            OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [2]: player rewarded with COMMAND - {}.", 
                    CommonReference.getName(playerMP), 
                    CommonReference.getPersistentUUID(playerMP),
                    this.commands);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GUISimpleElement widget, int mouseX, int mouseY) {
        Minecraft mc = ClientReference.getMinecraft();

        int scaleFactor = (int) widget.getScale();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(widget.getX(), widget.getY(), 0.0F);   

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableBlend(); 
        mc.getTextureManager().bindTexture(this.getIconTexture());
        GUIAdvancedElement.drawCustomSizedTexturedRect(0, 0, 0, 0, 48 * scaleFactor, 48 * scaleFactor, 48 * scaleFactor, 48 * scaleFactor);                 
        GlStateManager.disableBlend();  

        GlStateManager.popMatrix();
    }

    @Nonnull
    private ResourceLocation getIconTexture() {
        if (this.iconTexture == null) {
            if (this.iconRaw.length > 0) {
                ByteArrayInputStream baos = new ByteArrayInputStream(this.iconRaw);
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(baos);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                this.iconRaw = null;

                if (bufferedImage != null)
                    return this.iconTexture = ClientReference.getMinecraft().getTextureManager().getDynamicTextureLocation("reward_icon", new DynamicTexture(bufferedImage));
            }
            this.iconTexture = ClientReference.getMinecraft().getTextureManager().RESOURCE_LOCATION_EMPTY;
        }
        return this.iconTexture;
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }
}
