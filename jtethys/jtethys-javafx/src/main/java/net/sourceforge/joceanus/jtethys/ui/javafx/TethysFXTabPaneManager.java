/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;

/**
 * FX Tab Manager.
 */
public class TethysFXTabPaneManager
        extends TethysTabPaneManager {
    /**
     * The Node.
     */
    private final TethysFXNode theNode;

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
     * @param pFactory the GUI factory
     */
    TethysFXTabPaneManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the pane */
        theTabPane = new TabPane();
        theModel = theTabPane.getSelectionModel();
        theNode = new TethysFXNode(theTabPane);

        /* Tabs cannot be closed */
        theTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        /* Listen to selections */
        theModel.selectedItemProperty().addListener((v, o, n) -> notifySelection(n.getUserData()));
    }

    @Override
    public TethysFXNode getNode() {
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
    public TethysFXTabItem addTabItem(final String pName,
                                      final TethysComponent pItem) {
        return new TethysFXTabItem(this, pName, pItem);
    }

    @Override
    protected void enablePane(final boolean pEnabled) {
        theTabPane.setDisable(!pEnabled);
    }

    @Override
    public TethysFXTabItem getSelectedTab() {
        return (TethysFXTabItem) theModel.getSelectedItem().getUserData();
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
    public static class TethysFXTabItem
            extends TethysTabItem {
        /**
         * The Node.
         */
        private final TethysComponent theNode;

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
        TethysFXTabItem(final TethysFXTabPaneManager pPane,
                        final String pName,
                        final TethysComponent pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Create the pane */
            theTab = new Tab();
            theTab.setContent(TethysFXNode.getNode(pItem));
            theTab.setText(pName);
            theTab.setUserData(this);
            theNode = pItem;

            /* Add to the TabPane */
            theTabList = getPane().getTabPane().getTabs();
            theTabList.add(theTab);
        }

        @Override
        public TethysFXTabPaneManager getPane() {
            return (TethysFXTabPaneManager) super.getPane();
        }

        @Override
        public TethysFXNode getNode() {
            return (TethysFXNode) theNode.getNode();
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
