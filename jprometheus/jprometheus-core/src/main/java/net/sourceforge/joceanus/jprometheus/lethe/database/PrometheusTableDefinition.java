/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jprometheus.PrometheusLogicException;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusJDBCDriver;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.BinaryColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.BooleanColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.DateColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.DilutionColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.IdColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.IntegerColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.LongColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.MoneyColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.PriceColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.RateColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.RatioColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.ReferenceColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.StringColumn;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition.UnitsColumn;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Database field definition class. Maps each dataType to a database field.
 */
public class PrometheusTableDefinition {
    /**
     * The index prefix.
     */
    private static final String PREFIX_INDEX = "idx_";

    /**
     * The quote string.
     */
    protected static final String QUOTE_STRING = "\"";

    /**
     * The Descending string.
     */
    protected static final String STR_DESC = " DESC";

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * The Table name.
     */
    private final String theTableName;

    /**
     * The Database driver.
     */
    private final PrometheusJDBCDriver theDriver;

    /**
     * The Column Definitions.
     */
    private final List<PrometheusColumnDefinition> theList;

    /**
     * The Sort List.
     */
    private final List<PrometheusColumnDefinition> theSortList;

    /**
     * Are we sorting on a reference column.
     */
    private boolean sortOnReference;

    /**
     * The array list for the columns.
     */
    private final Map<MetisField, PrometheusColumnDefinition> theMap;

    /**
     * The prepared statement for the insert/update.
     */
    private PreparedStatement theStatement;

    /**
     * Constructor.
     * @param pDriver the driver
     * @param pName the table name
     */
    protected PrometheusTableDefinition(final PrometheusJDBCDriver pDriver,
                                        final String pName) {
        /* Record the name and driver */
        theTableName = pName;
        theDriver = pDriver;

        /* Create the column list */
        theList = new ArrayList<>();

        /* Create the sort list */
        theSortList = new ArrayList<>();

        /* Create the initial column map */
        theMap = new HashMap<>();

        /* Add an Id column */
        theList.add(new IdColumn(this));
    }

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
    protected Map<MetisField, PrometheusColumnDefinition> getMap() {
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
    protected List<PrometheusColumnDefinition> getColumns() {
        return theList;
    }

    /**
     * Sort List.
     * @return the sort list
     */
    protected List<PrometheusColumnDefinition> getSortList() {
        return theSortList;
    }

    /**
     * Obtain the driver.
     * @return the driver
     */
    protected PrometheusJDBCDriver getDriver() {
        return theDriver;
    }

    /**
     * Note that we have a sort on reference.
     */
    protected void setSortOnReference() {
        sortOnReference = true;
    }

    /**
     * Add a reference column.
     * @param pId the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public ReferenceColumn addReferenceColumn(final MetisField pId,
                                              final String pRef) {
        /* Create the new reference column */
        final ReferenceColumn myColumn = new ReferenceColumn(this, pId, pRef);

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
    public ReferenceColumn addNullReferenceColumn(final MetisField pId,
                                                  final String pRef) {
        final ReferenceColumn myColumn = addReferenceColumn(pId, pRef);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an integer column.
     * @param pId the column id
     * @return the integer column
     */
    public IntegerColumn addIntegerColumn(final MetisField pId) {
        /* Create the new integer column */
        final IntegerColumn myColumn = new IntegerColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add an integer column, which can be null.
     * @param pId the column id
     * @return the integer column
     */
    public IntegerColumn addNullIntegerColumn(final MetisField pId) {
        final IntegerColumn myColumn = addIntegerColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a long column.
     * @param pId the column id
     * @return the long column
     */
    public LongColumn addLongColumn(final MetisField pId) {
        /* Create the new long column */
        final LongColumn myColumn = new LongColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a long column, which can be null.
     * @param pId the column id
     * @return the long column
     */
    public LongColumn addNullLongColumn(final MetisField pId) {
        final LongColumn myColumn = addLongColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a boolean column.
     * @param pId the column id
     * @return the boolean column
     */
    public BooleanColumn addBooleanColumn(final MetisField pId) {
        /* Create the new boolean column */
        final BooleanColumn myColumn = new BooleanColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a boolean column, which can be null.
     * @param pId the column id
     * @return the boolean column
     */
    public BooleanColumn addNullBooleanColumn(final MetisField pId) {
        final BooleanColumn myColumn = addBooleanColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a date column.
     * @param pId the column id
     * @return the date column
     */
    public DateColumn addDateColumn(final MetisField pId) {
        /* Create the new date column */
        final DateColumn myColumn = new DateColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a date column, which can be null.
     * @param pId the column id
     * @return the date column
     */
    public DateColumn addNullDateColumn(final MetisField pId) {
        final DateColumn myColumn = addDateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a money column.
     * @param pId the column id
     * @return the money column
     */
    public MoneyColumn addMoneyColumn(final MetisField pId) {
        /* Create the new money column */
        final MoneyColumn myColumn = new MoneyColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a money column, which can be null.
     * @param pId the column id
     * @return the money column
     */
    public MoneyColumn addNullMoneyColumn(final MetisField pId) {
        final MoneyColumn myColumn = addMoneyColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     * @param pId the column id
     * @return the rate column
     */
    public RateColumn addRateColumn(final MetisField pId) {
        /* Create the new rate column */
        final RateColumn myColumn = new RateColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a rate column, which can be null.
     * @param pId the column id
     * @return the rate column
     */
    public RateColumn addNullRateColumn(final MetisField pId) {
        final RateColumn myColumn = addRateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     * @param pId the column id
     * @return the rate column
     */
    public RatioColumn addRatioColumn(final MetisField pId) {
        /* Create the new rate column */
        final RatioColumn myColumn = new RatioColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a rate column, which can be null.
     * @param pId the column id
     * @return the rate column
     */
    public RatioColumn addNullRatioColumn(final MetisField pId) {
        final RatioColumn myColumn = addRatioColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a binary column.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addBinaryColumn(final MetisField pId,
                                        final int pLength) {
        /* Create the new binary column */
        final BinaryColumn myColumn = new BinaryColumn(this, pId, pLength);

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
    public BinaryColumn addNullBinaryColumn(final MetisField pId,
                                            final int pLength) {
        final BinaryColumn myColumn = addBinaryColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an encrypted column.
     * @param pId the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public BinaryColumn addEncryptedColumn(final MetisField pId,
                                           final int pLength) {
        /* Create the new binary column */
        final BinaryColumn myColumn = new BinaryColumn(this, pId, GordianSecurityManager.getKeySetEncryptionLength(pLength, false));

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
    public BinaryColumn addNullEncryptedColumn(final MetisField pId,
                                               final int pLength) {
        final BinaryColumn myColumn = addEncryptedColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a string column.
     * @param pId the column id
     * @param pLength the character length
     * @return the binary column
     */
    public StringColumn addStringColumn(final MetisField pId,
                                        final int pLength) {
        /* Create the new string column */
        final StringColumn myColumn = new StringColumn(this, pId, pLength);

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
    public StringColumn addNullStringColumn(final MetisField pId,
                                            final int pLength) {
        final StringColumn myColumn = addStringColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Locate reference.
     * @param pTables the list of defined tables
     */
    protected void resolveReferences(final List<PrometheusTableDataItem<?, ?>> pTables) {
        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
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
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        int myIndex = 1;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
            myDef.loadValue(pResults, myIndex++);
        }
    }

    /**
     * Build column error string.
     * @param pCol the column definition.
     * @return the error string
     */
    private String getColumnError(final PrometheusColumnDefinition pCol) {
        return "Column " + pCol.getColumnName() + " in table " + theTableName;
    }

    /**
     * Insert values.
     * @param pStmt the statement
     * @throws SQLException on error
     * @throws OceanusException on error
     */
    protected void insertValues(final PreparedStatement pStmt) throws SQLException, OceanusException {
        /* Store the Statement */
        theStatement = pStmt;

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        int myIndex = 1;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();

            /* Reject if the value is not set */
            if (!myDef.isValueSet()) {
                throw new PrometheusLogicException(getColumnError(myDef) + " has no value for insert");
            }

            myDef.storeValue(theStatement, myIndex++);
        }
    }

    /**
     * Update values.
     * @param pStmt the statement
     * @throws SQLException on error
     */
    protected void updateValues(final PreparedStatement pStmt) throws SQLException {
        PrometheusColumnDefinition myId = null;

        /* Store the Statement */
        theStatement = pStmt;

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        int myIndex = 1;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();

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
        if (myId != null) {
            myId.storeValue(theStatement, myIndex);
        }
    }

    /**
     * Clear values for table.
     */
    protected void clearValues() {
        /* Loop over the Column Definitions */
        for (PrometheusColumnDefinition myDef : theList) {
            /* Clear value */
            myDef.clearValue();
        }
    }

    /**
     * Get Integer value for column.
     * @param pId the column id
     * @return the integer value
     * @throws OceanusException on error
     */
    public Integer getIntegerValue(final MetisField pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Integer type");
        }

        /* Return the value */
        final IntegerColumn myIntCol = (IntegerColumn) myCol;
        return myIntCol.getValue();
    }

    /**
     * Get Long value for column.
     * @param pId the column id
     * @return the long value
     * @throws OceanusException on error
     */
    public Long getLongValue(final MetisField pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Long type");
        }

        /* Return the value */
        final LongColumn myLongCol = (LongColumn) myCol;
        return myLongCol.getValue();
    }

    /**
     * Get Date value for column.
     * @param pId the column id
     * @return the Date value
     * @throws OceanusException on error
     */
    public TethysDate getDateValue(final MetisField pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a date column */
        if (!(myCol instanceof DateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Date type");
        }

        /* Return the value */
        final DateColumn myDateCol = (DateColumn) myCol;
        return myDateCol.getValue();
    }

    /**
     * Get Boolean value for column.
     * @param pId the column id
     * @return the boolean value
     * @throws OceanusException on error
     */
    public Boolean getBooleanValue(final MetisField pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn)) {
            throw new PrometheusLogicException("Column " + getColumnError(myCol) + " is not Boolean type");
        }

        /* Return the value */
        final BooleanColumn myBoolCol = (BooleanColumn) myCol;
        return myBoolCol.getValue();
    }

    /**
     * Get String value for column.
     * @param pId the column id
     * @return the String value
     * @throws OceanusException on error
     */
    public String getStringValue(final MetisField pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not String type");
        }

        /* Return the value */
        final StringColumn myStringCol = (StringColumn) myCol;
        return myStringCol.getValue();
    }

    /**
     * Get Money value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Money value
     * @throws OceanusException on error
     */
    public TethysMoney getMoneyValue(final MetisField pId,
                                     final MetisDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof MoneyColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not money type");
        }

        /* Access the value */
        final MoneyColumn myMoneyCol = (MoneyColumn) myCol;
        return myMoneyCol.getValue(pFormatter);
    }

    /**
     * Get Price value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the price value
     * @throws OceanusException on error
     */
    public TethysPrice getPriceValue(final MetisField pId,
                                     final MetisDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a price column */
        if (!(myCol instanceof PriceColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Price type");
        }

        /* Access the value */
        final PriceColumn myPriceCol = (PriceColumn) myCol;
        return myPriceCol.getValue(pFormatter);
    }

    /**
     * Get Rate value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the rate value
     * @throws OceanusException on error
     */
    public TethysRate getRateValue(final MetisField pId,
                                   final MetisDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof RateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Rate type");
        }

        /* Access the value */
        final RateColumn myRateCol = (RateColumn) myCol;
        return myRateCol.getValue(pFormatter);
    }

    /**
     * Get Units value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Units value
     * @throws OceanusException on error
     */
    public TethysUnits getUnitsValue(final MetisField pId,
                                     final MetisDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a units column */
        if (!(myCol instanceof UnitsColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Units type");
        }

        /* Access the value */
        final UnitsColumn myUnitsCol = (UnitsColumn) myCol;
        return myUnitsCol.getValue(pFormatter);
    }

    /**
     * Get Dilution value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Dilution value
     * @throws OceanusException on error
     */
    public TethysDilution getDilutionValue(final MetisField pId,
                                           final MetisDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a dilution column */
        if (!(myCol instanceof DilutionColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Dilution type");
        }

        /* Access the value */
        final DilutionColumn myDilutionCol = (DilutionColumn) myCol;
        return myDilutionCol.getValue(pFormatter);
    }

    /**
     * Get Ratio value for column.
     * @param pId the column id
     * @param pFormatter the data formatter
     * @return the Ratio value
     * @throws OceanusException on error
     */
    public TethysRatio getRatioValue(final MetisField pId,
                                     final MetisDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a ratio column */
        if (!(myCol instanceof RatioColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Ratio type");
        }

        /* Access the value */
        final RatioColumn myRatioCol = (RatioColumn) myCol;
        return myRatioCol.getValue(pFormatter);
    }

    /**
     * Get Binary value for column.
     * @param pId the column id
     * @return the binary value
     * @throws OceanusException on error
     */
    public byte[] getBinaryValue(final MetisField pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof BinaryColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Binary type");
        }

        /* Return the value */
        final BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        return myBinaryCol.getValue();
    }

    /**
     * Set Integer value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setIntegerValue(final MetisField pId,
                                final Integer pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof IntegerColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Integer type");
        }

        /* Set the value */
        final IntegerColumn myIntCol = (IntegerColumn) myCol;
        myIntCol.setValue(pValue);
    }

    /**
     * Set Long value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setLongValue(final MetisField pId,
                             final Long pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof LongColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Long type");
        }

        /* Set the value */
        final LongColumn myLongCol = (LongColumn) myCol;
        myLongCol.setValue(pValue);
    }

    /**
     * Set Boolean value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setBooleanValue(final MetisField pId,
                                final Boolean pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof BooleanColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Boolean type");
        }

        /* Set the value */
        final BooleanColumn myBoolCol = (BooleanColumn) myCol;
        myBoolCol.setValue(pValue);
    }

    /**
     * Set Date value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setDateValue(final MetisField pId,
                             final TethysDate pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a Date column */
        if (!(myCol instanceof DateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Date type");
        }

        /* Set the value */
        final DateColumn myDateCol = (DateColumn) myCol;
        myDateCol.setValue(pValue);
    }

    /**
     * Set String value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setStringValue(final MetisField pId,
                               final String pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof StringColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not String type");
        }

        /* Set the value */
        final StringColumn myStringCol = (StringColumn) myCol;
        myStringCol.setValue(pValue);
    }

    /**
     * Set Binary value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setBinaryValue(final MetisField pId,
                               final byte[] pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a binary column */
        if (!(myCol instanceof BinaryColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Binary type");
        }

        /* Set the value */
        final BinaryColumn myBinaryCol = (BinaryColumn) myCol;
        myBinaryCol.setValue(pValue);
    }

    /**
     * Set Money value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setMoneyValue(final MetisField pId,
                              final TethysMoney pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof MoneyColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Money type");
        }

        /* Set the value */
        final MoneyColumn myMoneyCol = (MoneyColumn) myCol;
        myMoneyCol.setValue(pValue);
    }

    /**
     * Set Rate value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setRateValue(final MetisField pId,
                             final TethysRate pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof RateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Rate type");
        }

        /* Set the value */
        final RateColumn myRateCol = (RateColumn) myCol;
        myRateCol.setValue(pValue);
    }

    /**
     * Set Ratio value for column.
     * @param pId the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setRatioValue(final MetisField pId,
                              final TethysRatio pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a ratio column */
        if (!(myCol instanceof RatioColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Ratio type");
        }

        /* Set the value */
        final RatioColumn myRatioCol = (RatioColumn) myCol;
        myRatioCol.setValue(pValue);
    }

    /**
     * Locate column for id.
     * @param pId the id of the column
     * @return the column
     * @throws OceanusException on error
     */
    private PrometheusColumnDefinition getColumnForId(final MetisField pId) throws OceanusException {
        /* Access the definition */
        final PrometheusColumnDefinition myDef = theMap.get(pId);

        /* Check that the id is in range and present */
        if (myDef == null) {
            throw new PrometheusLogicException("Invalid Column Id: " + pId + " for " + theTableName);
        }

        /* Return the column definition */
        return myDef;
    }

    /**
     * Build the create table string for the table.
     * @return the SQL string
     */
    protected String getCreateTableString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial create */
        myBuilder.append("create table ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" (");

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

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
        final Iterator<PrometheusColumnDefinition> myIterator = theSortList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
            if (!myFirst) {
                myBuilder.append(", ");
            }
            myBuilder.append(QUOTE_STRING);
            myBuilder.append(myDef.getColumnName());
            myBuilder.append(QUOTE_STRING);
            if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                myBuilder.append(STR_DESC);
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        switch (theDriver) {
            case SQLSERVER:
                myBuilder.append("if exists (select * from sys.tables where name = '");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(theTableName);
                myBuilder.append(QUOTE_STRING);
                myBuilder.append("') drop table ");
                myBuilder.append(theTableName);
                break;
            case MYSQL:
            case POSTGRESQL:
            default:
                myBuilder.append("drop table if exists ");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(theTableName);
                myBuilder.append(QUOTE_STRING);
                break;
        }

        /* Return the command */
        return myBuilder.toString();
    }

    /**
     * Build the drop index string for the table.
     * @return the SQL string
     */
    protected String getDropIndexString() {
        /* Return null if we are not indexed */
        if (!isIndexed() || !theDriver.explicitDropIndex()) {
            return null;
        }

        /* Build the drop command */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        switch (theDriver) {
            case SQLSERVER:
                myBuilder.append("if exists (select * from sys.indexes where name = '");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(PREFIX_INDEX);
                myBuilder.append(theTableName);
                myBuilder.append(QUOTE_STRING);
                myBuilder.append("') drop index ");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(PREFIX_INDEX);
                myBuilder.append(theTableName);
                myBuilder.append(QUOTE_STRING);
                break;
            case MYSQL:
            case POSTGRESQL:
            default:
                myBuilder.append("drop index if exists ");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(PREFIX_INDEX);
                myBuilder.append(theTableName);
                myBuilder.append(QUOTE_STRING);
                break;
        }

        /* Return the command */
        return myBuilder.toString();
    }

    /**
     * Build the load string for a list of columns.
     * @return the SQL string
     */
    protected String getLoadString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial select */
        myBuilder.append("select ");

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theSortList.iterator();

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            /* Access next column and skip if not reference column */
            final PrometheusColumnDefinition myDef = myIterator.next();
            if (!(myDef instanceof ReferenceColumn)) {
                continue;
            }

            /* Add the join */
            final ReferenceColumn myCol = (ReferenceColumn) myDef;
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theSortList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
            /* Handle secondary columns */
            if (!myFirst) {
                myBuilder.append(", ");
            }

            /* If we are using prefixes */
            if ((sortOnReference) || (pChar > 'a')) {
                /* If this is a reference column */
                if (myDef instanceof ReferenceColumn) {
                    /* Handle Reference column */
                    final ReferenceColumn myCol = (ReferenceColumn) myDef;
                    myCol.buildOrderString(myBuilder, pOffset + 1);
                } else {
                    /* Handle standard column with prefix */
                    myBuilder.append(pChar);
                    myBuilder.append(".");
                    myBuilder.append(QUOTE_STRING);
                    myBuilder.append(myDef.getColumnName());
                    myBuilder.append(QUOTE_STRING);
                    if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                        myBuilder.append(STR_DESC);
                    }
                }
            } else {
                /* Handle standard column */
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(myDef.getColumnName());
                myBuilder.append(QUOTE_STRING);
                if (myDef.getSortOrder() == SortOrder.DESCENDING) {
                    myBuilder.append(STR_DESC);
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        final StringBuilder myValues = new StringBuilder(BUFFER_LEN);

        /* Build the initial insert */
        myBuilder.append("insert into ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" (");

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();
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
     * @throws OceanusException on error
     */
    protected String getUpdateString() throws OceanusException {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial update */
        myBuilder.append("update ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" set ");

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        PrometheusColumnDefinition myId = null;
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();

            /* If this is the Id record */
            if (myDef instanceof IdColumn) {
                /* Reject if the value is not set */
                if (!myDef.isValueSet()) {
                    throw new PrometheusLogicException(getColumnError(myDef) + " has no value for update");
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
        if (myFirst || myId == null) {
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("delete from ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(" where ");

        /* Access the id definition */
        final PrometheusColumnDefinition myId = theList.get(0);

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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("select count(*) from ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(theTableName);
        myBuilder.append(QUOTE_STRING);
        return myBuilder.toString();
    }

    /**
     * SortOrder.
     */
    public enum SortOrder {
        /**
         * Ascending.
         */
        ASCENDING,

        /**
         * Descending.
         */
        DESCENDING;
    }
}
