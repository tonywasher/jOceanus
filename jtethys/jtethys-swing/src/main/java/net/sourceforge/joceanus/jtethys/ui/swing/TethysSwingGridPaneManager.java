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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Swing Grid Pane Manager.
 */
public class TethysSwingGridPaneManager
        extends TethysGridPaneManager<JComponent, Icon> {
    /**
     * The Pane.
     */
    private final JPanel thePanel;

    /**
     * The Layout.
     */
    private final GridBagLayout theLayout;

    /**
     * The Constraint Map.
     */
    private final Map<Integer, GridBagConstraints> theConstraintMap;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingGridPaneManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise the panel */
        super(pFactory);
        thePanel = new JPanel();
        theLayout = new GridBagLayout();
        thePanel.setLayout(theLayout);
        theConstraintMap = new HashMap<>();
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addCellAtPosition(final TethysNode<JComponent> pNode,
                                  final int pRow,
                                  final int pColumn) {
        /* Access the node */
        JComponent myNode = pNode.getNode();

        /* Obtain the padding size */
        Integer myHPad = getHGap() >> 1;
        Integer myVPad = getVGap() >> 1;

        /* Create constraints */
        GridBagConstraints myConstraints = new GridBagConstraints();
        myConstraints.gridx = pColumn;
        myConstraints.gridy = pRow;
        myConstraints.fill = GridBagConstraints.BOTH;
        myConstraints.insets = new Insets(myVPad, myHPad, myVPad, myHPad);

        /* Store the constraints */
        theConstraintMap.put(pNode.getId(), myConstraints);

        /* add the node */
        thePanel.add(myNode, myConstraints);
        addNode(pNode);
    }

    @Override
    public void setCellColumnSpan(final TethysNode<JComponent> pNode,
                                  final int pNumCols) {
        GridBagConstraints myConstraints = theConstraintMap.get(pNode.getId());
        if (myConstraints != null) {
            myConstraints.gridwidth = pNumCols;
            theLayout.setConstraints(pNode.getNode(), myConstraints);
        }
    }

    @Override
    public void setFinalCell(final TethysNode<JComponent> pNode) {
        setCellColumnSpan(pNode, GridBagConstraints.REMAINDER);
    }

    @Override
    public void allowCellGrowth(final TethysNode<JComponent> pNode) {
        GridBagConstraints myConstraints = theConstraintMap.get(pNode.getId());
        if (myConstraints != null) {
            myConstraints.weightx = 1.0;
            theLayout.setConstraints(pNode.getNode(), myConstraints);
        }
    }

    @Override
    public void setCellAlignment(final TethysNode<JComponent> pNode,
                                 final TethysAlignment pAlign) {
        GridBagConstraints myConstraints = theConstraintMap.get(pNode.getId());
        if (myConstraints != null) {
            myConstraints.anchor = determineAlignment(pAlign);
            myConstraints.fill = GridBagConstraints.VERTICAL;
            theLayout.setConstraints(pNode.getNode(), myConstraints);
        }
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = thePanel.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        thePanel.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = thePanel.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        thePanel.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), thePanel);
    }

    /**
     * Translate alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private int determineAlignment(final TethysAlignment pAlign) {
        switch (pAlign) {
            case NORTHWEST:
                return GridBagConstraints.NORTHWEST;
            case NORTH:
                return GridBagConstraints.NORTH;
            case NORTHEAST:
                return GridBagConstraints.NORTHEAST;
            case WEST:
                return GridBagConstraints.WEST;
            case CENTRE:
                return GridBagConstraints.CENTER;
            case EAST:
                return GridBagConstraints.EAST;
            case SOUTHWEST:
                return GridBagConstraints.SOUTHWEST;
            case SOUTH:
                return GridBagConstraints.SOUTH;
            case SOUTHEAST:
            default:
                return GridBagConstraints.SOUTHEAST;
        }
    }
}
