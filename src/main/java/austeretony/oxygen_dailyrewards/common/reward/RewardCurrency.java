package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonObject;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.sound.OxygenSoundEffects;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
import austeretony.oxygen_core.server.api.SoundEventHelperServer;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class RewardCurrency implements Reward {

    private String description;

    private int day, currencyIndex;

    private long amount;

    private boolean special;

    @Override
    public EnumReward getType() {
        return EnumReward.CURRENCY;
    }

    @Override
    public int getDay() {
        return this.day;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public boolean isSpecial() {
        return this.special;
    }

    public static Reward fromJson(JsonObject jsonObject) {
        RewardCurrency reward = new RewardCurrency();
        reward.day = jsonObject.get("day").getAsInt();
        reward.description = jsonObject.get("description").getAsString();
        reward.amount = jsonObject.get("amount").getAsLong();
        reward.special = jsonObject.get("special").getAsBoolean();
        reward.currencyIndex = jsonObject.get("currency_index").getAsInt();
        return reward;
    }

    @Override
    public void write(ByteBuf buffer) {  
        buffer.writeByte(this.day);
        ByteBufUtils.writeString(this.description, buffer);
        buffer.writeLong(this.amount);
        buffer.writeBoolean(this.special);
        buffer.writeByte(this.currencyIndex);
    }

    public static Reward read(ByteBuf buffer) {
        RewardCurrency reward = new RewardCurrency();
        reward.day = buffer.readByte();
        reward.description = ByteBufUtils.readString(buffer);
        reward.amount = buffer.readLong();
        reward.special = buffer.readBoolean();
        reward.currencyIndex = buffer.readByte();
        return reward;
    }

    @Override
    public void rewardPlayer(EntityPlayerMP playerMP) {
        CurrencyHelperServer.addCurrency(CommonReference.getPersistentUUID(playerMP), this.amount, this.currencyIndex);
        SoundEventHelperServer.playSoundClient(playerMP, OxygenSoundEffects.RINGING_COINS.getId());

        if (DailyRewardsConfig.ADVANCED_LOGGING.asBoolean())
            OxygenMain.LOGGER.info("[Daily Rewards] <{}/{}> [2]: player rewarded with CURRENCY - index <{}>, amount {}.", 
                    CommonReference.getName(playerMP), 
                    CommonReference.getPersistentUUID(playerMP),
                    this.currencyIndex,
                    this.amount);
    }

    public int getCurrencyIndex() {
        return this.currencyIndex;
    }
}
