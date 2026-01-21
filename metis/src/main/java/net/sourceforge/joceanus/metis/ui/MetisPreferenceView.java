/*
 * Metis: Java Data Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.joceanus.metis.ui;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIScrollPaneManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for editing preference Sets.
 */
public class MetisPreferenceView
        implements OceanusEventProvider<MetisPreferenceEvent>, TethysUIComponent {
    /**
     * Text for OK.
     */
    private static final String NLS_OK = MetisPreferenceResource.UI_BUTTON_OK.getValue();

    /**
     * Text for Reset.
     */
    private static final String NLS_RESET = MetisPreferenceResource.UI_BUTTON_RESET.getValue();

    /**
     * Text for Save.
     */
    private static final String NLS_SAVE = MetisPreferenceResource.UI_TITLE_SAVE.getValue();

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
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MetisPreferenceView.class);

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The GUI factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * The Border Pane.
     */
    private final TethysUIBorderPaneManager thePane;

    /**
     * The selection button.
     */
    private final TethysUIScrollButtonManager<TethysUIGenericWrapper> theSelectButton;

    /**
     * Preference menu.
     */
    private final TethysUIScrollMenu<TethysUIGenericWrapper> thePrefMenu;

    /**
     * The Properties Pane.
     */
    private final TethysUICardPaneManager<MetisPreferenceSetView> theProperties;

    /**
     * The Buttons Pane.
     */
    private final TethysUIBoxPaneManager theButtons;

    /**
     * The list of views.
     */
    private final List<MetisPreferenceSetView> theViews;

    /**
     * Constructor.
     *
     * @param pFactory       the GUI factory
     * @param pPreferenceMgr the preference manager
     */
    public MetisPreferenceView(final TethysUIFactory<?> pFactory,
                               final MetisPreferenceManager pPreferenceMgr) {
        /* Store parameters */
        theGuiFactory = pFactory;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the buttons */
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        final TethysUIButton myOKButton = myButtons.newButton();
        myOKButton.setTextOnly();
        myOKButton.setText(NLS_OK);
        final TethysUIButton myResetButton = myButtons.newButton();
        myResetButton.setTextOnly();
        myResetButton.setText(NLS_RESET);

        /* Add Listeners */
        myOKButton.getEventRegistrar().addEventListener(e -> saveUpdates());
        myResetButton.getEventRegistrar().addEventListener(e -> resetUpdates());

        /* Create the buttons box */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        theButtons = myPanes.newHBoxPane();
        theButtons.setBorderTitle(NLS_SAVE);
        theButtons.addSpacer();
        theButtons.addNode(myOKButton);
        theButtons.addSpacer();
        theButtons.addNode(myResetButton);
        theButtons.addSpacer();

        /* Create the properties pane */
        theProperties = myPanes.newCardPane();

        /* Create the view list */
        theViews = new ArrayList<>();

        /* Loop through the existing property sets */
        for (MetisPreferenceSet mySet : pPreferenceMgr.getPreferenceSets()) {
            /* Register the Set */
            registerSet(mySet);
        }

        /* Add a listener for the addition of subsequent propertySets */
        pPreferenceMgr.getEventRegistrar().addEventListener(this::handleNewPropertySet);

        /* Create selection button and label */
        final TethysUILabel myLabel = theGuiFactory.controlFactory().newLabel(NLS_SET);
        theSelectButton = myButtons.newScrollButton(TethysUIGenericWrapper.class);
        thePrefMenu = theSelectButton.getMenu();

        /* Create the selection panel */
        final TethysUIBoxPaneManager mySelection = myPanes.newHBoxPane();
        mySelection.setBorderTitle(NLS_SELECT);

        /* Create the layout for the selection panel */
        mySelection.addNode(myLabel);
        mySelection.addNode(theSelectButton);
        mySelection.addSpacer();

        /* Set listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handlePropertySetSelect());
        theSelectButton.setMenuConfigurator(c -> buildPreferenceMenu());

        /* Create a new Scroll Pane and add the card to it */
        final TethysUIScrollPaneManager myScrollPane = myPanes.newScrollPane();
        myScrollPane.setContent(theProperties);

        /* Create the border pane */
        thePane = myPanes.newBorderPane();
        thePane.setNorth(mySelection);
        thePane.setCentre(myScrollPane);
        thePane.setSouth(theButtons);

        /* Determine the active items */
        setSelectText();
        setVisibility();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePane;
    }

    @Override
    public OceanusEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * handle propertySetSelect event.
     */
    private void handlePropertySetSelect() {
        final MetisPreferenceSetView myView = (MetisPreferenceSetView) theSelectButton.getValue().getData();
        theProperties.selectCard(myView.toString());
    }

    /**
     * handle new propertySet event.
     *
     * @param pEvent the event
     */
    private void handleNewPropertySet(final OceanusEvent<MetisPreferenceEvent> pEvent) {
        /* Details is the property set that has been added */
        final MetisPreferenceSet mySet = pEvent.getDetails(MetisPreferenceSet.class);

        /* Register the set */
        registerSet(mySet);

        /* Note that the panel should be re-displayed */
        setVisibility();
    }

    /**
     * RegisterSet.
     *
     * @param pSet the set to register
     */
    private void registerSet(final MetisPreferenceSet pSet) {
        /* Ignore hidden sets */
        if (!pSet.isHidden()) {
            /* Create the underlying view */
            final MetisPreferenceSetView myView = createView(theGuiFactory, pSet);

            /* Add the view */
            theProperties.addCard(myView.toString(), myView);
            theViews.add(myView);

            /* Add listener */
            myView.getEventRegistrar().addEventListener(e -> {
                setVisibility();
                theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
            });
        }
    }

    /**
     * Create view for preference.
     *
     * @param pFactory the gui factory
     * @param pSet     the set to register
     * @return the view
     */
    protected MetisPreferenceSetView createView(final TethysUIFactory<?> pFactory,
                                                final MetisPreferenceSet pSet) {
        /* Create the underlying view */
        return new MetisPreferenceSetView(pFactory, pSet);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        final MetisPreferenceSetView myPanel = theProperties.getActiveCard();
        if (myPanel != null) {
            myPanel.determineFocus();
        }
    }

    /**
     * Does the panel have unsaved updates?
     *
     * @return true/false
     */
    public boolean hasUpdates() {
        final MetisPreferenceSetView myView = theProperties.getActiveCard();
        return (myView != null)
                && myView.hasChanges();
    }

    /**
     * Has this set of panels got the session focus?
     *
     * @return true/false
     */
    public boolean hasSession() {
        return hasUpdates();
    }

    /**
     * Save Updates.
     */
    private void saveUpdates() {
        try {
            final MetisPreferenceSetView myView = theProperties.getActiveCard();
            myView.storeChanges();
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
    private void resetUpdates() {
        /* Reset all changes */
        final MetisPreferenceSetView myView = theProperties.getActiveCard();
        myView.resetChanges();

        /* Set correct visibility */
        setVisibility();

        /* Notify that state has changed */
        theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
    }

    /**
     * Set the visibility.
     */
    private void setVisibility() {
        /* Enable selection */
        final MetisPreferenceSetView myView = theProperties.getActiveCard();
        theSelectButton.setEnabled((myView != null)
                && !myView.hasChanges());

        /* Show/Hide the buttons */
        theButtons.setVisible((myView != null)
                && myView.hasChanges());
    }

    /**
     * Set the select button text.
     */
    private void setSelectText() {
        /* Show selection text */
        theSelectButton.setValue(new TethysUIGenericWrapper(theProperties.getActiveCard()));
    }

    /**
     * Show Preference menu.
     */
    private void buildPreferenceMenu() {
        /* Reset the popUp menu */
        thePrefMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<?> myActive = null;
        final String myActiveName = theProperties.getActiveName();

        /* Loop through the views */
        for (MetisPreferenceSetView myView : theViews) {
            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<?> myItem = thePrefMenu.addItem(new TethysUIGenericWrapper(myView));

            /* If this is the active panel */
            if (myView.toString().equals(myActiveName)) {
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
