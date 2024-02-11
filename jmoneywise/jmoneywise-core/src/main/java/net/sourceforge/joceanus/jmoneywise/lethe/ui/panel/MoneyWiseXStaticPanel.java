/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.panel;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
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
import net.sourceforge.joceanus.jmoneywise.atlas.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseXView;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem.StaticList;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusXActionButtons;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;

/**
 * MoneyWise Static Panel.
 */
public class MoneyWiseXStaticPanel
        implements TethysEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * The view.
     */
    private final MoneyWiseXView theView;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UpdateSet associated with the panel.
     */
    private final UpdateSet theUpdateSet;

    /**
     * The ViewerEntry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The Panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The action buttons panel.
     */
    private final PrometheusXActionButtons theActionButtons;

    /**
     * The disabled check box.
     */
    private final TethysUICheckBox theDisabledCheckBox;

    /**
     * The Selection Panel.
     */
    private final TethysUIBorderPaneManager theSelectionPanel;

    /**
     * The select button.
     */
    private final TethysUIScrollButtonManager<TethysUIGenericWrapper> theSelectButton;

    /**
     * Data menu builder.
     */
    private final TethysUIScrollMenu<TethysUIGenericWrapper> theDataMenu;

    /**
     * The table card panel.
     */
    private final TethysUICardPaneManager<MoneyWiseXStaticTable<?, ?>> theTableCard;

    /**
     * The new card panel.
     */
    private final TethysUICardPaneManager<TethysUIScrollButtonManager<?>> theNewCard;

    /**
     * The list of panels.
     */
    private final List<MoneyWiseXStaticTable<?, ?>> thePanels;

    /**
     * Constructor.
     *
     * @param pView the view
     */
    MoneyWiseXStaticPanel(final MoneyWiseXView pView) {
        /* Store parameters */
        theView = pView;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = new UpdateSet(theView);

        /* Create the top level viewer entry for this view */
        theViewerEntry = pView.getViewerEntry(PrometheusViewerEntryId.STATIC);
        theViewerEntry.setTreeObject(theUpdateSet);

        /* Create the error panel */
        theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerEntry);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPanel());

        /* Create the list of panels */
        thePanels = new ArrayList<>();

        /* Access the Gui Factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create the action buttons panel */
        theActionButtons = new PrometheusXActionButtons(myGuiFactory, theUpdateSet);
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);

        /* Create the CheckBox */
        final TethysUIControlFactory myControls = myGuiFactory.controlFactory();
        theDisabledCheckBox = myControls.newCheckBox(MoneyWiseUIResource.STATICDATA_DISABLED.getValue());
        theDisabledCheckBox.getEventRegistrar().addEventListener(e -> showDisabled(theDisabledCheckBox.isSelected()));

        /* Create the select button */
        final TethysUILabel myLabel = myControls.newLabel(MoneyWiseUIResource.STATICDATA_SELECT.getValue());
        theSelectButton = myGuiFactory.buttonFactory().newScrollButton(TethysUIGenericWrapper.class);
        theDataMenu = theSelectButton.getMenu();
        theSelectButton.setMenuConfigurator(e -> buildDataMenu());
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handlePanelSelection());

        /* Create the selection panel */
        final TethysUIPaneFactory myPanes = myGuiFactory.paneFactory();
        theSelectionPanel = myPanes.newBorderPane();
        theSelectionPanel.setBorderTitle(MoneyWiseUIResource.STATICDATA_SELECTION.getValue());

        /* Create the new card panel */
        theNewCard = myPanes.newCardPane();
        theNewCard.setVisible(false);

        /* Create the layout for the selection panel */
        final TethysUIBoxPaneManager mySubPanel = myPanes.newHBoxPane();
        mySubPanel.addNode(myLabel);
        mySubPanel.addNode(theSelectButton);
        mySubPanel.addSpacer();
        mySubPanel.addNode(theDisabledCheckBox);
        mySubPanel.addSpacer();
        theSelectionPanel.setCentre(mySubPanel);
        theSelectionPanel.setEast(theNewCard);

        /* Create the header panel */
        final TethysUIBorderPaneManager myHeader = myPanes.newBorderPane();
        myHeader.setCentre(theSelectionPanel);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Create the table card panel */
        theTableCard = myPanes.newCardPane();

        /* Create the Panel */
        thePanel = myPanes.newBorderPane();

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
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
        TethysProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("StaticData");

        /* Loop through the panels */
        for (MoneyWiseXStaticTable<?, ?> myPanel : thePanels) {
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
        TethysUIScrollItem<TethysUIGenericWrapper> myActive = null;
        final String myActiveName = theTableCard.getActiveName();

        /* Loop through the panels */
        for (MoneyWiseXStaticTable<?, ?> myTable : thePanels) {
            /* Create a new MenuItem and add it to the popUp */
            final String myName = myTable.getItemType().getFieldName();
            final TethysUIScrollItem<TethysUIGenericWrapper> myItem = theDataMenu.addItem(new TethysUIGenericWrapper(myTable), myName);

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
     * @param <T> the data type
     */
    private <L extends StaticList<T>, T extends StaticDataItem> void addStatic(final MoneyWiseDataType pItemType,
                                                                               final Class<L> pListClass) {
        /* Create the new panel */
        final MoneyWiseXStaticTable<L, T> myPanel = new MoneyWiseXStaticTable<>(theView, theUpdateSet, theError, pItemType, pListClass);

        /* Add the listener for the panel */
        myPanel.getEventRegistrar().addEventListener(e -> setVisibility());

        /* Access list name */
        final String myName = pItemType.getFieldName();

        /* Add to the card panels */
        theTableCard.addCard(myName, myPanel);
        theNewCard.addCard(myName, myPanel.getNewButton());

        /* Make sure that the active set is displayed */
        theSelectButton.setValue(new TethysUIGenericWrapper(theTableCard.getActiveCard()), theTableCard.getActiveName());

        /* Add to the List */
        thePanels.add(myPanel);
    }

    /**
     * Select static data.
     * @param pStatic the static data to select
     */
    public void selectStatic(final StaticDataItem pStatic) {
        /* Access the item type */
        final MoneyWiseDataType myType = (MoneyWiseDataType) pStatic.getItemType();
        final String myName = myType.getFieldName();

        /* Access the panel */
        if (theTableCard.selectCard(myName)) {
            /* Update selection */
            final MoneyWiseXStaticTable<?, ?> myPanel = theTableCard.getActiveCard();
            theSelectButton.setValue(new TethysUIGenericWrapper(myPanel), myName);
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
        final MoneyWiseXStaticTable<?, ?> myPanel = theTableCard.getActiveCard();
        if (myPanel != null) {
            myPanel.determineFocus(theViewerEntry);
        }
    }

    /**
     * Show New button.
     */
    private void showNewButton() {
        /* Set visibility of New Button */
        final MoneyWiseXStaticTable<?, ?> myPanel = (MoneyWiseXStaticTable<?, ?>) theSelectButton.getValue().getData();
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
        final MoneyWiseXStaticTable<?, ?> myPanel = (MoneyWiseXStaticTable<?, ?>) theSelectButton.getValue().getData();
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
        for (MoneyWiseXStaticTable<?, ?> myPanel : thePanels) {
            myPanel.setShowAll(pShow);
        }
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Loop through the panels */
        for (MoneyWiseXStaticTable<?, ?> myPanel : thePanels) {
            myPanel.cancelEditing();
        }
    }

    /**
     * is the current table editing?
     * @return true/false
     */
    private boolean isEditing() {
        final MoneyWiseXStaticTable<?, ?> myPanel = (MoneyWiseXStaticTable<?, ?>) theSelectButton.getValue().getData();
        return myPanel.isEditing();
    }
}