/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.pane;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.pane.TethysUICoreBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * Swing BorderPane Manager.
 */
public class TethysUISwingBorderPaneManager
        extends TethysUICoreBorderPaneManager {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

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
     * @param pFactory the factory
     */
    TethysUISwingBorderPaneManager(final TethysUICoreFactory<?> pFactory) {
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
        theNode = new TethysUISwingNode(thePanel);
    }

    @Override
    public TethysUISwingNode getNode() {
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
    public void setCentre(final TethysUIComponent pNode) {
        super.setCentre(pNode);
        addNodeAtLocation(pNode, TethysUIBorderLocation.CENTRE);
    }

    @Override
    public void setNorth(final TethysUIComponent pNode) {
        super.setNorth(pNode);
        addNodeAtLocation(pNode, TethysUIBorderLocation.NORTH);
    }

    @Override
    public void setSouth(final TethysUIComponent pNode) {
        super.setSouth(pNode);
        addNodeAtLocation(pNode, TethysUIBorderLocation.SOUTH);
    }

    @Override
    public void setWest(final TethysUIComponent pNode) {
        super.setWest(pNode);
        addNodeAtLocation(pNode, TethysUIBorderLocation.WEST);
    }

    @Override
    public void setEast(final TethysUIComponent pNode) {
        super.setEast(pNode);
        addNodeAtLocation(pNode, TethysUIBorderLocation.EAST);
    }

    @Override
    protected void removeNode(final TethysUIComponent pNode) {
        thePanel.remove(TethysUISwingNode.getComponent(pNode));
        super.removeNode(pNode);
    }

    @Override
    protected void setLocationVisibility(final TethysUIBorderLocation pLocation,
                                         final boolean pVisible) {
        /* Set visibility of item */
        getNodeForLocation(pLocation).setVisible(pVisible);
    }

    /**
     * Add node at location.
     * @param pNode the node
     * @param pLocation the location
     */
    private void addNodeAtLocation(final TethysUIComponent pNode,
                                   final TethysUIBorderLocation pLocation) {
        if (pNode != null) {
            thePanel.add(TethysUISwingNode.getComponent(pNode), getLayoutForLocation(pLocation));
        }
    }

    /**
     * Obtain layout for location.
     * @param pLocation the location
     * @return the layout
     */
    private String getLayoutForLocation(final TethysUIBorderLocation pLocation) {
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
