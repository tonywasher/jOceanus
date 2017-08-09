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

import java.util.Iterator;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * FX Box Pane Manager.
 */
public class TethysFXBoxPaneManager
        extends TethysBoxPaneManager<Node, Node> {
    /**
     * The Node.
     */
    private Region theNode;

    /**
     * The BoxPane.
     */
    private final Pane theBoxPane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pHorizontal horizontal box true/false
     */
    protected TethysFXBoxPaneManager(final TethysFXGuiFactory pFactory,
                                     final boolean pHorizontal) {
        super(pFactory);
        if (pHorizontal) {
            final HBox myBox = new HBox(getGap());
            myBox.setAlignment(Pos.CENTER);
            theBoxPane = myBox;
        } else {
            final VBox myBox = new VBox(getGap());
            myBox.setAlignment(Pos.CENTER);
            theBoxPane = myBox;
        }
        theNode = theBoxPane;
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
        theBoxPane.getChildren().add(pNode.getNode());
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
                    theBoxPane.getChildren().add(myIndex, myChildNode);
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
            theBoxPane.getChildren().remove(myChildNode);
        }
    }

    @Override
    public void setGap(final Integer pGap) {
        super.setGap(pGap);
        if (theBoxPane instanceof HBox) {
            ((HBox) theBoxPane).setSpacing(getGap());
        } else if (theBoxPane instanceof VBox) {
            ((VBox) theBoxPane).setSpacing(getGap());
        }
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theBoxPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theBoxPane.setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theBoxPane);
    }

    @Override
    public void addSpacer() {
        final TethysFXSpacer mySpacer = new TethysFXSpacer(true);
        addSpacerNode(mySpacer);
        theBoxPane.getChildren().add(mySpacer.getNode());
    }

    @Override
    public void addStrut() {
        final TethysFXSpacer mySpacer = new TethysFXSpacer(false);
        addSpacerNode(mySpacer);
        theBoxPane.getChildren().add(mySpacer.getNode());
    }

    /**
     * Spacer node.
     */
    private final class TethysFXSpacer
            implements TethysNode<Node> {
        /**
         * Region.
         */
        private final Region theRegion;

        /**
         * Constructor.
         * @param pExpand true/false
         */
        private TethysFXSpacer(final boolean pExpand) {
            theRegion = new Region();
            theRegion.setPrefWidth(getGap());
            theRegion.setPrefHeight(getGap());
            if (pExpand) {
                HBox.setHgrow(theRegion, Priority.ALWAYS);
                VBox.setVgrow(theRegion, Priority.ALWAYS);
            }
        }

        @Override
        public Node getNode() {
            return theRegion;
        }

        @Override
        public void setEnabled(final boolean pEnabled) {
            theRegion.setDisable(!pEnabled);
        }

        @Override
        public void setVisible(final boolean pVisible) {
            theRegion.setVisible(pVisible);
        }

        @Override
        public Integer getId() {
            return -1;
        }
    }
}
