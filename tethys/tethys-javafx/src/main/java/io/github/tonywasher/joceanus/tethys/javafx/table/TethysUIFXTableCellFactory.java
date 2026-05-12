/*
 * Tethys: GUI Utilities
 * Copyright 2026. Tony Washer
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
public interface TethysUIFXTableCellFactory<C, R> {
    /**
     * Obtain String Cell Factory.
     *
     * @param pColumn the column
     * @return the string cell factory
     */
    Callback<TableColumn<R, String>, TableCell<R, String>> stringCellFactory(TethysUITableStringColumn<C, R> pColumn);

    /**
     * Obtain CharArray Cell Factory.
     *
     * @param pColumn the column
     * @return the charArray cell factory
     */
    Callback<TableColumn<R, char[]>, TableCell<R, char[]>> charArrayCellFactory(TethysUITableCharArrayColumn<C, R> pColumn);

    /**
     * Obtain Short Cell Factory.
     *
     * @param pColumn the column
     * @return the short cell factory
     */
    Callback<TableColumn<R, Short>, TableCell<R, Short>> shortCellFactory(TethysUITableShortColumn<C, R> pColumn);

    /**
     * Obtain Integer Cell Factory.
     *
     * @param pColumn the column
     * @return the integer cell factory
     */
    Callback<TableColumn<R, Integer>, TableCell<R, Integer>> integerCellFactory(TethysUITableIntegerColumn<C, R> pColumn);

    /**
     * Obtain Long Cell Factory.
     *
     * @param pColumn the column
     * @return the long cell factory
     */
    Callback<TableColumn<R, Long>, TableCell<R, Long>> longCellFactory(TethysUITableLongColumn<C, R> pColumn);

    /**
     * Obtain RawDecimal Cell Factory.
     *
     * @param pColumn the column
     * @return the rawDecimal cell factory
     */
    Callback<TableColumn<R, OceanusDecimal>, TableCell<R, OceanusDecimal>> rawDecimalCellFactory(TethysUITableRawDecimalColumn<C, R> pColumn);

    /**
     * Obtain Money Cell Factory.
     *
     * @param pColumn the column
     * @return the money cell factory
     */
    Callback<TableColumn<R, OceanusMoney>, TableCell<R, OceanusMoney>> moneyCellFactory(TethysUITableMoneyColumn<C, R> pColumn);

    /**
     * Obtain Price Cell Factory.
     *
     * @param pColumn the column
     * @return the price cell factory
     */
    Callback<TableColumn<R, OceanusPrice>, TableCell<R, OceanusPrice>> priceCellFactory(TethysUITablePriceColumn<C, R> pColumn);

    /**
     * Obtain Rate Cell Factory.
     *
     * @param pColumn the column
     * @return the rate cell factory
     */
    Callback<TableColumn<R, OceanusRate>, TableCell<R, OceanusRate>> rateCellFactory(TethysUITableRateColumn<C, R> pColumn);

    /**
     * Obtain Units Cell Factory.
     *
     * @param pColumn the column
     * @return the units cell factory
     */
    Callback<TableColumn<R, OceanusUnits>, TableCell<R, OceanusUnits>> unitsCellFactory(TethysUITableUnitsColumn<C, R> pColumn);

    /**
     * Obtain Ratio Cell Factory.
     *
     * @param pColumn the column
     * @return the ratio cell factory
     */
    Callback<TableColumn<R, OceanusRatio>, TableCell<R, OceanusRatio>> ratioCellFactory(TethysUITableRatioColumn<C, R> pColumn);

    /**
     * Obtain Scroll Cell Factory.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the scroll cell factory
     */
    <T> Callback<TableColumn<R, T>, TableCell<R, T>> scrollCellFactory(TethysUITableScrollColumn<T, C, R> pColumn,
                                                                       Class<T> pClazz);

    /**
     * Obtain List Cell Factory.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @return the scroll cell factory
     */
    <T extends Comparable<? super T>> Callback<TableColumn<R, List<T>>, TableCell<R, List<T>>> listCellFactory(TethysUITableListColumn<T, C, R> pColumn);

    /**
     * Obtain Date Cell Factory.
     *
     * @param pColumn the column
     * @return the date cell factory
     */
    Callback<TableColumn<R, OceanusDate>, TableCell<R, OceanusDate>> dateCellFactory(TethysUITableDateColumn<C, R> pColumn);

    /**
     * Obtain Icon Cell Factory.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the icon cell factory
     */
    <T> Callback<TableColumn<R, T>, TableCell<R, T>> iconCellFactory(TethysUITableIconColumn<T, C, R> pColumn,
                                                                     Class<T> pClazz);
}

