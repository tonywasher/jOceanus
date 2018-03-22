/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/service/sheet/odfdom/MetisOasisColumnMap.java $
 * $Revision: 923 $
 * $Author: Tony $
 * $Date: 2018-03-22 09:07:36 +0000 (Thu, 22 Mar 2018) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.service.sheet.odfdom;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.table.TableNumberColumnsRepeatedAttribute;
import org.odftoolkit.odfdom.dom.attribute.table.TableVisibilityAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
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
public class MetisOasisColumnMap {
    /**
     * Underlying sheet.
     */
    private final MetisOasisSheet theOasisSheet;

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
    protected MetisOasisColumnMap(final MetisOasisSheet pSheet) {
        /* Store parameters */
        theOasisSheet = pSheet;

        /* Process the columns */
        processColumnNode(pSheet.getTableElement());
    }

    /**
     * Obtain OasisSheet.
     * @return the last row.
     */
    protected MetisOasisSheet getSheet() {
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
                final TableTableColumnElement myColumn = (TableTableColumnElement) myNode;
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
    protected MetisOasisColumn getReadOnlyColumnByIndex(final int pColIndex) {
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
    protected MetisOasisColumn getMutableColumnByIndex(final int pColIndex) {
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
            final TableTableColumnElement myElement = theLastReference.getElement();

            /* Determine the existing number of repeated columns */
            Integer myRepeat = theLastReference.getRepeat();

            /* Adjust the number of repeated columns */
            myRepeat += pXtraCols;
            myElement.setTableNumberColumnsRepeatedAttribute(myRepeat);

            /* Adjust number of columns */
            theNumColumns += pXtraCols;

            /* else we need to add a completely new element */
        } else {
            /* Create a new column */
            final TableTableColumnElement myElement = theOasisSheet.newColumnElement();
            if (pXtraCols > 1) {
                /* Set repeat count */
                myElement.setTableNumberColumnsRepeatedAttribute(pXtraCols);
            }

            /* Add the column after the lastColumn */
            MetisOasisWorkBook.addAsNextSibling(myElement, theLastReference.getElement());

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
    private static boolean isEmpty(final TableTableColumnElement pElement) {
        /* Access the data attributes */
        final String defStyle = pElement.getTableDefaultCellStyleNameAttribute();
        final String style = pElement.getTableStyleNameAttribute();
        final String visible = pElement.getTableVisibilityAttribute();

        /* Empty if none of the data attributes exist */
        return (defStyle == null)
               && (style == null)
               && (visible.equals(TableVisibilityAttribute.DEFAULT_VALUE));
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
        private TableTableColumnElement theElement;

        /**
         * Constructor.
         * @param pElement the column element
         * @param pIndex the column index
         * @param pInstance the instance of the element
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
         * Access Repeat count.
         * @return the repeat count
         */
        private int getRepeat() {
            /* Determine the maximum instance */
            final Integer myRepeat = theElement.getTableNumberColumnsRepeatedAttribute();
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
        private MetisOasisColumn getMutableColumn() {
            /* If we are asking for an editable item that is a repeated element */
            if (getRepeat() > 1) {
                /* Make this element individual */
                makeIndividual();
            }

            /* Create and return the column object */
            return new MetisOasisColumn(MetisOasisColumnMap.this, theElement, theIndex, false);
        }

        /**
         * Obtain ReadOnly Column representation.
         * @return the column representation
         */
        private MetisOasisColumn getReadOnlyColumn() {
            /* Create and return the column object */
            return new MetisOasisColumn(MetisOasisColumnMap.this, theElement, theIndex, true);
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
            theElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), TableNumberColumnsRepeatedAttribute.ATTRIBUTE_NAME.getLocalName());

            /* If there are prior elements to hive off */
            if (theInstance > 0) {
                /* Create a new column element for this instance, and append after this one */
                final TableTableColumnElement myNew = theOasisSheet.newColumnElement();
                MetisOasisWorkBook.addAsNextSibling(myNew, theElement);

                /* If there are multiple columns before the split */
                if (theInstance > 1) {
                    /* Set the number of columns */
                    theElement.setTableNumberColumnsRepeatedAttribute(theInstance);
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
                final TableTableColumnElement myNew = theOasisSheet.newColumnElement();
                MetisOasisWorkBook.addAsNextSibling(myNew, theElement);

                /* Adjust the repeat count for trailing elements */
                if (myNumCols > 1) {
                    /* Set the number of columns */
                    myNew.setTableNumberColumnsRepeatedAttribute(myNumCols);
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
