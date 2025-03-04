/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.test.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;

/**
 * Helper functions for Scroll Examples.
 */
public class TethysTestHelper {
    /**
     * The max items.
     */
    private static final int MAX_ITEMS = 4;

    /**
     * The default short value.
     */
    public static final short SHORT_DEF = 3;

    /**
     * The default integer value.
     */
    public static final int INT_DEF = 25;

    /**
     * The default long value.
     */
    public static final long LONG_DEF = 300;

    /**
     * The default money value.
     */
    public static final OceanusMoney MONEY_DEF = new OceanusMoney("12.45");

    /**
     * The default price value.
     */
    public static final OceanusPrice PRICE_DEF = new OceanusPrice("2.2");

    /**
     * The default units value.
     */
    public static final OceanusUnits UNITS_DEF = new OceanusUnits("1");

    /**
     * The default rate value.
     */
    public static final OceanusRate RATE_DEF = new OceanusRate(".10");

    /**
     * The default ratio value.
     */
    public static final OceanusRatio RATIO_DEF = new OceanusRatio("1.6");

    /**
     * The factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factpry
     */
    TethysTestHelper(final TethysUIFactory<?> pFactory) {
        theFactory = pFactory;
    }

    /**
     * Build the context menu.
     * @param pMenu the menu to build
     */
    public void buildContextMenu(final TethysUIScrollMenu<String> pMenu) {
        /* Set the display count */
        pMenu.setMaxDisplayItems(MAX_ITEMS);
        pMenu.removeAllItems();

        /* Create the menu item */
        addMenuItem(pMenu, "First");
        addMenuItem(pMenu, "Second");
        addMenuItem(pMenu, "Third");
        addMenuItem(pMenu, "Fourth");
        final TethysUIScrollSubMenu<String> myMenu = addSubMenu(pMenu, "SubMenu");
        addSubMenuItem(myMenu, "AAAAA");
        addSubMenuItem(myMenu, "BBBBB");
        addMenuItem(pMenu, "Fifth");
        addMenuItem(pMenu, "Sixth");
        addMenuItem(pMenu, "Seventh");
        addMenuItem(pMenu, "Eighth");
    }

    /**
     * Add Menu Item for string.
     * @return the char Array password
     */
    public static char[] getPassword() {
        return "Password".toCharArray();
    }

    /**
     * Add Menu Item for string.
     * @param pMenu the menu to add to
     * @param pValue the value to add
     */
    private static void addMenuItem(final TethysUIScrollMenu<String> pMenu,
                                    final String pValue) {
        /* Add to context menu */
        pMenu.addItem(pValue);
    }

    /**
     * Add SubMenu for string.
     * @param pMenu the menu to add to
     * @param pValue the name to add
     * @return the subMenu
     */
    private static TethysUIScrollSubMenu<String> addSubMenu(final TethysUIScrollMenu<String> pMenu,
                                                            final String pValue) {
        /* Add to context menu */
        return pMenu.addSubMenu(pValue);
    }

    /**
     * Add SubMenu Item for string.
     * @param pMenu the subMenu
     * @param pValue the value to add
     */
    private static void addSubMenuItem(final TethysUIScrollSubMenu<String> pMenu,
                                       final String pValue) {
        /* Add to sub menu */
        pMenu.getSubMenu().addItem(pValue);
    }

    /**
     * Create selected list.
     * @return the list
     */
    public List<TethysTestListId> buildSelectedList() {
        /* Create the list */
        final List<TethysTestListId> myValues = new ArrayList<>();
        myValues.add(TethysTestListId.IMPORTANT);
        myValues.add(TethysTestListId.WORK);

        /* Set the value */
        return myValues;
    }

    /**
     * Create selectable list.
     * @return the list
     */
    public Iterator<TethysTestListId> buildSelectableList() {
        /* Create the list */
        final List<TethysTestListId> myValues = new ArrayList<>(Arrays.asList(TethysTestListId.values()));

        /* Set the value */
        return myValues.iterator();
    }

    /**
     * Build the simple IconMapSet.
     * @param <K> the keyId type
     * @param pFalseIcon the icon for the false state
     * @param pTrueIcon the icon for the true state
     * @return the new mapSet
     */
    public <K extends TethysUIIconId> TethysUIIconMapSet<Boolean> buildSimpleIconState(final K pFalseIcon,
                                                                                       final K pTrueIcon) {
        /* Create the state */
        final TethysUIButtonFactory<?> myButtons = theFactory.buttonFactory();
        final TethysUIIconMapSet<Boolean> myMapSet = myButtons.newIconMapSet();
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, pFalseIcon, "False");
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, pTrueIcon, "True");
        return myMapSet;
    }

    /**
     * Build the state IconState.
     * @param <K> the keyId type
     * @param pFalseIcon the icon for the false state
     * @param pTrueIcon the icon for the true state
     * @param pAltTrueIcon the icon for the true closed state
     * @return the map
     */
    public <K extends TethysUIIconId> Map<TethysIconState, TethysUIIconMapSet<Boolean>> buildStateIconState(final K pFalseIcon,
                                                                                                            final K pTrueIcon,
                                                                                                            final K pAltTrueIcon) {
        /* Create the map */
        final Map<TethysIconState, TethysUIIconMapSet<Boolean>> myMap = new EnumMap<>(TethysIconState.class);
        final TethysUIButtonFactory<?> myButtons = theFactory.buttonFactory();

        /* Create the CLOSED state */
        TethysUIIconMapSet<Boolean> myMapSet = myButtons.newIconMapSet();
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.FALSE, "False");
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.TRUE, pAltTrueIcon, "True");
        myMap.put(TethysIconState.CLOSED, myMapSet);

        /* Create the OPEN state */
        myMapSet = myButtons.newIconMapSet();
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, pFalseIcon, "False");
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, pTrueIcon, "True");
        myMap.put(TethysIconState.OPEN, myMapSet);

        /* Return the map */
        return myMap;
    }

    /**
     * build State button.
     * @param pManager the button manager
     */
    public void buildStateButton(final TethysUIScrollButtonManager<TethysIconState> pManager) {
        final TethysUIScrollMenu<TethysIconState> myMenu = pManager.getMenu();
        myMenu.addItem(TethysIconState.OPEN);
        myMenu.addItem(TethysIconState.CLOSED);
        pManager.setValue(TethysIconState.CLOSED);
    }

    /**
     * ENum for state.
     */
    public enum TethysIconState {
        /**
         * Open.
         */
        OPEN("Open"),

        /**
         * Closed.
         */
        CLOSED("Closed");

        /**
         * The display string.
         */
        private final String theDisplay;

        /**
         * Constructor.
         * @param pDisplay the display string
         */
        TethysIconState(final String pDisplay) {
            theDisplay = pDisplay;
        }

        @Override
        public String toString() {
            return theDisplay;
        }
    }
}
