/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;

/**
 * Swing Grid Pane Manager.
 */
public class TethysSwingGridPaneManager
        extends TethysGridPaneManager {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

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
    TethysSwingGridPaneManager(final TethysSwingGuiFactory pFactory) {
        /* Initialise the panel */
        super(pFactory);
        thePanel = new JPanel();
        theLayout = new GridBagLayout();
        thePanel.setLayout(theLayout);
        theConstraintMap = new HashMap<>();
        theNode = new TethysSwingNode(thePanel);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void addCellAtPosition(final TethysComponent pNode,
                                  final int pRow,
                                  final int pColumn) {
        /* Access the node */
        final JComponent myNode = TethysSwingNode.getComponent(pNode);

        /* Obtain the padding size */
        final int myHPad = getHGap() >> 1;
        final int myVPad = getVGap() >> 1;

        /* Create constraints */
        final GridBagConstraints myConstraints = new GridBagConstraints();
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
    public void setCellColumnSpan(final TethysComponent pNode,
                                  final int pNumCols) {
        final GridBagConstraints myConstraints = theConstraintMap.get(pNode.getId());
        if (myConstraints != null) {
            myConstraints.gridwidth = pNumCols;
            theLayout.setConstraints(TethysSwingNode.getComponent(pNode), myConstraints);
        }
    }

    @Override
    public void setFinalCell(final TethysComponent pNode) {
        setCellColumnSpan(pNode, GridBagConstraints.REMAINDER);
    }

    @Override
    public void allowCellGrowth(final TethysComponent pNode) {
        final GridBagConstraints myConstraints = theConstraintMap.get(pNode.getId());
        if (myConstraints != null) {
            myConstraints.weightx = 1.0;
            theLayout.setConstraints(TethysSwingNode.getComponent(pNode), myConstraints);
        }
    }

    @Override
    public void setCellAlignment(final TethysComponent pNode,
                                 final TethysAlignment pAlign) {
        final GridBagConstraints myConstraints = theConstraintMap.get(pNode.getId());
        if (myConstraints != null) {
            myConstraints.anchor = determineAlignment(pAlign);
            myConstraints.fill = GridBagConstraints.VERTICAL;
            theLayout.setConstraints(TethysSwingNode.getComponent(pNode), myConstraints);
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

    /**
     * Translate alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private static int determineAlignment(final TethysAlignment pAlign) {
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
