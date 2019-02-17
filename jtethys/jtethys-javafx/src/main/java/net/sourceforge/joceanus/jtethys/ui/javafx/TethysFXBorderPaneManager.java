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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;

/**
 * JavaFX BorderPane Manager.
 */
public class TethysFXBorderPaneManager
        extends TethysBorderPaneManager {
    /**
     * The Node.
     */
    private final TethysFXNode theNode;

    /**
     * The BorderPane.
     */
    private final BorderPane theBorderPane;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysFXBorderPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theBorderPane = new BorderPane();
        theNode = new TethysFXNode(theBorderPane);
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
    public void setCentre(final TethysComponent pNode) {
        super.setCentre(pNode);
        theBorderPane.setCenter(pNode == null
                                ? null
                                : TethysFXNode.getNode(pNode));
    }

    @Override
    public void setNorth(final TethysComponent pNode) {
        super.setNorth(pNode);
        if (pNode == null) {
            theBorderPane.setTop(null);
        } else {
            final Node myNode = TethysFXNode.getNode(pNode);
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.NORTH));
            theBorderPane.setTop(myNode);
        }
    }

    @Override
    public void setSouth(final TethysComponent pNode) {
        super.setSouth(pNode);
        if (pNode == null) {
            theBorderPane.setBottom(null);
        } else {
            final Node myNode = TethysFXNode.getNode(pNode);
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.SOUTH));
            theBorderPane.setBottom(myNode);
        }
    }

    @Override
    public void setWest(final TethysComponent pNode) {
        super.setWest(pNode);
        if (pNode == null) {
            theBorderPane.setLeft(null);
        } else {
            final Node myNode = TethysFXNode.getNode(pNode);
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.WEST));
            theBorderPane.setLeft(myNode);
        }
    }

    @Override
    public void setEast(final TethysComponent pNode) {
        super.setEast(pNode);
        if (pNode == null) {
            theBorderPane.setRight(null);
        } else {
            final Node myNode = TethysFXNode.getNode(pNode);
            BorderPane.setMargin(myNode, getInsetsForLocation(TethysBorderLocation.EAST));
            theBorderPane.setRight(myNode);
        }
    }

    @Override
    protected void setLocationVisibility(final TethysBorderLocation pLocation,
                                         final boolean pVisible) {
        /* Determine node */
        final Node myNode = pVisible
                            ? TethysFXNode.getNode(getNodeForLocation(pLocation))
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
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    /**
     * Obtain insets for location.
     *
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
