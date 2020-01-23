package austeretony.oxygen_dailyrewards.client.command;

import java.util.concurrent.TimeUnit;

import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.common.command.ArgumentExecutor;
import austeretony.oxygen_dailyrewards.client.DailyRewardsManagerClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class DailyRewardsArgumentClient implements ArgumentExecutor {

    @Override
    public String getName() {
        return "dailyrewards";
    }

    @Override
    public void process(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1)
            OxygenHelperClient.scheduleTask(()->this.openDailyRewardsMenu(), 100L, TimeUnit.MILLISECONDS);
        /*else if (args[1].equals("-test")) {
            ZonedDateTime 
            currentTime = TimeHelperClient.getZonedDateTime(),
            testTime = ZonedDateTime.of(2019, 1, 20, 23, 59, 59, 0, TimeHelperClient.getZoneId());

            Period period = Period.between(testTime.toLocalDate(), currentTime.toLocalDate());

            boolean rewardAvailable = (Period.between(testTime.toLocalDate(), currentTime.toLocalDate()).getDays() >= 1)
            && currentTime.getHour() >= DailyRewardsConfig.REWARD_TIME_OFFSET_HOURS.asInt();

            DailyRewardsMain.LOGGER.info(" ///--- Period in days: {}", period.getDays());
            DailyRewardsMain.LOGGER.info(" ///--- Reward available: {}", rewardAvailable);
        }*/
    }

    private void openDailyRewardsMenu() {
        ClientReference.delegateToClientThread(()->DailyRewardsManagerClient.instance().getMenuManager().openDailyRewardsMenu());
    }
}
