package austeretony.oxygen_dailyrewards.common.reward;

import javax.annotation.Nullable;

import austeretony.alternateui.screen.core.GUISimpleElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface Reward {

    EnumReward getType();

    int getDay();

    String getDescription();

    @Nullable
    String getTooltip();

    boolean isSpecial();

    void write(ByteBuf buffer);

    boolean rewardPlayer(EntityPlayerMP playerMP); 

    @SideOnly(Side.CLIENT)
    void draw(GUISimpleElement widget, int mouseX, int mouseY);

    @Nullable
    ItemStack getItemStack();
}
