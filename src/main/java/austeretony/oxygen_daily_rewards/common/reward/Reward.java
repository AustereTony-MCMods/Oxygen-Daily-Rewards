package austeretony.oxygen_daily_rewards.common.reward;

import austeretony.oxygen_core.client.gui.base.core.Widget;
import austeretony.oxygen_core.server.operation.Operation;
import io.netty.buffer.ByteBuf;

public interface Reward {

    void reward(Operation operation);

    void write(ByteBuf buffer);

    //client

    void drawBackground(Widget widget, int mouseX, int mouseY);

    void draw(Widget widget, int mouseX, int mouseY);

    void drawForeground(Widget widget, int mouseX, int mouseY);
}
