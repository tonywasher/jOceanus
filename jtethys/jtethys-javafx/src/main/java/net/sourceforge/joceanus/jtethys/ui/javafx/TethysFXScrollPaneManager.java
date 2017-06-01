/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;

/**
 * javaFX Scroll Pane Manager.
 */
public class TethysFXScrollPaneManager
        extends TethysScrollPaneManager<Node, Node> {
    /**
     * The node.
     */
    private Region theNode;

    /**
     * ScrollPane.
     */
    private final ScrollPane theScrollPane;

    /**
     * Content.
     */
    private TethysNode<Node> theContent;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysFXScrollPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theScrollPane = new ScrollPane();
        theNode = theScrollPane;
        theScrollPane.setFitToHeight(true);
        theScrollPane.setFitToWidth(true);
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theScrollPane.setDisable(!pEnabled);
        if (theContent != null) {
            theContent.setEnabled(pEnabled);
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setContent(final TethysNode<Node> pNode) {
        theContent = pNode;
        theScrollPane.setContent(pNode == null
                                               ? null
                                               : pNode.getNode());
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theScrollPane);
    }
}
