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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.sourceforge.joceanus.jtethys.ui.TethysTabManager;

/**
 * FX Tab Manager.
 */
public class TethysFXTabManager
        extends TethysTabManager<Node> {
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
     */
    public TethysFXTabManager() {
        /* Create the pane */
        theTabPane = new TabPane();
        theModel = theTabPane.getSelectionModel();

        /* Listen to selections */
        theModel.selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(final ObservableValue<? extends Tab> pValue,
                                final Tab pOldValue,
                                final Tab pNewValue) {
                notifySelection(pNewValue.getUserData());
            }
        });
    }

    @Override
    public Node getNode() {
        return theTabPane;
    }

    @Override
    public TethysFXTabItem addTabItem(final String pName,
                                      final Node pItem) {
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

    /**
     * TabItem class.
     */
    public static class TethysFXTabItem
            extends TethysTabItem<Node> {
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
        protected TethysFXTabItem(final TethysFXTabManager pPane,
                                  final String pName,
                                  final Node pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Create the pane */
            theTab = new Tab();
            theTab.setContent(pItem);
            theTab.setText(pName);
            theTab.setUserData(this);

            /* Add to the TabPane */
            theTabList = getPane().theTabPane.getTabs();
            theTabList.add(theTab);
        }

        @Override
        public TethysFXTabManager getPane() {
            return (TethysFXTabManager) super.getPane();
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
        }
    }
}
