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

import java.util.ArrayList;
import java.util.List;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.table.TableNumberColumnsRepeatedAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.w3c.dom.Node;

/**
 * Class representing a list of cells in Oasis.
 * @author Tony Washer
 */
public class OasisCellMap {
    /**
     * Underlying row.
     */
    private final OasisRow theOasisRow;

    /**
     * Underlying sheet.
     */
    private final OasisSheet theOasisSheet;

    /**
     * Underlying table row element.
     */
    private final TableTableRowElement theOasisTableRow;

    /**
     * Number of cells.
     */
    private int theNumCells = 0;

    /**
     * The last cell.
     */
    private OasisCell theLastCell = null;

    /**
     * List of of cells.
     */
    private List<OasisCell> theCells = new ArrayList<OasisCell>();

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     */
    protected OasisCellMap(final OasisRow pRow) {
        /* Store parameters */
        theOasisRow = pRow;
        theOasisTableRow = pRow.getRowElement();
        theOasisSheet = null;

        /* Loop through the children of the table */
        processCellNode(theOasisTableRow);
    }

    /**
     * Obtain OasisRow.
     * @return the last row.
     */
    protected OasisRow getRow() {
        return theOasisRow;
    }

    /**
     * Obtain last cell.
     * @return the last cell.
     */
    protected OasisCell getLastCell() {
        return theLastCell;
    }

    /**
     * Obtain cell count.
     * @return the cell count
     */
    protected int getCellCount() {
        return theNumCells;
    }

    /**
     * Process cell node.
     * @param pNode the node to process
     */
    private void processCellNode(final Node pNode) {
        /* Loop through the children of the node */
        for (Node myNode = pNode.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a column element */
            if (myNode instanceof TableTableCellElement) {
                /* Add column to list */
                TableTableCellElement myColumn = (TableTableCellElement) myNode;
                processCell(myColumn);
            }
        }
    }

    /**
     * Process a cell.
     * @param pCell the cell to process
     */
    private void processCell(final TableTableCellElement pCell) {
        /* Determine the number of repeated columns */
        Integer myRepeat = pCell.getTableNumberColumnsRepeatedAttribute();
        if (myRepeat == null) {
            myRepeat = 1;
        }

        /* Loop through the repeated columns */
        for (int myInstance = 0; myInstance < myRepeat; myInstance++) {
            /* Create the instance and add to columns */
            OasisCell myCell = new OasisCell(this, theLastCell, pCell, theNumCells++, myInstance);
            theCells.add(myCell);
            theLastCell = myCell;
        }
    }

    /**
     * Obtain a cell by its index.
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    protected OasisCell getCellByIndex(final int pCellIndex) {
        /* Handle negative cell index */
        if (pCellIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pCellIndex >= theNumCells) {
            return null;
        }

        /* Just return the cell */
        return theCells.get(pCellIndex);
    }

    /**
     * Make the cell an individual cell.
     * @param pCell the cell
     */
    public void makeIndividual(final OasisCell pCell) {
        /* Access the cell element */
        TableTableCellElement myElement = pCell.getCellElement();
        int myInstance = pCell.getInstance();

        /* If there are prior elements to hive off */
        if (myInstance > 0) {
            /* Create a new cell element and add it before this one */
            TableTableCellElement myNew = theOasisSheet.newCellElement();
            theOasisTableRow.insertBefore(myNew, myElement);

            /* If there are multiple cells before the split */
            if (myInstance > 1) {
                /* Set the number of cells */
                myNew.setTableNumberColumnsRepeatedAttribute(myInstance);
            }

            /* Loop through the earlier cells */
            OasisCell myCell = pCell.getPreviousCell();
            while (myCell != null) {
                /* Map to new cell */
                myCell.setCellElement(myNew);
                myCell = myCell.getPreviousCell();
            }
        }

        /* Determine how many trailing elements to hive off */
        int myRepeatCount = pCell.getRepeatCount();
        int myNumCells = myRepeatCount
                         - myInstance
                         - 1;

        /* Clear the number of columns */
        myElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), TableNumberColumnsRepeatedAttribute.ATTRIBUTE_NAME.getLocalName());

        /* Set zero instance */
        pCell.setInstance(0);

        /* If we have trailing columns */
        if (myNumCells > 0) {
            /* Create a new cell element and add it before this one */
            TableTableCellElement myNew = theOasisSheet.newCellElement();
            theOasisTableRow.insertBefore(myNew, myElement);

            /* Adjust the repeat count for trailing elements */
            if (myNumCells > 1) {
                /* Set the number of columns */
                myElement.setTableNumberColumnsRepeatedAttribute(myNumCells);
            }

            /* Set the element for this column */
            pCell.setCellElement(myNew);

            /* Loop through the later cells */
            OasisCell myCell = pCell.getNextCell();
            for (int i = 0; i < myNumCells; i++) {
                /* Map to new column */
                myCell.setInstance(i);
                myCell = myCell.getNextCell();
            }
        }
    }
}
