/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;

/**
 * FX Tab Manager.
 */
public class TethysFXTabPaneManager
        extends TethysTabPaneManager<Node, Node> {
    /**
     * The Node.
     */
    private Region theNode;

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
     * @param pFactory the GUI factory
     */
    protected TethysFXTabPaneManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the pane */
        theTabPane = new TabPane();
        theModel = theTabPane.getSelectionModel();
        theNode = theTabPane;

        /* Tabs cannot be closed */
        theTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        /* Listen to selections */
        theModel.selectedItemProperty().addListener((v, o, n) -> notifySelection(n.getUserData()));
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public TethysFXTabItem addTabItem(final String pName,
                                      final TethysNode<Node> pItem) {
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
        createWrapperPane();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        createWrapperPane();
    }

    /**
     * create wrapper pane.
     */
    private void createWrapperPane() {
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theTabPane);
    }

    /**
     * TabItem class.
     */
    public static class TethysFXTabItem
            extends TethysTabItem<Node, Node> {
        /**
         * The Node.
         */
        private final TethysNode<Node> theNode;

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
         * @param pPane the containing pane
         * @param pName the name of the tab
         * @param pItem the item
         */
        protected TethysFXTabItem(final TethysFXTabPaneManager pPane,
                                  final String pName,
                                  final TethysNode<Node> pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Create the pane */
            theTab = new Tab();
            theTab.setContent(pItem.getNode());
            theTab.setText(pName);
            theTab.setUserData(this);
            theNode = pItem;

            /* Add to the TabPane */
            theTabList = getPane().theTabPane.getTabs();
            theTabList.add(theTab);
        }

        @Override
        public TethysFXTabPaneManager getPane() {
            return (TethysFXTabPaneManager) super.getPane();
        }

        @Override
        public Node getNode() {
            return theTab.getContent();
        }

        @Override
        protected void attachToPane() {
            int myIndex = countPreviousVisibleSiblings();
            theTabList.add(myIndex, theTab);
        }

        @Override
        protected void detachFromPane() {
            int myIndex = countPreviousVisibleSiblings();
            theTabList.remove(myIndex);
        }

        @Override
        public void selectItem() {
            if (isVisible()) {
                int myIndex = countPreviousVisibleSiblings();
                getPane().theModel.select(myIndex);
            }
        }

        @Override
        protected void enableTab(final boolean pEnable) {
            theTab.setDisable(!pEnable);
            theNode.setEnabled(pEnable);
        }
    }
}
