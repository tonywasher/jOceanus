/*******************************************************************************
 * jTethys: Java Utilities
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.List;

import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;

/**
 * Helper functions for Scroll Examples.
 * @param <I> the icon type
 */
public class TethysScrollUITestHelper<N, I> {
    /**
     * The max items.
     */
    private static final int MAX_ITEMS = 4;

    /**
     * Available items.
     */
    private static final String[] AVAILABLE_ITEMS =
    { "Work", "Important", "Personal" };

    /**
     * Build the context menu.
     * @param pMenu the menu to build
     */
    public void buildContextMenu(final TethysScrollMenu<String, I> pMenu) {
        /* Set the display count */
        pMenu.setMaxDisplayItems(MAX_ITEMS);
        pMenu.removeAllItems();

        /* Create the menu item */
        addMenuItem(pMenu, "First");
        addMenuItem(pMenu, "Second");
        addMenuItem(pMenu, "Third");
        addMenuItem(pMenu, "Fourth");
        TethysScrollSubMenu<String, I> myMenu = addSubMenu(pMenu, "SubMenu");
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
    private void addMenuItem(final TethysScrollMenu<String, I> pMenu,
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
    private TethysScrollSubMenu<String, I> addSubMenu(final TethysScrollMenu<String, I> pMenu,
                                                      final String pValue) {
        /* Add to context menu */
        return pMenu.addSubMenu(pValue);
    }

    /**
     * Add SubMenu Item for string.
     * @param pMenu the subMenu
     * @param pValue the value to add
     */
    private void addSubMenuItem(final TethysScrollSubMenu<String, I> pMenu,
                                final String pValue) {
        /* Add to sub menu */
        pMenu.getSubMenu().addItem(pValue);
    }

    /**
     * Build the available items.
     * @param pManager the list manager
     * @param pSelected the list of selected items
     */
    public void buildAvailableItems(final TethysListButtonManager<String, N, I> pManager,
                                    final List<String> pSelected) {
        /* Set the display count */
        pManager.getMenu().setMaxDisplayItems(MAX_ITEMS);
        pManager.clearAvailableItems();

        /* Loop through the items */
        for (String myValue : AVAILABLE_ITEMS) {
            pManager.setAvailableItem(myValue);
            if (pSelected.contains(myValue)) {
                pManager.setSelectedItem(myValue);
            }
        }
    }

    /**
     * Adjust selected values.
     * @param pValue the item that has been toggled
     * @param pSelected the list of selected items
     */
    public void adjustSelected(final String pValue,
                               final List<String> pSelected) {
        /* Toggle membership */
        if (pSelected.contains(pValue)) {
            pSelected.remove(pValue);
        } else {
            pSelected.add(pValue);
        }
    }

    /**
     * Format selected values.
     * @param pSelected the list of selected items
     * @return the formatted values
     */
    public String formatSelected(final List<String> pSelected) {
        /* Toggle membership */
        StringBuilder myBuilder = new StringBuilder();
        for (String myValue : pSelected) {
            if (myBuilder.length() > 0) {
                myBuilder.append(',');
            }
            myBuilder.append(myValue);
        }
        return myBuilder.toString();
    }

    /**
     * Build the simple IconState.
     * @param pIconManager the menu to build
     * @param pFalseIcon the icon for the false state
     * @param pTrueIcon the icon for the true state
     */
    public void buildSimpleIconState(final TethysSimpleIconButtonManager<Boolean, N, I> pIconManager,
                                     final I pFalseIcon,
                                     final I pTrueIcon) {
        /* Set the state */
        pIconManager.setIconForValue(Boolean.FALSE, pFalseIcon);
        pIconManager.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pIconManager.setTooltipForValue(Boolean.FALSE, "False");
        pIconManager.setIconForValue(Boolean.TRUE, pTrueIcon);
        pIconManager.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pIconManager.setTooltipForValue(Boolean.TRUE, "True");
        pIconManager.setValue(Boolean.TRUE);
    }

    /**
     * Build the state IconState.
     * @param pIconManager the menu to build
     * @param pFalseIcon the icon for the false state
     * @param pTrueIcon the icon for the true state
     * @param pAltTrueIcon the icon for the true closed state
     */
    public void buildStateIconState(final TethysStateIconButtonManager<Boolean, IconState, N, I> pIconManager,
                                    final I pFalseIcon,
                                    final I pTrueIcon,
                                    final I pAltTrueIcon) {
        /* Set the CLOSED state */
        pIconManager.setMachineState(IconState.CLOSED);
        pIconManager.setIconForValue(Boolean.FALSE, null);
        pIconManager.setNewValueForValue(Boolean.FALSE, Boolean.FALSE);
        pIconManager.setTooltipForValue(Boolean.FALSE, "False");
        pIconManager.setIconForValue(Boolean.TRUE, pAltTrueIcon);
        pIconManager.setNewValueForValue(Boolean.TRUE, Boolean.TRUE);
        pIconManager.setTooltipForValue(Boolean.TRUE, "True");

        /* Set the OPEN state */
        pIconManager.setMachineState(IconState.OPEN);
        pIconManager.setIconForValue(Boolean.FALSE, pFalseIcon);
        pIconManager.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pIconManager.setTooltipForValue(Boolean.FALSE, "False");
        pIconManager.setIconForValue(Boolean.TRUE, pTrueIcon);
        pIconManager.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pIconManager.setTooltipForValue(Boolean.TRUE, "True");
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
