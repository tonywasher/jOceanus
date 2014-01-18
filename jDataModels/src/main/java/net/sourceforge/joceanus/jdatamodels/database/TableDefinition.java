/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jdatamodels.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamodels.JPrometheusLogicException;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.BinaryColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.BooleanColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.DateColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.DilutionColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.IdColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.IntegerColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.LongColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.MoneyColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.PriceColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.RateColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.RatioColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.ReferenceColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.StringColumn;
import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.UnitsColumn;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jgordianknot.crypto.CipherSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database field definition class. Maps each dataType to a database field.
 */
public class TableDefinition {
    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * The index prefix.
     */
    protected static final String PREFIX_INDEX = "idx_";

    /**
     * The quote string.
     */
    protected static final String QUOTE_STRING = "\"";

    /**
     * The Table name.
     */
    private final String theTableName;

    /**
     * The Database driver.
     */
    private final JDBCDriver theDriver;

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
    private final Map<JDataField, ColumnDefinition> theMap;

    /**
     * The prepared statement for the insert/update.
     */
    private PreparedStatement theStatement = null;

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
    protected Map<JDataField, ColumnDefinition> getMap() {
        return theMap;
    }

    /**
     * Is the table indexed.
     * @return true/false
     */
    protected boolean isIndexed() {
        return !theSortList.isEmpty();
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
     * Obtain the driver.
     * @return the driver
     */
    protected JDBCDriver getDriver() {
        return theDriver;
    }

    /**
     * Note that we have a sort on reference.
     */
    protected void setSortOnReference() {
        sortOnReference = true;
    }

    /**
     * Constructor.
     * @param pDriver the driver
     * @param pName the table name
     */
    protected TableDefinition(final JDBCDriver pDriver,
                              final String pName) {
        /* Record the name and driver */
        theTableName = pName;
        theDriver = pDriver;

        /* Create the column list */
        theList = new ArrayList<ColumnDefinition>();

        /* Create the sort list */
        theSortList = new ArrayList<ColumnDefinition>();

        /* Create the initial column map */
        theMap = new HashMap<JDataField, ColumnDefinition>();

        /* Add an Id column */
        theList.add(new IdColumn(this));
    }

    /**
     * Add a reference column.
     * @param pId the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public ReferenceColumn addReferenceColumn(final JDataField pId,
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
    public ReferenceColumn addNullReferenceColumn(final JDataField pId,
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
    public IntegerColumn addIntegerColumn(final JDataField pId) {
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
    public IntegerColumn addNullIntegerColumn(final JDataField pId) {
        IntegerColumn myColumn = addIntegerColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a long column.
     * @param pId the column id
     * @return the long column
     */
    public LongColumn addLongColumn(final JDataField pId) {
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
    public LongColumn addNullLongColumn(final JDataField pId) {
        LongColumn myColumn = addLongColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a boolean column.
     * @param pId the column id
     * @return the boolean column
     */
    public BooleanColumn addBooleanColumn(final JDataField pId) {
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
    public BooleanColumn addNullBooleanColumn(final JDataField pId) {
        BooleanColumn myColumn = addBooleanColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a date column.
     * @param pId the column id
     * @return the date column
     */
    public DateColumn addDateColumn(final JDataField pId) {
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
    public DateColumn addNullDateColumn(final JDataField pId) {
        DateColumn myColumn = addDateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a money column.
     * @param pId the column id
     * @return the money column
     */
    public MoneyColumn addMoneyColumn(final JDataField pId) {
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
    public MoneyColumn addNullMoneyColumn(final JDataField pId) {
        MoneyColumn myColumn = addMoneyColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     * @param pId the column id
     * @return the rate column
     */
    public RateColumn addRateColumn(final JDataField pId) {
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
    public RateColumn addNullRateColumn(final JDataField pId) {
        RateColumn myColumn = addRateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     * @param pId the column id
     * @return the rate column
     */
    public RatioColumn addRatioColumn(final JDataField pId) {
        /* Create the new rate column */
        RatioColumn myColumn = new RatioColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a rate column, which can be null.
     * @param pId the column id
     * @return the rate column
     */
    public RatioColumn addNullRatioColumn(final JDataField pId) {
        RatioColumn myColumn = addRatioColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a binary column.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addBinaryColumn(final JDataField pId,
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
    public BinaryColumn addNullBinaryColumn(final JDataField pId,
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
    public BinaryColumn addEncryptedColumn(final JDataField pId,
                                           final int pLength) {
        /* Create the new binary column */
        BinaryColumn myColumn = new BinaryColumn(this, pId, CipherSet.IVSIZE
                                                            + CipherSet.KEYIDLEN
                                                            + CipherSet.getEncryptionLength(2 * pLength));

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
    public BinaryColumn addNullEncryptedColumn(final JDataField pId,
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
    public StringColumn addStringColumn(final JDataField pId,
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
    public StringColumn addNullStringColumn(final JDataField pId,
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
        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
            myDef.locateReference(pTables);
        }
    }

    /**
     * Load results.
     * @param pResults the result set
     * @throws SQLException on error
     */
    protected void loadResults(final ResultSet pResults) throws SQLException {
        /* clear values */
        clearValues();

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        int myIndex = 1;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
            myDef.loadValue(pResults, myIndex++);
        }
    }

    /**
     * Build column error string.
     * @param pCol the column definition.
     * @return the error string
     */
    private String getColumnError(final ColumnDefinition pCol) {
        return "Column "
               + pCol.getColumnName()
               + " in table "
               + theTableName;
    }

    /**
     * Insert values.
     * @param pStmt the statement
     * @throws SQLException on error
     * @throws JOceanusException on error
     */
    protected void insertValues(final PreparedStatement pStmt) throws SQLException, JOceanusException {
        /* Store the Statement */
        theStatement = pStmt;

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        int myIndex = 1;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();

            /* Reject if the value is not set */
            if (!myDef.isValueSet()) {
                throw new JPrometheusLogicException(getColumnError(myDef)
                                                    + " has no value for insert");
            }

            myDef.storeValue(theStatement, myIndex++);
        }
    }

    /**
     * Update values.
     * @param pStmt the statement
     * @throws SQLException on error
     * @throws JOceanusException on error
     */
    protected void updateValues(final PreparedStatement pStmt) throws SQLException, JOceanusException {
        ColumnDefinition myId = null;

        /* Store the Statement */
        theStatement = pStmt;

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        int myIndex = 1;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();

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
     * @throws JOceanusException on error
     */
    public Integer getIntegerValue(final JDataField pId) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Integer type");
        }

        /* Return the value */
        IntegerColumn myIntCol = (IntegerColumn) myCol;
        return myIntCol.getValue();
    }

    /**
     * Get Long value for column.
     * @param pId the column id
     * @return the long value
     * @throws JOceanusException on error
     */
    public Long getLongValue(final JDataField pId) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Long type");
        }

        /* Return the value */
        LongColumn myLongCol = (LongColumn) myCol;
        return myLongCol.getValue();
    }

    /**
     * Get Date value for column.
     * @param pId the column id
     * @return the Date value
     * @throws JOceanusException on error
     */
    public JDateDay getDateValue(final JDataField pId) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a date column */
        if (!(myCol instanceof DateColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Date type");
        }

        /* Return the value */
        DateColumn myDateCol = (DateColumn) myCol;
        return myDateCol.getValue();
    }

    /**
     * Get Boolean value for column.
     * @param pId the column id
     * @return the boolean value
     * @throws JOceanusException on error
     */
    public Boolean getBooleanValue(final JDataField pId) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn)) {
            throw new JPrometheusLogicException("Column "
                                                + getColumnError(myCol)
                                                + " is not Boolean type");
        }

        /* Return the value */
        BooleanColumn myBoolCol = (BooleanColumn) myCol;
        return myBoolCol.getValue();
    }

    /**
     * Get String value for column.
     * @param pId the column id
     * @return the String value
     * @throws JOceanusException on error
     */
    public String getStringValue(final JDataField pId) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not String type");
        }

        /* Return the value */
        StringColumn myStringCol = (StringColumn) myCol;
        return myStringCol.getValue();
    }

    /**
     * Get Money value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Money value
     * @throws JOceanusException on error
     */
    public JMoney getMoneyValue(final JDataField pId,
                                final JDataFormatter pFormatter) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof MoneyColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not money type");
        }

        /* Access the value */
        MoneyColumn myMoneyCol = (MoneyColumn) myCol;
        return myMoneyCol.getValue(pFormatter);
    }

    /**
     * Get Price value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the price value
     * @throws JOceanusException on error
     */
    public JPrice getPriceValue(final JDataField pId,
                                final JDataFormatter pFormatter) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a price column */
        if (!(myCol instanceof PriceColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Price type");
        }

        /* Access the value */
        PriceColumn myPriceCol = (PriceColumn) myCol;
        return myPriceCol.getValue(pFormatter);
    }

    /**
     * Get Rate value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the rate value
     * @throws JOceanusException on error
     */
    public JRate getRateValue(final JDataField pId,
                              final JDataFormatter pFormatter) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof RateColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Rate type");
        }

        /* Access the value */
        RateColumn myRateCol = (RateColumn) myCol;
        return myRateCol.getValue(pFormatter);
    }

    /**
     * Get Units value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Units value
     * @throws JOceanusException on error
     */
    public JUnits getUnitsValue(final JDataField pId,
                                final JDataFormatter pFormatter) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a units column */
        if (!(myCol instanceof UnitsColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Units type");
        }

        /* Access the value */
        UnitsColumn myUnitsCol = (UnitsColumn) myCol;
        return myUnitsCol.getValue(pFormatter);
    }

    /**
     * Get Dilution value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Dilution value
     * @throws JOceanusException on error
     */
    public JDilution getDilutionValue(final JDataField pId,
                                      final JDataFormatter pFormatter) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a dilution column */
        if (!(myCol instanceof DilutionColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Dilution type");
        }

        /* Access the value */
        DilutionColumn myDilutionCol = (DilutionColumn) myCol;
        return myDilutionCol.getValue(pFormatter);
    }

    /**
     * Get Ratio value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Ratio value
     * @throws JOceanusException on error
     */
    public JRatio getRatioValue(final JDataField pId,
                                final JDataFormatter pFormatter) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a ratio column */
        if (!(myCol instanceof RatioColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Ratio type");
        }

        /* Access the value */
        RatioColumn myRatioCol = (RatioColumn) myCol;
        return myRatioCol.getValue(pFormatter);
    }

    /**
     * Get Binary value for column.
     * @param pId the column id
     * @return the binary value
     * @throws JOceanusException on error
     */
    public byte[] getBinaryValue(final JDataField pId) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof BinaryColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Binary type");
        }

        /* Return the value */
        BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        return myBinaryCol.getValue();
    }

    /**
     * Set Integer value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setIntegerValue(final JDataField pId,
                                final Integer pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Integer type");
        }

        /* Set the value */
        IntegerColumn myIntCol = (IntegerColumn) myCol;
        myIntCol.setValue(pValue);
    }

    /**
     * Set Long value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setLongValue(final JDataField pId,
                             final Long pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Long type");
        }

        /* Set the value */
        LongColumn myLongCol = (LongColumn) myCol;
        myLongCol.setValue(pValue);
    }

    /**
     * Set Boolean value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setBooleanValue(final JDataField pId,
                                final Boolean pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Boolean type");
        }

        /* Set the value */
        BooleanColumn myBoolCol = (BooleanColumn) myCol;
        myBoolCol.setValue(pValue);
    }

    /**
     * Set Date value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setDateValue(final JDataField pId,
                             final JDateDay pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a Date column */
        if (!(myCol instanceof DateColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Date type");
        }

        /* Set the value */
        DateColumn myDateCol = (DateColumn) myCol;
        myDateCol.setValue(pValue);
    }

    /**
     * Set String value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setStringValue(final JDataField pId,
                               final String pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not String type");
        }

        /* Set the value */
        StringColumn myStringCol = (StringColumn) myCol;
        myStringCol.setValue(pValue);
    }

    /**
     * Set Binary value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setBinaryValue(final JDataField pId,
                               final byte[] pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a binary column */
        if (!(myCol instanceof BinaryColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Binary type");
        }

        /* Set the value */
        BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        myBinaryCol.setValue(pValue);
    }

    /**
     * Set Money value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setMoneyValue(final JDataField pId,
                              final JMoney pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof MoneyColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Money type");
        }

        /* Set the value */
        MoneyColumn myMoneyCol = (MoneyColumn) myCol;
        myMoneyCol.setValue(pValue);
    }

    /**
     * Set Rate value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setRateValue(final JDataField pId,
                             final JRate pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof RateColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Rate type");
        }

        /* Set the value */
        RateColumn myRateCol = (RateColumn) myCol;
        myRateCol.setValue(pValue);
    }

    /**
     * Set Ratio value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    public void setRatioValue(final JDataField pId,
                              final JRatio pValue) throws JOceanusException {
        /* Obtain the correct id */
        ColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a ratio column */
        if (!(myCol instanceof RatioColumn)) {
            throw new JPrometheusLogicException(getColumnError(myCol)
                                                + " is not Ratio type");
        }

        /* Set the value */
        RatioColumn myRatioCol = (RatioColumn) myCol;
        myRatioCol.setValue(pValue);
    }

    /**
     * Locate column for id.
     * @param pId the id of the column
     * @return the column
     * @throws JOceanusException on error
     */
    private ColumnDefinition getColumnForId(final JDataField pId) throws JOceanusException {
        /* Access the definition */
        ColumnDefinition myDef = theMap.get(pId);

        /* Check that the id is in range and present */
        if (myDef == null) {
            throw new JPrometheusLogicException("Invalid Column Id: "
                                                + pId
                                                + " for "
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

        /* Build the initial create */
        myBuilder.append("create table ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" (");

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
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

        /* Return null if we are not indexed */
        if (!isIndexed()) {
            return null;
        }

        /* Build the initial create */
        myBuilder.append("create index ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(PREFIX_INDEX);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" on ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" (");

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theSortList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            myBuilder.append(QUOTE_STRING);
            myBuilder.append(myDef.getColumnName());
            myBuilder.append(QUOTE_STRING);
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
        return theDriver.getDropTableCommand(theTableName);
    }

    /**
     * Build the drop index string for the table.
     * @return the SQL string
     */
    protected String getDropIndexString() {
        /* Return null if we are not indexed */
        if (!isIndexed()) {
            return null;
        }

        /* Build the drop command */
        return theDriver.getDropIndexCommand(theTableName);
    }

    /**
     * Build the load string for a list of columns.
     * @return the SQL string
     */
    protected String getLoadString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial select */
        myBuilder.append("select ");

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            if (sortOnReference) {
                myBuilder.append("a.");
            }
            myBuilder.append(QUOTE_STRING);
            myBuilder.append(myDef.getColumnName());
            myBuilder.append(QUOTE_STRING);
            myFirst = false;
        }

        /* Close the statement */
        myBuilder.append(" from ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
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
            myBuilder.append(getOrderString('a', 0));
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

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theSortList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            /* Access next column and skip if not reference column */
            ColumnDefinition myDef = myIterator.next();
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

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theSortList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
            /* Handle secondary columns */
            if (!myFirst) {
                myBuilder.append(", ");
            }

            /* If we are using prefixes */
            if ((sortOnReference)
                || (pChar > 'a')) {
                /* If this is a reference column */
                if (myDef instanceof ReferenceColumn) {
                    /* Handle Reference column */
                    ReferenceColumn myCol = (ReferenceColumn) myDef;
                    myCol.buildOrderString(myBuilder, pOffset + 1);
                } else {
                    /* Handle standard column with prefix */
                    myBuilder.append(pChar);
                    myBuilder.append(".");
                    myBuilder.append(QUOTE_STRING);
                    myBuilder.append(myDef.getColumnName());
                    myBuilder.append(QUOTE_STRING);
                    if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                        myBuilder.append(" DESC");
                    }
                }
            } else {
                /* Handle standard column */
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(myDef.getColumnName());
                myBuilder.append(QUOTE_STRING);
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

        /* Build the initial insert */
        myBuilder.append("insert into ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" (");

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
                myValues.append(", ");
            }
            myBuilder.append(QUOTE_STRING);
            myBuilder.append(myDef.getColumnName());
            myBuilder.append(QUOTE_STRING);
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
     * @throws JOceanusException on error
     */
    protected String getUpdateString() throws JOceanusException {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial update */
        myBuilder.append("update ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" set ");

        /* Create the iterator */
        Iterator<ColumnDefinition> myIterator = theList.iterator();
        ColumnDefinition myId = null;
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            ColumnDefinition myDef = myIterator.next();

            /* If this is the Id record */
            if (myDef instanceof IdColumn) {
                /* Reject if the value is not set */
                if (!myDef.isValueSet()) {
                    throw new JPrometheusLogicException(getColumnError(myDef)
                                                        + " has no value for update");
                }

                /* Remember the column */
                myId = myDef;

                /* If this column is to be updated */
            } else if (myDef.isValueSet()) {
                /* Add the update of this column */
                if (!myFirst) {
                    myBuilder.append(", ");
                }
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(myDef.getColumnName());
                myBuilder.append(QUOTE_STRING);
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
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(myId.getColumnName());
        myBuilder.append(QUOTE_STRING);
        myBuilder.append("=?");
        return myBuilder.toString();
    }

    /**
     * Build the delete string for a table.
     * @return the SQL string
     */
    protected String getDeleteString() {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("delete from ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" where ");

        /* Access the id definition */
        ColumnDefinition myId = theList.get(0);

        /* Build the rest of the command */
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(myId.getColumnName());
        myBuilder.append(QUOTE_STRING);
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
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
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
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        return myBuilder.toString();
    }
}
