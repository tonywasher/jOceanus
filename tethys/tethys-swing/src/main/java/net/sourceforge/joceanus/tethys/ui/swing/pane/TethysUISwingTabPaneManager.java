/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.swing.pane;

import javax.swing.JTabbedPane;

import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.pane.TethysUICoreTabPaneManager;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;

/**
 * Swing Tab Manager.
 */
public class TethysUISwingTabPaneManager
        extends TethysUICoreTabPaneManager {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The TabPane.
     */
    private final JTabbedPane theTabPane;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected TethysUISwingTabPaneManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the pane */
        theTabPane = new JTabbedPane();
        theTabPane.addChangeListener(e -> notifySelection(getSelectedTab()));

        /* Create the node */
        theNode = new TethysUISwingNode(theTabPane);
    }

    @Override
    public TethysUISwingNode getNode() {
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
    public TethysUISwingTabItem findItemByName(final String pName) {
        return (TethysUISwingTabItem) super.findItemByName(pName);
    }

    @Override
    public TethysUISwingTabItem findItemByIndex(final int pIndex) {
        return (TethysUISwingTabItem) super.findItemByIndex(pIndex);
    }

    @Override
    public TethysUISwingTabItem addTabItem(final String pName,
                                           final TethysUIComponent pItem) {
        return new TethysUISwingTabItem(this, pName, pItem);
    }

    @Override
    protected void enablePane(final boolean pEnabled) {
        theTabPane.setEnabled(pEnabled);
    }

    @Override
    public TethysUISwingTabItem getSelectedTab() {
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
    public static class TethysUISwingTabItem
            extends TethysUICoreTabItem {
        /**
         * The component.
         */
        private final TethysUIComponent theComponent;

        /**
         * Constructor.
         * @param pPane the containing pane
         * @param pName the name of the tab
         * @param pItem the item
         */
        TethysUISwingTabItem(final TethysUISwingTabPaneManager pPane,
                             final String pName,
                             final TethysUIComponent pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Add to the TabPane */
            theComponent = pItem;
            pPane.getTabPane().addTab(pName, TethysUISwingNode.getComponent(theComponent));
        }

        @Override
        public TethysUISwingTabPaneManager getPane() {
            return (TethysUISwingTabPaneManager) super.getPane();
        }

        @Override
        protected void attachToPane() {
            final int myIndex = countPreviousVisibleSiblings();
            getPane().getTabPane().insertTab(getName(), null,
                    TethysUISwingNode.getComponent(theComponent), null, myIndex);
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
