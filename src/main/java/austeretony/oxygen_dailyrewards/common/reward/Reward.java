package austeretony.oxygen_dailyrewards.common.reward;

import com.google.gson.JsonElement;

import austeretony.alternateui.screen.core.GUISimpleElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface Reward {

    EnumReward getType();

    int getDay();

    String getDescription();

    long getAmount();

    boolean isSpecial();

    JsonElement toJson();

    void write(ByteBuf buffer);

    void rewardPlayer(EntityPlayerMP playerMP); 
}
