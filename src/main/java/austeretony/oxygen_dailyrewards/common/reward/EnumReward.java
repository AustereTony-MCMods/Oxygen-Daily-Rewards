package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;

public enum EnumReward {

    ITEM() {

        @Override
        public Reward fromJson(JsonObject jsonObject) {
            return RewardItem.fromJson(jsonObject);
        }

        @Override
        public Reward read(ByteBuf buffer) {
            return RewardItem.read(buffer);
        }
    },
    CURRENCY() {

        @Override
        public Reward fromJson(JsonObject jsonObject) {
            return RewardCurrency.fromJson(jsonObject);
        }

        @Override
        public Reward read(ByteBuf buffer) {
            return RewardCurrency.read(buffer);
        }
    },
    COMMAND() {

        @Override
        public Reward fromJson(JsonObject jsonObject) {
            return RewardCommand.fromJson(jsonObject);
        }

        @Override
        public Reward read(ByteBuf buffer) {
            return RewardCommand.read(buffer);
        }
    };

    public abstract Reward fromJson(JsonObject jsonObject);

    public abstract Reward read(ByteBuf buffer);
}
