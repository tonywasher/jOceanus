/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.swing.menu;

import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.tethys.core.menu.TethysUICoreMenuBarManager;

/**
 * Swing MenuBar Manager.
 */
public class TethysUISwingMenuBarManager
        extends TethysUICoreMenuBarManager {
    /**
     * The MenuBar.
     */
    private final JMenuBar theMenuBar;

    /**
     * Constructor.
     */
    TethysUISwingMenuBarManager() {
        theMenuBar = new JMenuBar();
    }

    /**
     * Obtain the node.
     * @return the node
     */
    public JMenuBar getNode() {
        return theMenuBar;
    }

    @Override
    public TethysUISwingMenuSubMenu newSubMenu(final TethysUIMenuId pId) {
        final TethysUISwingMenuSubMenu myMenu = new TethysUISwingMenuSubMenu(this, pId);
        theMenuBar.add(myMenu.getMenu());
        return myMenu;
    }

    @Override
    public TethysUISwingMenuSubMenu lookUpSubMenu(final TethysUIMenuId pId) {
        return (TethysUISwingMenuSubMenu) super.lookUpSubMenu(pId);
    }

    /**
     * SwingSubMenu.
     */
    public static class TethysUISwingMenuSubMenu
            extends TethysUICoreMenuSubMenu {
        /**
         * The SubMenu.
         */
        private final JMenu theMenu;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the Id
         */
        protected TethysUISwingMenuSubMenu(final TethysUISwingMenuBarManager pManager,
                                           final TethysUIMenuId pId) {
            super(pManager, pId);
            theMenu = new JMenu(pId.toString());
        }

        /**
         * Obtain the menu.
         * @return the menu
         */
        protected JMenu getMenu() {
            return theMenu;
        }

        @Override
        protected TethysUISwingMenuBarManager getManager() {
            return (TethysUISwingMenuBarManager) super.getManager();
        }

        @Override
        public TethysUISwingMenuSubMenu newSubMenu(final TethysUIMenuId pId) {
            final TethysUISwingMenuSubMenu myMenu = new TethysUISwingMenuSubMenu(getManager(), pId);
            theMenu.add(myMenu.getMenu());
            incrementItemCount();
            return myMenu;
        }

        @Override
        public TethysUISwingMenuItem newMenuItem(final TethysUIMenuId pId,
                                                 final Consumer<TethysUIMenuId> pAction) {
            final TethysUISwingMenuItem myItem = new TethysUISwingMenuItem(getManager(), pId, pAction);
            theMenu.add(myItem.getItem());
            incrementItemCount();
            return myItem;
        }

        @Override
        public void newSeparator() {
            theMenu.addSeparator();
        }

        @Override
        public void clearItems() {
            theMenu.removeAll();
            super.clearItems();
        }

        @Override
        protected void setVisible(final boolean pVisible) {
            theMenu.setVisible(pVisible);
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theMenu.setEnabled(pEnabled);
        }
    }

    /**
     * SwingMenuItem.
     */
    public static class TethysUISwingMenuItem
            extends TethysUICoreMenuItem {
        /**
         * The Menu Item.
         */
        private final JMenuItem theMenuItem;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         * @param pAction the action
         */
        protected TethysUISwingMenuItem(final TethysUISwingMenuBarManager pManager,
                                        final TethysUIMenuId pId,
                                        final Consumer<TethysUIMenuId> pAction) {
            /* Initialise underlying class */
            super(pManager, pId, pAction);
            theMenuItem = new JMenuItem();
            theMenuItem.setText(pId.toString());

            /* Add listener */
            theMenuItem.addActionListener(e -> notifyAction());
        }

        /**
         * Obtain the item.
         * @return the item
         */
        protected JMenuItem getItem() {
            return theMenuItem;
        }

        @Override
        protected void setVisible(final boolean pVisible) {
            theMenuItem.setVisible(pVisible);
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theMenuItem.setEnabled(pEnabled);
        }
    }
}
