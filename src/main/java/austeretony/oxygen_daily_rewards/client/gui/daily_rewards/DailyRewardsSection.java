package austeretony.oxygen_daily_rewards.client.gui.daily_rewards;

import austeretony.oxygen_core.client.api.OxygenClient;
import austeretony.oxygen_core.client.gui.base.GUIUtils;
import austeretony.oxygen_core.client.gui.base.Keys;
import austeretony.oxygen_core.client.gui.base.Layer;
import austeretony.oxygen_core.client.gui.base.Texts;
import austeretony.oxygen_core.client.gui.base.background.Background;
import austeretony.oxygen_core.client.gui.base.common.WidgetGroup;
import austeretony.oxygen_core.client.gui.base.core.Section;
import austeretony.oxygen_core.client.gui.base.special.KeyButton;
import austeretony.oxygen_core.client.gui.base.text.TextLabel;
import austeretony.oxygen_core.client.gui.util.OxygenGUIUtils;
import austeretony.oxygen_core.client.util.MinecraftClient;
import austeretony.oxygen_daily_rewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_daily_rewards.common.player.PlayerData;
import austeretony.oxygen_daily_rewards.common.reward.DailyReward;
import net.minecraft.client.gui.ScaledResolution;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DailyRewardsSection extends Section {

    private WidgetGroup widgetGroup;
    private KeyButton claimRewardButton;
    private TextLabel timeTillNextRewardLabel;

    private ScaledResolution sr;
    private ZonedDateTime serverTime;
    private boolean gotDailyReward;

    public DailyRewardsSection(@Nonnull DailyRewardsScreen screen) {
        super(screen);
        sr = GUIUtils.getScaledResolution();
    }

    @Override
    public void init() {
        addWidget(new Background.UnderlinedTitleButtons(this));
        serverTime = OxygenClient.getServerZonedDateTime();
        String monthName = localize("oxygen.month." + serverTime.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ROOT).toLowerCase());
        String titleStr = localize("oxygen_daily_rewards.gui.daily_rewards.title_month", monthName);
        addWidget(new TextLabel(4, 12, Texts.title(titleStr)));

        addWidget(widgetGroup = new WidgetGroup());

        String keyButtonText = localize("oxygen_daily_rewards.gui.daily_rewards.button.claim_reward");
        addWidget(claimRewardButton = new KeyButton(0, 0, Keys.ACTION_KEY, keyButtonText)
                .setLayer(Layer.FRONT)
                .setPressListener(DailyRewardsManagerClient.instance()::claimReward)
                .setEnabled(DailyRewardsManagerClient.instance().isRewardAvailable()));
        OxygenGUIUtils.calculateBottomCenteredOffscreenButtonPosition(claimRewardButton, 1, 1);
        addWidget(timeTillNextRewardLabel = new TextLabel(0, 0, Texts.additional("")).setLayer(Layer.FRONT));

        listRewards();
    }

    private void listRewards() {
        widgetGroup.clear();

        PlayerData playerData = DailyRewardsManagerClient.instance().getClientPlayerData();
        List<DailyReward> rewardList = DailyRewardsManagerClient.instance().getRewardList();
        int monthLength = serverTime.getMonth().length(serverTime.toLocalDate().isLeapYear());
        int dayOfMonth = serverTime.getDayOfMonth();
        gotDailyReward = !DailyRewardsManagerClient.instance().isRewardAvailable();

        int y = -DailyRewardsScreen.REWARD_WIDGET_SIZE + 14;
        for (DailyReward reward : rewardList) {
            int modulo = (reward.getDay() - 1) % 7;
            int x = 6 + modulo * DailyRewardsScreen.REWARD_WIDGET_SIZE + modulo;
            if (modulo == 0) {
                y += DailyRewardsScreen.REWARD_WIDGET_SIZE + 1;
            }

            widgetGroup.addWidget(new RewardWidget(x, y, reward,
                    reward.getDay() <= playerData.getDaysRewarded(),
                    reward.getDay() == playerData.getDaysRewarded() + 1,
                    reward.getDay() > (gotDailyReward ? playerData.getDaysRewarded() : playerData.getDaysRewarded() + 1),
                    reward.getDay() - playerData.getDaysRewarded() > monthLength - dayOfMonth + (gotDailyReward ? 0 : 1)));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        OxygenGUIUtils.closeScreenOnKeyPress(getScreen(), keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void update() {
        super.update();
        if (!gotDailyReward || MinecraftClient.getClientPlayer().ticksExisted % 20 != 0) return;

        PlayerData playerData = DailyRewardsManagerClient.instance().getClientPlayerData();
        Instant nextRewardTime = Instant.ofEpochMilli(playerData.getLastTimeRewardedMillis())
                .plusMillis(TimeUnit.HOURS.toMillis(24L));
        Duration duration =  Duration.between(OxygenClient.getInstant(), nextRewardTime);

        String timeLeftStr = String.format("%d:%02d:%02d", duration.getSeconds() / 3600, (duration.getSeconds() % 3600) / 60,
                (duration.getSeconds() % 60));
        timeTillNextRewardLabel.getText().setText(timeLeftStr);

        int labelX = (int) (sr.getScaledWidth() - 10 - GUIUtils.getTextWidth(timeLeftStr, timeTillNextRewardLabel.getText().getScale()));
        int labelY = sr.getScaledHeight() - 2;
        timeTillNextRewardLabel.setPosition(labelX, labelY);
    }

    public void rewardClaimed() {
        listRewards();
        claimRewardButton.setEnabled(false);
    }
}
