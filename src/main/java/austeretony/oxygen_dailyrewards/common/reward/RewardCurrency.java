package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonObject;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
import austeretony.oxygen_core.server.api.SoundEventHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RewardCurrency implements Reward {

    private String description;

    private int day, currencyIndex;

    private long amount;

    private boolean special;

    @Override
    public EnumReward getType() {
        return EnumReward.CURRENCY;
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
        return OxygenHelperClient.getCurrencyProperties(this.currencyIndex).getLocalizedName();
    }

    @Override
    public boolean isSpecial() {
        return this.special;
    }

    public static Reward fromJson(JsonObject jsonObject) {
        RewardCurrency reward = new RewardCurrency();
        reward.day = jsonObject.get("day").getAsInt();
        reward.description = jsonObject.get("description").getAsString();
        reward.amount = jsonObject.get("amount").getAsLong();
        reward.special = jsonObject.get("special").getAsBoolean();
        reward.currencyIndex = jsonObject.get("currency_index").getAsInt();
        return reward;
    }

    @Override
    public void write(ByteBuf buffer) {  
        buffer.writeByte(this.day);
        ByteBufUtils.writeString(this.description, buffer);
        buffer.writeLong(this.amount);
        buffer.writeBoolean(this.special);
        buffer.writeByte(this.currencyIndex);
    }

    public static Reward read(ByteBuf buffer) {
        RewardCurrency reward = new RewardCurrency();
        reward.day = buffer.readByte();
        reward.description = ByteBufUtils.readString(buffer);
        reward.amount = buffer.readLong();
        reward.special = buffer.readBoolean();
        reward.currencyIndex = buffer.readByte();
        return reward;
    }

    @Override
    public boolean rewardPlayer(EntityPlayerMP playerMP) {
        CurrencyHelperServer.addCurrency(CommonReference.getPersistentUUID(playerMP), this.amount, this.currencyIndex);
        SoundEventHelperServer.playSoundClient(playerMP, OxygenSoundEffects.RINGING_COINS.getId());

        if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
            OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [2]: player rewarded with CURRENCY - index <{}>, amount {}.", 
                    CommonReference.getName(playerMP), 
                    CommonReference.getPersistentUUID(playerMP),
                    this.currencyIndex,
                    this.amount);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GUISimpleElement widget, int mouseX, int mouseY) {
        Minecraft mc = ClientReference.getMinecraft();
        String amountStr = String.format("x%d", this.amount);

        CurrencyProperties currencyProperties = OxygenHelperClient.getCurrencyProperties(this.currencyIndex);

        float scale = widget.getScale();

        GlStateManager.pushMatrix();           
        GlStateManager.translate(widget.getX(), widget.getY(), 0.0F);   

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.enableBlend(); 
        mc.getTextureManager().bindTexture(currencyProperties.getIcon());
        GUIAdvancedElement.drawCustomSizedTexturedRect(
                20 * (int) scale + currencyProperties.getXOffset() * (int) scale, 
                20 * (int) scale + currencyProperties.getYOffset() * (int) scale, 
                0, 
                0, 
                currencyProperties.getIconWidth() * (int) scale, 
                currencyProperties.getIconHeight() * (int) scale, 
                currencyProperties.getIconWidth() * (int) scale, 
                currencyProperties.getIconHeight() * (int) scale);                 
        GlStateManager.disableBlend();  

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        float textScale = scale == 1.0F ? widget.getTextScale() - 0.1F : widget.getTextScale();

        GlStateManager.pushMatrix();           
        GlStateManager.translate((widget.getWidth() - widget.textWidth(amountStr, textScale)) / 2.0F, 30.0F * scale, 0.0F);            
        GlStateManager.scale(textScale, textScale, 0.0F);   
        mc.fontRenderer.drawString(amountStr, 0, 0, widget.getEnabledTextColor(), false); 
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }
}
