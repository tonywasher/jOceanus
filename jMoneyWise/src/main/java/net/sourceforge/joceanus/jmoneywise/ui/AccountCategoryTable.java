/*******************************************************************************
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jdatamodels.ui.ErrorPanel;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTable;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableColumn;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jdatamodels.ui.JDataTableModel;
import net.sourceforge.joceanus.jdatamodels.ui.SaveButtons;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdatamodels.views.UpdateEntry;
import net.sourceforge.joceanus.jdatamodels.views.UpdateSet;
import net.sourceforge.joceanus.jeventmanager.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.BooleanCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldManager;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.views.View;

/**
 * Account Category Maintenance.
 */
public class AccountCategoryTable
        extends JDataTable<AccountCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4419927779522960812L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountCategoryTable.class.getName());

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = NLS_BUNDLE.getString("TitleName");

    /**
     * FullName Column Title.
     */
    private static final String TITLE_FULLNAME = NLS_BUNDLE.getString("TitleFullName");

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = NLS_BUNDLE.getString("TitleCategory");

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = NLS_BUNDLE.getString("TitleDesc");

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = NLS_BUNDLE.getString("TitleActive");

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The event entry.
     */
    private final transient UpdateEntry<AccountCategory> theCategoryEntry;

    /**
     * The analysis data entry.
     */
    private final transient JDataEntry theDataCategories;

    /**
     * The save buttons.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The table model.
     */
    private final CategoryTableModel theModel;

    /**
     * The Column Model.
     */
    private final CategoryColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * Account Categories.
     */
    private transient AccountCategoryList theCategories = null;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public AccountCategoryTable(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet(theView);
        theCategoryEntry = theUpdateSet.registerClass(AccountCategory.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataCategories = myDataMgr.new JDataEntry(EventCategoryTable.class.getSimpleName());
        theDataCategories.addAsChildOf(mySection);
        theDataCategories.setObject(theUpdateSet);

        /* Create the save buttons */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataCategories);

        /* Create the table model */
        theModel = new CategoryTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new CategoryColumnModel(this);
        setColumnModel(theColumns);

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theError);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);

        /* Create listener */
        CategoryListener myListener = new CategoryListener();
        theView.addChangeListener(myListener);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataCategories.setFocus();
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Get the Events edit list */
        MoneyWiseData myData = theView.getData();
        AccountCategoryList myCategories = myData.getAccountCategories();
        theCategories = myCategories.deriveEditList();
        setList(theCategories);
        theCategoryEntry.setDataList(theCategories);
        theSaveButtons.setEnabled(true);
        fireStateChanged();

        /* Touch the updateSet */
        theDataCategories.setObject(theUpdateSet);
    }

    @Override
    protected void setError(final JDataException pError) {
        theError.addError(pError);
    }

    @Override
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    @Override
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    /**
     * JTable Data Model.
     */
    private final class CategoryTableModel
            extends JDataTableModel<AccountCategory> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 2654650162773869736L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryTableModel(final AccountCategoryTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                    ? 0
                    : theColumns.getColumnCount();
        }

        @Override
        public int getRowCount() {
            return (theCategories == null)
                    ? 0
                    : theCategories.size();
        }

        @Override
        public JDataField getFieldForCell(final AccountCategory pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final AccountCategory pCategory,
                                      final int pColIndex) {
            return false;
        }

        @Override
        public AccountCategory getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theCategories.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final AccountCategory pCategory,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pCategory, pColIndex);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final AccountCategory pRow) {
            /* Return visibility of row */
            return !pRow.isDeleted();
        }
    }

    /**
     * Listener class.
     */
    private final class CategoryListener
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the View */
            if (theView.equals(o)) {
                /* Refresh the data */
                refreshData();
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class CategoryColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6374822452498064568L;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 0;

        /**
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * FullName column id.
         */
        private static final int COLUMN_FULLNAME = 3;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 4;

        /**
         * Boolean Renderer.
         */
        private final BooleanCellRenderer theBooleanRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryColumnModel(final AccountCategoryTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theBooleanRenderer = theFieldMgr.allocateBooleanCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_FULLNAME, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_BOOL, theBooleanRenderer));
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_CATEGORY:
                    return TITLE_CAT;
                case COLUMN_FULLNAME:
                    return TITLE_FULLNAME;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the category column.
         * @param pCategory event category
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final AccountCategory pCategory,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pCategory.getSubCategory();
                case COLUMN_FULLNAME:
                    return pCategory.getName();
                case COLUMN_CATEGORY:
                    return pCategory.getCategoryType();
                case COLUMN_DESC:
                    return pCategory.getDesc();
                case COLUMN_ACTIVE:
                    return pCategory.isActive();
                default:
                    return null;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        protected JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return EventCategory.FIELD_SUBCAT;
                case COLUMN_DESC:
                    return EventCategory.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return EventCategory.FIELD_CATTYPE;
                case COLUMN_FULLNAME:
                    return EventCategory.FIELD_NAME;
                case COLUMN_ACTIVE:
                    return EventCategory.FIELD_ACTIVE;
                default:
                    return null;
            }
        }
    }
}
