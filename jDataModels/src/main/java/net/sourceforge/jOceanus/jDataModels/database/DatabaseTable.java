/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;

/**
 * Database Table class. This controls should be extended for each DataType/Table.
 * @param <T> the DataType
 */
public abstract class DatabaseTable<T extends DataItem & Comparable<? super T>> {
    /**
     * The Database control.
     */
    private final Database<?> theDatabase;

    /**
     * The Database connection.
     */
    private final Connection theConn;

    /**
     * The list of items for this table.
     */
    private DataList<T> theList = null;

    /**
     * The prepared statement.
     */
    private PreparedStatement theStmt = null;

    /**
     * The result set.
     */
    private ResultSet theResults = null;

    /**
     * The table definition.
     */
    private final TableDefinition theTable;

    /**
     * Obtain database.
     * @return the database
     */
    protected Database<?> getDatabase() {
        return theDatabase;
    }

    /**
     * Obtain table definition.
     * @return the table definition
     */
    protected TableDefinition getTableDef() {
        return theTable;
    }

    /**
     * Constructor.
     * @param pDatabase the database control
     * @param pTable the table name
     */
    protected DatabaseTable(final Database<?> pDatabase,
                            final String pTable) {
        /* Set the table */
        theDatabase = pDatabase;
        theConn = theDatabase.getConn();
        theTable = new TableDefinition(theDatabase.getDriver(), pTable);
    }

    /**
     * Access the table name.
     * @return the table name
     */
    protected String getTableName() {
        return theTable.getTableName();
    }

    /**
     * Access the table definition.
     * @return the table definition
     */
    protected TableDefinition getDefinition() {
        return theTable;
    }

    /**
     * Close the result set and statement.
     * @throws SQLException on error
     */
    protected void closeStmt() throws SQLException {
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
     * @return is there a next line
     * @throws SQLException on error
     */
    private boolean next() throws SQLException {
        return theResults.next();
    }

    /**
     * Prepare the statement.
     * @param pStatement the statement to prepare
     * @throws SQLException on error
     */
    private void prepareStatement(final String pStatement) throws SQLException {
        theStmt = theConn.prepareStatement(pStatement);
    }

    /**
     * Execute the prepared statement.
     * @throws SQLException on error
     */
    private void execute() throws SQLException {
        theStmt.executeUpdate();
        theTable.clearValues();
    }

    /**
     * Query the prepared statement.
     * @throws SQLException on error
     */
    private void executeQuery() throws SQLException {
        theTable.clearValues();
        theResults = theStmt.executeQuery();
    }

    /**
     * Commit the update.
     * @throws SQLException on error
     */
    private void commit() throws SQLException {
        theConn.commit();
    }

    /**
     * Execute a statement.
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
     * @return the count of items
     * @throws SQLException on error
     */
    protected int countLoadItems() throws SQLException {
        String myString;
        int myCount = 0;

        myString = theTable.getCountString();
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
     * @param pData the Data set
     */
    protected abstract void declareData(final DataSet<?> pData);

    /**
     * Set the list of items.
     * @param pList the list of items
     */
    protected void setList(final DataList<T> pList) {
        theList = pList;
    }

    /**
     * Obtain the list of items.
     * @return the list of items
     */
    protected DataList<T> getList() {
        return theList;
    }

    /**
     * Load an individual item from the result set.
     * @param pId the id of the item
     * @throws JDataException on error
     */
    protected abstract void loadItem(Integer pId) throws JDataException;

    /**
     * Set a field value for an item.
     * @param pItem the item to insert
     * @param pField the field id
     * @throws JDataException on error
     */
    protected void setFieldValue(final T pItem,
                                 final JDataField pField) throws JDataException {
        /* Switch on field id */
        if (pField == DataItem.FIELD_ID) {
            theTable.setIntegerValue(DataItem.FIELD_ID, pItem.getId());
        }
    }

    /**
     * Post-Process on a load operation.
     * @throws JDataException on error
     */
    protected void postProcessOnLoad() throws JDataException {
    }

    /**
     * Load items from the list into the table.
     * @param pTask the task control
     * @param pData the data
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    protected boolean loadItems(final TaskControl<?> pTask,
                                final DataSet<?> pData) throws JDataException {
        boolean bContinue = true;
        String myQuery;
        int mySteps;
        int myCount = 0;

        /* Declare the new stage */
        if (!pTask.setNewStage(getTableName())) {
            return false;
        }

        /* Access reporting steps */
        mySteps = pTask.getReportingSteps();

        /* Declare the Data */
        declareData(pData);

        /* Protect the load */
        try {
            /* Count the Items to be loaded */
            if (!pTask.setNumSteps(countLoadItems())) {
                return false;
            }

            /* Load the items from the table */
            myQuery = theTable.getLoadString();
            prepareStatement(myQuery);
            executeQuery();

            /* Loop through the results */
            while (next()) {
                /* Read in the results */
                theTable.loadResults(theResults);
                Integer myId = theTable.getIntegerValue(DataItem.FIELD_ID);

                /* Load the next item */
                loadItem(myId);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Close the Statement */
            closeStmt();

            /* Sort the list */
            theList.reSort();

            /* Perform post process */
            postProcessOnLoad();

        } catch (SQLException e) {
            throw new JDataException(ExceptionClass.SQLSERVER, "Failed to load " + getTableName(), e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Determine the count of items that are in a particular state.
     * @param pState the particular state
     * @return the count of items
     */
    private int countStateItems(final DataState pState) {
        /* Access the iterator */
        Iterator<T> myIterator = theList.iterator();
        int iCount = 0;

        /* Loop through the list */
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();

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
     * @param pTask the task control
     * @param pData the data
     * @param pBatch the batch control
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    protected boolean insertItems(final TaskControl<?> pTask,
                                  final DataSet<?> pData,
                                  final BatchControl pBatch) throws JDataException {
        /* Declare the new stage */
        if (!pTask.setNewStage("Inserting " + getTableName())) {
            return false;
        }

        /* Access reporting steps */
        boolean bContinue = true;
        int mySteps = pTask.getReportingSteps();
        int myCount = 0;
        T myCurr = null;

        /* Declare the Data */
        declareData(pData);

        /* Protect the insert */
        try {
            /* Declare the number of steps */
            if (!pTask.setNumSteps(countStateItems(DataState.NEW))) {
                return false;
            }

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, DataState.NEW);

            /* Prepare the insert statement */
            String myInsert = theTable.getInsertString();
            prepareStatement(myInsert);

            /* Access the iterator */
            Iterator<T> myIterator = theList.iterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                /* Ignore non-new items */
                myCurr = myIterator.next();
                if (myCurr.getState() != DataState.NEW) {
                    continue;
                }

                /* Loop through the columns */
                for (ColumnDefinition myCol : theTable.getColumns()) {
                    /* Access the column id */
                    JDataField iField = myCol.getColumnId();

                    /* Set the field value */
                    setFieldValue(myCurr, iField);
                }

                /* Apply the values */
                theTable.insertValues(theStmt);
                pBatch.addBatchItem();

                /* Execute the insert */
                execute();
                myCurr = null;

                /* If we have no further space in the batch */
                if (pBatch.isFull()) {
                    /* Commit the database */
                    commit();

                    /* Commit the batch */
                    pBatch.commitItems();
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Close the Statement */
            closeStmt();

        } catch (SQLException e) {
            theDatabase.close();
            throw new JDataException(ExceptionClass.SQLSERVER, myCurr, "Failed to insert " + getTableName(),
                    e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Update items from the list.
     * @param pTask the task control
     * @param pBatch the batch control
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    protected boolean updateItems(final TaskControl<?> pTask,
                                  final BatchControl pBatch) throws JDataException {
        /* Declare the new stage */
        if (!pTask.setNewStage("Updating " + getTableName())) {
            return false;
        }

        /* Access reporting steps */
        boolean bContinue = true;
        int mySteps = pTask.getReportingSteps();
        int myCount = 0;
        T myCurr = null;

        /* Protect the update */
        try {
            /* Declare the number of steps */
            if (!pTask.setNumSteps(countStateItems(DataState.CHANGED))) {
                return false;
            }

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, DataState.CHANGED);

            /* Access the iterator */
            Iterator<T> myIterator = theList.iterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                /* Ignore non-changed items */
                myCurr = myIterator.next();
                if (myCurr.getState() != DataState.CHANGED) {
                    continue;
                }

                /* Update the item */
                if (updateItem(myCurr)) {
                    /* Record the id and access the update string */
                    theTable.setIntegerValue(DataItem.FIELD_ID, myCurr.getId());
                    String myUpdate = theTable.getUpdateString();

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
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            theDatabase.close();
            throw new JDataException(ExceptionClass.SQLSERVER, myCurr, "Failed to update " + getTableName(),
                    e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Update the item.
     * @param pItem the item
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    private boolean updateItem(final T pItem) throws JDataException {
        ValueSet myCurr;
        ValueSet myBase;
        boolean isUpdated = false;

        /* Access the object and base */
        myCurr = pItem.getValueSet();
        myBase = pItem.getOriginalValues();

        /* Loop through the fields */
        for (ColumnDefinition myCol : theTable.getColumns()) {
            /* Skip null columns */
            if (myCol == null) {
                continue;
            }

            /* Access the column id */
            JDataField iField = myCol.getColumnId();

            /* Ignore ID column */
            if (DataItem.FIELD_ID.equals(iField)) {
                continue;
            }

            /* If the field has changed */
            if (myCurr.fieldChanged(iField, myBase).isDifferent()) {
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
     * @param pTask the task control
     * @param pBatch the batch control
     * @return Continue <code>true/false</code>
     * @throws JDataException on error
     */
    protected boolean deleteItems(final TaskControl<?> pTask,
                                  final BatchControl pBatch) throws JDataException {
        /* Declare the new stage */
        if (!pTask.setNewStage("Deleting " + getTableName())) {
            return false;
        }

        /* Access reporting steps */
        boolean bContinue = true;
        int mySteps = pTask.getReportingSteps();
        int myCount = 0;
        T myCurr = null;

        /* Protect the delete */
        try {
            /* Declare the number of steps */
            if (!pTask.setNumSteps(countStateItems(DataState.DELETED))) {
                return false;
            }

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, DataState.DELETED);

            /* Prepare the delete statement */
            String myDelete = theTable.getDeleteString();
            prepareStatement(myDelete);

            /* Access the iterator */
            ListIterator<T> myIterator = theList.listIterator();

            /* Loop through the list in reverse order */
            while (myIterator.hasPrevious()) {
                /* Ignore non-deleted items */
                myCurr = myIterator.previous();
                if (myCurr.getState() != DataState.DELETED) {
                    continue;
                }

                /* Declare the item in the batch */
                pBatch.addBatchItem();

                /* Ignore DelNew items as far as the database is concerned */
                if (myCurr.getBase().getState() != DataState.DELNEW) {
                    /* Apply the id */
                    theTable.setIntegerValue(DataItem.FIELD_ID, myCurr.getId());
                    theTable.updateValues(theStmt);

                    /* Execute the delete */
                    execute();
                    myCurr = null;
                }

                /* If we have no further space in the batch */
                if (pBatch.isFull()) {
                    /* Commit the database */
                    commit();

                    /* Commit the batch */
                    pBatch.commitItems();
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Close the Statement */
            closeStmt();
        } catch (SQLException e) {
            theDatabase.close();
            throw new JDataException(ExceptionClass.SQLSERVER, myCurr, "Failed to delete " + getTableName(),
                    e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Create the table.
     * @throws JDataException on error
     */
    protected void createTable() throws JDataException {
        String myCreate;

        /* Protect the create */
        try {
            /* Execute the create index statement */
            myCreate = theTable.getCreateTableString();
            executeStatement(myCreate);

            /* If the table has an index */
            if (theTable.isIndexed()) {
                /* Prepare the create index statement */
                myCreate = theTable.getCreateIndexString();
                executeStatement(myCreate);
            }
        } catch (SQLException e) {
            theDatabase.close();
            throw new JDataException(ExceptionClass.SQLSERVER, "Failed to create " + getTableName(), e);
        }
    }

    /**
     * Drop the table.
     * @throws JDataException on error
     */
    protected void dropTable() throws JDataException {
        String myDrop;

        /* Protect the drop */
        try {
            /* If the table has an index */
            if (theTable.isIndexed()) {
                /* Execute the drop index statement */
                myDrop = theTable.getDropIndexString();
                executeStatement(myDrop);
            }

            /* Execute the drop table statement */
            myDrop = theTable.getDropTableString();
            executeStatement(myDrop);
        } catch (SQLException e) {
            theDatabase.close();
            throw new JDataException(ExceptionClass.SQLSERVER, "Failed to drop " + getTableName(), e);
        }
    }

    /**
     * Truncate the table.
     * @throws JDataException on error
     */
    protected void purgeTable() throws JDataException {
        /* Protect the truncate */
        try {
            /* Execute the purge statement */
            String myTrunc = theTable.getPurgeString();
            executeStatement(myTrunc);
        } catch (SQLException e) {
            theDatabase.close();
            throw new JDataException(ExceptionClass.SQLSERVER, "Failed to purge " + getTableName(), e);
        }
    }
}
