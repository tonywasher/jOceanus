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
package io.github.tonywasher.joceanus.tethys.swing.table;

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
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableCharArrayCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableDateCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableIconCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableIntegerCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableListCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableLongCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableMoneyCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTablePriceCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableRateCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableRatioCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableRawDecimalCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableScrollCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableShortCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableStringCell;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableCellImpl.TethysUISwingTableUnitsCell;

import java.util.List;

/**
 * Swing Table Cell Factory.
 *
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysUISwingTableCellFactoryImpl<C, R>
        implements TethysUISwingTableCellFactory<C, R> {
    /**
     * The GUI Factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUISwingTableCellFactoryImpl(final TethysUICoreFactory<?> pFactory) {
        theGuiFactory = pFactory;
    }

    /**
     * Obtain String Cell.
     *
     * @param pColumn the column
     * @return the string cell
     */
    public TethysUISwingTableCellImpl<String, C, R> stringCell(final TethysUITableStringColumn<C, R> pColumn) {
        return new TethysUISwingTableStringCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain charArray Cell.
     *
     * @param pColumn the column
     * @return the charArray cell
     */
    public TethysUISwingTableCellImpl<char[], C, R> charArrayCell(final TethysUITableCharArrayColumn<C, R> pColumn) {
        return new TethysUISwingTableCharArrayCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Short Cell.
     *
     * @param pColumn the column
     * @return the short cell
     */
    public TethysUISwingTableCellImpl<Short, C, R> shortCell(final TethysUITableShortColumn<C, R> pColumn) {
        return new TethysUISwingTableShortCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Integer Cell.
     *
     * @param pColumn the column
     * @return the integer cell
     */
    public TethysUISwingTableCellImpl<Integer, C, R> integerCell(final TethysUITableIntegerColumn<C, R> pColumn) {
        return new TethysUISwingTableIntegerCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Long Cell.
     *
     * @param pColumn the column
     * @return the long cell
     */
    public TethysUISwingTableCellImpl<Long, C, R> longCell(final TethysUITableLongColumn<C, R> pColumn) {
        return new TethysUISwingTableLongCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell.
     *
     * @param pColumn the column
     * @return the money cell
     */
    public TethysUISwingTableCellImpl<OceanusDecimal, C, R> rawDecimalCell(final TethysUITableRawDecimalColumn<C, R> pColumn) {
        return new TethysUISwingTableRawDecimalCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell.
     *
     * @param pColumn the column
     * @return the money cell
     */
    public TethysUISwingTableCellImpl<OceanusMoney, C, R> moneyCell(final TethysUITableMoneyColumn<C, R> pColumn) {
        return new TethysUISwingTableMoneyCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Price Cell.
     *
     * @param pColumn the column
     * @return the price cell
     */
    public TethysUISwingTableCellImpl<OceanusPrice, C, R> priceCell(final TethysUITablePriceColumn<C, R> pColumn) {
        return new TethysUISwingTablePriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Rate Cell.
     *
     * @param pColumn the column
     * @return the rate cell
     */
    public TethysUISwingTableCellImpl<OceanusRate, C, R> rateCell(final TethysUITableRateColumn<C, R> pColumn) {
        return new TethysUISwingTableRateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Units Cell.
     *
     * @param pColumn the column
     * @return the units cell
     */
    public TethysUISwingTableCellImpl<OceanusUnits, C, R> unitsCell(final TethysUITableUnitsColumn<C, R> pColumn) {
        return new TethysUISwingTableUnitsCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Ratio Cell.
     *
     * @param pColumn the column
     * @return the ratio cell
     */
    public TethysUISwingTableCellImpl<OceanusRatio, C, R> ratioCell(final TethysUITableRatioColumn<C, R> pColumn) {
        return new TethysUISwingTableRatioCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Scroll Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the scroll cell
     */
    public <T> TethysUISwingTableCellImpl<T, C, R> scrollCell(final TethysUITableScrollColumn<T, C, R> pColumn,
                                                              final Class<T> pClazz) {
        return new TethysUISwingTableScrollCell<>(pColumn, theGuiFactory, pClazz);
    }

    /**
     * Obtain List Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @return the list cell
     */
    public <T extends Comparable<? super T>> TethysUISwingTableCellImpl<List<T>, C, R> listCell(final TethysUITableListColumn<T, C, R> pColumn) {
        return new TethysUISwingTableListCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Date Cell.
     *
     * @param pColumn the column
     * @return the date cell
     */
    public TethysUISwingTableCellImpl<OceanusDate, C, R> dateCell(final TethysUITableDateColumn<C, R> pColumn) {
        return new TethysUISwingTableDateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Icon Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the icon cell
     */
    public <T> TethysUISwingTableCellImpl<T, C, R> iconCell(final TethysUITableIconColumn<T, C, R> pColumn,
                                                            final Class<T> pClazz) {
        return new TethysUISwingTableIconCell<>(pColumn, theGuiFactory, pClazz);
    }
}
