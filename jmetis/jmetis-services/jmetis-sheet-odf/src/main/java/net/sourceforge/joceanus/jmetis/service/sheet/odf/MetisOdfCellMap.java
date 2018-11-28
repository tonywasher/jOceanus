/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class representing a list of cells in Oasis.
 * <p>
 * A simple array list is maintained to map from cell number to the underlying element. Each such
 * element may have a repeat count that means that multiple cells map to the same element.
 * <p>
 * The map is sparsely populated at the end to avoid addressing unused cells. This situation can
 * occur when the sheet is extended to the full 1024 possible columns, but with no active cells. The
 * map will initially only map up to the last TableTableCellElement regardless of the number of
 * cells that this last element represents. If cells are subsequently referenced past this point,
 * then the map will be expanded as required, so that the cell is included in the map.
 * <p>
 * If cells are referenced past those initially declared, then null will be returned to indicate
 * that the cell does not exist. In addition, if the {@link #getReadOnlyCellByIndex} method is used
 * for a cell that is empty, then null will be returned to indicate that the cell is empty.
 */
public class MetisOdfCellMap {
    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * Underlying row.
     */
    private final MetisOdfRow theOasisRow;

    /**
     * Underlying sheet.
     */
    private final MetisOdfSheet theOasisSheet;

    /**
     * Number of cells.
     */
    private int theNumCells;

    /**
     * The last reference.
     */
    private CellReference theLastReference;

    /**
     * List of of cells.
     */
    private List<CellReference> theCells = new ArrayList<>();

    /**
     * Constructor.
     * @param pRow the underlying row.
     */
    MetisOdfCellMap(final MetisOdfRow pRow) {
        /* Store parameters */
        theOasisRow = pRow;
        theOasisSheet = pRow.getSheet();
        theParser = theOasisSheet.getParser();

        /* Loop through the children of the row */
        processCellNode(pRow.getRowElement());
    }

    /**
     * Obtain OasisRow.
     * @return the last row.
     */
    protected MetisOdfRow getRow() {
        return theOasisRow;
    }

    /**
     * Obtain cell count.
     * @return the cell count
     */
    int getCellCount() {
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
            if (theParser.isElementOfType(myNode, MetisOdfTableItem.CELL)) {
                /* Add column to list */
                processCell((Element) myNode);
            }
        }
    }

    /**
     * Process a cell.
     * @param pCell the cell to process
     */
    private void processCell(final Element pCell) {
        /* Determine the number of repeated columns */
        final String myRepeatStr = theParser.getAttribute(pCell, MetisOdfTableItem.COLUMNREPEAT);
        final Integer myRepeat = myRepeatStr == null
                                 ? 1
                                 : Integer.parseInt(myRepeatStr);

        /* If we have references to extend */
        if (theCells.size() < theNumCells) {
            /* Ensure that references are fully extended */
            theLastReference.extendReferences(theNumCells);
        }

        /* Create the new reference and add it */
        final CellReference myRef = new CellReference(pCell, theNumCells, 0);
        myRef.addToList();

        /* Adjust number of cells */
        theNumCells += myRepeat;
    }

    /**
     * Obtain a readOnly cell by its index.
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    MetisOdfCell getReadOnlyCellByIndex(final int pCellIndex) {
        /* Handle index out of range */
        if (pCellIndex < 0
                || pCellIndex >= theNumCells) {
            return null;
        }

        /* If we have references to extend */
        if (theCells.size() <= pCellIndex) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pCellIndex);
        }

        /* Just return the cell */
        final CellReference myRef = theCells.get(pCellIndex);
        return myRef.isDataEmpty()
               ? null
               : myRef.getReadOnlyCell();
    }

    /**
     * Obtain a mutable cell by its index.
     * @param pCellIndex the index of the cell.
     * @return the column
     */
    MetisOdfCell getMutableCellByIndex(final int pCellIndex) {
        /* Handle index out of range */
        if (pCellIndex < 0
                || pCellIndex >= theNumCells) {
            return null;
        }

        /* If we have references to extend */
        if (theCells.size() <= pCellIndex) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pCellIndex);
        }

        /* Just return the cell */
        final CellReference myRef = theCells.get(pCellIndex);
        return myRef.getMutableCell();
    }

    /**
     * Add additional cells to table.
     * @param pXtraCells the number of rows to add.
     */
    void addAdditionalCells(final int pXtraCells) {
        /* If we have an existing reference that is empty */
        if (isEmpty(theParser, theLastReference.getElement())) {
            /* Obtain the last cell */
            final Element myElement = theLastReference.getElement();

            /* Determine the existing number of repeated cells */
            int myRepeat = theLastReference.getRepeat();

            /* Adjust the number of repeated cells */
            myRepeat += pXtraCells;
            theParser.setAttribute(myElement, MetisOdfTableItem.COLUMNREPEAT, myRepeat);

            /* Adjust number of cells */
            theNumCells += pXtraCells;

            /* else we need to add a completely new element */
        } else {
            /* Create a new cell */
            final Element myElement = theOasisSheet.newCellElement();
            if (pXtraCells > 1) {
                /* Set repeat count */
                theParser.setAttribute(myElement, MetisOdfTableItem.COLUMNREPEAT, pXtraCells);
            }

            /* Add the row after the lastCell */
            MetisOdfParser.addAsNextSibling(myElement, theLastReference.getElement());

            /* Process the element */
            processCell(myElement);
        }
    }

    /**
     * Is the Cell element empty.
     * @param pParser the parser
     * @param pElement the element to test
     * @return true/false
     */
    static boolean isEmpty(final MetisOdfParser pParser,
                           final Element pElement) {
        /* Access the data attributes */
        final boolean hasChildren = pElement.hasChildNodes();
        final String valType = pParser.getAttribute(pElement, MetisOdfOfficeItem.VALUETYPE);
        final String style = pParser.getAttribute(pElement, MetisOdfTableItem.STYLENAME);
        final String valid = pParser.getAttribute(pElement, MetisOdfTableItem.VALIDATIONNAME);

        /* Empty if none of the data attributes exist */
        return valType == null
                && valid == null
                && !hasChildren
                && style == null;
    }

    /**
     * Cell Reference class.
     */
    private final class CellReference {
        /**
         * Cell index.
         */
        private final int theIndex;

        /**
         * Cell instance.
         */
        private int theInstance;

        /**
         * Cell element.
         */
        private Element theElement;

        /**
         * Constructor.
         * @param pElement the cell element
         * @param pIndex the cell index
         * @param pInstance the instance of the element
         */
        private CellReference(final Element pElement,
                              final int pIndex,
                              final int pInstance) {
            /* Store parameters */
            theIndex = pIndex;
            theInstance = pInstance;
            theElement = pElement;
        }

        /**
         * Access Repeat count.
         * @return the repeat count
         */
        private int getRepeat() {
            /* Determine the maximum instance */
            final Integer myRepeat = theParser.getIntegerAttribute(theElement, MetisOdfTableItem.COLUMNREPEAT);
            return myRepeat == null
                   ? 1
                   : myRepeat;
        }

        /**
         * Access Cell element.
         * @return the element
         */
        private Element getElement() {
            return theElement;
        }

        /**
         * Add to list.
         */
        private void addToList() {
            /* Add to the map */
            theCells.add(this);
            theLastReference = this;
        }

        /**
         * Is the Cell element empty.
         * @return true/false
         */
        private boolean isDataEmpty() {
            /* Access the data attributes */
            final boolean hasChildren = theElement.hasChildNodes();
            final String valType = theParser.getAttribute(theElement, MetisOdfOfficeItem.VALUETYPE);

            /* Empty if none of the data attributes exist */
            return valType == null
                    && !hasChildren;
        }

        /**
         * Extend cell references.
         * @param pIndex the index to extend to
         */
        private void extendReferences(final int pIndex) {
            /* Loop through remaining instances */
            final int myRepeat = getRepeat();
            for (int iInstance = theInstance + 1, iIndex = theIndex + 1; iInstance < myRepeat; iInstance++, iIndex++) {
                /* Break loop if we have extended far enough */
                if (iIndex > pIndex) {
                    break;
                }

                /* Create the new reference and add it */
                final CellReference myRef = new CellReference(theElement, iIndex, iInstance);
                myRef.addToList();
            }
        }

        /**
         * Obtain Cell representation.
         * @return the cell representation
         */
        private MetisOdfCell getMutableCell() {
            /* If we are asking for an editable item that is a repeated element */
            if (getRepeat() > 1) {
                /* Make this element individual */
                makeIndividual();
            }

            /* Create and return the cell object */
            return new MetisOdfCell(MetisOdfCellMap.this, theElement, theIndex, false);
        }

        /**
         * Obtain ReadOnly Cell representation.
         * @return the cell representation
         */
        private MetisOdfCell getReadOnlyCell() {
            /* Create and return the cell object */
            return new MetisOdfCell(MetisOdfCellMap.this, theElement, theIndex, true);
        }

        /**
         * Make the column an individual column.
         */
        private void makeIndividual() {
            /* Determine how many trailing elements to hive off */
            final int myRepeatCount = getRepeat();
            final int myNumCells = myRepeatCount
                    - theInstance
                    - 1;

            /* If we have trailing cells */
            if (myNumCells > 0) {
                /* Make sure we reference the following cell */
                theLastReference.extendReferences(theIndex + 1);
            }

            /* Clear the number of cells */
            theParser.removeAttribute(theElement, MetisOdfTableItem.COLUMNREPEAT);

            /* If there are prior elements to hive off */
            if (theInstance > 0) {
                /* Create a new cell element for this instance, and append after this one */
                final Element myNew = theOasisSheet.newCellElement();
                MetisOdfParser.addAsNextSibling(myNew, theElement);

                /* If there are multiple cells before the split */
                if (theInstance > 1) {
                    /* Set the number of cells */
                    theParser.setAttribute(theElement, MetisOdfTableItem.COLUMNREPEAT, theInstance);
                }

                /* Store as the element */
                theElement = myNew;
            }

            /* Set zero instance */
            theInstance = 0;

            /* If we have trailing cells */
            if (myNumCells > 0) {
                /* Create a new cell element and add it before this one */
                final Element myNew = theOasisSheet.newCellElement();
                MetisOdfParser.addAsNextSibling(myNew, theElement);

                /* Adjust the repeat count for trailing elements */
                if (myNumCells > 1) {
                    /* Set the number of columns */
                    theParser.setAttribute(myNew, MetisOdfTableItem.COLUMNREPEAT, myNumCells);
                }

                /* Loop through the later columns */
                final ListIterator<CellReference> myIterator = theCells.listIterator(theIndex + 1);
                for (int i = 0; myIterator.hasNext()
                        && (i < myNumCells); i++) {
                    /* Map to new instance */
                    final CellReference myRef = myIterator.next();
                    myRef.theElement = myNew;
                    myRef.theInstance = i;
                }
            }
        }
    }
}
