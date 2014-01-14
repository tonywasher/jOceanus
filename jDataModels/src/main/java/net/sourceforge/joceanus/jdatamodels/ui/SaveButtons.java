/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.joceanus.jdatamodels.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import net.sourceforge.joceanus.jdatamodels.views.UpdateSet;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;

/**
 * Save buttons panel.
 * @author Tony Washer
 */
public class SaveButtons
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -6297579158428259704L;

    /**
     * OK.
     */
    public static final String CMD_OK = "OK";

    /**
     * Undo last change.
     */
    public static final String CMD_UNDO = "UNDO";

    /**
     * Reset all changes.
     */
    public static final String CMD_RESET = "RESET";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SaveButtons.class.getName());

    /**
     * Text for OK Button.
     */
    private static final String NLS_OK = NLS_BUNDLE.getString("OKButton");

    /**
     * Text for Undo Button.
     */
    private static final String NLS_UNDO = NLS_BUNDLE.getString("UndoButton");

    /**
     * Text for Reset Button.
     */
    private static final String NLS_RESET = NLS_BUNDLE.getString("ResetButton");

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("BoxTitle");

    /**
     * The update set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The OK button.
     */
    private final JButton theOKButton;

    /**
     * The Undo button.
     */
    private final JButton theUndoButton;

    /**
     * The Reset button.
     */
    private final JButton theResetButton;

    /**
     * Constructor.
     * @param pUpdateSet the update set
     */
    public SaveButtons(final UpdateSet pUpdateSet) {
        /* Create the boxes */
        theOKButton = new JButton(NLS_OK);
        theUndoButton = new JButton(NLS_UNDO);
        theResetButton = new JButton(NLS_RESET);

        /* Record the update set */
        theUpdateSet = pUpdateSet;

        /* Add the listener for item changes */
        SaveListener myListener = new SaveListener();
        theOKButton.addActionListener(myListener);
        theUndoButton.addActionListener(myListener);
        theResetButton.addActionListener(myListener);

        /* Create the save panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(theOKButton);
        add(Box.createHorizontalGlue());
        add(theUndoButton);
        add(Box.createHorizontalGlue());
        add(theResetButton);
        add(Box.createHorizontalGlue());

        /* Buttons are initially disabled */
        theOKButton.setEnabled(false);
        theUndoButton.setEnabled(false);
        theResetButton.setEnabled(false);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* If the table is locked clear the buttons */
        if (!bEnabled) {
            theOKButton.setEnabled(false);
            theUndoButton.setEnabled(false);
            theResetButton.setEnabled(false);

            /* Else look at the edit state */
        } else {
            switch (theUpdateSet.getEditState()) {
                case CLEAN:
                    theOKButton.setEnabled(false);
                    theUndoButton.setEnabled(false);
                    theResetButton.setEnabled(false);
                    break;
                case DIRTY:
                case ERROR:
                    theOKButton.setEnabled(false);
                    theUndoButton.setEnabled(true);
                    theResetButton.setEnabled(true);
                    break;
                case VALID:
                    theOKButton.setEnabled(true);
                    theUndoButton.setEnabled(true);
                    theResetButton.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Listener class.
     */
    private class SaveListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the OK button */
            if (theOKButton.equals(o)) {
                /* Pass command to the table */
                fireActionPerformed(CMD_OK);

                /* If this event relates to the Undo button */
            } else if (theUndoButton.equals(o)) {
                /* Pass command to the table */
                fireActionPerformed(CMD_UNDO);

                /* If this event relates to the Reset button */
            } else if (theResetButton.equals(o)) {
                /* Pass command to the table */
                fireActionPerformed(CMD_RESET);
            }
        }
    }
}
