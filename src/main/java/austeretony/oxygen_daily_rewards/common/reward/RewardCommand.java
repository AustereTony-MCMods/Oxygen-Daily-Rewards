package austeretony.oxygen_daily_rewards.common.reward;

import austeretony.oxygen_core.client.gui.base.GUIUtils;
import austeretony.oxygen_core.client.gui.base.core.Widget;
import austeretony.oxygen_core.client.settings.CoreSettings;
import austeretony.oxygen_core.client.util.ClientUtils;
import austeretony.oxygen_core.common.api.OxygenCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.*;
import austeretony.oxygen_core.server.operation.Operation;
import austeretony.oxygen_daily_rewards.client.gui.daily_rewards.DailyRewardsScreen;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class RewardCommand implements Reward {

    private final List<String> commandsList, tooltipLines;
    //server
    private final byte[] iconRaw;

    //client
    private ResourceLocation iconTexture;

    private RewardCommand(List<String> commandsList, byte[] iconRaw, List<String> tooltipLines) {
        this.commandsList = commandsList;
        this.tooltipLines = tooltipLines;
        this.iconRaw = iconRaw;
    }

    @Override
    public void reward(Operation operation) {
        final Function<EntityPlayerMP, Boolean> task = player -> {
            for (String command : commandsList) {
                String commandOut = CommonUtils.processCommandSelectors(player, command);
                if (MinecraftCommon.executeCommandConsole(commandOut) == 0) {
                    OxygenMain.logInfo(2, "[Daily Rewards] Failed to execute command '{}' for {}/{}",
                            commandOut, MinecraftCommon.getEntityName(player), MinecraftCommon.getEntityUUID(player));
                }
            }
            return true; // ignoring failed command execution...
        };
        operation.withGenericAction(CallingThread.MINECRAFT, null, task, Strings.join(commandsList, ", "));
    }

    public static RewardCommand fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonArray commandsArray = jsonObject.get("commands").getAsJsonArray();
        List<String> commandsList = new ArrayList<>(1);
        for (JsonElement arrayElement : commandsArray) {
            commandsList.add(arrayElement.getAsString());
        }

        String iconName = jsonObject.get("icon_name").getAsString();
        String iconPath = OxygenCommon.getConfigFolder() + "/data/server/daily_rewards/icons/" + iconName;

        JsonArray tooltipArray = jsonObject.get("tooltip_lines").getAsJsonArray();
        List<String> tooltipLines = new ArrayList<>(1);
        for (JsonElement arrayElement : tooltipArray) {
            tooltipLines.add(arrayElement.getAsString());
        }

        return new RewardCommand(commandsList, FileUtils.loadImageBytes(iconPath), tooltipLines);
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(iconRaw.length);
        buffer.writeBytes(iconRaw);

        buffer.writeByte(tooltipLines.size());
        for (String line : tooltipLines) {
            ByteBufUtils.writeString(line, buffer);
        }
    }

    public static RewardCommand read(ByteBuf buffer) {
        byte[] iconRaw = new byte[buffer.readInt()];
        buffer.readBytes(iconRaw);

        int amount = buffer.readByte();
        List<String> tooltipList = new ArrayList<>(1);
        for (int i = 0; i < amount; i++) {
            tooltipList.add(ByteBufUtils.readString(buffer));
        }

        return new RewardCommand(Collections.emptyList(), iconRaw, tooltipList);
    }

    @Override
    public void drawBackground(Widget widget, int mouseX, int mouseY) {
        int iconSize = DailyRewardsScreen.REWARD_WIDGET_SIZE;

        GUIUtils.colorDef();
        GUIUtils.drawTexturedRect(0, 0, iconSize, iconSize, getIconTexture(), 0, 0, iconSize, iconSize);
    }

    @Override
    public void draw(Widget widget, int mouseX, int mouseY) {}

    @Override
    public void drawForeground(Widget widget, int mouseX, int mouseY) {
        if (mouseX >= widget.getX() + 10 && mouseY >= widget.getY() + 10 && mouseX < widget.getX() + widget.getWidth() - 10
                && mouseY < widget.getY() + widget.getHeight() - 10) {
            int offset = widget.getWidth() - 2;
            int x = widget.getX() + offset;
            int y = widget.getY() + (int) ((widget.getHeight() - tooltipLines.size() * Widget.TOOLTIP_HEIGHT) / 2F);

            float width = 0;
            for (String line : tooltipLines) {
                float lineWidth = GUIUtils.getTextWidth(line, CoreSettings.SCALE_TEXT_TOOLTIP.asFloat()) + 6F;
                if (lineWidth > width) {
                    width = lineWidth;
                }
            }
            int startX = widget.getScreenX() + width + offset > widget.getScreen().width ? (int) (x - width - offset) : x;

            Widget.drawToolTip(startX, y, tooltipLines);
        }
    }

    @Nonnull
    public ResourceLocation getIconTexture() {
        if (iconTexture == null) {
            iconTexture = ClientUtils.getTextureLocationFromBytes(iconRaw);
        }
        return iconTexture;
    }

    @Override
    public String toString() {
        return "RewardItemCommand[" +
                "commandsList= " + commandsList +
                "]";
    }
}
