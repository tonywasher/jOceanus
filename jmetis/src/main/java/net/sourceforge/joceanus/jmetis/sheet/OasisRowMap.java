/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.table.TableNumberRowsRepeatedAttribute;
import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.w3c.dom.Node;

/**
 * Class to manage the rows for a Sheet in Oasis.
 * <p>
 * A simple array list is maintained to map from row number to the underlying element. Each such element may have a repeat count that means that multiple rows
 * map to the same element.
 * <p>
 * The map is sparsely populated at the end to avoid addressing unused rows. This situation can occur when the sheet is extended to the full 1048576 possible
 * rows, but with no active cells/rows. The map will initially only map up to the last TableTableRowElement regardless of the number of rows that this last
 * element represents. If rows are subsequently referenced past this point, then the map will be expanded as required, so that the row is included in the map.
 * <p>
 * If rows are referenced past those initially declared, then they will be automatically created if the {@link #getMutableRowByIndex} method is used. If the
 * {@link #getReadOnlyRowByIndex} method is used, then null will be returned to indicate that the row does not exist.
 */
public class OasisRowMap {
    /**
     * Underlying sheet.
     */
    private final OasisSheet theOasisSheet;

    /**
     * Number of rows.
     */
    private int theNumRows = 0;

    /**
     * Number of columns.
     */
    private int theNumCols;

    /**
     * The last reference.
     */
    private RowReference theLastReference = null;

    /**
     * List of of rows.
     */
    private List<RowReference> theRows = new ArrayList<RowReference>();

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     * @param pInitCols the initial number of columns
     */
    protected OasisRowMap(final OasisSheet pSheet,
                          final int pInitCols) {
        /* Store parameters */
        theOasisSheet = pSheet;
        theNumCols = pInitCols;

        /* Process the rows */
        processRowNode(pSheet.getTableElement());
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

        /* If we have references to extend */
        if (theRows.size() < theNumRows) {
            /* Ensure that references are fully extended */
            theLastReference.extendReferences(theNumRows);
        }

        /* Create the new reference and add it */
        RowReference myRef = new RowReference(pRow, theNumRows, 0);
        myRef.addToList();

        /* Adjust number of rows */
        theNumRows += myRepeat;
    }

    /**
     * Obtain a readOnly row by its index.
     * @param pRowIndex the index of the row.
     * @return the row
     */
    protected OasisRow getReadOnlyRowByIndex(final int pRowIndex) {
        /* Handle negative row index */
        if (pRowIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pRowIndex >= theNumRows) {
            return null;
        }

        /* If we have references to extend */
        if (theRows.size() <= pRowIndex) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pRowIndex);
        }

        /* Just return the row */
        RowReference myRef = theRows.get(pRowIndex);
        return myRef.getReadOnlyRow();
    }

    /**
     * Obtain a mutable row by its index, creating row if it does not exist.
     * @param pRowIndex the index of the row.
     * @return the row
     */
    protected OasisRow getMutableRowByIndex(final int pRowIndex) {
        /* Handle negative row index */
        if (pRowIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pRowIndex >= theNumRows) {
            /* Determine the number of extra rows required */
            int myXtraRows = pRowIndex
                             - theNumRows
                             + 1;

            /* Add additional rows */
            addAdditionalRows(myXtraRows);
        }

        /* If we have references to extend */
        if (theRows.size() <= pRowIndex) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pRowIndex);
        }

        /* Just return the row */
        RowReference myRef = theRows.get(pRowIndex);
        return myRef.getMutableRow();
    }

    /**
     * Add additional rows to table.
     * @param pXtraRows the number of rows to add.
     */
    protected void addAdditionalRows(final int pXtraRows) {
        /* If we have an existing reference that is empty */
        if ((theLastReference != null)
            && (isEmpty(theLastReference.getElement()))) {
            /* Obtain the last row */
            TableTableRowElement myElement = theLastReference.getElement();

            /* Determine the existing number of repeated rows */
            Integer myRepeat = theLastReference.getRepeat();

            /* Adjust the number of repeated rows */
            myRepeat += pXtraRows;
            myElement.setTableNumberRowsRepeatedAttribute(myRepeat);

            /* Adjust number of rows */
            theNumRows += pXtraRows;

            /* else we need to add a completely new element */
        } else {
            /* Create a new row */
            TableTableRowElement myElement = theOasisSheet.newRowElement(theNumCols);
            if (pXtraRows > 1) {
                /* Set repeat count */
                myElement.setTableNumberRowsRepeatedAttribute(pXtraRows);
            }

            /* Add the row after the lastRow */
            OasisWorkBook.addAsNextSibling(myElement, theLastReference.getElement());

            /* Process the element */
            processRow(myElement);
        }
    }

    /**
     * Add extra columns to rows.
     * @param pNumNewCols the number of new columns to add
     */
    protected void addColumnsToRows(final int pNumNewCols) {
        /* Adjust number of columns */
        theNumCols += pNumNewCols;

        /* Loop through the later rows */
        ListIterator<RowReference> myIterator = theRows.listIterator();
        while (myIterator.hasNext()) {
            /* Map to new instance */
            RowReference myRef = myIterator.next();

            /* Only deal with primary instances */
            if (myRef.theIndex == 0) {
                /* Add columns to the row */
                OasisRow myRow = myRef.getReadOnlyRow();
                myRow.addColumnsToRow(pNumNewCols);
            }
        }
    }

    /**
     * Is the Row element empty.
     * @param pElement the element to test
     * @return true/false
     */
    private static boolean isEmpty(final TableTableRowElement pElement) {
        /* If we have a child (should be a cell) */
        Node myChild = pElement.getFirstChild();
        if (myChild != null) {
            /* Must be an only child */
            if (myChild.getNextSibling() != null) {
                return false;
            }

            /* Must be an empty Cell Element */
            if ((!(myChild instanceof TableTableCellElement))
                || (!OasisCellMap.isEmpty((TableTableCellElement) myChild))) {
                return false;
            }
        }

        /* Access the data attributes */
        String defStyle = pElement.getTableDefaultCellStyleNameAttribute();
        String visible = pElement.getTableVisibilityAttribute();

        /* Empty if none of the data attributes exist */
        return (defStyle == null)
               && (visible.equals(TableVisibilityAttribute.DEFAULT_VALUE.toString()));
    }

    /**
     * Row Reference class.
     */
    private final class RowReference {
        /**
         * Row index.
         */
        private final int theIndex;

        /**
         * Row instance.
         */
        private int theInstance;

        /**
         * Row element.
         */
        private TableTableRowElement theElement;

        /**
         * Access Repeat count.
         * @return the repeat count
         */
        private int getRepeat() {
            /* Determine the maximum instance */
            Integer myRepeat = theElement.getTableNumberRowsRepeatedAttribute();
            return (myRepeat == null)
                    ? 1
                    : myRepeat;
        }

        /**
         * Access Row element.
         * @return the element
         */
        private TableTableRowElement getElement() {
            return theElement;
        }

        /**
         * Constructor.
         * @param pElement the row element
         * @param pIndex the row index
         * @param pInstance the instance of the element
         */
        private RowReference(final TableTableRowElement pElement,
                             final int pIndex,
                             final int pInstance) {
            /* Store parameters */
            theIndex = pIndex;
            theInstance = pInstance;
            theElement = pElement;
        }

        /**
         * Add to list.
         */
        private void addToList() {
            /* Add to the map */
            theRows.add(this);
            theLastReference = this;
        }

        /**
         * Extend row references.
         * @param pIndex the index to extend to
         */
        private void extendReferences(final int pIndex) {
            /* Loop through remaining instances */
            int myRepeat = getRepeat();
            for (int iInstance = theInstance + 1, iIndex = theIndex + 1; iInstance < myRepeat; iInstance++, iIndex++) {
                /* Break loop if we have extended far enough */
                if (iIndex > pIndex) {
                    break;
                }

                /* Create the new reference and add it */
                RowReference myRef = new RowReference(theElement, iIndex, iInstance);
                myRef.addToList();
            }
        }

        /**
         * Obtain Mutable Row representation.
         * @return the row representation
         */
        private OasisRow getMutableRow() {
            /* If we are asking for an editable item that is a repeated element */
            if (getRepeat() > 1) {
                /* Make this element individual */
                makeIndividual();
            }

            /* Create and return the row object */
            return new OasisRow(OasisRowMap.this, theElement, theIndex, false);
        }

        /**
         * Obtain ReadOnly Row representation.
         * @return the row representation
         */
        private OasisRow getReadOnlyRow() {
            /* Create and return the row object */
            return new OasisRow(OasisRowMap.this, theElement, theIndex, true);
        }

        /**
         * Make the row an individual row.
         */
        private void makeIndividual() {
            /* Determine how many trailing elements to hive off */
            int myRepeatCount = getRepeat();
            int myNumRows = myRepeatCount
                            - theInstance
                            - 1;

            /* If we have trailing rows */
            if (myNumRows > 0) {
                /* Make sure we reference the following row */
                theLastReference.extendReferences(theIndex + 1);
            }

            /* Clear the number of row */
            theElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), TableNumberRowsRepeatedAttribute.ATTRIBUTE_NAME.getLocalName());

            /* If there are prior elements to hive off */
            if (theInstance > 0) {
                /* Create a new row element for this instance, and append after this one */
                TableTableRowElement myNew = theOasisSheet.newRowElement(theNumCols);
                OasisWorkBook.addAsNextSibling(myNew, theElement);

                /* If there are multiple rows before the split */
                if (theInstance > 1) {
                    /* Set the number of rows */
                    theElement.setTableNumberRowsRepeatedAttribute(theInstance);
                }

                /* Store as the element */
                theElement = myNew;
            }

            /* Set zero instance */
            theInstance = 0;

            /* If we have trailing rows */
            if (myNumRows > 0) {
                /* Create a new row element and add it before this one */
                TableTableRowElement myNew = theOasisSheet.newRowElement(theNumCols);
                OasisWorkBook.addAsNextSibling(myNew, theElement);

                /* Adjust the repeat count for trailing elements */
                if (myNumRows > 1) {
                    /* Set the number of rows */
                    myNew.setTableNumberRowsRepeatedAttribute(myNumRows);
                }

                /* Loop through the later rows */
                ListIterator<RowReference> myIterator = theRows.listIterator(theIndex + 1);
                for (int i = 0; myIterator.hasNext()
                                && (i < myNumRows); i++) {
                    /* Map to new instance */
                    RowReference myRef = myIterator.next();
                    myRef.theElement = myNew;
                    myRef.theInstance = i;
                }
            }
        }
    }
}
