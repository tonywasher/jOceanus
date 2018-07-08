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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;

/**
 * Swing BorderPane Manager.
 */
public class TethysSwingBorderPaneManager
        extends TethysBorderPaneManager {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The BorderPane.
     */
    private final JPanel thePanel;

    /**
     * The BorderLayout.
     */
    private final BorderLayout theLayout;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    TethysSwingBorderPaneManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the layout */
        theLayout = new BorderLayout();
        theLayout.setHgap(getHGap());
        theLayout.setVgap(getVGap());

        /* Create the panel */
        thePanel = new JPanel();
        thePanel.setLayout(theLayout);

        /* Create the node */
        theNode = new TethysSwingNode(thePanel);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void setHGap(final Integer pGap) {
        super.setHGap(pGap);
        theLayout.setHgap(getHGap());
    }

    @Override
    public void setVGap(final Integer pGap) {
        super.setVGap(pGap);
        theLayout.setVgap(getVGap());
    }

    @Override
    public void setCentre(final TethysComponent pNode) {
        super.setCentre(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.CENTRE);
    }

    @Override
    public void setNorth(final TethysComponent pNode) {
        super.setNorth(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.NORTH);
    }

    @Override
    public void setSouth(final TethysComponent pNode) {
        super.setSouth(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.SOUTH);
    }

    @Override
    public void setWest(final TethysComponent pNode) {
        super.setWest(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.WEST);
    }

    @Override
    public void setEast(final TethysComponent pNode) {
        super.setEast(pNode);
        addNodeAtLocation(pNode, TethysBorderLocation.EAST);
    }

    @Override
    protected void removeNode(final TethysComponent pNode) {
        thePanel.remove(TethysSwingNode.getComponent(pNode));
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
    private void addNodeAtLocation(final TethysComponent pNode,
                                   final TethysBorderLocation pLocation) {
        if (pNode != null) {
            thePanel.add(TethysSwingNode.getComponent(pNode), getLayoutForLocation(pLocation));
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

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
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
