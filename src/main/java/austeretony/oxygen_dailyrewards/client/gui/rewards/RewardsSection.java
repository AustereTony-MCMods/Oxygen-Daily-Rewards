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
import austeretony.oxygen_core.client.gui.elements.OxygenButton;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_dailyrewards.client.test.time.TimeHelperClient;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsPrivilege;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import net.minecraft.client.Minecraft;

public class RewardsSection extends AbstractGUISection {

    private static final int 
    WIDGETS_PER_LIST = 7,
    WIDGET_WIDTH = 48, 
    WIDGET_HEIGHT = 64;

    private OxygenTextLabel nextRewardTimeLabel;

    private OxygenButton prevButton, nextButton, claimButton;

    private GUIElementsFramework framework;

    //cache

    private int currentDayOfMonth, daysRewarded, monthLength, listedWeek;

    private boolean rewardClaimed;

    public RewardsSection(DailyRewardsMenuScreen screen) {
        super(screen);
    }

    @Override
    public void init() {
        ZonedDateTime currentTime = TimeHelperClient.getServerZonedDateTime();
        this.currentDayOfMonth = currentTime.getDayOfMonth();

        Month month = currentTime.getMonth();
        this.monthLength = month.minLength();
        if (month == Month.FEBRUARY && currentTime.toLocalDate().isLeapYear())
            this.monthLength = 29;

        this.addElement(new RewardsBackgroundFiller(0, 0, this.getWidth(), this.getHeight()));
        this.addElement(new OxygenTextLabel(4, 12, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.title", month.getDisplayName(TextStyle.FULL, Locale.ROOT)), EnumBaseGUISetting.TEXT_TITLE_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.claimButton = new OxygenButton(6, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.claimButton")).disable());     
        this.claimButton.setKeyPressListener(Keyboard.KEY_E, ()->this.claimReward());

        this.addElement(this.nextRewardTimeLabel = new OxygenTextLabel(50, this.getHeight() - 3, "", EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat(), EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        this.addElement(this.prevButton = new OxygenButton(this.getWidth() - 90, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.prevButton")).disable());     
        this.addElement(this.nextButton = new OxygenButton(this.getWidth() - 46, this.getHeight() - 11, 40, 10, ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.nextButton")).disable());     

        this.addElement(this.framework = new GUIElementsFramework(this.screen, 6, 16, WIDGET_WIDTH * 3 + 3 + WIDGET_WIDTH * 2, WIDGET_HEIGHT * 2 + 1));

        this.rewardClaimed = !DailyRewardsManagerClient.instance().getPlayerData().isRewardAvailable();
        this.daysRewarded = DailyRewardsManagerClient.instance().getPlayerData().getDaysRewarded();

        this.listedWeek = (this.daysRewarded + 1) / WIDGETS_PER_LIST;
        this.listRewards(this.listedWeek * WIDGETS_PER_LIST + 1);

        this.updateButtonsState();
    }

    private void listRewards(int fromDay) {
        this.framework.getElements().clear();
        Reward reward;
        int 
        index = 0,
        maximumRewards = PrivilegesProviderClient.getAsInt(EnumDailyRewardsPrivilege.MAXIMUM_REWARDS_AMOUNT.id(), DailyRewardsConfig.MAXIMUM_REWARDS_AMOUNT.asInt());
        boolean rewarded, nextReward, locked, unreachable;
        for (int i = fromDay; i < fromDay + WIDGETS_PER_LIST; i++) {
            if (i > this.monthLength) break;
            reward = DailyRewardsManagerClient.instance().getRewardsDataContainer().getDailyReward(i);
            rewarded = i <= this.daysRewarded;
            nextReward = i == this.daysRewarded + 1 && i <= this.monthLength - this.currentDayOfMonth + 1;
            locked = this.rewardClaimed || i > this.daysRewarded + 1;
            unreachable = i > (this.monthLength - this.currentDayOfMonth + this.daysRewarded + (this.rewardClaimed ? 0 : 1)) || (maximumRewards != - 1 && i > maximumRewards);
            if (index < 3)
                this.framework.addElement(new DailyRewardWidget(6 + index++ * (WIDGET_WIDTH + 1), 16, reward, rewarded, nextReward, locked, unreachable));
            else if (index < 6)
                this.framework.addElement(new DailyRewardWidget(6 + (index++ - 3) * (WIDGET_WIDTH + 1), 16 + WIDGET_HEIGHT + 1, reward, rewarded, nextReward, locked, unreachable));
            else if (index == 6)
                this.framework.addElement(new DailyRewardWidgetBig(6 + 3 * (WIDGET_WIDTH + 1), 16, reward, rewarded, nextReward, locked, unreachable));
        }
    }

    private void updateButtonsState() {
        this.claimButton.setEnabled(!this.rewardClaimed);

        this.prevButton.setEnabled(this.listedWeek != 0);
        this.nextButton.setEnabled(this.listedWeek < 4);
    }

    private void claimReward() {
        Minecraft.getMinecraft().player.sendChatMessage("/oxygens dailyrewards -claim");
    }

    @Override
    public void handleElementClick(AbstractGUISection section, GUIBaseElement element, int mouseButton) {
        if (mouseButton == 0) {
            if (element == this.claimButton)
                this.claimReward();
            else if (element == this.prevButton) {
                this.listRewards(WIDGETS_PER_LIST * --this.listedWeek + 1);
                this.updateButtonsState();
            } else if (element == this.nextButton) {
                this.listRewards(WIDGETS_PER_LIST * ++this.listedWeek + 1);
                this.updateButtonsState();
            }
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
                    this.currentDayOfMonth == this.monthLength ? ClientReference.localize("oxygen_dailyrewards.gui.dailyrewards.nextMonthBegins") : ""));
        }  
    }

    public void rewardClaimed() {
        this.rewardClaimed = true;
        this.daysRewarded++;
        this.listedWeek = (this.daysRewarded + 1) / WIDGETS_PER_LIST;
        this.listRewards(this.listedWeek * WIDGETS_PER_LIST + 1);

        this.updateButtonsState();
    }
}
