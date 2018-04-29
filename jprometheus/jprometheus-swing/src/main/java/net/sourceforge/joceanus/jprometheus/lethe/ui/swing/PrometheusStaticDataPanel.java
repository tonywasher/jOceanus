/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.ui.swing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheFieldEnum;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.lethe.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingCheckBox;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingLabel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Top level panel for static data.
 * @param <E> the data type enum class
 */
public class PrometheusStaticDataPanel<E extends Enum<E> & MetisLetheFieldEnum>
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<JComponent> {
    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = PrometheusUIResource.STATIC_TITLE_SELECT.getValue();

    /**
     * Text for Data.
     */
    private static final String NLS_DATA = PrometheusUIResource.STATIC_PROMPT_DATA.getValue();

    /**
     * Text for Show Disabled.
     */
    private static final String NLS_DISABLED = PrometheusUIResource.STATIC_PROMPT_DISABLED.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UtilitySet.
     */
    private final JOceanusSwingUtilitySet theUtilitySet;

    /**
     * The data control.
     */
    private final DataControl<?, E, JComponent, Icon> theControl;

    /**
     * The Panel.
     */
    private final TethysSwingBorderPaneManager thePanel;

    /**
     * The Selection Panel.
     */
    private final TethysSwingBorderPaneManager theSelectionPanel;

    /**
     * The table card panel.
     */
    private final TethysSwingCardPaneManager<PrometheusStaticDataTable<?, ?, ?, E>> theTableCard;

    /**
     * The new card panel.
     */
    private final TethysSwingCardPaneManager<TethysSwingScrollButtonManager<?>> theNewCard;

    /**
     * The selection button.
     */
    private final TethysSwingScrollButtonManager<PrometheusStaticDataTable<?, ?, ?, E>> theSelectButton;

    /**
     * Data menu builder.
     */
    private final TethysScrollMenu<PrometheusStaticDataTable<?, ?, ?, E>, ?> theDataMenu;

    /**
     * The disabled check box.
     */
    private final TethysSwingCheckBox theDisabledCheckBox;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

    /**
     * The action buttons panel.
     */
    private final PrometheusActionButtons<JComponent, Icon> theActionButtons;

    /**
     * The UpdateSet.
     */
    private final UpdateSet<E> theUpdateSet;

    /**
     * The ViewerEntry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The list of panels.
     */
    private final List<PrometheusStaticDataTable<?, ?, ?, E>> thePanels;

    /**
     * Constructor.
     * @param pControl the data control
     * @param pUtilitySet the utility set
     * @param pClass the dataType class
     */
    public PrometheusStaticDataPanel(final DataControl<?, E, JComponent, Icon> pControl,
                                     final JOceanusSwingUtilitySet pUtilitySet,
                                     final Class<E> pClass) {
        /* Store control */
        theControl = pControl;
        theUtilitySet = pUtilitySet;

        /* Obtain GUI Factory */
        final TethysSwingGuiFactory myFactory = pUtilitySet.getGuiFactory();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the Panel */
        thePanel = myFactory.newBorderPane();

        /* Build the Update set */
        theUpdateSet = new UpdateSet<>(pControl, pClass);

        /* Create the top level viewer entry for this view */
        theViewerEntry = theControl.getViewerEntry(PrometheusViewerEntryId.STATIC);
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Create the error panel */
        theError = theControl.getToolkit().newErrorPanel(theViewerEntry);

        /* Create the action buttons panel */
        theActionButtons = new PrometheusActionButtons<>(myFactory, theUpdateSet);

        /* Create the panel list */
        thePanels = new ArrayList<>();

        /* Create selection button and label */
        final TethysSwingLabel myLabel = myFactory.newLabel(NLS_DATA);
        theSelectButton = myFactory.newScrollButton();

        /* Create the CheckBox */
        theDisabledCheckBox = myFactory.newCheckBox(NLS_DISABLED);

        /* Create the selection panel */
        theSelectionPanel = myFactory.newBorderPane();
        theSelectionPanel.setBorderTitle(NLS_SELECT);

        /* Create the new card panel */
        theNewCard = myFactory.newCardPane();

        /* Create the layout for the selection panel */
        final TethysSwingBoxPaneManager mySubPanel = myFactory.newHBoxPane();
        mySubPanel.addNode(myLabel);
        mySubPanel.addNode(theSelectButton);
        mySubPanel.addSpacer();
        mySubPanel.addNode(theDisabledCheckBox);
        mySubPanel.addSpacer();
        theSelectionPanel.setCentre(mySubPanel);
        theSelectionPanel.setEast(theNewCard);

        /* Create the header panel */
        final TethysSwingBorderPaneManager myHeader = myFactory.newBorderPane();
        myHeader.setCentre(theSelectionPanel);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Create the table card panel */
        theTableCard = myFactory.newCardPane();

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(theTableCard);

        /* Set visibility of new button */
        showNewButton();

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Add listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handlePanelSelection());
        theSelectButton.setMenuConfigurator(e -> buildDataMenu());
        theDisabledCheckBox.getEventRegistrar().addEventListener(e -> showDisabled(theDisabledCheckBox.isSelected()));
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPanel());
        theDataMenu = theSelectButton.getMenu();
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public JComponent getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Handle action buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel Editing */
        cancelEditing();

        /* Process the command */
        theUpdateSet.processCommand(pEvent.getEventId(), theError);

        /* Adjust visibility */
        setVisibility();
    }

    /**
     * Handle panel selection.
     */
    private void handlePanelSelection() {
        final PrometheusStaticDataTable<?, ?, ?, E> myPanel = theSelectButton.getValue();
        final String myName = myPanel.getItemType().getFieldName();
        theTableCard.selectCard(myName);
        theNewCard.selectCard(myName);
        showNewButton();
    }

    /**
     * Handle error panel.
     */
    private void handleErrorPanel() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelectionPanel.setVisible(!isError);

        /* Lock scroll-able area */
        theTableCard.setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * Build StaticData menu.
     */
    private void buildDataMenu() {
        /* Cancel any editing */
        cancelEditing();

        /* Reset the popUp menu */
        theDataMenu.removeAllItems();

        /* Record active item */
        TethysScrollMenuItem<PrometheusStaticDataTable<?, ?, ?, E>> myActive = null;
        final String myActiveName = theTableCard.getActiveName();

        /* Loop through the panels */
        final Iterator<PrometheusStaticDataTable<?, ?, ?, E>> myIterator = thePanels.iterator();
        while (myIterator.hasNext()) {
            final PrometheusStaticDataTable<?, ?, ?, E> myTable = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            final String myName = myTable.getItemType().getFieldName();
            final TethysScrollMenuItem<PrometheusStaticDataTable<?, ?, ?, E>> myItem = theDataMenu.addItem(myTable, myName);

            /* If this is the active panel */
            if (myName.equals(myActiveName)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Obtain the updateList.
     * @return the viewSet
     */
    protected UpdateSet<E> getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Add static panel.
     * @param pItemType the type of the list
     * @param pListClass the class of the list
     * @param <L> the list type
     * @param <S> the static class
     * @param <T> the data type
     */
    public <L extends StaticList<T, S, E>, T extends StaticData<T, S, E>, S extends Enum<S> & StaticInterface> void addStatic(final E pItemType,
                                                                                                                              final Class<L> pListClass) {
        /* Create the new panel */
        final PrometheusStaticDataTable<L, T, S, E> myPanel = new PrometheusStaticDataTable<>(theControl, theUpdateSet, theUtilitySet, theError, pItemType, pListClass);

        /* Add the listener for the panel */
        myPanel.getEventRegistrar().addEventListener(e -> setVisibility());

        /* Access list name */
        final String myName = pItemType.getFieldName();

        /* Add to the card panels */
        theTableCard.addCard(myName, myPanel);
        theNewCard.addCard(myName, myPanel.getNewButton());

        /* Make sure that the active set is displayed */
        theSelectButton.setValue(theTableCard.getActiveCard(), theTableCard.getActiveName());

        /* Add to the List */
        thePanels.add(myPanel);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        final PrometheusStaticDataTable<?, ?, ?, E> myPanel = theTableCard.getActiveCard();
        if (myPanel != null) {
            myPanel.determineFocus(theViewerEntry);
        }
    }

    /**
     * Set Selection.
     * @param pName the name that is selected
     */
    private void setSelection(final String pName) {
        /* Select the correct static */
        theTableCard.selectCard(pName);
        theNewCard.selectCard(pName);
        determineFocus();
    }

    /**
     * Has this set of tables got updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Return to caller */
        return theUpdateSet.hasUpdates();
    }

    /**
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Return to caller */
        return hasUpdates();
    }

    /**
     * Has this set of tables got errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Return to caller */
        return theUpdateSet.hasErrors();
    }

    /**
     * Select static data.
     * @param pStatic the static data to select
     */
    public void selectStatic(final StaticData<?, ?, E> pStatic) {
        /* Access the item type */
        final E myType = pStatic.getItemType();
        final String myName = myType.getFieldName();

        /* Access the panel */
        if (theTableCard.selectCard(myName)) {
            /* Update selection */
            final PrometheusStaticDataTable<?, ?, ?, E> myPanel = theTableCard.getActiveCard();
            myPanel.selectStatic(pStatic);
            setSelection(myName);
        }
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws OceanusException on error
     */
    public void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("StaticData");

        /* Loop through the map */
        for (PrometheusStaticDataTable<?, ?, ?, E> myPanel : thePanels) {
            /* Note the stage */
            myTask.startTask(myPanel.getItemType().getFieldName());

            /* Refresh the panel */
            myPanel.refreshData();
        }

        /* Touch the updateSet */
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Complete the task */
        myTask.end();
    }

    /**
     * Show disabled.
     * @param pShow true/false
     */
    public void showDisabled(final boolean pShow) {
        /* Loop through the map */
        for (PrometheusStaticDataTable<?, ?, ?, ?> myPanel : thePanels) {
            /* Update the panel */
            myPanel.setShowAll(pShow);
        }
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Loop through the map */
        for (PrometheusStaticDataTable<?, ?, ?, ?> myPanel : thePanels) {
            /* Refresh the underlying children */
            myPanel.cancelEditing();
        }
    }

    /**
     * Show New button.
     */
    private void showNewButton() {
        /* Set visibility of New Button */
        final PrometheusStaticDataTable<?, ?, ?, E> myPanel = theSelectButton.getValue();
        final boolean showNew = myPanel != null
                                && !myPanel.isFull();
        theNewCard.setVisible(showNew);
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        final boolean hasUpdates = hasUpdates();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates);

        /* Set visibility of New Button */
        showNewButton();

        /* Alert listeners that there has been a change */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }
}
