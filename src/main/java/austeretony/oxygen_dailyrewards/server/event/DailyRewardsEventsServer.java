package austeretony.oxygen_dailyrewards.server.event;

import austeretony.oxygen_core.server.api.event.OxygenPlayerLoadedEvent;
import austeretony.oxygen_core.server.api.event.OxygenWorldLoadedEvent;
import austeretony.oxygen_dailyrewards.server.DailyRewardsManagerServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DailyRewardsEventsServer {

    @SubscribeEvent
    public void onWorldLoaded(OxygenWorldLoadedEvent event) {
        DailyRewardsManagerServer.instance().worldLoaded();
    }

    @SubscribeEvent
    public void onPlayerUnloaded(OxygenPlayerLoadedEvent event) {
        DailyRewardsManagerServer.instance().onPlayerLoaded(event.playerMP);
    }
}
