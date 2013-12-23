/*******************************************************************************
 * jPreferenceSet: PreferenceSet Management
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
package net.sourceforge.joceanus.jpreferenceset;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jeventmanager.ActionDetailEvent;
import net.sourceforge.joceanus.jeventmanager.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;
import net.sourceforge.joceanus.jfieldset.JFieldManager;
import net.sourceforge.joceanus.jlayoutmanager.ArrowIcon;
import net.sourceforge.joceanus.jlayoutmanager.JScrollPopupMenu;

/**
 * Preference maintenance panel.
 * @author Tony Washer
 */
public class PreferencesPanel
        extends JEventPanel {
    /**
     * The serial Id.
     */
    private static final long serialVersionUID = -1512860688570367124L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PreferencesPanel.class.getName());

    /**
     * Text for OK.
     */
    private static final String NLS_OK = NLS_BUNDLE.getString("OKButton");

    /**
     * Text for Reset.
     */
    private static final String NLS_RESET = NLS_BUNDLE.getString("ResetButton");

    /**
     * Text for Options.
     */
    private static final String NLS_OPTIONS = NLS_BUNDLE.getString("SaveOptions");

    /**
     * Text for Selection.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("Selection");

    /**
     * Text for Set.
     */
    private static final String NLS_SET = NLS_BUNDLE.getString("PreferenceSet");

    /**
     * Store data error text.
     */
    private static final String ERROR_STORE = NLS_BUNDLE.getString("ErrorStore");

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The logger.
     */
    private final transient Logger theLogger;

    /**
     * The OK button.
     */
    private final JButton theOKButton;

    /**
     * The reset button.
     */
    private final JButton theResetButton;

    /**
     * The selection button.
     */
    private final JButton theSelectButton;

    /**
     * The properties panel.
     */
    private final JEnablePanel theProperties;

    /**
     * The layout.
     */
    private final CardLayout theLayout;

    /**
     * The active set.
     */
    private PreferenceSetPanel theActive = null;

    /**
     * The list of panels.
     */
    private final List<PreferenceSetPanel> thePanels;

    /**
     * The listener.
     */
    private final transient PropertyListener theListener;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pFieldMgr the field manager
     * @param pDataMgr the data manager
     * @param pSection the data section
     */
    public PreferencesPanel(final PreferenceManager pPreferenceMgr,
                            final JFieldManager pFieldMgr,
                            final JDataManager pDataMgr,
                            final JDataEntry pSection) {
        /* Access field manager and logger */
        theFieldMgr = pFieldMgr;
        theLogger = pPreferenceMgr.getLogger();

        /* Create the buttons */
        theOKButton = new JButton(NLS_OK);
        theResetButton = new JButton(NLS_RESET);

        /* Create the listener */
        theListener = new PropertyListener();

        /* Create the buttons panel */
        JPanel myButtons = new JPanel();
        myButtons.setBorder(BorderFactory.createTitledBorder(NLS_OPTIONS));

        /* Create the layout for the buttons panel */
        myButtons.setLayout(new BoxLayout(myButtons, BoxLayout.X_AXIS));
        myButtons.add(Box.createHorizontalGlue());
        myButtons.add(theOKButton);
        myButtons.add(Box.createHorizontalGlue());
        myButtons.add(theResetButton);
        myButtons.add(Box.createHorizontalGlue());

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_SET);
        theSelectButton = new JButton(ArrowIcon.DOWN);
        theSelectButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSelectButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the selection panel */
        JPanel mySelection = new JPanel();
        mySelection.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelection.setLayout(new BoxLayout(mySelection, BoxLayout.X_AXIS));
        mySelection.add(myLabel);
        mySelection.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelection.add(theSelectButton);
        mySelection.add(Box.createHorizontalGlue());

        /* Create the properties panel */
        theProperties = new JEnablePanel();
        theLayout = new CardLayout();
        theProperties.setLayout(theLayout);

        /* Create the panel list */
        thePanels = new ArrayList<PreferenceSetPanel>();

        /* Loop through the existing property sets */
        for (PreferenceSet mySet : pPreferenceMgr.getPreferenceSets()) {
            /* Register the Set */
            registerSet(mySet);
        }

        /* Add a listener for the addition of subsequent propertySets */
        PropertySetListener mySetListener = new PropertySetListener();
        pPreferenceMgr.addActionListener(mySetListener);

        /* Create a new Scroll Pane and add this table to it */
        JScrollPane myScroll = new JScrollPane();
        myScroll.setViewportView(theProperties);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mySelection);
        add(myScroll);
        add(Box.createVerticalGlue());
        add(myButtons);

        /* Determine the active items */
        theActive = thePanels.get(0);
        setSelectText();
        setVisibility();

        /* Add Listeners */
        theOKButton.addActionListener(theListener);
        theResetButton.addActionListener(theListener);
        theSelectButton.addActionListener(theListener);

        /* Create the debug entry, and attach to correct section */
        theDataEntry = pDataMgr.new JDataEntry("Preferences");
        theDataEntry.addAsChildOf(pSection);
        theDataEntry.setObject(pDataMgr);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataEntry.setFocus();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theSelectButton.setEnabled(bEnabled);
        theProperties.setEnabled(bEnabled);
    }

    /**
     * RegisterSet.
     * @param pSet the set to register
     */
    private void registerSet(final PreferenceSet pSet) {
        /* Create the underlying panel */
        PreferenceSetPanel myPanel = new PreferenceSetPanel(theLogger, theFieldMgr, pSet);

        /* Add the panel */
        theProperties.add(myPanel, myPanel.toString());
        thePanels.add(myPanel);

        /* Add listener */
        myPanel.addChangeListener(theListener);
    }

    /**
     * Does the panel have unsaved updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return (theActive != null)
               && (theActive.hasChanges());
    }

    /**
     * Save Updates.
     */
    public void saveUpdates() {
        try {
            theActive.storeChanges();
        } catch (JDataException e) {
            theLogger.log(Level.SEVERE, ERROR_STORE, e);
        }

        /* Set correct visibility */
        setVisibility();

        /* Notify that state has changed */
        fireStateChanged();
    }

    /**
     * Reset Updates.
     */
    public void resetUpdates() {
        /* Reset all changes */
        theActive.resetChanges();

        /* Set correct visibility */
        setVisibility();

        /* Notify that state has changed */
        fireStateChanged();
    }

    /**
     * Set the visibility.
     */
    protected final void setVisibility() {
        /* Enable selection */
        theSelectButton.setEnabled((theActive != null)
                                   && !theActive.hasChanges());

        /* Enable the buttons */
        theOKButton.setEnabled((theActive != null)
                               && theActive.hasChanges());
        theResetButton.setEnabled((theActive != null)
                                  && theActive.hasChanges());
    }

    /**
     * Set the select button text.
     */
    private void setSelectText() {
        /* Show selection text */
        theSelectButton.setText((theActive == null)
                ? null
                : theActive.toString());
    }

    /**
     * PropertyListener class.
     */
    private final class PropertyListener
            implements ActionListener, ChangeListener {
        /**
         * Show Preference menu.
         */
        private void showPreferenceMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Loop through the panels */
            Iterator<PreferenceSetPanel> myIterator = thePanels.iterator();
            while (myIterator.hasNext()) {
                PreferenceSetPanel myPanel = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                PreferenceAction myAction = new PreferenceAction(myPanel);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);
            }

            /* Show the Preference menu in the correct place */
            Rectangle myLoc = theSelectButton.getBounds();
            myPopUp.show(theSelectButton, 0, myLoc.height);
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the OK button */
            if (theOKButton.equals(o)) {
                /* Perform the command */
                saveUpdates();

                /* If this event relates to the reset button */
            } else if (theResetButton.equals(o)) {
                /* Perform the command */
                resetUpdates();

                /* If this event relates to the select button */
            } else if (theSelectButton.equals(o)) {
                /* Show the menu */
                showPreferenceMenu();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* Set visibility */
            setVisibility();

            /* Notify listeners */
            fireStateChanged();
        }
    }

    /**
     * Preference action class.
     */
    private final class PreferenceAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 4736215082949452032L;

        /**
         * Preference Panel Type.
         */
        private final PreferenceSetPanel thePanel;

        /**
         * Constructor.
         * @param pPanel the panel
         */
        private PreferenceAction(final PreferenceSetPanel pPanel) {
            super(pPanel.toString());
            thePanel = pPanel;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If the panel has changed */
            if (!Difference.isEqual(theActive, thePanel)) {
                /* Set the Active component */
                theActive = thePanel;
                setSelectText();

                /* Move correct card to front */
                theLayout.show(theProperties, theActive.toString());
            }
        }
    }

    /**
     * PropertySetListener class.
     */
    private final class PropertySetListener
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If this is an ActionDetailEvent */
            if (e instanceof ActionDetailEvent) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) e;
                Object o = evt.getDetails();

                /* If the details is a preference set */
                if (o instanceof PreferenceSet) {
                    /* Details is the property set that has been added */
                    PreferenceSet mySet = (PreferenceSet) o;

                    /* Register the set */
                    registerSet(mySet);

                    /* Note that the panel should be re-displayed */
                    setVisibility();
                    theProperties.invalidate();
                }
            }
        }
    }
}
