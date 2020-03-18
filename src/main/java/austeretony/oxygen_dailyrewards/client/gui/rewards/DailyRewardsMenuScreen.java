package austeretony.oxygen_dailyrewards.client.gui.rewards;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.alternateui.util.EnumGUIAlignment;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuEntry;
import austeretony.oxygen_dailyrewards.client.gui.menu.DailyRewardsMenuEntry;
import austeretony.oxygen_dailyrewards.client.settings.gui.EnumDailyRewardsGUISetting;

public class DailyRewardsMenuScreen extends AbstractGUIScreen {

    public static final OxygenMenuEntry DAILY_REWARDS_MENU_ENTRY = new DailyRewardsMenuEntry();

    private RewardsSection rewardsSection;

    @Override
    protected GUIWorkspace initWorkspace() {
        EnumGUIAlignment alignment = EnumGUIAlignment.CENTER;
        switch (EnumDailyRewardsGUISetting.DAILY_REWARDS_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            alignment = EnumGUIAlignment.LEFT;
            break;
        case 0:
            alignment = EnumGUIAlignment.CENTER;
            break;
        case 1:
            alignment = EnumGUIAlignment.RIGHT;
            break;    
        default:
            alignment = EnumGUIAlignment.CENTER;
            break;
        }
        return new GUIWorkspace(this, 255, 116).setAlignment(alignment, 0, 0);
    }

    @Override
    protected void initSections() {
        this.getWorkspace().initSection(this.rewardsSection = new RewardsSection(this));
    }

    @Override
    protected AbstractGUISection getDefaultSection() {
        return this.rewardsSection;
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element) {}

    @Override
    protected boolean doesGUIPauseGame() {
        return false;
    }

    public void rewardClaimed() {
        this.rewardsSection.rewardClaimed();
    }
}
