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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/ui/swing/GridBagUtilities.java $
 * $Revision: 642 $
 * $Author: Tony $
 * $Date: 2015-08-20 05:56:10 +0100 (Thu, 20 Aug 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Grid Bag helper class for simple forms.
 */
public class TethysSwingGridBagHelper {
    /**
     * Panel.
     */
    private final JPanel thePanel;

    /**
     * GridBagConstraints.
     */
    private GridBagConstraints theConstraints;

    /**
     * Row number.
     */
    private int theRowNo;

    /**
     * Insets.
     */
    private Insets theInsets;

    /**
     * Constructor.
     * @param pPanel the panel
     */
    public TethysSwingGridBagHelper(final JPanel pPanel) {
        thePanel = pPanel;
        thePanel.setLayout(new GridBagLayout());
        theConstraints = new GridBagConstraints();
        theRowNo = 0;
    }

    /**
     * Set Insets size.
     * @param pSize the InSet size.
     */
    public void setInsetSize(final int pSize) {
        theInsets = new Insets(pSize, pSize, pSize, pSize);
    }

    /**
     * Add small labelled row.
     * @param pLabel the label to add
     * @param pComponent the component to add
     */
    public void addSmallLabeledRow(final JComponent pLabel,
                                   final JComponent pComponent) {
        /* Add the label field */
        setLabelConstraints();
        thePanel.add(pLabel, theConstraints);

        /* Add the small field */
        setSmallFieldConstraints();
        thePanel.add(pComponent, theConstraints);

        /* Add the remainder field */
        setRemainderConstraints();
        thePanel.add(new JLabel(), theConstraints);
    }

    /**
     * Add full labelled row.
     * @param pLabel the label to add
     * @param pComponent the component to add
     */
    public void addFullLabeledRow(final JComponent pLabel,
                                  final JComponent pComponent) {
        /* Add the label field */
        setLabelConstraints();
        thePanel.add(pLabel, theConstraints);

        /* Add the large field */
        setLargeFieldConstraints();
        thePanel.add(pComponent, theConstraints);
    }

    /**
     * Add full row.
     * @param pComponent the component to add
     */
    public void addFullRow(final JComponent pComponent) {
        /* Add the full row */
        setFullRowConstraints();
        thePanel.add(pComponent, theConstraints);
    }

    /**
     * Set constraints for a label.
     */
    private void setLabelConstraints() {
        theConstraints.gridx = 0;
        theConstraints.gridy = theRowNo;
        theConstraints.gridwidth = 1;
        theConstraints.fill = GridBagConstraints.HORIZONTAL;
        theConstraints.weightx = 0.0;
        theConstraints.anchor = GridBagConstraints.LINE_END;
        theConstraints.insets = theInsets;
    }

    /**
     * Set constraints for a small field.
     */
    private void setSmallFieldConstraints() {
        theConstraints.gridx = 1;
        theConstraints.gridy = theRowNo;
        theConstraints.gridwidth = 1;
        theConstraints.fill = GridBagConstraints.NONE;
        theConstraints.weightx = 0.0;
        theConstraints.anchor = GridBagConstraints.LINE_START;
        theConstraints.insets = theInsets;
    }

    /**
     * Set constraints for a remainder field.
     */
    private void setRemainderConstraints() {
        theConstraints.gridx = 2;
        theConstraints.gridy = theRowNo++;
        theConstraints.gridwidth = GridBagConstraints.REMAINDER;
        theConstraints.fill = GridBagConstraints.HORIZONTAL;
        theConstraints.weightx = 1.0;
        theConstraints.anchor = GridBagConstraints.LINE_START;
        theConstraints.insets = theInsets;
    }

    /**
     * Set constraints for a large field.
     */
    private void setLargeFieldConstraints() {
        theConstraints.gridx = 1;
        theConstraints.gridy = theRowNo++;
        theConstraints.gridwidth = GridBagConstraints.REMAINDER;
        theConstraints.fill = GridBagConstraints.BOTH;
        theConstraints.weightx = 1.0;
        theConstraints.anchor = GridBagConstraints.LINE_START;
        theConstraints.insets = theInsets;
    }

    /**
     * Get constraints for a full row.
     */
    private void setFullRowConstraints() {
        theConstraints.gridx = 0;
        theConstraints.gridy = theRowNo++;
        theConstraints.gridwidth = GridBagConstraints.REMAINDER;
        theConstraints.fill = GridBagConstraints.HORIZONTAL;
        theConstraints.weightx = 1.0;
        theConstraints.anchor = GridBagConstraints.LINE_START;
        theConstraints.insets = theInsets;
    }
}
