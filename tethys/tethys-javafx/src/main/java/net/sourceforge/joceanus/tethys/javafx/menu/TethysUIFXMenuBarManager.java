/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.javafx.menu;

import java.util.function.Consumer;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import net.sourceforge.joceanus.tethys.core.menu.TethysUICoreMenuBarManager;

/**
 * JavaFX MenuBar Manager.
 */
public class TethysUIFXMenuBarManager
        extends TethysUICoreMenuBarManager {
    /**
     * The MenuBar.
     */
    private final MenuBar theMenuBar;

    /**
     * Constructor.
     */
    TethysUIFXMenuBarManager() {
        theMenuBar = new MenuBar();
    }

    /**
     * Obtain the node.
     * @return the node
     */
    public MenuBar getNode() {
        return theMenuBar;
    }

    @Override
    public TethysUIFXMenuSubMenu newSubMenu(final TethysUIMenuId pId) {
        final TethysUIFXMenuSubMenu myMenu = new TethysUIFXMenuSubMenu(this, pId);
        theMenuBar.getMenus().add(myMenu.getMenu());
        return myMenu;
    }

    @Override
    public TethysUIFXMenuSubMenu lookUpSubMenu(final TethysUIMenuId pId) {
        return (TethysUIFXMenuSubMenu) super.lookUpSubMenu(pId);
    }

    /**
     * JavaFXSubMenu.
     */
    public static class TethysUIFXMenuSubMenu
            extends TethysUICoreMenuSubMenu {
        /**
         * The SubMenu.
         */
        private final Menu theMenu;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the Id
         */
        protected TethysUIFXMenuSubMenu(final TethysUIFXMenuBarManager pManager,
                                        final TethysUIMenuId pId) {
            super(pManager, pId);
            theMenu = new Menu(pId.toString());
        }

        /**
         * Obtain the menu.
         * @return the menu
         */
        protected Menu getMenu() {
            return theMenu;
        }

        @Override
        protected TethysUIFXMenuBarManager getManager() {
            return (TethysUIFXMenuBarManager) super.getManager();
        }

        @Override
        public TethysUIFXMenuSubMenu newSubMenu(final TethysUIMenuId pId) {
            final TethysUIFXMenuSubMenu myMenu = new TethysUIFXMenuSubMenu(getManager(), pId);
            theMenu.getItems().add(myMenu.getMenu());
            incrementItemCount();
            return myMenu;
        }

        @Override
        public TethysUIFXMenuItem newMenuItem(final TethysUIMenuId pId,
                                              final Consumer<TethysUIMenuId> pAction) {
            final TethysUIFXMenuItem myItem = new TethysUIFXMenuItem(getManager(), pId, pAction);
            theMenu.getItems().add(myItem.getItem());
            incrementItemCount();
            return myItem;
        }

        @Override
        public void newSeparator() {
            theMenu.getItems().add(new SeparatorMenuItem());
        }

        @Override
        public void clearItems() {
            theMenu.getItems().clear();
            super.clearItems();
        }

        @Override
        protected void setVisible(final boolean pVisible) {
            theMenu.setVisible(pVisible);
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theMenu.setDisable(!pEnabled);
        }
    }

    /**
     * JavaFXMenuItem.
     */
    public static class TethysUIFXMenuItem
            extends TethysUICoreMenuItem {
        /**
         * The Menu Item.
         */
        private final MenuItem theMenuItem;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         * @param pAction the action
         */
        protected TethysUIFXMenuItem(final TethysUIFXMenuBarManager pManager,
                                     final TethysUIMenuId pId,
                                     final Consumer<TethysUIMenuId> pAction) {
            /* Initialise underlying class */
            super(pManager, pId, pAction);
            theMenuItem = new MenuItem();
            theMenuItem.setText(pId.toString());

            /* Add listener */
            theMenuItem.setOnAction(e -> notifyAction());
        }

        /**
         * Obtain the item.
         * @return the item
         */
        protected MenuItem getItem() {
            return theMenuItem;
        }

        @Override
        protected void setVisible(final boolean pVisible) {
            theMenuItem.setVisible(pVisible);
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theMenuItem.setDisable(!pEnabled);
        }
    }
}
