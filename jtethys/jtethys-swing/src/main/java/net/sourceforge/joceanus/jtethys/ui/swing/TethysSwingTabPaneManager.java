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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;

/**
 * Swing Tab Manager.
 */
public class TethysSwingTabPaneManager
        extends TethysTabPaneManager<JComponent, Icon> {
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
    }

    @Override
    public JComponent getNode() {
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
                                         final TethysNode<JComponent> pItem) {
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
        theTabPane.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theTabPane.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theTabPane.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theTabPane.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theTabPane.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), theTabPane);
    }

    /**
     * TabItem class.
     */
    public static class TethysSwingTabItem
            extends TethysTabItem<JComponent, Icon> {
        /**
         * The component.
         */
        private final TethysNode<JComponent> theNode;

        /**
         * Constructor.
         * @param pPane the containing pane
         * @param pName the name of the tab
         * @param pItem the item
         */
        protected TethysSwingTabItem(final TethysSwingTabPaneManager pPane,
                                     final String pName,
                                     final TethysNode<JComponent> pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Add to the TabPane */
            theNode = pItem;
            pPane.theTabPane.addTab(pName, theNode.getNode());
        }

        @Override
        public TethysSwingTabPaneManager getPane() {
            return (TethysSwingTabPaneManager) super.getPane();
        }

        @Override
        public JComponent getNode() {
            return theNode.getNode();
        }

        @Override
        protected void attachToPane() {
            int myIndex = countPreviousVisibleSiblings();
            getPane().theTabPane.insertTab(getName(), null, theNode.getNode(), null, myIndex);
        }

        @Override
        protected void detachFromPane() {
            int myIndex = countPreviousVisibleSiblings();
            getPane().theTabPane.remove(myIndex);
        }

        @Override
        public void selectItem() {
            if (isVisible()) {
                int myIndex = countPreviousVisibleSiblings();
                getPane().theTabPane.setSelectedIndex(myIndex);
            }
        }

        @Override
        protected void enableTab(final boolean pEnabled) {
            int myIndex = countPreviousVisibleSiblings();
            getPane().theTabPane.setEnabledAt(myIndex, pEnabled);
            theNode.setEnabled(pEnabled);
        }
    }
}
