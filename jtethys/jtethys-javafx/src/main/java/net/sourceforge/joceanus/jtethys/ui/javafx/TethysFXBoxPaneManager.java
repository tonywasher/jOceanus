/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;

/**
 * FX Box Pane Manager.
 */
public class TethysFXBoxPaneManager
        extends TethysBoxPaneManager {
    /**
     * The Node.
     */
    private final TethysFXNode theNode;

    /**
     * The BoxPane.
     */
    private final Pane theBoxPane;

    /**
     * Constructor.
     *
     * @param pFactory    the GUI factory
     * @param pHorizontal horizontal box true/false
     */
    TethysFXBoxPaneManager(final TethysFXGuiFactory pFactory,
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
        theNode = new TethysFXNode(theBoxPane);
    }

    @Override
    public TethysFXNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysComponent pNode) {
        super.addNode(pNode);
        theBoxPane.getChildren().add(TethysFXNode.getNode(pNode));
    }

    @Override
    public void setChildVisible(final TethysComponent pChild,
                                final boolean pVisible) {
        /* Handle nothing to do */
        final Node myChildNode = TethysFXNode.getNode(pChild);
        final boolean isVisible = myChildNode.isVisible();
        if (isVisible == pVisible) {
            return;
        }

        /* If the node is not visible */
        if (pVisible) {
            /* Count visible prior siblings */
            final int myId = pChild.getId();
            int myIndex = 0;
            final Iterator<TethysComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TethysComponent myNode = myIterator.next();
                final Integer myNodeId = myNode.getId();

                /* If we have found the node */
                if (myNodeId == myId) {
                    /* Set visible and add into the list */
                    myChildNode.setVisible(true);
                    theBoxPane.getChildren().add(myIndex, myChildNode);
                    break;
                }

                /* Increment count if node is visible */
                if (TethysFXNode.getNode(myNode).isVisible()) {
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
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void addSpacer() {
        final TethysFXSpacer mySpacer = new TethysFXSpacer(true);
        addSpacerNode(mySpacer);
        theBoxPane.getChildren().add(TethysFXNode.getNode(mySpacer));
    }

    @Override
    public void addStrut() {
        final TethysFXSpacer mySpacer = new TethysFXSpacer(false);
        addSpacerNode(mySpacer);
        theBoxPane.getChildren().add(TethysFXNode.getNode(mySpacer));
    }

    /**
     * Spacer node.
     */
    private final class TethysFXSpacer
            implements TethysComponent {
        /**
         * The Node.
         */
        private TethysFXNode theNode;

        /**
         * Region.
         */
        private final Region theRegion;

        /**
         * Constructor.
         *
         * @param pExpand true/false
         */
        TethysFXSpacer(final boolean pExpand) {
            theRegion = new Region();
            theRegion.setPrefWidth(getGap());
            theRegion.setPrefHeight(getGap());
            if (pExpand) {
                HBox.setHgrow(theRegion, Priority.ALWAYS);
                VBox.setVgrow(theRegion, Priority.ALWAYS);
            }
            theNode = new TethysFXNode(theRegion);
        }

        @Override
        public TethysFXNode getNode() {
            return theNode;
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
