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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Decimal.Money;
import net.sourceforge.JDecimal.Decimal.Rate;
import net.sourceforge.JGordianKnot.CipherSet;
import net.sourceforge.JGordianKnot.SymmetricKey;
import uk.co.tolcroft.models.data.DataItem;

public class TableDefinition {
    /**
     * The Table name
     */
    private String theTableName = null;

    /**
     * The Column Definitions
     */
    private List<ColumnDefinition> theList = null;

    /**
     * The Sort List
     */
    private List<ColumnDefinition> theSortList = null;

    /**
     * Are we sorting on a reference column
     */
    private boolean sortOnReference = false;

    /**
     * The array list for the columns
     */
    private final Map<ReportField, ColumnDefinition> theMap;

    /**
     * The prepared statement for the insert/update
     */
    private PreparedStatement theStatement = null;

    /**
     * The result set for the load
     */
    private ResultSet theResults = null;

    /**
     * Obtain the table name
     * @return the table name
     */
    protected String getTableName() {
        return theTableName;
    }

    /**
     * Is the table indexed
     * @return true/false
     */
    protected boolean isIndexed() {
        return theSortList.size() > 0;
    }

    /**
     * Column Definitions array
     * @return the columns
     */
    protected List<ColumnDefinition> getColumns() {
        return theList;
    }

    /**
     * Constructor
     * @param pName the table name
     */
    protected TableDefinition(String pName) {
        /* Record the name */
        theTableName = pName;

        /* Create the column list */
        theList = new ArrayList<ColumnDefinition>();

        /* Create the sort list */
        theSortList = new ArrayList<ColumnDefinition>();

        /* Create the initial column map */
        theMap = new HashMap<ReportField, ColumnDefinition>();

        /* Add an Id column */
        theList.add(new IdColumn());
    }

    /**
     * Add a reference column
     * @param pId the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public ReferenceColumn addReferenceColumn(ReportField pId,
                                              String pRef) {
        /* Create the new reference column */
        ReferenceColumn myColumn = new ReferenceColumn(pId, pRef);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public ReferenceColumn addNullReferenceColumn(ReportField pId,
                                                  String pRef) {
        ReferenceColumn myColumn = addReferenceColumn(pId, pRef);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an integer column
     * @param pId the column id
     * @return the integer column
     */
    public IntegerColumn addIntegerColumn(ReportField pId) {
        /* Create the new integer column */
        IntegerColumn myColumn = new IntegerColumn(pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public IntegerColumn addNullIntegerColumn(ReportField pId) {
        IntegerColumn myColumn = addIntegerColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a long column
     * @param pId the column id
     * @return the long column
     */
    public LongColumn addLongColumn(ReportField pId) {
        /* Create the new long column */
        LongColumn myColumn = new LongColumn(pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public LongColumn addNullLongColumn(ReportField pId) {
        LongColumn myColumn = addLongColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a boolean column
     * @param pId the column id
     * @return the boolean column
     */
    public BooleanColumn addBooleanColumn(ReportField pId) {
        /* Create the new boolean column */
        BooleanColumn myColumn = new BooleanColumn(pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public BooleanColumn addNullBooleanColumn(ReportField pId) {
        BooleanColumn myColumn = addBooleanColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a date column
     * @param pId the column id
     * @return the date column
     */
    public DateColumn addDateColumn(ReportField pId) {
        /* Create the new long column */
        DateColumn myColumn = new DateColumn(pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public DateColumn addNullDateColumn(ReportField pId) {
        DateColumn myColumn = addDateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a money column
     * @param pId the column id
     * @return the money column
     */
    public MoneyColumn addMoneyColumn(ReportField pId) {
        /* Create the new money column */
        MoneyColumn myColumn = new MoneyColumn(pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public MoneyColumn addNullMoneyColumn(ReportField pId) {
        MoneyColumn myColumn = addMoneyColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column
     * @param pId the column id
     * @return the rate column
     */
    public RateColumn addRateColumn(ReportField pId) {
        /* Create the new rate column */
        RateColumn myColumn = new RateColumn(pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public RateColumn addNullRateColumn(ReportField pId) {
        RateColumn myColumn = addRateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a binary column
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addBinaryColumn(ReportField pId,
                                        int pLength) {
        /* Create the new binary column */
        BinaryColumn myColumn = new BinaryColumn(pId, pLength);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public BinaryColumn addNullBinaryColumn(ReportField pId,
                                            int pLength) {
        BinaryColumn myColumn = addBinaryColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an encrypted column
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addEncryptedColumn(ReportField pId,
                                           int pLength) {
        /* Create the new binary column */
        BinaryColumn myColumn = new BinaryColumn(pId, SymmetricKey.IVSIZE + CipherSet.KEYIDLEN
                + SymmetricKey.getEncryptionLength(2 * pLength));

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public BinaryColumn addNullEncryptedColumn(ReportField pId,
                                               int pLength) {
        BinaryColumn myColumn = addEncryptedColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a string column
     * @param pId the column id
     * @param pLength the character length
     * @return the binary column
     */
    public StringColumn addStringColumn(ReportField pId,
                                        int pLength) {
        /* Create the new string column */
        StringColumn myColumn = new StringColumn(pId, pLength);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    public StringColumn addNullStringColumn(ReportField pId,
                                            int pLength) {
        StringColumn myColumn = addStringColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Locate reference
     * @param pTables the list of defined tables
     */
    protected void resolveReferences(List<DatabaseTable<?>> pTables) {
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
     * Load results
     * @param pResults the result set
     * @throws SQLException
     */
    protected void loadResults(ResultSet pResults) throws SQLException {
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
            myDef.loadValue(myIndex++);
        }
    }

    /**
     * Insert values
     * @param pStmt the statement
     * @throws SQLException
     * @throws ModelException
     */
    protected void insertValues(PreparedStatement pStmt) throws SQLException, ModelException {
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
            if (!myDef.isValueSet())
                throw new ModelException(ExceptionClass.LOGIC, "Column " + myDef.getColumnName()
                        + " in table " + theTableName + " has no value for insert");

            myDef.storeValue(myIndex++);
        }
    }

    /**
     * Update values
     * @param pStmt the statement
     * @throws SQLException
     * @throws ModelException
     */
    protected void updateValues(PreparedStatement pStmt) throws SQLException, ModelException {
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
            }

            /* Store value if it has been set */
            else if (myDef.isValueSet())
                myDef.storeValue(myIndex++);
        }

        /* Store the Id */
        myId.storeValue(myIndex);
    }

    /**
     * Clear values for table
     */
    protected void clearValues() {
        /* Loop over the Column Definitions */
        for (ColumnDefinition myDef : theList) {
            /* Clear value */
            myDef.clearValue();
        }
    }

    /**
     * Get Integer value for column
     * @param pId the column id
     * @return the integer value
     * @throws ModelException
     */
    public Integer getIntegerValue(ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Integer type");

        /* Return the value */
        IntegerColumn myIntCol = (IntegerColumn) myCol;
        return myIntCol.getValue();
    }

    /**
     * Get Long value for column
     * @param pId the column id
     * @return the long value
     * @throws ModelException
     */
    public Long getLongValue(ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof IntegerColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Long type");

        /* Return the value */
        LongColumn myLongCol = (LongColumn) myCol;
        return myLongCol.getValue();
    }

    /**
     * Get Date value for column
     * @param pId the column id
     * @return the Date value
     * @throws ModelException
     */
    public Date getDateValue(ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a date column */
        if (!(myCol instanceof DateColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Date type");

        /* Return the value */
        DateColumn myDateCol = (DateColumn) myCol;
        return myDateCol.getValue();
    }

    /**
     * Get Boolean value for column
     * @param pId the column id
     * @return the boolean value
     * @throws ModelException
     */
    public Boolean getBooleanValue(ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Boolean type");

        /* Return the value */
        BooleanColumn myBoolCol = (BooleanColumn) myCol;
        return myBoolCol.getValue();
    }

    /**
     * Get String value for column
     * @param pId the column id
     * @return the String value
     * @throws ModelException
     */
    public String getStringValue(ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not String type");

        /* Return the value */
        StringColumn myStringCol = (StringColumn) myCol;
        return myStringCol.getValue();
    }

    /**
     * Get Binary value for column
     * @param pId the column id
     * @return the binary value
     * @throws ModelException
     */
    public byte[] getBinaryValue(ReportField pId) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof BinaryColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Binary type");

        /* Return the value */
        BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        return myBinaryCol.getValue();
    }

    /**
     * Set Integer value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setIntegerValue(ReportField pId,
                                Integer pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Integer type");

        /* Set the value */
        IntegerColumn myIntCol = (IntegerColumn) myCol;
        myIntCol.setValue(pValue);
    }

    /**
     * Set Long value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setLongValue(ReportField pId,
                             Long pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Long type");

        /* Set the value */
        LongColumn myLongCol = (LongColumn) myCol;
        myLongCol.setValue(pValue);
    }

    /**
     * Set Boolean value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setBooleanValue(ReportField pId,
                                Boolean pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Boolean type");

        /* Set the value */
        BooleanColumn myBoolCol = (BooleanColumn) myCol;
        myBoolCol.setValue(pValue);
    }

    /**
     * Set Date value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setDateValue(ReportField pId,
                             DateDay pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a Date column */
        if (!(myCol instanceof DateColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Date type");

        /* Set the value */
        DateColumn myDateCol = (DateColumn) myCol;
        myDateCol.setValue(pValue);
    }

    /**
     * Set String value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setStringValue(ReportField pId,
                               String pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not String type");

        /* Set the value */
        StringColumn myStringCol = (StringColumn) myCol;
        myStringCol.setValue(pValue);
    }

    /**
     * Set Binary value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setBinaryValue(ReportField pId,
                               byte[] pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a binary column */
        if (!(myCol instanceof BinaryColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Binary type");

        /* Set the value */
        BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        myBinaryCol.setValue(pValue);
    }

    /**
     * Set Money value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setMoneyValue(ReportField pId,
                              Money pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof MoneyColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Money type");

        /* Set the value */
        MoneyColumn myMoneyCol = (MoneyColumn) myCol;
        myMoneyCol.setValue(pValue);
    }

    /**
     * Set Rate value for column
     * @param pId the column id
     * @param pValue the value
     * @throws ModelException
     */
    public void setRateValue(ReportField pId,
                             Rate pValue) throws ModelException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof RateColumn))
            throw new ModelException(ExceptionClass.LOGIC, "Column " + myCol.getColumnName() + " in table "
                    + theTableName + " is not Rate type");

        /* Set the value */
        RateColumn myRateCol = (RateColumn) myCol;
        myRateCol.setValue(pValue);
    }

    /**
     * Locate column for id
     * @param pId the id of the column
     * @return the column
     * @throws ModelException
     */
    private ColumnDefinition getColumnForId(ReportField pId) throws ModelException {
        /* Access the definition */
        ColumnDefinition myDef = theMap.get(pId);

        /* Check that the id is in range and present */
        if (myDef == null)
            throw new ModelException(ExceptionClass.LOGIC, "Invalid Column Id: " + pId + " for "
                    + theTableName);

        /* Return the column definition */
        return myDef;
    }

    /**
     * Build the create table string for the table
     * @return the SQL string
     */
    protected String getCreateTableString() {
        StringBuilder myBuilder = new StringBuilder(1000);
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
            if (!myFirst)
                myBuilder.append(", ");
            myDef.buildCreateString(myBuilder);
            myFirst = false;
        }

        /* Close the statement and return it */
        myBuilder.append(')');
        return myBuilder.toString();
    }

    /**
     * Build the create index string for the table
     * @return the SQL string
     */
    protected String getCreateIndexString() {
        StringBuilder myBuilder = new StringBuilder(1000);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;
        boolean myFirst = true;

        /* Return null if we are not indexed */
        if (!isIndexed())
            return null;

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
            if (!myFirst)
                myBuilder.append(", ");
            myBuilder.append(myDef.getColumnName());
            if (myDef.getSortOrder() == SortOrder.DESCENDING)
                myBuilder.append(" DESC");
            myFirst = false;
        }

        /* Close the statement and return it */
        myBuilder.append(')');
        return myBuilder.toString();
    }

    /**
     * Build the drop table string for the table
     * @return the SQL string
     */
    protected String getDropTableString() {
        StringBuilder myBuilder = new StringBuilder(1000);

        /* Build the initial create */
        myBuilder.append("if exists (select * from sys.tables where name = '");
        myBuilder.append(theTableName);
        myBuilder.append("') drop table ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Build the drop index string for the table
     * @return the SQL string
     */
    protected String getDropIndexString() {
        StringBuilder myBuilder = new StringBuilder(1000);

        /* Return null if we are not indexed */
        if (!isIndexed())
            return null;

        /* Build the initial create */
        myBuilder.append("if exists (select * from sys.indexes where name = 'idx_");
        myBuilder.append(theTableName);
        myBuilder.append("') drop index idx_");
        myBuilder.append(theTableName);
        myBuilder.append(" on ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Build the load string for a list of columns
     * @return the SQL string
     */
    protected String getLoadString() {
        StringBuilder myBuilder = new StringBuilder(1000);
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
            if (!myFirst)
                myBuilder.append(", ");
            if (sortOnReference)
                myBuilder.append("a.");
            myBuilder.append(myDef.getColumnName());
            myFirst = false;
        }

        /* Close the statement */
        myBuilder.append(" from ");
        myBuilder.append(theTableName);
        if (sortOnReference)
            myBuilder.append(" a");

        /* If we are indexed */
        if (isIndexed()) {
            /* Add Joins */
            if (sortOnReference)
                myBuilder.append(getJoinString('a', 1));

            /* Add the order clause */
            myBuilder.append(" order by ");

            /* Build the order string */
            myBuilder.append(getOrderString('a', 1));
        }

        return myBuilder.toString();
    }

    /**
     * Build the Join string for the list of columns
     * @param pChar the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    protected String getJoinString(char pChar,
                                   Integer pOffset) {
        StringBuilder myBuilder = new StringBuilder(1000);
        Iterator<ColumnDefinition> myIterator;
        ColumnDefinition myDef;

        /* Create the iterator */
        myIterator = theSortList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            /* Access next column and skip if not reference column */
            myDef = myIterator.next();
            if (!(myDef instanceof ReferenceColumn))
                continue;

            /* Add the join */
            ReferenceColumn myCol = (ReferenceColumn) myDef;
            myCol.buildJoinString(myBuilder, pChar, pOffset);
        }

        return myBuilder.toString();
    }

    /**
     * Build the Order string for the list of columns
     * @param pChar the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    protected String getOrderString(char pChar,
                                    Integer pOffset) {
        StringBuilder myBuilder = new StringBuilder(1000);
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
            if (!myFirst)
                myBuilder.append(", ");

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
                    if (myDef.getSortOrder() == SortOrder.DESCENDING)
                        myBuilder.append(" DESC");
                }
            } else {
                /* Handle standard column */
                myBuilder.append(myDef.getColumnName());
                if (myDef.getSortOrder() == SortOrder.DESCENDING)
                    myBuilder.append(" DESC");
            }

            /* Note secondary columns */
            myFirst = false;
        }

        return myBuilder.toString();
    }

    /**
     * Build the insert string for a list of columns
     * @return the SQL string
     */
    protected String getInsertString() {
        StringBuilder myBuilder = new StringBuilder(1000);
        StringBuilder myValues = new StringBuilder(100);
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
            if (!myFirst)
                myBuilder.append(", ");
            if (!myFirst)
                myValues.append(", ");
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
     * Build the update string for a list of columns
     * @return the SQL string
     * @throws ModelException
     */
    protected String getUpdateString() throws ModelException {
        StringBuilder myBuilder = new StringBuilder(1000);
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
                if (!myDef.isValueSet())
                    throw new ModelException(ExceptionClass.LOGIC, "Column " + myDef.getColumnName()
                            + " in table " + theTableName + " has no value for update");

                /* Remember the column */
                myId = myDef;
            }

            /* If this column is to be updated */
            else if (myDef.isValueSet()) {
                /* Add the update of this column */
                if (!myFirst)
                    myBuilder.append(", ");
                myBuilder.append(myDef.getColumnName());
                myBuilder.append("=?");
                myFirst = false;
            }
        }

        /* If we have no values then just return null */
        if (myFirst)
            return null;

        /* Close the statement and return it */
        myBuilder.append(" where ");
        myBuilder.append(myId.getColumnName());
        myBuilder.append("=?");
        return myBuilder.toString();
    }

    /**
     * Build the delete string for a table
     * @return the SQL string
     */
    protected String getDeleteString() {
        StringBuilder myBuilder = new StringBuilder(1000);
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
     * Build the purge string for a table
     * @return the SQL string
     */
    protected String getPurgeString() {
        StringBuilder myBuilder = new StringBuilder(1000);

        /* Build the initial delete */
        myBuilder.append("delete from ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * Build the count string for a table
     * @return the SQL string
     */
    protected String getCountString() {
        StringBuilder myBuilder = new StringBuilder(1000);

        /* Build the initial delete */
        myBuilder.append("select count(*) from ");
        myBuilder.append(theTableName);
        return myBuilder.toString();
    }

    /**
     * The underlying column definition class
     */
    public abstract class ColumnDefinition {
        /**
         * Column Identity
         */
        protected final ReportField theIdentity;

        /**
         * Is the column null-able
         */
        protected boolean isNullable = false;

        /**
         * Is the value set
         */
        private boolean isValueSet = false;

        /**
         * The value of the column
         */
        protected Object theValue = null;

        /**
         * The sort order of the column
         */
        protected SortOrder theOrder = null;

        /**
         * Obtain the column name
         * @return the name
         */
        protected String getColumnName() {
            return theIdentity.getName();
        }

        /**
         * Obtain the column id
         * @return the id
         */
        protected ReportField getColumnId() {
            return theIdentity;
        }

        /**
         * Obtain the sort order
         * @return the sort order
         */
        protected SortOrder getSortOrder() {
            return theOrder;
        }

        /**
         * Clear value
         */
        private void clearValue() {
            theValue = null;
            isValueSet = false;
        }

        /**
         * Set value
         * @param pValue the value
         */
        private void setValue(Object pValue) {
            theValue = pValue;
            isValueSet = true;
        }

        /**
         * Is the value set
         * @return true/false
         */
        private boolean isValueSet() {
            return isValueSet;
        }

        /**
         * Constructor
         * @param pId the column id
         */
        private ColumnDefinition(ReportField pId) {
            /* Record the identity */
            theIdentity = pId;

            /* Add to the map */
            theMap.put(theIdentity, this);
        }

        /**
         * Build the creation string for this column
         * @param pBuilder the String builder
         */
        protected void buildCreateString(StringBuilder pBuilder) {
            /* Add the name of the column */
            pBuilder.append(getColumnName());
            pBuilder.append(' ');

            /* Add the type of the column */
            buildColumnType(pBuilder);

            /* Add null-able indication */
            if (!isNullable)
                pBuilder.append(" not");
            pBuilder.append(" null");

            /* build the key reference */
            buildKeyReference(pBuilder);
        }

        /**
         * Set null-able column
         */
        protected void setNullable() {
            isNullable = true;
        }

        /**
         * Set sortOrder
         * @param pOrder the Sort direction
         */
        public void setSortOrder(SortOrder pOrder) {
            theOrder = pOrder;
            theSortList.add(this);
        }

        /**
         * Build the column type for this column
         * @param pBuilder the String builder
         */
        protected abstract void buildColumnType(StringBuilder pBuilder);

        /**
         * Load the value for this column
         * @param pIndex the index of the result column
         * @throws SQLException
         */
        protected abstract void loadValue(int pIndex) throws SQLException;

        /**
         * Store the value for this column
         * @param pIndex the index of the statement
         * @throws SQLException
         */
        protected abstract void storeValue(int pIndex) throws SQLException;

        /**
         * Define the key reference
         * @param pBuilder the String builder
         */
        protected void buildKeyReference(StringBuilder pBuilder) {
        }

        /**
         * Locate reference
         * @param pTables the list of defined tables
         */
        protected void locateReference(List<DatabaseTable<?>> pTables) {
        }
    }

    /**
     * The integerColumn Class
     */
    protected class IntegerColumn extends ColumnDefinition {
        /**
         * Constructor
         * @param pId the column id
         */
        private IntegerColumn(ReportField pId) {
            /* Record the column type and name */
            super(pId);
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("int");
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(Integer pValue) {
            super.setValue(pValue);
        }

        /**
         * Get the value
         * @return the value
         */
        private Integer getValue() {
            return (Integer) theValue;
        }

        @Override
        protected void loadValue(int pIndex) throws SQLException {
            int myValue = theResults.getInt(pIndex);
            if ((myValue == 0) && (theResults.wasNull()))
                setValue(null);
            else
                setValue(myValue);
        }

        @Override
        protected void storeValue(int pIndex) throws SQLException {
            Integer myValue = getValue();
            if (myValue == null)
                theStatement.setNull(pIndex, Types.INTEGER);
            else
                theStatement.setInt(pIndex, myValue);
        }
    }

    /**
     * The idColumn Class
     */
    protected class IdColumn extends IntegerColumn {
        /**
         * Constructor
         */
        private IdColumn() {
            /* Record the column type */
            super(DataItem.FIELD_ID);
        }

        @Override
        protected void buildKeyReference(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(" primary key");
        }
    }

    /**
     * The referenceColumn Class
     */
    protected class ReferenceColumn extends IntegerColumn {
        /**
         * The name of the referenced table
         */
        private final String theReference;

        /**
         * The definition of the referenced table
         */
        private TableDefinition theDefinition = null;

        /**
         * Constructor
         * @param pId the column id
         * @param pTable the name of the referenced table
         */
        private ReferenceColumn(ReportField pId, String pTable) {
            /* Record the column type */
            super(pId);
            theReference = pTable;
        }

        @Override
        public void setSortOrder(SortOrder pOrder) {
            super.setSortOrder(pOrder);
            sortOnReference = true;
        }

        @Override
        protected void buildKeyReference(StringBuilder pBuilder) {
            /* Add the reference */
            pBuilder.append(" references ");
            pBuilder.append(theReference);
            pBuilder.append('(');
            pBuilder.append(DataItem.FIELD_ID.getName());
            pBuilder.append(')');
        }

        @Override
        protected void locateReference(List<DatabaseTable<?>> pTables) {
            /* Access the Iterator */
            ListIterator<DatabaseTable<?>> myIterator;
            myIterator = pTables.listIterator();

            /* Loop through the Tables */
            while (myIterator.hasNext()) {
                /* Access Table */
                DatabaseTable<?> myTable = myIterator.next();

                /* If this is the referenced table */
                if (theReference.compareTo(myTable.getTableName()) == 0) {
                    /* Store the reference and break the loop */
                    theDefinition = myTable.getDefinition();
                    break;
                }
            }
        }

        /**
         * build Join String
         * @param pBuilder the String Builder
         * @param pChar the character for this table
         * @param pOffset the join offset
         */
        private void buildJoinString(StringBuilder pBuilder,
                                     char pChar,
                                     Integer pOffset) {
            Integer myOffset = pOffset;

            /* Calculate join character */
            char myChar = (char) ('a' + myOffset);

            /* Build Initial part of string */
            pBuilder.append(" join ");
            pBuilder.append(theReference);
            pBuilder.append(" ");
            pBuilder.append(myChar);

            /* Build the join */
            pBuilder.append(" on ");
            pBuilder.append(pChar);
            pBuilder.append(".");
            pBuilder.append(getColumnName());
            pBuilder.append(" = ");
            pBuilder.append(myChar);
            pBuilder.append(".");
            pBuilder.append(DataItem.FIELD_ID.getName());

            /* Increment offset */
            myOffset++;

            /* Add the join string for the underlying table */
            pBuilder.append(theDefinition.getJoinString(myChar, myOffset));
        }

        /**
         * build Order String
         * @param pBuilder the String Builder
         * @param pOffset the join offset
         */
        private void buildOrderString(StringBuilder pBuilder,
                                      Integer pOffset) {
            Iterator<ColumnDefinition> myIterator;
            ColumnDefinition myDef;
            boolean myFirst = true;
            Integer myOffset = pOffset;

            /* Calculate join character */
            char myChar = (char) ('a' + myOffset);

            /* Create the iterator */
            myIterator = theDefinition.theSortList.iterator();

            /* Loop through the columns */
            while (myIterator.hasNext()) {
                /* Access next column */
                myDef = myIterator.next();

                /* Handle subsequent columns */
                if (!myFirst)
                    pBuilder.append(", ");

                /* If this is a reference column */
                if (myDef instanceof ReferenceColumn) {
                    /* Increment offset */
                    myOffset++;

                    /* Determine new char */
                    char myNewChar = (char) ('a' + pOffset);

                    /* Add the join string for the underlying table */
                    ReferenceColumn myCol = (ReferenceColumn) myDef;
                    pBuilder.append(myCol.theDefinition.getOrderString(myNewChar, myOffset));
                }

                /* else standard column */
                else {
                    /* Build the column name */
                    pBuilder.append(myChar);
                    pBuilder.append(".");
                    pBuilder.append(myDef.getColumnName());
                    if (myDef.getSortOrder() == SortOrder.DESCENDING)
                        pBuilder.append(" DESC");
                }

                /* Note we have a column */
                myFirst = false;
            }
        }
    }

    /**
     * The longColumn Class
     */
    protected class LongColumn extends ColumnDefinition {
        /**
         * Constructor
         * @param pId the column id
         */
        private LongColumn(ReportField pId) {
            /* Record the column type */
            super(pId);
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("bigint");
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(Long pValue) {
            super.setValue(pValue);
        }

        /**
         * Get the value
         * @return the value
         */
        private Long getValue() {
            return (Long) theValue;
        }

        @Override
        protected void loadValue(int pIndex) throws SQLException {
            long myValue = theResults.getLong(pIndex);
            if ((myValue == 0) && (theResults.wasNull()))
                setValue(null);
            else
                setValue(myValue);
        }

        @Override
        protected void storeValue(int pIndex) throws SQLException {
            Long myValue = getValue();
            if (myValue == null)
                theStatement.setNull(pIndex, Types.BIGINT);
            else
                theStatement.setLong(pIndex, myValue);
        }
    }

    /**
     * The dateColumn Class
     */
    protected class DateColumn extends ColumnDefinition {
        /**
         * Constructor
         * @param pId the column id
         */
        private DateColumn(ReportField pId) {
            /* Record the column type */
            super(pId);
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("date");
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(Date pValue) {
            super.setValue(pValue);
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(DateDay pValue) {
            super.setValue((pValue == null) ? null : pValue.getDate());
        }

        /**
         * Get the value
         * @return the value
         */
        private Date getValue() {
            return (Date) theValue;
        }

        @Override
        protected void loadValue(int pIndex) throws SQLException {
            Date myValue = theResults.getDate(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(int pIndex) throws SQLException {
            java.sql.Date myDate = null;
            Date myValue = getValue();

            /* Build the date as a SQL date */
            if (myValue != null)
                myDate = new java.sql.Date(myValue.getTime());
            theStatement.setDate(pIndex, myDate);
        }
    }

    /**
     * The booleanColumn Class
     */
    protected class BooleanColumn extends ColumnDefinition {
        /**
         * Constructor
         * @param pId the column id
         */
        private BooleanColumn(ReportField pId) {
            /* Record the column type */
            super(pId);
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("bit");
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(Boolean pValue) {
            super.setValue(pValue);
        }

        /**
         * Get the value
         * @return the value
         */
        private Boolean getValue() {
            return (Boolean) theValue;
        }

        @Override
        protected void loadValue(int pIndex) throws SQLException {
            boolean myValue = theResults.getBoolean(pIndex);
            if ((myValue == false) && (theResults.wasNull()))
                setValue(null);
            else
                setValue(myValue);
        }

        @Override
        protected void storeValue(int pIndex) throws SQLException {
            Boolean myValue = getValue();
            if (myValue == null)
                theStatement.setNull(pIndex, Types.BIT);
            else
                theStatement.setBoolean(pIndex, myValue);
        }
    }

    /**
     * The stringColumn Class
     */
    protected class StringColumn extends ColumnDefinition {
        /**
         * The length of the column
         */
        private final int theLength;

        /**
         * Constructor
         * @param pId the column id
         * @param pLength the length
         */
        private StringColumn(ReportField pId, int pLength) {
            /* Record the column type */
            super(pId);
            theLength = pLength;
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("varchar(");
            pBuilder.append(theLength);
            pBuilder.append(')');
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(String pValue) {
            super.setValue(pValue);
        }

        /**
         * Get the value
         * @return the value
         */
        private String getValue() {
            return (String) theValue;
        }

        @Override
        protected void loadValue(int pIndex) throws SQLException {
            String myValue = theResults.getString(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(int pIndex) throws SQLException {
            theStatement.setString(pIndex, getValue());
        }
    }

    /**
     * The moneyColumn Class
     */
    protected class MoneyColumn extends StringColumn {
        /**
         * Constructor
         * @param pId the column id
         */
        private MoneyColumn(ReportField pId) {
            /* Record the column type */
            super(pId, 0);
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("money");
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(Money pValue) {
            String myString = null;
            if (pValue != null)
                myString = pValue.format(false);
            super.setValue(myString);
        }
    }

    /**
     * The rateColumn Class
     */
    protected class RateColumn extends StringColumn {
        /**
         * Constructor
         * @param pId the column id
         */
        private RateColumn(ReportField pId) {
            /* Record the column type */
            super(pId, 0);
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("decimal(4,2)");
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(Rate pValue) {
            String myString = null;
            if (pValue != null)
                myString = pValue.format(false);
            super.setValue(myString);
        }
    }

    /**
     * The binaryColumn Class
     */
    protected class BinaryColumn extends ColumnDefinition {
        /**
         * The length of the column
         */
        private final int theLength;

        /**
         * Constructor
         * @param pId the column id
         * @param pLength the length of the column
         */
        private BinaryColumn(ReportField pId, int pLength) {
            /* Record the column type */
            super(pId);
            theLength = pLength;
        }

        @Override
        protected void buildColumnType(StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append("varbinary(");
            pBuilder.append(theLength);
            pBuilder.append(')');
        }

        /**
         * Set the value
         * @param pValue the value
         */
        private void setValue(byte[] pValue) {
            super.setValue(pValue);
        }

        /**
         * Get the value
         * @return the value
         */
        private byte[] getValue() {
            return (byte[]) theValue;
        }

        @Override
        protected void loadValue(int pIndex) throws SQLException {
            byte[] myValue = theResults.getBytes(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(int pIndex) throws SQLException {
            theStatement.setBytes(pIndex, getValue());
        }
    }

    /**
     * Sort order indication
     */
    public enum SortOrder {
        ASCENDING, DESCENDING;
    }
}
