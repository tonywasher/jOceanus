/*******************************************************************************
 * jMoneyWise: Finance Application
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.BooleanCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

/**
 * Loan Category Maintenance.
 */
public class LoanCategoryTable
        extends JDataTable<LoanCategory, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 238331669002141160L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(LoanCategoryTable.class.getName());

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = LoanCategory.FIELD_NAME.getName();

    /**
     * FullName Column Title.
     */
    private static final String TITLE_FULLNAME = NLS_BUNDLE.getString("TitleFullName");

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = LoanCategory.FIELD_CATTYPE.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = LoanCategory.FIELD_DESC.getName();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = NLS_BUNDLE.getString("TitleActive");

    /**
     * Filter Prompt.
     */
    private static final String TITLE_FILTER = NLS_BUNDLE.getString("PromptFilter");

    /**
     * Filter Parents Title.
     */
    private static final String FILTER_PARENTS = NLS_BUNDLE.getString("PromptFilterParents");

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
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The event entry.
     */
    private final transient UpdateEntry<LoanCategory, MoneyWiseDataType> theCategoryEntry;

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
     * The filter panel.
     */
    private final JEnablePanel theFilterPanel;

    /**
     * The select button.
     */
    private final JButton theSelectButton;

    /**
     * Loan Categories.
     */
    private transient LoanCategoryList theCategories = null;

    /**
     * Active parent.
     */
    private transient LoanCategory theParent = null;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected JPanel getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public LoanCategoryTable(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView);
        theCategoryEntry = theUpdateSet.registerClass(LoanCategory.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_MAINT);
        theDataCategories = myDataMgr.new JDataEntry(LoanCategoryTable.class.getSimpleName());
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

        /* Create the filter components */
        JLabel myPrompt = new JLabel(TITLE_FILTER);
        theSelectButton = new JButton(ArrowIcon.DOWN);
        theSelectButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSelectButton.setHorizontalTextPosition(AbstractButton.LEFT);
        theSelectButton.setText(FILTER_PARENTS);

        /* Create the filter panel */
        theFilterPanel = new JEnablePanel();
        theFilterPanel.setLayout(new BoxLayout(theFilterPanel, BoxLayout.X_AXIS));
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(myPrompt);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));
        theFilterPanel.add(theSelectButton);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theError);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);

        /* Create listener */
        CategoryListener myListener = new CategoryListener();
        theView.addChangeListener(myListener);
        theSelectButton.addActionListener(myListener);
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
        LoanCategoryList myCategories = myData.getLoanCategories();
        theCategories = myCategories.deriveEditList();
        setList(theCategories);
        theCategoryEntry.setDataList(theCategories);
        theSaveButtons.setEnabled(true);
        fireStateChanged();

        /* Touch the updateSet */
        theDataCategories.setObject(theUpdateSet);
    }

    @Override
    protected void setError(final JOceanusException pError) {
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
            extends JDataTableModel<LoanCategory, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 9092928242872012322L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryTableModel(final LoanCategoryTable pTable) {
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
        public JDataField getFieldForCell(final LoanCategory pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final LoanCategory pCategory,
                                      final int pColIndex) {
            return false;
        }

        @Override
        public LoanCategory getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theCategories.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final LoanCategory pCategory,
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
        public boolean includeRow(final LoanCategory pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return (theParent == null)
                                      ? pRow.isCategoryClass(LoanCategoryClass.PARENT)
                                      : theParent.equals(pRow.getParentCategory());
        }
    }

    /**
     * Listener class.
     */
    private final class CategoryListener
            implements ChangeListener, ActionListener {

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

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Show the select menu */
            if (theCategories != null) {
                showSelectMenu();
            }
        }

        /**
         * Show Select menu.
         */
        private void showSelectMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Record active item */
            JMenuItem myActive = null;

            /* Create the no filter JMenuItem and add it to the popUp */
            CategoryAction myAction = new CategoryAction(null, FILTER_PARENTS);
            JMenuItem myItem = new JMenuItem(myAction);
            myPopUp.addMenuItem(myItem);

            /* If this is the active parent */
            if (theParent == null) {
                /* Record it */
                myActive = myItem;
            }

            /* Loop through the available category values */
            Iterator<LoanCategory> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                LoanCategory myCurr = myIterator.next();

                /* Ignore category if it is not a subTotal */
                LoanCategoryClass myClass = myCurr.getCategoryTypeClass();
                if (!myClass.isParentCategory()) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                myAction = new CategoryAction(myCurr);
                myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);

                /* If this is the active parent */
                if (myCurr.equals(theParent)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            myPopUp.showItem(myActive);

            /* Show the Select menu in the correct place */
            Rectangle myLoc = theSelectButton.getBounds();
            myPopUp.show(theSelectButton, 0, myLoc.height);
        }
    }

    /**
     * Category action class.
     */
    private final class CategoryAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 8329779340136316097L;

        /**
         * Category.
         */
        private final LoanCategory theCategory;

        /**
         * Label.
         */
        private final String theLabel;

        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        private CategoryAction(final LoanCategory pCategory) {
            super(pCategory.getName());
            theCategory = pCategory;
            theLabel = pCategory.getName();
        }

        /**
         * Constructor.
         * @param pCategory the category bucket
         * @param pName the name
         */
        private CategoryAction(final LoanCategory pCategory,
                               final String pName) {
            super(pName);
            theCategory = pCategory;
            theLabel = pName;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If this is a different category */
            if (!Difference.isEqual(theCategory, theParent)) {
                /* Store new category */
                theParent = theCategory;
                theSelectButton.setText(theLabel);
                theModel.fireNewDataEvents();
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class CategoryColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 9146044145854186894L;

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
        private CategoryColumnModel(final LoanCategoryTable pTable) {
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
        protected Object getItemValue(final LoanCategory pCategory,
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
                    return TransactionCategory.FIELD_SUBCAT;
                case COLUMN_DESC:
                    return TransactionCategory.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return TransactionCategory.FIELD_CATTYPE;
                case COLUMN_FULLNAME:
                    return TransactionCategory.FIELD_NAME;
                case COLUMN_ACTIVE:
                    return TransactionCategory.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
