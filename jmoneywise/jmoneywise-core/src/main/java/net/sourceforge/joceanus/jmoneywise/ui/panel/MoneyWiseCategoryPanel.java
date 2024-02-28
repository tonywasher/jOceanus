/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.panel;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jprometheus.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;

public class MoneyWiseCategoryPanel
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.CATEGORY_DATAENTRY.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = MoneyWiseUIResource.CATEGORY_TITLE_SELECT.getValue();

    /**
     * Text for Selection Prompt.
     */
    private static final String NLS_DATA = MoneyWiseUIResource.CATEGORY_PROMPT_SELECT.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final MoneyWiseView theView;

    /**
     * The Panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The select button.
     */
    private final TethysUIScrollButtonManager<PanelName> theSelectButton;

    /**
     * The card panel.
     */
    private final TethysUICardPaneManager<TethysUIComponent> theCardPanel;

    /**
     * The select panel.
     */
    private final TethysUIBorderPaneManager theSelectPanel;

    /**
     * The filter card panel.
     */
    private final TethysUICardPaneManager<TethysUIComponent> theFilterCardPanel;

    /**
     * Deposit Categories Table.
     */
    private final MoneyWiseDepositCategoryTable theDepositTable;

    /**
     * Cash Categories Table.
     */
    private final MoneyWiseCashCategoryTable theCashTable;

    /**
     * Loan Categories Table.
     */
    private final MoneyWiseLoanCategoryTable theLoanTable;

    /**
     * Transaction Categories Table.
     */
    private final MoneyWiseTransCategoryTable theEventTable;

    /**
     * Event Tags Table.
     */
    private final MoneyWiseTransTagTable theTagTable;

    /**
     * Regions Table.
     */
    private final MoneyWiseRegionTable theRegionTable;

    /**
     * The UpdateSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The viewer entry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The action buttons panel.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * Are we refreshing?
     */
    private boolean isRefreshing;

    /**
     * The active panel.
     */
    private PanelName theActive;

    /**
     * Constructor.
     * @param pView the data view
     */
    MoneyWiseCategoryPanel(final MoneyWiseView pView) {
        /* Store details */
        theView = pView;

        /* Access GUI Factory */
        final TethysUIFactory<?> myFactory = pView.getGuiFactory();
        final MetisViewerManager myViewer = pView.getViewerManager();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theEditSet = new PrometheusEditSet(pView);

        /* Create the Panel */
        final TethysUIPaneFactory myPanes = myFactory.paneFactory();
        thePanel = myPanes.newBorderPane();

        /* Create the top level viewer entry for this view */
        final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.MAINTENANCE);
        theViewerEntry = myViewer.newEntry(mySection, NLS_DATAENTRY);
        theViewerEntry.setTreeObject(theEditSet);

        /* Create the error panel */
        theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerEntry);

        /* Create the action buttons panel */
        theActionButtons = new PrometheusActionButtons(myFactory, theEditSet);

        /* Create the table panels */
        theDepositTable = new MoneyWiseDepositCategoryTable(pView, theEditSet, theError);
        theCashTable = new MoneyWiseCashCategoryTable(pView, theEditSet, theError);
        theLoanTable = new MoneyWiseLoanCategoryTable(pView, theEditSet, theError);
        theEventTable = new MoneyWiseTransCategoryTable(pView, theEditSet, theError);
        theTagTable = new MoneyWiseTransTagTable(pView, theEditSet, theError);
        theRegionTable = new MoneyWiseRegionTable(pView, theEditSet, theError);

        /* Create selection button and label */
        final TethysUILabel myLabel = myFactory.controlFactory().newLabel(NLS_DATA);
        theSelectButton = myFactory.buttonFactory().newScrollButton(PanelName.class);
        buildSelectMenu();

        /* Create the card panel */
        theCardPanel = myPanes.newCardPane();

        /* Add to the card panels */
        theCardPanel.addCard(PanelName.DEPOSITS.toString(), theDepositTable);
        theCardPanel.addCard(PanelName.CASH.toString(), theCashTable);
        theCardPanel.addCard(PanelName.LOANS.toString(), theLoanTable);
        theCardPanel.addCard(PanelName.EVENTS.toString(), theEventTable);
        theCardPanel.addCard(PanelName.EVENTTAGS.toString(), theTagTable);
        theCardPanel.addCard(PanelName.REGIONS.toString(), theRegionTable);
        theActive = PanelName.DEPOSITS;
        theSelectButton.setValue(theActive);

        /* Create the card panel */
        theFilterCardPanel = myPanes.newCardPane();

        /* Add to the card panels */
        theFilterCardPanel.addCard(PanelName.DEPOSITS.toString(), theDepositTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.CASH.toString(), theCashTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.LOANS.toString(), theLoanTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.EVENTS.toString(), theEventTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.EVENTTAGS.toString(), theTagTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.REGIONS.toString(), theRegionTable.getFilterPanel());

        /* Create the select prompt */
        final TethysUIBoxPaneManager mySelect = myPanes.newHBoxPane();
        mySelect.addNode(myLabel);
        mySelect.addNode(theSelectButton);

        /* Create the selection panel */
        theSelectPanel = myPanes.newBorderPane();
        theSelectPanel.setBorderTitle(NLS_SELECT);
        theSelectPanel.setWest(mySelect);
        theSelectPanel.setCentre(theFilterCardPanel);

        /* Create the header panel */
        final TethysUIBorderPaneManager myHeader = myPanes.newBorderPane();
        myHeader.setCentre(theSelectPanel);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(theCardPanel);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listeners */
        theSelectButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleSelection());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        setChildListeners(theDepositTable.getEventRegistrar());
        setChildListeners(theCashTable.getEventRegistrar());
        setChildListeners(theLoanTable.getEventRegistrar());
        setChildListeners(theEventTable.getEventRegistrar());
        setChildListeners(theTagTable.getEventRegistrar());
        setChildListeners(theRegionTable.getEventRegistrar());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * setChildListeners.
     * @param pRegistrar the registrar
     */
    private void setChildListeners(final TethysEventRegistrar<PrometheusDataEvent> pRegistrar) {
        pRegistrar.addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> {
            if (!isRefreshing) {
                setVisibility();
            }
        });
        pRegistrar.addEventListener(PrometheusDataEvent.GOTOWINDOW, this::handleGoToEvent);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSelectButton.setEnabled(pEnabled);
        theCardPanel.setEnabled(pEnabled);
        theFilterCardPanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Build select menu.
     */
    private void buildSelectMenu() {
        /* Create builder */
        final TethysUIScrollMenu<PanelName> myMenu = theSelectButton.getMenu();

        /* Loop through the panels */
        for (PanelName myPanel : PanelName.values()) {
            /* Create a new MenuItem for the panel */
            myMenu.addItem(myPanel);
        }
    }

    /**
     * Refresh data.
     * @throws OceanusException on error
     */
    public void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Categories");

        /* Note that we are refreshing */
        isRefreshing = true;

        /* Refresh the tables */
        theDepositTable.refreshData();
        theCashTable.refreshData();
        theLoanTable.refreshData();
        theEventTable.refreshData();
        theTagTable.refreshData();
        theRegionTable.refreshData();

        /* Clear refreshing flag */
        isRefreshing = false;
        setVisibility();

        /* Touch the updateSet */
        theViewerEntry.setTreeObject(theEditSet);

        /* Complete the task */
        myTask.end();
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Switch on active component */
        switch (theActive) {
            case DEPOSITS:
                theDepositTable.determineFocus(theViewerEntry);
                break;
            case CASH:
                theCashTable.determineFocus(theViewerEntry);
                break;
            case LOANS:
                theLoanTable.determineFocus(theViewerEntry);
                break;
            case EVENTS:
                theEventTable.determineFocus(theViewerEntry);
                break;
            case EVENTTAGS:
                theTagTable.determineFocus(theViewerEntry);
                break;
            case REGIONS:
                theRegionTable.determineFocus(theViewerEntry);
                break;
            default:
                break;
        }
    }

    /**
     * Does this panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        boolean hasUpdates = theDepositTable.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theCashTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theLoanTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theEventTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theTagTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theRegionTable.hasUpdates();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Determine whether we have session focus */
        boolean hasSession = theDepositTable.hasSession();
        if (!hasSession) {
            hasSession = theCashTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theLoanTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theEventTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theTagTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theRegionTable.hasSession();
        }

        /* Return to caller */
        return hasSession;
    }

    /**
     * Does this panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Determine whether we have errors */
        boolean hasErrors = theDepositTable.hasErrors();
        if (!hasErrors) {
            hasErrors = theCashTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theLoanTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theEventTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theTagTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theRegionTable.hasErrors();
        }

        /* Return to caller */
        return hasErrors;
    }

    /**
     * Does this panel have item editing occurring?
     * @return true/false
     */
    public boolean isItemEditing() {
        /* Determine whether we have item editing */
        boolean isEditing = theDepositTable.isItemEditing();
        if (!isEditing) {
            isEditing = theCashTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theLoanTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theEventTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theTagTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theRegionTable.isItemEditing();
        }

        /* Return to caller */
        return isEditing;
    }

    /**
     * Select category.
     * @param pCategory the category to select
     */
    public void selectCategory(final Object pCategory) {
        /* Determine which panel to show */
        if (pCategory instanceof MoneyWiseDepositCategory) {
            theDepositTable.selectCategory((MoneyWiseDepositCategory) pCategory);
            showPanel(PanelName.DEPOSITS);
        } else if (pCategory instanceof MoneyWiseCashCategory) {
            theCashTable.selectCategory((MoneyWiseCashCategory) pCategory);
            showPanel(PanelName.CASH);
        } else if (pCategory instanceof MoneyWiseLoanCategory) {
            theLoanTable.selectCategory((MoneyWiseLoanCategory) pCategory);
            showPanel(PanelName.LOANS);
        } else if (pCategory instanceof MoneyWiseTransCategory) {
            theEventTable.selectCategory((MoneyWiseTransCategory) pCategory);
            showPanel(PanelName.EVENTS);
        }
    }

    /**
     * Select tag.
     * @param pTag the category to select
     */
    public void selectTag(final Object pTag) {
        /* Determine which panel to show */
        if (pTag instanceof MoneyWiseTransTag) {
            theTagTable.selectTag((MoneyWiseTransTag) pTag);
            showPanel(PanelName.EVENTTAGS);
        }
    }

    /**
     * Select region.
     * @param pRegion the region to select
     */
    public void selectRegion(final Object pRegion) {
        /* Determine which panel to show */
        if (pRegion instanceof MoneyWiseRegion) {
            theRegionTable.selectRegion((MoneyWiseRegion) pRegion);
            showPanel(PanelName.REGIONS);
        }
    }

    /**
     * Show panel.
     * @param pName the panel name
     */
    private void showPanel(final PanelName pName) {
        /* Obtain name of panel */
        final String myName = pName.toString();

        /* Move correct card to front */
        theCardPanel.selectCard(myName);
        theFilterCardPanel.selectCard(myName);

        /* Note the active panel */
        theActive = pName;
        theSelectButton.setFixedText(myName);

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        final boolean hasUpdates = hasUpdates();
        final boolean isItemEditing = isItemEditing();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);

        /* Update the selection */
        theSelectButton.setEnabled(!isItemEditing);
        theFilterCardPanel.setEnabled(!isItemEditing);

        /* Alert listeners that there has been a change */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Cancel editing on subPanels */
        theDepositTable.cancelEditing();
        theCashTable.cancelEditing();
        theLoanTable.cancelEditing();
        theEventTable.cancelEditing();
        theTagTable.cancelEditing();
        theRegionTable.cancelEditing();
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelectPanel.setVisible(!isError);

        /* Lock card panel */
        theCardPanel.setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * handleSelection.
     */
    private void handleSelection() {
        /* Cancel any editing */
        cancelEditing();

        /* Show selected panel */
        showPanel(theSelectButton.getValue());
    }

    /**
     * handle Action Buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel editing */
        cancelEditing();

        /* Perform the command */
        theEditSet.processCommand(pEvent.getEventId(), theError);
    }

    /**
     * handle GoTo Event.
     * @param pEvent the event
     */
    private void handleGoToEvent(final TethysEvent<PrometheusDataEvent> pEvent) {
        /* Access details */
        @SuppressWarnings("unchecked")
        final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = pEvent.getDetails(PrometheusGoToEvent.class);

        /* Access event and obtain details */
        switch (myEvent.getId()) {
            /* Pass through the event */
            case STATEMENT:
            case ACCOUNT:
            case STATIC:
                theEventManager.cascadeEvent(pEvent);
                break;

            /* Access subPanels */
            case CATEGORY:
                selectCategory(myEvent.getDetails());
                break;
            case TAG:
                selectTag(pEvent.getDetails());
                break;
            case REGION:
                selectRegion(pEvent.getDetails());
                break;
            default:
                break;
        }
    }

    /**
     * Panel names.
     */
    private enum PanelName {
        /**
         * Deposits.
         */
        DEPOSITS(MoneyWiseBasicDataType.DEPOSITCATEGORY),

        /**
         * Cash.
         */
        CASH(MoneyWiseBasicDataType.CASHCATEGORY),

        /**
         * Loans.
         */
        LOANS(MoneyWiseBasicDataType.LOANCATEGORY),

        /**
         * Events.
         */
        EVENTS(MoneyWiseBasicDataType.TRANSCATEGORY),

        /**
         * Tags.
         */
        EVENTTAGS(MoneyWiseBasicDataType.TRANSTAG),

        /**
         * Regions.
         */
        REGIONS(MoneyWiseBasicDataType.REGION);

        /**
         * The String name.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pDataType the dataType
         */
        PanelName(final MoneyWiseBasicDataType pDataType) {
            theName = pDataType.getListName();
        }

        @Override
        public String toString() {
            /* return the name */
            return theName;
        }
    }
}
