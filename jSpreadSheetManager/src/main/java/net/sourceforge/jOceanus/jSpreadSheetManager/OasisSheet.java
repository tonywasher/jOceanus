/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2013 Tony Washer
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
package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.simple.table.Column;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

/**
 * Class representing an Oasis sheet within a workBook.
 */
public class OasisSheet
        extends DataSheet {
    /**
     * The Oasis WorkBook.
     */
    private final OasisWorkBook theOasisBook;

    /**
     * The ContentDom.
     */
    private final OdfContentDom theContentDom;

    /**
     * The Sheet index.
     */
    private final int theIndex;

    /**
     * The Oasis Table.
     */
    private final TableTableElement theOasisTable;

    /**
     * The Oasis Sheet.
     */
    private final Table theOasisSheet;

    /**
     * The Row Count.
     */
    private int theRowCount;

    /**
     * The Column Count.
     */
    private int theColCount;

    /**
     * Constructor for Oasis Sheet.
     * @param pBook the WorkBook
     * @param pTable the Oasis table
     * @param pIndex the index of the sheet
     */
    protected OasisSheet(final OasisWorkBook pBook,
                         final TableTableElement pTable,
                         final int pIndex) {
        /* Construct super-class */
        super(pTable.getTableNameAttribute());

        /* Store parameters */
        theOasisBook = pBook;
        theContentDom = pBook.getContentDom();
        theIndex = pIndex;
        theOasisTable = pTable;
        theOasisSheet = Table.getInstance(pTable);
        theRowCount = theOasisSheet.getRowCount();
        theColCount = theOasisSheet.getColumnCount();
    }

    /**
     * Obtain the underlying table element.
     * @return the element
     */
    public TableTableElement getTableElement() {
        return theOasisTable;
    }

    @Override
    public int getSheetIndex() {
        return theIndex;
    }

    @Override
    public int getRowCount() {
        return theRowCount;
    }

    @Override
    public DataRow getRowByIndex(final int pRowIndex) {
        /* Handle negative row index */
        if (pRowIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pRowIndex >= theRowCount) {
            /* If we need to add just a single row */
            if (pRowIndex == theRowCount) {
                /* Append row and return it */
                theRowCount++;
                return new OasisRow(this, theOasisSheet.appendRow(), pRowIndex);
            }

            /* Create the extra rows */
            int myXtraRows = pRowIndex
                             - theRowCount
                             + 1;
            List<Row> myRows = theOasisSheet.appendRows(myXtraRows);

            /* Access required row and return it */
            theRowCount += myXtraRows;
            return new OasisRow(this, myRows.get(myXtraRows - 1), pRowIndex);
        }

        /* Just return the required row */
        return new OasisRow(this, theOasisSheet.getRowByIndex(pRowIndex), pRowIndex);
    }

    /**
     * Obtain the column by index.
     * @param pColIndex the column index
     * @return the column
     */
    private Column getColumnByIndex(final int pColIndex) {
        /* Handle negative row index */
        if (pColIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pColIndex >= theColCount) {
            /* If we need to add just a single column */
            if (pColIndex == theColCount) {
                /* Append column and return it */
                theColCount++;
                return theOasisSheet.appendColumn();
            }

            /* Create the extra columns */
            int myXtraCols = pColIndex
                             - theColCount
                             + 1;
            List<Column> myColumns = theOasisSheet.appendColumns(myXtraCols);

            /* Access required column and return it */
            theColCount += myXtraCols;
            return myColumns.get(myXtraCols - 1);
        }

        /* Just return the required column */
        return theOasisSheet.getColumnByIndex(pColIndex);
    }

    @Override
    protected DataRow getRowByIndex(final DataView pView,
                                    final int pRowIndex) {
        /* Determine the actual index of the row */
        int myIndex = pView.convertRowIndex(pRowIndex);
        if (myIndex < 0) {
            return null;
        }

        /* Switch on book type */
        Row myOasisRow = theOasisSheet.getRowByIndex(myIndex);
        return new OasisRow(pView, myOasisRow, pRowIndex);
    }

    @Override
    public void declareRange(final String pName,
                             final CellPosition pFirstCell,
                             final CellPosition pLastCell) throws JDataException {
        /* Build the range */
        OasisCellRange myRange = new OasisCellRange(getName(), pFirstCell, pLastCell);

        /* Declare to workBook */
        theOasisBook.declareRange(pName, myRange);
    }

    @Override
    public void applyDataValidation(final CellPosition pFirstCell,
                                    final CellPosition pLastCell,
                                    final String pName) throws JDataException {
        /* Declare to workBook */
        theOasisBook.applyDataValidation(theOasisSheet, pFirstCell, pLastCell, pName);
    }

    @Override
    public void applyDataFilter(final CellPosition pBaseCell,
                                final int pNumRows) throws JDataException {
        /* Build the range */
        CellPosition myEnd = new CellPosition(pBaseCell.getColumnIndex(), pNumRows - 1);
        OasisCellRange myRange = new OasisCellRange(getName(), pBaseCell, myEnd);

        /* Declare to workbook */
        theOasisBook.applyDataFilter(myRange);
    }

    @Override
    public void createFreezePane(final CellPosition pFreezeCell) {
    }

    /**
     * Add columns to rows.
     */
    protected void addColumnsToRows() {
    }

    @Override
    public void setColumnHidden(final int pColIndex,
                                final boolean isHidden) {
        /* Obtain the column definition */
        Column myCol = theOasisSheet.getColumnByIndex(pColIndex);
        TableTableColumnElement myElement = myCol.getOdfElement();
        myElement.setTableVisibilityAttribute(isHidden
                ? TableVisibilityAttribute.Value.COLLAPSE.toString()
                : TableVisibilityAttribute.Value.VISIBLE.toString());
    }

    @Override
    public void setColumnWidth(final int pColIndex,
                               final int pWidth) {
        /* Obtain the column definition */
        Column myCol = theOasisSheet.getColumnByIndex(pColIndex);
        myCol.setWidth(pWidth << 1);
    }

    @Override
    public void setDefaultColumnStyle(final int pColIndex,
                                      final CellStyleType pStyle) {
        /* Obtain the column definition */
        Column myCol = getColumnByIndex(pColIndex);
        myCol.setDefaultCellStyle(theOasisBook.getCellStyle(pStyle));
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final OasisCell pCell,
                                final CellStyleType pStyle) {
        pCell.setCellStyle(theOasisBook.getStyleName(pStyle));
    }

    /**
     * Create a new TableTableColumnElement.
     * @return the new element
     */
    protected TableTableColumnElement newColumnElement() {
        return new TableTableColumnElement(theContentDom);
    }
}
