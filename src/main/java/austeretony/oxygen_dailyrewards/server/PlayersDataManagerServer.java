package austeretony.oxygen_dailyrewards.server;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.main.DailyRewardsMain;
import austeretony.oxygen_dailyrewards.common.main.EnumDailyRewardsStatusMessage;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayersDataManagerServer {

    private final DailyRewardsManagerServer manager;

    public PlayersDataManagerServer(DailyRewardsManagerServer manager) {
        this.manager = manager;
    }

    public void playerLoaded(EntityPlayerMP playerMP) {
        final Runnable task = ()->{
            UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
            RewardsPlayerDataServer playerData = this.manager.getPlayerDataContainer().createPlayerData(playerUUID);

            OxygenMain.network().sendTo(new CPSyncPlayerData(
                    playerData.getDaysRewarded(), 
                    playerData.getLastRewardTimeMillis()), playerMP);

            if (playerData.isRewardAvailable(playerUUID))
                OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_AVAILABLE.ordinal());
        };
        OxygenHelperServer.addRoutineTask(task);
    }

    public void claimReward(EntityPlayerMP playerMP) {
        RewardsPlayerDataServer playerData = this.manager.getPlayerDataContainer().getPlayerData(CommonReference.getPersistentUUID(playerMP));
        if (playerData != null) {
            if (playerData.isRewardAvailable(CommonReference.getPersistentUUID(playerMP))) {
                Reward reward = this.manager.getRewardsDataContainer().getDailyReward(playerData.getDaysRewarded() + 1);
                if (reward != null) {
                    if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [1]: trying reward player for day {}...",
                                CommonReference.getName(playerMP), 
                                CommonReference.getPersistentUUID(playerMP),
                                playerData.getDaysRewarded() + 1);

                    if (reward.rewardPlayer(playerMP)) {
                        playerData.rewarded();
                        playerData.setChanged(true);

                        OxygenMain.network().sendTo(new CPSyncPlayerData(
                                playerData.getDaysRewarded(), 
                                playerData.getLastRewardTimeMillis()), playerMP);

                        OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_CLAIMED.ordinal());
                        return;
                    } else if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
                        OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> failed to reward player for day {}.",
                                CommonReference.getName(playerMP), 
                                CommonReference.getPersistentUUID(playerMP),
                                playerData.getDaysRewarded() + 1);
                }
            } else
                OxygenHelperServer.sendStatusMessage(playerMP, DailyRewardsMain.DAILY_REWARDS_MOD_INDEX, EnumDailyRewardsStatusMessage.DAILY_REWARD_UNAVAILABLE.ordinal());
        }
    }
}
