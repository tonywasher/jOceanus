/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.database;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.preference.PrometheusColumnType;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Column definition classes handling data-type specifics.
 * @author Tony Washer
 */
public abstract class PrometheusColumnDefinition {
    /**
     * Open bracket.
     */
    private static final String STR_OPNBRK = "(";

    /**
     * Close bracket.
     */
    private static final String STR_CLSBRK = ")";

    /**
     * Comma.
     */
    private static final String STR_COMMA = ",";

    /**
     * Decimal length.
     */
    private static final String STR_NUMLEN = "18";

    /**
     * Standard Decimal length.
     */
    private static final String STR_STDDECLEN = "4";

    /**
     * Decimal length.
     */
    private static final String STR_XTRADECLEN = "6";

    /**
     * Table Definition.
     */
    private final PrometheusTableDefinition theTable;

    /**
     * Column Identity.
     */
    private final MetisDataFieldId theIdentity;

    /**
     * Is the column null-able.
     */
    private boolean isNullable;

    /**
     * Is the value set.
     */
    private boolean isValueSet;

    /**
     * The value of the column.
     */
    private Object theValue;

    /**
     * The sort order of the column.
     */
    private PrometheusSortOrder theOrder;

    /**
     * Constructor.
     * @param pTable the table to which the column belongs
     * @param pId the column id
     */
    protected PrometheusColumnDefinition(final PrometheusTableDefinition pTable,
                                         final MetisDataFieldId pId) {
        /* Record the identity and table */
        theIdentity = pId;
        theTable = pTable;

        /* Add to the map */
        theTable.getMap().put(theIdentity, this);
    }

    /**
     * Obtain the column name.
     * @return the name
     */
    protected String getColumnName() {
        return theIdentity.getId();
    }

    /**
     * Obtain the column id.
     * @return the id
     */
    protected MetisDataFieldId getColumnId() {
        return theIdentity;
    }

    /**
     * Obtain the sort order.
     * @return the sort order
     */
    protected PrometheusSortOrder getSortOrder() {
        return theOrder;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    protected Object getObject() {
        return theValue;
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected PrometheusTableDefinition getTable() {
        return theTable;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    protected PrometheusJDBCDriver getDriver() {
        return theTable.getDriver();
    }

    /**
     * Clear value.
     */
    protected void clearValue() {
        theValue = null;
        isValueSet = false;
    }

    /**
     * Set value.
     * @param pValue the value
     */
    protected void setObject(final Object pValue) {
        theValue = pValue;
        isValueSet = true;
    }

    /**
     * Is the value set.
     * @return true/false
     */
    protected boolean isValueSet() {
        return isValueSet;
    }

    /**
     * Build the creation string for this column.
     * @param pBuilder the String builder
     */
    protected void buildCreateString(final StringBuilder pBuilder) {
        /* Add the name of the column */
        theTable.addQuoteIfAllowed(pBuilder);
        pBuilder.append(getColumnName());
        theTable.addQuoteIfAllowed(pBuilder);
        pBuilder.append(' ');

        /* Add the type of the column */
        buildColumnType(pBuilder);

        /* Add null-able indication */
        if (!isNullable) {
            pBuilder.append(" not");
        }
        pBuilder.append(" null");

        /* build the key reference */
        buildKeyReference(pBuilder);
    }

    /**
     * Set null-able column.
     */
    protected void setNullable() {
        isNullable = true;
    }

    /**
     * Note that we have a sort on reference.
     */
    protected void setSortOnReference() {
        theTable.setSortOnReference();
    }

    /**
     * Set sortOrder.
     * @param pOrder the Sort direction
     */
    public void setSortOrder(final PrometheusSortOrder pOrder) {
        theOrder = pOrder;
        theTable.getSortList().add(this);
    }

    /**
     * Build the column type for this column.
     * @param pBuilder the String builder
     */
    protected abstract void buildColumnType(StringBuilder pBuilder);

    /**
     * Load the value for this column.
     * @param pResults the results
     * @param pIndex the index of the result column
     * @throws SQLException on error
     */
    protected abstract void loadValue(ResultSet pResults,
                                      int pIndex) throws SQLException;

    /**
     * Store the value for this column.
     * @param pStatement the prepared statement
     * @param pIndex the index of the statement
     * @throws SQLException on error
     */
    protected abstract void storeValue(PreparedStatement pStatement,
                                       int pIndex) throws SQLException;

    /**
     * Define the key reference.
     * @param pBuilder the String builder
     */
    protected void buildKeyReference(final StringBuilder pBuilder) {
    }

    /**
     * Locate reference.
     * @param pTables the list of defined tables
     */
    protected void locateReference(final List<PrometheusTableDataItem<?>> pTables) {
    }

    /**
     * The integerColumn Class.
     */
    protected static class PrometheusIntegerColumn
            extends PrometheusColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected PrometheusIntegerColumn(final PrometheusTableDefinition pTable,
                                          final MetisDataFieldId pId) {
            /* Record the column type and name */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.INTEGER));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Integer pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Integer getValue() {
            return (Integer) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final int myValue = pResults.getInt(pIndex);
            if (pResults.wasNull()) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final Integer myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.INTEGER);
            } else {
                pStatement.setInt(pIndex, myValue);
            }
        }
    }

    /**
     * The idColumn Class.
     */
    protected static final class PrometheusIdColumn
            extends PrometheusIntegerColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         */
        PrometheusIdColumn(final PrometheusTableDefinition pTable) {
            /* Record the column type */
            super(pTable, MetisDataResource.DATA_ID);
        }

        @Override
        protected void buildKeyReference(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(" primary key");
        }
    }

    /**
     * The referenceColumn Class.
     */
    protected static final class PrometheusReferenceColumn
            extends PrometheusIntegerColumn {
        /**
         * The name of the referenced table.
         */
        private final String theReference;

        /**
         * The definition of the referenced table.
         */
        private PrometheusTableDefinition theDefinition;

        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         * @param pRefTable the name of the referenced table
         */
        PrometheusReferenceColumn(final PrometheusTableDefinition pTable,
                                  final MetisDataFieldId pId,
                                  final String pRefTable) {
            /* Record the column type */
            super(pTable, pId);
            theReference = pRefTable;
        }

        @Override
        public void setSortOrder(final PrometheusSortOrder pOrder) {
            super.setSortOrder(pOrder);
            setSortOnReference();
        }

        @Override
        protected void buildKeyReference(final StringBuilder pBuilder) {
            /* Add the reference */
            pBuilder.append(" REFERENCES ");
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(theReference);
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(STR_OPNBRK);
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(MetisDataResource.DATA_ID.getValue());
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(STR_CLSBRK);
        }

        @Override
        protected void locateReference(final List<PrometheusTableDataItem<?>> pTables) {
            /* Access the Iterator */
            final ListIterator<PrometheusTableDataItem<?>> myIterator;
            myIterator = pTables.listIterator();

            /* Loop through the Tables */
            while (myIterator.hasNext()) {
                /* Access Table */
                final PrometheusTableDataItem<?> myTable = myIterator.next();

                /* If this is the referenced table */
                if (theReference.compareTo(myTable.getTableName()) == 0) {
                    /* Store the reference and break the loop */
                    theDefinition = myTable.getDefinition();
                    break;
                }
            }
        }

        /**
         * build Join String.
         * @param pBuilder the String Builder
         * @param pChar the character for this table
         * @param pOffset the join offset
         */
        void buildJoinString(final StringBuilder pBuilder,
                             final char pChar,
                             final Integer pOffset) {
            Integer myOffset = pOffset;

            /* Calculate join character */
            final char myChar = (char) ('a' + myOffset);

            /* Build Initial part of string */
            pBuilder.append(" join ");
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(theReference);
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(" ");
            pBuilder.append(myChar);

            /* Build the join */
            pBuilder.append(" on ");
            pBuilder.append(pChar);
            pBuilder.append(".");
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(getColumnName());
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(" = ");
            pBuilder.append(myChar);
            pBuilder.append(".");
            getTable().addQuoteIfAllowed(pBuilder);
            pBuilder.append(MetisDataResource.DATA_ID.getValue());
            getTable().addQuoteIfAllowed(pBuilder);

            /* Increment offset */
            myOffset++;

            /* Add the join string for the underlying table */
            pBuilder.append(theDefinition.getJoinString(myChar, myOffset));
        }

        /**
         * build Order String.
         * @param pBuilder the String Builder
         * @param pOffset the join offset
         */
        void buildOrderString(final StringBuilder pBuilder,
                              final Integer pOffset) {
            final Iterator<PrometheusColumnDefinition> myIterator;
            PrometheusColumnDefinition myDef;
            boolean myFirst = true;
            Integer myOffset = pOffset;

            /* Calculate join character */
            final char myChar = (char) ('a' + myOffset);

            /* Create the iterator */
            myIterator = theDefinition.getSortList().iterator();

            /* Loop through the columns */
            while (myIterator.hasNext()) {
                /* Access next column */
                myDef = myIterator.next();

                /* Handle subsequent columns */
                if (!myFirst) {
                    pBuilder.append(", ");
                }

                /* If this is a reference column */
                if (myDef instanceof PrometheusReferenceColumn) {
                    /* Increment offset */
                    myOffset++;

                    /* Determine new char */
                    final char myNewChar = (char) ('a' + myOffset);

                    /* Add the order string for the underlying table. */
                    /* Note that forced to implement in one line to avoid Sonar false positive. */
                    pBuilder.append(((PrometheusReferenceColumn) myDef).theDefinition.getOrderString(myNewChar, myOffset));

                    /* else standard column */
                } else {
                    /* Build the column name */
                    pBuilder.append(myChar);
                    pBuilder.append(".");
                    getTable().addQuoteIfAllowed(pBuilder);
                    pBuilder.append(myDef.getColumnName());
                    getTable().addQuoteIfAllowed(pBuilder);
                    if (myDef.getSortOrder() == PrometheusSortOrder.DESCENDING) {
                        pBuilder.append(" DESC");
                    }
                }

                /* Note we have a column */
                myFirst = false;
            }
        }
    }

    /**
     * The shortColumn Class.
     */
    protected static final class PrometheusShortColumn
            extends PrometheusColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusShortColumn(final PrometheusTableDefinition pTable,
                              final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.SHORT));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final Short pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        Short getValue() {
            return (Short) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final short myValue = pResults.getShort(pIndex);
            if (pResults.wasNull()) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final Short myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.SMALLINT);
            } else {
                pStatement.setShort(pIndex, myValue);
            }
        }
    }

    /**
     * The longColumn Class.
     */
    protected static final class PrometheusLongColumn
            extends PrometheusColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusLongColumn(final PrometheusTableDefinition pTable,
                             final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.LONG));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final Long pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        Long getValue() {
            return (Long) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final long myValue = pResults.getLong(pIndex);
            if (pResults.wasNull()) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final Long myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.BIGINT);
            } else {
                pStatement.setLong(pIndex, myValue);
            }
        }
    }

    /**
     * The dateColumn Class.
     */
    protected static final class PrometheusDateColumn
            extends PrometheusColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusDateColumn(final PrometheusTableDefinition pTable,
                             final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.DATE));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final OceanusDate pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        OceanusDate getValue() {
            return (OceanusDate) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final Date myValue = pResults.getDate(pIndex);
            setValue((myValue == null)
                    ? null
                    : new OceanusDate(myValue));
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final OceanusDate myValue = getValue();

            /* Build the date as a SQL date */
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.DATE);
            } else {
                final long myDateValue = myValue.toDate().getTime();
                final Date myDate = new Date(myDateValue);
                pStatement.setDate(pIndex, myDate);
            }
        }
    }

    /**
     * The booleanColumn Class.
     */
    protected static final class PrometheusBooleanColumn
            extends PrometheusColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusBooleanColumn(final PrometheusTableDefinition pTable,
                                final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.BOOLEAN));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final Boolean pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        Boolean getValue() {
            return (Boolean) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final boolean myValue = pResults.getBoolean(pIndex);
            if (pResults.wasNull()) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final Boolean myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.BIT);
            } else {
                pStatement.setBoolean(pIndex, myValue);
            }
        }
    }

    /**
     * The stringColumn Class.
     */
    protected static class PrometheusStringColumn
            extends PrometheusColumnDefinition {
        /**
         * The length of the column.
         */
        private final int theLength;

        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         * @param pLength the length
         */
        protected PrometheusStringColumn(final PrometheusTableDefinition pTable,
                                         final MetisDataFieldId pId,
                                         final int pLength) {
            /* Record the column type */
            super(pTable, pId);
            theLength = pLength;
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.STRING));
            pBuilder.append(STR_OPNBRK);
            pBuilder.append(theLength);
            pBuilder.append(STR_CLSBRK);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final String pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected String getValue() {
            return (String) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final String myValue = pResults.getString(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final String myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.VARCHAR);
            } else {
                pStatement.setString(pIndex, myValue);
            }
        }
    }

    /**
     * The moneyColumn Class.
     */
    protected static final class PrometheusMoneyColumn
            extends PrometheusStringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusMoneyColumn(final PrometheusTableDefinition pTable,
                              final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.MONEY));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final OceanusMoney pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final String myValue = getValue();
            if (myValue != null) {
                final BigDecimal myDecimal = new BigDecimal(myValue);
                pStatement.setBigDecimal(pIndex, myDecimal);
            } else {
                pStatement.setNull(pIndex, Types.DECIMAL);
            }
        }

        /**
         * Obtain the value.
         * @param pFormatter the data formatter
         * @return the money value
         * @throws OceanusException on error
         */
        public OceanusMoney getValue(final OceanusDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), OceanusMoney.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Money Value", e);
            }
        }
    }

    /**
     * The rateColumn Class.
     */
    protected static final class PrometheusRateColumn
            extends PrometheusStringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusRateColumn(final PrometheusTableDefinition pTable,
                             final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.DECIMAL));
            pBuilder.append(STR_OPNBRK);
            pBuilder.append(STR_NUMLEN);
            pBuilder.append(STR_COMMA);
            pBuilder.append(STR_STDDECLEN);
            pBuilder.append(STR_CLSBRK);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final OceanusRate pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final String myValue = getValue();
            if (myValue != null) {
                final BigDecimal myDecimal = new BigDecimal(myValue);
                pStatement.setBigDecimal(pIndex, myDecimal);
            } else {
                pStatement.setNull(pIndex, Types.DECIMAL);
            }
        }

        /**
         * Obtain the value.
         * @param pFormatter the data formatter
         * @return the money value
         * @throws OceanusException on error
         */
        public OceanusRate getValue(final OceanusDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), OceanusRate.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Rate Value", e);
            }
        }
    }

    /**
     * The priceColumn Class.
     */
    protected static final class PrometheusPriceColumn
            extends PrometheusStringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusPriceColumn(final PrometheusTableDefinition pTable,
                              final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.DECIMAL));
            pBuilder.append(STR_OPNBRK);
            pBuilder.append(STR_NUMLEN);
            pBuilder.append(STR_COMMA);
            pBuilder.append(STR_STDDECLEN);
            pBuilder.append(STR_CLSBRK);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final OceanusPrice pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final String myValue = getValue();
            if (myValue != null) {
                final BigDecimal myDecimal = new BigDecimal(myValue);
                pStatement.setBigDecimal(pIndex, myDecimal);
            } else {
                pStatement.setNull(pIndex, Types.DECIMAL);
            }
        }

        /**
         * Obtain the value.
         * @param pFormatter the data formatter
         * @return the money value
         * @throws OceanusException on error
         */
        public OceanusPrice getValue(final OceanusDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), OceanusPrice.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Price Value", e);
            }
        }
    }

    /**
     * The unitsColumn Class.
     */
    protected static final class PrometheusUnitsColumn
            extends PrometheusStringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusUnitsColumn(final PrometheusTableDefinition pTable,
                              final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.DECIMAL));
            pBuilder.append(STR_OPNBRK);
            pBuilder.append(STR_NUMLEN);
            pBuilder.append(STR_COMMA);
            pBuilder.append(STR_STDDECLEN);
            pBuilder.append(STR_CLSBRK);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final OceanusUnits pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final String myValue = getValue();
            if (myValue != null) {
                final BigDecimal myDecimal = new BigDecimal(myValue);
                pStatement.setBigDecimal(pIndex, myDecimal);
            } else {
                pStatement.setNull(pIndex, Types.DECIMAL);
            }
        }

        /**
         * Obtain the value.
         * @param pFormatter the data formatter
         * @return the money value
         * @throws OceanusException on error
         */
        public OceanusUnits getValue(final OceanusDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), OceanusUnits.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Units Value", e);
            }
        }
    }

    /**
     * The ratioColumn Class.
     */
    protected static final class PrometheusRatioColumn
            extends PrometheusStringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PrometheusRatioColumn(final PrometheusTableDefinition pTable,
                              final MetisDataFieldId pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(PrometheusColumnType.DECIMAL));
            pBuilder.append(STR_OPNBRK);
            pBuilder.append(STR_NUMLEN);
            pBuilder.append(STR_COMMA);
            pBuilder.append(STR_XTRADECLEN);
            pBuilder.append(STR_CLSBRK);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final OceanusRatio pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final String myValue = getValue();
            if (myValue != null) {
                final BigDecimal myDecimal = new BigDecimal(myValue);
                pStatement.setBigDecimal(pIndex, myDecimal);
            } else {
                pStatement.setNull(pIndex, Types.DECIMAL);
            }
        }

        /**
         * Obtain the value.
         * @param pFormatter the data formatter
         * @return the money value
         * @throws OceanusException on error
         */
        public OceanusRatio getValue(final OceanusDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), OceanusRatio.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Ratio Value", e);
            }
        }
    }

    /**
     * The binaryColumn Class.
     */
    protected static final class PrometheusBinaryColumn
            extends PrometheusColumnDefinition {
        /**
         * The length of the column.
         */
        private final int theLength;

        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         * @param pLength the length of the column
         */
        PrometheusBinaryColumn(final PrometheusTableDefinition pTable,
                               final MetisDataFieldId pId,
                               final int pLength) {
            /* Record the column type */
            super(pTable, pId);
            theLength = pLength;
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            final PrometheusJDBCDriver myDriver = getDriver();
            pBuilder.append(myDriver.getDatabaseType(PrometheusColumnType.BINARY));
            if (myDriver.defineBinaryLength()) {
                pBuilder.append("(");
                pBuilder.append(theLength);
                pBuilder.append(')');
            }
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        void setValue(final byte[] pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        byte[] getValue() {
            return (byte[]) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final byte[] myValue = pResults.getBytes(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            pStatement.setBytes(pIndex, getValue());
        }
    }
}
