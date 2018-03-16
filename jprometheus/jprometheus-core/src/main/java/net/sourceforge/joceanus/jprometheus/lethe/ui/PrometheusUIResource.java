/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.ui;

import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for jPrometheus UI Fields.
 */
public enum PrometheusUIResource implements TethysResourceId {
    /**
     * Menu Data.
     */
    MENU_DATA("Menu.Data"),

    /**
     * Menu Backup.
     */
    MENU_BACKUP("Menu.Backup"),

    /**
     * Menu Edit.
     */
    MENU_EDIT("Menu.Edit"),

    /**
     * Menu Security.
     */
    MENU_SECURITY("Menu.Security"),

    /**
     * Menu Help.
     */
    MENU_HELP("Menu.Help"),

    /**
     * MenuItem Undo.
     */
    MENUITEM_UNDO("MenuItem.UnDo"),

    /**
     * MenuItem Reset.
     */
    MENUITEM_RESET("MenuItem.Reset"),

    /**
     * MenuItem Help.
     */
    MENUITEM_HELP("MenuItem.Help"),

    /**
     * MenuItem DataViewer.
     */
    MENUITEM_DATAVIEWER("MenuItem.DataViewer"),

    /**
     * MenuItem About.
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
     * Icon Enable Tip.
     */
    ICON_TIP_ENABLE("Icon.Tip.Enable"),

    /**
     * Icon Disable Tip.
     */
    ICON_TIP_DISABLE("Icon.Tip.Disable"),

    /**
     * Icon GoTo Tip.
     */
    ICON_TIP_GOTO("Icon.Tip.GoTo"),

    /**
     * Table Row Column Title.
     */
    TABLE_TITLE_ROW("Table.Title.Row"),

    /**
     * Pane; Details Tab.
     */
    PANEL_TAB_DETAILS("Panel.Tab.Details"),

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
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(PrometheusUIResource.class.getCanonicalName());

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
    PrometheusUIResource(final String pKeyName) {
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
