/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.util.Enumeration;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.RowCellRenderer;

/**
 * TableColumn extension class.
 */
public class JDataTableColumn
        extends TableColumn {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6117303771805259099L;

    /**
     * Is the column currently in the model.
     */
    private boolean isMember = false;

    /**
     * The Model for the Table.
     */
    private AbstractTableModel theModel = null;

    /**
     * Constructor.
     * @param modelIndex model index
     * @param width column width
     * @param cellRenderer cell renderer
     * @param cellEditor cell editor
     */
    public JDataTableColumn(final int modelIndex,
                            final int width,
                            final TableCellRenderer cellRenderer,
                            final TableCellEditor cellEditor) {
        /* Call super-constructor */
        super(modelIndex, width, cellRenderer, cellEditor);
        setMinWidth(width);
    }

    /**
     * Constructor.
     * @param modelIndex model index
     * @param width column width
     * @param cellRenderer cell renderer
     */
    public JDataTableColumn(final int modelIndex,
                            final int width,
                            final TableCellRenderer cellRenderer) {
        /* Call super-constructor */
        this(modelIndex, width, cellRenderer, null);
    }

    /**
     * Is the column currently a member?
     * @return true/false
     */
    public boolean isMember() {
        return isMember;
    }

    /**
     * Set whether the column is a member.
     * @param pMember true/false
     */
    public void setMember(final boolean pMember) {
        isMember = pMember;
    }

    /**
     * Set the table model.
     * @param pModel the table model
     */
    public void setModel(final AbstractTableModel pModel) {
        theModel = pModel;
    }

    @Override
    public Object getHeaderValue() {
        /* Return the column name according to the model */
        return theModel.getColumnName(getModelIndex());
    }

    /**
     * Column Model class.
     * @param <E> the data type enum class
     */
    public static class JDataTableColumnModel<E extends Enum<E>>
            extends DefaultTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5503203201580691221L;

        /**
         * Date column standard width.
         */
        protected static final int WIDTH_DATE = 100;

        /**
         * Money column standard width.
         */
        protected static final int WIDTH_MONEY = 100;

        /**
         * Rate column standard width.
         */
        protected static final int WIDTH_RATE = 90;

        /**
         * Price column standard width.
         */
        protected static final int WIDTH_PRICE = 90;

        /**
         * Units column standard width.
         */
        protected static final int WIDTH_UNITS = 90;

        /**
         * Dilution column standard width.
         */
        protected static final int WIDTH_DILUTION = 90;

        /**
         * Name column standard width.
         */
        protected static final int WIDTH_NAME = 130;

        /**
         * Description column standard width.
         */
        protected static final int WIDTH_DESC = 200;

        /**
         * Icon column width.
         */
        protected static final int WIDTH_ICON = 20;

        /**
         * Integer column width.
         */
        protected static final int WIDTH_INT = 30;

        /**
         * Currency column width.
         */
        protected static final int WIDTH_CURR = 50;

        /**
         * Row Header Width.
         */
        protected static final int WIDTH_ROWHDR = 40;

        /**
         * The DataTableModel.
         */
        private final JDataTableModel<?, E> theModel;

        /**
         * The Declared column count.
         */
        private int theDeclaredCount;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected JDataTableColumnModel(final JDataTable<?, E> pTable) {
            /* Access TableModel */
            theModel = pTable.getTableModel();
        }

        /**
         * Obtain the declared column count.
         * @return the column count
         */
        public int getDeclaredCount() {
            return theDeclaredCount;
        }

        /**
         * Declare a column and add to the end of the model.
         * @param pColumn the column
         */
        protected void declareColumn(final JDataTableColumn pColumn) {
            /* Declare the column */
            addColumn(pColumn);
            pColumn.setMember(true);
            pColumn.setModel(theModel);

            /* Increment column count */
            theDeclaredCount++;
        }

        /**
         * Reveal column in its natural position.
         * @param pColumn the column
         */
        protected void revealColumn(final JDataTableColumn pColumn) {
            /* Ignore if already a member */
            if (pColumn.isMember()) {
                return;
            }

            /* Determine various factors */
            int myIndex = pColumn.getModelIndex();
            int myNumCols = getColumnCount();

            /* Determine insert point in table */
            int myInsert = 0;
            Enumeration<TableColumn> myEnumerator = getColumns();
            while (myEnumerator.hasMoreElements()) {
                TableColumn myColumn = myEnumerator.nextElement();

                /* If this column is prior to the revealed column */
                if (myColumn.getModelIndex() < myIndex) {
                    /* Bump insert point */
                    myInsert++;

                    /* else we have found the insert point */
                } else {
                    break;
                }
            }

            /* Insert the column */
            addColumn(pColumn);
            pColumn.setMember(true);

            /* If we need to move the column */
            if (myInsert < myNumCols) {
                /* Move the column to the correct place */
                moveColumn(myNumCols, myInsert);
            }
        }

        /**
         * Hide a column in the model.
         * @param pColumn the column
         */
        protected void hideColumn(final JDataTableColumn pColumn) {
            /* Set the range */
            removeColumn(pColumn);
            pColumn.setMember(false);
        }

        /**
         * Access the array of displayed column indices.
         * @return the array of columns
         */
        protected JDataField[] getColumnFields() {
            /* Declare the field array */
            JDataField[] myFields = new JDataField[getColumnCount()];
            int myCol;

            /* Loop through the columns */
            for (int i = 0; i < myFields.length; i++) {
                /* Access the column index for this column */
                myCol = getColumn(i).getModelIndex();

                /* Store the field # */
                myFields[i] = theModel.getFieldForCell(null, myCol);
            }

            /* return the fields */
            return myFields;
        }
    }

    /**
     * Row Column Model class.
     * @param <E> the data type enum class
     */
    protected static final class RowColumnModel<E extends Enum<E>>
            extends JDataTableColumnModel<E> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -579928883936388389L;

        /**
         * Row renderer.
         */
        private RowCellRenderer theRowRenderer = null;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected RowColumnModel(final JDataTable<?, E> pTable) {
            /* Call super-constructor */
            super(pTable);

            /* Create the relevant formatters/editors */
            theRowRenderer = pTable.getFieldMgr().allocateRowRenderer();

            /* Create the columns */
            JDataTableColumn myCol = new JDataTableColumn(0, WIDTH_ROWHDR, theRowRenderer, null);
            addColumn(myCol);
            myCol.setModel(pTable.getRowTableModel());
        }
    }
}
