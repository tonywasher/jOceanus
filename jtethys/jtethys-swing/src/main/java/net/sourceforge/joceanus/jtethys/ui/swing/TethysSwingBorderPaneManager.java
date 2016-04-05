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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Swing BorderPane Manager.
 */
public class TethysSwingBorderPaneManager
        extends TethysBorderPaneManager<JComponent, Icon> {
    /**
     * The BorderPane.
     */
    private final JPanel theNode;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingBorderPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theNode = new JPanel();
        theNode.setLayout(new BorderLayout());
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setCentre(final TethysNode<JComponent> pNode) {
        super.setCentre(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.CENTRE);
    }

    @Override
    public void setNorth(final TethysNode<JComponent> pNode) {
        super.setNorth(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.NORTH);
    }

    @Override
    public void setSouth(final TethysNode<JComponent> pNode) {
        super.setSouth(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.SOUTH);
    }

    @Override
    public void setWest(final TethysNode<JComponent> pNode) {
        super.setWest(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.WEST);
    }

    @Override
    public void setEast(final TethysNode<JComponent> pNode) {
        super.setEast(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.EAST);
    }

    @Override
    protected void removeNode(final TethysNode<JComponent> pNode) {
        theNode.remove(pNode.getNode());
        super.removeNode(pNode);
    }

    @Override
    protected void setLocationVisibility(final TethysBorderLocation pLocation,
                                         final boolean pVisible) {
        /* Set visibility of item */
        getNodeForLocation(pLocation).setVisible(pVisible);
    }

    /**
     * Add node at location.
     * @param pNode the node
     * @param pLocation the location
     */
    private void addNodeAtLocation(final TethysNode<JComponent> pNode,
                                   final TethysBorderLocation pLocation) {
        if (pNode != null) {
            theNode.add(pNode.getNode(), getLayoutForLocation(pLocation));
        }
    }

    /**
     * Obtain layout for location.
     * @param pLocation the location
     * @return the layout
     */
    private String getLayoutForLocation(final TethysBorderLocation pLocation) {
        switch (pLocation) {
            case NORTH:
                return BorderLayout.PAGE_START;
            case SOUTH:
                return BorderLayout.PAGE_END;
            case WEST:
                return BorderLayout.LINE_START;
            case EAST:
                return BorderLayout.LINE_END;
            case CENTRE:
            default:
                return BorderLayout.CENTER;
        }
    }
}
