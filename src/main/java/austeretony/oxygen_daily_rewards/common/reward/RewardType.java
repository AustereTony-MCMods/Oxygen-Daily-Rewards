package austeretony.oxygen_daily_rewards.common.reward;

import austeretony.oxygen_core.client.util.MinecraftClient;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;

public enum RewardType {

    ITEM_STACK("oxygen_daily_rewards.reward_type.item_stack") {
        @Override
        public Reward fromJson(JsonElement jsonElement) {
            return RewardItemStack.fromJson(jsonElement);
        }

        @Override
        public Reward read(ByteBuf buffer) {
            return RewardItemStack.read(buffer);
        }
    },
    CURRENCY("oxygen_daily_rewards.reward_type.currency") {
        @Override
        public Reward fromJson(JsonElement jsonElement) {
            return RewardCurrency.fromJson(jsonElement);
        }

        @Override
        public Reward read(ByteBuf buffer) {
            return RewardCurrency.read(buffer);
        }
    },
    COMMAND("oxygen_daily_rewards.reward_type.command") {
        @Override
        public Reward fromJson(JsonElement jsonElement) {
            return RewardCommand.fromJson(jsonElement);
        }

        @Override
        public Reward read(ByteBuf buffer) {
            return RewardCommand.read(buffer);
        }
    };

    private final String description;

    RewardType(String description) {
        this.description = description;
    }

    public String getLocalizedDescription() {
        return MinecraftClient.localize(description);
    }

    public abstract Reward fromJson(JsonElement jsonElement);

    public abstract Reward read(ByteBuf buffer);
}
