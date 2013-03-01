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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.table.TableNumberColumnsRepeatedAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.w3c.dom.Node;

/**
 * Class representing a list of columns in Oasis.
 * @author Tony Washer
 */
public class OasisColumnMap {
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
     * The last column.
     */
    private OasisColumn theLastColumn = null;

    /**
     * List of of columns.
     */
    private List<OasisColumn> theColumns = new ArrayList<OasisColumn>();

    /**
     * Constructor.
     * @param pSheet the underlying sheet.
     * @throws JDataException on error
     */
    protected OasisColumnMap(final OasisSheet pSheet) throws JDataException {
        /* Store parameters */
        theOasisSheet = pSheet;
        theOasisTable = pSheet.getTableElement();

        /* Loop through the children of the table */
        for (Node myNode = theOasisTable.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a column element */
            if (myNode instanceof TableTableColumnElement) {
                /* Add column to list */
                TableTableColumnElement myColumn = (TableTableColumnElement) myNode;
                processColumn(myColumn);
                continue;
            }

            /* If this is a header columns element */
            if (myNode instanceof TableTableHeaderColumnsElement) {
                /* Add column to list */
                TableTableHeaderColumnsElement myHeaders = (TableTableHeaderColumnsElement) myNode;
                processHeaders(myHeaders);
                continue;
            }

            /* Don't allow columns or column group elements */
            if ((myNode instanceof TableTableColumnsElement)
                || (myNode instanceof TableTableColumnGroupElement)) {
                /* Add column to list */
                throw new JDataException(ExceptionClass.LOGIC, myNode, "Unsupported node");
            }
        }
    }

    /**
     * Obtain last column.
     * @return the last column.
     */
    protected OasisColumn getLastColumn() {
        return theLastColumn;
    }

    /**
     * Obtain column count.
     * @return the column count
     */
    protected int getColumnCount() {
        return theNumColumns;
    }

    /**
     * Process header columns.
     * @param pHeaders the header columns to process
     * @throws JDataException on error
     */
    private void processHeaders(final TableTableHeaderColumnsElement pHeaders) throws JDataException {
        /* Loop through the column children of the header */
        for (Node myNode = pHeaders.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a column */
            if (myNode instanceof TableTableColumnElement) {
                /* Add column to list */
                TableTableColumnElement myColumn = (TableTableColumnElement) myNode;
                processColumn(myColumn);
            }

            /* Don't allow columns or column group elements */
            if ((myNode instanceof TableTableHeaderColumnsElement)
                || (myNode instanceof TableTableColumnsElement)
                || (myNode instanceof TableTableColumnGroupElement)) {
                /* Add column to list */
                throw new JDataException(ExceptionClass.LOGIC, myNode, "Unsupported node");
            }
        }
    }

    /**
     * Process a column.
     * @param pColumn the column to process
     */
    private void processColumn(final TableTableColumnElement pColumn) {
        /* Determine the number of repeated columns */
        Integer myRepeat = pColumn.getTableNumberColumnsRepeatedAttribute();
        if (myRepeat == null) {
            myRepeat = 1;
        }

        /* Loop through the repeated columns */
        for (int myInstance = 0; myInstance < myRepeat; myInstance++) {
            /* Create the instance and add to columns */
            OasisColumn myCol = new OasisColumn(this, theLastColumn, pColumn, theNumColumns++, myInstance);
            theColumns.add(myCol);
            theLastColumn = myCol;
        }
    }

    /**
     * Obtain a column by its index.
     * @param pColIndex the index of the column.
     * @return the column
     */
    protected OasisColumn getColumnByIndex(final int pColIndex) {
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

            /* If we have existing columns */
            if (theLastColumn != null) {
                /* Obtain the last column */
                myElement = theLastColumn.getColumnElement();

                /* Determine the existing number of repeated columns */
                Integer myRepeat = theLastColumn.getRepeatCount();

                /* If we have a repeated item */
                if (myRepeat > 1) {
                    /* Loop through the additional columns */
                    for (int myInstance = 0; myInstance < myXtraCols; myInstance++) {
                        /* Create the instance and add to columns */
                        OasisColumn myCol = new OasisColumn(this, theLastColumn, myElement, theNumColumns++, myRepeat
                                                                                                             + myInstance);
                        theColumns.add(myCol);
                        theLastColumn = myCol;
                    }

                    /* Adjust the number of repeated columns */
                    myRepeat += myXtraCols;
                    myElement.setTableNumberColumnsRepeatedAttribute(myRepeat);

                    /* Report addition of columns */
                    theOasisSheet.addColumnsToRows();

                    /* Return the required column */
                    return theLastColumn;
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
            theOasisSheet.addColumnsToRows();

            /* Return the required column */
            return theLastColumn;
        }

        /* Just return the column */
        return theColumns.get(pColIndex);
    }

    /**
     * Make the column an individual column.
     * @param pColumn the column
     */
    public void makeIndividual(final OasisColumn pColumn) {
        /* Access the column element */
        TableTableColumnElement myElement = pColumn.getColumnElement();
        int myInstance = pColumn.getInstance();

        /* If there are prior elements to hive off */
        if (myInstance > 0) {
            /* Create a new column element and add it before this one */
            TableTableColumnElement myNew = theOasisSheet.newColumnElement();
            theOasisTable.insertBefore(myNew, myElement);

            /* If there are multiple columns before the split */
            if (myInstance > 1) {
                /* Set the number of columns */
                myNew.setTableNumberColumnsRepeatedAttribute(myInstance);
            }

            /* Loop through the earlier columns */
            OasisColumn myCol = pColumn.getPreviousColumn();
            while (myCol.getInstance() > 0) {
                /* Map to new column */
                myCol.setColumnElement(myNew);
                myCol = myCol.getPreviousColumn();
            }
        }

        /* Determine how many trailing elements to hive off */
        int myRepeatCount = pColumn.getRepeatCount();
        int myNumCols = myRepeatCount
                        - myInstance
                        + 1;

        /* Clear the number of columns */
        myElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), TableNumberColumnsRepeatedAttribute.ATTRIBUTE_NAME.getLocalName());

        /* Set zero instance */
        pColumn.setInstance(0);

        /* If we have trailing columns */
        if (myNumCols > 0) {
            /* Create a new column element and add it before this one */
            TableTableColumnElement myNew = theOasisSheet.newColumnElement();
            theOasisTable.insertBefore(myNew, myElement);

            /* Adjust the repeat count for trailing elements */
            if (myNumCols > 1) {
                /* Set the number of columns */
                myElement.setTableNumberColumnsRepeatedAttribute(myNumCols);
            }

            /* Set the element for this column */
            pColumn.setColumnElement(myNew);

            /* Loop through the later rows */
            OasisColumn myCol = pColumn.getNextColumn();
            for (int i = 0; i < myNumCols; i++) {
                /* Map to new column */
                myCol.setInstance(i);
                myCol = myCol.getNextColumn();
            }
        }
    }
}
