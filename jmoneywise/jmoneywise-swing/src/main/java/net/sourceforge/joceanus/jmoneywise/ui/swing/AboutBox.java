/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Display the about box.
 */
public class AboutBox
        extends JDialog
        implements ActionListener {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5518361782722672513L;

    /**
     * Program version.
     */
    private static final String PROGRAM_VERSION = ProgramResource.PROGRAM_VERSION.getValue();

    /**
     * Program revision.
     */
    private static final String PROGRAM_REVISION = ProgramResource.PROGRAM_REVISION.getValue();

    /**
     * Program buildDate.
     */
    private static final String PROGRAM_BUILDDATE = ProgramResource.PROGRAM_BUILTON.getValue();

    /**
     * Program copyright.
     */
    private static final String PROGRAM_COPYRIGHT = ProgramResource.PROGRAM_COPYRIGHT.getValue();

    /**
     * Border thickness.
     */
    private static final int BORDER_WIDTH = 3;

    /**
     * The OK button.
     */
    private final JButton theOKButton;

    /**
     * Constructor.
     * @param pParent the parent frame for the dialog
     * @param pTitle the title
     */
    public AboutBox(final JFrame pParent,
                    final String pTitle) {
        /* Initialise the dialog (this calls dialogInit) */
        super(pParent, pTitle, true);

        /* Set as undecorated */
        setUndecorated(true);

        /* Create the components */
        JLabel myProduct = new JLabel(pTitle);
        JLabel myVersion = new JLabel("version: "
                                      + PROGRAM_VERSION);
        JLabel myRevision = new JLabel("revision: "
                                       + PROGRAM_REVISION);
        JLabel myBuild = new JLabel("builtOn: "
                                    + PROGRAM_BUILDDATE);
        JLabel myCopyright = new JLabel(PROGRAM_COPYRIGHT);
        myProduct.setAlignmentX(Component.CENTER_ALIGNMENT);
        myVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        myRevision.setAlignmentX(Component.CENTER_ALIGNMENT);
        myBuild.setAlignmentX(Component.CENTER_ALIGNMENT);
        myCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Create the OK button */
        theOKButton = new JButton("OK");
        theOKButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Add the listener for item changes */
        theOKButton.addActionListener(this);

        /* Layout the panel */
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        myPanel.setBorder(BorderFactory.createLineBorder(Color.black, BORDER_WIDTH));
        myPanel.add(myProduct);
        myPanel.add(myCopyright);
        myPanel.add(new JLabel());
        myPanel.add(myVersion);
        myPanel.add(myRevision);
        myPanel.add(myBuild);
        myPanel.add(new JLabel());
        myPanel.add(theOKButton);

        /* Set this to be the main panel */
        getContentPane().add(myPanel);
        pack();

        /* Set the relative location */
        setLocationRelativeTo(pParent);
    }

    /**
     * show the dialog.
     */
    public void showDialog() {
        /* Show the dialog */
        setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the OK button */
        if (theOKButton.equals(o)) {
            /* Close the dialog */
            setVisible(false);
        }
    }
}
