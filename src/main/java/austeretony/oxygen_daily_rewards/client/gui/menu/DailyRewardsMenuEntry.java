package austeretony.oxygen_daily_rewards.client.gui.menu;

import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_core.client.util.MinecraftClient;
import austeretony.oxygen_daily_rewards.client.settings.DailyRewardsSettings;
import austeretony.oxygen_daily_rewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import net.minecraft.util.ResourceLocation;

public class DailyRewardsMenuEntry implements OxygenMenuEntry {

    private static final ResourceLocation ICON = new ResourceLocation(DailyRewardsMain.MOD_ID,
            "textures/gui/menu/daily_rewards.png");

    @Override
    public int getScreenId() {
        return DailyRewardsMain.SCREEN_ID_DAILY_REWARDS;
    }

    @Override
    public String getDisplayName() {
        return MinecraftClient.localize("oxygen_daily_rewards.gui.daily_rewards.title");
    }

    @Override
    public int getPriority() {
        return 7000;
    }

    @Override
    public ResourceLocation getIconTexture() {
        return ICON;
    }

    @Override
    public int getKeyCode() {
        return DailyRewardsConfig.DAILY_REWARDS_SCREEN_KEY.asInt();
    }

    @Override
    public boolean isValid() {
        return DailyRewardsSettings.ADD_DAILY_REWARDS_SCREEN_TO_OXYGEN_MENU.asBoolean();
    }
}
