package austeretony.oxygen_dailyrewards.client.gui.rewards;

import java.time.Duration;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import org.lwjgl.input.Keyboard;

import austeretony.alternateui.screen.core.AbstractGUISection;
import austeretony.alternateui.screen.core.GUIBaseElement;
import austeretony.alternateui.screen.framework.GUIElementsFramework;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.api.PrivilegesProviderClient;
import austeretony.oxygen_core.client.api.TimeHelperClient;
import austeretony.oxygen_core.client.gui.elements.OxygenDefaultBackgroundWithButtonsFiller;
import austeretony.oxygen_core.client.gui.elements.OxygenKeyButton;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsPrivilege;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class RewardsSection extends AbstractGUISection {

    private static final int 
    WIDGETS_PER_LIST = 7,
    WIDGET_WIDTH = 48, 
    WIDGET_HEIGHT = 48;

    private OxygenTextLabel nextRewardTimeLabel;

    private OxygenKeyButton prevWeekButton, nextWeekButton, claimRewardButton;

    private GUIElementsFramework framework;

    //cache

    private int currentDay, daysRewarded, totalRewardsAmount, listedWeek, maxListedWeek;

    private boolean initialized, rewardClaimed;

    public RewardsSection(DailyRewardsMenuScreen screen) {
        super(screen);
    }

    @Override
    public void init() {
        this.addElement(new OxygenDefaultBackgroundWithButtonsFiller(0, 0, this.getWidth(), this.getHeight()));

        String menuTitle = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.menu.title");
        this.currentDay = 0;
        this.totalRewardsAmount = DailyRewardsManagerClient.instance().getRewardsDataContainer().getRewards().size();
        if (DailyRewardsConfig.REWARD_MODE.asInt() == 0) {
            ZonedDateTime currentTime = TimeHelperClient.getServerZonedDateTime();
            this.currentDay = currentTime.getDayOfMonth();

            Month month = currentTime.getMonth();
            if (month == Month.FEBRUARY)
                this.totalRewardsAmount = Month.FEBRUARY.length(currentTime.toLocalDate().isLeapYear());
            else
                this.totalRewardsAmount = month.minLength();

            String monthName = ClientReference.localize("oxygen_core.month." + month.getDisplayName(TextStyle.FULL, Locale.ROOT).toLowerCase());
            menuTitle = ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.title", monthName);
        }
        this.maxListedWeek = this.totalRewardsAmount / 7 + (this.totalRewardsAmount % 7 == 0 ? 0 : 1);

        this.addElement(new OxygenTextLabel(4, 12, menuTitle, EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.claimRewardButton = new OxygenKeyButton(0, this.getY() + this.getHeight() + this.screen.guiTop - 8, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.button.claimReward"), Keyboard.KEY_E, this::claimReward).disable());     
        this.addElement(this.nextRewardTimeLabel = new OxygenTextLabel(this.getWidth(), this.getY() + this.getHeight() + this.screen.guiTop - 1, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.prevWeekButton = new OxygenKeyButton(0, this.getY() + this.getHeight() + this.screen.guiTop - 8, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.button.prevWeek"), Keyboard.KEY_A, this::switchWeekBack).disable());     
        this.addElement(this.nextWeekButton = new OxygenKeyButton(0, this.getY() + this.getHeight() + this.screen.guiTop - 8, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.button.nextWeek"), Keyboard.KEY_D, this::switchWeekForward).disable());     

        this.addElement(this.framework = new GUIElementsFramework(this.screen, 6, 16, WIDGET_WIDTH * 3 + 3 + WIDGET_WIDTH * 2, WIDGET_HEIGHT * 2 + 1));

        this.rewardClaimed = !DailyRewardsManagerClient.instance().getPlayerData().isRewardAvailable();
        this.daysRewarded = DailyRewardsManagerClient.instance().getPlayerData().getDaysRewarded();

        this.listedWeek = this.daysRewarded / WIDGETS_PER_LIST;
        this.listRewards(this.listedWeek * WIDGETS_PER_LIST + 1);

        this.updateButtonsState();
    }

    private void calculateButtonsHorizontalPosition() {
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.claimRewardButton.setX((sr.getScaledWidth() - (12 + this.textWidth(this.claimRewardButton.getDisplayText(), this.claimRewardButton.getTextScale()))) / 2 - this.screen.guiLeft);

        this.prevWeekButton.setX(sr.getScaledWidth() / 2 - 50 - (12 + this.textWidth(this.prevWeekButton.getDisplayText(), this.prevWeekButton.getTextScale())) - this.screen.guiLeft);
        this.nextWeekButton.setX(sr.getScaledWidth() / 2 + 50 - this.screen.guiLeft);
    }

    private void listRewards(int fromDay) {
        this.framework.getElements().clear();
        Reward reward;
        int 
        index = 0,
        maximumRewards = PrivilegesProviderClient.getAsInt(EnumDailyRewardsPrivilege.MAXIMUM_REWARDS_AMOUNT_PER_MONTH.id(), DailyRewardsConfig.MAXIMUM_REWARDS_PER_MONTH.asInt());
        boolean rewarded, nextReward, locked, unreachable;
        for (int i = fromDay; i < fromDay + WIDGETS_PER_LIST; i++) {
            if (i > this.totalRewardsAmount) break;

            reward = DailyRewardsManagerClient.instance().getRewardsDataContainer().getDailyReward(i);
            if (reward != null) {
                rewarded = i <= this.daysRewarded;
                nextReward = i == this.daysRewarded + 1 && i <= this.totalRewardsAmount - this.currentDay + 1;
                locked = this.rewardClaimed || i > this.daysRewarded + 1;
                unreachable = i > (this.totalRewardsAmount - this.currentDay + this.daysRewarded + (this.rewardClaimed ? 0 : 1)) || i > maximumRewards;

                if (index < 3)
                    this.framework.addElement(new DailyRewardWidget(6 + index++ * (WIDGET_WIDTH + 1), 16, reward, rewarded, nextReward, locked, unreachable));
                else if (index < 6)
                    this.framework.addElement(new DailyRewardWidget(6 + (index++ - 3) * (WIDGET_WIDTH + 1), 16 + WIDGET_HEIGHT + 1, reward, rewarded, nextReward, locked, unreachable));
                else if (index == 6)
                    this.framework.addElement(new DailyRewardWidgetBig(6 + 3 * (WIDGET_WIDTH + 1), 16, reward, rewarded, nextReward, locked, unreachable));
            }
        }
    }

    private void updateButtonsState() {
        this.claimRewardButton.setEnabled(!this.rewardClaimed);

        this.prevWeekButton.setEnabled(this.listedWeek != 0);
        this.nextWeekButton.setEnabled(this.listedWeek < (this.maxListedWeek - 1));
    }

    private void claimReward() {
        Minecraft.getMinecraft().player.sendChatMessage("/oxygens dailyrewards -claim");
    }

    private void switchWeekBack() {
        this.listRewards(WIDGETS_PER_LIST * --this.listedWeek + 1);
        this.updateButtonsState();
    }

    private void switchWeekForward() {
        this.listRewards(WIDGETS_PER_LIST * ++this.listedWeek + 1);
        this.updateButtonsState();
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.claimRewardButton)
                this.claimReward();
            else if (element == this.prevWeekButton)
                this.switchWeekBack();
            else if (element == this.nextWeekButton)
                this.switchWeekForward();
        }
    }

    @Override
    public void update() {
        if (this.rewardClaimed && this.mc.player.ticksExisted % 20 == 0) {
            Duration duration = DailyRewardsManagerClient.instance().getPlayerData().getTimeLeftUntilNextReward(); 
            if (duration.getSeconds() <= 0L) {
                this.rewardClaimed = false;
                this.nextRewardTimeLabel.setDisplayText("");
                this.listRewards(this.listedWeek * WIDGETS_PER_LIST + 1);
                this.updateButtonsState();
                return;
            }

            this.nextRewardTimeLabel.setDisplayText(String.format("%d:%02d:%02d %s", duration.getSeconds() / 3600, (duration.getSeconds() % 3600) / 60, (duration.getSeconds() % 60), 
                    this.currentDay == this.totalRewardsAmount ? ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.nextMonthBegins") : ""));

            ScaledResolution sr = new ScaledResolution(this.mc);
            this.nextRewardTimeLabel.setX(sr.getScaledWidth() - this.screen.guiLeft - 4 - this.textWidth(this.nextRewardTimeLabel.getDisplayText(), this.nextRewardTimeLabel.getTextScale()));
        }  

        if (!this.initialized) {
            this.initialized = true;
            this.calculateButtonsHorizontalPosition();
        }
    }

    public void rewardClaimed() {
        this.rewardClaimed = true;
        this.daysRewarded++;
        this.listedWeek = (this.daysRewarded + 1) / WIDGETS_PER_LIST;
        this.listRewards(DailyRewardsConfig.REWARD_MODE.asInt() == 0 ? this.listedWeek * WIDGETS_PER_LIST + 1 : 1);

        this.updateButtonsState();
    }
}
