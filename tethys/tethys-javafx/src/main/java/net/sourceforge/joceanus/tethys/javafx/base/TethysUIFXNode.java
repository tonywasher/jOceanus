/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.base;

import javafx.scene.Node;
import javafx.scene.layout.Region;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUINode;

/**
 * javaFX Node.
 */
public class TethysUIFXNode
        implements TethysUINode {
    /**
     * The node.
     */
    private final Node theUnderlying;

    /**
     * The node.
     */
    private Node theNode;

    /**
     * Constructor.
     * @param pNode the node
     */
    public TethysUIFXNode(final Node pNode) {
        theUnderlying = pNode;
        theNode = theUnderlying;
    }

    /**
     * Obtain the node that this represents.
     * @return the node
     */
    public Node getNode() {
        return theNode;
    }

    /**
     * Obtain the node.
     * @param pChild the Tethys child component
     * @return the javaFX node.
     */
    public static Node getNode(final TethysUIComponent pChild) {
        return pChild == null
                ? null
                : ((TethysUIFXNode) pChild.getNode()).getNode();
    }

    /**
     * Is the node visible?
     * @return true/false
     */
    public boolean isVisible() {
        return theNode.isVisible();
    }

    /**
     * Set visibility of node.
     * @param pVisible set node visible true/false?
     */
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    /**
     * Set managed state of node.
     * @param pManaged set node managed true/false?
     */
    public void setManaged(final boolean pManaged) {
        theNode.setManaged(pManaged);
    }

    /**
     * Set the preferred width of the underlying node.
     * @param pWidth the preferred width
     */
    public void setPreferredWidth(final Integer pWidth) {
        ((Region) theUnderlying).setPrefWidth(pWidth);
    }

    /**
     * Set the preferred height of the underlying node.
     * @param pHeight the preferred height
     */
    public void setPreferredHeight(final Integer pHeight) {
        ((Region) theUnderlying).setPrefHeight(pHeight);
    }

    /**
     * create wrapper pane.
     * @param pTitle the title
     * @param pPadding the padding
     */
    public void createWrapperPane(final String pTitle,
                                  final Integer pPadding) {
        theNode = TethysUIFXUtils.getBorderedPane(pTitle, pPadding, theUnderlying);
    }
}
