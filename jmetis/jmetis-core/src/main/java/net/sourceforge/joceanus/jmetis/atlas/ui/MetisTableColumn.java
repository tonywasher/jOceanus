/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldVersionedDef;
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
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisTableColumn<T, R extends MetisFieldTableItem, N, I> {
    /**
     * The underlying table.
     */
    private final MetisTableManager<R, N, I> theTable;

    /**
     * The underlying column.
     */
    private final TethysTableColumn<T, MetisFieldId, R, N, I> theColumn;

    /**
     * The underlying field.
     */
    private final MetisFieldDef theField;

    /**
     * Constructor.
     * @param pTable the table
     * @param pColumn the column
     */
    protected MetisTableColumn(final MetisTableManager<R, N, I> pTable,
                               final TethysTableColumn<T, MetisFieldId, R, N, I> pColumn) {
        /* Store parameters */
        theTable = pTable;
        theColumn = pColumn;

        /* Obtain the field */
        final MetisFieldId myId = theColumn.getId();
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
    public MetisTableManager<R, N, I> getTable() {
        return theTable;
    }

    /**
     * Obtain the column.
     * @return the column
     */
    protected TethysTableColumn<T, MetisFieldId, R, N, I> getColumn() {
        return theColumn;
    }

    /**
     * Obtain the id of the column.
     * @return the id
     */
    public MetisFieldId getId() {
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
    public MetisFieldId getRePaintColumnId() {
        return theColumn.getRePaintColumnId();
    }

    /**
     * Set repaintColumnId.
     * @param pRePaintId the repaint id
     */
    public void setRepaintColumnId(final MetisFieldId pRePaintId) {
        theColumn.setRepaintColumnId(pRePaintId);
    }

    /**
     * String column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableStringColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<String, R, N, I>
            implements TethysTableValidatedColumn<String, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableStringColumn(final MetisTableManager<R, N, I> pTable,
                                         final TethysTableStringColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableStringColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableStringColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<String, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * CharArray column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableCharArrayColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<char[], R, N, I>
            implements TethysTableValidatedColumn<char[], R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableCharArrayColumn(final MetisTableManager<R, N, I> pTable,
                                            final TethysTableCharArrayColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableCharArrayColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableCharArrayColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<char[], R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * Short column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableShortColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<Short, R, N, I>
            implements TethysTableValidatedColumn<Short, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableShortColumn(final MetisTableManager<R, N, I> pTable,
                                        final TethysTableShortColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableShortColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableShortColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<Short, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * Integer column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableIntegerColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<Integer, R, N, I>
            implements TethysTableValidatedColumn<Integer, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableIntegerColumn(final MetisTableManager<R, N, I> pTable,
                                          final TethysTableIntegerColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableIntegerColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableIntegerColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<Integer, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * Long column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableLongColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<Long, R, N, I>
            implements TethysTableValidatedColumn<Long, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableLongColumn(final MetisTableManager<R, N, I> pTable,
                                       final TethysTableLongColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableLongColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableLongColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<Long, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * RawDecimals column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableRawDecimalColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysDecimal, R, N, I>
            implements TethysTableDecimalColumn<R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableRawDecimalColumn(final MetisTableManager<R, N, I> pTable,
                                             final TethysTableRawDecimalColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableRawDecimalColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableRawDecimalColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysDecimal, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }

        @Override
        public void setNumDecimals(final Function<R, Integer> pSupplier) {
            getColumn().setNumDecimals(pSupplier);
        }
    }

    /**
     * Money column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableMoneyColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysMoney, R, N, I>
            implements TethysTableCurrencyColumn<TethysMoney, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableMoneyColumn(final MetisTableManager<R, N, I> pTable,
                                        final TethysTableMoneyColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableMoneyColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableMoneyColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysMoney, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }

        @Override
        public void setDeemedCurrency(final Function<R, Currency> pSupplier) {
            getColumn().setDeemedCurrency(pSupplier);
        }
    }

    /**
     * Price column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTablePriceColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysPrice, R, N, I>
            implements TethysTableCurrencyColumn<TethysPrice, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTablePriceColumn(final MetisTableManager<R, N, I> pTable,
                                        final TethysTablePriceColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTablePriceColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTablePriceColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysPrice, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }

        @Override
        public void setDeemedCurrency(final Function<R, Currency> pSupplier) {
            getColumn().setDeemedCurrency(pSupplier);
        }
    }

    /**
     * Units column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableUnitsColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysUnits, R, N, I>
            implements TethysTableValidatedColumn<TethysUnits, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableUnitsColumn(final MetisTableManager<R, N, I> pTable,
                                        final TethysTableUnitsColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableUnitsColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableUnitsColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysUnits, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * Rate column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableRateColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysRate, R, N, I>
            implements TethysTableValidatedColumn<TethysRate, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableRateColumn(final MetisTableManager<R, N, I> pTable,
                                       final TethysTableRateColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableRateColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableRateColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysRate, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * Ratio column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableRatioColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysRatio, R, N, I>
            implements TethysTableValidatedColumn<TethysRatio, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableRatioColumn(final MetisTableManager<R, N, I> pTable,
                                        final TethysTableRatioColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableRatioColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableRatioColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysRatio, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * Dilution column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableDilutionColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysDilution, R, N, I>
            implements TethysTableValidatedColumn<TethysDilution, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableDilutionColumn(final MetisTableManager<R, N, I> pTable,
                                           final TethysTableDilutionColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableDilutionColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableDilutionColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysDilution, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }
    }

    /**
     * DilutedPrice column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableDilutedPriceColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysDilutedPrice, R, N, I>
            implements TethysTableCurrencyColumn<TethysDilutedPrice, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableDilutedPriceColumn(final MetisTableManager<R, N, I> pTable,
                                               final TethysTableDilutedPriceColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableDilutedPriceColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableDilutedPriceColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setValidator(final BiFunction<TethysDilutedPrice, R, String> pValidator) {
            getColumn().setValidator(pValidator);
        }

        @Override
        public void setDeemedCurrency(final Function<R, Currency> pSupplier) {
            getColumn().setDeemedCurrency(pSupplier);
        }
    }

    /**
     * Icon column.
     * @param <T> the item type
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableIconColumn<T, R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<T, R, N, I>
            implements TethysTableIconConfig<T, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableIconColumn(final MetisTableManager<R, N, I> pTable,
                                       final TethysTableIconColumn<T, MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableIconColumn<T, MetisFieldId, R, N, I> getColumn() {
            return (TethysTableIconColumn<T, MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setIconMapSet(final Function<R, TethysIconMapSet<T>> pSupplier) {
            getColumn().setIconMapSet(pSupplier);
        }
    }

    /**
     * Date column.
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableDateColumn<R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<TethysDate, R, N, I>
            implements TethysTableDateConfig<R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableDateColumn(final MetisTableManager<R, N, I> pTable,
                                       final TethysTableDateColumn<MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableDateColumn<MetisFieldId, R, N, I> getColumn() {
            return (TethysTableDateColumn<MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setDateConfigurator(final BiConsumer<R, TethysDateConfig> pConfigurator) {
            getColumn().setDateConfigurator(pConfigurator);
        }
    }

    /**
     * Scroll column.
     * @param <T> the item type
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableScrollColumn<T, R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<T, R, N, I>
            implements TethysTableScrollConfig<T, R, I> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableScrollColumn(final MetisTableManager<R, N, I> pTable,
                                         final TethysTableScrollColumn<T, MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableScrollColumn<T, MetisFieldId, R, N, I> getColumn() {
            return (TethysTableScrollColumn<T, MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setMenuConfigurator(final BiConsumer<R, TethysScrollMenu<T, I>> pConfigurator) {
            getColumn().setMenuConfigurator(pConfigurator);
        }
    }

    /**
     * List column.
     * @param <T> the item type
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisTableListColumn<T extends Comparable<T>, R extends MetisFieldTableItem, N, I>
            extends MetisTableColumn<List<T>, R, N, I>
            implements TethysTableListConfig<T, R> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisTableListColumn(final MetisTableManager<R, N, I> pTable,
                                       final TethysTableListColumn<T, MetisFieldId, R, N, I> pColumn) {
            super(pTable, pColumn);
        }

        @Override
        protected TethysTableListColumn<T, MetisFieldId, R, N, I> getColumn() {
            return (TethysTableListColumn<T, MetisFieldId, R, N, I>) super.getColumn();
        }

        @Override
        public void setSelectables(final Function<R, Iterator<T>> pSelectables) {
            getColumn().setSelectables(pSelectables);
        }
    }
}
