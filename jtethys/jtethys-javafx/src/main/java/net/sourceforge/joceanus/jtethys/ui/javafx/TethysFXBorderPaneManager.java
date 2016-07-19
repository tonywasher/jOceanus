/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * JavaFX BorderPane Manager.
 */
public class TethysFXBorderPaneManager
        extends TethysBorderPaneManager<Node, Node> {
    /**
     * The Node.
     */
    private Region theNode;

    /**
     * The BorderPane.
     */
    private final BorderPane theBorderPane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXBorderPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theBorderPane = new BorderPane();
        theNode = theBorderPane;
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
    public void setCentre(final TethysNode<Node> pNode) {
        super.setCentre(pNode);
        theBorderPane.setCenter(pNode == null
                                              ? null
                                              : pNode.getNode());
    }

    @Override
    public void setNorth(final TethysNode<Node> pNode) {
        super.setNorth(pNode);
        if (pNode == null) {
            theBorderPane.setTop(null);
        } else {
            Node myNode = pNode.getNode();
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.NORTH));
            theBorderPane.setTop(myNode);
        }
    }

    @Override
    public void setSouth(final TethysNode<Node> pNode) {
        super.setSouth(pNode);
        if (pNode == null) {
            theBorderPane.setBottom(null);
        } else {
            Node myNode = pNode.getNode();
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.SOUTH));
            theBorderPane.setBottom(myNode);
        }
    }

    @Override
    public void setWest(final TethysNode<Node> pNode) {
        super.setWest(pNode);
        if (pNode == null) {
            theBorderPane.setLeft(null);
        } else {
            Node myNode = pNode.getNode();
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.WEST));
            theBorderPane.setLeft(myNode);
        }
    }

    @Override
    public void setEast(final TethysNode<Node> pNode) {
        super.setEast(pNode);
        if (pNode == null) {
            theBorderPane.setRight(null);
        } else {
            Node myNode = pNode.getNode();
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.EAST));
            theBorderPane.setRight(myNode);
        }
    }

    @Override
    protected void setLocationVisibility(final TethysBorderLocation pLocation,
                                         final boolean pVisible) {
        /* Determine node */
        Node myNode = pVisible
                               ? getNodeForLocation(pLocation).getNode()
                               : null;

        /* Switch on location */
        switch (pLocation) {
            case NORTH:
                theBorderPane.setTop(myNode);
                break;
            case SOUTH:
                theBorderPane.setBottom(myNode);
                break;
            case WEST:
                theBorderPane.setLeft(myNode);
                break;
            case EAST:
                theBorderPane.setRight(myNode);
                break;
            case CENTRE:
            default:
                theBorderPane.setCenter(myNode);
                break;
        }
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theBorderPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theBorderPane.setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theBorderPane);
    }

    /**
     * Obtain insets for location.
     * @param pLocation the location
     * @return the insets
     */
    private Insets getInsetsForLocation(final TethysBorderLocation pLocation) {
        switch (pLocation) {
            case NORTH:
                return new Insets(0, 0, getVGap(), 0);
            case EAST:
                return new Insets(0, 0, 0, getHGap());
            case SOUTH:
                return new Insets(getVGap(), 0, 0, 0);
            case WEST:
                return new Insets(0, getHGap(), 0, 0);
            default:
                return null;
        }
    }
}
