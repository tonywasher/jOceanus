/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.base;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoSetItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIFileSelector;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUIOnCellCommit;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * MoneyWise Base Table.
 * @param <T> the data type
 */
public abstract class MoneyWiseBaseTable<T extends PrometheusDataItem>
        implements OceanusEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * The logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseBaseTable.class);

    /**
     * Date column standard width.
     */
    protected static final int WIDTH_DATE = 100;

    /**
     * Money column standard width.
     */
    protected static final int WIDTH_MONEY = 100;

    /**
     * Rate column standard width.
     */
    protected static final int WIDTH_RATE = 90;

    /**
     * Price column standard width.
     */
    protected static final int WIDTH_PRICE = 90;

    /**
     * Units column standard width.
     */
    protected static final int WIDTH_UNITS = 90;

    /**
     * Dilution column standard width.
     */
    protected static final int WIDTH_DILUTION = 90;

    /**
     * Name column standard width.
     */
    protected static final int WIDTH_NAME = 130;

    /**
     * Description column standard width.
     */
    protected static final int WIDTH_DESC = 200;

    /**
     * Icon column width.
     */
    protected static final int WIDTH_ICON = 20;

    /**
     * Integer column width.
     */
    protected static final int WIDTH_INT = 30;

    /**
     * Currency column width.
     */
    protected static final int WIDTH_CURR = 50;

    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The ItemType.
     */
    private final MetisListKey theItemType;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The EditSet associated with the table.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The EditEntry.
     */
    private final PrometheusEditEntry<T> theEditEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The underlying table.
     */
    private final TethysUITableManager<MetisDataFieldId, T> theTable;

    /**
     * The selection control.
     */
    private final MoneyWiseTableSelect<T> theSelect;

    /**
     * is the table editing?
     */
    private boolean isEditing;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     * @param pDataType the dataType
     */
    protected MoneyWiseBaseTable(final MoneyWiseView pView,
                                 final PrometheusEditSet pEditSet,
                                 final MetisErrorPanel pError,
                                 final MetisListKey pDataType) {
        /* Store parameters */
        theView = pView;
        theError = pError;
        theItemType = pDataType;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Build the Edit set */
        theEditSet = pEditSet;
        theEditEntry = theEditSet.registerType(pDataType);

        /* Create the panel */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        thePanel = myGuiFactory.paneFactory().newBorderPane();

        /* Create the table */
        theTable = myGuiFactory.tableFactory().newTable();
        thePanel.setCentre(theTable);

        /* Set table configuration */
        theTable.setOnCommitError(this::setError)
                .setOnValidateError(this::showValidateError)
                .setOnCellEditState(this::handleEditState)
                .setChanged(this::isFieldChanged)
                .setError(this::isFieldInError)
                .setFilter(this::isFiltered)
                .setOnSelect(this::selectItem)
                .setRepaintRowOnCommit(true)
                .setEditable(true);

        /* Add listeners */
        theEditSet.getEventRegistrar().addEventListener(e -> handleRewind());

        /* Create the selection control */
        theSelect = new MoneyWiseTableSelect<>(theTable, this::isFiltered);
    }

    /**
     * Declare item panel.
     * @param pPanel the item panel
     */
    protected void declareItemPanel(final MoneyWiseItemPanel<T> pPanel) {
        theSelect.declareItemPanel(pPanel);
        thePanel.setSouth(pPanel);
        pPanel.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
        pPanel.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> setTableEnabled(!pPanel.isEditing()));
        pPanel.setPreferredSize();
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    /**
     * Obtain the error panel.
     * @return the error panel
     */
    public MetisErrorPanel getErrorPanel() {
        return theError;
    }

    /**
     * Set the table enabled status.
     * @param pEnabled true/false
     */
    public void setTableEnabled(final boolean pEnabled) {
        theTable.setEnabled(pEnabled);
    }

    /**
     * Obtain the item type.
     * @return the item type
     */
    public MetisListKey getItemType() {
        return theItemType;
    }

    /**
     * Obtain the view.
     * @return the view
     */
    protected MoneyWiseView getView() {
        return theView;
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected TethysUITableManager<MetisDataFieldId, T> getTable() {
        return theTable;
    }

    /**
     * Obtain the editSet.
     * @return the set
     */
    protected PrometheusEditSet getEditSet() {
        return theEditSet;
    }

    /**
     * Obtain the editEntry.
     * @return the entry
     */
    protected PrometheusEditEntry<T> getEditEntry() {
        return theEditEntry;
    }

    /**
     * Refresh data.
     * @throws OceanusException on error
     */
    protected abstract void refreshData() throws OceanusException;

    /**
     * Cancel editing.
     */
    public void cancelEditing() {
        theTable.cancelEditing();
    }

    /**
     * Is the table editing?
     * @return true/false
     */
    public boolean isEditing() {
        return isEditing;
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    public void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        theTable.requestFocus();

        /* Set the required focus */
        pEntry.setFocus(theEditEntry.getName());
    }

    /**
     * Handle updateSet rewind.
     */
    protected void handleRewind() {
        updateTableData();
    }

    /**
     * Handle updateSet rewind.
     */
    protected void updateTableData() {
        theSelect.updateTableData();
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     * @throws OceanusException on error
     */
    protected void deleteRow(final T pRow,
                             final Object pValue) throws OceanusException {
        pRow.setDeleted(true);
    }

    /**
     * Handle updateSet rewind.
     */
    protected void restoreSelected() {
        theSelect.restoreSelected();
    }

    /**
     * Select an item.
     * @param pItem the item
     */
    protected void selectItem(final T pItem) {
        theSelect.recordSelection(pItem);
    }

    /**
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    protected <V> void updateField(final TethysUIOnCellCommit<T, V> pOnCommit,
                                   final T pRow,
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
            throw new MoneyWiseDataException("Failed to update field", e);
        }

        /* Check for changes */
        if (pRow.checkForHistory()) {
            /* Increment data version */
            theEditSet.incrementVersion();

            /* Update components to reflect changes */
            updateTableData();
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
        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Does the panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return theEditSet.hasUpdates();
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
        return theEditSet.hasErrors();
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return false;
    }

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldInError(final MetisDataFieldId pField,
                                   final T pItem) {
        return pField != null && pItem.getFieldErrors(pField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    public boolean isFieldChanged(final MetisDataFieldId pField,
                                  final T pItem) {
        /* Header is never changed */
        if (pItem.isHeader()) {
            return false;
        }

        /* If the field is a dataInfoClass as part of an infoSetItem */
        if (pField instanceof PrometheusDataInfoClass myClass
                && pItem instanceof PrometheusInfoSetItem myItem) {
            /* Check with the infoSet whether the field has changed */
            return myItem.getInfoSet().fieldChanged(myClass).isDifferent();
        }

        /* Handle standard fields */
        return pField != null
                && pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * isFiltered?
     * @param pRow the row
     * @return true/false
     */
    protected boolean isFiltered(final T pRow) {
        return !pRow.isDeleted();
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    public String isValidName(final String pNewName,
                              final T pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!PrometheusDataItem.validString(pNewName, getInvalidNameChars())) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (PrometheusDataItem.byteLength(pNewName) > PrometheusDataItem.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        final Iterator<PrometheusDataItem> myIterator = nameSpaceIterator();
        while (myIterator.hasNext()) {
            final PrometheusDataItem myValue = myIterator.next();

            /* Ignore self and deleted */
            if (!(myValue instanceof MetisDataNamedItem)
                    || myValue.isDeleted()
                    || myValue.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            final MetisDataNamedItem myNamed = (MetisDataNamedItem) myValue;
            if (isDuplicateName(pNewName, pRow, myNamed)) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
    }

    /**
     * is name a match?
     * @param pNewName the new name
     * @param pRow the row
     * @param pCheck the item to check against
     * @return true/false
     */
    protected boolean isDuplicateName(final String pNewName,
                                      final T pRow,
                                      final MetisDataNamedItem pCheck) {
        /* Check for duplicate */
        return pNewName.equals(pCheck.getName());
    }

    /**
     * Obtain the nameSpace iterator.
      * @return the iterator
     */
    protected Iterator<PrometheusDataItem> nameSpaceIterator() {
        return new MoneyWiseNameSpaceIterator(theEditSet, theItemType);
    }

    /**
     * Obtain the string of illegal name characters.
     * @return the invalid characters
     */
    protected String getInvalidNameChars() {
        return null;
    }

    /**
     * is Valid description?
     * @param pNewDesc the new description
     * @param pRow the row
     * @return error message or null
     */
    protected String isValidDesc(final String pNewDesc,
                                 final T pRow) {
        /* Reject description that is too long */
        if (pNewDesc != null
                && PrometheusDataItem.byteLength(pNewDesc) > PrometheusDataItem.DESCLEN) {
            return "Description too long";
        }

        /* Valid description */
        return null;
    }

    /**
     * Show validation error.
     * @param pError the error message
     */
    public void showValidateError(final String pError) {
        theError.showValidateError(pError);
    }

    /**
     * Check edit state.
     * @param pState the new state
     */
    private void handleEditState(final Boolean pState) {
        isEditing = pState;
        notifyChanges();
    }

    /**
     * build CSV representation of Model.
     * @return the CSV text
     */
    private String createCSV() {
        /* Create the stringBuilder */
        final TethysUITableManager<MetisDataFieldId, T> myTable = getTable();
        final StringBuilder myBuilder = new StringBuilder();

        /* Loop through the columns */
        Iterator<MetisDataFieldId> myColIterator = myTable.columnIterator();
        boolean bDoneFirst = false;
        while (myColIterator.hasNext()) {
            final MetisDataFieldId myColId = myColIterator.next();
            final TethysUITableColumn<?, MetisDataFieldId, T> myCol = myTable.getColumn(myColId);

            /* Add the column name */
            if (bDoneFirst) {
                myBuilder.append(",");
            }
            bDoneFirst = true;
            myBuilder.append(myCol.getName());
        }
        myBuilder.append("\n");

        /* Loop through the rows */
        final Iterator<T> myRowIterator = myTable.viewIterator();
        while (myRowIterator.hasNext()) {
            final T myRow = myRowIterator.next();

            /* Loop through the columns */
            myColIterator = myTable.columnIterator();
            bDoneFirst = false;
            while (myColIterator.hasNext()) {
                final MetisDataFieldId myColId = myColIterator.next();
                final TethysUITableColumn<?, MetisDataFieldId, T> myCol = myTable.getColumn(myColId);

                /* Output the column value */
                final Object myVar = myCol.getValueForRow(myRow);
                if (bDoneFirst) {
                    myBuilder.append(",");
                }
                bDoneFirst = true;
                if (myVar != null) {
                    myBuilder.append(myVar);
                }
            }
            myBuilder.append("\n");
        }

        /* Return the CSV file */
        return myBuilder.toString();
    }

    /**
     * Write CSV to file.
     * @param pFactory the gui factory
     */
    public void writeCSVToFile(final TethysUIFactory<?> pFactory) {
        try {
            /* Create a file selector */
            final TethysUIFileSelector mySelector = pFactory.dialogFactory().newFileSelector();

            /* Select File */
            mySelector.setUseSave(true);
            final File myFile = mySelector.selectFile();
            if (myFile != null) {
                final String myCSV = createCSV();
                writeToFile(myFile, myCSV);
            }

        } catch (OceanusException e) {
            LOGGER.error("Failed to write to file", e);
        }
    }

    /**
     * Write CSV to file.
     * @param pFile the file to write to
     * @param pData the data to write
     * @throws OceanusException on error
     */
    private static void writeToFile(final File pFile,
                                    final String pData) throws OceanusException {
        /* Protect the writeToFile */
        try (PrintWriter myWriter = new PrintWriter(pFile, StandardCharsets.UTF_8)) {
            /* Write data to file */
            myWriter.print(pData);

        } catch (IOException e) {
            throw new MoneyWiseDataException("Failed to output CSV", e);
        }
    }
}
