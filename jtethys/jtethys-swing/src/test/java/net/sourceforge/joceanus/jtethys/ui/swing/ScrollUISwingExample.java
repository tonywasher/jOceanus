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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/swing/DemoFilter.java $
 * $Revision: 579 $
 * $Author: Tony $
 * $Date: 2015-03-24 15:06:09 +0000 (Tue, 24 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scroll utilities examples.
 */
public class ScrollUISwingExample
        extends JApplet {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1335897095869650737L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrollUISwingExample.class);

    /**
     * The popUp menu.
     */
    private final JScrollPopupMenu thePopupMenu;

    /**
     * The text field.
     */
    private final JLabel theDisplayValue;

    /**
     * The active value.
     */
    private String theActiveValue;

    /**
     * Constructor.
     */
    public ScrollUISwingExample() {
        thePopupMenu = new JScrollPopupMenu();
        theDisplayValue = new JLabel();
    }

    @Override
    public void init() {
        // Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    /* Access the panel */
                    JPanel myPanel = buildPanel();
                    setContentPane(myPanel);
                }
            });
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke thread", e);
        } catch (InterruptedException e) {
            LOGGER.error("Thread was interrupted", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("ScrollUI Example");

            /* Create the UI */
            ScrollUISwingExample myExample = new ScrollUISwingExample();

            /* Build the panel */
            JPanel myPanel = myExample.buildPanel();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Build the panel.
     */
    private JPanel buildPanel() {
        /* Build the popUp menu */
        buildPopupMenu();

        /* Create the display value */
        theDisplayValue.setText("null");

        /* Create a panel */
        JPanel myPanel = new JPanel();
        JLabel myLabel = new JLabel("Selected Value:");
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.add(myLabel);
        myPanel.add(theDisplayValue);

        /* Add popUp listener */
        myPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    thePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            public void mouseReleased(final MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    thePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        /* Return the panel */
        return myPanel;
    }

    /**
     * Build the popUp menu.
     */
    private void buildPopupMenu() {
        /* Set the display count */
        thePopupMenu.setMaxDisplayItems(4);

        /* Create the menu item */
        addMenuItem("First");
        addMenuItem("Second");
        addMenuItem("Third");
        addMenuItem("Fourth");
        addMenuItem("Fifth");
        addMenuItem("Sixth");
        addMenuItem("Seventh");
        addMenuItem("Eighth");
    }

    /**
     * Add Menu Item for string.
     * @param pValue the value to add
     */
    private void addMenuItem(final String pValue) {
        /* Create the menu item */
        ValueAction myAction = new ValueAction(pValue);
        JMenuItem myItem = new JMenuItem(myAction);

        /* Add to popup menu */
        thePopupMenu.addMenuItem(myItem);
    }

    /**
     * Set the active value.
     * @param pValue the value to set
     */
    private void setActiveValue(final String pValue) {
        /* Record the value */
        theActiveValue = pValue;
        theDisplayValue.setText(pValue);
    }

    /**
     * Value action class.
     */
    private final class ValueAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2071811503847714024L;

        /**
         * Value.
         */
        private final String theValue;

        /**
         * Constructor.
         * @param pButton the button
         * @param pItem the item
         * @param pName the name
         */
        private ValueAction(final String pValue) {
            super(pValue);
            theValue = pValue;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Set the item */
            setActiveValue(theValue);
        }
    }
}
