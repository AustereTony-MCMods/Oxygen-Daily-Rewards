package austeretony.oxygen_dailyrewards.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.EnumOxygenStatusMessage;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.OxygenManagerServer;
import austeretony.oxygen_core.server.api.InventoryProviderServer;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_dailyrewards.common.reward.EnumReward;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import net.minecraft.entity.player.EntityPlayerMP;

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

        OxygenMain.network().sendTo(new CPSyncPlayerData(
                playerData.getDaysRewarded(), 
                playerData.getLastRewardTimeMillis()), playerMP);

        if (playerData.isRewardAvailable(playerUUID))
            OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_AVAILABLE.ordinal());
    }

    public void claimReward(EntityPlayerMP playerMP) {
        RewardsPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(CommonReference.getPersistentUUID(playerMP));
        if (playerData != null) {
            if (playerData.isRewardAvailable(CommonReference.getPersistentUUID(playerMP))) {
                Reward reward = this.manager.getRewardsDataContainer().getDailyReward(playerData.getDaysRewarded() + 1);
                if (reward != null) {
                    if (reward.getType() != EnumReward.CURRENCY
                            && InventoryProviderServer.getPlayerInventory().getEmptySlotsAmount(playerMP) < reward.getAmount()) {
                        OxygenManagerServer.instance().sendStatusMessage(playerMP, EnumOxygenStatusMessage.INVENTORY_FULL);
                        return;
                    }

                    if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [1]: rewarding player for day {}...",
                                CommonReference.getName(playerMP), 
                                CommonReference.getPersistentUUID(playerMP),
                                playerData.getDaysRewarded() + 1);

                    reward.rewardPlayer(playerMP);
                    playerData.rewarded();

                    playerData.setChanged(true);

                    OxygenMain.network().sendTo(new CPSyncPlayerData(
                            playerData.getDaysRewarded(), 
                            playerData.getLastRewardTimeMillis()), playerMP);

                    OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_CLAIMED.ordinal());
                }
            } else
                OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_UNAVAILABLE.ordinal());
        }
    }
}
