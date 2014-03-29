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

import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * Static Data Table.
 * @author Tony Washer
 * @param <L> the list type
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public class StaticDataTable<L extends StaticList<T, ?, E>, T extends StaticData<T, ?, E>, E extends Enum<E>>
        extends JDataTable<T, E> {
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
    private static final String TITLE_CLASS = StaticData.FIELD_CLASS.getName();

    /**
     * Name column title.
     */
    private static final String TITLE_NAME = StaticData.FIELD_NAME.getName();

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = StaticData.FIELD_DESC.getName();

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
    private final transient DataControl<?, E> theControl;

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
    private final transient UpdateSet<E> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final transient UpdateEntry<T, E> theUpdateEntry;

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
    protected void setError(final JOceanusException pError) {
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
    public StaticDataTable(final DataControl<?, E> pControl,
                           final UpdateSet<E> pUpdateSet,
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
        theUpdateSet.addChangeListener(new StaticListener());

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
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Access data */
        DataSet<?, E> myData = theControl.getData();

        /* Access edit list */
        theStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(theStatic.deriveList(ListStyle.EDIT));

        /* Update the Data View */
        setList(theStatic);
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * Select static data.
     * @param pStatic the static data
     */
    protected void selectStatic(final StaticData<?, ?, E> pStatic) {
        /* Find the item in the list */
        int myIndex = theStatic.indexOf(pStatic);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    /**
     * The listener class.
     */
    private final class StaticListener
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

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
            extends JDataTableModel<T, E> {
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
        public boolean isCellEditable(final T pTrans,
                                      final int pColIndex) {
            return false;
        }

        @Override
        public Object getItemValue(final T pItem,
                                   final int pColIndex) {
            /* Obtain the item value for the column */
            return theColumns.getItemValue(pItem, pColIndex);
        }
    }

    /**
     * Column Model class.
     */
    private final class StaticColumnModel
            extends JDataTableColumnModel<E> {
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
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 4;

        /**
         * Class column width.
         */
        private static final int WIDTH_CLASS = 90;

        /**
         * String renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Icon renderer.
         */
        private final IconCellRenderer theIconRenderer;

        /**
         * Constructor.
         */
        private StaticColumnModel() {
            /* call constructor */
            super(StaticDataTable.this);

            /* Create the relevant renderers */
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theIconRenderer = theFieldMgr.allocateIconCellRenderer();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_CLASS, WIDTH_CLASS, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theIconRenderer));
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
                case COLUMN_ACTIVE:
                    return pItem.isActive()
                                           ? ICON_ACTIVE
                                           : null;
                default:
                    return null;
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
                case COLUMN_ACTIVE:
                    return DataItem.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
