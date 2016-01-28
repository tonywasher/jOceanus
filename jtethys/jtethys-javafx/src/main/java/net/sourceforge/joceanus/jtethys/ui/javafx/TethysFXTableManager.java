/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.Comparator;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;

/**
 * JavaFX Table manager.
 * @param <I> the column identity
 * @param <R> the row type
 */
public class TethysFXTableManager<I, R>
        extends TethysTableManager<I, R, Node> {
    /**
     * The TableView.
     */
    private final TableView<R> theTable;

    /**
     * The Columns.
     */
    private final ObservableList<TableColumn<R, ?>> theColumns;

    /**
     * The CellFactory.
     */
    private final TethysFXTableCellFactory<I, R> theCellFactory;

    /**
     * The Comparator.
     */
    private Comparator<R> theComparator;

    /**
     * The Filter.
     */
    private Predicate<R> theFilter;

    /**
     * The Items.
     */
    private ObservableList<R> theItems;

    /**
     * Constructor.
     */
    public TethysFXTableManager() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysFXTableManager(final TethysDataFormatter pFormatter) {
        /* Create fields */
        theTable = new TableView<>();
        theColumns = theTable.getColumns();
        theCellFactory = new TethysFXTableCellFactory<>(pFormatter);

        /* Listen to factory */
        theCellFactory.getEventRegistrar().addEventListener(this::cascadeEvent);

        /* Configure the table */
        theTable.setEditable(true);
    }

    @Override
    public Node getNode() {
        return theTable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TethysFXTableColumn<I, R, ?> getColumn(final I pId) {
        return (TethysFXTableColumn<I, R, ?>) super.getColumn(pId);
    }

    /**
     * Set the table items.
     * @param pItems the items
     */
    public void setItems(final ObservableList<R> pItems) {
        theItems = pItems;
        setTheItems();
    }

    @Override
    public void setFilter(final Predicate<R> pFilter) {
        theFilter = pFilter;
        setTheItems();
    }

    @Override
    public void setComparator(final Comparator<R> pComparator) {
        theComparator = pComparator;
        setTheItems();
    }

    /**
     * Set the table items.
     */
    private void setTheItems() {
        /* Access the underlying copy */
        ObservableList<R> myItems = theItems;

        /* If we have any items */
        if (myItems != null) {
            /* Apply filter if specified */
            if (theFilter != null) {
                myItems = myItems.filtered(theFilter);
            }

            /* Apply sort if specified */
            if (theComparator != null) {
                myItems = myItems.sorted(theComparator);
            }
        }

        /* Declare the items */
        theTable.setItems(myItems);
    }

    @Override
    public TethysFXTableStringColumn<I, R> declareStringColumn(final I pId) {
        return new TethysFXTableStringColumn<>(this, pId);
    }

    @Override
    public TethysFXTableShortColumn<I, R> declareShortColumn(final I pId) {
        return new TethysFXTableShortColumn<>(this, pId);
    }

    @Override
    public TethysFXTableIntegerColumn<I, R> declareIntegerColumn(final I pId) {
        return new TethysFXTableIntegerColumn<>(this, pId);
    }

    @Override
    public TethysFXTableLongColumn<I, R> declareLongColumn(final I pId) {
        return new TethysFXTableLongColumn<>(this, pId);
    }

    @Override
    public TethysFXTableMoneyColumn<I, R> declareMoneyColumn(final I pId) {
        return new TethysFXTableMoneyColumn<>(this, pId);
    }

    @Override
    public TethysFXTablePriceColumn<I, R> declarePriceColumn(final I pId) {
        return new TethysFXTablePriceColumn<>(this, pId);
    }

    @Override
    public TethysFXTableRateColumn<I, R> declareRateColumn(final I pId) {
        return new TethysFXTableRateColumn<>(this, pId);
    }

    @Override
    public TethysFXTableUnitsColumn<I, R> declareUnitsColumn(final I pId) {
        return new TethysFXTableUnitsColumn<>(this, pId);
    }

    @Override
    public TethysFXTableDilutionColumn<I, R> declareDilutionColumn(final I pId) {
        return new TethysFXTableDilutionColumn<>(this, pId);
    }

    @Override
    public TethysFXTableRatioColumn<I, R> declareRatioColumn(final I pId) {
        return new TethysFXTableRatioColumn<>(this, pId);
    }

    @Override
    public TethysFXTableDilutedPriceColumn<I, R> declareDilutedPriceColumn(final I pId) {
        return new TethysFXTableDilutedPriceColumn<>(this, pId);
    }

    @Override
    public TethysFXTableDateColumn<I, R> declareDateColumn(final I pId) {
        return new TethysFXTableDateColumn<>(this, pId);
    }

    @Override
    public <C> TethysFXTableScrollColumn<I, R, C> declareScrollColumn(final I pId,
                                                                      final Class<C> pClass) {
        return new TethysFXTableScrollColumn<>(this, pId, pClass);
    }

    @Override
    public <C> TethysFXTableListColumn<I, R, C> declareListColumn(final I pId,
                                                                  final Class<C> pClass) {
        return new TethysFXTableListColumn<>(this, pId, pClass);
    }

    @Override
    public <C> TethysFXTableIconColumn<I, R, C> declareIconColumn(final I pId,
                                                                  final Class<C> pClass) {
        return new TethysFXTableIconColumn<>(this, pId, pClass);
    }

    @Override
    public <C> TethysFXTableStateIconColumn<I, R, C> declareStateIconColumn(final I pId,
                                                                            final Class<C> pClass) {
        return new TethysFXTableStateIconColumn<>(this, pId, pClass);
    }

    /**
     * Column Definition.
     * @param <I> the column identity
     * @param <R> the row type
     * @param <C> the column type
     */
    public static class TethysFXTableColumn<I, R, C>
            extends TethysTableColumn<I, R, Node> {
        /**
         * The underlying column.
         */
        private final TableColumn<R, C> theColumn;

        /**
         * Constructor.
         * @param pTable the owning table
         * @param pId the column id
         * @param pType the type of the column
         */
        protected TethysFXTableColumn(final TethysFXTableManager<I, R> pTable,
                                      final I pId,
                                      final TethysFieldType pType) {
            /* Initialise underlying class */
            super(pTable, pId, pType);

            /* Create the column and add to the table */
            theColumn = new TableColumn<>(getName());
            pTable.theColumns.add(theColumn);

            /* Configure the column */
            theColumn.setSortable(false);
            theColumn.setEditable(true);
            setColumnWidth(getCellType().getDefaultWidth());
        }

        /**
         * Set cell value Factory.
         * @param pFactory the cell factory
         */
        public void setCellValueFactory(final Callback<CellDataFeatures<R, C>, ObservableValue<C>> pFactory) {
            theColumn.setCellValueFactory(pFactory);
        }

        /**
         * Declare cell Factory.
         * @param pFactory the cell factory
         */
        protected void declareCellFactory(final Callback<TableColumn<R, C>, TableCell<R, C>> pFactory) {
            theColumn.setCellFactory(pFactory);
        }

        @Override
        public TethysFXTableManager<I, R> getTable() {
            return (TethysFXTableManager<I, R>) super.getTable();
        }

        @Override
        protected void attachToTable() {
            theColumn.setVisible(true);
        }

        @Override
        protected void detachFromTable() {
            theColumn.setVisible(false);
        }

        /**
         * Set the column width.
         * @param pWidth the width
         */
        public void setColumnWidth(final int pWidth) {
            theColumn.setPrefWidth(pWidth);
        }
    }

    /**
     * String Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableStringColumn<I, R>
            extends TethysFXTableColumn<I, R, String> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableStringColumn(final TethysFXTableManager<I, R> pTable,
                                            final I pId) {
            super(pTable, pId, TethysFieldType.STRING);
            declareCellFactory(getTable().theCellFactory.stringCellFactory(this));
        }
    }

    /**
     * Short Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableShortColumn<I, R>
            extends TethysFXTableColumn<I, R, Short> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableShortColumn(final TethysFXTableManager<I, R> pTable,
                                           final I pId) {
            super(pTable, pId, TethysFieldType.SHORT);
            declareCellFactory(getTable().theCellFactory.shortCellFactory(this));
        }
    }

    /**
     * Integer Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIntegerColumn<I, R>
            extends TethysFXTableColumn<I, R, Integer> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableIntegerColumn(final TethysFXTableManager<I, R> pTable,
                                             final I pId) {
            super(pTable, pId, TethysFieldType.INTEGER);
            declareCellFactory(getTable().theCellFactory.integerCellFactory(this));
        }
    }

    /**
     * Long Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableLongColumn<I, R>
            extends TethysFXTableColumn<I, R, Long> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableLongColumn(final TethysFXTableManager<I, R> pTable,
                                          final I pId) {
            super(pTable, pId, TethysFieldType.LONG);
            declareCellFactory(getTable().theCellFactory.longCellFactory(this));
        }
    }

    /**
     * Money Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableMoneyColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysMoney> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableMoneyColumn(final TethysFXTableManager<I, R> pTable,
                                           final I pId) {
            super(pTable, pId, TethysFieldType.MONEY);
            declareCellFactory(getTable().theCellFactory.moneyCellFactory(this));
        }
    }

    /**
     * Price Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTablePriceColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysPrice> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTablePriceColumn(final TethysFXTableManager<I, R> pTable,
                                           final I pId) {
            super(pTable, pId, TethysFieldType.PRICE);
            declareCellFactory(getTable().theCellFactory.priceCellFactory(this));
        }
    }

    /**
     * Rate Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRateColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysRate> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRateColumn(final TethysFXTableManager<I, R> pTable,
                                          final I pId) {
            super(pTable, pId, TethysFieldType.RATE);
            declareCellFactory(getTable().theCellFactory.rateCellFactory(this));
        }
    }

    /**
     * Units Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableUnitsColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysUnits> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableUnitsColumn(final TethysFXTableManager<I, R> pTable,
                                           final I pId) {
            super(pTable, pId, TethysFieldType.UNITS);
            declareCellFactory(getTable().theCellFactory.unitsCellFactory(this));
        }
    }

    /**
     * Dilution Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutionColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysDilution> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDilutionColumn(final TethysFXTableManager<I, R> pTable,
                                              final I pId) {
            super(pTable, pId, TethysFieldType.DILUTION);
            declareCellFactory(getTable().theCellFactory.dilutionCellFactory(this));
        }
    }

    /**
     * Ratio Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRatioColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysRatio> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableRatioColumn(final TethysFXTableManager<I, R> pTable,
                                           final I pId) {
            super(pTable, pId, TethysFieldType.RATIO);
            declareCellFactory(getTable().theCellFactory.ratioCellFactory(this));
        }
    }

    /**
     * DilutedPrice Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutedPriceColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDilutedPriceColumn(final TethysFXTableManager<I, R> pTable,
                                                  final I pId) {
            super(pTable, pId, TethysFieldType.DILUTEDPRICE);
            declareCellFactory(getTable().theCellFactory.dilutedPriceCellFactory(this));
        }
    }

    /**
     * Date Column.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDateColumn<I, R>
            extends TethysFXTableColumn<I, R, TethysDate> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         */
        protected TethysFXTableDateColumn(final TethysFXTableManager<I, R> pTable,
                                          final I pId) {
            super(pTable, pId, TethysFieldType.DATE);
            declareCellFactory(getTable().theCellFactory.dateCellFactory(this));
        }
    }

    /**
     * Scroll Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysFXTableScrollColumn<I, R, C>
            extends TethysFXTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysFXTableScrollColumn(final TethysFXTableManager<I, R> pTable,
                                            final I pId,
                                            final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.SCROLL);
            declareCellFactory(getTable().theCellFactory.scrollCellFactory(this, pClass));
        }
    }

    /**
     * List Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysFXTableListColumn<I, R, C>
            extends TethysFXTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysFXTableListColumn(final TethysFXTableManager<I, R> pTable,
                                          final I pId,
                                          final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.LIST);
            declareCellFactory(getTable().theCellFactory.listCellFactory(this, pClass));
        }
    }

    /**
     * Icon Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysFXTableIconColumn<I, R, C>
            extends TethysFXTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysFXTableIconColumn(final TethysFXTableManager<I, R> pTable,
                                          final I pId,
                                          final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.ICON);
            declareCellFactory(getTable().theCellFactory.iconCellFactory(this, pClass));
        }
    }

    /**
     * StateIcon Column.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column type
     */
    public static class TethysFXTableStateIconColumn<I, R, C>
            extends TethysFXTableColumn<I, R, C> {
        /**
         * Constructor.
         * @param pTable the table
         * @param pId the id
         * @param pClass the item class
         */
        protected TethysFXTableStateIconColumn(final TethysFXTableManager<I, R> pTable,
                                               final I pId,
                                               final Class<C> pClass) {
            super(pTable, pId, TethysFieldType.STATEICON);
            declareCellFactory(getTable().theCellFactory.stateIconCellFactory(this, pClass));
        }
    }
}
