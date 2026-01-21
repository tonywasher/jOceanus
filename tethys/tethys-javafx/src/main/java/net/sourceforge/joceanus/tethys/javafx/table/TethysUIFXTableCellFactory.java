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
package net.sourceforge.joceanus.tethys.javafx.table;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableCharArrayCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableDateCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableIconCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableIntegerCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableListCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableLongCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableMoneyCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTablePriceCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableRateCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableRatioCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableRawDecimalCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableScrollCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableShortCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableStringCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableCell.TethysUIFXTableUnitsCell;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableCharArrayColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableDateColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableIconColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableIntegerColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableListColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableLongColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableMoneyColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTablePriceColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRateColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRatioColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRawDecimalColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableScrollColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableShortColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableStringColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableUnitsColumn;

import java.util.List;

/**
 * TableCell implementations built on DataEditFields.
 *
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysUIFXTableCellFactory<C, R> {
    /**
     * The GUI Factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUIFXTableCellFactory(final TethysUICoreFactory<?> pFactory) {
        theGuiFactory = pFactory;
    }

    /**
     * Obtain String Cell Factory.
     *
     * @param pColumn the column
     * @return the string cell factory
     */
    Callback<TableColumn<R, String>, TableCell<R, String>> stringCellFactory(final TethysUIFXTableStringColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableStringCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain CharArray Cell Factory.
     *
     * @param pColumn the column
     * @return the charArray cell factory
     */
    Callback<TableColumn<R, char[]>, TableCell<R, char[]>> charArrayCellFactory(final TethysUIFXTableCharArrayColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableCharArrayCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Short Cell Factory.
     *
     * @param pColumn the column
     * @return the short cell factory
     */
    Callback<TableColumn<R, Short>, TableCell<R, Short>> shortCellFactory(final TethysUIFXTableShortColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableShortCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Integer Cell Factory.
     *
     * @param pColumn the column
     * @return the integer cell factory
     */
    Callback<TableColumn<R, Integer>, TableCell<R, Integer>> integerCellFactory(final TethysUIFXTableIntegerColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableIntegerCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Long Cell Factory.
     *
     * @param pColumn the column
     * @return the long cell factory
     */
    Callback<TableColumn<R, Long>, TableCell<R, Long>> longCellFactory(final TethysUIFXTableLongColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableLongCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain RawDecimal Cell Factory.
     *
     * @param pColumn the column
     * @return the rawDecimal cell factory
     */
    Callback<TableColumn<R, OceanusDecimal>, TableCell<R, OceanusDecimal>> rawDecimalCellFactory(final TethysUIFXTableRawDecimalColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableRawDecimalCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell Factory.
     *
     * @param pColumn the column
     * @return the money cell factory
     */
    Callback<TableColumn<R, OceanusMoney>, TableCell<R, OceanusMoney>> moneyCellFactory(final TethysUIFXTableMoneyColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableMoneyCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Price Cell Factory.
     *
     * @param pColumn the column
     * @return the price cell factory
     */
    Callback<TableColumn<R, OceanusPrice>, TableCell<R, OceanusPrice>> priceCellFactory(final TethysUIFXTablePriceColumn<C, R> pColumn) {
        return e -> new TethysUIFXTablePriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Rate Cell Factory.
     *
     * @param pColumn the column
     * @return the rate cell factory
     */
    Callback<TableColumn<R, OceanusRate>, TableCell<R, OceanusRate>> rateCellFactory(final TethysUIFXTableRateColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableRateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Units Cell Factory.
     *
     * @param pColumn the column
     * @return the units cell factory
     */
    Callback<TableColumn<R, OceanusUnits>, TableCell<R, OceanusUnits>> unitsCellFactory(final TethysUIFXTableUnitsColumn<C, R> pColumn) {
        return e -> new TethysUIFXTableUnitsCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Ratio Cell Factory.
     *
     * @param pColumn the column
     * @return the ratio cell factory
     */
    Callback<TableColumn<R, OceanusRatio>, TableCell<R, OceanusRatio>> ratioCellFactory(final TethysUIFXTableRatioColumn<C, R> pColumn) {
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
    <T> Callback<TableColumn<R, T>, TableCell<R, T>> scrollCellFactory(final TethysUIFXTableScrollColumn<T, C, R> pColumn,
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
    <T extends Comparable<? super T>> Callback<TableColumn<R, List<T>>, TableCell<R, List<T>>> listCellFactory(final TethysUIFXTableListColumn<T, C, R> pColumn) {
        return e -> new TethysUIFXTableListCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Date Cell Factory.
     *
     * @param pColumn the column
     * @return the date cell factory
     */
    Callback<TableColumn<R, OceanusDate>, TableCell<R, OceanusDate>> dateCellFactory(final TethysUIFXTableDateColumn<C, R> pColumn) {
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
    <T> Callback<TableColumn<R, T>, TableCell<R, T>> iconCellFactory(final TethysUIFXTableIconColumn<T, C, R> pColumn,
                                                                     final Class<T> pClazz) {
        return e -> new TethysUIFXTableIconCell<>(pColumn, theGuiFactory, pClazz);
    }
}
