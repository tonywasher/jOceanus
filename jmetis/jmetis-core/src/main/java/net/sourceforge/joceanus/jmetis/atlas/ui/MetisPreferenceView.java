/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Panel for editing preference Sets.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisPreferenceView<N, I>
        implements TethysEventProvider<MetisPreferenceEvent>, TethysNode<N> {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisPreferenceView.class);

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The GUI factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The Border Pane.
     */
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * The selection button.
     */
    private final TethysScrollButtonManager<MetisPreferenceSetView<?, N, I>, N, I> theSelectButton;

    /**
     * Preference menu.
     */
    private final TethysScrollMenu<MetisPreferenceSetView<?, N, I>, I> thePrefMenu;

    /**
     * The Properties Pane.
     */
    private final TethysCardPaneManager<N, I, MetisPreferenceSetView<?, N, I>> theProperties;

    /**
     * The Buttons Pane.
     */
    private final TethysBoxPaneManager<N, I> theButtons;

    /**
     * The OK button.
     */
    private final TethysButton<N, I> theOKButton;

    /**
     * The reset button.
     */
    private final TethysButton<N, I> theResetButton;

    /**
     * The list of views.
     */
    private final List<MetisPreferenceSetView<?, N, I>> theViews;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pPreferenceMgr the preference manager
     */
    public MetisPreferenceView(final TethysGuiFactory<N, I> pFactory,
                               final MetisPreferenceManager pPreferenceMgr) {
        /* Store parameters */
        theGuiFactory = pFactory;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the buttons */
        theOKButton = theGuiFactory.newButton();
        theOKButton.setTextOnly();
        theOKButton.setText(NLS_OK);
        theResetButton = theGuiFactory.newButton();
        theResetButton.setTextOnly();
        theResetButton.setText(NLS_RESET);

        /* Add Listeners */
        theOKButton.getEventRegistrar().addEventListener(e -> saveUpdates());
        theResetButton.getEventRegistrar().addEventListener(e -> resetUpdates());

        /* Create the buttons box */
        theButtons = theGuiFactory.newHBoxPane();
        theButtons.setBorderTitle(NLS_SAVE);
        theButtons.addSpacer();
        theButtons.addNode(theOKButton);
        theButtons.addSpacer();
        theButtons.addNode(theResetButton);
        theButtons.addSpacer();

        /* Create the properties pane */
        theProperties = theGuiFactory.newCardPane();

        /* Create the view list */
        theViews = new ArrayList<>();

        /* Loop through the existing property sets */
        for (MetisPreferenceSet<?> mySet : pPreferenceMgr.getPreferenceSets()) {
            /* Register the Set */
            registerSet(mySet);
        }

        /* Add a listener for the addition of subsequent propertySets */
        pPreferenceMgr.getEventRegistrar().addEventListener(this::handleNewPropertySet);

        /* Create selection button and label */
        final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(NLS_SET);
        theSelectButton = pFactory.newScrollButton();
        thePrefMenu = theSelectButton.getMenu();

        /* Create the selection panel */
        final TethysBoxPaneManager<N, I> mySelection = theGuiFactory.newHBoxPane();
        mySelection.setBorderTitle(NLS_SELECT);

        /* Create the layout for the selection panel */
        mySelection.addNode(myLabel);
        mySelection.addNode(theSelectButton);
        mySelection.addSpacer();

        /* Set listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handlePropertySetSelect());
        theSelectButton.setMenuConfigurator(c -> buildPreferenceMenu());

        /* Create a new Scroll Pane and add the card to it */
        final TethysScrollPaneManager<N, I> myScrollPane = theGuiFactory.newScrollPane();
        myScrollPane.setContent(theProperties);

        /* Create the border pane */
        thePane = theGuiFactory.newBorderPane();
        thePane.setNorth(mySelection);
        thePane.setCentre(myScrollPane);
        thePane.setSouth(theButtons);

        /* Determine the active items */
        setSelectText();
        setVisibility();
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    @Override
    public N getNode() {
        return thePane.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePane.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePane.setEnabled(pEnabled);
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * handle propertySetSelect event.
     */
    private void handlePropertySetSelect() {
        final MetisPreferenceSetView<?, N, I> myView = theSelectButton.getValue();
        theProperties.selectCard(myView.toString());
    }

    /**
     * handle new propertySet event.
     * @param pEvent the event
     */
    private void handleNewPropertySet(final TethysEvent<MetisPreferenceEvent> pEvent) {
        /* Details is the property set that has been added */
        final MetisPreferenceSet<?> mySet = pEvent.getDetails(MetisPreferenceSet.class);

        /* Register the set */
        registerSet(mySet);

        /* Note that the panel should be re-displayed */
        setVisibility();
    }

    /**
     * RegisterSet.
     * @param pSet the set to register
     */
    private void registerSet(final MetisPreferenceSet<?> pSet) {
        /* Ignore hidden sets */
        if (!pSet.isHidden()) {
            /* Create the underlying view */
            final MetisPreferenceSetView<?, N, I> myView = new MetisPreferenceSetView<>(theGuiFactory, pSet);

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
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        final MetisPreferenceSetView<?, N, I> myPanel = theProperties.getActiveCard();
        if (myPanel != null) {
            myPanel.determineFocus();
        }
    }

    /**
     * Does the panel have unsaved updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        final MetisPreferenceSetView<?, N, I> myView = theProperties.getActiveCard();
        return (myView != null)
               && myView.hasChanges();
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
    private void saveUpdates() {
        try {
            final MetisPreferenceSetView<?, N, I> myView = theProperties.getActiveCard();
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
        final MetisPreferenceSetView<?, N, I> myView = theProperties.getActiveCard();
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
        final MetisPreferenceSetView<?, N, I> myView = theProperties.getActiveCard();
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
        final String myActiveName = theProperties.getActiveName();

        /* Loop through the views */
        final Iterator<MetisPreferenceSetView<?, N, I>> myIterator = theViews.iterator();
        while (myIterator.hasNext()) {
            final MetisPreferenceSetView<?, N, I> myView = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<?> myItem = thePrefMenu.addItem(myView);

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
