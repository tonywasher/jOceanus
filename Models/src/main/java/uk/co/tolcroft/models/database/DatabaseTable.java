/*******************************************************************************
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
package uk.co.tolcroft.models.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.ValueSet;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.threads.ThreadStatus;

public abstract class DatabaseTable<T extends DataItem<T>> {
    /**
     * The Database control
     */
    private Database<?> theDatabase = null;

    /**
     * The Database connection
     */
    private Connection theConn = null;

    /**
     * The list of items for this table
     */
    private DataList<?, T> theList = null;

    /**
     * The prepared statement
     */
    private PreparedStatement theStmt = null;

    /**
     * The result set
     */
    private ResultSet theResults = null;

    /**
     * The table definition
     */
    private TableDefinition theTable = null;

    /**
     * Constructor
     * @param pDatabase the database control
     * @param pTable the table name
     */
    protected DatabaseTable(Database<?> pDatabase,
                            String pTable) {
        /* Set the table */
        theDatabase = pDatabase;
        theConn = theDatabase.getConn();
        theTable = new TableDefinition(pTable);
        defineTable(theTable);
    }

    /**
     * define the table columns
     * @param pTableDef the Table definition
     */
    protected void defineTable(TableDefinition pTableDef) {
    }

    /**
     * Access the table name
     * @return the table name
     */
    protected String getTableName() {
        return theTable.getTableName();
    }

    /**
     * Access the table definition
     * @return the table definition
     */
    protected TableDefinition getDefinition() {
        return theTable;
    }

    /**
     * Close the result set and statement
     * @throws SQLException
     */
    protected void closeStmt() throws SQLException {
        theTable.clearValues();
        if (theResults != null)
            theResults.close();
        if (theStmt != null)
            theStmt.close();
    }

    /**
     * Shift to next line in result set
     * @return is there a next line
     * @throws SQLException
     */
    private boolean next() throws SQLException {
        return theResults.next();
    }

    /**
     * Prepare the statement
     * @param pStatement the statement to prepare
     * @throws SQLException
     */
    private void prepareStatement(String pStatement) throws SQLException {
        theStmt = theConn.prepareStatement(pStatement);
    }

    /**
     * Execute the prepared statement
     * @throws SQLException
     */
    private void execute() throws SQLException {
        theStmt.executeUpdate();
        theTable.clearValues();
    }

    /**
     * Query the prepared statement
     * @throws SQLException
     */
    private void executeQuery() throws SQLException {
        theTable.clearValues();
        theResults = theStmt.executeQuery();
    }

    /**
     * Commit the update
     * @throws SQLException
     */
    private void commit() throws SQLException {
        theConn.commit();
    }

    /**
     * Execute a statement
     * @param pStatement the statement
     * @throws SQLException
     * 
     */
    private void executeStatement(String pStatement) throws SQLException {
        /* Prepare the statement */
        prepareStatement(pStatement);

        /* Execute the delete */
        execute();
        commit();

        /* Close the Statement */
        closeStmt();
    }

    /**
     * Count the number of items to be loaded
     * @return the count of items
     * @throws SQLException
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
     * Declare DataSet
     * @param pData the Data set
     */
    protected abstract void declareData(DataSet<?> pData);

    /**
     * Set the list of items
     * @param pList the list of items
     */
    protected void setList(DataList<?, T> pList) {
        theList = pList;
    }

    /**
     * Obtain the list of items
     * @return the list of items
     */
    protected DataList<?, T> getList() {
        return theList;
    }

    /**
     * Load an individual item from the result set
     * @param pId the id of the item
     * @throws ModelException
     */
    protected abstract void loadItem(int pId) throws ModelException;

    /**
     * Set a field value for an item
     * @param pItem the item to insert
     * @param pField the field id
     * @throws ModelException
     */
    protected void setFieldValue(T pItem,
                                 ReportField pField) throws ModelException {
        /* Switch on field id */
        if (pField == DataItem.FIELD_ID) {
            theTable.setIntegerValue(DataItem.FIELD_ID, pItem.getId());
        }
    }

    /**
     * Post-Process on a load operation
     * @throws ModelException
     */
    protected void postProcessOnLoad() throws ModelException {
    }

    /**
     * Load items from the list into the table
     * @param pThread the thread control
     * @param pData the data
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    protected boolean loadItems(ThreadStatus<?> pThread,
                                DataSet<?> pData) throws ModelException {
        boolean bContinue = true;
        String myQuery;
        int mySteps;
        int myCount = 0;

        /* Declare the new stage */
        if (!pThread.setNewStage(getTableName()))
            return false;

        /* Access reporting steps */
        mySteps = pThread.getReportingSteps();

        /* Declare the Data */
        declareData(pData);

        /* Protect the load */
        try {
            /* Count the Items to be loaded */
            if (!pThread.setNumSteps(countLoadItems()))
                return false;

            /* Load the items from the table */
            myQuery = theTable.getLoadString();
            prepareStatement(myQuery);
            executeQuery();

            /* Loop through the results */
            while (next()) {
                /* Read in the results */
                theTable.loadResults(theResults);
                int myId = theTable.getIntegerValue(DataItem.FIELD_ID);

                /* Load the next item */
                loadItem(myId);

                /* Report the progress */
                myCount++;
                if ((myCount % mySteps) == 0)
                    if (!pThread.setStepsDone(myCount))
                        return false;
            }

            /* Close the Statement */
            closeStmt();

            /* Perform post process */
            postProcessOnLoad();
        }

        catch (Exception e) {
            throw new ModelException(ExceptionClass.SQLSERVER, "Failed to load " + getTableName(), e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Determine the count of items that are in a particular state
     * @param pState the particular state
     * @return the count of items
     */
    private int countStateItems(DataState pState) {
        DataListIterator<T> myIterator;
        T myCurr;
        int iCount = 0;

        /* Access the iterator */
        myIterator = theList.listIterator(true);

        /* Loop through the list */
        while ((myCurr = myIterator.next()) != null) {
            /* Ignore items that are not this type */
            if (myCurr.getState() != pState)
                continue;

            /* Increment count */
            ++iCount;
        }

        /* Return count */
        return iCount;
    }

    /**
     * Insert new items from the list
     * @param pThread the thread control
     * @param pData the data
     * @param pBatch the batch control
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    protected boolean insertItems(ThreadStatus<?> pThread,
                                  DataSet<?> pData,
                                  BatchControl pBatch) throws ModelException {
        DataListIterator<T> myIterator;
        T myCurr = null;
        int myCount = 0;
        int mySteps;
        String myInsert;
        boolean bContinue = true;

        /* Declare the new stage */
        if (!pThread.setNewStage("Inserting " + getTableName()))
            return false;

        /* Access reporting steps */
        mySteps = pThread.getReportingSteps();

        /* Declare the Data */
        declareData(pData);

        /* Protect the insert */
        try {
            /* Declare the number of steps */
            if (!pThread.setNumSteps(countStateItems(DataState.NEW)))
                return false;

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, DataState.NEW);

            /* Prepare the insert statement */
            myInsert = theTable.getInsertString();
            prepareStatement(myInsert);

            /* Access the iterator */
            myIterator = theList.listIterator(true);

            /* Loop through the list */
            while ((myCurr = myIterator.next()) != null) {
                /* Ignore non-new items */
                if (myCurr.getState() != DataState.NEW)
                    continue;

                /* Loop through the columns */
                for (ColumnDefinition myCol : theTable.getColumns()) {
                    /* Access the column id */
                    ReportField iField = myCol.getColumnId();

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
                if ((myCount % mySteps) == 0)
                    if (!pThread.setStepsDone(myCount))
                        return false;
            }

            /* Close the Statement */
            closeStmt();
        }

        catch (Exception e) {
            theDatabase.close();
            throw new ModelException(ExceptionClass.SQLSERVER, myCurr, "Failed to insert " + getTableName(),
                    e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Update items from the list
     * @param pThread the thread control
     * @param pBatch the batch control
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    protected boolean updateItems(ThreadStatus<?> pThread,
                                  BatchControl pBatch) throws ModelException {
        DataListIterator<T> myIterator;
        T myCurr = null;
        int myCount = 0;
        int mySteps;
        String myUpdate;
        boolean bContinue = true;

        /* Declare the new stage */
        if (!pThread.setNewStage("Updating " + getTableName()))
            return false;

        /* Access reporting steps */
        mySteps = pThread.getReportingSteps();

        /* Protect the update */
        try {
            /* Declare the number of steps */
            if (!pThread.setNumSteps(countStateItems(DataState.CHANGED)))
                return false;

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, DataState.CHANGED);

            /* Access the iterator */
            myIterator = theList.listIterator(true);

            /* Loop through the list */
            while ((myCurr = myIterator.next()) != null) {
                /* Ignore non-changed items */
                if (myCurr.getState() != DataState.CHANGED)
                    continue;

                /* Update the item */
                if (updateItem(myCurr)) {
                    /* Record the id and access the update string */
                    theTable.setIntegerValue(DataItem.FIELD_ID, myCurr.getId());
                    myUpdate = theTable.getUpdateString();

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
                    if ((myCount % mySteps) == 0)
                        if (!pThread.setStepsDone(myCount))
                            return false;
                }
            }
        }

        catch (Exception e) {
            theDatabase.close();
            throw new ModelException(ExceptionClass.SQLSERVER, myCurr, "Failed to update " + getTableName(),
                    e);
        }

        /* Return to caller */
        return bContinue;
    }

    /* Update the item */
    private boolean updateItem(T pItem) throws ModelException {
        ValueSet<T> myCurr;
        ValueSet<T> myBase;
        boolean isUpdated = false;

        /* Access the object and base */
        myCurr = pItem.getValueSet();
        myBase = pItem.getOriginalValues();

        /* Protect the update */
        try {
            /* Loop through the fields */
            for (ColumnDefinition myCol : theTable.getColumns()) {
                /* Skip null columns */
                if (myCol == null)
                    continue;

                /* Access the column id */
                ReportField iField = myCol.getColumnId();

                /* Ignore ID column */
                if (iField == DataItem.FIELD_ID)
                    continue;

                /* If the field has changed */
                if (myCurr.fieldChanged(iField, myBase).isDifferent()) {
                    /* Record the change */
                    isUpdated = true;
                    setFieldValue(pItem, iField);
                }
            }
        }

        catch (Exception e) {
            throw new ModelException(ExceptionClass.SQLSERVER, pItem, "Failed to update item", e);
        }

        /* Return to caller */
        return isUpdated;
    }

    /**
     * Delete items from the list
     * @param pThread the thread control
     * @param pBatch the batch control
     * @return Continue <code>true/false</code>
     * @throws ModelException
     */
    protected boolean deleteItems(ThreadStatus<?> pThread,
                                  BatchControl pBatch) throws ModelException {
        DataListIterator<T> myIterator;
        T myCurr = null;
        int myCount = 0;
        int mySteps;
        String myDelete;
        boolean bContinue = true;

        /* Declare the new stage */
        if (!pThread.setNewStage("Deleting " + getTableName()))
            return false;

        /* Access reporting steps */
        mySteps = pThread.getReportingSteps();

        /* Protect the delete */
        try {
            /* Declare the number of steps */
            if (!pThread.setNumSteps(countStateItems(DataState.DELETED)))
                return false;

            /* Declare the table and mode */
            pBatch.setCurrentTable(this, DataState.DELETED);

            /* Prepare the delete statement */
            myDelete = theTable.getDeleteString();
            prepareStatement(myDelete);

            /* Access the iterator */
            myIterator = theList.listIterator(true);

            /* Loop through the list in reverse order */
            while ((myCurr = myIterator.previous()) != null) {
                /* Ignore non-deleted items */
                if (myCurr.getState() != DataState.DELETED)
                    continue;

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
                if ((myCount % mySteps) == 0)
                    if (!pThread.setStepsDone(myCount))
                        return false;
            }

            /* Close the Statement */
            closeStmt();
        }

        catch (Exception e) {
            theDatabase.close();
            throw new ModelException(ExceptionClass.SQLSERVER, myCurr, "Failed to delete " + getTableName(),
                    e);
        }

        /* Return to caller */
        return bContinue;
    }

    /**
     * Create the table
     * @throws ModelException
     */
    protected void createTable() throws ModelException {
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
        }

        catch (Exception e) {
            theDatabase.close();
            throw new ModelException(ExceptionClass.SQLSERVER, "Failed to create " + getTableName(), e);
        }

        /* Return to caller */
        return;
    }

    /**
     * Drop the table
     * @throws ModelException
     */
    protected void dropTable() throws ModelException {
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
        }

        catch (Exception e) {
            theDatabase.close();
            throw new ModelException(ExceptionClass.SQLSERVER, "Failed to drop " + getTableName(), e);
        }

        /* Return to caller */
        return;
    }

    /**
     * Truncate the table
     * @throws ModelException
     */
    protected void purgeTable() throws ModelException {
        String myTrunc;

        /* Protect the truncate */
        try {
            /* Execute the purge statement */
            myTrunc = theTable.getPurgeString();
            executeStatement(myTrunc);
        }

        catch (Exception e) {
            theDatabase.close();
            throw new ModelException(ExceptionClass.SQLSERVER, "Failed to purge " + getTableName(), e);
        }

        /* Return to caller */
        return;
    }
}
