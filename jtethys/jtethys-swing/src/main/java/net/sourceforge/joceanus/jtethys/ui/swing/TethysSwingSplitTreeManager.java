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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 */
public class TethysSwingSplitTreeManager<T>
        extends TethysSplitTreeManager<T, JComponent, Icon> {
    /**
     * Split pane.
     */
    private final JSplitPane theSplit;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingSplitTreeManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(new TethysSwingTreeManager<T>(), new TethysSwingHTMLManager(pFactory));

        /* Create scroll-panes */
        JScrollPane myTreeScroll = new JScrollPane(getTreeManager().getNode());
        JScrollPane myHTMLScroll = new JScrollPane(getHTMLManager().getNode());

        /* Create the split pane */
        theSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myTreeScroll, myHTMLScroll);
        theSplit.setOneTouchExpandable(true);
    }

    @Override
    public TethysSwingTreeManager<T> getTreeManager() {
        return (TethysSwingTreeManager<T>) super.getTreeManager();
    }

    @Override
    public TethysSwingHTMLManager getHTMLManager() {
        return (TethysSwingHTMLManager) super.getHTMLManager();
    }

    @Override
    public JComponent getNode() {
        return theSplit;
    }
}
