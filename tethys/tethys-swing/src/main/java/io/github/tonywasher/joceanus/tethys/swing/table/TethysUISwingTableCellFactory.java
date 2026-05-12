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

package io.github.tonywasher.joceanus.tethys.swing.table;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableCell;
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

import java.util.List;

/**
 * CellFactory interface.
 *
 * @param <C> the column identity
 * @param <R> the row type
 */

public interface TethysUISwingTableCellFactory<C, R> {
    /**
     * Obtain String Cell.
     *
     * @param pColumn the column
     * @return the string cell
     */
    TethysUITableCell<String, C, R> stringCell(TethysUITableStringColumn<C, R> pColumn);

    /**
     * Obtain charArray Cell.
     *
     * @param pColumn the column
     * @return the charArray cell
     */
    TethysUITableCell<char[], C, R> charArrayCell(TethysUITableCharArrayColumn<C, R> pColumn);

    /**
     * Obtain Short Cell.
     *
     * @param pColumn the column
     * @return the short cell
     */
    TethysUITableCell<Short, C, R> shortCell(TethysUITableShortColumn<C, R> pColumn);

    /**
     * Obtain Integer Cell.
     *
     * @param pColumn the column
     * @return the integer cell
     */
    TethysUITableCell<Integer, C, R> integerCell(TethysUITableIntegerColumn<C, R> pColumn);

    /**
     * Obtain Long Cell.
     *
     * @param pColumn the column
     * @return the long cell
     */
    TethysUITableCell<Long, C, R> longCell(TethysUITableLongColumn<C, R> pColumn);

    /**
     * Obtain Money Cell.
     *
     * @param pColumn the column
     * @return the money cell
     */
    TethysUITableCell<OceanusDecimal, C, R> rawDecimalCell(TethysUITableRawDecimalColumn<C, R> pColumn);

    /**
     * Obtain Money Cell.
     *
     * @param pColumn the column
     * @return the money cell
     */
    TethysUITableCell<OceanusMoney, C, R> moneyCell(TethysUITableMoneyColumn<C, R> pColumn);

    /**
     * Obtain Price Cell.
     *
     * @param pColumn the column
     * @return the price cell
     */
    TethysUITableCell<OceanusPrice, C, R> priceCell(TethysUITablePriceColumn<C, R> pColumn);

    /**
     * Obtain Rate Cell.
     *
     * @param pColumn the column
     * @return the rate cell
     */
    TethysUITableCell<OceanusRate, C, R> rateCell(TethysUITableRateColumn<C, R> pColumn);

    /**
     * Obtain Units Cell.
     *
     * @param pColumn the column
     * @return the units cell
     */
    TethysUITableCell<OceanusUnits, C, R> unitsCell(TethysUITableUnitsColumn<C, R> pColumn);

    /**
     * Obtain Ratio Cell.
     *
     * @param pColumn the column
     * @return the ratio cell
     */
    TethysUITableCell<OceanusRatio, C, R> ratioCell(TethysUITableRatioColumn<C, R> pColumn);

    /**
     * Obtain Scroll Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the scroll cell
     */
    <T> TethysUITableCell<T, C, R> scrollCell(TethysUITableScrollColumn<T, C, R> pColumn,
                                              Class<T> pClazz);

    /**
     * Obtain List Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @return the list cell
     */
    <T extends Comparable<? super T>> TethysUITableCell<List<T>, C, R> listCell(TethysUITableListColumn<T, C, R> pColumn);

    /**
     * Obtain Date Cell.
     *
     * @param pColumn the column
     * @return the date cell
     */
    TethysUITableCell<OceanusDate, C, R> dateCell(final TethysUITableDateColumn<C, R> pColumn);

    /**
     * Obtain Icon Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the icon cell
     */
    <T> TethysUITableCell<T, C, R> iconCell(final TethysUITableIconColumn<T, C, R> pColumn,
                                            final Class<T> pClazz);
}
