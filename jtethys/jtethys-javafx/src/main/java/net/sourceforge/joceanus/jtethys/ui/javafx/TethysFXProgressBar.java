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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import net.sourceforge.joceanus.jtethys.ui.TethysProgressBar;

/**
 * JavaFX ProgressBar.
 */
public class TethysFXProgressBar
        extends TethysProgressBar<Node, Node> {
    /**
     * The node.
     */
    private Node theNode;

    /**
     * The ProgressBar.
     */
    private final ProgressBar theProgress;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXProgressBar(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theProgress = new ProgressBar();
        theNode = theProgress;
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theProgress.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setProgress(final int pValue,
                            final int pMaximum) {
        setProgress(((double) pValue) / pMaximum);
    }

    @Override
    public void setProgress(final double pValue) {
        theProgress.setProgress(pValue);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theProgress.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theProgress.setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theProgress);
    }
}