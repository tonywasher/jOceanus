package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import net.sourceforge.jOceanus.jSpreadSheetManager.SheetWorkBook.WorkBookType;

public class Tester {
    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {

        /**
         * Load an ODS Spreadsheet using jOpenDocument
         */
        // loadRange(null);
        try {
            SheetWorkBook myBook = new SheetWorkBook(WorkBookType.OASISODS);
            SheetSheet mySheet = myBook.newSheet("TestData");
            int i = mySheet.getRowCount();
            SheetRow myRow = mySheet.getRowByIndex(2);
            i = mySheet.getRowCount();
            // mySheet.setColumnHidden(1, false);
            // SheetCell myCell = myRow.getCellByIndex(0);
            // myCell.setStringValue("Banking:Current");
            SheetCell myCell = myRow.getCellByIndex(1);
            myCell.setStringValue("Barclays");
            myCell = myRow.getCellByIndex(2);
            myCell.setDateValue(new Date());
            myCell = myRow.getCellByIndex(10);
            myCell.setBooleanValue(Boolean.TRUE);
            // File myFile = new File("C:\\Users\\Tony\\Documents\\TestODS.ods");
            // FileOutputStream myOutFile = new FileOutputStream(myFile);
            // BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
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
            SheetWorkBook myBook = new SheetWorkBook(myInBuffer, WorkBookType.OASISODS);
            SheetView myView = myBook.getRangeView("AccountInfo");
            int iNumRows = myView.getRowCount();
            int iNumCols = myView.getColumnCount();
            for (SheetRow myRow = myView.getRowByIndex(0); myRow != null; myRow = myRow.getNextRow()) {
                SheetCell myCell = myRow.getCellByIndex(0);
                String myType = myCell.getStringValue();
                myCell = myRow.getCellByIndex(1);
                String myParent = (myCell == null) ? null : myCell.getStringValue();
                myCell = myRow.getCellByIndex(2);
                String myAlias = (myCell == null) ? null : myCell.getStringValue();
                myCell = myRow.getCellByIndex(3);
                String myPortfolio = (myCell == null) ? null : myCell.getStringValue();
                myCell = myRow.getCellByIndex(4);
                String myBalance = (myCell == null) ? null : myCell.getStringValue();
                myCell = myRow.getCellByIndex(5);
                String mySymbol = (myCell == null) ? null : myCell.getStringValue();
                myCell = myRow.getCellByIndex(6);
                Boolean isTaxFree = (myCell == null) ? null : myCell.getBooleanValue();
                myCell = myRow.getCellByIndex(7);
                Boolean isClosed = (myCell == null) ? null : myCell.getBooleanValue();
                myCell = myRow.getCellByIndex(8);
                myCell = null;
            }
        } catch (Exception e) {
            e = null;
        }
    }

}
