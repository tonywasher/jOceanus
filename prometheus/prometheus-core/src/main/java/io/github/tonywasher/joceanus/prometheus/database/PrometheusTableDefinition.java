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
package io.github.tonywasher.joceanus.prometheus.database;

import io.github.tonywasher.joceanus.gordianknot.util.GordianUtilities;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusBinaryColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusBooleanColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusDateColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusIdColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusIntegerColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusLongColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusMoneyColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusPriceColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusRateColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusRatioColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusReferenceColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusStringColumn;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusColumnDefinition.PrometheusUnitsColumn;
import io.github.tonywasher.joceanus.prometheus.exc.PrometheusLogicException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private final Map<MetisDataFieldId, PrometheusColumnDefinition> theMap;

    /**
     * The prepared statement for the insert/update.
     */
    private PreparedStatement theStatement;

    /**
     * Constructor.
     *
     * @param pDriver the driver
     * @param pName   the table name
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
        theList.add(new PrometheusIdColumn(this));
    }

    /**
     * Obtain the table name.
     *
     * @return the table name
     */
    protected String getTableName() {
        return theTableName;
    }

    /**
     * Obtain the column map.
     *
     * @return the map
     */
    protected Map<MetisDataFieldId, PrometheusColumnDefinition> getMap() {
        return theMap;
    }

    /**
     * Is the table indexed.
     *
     * @return true/false
     */
    protected boolean isIndexed() {
        return !theSortList.isEmpty();
    }

    /**
     * Column Definitions array.
     *
     * @return the columns
     */
    protected List<PrometheusColumnDefinition> getColumns() {
        return theList;
    }

    /**
     * Sort List.
     *
     * @return the sort list
     */
    protected List<PrometheusColumnDefinition> getSortList() {
        return theSortList;
    }

    /**
     * Obtain the driver.
     *
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
     *
     * @param pId  the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public PrometheusReferenceColumn addReferenceColumn(final MetisDataFieldId pId,
                                                        final String pRef) {
        /* Create the new reference column */
        final PrometheusReferenceColumn myColumn = new PrometheusReferenceColumn(this, pId, pRef);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a reference column, which can be null.
     *
     * @param pId  the column id
     * @param pRef the reference table
     * @return the reference column
     */
    public PrometheusReferenceColumn addNullReferenceColumn(final MetisDataFieldId pId,
                                                            final String pRef) {
        final PrometheusReferenceColumn myColumn = addReferenceColumn(pId, pRef);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an integer column.
     *
     * @param pId the column id
     * @return the integer column
     */
    public PrometheusIntegerColumn addIntegerColumn(final MetisDataFieldId pId) {
        /* Create the new integer column */
        final PrometheusIntegerColumn myColumn = new PrometheusIntegerColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add an integer column, which can be null.
     *
     * @param pId the column id
     * @return the integer column
     */
    public PrometheusIntegerColumn addNullIntegerColumn(final MetisDataFieldId pId) {
        final PrometheusIntegerColumn myColumn = addIntegerColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a long column.
     *
     * @param pId the column id
     * @return the long column
     */
    public PrometheusLongColumn addLongColumn(final MetisDataFieldId pId) {
        /* Create the new long column */
        final PrometheusLongColumn myColumn = new PrometheusLongColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a long column, which can be null.
     *
     * @param pId the column id
     * @return the long column
     */
    public PrometheusLongColumn addNullLongColumn(final MetisDataFieldId pId) {
        final PrometheusLongColumn myColumn = addLongColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a boolean column.
     *
     * @param pId the column id
     * @return the boolean column
     */
    public PrometheusBooleanColumn addBooleanColumn(final MetisDataFieldId pId) {
        /* Create the new boolean column */
        final PrometheusBooleanColumn myColumn = new PrometheusBooleanColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a boolean column, which can be null.
     *
     * @param pId the column id
     * @return the boolean column
     */
    public PrometheusBooleanColumn addNullBooleanColumn(final MetisDataFieldId pId) {
        final PrometheusBooleanColumn myColumn = addBooleanColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a date column.
     *
     * @param pId the column id
     * @return the date column
     */
    public PrometheusDateColumn addDateColumn(final MetisDataFieldId pId) {
        /* Create the new date column */
        final PrometheusDateColumn myColumn = new PrometheusDateColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a date column, which can be null.
     *
     * @param pId the column id
     * @return the date column
     */
    public PrometheusDateColumn addNullDateColumn(final MetisDataFieldId pId) {
        final PrometheusDateColumn myColumn = addDateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a money column.
     *
     * @param pId the column id
     * @return the money column
     */
    public PrometheusMoneyColumn addMoneyColumn(final MetisDataFieldId pId) {
        /* Create the new money column */
        final PrometheusMoneyColumn myColumn = new PrometheusMoneyColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a money column, which can be null.
     *
     * @param pId the column id
     * @return the money column
     */
    public PrometheusMoneyColumn addNullMoneyColumn(final MetisDataFieldId pId) {
        final PrometheusMoneyColumn myColumn = addMoneyColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     *
     * @param pId the column id
     * @return the rate column
     */
    public PrometheusRateColumn addRateColumn(final MetisDataFieldId pId) {
        /* Create the new rate column */
        final PrometheusRateColumn myColumn = new PrometheusRateColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a rate column, which can be null.
     *
     * @param pId the column id
     * @return the rate column
     */
    public PrometheusRateColumn addNullRateColumn(final MetisDataFieldId pId) {
        final PrometheusRateColumn myColumn = addRateColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a rate column.
     *
     * @param pId the column id
     * @return the rate column
     */
    public PrometheusRatioColumn addRatioColumn(final MetisDataFieldId pId) {
        /* Create the new rate column */
        final PrometheusRatioColumn myColumn = new PrometheusRatioColumn(this, pId);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a rate column, which can be null.
     *
     * @param pId the column id
     * @return the rate column
     */
    public PrometheusRatioColumn addNullRatioColumn(final MetisDataFieldId pId) {
        final PrometheusRatioColumn myColumn = addRatioColumn(pId);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a binary column.
     *
     * @param pId     the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public PrometheusBinaryColumn addBinaryColumn(final MetisDataFieldId pId,
                                                  final int pLength) {
        /* Create the new binary column */
        final PrometheusBinaryColumn myColumn = new PrometheusBinaryColumn(this, pId, pLength);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a binary column, which can be null.
     *
     * @param pId     the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public PrometheusBinaryColumn addNullBinaryColumn(final MetisDataFieldId pId,
                                                      final int pLength) {
        final PrometheusBinaryColumn myColumn = addBinaryColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add an encrypted column.
     *
     * @param pId     the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public PrometheusBinaryColumn addEncryptedColumn(final MetisDataFieldId pId,
                                                     final int pLength) {
        /* Create the new binary column */
        final PrometheusBinaryColumn myColumn = new PrometheusBinaryColumn(this, pId, GordianUtilities.getKeySetEncryptionLength(pLength));

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add an encrypted column, which can be null.
     *
     * @param pId     the column id
     * @param pLength the underlying (character) length
     * @return the binary column
     */
    public PrometheusBinaryColumn addNullEncryptedColumn(final MetisDataFieldId pId,
                                                         final int pLength) {
        final PrometheusBinaryColumn myColumn = addEncryptedColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Add a string column.
     *
     * @param pId     the column id
     * @param pLength the character length
     * @return the binary column
     */
    public PrometheusStringColumn addStringColumn(final MetisDataFieldId pId,
                                                  final int pLength) {
        /* Create the new string column */
        final PrometheusStringColumn myColumn = new PrometheusStringColumn(this, pId, pLength);

        /* Add it to the list and return it */
        theList.add(myColumn);
        return myColumn;
    }

    /**
     * Add a string column, which can be null.
     *
     * @param pId     the column id
     * @param pLength the character length
     * @return the binary column
     */
    public PrometheusStringColumn addNullStringColumn(final MetisDataFieldId pId,
                                                      final int pLength) {
        final PrometheusStringColumn myColumn = addStringColumn(pId, pLength);
        myColumn.setNullable();
        return myColumn;
    }

    /**
     * Locate reference.
     *
     * @param pTables the list of defined tables
     */
    protected void resolveReferences(final List<PrometheusTableDataItem<?>> pTables) {
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
     *
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
     *
     * @param pCol the column definition.
     * @return the error string
     */
    private String getColumnError(final PrometheusColumnDefinition pCol) {
        return "Column " + pCol.getColumnName() + " in table " + theTableName;
    }

    /**
     * Insert values.
     *
     * @param pStmt the statement
     * @throws SQLException     on error
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
     *
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
            if (myDef instanceof PrometheusIdColumn) {
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
     *
     * @param pId the column id
     * @return the integer value
     * @throws OceanusException on error
     */
    public Integer getIntegerValue(final MetisDataFieldId pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof PrometheusIntegerColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Integer type");
        }

        /* Return the value */
        final PrometheusIntegerColumn myIntCol = (PrometheusIntegerColumn) myCol;
        return myIntCol.getValue();
    }

    /**
     * Get Long value for column.
     *
     * @param pId the column id
     * @return the long value
     * @throws OceanusException on error
     */
    public Long getLongValue(final MetisDataFieldId pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof PrometheusLongColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Long type");
        }

        /* Return the value */
        final PrometheusLongColumn myLongCol = (PrometheusLongColumn) myCol;
        return myLongCol.getValue();
    }

    /**
     * Get Date value for column.
     *
     * @param pId the column id
     * @return the Date value
     * @throws OceanusException on error
     */
    public OceanusDate getDateValue(final MetisDataFieldId pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a date column */
        if (!(myCol instanceof PrometheusDateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Date type");
        }

        /* Return the value */
        final PrometheusDateColumn myDateCol = (PrometheusDateColumn) myCol;
        return myDateCol.getValue();
    }

    /**
     * Get Boolean value for column.
     *
     * @param pId the column id
     * @return the boolean value
     * @throws OceanusException on error
     */
    public Boolean getBooleanValue(final MetisDataFieldId pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof PrometheusBooleanColumn)) {
            throw new PrometheusLogicException("Column " + getColumnError(myCol) + " is not Boolean type");
        }

        /* Return the value */
        final PrometheusBooleanColumn myBoolCol = (PrometheusBooleanColumn) myCol;
        return myBoolCol.getValue();
    }

    /**
     * Get String value for column.
     *
     * @param pId the column id
     * @return the String value
     * @throws OceanusException on error
     */
    public String getStringValue(final MetisDataFieldId pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof PrometheusStringColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not String type");
        }

        /* Return the value */
        final PrometheusStringColumn myStringCol = (PrometheusStringColumn) myCol;
        return myStringCol.getValue();
    }

    /**
     * Get Money value for column.
     *
     * @param pId        the column id
     * @param pFormatter the data formatter
     * @return the Money value
     * @throws OceanusException on error
     */
    public OceanusMoney getMoneyValue(final MetisDataFieldId pId,
                                      final OceanusDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof PrometheusMoneyColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not money type");
        }

        /* Access the value */
        final PrometheusMoneyColumn myMoneyCol = (PrometheusMoneyColumn) myCol;
        return myMoneyCol.getValue(pFormatter);
    }

    /**
     * Get Price value for column.
     *
     * @param pId        the column id
     * @param pFormatter the data formatter
     * @return the price value
     * @throws OceanusException on error
     */
    public OceanusPrice getPriceValue(final MetisDataFieldId pId,
                                      final OceanusDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a price column */
        if (!(myCol instanceof PrometheusPriceColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Price type");
        }

        /* Access the value */
        final PrometheusPriceColumn myPriceCol = (PrometheusPriceColumn) myCol;
        return myPriceCol.getValue(pFormatter);
    }

    /**
     * Get Rate value for column.
     *
     * @param pId        the column id
     * @param pFormatter the data formatter
     * @return the rate value
     * @throws OceanusException on error
     */
    public OceanusRate getRateValue(final MetisDataFieldId pId,
                                    final OceanusDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof PrometheusRateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Rate type");
        }

        /* Access the value */
        final PrometheusRateColumn myRateCol = (PrometheusRateColumn) myCol;
        return myRateCol.getValue(pFormatter);
    }

    /**
     * Get Units value for column.
     *
     * @param pId        the column id
     * @param pFormatter the data formatter
     * @return the Units value
     * @throws OceanusException on error
     */
    public OceanusUnits getUnitsValue(final MetisDataFieldId pId,
                                      final OceanusDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a units column */
        if (!(myCol instanceof PrometheusUnitsColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Units type");
        }

        /* Access the value */
        final PrometheusUnitsColumn myUnitsCol = (PrometheusUnitsColumn) myCol;
        return myUnitsCol.getValue(pFormatter);
    }

    /**
     * Get Ratio value for column.
     *
     * @param pId        the column id
     * @param pFormatter the data formatter
     * @return the Ratio value
     * @throws OceanusException on error
     */
    public OceanusRatio getRatioValue(final MetisDataFieldId pId,
                                      final OceanusDataFormatter pFormatter) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a ratio column */
        if (!(myCol instanceof PrometheusRatioColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Ratio type");
        }

        /* Access the value */
        final PrometheusRatioColumn myRatioCol = (PrometheusRatioColumn) myCol;
        return myRatioCol.getValue(pFormatter);
    }

    /**
     * Get Binary value for column.
     *
     * @param pId the column id
     * @return the binary value
     * @throws OceanusException on error
     */
    public byte[] getBinaryValue(final MetisDataFieldId pId) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof PrometheusBinaryColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Binary type");
        }

        /* Return the value */
        final PrometheusBinaryColumn myBinaryCol = (PrometheusBinaryColumn) myCol;
        return myBinaryCol.getValue();
    }

    /**
     * Set Integer value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setIntegerValue(final MetisDataFieldId pId,
                                final Integer pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not an integer column */
        if (!(myCol instanceof PrometheusIntegerColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Integer type");
        }

        /* Set the value */
        final PrometheusIntegerColumn myIntCol = (PrometheusIntegerColumn) myCol;
        myIntCol.setValue(pValue);
    }

    /**
     * Set Long value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setLongValue(final MetisDataFieldId pId,
                             final Long pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a long column */
        if (!(myCol instanceof PrometheusLongColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Long type");
        }

        /* Set the value */
        final PrometheusLongColumn myLongCol = (PrometheusLongColumn) myCol;
        myLongCol.setValue(pValue);
    }

    /**
     * Set Boolean value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setBooleanValue(final MetisDataFieldId pId,
                                final Boolean pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a boolean column */
        if (!(myCol instanceof PrometheusBooleanColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Boolean type");
        }

        /* Set the value */
        final PrometheusBooleanColumn myBoolCol = (PrometheusBooleanColumn) myCol;
        myBoolCol.setValue(pValue);
    }

    /**
     * Set Date value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setDateValue(final MetisDataFieldId pId,
                             final OceanusDate pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a Date column */
        if (!(myCol instanceof PrometheusDateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Date type");
        }

        /* Set the value */
        final PrometheusDateColumn myDateCol = (PrometheusDateColumn) myCol;
        myDateCol.setValue(pValue);
    }

    /**
     * Set String value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setStringValue(final MetisDataFieldId pId,
                               final String pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a string column */
        if (!(myCol instanceof PrometheusStringColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not String type");
        }

        /* Set the value */
        final PrometheusStringColumn myStringCol = (PrometheusStringColumn) myCol;
        myStringCol.setValue(pValue);
    }

    /**
     * Set Binary value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setBinaryValue(final MetisDataFieldId pId,
                               final byte[] pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a binary column */
        if (!(myCol instanceof PrometheusBinaryColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Binary type");
        }

        /* Set the value */
        final PrometheusBinaryColumn myBinaryCol = (PrometheusBinaryColumn) myCol;
        myBinaryCol.setValue(pValue);
    }

    /**
     * Set Money value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setMoneyValue(final MetisDataFieldId pId,
                              final OceanusMoney pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a money column */
        if (!(myCol instanceof PrometheusMoneyColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Money type");
        }

        /* Set the value */
        final PrometheusMoneyColumn myMoneyCol = (PrometheusMoneyColumn) myCol;
        myMoneyCol.setValue(pValue);
    }

    /**
     * Set Rate value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setRateValue(final MetisDataFieldId pId,
                             final OceanusRate pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a rate column */
        if (!(myCol instanceof PrometheusRateColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Rate type");
        }

        /* Set the value */
        final PrometheusRateColumn myRateCol = (PrometheusRateColumn) myCol;
        myRateCol.setValue(pValue);
    }

    /**
     * Set Ratio value for column.
     *
     * @param pId    the column id
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setRatioValue(final MetisDataFieldId pId,
                              final OceanusRatio pValue) throws OceanusException {
        /* Obtain the correct id */
        final PrometheusColumnDefinition myCol = getColumnForId(pId);

        /* Reject if this is not a ratio column */
        if (!(myCol instanceof PrometheusRatioColumn)) {
            throw new PrometheusLogicException(getColumnError(myCol) + " is not Ratio type");
        }

        /* Set the value */
        final PrometheusRatioColumn myRatioCol = (PrometheusRatioColumn) myCol;
        myRatioCol.setValue(pValue);
    }

    /**
     * Locate column for id.
     *
     * @param pId the id of the column
     * @return the column
     * @throws OceanusException on error
     */
    private PrometheusColumnDefinition getColumnForId(final MetisDataFieldId pId) throws OceanusException {
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
     *
     * @return the SQL string
     */
    protected String getCreateTableString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial create */
        myBuilder.append("create table ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
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
     *
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
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(PREFIX_INDEX);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(" on ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
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
            addQuoteIfAllowed(myBuilder);
            myBuilder.append(myDef.getColumnName());
            addQuoteIfAllowed(myBuilder);
            if (myDef.getSortOrder() == PrometheusSortOrder.DESCENDING) {
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
     *
     * @return the SQL string
     */
    protected String getDropTableString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("drop table if exists ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);

        /* Return the command */
        return myBuilder.toString();
    }

    /**
     * Build the drop index string for the table.
     *
     * @return the SQL string
     */
    protected String getDropIndexString() {
        /* Return null if we are not indexed */
        if (!isIndexed() || !theDriver.explicitDropIndex()) {
            return null;
        }

        /* Build the drop command */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("drop index if exists ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(PREFIX_INDEX);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
        if (!PrometheusJDBCDriver.POSTGRESQL.equals(theDriver)) {
            myBuilder.append(" on ");
            addQuoteIfAllowed(myBuilder);
            myBuilder.append(theTableName);
            addQuoteIfAllowed(myBuilder);
        }

        /* Return the command */
        return myBuilder.toString();
    }

    /**
     * Build the load string for a list of columns.
     *
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
            addQuoteIfAllowed(myBuilder);
            myBuilder.append(myDef.getColumnName());
            addQuoteIfAllowed(myBuilder);
            myFirst = false;
        }

        /* Close the statement */
        myBuilder.append(" from ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
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
     *
     * @param pChar   the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    protected String getJoinString(final char pChar,
                                   final Integer pOffset) {
        /* Loop through the columns */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        for (PrometheusColumnDefinition myDef : theSortList) {
            /* Access next column and skip if not reference column */
            if (!(myDef instanceof PrometheusReferenceColumn)) {
                continue;
            }

            /* Add the join */
            final PrometheusReferenceColumn myCol = (PrometheusReferenceColumn) myDef;
            myCol.buildJoinString(myBuilder, pChar, pOffset);
        }

        return myBuilder.toString();
    }

    /**
     * Build the Order string for the list of columns.
     *
     * @param pChar   the character for this table
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
            if (sortOnReference || pChar > 'a') {
                /* If this is a reference column */
                if (myDef instanceof PrometheusReferenceColumn myCol) {
                    /* Handle Reference column */
                    myCol.buildOrderString(myBuilder, pOffset + 1);
                } else {
                    /* Handle standard column with prefix */
                    myBuilder.append(pChar);
                    myBuilder.append(".");
                    addQuoteIfAllowed(myBuilder);
                    myBuilder.append(myDef.getColumnName());
                    addQuoteIfAllowed(myBuilder);
                    if (myDef.getSortOrder() == PrometheusSortOrder.DESCENDING) {
                        myBuilder.append(STR_DESC);
                    }
                }
            } else {
                /* Handle standard column */
                addQuoteIfAllowed(myBuilder);
                myBuilder.append(myDef.getColumnName());
                addQuoteIfAllowed(myBuilder);
                if (myDef.getSortOrder() == PrometheusSortOrder.DESCENDING) {
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
     *
     * @return the SQL string
     */
    protected String getInsertString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        final StringBuilder myValues = new StringBuilder(BUFFER_LEN);

        /* Build the initial insert */
        myBuilder.append("insert into ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
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
            addQuoteIfAllowed(myBuilder);
            myBuilder.append(myDef.getColumnName());
            addQuoteIfAllowed(myBuilder);
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
     *
     * @return the SQL string
     * @throws OceanusException on error
     */
    protected String getUpdateString() throws OceanusException {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial update */
        myBuilder.append("update ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(" set ");

        /* Create the iterator */
        final Iterator<PrometheusColumnDefinition> myIterator = theList.iterator();
        PrometheusColumnDefinition myId = null;
        boolean myFirst = true;

        /* Loop through the columns */
        while (myIterator.hasNext()) {
            final PrometheusColumnDefinition myDef = myIterator.next();

            /* If this is the Id record */
            if (myDef instanceof PrometheusIdColumn) {
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
                addQuoteIfAllowed(myBuilder);
                myBuilder.append(myDef.getColumnName());
                addQuoteIfAllowed(myBuilder);
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
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(myId.getColumnName());
        addQuoteIfAllowed(myBuilder);
        myBuilder.append("=?");
        return myBuilder.toString();
    }

    /**
     * Build the delete string for a table.
     *
     * @return the SQL string
     */
    protected String getDeleteString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("delete from ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(" where ");

        /* Access the id definition */
        final PrometheusColumnDefinition myId = theList.get(0);

        /* Build the rest of the command */
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(myId.getColumnName());
        addQuoteIfAllowed(myBuilder);
        myBuilder.append("=?");
        return myBuilder.toString();
    }

    /**
     * Build the purge string for a table.
     *
     * @return the SQL string
     */
    protected String getPurgeString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("delete from ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
        return myBuilder.toString();
    }

    /**
     * Build the count string for a table.
     *
     * @return the SQL string
     */
    protected String getCountString() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial delete */
        myBuilder.append("select count(*) from ");
        addQuoteIfAllowed(myBuilder);
        myBuilder.append(theTableName);
        addQuoteIfAllowed(myBuilder);
        return myBuilder.toString();
    }

    /**
     * Add quote if necessary.
     *
     * @param pBuilder the builder
     */
    void addQuoteIfAllowed(final StringBuilder pBuilder) {
        if (theDriver.useQuotes()) {
            pBuilder.append(QUOTE_STRING);
        }
    }

    /**
     * SortOrder.
     */
    public enum PrometheusSortOrder {
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
