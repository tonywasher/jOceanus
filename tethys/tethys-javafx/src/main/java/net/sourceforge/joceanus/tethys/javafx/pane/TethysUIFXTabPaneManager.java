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
package net.sourceforge.joceanus.tethys.javafx.pane;

import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.core.pane.TethysUICoreTabPaneManager;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * FX Tab Manager.
 */
public class TethysUIFXTabPaneManager
        extends TethysUICoreTabPaneManager {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The TabPane.
     */
    private final TabPane theTabPane;

    /**
     * The SelectionModel.
     */
    private final SingleSelectionModel<Tab> theModel;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    TethysUIFXTabPaneManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the pane */
        theTabPane = new TabPane();
        theModel = theTabPane.getSelectionModel();
        theNode = new TethysUIFXNode(theTabPane);

        /* Tabs cannot be closed */
        theTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        /* Listen to selections */
        theModel.selectedItemProperty().addListener((v, o, n) -> notifySelection(n == null ? null : n.getUserData()));
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    /**
     * Obtain the tabPane.
     *
     * @return the tabPane
     */
    TabPane getTabPane() {
        return theTabPane;
    }

    /**
     * Obtain the selection model.
     *
     * @return the selection model
     */
    SingleSelectionModel<Tab> getSelectionModel() {
        return theModel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public TethysUIFXTabItem addTabItem(final String pName,
                                        final TethysUIComponent pItem) {
        return new TethysUIFXTabItem(this, pName, pItem);
    }

    @Override
    protected void enablePane(final boolean pEnabled) {
        theTabPane.setDisable(!pEnabled);
    }

    @Override
    public TethysUIFXTabItem getSelectedTab() {
        return (TethysUIFXTabItem) theModel.getSelectedItem().getUserData();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theTabPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theTabPane.setPrefHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    /**
     * TabItem class.
     */
    public static class TethysUIFXTabItem
            extends TethysUICoreTabItem {
        /**
         * The Node.
         */
        private final TethysUIComponent theNode;

        /**
         * The Tab.
         */
        private final Tab theTab;

        /**
         * The Tabs.
         */
        private final ObservableList<Tab> theTabList;

        /**
         * Constructor.
         *
         * @param pPane the containing pane
         * @param pName the name of the tab
         * @param pItem the item
         */
        TethysUIFXTabItem(final TethysUIFXTabPaneManager pPane,
                          final String pName,
                          final TethysUIComponent pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Create the pane */
            theTab = new Tab();
            theTab.setContent(TethysUIFXNode.getNode(pItem));
            theTab.setText(pName);
            theTab.setUserData(this);
            theNode = pItem;

            /* Add to the TabPane */
            theTabList = getPane().getTabPane().getTabs();
            theTabList.add(theTab);
        }

        @Override
        public TethysUIFXTabPaneManager getPane() {
            return (TethysUIFXTabPaneManager) super.getPane();
        }

        @Override
        protected void attachToPane() {
            final int myIndex = countPreviousVisibleSiblings();
            theTabList.add(myIndex, theTab);
        }

        @Override
        protected void detachFromPane() {
            final int myIndex = countPreviousVisibleSiblings();
            theTabList.remove(myIndex);
        }

        @Override
        public void selectItem() {
            if (isVisible()) {
                final int myIndex = countPreviousVisibleSiblings();
                getPane().getSelectionModel().select(myIndex);
            }
        }

        @Override
        protected void enableTab(final boolean pEnable) {
            theTab.setDisable(!pEnable);
            theNode.setEnabled(pEnable);
        }
    }
}
