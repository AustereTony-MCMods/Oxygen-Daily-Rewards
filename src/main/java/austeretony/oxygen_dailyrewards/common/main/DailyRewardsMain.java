package austeretony.oxygen_dailyrewards.common.main;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.command.CommandOxygenOperator;
import austeretony.oxygen_core.server.command.CommandOxygenServer;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_dailyrewards.client.DailyRewardsStatusMessagesHandler;
import austeretony.oxygen_dailyrewards.client.command.DailyRewardsArgumentClient;
import austeretony.oxygen_dailyrewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_dailyrewards.common.network.client.CPSyncRewardsData;
import austeretony.oxygen_dailyrewards.server.DailyRewardsManagerServer;
import austeretony.oxygen_dailyrewards.server.command.DailyRewardsArgumentOperator;
import austeretony.oxygen_dailyrewards.server.command.DailyRewardsArgumentServer;
import austeretony.oxygen_dailyrewards.server.event.DailyRewardsEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = DailyRewardsMain.MODID, 
        name = DailyRewardsMain.NAME, 
        version = DailyRewardsMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.10.0,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = DailyRewardsMain.VERSIONS_FORGE_URL)
public class DailyRewardsMain {

    public static final String 
    MODID = "oxygen_dailyrewards",
    NAME = "Oxygen: Daily Rewards",
    VERSION = "0.10.1",
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Daily-Rewards/info/mod_versions_forge.json";

    public static final int 
    DAILY_REWARDS_MOD_INDEX = 14,

    DAILY_REWARDS_MENU_SCREEN_ID = 140,

    CLAIM_REWARD_REQUEST_ID = 145;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    //TODO Move to Core
    public static final DateTimeFormatter DEBUG_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd z", Locale.ENGLISH);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new DailyRewardsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgument(new DailyRewardsArgumentClient());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        DailyRewardsManagerServer.create();
        CommonReference.registerEvent(new DailyRewardsEventsServer());
        NetworkRequestsRegistryServer.registerRequest(CLAIM_REWARD_REQUEST_ID, 5000);
        CommandOxygenServer.registerArgument(new DailyRewardsArgumentServer());
        CommandOxygenOperator.registerArgument(new DailyRewardsArgumentOperator());
        EnumDailyRewardsPrivilege.register();
        if (event.getSide() == Side.CLIENT) {
            DailyRewardsManagerClient.create();
            OxygenHelperClient.registerStatusMessagesHandler(new DailyRewardsStatusMessagesHandler());
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPSyncRewardsData.class);
        OxygenMain.network().registerPacket(CPSyncPlayerData.class);
    }
}
