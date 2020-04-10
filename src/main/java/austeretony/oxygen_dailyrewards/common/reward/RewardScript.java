package austeretony.oxygen_dailyrewards.common.reward;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import com.google.gson.JsonObject;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.scripting.ScriptWrapper;
import austeretony.oxygen_core.common.scripting.ScriptingProvider;
import austeretony.oxygen_core.common.scripting.Shell;
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

public class RewardScript implements Reward {

    private String description, tooltip;

    private int day, amount;

    private boolean special;

    @Nullable
    private ScriptWrapper scriptWrapper;

    //server
    private byte[] iconRaw;

    //client
    private ResourceLocation iconTexture;

    @Override
    public EnumReward getType() {
        return EnumReward.SCRIPT;
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
        RewardScript reward = new RewardScript();
        reward.day = jsonObject.get("day").getAsInt();
        reward.description = jsonObject.get("description").getAsString();
        reward.special = jsonObject.get("special").getAsBoolean();
        reward.amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsInt() : 0;
        reward.tooltip = jsonObject.get("tooltip").getAsString();

        reward.loadIconBytes(jsonObject.get("icon").getAsString());
        reward.loadScript(jsonObject.get("script").getAsString());

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

    private void loadScript(String scriptName) {
        try {
            this.scriptWrapper = ScriptWrapper.fromFile(CommonReference.getGameFolder() + "/config/oxygen/data/server/daily rewards/scripts/" + scriptName, scriptName);
        } catch (IOException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Failed to load reward script: {}", scriptName);
            exception.printStackTrace();
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
        RewardScript reward = new RewardScript();
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

        if (this.scriptWrapper != null) {
            Shell shell = ScriptingProvider.createShell();

            shell.put("world", playerMP.world);
            shell.put("player", playerMP);

            Object result = shell.evaluate(this.scriptWrapper.getScriptText(), this.scriptWrapper.getName(), DailyRewardsConfig.DEBUG_SCRIPTS.asBoolean());

            if (result != null && (Boolean) result) {
                if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                    OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [2]: player rewarded with SCRIPT - {}.", 
                            CommonReference.getName(playerMP), 
                            CommonReference.getPersistentUUID(playerMP),
                            this.scriptWrapper.getName());
                return true;
            }
        }
        return false;
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
