/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataWalker.DebugManager;
import net.sourceforge.JDataWalker.DebugManager.DebugEntry;
import net.sourceforge.JDataWalker.ModelException;
import uk.co.tolcroft.models.ui.StdInterfaces.stdPanel;

public class ErrorPanel extends JPanel {
    private static final long serialVersionUID = -1868069138054965874L;

    /* Members */
    private stdPanel theParent = null;
    private JLabel theErrorField = null;
    private JButton theClearButton = null;
    private DebugEntry theDebugError = null;
    private ModelException theError = null;

    /**
     * Constructor
     * @param pParent the parent
     */
    public ErrorPanel(stdPanel pParent) {
        /* Store parent */
        theParent = pParent;

        /* Create the error debug entry for this view */
        DebugManager myDebugMgr = theParent.getDebugManager();
        theDebugError = myDebugMgr.new DebugEntry("Error");
        theDebugError.addAsChildOf(theParent.getDebugEntry());
        theDebugError.hideEntry();

        /* Create the error field */
        theErrorField = new JLabel();

        /* Create the clear button */
        theClearButton = new JButton("Clear");

        /* Add the listener for item changes */
        theClearButton.addActionListener(new ErrorListener());

        /* Create the error panel */
        setBorder(javax.swing.BorderFactory.createTitledBorder("Error"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(this);
        setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                myLayout.createSequentialGroup().addContainerGap().addComponent(theClearButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(theErrorField)
                        .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theClearButton).addComponent(theErrorField));

        /* Set the Error panel to be red and invisible */
        theErrorField.setForeground(Color.red);
        setVisible(false);
    }

    /**
     * Set error indication for window
     * @param pException the exception
     */
    public void setError(ModelException pException) {
        /* Record the error */
        theError = pException;

        /* Set the string for the error field */
        theErrorField.setText(pException.getMessage());

        /* Make the panel visible */
        setVisible(true);

        /* Show the debug */
        theDebugError.setObject(theError);
        theDebugError.showEntry();

        /* Call the parent to lock other windows */
        theParent.lockOnError(true);
    }

    /**
     * Clear error indication for this window
     */
    public void clearError() {
        /* If we currently have an error */
        if (theError != null) {
            /* Clear the error */
            theError = null;
            theDebugError.setObject(theError);
            theDebugError.hideEntry();
        }

        /* Make the panel invisible */
        setVisible(false);

        /* Call the parent to unlock other windows */
        theParent.lockOnError(false);
    }

    /**
     * Listener class
     */
    private class ErrorListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            /* If this event relates to the Clear box */
            if (evt.getSource() == theClearButton) {
                /* Clear the error */
                clearError();
            }
        }
    }
}
