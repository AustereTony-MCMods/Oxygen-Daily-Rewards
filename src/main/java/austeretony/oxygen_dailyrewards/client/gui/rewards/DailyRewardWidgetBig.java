package austeretony.oxygen_dailyrewards.client.gui.rewards;

import austeretony.alternateui.screen.core.GUIAdvancedElement;
import austeretony.alternateui.screen.core.GUISimpleElement;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.OxygenGUITextures;
import austeretony.oxygen_core.client.gui.OxygenGUIUtils;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import net.minecraft.client.renderer.GlStateManager;

public class DailyRewardWidgetBig extends GUISimpleElement<DailyRewardWidgetBig> {

    private final String dayStr, descriptionStr, rewardAvailableStr, nextRewardStr;

    private final boolean special, rewarded, nextReward, locked, unreachable;

    private final Reward reward;

    public DailyRewardWidgetBig(int xPosition, int yPosition, Reward reward, boolean rewarded, boolean nextReward, boolean locked, boolean unreachable) {
        this.setPosition(xPosition, yPosition);
        this.setSize(96, 97);

        this.setScale(2.0F);

        this.dayStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.rewardDay", reward.getDay());
        this.descriptionStr = ClientReference.localize(reward.getDescription());

        this.rewardAvailableStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.rewardAvailable");
        this.nextRewardStr = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.nextReward");

        this.special = reward.isSpecial();
        this.rewarded = rewarded;
        this.nextReward = nextReward;
        this.locked = locked;
        this.unreachable = unreachable;

        this.reward = reward;

        if (reward.getTooltip() != null)
            this.initTooltip(reward.getTooltip(), EnumBaseGUISetting.TOOLTIP_TEXT_COLOR.get().asInt(), EnumBaseGUISetting.TOOLTIP_BACKGROUND_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_TOOLTIP_SCALE.get().asFloat());

        this.setDynamicBackgroundColor(EnumBaseGUISetting.ELEMENT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.INACTIVE_ELEMENT_COLOR.get().asInt(), EnumBaseGUISetting.ELEMENT_HOVERED_COLOR.get().asInt());
        this.setTextDynamicColor(EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt(), EnumBaseGUISetting.ACTIVE_ELEMENT_COLOR.get().asInt());
        this.setStaticBackgroundColor(EnumBaseGUISetting.STATUS_ELEMENT_COLOR.get().asInt());
        this.setDebugColor(EnumBaseGUISetting.BACKGROUND_ADDITIONAL_COLOR.get().asInt());
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

            if (this.nextReward) {
                if (!this.rewarded && !this.locked) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    OxygenGUIUtils.drawGradientRect(0.0D, 0.0D, this.getWidth(), this.getHeight() - 10, 0x00000000, this.getStaticBackgroundColor(), EnumGUIAlignment.TOP);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }
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
            GlStateManager.scale(this.getTextScale() + 0.1F, this.getTextScale() + 0.1F, 0.0F);   
            this.mc.fontRenderer.drawString(this.dayStr, 0, 0, this.getEnabledTextColor(), false); 
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();           
            GlStateManager.translate(2.0F, this.getHeight() - 7.0F, 0.0F);            
            GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F);   
            this.mc.fontRenderer.drawString(this.descriptionStr, 0, 0, this.getDisabledTextColor(), false); 
            GlStateManager.popMatrix();

            if (this.nextReward) {
                GlStateManager.pushMatrix();           
                GlStateManager.translate(2.0F, 11.0F, 0.0F);            
                GlStateManager.scale(this.getTextScale(), this.getTextScale(), 0.0F);   
                this.mc.fontRenderer.drawString(!this.rewarded && !this.locked ? this.rewardAvailableStr : this.nextRewardStr, 0, 0, this.getDisabledTextColor(), false); 
                GlStateManager.popMatrix();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (this.special) {
                GlStateManager.enableBlend(); 
                this.mc.getTextureManager().bindTexture(OxygenGUITextures.STAR_ICONS);                        
                GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 10, 2, 0, 0, 8, 8, 24, 8);     
                GlStateManager.disableBlend(); 
            }

            if (this.locked && !this.unreachable && !this.rewarded) {
                GlStateManager.enableBlend(); 
                this.mc.getTextureManager().bindTexture(OxygenGUITextures.LOCK_ICONS);                        
                GUIAdvancedElement.drawCustomSizedTexturedRect(this.getWidth() - 10, this.getHeight() - 10, 0, 0, 8, 8, 24, 8);     
                GlStateManager.disableBlend(); 
            }

            GlStateManager.popMatrix();

            this.reward.draw(this, mouseX, mouseY);
        }
    }

    @Override
    public void drawTooltip(int mouseX, int mouseY) {
        if (this.reward.getItemStack() != null && mouseX >= this.getX() + 32 && mouseY >= this.getY() + 32 && mouseX < this.getX() + 64 && mouseY < this.getY() + 64)
            this.screen.drawToolTip(this.reward.getItemStack(), mouseX + 4, mouseY);
        else if (this.hasTooltip() && mouseX >= this.getX() + 40 && mouseY >= this.getY() + 40 && mouseX < this.getX() + 56 && mouseY < this.getY() + 56) {
            float 
            width = this.textWidth(this.getTooltipText(), this.getTooltipScaleFactor()) + 6.0F,
            height = 9.0F;

            GlStateManager.pushMatrix();            
            GlStateManager.translate((this.getX() + this.getWidth() / 2.0F) - (width / 2.0F), this.getY() + (this.getHeight() / 2) - 24.0F, 0.0F);            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);   

            //background
            drawRect(0, 0, (int) width, (int) height, this.getTooltipBackgroundColor());

            //frame
            OxygenGUIUtils.drawRect(0.0D, 0.0D, 0.3D, height, this.getDebugColor());
            OxygenGUIUtils.drawRect(width - 0.4D, 0.0D, width, height, this.getDebugColor());
            OxygenGUIUtils.drawRect(0.0D, 0.0D, width, 0.4D, this.getDebugColor());
            OxygenGUIUtils.drawRect(0.0D, height - 0.4D, width, height, this.getDebugColor());

            GlStateManager.pushMatrix();            
            GlStateManager.translate((width - this.textWidth(this.getTooltipText(), this.getTooltipScaleFactor())) / 2.0F, (height - this.textHeight(this.getTooltipScaleFactor())) / 2.0F + 1.0F, 0.0F);            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);   
            GlStateManager.scale(this.getTooltipScaleFactor(), this.getTooltipScaleFactor(), 0.0F);  

            this.mc.fontRenderer.drawString(this.getTooltipText(), 0, 0, this.getTooltipTextColor(), false);

            GlStateManager.popMatrix(); 

            GlStateManager.popMatrix(); 
        }  
    }
}
