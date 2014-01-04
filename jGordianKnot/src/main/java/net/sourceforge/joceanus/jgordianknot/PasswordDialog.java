/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jlayoutmanager.SpringUtilities;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public class PasswordDialog
        extends JDialog
        implements ActionListener {
    /**
     * Serial version ID.
     */
    private static final long serialVersionUID = 5867685302365849587L;

    /**
     * Minimum password length.
     */
    private static final int MIN_PASSWORD_LEN = 8;

    /**
     * password field width.
     */
    private static final int PASSWORD_FIELD_LEN = 30;

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 3;

    /**
     * adding width.
     */
    private static final int PADDING_SIZE = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PasswordDialog.class.getName());

    /**
     * Text for Password Label.
     */
    private static final String NLS_PASSWORD = NLS_BUNDLE.getString("Password");

    /**
     * Text for Confirm Label.
     */
    private static final String NLS_CONFIRM = NLS_BUNDLE.getString("Confirm");

    /**
     * Text for OK Button.
     */
    private static final String NLS_OK = NLS_BUNDLE.getString("OKButton");

    /**
     * Text for Cancel Button.
     */
    private static final String NLS_CANCEL = NLS_BUNDLE.getString("CancelButton");

    /**
     * Text for Error Panel.
     */
    private static final String NLS_ERROR = NLS_BUNDLE.getString("Error");

    /**
     * Text for Error Panel.
     */
    private static final String NLS_CONFIRMERROR = NLS_BUNDLE.getString("ConfirmError");

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR1 = NLS_BUNDLE.getString("LengthError1");

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR2 = NLS_BUNDLE.getString("LengthError2");

    /**
     * Obtained password.
     */
    private char[] thePassword = null;

    /**
     * Confirmation password.
     */
    private char[] theConfirm = null;

    /**
     * OK Button.
     */
    private final JButton theOKButton;

    /**
     * Cancel Button.
     */
    private final JButton theCancelButton;

    /**
     * Error field.
     */
    private final JLabel theErrorField;

    /**
     * Password field.
     */
    private final JPasswordField thePassField;

    /**
     * The error panel.
     */
    private JPanel theError = null;

    /**
     * Confirmation field.
     */
    private final JPasswordField theConfirmField;

    /**
     * Is the password set.
     */
    private boolean isPasswordSet = false;

    /**
     * Do we need to confirm the password.
     */
    private final boolean needConfirm;

    /**
     * Obtain the password.
     * @return the password
     */
    protected char[] getPassword() {
        return thePassword;
    }

    /**
     * Is the password set.
     * @return true/false
     */
    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    /**
     * Constructor.
     * @param pParent the parent frame for the dialog
     * @param pTitle the title
     * @param pNeedConfirm true/false
     */
    public PasswordDialog(final JFrame pParent,
                          final String pTitle,
                          final boolean pNeedConfirm) {
        /* Initialise the dialog (this calls dialogInit) */
        super(pParent, pTitle, true);

        /* Store the parameters */
        needConfirm = pNeedConfirm;

        /* Create the components */
        JLabel myPassLabel = new JLabel(NLS_PASSWORD, SwingConstants.TRAILING);
        JLabel myConfLabel = new JLabel(NLS_CONFIRM, SwingConstants.TRAILING);
        thePassField = new JPasswordField("", PASSWORD_FIELD_LEN);
        theConfirmField = new JPasswordField("", PASSWORD_FIELD_LEN);
        theOKButton = new JButton(NLS_OK);
        theCancelButton = new JButton(NLS_CANCEL);
        theErrorField = new JLabel();

        /* Add the listener for item changes */
        theOKButton.addActionListener(this);
        theCancelButton.addActionListener(this);
        thePassField.addActionListener(this);
        theConfirmField.addActionListener(this);

        /* Create the error panel */
        theError = new JPanel();
        theError.setBorder(BorderFactory.createTitledBorder(NLS_ERROR));
        theError.add(theErrorField);

        /* Set the Error panel to be red and invisible */
        theErrorField.setForeground(Color.red);
        theError.setVisible(false);

        /* Create the panel */
        JPanel myForm = new JPanel();

        /* Layout the password panel */
        SpringLayout mySpring = new SpringLayout();
        int myNumRows = 1;
        myForm.setLayout(mySpring);
        myForm.add(myPassLabel);
        myForm.add(thePassField);
        myForm.add(theOKButton);
        if (needConfirm) {
            myNumRows++;
            myForm.add(myConfLabel);
            myForm.add(theConfirmField);
            myForm.add(theCancelButton);
        }
        SpringUtilities.makeCompactGrid(myForm, mySpring, myNumRows, NUM_COLS, PADDING_SIZE);

        /* Layout the panel */
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        myPanel.add(myForm);
        myPanel.add(theError);
        if (needConfirm) {
            /* Set a focus traversal policy */
            myPanel.setFocusTraversalPolicy(new TraversalPolicy());
            myPanel.setFocusCycleRoot(true);
        }

        /* Set this to be the main panel */
        getContentPane().add(myPanel);
        pack();

        /* Set the relative location */
        setLocationRelativeTo(pParent);
    }

    /**
     * Release resources.
     */
    public void release() {
        if (thePassword != null) {
            Arrays.fill(thePassword, (char) 0);
        }
        if (theConfirm != null) {
            Arrays.fill(theConfirm, (char) 0);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the OK box or the password field */
        if ((theOKButton.equals(o))
            || (thePassField.equals(o))
            || (theConfirmField.equals(o))) {
            /* Access the password */
            thePassword = thePassField.getPassword();

            /* Access the confirm password */
            theConfirm = theConfirmField.getPassword();

            /* If we need to confirm the password */
            if (needConfirm) {
                /* If the password is less than the minimum length */
                if (thePassword.length < MIN_PASSWORD_LEN) {
                    /* Set error and return */
                    setError(NLS_LENGTHERR1
                             + " "
                             + MIN_PASSWORD_LEN
                             + " "
                             + NLS_LENGTHERR2);
                    return;
                }

                /* If the confirm password does not match */
                if (!Arrays.equals(thePassword, theConfirm)) {
                    /* Set error and return */
                    setError(NLS_CONFIRMERROR);
                    return;
                }
            }

            /* Note that we have set the password */
            isPasswordSet = true;

            /* Close the dialog */
            setVisible(false);

            /* else if this event relates to the Cancel button */
        } else if (theCancelButton.equals(o)) {
            /* Note that we have set the password */
            isPasswordSet = false;

            /* Close the dialog */
            setVisible(false);
        }
    }

    /**
     * set the error.
     * @param pError the error to display
     */
    public void setError(final String pError) {
        /* Set the string to the error field */
        theErrorField.setText(pError);

        /* Show that we need to update the password */
        isPasswordSet = false;

        /* Set the error panel to visible */
        theError.setVisible(true);
        pack();
    }

    /**
     * show the dialog.
     */
    public void showDialog() {
        /* Show the dialog */
        setVisible(true);
    }

    /**
     * Show the dialog under an invokeAndWait clause.
     * @param pDialog the dialog to show
     * @param pLogger the logger
     * @return successful dialog usage true/false
     */
    public static boolean showTheDialog(final PasswordDialog pDialog,
                                        final Logger pLogger) {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            pDialog.showDialog();

            /* else we must use invokeAndWait */
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        /* invoke the dialog */
                        pDialog.showDialog();
                    }
                });
            } catch (InvocationTargetException | InterruptedException e) {
                pLogger.log(Level.SEVERE, "Failed to display dialog", e);
            }
        }

        /* Return to caller */
        return pDialog.isPasswordSet();
    }

    /**
     * Focus traversal policy, used so that you tab straight to confirm from password.
     */
    private class TraversalPolicy
            extends FocusTraversalPolicy {
        @Override
        public Component getComponentAfter(final Container pRoot,
                                           final Component pCurrent) {
            /* Handle field order */
            if (thePassField.equals(pCurrent)) {
                return theConfirmField;
            }
            if (theConfirmField.equals(pCurrent)) {
                return theOKButton;
            }
            if (theOKButton.equals(pCurrent)) {
                return theCancelButton;
            }
            if (theCancelButton.equals(pCurrent)) {
                return thePassField;
            }

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getComponentBefore(final Container pRoot,
                                            final Component pCurrent) {
            /* Handle field order */
            if (thePassField.equals(pCurrent)) {
                return theCancelButton;
            }
            if (theConfirmField.equals(pCurrent)) {
                return thePassField;
            }
            if (theOKButton.equals(pCurrent)) {
                return theConfirmField;
            }
            if (theCancelButton.equals(pCurrent)) {
                return theOKButton;
            }

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getDefaultComponent(final Container pRoot) {
            /* Return the first component */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getFirstComponent(final Container pRoot) {
            /* Return the password field */
            return thePassField;
        }

        @Override
        public Component getLastComponent(final Container pRoot) {
            /* Return the password field */
            return theCancelButton;
        }
    }
}
