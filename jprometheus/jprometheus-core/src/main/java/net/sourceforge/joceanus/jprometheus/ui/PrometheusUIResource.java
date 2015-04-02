/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.ui;

import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jPrometheus UI Fields.
 */
public enum PrometheusUIResource implements ResourceId {
    /**
     * Data Menu.
     */
    MENU_DATA("Menu.Data"),

    /**
     * Edit Menu.
     */
    MENU_EDIT("Menu.Edit"),

    /**
     * Backup Menu.
     */
    MENU_BACKUP("Menu.Backup"),

    /**
     * Security Menu.
     */
    MENU_SECURITY("Menu.Security"),

    /**
     * Help Menu.
     */
    MENU_HELP("Menu.Help"),

    /**
     * LoadDB MenuItem.
     */
    MENUITEM_LOADDB("MenuItem.LoadDB"),

    /**
     * StoreDB MenuItem.
     */
    MENUITEM_STOREDB("MenuItem.StoreDB"),

    /**
     * CreateDB MenuItem.
     */
    MENUITEM_CREATEDB("MenuItem.CreateDB"),

    /**
     * PurgeDB MenuItem.
     */
    MENUITEM_PURGEDB("MenuItem.PurgeDB"),

    /**
     * UnDo MenuItem.
     */
    MENUITEM_UNDO("MenuItem.UnDo"),

    /**
     * Reset MenuItem.
     */
    MENUITEM_RESET("MenuItem.Reset"),

    /**
     * Create Backup MenuItem.
     */
    MENUITEM_BACKUPCREATE("MenuItem.CreateBackup"),

    /**
     * Restore Backup MenuItem.
     */
    MENUITEM_BACKUPRESTORE("MenuItem.RestoreBackup"),

    /**
     * Restore Backup MenuItem.
     */
    MENUITEM_XMLCREATE("MenuItem.CreateXml"),

    /**
     * Restore Backup MenuItem.
     */
    MENUITEM_XMLLOAD("MenuItem.RestoreXml"),

    /**
     * Restore Backup MenuItem.
     */
    MENUITEM_XTRACTCREATE("MenuItem.CreateXtract"),

    /**
     * RenewSecurity MenuItem.
     */
    MENUITEM_SECURERENEW("MenuItem.RenewSecurity"),

    /**
     * Change Password MenuItem.
     */
    MENUITEM_CHANGEPASS("MenuItem.ChangePass"),

    /**
     * Help MenuItem.
     */
    MENUITEM_HELP("MenuItem.Help"),

    /**
     * DataMgr MenuItem.
     */
    MENUITEM_DATAMGR("MenuItem.DataMgr"),

    /**
     * About MenuItem.
     */
    MENUITEM_ABOUT("MenuItem.About"),

    /**
     * Discard Changes Prompt.
     */
    PROMPT_DISCARD("Prompt.Discard"),

    /**
     * Close Title.
     */
    TITLE_CLOSE("Title.Close"),

    /**
     * ActionButtons Title.
     */
    ACTION_TITLE_SAVE("Action.Title.Save"),

    /**
     * ErrorBox Clear Button.
     */
    ERROR_BUTTON_CLEAR("Error.Button.Clear"),

    /**
     * ErrorBox Title.
     */
    ERROR_TITLE("Error.Title"),

    /**
     * StatusBar Cancel Button.
     */
    STATUSBAR_BUTTON_CANCEL("StatusBar.Button.Cancel"),

    /**
     * StatusBar Clear Button.
     */
    STATUSBAR_BUTTON_CLEAR("StatusBar.Button.Clear"),

    /**
     * StatusBar Progress Title.
     */
    STATUSBAR_TITLE_PROGRESS("StatusBar.Title.Progress"),

    /**
     * StatusBar Status Title.
     */
    STATUSBAR_TITLE_STATUS("StatusBar.Title.Status"),

    /**
     * StatusBar Success Status.
     */
    STATUSBAR_STATUS_SUCCESS("StatusBar.Status.Succeeded"),

    /**
     * StatusBar Success Status.
     */
    STATUSBAR_STATUS_FAIL("StatusBar.Status.Failed"),

    /**
     * StatusBar Cancel Status.
     */
    STATUSBAR_STATUS_CANCEL("StatusBar.Status.Cancelled"),

    /**
     * Icon Active Tip.
     */
    ICON_TIP_ACTIVE("Icon.Tip.Active"),

    /**
     * Icon Enable Tip.
     */
    ICON_TIP_ENABLE("Icon.Tip.Enable"),

    /**
     * Icon Disable Tip.
     */
    ICON_TIP_DISABLE("Icon.Tip.Disable"),

    /**
     * Icon Commit Tip.
     */
    ICON_TIP_COMMIT("Icon.Tip.Commit"),

    /**
     * Icon UnDo Tip.
     */
    ICON_TIP_UNDO("Icon.Tip.UnDo"),

    /**
     * Icon Reset Tip.
     */
    ICON_TIP_RESET("Icon.Tip.Reset"),

    /**
     * Icon Cancel Tip.
     */
    ICON_TIP_CANCEL("Icon.Tip.Cancel"),

    /**
     * Icon GoTo Tip.
     */
    ICON_TIP_GOTO("Icon.Tip.GoTo"),

    /**
     * Icon Edit Tip.
     */
    ICON_TIP_EDIT("Icon.Tip.Edit"),

    /**
     * Icon New Tip.
     */
    ICON_TIP_NEW("Icon.Tip.New"),

    /**
     * Icon Delete Tip.
     */
    ICON_TIP_DELETE("Icon.Tip.Delete"),

    /**
     * Table Row Column Title.
     */
    TABLE_TITLE_ROW("Table.Title.Row"),

    /**
     * Pane; Details Tab.
     */
    PANEL_TAB_DETAILS("Panel.Tab.Details"),

    /**
     * Static DataEntry.
     */
    STATIC_DATAENTRY("Static.DataEntry"),

    /**
     * Static Select Title.
     */
    STATIC_TITLE_SELECT("Static.Title.Select"),

    /**
     * Static Data Prompt.
     */
    STATIC_PROMPT_DATA("Static.Prompt.Data"),

    /**
     * Static Disabled Prompt.
     */
    STATIC_PROMPT_DISABLED("Static.Prompt.Disabled"),

    /**
     * Static Active Column Title.
     */
    STATIC_TITLE_ACTIVE("Static.Title.Active");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(PrometheusUIResource.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private PrometheusUIResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jPrometheus.ui";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
