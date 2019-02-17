/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;

/**
 * Swing MenuBar Manager.
 */
public class TethysSwingMenuBarManager
        extends TethysMenuBarManager {
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

    /**
     * Obtain the node.
     * @return the node
     */
    public JMenuBar getNode() {
        return theMenuBar;
    }

    @Override
    public <I> TethysSwingMenuSubMenu<I> newSubMenu(final I pId) {
        final TethysSwingMenuSubMenu<I> myMenu = new TethysSwingMenuSubMenu<>(this, pId);
        theMenuBar.add(myMenu.getMenu());
        return myMenu;
    }

    @Override
    public <I> TethysSwingMenuSubMenu<I> lookUpSubMenu(final I pId) {
        return (TethysSwingMenuSubMenu<I>) super.lookUpSubMenu(pId);
    }

    /**
     * SwingSubMenu.
     * @param <S> the id type
     */
    public static class TethysSwingMenuSubMenu<S>
            extends TethysMenuSubMenu<S> {
        /**
         * The SubMenu.
         */
        private final JMenu theMenu;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the Id
         */
        protected TethysSwingMenuSubMenu(final TethysSwingMenuBarManager pManager,
                                         final S pId) {
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
        protected TethysSwingMenuBarManager getManager() {
            return (TethysSwingMenuBarManager) super.getManager();
        }

        @Override
        public <I> TethysSwingMenuSubMenu<I> newSubMenu(final I pId) {
            final TethysSwingMenuSubMenu<I> myMenu = new TethysSwingMenuSubMenu<>(getManager(), pId);
            theMenu.add(myMenu.getMenu());
            incrementItemCount();
            return myMenu;
        }

        @Override
        public <I> TethysSwingMenuItem<I> newMenuItem(final I pId,
                                                      final Consumer<I> pAction) {
            final TethysSwingMenuItem<I> myItem = new TethysSwingMenuItem<>(getManager(), pId, pAction);
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
     * @param <I> the id type
     */
    public static class TethysSwingMenuItem<I>
            extends TethysMenuItem<I> {
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
        protected TethysSwingMenuItem(final TethysSwingMenuBarManager pManager,
                                      final I pId,
                                      final Consumer<I> pAction) {
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
