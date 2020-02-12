package austeretony.oxygen_dailyrewards.common.main;

import java.util.zip.Deflater;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.gui.settings.SettingsScreen;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.command.CommandOxygenOperator;
import austeretony.oxygen_core.server.command.CommandOxygenServer;
import austeretony.oxygen_core.server.network.NetworkRequestsRegistryServer;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import austeretony.oxygen_dailyrewards.client.DailyRewardsStatusMessagesHandler;
import austeretony.oxygen_dailyrewards.client.command.DailyRewardsArgumentClient;
import austeretony.oxygen_dailyrewards.client.gui.rewards.DailyRewardsMenuScreen;
import austeretony.oxygen_dailyrewards.client.gui.settings.DailyRewardsSettingsContainer;
import austeretony.oxygen_dailyrewards.client.settings.EnumDailyRewardsClientSettings;
import austeretony.oxygen_dailyrewards.client.settings.gui.EnumDailyRewardsGUISetting;
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
        dependencies = "required-after:oxygen_core@[0.10.2,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = DailyRewardsMain.VERSIONS_FORGE_URL)
public class DailyRewardsMain {

    public static final String 
    MODID = "oxygen_dailyrewards",
    NAME = "Oxygen: Daily Rewards",
    VERSION = "0.10.4",
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Daily-Rewards/info/mod_versions_forge.json";

    public static final int 
    DAILY_REWARDS_MOD_INDEX = 14,

    DAILY_REWARDS_MENU_SCREEN_ID = 140,

    CLAIM_REWARD_REQUEST_ID = 140;

    public static final Logger DAILY_REWARDS_LOGGER = getLogger("daily rewards", "rewards", "Oxygen/Daily Rewards");

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
            OxygenGUIHelper.registerOxygenMenuEntry(DailyRewardsMenuScreen.DAILY_REWARDS_MENU_ENTRY);
            EnumDailyRewardsClientSettings.register();
            EnumDailyRewardsGUISetting.register();
            SettingsScreen.registerSettingsContainer(new DailyRewardsSettingsContainer());
        }
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPSyncRewardsData.class);
        OxygenMain.network().registerPacket(CPSyncPlayerData.class);
    }

    //TODO 0.11 Move to Core into package "common.utils"
    public static final Logger getLogger(String cat, String file, String name){
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        final Configuration configuration = loggerContext.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder().withPattern("[%d{dd-MMM-yyyy HH:mm:ss}] [%t] [%c]: %m%n").withConfiguration(configuration).build();
        RollingFileAppender appender = RollingFileAppender.newBuilder()
                .setConfiguration(configuration)
                .withFileName("./logs/oxygen/" + cat + "/" + file + ".log")
                .withFilePattern("./logs/oxygen/" + cat + "/" + file + "-%d{yyyy-MM-dd}.%i.log.gz")
                .withName(name)
                .withAppend(true)
                .withImmediateFlush(true)
                .withBufferedIo(true)
                .withBufferSize(8192)
                .withCreateOnDemand(false)
                .withLocking(false)
                .withLayout(layout)
                .withPolicy(CompositeTriggeringPolicy.createPolicy(SizeBasedTriggeringPolicy.createPolicy("10 M"), TimeBasedTriggeringPolicy.createPolicy("1", null)))
                .withStrategy(DefaultRolloverStrategy.createStrategy(String.valueOf(Integer.MAX_VALUE), String.valueOf(1), "max", String.valueOf(Deflater.NO_COMPRESSION), null, true, configuration)).build();
        appender.start();
        configuration.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef(name, null, null);
        LoggerConfig loggerConfig = LoggerConfig.createLogger(true, Level.INFO, name, "true", new AppenderRef[] {ref}, null, configuration, null);
        configuration.addLogger(name, loggerConfig);
        loggerContext.getLogger(name).addAppender(appender);
        loggerContext.updateLoggers();

        return loggerContext.getLogger(name);
    }
}
