/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;
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
     * The default password value.
     */
    public static final char[] PASS_DEF = "Password".toCharArray();

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
        TethysScrollSubMenu<String, ?> myMenu = addSubMenu(pMenu, "SubMenu");
        addSubMenuItem(myMenu, "AAAAA");
        addSubMenuItem(myMenu, "BBBBB");
        addMenuItem(pMenu, "Fifth");
        addMenuItem(pMenu, "Sixth");
        addMenuItem(pMenu, "Seventh");
        addMenuItem(pMenu, "Eighth");
    }

    /**
     * Add Menu Item for string.
     * @param pMenu the menu to add to
     * @param pValue the value to add
     */
    private void addMenuItem(final TethysScrollMenu<String, ?> pMenu,
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
    private TethysScrollSubMenu<String, ?> addSubMenu(final TethysScrollMenu<String, ?> pMenu,
                                                      final String pValue) {
        /* Add to context menu */
        return pMenu.addSubMenu(pValue);
    }

    /**
     * Add SubMenu Item for string.
     * @param pMenu the subMenu
     * @param pValue the value to add
     */
    private void addSubMenuItem(final TethysScrollSubMenu<String, ?> pMenu,
                                final String pValue) {
        /* Add to sub menu */
        pMenu.getSubMenu().addItem(pValue);
    }

    /**
     * Create list.
     * @return the list
     */
    public TethysItemList<TethysListId> buildToggleList() {
        /* Create the list */
        TethysItemList<TethysListId> myValues = new TethysItemList<>();

        /* Loop through the items */
        for (TethysListId myValue : TethysListId.values()) {
            myValues.setSelectableItem(myValue);
        }

        /* Select the work value */
        myValues.selectItem(TethysListId.WORK);

        /* Set the value */
        return myValues;
    }

    /**
     * Build the simple IconState.
     * @param <K> the keyId type
     * @param pIconManager the menu to build
     * @param pFalseIcon the icon for the false state
     * @param pTrueIcon the icon for the true state
     */
    public <K extends Enum<K> & TethysIconId> void buildSimpleIconState(final TethysSimpleIconButtonManager<Boolean, ?, ?> pIconManager,
                                                                        final K pFalseIcon,
                                                                        final K pTrueIcon) {
        /* Set the state */
        pIconManager.setDetailsForValue(Boolean.FALSE, Boolean.TRUE, pFalseIcon, "False");
        pIconManager.setDetailsForValue(Boolean.TRUE, Boolean.FALSE, pTrueIcon, "True");
        pIconManager.setValue(Boolean.TRUE);
    }

    /**
     * Build the state IconState.
     * @param <K> the keyId type
     * @param pIconManager the menu to build
     * @param pFalseIcon the icon for the false state
     * @param pTrueIcon the icon for the true state
     * @param pAltTrueIcon the icon for the true closed state
     */
    public <K extends Enum<K> & TethysIconId> void buildStateIconState(final TethysStateIconButtonManager<Boolean, IconState, ?, ?> pIconManager,
                                                                       final K pFalseIcon,
                                                                       final K pTrueIcon,
                                                                       final K pAltTrueIcon) {
        /* Set the CLOSED state */
        pIconManager.setMachineState(IconState.CLOSED);
        pIconManager.setDetailsForValue(Boolean.FALSE, Boolean.FALSE, "False");
        pIconManager.setDetailsForValue(Boolean.TRUE, Boolean.TRUE, pAltTrueIcon, "True");

        /* Set the OPEN state */
        pIconManager.setMachineState(IconState.OPEN);
        pIconManager.setDetailsForValue(Boolean.FALSE, Boolean.TRUE, pFalseIcon, "False");
        pIconManager.setDetailsForValue(Boolean.TRUE, Boolean.FALSE, pTrueIcon, "True");
        pIconManager.setValue(Boolean.TRUE);
    }

    /**
     * build State button.
     * @param pManager the button manager
     */
    public void buildStateButton(final TethysScrollButtonManager<IconState, N, I> pManager) {
        TethysScrollMenu<IconState, I> myMenu = pManager.getMenu();
        myMenu.addItem(IconState.OPEN);
        myMenu.addItem(IconState.CLOSED);
        pManager.setValue(IconState.OPEN);
    }

    /**
     * validate a dilution
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
        private IconState(final String pDisplay) {
            theDisplay = pDisplay;
        }

        @Override
        public String toString() {
            return theDisplay;
        }
    }
}
