package austeretony.oxygen_dailyrewards.client.gui.settings;

import austeretony.alternateui.screen.framework.GUIElementsFramework;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenCheckBoxButton;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList.OxygenDropDownListWrapperEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.settings.ElementsContainer;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetColorCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetKeyCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetOffsetCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetScaleCallback;
import austeretony.oxygen_dailyrewards.client.settings.EnumDailyRewardsClientSettings;
import austeretony.oxygen_dailyrewards.client.settings.gui.EnumDailyRewardsGUISetting;

public class DailyRewardsSettingsContainer implements ElementsContainer {

    //common

    private OxygenCheckBoxButton addFriendsListButton;

    //interface

    private OxygenDropDownList alignmentFriendsList;


    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_dailyrewards.gui.settings.module.dailyrewards");
    }

    @Override
    public boolean hasCommonSettings() {
        return true;
    }

    @Override
    public boolean hasGUISettings() {
        return true;
    }

    @Override
    public void addCommon(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.oxygenMenu"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //add friends list to menu
        framework.addElement(new OxygenTextLabel(78, 34, ClientReference.localize("oxygen_dailyrewards.gui.settings.option.addDailyRewardsMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.addFriendsListButton = new OxygenCheckBoxButton(68, 29));
        this.addFriendsListButton.setToggled(EnumDailyRewardsClientSettings.ADD_DAILY_REWARDS_MENU.get().asBoolean());
        this.addFriendsListButton.setClickListener((mouseX, mouseY, mouseButton)->{
            EnumDailyRewardsClientSettings.ADD_DAILY_REWARDS_MENU.get().setValue(String.valueOf(this.addFriendsListButton.isToggled()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });
    }

    @Override
    public void addGUI(GUIElementsFramework framework) {
        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.alignment"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //friends list alignment

        String currAlignmentStr;
        switch (EnumDailyRewardsGUISetting.DAILY_REWARDS_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        case 0:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        case 1:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.right");
            break;    
        default:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        }
        framework.addElement(this.alignmentFriendsList = new OxygenDropDownList(68, 35, 55, currAlignmentStr));
        this.alignmentFriendsList.addElement(new OxygenDropDownListWrapperEntry<Integer>(- 1, ClientReference.localize("oxygen_core.alignment.left")));
        this.alignmentFriendsList.addElement(new OxygenDropDownListWrapperEntry<Integer>(0, ClientReference.localize("oxygen_core.alignment.center")));
        this.alignmentFriendsList.addElement(new OxygenDropDownListWrapperEntry<Integer>(1, ClientReference.localize("oxygen_core.alignment.right")));

        this.alignmentFriendsList.<OxygenDropDownListWrapperEntry<Integer>>setElementClickListener((element)->{
            EnumDailyRewardsGUISetting.DAILY_REWARDS_MENU_ALIGNMENT.get().setValue(String.valueOf(element.getWrapped()));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 33, ClientReference.localize("oxygen_dailyrewards.gui.settings.option.dailyRewardsMenuAlignment"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));
    }

    @Override
    public void resetCommon() {
        //add friends list to menu
        this.addFriendsListButton.setToggled(true);
        EnumDailyRewardsClientSettings.ADD_DAILY_REWARDS_MENU.get().reset();    

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void resetGUI() {
        //friends list alignment
        this.alignmentFriendsList.setDisplayText(ClientReference.localize("oxygen_core.alignment.center"));
        EnumDailyRewardsGUISetting.DAILY_REWARDS_MENU_ALIGNMENT.get().reset();

        OxygenManagerClient.instance().getClientSettingManager().changed();
    }


    @Override
    public void initSetColorCallback(SetColorCallback callback) {}

    @Override
    public void initSetScaleCallback(SetScaleCallback callback) {}

    @Override
    public void initSetOffsetCallback(SetOffsetCallback callback) {}

    @Override
    public void initSetKeyCallback(SetKeyCallback callback) {}
}
