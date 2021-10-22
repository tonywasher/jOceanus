/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.ui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType.PortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticInterface;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * MoneyWise Static Panel.
 */
public class MoneyWiseStaticPanel
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UpdateSet associated with the panel.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The ViewerEntry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The Panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The action buttons panel.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * The disabled check box.
     */
    private final TethysCheckBox theDisabledCheckBox;

    /**
     * The Selection Panel.
     */
    private final TethysBorderPaneManager theSelectionPanel;

    /**
     * The select button.
     */
    private final TethysScrollButtonManager<MoneyWiseStaticTable<?, ?, ?>> theSelectButton;

    /**
     * Data menu builder.
     */
    private final TethysScrollMenu<MoneyWiseStaticTable<?, ?, ?>> theDataMenu;

    /**
     * The table card panel.
     */
    private final TethysCardPaneManager<MoneyWiseStaticTable<?, ?, ?>> theTableCard;

    /**
     * The new card panel.
     */
    private final TethysCardPaneManager<TethysScrollButtonManager<?>> theNewCard;

    /**
     * The list of panels.
     */
    private final List<MoneyWiseStaticTable<?, ?, ?>> thePanels;

    /**
     * Constructor.
     *
     * @param pView the view
     */
    public MoneyWiseStaticPanel(final MoneyWiseView pView) {
        /* Store parameters */
        theView = pView;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);

        /* Create the top level viewer entry for this view */
        theViewerEntry = pView.getViewerEntry(PrometheusViewerEntryId.STATIC);
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Create the error panel */
        theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerEntry);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPanel());

        /* Create the list of panels */
        thePanels = new ArrayList<>();

        /* Access the Gui Factory */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();

        /* Create the action buttons panel */
        theActionButtons = new PrometheusActionButtons(myGuiFactory, theUpdateSet);
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);

        /* Create the CheckBox */
        theDisabledCheckBox = myGuiFactory.newCheckBox(MoneyWiseUIResource.STATICDATA_DISABLED.getValue());
        theDisabledCheckBox.getEventRegistrar().addEventListener(e -> showDisabled(theDisabledCheckBox.isSelected()));

        /* Create the select button */
        final TethysLabel myLabel = myGuiFactory.newLabel(MoneyWiseUIResource.STATICDATA_SELECT.getValue());
        theSelectButton = myGuiFactory.newScrollButton();
        theDataMenu = theSelectButton.getMenu();
        theSelectButton.setMenuConfigurator(e -> buildDataMenu());
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handlePanelSelection());

        /* Create the selection panel */
        theSelectionPanel = myGuiFactory.newBorderPane();
        theSelectionPanel.setBorderTitle(MoneyWiseUIResource.STATICDATA_SELECTION.getValue());

        /* Create the new card panel */
        theNewCard = myGuiFactory.newCardPane();
        theNewCard.setVisible(false);

        /* Create the layout for the selection panel */
        final TethysBoxPaneManager mySubPanel = myGuiFactory.newHBoxPane();
        mySubPanel.addNode(myLabel);
        mySubPanel.addNode(theSelectButton);
        mySubPanel.addSpacer();
        mySubPanel.addNode(theDisabledCheckBox);
        mySubPanel.addSpacer();
        theSelectionPanel.setCentre(mySubPanel);
        theSelectionPanel.setEast(theNewCard);

        /* Create the header panel */
        final TethysBorderPaneManager myHeader = myGuiFactory.newBorderPane();
        myHeader.setCentre(theSelectionPanel);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Create the table card panel */
        theTableCard = myGuiFactory.newCardPane();

        /* Create the Panel */
        thePanel = myGuiFactory.newBorderPane();

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(theTableCard);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Add the static data */
        addStatic(MoneyWiseDataType.DEPOSITTYPE, DepositCategoryTypeList.class);
        addStatic(MoneyWiseDataType.CASHTYPE, CashCategoryTypeList.class);
        addStatic(MoneyWiseDataType.LOANTYPE, LoanCategoryTypeList.class);
        addStatic(MoneyWiseDataType.PAYEETYPE, PayeeTypeList.class);
        addStatic(MoneyWiseDataType.PORTFOLIOTYPE, PortfolioTypeList.class);
        addStatic(MoneyWiseDataType.SECURITYTYPE, SecurityTypeList.class);
        addStatic(MoneyWiseDataType.TRANSTYPE, TransactionCategoryTypeList.class);
        addStatic(MoneyWiseDataType.CURRENCY, AssetCurrencyList.class);
        addStatic(MoneyWiseDataType.TAXBASIS, TaxBasisList.class);
        addStatic(MoneyWiseDataType.FREQUENCY, FrequencyList.class);
        addStatic(MoneyWiseDataType.ACCOUNTINFOTYPE, AccountInfoTypeList.class);
        addStatic(MoneyWiseDataType.TRANSINFOTYPE, TransactionInfoTypeList.class);
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
    public TethysNode getNode() {
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
        return hasUpdates() || isEditing();
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
     * Refresh views/controls after a load/update of underlying data.
     * @throws OceanusException on error
     */
    public void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("StaticData");

        /* Loop through the panels */
        for (MoneyWiseStaticTable<?, ?, ?> myPanel : thePanels) {
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
     * Build StaticData menu.
     */
    private void buildDataMenu() {
        /* Cancel any editing */
        cancelEditing();

        /* Reset the popUp menu */
        theDataMenu.removeAllItems();

        /* Record active item */
        TethysScrollMenuItem<MoneyWiseStaticTable<?, ?, ?>> myActive = null;
        final String myActiveName = theTableCard.getActiveName();

        /* Loop through the panels */
        for (MoneyWiseStaticTable<?, ?, ?> myTable : thePanels) {
            /* Create a new MenuItem and add it to the popUp */
            final String myName = myTable.getItemType().getFieldName();
            final TethysScrollMenuItem<MoneyWiseStaticTable<?, ?, ?>> myItem = theDataMenu.addItem(myTable, myName);

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
     * Add static panel.
     * @param pItemType the type of the list
     * @param pListClass the class of the list
     * @param <L> the list type
     * @param <S> the static class
     * @param <T> the data type
     */
    private <L extends StaticList<T, S, MoneyWiseDataType>, T extends StaticData<T, S, MoneyWiseDataType>, S extends Enum<S> & StaticInterface> void addStatic(final MoneyWiseDataType pItemType,
                                                                                                                                                               final Class<L> pListClass) {
        /* Create the new panel */
        final MoneyWiseStaticTable<L, T, S> myPanel = new MoneyWiseStaticTable<>(theView, theUpdateSet, theError, pItemType, pListClass);

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
     * Select static data.
     * @param pStatic the static data to select
     */
    public void selectStatic(final StaticData<?, ?, MoneyWiseDataType> pStatic) {
        /* Access the item type */
        final MoneyWiseDataType myType = pStatic.getItemType();
        final String myName = myType.getFieldName();

        /* Access the panel */
        if (theTableCard.selectCard(myName)) {
            /* Update selection */
            final MoneyWiseStaticTable<?, ?, ?> myPanel = theTableCard.getActiveCard();
            theSelectButton.setValue(myPanel, myName);
            myPanel.selectStatic(pStatic);
            setSelection(myName);
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
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        final MoneyWiseStaticTable<?, ?, ?> myPanel = theTableCard.getActiveCard();
        if (myPanel != null) {
            myPanel.determineFocus(theViewerEntry);
        }
    }

    /**
     * Show New button.
     */
    private void showNewButton() {
        /* Set visibility of New Button */
        final MoneyWiseStaticTable<?, ?, ?> myPanel = theSelectButton.getValue();
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
        final MoneyWiseStaticTable<?, ?, ?> myPanel = theSelectButton.getValue();
        final String myName = myPanel.getItemType().getFieldName();
        theTableCard.selectCard(myName);
        theNewCard.selectCard(myName);
        myPanel.determineFocus(theViewerEntry);
        showNewButton();
    }

    /**
     * Show disabled.
     * @param pShow true/false
     */
    public void showDisabled(final boolean pShow) {
        /* Loop through the panels */
        for (MoneyWiseStaticTable<?, ?, ?> myPanel : thePanels) {
            myPanel.setShowAll(pShow);
        }
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Loop through the panels */
        for (MoneyWiseStaticTable<?, ?, ?> myPanel : thePanels) {
            myPanel.cancelEditing();
        }
    }

    /**
     * is the current table editing?
     * @return true/false
     */
    private boolean isEditing() {
        final MoneyWiseStaticTable<?, ?, ?> myPanel = theSelectButton.getValue();
        return myPanel.isEditing();
    }
}
