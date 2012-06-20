/*******************************************************************************
 * JGordianKnot: Security Suite
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
package net.sourceforge.JGordianKnot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 * @author Tony Washer
 */
public class PasswordDialog extends JDialog implements ActionListener {
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
    public char[] getPassword() {
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

        /* Store the confirm option */
        needConfirm = pNeedConfirm;

        /* Local variables */
        JLabel myPassLabel;
        JLabel myConfLabel;
        JPanel myPanel;

        /* Create the components */
        myPassLabel = new JLabel("Password:");
        myConfLabel = new JLabel("Confirm:");
        thePassField = new JPasswordField("", PASSWORD_FIELD_LEN);
        theConfirmField = new JPasswordField("", PASSWORD_FIELD_LEN);
        theOKButton = new JButton("OK");
        theCancelButton = new JButton("Cancel");
        theErrorField = new JLabel();

        /* Add the listener for item changes */
        theOKButton.addActionListener(this);
        theCancelButton.addActionListener(this);
        thePassField.addActionListener(this);
        theConfirmField.addActionListener(this);

        /* Create the error panel */
        theError = new JPanel();
        theError.setBorder(javax.swing.BorderFactory.createTitledBorder("Error"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(theError);
        theError.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup().addContainerGap().addComponent(theErrorField)
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theErrorField));

        /* Set the Error panel to be red and invisible */
        theErrorField.setForeground(Color.red);
        theError.setVisible(false);

        /* Create the panel */
        myPanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(myPanel);
        myPanel.setLayout(myLayout);

        /* If we need confirmation */
        if (needConfirm) {
            /* Set the layout */
            myLayout.setHorizontalGroup(myLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(theError, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING,
                              myLayout.createSequentialGroup()
                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(myPassLabel).addComponent(myConfLabel))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(thePassField)
                                                        .addComponent(theConfirmField))
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(theOKButton,
                                                                      GroupLayout.Alignment.LEADING,
                                                                      GroupLayout.DEFAULT_SIZE,
                                                                      GroupLayout.DEFAULT_SIZE,
                                                                      Short.MAX_VALUE)
                                                        .addComponent(theCancelButton,
                                                                      GroupLayout.Alignment.LEADING,
                                                                      GroupLayout.DEFAULT_SIZE,
                                                                      GroupLayout.DEFAULT_SIZE,
                                                                      Short.MAX_VALUE))));
            myLayout.setVerticalGroup(myLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING,
                              myLayout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                             false).addComponent(myPassLabel)
                                                        .addComponent(thePassField).addComponent(theOKButton))
                                      .addContainerGap()
                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                             false).addComponent(myConfLabel)
                                                        .addComponent(theConfirmField)
                                                        .addComponent(theCancelButton)).addContainerGap()
                                      .addComponent(theError).addContainerGap()));

            /* Set a focus traversal policy */
            myPanel.setFocusTraversalPolicy(new TraversalPolicy());
            myPanel.setFocusCycleRoot(true);

            /* Else we need no confirmation */
        } else {
            /* Set the layout */
            myLayout.setHorizontalGroup(myLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(theError)
                    .addGroup(GroupLayout.Alignment.TRAILING,
                              myLayout.createSequentialGroup()
                                      .addContainerGap()
                                      .addComponent(myPassLabel)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                       GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                      .addComponent(thePassField)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                       GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                      .addComponent(theOKButton).addContainerGap()));
            myLayout.setVerticalGroup(myLayout
                    .createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING,
                              myLayout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                             false).addComponent(myPassLabel)
                                                        .addComponent(thePassField).addComponent(theOKButton))
                                      .addContainerGap().addComponent(theError).addContainerGap()));
        }

        /* Set this to be the main panel */
        getContentPane().add(myPanel);
        pack();

        /* Set the relative location */
        setLocationRelativeTo(pParent);
    }

    @Override
    protected void finalize() throws Throwable {
        if (thePassword != null) {
            Arrays.fill(thePassword, (char) 0);
        }
        super.finalize();
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the OK box or the password field */
        if ((theOKButton.equals(o)) || (thePassField.equals(o)) || (theConfirmField.equals(o))) {
            /* Access the password */
            thePassword = thePassField.getPassword();

            /* Access the confirm password */
            theConfirm = theConfirmField.getPassword();

            /* If we need to confirm the password */
            if (needConfirm) {
                /* If the password is less than the minimum length */
                if (thePassword.length < MIN_PASSWORD_LEN) {
                    /* Set error and return */
                    setError("Password must be at least " + MIN_PASSWORD_LEN + " characters long");
                    return;
                }

                /* If the confirm password does not match */
                if (!Arrays.equals(thePassword, theConfirm)) {
                    /* Set error and return */
                    setError("Confirmation password does not match password");
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
     * @return successful dialog usage true/false
     */
    public static boolean showTheDialog(final PasswordDialog pDialog) {
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
            } catch (Exception e) {
                return false;
            }
        }

        /* Return to caller */
        return pDialog.isPasswordSet();
    }

    /**
     * Focus traversal policy, used so that you tab straight to confirm from password.
     */
    private class TraversalPolicy extends FocusTraversalPolicy {
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
