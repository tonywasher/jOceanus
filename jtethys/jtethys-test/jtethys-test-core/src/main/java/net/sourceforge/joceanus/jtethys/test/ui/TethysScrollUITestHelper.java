/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;

/**
 * Helper functions for Scroll Examples.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class TethysScrollUITestHelper<N, I> {
    /**
     * The max items.
     */
    private static final int MAX_ITEMS = 4;

    /**
     * Default icon width.
     */
    private static final int DEFAULT_ICONWIDTH = 24;

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
    public static final TethysMoney MONEY_DEF = new TethysMoney("12.45");

    /**
     * The default price value.
     */
    public static final TethysPrice PRICE_DEF = new TethysPrice("2.2");

    /**
     * The default units value.
     */
    public static final TethysUnits UNITS_DEF = new TethysUnits("1");

    /**
     * The default rate value.
     */
    public static final TethysRate RATE_DEF = new TethysRate(".10");

    /**
     * The default ratio value.
     */
    public static final TethysRatio RATIO_DEF = new TethysRatio("1.6");

    /**
     * The default dilution value.
     */
    public static final TethysDilution DILUTION_DEF = new TethysDilution("0.5");

    /**
     * Build the context menu.
     * @param pMenu the menu to build
     */
    public void buildContextMenu(final TethysScrollMenu<String, ?> pMenu) {
        /* Set the display count */
        pMenu.setMaxDisplayItems(MAX_ITEMS);
        pMenu.removeAllItems();

        /* Create the menu item */
        addMenuItem(pMenu, "First");
        addMenuItem(pMenu, "Second");
        addMenuItem(pMenu, "Third");
        addMenuItem(pMenu, "Fourth");
        final TethysScrollSubMenu<String, ?> myMenu = addSubMenu(pMenu, "SubMenu");
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
    private static void addMenuItem(final TethysScrollMenu<String, ?> pMenu,
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
    private static TethysScrollSubMenu<String, ?> addSubMenu(final TethysScrollMenu<String, ?> pMenu,
                                                             final String pValue) {
        /* Add to context menu */
        return pMenu.addSubMenu(pValue);
    }

    /**
     * Add SubMenu Item for string.
     * @param pMenu the subMenu
     * @param pValue the value to add
     */
    private static void addSubMenuItem(final TethysScrollSubMenu<String, ?> pMenu,
                                       final String pValue) {
        /* Add to sub menu */
        pMenu.getSubMenu().addItem(pValue);
    }

    /**
     * Create selected list.
     * @return the list
     */
    public List<TethysListId> buildSelectedList() {
        /* Create the list */
        final List<TethysListId> myValues = new ArrayList<>();
        myValues.add(TethysListId.IMPORTANT);
        myValues.add(TethysListId.WORK);

        /* Set the value */
        return myValues;
    }

    /**
     * Create selectable list.
     * @return the list
     */
    public Iterator<TethysListId> buildSelectableList() {
        /* Create the list */
        final List<TethysListId> myValues = new ArrayList<>();

        /* Loop through the items */
        for (TethysListId myValue : TethysListId.values()) {
            myValues.add(myValue);
        }

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
    public <K extends Enum<K> & TethysIconId> TethysIconMapSet<Boolean> buildSimpleIconState(final K pFalseIcon,
                                                                                             final K pTrueIcon) {
        /* Create the state */
        final TethysIconMapSet<Boolean> myMapSet = new TethysIconMapSet<>(DEFAULT_ICONWIDTH);
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
    public <K extends Enum<K> & TethysIconId> Map<IconState, TethysIconMapSet<Boolean>> buildStateIconState(final K pFalseIcon,
                                                                                                            final K pTrueIcon,
                                                                                                            final K pAltTrueIcon) {
        /* Create the map */
        final Map<IconState, TethysIconMapSet<Boolean>> myMap = new EnumMap<>(IconState.class);

        /* Create the CLOSED state */
        TethysIconMapSet<Boolean> myMapSet = new TethysIconMapSet<>(DEFAULT_ICONWIDTH);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.FALSE, "False");
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.TRUE, pAltTrueIcon, "True");
        myMap.put(IconState.CLOSED, myMapSet);

        /* Create the OPEN state */
        myMapSet = new TethysIconMapSet<>(DEFAULT_ICONWIDTH);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, pFalseIcon, "False");
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, pTrueIcon, "True");
        myMap.put(IconState.OPEN, myMapSet);

        /* Return the map */
        return myMap;
    }

    /**
     * build State button.
     * @param pManager the button manager
     */
    public void buildStateButton(final TethysScrollButtonManager<IconState, N, I> pManager) {
        final TethysScrollMenu<IconState, I> myMenu = pManager.getMenu();
        myMenu.addItem(IconState.OPEN);
        myMenu.addItem(IconState.CLOSED);
        pManager.setValue(IconState.CLOSED);
    }

    /**
     * validate a dilution.
     * @param pDilution the dilution
     * @return null for valid or error message for invalid
     */
    public static String validateDilution(final TethysDilution pDilution) {
        if (pDilution == null) {
            return "Dilution cannot be null";
        }
        if (!pDilution.isPositive()) {
            return "Dilution must be positive";
        }
        if (pDilution.compareTo(TethysDilution.MAX_DILUTION) > 0) {
            return "Dilution must be be less or equal to " + TethysDilution.MAX_DILUTION;
        }
        return null;
    }

    /**
     * ENum for state.
     */
    public enum IconState {
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
        IconState(final String pDisplay) {
            theDisplay = pDisplay;
        }

        @Override
        public String toString() {
            return theDisplay;
        }
    }
}
