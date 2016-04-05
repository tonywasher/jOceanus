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
package net.sourceforge.joceanus.jmetis.newviewer.javafx;

import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTreeManager.TethysFXTreeItem;

/**
 * JavaFX Data Viewer Entry.
 */
public class MetisFXViewerEntry
        extends MetisViewerEntry<MetisFXViewerEntry, Node, Node> {
    /**
     * The Associated Tree Item.
     */
    private TethysFXTreeItem<MetisFXViewerEntry> theTreeItem;

    /**
     * Constructor.
     * @param pManager the viewer manager
     * @param pName the entry name
     */
    protected MetisFXViewerEntry(final MetisFXViewerManager pManager,
                                 final String pName) {
        /* Store parameters */
        super(pManager, pName);
    }

    /**
     * Get tree item.
     * @return the tree item
     */
    protected TethysFXTreeItem<MetisFXViewerEntry> getTreeItem() {
        return theTreeItem;
    }

    /**
     * Set tree item.
     * @param pItem the tree item
     */
    protected void setTreeItem(final TethysFXTreeItem<MetisFXViewerEntry> pItem) {
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
