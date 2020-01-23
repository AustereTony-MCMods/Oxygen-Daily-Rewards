package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.inventory.InventoryHelper;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class RewardItem implements Reward {

    private String description;

    private int day;

    private long amount;

    private boolean special;

    private ItemStackWrapper stackWrapper;

    @Override
    public EnumReward getType() {
        return EnumReward.ITEM;
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
        jsonObject.add("itemstack", this.stackWrapper.toJson());
        return jsonObject;
    }

    public static Reward fromJson(JsonObject jsonObject) {
        RewardItem reward = new RewardItem();
        reward.day = jsonObject.get("day").getAsInt();
        reward.description = jsonObject.get("description").getAsString();
        reward.amount = jsonObject.get("amount").getAsLong();
        reward.special = jsonObject.get("special").getAsBoolean();
        reward.stackWrapper = ItemStackWrapper.fromJson(jsonObject.get("itemstack").getAsJsonObject());
        return reward;
    }

    @Override
    public void write(ByteBuf buffer) {  
        buffer.writeByte(this.day);
        ByteBufUtils.writeString(this.description, buffer);
        buffer.writeLong(this.amount);
        buffer.writeBoolean(this.special);
        this.stackWrapper.write(buffer);
    }

    public static Reward read(ByteBuf buffer) {
        RewardItem reward = new RewardItem();
        reward.day = buffer.readByte();
        reward.description = ByteBufUtils.readString(buffer);
        reward.amount = buffer.readLong();
        reward.special = buffer.readBoolean();
        reward.stackWrapper = ItemStackWrapper.read(buffer);
        return reward;
    }

    @Override
    public void rewardPlayer(EntityPlayerMP playerMP) { 
        CommonReference.delegateToServerThread(
                ()->InventoryHelper.addItemStack(playerMP, this.stackWrapper.getCachedItemStack(), (int) this.amount));
    }

    public ItemStackWrapper getStackWrapper() {
        return this.stackWrapper;
    }
}
