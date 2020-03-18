package austeretony.oxygen_dailyrewards.client;

import austeretony.oxygen_core.client.chat.MessageFormatter;
import austeretony.oxygen_core.common.chat.ChatMessagesHandler;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class DailyRewardsStatusMessagesHandler implements ChatMessagesHandler {

    private final MessageFormatter formatter = (messageIndex, args)->{
        if (messageIndex == EnumDailyRewardsStatusMessage.DAILY_REWARD_AVAILABLE.ordinal()) {
            ITextComponent 
            message = new TextComponentTranslation("oxygen_dailyrewards.status.message.dailyRewardAvailable"),
            command = new TextComponentTranslation("oxygen_dailyrewards.status.message.clickHere");

            message.getStyle().setItalic(true);
            message.getStyle().setColor(TextFormatting.AQUA);
            command.getStyle().setItalic(true);
            command.getStyle().setUnderlined(true);
            command.getStyle().setColor(TextFormatting.WHITE);
            command.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/oxygenc dailyrewards"));

            return message.appendSibling(command);
        }
        return null;
    };

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

    @Override
    public MessageFormatter getMessageFormatter() {
        return this.formatter;
    }
}
