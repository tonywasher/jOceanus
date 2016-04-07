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

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

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
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingGridPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        thePanel = new JPanel();
        thePanel.setLayout(new GridLayout());
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
    public void addSingleCell(final TethysNode<JComponent> pNode) {
        /* Access the node */
        JComponent myNode = pNode.getNode();

        /* Create constraints */
        GridBagConstraints myConstraints = new GridBagConstraints();
        myConstraints.gridx = getColumnIndex();
        myConstraints.gridy = getRowIndex();
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);

        /* add the node */
        thePanel.add(myNode, myConstraints);
        addNode(pNode);

        /* Shift to next column */
        useColumns(1);
    }

    @Override
    public void addFinalCell(final TethysNode<JComponent> pNode) {
        /* Access the node */
        JComponent myNode = pNode.getNode();

        /* Create constraints */
        GridBagConstraints myConstraints = new GridBagConstraints();
        myConstraints.gridx = getColumnIndex();
        myConstraints.gridy = getRowIndex();
        myConstraints.gridwidth = GridBagConstraints.REMAINDER;
        myConstraints.fill = GridBagConstraints.HORIZONTAL;
        myConstraints.weightx = 1.0;
        myConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);

        /* add the node */
        thePanel.add(myNode, myConstraints);
        addNode(pNode);

        /* Shift to next row */
        newRow();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        thePanel.setBorder(BorderFactory.createTitledBorder(pTitle));
    }
}
