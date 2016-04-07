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
import javafx.scene.layout.BorderPane;
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
    private Node theNode;

    /**
     * The BorderPane.
     */
    private final BorderPane theBorderNode;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXBorderPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theBorderNode = new BorderPane();
        theNode = theBorderNode;
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setCentre(final TethysNode<Node> pNode) {
        super.setCentre(pNode);
        theBorderNode.setCenter(pNode == null
                                              ? null
                                              : pNode.getNode());
    }

    @Override
    public void setNorth(final TethysNode<Node> pNode) {
        super.setNorth(pNode);
        theBorderNode.setTop(pNode == null
                                           ? null
                                           : pNode.getNode());
    }

    @Override
    public void setSouth(final TethysNode<Node> pNode) {
        super.setSouth(pNode);
        theBorderNode.setBottom(pNode == null
                                              ? null
                                              : pNode.getNode());
    }

    @Override
    public void setWest(final TethysNode<Node> pNode) {
        super.setWest(pNode);
        theBorderNode.setLeft(pNode == null
                                            ? null
                                            : pNode.getNode());
    }

    @Override
    public void setEast(final TethysNode<Node> pNode) {
        super.setEast(pNode);
        theBorderNode.setRight(pNode == null
                                             ? null
                                             : pNode.getNode());
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
                theBorderNode.setTop(myNode);
                break;
            case SOUTH:
                theBorderNode.setBottom(myNode);
                break;
            case WEST:
                theBorderNode.setLeft(myNode);
                break;
            case EAST:
                theBorderNode.setRight(myNode);
                break;
            case CENTRE:
            default:
                theBorderNode.setCenter(myNode);
                break;
        }
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        theNode = TethysFXGuiUtils.getTitledPane(pTitle, theBorderNode);
    }
}
