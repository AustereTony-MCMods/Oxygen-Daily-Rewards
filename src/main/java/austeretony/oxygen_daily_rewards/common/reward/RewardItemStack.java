package austeretony.oxygen_daily_rewards.common.reward;

import austeretony.oxygen_core.client.gui.base.GUIUtils;
import austeretony.oxygen_core.client.gui.base.core.Widget;
import austeretony.oxygen_core.client.settings.CoreSettings;
import austeretony.oxygen_core.common.item.ItemStackWrapper;
import austeretony.oxygen_core.server.operation.Operation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.util.List;

public class RewardItemStack implements Reward {

    private final ItemStackWrapper stackWrapper;
    private final int quantity;

    private RewardItemStack(ItemStackWrapper stackWrapper, int quantity) {
        this.stackWrapper = stackWrapper;
        this.quantity = quantity;
    }

    @Override
    public void reward(Operation operation) {
        operation.withItemAdd(stackWrapper, quantity);
    }

    public static RewardItemStack fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        ItemStackWrapper stackWrapper = ItemStackWrapper.fromJson(jsonObject.getAsJsonObject("item_stack"));
        return new RewardItemStack(stackWrapper, jsonObject.get("quantity").getAsInt());
    }

    @Override
    public void write(ByteBuf buffer) {
        stackWrapper.write(buffer);
        buffer.writeShort(quantity);
    }

    public static RewardItemStack read(ByteBuf buffer) {
        ItemStackWrapper stackWrapper = ItemStackWrapper.read(buffer);
        return new RewardItemStack(stackWrapper, buffer.readShort());
    }

    @Override
    public void drawBackground(Widget widget, int mouseX, int mouseY) {}

    @Override
    public void draw(Widget widget, int mouseX, int mouseY) {
        GUIUtils.renderItemStack(stackWrapper.getItemStackCached(), 12, 12,
                CoreSettings.ENABLE_DURABILITY_BARS_GUI_DISPLAY.asBoolean());

        float textScale = CoreSettings.SCALE_TEXT_ADDITIONAL.asFloat() - .05F;
        String valueStr = "x" + quantity;
        float valueStrWidth = GUIUtils.getTextWidth(valueStr, textScale);
        GUIUtils.drawString(valueStr, (widget.getWidth() - valueStrWidth) / 2,
                12 + 1 + 16, textScale, CoreSettings.COLOR_TEXT_BASE_ENABLED.asInt(), false);
    }

    @Override
    public void drawForeground(Widget widget, int mouseX, int mouseY) {
        if (mouseX >= widget.getX() + 12 && mouseY >= widget.getY() + 12 && mouseX < widget.getX() + widget.getWidth() - 12
                && mouseY < widget.getY() + widget.getHeight() - 12) {
            int offset = (int) ((widget.getWidth() - 16) / 2F + 16);
            int x = widget.getX() + offset;

            ItemStack itemStack = stackWrapper.getItemStackCached();
            List<String> tooltipLines = GUIUtils.getItemStackToolTip(itemStack);
            float width = 0;
            for (String line : tooltipLines) {
                float lineWidth = GUIUtils.getTextWidth(line, CoreSettings.SCALE_TEXT_TOOLTIP.asFloat()) + 6F;
                if (lineWidth > width) {
                    width = lineWidth;
                }
            }
            int startX = widget.getScreenX() + width + offset > widget.getScreen().width ? (int) (x - width - 16) : x;
            int y = widget.getY() + (int) ((widget.getHeight() - tooltipLines.size() * Widget.TOOLTIP_HEIGHT) / 2F);

            Widget.drawToolTip(startX, y, tooltipLines);
        }
    }

    @Override
    public String toString() {
        return "RewardItemStack[" +
                "stackWrapper= " + stackWrapper + ", " +
                "quantity= " + quantity +
                "]";
    }
}
