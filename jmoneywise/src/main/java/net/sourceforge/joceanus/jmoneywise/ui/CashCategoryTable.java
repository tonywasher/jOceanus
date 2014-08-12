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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.CashCategoryPanel;
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
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Cash Category Maintenance.
 */
public class CashCategoryTable
        extends JDataTable<CashCategory, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1041575446343563133L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(CashCategoryTable.class.getName());

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = CashCategory.FIELD_NAME.getName();

    /**
     * FullName Column Title.
     */
    private static final String TITLE_FULLNAME = NLS_BUNDLE.getString("TitleFullName");

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = CashCategory.FIELD_CATTYPE.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = CashCategory.FIELD_DESC.getName();

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
    private final transient UpdateEntry<CashCategory, MoneyWiseDataType> theCategoryEntry;

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
    private final JScrollButton<CashCategory> theSelectButton;

    /**
     * The new button.
     */
    private final JButton theNewButton;

    /**
     * The CashCategory dialog.
     */
    private final CashCategoryPanel theActiveCategory;

    /**
     * Cash Categories.
     */
    private transient CashCategoryList theCategories = null;

    /**
     * Cash Categories Types.
     */
    private transient CashCategoryTypeList theCategoryTypes = null;

    /**
     * Active parent.
     */
    private transient CashCategory theParent = null;

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
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveCategory.isEditing();
    }

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public CashCategoryTable(final View pView,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final ErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theCategoryEntry = theUpdateSet.registerType(MoneyWiseDataType.CASHCATEGORY);
        setUpdateSet(theUpdateSet);

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
        theSelectButton = new JScrollButton<CashCategory>();
        theSelectButton.setValue(null, FILTER_PARENTS);

        /* Create new button */
        theNewButton = new JButton(NLS_NEW);
        theNewButton.setVerticalTextPosition(AbstractButton.CENTER);
        theNewButton.setHorizontalTextPosition(AbstractButton.LEFT);

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

        /* Create a Category panel */
        theActiveCategory = new CashCategoryPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveCategory);

        /* Initialise the columns */
        theColumns.setColumns();

        /* Create listener */
        CategoryListener myListener = new CategoryListener();
        theUpdateSet.addChangeListener(myListener);
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theNewButton.addActionListener(myListener);

        /* Add selection listener */
        getSelectionModel().addListSelectionListener(myListener);
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
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Get the Category edit list */
        MoneyWiseData myData = theView.getData();
        theCategoryTypes = myData.getCashCategoryTypes();
        CashCategoryList myCategories = myData.getCashCategories();
        theCategories = myCategories.deriveEditList();
        theCategories.resolveUpdateSetLinks();
        theCategoryEntry.setDataList(theCategories);

        /* If we have a parent */
        if (theParent != null) {
            /* Update the parent via the edit list */
            theParent = theCategories.findItemById(theParent.getId());
            theSelectButton.setValue(theParent);
        }

        /* Notify panel of refresh */
        theActiveCategory.refreshData();

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

    @Override
    public void cancelEditing() {
        /* Cancel editing on table */
        super.cancelEditing();

        /* Stop editing any item */
        theActiveCategory.setEditable(false);
    }

    /**
     * Select category.
     * @param pCategory the category to select
     */
    protected void selectCategory(final CashCategory pCategory) {
        /* Ensure the correct parent is selected */
        CashCategory myParent = pCategory.getParentCategory();
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
    private void selectParent(final CashCategory pParent) {
        theParent = pParent;
        if (pParent == null) {
            theSelectButton.setValue(null, FILTER_PARENTS);
        } else {
            theSelectButton.setValue(pParent);
        }
        theColumns.setColumns();
        theModel.fireNewDataEvents();
        getSelectionModel().setSelectionInterval(0, 0);
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveCategory.isEditing());

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * JTable Data Model.
     */
    private final class CategoryTableModel
            extends JDataTableModel<CashCategory, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 1553565397986549529L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryTableModel(final CashCategoryTable pTable) {
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
        public JDataField getFieldForCell(final CashCategory pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final CashCategory pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public CashCategory getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theCategories.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final CashCategory pCategory,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pCategory, pColIndex);
        }

        @Override
        public void setItemValue(final CashCategory pItem,
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
        public boolean includeRow(final CashCategory pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return (theParent == null)
                                      ? pRow.isCategoryClass(CashCategoryClass.PARENT)
                                      : theParent.equals(pRow.getParentCategory());
        }
    }

    /**
     * Listener class.
     */
    private final class CategoryListener
            implements PropertyChangeListener, ChangeListener, ActionListener, ListSelectionListener {
        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<CashCategory> theCategoryMenuBuilder;

        /**
         * Constructor.
         */
        private CategoryListener() {
            /* Access builders */
            theCategoryMenuBuilder = theSelectButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theActiveCategory.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveCategory.isEditing()) {
                    /* Refresh the model */
                    theSelectButton.refreshText();
                    theModel.fireNewDataEvents();
                }

                /* Adjust for changes */
                notifyChanges();
            }

            /* If we are building selection menu */
            if (theCategoryMenuBuilder.equals(o)) {
                /* Reset the popUp menu */
                theCategoryMenuBuilder.clearMenu();

                /* Build the selection menu */
                if (theCategories != null) {
                    buildSelectMenu();
                }
            }

            /* If we are noting change of edit state */
            if (theActiveCategory.equals(o)) {
                /* If the category is now deleted */
                if (theActiveCategory.isItemDeleted()) {
                    /* Refresh the model */
                    theModel.fireNewDataEvents();
                }

                /* Note changes */
                notifyChanges();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the new button */
            if (theNewButton.equals(o)) {
            }
        }

        @Override
        public void valueChanged(final ListSelectionEvent pEvent) {
            /* If we have finished selecting */
            if (!pEvent.getValueIsAdjusting()) {
                /* Access selection model */
                ListSelectionModel myModel = getSelectionModel();
                if (!myModel.isSelectionEmpty()) {
                    /* Loop through the indices */
                    int iIndex = myModel.getMinSelectionIndex();
                    iIndex = convertRowIndexToModel(iIndex);
                    CashCategory myCategory = theCategories.get(iIndex);
                    theActiveCategory.setItem(myCategory);
                } else {
                    theActiveCategory.setEditable(false);
                    theActiveCategory.setItem(null);
                    notifyChanges();
                }
            }
        }

        /**
         * Build Select menu.
         */
        private void buildSelectMenu() {
            /* Record active item */
            JMenuItem myActive = null;

            /* Create the filter parents JMenuItem and add it to the popUp */
            JMenuItem myItem = theCategoryMenuBuilder.addItem(null, FILTER_PARENTS);

            /* If this is the active parent */
            if (theParent == null) {
                /* Record it */
                myActive = myItem;
            }

            /* Loop through the available category values */
            Iterator<CashCategory> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                CashCategory myCurr = myIterator.next();
                CashCategoryType myType = myCurr.getCategoryType();

                /* Ignore deleted */
                boolean bIgnore = myCurr.isDeleted();

                /* Ignore category if it is a parent */
                bIgnore |= !myType.getCashClass().isParentCategory();
                if (bIgnore) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                myItem = theCategoryMenuBuilder.addItem(myCurr);

                /* If this is the active parent */
                if (myCurr.equals(theParent)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theCategoryMenuBuilder.showItem(myActive);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent pEvent) {
            /* Access the source */
            Object o = pEvent.getSource();

            /* If this is the select button */
            if (theSelectButton.equals(o)) {
                /* If this is a different category */
                CashCategory myCategory = theSelectButton.getValue();
                if (!Difference.isEqual(myCategory, theParent)) {
                    /* Store new category */
                    selectParent(myCategory);
                }
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
        private static final long serialVersionUID = 687691967421901027L;

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
        private final IconButtonCellRenderer<Boolean> theIconRenderer;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theIconEditor;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<CashCategoryType> theScrollEditor;

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
        private CategoryColumnModel(final CashCategoryTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters/editors */
            theIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theScrollEditor = theFieldMgr.allocateScrollButtonCellEditor(CashCategoryType.class);
            theIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theIconEditor);
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            theFullNameColumn = new JDataTableColumn(COLUMN_FULLNAME, WIDTH_NAME, theStringRenderer);
            declareColumn(theFullNameColumn);
            theCategoryColumn = new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theScrollEditor);
            declareColumn(theCategoryColumn);
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theIconRenderer, theIconEditor));

            /* Add listener */
            theScrollEditor.addChangeListener(new ScrollEditorListener());
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
        protected Object getItemValue(final CashCategory pCategory,
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
                    return pCategory.isActive();
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
        private void setItemValue(final CashCategory pItem,
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
                    pItem.setCategoryType((CashCategoryType) pValue);
                    break;
                case COLUMN_ACTIVE:
                    deleteRow(pItem);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final CashCategory pItem,
                                       final int pColIndex) {
            /* Switch on column */
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
                    return CashCategory.FIELD_SUBCAT;
                case COLUMN_DESC:
                    return CashCategory.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return CashCategory.FIELD_CATTYPE;
                case COLUMN_FULLNAME:
                    return CashCategory.FIELD_NAME;
                case COLUMN_ACTIVE:
                    return CashCategory.FIELD_TOUCH;
                default:
                    return null;
            }
        }

        /**
         * ScrollEditorListener.
         */
        private class ScrollEditorListener
                implements ChangeListener {
            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                buildCategoryTypeMenu();
            }

            /**
             * Build the category type list for the item.
             */
            private void buildCategoryTypeMenu() {
                /* Access details */
                JScrollMenuBuilder<CashCategoryType> myBuilder = theScrollEditor.getMenuBuilder();
                Point myCell = theScrollEditor.getPoint();
                myBuilder.clearMenu();

                /* Record active item */
                CashCategory myCategory = theCategories.get(myCell.y);
                CashCategoryType myCurr = myCategory.getCategoryType();
                JMenuItem myActive = null;

                /* Loop through the CashCategoryTypes */
                Iterator<CashCategoryType> myIterator = theCategoryTypes.iterator();
                while (myIterator.hasNext()) {
                    CashCategoryType myType = myIterator.next();

                    /* Ignore deleted or disabled */
                    boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

                    /* Ignore category if it is a parent */
                    bIgnore |= myType.getCashClass().isParentCategory();
                    if (bIgnore) {
                        continue;
                    }

                    /* Create a new action for the type */
                    JMenuItem myItem = myBuilder.addItem(myType);

                    /* If this is the active type */
                    if (myType.equals(myCurr)) {
                        /* Record it */
                        myActive = myItem;
                    }
                }

                /* Ensure active item is visible */
                myBuilder.showItem(myActive);
            }
        }
    }
}
