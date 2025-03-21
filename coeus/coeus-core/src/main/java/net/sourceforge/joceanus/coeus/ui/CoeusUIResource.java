/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.coeus.ui;

import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource IDs for Coeus UI.
 */
public enum CoeusUIResource
        implements OceanusBundleId {
    /**
     * Filter Selection Title.
     */
    FILTER_TITLE("filter.selection"),

    /**
     * TotalSet prompt.
     */
    TOTALS_PROMPT("totals.prompt"),

    /**
     * Report Selection Title.
     */
    REPORT_TITLE("report.selection"),

    /**
     * ReportType prompt.
     */
    REPORT_PROMPT("report.prompt"),

    /**
     * Market prompt.
     */
    MARKET_PROMPT("market.prompt"),

    /**
     * Type prompt.
     */
    TYPE_PROMPT("type.prompt"),

    /**
     * Month prompt.
     */
    MONTH_PROMPT("month.prompt"),

    /**
     * Loan prompt.
     */
    LOAN_PROMPT("loan.prompt"),

    /**
     * Menu All.
     */
    MENU_ALL("menu.all"),

    /**
     * Preference Base.
     */
    PREFERENCE_BASE("pref.base"),

    /**
     * Preference Calendar.
     */
    PREFERENCE_CALENDAR("pref.calendar"),

    /**
     * Menu Help.
     */
    MENU_HELP("menu.help"),

    /**
     * Menu DataViewer.
     */
    MENU_DATAVIEWER("menu.dataViewer"),

    /**
     * Menu About.
     */
    MENU_ABOUT("menu.about"),

    /**
     * Tab Reports.
     */
    TAB_REPORTS("tab.reports"),

    /**
     * Tab Statements.
     */
    TAB_STATEMENTS("tab.statements"),

    /**
     * Tab Preferences.
     */
    TAB_PREFERENCES("tab.preferences"),

    /**
     * Tab Log.
     */
    TAB_LOG("tab.log");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(CoeusResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The MenuItem Map.
     */
    private static final Map<CoeusMenuItem, OceanusBundleId> MENUITEM_MAP = buildMenuItemMap();

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
    CoeusUIResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "coeus.ui";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build menuItem map.
     * @return the map
     */
    private static Map<CoeusMenuItem, OceanusBundleId> buildMenuItemMap() {
        /* Create the map and return it */
        final Map<CoeusMenuItem, OceanusBundleId> myMap = new EnumMap<>(CoeusMenuItem.class);
        myMap.put(CoeusMenuItem.HELP, MENU_HELP);
        myMap.put(CoeusMenuItem.DATAVIEWER, MENU_DATAVIEWER);
        myMap.put(CoeusMenuItem.ABOUT, MENU_ABOUT);
        return myMap;
    }

    /**
     * Obtain key for menuItem.
     * @param pMenuItem the menuItem
     * @return the resource key
     */
    public static OceanusBundleId getKeyForMenuItem(final CoeusMenuItem pMenuItem) {
        return OceanusBundleLoader.getKeyForEnum(MENUITEM_MAP, pMenuItem);
    }
}
