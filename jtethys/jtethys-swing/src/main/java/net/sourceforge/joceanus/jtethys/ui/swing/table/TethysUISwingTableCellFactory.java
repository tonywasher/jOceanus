/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing.table;

import java.util.List;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableCharArrayCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableDateCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableDilutedPriceCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableDilutionCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableIconCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableIntegerCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableListCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableLongCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableMoneyCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTablePriceCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableRateCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableRatioCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableRawDecimalCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableScrollCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableShortCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableStringCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableCell.TethysUISwingTableUnitsCell;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableUnitsColumn;

/**
 * Swing Table Cell Factory.
 *
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysUISwingTableCellFactory<C, R> {
    /**
     * The GUI Factory.
     */
    private final TethysUICoreFactory<?> theGuiFactory;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUISwingTableCellFactory(final TethysUICoreFactory<?> pFactory) {
        theGuiFactory = pFactory;
    }

    /**
     * Obtain String Cell.
     *
     * @param pColumn the column
     * @return the string cell
     */
    TethysUISwingTableCell<String, C, R> stringCell(final TethysUISwingTableStringColumn<C, R> pColumn) {
        return new TethysUISwingTableStringCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain charArray Cell.
     *
     * @param pColumn the column
     * @return the charArray cell
     */
    TethysUISwingTableCell<char[], C, R> charArrayCell(final TethysUISwingTableCharArrayColumn<C, R> pColumn) {
        return new TethysUISwingTableCharArrayCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Short Cell.
     *
     * @param pColumn the column
     * @return the short cell
     */
    TethysUISwingTableCell<Short, C, R> shortCell(final TethysUISwingTableShortColumn<C, R> pColumn) {
        return new TethysUISwingTableShortCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Integer Cell.
     *
     * @param pColumn the column
     * @return the integer cell
     */
    TethysUISwingTableCell<Integer, C, R> integerCell(final TethysUISwingTableIntegerColumn<C, R> pColumn) {
        return new TethysUISwingTableIntegerCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Long Cell.
     *
     * @param pColumn the column
     * @return the long cell
     */
    TethysUISwingTableCell<Long, C, R> longCell(final TethysUISwingTableLongColumn<C, R> pColumn) {
        return new TethysUISwingTableLongCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell.
     *
     * @param pColumn the column
     * @return the money cell
     */
    TethysUISwingTableCell<TethysDecimal, C, R> rawDecimalCell(final TethysUISwingTableRawDecimalColumn<C, R> pColumn) {
        return new TethysUISwingTableRawDecimalCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell.
     *
     * @param pColumn the column
     * @return the money cell
     */
    TethysUISwingTableCell<TethysMoney, C, R> moneyCell(final TethysUISwingTableMoneyColumn<C, R> pColumn) {
        return new TethysUISwingTableMoneyCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Price Cell.
     *
     * @param pColumn the column
     * @return the price cell
     */
    TethysUISwingTableCell<TethysPrice, C, R> priceCell(final TethysUISwingTablePriceColumn<C, R> pColumn) {
        return new TethysUISwingTablePriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Rate Cell.
     *
     * @param pColumn the column
     * @return the rate cell
     */
    TethysUISwingTableCell<TethysRate, C, R> rateCell(final TethysUISwingTableRateColumn<C, R> pColumn) {
        return new TethysUISwingTableRateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Units Cell.
     *
     * @param pColumn the column
     * @return the units cell
     */
    TethysUISwingTableCell<TethysUnits, C, R> unitsCell(final TethysUISwingTableUnitsColumn<C, R> pColumn) {
        return new TethysUISwingTableUnitsCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Dilution Cell.
     *
     * @param pColumn the column
     * @return the dilution cell
     */
    TethysUISwingTableCell<TethysDilution, C, R> dilutionCell(final TethysUISwingTableDilutionColumn<C, R> pColumn) {
        return new TethysUISwingTableDilutionCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Ratio Cell.
     *
     * @param pColumn the column
     * @return the ratio cell
     */
    TethysUISwingTableCell<TethysRatio, C, R> ratioCell(final TethysUISwingTableRatioColumn<C, R> pColumn) {
        return new TethysUISwingTableRatioCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain DilutedPrice Cell.
     *
     * @param pColumn the column
     * @return the dilutedPrice cell
     */
    TethysUISwingTableCell<TethysDilutedPrice, C, R> dilutedPriceCell(final TethysUISwingTableDilutedPriceColumn<C, R> pColumn) {
        return new TethysUISwingTableDilutedPriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Scroll Cell.
     *
     * @param <T>     the column type
     * @param pColumn the column
     * @param pClazz  the class of the item
     * @return the scroll cell
     */
    <T> TethysUISwingTableCell<T, C, R> scrollCell(final TethysUISwingTableScrollColumn<T, C, R> pColumn,
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
    <T extends Comparable<T>> TethysUISwingTableCell<List<T>, C, R> listCell(final TethysUISwingTableListColumn<T, C, R> pColumn) {
        return new TethysUISwingTableListCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Date Cell.
     *
     * @param pColumn the column
     * @return the date cell
     */
    TethysUISwingTableCell<TethysDate, C, R> dateCell(final TethysUISwingTableDateColumn<C, R> pColumn) {
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
    <T> TethysUISwingTableCell<T, C, R> iconCell(final TethysUISwingTableIconColumn<T, C, R> pColumn,
                                                 final Class<T> pClazz) {
        return new TethysUISwingTableIconCell<>(pColumn, theGuiFactory, pClazz);
    }
}
