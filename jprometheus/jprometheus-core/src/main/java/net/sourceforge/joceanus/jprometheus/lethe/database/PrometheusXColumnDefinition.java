/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusColumnType;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusJDBCDriver;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Column definition classes handling data-type specifics.
 * @author Tony Washer
 */
public abstract class PrometheusXColumnDefinition {
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
    private final PrometheusXTableDefinition theTable;

    /**
     * Column Identity.
     */
    private final MetisLetheField theIdentity;

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
    private SortOrder theOrder;

    /**
     * Constructor.
     * @param pTable the table to which the column belongs
     * @param pId the column id
     */
    protected PrometheusXColumnDefinition(final PrometheusXTableDefinition pTable,
                                          final MetisLetheField pId) {
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
        return theIdentity.getName();
    }

    /**
     * Obtain the column id.
     * @return the id
     */
    protected MetisLetheField getColumnId() {
        return theIdentity;
    }

    /**
     * Obtain the sort order.
     * @return the sort order
     */
    protected SortOrder getSortOrder() {
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
        pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
        pBuilder.append(getColumnName());
        pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
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
    public void setSortOrder(final SortOrder pOrder) {
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
    protected void locateReference(final List<PrometheusXTableDataItem<?>> pTables) {
    }

    /**
     * The integerColumn Class.
     */
    protected static class IntegerColumn
            extends PrometheusXColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected IntegerColumn(final PrometheusXTableDefinition pTable,
                                final MetisLetheField pId) {
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
    protected static final class IdColumn
            extends IntegerColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         */
        IdColumn(final PrometheusXTableDefinition pTable) {
            /* Record the column type */
            super(pTable, DataItem.FIELD_ID);
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
    protected static final class ReferenceColumn
            extends IntegerColumn {
        /**
         * The name of the referenced table.
         */
        private final String theReference;

        /**
         * The definition of the referenced table.
         */
        private PrometheusXTableDefinition theDefinition;

        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         * @param pRefTable the name of the referenced table
         */
        ReferenceColumn(final PrometheusXTableDefinition pTable,
                        final MetisLetheField pId,
                        final String pRefTable) {
            /* Record the column type */
            super(pTable, pId);
            theReference = pRefTable;
        }

        @Override
        public void setSortOrder(final SortOrder pOrder) {
            super.setSortOrder(pOrder);
            setSortOnReference();
        }

        @Override
        protected void buildKeyReference(final StringBuilder pBuilder) {
            /* Add the reference */
            pBuilder.append(" REFERENCES ");
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(theReference);
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(STR_OPNBRK);
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(DataItem.FIELD_ID.getName());
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(STR_CLSBRK);
        }

        @Override
        protected void locateReference(final List<PrometheusXTableDataItem<?>> pTables) {
            /* Access the Iterator */
            final ListIterator<PrometheusXTableDataItem<?>> myIterator;
            myIterator = pTables.listIterator();

            /* Loop through the Tables */
            while (myIterator.hasNext()) {
                /* Access Table */
                final PrometheusXTableDataItem<?> myTable = myIterator.next();

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
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(theReference);
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(" ");
            pBuilder.append(myChar);

            /* Build the join */
            pBuilder.append(" on ");
            pBuilder.append(pChar);
            pBuilder.append(".");
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(getColumnName());
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(" = ");
            pBuilder.append(myChar);
            pBuilder.append(".");
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
            pBuilder.append(DataItem.FIELD_ID.getName());
            pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);

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
            final Iterator<PrometheusXColumnDefinition> myIterator;
            PrometheusXColumnDefinition myDef;
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
                if (myDef instanceof ReferenceColumn) {
                    /* Increment offset */
                    myOffset++;

                    /* Determine new char */
                    final char myNewChar = (char) ('a' + myOffset);

                    /* Add the order string for the underlying table. */
                    /* Note that forced to implement in one line to avoid Sonar false positive. */
                    pBuilder.append((((ReferenceColumn) myDef).theDefinition).getOrderString(myNewChar, myOffset));

                    /* else standard column */
                } else {
                    /* Build the column name */
                    pBuilder.append(myChar);
                    pBuilder.append(".");
                    pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
                    pBuilder.append(myDef.getColumnName());
                    pBuilder.append(PrometheusXTableDefinition.QUOTE_STRING);
                    if (myDef.getSortOrder() == SortOrder.DESCENDING) {
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
    protected static final class ShortColumn
            extends PrometheusXColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        ShortColumn(final PrometheusXTableDefinition pTable,
                    final MetisLetheField pId) {
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
    protected static final class LongColumn
            extends PrometheusXColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        LongColumn(final PrometheusXTableDefinition pTable,
                   final MetisLetheField pId) {
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
    protected static final class DateColumn
            extends PrometheusXColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        DateColumn(final PrometheusXTableDefinition pTable,
                   final MetisLetheField pId) {
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
        void setValue(final TethysDate pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        TethysDate getValue() {
            return (TethysDate) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            final Date myValue = pResults.getDate(pIndex);
            setValue((myValue == null)
                                       ? null
                                       : new TethysDate(myValue));
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            final TethysDate myValue = getValue();

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
    protected static final class BooleanColumn
            extends PrometheusXColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        BooleanColumn(final PrometheusXTableDefinition pTable,
                      final MetisLetheField pId) {
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
    protected static class StringColumn
            extends PrometheusXColumnDefinition {
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
        protected StringColumn(final PrometheusXTableDefinition pTable,
                               final MetisLetheField pId,
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
    protected static final class MoneyColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        MoneyColumn(final PrometheusXTableDefinition pTable,
                    final MetisLetheField pId) {
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
        void setValue(final TethysMoney pValue) {
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
        public TethysMoney getValue(final TethysUIDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), TethysMoney.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Money Value", e);
            }
        }
    }

    /**
     * The rateColumn Class.
     */
    protected static final class RateColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        RateColumn(final PrometheusXTableDefinition pTable,
                   final MetisLetheField pId) {
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
        void setValue(final TethysRate pValue) {
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
        public TethysRate getValue(final TethysUIDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), TethysRate.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Rate Value", e);
            }
        }
    }

    /**
     * The priceColumn Class.
     */
    protected static final class PriceColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        PriceColumn(final PrometheusXTableDefinition pTable,
                    final MetisLetheField pId) {
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
        void setValue(final TethysPrice pValue) {
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
        public TethysPrice getValue(final TethysUIDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), TethysPrice.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Price Value", e);
            }
        }
    }

    /**
     * The unitsColumn Class.
     */
    protected static final class UnitsColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        UnitsColumn(final PrometheusXTableDefinition pTable,
                    final MetisLetheField pId) {
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
        void setValue(final TethysUnits pValue) {
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
        public TethysUnits getValue(final TethysUIDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), TethysUnits.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Units Value", e);
            }
        }
    }

    /**
     * The ratioColumn Class.
     */
    protected static final class RatioColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        RatioColumn(final PrometheusXTableDefinition pTable,
                    final MetisLetheField pId) {
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
        void setValue(final TethysRatio pValue) {
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
        public TethysRatio getValue(final TethysUIDataFormatter pFormatter) throws OceanusException {
            try {
                return pFormatter.parseValue(getValue(), TethysRatio.class);
            } catch (IllegalArgumentException e) {
                throw new PrometheusDataException(getValue(), "Bad Ratio Value", e);
            }
        }
    }

    /**
     * The binaryColumn Class.
     */
    protected static final class BinaryColumn
            extends PrometheusXColumnDefinition {
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
        BinaryColumn(final PrometheusXTableDefinition pTable,
                     final MetisLetheField pId,
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
