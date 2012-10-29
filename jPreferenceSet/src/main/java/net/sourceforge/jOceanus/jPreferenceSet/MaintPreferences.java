/*******************************************************************************
 * jPreferenceSet: PreferenceSet Management
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
package net.sourceforge.jOceanus.jPreferenceSet;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataManager;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jEventManager.ActionDetailEvent;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jFieldSet.RenderManager;

/**
 * Preference maintenance panel.
 * @author Tony Washer
 */
public class MaintPreferences extends JEventPanel {
    /**
     * The serial Id.
     */
    private static final long serialVersionUID = -1512860688570367124L;

    /**
     * The panel width.
     */
    private static final int SELECT_WIDTH = 300;

    /**
     * The panel height.
     */
    private static final int SELECT_HEIGHT = 25;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MaintPreferences.class
            .getName());

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
     * The render manager.
     */
    private final transient RenderManager theRenderMgr;

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
     * The selection panel.
     */
    private final JComboBox<PreferenceSetPanel> theSelect;

    /**
     * The properties panel.
     */
    private final JPanel theProperties;

    /**
     * The layout.
     */
    private final CardLayout theLayout;

    /**
     * The active set.
     */
    private PreferenceSetPanel theActive = null;

    /**
     * The listener.
     */
    private final transient PropertyListener theListener;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pRenderMgr the render manager
     * @param pDataMgr the data manager
     * @param pSection the data section
     */
    public MaintPreferences(final PreferenceManager pPreferenceMgr,
                            final RenderManager pRenderMgr,
                            final JDataManager pDataMgr,
                            final JDataEntry pSection) {
        /* Access render manager */
        theRenderMgr = pRenderMgr;

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

        /* Create selection box and label */
        JLabel myLabel = new JLabel(NLS_SET);
        theSelect = new JComboBox<PreferenceSetPanel>();
        theSelect.setMaximumSize(new Dimension(SELECT_WIDTH, SELECT_HEIGHT));

        /* Create the selection panel */
        JPanel mySelection = new JPanel();
        mySelection.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelection.setLayout(new BoxLayout(mySelection, BoxLayout.X_AXIS));
        mySelection.add(myLabel);
        mySelection.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelection.add(theSelect);

        /* Create the properties panel */
        theProperties = new JPanel();
        theLayout = new CardLayout();
        theProperties.setLayout(theLayout);

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
        theActive = (PreferenceSetPanel) theSelect.getSelectedItem();
        setVisibility();

        /* Add Listeners */
        theOKButton.addActionListener(theListener);
        theResetButton.addActionListener(theListener);
        theSelect.addItemListener(theListener);

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

    /**
     * RegisterSet.
     * @param pSet the set to register
     */
    private void registerSet(final PreferenceSet pSet) {
        /* Create the underlying panel */
        PreferenceSetPanel myPanel = new PreferenceSetPanel(theRenderMgr, pSet);

        /* Add the panel */
        theProperties.add(myPanel, myPanel.toString());

        /* Add name to the ComboBox */
        theSelect.addItem(myPanel);
        myPanel.addChangeListener(theListener);
    }

    /**
     * Does the panel have unsaved updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return ((theActive != null) && (theActive.hasChanges()));
    }

    /**
     * Save Updates.
     */
    public void saveUpdates() {
        try {
            theActive.storeChanges();
        } catch (JDataException e) {
            e = null;
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
        theSelect.setEnabled((theActive != null) && !theActive.hasChanges());

        /* Enable the buttons */
        theOKButton.setEnabled((theActive != null) && theActive.hasChanges());
        theResetButton.setEnabled((theActive != null) && theActive.hasChanges());
    }

    /**
     * PropertyListener class.
     */
    private final class PropertyListener implements ActionListener, ItemListener, ChangeListener {
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
        public void itemStateChanged(final ItemEvent evt) {
            /* If this event relates to the selected box */
            if (theSelect.equals(evt.getSource())) {
                /* Set the Active component */
                theActive = (PreferenceSetPanel) evt.getItem();

                /* Show the requested set */
                theLayout.show(theProperties, theActive.toString());
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
     * PropertySetListener class.
     */
    private final class PropertySetListener implements ActionListener {
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
