/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager.JDataEntry;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.swing.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.swing.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Text for OK.
     */
    private static final String NLS_OK = PreferenceResource.UI_BUTTON_OK.getValue();

    /**
     * Text for Reset.
     */
    private static final String NLS_RESET = PreferenceResource.UI_BUTTON_RESET.getValue();

    /**
     * Text for Options.
     */
    private static final String NLS_OPTIONS = PreferenceResource.UI_TITLE_SAVE.getValue();

    /**
     * Text for Selection.
     */
    private static final String NLS_SELECT = PreferenceResource.UI_TITLE_SELECT.getValue();

    /**
     * Text for Set.
     */
    private static final String NLS_SET = PreferenceResource.UI_LABEL_SET.getValue();

    /**
     * Store data error text.
     */
    private static final String ERROR_STORE = PreferenceResource.UI_ERROR_STORE.getValue();

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferencesPanel.class);

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Data entry.
     */
    private final transient JDataEntry theDataEntry;

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
    private final JScrollButton<PreferenceSetPanel> theSelectButton;

    /**
     * The properties panel.
     */
    private final JEnablePanel theProperties;

    /**
     * The buttons panel.
     */
    private final JPanel theButtons;

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
    private final transient List<PreferenceSetPanel> thePanels;

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
                            final ViewerManager pDataMgr,
                            final JDataEntry pSection) {
        /* Access field manager and logger */
        theFieldMgr = pFieldMgr;

        /* Create the buttons */
        theOKButton = new JButton(NLS_OK);
        theResetButton = new JButton(NLS_RESET);

        /* Create the buttons panel */
        theButtons = new JPanel();
        theButtons.setBorder(BorderFactory.createTitledBorder(NLS_OPTIONS));

        /* Create the layout for the buttons panel */
        theButtons.setLayout(new BoxLayout(theButtons, BoxLayout.X_AXIS));
        theButtons.add(Box.createHorizontalGlue());
        theButtons.add(theOKButton);
        theButtons.add(Box.createHorizontalGlue());
        theButtons.add(theResetButton);
        theButtons.add(Box.createHorizontalGlue());

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_SET);
        theSelectButton = new JScrollButton<PreferenceSetPanel>();

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

        /* Add Listeners */
        theListener = new PropertyListener();
        theOKButton.addActionListener(theListener);
        theResetButton.addActionListener(theListener);
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, theListener);

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
        add(theButtons);

        /* Determine the active items */
        theActive = thePanels.get(0);
        setSelectText();
        setVisibility();

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
        PreferenceSetPanel myPanel = new PreferenceSetPanel(theFieldMgr, pSet);

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
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        return hasUpdates();
    }

    /**
     * Save Updates.
     */
    public void saveUpdates() {
        try {
            theActive.storeChanges();
        } catch (JOceanusException e) {
            LOGGER.error(ERROR_STORE, e);
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

        /* Show/Hide the buttons */
        theButtons.setVisible((theActive != null)
                              && theActive.hasChanges());
    }

    /**
     * Set the select button text.
     */
    private void setSelectText() {
        /* Show selection text */
        theSelectButton.setValue(theActive);
    }

    /**
     * PropertyListener class.
     */
    private final class PropertyListener
            implements ActionListener, PropertyChangeListener, ChangeListener {
        /**
         * Preference menu builder.
         */
        private final JScrollMenuBuilder<PreferenceSetPanel> thePrefMenuBuilder;

        /**
         * Constructor.
         */
        private PropertyListener() {
            /* Access builders */
            thePrefMenuBuilder = theSelectButton.getMenuBuilder();
            thePrefMenuBuilder.addChangeListener(this);
        }

        /**
         * Show Preference menu.
         */
        private void buildPreferenceMenu() {
            /* Reset the popUp menu */
            thePrefMenuBuilder.clearMenu();

            /* Record active item */
            JMenuItem myActive = null;

            /* Loop through the panels */
            Iterator<PreferenceSetPanel> myIterator = thePanels.iterator();
            while (myIterator.hasNext()) {
                PreferenceSetPanel myPanel = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = thePrefMenuBuilder.addItem(myPanel);

                /* If this is the active panel */
                if (myPanel.equals(theActive)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            thePrefMenuBuilder.showItem(myActive);
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
            }
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            if (thePrefMenuBuilder.equals(o)) {
                /* Build the preference menu */
                buildPreferenceMenu();
            } else {
                /* Set visibility */
                setVisibility();

                /* Notify listeners */
                fireStateChanged();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the Select button */
            if (theSelectButton.equals(o)) {
                /* If the panel has changed */
                PreferenceSetPanel myPanel = theSelectButton.getValue();
                if (!Difference.isEqual(theActive, myPanel)) {
                    /* Set the Active component */
                    theActive = myPanel;

                    /* Move correct card to front */
                    theLayout.show(theProperties, theActive.toString());
                }
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
