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
package net.sourceforge.joceanus.jmetis.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Currency;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCell;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellRange;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetProvider;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetRow;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sheet Tester.
 */
public final class MetisSheetTester {
    /**
     * Create a logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetisSheetTester.class);

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        /**
         * Load an ODS Spreadsheet using jOpenDocument
         */
        final MetisSheetCellRange myRange = new MetisSheetCellRange("'19''87'.AA22:.AC45");
        final MetisSheetCellRange myTwo = new MetisSheetCellRange("Test'1987", myRange.getFirstCell().getPosition(), myRange.getLastCell().getPosition());
        String h = myTwo.toString();
        // loadRange();
        writeSample();
    }

    /**
     * Private constructor.
     */
    private MetisSheetTester() {
    }

    /**
     * Write a sample spreadsheet.
     */
    public static void writeSample() {

        /**
         * Write a sample ODS Spreadsheet
         */
        try {
            /* Create workBook and sheet */
            MetisSheetWorkBook myBook = MetisSheetProvider.newWorkBook(MetisSheetWorkBookType.OASISODS);
            MetisSheetSheet mySheet = myBook.newSheet("TestData");

            /* Set default styles for the columns */
            mySheet.getMutableColumnByIndex(1).setDefaultCellStyle(MetisSheetCellStyleType.STRING);
            mySheet.getMutableColumnByIndex(2).setDefaultCellStyle(MetisSheetCellStyleType.DATE);
            mySheet.getMutableColumnByIndex(10).setDefaultCellStyle(MetisSheetCellStyleType.BOOLEAN);
            mySheet.getMutableColumnByIndex(4).setDefaultCellStyle(MetisSheetCellStyleType.RATE);
            mySheet.getMutableColumnByIndex(5).setDefaultCellStyle(MetisSheetCellStyleType.UNITS);
            mySheet.getMutableColumnByIndex(7).setDefaultCellStyle(MetisSheetCellStyleType.INTEGER);
            mySheet.getMutableColumnByIndex(3).setDefaultCellStyle(MetisSheetCellStyleType.MONEY);
            mySheet.getMutableColumnByIndex(11).setDefaultCellStyle(MetisSheetCellStyleType.PRICE);
            mySheet.getMutableColumnByIndex(12).setDefaultCellStyle(MetisSheetCellStyleType.DILUTION);
            mySheet.getMutableColumnByIndex(13).setDefaultCellStyle(MetisSheetCellStyleType.RATIO);

            /* Access an explicit row */
            int i = mySheet.getRowCount();
            MetisSheetRow myRow = mySheet.getMutableRowByIndex(2);
            i = mySheet.getRowCount();

            /* Write data into each of the cells */
            MetisSheetCell myCell = myRow.getMutableCellByIndex(1);
            myCell.setStringValue("Barclays");
            myCell = myRow.getMutableCellByIndex(2);
            myCell.setDateValue(new TethysDate());
            myCell = myRow.getMutableCellByIndex(10);
            myCell.setBooleanValue(Boolean.TRUE);
            myCell = myRow.getMutableCellByIndex(4);
            myCell.setDecimalValue(new TethysRate("0.2"));
            myCell = myRow.getMutableCellByIndex(5);
            myCell.setDecimalValue(new TethysUnits("0.10"));
            myCell = myRow.getMutableCellByIndex(7);
            myCell.setIntegerValue(4);
            myCell = myRow.getMutableCellByIndex(3);
            myCell.setDecimalValue(new TethysMoney("13.45"));
            myCell = myRow.getMutableCellByIndex(11);
            myCell.setMonetaryValue(new TethysPrice(Currency.getInstance("USD")));
            myCell = myRow.getMutableCellByIndex(12);
            myCell.setDecimalValue(new TethysDilution("0.1345"));
            myCell = myRow.getMutableCellByIndex(13);
            myCell.setDecimalValue(new TethysRatio("0.66"));

            /* Write the spreadsheet out */
            final File myXFile = new File("C:\\Users\\Tony\\Documents\\TestODS.ods");
            try (FileOutputStream myOutFile = new FileOutputStream(myXFile);
                 BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile)) {
                myBook.saveToStream(myOutBuffer);
            } catch (IOException
                     | OceanusException e){
                LOGGER.fatal("Failed to save file", e);
                return;
            }

            /* Load the file and access the sheet */
            try (FileInputStream myInFile = new FileInputStream(myXFile);
                 BufferedInputStream myInBuffer = new BufferedInputStream(myInFile)) {
                myBook = MetisSheetProvider.loadFromStream(MetisSheetWorkBookType.OASISODS, myInBuffer);
                mySheet = myBook.getSheet("TestData");
            } catch (IOException
                    | OceanusException e) {
                LOGGER.fatal("Failed to load file", e);
                return;
            }

            /* Access the row */
            myRow = mySheet.getReadOnlyRowByIndex(2);

            /* Access each of the cell values */
            myCell = myRow.getReadOnlyCellByIndex(1);
            Object myValue = myCell.getStringValue();
            myCell = myRow.getReadOnlyCellByIndex(2);
            myValue = myCell.getDateValue();
            myCell = myRow.getReadOnlyCellByIndex(10);
            myValue = myCell.getBooleanValue();
            myCell = myRow.getReadOnlyCellByIndex(4);
            myValue = myCell.getRateValue();
            myCell = myRow.getReadOnlyCellByIndex(5);
            myValue = myCell.getUnitsValue();
            myCell = myRow.getReadOnlyCellByIndex(7);
            myValue = myCell.getIntegerValue();
            myCell = myRow.getReadOnlyCellByIndex(3);
            myValue = myCell.getMoneyValue();
            myCell = myRow.getReadOnlyCellByIndex(11);
            myValue = myCell.getPriceValue();
            myCell = myRow.getReadOnlyCellByIndex(12);
            myValue = myCell.getDilutionValue();
            myCell = myRow.getReadOnlyCellByIndex(3);
            myValue = myCell.getRatioValue();
            myCell = null;
        } catch (OceanusException e) {
            LOGGER.fatal("Failed to parse file", e);
        }
    }

    /**
     * Main entry point.
     */
    private static void loadRange() {

        /**
         * Load an ODS Spreadsheet using jOpenDocument
         */
        final File myFile = new File("C:\\Users\\Tony\\Documents\\NewFinance.ods");
        try (FileInputStream myInFile = new FileInputStream(myFile);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInFile)) {
            final MetisSheetWorkBook myBook = MetisSheetProvider.loadFromStream(MetisSheetWorkBookType.OASISODS, myInBuffer);
            final MetisSheetView myView = myBook.getRangeView("Finance82");
            final int iNumRows = myView.getRowCount();
            final int iNumCols = myView.getColumnCount();
            for (MetisSheetRow myRow = myView.getRowByIndex(0); myRow != null; myRow = myRow.getNextRow()) {
                MetisSheetCell myCell = myView.getRowCellByIndex(myRow, 0);
                final String myType = myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 1);
                final String myParent = myCell == null
                                                   ? null
                                                   : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 2);
                final String myAlias = myCell == null
                                                  ? null
                                                  : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 3);
                final String myPortfolio = myCell == null
                                                      ? null
                                                      : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 4);
                final String myBalance = myCell == null
                                                    ? null
                                                    : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 5);
                final String mySymbol = myCell == null
                                                   ? null
                                                   : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 6);
                final Boolean isTaxFree = myCell == null
                                                     ? null
                                                     : myCell.getBooleanValue();
                myCell = myView.getRowCellByIndex(myRow, 7);
                final Boolean isClosed = myCell == null
                                                    ? null
                                                    : myCell.getBooleanValue();
                myCell = myView.getRowCellByIndex(myRow, 8);
                myCell = null;
            }
        } catch (IOException
                | OceanusException e) {
            LOGGER.fatal("Failed to load range", e);
        }
    }
}
