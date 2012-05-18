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

public class PasswordDialog extends JDialog implements ActionListener {
    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 5867685302365849587L;

    /**
     * Minimum password length
     */
    private static final int minPasswordLen = 8;

    /**
     * Obtained password
     */
    private char[] thePassword = null;

    /**
     * Confirmation password
     */
    private char[] theConfirm = null;

    /**
     * OK Button
     */
    private JButton theOKButton = null;

    /**
     * Cancel Button
     */
    private JButton theCancelButton = null;

    /**
     * Error field
     */
    private JLabel theErrorField = null;

    /**
     * Password field
     */
    private JPasswordField thePassField = null;

    /**
     * The error panel
     */
    private JPanel theError = null;

    /**
     * Confirmation field
     */
    private JPasswordField theConfirmField = null;

    /**
     * Is the password set
     */
    private boolean isPasswordSet = false;

    /**
     * Do we need to confirm the password
     */
    private boolean needConfirm = false;

    /**
     * Obtain the password
     * @return the password
     */
    public char[] getPassword() {
        return thePassword;
    }

    /**
     * Is the password set
     * @return true/false
     */
    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    /**
     * Constructor
     * @param pParent the parent frame for the dialog
     * @param pTitle the title
     * @param needConfirm true/false
     */
    public PasswordDialog(JFrame pParent,
                          String pTitle,
                          boolean needConfirm) {
        /* Initialise the dialog (this calls dialogInit) */
        super(pParent, pTitle, true);

        /* Store the confirm option */
        this.needConfirm = needConfirm;

        /* Local variables */
        JLabel myPassLabel;
        JLabel myConfLabel;
        JPanel myPanel;

        /* Create the components */
        myPassLabel = new JLabel("Password:");
        myConfLabel = new JLabel("Confirm:");
        thePassField = new JPasswordField("", 30);
        theConfirmField = new JPasswordField("", 30);
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
        }

        /* Else we need no confirmation */
        else {
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
    protected void finalize() throws Exception {
        if (thePassword != null)
            Arrays.fill(thePassword, (char) 0);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        /* If this event relates to the OK box or the password field */
        if ((evt.getSource() == (Object) theOKButton) || (evt.getSource() == (Object) thePassField)
                || (evt.getSource() == (Object) theConfirmField)) {
            /* Access the password */
            thePassword = thePassField.getPassword();

            /* Access the confirm password */
            theConfirm = theConfirmField.getPassword();

            /* If we need to confirm the password */
            if (needConfirm) {
                /* If the password is less than the minimum length */
                if (thePassword.length < minPasswordLen) {
                    /* Set error and return */
                    setError("Password must be at least " + minPasswordLen + " characters long");
                    return;
                }

                /* If the confirm password does not match */
                if (!java.util.Arrays.equals(thePassword, theConfirm)) {
                    /* Set error and return */
                    setError("Confirmation password does not match password");
                    return;
                }
            }

            /* Note that we have set the password */
            isPasswordSet = true;

            /* Close the dialog */
            setVisible(false);
        }

        /* else if this event relates to the Cancel button */
        else if (evt.getSource() == (Object) theCancelButton) {
            /* Note that we have set the password */
            isPasswordSet = false;

            /* Close the dialog */
            setVisible(false);
        }
    }

    /**
     * set the error
     * @param pError the error to display
     */
    public void setError(String pError) {
        /* Set the string to the error field */
        theErrorField.setText(pError);

        /* Show that we need to update the password */
        isPasswordSet = false;

        /* Set the error panel to visible */
        theError.setVisible(true);
        pack();
    }

    /**
     * show the dialog
     */
    public void showDialog() {
        /* Show the dialog */
        setVisible(true);
    }

    /* Focus traversal policy, used so that you tab straight to confirm from password */
    private class TraversalPolicy extends FocusTraversalPolicy {
        @Override
        public Component getComponentAfter(Container pRoot,
                                           Component pCurrent) {
            /* Handle field order */
            if (pCurrent == thePassField)
                return theConfirmField;
            if (pCurrent == theConfirmField)
                return theOKButton;
            if (pCurrent == theOKButton)
                return theCancelButton;
            if (pCurrent == theCancelButton)
                return thePassField;

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getComponentBefore(Container pRoot,
                                            Component pCurrent) {
            /* Handle field order */
            if (pCurrent == thePassField)
                return theCancelButton;
            if (pCurrent == theConfirmField)
                return thePassField;
            if (pCurrent == theOKButton)
                return theConfirmField;
            if (pCurrent == theCancelButton)
                return theOKButton;

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getDefaultComponent(Container pRoot) {
            /* Return the first component */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getFirstComponent(Container pRoot) {
            /* Return the password field */
            return thePassField;
        }

        @Override
        public Component getLastComponent(Container pRoot) {
            /* Return the password field */
            return theCancelButton;
        }
    }
}
