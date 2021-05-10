package austeretony.oxygen_daily_rewards.common.reward;

import austeretony.oxygen_core.client.api.OxygenClient;
import austeretony.oxygen_core.client.gui.base.GUIUtils;
import austeretony.oxygen_core.client.gui.base.core.Widget;
import austeretony.oxygen_core.client.preset.CurrencyProperties;
import austeretony.oxygen_core.client.settings.CoreSettings;
import austeretony.oxygen_core.common.util.CommonUtils;
import austeretony.oxygen_core.server.operation.Operation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;

public class RewardCurrency implements Reward {

    private final int currencyIndex;
    private final long value;

    //client
    private CurrencyProperties properties;

    private RewardCurrency(int currencyIndex, long value) {
        this.currencyIndex = currencyIndex;
        this.value = value;
    }

    @Override
    public void reward(Operation operation) {
        operation.withCurrencyGain(currencyIndex, value);
    }

    public static RewardCurrency fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new RewardCurrency(jsonObject.get("currency_index").getAsInt(), jsonObject.get("value").getAsLong());
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(currencyIndex);
        buffer.writeLong(value);
    }

    public static RewardCurrency read(ByteBuf buffer) {
        return new RewardCurrency(buffer.readByte(), buffer.readLong());
    }

    @Override
    public void drawBackground(Widget widget, int mouseX, int mouseY) {}

    @Override
    public void draw(Widget widget, int mouseX, int mouseY) {
        if (properties == null) {
            properties = OxygenClient.getCurrencyProperties(currencyIndex);
        }
        if (properties == null) return;

        GUIUtils.colorDef();
        GUIUtils.drawTexturedRect((widget.getWidth() - (properties.getIconWidth() + 2 * properties.getIconXOffset())) / 2F,
                16 + properties.getIconYOffset(), properties.getIconWidth(), properties.getIconHeight(),
                properties.getIconTexture(), 0, 0, properties.getIconWidth(), properties.getIconHeight());

        float textScale = CoreSettings.SCALE_TEXT_ADDITIONAL.asFloat() - .05F;
        String valueStr = "x" + CommonUtils.formatCurrencyValue(value);
        float valueStrWidth = GUIUtils.getTextWidth(valueStr, textScale);
        GUIUtils.drawString(valueStr, (widget.getWidth() - valueStrWidth) / 2,
                16 + 2 + (properties.getIconWidth() + 2 * properties.getIconXOffset()), textScale, CoreSettings.COLOR_TEXT_BASE_ENABLED.asInt(), false);
    }

    @Override
    public void drawForeground(Widget widget, int mouseX, int mouseY) {
        if (properties == null) {
            properties = OxygenClient.getCurrencyProperties(currencyIndex);
        }
        if (properties == null) return;

        if (mouseX >= widget.getX() + 14 && mouseY >= widget.getY() + 14 && mouseX < widget.getX() + widget.getWidth() - 14
                && mouseY < widget.getY() + widget.getHeight() - 14) {
            int offset = (int) ((widget.getWidth() - 16) / 2F + 16);
            int x = widget.getX() + offset;
            int y = widget.getY() + (int) ((widget.getHeight() - Widget.TOOLTIP_HEIGHT) / 2F);
            float width = GUIUtils.getTextWidth(properties.getLocalizedName(), CoreSettings.SCALE_TEXT_TOOLTIP.asFloat()) + 6F;
            int startX = widget.getScreenX() + width + offset > widget.getScreen().width ? (int) (x - width - 16) : x;

            Widget.drawToolTip(startX, y, properties.getLocalizedName());
        }
    }

    @Override
    public String toString() {
        return "RewardItemCurrency[" +
                "currencyIndex= " + currencyIndex + ", " +
                "value= " + value +
                "]";
    }
}
