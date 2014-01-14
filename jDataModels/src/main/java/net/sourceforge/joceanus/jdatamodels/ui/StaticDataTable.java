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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList.ListStyle;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;
import net.sourceforge.joceanus.jdatamodels.data.StaticData.StaticList;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdatamodels.views.UpdateEntry;
import net.sourceforge.joceanus.jdatamodels.views.UpdateSet;
import net.sourceforge.joceanus.jeventmanager.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.BooleanCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.BooleanCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldManager;

/**
 * Static Data Table.
 * @author Tony Washer
 * @param <L> the list type
 * @param <T> the data type
 */
public class StaticDataTable<L extends StaticList<T, ?>, T extends StaticData<T, ?>>
        extends JDataTable<T> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8747707037700378702L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(StaticDataTable.class.getName());

    /**
     * Class column title.
     */
    private static final String TITLE_CLASS = NLS_BUNDLE.getString("TitleClass");

    /**
     * Name column title.
     */
    private static final String TITLE_NAME = NLS_BUNDLE.getString("TitleName");

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = NLS_BUNDLE.getString("TitleDesc");

    /**
     * Order column title.
     */
    private static final String TITLE_ORDER = NLS_BUNDLE.getString("TitleSortOrder");

    /**
     * Enabled column title.
     */
    private static final String TITLE_ENABLED = NLS_BUNDLE.getString("TitleEnabled");

    /**
     * Active column title.
     */
    private static final String TITLE_ACTIVE = NLS_BUNDLE.getString("TitleActive");

    /**
     * Panel width.
     */
    private static final int WIDTH_PANEL = 800;

    /**
     * Panel height.
     */
    private static final int HEIGHT_PANEL = 200;

    /**
     * The Data view.
     */
    private final transient DataControl<?> theControl;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The Data class.
     */
    private final Class<L> theClass;

    /**
     * The List.
     */
    private transient L theStatic = null;

    /**
     * The Table Model.
     */
    private final StaticModel theModel;

    /**
     * The Columns model.
     */
    private final StaticColumnModel theColumns;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final transient UpdateEntry<T> theUpdateEntry;

    /**
     * The Error panel.
     */
    private final ErrorPanel theError;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    protected void setError(final JDataException pError) {
        theError.addError(pError);
    }

    /**
     * Constructor.
     * @param pControl the data control
     * @param pUpdateSet the update set
     * @param pError the error panel
     * @param pListClass the list class
     * @param pItemClass the item class
     */
    public StaticDataTable(final DataControl<?> pControl,
                           final UpdateSet pUpdateSet,
                           final ErrorPanel pError,
                           final Class<L> pListClass,
                           final Class<T> pItemClass) {
        /* Record the passed details */
        theError = pError;
        theClass = pListClass;
        theControl = pControl;
        theFieldMgr = theControl.getFieldMgr();
        setFieldMgr(theFieldMgr);
        setLogger(theControl.getLogger());

        /* Build the Update set and List */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerClass(pItemClass);
        setUpdateSet(theUpdateSet);
        theUpdateSet.addActionListener(new StaticListener());

        /* Set the table model */
        theModel = new StaticModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StaticColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
    }

    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (theStatic != null) {
            theStatic.findEditState();
        }

        /* Notify that there have been changes */
        fireStateChanged();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JDataException on error
     */
    protected void refreshData() throws JDataException {
        /* Access data */
        DataSet<?, ?> myData = theControl.getData();

        /* Access edit list */
        theStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(theStatic.deriveList(ListStyle.EDIT));

        /* Update the Data View */
        setList(theStatic);
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * The listener class.
     */
    private final class StaticListener
            implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }
    }

    /**
     * Static table model.
     */
    public final class StaticModel
            extends JDataTableModel<T> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6428052539280821038L;

        /**
         * Constructor.
         */
        private StaticModel() {
            /* call constructor */
            super(StaticDataTable.this);
        }

        @Override
        public T getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theStatic.get(pRowIndex);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null)
                    ? 0
                    : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theStatic == null)
                    ? 0
                    : theStatic.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public JDataField getFieldForCell(final T pItem,
                                          final int pColIndex) {
            /* Obtain the column field */
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final T pItem,
                                      final int pColIndex) {
            /* Is the cell editable? */
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Object getItemValue(final T pItem,
                                   final int pColIndex) {
            /* Obtain the item value for the column */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final T pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JDataException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }
    }

    /**
     * Column Model class.
     */
    private final class StaticColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 676363206266447113L;

        /**
         * Class column id.
         */
        private static final int COLUMN_CLASS = 0;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * Order column id.
         */
        private static final int COLUMN_ORDER = 3;

        /**
         * Enabled column id.
         */
        private static final int COLUMN_ENABLED = 4;

        /**
         * Class column width.
         */
        private static final int WIDTH_CLASS = 90;

        /**
         * Order column width.
         */
        private static final int WIDTH_ORDER = 20;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 5;

        /**
         * Integer renderer.
         */
        private final IntegerCellRenderer theIntegerRenderer;

        /**
         * String renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Boolean renderer.
         */
        private final BooleanCellRenderer theBooleanRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Boolean Editor.
         */
        private final BooleanCellEditor theBooleanEditor;

        /**
         * Constructor.
         */
        private StaticColumnModel() {
            /* call constructor */
            super(StaticDataTable.this);

            /* Create the relevant formatters/editors */
            theIntegerRenderer = theFieldMgr.allocateIntegerCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theBooleanRenderer = theFieldMgr.allocateBooleanCellRenderer();
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theBooleanEditor = theFieldMgr.allocateBooleanCellEditor();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_CLASS, WIDTH_CLASS, theStringRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_ORDER, WIDTH_ORDER, theIntegerRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_ENABLED, WIDTH_BOOL, theBooleanRenderer, theBooleanEditor));
            addColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_BOOL, theBooleanRenderer, null));
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return TITLE_CLASS;
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_ORDER:
                    return TITLE_ORDER;
                case COLUMN_ENABLED:
                    return TITLE_ENABLED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @return the value
         */
        private Object getItemValue(final T pItem,
                                    final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return pItem.getStaticClass().toString();
                case COLUMN_NAME:
                    return pItem.getName();
                case COLUMN_DESC:
                    return pItem.getDesc();
                case COLUMN_ENABLED:
                    return pItem.getEnabled();
                case COLUMN_ORDER:
                    return pItem.getOrder();
                case COLUMN_ACTIVE:
                    return pItem.isActive();
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value
         * @throws JDataException on error
         */
        private void setItemValue(final T pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JDataException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_ENABLED:
                    pItem.setEnabled((Boolean) pValue);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the item editable at the column index.
         * @param pItem the item
         * @param pColIndex column index
         * @return true/false
         */
        private boolean isCellEditable(final T pItem,
                                       final int pColIndex) {
            /* switch on column */
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return pItem.getEnabled();
                case COLUMN_ENABLED:
                    return !pItem.isActive();
                case COLUMN_CLASS:
                case COLUMN_ORDER:
                case COLUMN_ACTIVE:
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        private JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_CLASS:
                    return StaticData.FIELD_CLASS;
                case COLUMN_NAME:
                    return StaticData.FIELD_NAME;
                case COLUMN_DESC:
                    return StaticData.FIELD_DESC;
                case COLUMN_ENABLED:
                    return StaticData.FIELD_ENABLED;
                case COLUMN_ORDER:
                    return StaticData.FIELD_ORDER;
                case COLUMN_ACTIVE:
                    return DataItem.FIELD_ACTIVE;
                default:
                    return null;
            }
        }
    }
}
