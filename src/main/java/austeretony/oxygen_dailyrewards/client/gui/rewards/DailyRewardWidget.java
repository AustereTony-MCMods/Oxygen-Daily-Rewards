package austeretony.oxygen_dailyrewards.client.gui.rewards;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseClientSetting;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.currency.CurrencyProperties;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import austeretony.oxygen_dailyrewards.common.reward.RewardCurrency;
import austeretony.oxygen_dailyrewards.common.reward.RewardItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class DailyRewardWidget extends GUISimpleElement<DailyRewardWidget> {

    private final Reward reward;

    private final String dayStr, descriptionStr, amountStr, rewardAvailableStr, nextRewardStr;

    private final boolean special, rewarded, nextReward, locked, unreachable;

    private ItemStack itemStack;

    private CurrencyProperties properties;

    public DailyRewardWidget(int xPosition, int yPosition, Reward reward, boolean rewarded, boolean nextReward, boolean locked, boolean unreachable) {
        this.setPosition(xPosition, yPosition);
        this.setSize(48, 64);

        this.reward = reward;

        this.dayStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.rewardDay", reward.getDay());
        this.descriptionStr = ClientReference.localize(reward.getDescription());
        this.amountStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.amount", reward.getAmount());

        this.rewardAvailableStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.rewardAvailable");
        this.nextRewardStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.nextReward");

        this.special = reward.isSpecial();
        this.rewarded = rewarded;
        this.nextReward = nextReward;
        this.locked = locked;
        this.unreachable = unreachable;

        switch (reward.getType()) {
        case ITEM:
            this.itemStack = ((RewardItem) this.reward).getStackWrapper().getCachedItemStack();
            this.setDisplayText(EnumBaseClientSetting.ENABLE_RARITY_COLORS.get().asBoolean() ? this.itemStack.getRarity().rarityColor + this.itemStack.getDisplayName() : this.itemStack.getDisplayName());
            break;
        case CURRENCY:
            this.properties = OxygenHelperClient.getCurrencyProperties(((RewardCurrency) this.reward).getCurrencyIndex());
            this.setDisplayText(this.properties.getLocalizedName());
            break;
        }

        this.setDynamicBackgroundColor(EnumBaseGUISetting.ELEMENT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.INACTIVE_ELEMENT_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_HOVERED_COLOR.get().asInt());
        this.setTextDynamicColor(EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.ACTIVE_ELEMENT_COLOR.get().asInt());
        this.setStaticBackgroundColor(EnumBaseGUISetting.STATUS_ELEMENT_COLOR.get().asInt());
        this.setTextScale(EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat());
        this.enableFull();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.isVisible()) {   
            GlStateManager.pushMatrix();           
            GlStateManager.translate(this.getX(), this.getY(), 0.0F);           
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            drawRect(0, 0, this.getWidth(), this.getHeight(), this.getEnabledBackgroundColor());
            drawRect(0, this.getHeight() - 10, this.getWidth(), this.getHeight(), this.getHoveredBackgroundColor());

            if (this.nextReward) {
                if (!this.rewarded && !this.locked) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, this.getWidth(), this.getHeight() - 10, 0x00000000, this.getStaticBackgroundColor(), EnumGUIAlignment.TOP);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }

                GlStateManager.pushMatrix();           
                GlStateManager.translate(2.0F, 9.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale() - 0.1F, this.getTextScale() - 0.1F, 0.0F);   
                this.mc.fontRenderer.drawString(!this.rewarded && !this.locked ? this.rewardAvailableStr : this.nextRewardStr, 0, 0, this.getDisabledTextColor(), false); 
                GlStateManager.popMatrix();
            } else if (this.unreachable) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, this.getWidth(), this.getHeight() - 10, 0x00000000, this.getDisabledBackgroundColor(), EnumGUIAlignment.TOP);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else if (this.rewarded) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, this.getWidth(), this.getHeight() - 10, 0x00000000, this.getHoveredTextColor(), EnumGUIAlignment.TOP);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GlStateManager.pushMatrix();           
            GlStateManager.translate(2.0F, 2.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F);   
            this.mc.fontRenderer.drawString(this.dayStr, 0, 0, this.getEnabledTextColor(), false); 
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();           
            GlStateManager.translate((this.getWidth() - this.textWidth(this.amountStr, this.getTextScale() - 0.05F)) / 2.0F, 38.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() - 0.05F, this.getTextScale() - 0.05F, 0.0F);   
            this.mc.fontRenderer.drawString(this.amountStr, 0, 0, this.getEnabledTextColor(), false); 
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();           
            GlStateManager.translate(2.0F, this.getHeight() - 16.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() - 0.1F, this.getTextScale() - 0.1F, 0.0F);   
            this.mc.fontRenderer.drawString(this.descriptionStr, 0, 0, this.getDisabledTextColor(), false); 
            GlStateManager.popMatrix();

            if (this.itemStack != null) {
                RenderHelper.enableGUIStandardItemLighting();            
                GlStateManager.enableDepth();
                this.itemRender.renderItemAndEffectIntoGUI(this.itemStack, 16, 19);    
                GlStateManager.disableDepth();
                RenderHelper.disableStandardItemLighting();
            } else if (this.properties != null) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                GlStateManager.enableBlend(); 
                this.mc.getTextureManager().bindTexture(this.properties.getIcon());
                GUIAdvancedElement.drawCustomSizedTexturedRect(16 + this.properties.getXOffset() * 2, 19 + this.properties.getYOffset() * 2, 0, 0, this.properties.getIconWidth() * 2, this.properties.getIconHeight() * 2, this.properties.getIconWidth() * 2, this.properties.getIconHeight() * 2);                 
                GlStateManager.disableBlend();      
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.pushMatrix();           
            GlStateManager.translate(2.0F, this.getHeight() - 7.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale() - 0.1F, this.getTextScale() - 0.1F, 0.0F);   
            this.mc.fontRenderer.drawString(this.getDisplayText(), 0, 0, this.getEnabledTextColor(), false); 
            GlStateManager.popMatrix();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (this.special) {
                GlStateManager.enableBlend(); 
                this.mc.getTextureManager().bindTexture(DailyRewardsMenuScreen.SPECIAL_ICON);                        
                GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 8, 2, 0, 0, 6, 6, 6, 6);     
                GlStateManager.disableBlend(); 
            }

            if (this.locked && !this.unreachable && !this.rewarded) {
                GlStateManager.enableBlend(); 
                this.mc.getTextureManager().bindTexture(DailyRewardsMenuScreen.LOCKED_ICON);                        
                GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 8, this.getHeight() - 18, 0, 0, 6, 6, 6, 6);     
                GlStateManager.disableBlend(); 
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.itemStack != null && mouseX >= this.getX() + 16 && mouseY >= this.getY() + 16 && mouseX < this.getX() + 32 && mouseY < this.getY() + 32)
            this.screen.drawToolTip(this.itemStack, mouseX + 4, mouseY);
    }
}
