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

import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.PayeePanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Payee Table.
 */
public class MoneyWisePayeeTable
        extends MoneyWiseBaseTable<Payee> {
    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The locked check box.
     */
    private final TethysCheckBox theLockedCheckBox;

    /**
     * The closed column.
     */
    private final TethysTableColumn<Boolean, MetisLetheField, Payee> theClosedColumn;

    /**
     * The Payee dialog.
     */
    private final PayeePanel theActivePayee;

    /**
     * The edit list.
     */
    private PayeeList thePayees;

    /**
     * Are we showing closed accounts?
     */
    private boolean doShowClosed;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWisePayeeTable(final MoneyWiseView pView,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.PAYEE);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, Payee> myTable = getTable();

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create the CheckBox */
        theLockedCheckBox = myGuiFactory.newCheckBox(PROMPT_CLOSED);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theLockedCheckBox);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Create a payee panel */
        theActivePayee = new PayeePanel(myGuiFactory, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActivePayee);

        /* Set table configuration */
        myTable.setDisabled(Payee::isDisabled)
               .setComparator(Payee::compareTo)
               .setOnSelect(theActivePayee::setItem);

        /* Create the name column */
        myTable.declareStringColumn(Payee.FIELD_NAME)
               .setValidator(this::isValidName)
               .setCellValueFactory(Payee::getName)
               .setEditable(true)
               .setOnCommit((r, v) -> updateField(Payee::setName, r, v));

        /* Create the payee type column */
        myTable.declareScrollColumn(Payee.FIELD_PAYEETYPE, PayeeType.class)
               .setMenuConfigurator(this::buildPayeeTypeMenu)
               .setCellValueFactory(Payee::getPayeeType)
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setOnCommit((r, v) -> updateField(Payee::setPayeeType, r, v));

        /* Create the description column */
        myTable.declareStringColumn(Payee.FIELD_DESC)
               .setValidator(this::isValidDesc)
               .setCellValueFactory(Payee::getDesc)
               .setEditable(true)
               .setOnCommit((r, v) -> updateField(Payee::setDescription, r, v));

        /* Create the Closed column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton();
        theClosedColumn = myTable.declareIconColumn(Payee.FIELD_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(Payee::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setOnCommit((r, v) -> updateField(Payee::setClosed, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(Payee.FIELD_TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        myTable.declareDateColumn(Payee.FIELD_EVTLAST)
               .setCellValueFactory(this::getLatestTranDate)
               .setName(MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue())
               .setEditable(false);

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theLockedCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theLockedCheckBox.isSelected()));
        theActivePayee.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected boolean isItemEditing() {
        return theActivePayee.isEditing();
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Determine closed state.
     * @param pPayee the payee
     * @return the state
     */
    private boolean determineClosedState(final Payee pPayee) {
        return pPayee.isClosed() || !pPayee.isRelevant();
    }

    /**
     * Obtain the date of the latest transaction.
     * @param pPayee the payee
     * @return the date or null
     */
    private TethysDate getLatestTranDate(final Payee pPayee) {
        final Transaction myTran = pPayee.getLatest();
        return myTran == null ? null : myTran.getDate();
    }

    /**
     * Set the showAll indicator.
     * @param pShowAll show closed accounts?
     */
    private void setShowAll(final boolean pShowAll) {
        doShowClosed = pShowAll;
        cancelEditing();
        getTable().setFilter(this::isFiltered);
        theClosedColumn.setVisible(pShowAll);
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("Payees");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final PayeeList myBase = myData.getDataList(PayeeList.class);
        thePayees = (PayeeList) myBase.deriveList(ListStyle.EDIT);
        thePayees.mapData();
        getTable().setItems(thePayees.getUnderlyingList());
        getUpdateEntry().setDataList(thePayees);

        /* Notify panel of refresh */
        theActivePayee.refreshData();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void cancelEditing() {
        super.cancelEditing();
        theActivePayee.setEditable(false);
    }

    /**
     * Select payee.
     * @param pPayee the payee to select
     */
    void selectPayee(final Payee pPayee) {
        /* If the item is closed, but we are not showing closed items */
        if (pPayee.isClosed()
                && !theLockedCheckBox.isSelected()) {
            theLockedCheckBox.setSelected(true);
            setShowAll(true);
        }

        /* If we are changing the selection */
        final Payee myCurrent = theActivePayee.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pPayee)) {
            /* Select the row and ensure that it is visible */
            getTable().selectRowWithScroll(pPayee);
            theActivePayee.setItem(pPayee);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActivePayee.isEditing()) {
            /* Handle the reWind */
            getTable().fireTableDataChanged();
            selectPayee(theActivePayee.getSelectedItem());
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActivePayee.isEditing()) {
            /* handle the edit transition */
            getTable().fireTableDataChanged();
            selectPayee(theActivePayee.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Build the payee type list for the item.
     * @param pPayee the item
     * @param pMenu the menu to build
     */
    private void buildPayeeTypeMenu(final Payee pPayee,
                                    final TethysScrollMenu<PayeeType> pMenu) {
        /* Build the menu */
        theActivePayee.buildPayeeTypeMenu(pMenu, pPayee);
    }

    /**
     * New item.
     */
    private void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new category */
            final Payee myPayee = thePayees.addNewItem();
            myPayee.setDefaults();

            /* Set as new and adjust map */
            myPayee.setNewVersion();
            myPayee.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myPayee.validate();
            theActivePayee.setNewItem(myPayee);

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new payee", e);

            /* Show the error */
            setError(myError);
        }
    }

    @Override
    protected boolean isFiltered(final Payee pRow) {
        /* Handle filter */
        return super.isFiltered(pRow) && (doShowClosed || !pRow.isDisabled());
    }

    @Override
    protected String getInvalidNameChars() {
        return ":";
    }
}

