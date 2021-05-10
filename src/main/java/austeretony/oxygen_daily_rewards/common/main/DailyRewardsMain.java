package austeretony.oxygen_daily_rewards.common.main;

import austeretony.oxygen_core.client.api.OxygenClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.menu.OxygenMenuHelper;
import austeretony.oxygen_core.common.api.OxygenCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.common.util.MinecraftCommon;
import austeretony.oxygen_core.server.api.OxygenServer;
import austeretony.oxygen_core.server.command.CommandOxygenOperator;
import austeretony.oxygen_core.server.command.CommandOxygenServer;
import austeretony.oxygen_daily_rewards.client.command.DailyRewardsArgumentClient;
import austeretony.oxygen_daily_rewards.client.gui.daily_rewards.DailyRewardsScreen;
import austeretony.oxygen_daily_rewards.client.settings.DailyRewardsSettings;
import austeretony.oxygen_daily_rewards.common.config.DailyRewardsConfig;
import austeretony.oxygen_daily_rewards.common.network.client.CPRewardClaimed;
import austeretony.oxygen_daily_rewards.common.network.client.CPSyncPlayerData;
import austeretony.oxygen_daily_rewards.common.network.client.CPSyncRewards;
import austeretony.oxygen_daily_rewards.common.network.server.SPClaimReward;
import austeretony.oxygen_daily_rewards.server.command.DailyRewardArgumentOperator;
import austeretony.oxygen_daily_rewards.server.command.DailyRewardsArgumentServer;
import austeretony.oxygen_daily_rewards.server.event.DailyRewardsEventsServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = DailyRewardsMain.MOD_ID,
        name = DailyRewardsMain.NAME,
        version = DailyRewardsMain.VERSION,
        dependencies = "required-after:oxygen_core@[0.12.0,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = DailyRewardsMain.VERSIONS_FORGE_URL)
public class DailyRewardsMain {

    public static final String
            MOD_ID = "oxygen_daily_rewards",
            NAME = "Oxygen: Daily Rewards",
            VERSION = "0.12.0",
            VERSION_CUSTOM = VERSION + ":beta:0",
            VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Daily-Rewards/info/versions.json";

    //oxygen module index
    public static final int MODULE_INDEX = 7;

    //screen id
    public static final int SCREEN_ID_DAILY_REWARDS = 70;

    //network requests ids
    public static final int NET_REQUEST_CLAIM_REWARD = 70;

    //key binding id
    public static final int KEYBINDING_ID_OPEN_DAILY_REWARDS_SCREEN = 70;

    //operations
    public static String OPERATION_REWARD_GAIN = "daily_rewards:reward_gain";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenCommon.registerConfig(new DailyRewardsConfig());
        if (event.getSide() == Side.CLIENT) {
            CommandOxygenClient.registerArgument(new DailyRewardsArgumentClient());
            OxygenClient.registerKeyBind(
                    KEYBINDING_ID_OPEN_DAILY_REWARDS_SCREEN,
                    "key.oxygen_daily_rewards.open_daily_rewards_screen",
                    OxygenMain.KEY_BINDINGS_CATEGORY,
                    DailyRewardsConfig.DAILY_REWARDS_SCREEN_KEY::asInt,
                    DailyRewardsConfig.ENABLE_DAILY_REWARDS_SCREEN_KEY::asBoolean,
                    true,
                    () -> OxygenClient.openScreen(SCREEN_ID_DAILY_REWARDS));
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        initNetwork();
        MinecraftCommon.registerEventHandler(new DailyRewardsEventsServer());
        OxygenServer.registerNetworkRequest(NET_REQUEST_CLAIM_REWARD, 2_000);
        DailyRewardsPrivileges.register();
        CommandOxygenServer.registerArgument(new DailyRewardsArgumentServer());
        CommandOxygenOperator.registerArgument(new DailyRewardArgumentOperator());
        if (event.getSide() == Side.CLIENT) {
            DailyRewardsSettings.register();
            OxygenMenuHelper.addMenuEntry(DailyRewardsScreen.DAILY_REWARDS_MENU_ENTRY);
            OxygenClient.registerScreen(SCREEN_ID_DAILY_REWARDS, DailyRewardsScreen::open);
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPSyncPlayerData.class);
        OxygenMain.network().registerPacket(CPSyncRewards.class);
        OxygenMain.network().registerPacket(CPRewardClaimed.class);

        OxygenMain.network().registerPacket(SPClaimReward.class);
    }
}
