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
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;

/**
 * Action buttons panel.
 * @author Tony Washer
 */
public class ActionButtons
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 8671520716355146811L;

    /**
     * Strut width.
     */
    private static final int STRUT_LENGTH = 5;

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
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(ActionButtons.class.getName());

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("BoxTitle");

    /**
     * The update set.
     */
    private final transient UpdateSet<?> theUpdateSet;

    /**
     * The Commit button.
     */
    private final JIconButton<Boolean> theCommitButton;

    /**
     * The Undo button.
     */
    private final JIconButton<Boolean> theUndoButton;

    /**
     * The Reset button.
     */
    private final JIconButton<Boolean> theResetButton;

    /**
     * Constructor.
     * @param pUpdateSet the update set
     */
    public ActionButtons(final UpdateSet<?> pUpdateSet) {
        this(pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public ActionButtons(final UpdateSet<?> pUpdateSet,
                         final boolean pHorizontal) {
        /* Record the update set */
        theUpdateSet = pUpdateSet;

        /* Create the button states */
        DefaultIconButtonState<Boolean> myCommitState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myUndoState = new DefaultIconButtonState<Boolean>();
        DefaultIconButtonState<Boolean> myResetState = new DefaultIconButtonState<Boolean>();

        /* Create the buttons */
        theCommitButton = new JIconButton<Boolean>(myCommitState);
        theUndoButton = new JIconButton<Boolean>(myUndoState);
        theResetButton = new JIconButton<Boolean>(myResetState);

        /* Make buttons the size of the icons */
        Insets myInsets = new Insets(0, 0, 0, 0);
        theCommitButton.setMargin(myInsets);
        theUndoButton.setMargin(myInsets);
        theResetButton.setMargin(myInsets);

        /* Set the states */
        PrometheusIcons.buildCommitButton(myCommitState);
        PrometheusIcons.buildUndoButton(myUndoState);
        PrometheusIcons.buildResetButton(myResetState);

        /* Create the title */
        if (pHorizontal) {
            setBorder(BorderFactory.createTitledBorder(NLS_TITLE));
        } else {
            add(new JLabel(NLS_TITLE));
        }

        /* Create the standard strut */
        Dimension myStrutSize = pHorizontal
                                           ? new Dimension(STRUT_LENGTH, 0)
                                           : new Dimension(0, STRUT_LENGTH);

        /* Define the layout */
        setLayout(new BoxLayout(this, pHorizontal
                                                 ? BoxLayout.X_AXIS
                                                 : BoxLayout.Y_AXIS));
        add(Box.createRigidArea(myStrutSize));
        add(theCommitButton);
        add(Box.createRigidArea(myStrutSize));
        add(theUndoButton);
        add(Box.createRigidArea(myStrutSize));
        add(theResetButton);
        add(Box.createRigidArea(myStrutSize));

        /* Add the listener for item changes */
        SaveListener myListener = new SaveListener();
        theCommitButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
        theUndoButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);
        theResetButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, myListener);

        /* Buttons are initially disabled */
        theCommitButton.setEnabled(false);
        theUndoButton.setEnabled(false);
        theResetButton.setEnabled(false);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* If the table is locked clear the buttons */
        if (!bEnabled) {
            theCommitButton.setEnabled(false);
            theUndoButton.setEnabled(false);
            theResetButton.setEnabled(false);

            /* Else look at the edit state */
        } else {
            /* Set the values */
            theCommitButton.storeValue(Boolean.TRUE);
            theUndoButton.storeValue(Boolean.TRUE);
            theResetButton.storeValue(Boolean.TRUE);
            switch (theUpdateSet.getEditState()) {
                case CLEAN:
                    theCommitButton.setEnabled(false);
                    theUndoButton.setEnabled(false);
                    theResetButton.setEnabled(false);
                    break;
                case DIRTY:
                case ERROR:
                    theCommitButton.setEnabled(false);
                    theUndoButton.setEnabled(true);
                    theResetButton.setEnabled(true);
                    break;
                case VALID:
                    theCommitButton.setEnabled(true);
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
            implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the OK button */
            if (theCommitButton.equals(o)) {
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
