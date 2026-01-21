/*
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.data.MetisDataState;
import io.github.tonywasher.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.exc.PrometheusIOException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Database Table class. This controls should be extended for each DataType/Table.
 *
 * @param <T> the DataType
 */
public abstract class PrometheusTableDataItem<T extends PrometheusDataItem> {
    /**
     * The Database control.
     */
    private final PrometheusDataStore theDatabase;

    /**
     * The Database connection.
     */
    private final Connection theConn;

    /**
     * The list of items for this table.
     */
    private PrometheusDataList<T> theList;

    /**
     * The prepared statement.
     */
    private PreparedStatement theStmt;

    /**
     * Do we have batched updated in the prepared statement?
     */
    private boolean hasBatchedUpdates;

    /**
     * The result set.
     */
    private ResultSet theResults;

    /**
     * The table definition.
     */
    private final PrometheusTableDefinition theTable;

    /**
     * Constructor.
     *
     * @param pDatabase the database control
     * @param pTable    the table name
     */
    protected PrometheusTableDataItem(final PrometheusDataStore pDatabase,
                                      final String pTable) {
        /* Set the table */
        theDatabase = pDatabase;
        theConn = theDatabase.getConn();
        theTable = new PrometheusTableDefinition(theDatabase.getDriver(), pTable);
    }

    /**
     * Obtain database.
     *
     * @return the database
     */
    protected PrometheusDataStore getDatabase() {
        return theDatabase;
    }

    /**
     * Obtain table definition.
     *
     * @return the table definition
     */
    protected PrometheusTableDefinition getTableDef() {
        return theTable;
    }

    /**
     * Access the table name.
     *
     * @return the table name
     */
    protected String getTableName() {
        return theTable.getTableName();
    }

    /**
     * Access the table definition.
     *
     * @return the table definition
     */
    protected PrometheusTableDefinition getDefinition() {
        return theTable;
    }

    /**
     * Close the result set and statement.
     *
     * @throws SQLException on error
     */
    protected void closeStmt() throws SQLException {
        executeBatch();
        theTable.clearValues();
        if (theResults != null) {
            theResults.close();
        }
        if (theStmt != null) {
            theStmt.close();
        }
    }

    /**
     * Shift to next line in result set.
     *
     * @return is there a next line
     * @throws SQLException on error
     */
    private boolean nextLine() throws SQLException {
        return theResults.next();
    }

    /**
     * Prepare the statement.
     *
     * @param pStatement the statement to prepare
     * @throws SQLException on error
     */
    private void prepareStatement(final String pStatement) throws SQLException {
        theStmt = theConn.prepareStatement(pStatement);
    }

    /**
     * Execute the prepared statement.
     *
     * @throws SQLException on error
     */
    private void execute() throws SQLException {
        theStmt.executeUpdate();
        theTable.clearValues();
    }

    /**
     * Add to the batched statement.
     *
     * @throws SQLException on error
     */
    private void addToBatch() throws SQLException {
        theStmt.addBatch();
        theTable.clearValues();
        hasBatchedUpdates = true;
    }

    /**
     * Execute the batched statement.
     *
     * @throws SQLException on error
     */
    private void executeBatch() throws SQLException {
        if (hasBatchedUpdates) {
            theStmt.executeBatch();
            hasBatchedUpdates = false;
        }
    }

    /**
     * Query the prepared statement.
     *
     * @throws SQLException on error
     */
    private void executeQuery() throws SQLException {
        theTable.clearValues();
        theResults = theStmt.executeQuery();
    }

    /**
     * Commit the update.
     *
     * @throws SQLException on error
     */
    private void commit() throws SQLException {
        theConn.commit();
    }

    /**
     * Execute a statement.
     *
     * @param pStatement the statement
     * @throws SQLException on error
     */
    private void executeStatement(final String pStatement) throws SQLException {
        /* Prepare the statement */
        prepareStatement(pStatement);

        /* Execute the delete */
        execute();
        commit();

        /* Close the Statement */
        closeStmt();
    }

    /**
     * Count the number of items to be loaded.
     *
     * @return the count of items
     * @throws SQLException on error
     */
    protected int countLoadItems() throws SQLException {
        int myCount = 0;
        final String myString = theTable.getCountString();
        prepareStatement(myString);
        executeQuery();

        /* Loop through the results */
        while (theResults.next()) {
            /* Get the count */
            myCount = theResults.getInt(1);
        }

        /* Close the Statement */
        closeStmt();

        /* Return the count */
        return myCount;
    }

    /**
     * Declare DataSet.
     *
     * @param pData the Data set
     */
    protected abstract void declareData(PrometheusDataSet pData);

    /**
     * Set the list of items.
     *
     * @param pList the list of items
     */
    protected void setList(final PrometheusDataList<T> pList) {
        theList = pList;
    }

    /**
     * Obtain the list of items.
     *
     * @return the list of items
     */
    protected PrometheusDataList<T> getList() {
        return theList;
    }

    /**
     * Load an individual item from the result set.
     *
     * @return the values for the row
     * @throws OceanusException on error
     */
    protected abstract PrometheusDataValues loadValues() throws OceanusException;

    /**
     * Set a field value for an item.
     *
     * @param pItem  the item to insert
     * @param pField the field id
     * @throws OceanusException on error
     */
    protected void setFieldValue(final T pItem,
                                 final MetisDataFieldId pField) throws OceanusException {
        /* Switch on field id */
        if (pField.equals(MetisDataResource.DATA_ID)) {
            theTable.setIntegerValue(MetisDataResource.DATA_ID, pItem.getIndexedId());
        }
    }

    /**
     * Post-Process on a load operation.
     *
     * @throws OceanusException on error
     */
    protected void postProcessOnLoad() throws OceanusException {
        /* PostProcess the list */
        theList.postProcessOnLoad();
    }

    /**
     * Load items from the list into the table.
     *
     * @param pReport the report
     * @param pData   the data
     * @throws OceanusException on error
     */
    protected void loadItems(final TethysUIThreadStatusReport pReport,
                             final PrometheusDataSet pData) throws OceanusException {
        /* Declare the new stage */
        pReport.setNewStage(getTableName());

        /* Declare the Data */
        declareData(pData);

        /* Protect the load */
        try {
            /* Count the Items to be loaded */
            pReport.setNumSteps(countLoadItems());

            /* Load the items from the table */
            final String myQuery = theTable.getLoadString();
            prepareStatement(myQuery);
            executeQuery();

            /* Loop through the results */
            while (nextLine()) {
                /* Read in the results */
                theTable.loadResults(theResults);

                /* Load the next item */
                theList.addValuesItem(loadValues());

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Close the Statement */
            closeStmt();

            /* Perform post process */
            postProcessOnLoad();

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to load " + getTableName(), e);
        }
    }

    /**
     * Determine the count of items that are in a particular state.
     *
     * @param pState the particular state
     * @return the count of items
     */
    private int countStateItems(final MetisDataState pState) {
        /* Initialise the count */
        int iCount = 0;

        /* Loop through the list */
        final Iterator<T> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* Ignore items that are not this type */
            if (myCurr.getState() != pState) {
                continue;
            }

            /* Increment count */
            ++iCount;
        }

        /* Return count */
        return iCount;
    }

    /**
     * Insert new items from the list.
     *
     * @param pReport the report
     * @param pData   the data
     * @param pBatch  the batch control
     * @throws OceanusException on error
     */
    protected void insertItems(final TethysUIThreadStatusReport pReport,
                               final PrometheusDataSet pData,
                               final PrometheusBatchControl pBatch) throws OceanusException {
        /* Declare the new stage */
        pReport.setNewStage("Inserting " + getTableName());

        /* Declare the Data */
        declareData(pData);

        /* Protect the insert */
        T myCurr = null;
        try {
            /* Declare the number of steps */
            final int mySteps = countStateItems(MetisDataState.NEW);
            pReport.setNumSteps(mySteps);
            if (mySteps == 0) {
                return;
            }

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, MetisDataState.NEW);

            /* Prepare the insert statement */
            final String myInsert = theTable.getInsertString();
            prepareStatement(myInsert);

            /* Loop through the list */
            final Iterator<T> myIterator = theList.iterator();
            while (myIterator.hasNext()) {
                /* Ignore non-new items */
                myCurr = myIterator.next();
                if (myCurr.getState() != MetisDataState.NEW) {
                    continue;
                }

                /* Loop through the columns */
                for (PrometheusColumnDefinition myCol : theTable.getColumns()) {
                    /* Access the column id */
                    final MetisDataFieldId iField = myCol.getColumnId();

                    /* Set the field value */
                    setFieldValue(myCurr, iField);
                }

                /* Apply the values */
                theTable.insertValues(theStmt);
                pBatch.addBatchItem();

                /* Add to the statement batch */
                addToBatch();
                myCurr = null;

                /* If we have no further space in the batch */
                if (pBatch.isFull()) {
                    /* ExecuteBatch and commit the database */
                    executeBatch();
                    commit();

                    /* Commit the batch */
                    pBatch.commitItems();
                }

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Close the Statement */
            closeStmt();

        } catch (SQLException e) {
            throw new PrometheusDataException(myCurr, "Failed to insert " + getTableName(), e);
        }
    }

    /**
     * Update items from the list.
     *
     * @param pReport the report
     * @param pBatch  the batch control
     * @throws OceanusException on error
     */
    protected void updateItems(final TethysUIThreadStatusReport pReport,
                               final PrometheusBatchControl pBatch) throws OceanusException {
        /* Declare the new stage */
        pReport.setNewStage("Updating " + getTableName());

        /* Protect the update */
        T myCurr = null;
        try {
            /* Declare the number of steps */
            final int mySteps = countStateItems(MetisDataState.CHANGED);
            pReport.setNumSteps(mySteps);
            if (mySteps == 0) {
                return;
            }

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, MetisDataState.CHANGED);

            /* Loop through the list */
            final Iterator<T> myIterator = theList.iterator();
            while (myIterator.hasNext()) {
                /* Ignore non-changed items */
                myCurr = myIterator.next();
                if ((myCurr.getState() != MetisDataState.CHANGED)
                        || !updateItem(myCurr)) {
                    continue;
                }

                /* Record the id and access the update string */
                theTable.setIntegerValue(MetisDataResource.DATA_ID, myCurr.getIndexedId());
                final String myUpdate = theTable.getUpdateString();

                /* Prepare the statement and declare values */
                prepareStatement(myUpdate);
                theTable.updateValues(theStmt);
                pBatch.addBatchItem();

                /* Execute the update */
                execute();
                myCurr = null;

                /* Close the Statement */
                closeStmt();

                /* If we have no further space in the batch */
                if (pBatch.isFull()) {
                    /* Commit the database */
                    commit();

                    /* Commit the batch */
                    pBatch.commitItems();
                }

                /* Report the progress */
                pReport.setNextStep();
            }
        } catch (SQLException e) {
            throw new PrometheusDataException(myCurr, "Failed to update " + getTableName(), e);
        }
    }

    /**
     * Update the item.
     *
     * @param pItem the item
     * @return Continue <code>true/false</code>
     * @throws OceanusException on error
     */
    private boolean updateItem(final T pItem) throws OceanusException {
        /* Access the object and base */
        final MetisFieldVersionValues myCurr = pItem.getValues();
        final MetisFieldVersionValues myBase = pItem.getOriginalValues();
        boolean isUpdated = false;

        /* Loop through the fields */
        for (PrometheusColumnDefinition myCol : theTable.getColumns()) {
            /* Skip null and Id columns */
            if (myCol == null) {
                continue;
            }

            /* Access the column id */
            final MetisDataFieldId iField = myCol.getColumnId();

            /* If the non-Id field has changed */
            if (!MetisDataResource.DATA_ID.equals(myCol.getColumnId())
                    && myCurr.fieldChanged(iField, myBase).isDifferent()) {
                /* Record the change */
                isUpdated = true;
                setFieldValue(pItem, iField);
            }
        }

        /* Return to caller */
        return isUpdated;
    }

    /**
     * Delete items from the list.
     *
     * @param pReport the report
     * @param pBatch  the batch control
     * @throws OceanusException on error
     */
    protected void deleteItems(final TethysUIThreadStatusReport pReport,
                               final PrometheusBatchControl pBatch) throws OceanusException {
        /* Declare the new stage */
        pReport.setNewStage("Deleting " + getTableName());

        /* Protect the delete */
        T myCurr = null;
        try {
            /* Declare the number of steps */
            int mySteps = countStateItems(MetisDataState.DELETED);
            mySteps += countStateItems(MetisDataState.DELNEW);
            pReport.setNumSteps(mySteps);
            if (mySteps == 0) {
                return;
            }

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, MetisDataState.DELETED);

            /* Prepare the delete statement */
            final String myDelete = theTable.getDeleteString();
            prepareStatement(myDelete);

            /* Access the iterator */
            final ListIterator<T> myIterator = theList.listIterator(theList.size());

            /* Loop through the list in reverse order */
            while (myIterator.hasPrevious()) {
                /* Ignore non-deleted items */
                myCurr = myIterator.previous();
                final MetisDataState myState = myCurr.getState();
                if ((myState != MetisDataState.DELETED)
                        && (myState != MetisDataState.DELNEW)) {
                    continue;
                }

                /* Declare the item in the batch */
                pBatch.addBatchItem();

                /* Ignore DelNew items as far as the database is concerned */
                if (myState != MetisDataState.DELNEW) {
                    /* Apply the id */
                    theTable.setIntegerValue(MetisDataResource.DATA_ID, myCurr.getIndexedId());
                    theTable.updateValues(theStmt);

                    /* Add to the statement batch */
                    addToBatch();
                }

                /* If we have no further space in the batch */
                if (pBatch.isFull()) {
                    /* ExecuteBatch and commit the database */
                    executeBatch();
                    commit();

                    /* Commit the batch */
                    pBatch.commitItems();
                }

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Close the Statement */
            closeStmt();

        } catch (SQLException e) {
            throw new PrometheusDataException(myCurr, "Failed to delete " + getTableName(), e);
        }
    }

    /**
     * Create the table.
     *
     * @throws OceanusException on error
     */
    protected void createTable() throws OceanusException {
        /* Protect the create */
        try {
            /* Execute the create index statement */
            String myCreate = theTable.getCreateTableString();
            executeStatement(myCreate);

            /* If the table has an index */
            if (theTable.isIndexed()) {
                /* Prepare the create index statement */
                myCreate = theTable.getCreateIndexString();
                executeStatement(myCreate);
            }

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to create " + getTableName(), e);
        }
    }

    /**
     * Create the database.
     *
     * @param pDatabase the database name
     * @throws OceanusException on error
     */
    void createDatabase(final String pDatabase) throws OceanusException {
        /* Protect the statements */
        try {
            /* Execute the drop and create database statements */
            theConn.setAutoCommit(true);
            final String myDrop = "DROP DATABASE IF EXISTS " + pDatabase;
            prepareStatement(myDrop);
            theStmt.execute();
            theStmt.close();
            final String myCreate = "CREATE DATABASE " + pDatabase;
            prepareStatement(myCreate);
            theStmt.execute();
            theStmt.close();
            theConn.setAutoCommit(false);

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to create database", e);
        }
    }

    /**
     * Drop the table.
     *
     * @throws OceanusException on error
     */
    protected void dropTable() throws OceanusException {
        /* Protect the drop */
        try {
            /* If we should drop the index */
            String myDrop = theTable.getDropIndexString();
            if (myDrop != null) {
                /* Execute the drop index statement */
                executeStatement(myDrop);
            }

            /* Execute the drop table statement */
            myDrop = theTable.getDropTableString();
            executeStatement(myDrop);

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to drop " + getTableName(), e);
        }
    }

    /**
     * Truncate the table.
     *
     * @throws OceanusException on error
     */
    protected void purgeTable() throws OceanusException {
        /* Protect the truncate */
        try {
            /* Execute the purge statement */
            final String myTrunc = theTable.getPurgeString();
            executeStatement(myTrunc);

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to purge " + getTableName(), e);
        }
    }

    /**
     * Obtain row values.
     *
     * @param pName the name of the item
     * @return the row values.
     * @throws OceanusException on error
     */
    protected PrometheusDataValues getRowValues(final String pName) throws OceanusException {
        /* Allocate the values */
        final PrometheusDataValues myValues = new PrometheusDataValues(pName);

        /* Add the id and return the new values */
        myValues.addValue(MetisDataResource.DATA_ID, theTable.getIntegerValue(MetisDataResource.DATA_ID));
        return myValues;
    }
}
