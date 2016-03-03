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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;

/**
 * FX MenuBar Manager.
 * @param <T> the item type
 */
public class TethysFXMenuBarManager<T>
        extends TethysMenuBarManager<T, Node> {
    /**
     * The MenuBar.
     */
    private final MenuBar theMenuBar;

    /**
     * Constructor.
     */
    public TethysFXMenuBarManager() {
        theMenuBar = new MenuBar();
    }

    @Override
    public Node getNode() {
        return theMenuBar;
    }

    @Override
    public TethysFXMenuBarSubMenu addSubMenu(final String pText) {
        TethysFXMenuBarSubMenu myMenu = new TethysFXMenuBarSubMenu(pText);
        theMenuBar.getMenus().add(myMenu.getMenu());
        return myMenu;
    }

    /**
     * FXMenu.
     */
    public class TethysFXMenuBarSubMenu
            extends TethysMenuBarSubMenu {
        /**
         * The SubMenu.
         */
        private final Menu theMenu;

        /**
         * Constructor.
         * @param pText the menu text
         */
        protected TethysFXMenuBarSubMenu(final String pText) {
            theMenu = new Menu(pText);
        }

        /**
         * Obtain the menu.
         * @return the menu
         */
        private Menu getMenu() {
            return theMenu;
        }

        @Override
        public TethysFXMenuBarSubMenu addSubMenu(final String pText) {
            TethysFXMenuBarSubMenu myMenu = new TethysFXMenuBarSubMenu(pText);
            theMenu.getItems().add(myMenu.getMenu());
            incrementItemCount();
            return myMenu;
        }

        @Override
        public TethysFXMenuBarItem addMenuItem(final T pItem) {
            TethysFXMenuBarItem myItem = new TethysFXMenuBarItem(pItem);
            theMenu.getItems().add(myItem.getMenuItem());
            incrementItemCount();
            return myItem;
        }

        @Override
        public void addSeparator() {
            theMenu.getItems().add(new SeparatorMenuItem());
        }

        @Override
        public void clearItems() {
            theMenu.getItems().clear();
            super.clearItems();
        }

        @Override
        protected void enableMenu(final boolean pEnabled) {
            theMenu.setDisable(!pEnabled);
        }
    }

    /**
     * FXMenuItem.
     */
    public class TethysFXMenuBarItem
            extends TethysMenuBarItem {
        /**
         * The Menu Item.
         */
        private final MenuItem theMenuItem;

        /**
         * Constructor.
         * @param pItem the item
         */
        protected TethysFXMenuBarItem(final T pItem) {
            /* Initialise underlying class */
            super(pItem);
            theMenuItem = new MenuItem();
            theMenuItem.setText(pItem.toString());

            /* Add listener */
            theMenuItem.setOnAction(e -> notifySelection());
        }

        /**
         * Obtain the menu item.
         * @return the menu item
         */
        private MenuItem getMenuItem() {
            return theMenuItem;
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theMenuItem.setDisable(!pEnabled);
        }
    }
}
