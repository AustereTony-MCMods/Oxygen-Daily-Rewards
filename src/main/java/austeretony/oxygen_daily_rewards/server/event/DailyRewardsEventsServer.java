package austeretony.oxygen_daily_rewards.server.event;

import austeretony.oxygen_core.server.event.OxygenPlayerEvent;
import austeretony.oxygen_core.server.event.OxygenServerEvent;
import austeretony.oxygen_daily_rewards.server.DailyRewardsManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DailyRewardsEventsServer {

    @SubscribeEvent
    public void onServerStarting(OxygenServerEvent.Starting event) {
        DailyRewardsManagerServer.instance().serverStarting();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(OxygenPlayerEvent.LoggedIn event) {
        DailyRewardsManagerServer.instance().playerLoggedIn(event.getPlayer());
    }
}
