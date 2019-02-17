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

import javax.swing.JTabbedPane;

import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;

/**
 * Swing Tab Manager.
 */
public class TethysSwingTabPaneManager
        extends TethysTabPaneManager {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The TabPane.
     */
    private final JTabbedPane theTabPane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingTabPaneManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the pane */
        theTabPane = new JTabbedPane();
        theTabPane.addChangeListener(e -> notifySelection(getSelectedTab()));

        /* Create the node */
        theNode = new TethysSwingNode(theTabPane);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    /**
     * Obtain the tabPane.
     * @return the tabPane
     */
    JTabbedPane getTabPane() {
        return theTabPane;
    }

    @Override
    public TethysSwingTabItem findItemByName(final String pName) {
        return (TethysSwingTabItem) super.findItemByName(pName);
    }

    @Override
    public TethysSwingTabItem findItemByIndex(final int pIndex) {
        return (TethysSwingTabItem) super.findItemByIndex(pIndex);
    }

    @Override
    public TethysSwingTabItem addTabItem(final String pName,
                                         final TethysComponent pItem) {
        return new TethysSwingTabItem(this, pName, pItem);
    }

    @Override
    protected void enablePane(final boolean pEnabled) {
        theTabPane.setEnabled(pEnabled);
    }

    @Override
    public TethysSwingTabItem getSelectedTab() {
        return findItemByIndex(theTabPane.getSelectedIndex());
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
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
    public static class TethysSwingTabItem
            extends TethysTabItem {
        /**
         * The component.
         */
        private final TethysComponent theComponent;

        /**
         * Constructor.
         * @param pPane the containing pane
         * @param pName the name of the tab
         * @param pItem the item
         */
        TethysSwingTabItem(final TethysSwingTabPaneManager pPane,
                           final String pName,
                           final TethysComponent pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Add to the TabPane */
            theComponent = pItem;
            pPane.getTabPane().addTab(pName, TethysSwingNode.getComponent(theComponent));
        }

        @Override
        public TethysSwingTabPaneManager getPane() {
            return (TethysSwingTabPaneManager) super.getPane();
        }

        @Override
        public TethysSwingNode getNode() {
            return (TethysSwingNode) theComponent.getNode();
        }

        @Override
        protected void attachToPane() {
            final int myIndex = countPreviousVisibleSiblings();
            getPane().getTabPane().insertTab(getName(), null,
                    TethysSwingNode.getComponent(theComponent), null, myIndex);
        }

        @Override
        protected void detachFromPane() {
            final int myIndex = countPreviousVisibleSiblings();
            getPane().getTabPane().remove(myIndex);
        }

        @Override
        public void selectItem() {
            if (isVisible()) {
                final int myIndex = countPreviousVisibleSiblings();
                getPane().getTabPane().setSelectedIndex(myIndex);
            }
        }

        @Override
        protected void enableTab(final boolean pEnabled) {
            final int myIndex = countPreviousVisibleSiblings();
            getPane().getTabPane().setEnabledAt(myIndex, pEnabled);
            theComponent.setEnabled(pEnabled);
        }
    }
}
