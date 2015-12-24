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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/swing/JScrollListButton.java $
 * $Revision: 585 $
 * $Author: Tony $
 * $Date: 2015-03-30 06:24:29 +0100 (Mon, 30 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;

/**
 * Swing MenuBar Manager.
 * @param <T> the item type
 */
public class TethysSwingMenuBarManager<T>
        extends TethysMenuBarManager<T, JComponent> {
    /**
     * The MenuBar.
     */
    private final JMenuBar theMenuBar;

    /**
     * Constructor.
     */
    public TethysSwingMenuBarManager() {
        theMenuBar = new JMenuBar();
    }

    @Override
    public JComponent getNode() {
        return theMenuBar;
    }

    @Override
    public TethysSwingMenuBarSubMenu addSubMenu(final String pText) {
        TethysSwingMenuBarSubMenu myMenu = new TethysSwingMenuBarSubMenu(pText);
        theMenuBar.add(myMenu.getMenu());
        return myMenu;
    }

    /**
     * SwingMenu.
     */
    public class TethysSwingMenuBarSubMenu
            extends TethysMenuBarSubMenu {
        /**
         * The SubMenu.
         */
        private final JMenu theMenu;

        /**
         * Constructor.
         * @param pText the menu text
         */
        protected TethysSwingMenuBarSubMenu(final String pText) {
            theMenu = new JMenu(pText);
        }

        /**
         * Obtain the menu.
         * @return the menu
         */
        private JMenu getMenu() {
            return theMenu;
        }

        @Override
        public TethysSwingMenuBarSubMenu addSubMenu(final String pText) {
            TethysSwingMenuBarSubMenu myMenu = new TethysSwingMenuBarSubMenu(pText);
            theMenu.add(myMenu.getMenu());
            incrementItemCount();
            return myMenu;
        }

        @Override
        public TethysSwingMenuBarItem addMenuItem(final T pItem) {
            TethysSwingMenuBarItem myItem = new TethysSwingMenuBarItem(pItem);
            theMenu.add(myItem.getMenuItem());
            incrementItemCount();
            return myItem;
        }

        @Override
        public void addSeparator() {
            theMenu.addSeparator();
        }

        @Override
        public void clearItems() {
            theMenu.removeAll();
            super.clearItems();
        }

        @Override
        protected void enableMenu(final boolean pEnabled) {
            theMenu.setEnabled(pEnabled);
        }
    }

    /**
     * SwingMenuItem.
     */
    public class TethysSwingMenuBarItem
            extends TethysMenuBarItem {
        /**
         * The Menu Item.
         */
        private final JMenuItem theMenuItem;

        /**
         * Constructor.
         * @param pItem the item
         */
        protected TethysSwingMenuBarItem(final T pItem) {
            /* Initialise underlying class */
            super(pItem);
            theMenuItem = new JMenuItem();
            theMenuItem.setText(pItem.toString());

            /* Add listener */
            theMenuItem.addActionListener(e -> notifySelection());
        }

        /**
         * Obtain the menu item.
         * @return the menu item
         */
        private JMenuItem getMenuItem() {
            return theMenuItem;
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theMenuItem.setEnabled(pEnabled);
        }
    }
}
