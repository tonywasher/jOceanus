package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;

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
        // loadRange(null);
        try {
            // File myFile = new File("C:\\Users\\Tony\\Documents\\NewFinance.ods");
            // FileInputStream myInFile = new FileInputStream(myFile);
            // BufferedInputStream myInBuffer = new BufferedInputStream(myInFile);
            // DataWorkBook myBook = new DataWorkBook(myInBuffer, WorkBookType.OasisODS);
            DataWorkBook myBook = new DataWorkBook(WorkBookType.OasisODS);
            DataSheet mySheet = myBook.newSheet("TestData", 3, 11);
            mySheet.createColumnByIndex(1).setDefaultCellStyle(CellStyleType.String);
            mySheet.createColumnByIndex(2).setDefaultCellStyle(CellStyleType.Date);
            mySheet.createColumnByIndex(10).setDefaultCellStyle(CellStyleType.Boolean);
            mySheet.createColumnByIndex(4).setDefaultCellStyle(CellStyleType.Rate);
            mySheet.createColumnByIndex(5).setDefaultCellStyle(CellStyleType.Units);
            mySheet.createColumnByIndex(7).setDefaultCellStyle(CellStyleType.Integer);
            int i = mySheet.getRowCount();
            DataRow myRow = mySheet.createRowByIndex(2);
            i = mySheet.getRowCount();
            // mySheet.setColumnHidden(1, false);
            // SheetCell myCell = myRow.getCellByIndex(0);
            // myCell.setStringValue("Banking:Current");
            DataCell myCell = myRow.createCellByIndex(1);
            myCell.setStringValue("Barclays");
            myCell = myRow.createCellByIndex(2);
            myCell.setDateValue(new Date());
            myCell = myRow.createCellByIndex(10);
            myCell.setBooleanValue(Boolean.TRUE);
            myCell = myRow.createCellByIndex(4);
            myCell.setDecimalValue(new JRate("0.2"));
            myCell = myRow.createCellByIndex(5);
            myCell.setDecimalValue(new JUnits("0.10"));
            myCell = myRow.createCellByIndex(7);
            myCell.setIntegerValue(4);
            File myXFile = new File("C:\\Users\\Tony\\Documents\\TestODS.ods");
            FileOutputStream myOutFile = new FileOutputStream(myXFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            myCell = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    private static void loadRange(final String[] args) {

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
                DataCell myCell = myRow.getCellByIndex(0);
                String myType = myCell.getStringValue();
                myCell = myRow.getCellByIndex(1);
                String myParent = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myRow.getCellByIndex(2);
                String myAlias = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myRow.getCellByIndex(3);
                String myPortfolio = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myRow.getCellByIndex(4);
                String myBalance = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myRow.getCellByIndex(5);
                String mySymbol = (myCell == null)
                        ? null
                        : myCell.getStringValue();
                myCell = myRow.getCellByIndex(6);
                Boolean isTaxFree = (myCell == null)
                        ? null
                        : myCell.getBooleanValue();
                myCell = myRow.getCellByIndex(7);
                Boolean isClosed = (myCell == null)
                        ? null
                        : myCell.getBooleanValue();
                myCell = myRow.getCellByIndex(8);
                myCell = null;
            }
        } catch (Exception e) {
            e = null;
        }
    }

}
