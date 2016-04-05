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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Preference maintenance panel.
 * @author Tony Washer
 */
public class MetisPreferencesPanel
        implements TethysEventProvider<MetisPreferenceEvent>, TethysNode<JComponent> {
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
     * The Gui Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

    /**
     * The Data entry.
     */
    private final MetisViewerEntry theDataEntry;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

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
    private final TethysSwingScrollButtonManager<MetisPreferenceSetPanel> theSelectButton;

    /**
     * The properties panel.
     */
    private final TethysSwingCardPaneManager<MetisPreferenceSetPanel> theProperties;

    /**
     * The buttons panel.
     */
    private final JPanel theButtons;

    /**
     * The scroll pane.
     */
    private final JScrollPane theScrollPane;

    /**
     * Preference menu.
     */
    private final TethysScrollMenu<MetisPreferenceSetPanel, ?> thePrefMenu;

    /**
     * The list of panels.
     */
    private final List<MetisPreferenceSetPanel> thePanels;

    /**
     * Constructor.
     * @param pFactory the GuiFactory
     * @param pPreferenceMgr the preference manager
     * @param pFieldMgr the field manager
     * @param pDataMgr the data manager
     * @param pSection the data section
     */
    public MetisPreferencesPanel(final TethysSwingGuiFactory pFactory,
                                 final MetisPreferenceManager pPreferenceMgr,
                                 final MetisFieldManager pFieldMgr,
                                 final MetisViewerManager pDataMgr,
                                 final MetisViewerEntry pSection) {
        /* Record the GUI Factory */
        theGuiFactory = pFactory;
        theId = theGuiFactory.getNextId();

        /* Access field manager and logger */
        theFieldMgr = pFieldMgr;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = new TethysSwingEnablePanel();

        /* Create the buttons */
        theOKButton = new JButton(NLS_OK);
        theResetButton = new JButton(NLS_RESET);

        /* Create the buttons panel */
        theButtons = new TethysSwingEnablePanel();
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
        theSelectButton = pFactory.newScrollButton();

        /* Create the selection panel */
        JPanel mySelection = new TethysSwingEnablePanel();
        mySelection.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelection.setLayout(new BoxLayout(mySelection, BoxLayout.X_AXIS));
        mySelection.add(myLabel);
        mySelection.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelection.add(theSelectButton.getNode());
        mySelection.add(Box.createHorizontalGlue());

        /* Create the properties panel */
        theProperties = pFactory.newCardPane();

        /* Add Listeners */
        theOKButton.addActionListener(e -> saveUpdates());
        theResetButton.addActionListener(e -> resetUpdates());
        thePrefMenu = theSelectButton.getMenu();
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handlePropertySetSelect());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildPreferenceMenu());

        /* Create the panel list */
        thePanels = new ArrayList<>();

        /* Loop through the existing property sets */
        for (MetisPreferenceSet mySet : pPreferenceMgr.getPreferenceSets()) {
            /* Register the Set */
            registerSet(mySet);
        }

        /* Create a new Scroll Pane and add this table to it */
        theScrollPane = new JScrollPane();
        theScrollPane.setViewportView(theProperties.getNode());

        /* Now define the panel */
        thePanel.setLayout(new BorderLayout());
        thePanel.add(mySelection, BorderLayout.PAGE_START);
        thePanel.add(theScrollPane, BorderLayout.CENTER);
        thePanel.add(theButtons, BorderLayout.PAGE_END);

        /* Determine the active items */
        setSelectText();
        setVisibility();

        /* Create the debug entry, and attach to correct section */
        theDataEntry = pDataMgr.newEntry("Preferences");
        theDataEntry.addAsChildOf(pSection);
        theDataEntry.setObject(pPreferenceMgr);

        /* Add a listener for the addition of subsequent propertySets */
        pPreferenceMgr.getEventRegistrar().addEventListener(this::handleNewPropertySet);
    }

    /**
     * handle propertySetSelect event.
     */
    private void handlePropertySetSelect() {
        MetisPreferenceSetPanel myPanel = theSelectButton.getValue();
        theProperties.selectCard(myPanel.toString());
    }

    /**
     * handle new propertySet event.
     * @param pEvent the event
     */
    private void handleNewPropertySet(final TethysEvent<MetisPreferenceEvent> pEvent) {
        /* Details is the property set that has been added */
        MetisPreferenceSet mySet = pEvent.getDetails(MetisPreferenceSet.class);

        /* Register the set */
        registerSet(mySet);

        /* Note that the panel should be re-displayed */
        setVisibility();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        thePanel.requestFocusInWindow();

        /* Set the required focus */
        theDataEntry.setFocus();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Pass on to important elements */
        theSelectButton.setEnabled(pEnabled);
        theScrollPane.setEnabled(pEnabled);
        theProperties.setEnabled(pEnabled);
    }

    /**
     * RegisterSet.
     * @param pSet the set to register
     */
    private void registerSet(final MetisPreferenceSet pSet) {
        /* Create the underlying panel */
        MetisPreferenceSetPanel myPanel = new MetisPreferenceSetPanel(theGuiFactory, theFieldMgr, pSet);

        /* Add the panel */
        theProperties.addCard(myPanel.toString(), myPanel);
        thePanels.add(myPanel);

        /* Add listener */
        myPanel.getEventRegistrar().addEventListener(e -> {
            setVisibility();
            theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
        });
    }

    /**
     * Does the panel have unsaved updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        MetisPreferenceSetPanel myPanel = theProperties.getActiveCard();
        return (myPanel != null)
               && myPanel.hasChanges();
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
            MetisPreferenceSetPanel myPanel = theProperties.getActiveCard();
            myPanel.storeChanges();
        } catch (OceanusException e) {
            LOGGER.error(ERROR_STORE, e);
        }

        /* Set correct visibility */
        setVisibility();

        /* Notify that state has changed */
        theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
    }

    /**
     * Reset Updates.
     */
    public void resetUpdates() {
        /* Reset all changes */
        MetisPreferenceSetPanel myPanel = theProperties.getActiveCard();
        myPanel.resetChanges();

        /* Set correct visibility */
        setVisibility();

        /* Notify that state has changed */
        theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
    }

    /**
     * Set the visibility.
     */
    protected final void setVisibility() {
        /* Enable selection */
        MetisPreferenceSetPanel myPanel = theProperties.getActiveCard();
        theSelectButton.setEnabled((myPanel != null)
                                   && !myPanel.hasChanges());

        /* Show/Hide the buttons */
        theButtons.setVisible((myPanel != null)
                              && myPanel.hasChanges());
    }

    /**
     * Set the select button text.
     */
    private void setSelectText() {
        /* Show selection text */
        theSelectButton.setValue(theProperties.getActiveCard());
    }

    /**
     * Show Preference menu.
     */
    private void buildPreferenceMenu() {
        /* Reset the popUp menu */
        thePrefMenu.removeAllItems();

        /* Record active item */
        TethysScrollMenuItem<?> myActive = null;
        String myActiveName = theProperties.getActiveName();

        /* Loop through the panels */
        Iterator<MetisPreferenceSetPanel> myIterator = thePanels.iterator();
        while (myIterator.hasNext()) {
            MetisPreferenceSetPanel myPanel = myIterator.next();

            /* Create a new JMenuItem and add it to the popUp */
            TethysScrollMenuItem<?> myItem = thePrefMenu.addItem(myPanel);

            /* If this is the active panel */
            if (myPanel.toString().equals(myActiveName)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }
}
