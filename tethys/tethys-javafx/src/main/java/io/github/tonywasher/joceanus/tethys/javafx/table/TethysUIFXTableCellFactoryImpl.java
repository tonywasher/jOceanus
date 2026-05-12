/*
 * Tethys: GUI Utilities
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
package io.github.tonywasher.joceanus.tethys.javafx.table;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableCharArrayColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableDateColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableIconColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableIntegerColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableListColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableLongColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableMoneyColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITablePriceColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRateColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRatioColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRawDecimalColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableScrollColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableShortColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableStringColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableUnitsColumn;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableCharArrayCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableDateCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableIconCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableIntegerCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableListCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableLongCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableMoneyCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTablePriceCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableRateCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableRatioCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableRawDecimalCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableScrollCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableShortCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableStringCell;
import io.github.tonywasher.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableUnitsCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.List;

/**
 * TableCell implementations built on DataEditFields.
 *
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysUIFXTableCellFactoryImpl<C, R>
        implements TethysUIFXTableCellFactory<C, R> {
    /**
     * The GUI Factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUIFXTableCellFactoryImpl(final TethysUICoreFactory<?> pFactory) {
        theGuiFactory = pFactory;
    }

    /**
     * Obtain String Cell Factory.
     *
     * @param pColumn the column
     * @return the string cell factory
     */
    public Callback<TableColumn<R, String>, TableCell<R, String>> stringCellFactory(final TethysUITableStringColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableStringCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain CharArray Cell Factory.
     *
     * @param pColumn the column
     * @return the charArray cell factory
     */
    public Callback<TableColumn<R, char[]>, TableCell<R, char[]>> charArrayCellFactory(final TethysUITableCharArrayColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableCharArrayCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Short Cell Factory.
     *
     * @param pColumn the column
     * @return the short cell factory
     */
    public Callback<TableColumn<R, Short>, TableCell<R, Short>> shortCellFactory(final TethysUITableShortColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableShortCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Integer Cell Factory.
     *
     * @param pColumn the column
     * @return the integer cell factory
     */
    public Callback<TableColumn<R, Integer>, TableCell<R, Integer>> integerCellFactory(final TethysUITableIntegerColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableIntegerCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Long Cell Factory.
     *
     * @param pColumn the column
     * @return the long cell factory
     */
    public Callback<TableColumn<R, Long>, TableCell<R, Long>> longCellFactory(final TethysUITableLongColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableLongCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain RawDecimal Cell Factory.
     *
     * @param pColumn the column
     * @return the rawDecimal cell factory
     */
    public Callback<TableColumn<R, OceanusDecimal>, TableCell<R, OceanusDecimal>> rawDecimalCellFactory(final TethysUITableRawDecimalColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableRawDecimalCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell Factory.
     *
     * @param pColumn the column
     * @return the money cell factory
     */
    public Callback<TableColumn<R, OceanusMoney>, TableCell<R, OceanusMoney>> moneyCellFactory(final TethysUITableMoneyColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableMoneyCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Price Cell Factory.
     *
     * @param pColumn the column
     * @return the price cell factory
     */
    public Callback<TableColumn<R, OceanusPrice>, TableCell<R, OceanusPrice>> priceCellFactory(final TethysUITablePriceColumn<C, R> pColumn) {
        return e -> new TethysUIFXTablePriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Rate Cell Factory.
     *
     * @param pColumn the column
     * @return the rate cell factory
     */
    public Callback<TableColumn<R, OceanusRate>, TableCell<R, OceanusRate>> rateCellFactory(final TethysUITableRateColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableRateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Units Cell Factory.
     *
     * @param pColumn the column
     * @return the units cell factory
     */
    public Callback<TableColumn<R, OceanusUnits>, TableCell<R, OceanusUnits>> unitsCellFactory(final TethysUITableUnitsColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableUnitsCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Ratio Cell Factory.
     *
     * @param pColumn the column
     * @return the ratio cell factory
     */
    public Callback<TableColumn<R, OceanusRatio>, TableCell<R, OceanusRatio>> ratioCellFactory(final TethysUITableRatioColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableRatioCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Scroll Cell Factory.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the scroll cell factory
     */
    public <T> Callback<TableColumn<R, T>, TableCell<R, T>> scrollCellFactory(final TethysUITableScrollColumn<T, C, R> pColumn,
                                                                              final Class<T> pClazz) {
        return e -> new TethysUIFXTableScrollCell<>(pColumn, theGuiFactory, pClazz);
    }

    /**
     * Obtain List Cell Factory.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @return the scroll cell factory
     */
    public <T extends Comparable<? super T>> Callback<TableColumn<R, List<T>>, TableCell<R, List<T>>> listCellFactory(final TethysUITableListColumn<T, C, R> pColumn) {
        return e -> new TethysUIFXTableListCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Date Cell Factory.
     *
     * @param pColumn the column
     * @return the date cell factory
     */
    public Callback<TableColumn<R, OceanusDate>, TableCell<R, OceanusDate>> dateCellFactory(final TethysUITableDateColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableDateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Icon Cell Factory.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the icon cell factory
     */
    public <T> Callback<TableColumn<R, T>, TableCell<R, T>> iconCellFactory(final TethysUITableIconColumn<T, C, R> pColumn,
                                                                            final Class<T> pClazz) {
        return e -> new TethysUIFXTableIconCell<>(pColumn, theGuiFactory, pClazz);
    }
}
