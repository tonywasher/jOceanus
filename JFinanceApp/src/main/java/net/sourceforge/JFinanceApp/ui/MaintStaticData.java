/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.ui;

import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList.ListStyle;
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JDataModels.data.StaticData.StaticList;
import net.sourceforge.JDataModels.ui.Editor.BooleanEditor;
import net.sourceforge.JDataModels.ui.Editor.StringEditor;
import net.sourceforge.JDataModels.ui.ErrorPanel;
import net.sourceforge.JDataModels.ui.JDataTable;
import net.sourceforge.JDataModels.ui.JDataTableColumn;
import net.sourceforge.JDataModels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.JDataModels.ui.JDataTableModel;
import net.sourceforge.JDataModels.ui.RenderManager;
import net.sourceforge.JDataModels.ui.Renderer.BooleanRenderer;
import net.sourceforge.JDataModels.ui.Renderer.IntegerRenderer;
import net.sourceforge.JDataModels.ui.Renderer.StringRenderer;
import net.sourceforge.JDataModels.views.UpdateSet;
import net.sourceforge.JDataModels.views.UpdateSet.UpdateEntry;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.views.View;

/**
 * Static Data Table.
 * @author Tony Washer
 * @param <L> the list type
 * @param <T> the data type
 */
public class MaintStaticData<L extends StaticList<T, ?>, T extends StaticData<T, ?>> extends JDataTable<T> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8747707037700378702L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle
            .getBundle(MaintStaticData.class.getName());

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
     * Active column id.
     */
    private static final int COLUMN_ACTIVE = 5;

    /**
     * Class column width.
     */
    private static final int WIDTH_CLASS = 90;

    /**
     * Name column width.
     */
    private static final int WIDTH_NAME = 80;

    /**
     * Description column width.
     */
    private static final int WIDTH_DESC = 200;

    /**
     * Order column width.
     */
    private static final int WIDTH_ORDER = 20;

    /**
     * Enabled column width.
     */
    private static final int WIDTH_ENABLED = 20;

    /**
     * Active column width.
     */
    private static final int WIDTH_ACTIVE = 20;

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
    private final transient View theView;

    /**
     * The render manager.
     */
    private final transient RenderManager theRenderMgr;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * The Data class.
     */
    private final Class<L> theClass;

    /**
     * The List.
     */
    private transient L theStatic = null;

    /**
     * Self reference.
     */
    private final MaintStaticData<L, T> theTable = this;

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
    private final transient UpdateEntry theUpdateEntry;

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
        theError.setError(pError);
    }

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the update set
     * @param pError the error panel
     * @param pClass the list class
     */
    public MaintStaticData(final View pView,
                           final UpdateSet pUpdateSet,
                           final ErrorPanel pError,
                           final Class<L> pClass) {
        /* Record the passed details */
        theError = pError;
        theClass = pClass;
        theView = pView;
        theRenderMgr = theView.getRenderMgr();
        setRenderMgr(theRenderMgr);

        /* Build the Update set and List */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerClass(pClass);
        setUpdateSet(theUpdateSet);

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
        thePanel = new JPanel();

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
    public void refreshData() throws JDataException {
        /* Access data */
        FinanceData myData = theView.getData();

        /* Access edit list */
        theStatic = myData.getDataList(theClass);
        theStatic = theClass.cast(theStatic.deriveList(ListStyle.EDIT));

        /* Update the Data View */
        setList(theStatic);
        theUpdateEntry.setDataList(theStatic);
    }

    /**
     * Static table model.
     */
    public final class StaticModel extends JDataTableModel<T> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6428052539280821038L;

        /**
         * Constructor.
         */
        private StaticModel() {
            /* call constructor */
            super(theTable);
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
            return (theColumns == null) ? 0 : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theStatic == null) ? 0 : theStatic.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
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

        @Override
        public JDataField getFieldForCell(final T pItem,
                                          final int pColIndex) {
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

        @Override
        public Class<?> getColumnClass(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DESC:
                    return String.class;
                case COLUMN_CLASS:
                    return String.class;
                case COLUMN_NAME:
                    return String.class;
                case COLUMN_ORDER:
                    return Integer.class;
                case COLUMN_ENABLED:
                    return Boolean.class;
                case COLUMN_ACTIVE:
                    return Boolean.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public boolean isCellEditable(final T pItem,
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

        @Override
        public Object getItemValue(final T pItem,
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

        @Override
        public void setItemValue(final T pItem,
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
    }

    /**
     * Column Model class.
     */
    private final class StaticColumnModel extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 676363206266447113L;

        /**
         * Integer renderer.
         */
        private final IntegerRenderer theIntegerRenderer;

        /**
         * String renderer.
         */
        private final StringRenderer theStringRenderer;

        /**
         * Boolean renderer.
         */
        private final BooleanRenderer theBooleanRenderer;

        /**
         * String Editor.
         */
        private final StringEditor theStringEditor;

        /**
         * Boolean Editor.
         */
        private final BooleanEditor theBooleanEditor;

        /**
         * Constructor.
         */
        private StaticColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theIntegerRenderer = theRenderMgr.allocateIntegerRenderer();
            theStringRenderer = theRenderMgr.allocateStringRenderer();
            theBooleanRenderer = theRenderMgr.allocateBooleanRenderer();
            theStringEditor = new StringEditor();
            theBooleanEditor = new BooleanEditor();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_CLASS, WIDTH_CLASS, theStringRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_ORDER, WIDTH_ORDER, theIntegerRenderer, null));
            addColumn(new JDataTableColumn(COLUMN_ENABLED, WIDTH_ENABLED, theBooleanRenderer,
                    theBooleanEditor));
            addColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ACTIVE, theBooleanRenderer, null));
        }
    }
}
