package austeretony.oxygen_dailyrewards.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_dailyrewards.common.reward.EnumReward;
import austeretony.oxygen_dailyrewards.common.reward.Reward;
import io.netty.buffer.ByteBuf;

public class RewardsDataContainerClient {

    private final DailyRewardsManagerClient manager;

    private final List<Reward> rewards = new ArrayList<>(31);

    public RewardsDataContainerClient(DailyRewardsManagerClient manager) {
        this.manager = manager;
    }

    public List<Reward> getRewards() {
        return this.rewards;
    }

    @Nullable
    public Reward getDailyReward(int day) {
        Reward reward = null;
        try {
            reward = this.rewards.get(day - 1);
        } catch (IndexOutOfBoundsException exception) {
            OxygenMain.LOGGER.error("[Daily Rewards] Reward index <{}> out of bounds!", day);
            exception.printStackTrace();
        }
        return reward;
    }

    public void rewardsDataReceived(ByteBuf buffer) {
        this.rewards.clear();
        try {
            EnumReward enumReward;
            int amount = buffer.readByte();
            for (int i = 0; i < amount; i++) {
                enumReward = EnumReward.values()[buffer.readByte()];
                this.rewards.add(enumReward.read(buffer));
            }
            OxygenMain.LOGGER.info("[Daily Rewards] Rewards data synchronized.");
        } finally {
            if (buffer != null)
                buffer.release();
        }
    }
}
