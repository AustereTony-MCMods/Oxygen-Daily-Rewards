package austeretony.oxygen_daily_rewards.common.reward;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.util.Locale;

public class DailyReward {

    private final RewardType type;
    private final int day;
    private final boolean isSpecial;
    @Nonnull
    private final Reward item;

    public DailyReward(RewardType type, int day, boolean isSpecial, @Nonnull Reward item) {
        this.type = type;
        this.day = day;
        this.isSpecial = isSpecial;
        this.item = item;
    }

    public RewardType getType() {
        return type;
    }

    public int getDay() {
        return day;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    @Nonnull
    public Reward getItem() {
        return item;
    }

    public static DailyReward fromJson(JsonObject jsonObject) {
        RewardType type = RewardType.valueOf(jsonObject.get("type").getAsString().toUpperCase(Locale.ROOT));
        Reward item = type.fromJson(jsonObject.get("reward"));
        return new DailyReward(type, jsonObject.get("day").getAsInt(), jsonObject.get("is_special").getAsBoolean(), item);
    }

    public void write(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        buffer.writeShort(day);
        buffer.writeBoolean(isSpecial);
        item.write(buffer);
    }

    public static DailyReward read(ByteBuf buffer) {
        RewardType type = RewardType.values()[buffer.readByte()];
        return new DailyReward(type, buffer.readShort(), buffer.readBoolean(), type.read(buffer));
    }
}
