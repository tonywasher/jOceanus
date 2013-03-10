package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import net.sourceforge.jOceanus.jDecimal.JMoney;
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
            DataSheet mySheet = myBook.newSheet("TestData");
            mySheet.getMutableColumnByIndex(1).setDefaultCellStyle(CellStyleType.String);
            mySheet.getMutableColumnByIndex(2).setDefaultCellStyle(CellStyleType.Date);
            mySheet.getMutableColumnByIndex(10).setDefaultCellStyle(CellStyleType.Boolean);
            mySheet.getMutableColumnByIndex(4).setDefaultCellStyle(CellStyleType.Rate);
            mySheet.getMutableColumnByIndex(5).setDefaultCellStyle(CellStyleType.Units);
            mySheet.getMutableColumnByIndex(7).setDefaultCellStyle(CellStyleType.Integer);
            mySheet.getMutableColumnByIndex(3).setDefaultCellStyle(CellStyleType.Money);
            int i = mySheet.getRowCount();
            DataRow myRow = mySheet.getMutableRowByIndex(2);
            i = mySheet.getRowCount();
            // mySheet.setColumnHidden(1, false);
            // SheetCell myCell = myRow.getCellByIndex(0);
            // myCell.setStringValue("Banking:Current");
            DataCell myCell = myRow.getMutableCellByIndex(1);
            myCell.setStringValue("Barclays");
            myCell = myRow.getMutableCellByIndex(2);
            myCell.setDateValue(new Date());
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
            File myXFile = new File("C:\\Users\\Tony\\Documents\\TestODS.ods");
            FileOutputStream myOutFile = new FileOutputStream(myXFile);
            BufferedOutputStream myOutBuffer = new BufferedOutputStream(myOutFile);
            myBook.saveToStream(myOutBuffer);
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
