/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Currency;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetProvider;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetSheet;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Sheet Tester.
 */
public final class PrometheusSheetTester {
    /**
     * Create a logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(PrometheusSheetTester.class);

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        /* Test a SpreadSheet for each type */
        // loadRange();
        writeSample(PrometheusSheetWorkBookType.OASIS,  "TestOasis.ods");
        writeSample(PrometheusSheetWorkBookType.EXCELXLS,  "TestExcel.xls");
        writeSample(PrometheusSheetWorkBookType.EXCELXLSX,  "TestExcel.xlsx");
    }

    /**
     * Private constructor.
     */
    private PrometheusSheetTester() {
    }

    /**
     * Write a sample spreadsheet.
     * @param pType the workBookType
     * @param pName the workBookName
     */
    private static void writeSample(final PrometheusSheetWorkBookType pType,
                                    final String pName) {
        /**
         * Write a sample ODS Spreadsheet.
         */
        try {
            /* Create workBook and sheet */
            System.out.println("Building " + pName);
            PrometheusSheetWorkBook myBook = PrometheusSheetProvider.newWorkBook(pType);
            PrometheusSheetSheet mySheet = myBook.newSheet("TestData");

            /* Build the Sheet */
            CellDataType.buildSheet(mySheet);

            /* Write the spreadsheet out */
            System.out.println("Writing " + pName);
            final String myHome = System.getProperty("user.home");
            final File myXFile = new File(myHome + "\\Documents\\" + pName);
            try (FileOutputStream myOutFile = new FileOutputStream(myXFile);
                 BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile)) {
                myBook.saveToStream(myOutBuffer);
            } catch (IOException
                     | OceanusException e) {
                LOGGER.fatal("Failed to save file", e);
                return;
            }

            /* Load the file and access the sheet */
            System.out.println("Loading " + pName);
            try (FileInputStream myInFile = new FileInputStream(myXFile);
                 BufferedInputStream myInBuffer = new BufferedInputStream(myInFile)) {
                myBook = PrometheusSheetProvider.loadFromStream(pType, myInBuffer);
                mySheet = myBook.getSheet("TestData");            } catch (IOException
                    | OceanusException e) {
                LOGGER.fatal("Failed to load file", e);
                return;
            }

            /* Check the Sheet */
            CellDataType.checkSheet(mySheet);

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
        final File myFile = new File("C:\\Users\\Tony\\Documents\\MoneywiseNI.ods");
        try (FileInputStream myInFile = new FileInputStream(myFile);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInFile)) {
            final PrometheusSheetWorkBook myBook = PrometheusSheetProvider.loadFromStream(PrometheusSheetWorkBookType.OASIS, myInBuffer);
            final PrometheusSheetView myView = myBook.getRangeView("Finance82");
            final int iNumRows = myView.getRowCount();
            final int iNumCols = myView.getColumnCount();
            for (PrometheusSheetRow myRow = myView.getRowByIndex(0); myRow != null; myRow = myRow.getNextRow()) {
                int myIndex = 0;
                PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final String myType = myCell.getString();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final String myParent = myCell == null
                                                   ? null
                                                   : myCell.getString();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final String myAlias = myCell == null
                                                  ? null
                                                  : myCell.getString();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final String myPortfolio = myCell == null
                                                      ? null
                                                      : myCell.getString();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final String myBalance = myCell == null
                                                    ? null
                                                    : myCell.getString();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final String mySymbol = myCell == null
                                                   ? null
                                                   : myCell.getString();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final Boolean isTaxFree = myCell == null
                                                     ? null
                                                     : myCell.getBoolean();
                myCell = myView.getRowCellByIndex(myRow, myIndex++);
                final Boolean isClosed = myCell == null
                                                    ? null
                                                    : myCell.getBoolean();
                myCell = myView.getRowCellByIndex(myRow, myIndex);
                myCell = null;
            }
        } catch (IOException
                | OceanusException e) {
            LOGGER.fatal("Failed to load range", e);
        }
    }

    /**
     * Cell Data.
     */
    public enum CellDataType {
        /**
         * String.
         */
        STRING("String", "Barclays"),

        /**
         * Integer.
         */
        INTEGER("Integer", 25),

        /**
         * Date.
         */
        DATE("Date", new TethysDate()),

        /**
         * Units.
         */
        UNITS("Units", new TethysUnits("0.10")),

        /**
         * Ratio.
         */
        RATIO("Ratio", new TethysRatio("0.66")),

        /**
         * Units.
         */
        DILUTION("Dilution", new TethysDilution("0.1345")),

        /**
         * Rate.
         */
        RATE("Rate", TethysRate.getWholePercentage(10)),

        /**
         * MoneyGB.
         */
        MONEYGB("MoneyGB", TethysMoney.getWholeUnits(23, Currency.getInstance("GBP"))),

        /**
         * PriceJPY.
         */
        PRICEJPY("PriceJP", TethysPrice.getWholeUnits(6, Currency.getInstance("JPY"))),

        /**
         * MoneyUS.
         */
        MONEYUS("MoneyUS", TethysMoney.getWholeUnits(46, Currency.getInstance("USD"))),

        /**
         * PriceEUR.
         */
        PRICEEUR("PriceEU", TethysPrice.getWholeUnits(16, Currency.getInstance("EUR"))),

        /**
         * Boolean.
         */
        BOOLEAN("Boolean", Boolean.TRUE);

        /**
         * The header.
         */
        private final String theHeader;

        /**
         * The value.
         */
        private final Object theValue;

        /**
         * Constructor.
         * @param pHeader the header
         * @param pValue the value
         */
        CellDataType(final String pHeader,
                     final Object pValue) {
            theHeader = pHeader;
            theValue = pValue;
        }

        /**
         * Does this CellType have an alt value?
         * @return true/false
         */
        public boolean hasAltValue() {
            switch (this) {
                case MONEYGB:
                case MONEYUS:
                case PRICEJPY:
                case PRICEEUR:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Set Cell value.
         * @param pRow the row
         * @throws OceanusException on error
         */
        void setHeader(final PrometheusSheetRow pRow) throws OceanusException {
            final PrometheusSheetCell myCell = pRow.getMutableCellByIndex(ordinal());
            myCell.setHeader(theHeader);
        }

        /**
         * Set Cell value.
         * @param pRow the row
         * @throws OceanusException on error
         */
        void setValue(final PrometheusSheetRow pRow) throws OceanusException {
            final PrometheusSheetCell myCell = pRow.getMutableCellByIndex(ordinal());
            switch (this) {
                case BOOLEAN:
                    myCell.setBoolean((Boolean) theValue);
                    break;
                case INTEGER:
                    myCell.setInteger((Integer) theValue);
                    break;
                case DATE:
                    myCell.setDate((TethysDate) theValue);
                    break;
                case RATE:
                case RATIO:
                case UNITS:
                case DILUTION:
                case MONEYGB:
                case MONEYUS:
                case PRICEJPY:
                case PRICEEUR:
                    myCell.setDecimal((TethysDecimal) theValue);
                    break;
                case STRING:
                default:
                    myCell.setString((String) theValue);
                    break;
            }
        }

        /**
         * Set Alt Cell value.
         * @param pRow the row
         * @throws OceanusException on error
         */
        void setAltValue(final PrometheusSheetRow pRow) throws OceanusException {
            final PrometheusSheetCell myCell = pRow.getMutableCellByIndex(ordinal());
            switch (this) {
                case MONEYGB:
                case MONEYUS:
                case PRICEJPY:
                case PRICEEUR:
                    myCell.setMonetary((TethysMoney) theValue);
                    break;
                default:
                    break;
            }
        }

        /**
         * Check Cell value.
         * @param pRow the row
         * @throws OceanusException on error
         */
        public void checkCellValue(final PrometheusSheetRow pRow) throws OceanusException {
            final Object myValue = getCellValue(pRow);
            if (!theValue.equals(myValue)) {
                LOGGER.error(theHeader + " differs");
            }
        }

        /**
         * Get Cell value.
         * @param pRow the row
         * @return the value
         * @throws OceanusException on error
         */
        private Object getCellValue(final PrometheusSheetRow pRow) throws OceanusException {
            final PrometheusSheetCell myCell = pRow.getReadOnlyCellByIndex(ordinal());
            switch (this) {
                case BOOLEAN:
                    return myCell.getBoolean();
                case INTEGER:
                    return myCell.getInteger();
                case DATE:
                    return myCell.getDate();
                case RATE:
                    return myCell.getRate();
                case RATIO:
                    return myCell.getRatio();
                case UNITS:
                    return myCell.getUnits();
                case DILUTION:
                    return myCell.getDilution();
                case MONEYGB:
                case MONEYUS:
                    return myCell.getMoney();
                case PRICEJPY:
                case PRICEEUR:
                    return myCell.getPrice();
                case STRING:
                default:
                    return myCell.getString();
            }
        }

        /**
         * Get StyleType.
         * @return the styleType
         */
        private PrometheusSheetCellStyleType getStyleType() {
            switch (this) {
                case BOOLEAN:
                    return PrometheusSheetCellStyleType.BOOLEAN;
                case INTEGER:
                    return PrometheusSheetCellStyleType.INTEGER;
                case DATE:
                    return PrometheusSheetCellStyleType.DATE;
                case RATE:
                    return PrometheusSheetCellStyleType.RATE;
                case RATIO:
                    return PrometheusSheetCellStyleType.RATIO;
                case UNITS:
                    return PrometheusSheetCellStyleType.UNITS;
                case DILUTION:
                    return PrometheusSheetCellStyleType.DILUTION;
                case MONEYGB:
                case MONEYUS:
                    return PrometheusSheetCellStyleType.MONEY;
                case PRICEJPY:
                case PRICEEUR:
                    return PrometheusSheetCellStyleType.PRICE;
                case STRING:
                default:
                    return PrometheusSheetCellStyleType.STRING;
            }
        }

        /**
         * Build SpreadSheet.
         * @param pSheet the sheet.
         * @throws OceanusException on error
         */
        public static void buildSheet(final PrometheusSheetSheet pSheet) throws OceanusException {
            /* Loop through the cellTypes */
            for (CellDataType myType: CellDataType.values()) {
                /* Define the columns */
                pSheet.getMutableColumnByIndex(myType.ordinal()).setDefaultCellStyle(myType.getStyleType());
            }

            /* Access the rows */
            final PrometheusSheetRow myHdrRow = pSheet.getMutableRowByIndex(0);
            final PrometheusSheetRow myDataRow = pSheet.getMutableRowByIndex(1);
            final PrometheusSheetRow myAltRow = pSheet.getMutableRowByIndex(2);

            /* Loop through the cellTypes */
            for (CellDataType myType: CellDataType.values()) {
                /* Populate the data */
                myType.setHeader(myHdrRow);
                myType.setValue(myDataRow);
                if (myType.hasAltValue()) {
                    myType.setAltValue(myAltRow);
                }
            }
        }

        /**
         * Check SpreadSheet.
         * @param pSheet the sheet.
         * @throws OceanusException on error
         */
        public static void checkSheet(final PrometheusSheetSheet pSheet) throws OceanusException {
            /* Access the rows */
            final PrometheusSheetRow myDataRow = pSheet.getReadOnlyRowByIndex(1);
            final PrometheusSheetRow myAltRow = pSheet.getReadOnlyRowByIndex(2);

            /* Loop through the cellTypes */
            for (CellDataType myType: CellDataType.values()) {
                /* Check cell values */
                myType.checkCellValue(myDataRow);
                if (myType.hasAltValue()) {
                    myType.checkCellValue(myAltRow);
                }
            }
        }
    }
}
