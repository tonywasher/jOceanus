/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Load error.
     */
    private static final String ERROR_LOAD = "Failed to load application properties";

    /**
     * Border thickness.
     */
    private static final int BORDER_WIDTH = 3;

    /**
     * The properties.
     */
    private static Properties theProperties;

    /**
     * The OK button.
     */
    private final JButton theOKButton;

    /**
     * Constructor.
     * @param pParent the parent frame for the dialog
     * @param pTitle the title
     * @param pLogger the logger
     */
    public AboutBox(final JFrame pParent,
                    final String pTitle,
                    final Logger pLogger) {
        /* Initialise the dialog (this calls dialogInit) */
        super(pParent, pTitle, true);

        /* Load properties */
        Properties myProperties = loadProperties(pLogger);

        /* Set as undecorated */
        setUndecorated(true);

        /* Create the components */
        JLabel myProduct = new JLabel(pTitle);
        JLabel myVersion = new JLabel("version: "
                                      + myProperties.getProperty("version"));
        JLabel myRevision = new JLabel("revision: "
                                       + myProperties.getProperty("revision"));
        JLabel myBuild = new JLabel("builtOn: "
                                    + myProperties.getProperty("timeStamp"));
        JLabel myCopyright = new JLabel("Copyright 2012,2014 Tony Washer");
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

    /**
     * Load the project properties.
     * @param pLogger the logger
     * @return the project properties.
     */
    private Properties loadProperties(final Logger pLogger) {
        /* Return previously cached value if available */
        if (theProperties != null) {
            return theProperties;
        }

        /* Protect calls */
        InputStream in = null;
        try {
            /* Create the properties */
            Properties myProperties = new Properties();

            /* Load the stream */
            in = AboutBox.class.getResourceAsStream("/META-INF/jMoneyWise.properties");
            myProperties.load(in);

            /* Store properties */
            theProperties = myProperties;

            /* Return the properties */
            return myProperties;
        } catch (IOException e) {
            pLogger.log(Level.SEVERE, ERROR_LOAD, e);
            return new Properties();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                pLogger.log(Level.SEVERE, ERROR_LOAD, e);
            }
        }
    }
}
