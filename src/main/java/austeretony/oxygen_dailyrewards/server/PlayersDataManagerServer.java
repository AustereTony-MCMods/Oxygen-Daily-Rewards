package austeretony.oxygen_dailyrewards.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class PlayersDataManagerServer {

    private final DailyRewardsManagerServer manager;

    public PlayersDataManagerServer(DailyRewardsManagerServer manager) {
        this.manager = manager;
    }

    public void onPlayerLoaded(EntityPlayerMP playerMP) {
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        RewardsPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(playerUUID);
        if (playerData == null)
            playerData = this.manager.getPlayerDataContainer().createPlayerData(playerUUID);
        playerData.init();

        OxygenMain.network().sendTo(new CPSyncPlayerData(
                playerData.getDaysRewarded(), 
                playerData.getLastRewardTimeMillis()), playerMP);

        if (playerData.isRewardAvailable(playerUUID)) {
            ITextComponent 
            message = new TextComponentTranslation("oxygen_dailyrewards.message.dailyRewardAvailable"),
            component = new TextComponentTranslation("oxygen_dailyrewards.message.clickHere");
            message.getStyle().setItalic(true);
            message.getStyle().setColor(TextFormatting.AQUA);
            component.getStyle().setItalic(true);
            component.getStyle().setUnderlined(true);
            component.getStyle().setColor(TextFormatting.WHITE);
            component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/oxygenc dailyrewards"));

            playerMP.sendMessage(message.appendSibling(component));
        }
    }

    public void claimReward(EntityPlayerMP playerMP) {
        RewardsPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(CommonReference.getPersistentUUID(playerMP));
        if (playerData != null) {
            if (playerData.isRewardAvailable(CommonReference.getPersistentUUID(playerMP))) {
                this.manager.getRewardsDataContainer().getDailyReward(playerData.getDaysRewarded() + 1).rewardPlayer(playerMP);
                playerData.rewarded();

                playerData.setChanged(true);

                OxygenMain.network().sendTo(new CPSyncPlayerData(
                        playerData.getDaysRewarded(), 
                        playerData.getLastRewardTimeMillis()), playerMP);

                OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_CLAIMED.ordinal());
            } else
                OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_UNAVAILABLE.ordinal());
        }
    }
}
