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
package net.sourceforge.joceanus.jtethys.ui.swing.control;

import javax.swing.JSplitPane;

import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.control.TethysUICoreSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 */
public final class TethysUISwingSplitTreeManager<T>
        extends TethysUICoreSplitTreeManager<T> {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * Split pane.
     */
    private final JSplitPane theSplit;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysUISwingSplitTreeManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create scroll-panes */
        final TethysUIScrollPaneManager myTreeScroll = pFactory.paneFactory().newScrollPane();
        myTreeScroll.setContent(getTreeManager());
        final TethysUIScrollPaneManager myHTMLScroll = pFactory.paneFactory().newScrollPane();
        myHTMLScroll.setContent(getHTMLManager());

        /* Store HTML pane in border pane */
        final TethysUIBorderPaneManager myHTMLPane = getHTMLPane();
        myHTMLPane.setCentre(myHTMLScroll);

        /* Create the split pane */
        theSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                TethysUISwingNode.getComponent(myTreeScroll),
                TethysUISwingNode.getComponent(myHTMLPane));
        theSplit.setOneTouchExpandable(true);

        /* create the node */
        theNode = new TethysUISwingNode(theSplit);

        /* Set the default weight */
        setWeight(DEFAULT_WEIGHT);
    }

    @Override
    public TethysUISwingTreeManager<T> getTreeManager() {
        return (TethysUISwingTreeManager<T>) super.getTreeManager();
    }

    @Override
    public TethysUISwingHTMLManager getHTMLManager() {
        return (TethysUISwingHTMLManager) super.getHTMLManager();
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSplit.setEnabled(pEnabled);
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
    public void setWeight(final double pWeight) {
        super.setWeight(pWeight);
        theSplit.setResizeWeight(pWeight);
    }
}
