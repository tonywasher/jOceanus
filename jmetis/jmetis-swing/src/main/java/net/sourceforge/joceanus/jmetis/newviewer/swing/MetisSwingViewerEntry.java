/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.newviewer.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTreeManager.TethysSwingTreeItem;

/**
 * JavaSwing Data Viewer Entry.
 */
public class MetisSwingViewerEntry
        extends MetisViewerEntry<MetisSwingViewerEntry, JComponent, Icon> {
    /**
     * The Associated Tree Item.
     */
    private TethysSwingTreeItem<MetisSwingViewerEntry> theTreeItem;

    /**
     * Constructor.
     * @param pManager the viewer manager
     * @param pName the entry name
     */
    protected MetisSwingViewerEntry(final MetisSwingViewerManager pManager,
                                    final String pName) {
        /* Store parameters */
        super(pManager, pName);
    }

    /**
     * Get tree item.
     * @return the tree item
     */
    protected TethysSwingTreeItem<MetisSwingViewerEntry> getTreeItem() {
        return theTreeItem;
    }

    /**
     * Set tree item.
     * @param pItem the tree item
     */
    protected void setTreeItem(final TethysSwingTreeItem<MetisSwingViewerEntry> pItem) {
        theTreeItem = pItem;
    }

    @Override
    public void setFocus() {
        theTreeItem.setFocus();
    }

    @Override
    public void hideEntry() {
        theTreeItem.setVisible(false);
    }

    @Override
    public void showEntry() {
        theTreeItem.setVisible(true);
    }

    @Override
    public void removeChildren() {
        theTreeItem.removeChildren();
    }
}
