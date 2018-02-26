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
package net.sourceforge.joceanus.jmetis.atlas.ui.swing;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionValues.MetisFieldEncryptedValue;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableCharArrayColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableDateColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableDilutedPriceColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableDilutionColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableIconColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableIntegerColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableListColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableLongColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableMoneyColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTablePriceColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableRateColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableRatioColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableRawDecimalColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableScrollColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableShortColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableStringColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableUnitsColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableManager;
import net.sourceforge.joceanus.jmetis.eos.list.MetisEosListIndexed;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableUnitsColumn;

/**
 * Metis swing table manager.
 * @param <R> the item type
 */
public class MetisSwingTableManager<R extends MetisFieldTableItem>
        extends MetisTableManager<R, JComponent, Icon> {
    /**
     * Table List.
     */
    private final MetisSwingTableListManager<R> theList;

    /**
     * Table Calculator.
     */
    private MetisTableCalculator<R> theCalculator;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pClazz the class of the item
     * @param pList the versioned list
     */
    public MetisSwingTableManager(final TethysSwingGuiFactory pFactory,
                                  final Class<R> pClazz,
                                  final MetisEosListIndexed<R> pList) {
        /* Initialise underlying class */
        super(pFactory, pClazz);

        /* Create the table list */
        theList = new MetisSwingTableListManager<>(this, pList);
        getTable().setItems(theList.getTableList());
    }

    @Override
    protected TethysSwingTableManager<MetisDataFieldId, R> getTable() {
        return (TethysSwingTableManager<MetisDataFieldId, R>) super.getTable();
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pField the field
     * @param pClazz the item class
     * @return the value
     */
    private <T> T getItemFieldValue(final R pItem,
                                    final MetisFieldDef pField,
                                    final Class<T> pClazz) {
        return pField.getStorage().isCalculated()
                                                  ? getCalculatedFieldValue(pItem, pField, pClazz)
                                                  : getStandardFieldValue(pItem, pField, pClazz);
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pField the field
     * @param pClazz the item class
     * @return the value
     */
    private <T> T getStandardFieldValue(final R pItem,
                                        final MetisFieldDef pField,
                                        final Class<T> pClazz) {
        Object myValue = pField.getFieldValue(pItem);
        if (myValue == MetisDataFieldValue.SKIP) {
            myValue = null;
        }
        if (myValue instanceof MetisFieldEncryptedValue) {
            myValue = ((MetisFieldEncryptedValue) myValue).getValue();
        }
        return pClazz.cast(myValue);
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pField the field
     * @param pClazz the item class
     * @return the value
     */
    private <T> T getCalculatedFieldValue(final R pItem,
                                          final MetisFieldDef pField,
                                          final Class<T> pClazz) {
        final Object myValue = theCalculator == null
                                                     ? null
                                                     : theCalculator.calculateValue(pItem, pField);
        return pClazz.cast(myValue);
    }

    /**
     * Fire TableData changed.
     */
    protected void fireTableDataChanged() {
        getTable().fireTableDataChanged();
    }

    /**
     * Fire TableRow deleted.
     * @param pRowIndex the row index
     */
    protected void fireTableRowDeleted(final int pRowIndex) {
        getTable().fireTableRowDeleted(pRowIndex);
    }

    /**
     * Fire TableRow changed.
     * @param pRowIndex the row index
     */
    protected void fireTableRowChanged(final int pRowIndex) {
        getTable().fireTableRowChanged(pRowIndex);
    }

    /**
     * Fire TableRow added.
     * @param pRowIndex the row index
     */
    protected void fireTableRowAdded(final int pRowIndex) {
        getTable().fireTableRowAdded(pRowIndex);
    }

    @Override
    public void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theCalculator = pCalculator;
        getTable().fireTableDataChanged();
    }

    @Override
    public MetisSwingTableStringColumn<R> declareStringColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableStringColumn<>(this, getTable().declareStringColumn(pId));
    }

    @Override
    public MetisSwingTableCharArrayColumn<R> declareCharArrayColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableCharArrayColumn<>(this, getTable().declareCharArrayColumn(pId));
    }

    @Override
    public MetisSwingTableShortColumn<R> declareShortColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableShortColumn<>(this, getTable().declareShortColumn(pId));
    }

    @Override
    public MetisSwingTableIntegerColumn<R> declareIntegerColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableIntegerColumn<>(this, getTable().declareIntegerColumn(pId));
    }

    @Override
    public MetisSwingTableLongColumn<R> declareLongColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableLongColumn<>(this, getTable().declareLongColumn(pId));
    }

    @Override
    public MetisSwingTableRawDecimalColumn<R> declareRawDecimalColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableRawDecimalColumn<>(this, getTable().declareRawDecimalColumn(pId));
    }

    @Override
    public MetisSwingTableMoneyColumn<R> declareMoneyColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableMoneyColumn<>(this, getTable().declareMoneyColumn(pId));
    }

    @Override
    public MetisSwingTablePriceColumn<R> declarePriceColumn(final MetisDataFieldId pId) {
        return new MetisSwingTablePriceColumn<>(this, getTable().declarePriceColumn(pId));
    }

    @Override
    public MetisSwingTableRateColumn<R> declareRateColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableRateColumn<>(this, getTable().declareRateColumn(pId));
    }

    @Override
    public MetisSwingTableUnitsColumn<R> declareUnitsColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableUnitsColumn<>(this, getTable().declareUnitsColumn(pId));
    }

    @Override
    public MetisSwingTableDilutionColumn<R> declareDilutionColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableDilutionColumn<>(this, getTable().declareDilutionColumn(pId));
    }

    @Override
    public MetisSwingTableRatioColumn<R> declareRatioColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableRatioColumn<>(this, getTable().declareRatioColumn(pId));
    }

    @Override
    public MetisSwingTableDilutedPriceColumn<R> declareDilutedPriceColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableDilutedPriceColumn<>(this, getTable().declareDilutedPriceColumn(pId));
    }

    @Override
    public MetisSwingTableDateColumn<R> declareDateColumn(final MetisDataFieldId pId) {
        return new MetisSwingTableDateColumn<>(this, getTable().declareDateColumn(pId));
    }

    @Override
    public <T> MetisSwingTableScrollColumn<T, R> declareScrollColumn(final MetisDataFieldId pId,
                                                                     final Class<T> pClazz) {
        return new MetisSwingTableScrollColumn<>(this, getTable().declareScrollColumn(pId, pClazz), pClazz);
    }

    @Override
    public <T extends Comparable<T>> MetisSwingTableListColumn<T, R> declareListColumn(final MetisDataFieldId pId) {
        final TethysSwingTableListColumn<T, MetisDataFieldId, R> myColumn = getTable().declareListColumn(pId);
        return new MetisSwingTableListColumn<>(this, myColumn);
    }

    @Override
    public <T> MetisSwingTableIconColumn<T, R> declareIconColumn(final MetisDataFieldId pId,
                                                                 final Class<T> pClazz) {
        return new MetisSwingTableIconColumn<>(this, getTable().declareIconColumn(pId, pClazz), pClazz);
    }

    /**
     * String Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableStringColumn<R extends MetisFieldTableItem>
            extends MetisTableStringColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableStringColumn(final MetisSwingTableManager<R> pTable,
                                              final TethysSwingTableStringColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), String.class));
        }
    }

    /**
     * CharArray Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableCharArrayColumn<R extends MetisFieldTableItem>
            extends MetisTableCharArrayColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableCharArrayColumn(final MetisSwingTableManager<R> pTable,
                                                 final TethysSwingTableCharArrayColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), char[].class));
        }
    }

    /**
     * Short Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableShortColumn<R extends MetisFieldTableItem>
            extends MetisTableShortColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableShortColumn(final MetisSwingTableManager<R> pTable,
                                             final TethysSwingTableShortColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), Short.class));
        }
    }

    /**
     * Integer Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableIntegerColumn<R extends MetisFieldTableItem>
            extends MetisTableIntegerColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableIntegerColumn(final MetisSwingTableManager<R> pTable,
                                               final TethysSwingTableIntegerColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), Integer.class));
        }
    }

    /**
     * Long Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableLongColumn<R extends MetisFieldTableItem>
            extends MetisTableLongColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableLongColumn(final MetisSwingTableManager<R> pTable,
                                            final TethysSwingTableLongColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), Long.class));
        }
    }

    /**
     * RawDecimal Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableRawDecimalColumn<R extends MetisFieldTableItem>
            extends MetisTableRawDecimalColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableRawDecimalColumn(final MetisSwingTableManager<R> pTable,
                                                  final TethysSwingTableRawDecimalColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysDecimal.class));
        }
    }

    /**
     * Money Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableMoneyColumn<R extends MetisFieldTableItem>
            extends MetisTableMoneyColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableMoneyColumn(final MetisSwingTableManager<R> pTable,
                                             final TethysSwingTableMoneyColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysMoney.class));
        }
    }

    /**
     * Price Column.
     * @param <R> the item type
     */
    public static class MetisSwingTablePriceColumn<R extends MetisFieldTableItem>
            extends MetisTablePriceColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTablePriceColumn(final MetisSwingTableManager<R> pTable,
                                             final TethysSwingTablePriceColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysPrice.class));
        }
    }

    /**
     * Rate Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableRateColumn<R extends MetisFieldTableItem>
            extends MetisTableRateColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableRateColumn(final MetisSwingTableManager<R> pTable,
                                            final TethysSwingTableRateColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysRate.class));
        }
    }

    /**
     * Units Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableUnitsColumn<R extends MetisFieldTableItem>
            extends MetisTableUnitsColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableUnitsColumn(final MetisSwingTableManager<R> pTable,
                                             final TethysSwingTableUnitsColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysUnits.class));
        }
    }

    /**
     * Ratio Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableRatioColumn<R extends MetisFieldTableItem>
            extends MetisTableRatioColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableRatioColumn(final MetisSwingTableManager<R> pTable,
                                             final TethysSwingTableRatioColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysRatio.class));
        }
    }

    /**
     * Dilution Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableDilutionColumn<R extends MetisFieldTableItem>
            extends MetisTableDilutionColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableDilutionColumn(final MetisSwingTableManager<R> pTable,
                                                final TethysSwingTableDilutionColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysDilution.class));
        }
    }

    /**
     * DilutedPrice Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableDilutedPriceColumn<R extends MetisFieldTableItem>
            extends MetisTableDilutedPriceColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableDilutedPriceColumn(final MetisSwingTableManager<R> pTable,
                                                    final TethysSwingTableDilutedPriceColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysDilutedPrice.class));
        }
    }

    /**
     * Date Column.
     * @param <R> the item type
     */
    public static class MetisSwingTableDateColumn<R extends MetisFieldTableItem>
            extends MetisTableDateColumn<R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisSwingTableDateColumn(final MetisSwingTableManager<R> pTable,
                                            final TethysSwingTableDateColumn<MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), TethysDate.class));
        }
    }

    /**
     * Icon Column.
     * @param <T> the value type
     * @param <R> the item type
     */
    public static class MetisSwingTableIconColumn<T, R extends MetisFieldTableItem>
            extends MetisTableIconColumn<T, R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         * @param pClazz the value class
         */
        protected MetisSwingTableIconColumn(final MetisSwingTableManager<R> pTable,
                                            final TethysSwingTableIconColumn<T, MetisDataFieldId, R> pColumn,
                                            final Class<T> pClazz) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), pClazz));
        }
    }

    /**
     * Scroll Column.
     * @param <T> the value type
     * @param <R> the item type
     */
    public static class MetisSwingTableScrollColumn<T, R extends MetisFieldTableItem>
            extends MetisTableScrollColumn<T, R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         * @param pClazz the value class
         */
        protected MetisSwingTableScrollColumn(final MetisSwingTableManager<R> pTable,
                                              final TethysSwingTableScrollColumn<T, MetisDataFieldId, R> pColumn,
                                              final Class<T> pClazz) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), pClazz));
        }
    }

    /**
     * List Column.
     * @param <T> the value type
     * @param <R> the item type
     */
    public static class MetisSwingTableListColumn<T extends Comparable<T>, R extends MetisFieldTableItem>
            extends MetisTableListColumn<T, R, JComponent, Icon> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        @SuppressWarnings("unchecked")
        protected MetisSwingTableListColumn(final MetisSwingTableManager<R> pTable,
                                            final TethysSwingTableListColumn<T, MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pColumn.setCellValueFactory(p -> pTable.getItemFieldValue(p, getField(), List.class));
        }
    }
}
