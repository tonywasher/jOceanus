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
import java.awt.Point;
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
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.PopUpMenuCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.PopUpMenuCellEditor.PopUpAction;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.PopUpMenuSelector;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
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
        extends JDataTable<LoanCategory, MoneyWiseDataType>
        implements PopUpMenuSelector {
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
     * Text for New Button.
     */
    private static final String NLS_NEW = NLS_BUNDLE.getString("NewButton");

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
     * The new button.
     */
    private final JButton theNewButton;

    /**
     * Loan Categories.
     */
    private transient LoanCategoryList theCategories = null;

    /**
     * Loan Categories Types.
     */
    private transient LoanCategoryTypeList theCategoryTypes = null;

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
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanCategoryTable(final View pView,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final ErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Create listener */
        CategoryListener myListener = new CategoryListener();

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theCategoryEntry = theUpdateSet.registerClass(LoanCategory.class);
        setUpdateSet(theUpdateSet);
        theUpdateSet.addChangeListener(myListener);

        /* Create the table model */
        theModel = new CategoryTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new CategoryColumnModel(this);
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the filter components */
        JLabel myPrompt = new JLabel(TITLE_FILTER);
        theSelectButton = new JButton(ArrowIcon.DOWN);
        theSelectButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSelectButton.setHorizontalTextPosition(AbstractButton.LEFT);
        theSelectButton.setText(FILTER_PARENTS);
        theSelectButton.addActionListener(myListener);

        /* Create new button */
        theNewButton = new JButton(NLS_NEW);
        theNewButton.setVerticalTextPosition(AbstractButton.CENTER);
        theNewButton.setHorizontalTextPosition(AbstractButton.LEFT);
        theNewButton.addActionListener(myListener);

        /* Create the filter panel */
        theFilterPanel = new JEnablePanel();
        theFilterPanel.setLayout(new BoxLayout(theFilterPanel, BoxLayout.X_AXIS));
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(myPrompt);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));
        theFilterPanel.add(theSelectButton);
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(theNewButton);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());

        /* Initialise the columns */
        theColumns.setColumns();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theCategoryEntry.getName());
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Get the Category edit list */
        MoneyWiseData myData = theView.getData();
        theCategoryTypes = myData.getLoanCategoryTypes();
        LoanCategoryList myCategories = myData.getLoanCategories();
        theCategories = myCategories.deriveEditList();
        theCategoryEntry.setDataList(theCategories);

        /* If we have a parent */
        if (theParent != null) {
            /* Update the parent via the edit list */
            theParent = theCategories.findItemById(theParent.getId());
            theSelectButton.setText(theParent.getName());
        }

        /* Notify of the change */
        setList(theCategories);
        fireStateChanged();
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
     * Select category.
     * @param pCategory the category to select
     */
    protected void selectCategory(final LoanCategory pCategory) {
        /* Ensure the correct parent is selected */
        LoanCategory myParent = pCategory.getParentCategory();
        if (!myParent.equals(theParent)) {
            myParent = theCategories.findItemById(myParent.getId());
            selectParent(myParent);
        }

        /* Find the item in the list */
        int myIndex = theCategories.indexOf(pCategory);
        myIndex = convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    /**
     * Select parent.
     * @param pParent the parent category
     */
    private void selectParent(final LoanCategory pParent) {
        theParent = pParent;
        theSelectButton.setText(pParent == null
                                               ? FILTER_PARENTS
                                               : pParent.getName());
        theColumns.setColumns();
        theModel.fireNewDataEvents();
    }

    @Override
    public JPopupMenu getPopUpMenu(final PopUpMenuCellEditor pEditor,
                                   final int pRowIndex,
                                   final int pColIndex) {
        /* Create new menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Record active item */
        LoanCategory myCategory = theCategories.get(pRowIndex);
        LoanCategoryType myCurr = myCategory.getCategoryType();
        JMenuItem myActive = null;

        /* Loop through the LoanCategoryTypes */
        Iterator<LoanCategoryType> myIterator = theCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            LoanCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getLoanClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            PopUpAction myAction = pEditor.getNewAction(myType);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        myMenu.showItem(myActive);

        /* Return the menu */
        return myMenu;
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
                                       : theColumns.getDeclaredCount();
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
        public boolean isCellEditable(final LoanCategory pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public LoanCategory getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theCategories.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final LoanCategory pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final LoanCategory pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
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

        @Override
        public Object buttonClick(final Point pCell) {
            /* Access the item */
            LoanCategory myItem = getItemAtIndex(pCell.y);

            /* Process the click */
            return theColumns.buttonClick(myItem, pCell.x);
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

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the select button */
            if (theSelectButton.equals(o)) {
                /* Show the select menu */
                if (theCategories != null) {
                    showSelectMenu();
                }
            }

            /* If this is the new button */
            if (theNewButton.equals(o)) {
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
            CategoryAction myAction = new CategoryAction(FILTER_PARENTS);
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
                LoanCategoryType myType = myCurr.getCategoryType();

                /* Ignore deleted */
                boolean bIgnore = myCurr.isDeleted();

                /* Ignore category if it is not a parent */
                bIgnore |= !myType.getLoanClass().isParentCategory();
                if (bIgnore) {
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
         * Constructor.
         * @param pCategory the category bucket
         */
        private CategoryAction(final LoanCategory pCategory) {
            super(pCategory.getName());
            theCategory = pCategory;
        }

        /**
         * Constructor.
         * @param pName the name
         */
        private CategoryAction(final String pName) {
            super(pName);
            theCategory = null;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If this is a different category */
            if (!Difference.isEqual(theCategory, theParent)) {
                /* Store new category */
                selectParent(theCategory);
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
         * FullName column id.
         */
        private static final int COLUMN_FULLNAME = 1;

        /**
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 2;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 3;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 4;

        /**
         * Icon Renderer.
         */
        private final IconCellRenderer theIconRenderer;

        /**
         * Icon editor.
         */
        private final IconCellEditor theIconEditor;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * PopUp Menu Editor.
         */
        private final PopUpMenuCellEditor theMenuEditor;

        /**
         * FullName column.
         */
        private final JDataTableColumn theFullNameColumn;

        /**
         * Category column.
         */
        private final JDataTableColumn theCategoryColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryColumnModel(final LoanCategoryTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theIconRenderer = theFieldMgr.allocateIconCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theIconEditor = theFieldMgr.allocateIconCellEditor(pTable);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theMenuEditor = theFieldMgr.allocatePopUpMenuCellEditor();

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            theFullNameColumn = new JDataTableColumn(COLUMN_FULLNAME, WIDTH_NAME, theStringRenderer);
            declareColumn(theFullNameColumn);
            theCategoryColumn = new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theMenuEditor);
            declareColumn(theCategoryColumn);
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theIconRenderer, theIconEditor));
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on parent */
            if (theParent != null) {
                revealColumn(theFullNameColumn);
                revealColumn(theCategoryColumn);
            } else {
                hideColumn(theFullNameColumn);
                hideColumn(theCategoryColumn);
            }
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
                    String mySubCat = pCategory.getSubCategory();
                    return (mySubCat == null)
                                             ? pCategory.getName()
                                             : mySubCat;
                case COLUMN_FULLNAME:
                    return pCategory.getName();
                case COLUMN_CATEGORY:
                    return pCategory.getCategoryType();
                case COLUMN_DESC:
                    return pCategory.getDesc();
                case COLUMN_ACTIVE:
                    return pCategory.isActive()
                                               ? ICON_ACTIVE
                                               : ICON_DELETE;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws JOceanusException on error
         */
        private void setItemValue(final LoanCategory pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setSubCategoryName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pItem.setCategoryType((LoanCategoryType) pValue);
                    break;
                default:
                    break;
            }
        }

        /**
         * Handle a button click.
         * @param pItem the item
         * @param pColIndex the column
         * @return the new object
         */
        private Object buttonClick(final LoanCategory pItem,
                                   final int pColIndex) {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_ACTIVE:
                    deleteRow(pItem);
                    return null;
                default:
                    return null;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final LoanCategory pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_CATEGORY:
                case COLUMN_ACTIVE:
                    return !pItem.isActive();
                default:
                    return false;
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
                    return LoanCategory.FIELD_SUBCAT;
                case COLUMN_DESC:
                    return LoanCategory.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return LoanCategory.FIELD_CATTYPE;
                case COLUMN_FULLNAME:
                    return LoanCategory.FIELD_NAME;
                case COLUMN_ACTIVE:
                    return LoanCategory.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
