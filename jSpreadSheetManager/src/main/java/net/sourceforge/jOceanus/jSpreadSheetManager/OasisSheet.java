/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;

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
     * The Row Map.
     */
    private OasisRowMap theRowMap;

    /**
     * The Column Map.
     */
    private OasisColumnMap theColMap;

    /**
     * Is the sheet readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor for Oasis Sheet.
     * @param pBook the WorkBook
     * @param pTable the Oasis table
     * @param pIndex the index of the sheet
     * @param pReadOnly is the sheet readOnly?
     */
    protected OasisSheet(final OasisWorkBook pBook,
                         final TableTableElement pTable,
                         final int pIndex,
                         final boolean pReadOnly) {
        /* Construct super-class */
        super(pTable.getTableNameAttribute());

        /* Store parameters */
        theOasisBook = pBook;
        theContentDom = pBook.getContentDom();
        theIndex = pIndex;
        theOasisTable = pTable;
        isReadOnly = pReadOnly;

        /* Create the maps */
        theColMap = new OasisColumnMap(this);
        theRowMap = new OasisRowMap(this, theColMap.getColumnCount());
    }

    /**
     * Obtain formatter.
     * @return the formatter.
     */
    protected JDataFormatter getFormatter() {
        return theOasisBook.getFormatter();
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
        return theRowMap.getRowCount();
    }

    @Override
    public OasisRow getReadOnlyRowByIndex(final int pRowIndex) {
        /* Obtain row from row map */
        return theRowMap.getReadOnlyRowByIndex(pRowIndex);
    }

    @Override
    public OasisRow getMutableRowByIndex(final int pRowIndex) {
        /* Obtain row from row map, creating row if necessary */
        return (isReadOnly)
                ? null
                : theRowMap.getMutableRowByIndex(pRowIndex);
    }

    @Override
    public OasisColumn getReadOnlyColumnByIndex(final int pColIndex) {
        /* Obtain column from column map */
        return theColMap.getReadOnlyColumnByIndex(pColIndex);
    }

    @Override
    public OasisColumn getMutableColumnByIndex(final int pColIndex) {
        /* Obtain column from column map, creating column if necessary */
        return (isReadOnly)
                ? null
                : theColMap.getMutableColumnByIndex(pColIndex);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly) {
            theOasisTable.setTableStyleNameAttribute(isHidden
                    ? OasisWorkBook.STYLE_HIDDENTABLE
                    : OasisWorkBook.STYLE_TABLE);
        }
    }

    /**
     * Set the column style for the column.
     * @param pColumn the column
     * @param pStyle the style
     */
    protected void setColumnStyle(final TableTableColumnElement pColumn,
                                  final CellStyleType pStyle) {
        pColumn.setTableStyleNameAttribute(OasisWorkBook.getColumnStyleName(pStyle));
    }

    /**
     * Set the default style for the column.
     * @param pColumn the column index
     * @param pStyle the style
     */
    protected void setDefaultCellStyle(final TableTableColumnElement pColumn,
                                       final CellStyleType pStyle) {
        OdfStyle myStyle = theOasisBook.getCellStyle(pStyle);
        pColumn.setTableDefaultCellStyleNameAttribute(myStyle.getStyleNameAttribute());
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setCellStyle(final TableTableCellElement pCell,
                                final Object pValue) {
        OdfStyle myStyle = theOasisBook.getCellStyle(pValue);
        pCell.setTableStyleNameAttribute(myStyle.getStyleNameAttribute());
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setAlternateCellStyle(final TableTableCellElement pCell,
                                         final Object pValue) {
        OdfStyle myStyle = theOasisBook.getAlternateCellStyle(pValue);
        pCell.setTableStyleNameAttribute(myStyle.getStyleNameAttribute());
    }

    @Override
    public void declareRange(final String pName,
                             final CellPosition pFirstCell,
                             final CellPosition pLastCell) throws JDataException {
        if (!isReadOnly) {
            /* Build the range */
            OasisCellRange myRange = new OasisCellRange(getName(), pFirstCell, pLastCell);

            /* Declare to workBook */
            theOasisBook.declareRange(pName, myRange);
        }
    }

    @Override
    public void applyDataValidation(final CellPosition pFirstCell,
                                    final CellPosition pLastCell,
                                    final String pName) throws JDataException {
        if (!isReadOnly) {
            /* Declare to workBook */
            theOasisBook.applyDataValidation(this, pFirstCell, pLastCell, pName);
        }
    }

    @Override
    public void applyDataFilter(final CellPosition pBaseCell,
                                final int pNumRows) throws JDataException {
        if (!isReadOnly) {
            /* Build the range */
            CellPosition myEnd = new CellPosition(pBaseCell.getColumnIndex(), pNumRows - 1);
            OasisCellRange myRange = new OasisCellRange(getName(), pBaseCell, myEnd);

            /* Declare to workbook */
            theOasisBook.applyDataFilter(myRange);
        }
    }

    @Override
    public void createFreezePane(final CellPosition pFreezeCell) {
    }

    /**
     * Add columns to rows.
     * @param pNumNewCols number of new columns to add
     */
    protected void addColumnsToRows(final int pNumNewCols) {
        /* pass call to rows */
        theRowMap.addColumnsToRows(pNumNewCols);
    }

    /**
     * Create a new TableTableColumnElement.
     * @return the new element
     */
    protected TableTableColumnElement newColumnElement() {
        return new TableTableColumnElement(theContentDom);
    }

    /**
     * Create a new TableTableRowElement.
     * @param pNumCols the number of columns for the row
     * @return the new element
     */
    protected TableTableRowElement newRowElement(final int pNumCols) {
        /* Allocate the row */
        TableTableRowElement myRow = new TableTableRowElement(theContentDom);
        myRow.setTableStyleNameAttribute(OasisWorkBook.STYLE_ROW);

        /* Allocate a cell for the row */
        TableTableCellElement myCell = new TableTableCellElement(theContentDom);
        myRow.appendChild(myCell);

        /* Handle repeat count */
        if (pNumCols > 1) {
            myCell.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Return the row */
        return myRow;
    }

    /**
     * Create a new TableTableCellElement.
     * @return the new element
     */
    protected TableTableCellElement newCellElement() {
        return new TableTableCellElement(theContentDom);
    }
}
