/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdatamodels.ui;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.RowCellRenderer;

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

    @Override
    public Object getHeaderValue() {
        /* Return the column name according to the model */
        return theModel.getColumnName(getModelIndex());
    }

    /**
     * Column Model class.
     */
    public static class JDataTableColumnModel
            extends DefaultTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5503203201580691221L;

        /**
         * Date column standard width.
         */
        protected static final int WIDTH_DATE = 90;

        /**
         * Name column standard width.
         */
        protected static final int WIDTH_MONEY = 90;

        /**
         * Name column standard width.
         */
        protected static final int WIDTH_NAME = 130;

        /**
         * The DataTableModel.
         */
        private final JDataTableModel<?> theModel;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected JDataTableColumnModel(final JDataTable<?> pTable) {
            /* Access TableModel */
            theModel = pTable.getTableModel();
        }

        /**
         * Add a column to the end of the model.
         * @param pColumn the column
         */
        protected void addColumn(final JDataTableColumn pColumn) {
            /* Set the range */
            super.addColumn(pColumn);
            pColumn.setMember(true);
            pColumn.setModel(theModel);
        }

        /**
         * Remove a column from the model.
         * @param pColumn the column
         */
        protected void removeColumn(final JDataTableColumn pColumn) {
            /* Set the range */
            super.removeColumn(pColumn);
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
     */
    protected static final class RowColumnModel
            extends JDataTableColumnModel {
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
        protected RowColumnModel(final JDataTable<?> pTable) {
            /* Call super-constructor */
            super(pTable);

            /* Create the relevant formatters/editors */
            theRowRenderer = pTable.getFieldMgr().allocateRowRenderer();

            /* Create the columns */
            JDataTableColumn myCol = new JDataTableColumn(0, JDataTable.ROWHDR_WIDTH, theRowRenderer, null);
            addColumn(myCol);
            myCol.setModel(pTable.getRowTableModel());
        }
    }
}
