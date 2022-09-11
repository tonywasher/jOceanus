/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.base;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnCellCommit;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;

/**
 * MoneyWise Base Table.
 * @param <T> the data type
 */
public abstract class MoneyWiseBaseTable<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>>
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * The logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseBaseTable.class);

    /**
     * Panel height.
     */
    protected static final int HEIGHT_PANEL = 200;

    /**
     * Panel width.
     */
    protected static final int WIDTH_PANEL = 1100;

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
    private final MoneyWiseDataType theItemType;

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
    private final UpdateEntry<T, MoneyWiseDataType> theUpdateEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The underlying table.
     */
    private final TethysTableManager<PrometheusDataFieldId, T> theTable;

    /**
     * is the table editing?
     */
    private boolean isEditing;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     * @param pDataType the dataType
     */
    protected MoneyWiseBaseTable(final MoneyWiseView pView,
                                 final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                 final MetisErrorPanel pError,
                                 final MoneyWiseDataType pDataType) {
        /* Store parameters */
        theView = pView;
        theError = pError;
        theItemType = pDataType;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(pDataType);

        /* Create the panel */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
        thePanel = myGuiFactory.newBorderPane();

        /* Create the table */
        theTable = myGuiFactory.newTable();
        thePanel.setCentre(theTable);

        /* Set table configuration */
        theTable.setOnCommitError(this::setError)
                .setOnValidateError(this::showValidateError)
                .setOnCellEditState(this::handleEditState)
                .setChanged(this::isFieldChanged)
                .setError(this::isFieldInError)
                .setFilter(this::isFiltered)
                .setRepaintRowOnCommit(true)
                .setEditable(true);

        /* Set standard size */
        theTable.setPreferredWidthAndHeight(WIDTH_PANEL, HEIGHT_PANEL);

        /* Add listeners */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
    }

    /**
     * Declare item panel.
     * @param pPanel the item panel
     */
    protected void declareItemPanel(final MoneyWiseItemPanel<T> pPanel) {
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(TethysSwingNode.getComponent(pPanel), BorderLayout.CENTER);
        thePanel.setSouth(myPanel);
        pPanel.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
    }

    /**
     * Declare item panel.
     * @param pPanel the item panel
     */
    protected void declareItemPanel(final MoneyWiseNewItemPanel<T> pPanel) {
        thePanel.setSouth(pPanel);
        pPanel.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
        pPanel.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> {
            setTableEnabled(!pPanel.isEditing());
        });
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

    /**
     * Set the table enabled status.
     * @param pEnabled true/false
     */
    public void setTableEnabled(final boolean pEnabled) {
        theTable.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Obtain the item type.
     * @return the item type
     */
    public MoneyWiseDataType getItemType() {
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
    protected TethysTableManager<PrometheusDataFieldId, T> getTable() {
        return theTable;
    }

    /**
     * Obtain the updateSet.
     * @return the set
     */
    protected UpdateSet<MoneyWiseDataType> getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Obtain the updateEntry.
     * @return the entry
     */
    protected UpdateEntry<T, MoneyWiseDataType> getUpdateEntry() {
        return theUpdateEntry;
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
        pEntry.setFocus(theUpdateEntry.getName());
    }

    /**
     * Handle updateSet rewind.
     */
    protected void handleRewind() {
        theTable.fireTableDataChanged();
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
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    protected <V> void updateField(final TethysOnCellCommit<T, V> pOnCommit,
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
        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
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
    private boolean isFieldInError(final PrometheusDataFieldId pField,
                                   final T pItem) {
        final MetisLetheField myField = pField.getLetheField();
        return myField != null && pItem.getFieldErrors(myField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final PrometheusDataFieldId pField,
                                   final T pItem) {
        final MetisLetheField myField = pField.getLetheField();
        return myField != null && !pItem.isHeader() && pItem.fieldChanged(myField).isDifferent();
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
    protected String isValidName(final String pNewName,
                                 final T pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!DataItem.validString(pNewName, getInvalidNameChars())) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (DataItem.byteLength(pNewName) > DataItem.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        final Iterator<T> myIterator = theTable.itemIterator();
        while (myIterator.hasNext()) {
            final T myValue = myIterator.next();

            /* Ignore self and deleted */
            if (!(myValue instanceof MetisDataNamedItem)
                    || myValue.isDeleted()
                    || myValue.equals(pRow)) {
                continue;
            }

            /* Check for duplicate */
            final MetisDataNamedItem myNamed = (MetisDataNamedItem) myValue;
            if (pNewName.equals(myNamed.getName())) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
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
                && DataItem.byteLength(pNewDesc) > DataItem.DESCLEN) {
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
        isEditing = pState;
        notifyChanges();
    }

    /**
     * build CSV representation of Model.
     * @return the CSV text
     */
    private String createCSV() {
        /* Create the stringBuilder */
        final TethysTableManager<PrometheusDataFieldId, T> myTable = getTable();
        final StringBuilder myBuilder = new StringBuilder();

        /* Loop through the columns */
        Iterator<PrometheusDataFieldId> myColIterator = myTable.columnIterator();
        boolean bDoneFirst = false;
        while (myColIterator.hasNext()) {
            final PrometheusDataFieldId myColId = myColIterator.next();
            final TethysTableColumn<?, PrometheusDataFieldId, T> myCol = myTable.getColumn(myColId);

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
                final PrometheusDataFieldId myColId = myColIterator.next();
                final TethysTableColumn<?, PrometheusDataFieldId, T> myCol = myTable.getColumn(myColId);

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
    public void writeCSVToFile(final TethysGuiFactory pFactory) {
        try {
            /* Create a file selector */
            final TethysFileSelector mySelector = pFactory.newFileSelector();

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
            throw new TethysDataException("Failed to output CSV", e);
        }
    }
}
