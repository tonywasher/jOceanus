package net.sourceforge.joceanus.jspreadsheetmanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Currency;

import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook.WorkBookType;
import net.sourceforge.joceanus.jspreadsheetmanager.OasisCellAddress.OasisCellRange;

public class Tester {
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
            DataWorkBook myBook = new DataWorkBook(WorkBookType.OasisODS);
            DataSheet mySheet = myBook.newSheet("TestData");

            /* Set default styles for the columns */
            mySheet.getMutableColumnByIndex(1).setDefaultCellStyle(CellStyleType.String);
            mySheet.getMutableColumnByIndex(2).setDefaultCellStyle(CellStyleType.Date);
            mySheet.getMutableColumnByIndex(10).setDefaultCellStyle(CellStyleType.Boolean);
            mySheet.getMutableColumnByIndex(4).setDefaultCellStyle(CellStyleType.Rate);
            mySheet.getMutableColumnByIndex(5).setDefaultCellStyle(CellStyleType.Units);
            mySheet.getMutableColumnByIndex(7).setDefaultCellStyle(CellStyleType.Integer);
            mySheet.getMutableColumnByIndex(3).setDefaultCellStyle(CellStyleType.Money);
            mySheet.getMutableColumnByIndex(11).setDefaultCellStyle(CellStyleType.Price);
            mySheet.getMutableColumnByIndex(12).setDefaultCellStyle(CellStyleType.Dilution);
            mySheet.getMutableColumnByIndex(13).setDefaultCellStyle(CellStyleType.Ratio);

            /* Access an explicit row */
            int i = mySheet.getRowCount();
            DataRow myRow = mySheet.getMutableRowByIndex(2);
            i = mySheet.getRowCount();

            /* Write data into each of the cells */
            DataCell myCell = myRow.getMutableCellByIndex(1);
            myCell.setStringValue("Barclays");
            myCell = myRow.getMutableCellByIndex(2);
            myCell.setDateValue(new JDateDay());
            myCell = myRow.getMutableCellByIndex(10);
            myCell.setBooleanValue(Boolean.TRUE);
            myCell = myRow.getMutableCellByIndex(4);
            myCell.setDecimalValue(new JRate("0.2"));
            myCell = myRow.getMutableCellByIndex(5);
            myCell.setDecimalValue(new JUnits("0.10"));
            myCell = myRow.getMutableCellByIndex(7);
            myCell.setIntegerValue(4);
            myCell = myRow.getMutableCellByIndex(3);
            myCell.setDecimalValue(new JMoney("13.45"));
            myCell = myRow.getMutableCellByIndex(11);
            myCell.setMonetaryValue(new JPrice(Currency.getInstance("USD")));
            myCell = myRow.getMutableCellByIndex(12);
            myCell.setDecimalValue(new JDilution("0.1345"));
            myCell = myRow.getMutableCellByIndex(13);
            myCell.setDecimalValue(new JRatio("0.66"));

            /* Write the spreadsheet out */
            File myXFile = new File("C:\\Users\\Tony\\Documents\\TestODS.ods");
            FileOutputStream myOutFile = new FileOutputStream(myXFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            myBook.saveToStream(myOutBuffer);
            myOutBuffer.close();

            /* Load the file and access the sheet */
            FileInputStream myInFile = new FileInputStream(myXFile);
            BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            myBook = new DataWorkBook(myInBuffer, WorkBookType.OasisODS);
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

        } catch (Exception e) {
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
            DataWorkBook myBook = new DataWorkBook(myInBuffer, WorkBookType.OasisODS);
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
        } catch (Exception e) {
            e = null;
        }
    }

}
