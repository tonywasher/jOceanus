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

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;

/**
 * JavaFX MenuBar Manager.
 */
public class TethysFXMenuBarManager
        extends TethysMenuBarManager<Node> {
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
    public <I> TethysFXMenuSubMenu<I> newSubMenu(final I pId) {
        TethysFXMenuSubMenu<I> myMenu = new TethysFXMenuSubMenu<>(pId);
        theMenuBar.getMenus().add(myMenu.getMenu());
        return myMenu;
    }

    @Override
    public <I> TethysFXMenuSubMenu<I> lookUpSubMenu(final I pId) {
        return (TethysFXMenuSubMenu<I>) super.lookUpSubMenu(pId);
    }

    /**
     * JavaFXSubMenu.
     * @param <S> the id type
     */
    public class TethysFXMenuSubMenu<S>
            extends TethysMenuSubMenu<S> {
        /**
         * The SubMenu.
         */
        private final Menu theMenu;

        /**
         * Constructor.
         * @param pId the Id
         */
        protected TethysFXMenuSubMenu(final S pId) {
            super(pId);
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
        public <I> TethysFXMenuSubMenu<I> newSubMenu(final I pId) {
            TethysFXMenuSubMenu<I> myMenu = new TethysFXMenuSubMenu<>(pId);
            theMenu.getItems().add(myMenu.getMenu());
            incrementItemCount();
            return myMenu;
        }

        @Override
        public <I> TethysFXMenuItem<I> newMenuItem(final I pId,
                                                   final Consumer<I> pAction) {
            TethysFXMenuItem<I> myItem = new TethysFXMenuItem<>(pId, pAction);
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
     * @param <I> the id type
     */
    public class TethysFXMenuItem<I>
            extends TethysMenuItem<I> {
        /**
         * The Menu Item.
         */
        private final MenuItem theMenuItem;

        /**
         * Constructor.
         * @param pId the id
         * @param pAction the action
         */
        protected TethysFXMenuItem(final I pId,
                                   final Consumer<I> pAction) {
            /* Initialise underlying class */
            super(pId, pAction);
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
