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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisWorkBook.OasisStyle;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;

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
    public OasisRow getRowByIndex(final int pRowIndex) {
        /* Obtain row from row map */
        return theRowMap.getRowByIndex(pRowIndex);
    }

    @Override
    public OasisRow createRowByIndex(final int pRowIndex) {
        /* Obtain row from row map, creating row if necessary */
        return theRowMap.createRowByIndex(pRowIndex);
    }

    @Override
    public OasisColumn getColumnByIndex(final int pColIndex) {
        /* Obtain column from column map */
        return theColMap.getColumnByIndex(pColIndex);
    }

    @Override
    public OasisColumn createColumnByIndex(final int pColIndex) {
        /* Obtain column from column map, creating column if necessary */
        return theColMap.createColumnByIndex(pColIndex);
    }

    // @Override
    // protected OasisRow getRowByIndex(final DataView pView,
    // final int pRowIndex) {
    // /* Determine the actual index of the row */
    // int myIndex = pView.convertRowIndex(pRowIndex);
    // if (myIndex < 0) {
    // return null;
    // }
    //
    // /* Obtain from row map */
    // return theRowMap.getRowByIndex(myIndex);
    // }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(final boolean isHidden) {
        theOasisTable.setTableStyleNameAttribute(isHidden
                ? OasisWorkBook.getStyleName(OasisStyle.HiddenTable)
                : OasisWorkBook.getStyleName(OasisStyle.Table));
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
        theOasisBook.applyDataValidation(this, pFirstCell, pLastCell, pName);
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
        myRow.setTableStyleNameAttribute(OasisWorkBook.getStyleName(OasisStyle.Row));

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
