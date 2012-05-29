/*******************************************************************************
 * JDataModel: Data models
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JGordianKnot.CipherSet;
import net.sourceforge.JGordianKnot.SymmetricKey;
import uk.co.tolcroft.models.database.ColumnDefinition.BinaryColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.BooleanColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.DateColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.IdColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.IntegerColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.LongColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.MoneyColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.RateColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.ReferenceColumn;
import uk.co.tolcroft.models.database.ColumnDefinition.StringColumn;

/**
 * Database field definition class. Maps each dataType to a database field.
 */
public class TableDefinition {
    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * The Table name.
     */
    private final String theTableName;

    /**
     * The Column Definitions.
     */
    private final List<ColumnDefinition> theList;

    /**
     * The Sort List.
     */
    private final List<ColumnDefinition> theSortList;

    /**
     * Are we sorting on a reference column.
     */
    private boolean sortOnReference = false;

    /**
     * The array list for the columns.
     */
    private final Map<ReportField, ColumnDefinition> theMap;

    /**
     * The prepared statement for the insert/update.
     */
    private PreparedStatement theStatement = null;

    /**
     * The result set for the load.
     */
    private ResultSet theResults = null;

    /**
     * Obtain the table name.
     * @return the table name
     */
    protected String getTableName() {
        return theTableName;
    }

    /**
     * Obtain the column map.
     * @return the map
     */
    protected Map<ReportField, ColumnDefinition> getMap() {
        return theMap;
    }

    /**
     * Is the table indexed.
     * @return true/false
     */
    protected boolean isIndexed() {
        return theSortList.size() > 0;
    }

    /**
     * Column Definitions array.
     * @return the columns
     */
    protected List<ColumnDefinition> getColumns() {
        return theList;
    }

    /**
     * Sort List.
     * @return the sort list
     */
    protected List<ColumnDefinition> getSortList() {
        return theSortList;
    }

    /**
     * Note that we have a sort on reference.
     */
    protected void setSortOnReference() {
        sortOnReference = true;
    }

    /**
     * Constructor.
     * @param pName the table name
     */
    protected TableDefinition(final String pName) {
        /* Record the name */
        theTableName = pName;

        /* Create the column list */
        theList = new ArrayList<ColumnDefinition>();

        /* Create the sort list */
        theSortList = new ArrayList<ColumnDefinition>();

        /* Create the initial column map */
        theMap = new HashMap<ReportField, ColumnDefinition>();

        /* Add an Id column */
        theList.add(new IdColumn(this));
    }

    /**
     * Add a reference column.
     * @param pId the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public ReferenceColumn addReferenceColumn(final ReportField pId,
                                              final String pRef) {
        /* Create the new reference column */
        ReferenceColumn myColumn = new ReferenceColumn(this, pId, pRef);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a reference column, which can be null.
     * @param pId the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public ReferenceColumn addNullReferenceColumn(final ReportField pId,
                                                  final String pRef) {
        ReferenceColumn myColumn = addReferenceColumn(pId, pRef);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an integer column.
     * @param pId the column id
     * @return the integer column
     */
    public IntegerColumn addIntegerColumn(final ReportField pId) {
        /* Create the new integer column */
        IntegerColumn myColumn = new IntegerColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add an integer column, which can be null.
     * @param pId the column id
     * @return the integer column
     */
    public IntegerColumn addNullIntegerColumn(final ReportField pId) {
        IntegerColumn myColumn = addIntegerColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a long column.
     * @param pId the column id
     * @return the long column
     */
    public LongColumn addLongColumn(final ReportField pId) {
        /* Create the new long column */
        LongColumn myColumn = new LongColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a long column, which can be null.
     * @param pId the column id
     * @return the long column
     */
    public LongColumn addNullLongColumn(final ReportField pId) {
        LongColumn myColumn = addLongColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a boolean column.
     * @param pId the column id
     * @return the boolean column
     */
    public BooleanColumn addBooleanColumn(final ReportField pId) {
        /* Create the new boolean column */
        BooleanColumn myColumn = new BooleanColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a boolean column, which can be null.
     * @param pId the column id
     * @return the boolean column
     */
    public BooleanColumn addNullBooleanColumn(final ReportField pId) {
        BooleanColumn myColumn = addBooleanColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a date column.
     * @param pId the column id
     * @return the date column
     */
    public DateColumn addDateColumn(final ReportField pId) {
        /* Create the new date column */
        DateColumn myColumn = new DateColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a date column, which can be null.
     * @param pId the column id
     * @return the date column
     */
    public DateColumn addNullDateColumn(final ReportField pId) {
        DateColumn myColumn = addDateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a money column.
     * @param pId the column id
     * @return the money column
     */
    public MoneyColumn addMoneyColumn(final ReportField pId) {
        /* Create the new money column */
        MoneyColumn myColumn = new MoneyColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a money column, which can be null.
     * @param pId the column id
     * @return the money column
     */
    public MoneyColumn addNullMoneyColumn(final ReportField pId) {
        MoneyColumn myColumn = addMoneyColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     * @param pId the column id
     * @return the rate column
     */
    public RateColumn addRateColumn(final ReportField pId) {
        /* Create the new rate column */
        RateColumn myColumn = new RateColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a rate column, which can be null.
     * @param pId the column id
     * @return the rate column
     */
    public RateColumn addNullRateColumn(final ReportField pId) {
        RateColumn myColumn = addRateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a binary column.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addBinaryColumn(final ReportField pId,
                                        final int pLength) {
        /* Create the new binary column */
        BinaryColumn myColumn = new BinaryColumn(this, pId, pLength);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a binary column, which can be null.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addNullBinaryColumn(final ReportField pId,
                                            final int pLength) {
        BinaryColumn myColumn = addBinaryColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an encrypted column.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addEncryptedColumn(final ReportField pId,
                                           final int pLength) {
        /* Create the new binary column */
        BinaryColumn myColumn = new BinaryColumn(this, pId, SymmetricKey.IVSIZE + CipherSet.KEYIDLEN
                + SymmetricKey.getEncryptionLength(2 * pLength));

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add an encrypted column, which can be null.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addNullEncryptedColumn(final ReportField pId,
                                               final int pLength) {
        BinaryColumn myColumn = addEncryptedColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a string column.
     * @param pId the column id
     * @param pLength the character length
     * @return the binary column
     */
    public StringColumn addStringColumn(final ReportField pId,
                                        final int pLength) {
        /* Create the new string column */
        StringColumn myColumn = new StringColumn(this, pId, pLength);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a string column, which can be null.
     * @param pId the column id
     * @param pLength the character length
     * @return the binary column
     */
    public StringColumn addNullStringColumn(final ReportField pId,
                                            final int pLength) {
        StringColumn myColumn = addStringColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Locate reference.
     * @param pTables the list of defined tables
     */
    protected void resolveReferences(final List<DatabaseTable<?>> pTables) {
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            myDef.locateReference(pTables);
        }
    }

    /**
     * Load results.
     * @param pResults the result set
     * @throws SQLException on error
     */
    protected void loadResults(final ResultSet pResults) throws SQLException {
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        int myIndex = 1;

        /* Store the result set and clear values */
        theResults = pResults;
        clearValues();

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            myDef.loadValue(theResults, myIndex++);
        }
    }

    /**
     * Insert values.
     * @param pStmt the statement
     * @throws SQLException on error
     * @throws ModelException on error
     */
    protected void insertValues(final PreparedStatement pStmt) throws SQLException, ModelException {
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        int myIndex = 1;

        /* Store the Statement */
        theStatement = pStmt;

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();

            /* Reject if the value is not set */
            if (!myDef.isValueSet()) {
                throw new ModelException(ExceptionClass.LOGIC, "Column " + myDef.getColumnName()
                        + " in table " + theTableName + " has no value for insert");
            }

            myDef.storeValue(theStatement, myIndex++);
        }
    }

    /**
     * Update values.
     * @param pStmt the statement
     * @throws SQLException on error
     * @throws ModelException on error
     */
    protected void updateValues(final PreparedStatement pStmt) throws SQLException, ModelException {
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        ColumnDefinition myId = null;
        int myIndex = 1;

        /* Store the Statement */
        theStatement = pStmt;

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();

            /* If this is the Id record */
            if (myDef instanceof IdColumn) {
                /* Remember the column */
                myId = myDef;

                /* Store value if it has been set */
            } else if (myDef.isValueSet()) {
                myDef.storeValue(theStatement, myIndex++);
            }
        }

        /* Store the Id */
        myId.storeValue(theStatement, myIndex);
    }

    /**
     * Clear values for table.
     */
    protected void clearValues() {
        /* Loop over the Column Definitions */
        for (ColumnDefinition myDef : theList) {
            /* Clear value */
            myDef.clearValue();
        }
    }

    /**
     * Get Integer value for column.
     * @param pId the column id
     * @return the integer value
     * @throws ModelException on error
     */
    public Integer getIntegerValue(final ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Integer type");
        }

        /* Return the value */
        IntegerColumn myIntCol = (IntegerColumn) myCol;
        return myIntCol.getValue();
    }

    /**
     * Get Long value for column.
     * @param pId the column id
     * @return the long value
     * @throws ModelException on error
     */
    public Long getLongValue(final ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Long type");
        }

        /* Return the value */
        LongColumn myLongCol = (LongColumn) myCol;
        return myLongCol.getValue();
    }

    /**
     * Get Date value for column.
     * @param pId the column id
     * @return the Date value
     * @throws ModelException on error
     */
    public Date getDateValue(final ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a date column */
        if (!(myCol instanceof DateColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Date type");
        }

        /* Return the value */
        DateColumn myDateCol = (DateColumn) myCol;
        return myDateCol.getValue();
    }

    /**
     * Get Boolean value for column.
     * @param pId the column id
     * @return the boolean value
     * @throws ModelException on error
     */
    public Boolean getBooleanValue(final ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Boolean type");
        }

        /* Return the value */
        BooleanColumn myBoolCol = (BooleanColumn) myCol;
        return myBoolCol.getValue();
    }

    /**
     * Get String value for column.
     * @param pId the column id
     * @return the String value
     * @throws ModelException on error
     */
    public String getStringValue(final ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not String type");
        }

        /* Return the value */
        StringColumn myStringCol = (StringColumn) myCol;
        return myStringCol.getValue();
    }

    /**
     * Get Binary value for column.
     * @param pId the column id
     * @return the binary value
     * @throws ModelException on error
     */
    public byte[] getBinaryValue(final ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof BinaryColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Binary type");
        }

        /* Return the value */
        BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        return myBinaryCol.getValue();
    }

    /**
     * Set Integer value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setIntegerValue(final ReportField pId,
                                final Integer pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Integer type");
        }

        /* Set the value */
        IntegerColumn myIntCol = (IntegerColumn) myCol;
        myIntCol.setValue(pValue);
    }

    /**
     * Set Long value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setLongValue(final ReportField pId,
                             final Long pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Long type");
        }

        /* Set the value */
        LongColumn myLongCol = (LongColumn) myCol;
        myLongCol.setValue(pValue);
    }

    /**
     * Set Boolean value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setBooleanValue(final ReportField pId,
                                final Boolean pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Boolean type");
        }

        /* Set the value */
        BooleanColumn myBoolCol = (BooleanColumn) myCol;
        myBoolCol.setValue(pValue);
    }

    /**
     * Set Date value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setDateValue(final ReportField pId,
                             final DateDay pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a Date column */
        if (!(myCol instanceof DateColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Date type");
        }

        /* Set the value */
        DateColumn myDateCol = (DateColumn) myCol;
        myDateCol.setValue(pValue);
    }

    /**
     * Set String value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setStringValue(final ReportField pId,
                               final String pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not String type");
        }

        /* Set the value */
        StringColumn myStringCol = (StringColumn) myCol;
        myStringCol.setValue(pValue);
    }

    /**
     * Set Binary value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setBinaryValue(final ReportField pId,
                               final byte[] pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a binary column */
        if (!(myCol instanceof BinaryColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Binary type");
        }

        /* Set the value */
        BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        myBinaryCol.setValue(pValue);
    }

    /**
     * Set Money value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setMoneyValue(final ReportField pId,
                              final Money pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof MoneyColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Money type");
        }

        /* Set the value */
        MoneyColumn myMoneyCol = (MoneyColumn) myCol;
        myMoneyCol.setValue(pValue);
    }

    /**
     * Set Rate value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException on error
     */
    public void setRateValue(final ReportField pId,
                             final Rate pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof RateColumn)) {
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Rate type");
        }

        /* Set the value */
        RateColumn myRateCol = (RateColumn) myCol;
        myRateCol.setValue(pValue);
    }

    /**
     * Locate column for id.
     * @param pId the id of the column
     * @return the column
     * @throws ModelException on error
     */
    private ColumnDefinition getColumnForId(final ReportField pId) throws ModelException {
        /* Access the definition */
        ColumnDefinition myDef = theMap.get(pId);

        /* Check that the id is in range and present */
        if (myDef == null) {
            throw new ModelException(ExceptionClass.LOGIC, "Invalid Column Id: " + pId + " for "
                    + theTableName);
        }

        /* Return the column definition */
        return myDef;
    }

    /**
     * Build the create table string for the table.
     * @return the SQL string
     */
    protected String getCreateTableString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        boolean myFirst = true;

        /* Build the initial create */
        myBuilder.append("create table ");
        myBuilder.append(theTableName);
        myBuilder.append(" (");

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            myDef.buildCreateString(myBuilder);
            myFirst = false;
        }

        /* Close the statement and return it */
        myBuilder.append(')');
        return myBuilder.toString();
    }

    /**
     * Build the create index string for the table.
     * @return the SQL string
     */
    protected String getCreateIndexString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        boolean myFirst = true;

        /* Return null if we are not indexed */
        if (!isIndexed()) {
            return null;
        }

        /* Build the initial create */
        myBuilder.append("create index idx_");
        myBuilder.append(theTableName);
        myBuilder.append(" on ");
        myBuilder.append(theTableName);
        myBuilder.append(" (");

        /* Create the iterator */
        myIterator = theSortList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            myBuilder.append(myDef.getColumnName());
            if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                myBuilder.append(" DESC");
            }
            myFirst = false;
        }

        /* Close the statement and return it */
        myBuilder.append(')');
        return myBuilder.toString();
    }

    /**
     * Build the drop table string for the table.
     * @return the SQL string
     */
    protected String getDropTableString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the drop command */
        myBuilder.append("if exists (select * from sys.tables where name = '");
        myBuilder.append(theTableName);
        myBuilder.append("') drop table ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Build the drop index string for the table.
     * @return the SQL string
     */
    protected String getDropIndexString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Return null if we are not indexed */
        if (!isIndexed()) {
            return null;
        }

        /* Build the drop command */
        myBuilder.append("if exists (select * from sys.indexes where name = 'idx_");
        myBuilder.append(theTableName);
        myBuilder.append("') drop index idx_");
        myBuilder.append(theTableName);
        myBuilder.append(" on ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Build the load string for a list of columns.
     * @return the SQL string
     */
    protected String getLoadString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        boolean myFirst = true;

        /* Build the initial insert */
        myBuilder.append("select ");

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            if (sortOnReference) {
                myBuilder.append("a.");
            }
            myBuilder.append(myDef.getColumnName());
            myFirst = false;
        }

        /* Close the statement */
        myBuilder.append(" from ");
        myBuilder.append(theTableName);
        if (sortOnReference) {
            myBuilder.append(" a");
        }

        /* If we are indexed */
        if (isIndexed()) {
            /* Add Joins */
            if (sortOnReference) {
                myBuilder.append(getJoinString('a', 1));
            }

            /* Add the order clause */
            myBuilder.append(" order by ");

            /* Build the order string */
            myBuilder.append(getOrderString('a', 1));
        }

        return myBuilder.toString();
    }

    /**
     * Build the Join string for the list of columns.
     * @param pChar the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    protected String getJoinString(final char pChar,
                                   final Integer pOffset) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;

        /* Create the iterator */
        myIterator = theSortList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            /* Access next column and skip if not reference column */
            myDef = myIterator.next();
            if (!(myDef instanceof ReferenceColumn)) {
                continue;
            }

            /* Add the join */
            ReferenceColumn myCol = (ReferenceColumn) myDef;
            myCol.buildJoinString(myBuilder, pChar, pOffset);
        }

        return myBuilder.toString();
    }

    /**
     * Build the Order string for the list of columns.
     * @param pChar the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    protected String getOrderString(final char pChar,
                                    final Integer pOffset) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        boolean myFirst;

        /* Create the iterator */
        myIterator = theSortList.iterator();
        myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            /* Handle secondary columns */
            if (!myFirst) {
                myBuilder.append(", ");
            }

            /* If we are using prefixes */
            if ((sortOnReference) || (pChar > 'a')) {
                /* If this is a reference column */
                if (myDef instanceof ReferenceColumn) {
                    /* Handle Reference column */
                    ReferenceColumn myCol = (ReferenceColumn) myDef;
                    myCol.buildOrderString(myBuilder, pOffset);
                } else {
                    /* Handle standard column with prefix */
                    myBuilder.append(pChar);
                    myBuilder.append(".");
                    myBuilder.append(myDef.getColumnName());
                    if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                        myBuilder.append(" DESC");
                    }
                }
            } else {
                /* Handle standard column */
                myBuilder.append(myDef.getColumnName());
                if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                    myBuilder.append(" DESC");
                }
            }

            /* Note secondary columns */
            myFirst = false;
        }

        return myBuilder.toString();
    }

    /**
     * Build the insert string for a list of columns.
     * @return the SQL string
     */
    protected String getInsertString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        StringBuilder myValues = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        boolean myFirst = true;

        /* Build the initial insert */
        myBuilder.append("insert into ");
        myBuilder.append(theTableName);
        myBuilder.append(" (");

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            if (!myFirst) {
                myValues.append(", ");
            }
            myBuilder.append(myDef.getColumnName());
            myValues.append('?');
            myFirst = false;
        }

        /* Close the statement and return it */
        myBuilder.append(") values(");
        myBuilder.append(myValues);
        myBuilder.append(')');
        return myBuilder.toString();
    }

    /**
     * Build the update string for a list of columns.
     * @return the SQL string
     * @throws ModelException on error
     */
    protected String getUpdateString() throws ModelException {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        ColumnDefinition myId = null;
        boolean myFirst = true;

        /* Build the initial update */
        myBuilder.append("update ");
        myBuilder.append(theTableName);
        myBuilder.append(" set ");

        /* Create the iterator */
        myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            myDef = myIterator.next();

            /* If this is the Id record */
            if (myDef instanceof IdColumn) {
                /* Reject if the value is not set */
                if (!myDef.isValueSet()) {
                    throw new ModelException(ExceptionClass.LOGIC, "Column " + myDef.getColumnName()
                            + " in table " + theTableName + " has no value for update");
                }

                /* Remember the column */
                myId = myDef;

                /* If this column is to be updated */
            } else if (myDef.isValueSet()) {
                /* Add the update of this column */
                if (!myFirst) {
                    myBuilder.append(", ");
                }
                myBuilder.append(myDef.getColumnName());
                myBuilder.append("=?");
                myFirst = false;
            }
        }

        /* If we have no values then just return null */
        if (myFirst) {
            return null;
        }

        /* Close the statement and return it */
        myBuilder.append(" where ");
        myBuilder.append(myId.getColumnName());
        myBuilder.append("=?");
        return myBuilder.toString();
    }

    /**
     * Build the delete string for a table.
     * @return the SQL string
     */
    protected String getDeleteString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        ColumnDefinition myId = null;

        /* Build the initial delete */
        myBuilder.append("delete from ");
        myBuilder.append(theTableName);
        myBuilder.append(" where ");

        /* Access the id definition */
        myId = theList.get(0);

        /* Build the rest of the command */
        myBuilder.append(myId.getColumnName());
        myBuilder.append("=?");
        return myBuilder.toString();
    }

    /**
     * Build the purge string for a table.
     * @return the SQL string
     */
    protected String getPurgeString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("delete from ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Build the count string for a table.
     * @return the SQL string
     */
    protected String getCountString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("select count(*) from ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Sort order indication.
     */
    public enum SortOrder {
        /**
         * Ascending sort order.
         */
        ASCENDING,

        /**
         * Descending sort order.
         */
        DESCENDING;
    }
}
