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
 * Class to manage the columns for a Sheet in Oasis.
 * <p>
 * A simple array list is maintained to map from column number to the underlying element. Each such
 * element may have a repeat count that means that multiple columns map to the same element.
 * <p>
 * The map is sparsely populated at the end to avoid addressing unused columns. This situation can
 * occur when the sheet is extended to the full 1024 possible columns, but with no active
 * cells/columns. The map will initially only map up to the last TableTableColumnElement regardless
 * of the number of columns that this last element represents. If columns are subsequently
 * referenced past this point, then the map will be expanded as required, so that the column is
 * included in the map.
 * <p>
 * If columns are referenced past those initially declared, then they will be automatically created
 * if the {@link #getMutableColumnByIndex} method is used. If the {@link #getReadOnlyColumnByIndex}
 * method is used, then null will be returned to indicate that the column does not exist.
 */
public class MetisOdfColumnMap {
    /**
     * Underlying sheet.
     */
    private final MetisOdfSheet theOasisSheet;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * Number of columns.
     */
    private int theNumColumns;

    /**
     * The last reference.
     */
    private ColumnReference theLastReference;

    /**
     * List of of columns.
     */
    private final List<ColumnReference> theColumns = new ArrayList<>();

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     */
    MetisOdfColumnMap(final MetisOdfSheet pSheet) {
        /* Store parameters */
        theOasisSheet = pSheet;
        theParser = pSheet.getParser();

        /* Process the columns */
        processColumnNode(pSheet.getTableElement());
    }

    /**
     * Obtain OasisSheet.
     * @return the last row.
     */
    protected MetisOdfSheet getSheet() {
        return theOasisSheet;
    }

    /**
     * Obtain column count.
     * @return the column count
     */
    int getColumnCount() {
        return theNumColumns;
    }

    /**
     * Process column node.
     * @param pNode the node to process
     */
    private void processColumnNode(final Node pNode) {
        /* Loop through the children of the node */
        for (Node myNode = pNode.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a column element */
            if (theParser.isElementOfType(myNode, MetisOdfTableItem.COLUMN)) {
                /* Add column to list */
                processColumn((Element) myNode);

                /* If this is a node that contains columns */
            } else if (theParser.isElementOfType(myNode, MetisOdfTableItem.COLUMNGROUP)
                    || theParser.isElementOfType(myNode, MetisOdfTableItem.HDRCOLUMNS)
                    || theParser.isElementOfType(myNode, MetisOdfTableItem.COLUMNS)) {
                /* Process nodes */
                processColumnNode(myNode);
            }
        }
    }

    /**
     * Process a column node.
     * @param pColumn the column node to process
     */
    private void processColumn(final Element pColumn) {
        /* Determine the number of repeated columns */
        final String myRepeatStr = theParser.getAttribute(pColumn, MetisOdfTableItem.COLUMNREPEAT);
        final Integer myRepeat = myRepeatStr == null
                                 ? 1
                                 : Integer.parseInt(myRepeatStr);

        /* If we have references to extend */
        if (theColumns.size() < theNumColumns) {
            /* Ensure that references are fully extended */
            theLastReference.extendReferences(theNumColumns);
        }

        /* Create the new reference and add it */
        final ColumnReference myRef = new ColumnReference(pColumn, theNumColumns, 0);
        myRef.addToList();

        /* Adjust number of columns */
        theNumColumns += myRepeat;
    }

    /**
     * Obtain a readOnly column by its index.
     * @param pColIndex the index of the column.
     * @return the column if it exists, else null
     */
    MetisOdfColumn getReadOnlyColumnByIndex(final int pColIndex) {
        /* Handle negative column index */
        if (pColIndex < 0) {
            return null;
        }

        /* Handle beyond table limits */
        if (pColIndex >= theNumColumns) {
            return null;
        }

        /* If we have references to extend */
        if (theColumns.size() <= pColIndex) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pColIndex);
        }

        /* Just return the column */
        final ColumnReference myRef = theColumns.get(pColIndex);
        return myRef.getReadOnlyColumn();
    }

    /**
     * Obtain a mutable column by its index, creating column if it does not exist.
     * @param pColIndex the index of the column.
     * @return the column
     */
    MetisOdfColumn getMutableColumnByIndex(final int pColIndex) {
        /* Handle negative column index */
        if (pColIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pColIndex >= theNumColumns) {
            /* Determine the number of extra columns required */
            final int myXtraCols = pColIndex
                    - theNumColumns
                    + 1;

            /* Add additional columns */
            addAdditionalColumns(myXtraCols);
        }

        /* If we have references to extend */
        if (theColumns.size() <= pColIndex) {
            /* Ensure that references are sufficiently extended */
            theLastReference.extendReferences(pColIndex);
        }

        /* Return the required column */
        final ColumnReference myRef = theColumns.get(pColIndex);
        return myRef.getMutableColumn();
    }

    /**
     * Add additional columns to table.
     * @param pXtraCols the number of columns to add.
     */
    private void addAdditionalColumns(final int pXtraCols) {
        /* If we have an existing reference that is empty */
        if (isEmpty(theLastReference.getElement())) {
            /* Obtain the last column */
            final Element myElement = theLastReference.getElement();

            /* Determine the existing number of repeated columns */
            int myRepeat = theLastReference.getRepeat();

            /* Adjust the number of repeated columns */
            myRepeat += pXtraCols;
            theParser.setAttribute(myElement, MetisOdfTableItem.COLUMNREPEAT, myRepeat);

            /* Adjust number of columns */
            theNumColumns += pXtraCols;

            /* else we need to add a completely new element */
        } else {
            /* Create a new column */
            final Element myElement = theOasisSheet.newColumnElement();
            if (pXtraCols > 1) {
                /* Set repeat count */
                theParser.setAttribute(myElement, MetisOdfTableItem.COLUMNREPEAT, pXtraCols);
            }

            /* Add the column after the lastColumn */
            MetisOdfParser.addAsNextSibling(myElement, theLastReference.getElement());

            /* Process the element */
            processColumn(myElement);
        }

        /* Report addition of columns */
        theOasisSheet.addColumnsToRows(pXtraCols);
    }

    /**
     * Is the Column element empty.
      * @param pElement the element to test
     * @return true/false
     */
    private boolean isEmpty(final Element pElement) {
        /* Access the data attributes */
        final String defStyle = theParser.getAttribute(pElement, MetisOdfTableItem.DEFAULTCELLSTYLE);
        final String style = theParser.getAttribute(pElement, MetisOdfTableItem.STYLENAME);
        final String visible = theParser.getAttribute(pElement, MetisOdfTableItem.VISIBILITY);

        /* Empty if none of the data attributes exist */
        return defStyle == null
                && style == null
                && (visible == null
                || visible.equals(MetisOdfValue.VISIBLE.getValue()));
    }

    /**
     * Column Reference class.
     */
    private final class ColumnReference {
        /**
         * Column index.
         */
        private final int theIndex;

        /**
         * Column instance.
         */
        private int theInstance;

        /**
         * Column element.
         */
        private Element theElement;

        /**
         * Constructor.
         * @param pElement the column element
         * @param pIndex the column index
         * @param pInstance the instance of the element
         */
        private ColumnReference(final Element pElement,
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
         * Access Column element.
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
            theColumns.add(this);
            theLastReference = this;
        }

        /**
         * Extend column references.
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
                final ColumnReference myRef = new ColumnReference(theElement, iIndex, iInstance);
                myRef.addToList();
            }
        }

        /**
         * Obtain Mutable column representation.
         * @return the mutable column representation
         */
        private MetisOdfColumn getMutableColumn() {
            /* If we are asking for an editable item that is a repeated element */
            if (getRepeat() > 1) {
                /* Make this element individual */
                makeIndividual();
            }

            /* Create and return the column object */
            return new MetisOdfColumn(MetisOdfColumnMap.this, theElement, theIndex, false);
        }

        /**
         * Obtain ReadOnly Column representation.
         * @return the column representation
         */
        private MetisOdfColumn getReadOnlyColumn() {
            /* Create and return the column object */
            return new MetisOdfColumn(MetisOdfColumnMap.this, theElement, theIndex, true);
        }

        /**
         * Make the column an individual column.
         */
        private void makeIndividual() {
            /* Determine how many trailing elements to hive off */
            final int myRepeatCount = getRepeat();
            final int myNumCols = myRepeatCount
                    - theInstance
                    - 1;

            /* If we have trailing columns */
            if (myNumCols > 0) {
                /* Make sure we reference the following column */
                theLastReference.extendReferences(theIndex + 1);
            }

            /* Clear the number of columns */
            theParser.removeAttribute(theElement, MetisOdfTableItem.COLUMNREPEAT);

            /* If there are prior elements to hive off */
            if (theInstance > 0) {
                /* Create a new column element for this instance, and append after this one */
                final Element myNew = theOasisSheet.newColumnElement();
                MetisOdfParser.addAsNextSibling(myNew, theElement);

                /* If there are multiple columns before the split */
                if (theInstance > 1) {
                    /* Set the number of columns */
                    theParser.setAttribute(theElement, MetisOdfTableItem.COLUMNREPEAT, theInstance);
                }

                /* Store as the element */
                theElement = myNew;
            }

            /* Set zero instance */
            theInstance = 0;

            /* If we have trailing columns */
            if (myNumCols > 0) {
                /* Make sure we reference the following column */
                extendReferences(theIndex + 1);

                /* Create a new column element and add it after this one */
                final Element myNew = theOasisSheet.newColumnElement();
                MetisOdfParser.addAsNextSibling(myNew, theElement);

                /* Adjust the repeat count for trailing elements */
                if (myNumCols > 1) {
                    /* Set the number of columns */
                    theParser.setAttribute(myNew, MetisOdfTableItem.COLUMNREPEAT, myNumCols);
                }

                /* Loop through the later columns */
                final ListIterator<ColumnReference> myIterator = theColumns.listIterator(theIndex + 1);
                for (int i = 0; myIterator.hasNext()
                        && (i < myNumCols); i++) {
                    /* Map to new instance */
                    final ColumnReference myRef = myIterator.next();
                    myRef.theElement = myNew;
                    myRef.theInstance = i;
                }
            }
        }
    }
}
