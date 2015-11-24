/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Currency;

import net.sourceforge.joceanus.jmetis.sheet.CellStyleType;
import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataSheet;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.OasisCellAddress.OasisCellRange;
import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

public class SheetTest {
    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        /**
         * Load an ODS Spreadsheet using jOpenDocument
         */
        OasisCellRange myRange = new OasisCellRange("'19''87'.AA22:.AC45");
        OasisCellRange myTwo = new OasisCellRange("Test'1987", myRange.getFirstCell().getPosition(), myRange.getLastCell().getPosition());
        String h = myTwo.toString();
        // loadRange();
        writeSample();
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
            DataWorkBook myBook = new DataWorkBook(WorkBookType.OASISODS);
            DataSheet mySheet = myBook.newSheet("TestData");

            /* Set default styles for the columns */
            mySheet.getMutableColumnByIndex(1).setDefaultCellStyle(CellStyleType.STRING);
            mySheet.getMutableColumnByIndex(2).setDefaultCellStyle(CellStyleType.DATE);
            mySheet.getMutableColumnByIndex(10).setDefaultCellStyle(CellStyleType.BOOLEAN);
            mySheet.getMutableColumnByIndex(4).setDefaultCellStyle(CellStyleType.RATE);
            mySheet.getMutableColumnByIndex(5).setDefaultCellStyle(CellStyleType.UNITS);
            mySheet.getMutableColumnByIndex(7).setDefaultCellStyle(CellStyleType.INTEGER);
            mySheet.getMutableColumnByIndex(3).setDefaultCellStyle(CellStyleType.MONEY);
            mySheet.getMutableColumnByIndex(11).setDefaultCellStyle(CellStyleType.PRICE);
            mySheet.getMutableColumnByIndex(12).setDefaultCellStyle(CellStyleType.DILUTION);
            mySheet.getMutableColumnByIndex(13).setDefaultCellStyle(CellStyleType.RATIO);

            /* Access an explicit row */
            int i = mySheet.getRowCount();
            DataRow myRow = mySheet.getMutableRowByIndex(2);
            i = mySheet.getRowCount();

            /* Write data into each of the cells */
            DataCell myCell = myRow.getMutableCellByIndex(1);
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
            File myXFile = new File("C:\\Users\\Tony\\Documents\\TestODS.ods");
            FileOutputStream myOutFile = new FileOutputStream(myXFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            myBook.saveToStream(myOutBuffer);
            myOutBuffer.close();

            /* Load the file and access the sheet */
            FileInputStream myInFile = new FileInputStream(myXFile);
            BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            myBook = new DataWorkBook(myInBuffer, WorkBookType.OASISODS);
            mySheet = myBook.getSheet("TestData");
            myInBuffer.close();

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry point.
     */
    private static void loadRange() {

        /**
         * Load an ODS Spreadsheet using jOpenDocument
         */
        try {
            File myFile = new File("C:\\Users\\Tony\\Documents\\NewFinance.ods");
            FileInputStream myInFile = new FileInputStream(myFile);
            BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            DataWorkBook myBook = new DataWorkBook(myInBuffer, WorkBookType.OASISODS);
            DataView myView = myBook.getRangeView("Finance82");
            int iNumRows = myView.getRowCount();
            int iNumCols = myView.getColumnCount();
            for (DataRow myRow = myView.getRowByIndex(0); myRow != null; myRow = myRow.getNextRow()) {
                DataCell myCell = myView.getRowCellByIndex(myRow, 0);
                String myType = myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 1);
                String myParent = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 2);
                String myAlias = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 3);
                String myPortfolio = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 4);
                String myBalance = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 5);
                String mySymbol = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myView.getRowCellByIndex(myRow, 6);
                Boolean isTaxFree = (myCell == null)
                        ? null
                        : myCell.getBooleanValue();
                myCell = myView.getRowCellByIndex(myRow, 7);
                Boolean isClosed = (myCell == null)
                        ? null
                        : myCell.getBooleanValue();
                myCell = myView.getRowCellByIndex(myRow, 8);
                myCell = null;
            }
        } catch (FileNotFoundException e) {
            e = null;
        } catch (OceanusException e) {
            e = null;
        }
    }
}
