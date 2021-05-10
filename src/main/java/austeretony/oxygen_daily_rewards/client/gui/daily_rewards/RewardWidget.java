package austeretony.oxygen_daily_rewards.client.gui.daily_rewards;

import austeretony.oxygen_core.client.gui.base.Alignment;
import austeretony.oxygen_core.client.gui.base.GUIUtils;
import austeretony.oxygen_core.client.gui.base.Textures;
import austeretony.oxygen_core.client.gui.base.block.Texture;
import austeretony.oxygen_core.client.gui.base.core.Widget;
import austeretony.oxygen_core.client.settings.CoreSettings;
import austeretony.oxygen_daily_rewards.common.reward.DailyReward;

public class RewardWidget extends Widget<RewardWidget> {

    private final DailyReward reward;
    private final boolean gotThisReward, nextDayReward, locked, unreachable;

    private static final Texture STAR_ICON_TEXTURE = Texture.builder()
            .texture(Textures.STAR_ICONS)
            .size(6, 6)
            .imageSize(18, 6)
            .build();
    private static final Texture LOCK_ICON_TEXTURE = Texture.builder()
            .texture(Textures.LOCK_ICONS)
            .size(6, 6)
            .imageSize(18, 6)
            .build();

    public RewardWidget(int x, int y, DailyReward reward, boolean gotThisReward, boolean nextDayReward, boolean locked,
                        boolean unreachable) {
        setPosition(x, y);
        setSize(DailyRewardsScreen.REWARD_WIDGET_SIZE, DailyRewardsScreen.REWARD_WIDGET_SIZE);
        this.reward = reward;
        this.gotThisReward = gotThisReward;
        this.nextDayReward = nextDayReward;
        this.locked = locked;
        this.unreachable = unreachable;

        setEnabled(true);
        setVisible(true);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (!isVisible()) return;
        GUIUtils.pushMatrix();
        GUIUtils.translate(getX(), getY());

        GUIUtils.enableBlend();
        GUIUtils.drawRect(0, 0, getWidth(), getHeight(), CoreSettings.COLOR_BACKGROUND_BASE.asInt());
        reward.getItem().drawBackground(this, mouseX, mouseY);
        if (gotThisReward) {
            GUIUtils.drawGradientRect(0, 0, getWidth(), getHeight() - 10,
                    0x000000, CoreSettings.COLOR_ELEMENT_ACTIVE.asInt(), Alignment.TOP);
        } else if (unreachable) {
            GUIUtils.drawGradientRect(0, 0, getWidth(), getHeight() - 10,
                    0x000000, CoreSettings.COLOR_ELEMENT_INACTIVE.asInt(), Alignment.TOP);
        } else if (nextDayReward) {
            GUIUtils.drawGradientRect(0, 0, getWidth(), getHeight() - 10,
                    0x000000, CoreSettings.COLOR_ELEMENT_SPECIAL.asInt(), Alignment.TOP);
        }
        GUIUtils.drawFrame(0, 0, getWidth(), getHeight());
        GUIUtils.disableBlend();

        float textScale = CoreSettings.SCALE_TEXT_ADDITIONAL.asFloat();
        GUIUtils.drawString(localize("oxygen_daily_rewards.gui.daily_reward.widget.day", reward.getDay()), 2, 2,
                textScale, CoreSettings.COLOR_TEXT_BASE_ENABLED.asInt(), false);

        textScale -= .05F;
        if (nextDayReward) {
            String hintStr = !gotThisReward && !locked ? localize("oxygen_daily_rewards.gui.daily_reward.widget.available_reward")
                    : localize("oxygen_daily_rewards.gui.daily_reward.widget.next_reward");
            GUIUtils.drawString(hintStr, 2, 9, textScale, CoreSettings.COLOR_TEXT_ADDITIONAL_ENABLED.asInt(), false);
        }
        GUIUtils.drawString(reward.getType().getLocalizedDescription(), 2, getHeight() - 2 - GUIUtils.getTextHeight(textScale),
                textScale, CoreSettings.COLOR_TEXT_ADDITIONAL_ENABLED.asInt(), false);

        if (reward.isSpecial()) {
            GUIUtils.colorDef();
            GUIUtils.drawTexturedRect(getWidth() - 8, 2, STAR_ICON_TEXTURE);
        }
        if (locked && !unreachable) {
            GUIUtils.colorDef();
            GUIUtils.drawTexturedRect(getWidth() - 8, getHeight() - 8, LOCK_ICON_TEXTURE);
        }
        reward.getItem().draw(this, mouseX, mouseY);

        GUIUtils.popMatrix();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        if (!isVisible()) return;
        reward.getItem().drawForeground(this, mouseX, mouseY);
    }
}
