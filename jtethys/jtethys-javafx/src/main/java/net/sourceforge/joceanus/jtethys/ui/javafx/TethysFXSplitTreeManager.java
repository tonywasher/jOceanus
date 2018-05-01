/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 * @param <T> the item type
 */
public final class TethysFXSplitTreeManager<T>
        extends TethysSplitTreeManager<T, Node, Node> {
    /**
     * Split pane.
     */
    private final SplitPane theSplit;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXSplitTreeManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Store HTML pane in border pane */
        final TethysFXBorderPaneManager myHTMLPane = getHTMLPane();
        myHTMLPane.setCentre(getHTMLManager());

        /* Create the split pane */
        theSplit = new SplitPane();
        theSplit.setOrientation(Orientation.HORIZONTAL);
        theSplit.getItems().addAll(getTreeManager().getNode(), myHTMLPane.getNode());
    }

    @Override
    public TethysFXTreeManager<T> getTreeManager() {
        return (TethysFXTreeManager<T>) super.getTreeManager();
    }

    @Override
    public TethysFXHTMLManager getHTMLManager() {
        return (TethysFXHTMLManager) super.getHTMLManager();
    }

    @Override
    protected TethysFXBorderPaneManager getHTMLPane() {
        return (TethysFXBorderPaneManager) super.getHTMLPane();
    }

    @Override
    public Node getNode() {
        return theSplit;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSplit.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theSplit.setVisible(pVisible);
        theSplit.setManaged(pVisible);
    }
}
