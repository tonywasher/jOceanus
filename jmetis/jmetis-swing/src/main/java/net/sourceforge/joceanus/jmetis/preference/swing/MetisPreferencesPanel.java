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
package net.sourceforge.joceanus.jmetis.preference.swing;

import java.awt.BorderLayout;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysActionRegistration;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

/**
 * Preference maintenance panel.
 * @author Tony Washer
 */
public class MetisPreferencesPanel
        extends JPanel
        implements TethysEventProvider {
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
    private static final String NLS_OK = MetisPreferenceResource.UI_BUTTON_OK.getValue();

    /**
     * Text for Reset.
     */
    private static final String NLS_RESET = MetisPreferenceResource.UI_BUTTON_RESET.getValue();

    /**
     * Text for Options.
     */
    private static final String NLS_OPTIONS = MetisPreferenceResource.UI_TITLE_SAVE.getValue();

    /**
     * Text for Selection.
     */
    private static final String NLS_SELECT = MetisPreferenceResource.UI_TITLE_SELECT.getValue();

    /**
     * Text for Set.
     */
    private static final String NLS_SET = MetisPreferenceResource.UI_LABEL_SET.getValue();

    /**
     * Store data error text.
     */
    private static final String ERROR_STORE = MetisPreferenceResource.UI_ERROR_STORE.getValue();

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisPreferencesPanel.class);

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager theEventManager;

    /**
     * The field manager.
     */
    private final transient MetisFieldManager theFieldMgr;

    /**
     * The Data entry.
     */
    private final transient MetisViewerEntry theDataEntry;

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
    private final JScrollButton<MetisPreferenceSetPanel> theSelectButton;

    /**
     * The properties panel.
     */
    private final TethysSwingEnablePanel theProperties;

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
    private MetisPreferenceSetPanel theActive = null;

    /**
     * The list of panels.
     */
    private final transient List<MetisPreferenceSetPanel> thePanels;

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
    public MetisPreferencesPanel(final MetisPreferenceManager pPreferenceMgr,
                                 final MetisFieldManager pFieldMgr,
                                 final MetisViewerManager pDataMgr,
                                 final MetisViewerEntry pSection) {
        /* Access field manager and logger */
        theFieldMgr = pFieldMgr;

        /* Create the event manager */
        theEventManager = new TethysEventManager();

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
        theSelectButton = new JScrollButton<MetisPreferenceSetPanel>();

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
        theProperties = new TethysSwingEnablePanel();
        theLayout = new CardLayout();
        theProperties.setLayout(theLayout);

        /* Add Listeners */
        theListener = new PropertyListener();
        theOKButton.addActionListener(theListener);
        theResetButton.addActionListener(theListener);
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, theListener);

        /* Create the panel list */
        thePanels = new ArrayList<MetisPreferenceSetPanel>();

        /* Loop through the existing property sets */
        for (MetisPreferenceSet mySet : pPreferenceMgr.getPreferenceSets()) {
            /* Register the Set */
            registerSet(mySet);
        }

        /* Create a new Scroll Pane and add this table to it */
        JScrollPane myScroll = new JScrollPane();
        myScroll.setViewportView(theProperties);

        /* Now define the panel */
        setLayout(new BorderLayout());
        add(mySelection, BorderLayout.PAGE_START);
        add(myScroll, BorderLayout.CENTER);
        add(theButtons, BorderLayout.PAGE_END);

        /* Determine the active items */
        theActive = thePanels.get(0);
        setSelectText();
        setVisibility();

        /* Create the debug entry, and attach to correct section */
        theDataEntry = pDataMgr.newEntry("Preferences");
        theDataEntry.addAsChildOf(pSection);
        theDataEntry.setObject(pPreferenceMgr);

        /* Add a listener for the addition of subsequent propertySets */
        new PrefSetListener(pPreferenceMgr);
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
    private void registerSet(final MetisPreferenceSet pSet) {
        /* Create the underlying panel */
        MetisPreferenceSetPanel myPanel = new MetisPreferenceSetPanel(theFieldMgr, pSet);

        /* Add the panel */
        theProperties.add(myPanel, myPanel.toString());
        thePanels.add(myPanel);

        /* Add listener */
        myPanel.getEventRegistrar().addChangeListener(theListener);
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
        } catch (OceanusException e) {
            LOGGER.error(ERROR_STORE, e);
        }

        /* Set correct visibility */
        setVisibility();

        /* Notify that state has changed */
        theEventManager.fireStateChanged();
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
        theEventManager.fireStateChanged();
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
            implements ActionListener, PropertyChangeListener, TethysChangeEventListener {
        /**
         * Preference menu builder.
         */
        private final JScrollMenuBuilder<MetisPreferenceSetPanel> thePrefMenuBuilder;

        /**
         * PrefMenu Registration.
         */
        private final TethysChangeRegistration thePrefMenuReg;

        /**
         * Constructor.
         */
        private PropertyListener() {
            /* Access builders */
            thePrefMenuBuilder = theSelectButton.getMenuBuilder();
            thePrefMenuReg = thePrefMenuBuilder.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChange(final TethysChangeEvent pEvent) {
            /* Handle menu type */
            if (thePrefMenuReg.isRelevant(pEvent)) {
                /* Build the preference menu */
                buildPreferenceMenu();

                /* else its one of the subPanels */
            } else {
                /* Set visibility */
                setVisibility();

                /* Notify listeners */
                theEventManager.fireStateChanged();
            }
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
            Iterator<MetisPreferenceSetPanel> myIterator = thePanels.iterator();
            while (myIterator.hasNext()) {
                MetisPreferenceSetPanel myPanel = myIterator.next();

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
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the Select button */
            if (theSelectButton.equals(o)) {
                /* If the panel has changed */
                MetisPreferenceSetPanel myPanel = theSelectButton.getValue();
                if (!MetisDifference.isEqual(theActive, myPanel)) {
                    /* Set the Active component */
                    theActive = myPanel;

                    /* Move correct card to front */
                    theLayout.show(theProperties, theActive.toString());
                }
            }
        }
    }

    /**
     * PreferenceSetListener class.
     */
    private final class PrefSetListener
            implements TethysActionEventListener {
        /**
         * UpdateSet Registration.
         */
        private final TethysActionRegistration thePrefSetReg;

        /**
         * Constructor.
         * @param pPreferenceMgr the preference manager
         */
        private PrefSetListener(final MetisPreferenceManager pPreferenceMgr) {
            thePrefSetReg = pPreferenceMgr.getEventRegistrar().addActionListener(this);
        }

        @Override
        public void processAction(final TethysActionEvent pEvent) {
            /* If this is a new preference set */
            if (thePrefSetReg.isRelevant(pEvent)) {
                /* Details is the property set that has been added */
                MetisPreferenceSet mySet = pEvent.getDetails(MetisPreferenceSet.class);

                /* Register the set */
                registerSet(mySet);

                /* Note that the panel should be re-displayed */
                setVisibility();
                theProperties.invalidate();
            }
        }
    }
}
