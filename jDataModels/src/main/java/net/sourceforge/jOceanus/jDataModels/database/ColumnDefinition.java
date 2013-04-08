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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.SortOrder;

import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;

/**
 * Column definition classes handling data-type specifics.
 * @author Tony Washer
 */
public abstract class ColumnDefinition {
    /**
     * Table Definition.
     */
    private final TableDefinition theTable;

    /**
     * Column Identity.
     */
    private final JDataField theIdentity;

    /**
     * Is the column null-able.
     */
    private boolean isNullable = false;

    /**
     * Is the value set.
     */
    private boolean isValueSet = false;

    /**
     * The value of the column.
     */
    private Object theValue = null;

    /**
     * The sort order of the column.
     */
    private SortOrder theOrder = null;

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
    protected JDataField getColumnId() {
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
    protected JDBCDriver getDriver() {
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
     * Constructor.
     * @param pTable the table to which the column belongs
     * @param pId the column id
     */
    protected ColumnDefinition(final TableDefinition pTable,
                               final JDataField pId) {
        /* Record the identity and table */
        theIdentity = pId;
        theTable = pTable;

        /* Add to the map */
        theTable.getMap().put(theIdentity, this);
    }

    /**
     * Build the creation string for this column.
     * @param pBuilder the String builder
     */
    protected void buildCreateString(final StringBuilder pBuilder) {
        /* Add the name of the column */
        pBuilder.append(TableDefinition.QUOTE_STRING);
        pBuilder.append(getColumnName());
        pBuilder.append(TableDefinition.QUOTE_STRING);
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
    protected abstract void buildColumnType(final StringBuilder pBuilder);

    /**
     * Load the value for this column.
     * @param pResults the results
     * @param pIndex the index of the result column
     * @throws SQLException on error
     */
    protected abstract void loadValue(ResultSet pResults,
                                      final int pIndex) throws SQLException;

    /**
     * Store the value for this column.
     * @param pStatement the prepared statement
     * @param pIndex the index of the statement
     * @throws SQLException on error
     */
    protected abstract void storeValue(PreparedStatement pStatement,
                                       final int pIndex) throws SQLException;

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
    protected void locateReference(final List<DatabaseTable<?>> pTables) {
    }

    /**
     * The integerColumn Class.
     */
    protected static class IntegerColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected IntegerColumn(final TableDefinition pTable,
                                final JDataField pId) {
            /* Record the column type and name */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Integer));
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
            int myValue = pResults.getInt(pIndex);
            if ((myValue == 0)
                && (pResults.wasNull())) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            Integer myValue = getValue();
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
    protected static class IdColumn
            extends IntegerColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         */
        protected IdColumn(final TableDefinition pTable) {
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
    protected static class ReferenceColumn
            extends IntegerColumn {
        /**
         * The name of the referenced table.
         */
        private final String theReference;

        /**
         * The definition of the referenced table.
         */
        private TableDefinition theDefinition = null;

        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         * @param pRefTable the name of the referenced table
         */
        protected ReferenceColumn(final TableDefinition pTable,
                                  final JDataField pId,
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
            pBuilder.append(" references ");
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(theReference);
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append('(');
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(DataItem.FIELD_ID.getName());
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(')');
        }

        @Override
        protected void locateReference(final List<DatabaseTable<?>> pTables) {
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
         * build Join String.
         * @param pBuilder the String Builder
         * @param pChar the character for this table
         * @param pOffset the join offset
         */
        protected void buildJoinString(final StringBuilder pBuilder,
                                       final char pChar,
                                       final Integer pOffset) {
            Integer myOffset = pOffset;

            /* Calculate join character */
            char myChar = (char) ('a' + myOffset);

            /* Build Initial part of string */
            pBuilder.append(" join ");
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(theReference);
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(" ");
            pBuilder.append(myChar);

            /* Build the join */
            pBuilder.append(" on ");
            pBuilder.append(pChar);
            pBuilder.append(".");
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(getColumnName());
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(" = ");
            pBuilder.append(myChar);
            pBuilder.append(".");
            pBuilder.append(TableDefinition.QUOTE_STRING);
            pBuilder.append(DataItem.FIELD_ID.getName());
            pBuilder.append(TableDefinition.QUOTE_STRING);

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
        protected void buildOrderString(final StringBuilder pBuilder,
                                        final Integer pOffset) {
            Iterator<ColumnDefinition> myIterator;
            ColumnDefinition myDef;
            boolean myFirst = true;
            Integer myOffset = pOffset;

            /* Calculate join character */
            char myChar = (char) ('a' + myOffset);

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
                    char myNewChar = (char) ('a' + myOffset);

                    /* Add the order string for the underlying table. */
                    /* Note that forced to implement in one line to avoid Sonar false positive. */
                    pBuilder.append((((ReferenceColumn) myDef).theDefinition).getOrderString(myNewChar, myOffset));

                    /* else standard column */
                } else {
                    /* Build the column name */
                    pBuilder.append(myChar);
                    pBuilder.append(".");
                    pBuilder.append(TableDefinition.QUOTE_STRING);
                    pBuilder.append(myDef.getColumnName());
                    pBuilder.append(TableDefinition.QUOTE_STRING);
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
    protected static class ShortColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected ShortColumn(final TableDefinition pTable,
                              final JDataField pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Short));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Short pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Short getValue() {
            return (Short) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            short myValue = pResults.getShort(pIndex);
            if ((myValue == 0)
                && (pResults.wasNull())) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            Short myValue = getValue();
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
    protected static class LongColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected LongColumn(final TableDefinition pTable,
                             final JDataField pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Long));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Long pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Long getValue() {
            return (Long) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            long myValue = pResults.getLong(pIndex);
            if ((myValue == 0)
                && (pResults.wasNull())) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            Long myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.BIGINT);
            } else {
                pStatement.setLong(pIndex, myValue);
            }
        }
    }

    /**
     * The floatColumn Class.
     */
    protected static class FloatColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected FloatColumn(final TableDefinition pTable,
                              final JDataField pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Float));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Float pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Float getValue() {
            return (Float) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            float myValue = pResults.getFloat(pIndex);
            if ((myValue == 0)
                && (pResults.wasNull())) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            Float myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.REAL);
            } else {
                pStatement.setFloat(pIndex, myValue);
            }
        }
    }

    /**
     * The doubleColumn Class.
     */
    protected static class DoubleColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected DoubleColumn(final TableDefinition pTable,
                               final JDataField pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Double));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Double pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Double getValue() {
            return (Double) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            double myValue = pResults.getDouble(pIndex);
            if ((myValue == 0)
                && (pResults.wasNull())) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            Double myValue = getValue();
            if (myValue == null) {
                pStatement.setNull(pIndex, Types.FLOAT);
            } else {
                pStatement.setDouble(pIndex, myValue);
            }
        }
    }

    /**
     * The dateColumn Class.
     */
    protected static class DateColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected DateColumn(final TableDefinition pTable,
                             final JDataField pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Date));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Date pValue) {
            super.setObject(pValue);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JDateDay pValue) {
            super.setObject((pValue == null)
                    ? null
                    : pValue.getDate());
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Date getValue() {
            return (Date) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            Date myValue = pResults.getDate(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            java.sql.Date myDate = null;
            Date myValue = getValue();

            /* Build the date as a SQL date */
            if (myValue != null) {
                myDate = new java.sql.Date(myValue.getTime());
            }
            pStatement.setDate(pIndex, myDate);
        }
    }

    /**
     * The booleanColumn Class.
     */
    protected static class BooleanColumn
            extends ColumnDefinition {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected BooleanColumn(final TableDefinition pTable,
                                final JDataField pId) {
            /* Record the column type */
            super(pTable, pId);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Boolean));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Boolean pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected Boolean getValue() {
            return (Boolean) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            boolean myValue = pResults.getBoolean(pIndex);
            if ((!myValue)
                && (pResults.wasNull())) {
                setValue(null);
            } else {
                setValue(myValue);
            }
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            Boolean myValue = getValue();
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
            extends ColumnDefinition {
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
        protected StringColumn(final TableDefinition pTable,
                               final JDataField pId,
                               final int pLength) {
            /* Record the column type */
            super(pTable, pId);
            theLength = pLength;
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.String));
            pBuilder.append("(");
            pBuilder.append(theLength);
            pBuilder.append(')');
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
            String myValue = pResults.getString(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            pStatement.setString(pIndex, getValue());
        }
    }

    /**
     * The moneyColumn Class.
     */
    protected static class MoneyColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected MoneyColumn(final TableDefinition pTable,
                              final JDataField pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Money));
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JMoney pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            BigDecimal myDecimal = null;
            String myValue = getValue();
            if (myValue != null) {
                myDecimal = new BigDecimal(myValue);
            }
            pStatement.setBigDecimal(pIndex, myDecimal);
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
        protected RateColumn(final TableDefinition pTable,
                             final JDataField pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Decimal));
            pBuilder.append("(18,4)");
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JRate pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            BigDecimal myDecimal = null;
            String myValue = getValue();
            if (myValue != null) {
                myDecimal = new BigDecimal(myValue);
            }
            pStatement.setBigDecimal(pIndex, myDecimal);
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
        protected PriceColumn(final TableDefinition pTable,
                              final JDataField pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Decimal));
            pBuilder.append("(18,4)");
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JPrice pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
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
        protected UnitsColumn(final TableDefinition pTable,
                              final JDataField pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Decimal));
            pBuilder.append("(18,4)");
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JUnits pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }
    }

    /**
     * The dilutionColumn Class.
     */
    protected static final class DilutionColumn
            extends StringColumn {
        /**
         * Constructor.
         * @param pTable the table to which the column belongs
         * @param pId the column id
         */
        protected DilutionColumn(final TableDefinition pTable,
                                 final JDataField pId) {
            /* Record the column type */
            super(pTable, pId, 0);
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            pBuilder.append(getDriver().getDatabaseType(ColumnType.Decimal));
            pBuilder.append("(18,6)");
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JDilution pValue) {
            String myString = null;
            if (pValue != null) {
                myString = pValue.toString();
            }
            super.setObject(myString);
        }
    }

    /**
     * The binaryColumn Class.
     */
    protected static final class BinaryColumn
            extends ColumnDefinition {
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
        protected BinaryColumn(final TableDefinition pTable,
                               final JDataField pId,
                               final int pLength) {
            /* Record the column type */
            super(pTable, pId);
            theLength = pLength;
        }

        @Override
        protected void buildColumnType(final StringBuilder pBuilder) {
            /* Add the column type */
            JDBCDriver myDriver = getDriver();
            pBuilder.append(myDriver.getDatabaseType(ColumnType.Binary));
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
        protected void setValue(final byte[] pValue) {
            super.setObject(pValue);
        }

        /**
         * Get the value.
         * @return the value
         */
        protected byte[] getValue() {
            return (byte[]) super.getObject();
        }

        @Override
        protected void loadValue(final ResultSet pResults,
                                 final int pIndex) throws SQLException {
            byte[] myValue = pResults.getBytes(pIndex);
            setValue(myValue);
        }

        @Override
        protected void storeValue(final PreparedStatement pStatement,
                                  final int pIndex) throws SQLException {
            pStatement.setBytes(pIndex, getValue());
        }
    }

    /**
     * Column types.
     */
    public enum ColumnType {
        /**
         * Boolean.
         */
        Boolean,

        /**
         * Short.
         */
        Short,

        /**
         * Integer.
         */
        Integer,

        /**
         * Long.
         */
        Long,

        /**
         * Float.
         */
        Float,

        /**
         * Double.
         */
        Double,

        /**
         * String.
         */
        String,

        /**
         * Date.
         */
        Date,

        /**
         * Money.
         */
        Money,

        /**
         * Decimal.
         */
        Decimal,

        /**
         * Binary.
         */
        Binary;
    }
}
