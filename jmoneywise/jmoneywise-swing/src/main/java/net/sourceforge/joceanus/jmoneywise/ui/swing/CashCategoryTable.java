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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.CashCategoryPanel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusSwingErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Cash Category Maintenance.
 */
public class CashCategoryTable
        extends JDataTable<CashCategory, MoneyWiseDataType> {
    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = CashCategory.FIELD_NAME.getName();

    /**
     * FullName Column Title.
     */
    private static final String TITLE_FULLNAME = MoneyWiseUIResource.CATEGORY_COLUMN_FULLNAME.getValue();

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
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * Filter Prompt.
     */
    private static final String TITLE_FILTER = MoneyWiseUIResource.CATEGORY_PROMPT_FILTER.getValue();

    /**
     * Filter Parents Title.
     */
    private static final String FILTER_PARENTS = MoneyWiseUIResource.CATEGORY_FILTER_PARENT.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The event entry.
     */
    private final UpdateEntry<CashCategory, MoneyWiseDataType> theCategoryEntry;

    /**
     * The error panel.
     */
    private final PrometheusSwingErrorPanel theError;

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
    private final TethysSwingEnablePanel thePanel;

    /**
     * The filter panel.
     */
    private final TethysSwingEnablePanel theFilterPanel;

    /**
     * The select button.
     */
    private final TethysSwingScrollButtonManager<CashCategory> theSelectButton;

    /**
     * The new button.
     */
    private final TethysSwingScrollButtonManager<String> theNewButton;

    /**
     * The CashCategory dialog.
     */
    private final CashCategoryPanel theActiveCategory;

    /**
     * The List Selection Model.
     */
    private final JDataTableSelection<CashCategory, MoneyWiseDataType> theSelectionModel;

    /**
     * Category menu builder.
     */
    private final TethysScrollMenu<CashCategory, ?> theCategoryMenu;

    /**
     * Cash Categories.
     */
    private CashCategoryList theCategories;

    /**
     * Active parent.
     */
    private CashCategory theParent;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public CashCategoryTable(final SwingView pView,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final PrometheusSwingErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldManager();
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
        JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the filter components */
        JLabel myPrompt = new JLabel(TITLE_FILTER);
        theSelectButton = new TethysSwingScrollButtonManager<>();
        theSelectButton.setValue(null, FILTER_PARENTS);

        /* Create new button */
        theNewButton = new TethysSwingScrollButtonManager<>();
        PrometheusIcon.configureNewScrollButton(theNewButton);

        /* Create the filter panel */
        theFilterPanel = new TethysSwingEnablePanel();
        theFilterPanel.setLayout(new BoxLayout(theFilterPanel, BoxLayout.X_AXIS));
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(myPrompt);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));
        theFilterPanel.add(theSelectButton.getNode());
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(theNewButton.getNode());
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create a Category panel */
        theActiveCategory = new CashCategoryPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveCategory.getNode(), BorderLayout.PAGE_END);

        /* Initialise the columns */
        theColumns.setColumns();

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<>(this, theActiveCategory);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);
        theCategoryMenu = theSelectButton.getMenu();

        /* Listen to swing events */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleCashSelection());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildSelectMenu());
        theNewButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theModel.addNewItem());
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysSwingEnablePanel getFilterPanel() {
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
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theCategoryEntry.getName());
    }

    /**
     * Refresh data.
     * @throws OceanusException on error
     */
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Cash");

        /* Get the Category edit list */
        MoneyWiseData myData = theView.getData();
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

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    @Override
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
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
        if (!MetisDifference.isEqual(theParent, myParent)) {
            if (myParent != null) {
                myParent = theCategories.findItemById(myParent.getId());
            }
            selectParent(myParent);
        }

        /* Find the item in the list */
        int myIndex = theCategories.indexOf(pCategory);
        myIndex = getTable().convertRowIndexToView(myIndex);
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
        theSelectionModel.handleNewFilter();
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveCategory.isEditing());

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Handle cash selection.
     */
    private void handleCashSelection() {
        CashCategory myCategory = theSelectButton.getValue();
        if (!MetisDifference.isEqual(myCategory, theParent)) {
            /* Store new category */
            selectParent(myCategory);
        }
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCategory.isEditing()) {
            /* Handle the reWind */
            theSelectButton.refreshText();
            theSelectionModel.handleReWind();
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveCategory.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Build Select menu.
     */
    private void buildSelectMenu() {
        /* Clear the menu */
        theCategoryMenu.removeAllItems();

        /* Cope if we have no categories */
        if (theCategories == null) {
            return;
        }

        /* Record active item */
        TethysScrollMenuItem<CashCategory> myActive = null;

        /* Create the filter parents JMenuItem and add it to the popUp */
        TethysScrollMenuItem<CashCategory> myItem = theCategoryMenu.addItem(null, FILTER_PARENTS);

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

            /* Ignore category if it is not a parent */
            bIgnore |= !myType.getCashClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            myItem = theCategoryMenu.addItem(myCurr);

            /* If this is the active parent */
            if (myCurr.equals(theParent)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
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
        public MetisField getFieldForCell(final CashCategory pItem,
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
                                 final Object pValue) throws OceanusException {
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

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new category */
                CashCategory myCategory = new CashCategory(theCategories);
                myCategory.setDefaults(theParent);

                /* Add the new item */
                myCategory.setNewVersion();
                theCategories.append(myCategory);

                /* Validate the new item and notify of the changes */
                myCategory.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveCategory.setNewItem(myCategory);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                OceanusException myError = new JMoneyWiseDataException("Failed to create new category", e);

                /* Show the error */
                setError(myError);
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
        private final IconButtonCellRenderer<ActionType> theIconRenderer;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theIconEditor;

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
            theIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
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
            theScrollEditor.getEventRegistrar().addEventListener(e -> buildCategoryTypeMenu());
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
                    return pCategory.isActive()
                                                ? ActionType.ACTIVE
                                                : ActionType.DELETE;
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws OceanusException on error
         */
        private void setItemValue(final CashCategory pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
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
                    pItem.setDeleted(true);
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
        protected MetisField getFieldForCell(final int pColIndex) {
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
         * Build the category type list for the item.
         */
        private void buildCategoryTypeMenu() {
            /* Access details */
            JScrollMenuBuilder<CashCategoryType> myBuilder = theScrollEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theScrollEditor.getPoint();
            CashCategory myCategory = theCategories.get(myCell.y);

            /* Build the menu */
            theActiveCategory.buildCategoryTypeMenu(myBuilder, myCategory);
        }
    }
}
