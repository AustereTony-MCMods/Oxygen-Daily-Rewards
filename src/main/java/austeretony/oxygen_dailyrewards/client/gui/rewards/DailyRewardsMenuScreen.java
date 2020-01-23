package austeretony.oxygen_dailyrewards.client.gui.rewards;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import austeretony.alternateui.screen.core.AbstractGUIScreen;
import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.core.GUIWorkspace;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import net.minecraft.util.ResourceLocation;

public class DailyRewardsMenuScreen extends AbstractGUIScreen {

    public static final DateTimeFormatter TIMER_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:dd", Locale.ENGLISH);

    public static final ResourceLocation 
    SPECIAL_ICON = new ResourceLocation(DailyRewardsMain.MODID, "textures/gui/special_icon.png"),
    LOCKED_ICON = new ResourceLocation(DailyRewardsMain.MODID, "textures/gui/locked_icon.png");

    private RewardsSection rewardsSection;

    @Override
    protected GUIWorkspace initWorkspace() {
        return new GUIWorkspace(this, 12 + 48 * 5 + 3, 160);
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
