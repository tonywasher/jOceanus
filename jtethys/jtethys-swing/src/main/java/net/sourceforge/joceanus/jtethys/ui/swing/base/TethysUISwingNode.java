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
package net.sourceforge.joceanus.jtethys.ui.swing.base;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUINode;

/**
 * Swing Node instance.
 */
public class TethysUISwingNode
        implements TethysUINode {
    /**
     * The underlying node.
     */
    private final JComponent theUnderlying;

    /**
     * The node.
     */
    private JComponent theNode;

    /**
     * Constructor.
     * @param pComponent the component
     */
    public TethysUISwingNode(final JComponent pComponent) {
        theUnderlying = pComponent;
        theNode = theUnderlying;
    }

    /**
     * Obtain the underlying node.
     * @return the icon
     */
    public JComponent getNode() {
        return theNode;
    }

    /**
     * Obtain the child component.
     * @param pChild the Tethys child component
     * @return the Swing component.
     */
    public static JComponent getComponent(final TethysUIComponent pChild) {
        return  pChild == null
                ? null
                : ((TethysUISwingNode) pChild.getNode()).getNode();
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
     * Set the preferred width of the underlying node.
     * @param pWidth the preferred width
     */
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theUnderlying.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theUnderlying.setPreferredSize(myDim);
    }

    /**
     * Set the preferred height of the underlying node.
     * @param pHeight the preferred height
     */
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theUnderlying.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theUnderlying.setPreferredSize(myDim);
    }

    /**
     * create wrapper pane.
     * @param pTitle the title
     * @param pPadding the padding
     */
    public void createWrapperPane(final String pTitle,
                                  final Integer pPadding) {
        /* If the underlying node is a JPanel/JLabel */
        if (theUnderlying instanceof JPanel
                || theUnderlying instanceof JLabel) {
            /* Set the panel border */
            TethysUISwingUtils.setPanelBorder(pTitle, pPadding, theUnderlying);

            /* Else add a bordered panel */
        } else {
            theNode = TethysUISwingUtils.addPanelBorder(pTitle, pPadding, theUnderlying);
        }
    }
}
