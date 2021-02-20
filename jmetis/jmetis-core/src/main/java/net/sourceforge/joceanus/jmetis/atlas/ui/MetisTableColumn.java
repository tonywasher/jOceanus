/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableCurrencyColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableDateConfig;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableIconConfig;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableListConfig;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableScrollConfig;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableUnitsColumn;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableValidatedColumn;

/**
 * Table Column.
 * @param <T> the value type
 * @param <R> the row type
 */
public abstract class MetisTableColumn<T, R extends MetisFieldTableItem> {
    /**
     * The underlying table.
     */
    private final MetisTableManager<R> theTable;

    /**
     * The underlying column.
     */
    private final TethysTableColumn<T, MetisDataFieldId, R> theColumn;

    /**
     * The underlying field.
     */
    private final MetisFieldDef theField;

    /**
     * Constructor.
     * @param pTable the table
     * @param pColumn the column
     */
    protected MetisTableColumn(final MetisTableManager<R> pTable,
                               final TethysTableColumn<T, MetisDataFieldId, R> pColumn) {
        /* Store parameters */
        theTable = pTable;
        theColumn = pColumn;

        /* Obtain the field */
        final MetisDataFieldId myId = theColumn.getId();
        theField = theTable.getFieldForId(myId);

        /* Default to editable if the field is Versioned */
        if (theField instanceof MetisFieldVersionedDef) {
            theColumn.setOnCommit(this::commitTheValue);
        } else {
            setEditable(false);
        }
    }

    /**
     * Commit value.
     * @param pRow the row
     * @param pValue the new value
     * @throws OceanusException on error
     */
    private void commitTheValue(final R pRow,
                                final T pValue) throws OceanusException {
        if (theField instanceof MetisFieldVersionedDef) {
            ((MetisFieldVersionedDef) theField).setFieldValue(pRow, pValue);
        }
    }

    /**
     * Obtain the table.
     * @return the table
     */
    public MetisTableManager<R> getTable() {
        return theTable;
    }

    /**
     * Obtain the column.
     * @return the column
     */
    protected TethysTableColumn<T, MetisDataFieldId, R> getColumn() {
        return theColumn;
    }

    /**
     * Obtain the id of the column.
     * @return the id
     */
    public MetisDataFieldId getId() {
        return theColumn.getId();
    }

    /**
     * Obtain the field of the column.
     * @return the field
     */
    protected MetisFieldDef getField() {
        return theField;
    }

    /**
     * Obtain the cellType.
     * @return the cellType
     */
    public TethysFieldType getCellType() {
        return theColumn.getCellType();
    }

    /**
     * Obtain the name of the column.
     * @return the name
     */
    public String getName() {
        return theColumn.getName();
    }

    /**
     * Set the name of the column.
     * @param pName the name
     */
    public void setName(final String pName) {
        theColumn.setName(pName);
    }

    /**
     * Set the width of the column.
     * @param pWidth the width
     */
    public void setColumnWidth(final int pWidth) {
        theColumn.setColumnWidth(pWidth);
    }

    /**
     * is the column visible?
     * @return true/false
     */
    public boolean isVisible() {
        return theColumn.isVisible();
    }

    /**
     * Set the visibility of the column.
     * @param pVisible true/false
     */
    public void setVisible(final boolean pVisible) {
        theColumn.setVisible(pVisible);
    }

    /**
     * is the column edit-able?
     * @return true/false
     */
    public boolean isEditable() {
        return theColumn.isEditable();
    }

    /**
     * Set the edit-ability of the column.
     * @param pEditable true/false
     */
    public void setEditable(final boolean pEditable) {
        theColumn.setEditable(pEditable
                              && theField instanceof MetisFieldVersionedDef);
    }

    /**
     * Set the predicate to determine whether a cell is editable.
     * @param pEditable the predicate
     */
    public void setCellEditable(final Predicate<R> pEditable) {
        theColumn.setCellEditable(pEditable);
    }

    /**
     * do we rePaintColumn on commit?
     * @return true/false
     */
    public boolean doRePaintColumnOnCommit() {
        return theColumn.doRePaintColumnOnCommit();
    }

    /**
     * Set repaintColumn on Commit.
     * @param pRePaint the flag
     */
    public void setRepaintColumnOnCommit(final boolean pRePaint) {
        theColumn.setRepaintColumnOnCommit(pRePaint);
    }

    /**
     * get the column id which forces a rePaint.
     * @return the column id
     */
    public MetisDataFieldId getRePaintColumnId() {
        return theColumn.getRePaintColumnId();
    }

    /**
     * Set repaintColumnId.
     * @param pRePaintId the repaint id
     */
    public void setRepaintColumnId(final MetisDataFieldId pRePaintId) {
        theColumn.setRepaintColumnId(pRePaintId);
    }

    /**
     * String column.
     * @param <R> the row type
     */
    public static class MetisTableStringColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<String, R>
            implements TethysTableValidatedColumn<String, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableStringColumn(final MetisTableManager<R> pTable,
                                         final TethysTableStringColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableStringColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableStringColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableStringColumn<R> setValidator(final BiFunction<String, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * CharArray column.
     * @param <R> the row type
     */
    public static class MetisTableCharArrayColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<char[], R>
            implements TethysTableValidatedColumn<char[], R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableCharArrayColumn(final MetisTableManager<R> pTable,
                                            final TethysTableCharArrayColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableCharArrayColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableCharArrayColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableCharArrayColumn<R> setValidator(final BiFunction<char[], R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * Short column.
     * @param <R> the row type
     */
    public static class MetisTableShortColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<Short, R>
            implements TethysTableValidatedColumn<Short, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableShortColumn(final MetisTableManager<R> pTable,
                                        final TethysTableShortColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableShortColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableShortColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableShortColumn<R> setValidator(final BiFunction<Short, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * Integer column.
     * @param <R> the row type
     */
    public static class MetisTableIntegerColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<Integer, R>
            implements TethysTableValidatedColumn<Integer, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableIntegerColumn(final MetisTableManager<R> pTable,
                                          final TethysTableIntegerColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableIntegerColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableIntegerColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableIntegerColumn<R> setValidator(final BiFunction<Integer, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * Long column.
     * @param <R> the row type
     */
    public static class MetisTableLongColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<Long, R>
            implements TethysTableValidatedColumn<Long, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableLongColumn(final MetisTableManager<R> pTable,
                                       final TethysTableLongColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableLongColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableLongColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableLongColumn<R> setValidator(final BiFunction<Long, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * RawDecimals column.
     * @param <R> the row type
     */
    public static class MetisTableRawDecimalColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysDecimal, R>
            implements TethysTableDecimalColumn<R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableRawDecimalColumn(final MetisTableManager<R> pTable,
                                             final TethysTableRawDecimalColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableRawDecimalColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableRawDecimalColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableRawDecimalColumn<R> setValidator(final BiFunction<TethysDecimal, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }

        @Override
        public MetisTableRawDecimalColumn<R> setNumDecimals(final ToIntFunction<R> pSupplier) {
            getColumn().setNumDecimals(pSupplier);
            return this;
        }
    }

    /**
     * Money column.
     * @param <R> the row type
     */
    public static class MetisTableMoneyColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysMoney, R>
            implements TethysTableCurrencyColumn<TethysMoney, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableMoneyColumn(final MetisTableManager<R> pTable,
                                        final TethysTableMoneyColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableMoneyColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableMoneyColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableMoneyColumn<R> setValidator(final BiFunction<TethysMoney, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }

        @Override
        public MetisTableMoneyColumn<R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
            getColumn().setDeemedCurrency(pSupplier);
            return this;
        }
    }

    /**
     * Price column.
     * @param <R> the row type
     */
    public static class MetisTablePriceColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysPrice, R>
            implements TethysTableCurrencyColumn<TethysPrice, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTablePriceColumn(final MetisTableManager<R> pTable,
                                        final TethysTablePriceColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTablePriceColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTablePriceColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTablePriceColumn<R> setValidator(final BiFunction<TethysPrice, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }

        @Override
        public MetisTablePriceColumn<R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
            getColumn().setDeemedCurrency(pSupplier);
            return this;
        }
    }

    /**
     * Units column.
     * @param <R> the row type
     */
    public static class MetisTableUnitsColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysUnits, R>
            implements TethysTableValidatedColumn<TethysUnits, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableUnitsColumn(final MetisTableManager<R> pTable,
                                        final TethysTableUnitsColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableUnitsColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableUnitsColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableUnitsColumn<R> setValidator(final BiFunction<TethysUnits, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * Rate column.
     * @param <R> the row type
     */
    public static class MetisTableRateColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysRate, R>
            implements TethysTableValidatedColumn<TethysRate, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableRateColumn(final MetisTableManager<R> pTable,
                                       final TethysTableRateColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableRateColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableRateColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableRateColumn<R> setValidator(final BiFunction<TethysRate, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * Ratio column.
     * @param <R> the row type
     */
    public static class MetisTableRatioColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysRatio, R>
            implements TethysTableValidatedColumn<TethysRatio, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableRatioColumn(final MetisTableManager<R> pTable,
                                        final TethysTableRatioColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableRatioColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableRatioColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableRatioColumn<R> setValidator(final BiFunction<TethysRatio, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * Dilution column.
     * @param <R> the row type
     */
    public static class MetisTableDilutionColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysDilution, R>
            implements TethysTableValidatedColumn<TethysDilution, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableDilutionColumn(final MetisTableManager<R> pTable,
                                           final TethysTableDilutionColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableDilutionColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableDilutionColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableDilutionColumn<R> setValidator(final BiFunction<TethysDilution, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }
    }

    /**
     * DilutedPrice column.
     * @param <R> the row type
     */
    public static class MetisTableDilutedPriceColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysDilutedPrice, R>
            implements TethysTableCurrencyColumn<TethysDilutedPrice, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableDilutedPriceColumn(final MetisTableManager<R> pTable,
                                               final TethysTableDilutedPriceColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableDilutedPriceColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableDilutedPriceColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableDilutedPriceColumn<R> setValidator(final BiFunction<TethysDilutedPrice, R, String> pValidator) {
            getColumn().setValidator(pValidator);
            return this;
        }

        @Override
        public MetisTableDilutedPriceColumn<R> setDeemedCurrency(final Function<R, Currency> pSupplier) {
            getColumn().setDeemedCurrency(pSupplier);
            return this;
        }
    }

    /**
     * Icon column.
     * @param <T> the item type
     * @param <R> the row type
     */
    public static class MetisTableIconColumn<T, R extends MetisFieldTableItem>
            extends MetisTableColumn<T, R>
            implements TethysTableIconConfig<T, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableIconColumn(final MetisTableManager<R> pTable,
                                       final TethysTableIconColumn<T, MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableIconColumn<T, MetisDataFieldId, R> getColumn() {
            return (TethysTableIconColumn<T, MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableIconColumn<T, R> setIconMapSet(final Function<R, TethysIconMapSet<T>> pSupplier) {
            getColumn().setIconMapSet(pSupplier);
            return this;
        }
    }

    /**
     * Date column.
     * @param <R> the row type
     */
    public static class MetisTableDateColumn<R extends MetisFieldTableItem>
            extends MetisTableColumn<TethysDate, R>
            implements TethysTableDateConfig<R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableDateColumn(final MetisTableManager<R> pTable,
                                       final TethysTableDateColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableDateColumn<MetisDataFieldId, R> getColumn() {
            return (TethysTableDateColumn<MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableDateColumn<R> setDateConfigurator(final BiConsumer<R, TethysDateConfig> pConfigurator) {
            getColumn().setDateConfigurator(pConfigurator);
            return this;
        }
    }

    /**
     * Scroll column.
     * @param <T> the item type
     * @param <R> the row type
     */
    public static class MetisTableScrollColumn<T, R extends MetisFieldTableItem>
            extends MetisTableColumn<T, R>
            implements TethysTableScrollConfig<T, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableScrollColumn(final MetisTableManager<R> pTable,
                                         final TethysTableScrollColumn<T, MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableScrollColumn<T, MetisDataFieldId, R> getColumn() {
            return (TethysTableScrollColumn<T, MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableScrollColumn<T, R> setMenuConfigurator(final BiConsumer<R, TethysScrollMenu<T>> pConfigurator) {
            getColumn().setMenuConfigurator(pConfigurator);
            return this;
        }
    }

    /**
     * List column.
     * @param <T> the item type
     * @param <R> the row type
     */
    public static class MetisTableListColumn<T extends Comparable<T>, R extends MetisFieldTableItem>
            extends MetisTableColumn<List<T>, R>
            implements TethysTableListConfig<T, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableListColumn(final MetisTableManager<R> pTable,
                                       final TethysTableListColumn<T, MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableListColumn<T, MetisDataFieldId, R> getColumn() {
            return (TethysTableListColumn<T, MetisDataFieldId, R>) super.getColumn();
        }

        @Override
        public MetisTableListColumn<T, R> setSelectables(final Function<R, Iterator<T>> pSelectables) {
            getColumn().setSelectables(pSelectables);
            return this;
        }
    }
}
