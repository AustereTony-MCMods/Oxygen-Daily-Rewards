package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonObject;

import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.InventoryProviderServer;
import austeretony.oxygen_core.server.api.SoundEventHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RewardItem implements Reward {

    private String description;

    private int day;

    private int amount;

    private boolean special;

    private ItemStackWrapper stackWrapper;

    @Override
    public EnumReward getType() {
        return EnumReward.ITEM;
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
        return null;
    }

    @Override
    public boolean isSpecial() {
        return this.special;
    }

    public static Reward fromJson(JsonObject jsonObject) {
        RewardItem reward = new RewardItem();
        reward.day = jsonObject.get("day").getAsInt();
        reward.description = jsonObject.get("description").getAsString();
        reward.amount = jsonObject.get("amount").getAsInt();
        reward.special = jsonObject.get("special").getAsBoolean();
        reward.stackWrapper = ItemStackWrapper.fromJson(jsonObject.get("itemstack").getAsJsonObject());
        return reward;
    }

    @Override
    public void write(ByteBuf buffer) {  
        buffer.writeByte(this.day);
        ByteBufUtils.writeString(this.description, buffer);
        buffer.writeShort(this.amount);
        buffer.writeBoolean(this.special);
        this.stackWrapper.write(buffer);
    }

    public static Reward read(ByteBuf buffer) {
        RewardItem reward = new RewardItem();
        reward.day = buffer.readByte();
        reward.description = ByteBufUtils.readString(buffer);
        reward.amount = buffer.readShort();
        reward.special = buffer.readBoolean();
        reward.stackWrapper = ItemStackWrapper.read(buffer);
        return reward;
    }

    @Override
    public boolean rewardPlayer(EntityPlayerMP playerMP) { 
        if (InventoryProviderServer.getPlayerInventory().haveEnoughSpace(playerMP, this.stackWrapper, this.amount)) {
            InventoryProviderServer.getPlayerInventory().addItem(playerMP, this.stackWrapper, this.amount);
            SoundEventHelperServer.playSoundClient(playerMP, OxygenSoundEffects.INVENTORY_OPERATION.getId());

            if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [2]: player rewarded with ITEM - name <{}>, amount {}.", 
                        CommonReference.getName(playerMP), 
                        CommonReference.getPersistentUUID(playerMP),
                        this.getItemStack().getDisplayName(),
                        this.amount);
            return true;
        } else
            OxygenManagerServer.instance().sendStatusMessage(playerMP, EnumOxygenStatusMessage.INVENTORY_FULL);
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GUISimpleElement widget, int mouseX, int mouseY) {
        Minecraft mc = ClientReference.getMinecraft();
        String amountStr = String.format("x%d", this.amount);

        float scale = widget.getScale();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(widget.getX(), widget.getY(), 0.0F);   

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.enableGUIStandardItemLighting();            
        GlStateManager.enableDepth();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(16.0F * scale, 16.0F * scale, 0.0F);            
        GlStateManager.scale(1.0F * scale, 1.0F * scale, 1.0F * scale); 
        mc.getRenderItem().renderItemAndEffectIntoGUI(this.getItemStack(), 0, 0);  
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        float textScale = scale == 1.0F ? widget.getTextScale() - 0.1F : widget.getTextScale();

        GlStateManager.pushMatrix();           
        GlStateManager.translate((widget.getWidth() - widget.textWidth(amountStr, textScale)) / 2.0F, 34.0F * scale, 0.0F);            
        GlStateManager.scale(textScale, textScale, 0.0F);   
        mc.fontRenderer.drawString(amountStr, 0, 0, widget.getEnabledTextColor(), false); 
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    @Override
    public ItemStack getItemStack() {
        return this.stackWrapper.getCachedItemStack();
    }
}
