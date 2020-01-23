package austeretony.oxygen_dailyrewards.client;

import austeretony.oxygen_core.common.chat.ChatMessagesHandler;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;

public class DailyRewardsStatusMessagesHandler implements ChatMessagesHandler {

    @Override
    public int getModIndex() {
        return DailyRewardsMain.DAILY_REWARDS_MOD_INDEX;
    }

    @Override
    public String getMessage(int messageIndex) {
        if (messageIndex == EnumDailyRewardsStatusMessage.DAILY_REWARD_CLAIMED.ordinal())//dirty hack
            DailyRewardsManagerClient.instance().getMenuManager().rewardClaimed();
        return EnumDailyRewardsStatusMessage.values()[messageIndex].localized();
    }
}
