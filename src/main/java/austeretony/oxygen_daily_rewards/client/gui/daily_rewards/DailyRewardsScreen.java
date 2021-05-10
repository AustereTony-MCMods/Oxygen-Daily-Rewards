package austeretony.oxygen_daily_rewards.client.gui.daily_rewards;

import austeretony.oxygen_core.client.gui.base.Alignment;
import austeretony.oxygen_core.client.gui.base.core.OxygenScreen;
import austeretony.oxygen_core.client.gui.base.core.Section;
import austeretony.oxygen_core.client.gui.base.core.Workspace;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_core.client.util.MinecraftClient;
import austeretony.oxygen_daily_rewards.client.gui.menu.DailyRewardsMenuEntry;
import austeretony.oxygen_daily_rewards.client.settings.DailyRewardsSettings;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsMain;
import net.minecraft.client.gui.GuiScreen;

public class DailyRewardsScreen extends OxygenScreen {

    public static final OxygenMenuEntry DAILY_REWARDS_MENU_ENTRY = new DailyRewardsMenuEntry();
    public static final int REWARD_WIDGET_SIZE = 40;

    private DailyRewardsSection section;

    @Override
    public int getScreenId() {
        return DailyRewardsMain.SCREEN_ID_DAILY_REWARDS;
    }

    @Override
    public Workspace createWorkspace() {
        Workspace workspace = new Workspace(this, 6 * 2 + REWARD_WIDGET_SIZE * 7 + 6,
                14 + REWARD_WIDGET_SIZE * 5 + 6 + 2);
        workspace.setAlignment(Alignment.valueOf(DailyRewardsSettings.DAILY_REWARDS_SCREEN_ALIGNMENT.asString()), 0, 0);
        return workspace;
    }

    @Override
    public void addSections() {
        getWorkspace().addSection(section = new DailyRewardsSection(this));
    }

    @Override
    public Section getDefaultSection() {
        return section;
    }

    public static void open() {
        MinecraftClient.displayGuiScreen(new DailyRewardsScreen());
    }

    public static void rewardClaimed() {
        GuiScreen screen = MinecraftClient.getCurrentScreen();
        if (screen instanceof DailyRewardsScreen) {
            ((DailyRewardsScreen) screen).section.rewardClaimed();
        }
    }
}
