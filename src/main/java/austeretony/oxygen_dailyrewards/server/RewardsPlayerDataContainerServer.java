package austeretony.oxygen_dailyrewards.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import austeretony.oxygen_core.server.api.OxygenHelperServer;

public class RewardsPlayerDataContainerServer {

    private final DailyRewardsManagerServer manager;

    private final Map<UUID, RewardsPlayerDataServer> players = new ConcurrentHashMap<>();

    public RewardsPlayerDataContainerServer(DailyRewardsManagerServer manager) {
        this.manager = manager;
    }

    public RewardsPlayerDataServer createPlayerData(UUID playerUUID) {
        RewardsPlayerDataServer playerData = new RewardsPlayerDataServer(OxygenHelperServer.getDataFolder() + "/server/players/" + playerUUID + "/daily rewards/player_data.dat");
        OxygenHelperServer.loadPersistentData(playerData);
        this.players.put(playerUUID, playerData);
        return playerData;
    }

    public RewardsPlayerDataServer getPlayerData(UUID playerUUID) {
        return this.players.get(playerUUID);
    }

    public void save() {
        OxygenHelperServer.addRoutineTask(()->{
            for (RewardsPlayerDataServer data : this.players.values()) {
                if (data.isChanged()) {
                    data.setChanged(false);
                    OxygenHelperServer.savePersistentDataAsync(data);
                }
            }   
        });
    }
}
