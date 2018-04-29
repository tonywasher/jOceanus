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

import java.util.Iterator;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysFlowPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * FX Flow Pane Manager.
 */
public class TethysFXFlowPaneManager
        extends TethysFlowPaneManager<Node, Node> {
    /**
     * The Node.
     */
    private Region theNode;

    /**
     * The FlowPane.
     */
    private final FlowPane theFlowPane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXFlowPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theFlowPane = new FlowPane();
        theNode = theFlowPane;
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysNode<Node> pNode) {
        super.addNode(pNode);
        theFlowPane.getChildren().add(pNode.getNode());
    }

    @Override
    public void setChildVisible(final TethysNode<Node> pChild,
                                final boolean pVisible) {
        /* Handle nothing to do */
        final Node myChildNode = pChild.getNode();
        final boolean isVisible = myChildNode.isVisible();
        if (isVisible == pVisible) {
            return;
        }

        /* If the node is not visible */
        if (pVisible) {
            /* Count visible prior siblings */
            final int myId = pChild.getId();
            int myIndex = 0;
            final Iterator<TethysNode<Node>> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TethysNode<Node> myNode = myIterator.next();
                final Integer myNodeId = myNode.getId();

                /* If we have found the node */
                if (myNodeId == myId) {
                    /* Set visible and add into the list */
                    myChildNode.setVisible(true);
                    theFlowPane.getChildren().add(myIndex, myChildNode);
                    break;
                }

                /* Increment count if node is visible */
                if (myNode.getNode().isVisible()) {
                    myIndex++;
                }
            }

            /* else we must hide the node */
        } else {
            /* set invisible and remove from the list */
            myChildNode.setVisible(false);
            theFlowPane.getChildren().remove(myChildNode);
        }
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theFlowPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theFlowPane.setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theFlowPane);
    }
}
