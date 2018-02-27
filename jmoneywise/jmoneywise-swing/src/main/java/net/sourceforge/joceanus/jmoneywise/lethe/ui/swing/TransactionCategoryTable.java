/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldStringCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.TransactionCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.TransactionCategoryPanel.CategoryType;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusAction;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableSelection;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingLabel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Transaction Category Maintenance.
 */
public class TransactionCategoryTable
        extends PrometheusDataTable<TransactionCategory, MoneyWiseDataType> {
    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = TransactionCategory.FIELD_NAME.getName();

    /**
     * FullName Column Title.
     */
    private static final String TITLE_FULLNAME = MoneyWiseUIResource.CATEGORY_COLUMN_FULLNAME.getValue();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = TransactionCategory.FIELD_CATTYPE.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = TransactionCategory.FIELD_DESC.getName();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * Filter Prompt.
     */
    private static final String TITLE_FILTER = MoneyWiseUIResource.CATEGORY_PROMPT_FILTER.getValue();

    /**
     * Filter All Title.
     */
    private static final String FILTER_ALL = MoneyWiseUIResource.CATEGORY_FILTER_SHOWALL.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The event entry.
     */
    private final UpdateEntry<TransactionCategory, MoneyWiseDataType> theCategoryEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

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
    private final JPanel thePanel;

    /**
     * The filter panel.
     */
    private final TethysSwingBoxPaneManager theFilterPanel;

    /**
     * The select button.
     */
    private final TethysSwingScrollButtonManager<TransactionCategory> theSelectButton;

    /**
     * The new button.
     */
    private final TethysSwingButton theNewButton;

    /**
     * The TransactionCategory dialog.
     */
    private final TransactionCategoryPanel theActiveCategory;

    /**
     * The List Selection Model.
     */
    private final PrometheusDataTableSelection<TransactionCategory, MoneyWiseDataType> theSelectionModel;

    /**
     * Category menu builder.
     */
    private final TethysScrollMenu<TransactionCategory, ?> theCategoryMenu;

    /**
     * Event Categories.
     */
    private TransactionCategoryList theCategories;

    /**
     * Active parent.
     */
    private TransactionCategory theParent;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionCategoryTable(final SwingView pView,
                                    final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                    final MetisErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theCategoryEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSCATEGORY);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new CategoryTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new CategoryColumnModel(this);
        final JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create new button */
        final TethysSwingGuiFactory myFactory = pView.getGuiFactory();
        theNewButton = myFactory.newButton();
        PrometheusIcon.configureNewIconButton(theNewButton);

        /* Create the filter components */
        final TethysSwingLabel myPrompt = myFactory.newLabel(TITLE_FILTER);
        theSelectButton = myFactory.newScrollButton();
        theSelectButton.setValue(null, FILTER_ALL);

        /* Create the filter panel */
        theFilterPanel = myFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myPrompt);
        theFilterPanel.addNode(theSelectButton);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theNewButton);

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create a Category panel */
        theActiveCategory = new TransactionCategoryPanel(myFactory, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveCategory.getNode(), BorderLayout.PAGE_END);

        /* Initialise the columns */
        theColumns.setColumns();

        /* Create the selection model */
        theSelectionModel = new PrometheusDataTableSelection<>(this, theActiveCategory);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);
        theCategoryMenu = theSelectButton.getMenu();

        /* Listen to swing events */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theSelectButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleCategorySelection());
        theSelectButton.setMenuConfigurator(e -> buildSelectMenu());
        theNewButton.getEventRegistrar().addEventListener(e -> theModel.addNewItem());
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysSwingBoxPaneManager getFilterPanel() {
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
        myTask = myTask.startTask("Transactions");

        /* Get the Category edit list */
        final MoneyWiseData myData = theView.getData();
        final TransactionCategoryList myCategories = myData.getTransCategories();
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
    protected void selectCategory(final TransactionCategory pCategory) {
        /* Obtain the parent of the category */
        TransactionCategory myParent = pCategory.getParentCategory();

        /* If we have a changed category */
        if (!MetisDataDifference.isEqual(myParent, theParent)) {
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
    private void selectParent(final TransactionCategory pParent) {
        theParent = pParent;
        if (pParent == null) {
            theSelectButton.setValue(null, FILTER_ALL);
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
        theNewButton.setVisible(theParent != null);

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Handle deposit selection.
     */
    private void handleCategorySelection() {
        final TransactionCategory myCategory = theSelectButton.getValue();
        if (!MetisDataDifference.isEqual(myCategory, theParent)) {
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
        TethysScrollMenuItem<TransactionCategory> myActive = null;

        /* Create the filter parents MenuItem and add it to the popUp */
        TethysScrollMenuItem<TransactionCategory> myItem = theCategoryMenu.addItem(null, FILTER_ALL);

        /* If this is the active parent */
        if (theParent == null) {
            /* Record it */
            myActive = myItem;
        }

        /* Create the totals MenuItem and add it to the popUp */
        final TransactionCategory myTotals = theCategories.getSingularClass(TransactionCategoryClass.TOTALS);
        myItem = theCategoryMenu.addItem(myTotals);

        /* If this is the active parent */
        if (myTotals.equals(theParent)) {
            /* Record it */
            myActive = myItem;
        }

        /* Loop through the available category values */
        final Iterator<TransactionCategory> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategory myCurr = myIterator.next();

            /* Ignore category if it is not a subTotal */
            final TransactionCategoryClass myClass = myCurr.getCategoryTypeClass();
            if (!myClass.isSubTotal()) {
                continue;
            }

            /* Create a new JMenuItem and add it to the popUp */
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
            extends PrometheusDataTableModel<TransactionCategory, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -3367619290052755129L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryTableModel(final TransactionCategoryTable pTable) {
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
        public MetisField getFieldForCell(final TransactionCategory pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final TransactionCategory pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public TransactionCategory getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theCategories.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final TransactionCategory pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final TransactionCategory pItem,
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
        public boolean includeRow(final TransactionCategory pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return (theParent == null)
                                       ? true
                                       : theParent.equals(pRow.getParentCategory());
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new category */
                final TransactionCategory myCategory = new TransactionCategory(theCategories);
                myCategory.setDefaults(theParent);

                /* Add the new item */
                myCategory.setNewVersion();
                theCategories.add(myCategory);

                /* Validate the new item and notify of the changes */
                myCategory.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveCategory.setNewItem(myCategory);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                final OceanusException myError = new MoneyWiseDataException("Failed to create new category", e);

                /* Show the error */
                setError(myError);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class CategoryColumnModel
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5129198935581030200L;

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
         * FullName column.
         */
        private final PrometheusDataTableColumn theFullNameColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryColumnModel(final TransactionCategoryTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            final MetisFieldIconButtonCellEditor<PrometheusAction> myIconEditor = theFieldMgr.allocateIconButtonCellEditor(PrometheusAction.class);
            final MetisFieldStringCellEditor myStringEditor = theFieldMgr.allocateStringCellEditor();
            final MetisFieldScrollButtonCellEditor<TransactionCategoryType> myScrollEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionCategoryType.class);
            final MetisFieldIconButtonCellRenderer<PrometheusAction> myIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(PrometheusAction.class);
            final MetisFieldStringCellRenderer myStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButton */
            final TethysIconMapSet<PrometheusAction> myActionMapSet = PrometheusIcon.configureStatusIconButton();
            myIconRenderer.setIconMapSet(r -> myActionMapSet);
            myIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_NAME, WIDTH_NAME, myStringRenderer, myStringEditor));
            theFullNameColumn = new PrometheusDataTableColumn(COLUMN_FULLNAME, WIDTH_NAME, myStringRenderer);
            declareColumn(theFullNameColumn);
            declareColumn(new PrometheusDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, myStringRenderer, myScrollEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_DESC, WIDTH_NAME, myStringRenderer, myStringEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, myIconRenderer, myIconEditor));

            /* Add configurator */
            myScrollEditor.setMenuConfigurator(this::buildCategoryTypeMenu);
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on parent */
            if ((theParent == null)
                || !theParent.isCategoryClass(TransactionCategoryClass.TOTALS)) {
                revealColumn(theFullNameColumn);
            } else {
                hideColumn(theFullNameColumn);
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
        protected Object getItemValue(final TransactionCategory pCategory,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    final String mySubCat = pCategory.getSubCategory();
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
                                                ? PrometheusAction.ACTIVE
                                                : PrometheusAction.DELETE;
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
        private void setItemValue(final TransactionCategory pItem,
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
                    pItem.setCategoryType((TransactionCategoryType) pValue);
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
        private boolean isCellEditable(final TransactionCategory pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_CATEGORY:
                    return pItem.isActive()
                                            ? false
                                            : CategoryType.determineType(pItem).isChangeable();
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

        /**
         * Build the category type list for the item.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildCategoryTypeMenu(final Integer pRowIndex,
                                           final TethysScrollMenu<TransactionCategoryType, Icon> pMenu) {
            /* Record active item */
            final TransactionCategory myCategory = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveCategory.buildCategoryTypeMenu(pMenu, myCategory);
        }
    }
}
