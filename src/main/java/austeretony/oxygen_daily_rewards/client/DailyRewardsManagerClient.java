package austeretony.oxygen_daily_rewards.client;

import austeretony.oxygen_core.client.api.OxygenClient;
import austeretony.oxygen_core.client.api.PrivilegesClient;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_daily_rewards.common.main.DailyRewardsPrivileges;
import austeretony.oxygen_daily_rewards.common.network.server.SPClaimReward;
import austeretony.oxygen_daily_rewards.common.player.PlayerData;
import austeretony.oxygen_daily_rewards.common.reward.DailyReward;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class DailyRewardsManagerClient {

    private static DailyRewardsManagerClient instance;

    private final List<DailyReward> rewardList = new ArrayList<>();
    private final PlayerData clientPlayerData = new PlayerData();

    private DailyRewardsManagerClient() {}

    public static DailyRewardsManagerClient instance() {
        if (instance == null)
            instance = new DailyRewardsManagerClient();
        return instance;
    }

    public List<DailyReward> getRewardList() {
        return rewardList;
    }

    public PlayerData getClientPlayerData() {
        return clientPlayerData;
    }

    public void playerDataSynchronized(long lastRewardMillis, int daysRewarded) {
        UUID playerUUID = OxygenClient.getClientPlayerUUID();
        clientPlayerData.setPlayerUUID(playerUUID);
        clientPlayerData.setFilePath(OxygenClient.getDataFolder() + "/players/" + playerUUID
                + "/daily_rewards/player_data.dat");

        clientPlayerData.setLastTimeRewardedMillis(lastRewardMillis);
        clientPlayerData.setDaysRewarded(daysRewarded);
        OxygenMain.logInfo(1, "[Daily Rewards] Player data synchronized.");
    }

    public void rewardsSynchronized(byte[] rewardsRaw) {
        rewardList.clear();
        ByteBuf buffer = null;
        try {
            buffer = Unpooled.wrappedBuffer(rewardsRaw);

            int amount = buffer.readShort();
            for (int i = 0; i < amount; i++) {
                rewardList.add(DailyReward.read(buffer));
            }
            OxygenMain.logInfo(1, "[Daily Rewards] Rewards data synchronized.");
        } finally {
            if (buffer != null) {
                buffer.release();
            }
        }
    }

    public boolean isRewardAvailable() {
        if (!PrivilegesClient.getBoolean(DailyRewardsPrivileges.DAILY_REWARDS_ACCESS.getId(), true)) {
            return false;
        }

        Instant nextRewardTime = Instant.ofEpochMilli(clientPlayerData.getLastTimeRewardedMillis())
                .plusMillis(TimeUnit.HOURS.toMillis(24L));
        return OxygenClient.getInstant().isAfter(nextRewardTime);
    }

    public void claimReward() {
        OxygenMain.network().sendToServer(new SPClaimReward());
    }
}
