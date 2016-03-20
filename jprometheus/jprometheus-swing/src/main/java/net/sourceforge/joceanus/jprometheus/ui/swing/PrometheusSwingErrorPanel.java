/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusErrorPanel;

/**
 * Error panel.
 * @author Tony Washer
 */
public class PrometheusSwingErrorPanel
        extends PrometheusErrorPanel<JComponent> {
    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * The error field.
     */
    private final JLabel theErrorField;

    /**
     * The clear button.
     */
    private final JButton theClearButton;

    /**
     * Constructor.
     * @param pManager the data manager
     * @param pParent the parent data entry
     */
    public PrometheusSwingErrorPanel(final MetisViewerManager pManager,
                      final MetisViewerEntry pParent) {
        /* Initialise underlying class */
        super(pManager, pParent);

        /* Create the error field */
        theErrorField = new JLabel();

        /* Create the clear button */
        theClearButton = new JButton(NLS_CLEAR);

        /* Add the listener for item changes */
        theClearButton.addActionListener(e -> clearErrors());

        /* Create the error panel */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.X_AXIS));
        thePanel.setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(theClearButton);
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(theErrorField);

        /* Set the Error panel to be red and invisible */
        theErrorField.setForeground(Color.red);
        thePanel.setVisible(false);
    }

    @Override
    public JPanel getNode() {
        return thePanel;
    }

    @Override
    protected void setErrorText(final String pText) {
        /* Set the string for the error field */
        theErrorField.setText(pText);

        /* Make the panel visible */
        thePanel.setVisible(true);
    }

    @Override
    public void setVisible(final boolean bVisible) {
        thePanel.setVisible(bVisible);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theClearButton.setEnabled(bEnabled);
    }
}
