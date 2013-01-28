package net.sourceforge.jOceanus.jSpreadSheetManager;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCellRange;
import org.odftoolkit.odfdom.doc.table.OdfTableColumn;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;

/**
 * Class representing a sheet within a workBook.
 */
public class DataSheet {
    /**
     * Character width.
     */
    private static final int WIDTH_CHAR = 256;

    /**
     * Sheet type.
     */
    private final WorkBookType theBookType;

    /**
     * The Excel WorkBook.
     */
    private final ExcelWorkBook theExcelBook;

    /**
     * The Excel Sheet.
     */
    private final HSSFSheet theExcelSheet;

    /**
     * The Oasis WorkBook.
     */
    private final OasisWorkBook theOasisBook;

    /**
     * The Oasis Sheet.
     */
    private final OdfTable theOasisSheet;

    /**
     * Name of sheet.
     */
    private final String theSheetName;

    /**
     * Obtain the name of the sheet.
     * @return the name
     */
    public String getName() {
        return theSheetName;
    }

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    protected CellValue evaluateFormula(final HSSFCell pCell) {
        return theExcelBook.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    protected String formatCellValue(final HSSFCell pCell) {
        return theExcelBook.formatCellValue(pCell);
    }

    /**
     * Constructor for Excel Sheet.
     * @param pBook the WorkBook
     * @param pSheet the Excel sheet
     */
    protected DataSheet(final ExcelWorkBook pBook,
                        final HSSFSheet pSheet) {
        /* Store parameters */
        theExcelBook = pBook;
        theExcelSheet = pSheet;
        theOasisBook = null;
        theOasisSheet = null;
        theBookType = WorkBookType.ExcelXLS;
        theSheetName = pSheet.getSheetName();
    }

    /**
     * Constructor for Oasis Sheet.
     * @param pBook the WorkBook
     * @param pSheet the Oasis sheet
     */
    protected DataSheet(final OasisWorkBook pBook,
                        final OdfTable pSheet) {
        /* Store parameters */
        theOasisBook = pBook;
        theOasisSheet = pSheet;
        theExcelBook = null;
        theExcelSheet = null;
        theBookType = WorkBookType.OasisODS;
        theSheetName = pSheet.getTableName();
    }

    /**
     * Get row count.
     * @return the count of rows
     */
    public int getRowCount() {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                int iLastRowNum = theExcelSheet.getLastRowNum();
                return (iLastRowNum == 0) ? theExcelSheet.getPhysicalNumberOfRows() : iLastRowNum + 1;
            case OasisODS:
                return theOasisSheet.getRowCount();
            default:
                return 0;
        }
    }

    /**
     * Obtain the row at required index within the sheet.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public DataRow getRowByIndex(final int pRowIndex) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                HSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
                return new DataRow(this, myExcelRow);
            case OasisODS:
                OdfTableRow myOasisRow = theOasisSheet.getRowByIndex(pRowIndex);
                return new DataRow(this, myOasisRow);
            default:
                return null;
        }
    }

    /**
     * Create the row at required index within the sheet.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public DataRow createRowByIndex(final int pRowIndex) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                HSSFRow myExcelRow = theExcelSheet.createRow(pRowIndex);
                return new DataRow(this, myExcelRow);
            case OasisODS:
                OdfTableRow myOasisRow = theOasisSheet.getRowByIndex(pRowIndex);
                return new DataRow(this, myOasisRow);
            default:
                return null;
        }
    }

    /**
     * Obtain the row at required index within the view.
     * @param pView the requested row index
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    protected DataRow getViewRowByIndex(final DataView pView,
                                        final int pRowIndex) {
        /* Determine the actual index of the row */
        int myIndex = pView.getFirstCell().getRowIndex()
                      + pRowIndex;

        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                HSSFRow myExcelRow = theExcelSheet.getRow(myIndex);
                return new DataRow(pView, myExcelRow);
            case OasisODS:
                OdfTableRow myOasisRow = theOasisSheet.getRowByIndex(myIndex);
                return new DataRow(pView, myOasisRow);
            default:
                return null;
        }
    }

    /**
     * Name a range.
     * @param pName the name of the range
     * @param pFirstCell the first cell in the range
     * @param pLastCell the last cell in the range
     * @throws JDataException on error
     */
    public void declareRange(final String pName,
                             final CellPosition pFirstCell,
                             final CellPosition pLastCell) throws JDataException {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                /* Build the area reference */
                CellReference myFirst = new CellReference(theSheetName, pFirstCell.getRowIndex(), pFirstCell.getColumnIndex(), true, true);
                CellReference myLast = new CellReference(theSheetName, pLastCell.getRowIndex(), pLastCell.getColumnIndex(), true, true);
                AreaReference myArea = new AreaReference(myFirst, myLast);

                /* Declare to workBook */
                theExcelBook.declareRange(pName, myArea);
                break;
            case OasisODS:
                /* Build the range */
                OasisCellRange myRange = new OasisCellRange(theSheetName, pFirstCell, pLastCell);

                /* Declare to workBook */
                theOasisBook.declareRange(pName, myRange);
                break;
            default:
                break;
        }
    }

    /**
     * Apply data validation to a range of cells.
     * @param pFirstCell the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pName the name of the validation range list
     * @throws JDataException on error
     */
    public void applyDataValidation(final CellPosition pFirstCell,
                                    final CellPosition pLastCell,
                                    final String pName) throws JDataException {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                /* Create the CellAddressList */
                CellRangeAddressList myCells = new CellRangeAddressList(pFirstCell.getRowIndex(), pLastCell.getRowIndex(), pFirstCell.getColumnIndex(),
                        pLastCell.getColumnIndex());

                /* Declare to workBook */
                theExcelBook.applyDataValidation(theExcelSheet, myCells, pName);
                break;
            case OasisODS:
                /* Create the CellAddressList */
                OdfTableCellRange myRange = theOasisSheet.getCellRangeByPosition(pFirstCell.getColumnIndex(), pFirstCell.getRowIndex(),
                        pLastCell.getRowIndex(), pLastCell.getColumnIndex());

                /* Declare to workBook */
                theOasisBook.applyDataValidation(theOasisSheet, myRange, pName);
                break;
            default:
                break;
        }
    }

    /**
     * Name a single cell as a range.
     * @param pName the name of the range
     * @param pSingleCell the cell to name
     * @throws JDataException on error
     */
    public void declareRange(final String pName,
                             final CellPosition pSingleCell) throws JDataException {
        /* declare the range */
        declareRange(pName, pSingleCell, pSingleCell);
    }

    /**
     * Create freeze panes.
     * @param pFreezeCell the cell to freeze at
     */
    public void createFreezePane(final CellPosition pFreezeCell) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                theExcelSheet.createFreezePane(pFreezeCell.getColumnIndex(), pFreezeCell.getRowIndex());
                break;
            case OasisODS:
            default:
                break;
        }
    }

    /**
     * Set Column hidden status.
     * @param pColIndex the column to show/hide
     * @param isHidden is the column hidden?
     */
    public void setColumnHidden(final int pColIndex,
                                final boolean isHidden) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                theExcelSheet.setColumnHidden(pColIndex, isHidden);
                break;
            case OasisODS:
                /* Obtain the column definition */
                OdfTableColumn myCol = theOasisSheet.getColumnByIndex(pColIndex);
                TableTableColumnElement myElement = myCol.getOdfElement();
                myElement.setTableVisibilityAttribute(isHidden ? "collapse" : "visible");
                break;
            default:
                break;
        }
    }

    /**
     * Set Column width.
     * @param pColIndex the column to set width for
     * @param pWidth the width in characters
     */
    public void setColumnWidth(final int pColIndex,
                               final int pWidth) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                /* Set the column width */
                theExcelSheet.setColumnWidth(pColIndex, WIDTH_CHAR
                                                        * pWidth);
                break;
            case OasisODS:
                /* Obtain the column definition */
                OdfTableColumn myCol = theOasisSheet.getColumnByIndex(pColIndex);
                myCol.setWidth(2 * pWidth);
                break;
            default:
                break;
        }
    }

    /**
     * Set Column width.
     * @param pColIndex the column to set style for
     * @param pStyle the default style type
     */
    public void setDefaultColumnStyle(final int pColIndex,
                                      final CellStyleType pStyle) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                theExcelSheet.setDefaultColumnStyle(pColIndex, theExcelBook.getCellStyle(pStyle));
                break;
            case OasisODS:
                /* Obtain the column definition */
                OdfTableColumn myCol = theOasisSheet.getColumnByIndex(pColIndex);
                myCol.setDefaultCellStyle(theOasisBook.getCellStyle(pStyle));
                break;
            default:
                break;
        }
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final DataCell pCell,
                                final CellStyleType pStyle) {
        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                pCell.setCellStyle(theExcelBook.getCellStyle(pStyle));
                break;
            case OasisODS:
                pCell.setCellStyle(theOasisBook.getStyleName(pStyle));
                break;
            default:
                break;
        }
    }
}
