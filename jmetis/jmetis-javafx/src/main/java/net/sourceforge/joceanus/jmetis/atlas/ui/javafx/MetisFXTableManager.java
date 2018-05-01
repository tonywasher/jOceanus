/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import javafx.scene.Node;
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
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.list.MetisListEditSession;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmetis.list.MetisListKey;
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
     * Table EditSession.
     */
    private final MetisListEditSession theSession;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pClazz the class of the item
     * @param pList the versioned list
     */
    public MetisFXTableManager(final TethysFXGuiFactory pFactory,
                               final Class<R> pClazz,
                               final MetisListIndexed<R> pList) {
        /* Initialise underlying class */
        super(pFactory, MetisFieldSet.lookUpFieldSet(pClazz));

        /* Create the table list */
        theList = new MetisFXTableList<>(this, pList);
        theItemFields = theList.getListFields();
        getTable().setItems(theList.getUnderlyingList());

        /* Set non editable */
        theSession = null;
        setEditable(false);
    }

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pItemType the itemType of the item
     * @param pSession the editSession
     */
    public MetisFXTableManager(final TethysFXGuiFactory pFactory,
                               final MetisListKey pItemType,
                               final MetisListEditSession pSession) {
        /* Initialise underlying class */
        super(pFactory, MetisFieldSet.lookUpFieldSet(pItemType.getClazz()));

        /* Can only be editable if we are an instance of FieldVersionedItem */
        theSession = MetisFieldVersionedItem.class.isAssignableFrom(pItemType.getClazz())
                                                                                          ? pSession
                                                                                          : null;
        setEditable(theSession != null);

        /* Create the table list */
        theList = new MetisFXTableList<>(this, pSession.getList(pItemType));
        theItemFields = theList.getListFields();
        getTable().setItems(theList.getUnderlyingList());
    }

    @Override
    protected TethysFXTableManager<MetisDataFieldId, R> getTable() {
        return (TethysFXTableManager<MetisDataFieldId, R>) super.getTable();
    }

    /**
     * Obtain the item fields.
     * @return the itemFields
     */
    MetisFXTableListFields<R> getItemFields() {
        return theItemFields;
    }

    /**
     * Force a sort operation.
     */
    void forceSort() {
        getTable().forceSort();
    }

    @Override
    public void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theItemFields.setCalculator(pCalculator);
    }

    @Override
    public MetisFXTableStringColumn<R> declareStringColumn(final MetisDataFieldId pId) {
        return new MetisFXTableStringColumn<>(this, getTable().declareStringColumn(pId));
    }

    @Override
    public MetisFXTableCharArrayColumn<R> declareCharArrayColumn(final MetisDataFieldId pId) {
        return new MetisFXTableCharArrayColumn<>(this, getTable().declareCharArrayColumn(pId));
    }

    @Override
    public MetisFXTableShortColumn<R> declareShortColumn(final MetisDataFieldId pId) {
        return new MetisFXTableShortColumn<>(this, getTable().declareShortColumn(pId));
    }

    @Override
    public MetisFXTableIntegerColumn<R> declareIntegerColumn(final MetisDataFieldId pId) {
        return new MetisFXTableIntegerColumn<>(this, getTable().declareIntegerColumn(pId));
    }

    @Override
    public MetisFXTableLongColumn<R> declareLongColumn(final MetisDataFieldId pId) {
        return new MetisFXTableLongColumn<>(this, getTable().declareLongColumn(pId));
    }

    @Override
    public MetisFXTableRawDecimalColumn<R> declareRawDecimalColumn(final MetisDataFieldId pId) {
        return new MetisFXTableRawDecimalColumn<>(this, getTable().declareRawDecimalColumn(pId));
    }

    @Override
    public MetisFXTableMoneyColumn<R> declareMoneyColumn(final MetisDataFieldId pId) {
        return new MetisFXTableMoneyColumn<>(this, getTable().declareMoneyColumn(pId));
    }

    @Override
    public MetisFXTablePriceColumn<R> declarePriceColumn(final MetisDataFieldId pId) {
        return new MetisFXTablePriceColumn<>(this, getTable().declarePriceColumn(pId));
    }

    @Override
    public MetisFXTableRateColumn<R> declareRateColumn(final MetisDataFieldId pId) {
        return new MetisFXTableRateColumn<>(this, getTable().declareRateColumn(pId));
    }

    @Override
    public MetisFXTableUnitsColumn<R> declareUnitsColumn(final MetisDataFieldId pId) {
        return new MetisFXTableUnitsColumn<>(this, getTable().declareUnitsColumn(pId));
    }

    @Override
    public MetisFXTableDilutionColumn<R> declareDilutionColumn(final MetisDataFieldId pId) {
        return new MetisFXTableDilutionColumn<>(this, getTable().declareDilutionColumn(pId));
    }

    @Override
    public MetisFXTableRatioColumn<R> declareRatioColumn(final MetisDataFieldId pId) {
        return new MetisFXTableRatioColumn<>(this, getTable().declareRatioColumn(pId));
    }

    @Override
    public MetisFXTableDilutedPriceColumn<R> declareDilutedPriceColumn(final MetisDataFieldId pId) {
        return new MetisFXTableDilutedPriceColumn<>(this, getTable().declareDilutedPriceColumn(pId));
    }

    @Override
    public MetisFXTableDateColumn<R> declareDateColumn(final MetisDataFieldId pId) {
        return new MetisFXTableDateColumn<>(this, getTable().declareDateColumn(pId));
    }

    @Override
    public <T> MetisFXTableScrollColumn<T, R> declareScrollColumn(final MetisDataFieldId pId,
                                                                  final Class<T> pClazz) {
        return new MetisFXTableScrollColumn<>(this, getTable().declareScrollColumn(pId, pClazz));
    }

    @Override
    public <T extends Comparable<T>> MetisFXTableListColumn<T, R> declareListColumn(final MetisDataFieldId pId) {
        final TethysFXTableListColumn<T, MetisDataFieldId, R> myColumn = getTable().declareListColumn(pId);
        return new MetisFXTableListColumn<>(this, myColumn);
    }

    @Override
    public <T> MetisFXTableIconColumn<T, R> declareIconColumn(final MetisDataFieldId pId,
                                                              final Class<T> pClazz) {
        return new MetisFXTableIconColumn<>(this, getTable().declareIconColumn(pId, pClazz));
    }

    /**
     * Configure the column.
     * @param pColumn the column
     * @param pField the field
     */
    void configureColumn(final TethysFXTableColumn<?, MetisDataFieldId, R> pColumn,
                         final MetisFieldDef pField) {
        pColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pField));
        if (theSession != null && pField instanceof MetisFieldVersionedDef) {
            pColumn.setOnCommit((r, v) -> theSession.setFieldForItem(r, (MetisFieldVersionedDef) pField, v));
        }
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
                                           final TethysFXTableStringColumn<MetisDataFieldId, R> pColumn) {
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
                                              final TethysFXTableCharArrayColumn<MetisDataFieldId, R> pColumn) {
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
                                          final TethysFXTableShortColumn<MetisDataFieldId, R> pColumn) {
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
                                            final TethysFXTableIntegerColumn<MetisDataFieldId, R> pColumn) {
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
                                         final TethysFXTableLongColumn<MetisDataFieldId, R> pColumn) {
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
                                               final TethysFXTableRawDecimalColumn<MetisDataFieldId, R> pColumn) {
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
                                          final TethysFXTableMoneyColumn<MetisDataFieldId, R> pColumn) {
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
                                          final TethysFXTablePriceColumn<MetisDataFieldId, R> pColumn) {
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
                                         final TethysFXTableRateColumn<MetisDataFieldId, R> pColumn) {
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
                                          final TethysFXTableUnitsColumn<MetisDataFieldId, R> pColumn) {
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
                                          final TethysFXTableRatioColumn<MetisDataFieldId, R> pColumn) {
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
                                             final TethysFXTableDilutionColumn<MetisDataFieldId, R> pColumn) {
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
                                                 final TethysFXTableDilutedPriceColumn<MetisDataFieldId, R> pColumn) {
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
                                         final TethysFXTableDateColumn<MetisDataFieldId, R> pColumn) {
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
                                         final TethysFXTableIconColumn<T, MetisDataFieldId, R> pColumn) {
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
                                           final TethysFXTableScrollColumn<T, MetisDataFieldId, R> pColumn) {
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
                                         final TethysFXTableListColumn<T, MetisDataFieldId, R> pColumn) {
            super(pTable, pColumn);
            pTable.configureColumn(pColumn, getField());
        }
    }
}
