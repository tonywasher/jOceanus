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

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.table.TableNumberRowsRepeatedAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.w3c.dom.Node;

/**
 * Class representing a list of rows in Oasis.
 * @author Tony Washer
 */
public class OasisRowMap {
    /**
     * Underlying sheet.
     */
    private final OasisSheet theOasisSheet;

    /**
     * Underlying table element.
     */
    private final TableTableElement theOasisTable;

    /**
     * Number of rows.
     */
    private int theNumRows = 0;

    /**
     * Number of columns.
     */
    private int theNumCols;

    /**
     * The last column.
     */
    private OasisRow theLastRow = null;

    /**
     * List of of rows.
     */
    private List<OasisRow> theRows = new ArrayList<OasisRow>();

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     * @param pInitCols the initial number of columns
     */
    protected OasisRowMap(final OasisSheet pSheet,
                          final int pInitCols) {
        /* Store parameters */
        theOasisSheet = pSheet;
        theOasisTable = pSheet.getTableElement();
        theNumCols = pInitCols;

        /* Process the rows */
        processRowNode(theOasisTable);
    }

    /**
     * Obtain OasisSheet.
     * @return the last row.
     */
    protected OasisSheet getSheet() {
        return theOasisSheet;
    }

    /**
     * Obtain formatter.
     * @return the formatter.
     */
    protected JDataFormatter getFormatter() {
        return theOasisSheet.getFormatter();
    }

    /**
     * Obtain last row.
     * @return the last row.
     */
    protected OasisRow getLastRow() {
        return theLastRow;
    }

    /**
     * Obtain row count.
     * @return the row count
     */
    protected int getRowCount() {
        return theNumRows;
    }

    /**
     * Process row node.
     * @param pNode the node to process
     */
    private void processRowNode(final Node pNode) {
        /* Loop through the children of the node */
        for (Node myNode = pNode.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a row element */
            if (myNode instanceof TableTableRowElement) {
                /* Add column to list */
                TableTableRowElement myRow = (TableTableRowElement) myNode;
                processRow(myRow);

                /* If this is a node that contains columns */
            } else if ((myNode instanceof TableTableHeaderRowsElement)
                       || (myNode instanceof TableTableRowGroupElement)
                       || (myNode instanceof TableTableRowsElement)) {
                /* Process nodes */
                processRowNode(myNode);
            }
        }
    }

    /**
     * Process a row.
     * @param pRow the row to process
     */
    private void processRow(final TableTableRowElement pRow) {
        /* Determine the number of repeated rows */
        Integer myRepeat = pRow.getTableNumberRowsRepeatedAttribute();
        if (myRepeat == null) {
            myRepeat = 1;
        }

        /* Loop through the repeated rows */
        for (int myInstance = 0; myInstance < myRepeat; myInstance++) {
            /* Create the instance and add to columns */
            OasisRow myCol = new OasisRow(this, theLastRow, pRow, theNumRows++, myInstance);
            theRows.add(myCol);
            theLastRow = myCol;
        }
    }

    /**
     * Obtain a row by its index.
     * @param pRowIndex the index of the row.
     * @return the row
     */
    protected OasisRow getRowByIndex(final int pRowIndex) {
        /* Handle negative row index */
        if (pRowIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pRowIndex >= theNumRows) {
            return null;
        }

        /* Just return the row */
        return theRows.get(pRowIndex);
    }

    /**
     * Obtain a row by its index.
     * @param pRowIndex the index of the row.
     * @return the row
     */
    protected OasisRow createRowByIndex(final int pRowIndex) {
        /* Handle negative row index */
        if (pRowIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pRowIndex >= theNumRows) {
            /* Declare variables */
            TableTableRowElement myElement;

            /* Determine the number of extra rows required */
            int myXtraRows = pRowIndex
                             - theNumRows
                             + 1;

            /* If we have existing rows */
            if (theLastRow != null) {
                /* Obtain the last row */
                myElement = theLastRow.getRowElement();

                /* Determine the existing number of repeated rows */
                Integer myRepeat = theLastRow.getRepeatCount();

                /* If we have a repeated item */
                if (myRepeat > 1) {
                    /* Loop through the additional rows */
                    for (int myInstance = 0; myInstance < myXtraRows; myInstance++) {
                        /* Create the instance and add to rows */
                        OasisRow myRow = new OasisRow(this, theLastRow, myElement, theNumRows++, myRepeat
                                                                                                 + myInstance);
                        theRows.add(myRow);
                        theLastRow = myRow;
                    }

                    /* Adjust the number of repeated rows */
                    myRepeat += myXtraRows;
                    myElement.setTableNumberRowsRepeatedAttribute(myRepeat);

                    /* Return the required row */
                    return theLastRow;
                }
            }

            /* Create a new row */
            myElement = theOasisTable.newTableTableRowElement();
            if (myXtraRows > 1) {
                /* Set repeat count */
                myElement.setTableNumberRowsRepeatedAttribute(myXtraRows);
            }

            /* Process the element */
            processRow(myElement);

            /* Return the required row */
            return theLastRow;
        }

        /* Just return the row */
        return theRows.get(pRowIndex);
    }

    /**
     * Add extra columns to rows.
     * @param pNumNewCols the number of new columns to add
     */
    public void addColumnsToRows(final int pNumNewCols) {
        /* Adjust number of columns */
        theNumCols += pNumNewCols;
    }

    /**
     * Make the row an individual row.
     * @param pRow the row
     */
    public void makeIndividual(final OasisRow pRow) {
        /* Access the row element */
        TableTableRowElement myElement = pRow.getRowElement();
        int myInstance = pRow.getInstance();

        /* If there are prior elements to hive off */
        if (myInstance > 0) {
            /* Create a new row element and add it before this one */
            TableTableRowElement myNew = theOasisSheet.newRowElement(theNumCols);
            theOasisTable.insertBefore(myNew, myElement);

            /* If there are multiple rows before the split */
            if (myInstance > 1) {
                /* Set the number of rows */
                myNew.setTableNumberRowsRepeatedAttribute(myInstance);
            }

            /* Loop through the earlier rows */
            OasisRow myRow = pRow.getPreviousRow();
            while (myRow != null) {
                /* Map to new row */
                myRow.setRowElement(myNew);

                /* Break loop if this is not a virtual instance */
                if (!myRow.isVirtual()) {
                    break;
                }

                /* Move to previous row */
                myRow = myRow.getPreviousRow();
            }
        }

        /* Determine how many trailing elements to hive off */
        int myRepeatCount = pRow.getRepeatCount();
        int myNumRows = myRepeatCount
                        - myInstance
                        - 1;

        /* Clear the number of rows */
        myElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), TableNumberRowsRepeatedAttribute.ATTRIBUTE_NAME.getLocalName());

        /* Set zero instance */
        pRow.setInstance(0);

        /* If we have trailing rows */
        if (myNumRows > 0) {
            /* Create a new row element and add it before this one */
            TableTableRowElement myNew = theOasisSheet.newRowElement(theNumCols);
            theOasisTable.insertBefore(myNew, myElement);

            /* Adjust the repeat count for trailing elements */
            if (myNumRows > 1) {
                /* Set the number of columns */
                myElement.setTableNumberRowsRepeatedAttribute(myNumRows);
            }

            /* Set the element for this row */
            pRow.setRowElement(myNew);

            /* Loop through the later rows */
            OasisRow myRow = pRow.getNextRow();
            for (int i = 0; i < myNumRows; i++) {
                /* Map to new column */
                myRow.setInstance(i);
                myRow = myRow.getNextRow();
            }
        }
    }
}
