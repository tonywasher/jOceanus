/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.control;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.control.TethysUICoreSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.pane.TethysUIFXBorderPaneManager;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 *
 * @param <T> the item type
 */
public final class TethysUIFXSplitTreeManager<T>
        extends TethysUICoreSplitTreeManager<T> {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * Split pane.
     */
    private final SplitPane theSplit;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysUIFXSplitTreeManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Store HTML pane in border pane */
        final TethysUIFXBorderPaneManager myHTMLPane = getHTMLPane();
        myHTMLPane.setCentre(getHTMLManager());

        /* Create the split pane */
        theSplit = new SplitPane();
        theSplit.setOrientation(Orientation.HORIZONTAL);
        theSplit.getItems().addAll(TethysUIFXNode.getNode(getTreeManager()),
                TethysUIFXNode.getNode(myHTMLPane));

        /* create the node */
        theNode = new TethysUIFXNode(theSplit);

        /* Set the default weight */
        setWeight(DEFAULT_WEIGHT);
    }

    @Override
    public TethysUIFXTreeManager<T> getTreeManager() {
        return (TethysUIFXTreeManager<T>) super.getTreeManager();
    }

    @Override
    public TethysUIFXHTMLManager getHTMLManager() {
        return (TethysUIFXHTMLManager) super.getHTMLManager();
    }

    @Override
    protected TethysUIFXBorderPaneManager getHTMLPane() {
        return (TethysUIFXBorderPaneManager) super.getHTMLPane();
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
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

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theSplit.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theSplit.setPrefHeight(pHeight);
    }

    @Override
    public void setWeight(final double pWeight) {
        super.setWeight(pWeight);
        Platform.runLater(() -> theSplit.setDividerPositions(getWeight()));
    }
}
