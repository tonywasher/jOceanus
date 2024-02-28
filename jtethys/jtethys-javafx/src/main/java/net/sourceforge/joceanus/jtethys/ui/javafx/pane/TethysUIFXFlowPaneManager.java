/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.pane;

import java.util.Iterator;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.pane.TethysUICoreFlowPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;

/**
 * FX Flow Pane Manager.
 */
public class TethysUIFXFlowPaneManager
        extends TethysUICoreFlowPaneManager {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The FlowPane.
     */
    private final FlowPane theFlowPane;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    TethysUIFXFlowPaneManager(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theFlowPane = new FlowPane();
        theNode = new TethysUIFXNode(theFlowPane);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysUIComponent pNode) {
        super.addNode(pNode);
        theFlowPane.getChildren().add(TethysUIFXNode.getNode(pNode));
    }

    @Override
    public void setChildVisible(final TethysUIComponent pChild,
                                final boolean pVisible) {
        /* Handle nothing to do */
        final Node myChildNode = TethysUIFXNode.getNode(pChild);
        final boolean isVisible = myChildNode.isVisible();
        if (isVisible == pVisible) {
            return;
        }

        /* If the node is not visible */
        if (pVisible) {
            /* Count visible prior siblings */
            final int myId = pChild.getId();
            int myIndex = 0;
            final Iterator<TethysUIComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TethysUIComponent myNode = myIterator.next();
                final Integer myNodeId = myNode.getId();

                /* If we have found the node */
                if (myNodeId == myId) {
                    /* Set visible and add into the list */
                    myChildNode.setVisible(true);
                    theFlowPane.getChildren().add(myIndex, myChildNode);
                    break;
                }

                /* Increment count if node is visible */
                if (TethysUIFXNode.getNode(myNode).isVisible()) {
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
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }
}
