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
import java.util.ListIterator;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.table.TableNumberColumnsRepeatedAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.w3c.dom.Node;

/**
 * Class to manage the columns for a Sheet in Oasis.
 * <p>
 * A simple array list is maintained to map from column number to underlying element (and instance of that element).
 * <p>
 * The map is sparsely populated at the end to avoid addressing unused columns. The map will initially only map up to the last TableTableColumnElement
 * regardless of the number of columns that this last element represents. If columns are subsequently referenced past this point, then the map will be expanded
 * as required.
 * <p>
 * Additional columns can only be appended using the {@link #createColumnByIndex} method, if these are referenced using {@link #getColumnByIndex} then null will
 * be returned.
 */
public class OasisColumnMap {
    /**
     * Self Reference.
     */
    private final OasisColumnMap theSelf = this;

    /**
     * Underlying sheet.
     */
    private final OasisSheet theOasisSheet;

    /**
     * Underlying table element.
     */
    private final TableTableElement theOasisTable;

    /**
     * Number of columns.
     */
    private int theNumColumns = 0;

    /**
     * The last reference.
     */
    private ColumnReference theLastReference = null;

    /**
     * List of of columns.
     */
    private final List<ColumnReference> theColumns = new ArrayList<ColumnReference>();

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     */
    protected OasisColumnMap(final OasisSheet pSheet) {
        /* Store parameters */
        theOasisSheet = pSheet;
        theOasisTable = pSheet.getTableElement();

        /* Process the columns */
        processColumnNode(theOasisTable);
    }

    /**
     * Obtain OasisSheet.
     * @return the last row.
     */
    protected OasisSheet getSheet() {
        return theOasisSheet;
    }

    /**
     * Obtain column count.
     * @return the column count
     */
    protected int getColumnCount() {
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
            if (myNode instanceof TableTableColumnElement) {
                /* Add column to list */
                TableTableColumnElement myColumn = (TableTableColumnElement) myNode;
                processColumn(myColumn);

                /* If this is a node that contains columns */
            } else if ((myNode instanceof TableTableHeaderColumnsElement)
                       || (myNode instanceof TableTableColumnGroupElement)
                       || (myNode instanceof TableTableColumnsElement)) {
                /* Process nodes */
                processColumnNode(myNode);
            }
        }
    }

    /**
     * Process a column node.
     * @param pColumn the column node to process
     */
    private void processColumn(final TableTableColumnElement pColumn) {
        /* Determine the number of repeated columns */
        Integer myRepeat = pColumn.getTableNumberColumnsRepeatedAttribute();
        if (myRepeat == null) {
            myRepeat = 1;
        }

        /* If we have references to extend */
        if (theColumns.size() < theNumColumns) {
            /* Ensure that references are fully extended */
            theLastReference.extendReferences(theNumColumns);
        }

        /* Create the new reference and add it */
        ColumnReference myRef = new ColumnReference(pColumn, theNumColumns, 0);
        myRef.addToList();

        /* Adjust number of columns */
        theNumColumns += myRepeat;
    }

    /**
     * Obtain a readOnly column by its index.
     * @param pColIndex the index of the column.
     * @return the column if it exists, else null
     */
    protected OasisColumn getColumnByIndex(final int pColIndex) {
        /* Handle negative column index */
        if (pColIndex < 0) {
            return null;
        }

        /* Handle beyond table limits */
        if (pColIndex >= theNumColumns) {
            return null;
        }

        /* If we have references to extend */
        if (theColumns.size() < pColIndex + 1) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pColIndex);
        }

        /* Just return the column */
        ColumnReference myRef = theColumns.get(pColIndex);
        return myRef.getReadOnlyColumn();
    }

    /**
     * Obtain a changeable column by its index, creating column if it does not exist.
     * @param pColIndex the index of the column.
     * @return the column
     */
    protected OasisColumn createColumnByIndex(final int pColIndex) {
        /* Handle negative column index */
        if (pColIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pColIndex >= theNumColumns) {
            /* Declare variables */
            TableTableColumnElement myElement;

            /* Determine the number of extra columns required */
            int myXtraCols = pColIndex
                             - theNumColumns
                             + 1;

            /* If we have existing reference */
            if (theLastReference != null) {
                /* If we have references to extend */
                if (theColumns.size() < theNumColumns) {
                    /* Ensure that references are fully extended */
                    theLastReference.extendReferences(theNumColumns);
                }

                /* If the last element is empty */
                if (theLastReference.isEmpty()) {
                    /* Obtain the last column */
                    myElement = theLastReference.getElement();

                    /* Determine the existing number of repeated columns */
                    Integer myRepeat = theLastReference.getRepeat();

                    /* Adjust the number of repeated columns */
                    myRepeat += myXtraCols;
                    myElement.setTableNumberColumnsRepeatedAttribute(myRepeat);

                    /* Adjust number of columns */
                    theNumColumns += myRepeat;

                    /* Ensure that references are fully extended */
                    theLastReference.extendReferences(theNumColumns);

                    /* Report addition of columns */
                    theOasisSheet.addColumnsToRows(myXtraCols);

                    /* Return the required column */
                    ColumnReference myRef = theColumns.get(pColIndex);
                    return myRef.getColumn();
                }
            }

            /* Create a new column */
            myElement = theOasisTable.newTableTableColumnElement();
            if (myXtraCols > 1) {
                /* Set repeat count */
                myElement.setTableNumberColumnsRepeatedAttribute(myXtraCols);
            }

            /* Process the element */
            processColumn(myElement);

            /* Report addition of columns */
            theOasisSheet.addColumnsToRows(myXtraCols);

            /* Return the required column */
            ColumnReference myRef = theColumns.get(pColIndex);
            return myRef.getColumn();
        }

        /* If we have references to extend */
        if (theColumns.size() <= pColIndex + 1) {
            /* Ensure that references are extended sufficiently */
            theLastReference.extendReferences(pColIndex + 1);
        }

        /* Just return the column */
        ColumnReference myRef = theColumns.get(pColIndex);
        return myRef.getColumn();
    }

    /**
     * Column Reference class
     */
    private class ColumnReference {
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
        private TableTableColumnElement theElement;

        /**
         * Access Repeat count.
         * @return the repeat count
         */
        private int getRepeat() {
            /* Determine the maximum instance */
            Integer myRepeat = theElement.getTableNumberColumnsRepeatedAttribute();
            return (myRepeat == null)
                    ? 1
                    : myRepeat;
        }

        /**
         * Access Column element.
         * @return the element
         */
        private TableTableColumnElement getElement() {
            return theElement;
        }

        /**
         * Constructor.
         */
        private ColumnReference(final TableTableColumnElement pElement,
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
            theColumns.add(this);
            theLastReference = this;
        }

        /**
         * Is the Column element empty.
         */
        private boolean isEmpty() {
            /* Empty if none of the data attributes exist */
            return ((theElement.getTableDefaultCellStyleNameAttribute() != null)
                    || (theElement.getTableStyleNameAttribute() != null) || (theElement.getTableVisibilityAttribute() != null));
        }

        /**
         * Extend column references.
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
                ColumnReference myRef = new ColumnReference(theElement, iIndex, iInstance);
                myRef.addToList();
            }
        }

        /**
         * Obtain Column representation.
         * @return the column representation
         */
        private OasisColumn getColumn() {
            /* If we are asking for an editable item that is a repeated element */
            if (getRepeat() > 1) {
                /* Make this element individual */
                makeIndividual();
            }

            /* Create and return the column object */
            return new OasisColumn(theSelf, theElement, theIndex, false);
        }

        /**
         * Obtain ReadOnly Column representation.
         * @return the column representation
         */
        private OasisColumn getReadOnlyColumn() {
            /* Create and return the column object */
            return new OasisColumn(theSelf, theElement, theIndex, true);
        }

        /**
         * Make the column an individual column.
         */
        private void makeIndividual() {
            /* If there are prior elements to hive off */
            if (theInstance > 0) {
                /* Create a new column element and add it before this one */
                TableTableColumnElement myNew = theOasisSheet.newColumnElement();
                theOasisTable.insertBefore(myNew, theElement);

                /* If there are multiple columns before the split */
                if (theInstance > 1) {
                    /* Set the number of columns */
                    myNew.setTableNumberColumnsRepeatedAttribute(theInstance);
                }

                /* Loop through the earlier columns */
                ListIterator<ColumnReference> myIterator = theColumns.listIterator(theIndex);
                while (myIterator.hasPrevious()) {
                    /* Map to new column element */
                    ColumnReference myRef = myIterator.previous();
                    myRef.theElement = myNew;

                    /* Break loop if this is not a virtual instance */
                    if (myRef.theInstance == 0) {
                        break;
                    }
                }
            }

            /* Determine how many trailing elements to hive off */
            int myRepeatCount = getRepeat();
            int myNumCols = myRepeatCount
                            - theInstance
                            - 1;

            /* Clear the number of columns */
            theElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), TableNumberColumnsRepeatedAttribute.ATTRIBUTE_NAME.getLocalName());

            /* Set zero instance */
            theInstance = 0;

            /* If we have trailing columns */
            if (myNumCols > 0) {
                /* Create a new column element and add it before this one */
                TableTableColumnElement myNew = theOasisSheet.newColumnElement();
                theOasisTable.insertBefore(myNew, theElement);

                /* Adjust the repeat count for trailing elements */
                if (myNumCols > 1) {
                    /* Set the number of columns */
                    theElement.setTableNumberColumnsRepeatedAttribute(myNumCols);
                }

                /* Set the element for this column */
                theElement = myNew;

                /* Loop through the later columns */
                ListIterator<ColumnReference> myIterator = theColumns.listIterator(theIndex + 1);
                for (int i = 0; myIterator.hasNext()
                                && (i < myNumCols); i++) {
                    /* Map to new instance */
                    ColumnReference myRef = myIterator.next();
                    myRef.theInstance = i;
                }
            }
        }
    }
}
