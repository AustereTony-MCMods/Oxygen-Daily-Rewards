package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import austeretony.oxygen_core.server.api.CurrencyHelperServer;
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

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("day", new JsonPrimitive(this.day));
        jsonObject.add("description", new JsonPrimitive(this.description));
        jsonObject.add("amount", new JsonPrimitive(this.amount));
        jsonObject.add("special", new JsonPrimitive(this.special));
        jsonObject.add("currency_index", new JsonPrimitive(this.currencyIndex));
        return jsonObject;
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
    }

    public int getCurrencyIndex() {
        return this.currencyIndex;
    }
}
