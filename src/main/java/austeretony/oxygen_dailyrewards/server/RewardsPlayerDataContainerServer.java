package austeretony.oxygen_dailyrewards.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import austeretony.oxygen_core.server.api.OxygenHelperServer;

public class RewardsPlayerDataContainerServer {

    private final DailyRewardsManagerServer manager;

    private final Map<UUID, RewardsPlayerDataServer> players = new ConcurrentHashMap<>();

    protected RewardsPlayerDataContainerServer(DailyRewardsManagerServer manager) {
        this.manager = manager;
    }

    public RewardsPlayerDataServer createPlayerData(UUID playerUUID) {
        RewardsPlayerDataServer playerData = new RewardsPlayerDataServer(OxygenHelperServer.getDataFolder() + "/server/players/" + playerUUID + "/daily rewards/player_data.dat");        
        OxygenHelperServer.loadPersistentData(playerData);
        this.players.put(playerUUID, playerData);
        return playerData;
    }

    @Nullable
    public RewardsPlayerDataServer getPlayerData(UUID playerUUID) {
        return this.players.get(playerUUID);
    }

    void save() {
        for (RewardsPlayerDataServer playerData : this.players.values()) {
            if (playerData.isChanged()) {
                playerData.setChanged(false);
                OxygenHelperServer.savePersistentDataAsync(playerData);
            }
        }   
    }
}
