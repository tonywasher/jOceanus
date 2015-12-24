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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import net.sourceforge.joceanus.jtethys.ui.TethysTabManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnableTabbed;

/**
 * Swing Tab Manager.
 */
public class TethysSwingTabManager
        extends TethysTabManager<JComponent> {
    /**
     * The TabPane.
     */
    private final JTabbedPane theTabPane;

    /**
     * Constructor.
     */
    public TethysSwingTabManager() {
        /* Create the pane */
        theTabPane = new TethysSwingEnableTabbed();
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
                                         final JComponent pItem) {
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

    /**
     * TabItem class.
     */
    public static class TethysSwingTabItem
            extends TethysTabItem<JComponent> {
        /**
         * The component.
         */
        private final JComponent theNode;

        /**
         * Constructor.
         * @param pPane the containing pane
         * @param pName the name of the tab
         * @param pItem the item
         */
        protected TethysSwingTabItem(final TethysSwingTabManager pPane,
                                     final String pName,
                                     final JComponent pItem) {
            /* Initialise the underlying class */
            super(pPane, pName);

            /* Add to the TabPane */
            theNode = pItem;
            pPane.theTabPane.addTab(pName, theNode);
        }

        @Override
        public TethysSwingTabManager getPane() {
            return (TethysSwingTabManager) super.getPane();
        }

        @Override
        public JComponent getNode() {
            return theNode;
        }

        @Override
        protected void attachToPane() {
            int myIndex = countPreviousVisibleSiblings();
            getPane().theTabPane.insertTab(getName(), null, theNode, null, myIndex);
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
