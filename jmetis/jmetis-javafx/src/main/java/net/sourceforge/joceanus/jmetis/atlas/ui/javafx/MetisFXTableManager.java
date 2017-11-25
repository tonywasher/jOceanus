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
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableUnitsColumn;

/**
 * Metis javaFX table manager.
 * @param <R> the item type
 */
public class MetisFXTableManager<R extends MetisFieldTableItem>
        extends MetisTableManager<R, Node, Node> {
    /**
     * Table List.
     */
    private final MetisFXTableList<R> theList;

    /**
     * Table ListFields.
     */
    private final MetisFXTableListFields<R> theItemFields;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pClazz the class of the item
     * @param pList the versioned list
     */
    public MetisFXTableManager(final TethysFXGuiFactory pFactory,
                               final Class<R> pClazz,
                               final MetisIndexedList<R> pList) {
        /* Initialise underlying class */
        super(pFactory, pClazz);

        /* Create the table list */
        theList = new MetisFXTableList<>(pList);
        theItemFields = theList.getListFields();
        getTable().setItems(theList.getUnderlyingList());
    }

    @Override
    protected TethysFXTableManager<MetisFieldId, R> getTable() {
        return (TethysFXTableManager<MetisFieldId, R>) super.getTable();
    }

    /**
     * Obtain the item fields.
     * @return the itemFields
     */
    MetisFXTableListFields<R> getItemFields() {
        return theItemFields;
    }

    @Override
    public void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theItemFields.setCalculator(pCalculator);
    }

    @Override
    public MetisFXTableStringColumn<R> declareStringColumn(final MetisFieldId pId) {
        return new MetisFXTableStringColumn<>(this, getTable().declareStringColumn(pId));
    }

    @Override
    public MetisFXTableCharArrayColumn<R> declareCharArrayColumn(final MetisFieldId pId) {
        return new MetisFXTableCharArrayColumn<>(this, getTable().declareCharArrayColumn(pId));
    }

    @Override
    public MetisFXTableShortColumn<R> declareShortColumn(final MetisFieldId pId) {
        return new MetisFXTableShortColumn<>(this, getTable().declareShortColumn(pId));
    }

    @Override
    public MetisFXTableIntegerColumn<R> declareIntegerColumn(final MetisFieldId pId) {
        return new MetisFXTableIntegerColumn<>(this, getTable().declareIntegerColumn(pId));
    }

    @Override
    public MetisFXTableLongColumn<R> declareLongColumn(final MetisFieldId pId) {
        return new MetisFXTableLongColumn<>(this, getTable().declareLongColumn(pId));
    }

    @Override
    public MetisFXTableRawDecimalColumn<R> declareRawDecimalColumn(final MetisFieldId pId) {
        return new MetisFXTableRawDecimalColumn<>(this, getTable().declareRawDecimalColumn(pId));
    }

    @Override
    public MetisFXTableMoneyColumn<R> declareMoneyColumn(final MetisFieldId pId) {
        return new MetisFXTableMoneyColumn<>(this, getTable().declareMoneyColumn(pId));
    }

    @Override
    public MetisFXTablePriceColumn<R> declarePriceColumn(final MetisFieldId pId) {
        return new MetisFXTablePriceColumn<>(this, getTable().declarePriceColumn(pId));
    }

    @Override
    public MetisFXTableRateColumn<R> declareRateColumn(final MetisFieldId pId) {
        return new MetisFXTableRateColumn<>(this, getTable().declareRateColumn(pId));
    }

    @Override
    public MetisFXTableUnitsColumn<R> declareUnitsColumn(final MetisFieldId pId) {
        return new MetisFXTableUnitsColumn<>(this, getTable().declareUnitsColumn(pId));
    }

    @Override
    public MetisFXTableDilutionColumn<R> declareDilutionColumn(final MetisFieldId pId) {
        return new MetisFXTableDilutionColumn<>(this, getTable().declareDilutionColumn(pId));
    }

    @Override
    public MetisFXTableRatioColumn<R> declareRatioColumn(final MetisFieldId pId) {
        return new MetisFXTableRatioColumn<>(this, getTable().declareRatioColumn(pId));
    }

    @Override
    public MetisFXTableDilutedPriceColumn<R> declareDilutedPriceColumn(final MetisFieldId pId) {
        return new MetisFXTableDilutedPriceColumn<>(this, getTable().declareDilutedPriceColumn(pId));
    }

    @Override
    public MetisFXTableDateColumn<R> declareDateColumn(final MetisFieldId pId) {
        return new MetisFXTableDateColumn<>(this, getTable().declareDateColumn(pId));
    }

    @Override
    public <T> MetisFXTableScrollColumn<T, R> declareScrollColumn(final MetisFieldId pId,
                                                                  final Class<T> pClazz) {
        return new MetisFXTableScrollColumn<>(this, getTable().declareScrollColumn(pId, pClazz));
    }

    @Override
    public <T extends Comparable<T>> MetisFXTableListColumn<T, R> declareListColumn(final MetisFieldId pId) {
        final TethysFXTableListColumn<T, MetisFieldId, R> myColumn = getTable().declareListColumn(pId);
        return new MetisFXTableListColumn<>(this, myColumn);
    }

    @Override
    public <T> MetisFXTableIconColumn<T, R> declareIconColumn(final MetisFieldId pId,
                                                              final Class<T> pClazz) {
        return new MetisFXTableIconColumn<>(this, getTable().declareIconColumn(pId, pClazz));
    }

    /**
     * Configure the column.
     * @param pColumn the column
     * @param pField the field
     */
    void configureColumn(final TethysFXTableColumn<?, MetisFieldId, R> pColumn,
                         final MetisFieldDef pField) {
        pColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pField));
        theItemFields.declareField(pField);
    }

    /**
     * String Column.
     * @param <R> the item type
     */
    public static class MetisFXTableStringColumn<R extends MetisFieldTableItem>
            extends MetisTableStringColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableStringColumn(final MetisFXTableManager<R> pTable,
                                           final TethysFXTableStringColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * CharArray Column.
     * @param <R> the item type
     */
    public static class MetisFXTableCharArrayColumn<R extends MetisFieldTableItem>
            extends MetisTableCharArrayColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableCharArrayColumn(final MetisFXTableManager<R> pTable,
                                              final TethysFXTableCharArrayColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Short Column.
     * @param <R> the item type
     */
    public static class MetisFXTableShortColumn<R extends MetisFieldTableItem>
            extends MetisTableShortColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableShortColumn(final MetisFXTableManager<R> pTable,
                                          final TethysFXTableShortColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Integer Column.
     * @param <R> the item type
     */
    public static class MetisFXTableIntegerColumn<R extends MetisFieldTableItem>
            extends MetisTableIntegerColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableIntegerColumn(final MetisFXTableManager<R> pTable,
                                            final TethysFXTableIntegerColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Long Column.
     * @param <R> the item type
     */
    public static class MetisFXTableLongColumn<R extends MetisFieldTableItem>
            extends MetisTableLongColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableLongColumn(final MetisFXTableManager<R> pTable,
                                         final TethysFXTableLongColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * RawDecimal Column.
     * @param <R> the item type
     */
    public static class MetisFXTableRawDecimalColumn<R extends MetisFieldTableItem>
            extends MetisTableRawDecimalColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableRawDecimalColumn(final MetisFXTableManager<R> pTable,
                                               final TethysFXTableRawDecimalColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Money Column.
     * @param <R> the item type
     */
    public static class MetisFXTableMoneyColumn<R extends MetisFieldTableItem>
            extends MetisTableMoneyColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableMoneyColumn(final MetisFXTableManager<R> pTable,
                                          final TethysFXTableMoneyColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Price Column.
     * @param <R> the item type
     */
    public static class MetisFXTablePriceColumn<R extends MetisFieldTableItem>
            extends MetisTablePriceColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTablePriceColumn(final MetisFXTableManager<R> pTable,
                                          final TethysFXTablePriceColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Rate Column.
     * @param <R> the item type
     */
    public static class MetisFXTableRateColumn<R extends MetisFieldTableItem>
            extends MetisTableRateColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableRateColumn(final MetisFXTableManager<R> pTable,
                                         final TethysFXTableRateColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Units Column.
     * @param <R> the item type
     */
    public static class MetisFXTableUnitsColumn<R extends MetisFieldTableItem>
            extends MetisTableUnitsColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableUnitsColumn(final MetisFXTableManager<R> pTable,
                                          final TethysFXTableUnitsColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Ratio Column.
     * @param <R> the item type
     */
    public static class MetisFXTableRatioColumn<R extends MetisFieldTableItem>
            extends MetisTableRatioColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableRatioColumn(final MetisFXTableManager<R> pTable,
                                          final TethysFXTableRatioColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Dilution Column.
     * @param <R> the item type
     */
    public static class MetisFXTableDilutionColumn<R extends MetisFieldTableItem>
            extends MetisTableDilutionColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableDilutionColumn(final MetisFXTableManager<R> pTable,
                                             final TethysFXTableDilutionColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * DilutedPrice Column.
     * @param <R> the item type
     */
    public static class MetisFXTableDilutedPriceColumn<R extends MetisFieldTableItem>
            extends MetisTableDilutedPriceColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableDilutedPriceColumn(final MetisFXTableManager<R> pTable,
                                                 final TethysFXTableDilutedPriceColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Date Column.
     * @param <R> the item type
     */
    public static class MetisFXTableDateColumn<R extends MetisFieldTableItem>
            extends MetisTableDateColumn<R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableDateColumn(final MetisFXTableManager<R> pTable,
                                         final TethysFXTableDateColumn<MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Icon Column.
     * @param <T> the value type
     * @param <R> the item type
     */
    public static class MetisFXTableIconColumn<T, R extends MetisFieldTableItem>
            extends MetisTableIconColumn<T, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableIconColumn(final MetisFXTableManager<R> pTable,
                                         final TethysFXTableIconColumn<T, MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * Scroll Column.
     * @param <T> the value type
     * @param <R> the item type
     */
    public static class MetisFXTableScrollColumn<T, R extends MetisFieldTableItem>
            extends MetisTableScrollColumn<T, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableScrollColumn(final MetisFXTableManager<R> pTable,
                                           final TethysFXTableScrollColumn<T, MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }

    /**
     * List Column.
     * @param <T> the value type
     * @param <R> the item type
     */
    public static class MetisFXTableListColumn<T extends Comparable<T>, R extends MetisFieldTableItem>
            extends MetisTableListColumn<T, R, Node, Node> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pColumn the column
         */
        protected MetisFXTableListColumn(final MetisFXTableManager<R> pTable,
                                         final TethysFXTableListColumn<T, MetisFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }
}
