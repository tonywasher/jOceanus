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

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
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
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnCellCommit;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise Payee Table.
 */
public class MoneyWisePayeeTable
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UpdateSet associated with the table.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final UpdateEntry<Payee, MoneyWiseDataType> theUpdateEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The locked check box.
     */
    private final TethysCheckBox theLockedCheckBox;

    /**
     * The underlying table.
     */
    private final TethysSwingTableManager<MetisLetheField, Payee> theTable;

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
        theView = pView;
        theError = pError;

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) theView.getToolkit()).getFieldManager();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.PAYEE);

        /* Create the panel */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        thePanel = myGuiFactory.newBorderPane();

        /* Create the table */
        theTable = myGuiFactory.newTable();
        thePanel.setCentre(theTable);

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
        theActivePayee = new PayeePanel(myGuiFactory, myFieldMgr, theUpdateSet, theError);
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(TethysSwingNode.getComponent(theActivePayee), BorderLayout.CENTER);
        thePanel.setSouth(myPanel);

        /* Set table configuration */
        theTable.setOnCommitError(this::setError)
                .setOnValidateError(this::showValidateError)
                .setOnCellEditState(this::handleEditState)
                .setDisabled(Payee::isDisabled)
                .setChanged(this::isFieldChanged)
                .setError(this::isFieldInError)
                .setComparator(Payee::compareTo)
                .setFilter(this::isFiltered)
                .setEditable(true)
                .setOnSelect(theActivePayee::setItem);

        /* Create the name column */
        theTable.declareStringColumn(Payee.FIELD_NAME)
                .setValidator(this::isValidName)
                .setCellValueFactory(Payee::getName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Payee::setName, r, v));

        /* Create the payee type column */
        theTable.declareScrollColumn(Payee.FIELD_PAYEETYPE, PayeeType.class)
                .setMenuConfigurator(this::buildPayeeTypeMenu)
                .setCellValueFactory(Payee::getPayeeType)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(Payee::setPayeeType, r, v));

        /* Create the description column */
        theTable.declareStringColumn(Payee.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(Payee::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(Payee::setDescription, r, v));

        /* Create the Closed column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myClosedMapSets = MoneyWiseIcon.configureLockedIconButton();
        theClosedColumn = theTable.declareIconColumn(Payee.FIELD_CLOSED, Boolean.class)
                .setIconMapSet(r -> myClosedMapSets.get(determineClosedState(r)))
                .setCellValueFactory(Payee::isClosed)
                .setEditable(true)
                .setCellEditable(this::determineClosedState)
                .setOnCommit((r, v) -> updateField(Payee::setClosed, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        theTable.declareIconColumn(Payee.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Create the latest event column */
        theTable.declareDateColumn(Payee.FIELD_EVTLAST)
                .setCellValueFactory(this::getLatestTranDate)
                .setName(MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue())
                .setEditable(false);

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theLockedCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theLockedCheckBox.isSelected()));
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActivePayee.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActivePayee.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
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

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActivePayee.isEditing();
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
        theTable.setFilter(this::isFiltered);
        theClosedColumn.setVisible(pShowAll);
    }

    /**
     * Refresh data.
     */
    void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Payees");

        /* Access list */
        final MoneyWiseData myData = theView.getData();
        final PayeeList myBase = myData.getDataList(PayeeList.class);
        thePayees = (PayeeList) myBase.deriveList(ListStyle.EDIT);
        thePayees.mapData();
        theTable.setItems(thePayees.getUnderlyingList());
        theUpdateEntry.setDataList(thePayees);

        /* Notify panel of refresh */
        theActivePayee.refreshData();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Cancel editing.
     */
    void cancelEditing() {
        theTable.cancelEditing();
        theActivePayee.setEditable(false);
    }

    /**
     * Does the panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    /**
     * Does the panel have a session?
     * @return true/false
     */
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
    }

    /**
     * Does the panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        theTable.requestFocus();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
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
            theTable.selectRowWithScroll(pPayee);
            theActivePayee.setItem(pPayee);
        }
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     */
    private void deleteRow(final Payee pRow,
                           final Object pValue) {
        pRow.setDeleted(true);
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActivePayee.isEditing()) {
            /* Handle the reWind */
            theTable.fireTableDataChanged();
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
            theTable.fireTableDataChanged();
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
            theUpdateSet.incrementVersion();

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

    /**
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    private <V> void updateField(final TethysOnCellCommit<Payee, V> pOnCommit,
                                 final Payee pRow,
                                 final V pValue) throws OceanusException {
        /* Push history */
        pRow.pushHistory();

        /* Protect against Exceptions */
        try {
            /* Set the item value */
            pOnCommit.commitCell(pRow, pValue);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Reset values */
            pRow.popHistory();

            /* Throw the error */
            throw new PrometheusDataException("Failed to update field", e);
        }

        /* Check for changes */
        if (pRow.checkForHistory()) {
            /* Increment data version */
            theUpdateSet.incrementVersion();

            /* Update components to reflect changes */
            theTable.fireTableDataChanged();
            notifyChanges();
        }
    }

    /**
     * Set the error.
     * @param pError the error
     */
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Notify that there have been changes to this list.
     */
    protected void notifyChanges() {
        /* Adjust enable of the table */
        theTable.setEnabled(!theActivePayee.isEditing());

        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldInError(final MetisLetheField pField,
                                   final Payee pItem) {
        return pItem.getFieldErrors(pField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final MetisLetheField pField,
                                   final Payee pItem) {
        return pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * isFiltered?
     * @param pRow the row
     * @return true/false
     */
    private boolean isFiltered(final Payee pRow) {
        /* Handle filter */
        return doShowClosed || !pRow.isDisabled();
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    private String isValidName(final String pNewName,
                               final Payee pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!DataItem.validString(pNewName, ":")) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (DataItem.byteLength(pNewName) > Payee.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        for (Payee myValue : thePayees.getUnderlyingList()) {
            /* Ignore self, deleted and non-siblings */
            if (myValue.isDeleted()
                    || myValue.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            if (pNewName.equals(myValue.getName())) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
    }

    /**
     * is Valid description?
     * @param pNewDesc the new description
     * @param pRow the row
     * @return error message or null
     */
    private String isValidDesc(final String pNewDesc,
                               final Payee pRow) {
        /* Reject description that is too long */
        if (pNewDesc != null
                && DataItem.byteLength(pNewDesc) > Payee.DESCLEN) {
            return "Description too long";
        }

        /* Valid description */
        return null;
    }

    /**
     * Show validation error TODO use panel.
     * @param pError the error message
     */
    private void showValidateError(final String pError) {
        System.out.println(Objects.requireNonNullElse(pError, "Error cleared"));
    }

    /**
     * Check edit state.
     * @param pState the new state
     */
    private void handleEditState(final Boolean pState) {
        notifyChanges();
    }
}

