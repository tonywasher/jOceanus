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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.TransactionCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.TransactionCategoryPanel.CategoryType;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;

/**
 * Transaction Category Maintenance.
 */
public class TransactionCategoryTable
        extends JDataTable<TransactionCategory, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3913303076200887840L;

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
    private final transient SwingView theView;

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
    private final transient UpdateEntry<TransactionCategory, MoneyWiseDataType> theCategoryEntry;

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
    private final TethysSwingEnablePanel thePanel;

    /**
     * The filter panel.
     */
    private final TethysSwingEnablePanel theFilterPanel;

    /**
     * The select button.
     */
    private final JScrollButton<TransactionCategory> theSelectButton;

    /**
     * The new button.
     */
    private final JButton theNewButton;

    /**
     * The TransactionCategory dialog.
     */
    private final TransactionCategoryPanel theActiveCategory;

    /**
     * The List Selection Model.
     */
    private final transient JDataTableSelection<TransactionCategory, MoneyWiseDataType> theSelectionModel;

    /**
     * Event Categories.
     */
    private transient TransactionCategoryList theCategories = null;

    /**
     * Active parent.
     */
    private transient TransactionCategory theParent = null;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionCategoryTable(final SwingView pView,
                                    final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                    final ErrorPanel pError) {
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
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the filter components */
        JLabel myPrompt = new JLabel(TITLE_FILTER);
        theSelectButton = new JScrollButton<TransactionCategory>();
        theSelectButton.setValue(null, FILTER_ALL);

        /* Create new button */
        theNewButton = MoneyWiseIcons.getNewButton();
        theNewButton.setVisible(false);

        /* Create the filter panel */
        theFilterPanel = new TethysSwingEnablePanel();
        theFilterPanel.setLayout(new BoxLayout(theFilterPanel, BoxLayout.X_AXIS));
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(myPrompt);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));
        theFilterPanel.add(theSelectButton);
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(theNewButton);
        theFilterPanel.add(Box.createRigidArea(new Dimension(CategoryPanel.STRUT_WIDTH, 0)));

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());

        /* Create a Category panel */
        theActiveCategory = new TransactionCategoryPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveCategory);

        /* Initialise the columns */
        theColumns.setColumns();

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<TransactionCategory, MoneyWiseDataType>(this, theActiveCategory);

        /* Create listener */
        new CategoryListener();
    }

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
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final ViewerEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theCategoryEntry.getName());
    }

    /**
     * Refresh data.
     * @throws OceanusException on error
     */
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Transactions");

        /* Get the Category edit list */
        MoneyWiseData myData = theView.getData();
        TransactionCategoryList myCategories = myData.getTransCategories();
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
        if (!Difference.isEqual(myParent, theParent)) {
            if (myParent != null) {
                myParent = theCategories.findItemById(myParent.getId());
            }
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
     * JTable Data Model.
     */
    private final class CategoryTableModel
            extends JDataTableModel<TransactionCategory, MoneyWiseDataType> {
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
        public JDataField getFieldForCell(final TransactionCategory pItem,
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
                TransactionCategory myCategory = new TransactionCategory(theCategories);
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
     * Listener class.
     */
    private final class CategoryListener
            implements PropertyChangeListener, ActionListener, TethysActionEventListener, TethysChangeEventListener {
        /**
         * UpdateSet Registration.
         */
        private final TethysChangeRegistration theUpdateSetReg;

        /**
         * Category menu builder.
         */
        private final JScrollMenuBuilder<TransactionCategory> theCategoryMenuBuilder;

        /**
         * CategoryMenu Registration.
         */
        private final TethysChangeRegistration theCategoryMenuReg;

        /**
         * Category Change Registration.
         */
        private final TethysChangeRegistration theCatPanelReg;

        /**
         * Constructor.
         */
        private CategoryListener() {
            /* Access builders */
            theCategoryMenuBuilder = theSelectButton.getMenuBuilder();

            /* Register listeners */
            theUpdateSetReg = theUpdateSet.getEventRegistrar().addChangeListener(this);
            theCategoryMenuReg = theCategoryMenuBuilder.getEventRegistrar().addChangeListener(this);
            theCatPanelReg = theActiveCategory.getEventRegistrar().addChangeListener(this);
            theActiveCategory.getEventRegistrar().addActionListener(this);

            /* Listen to swing events */
            theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theNewButton.addActionListener(this);
        }

        @Override
        public void processChangeEvent(final TethysChangeEvent pEvent) {
            /* If we are performing a rewind */
            if (theUpdateSetReg.isRelevant(pEvent)) {
                /* Only action if we are not editing */
                if (!theActiveCategory.isEditing()) {
                    /* Handle the reWind */
                    theSelectButton.refreshText();
                    theSelectionModel.handleReWind();
                }

                /* Adjust for changes */
                notifyChanges();

                /* If we are building selection menu */
            } else if (theCategoryMenuReg.isRelevant(pEvent)) {
                /* Reset the popUp menu */
                theCategoryMenuBuilder.clearMenu();

                /* Build the selection menu */
                if (theCategories != null) {
                    buildSelectMenu();
                }

                /* If we are handling panel state */
            } else if (theCatPanelReg.isRelevant(pEvent)) {
                /* Only action if we are not editing */
                if (!theActiveCategory.isEditing()) {
                    /* handle the edit transition */
                    theSelectionModel.handleEditTransition();
                }

                /* Note changes */
                notifyChanges();
            }
        }

        @Override
        public void processActionEvent(final TethysActionEvent pEvent) {
            cascadeActionEvent(pEvent);
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* Handle actions */
            if (theNewButton.equals(o)) {
                theModel.addNewItem();
            }
        }

        /**
         * Build Select menu.
         */
        private void buildSelectMenu() {
            /* Record active item */
            JMenuItem myActive = null;

            /* Create the filter parents JMenuItem and add it to the popUp */
            JMenuItem myItem = theCategoryMenuBuilder.addItem(null, FILTER_ALL);

            /* If this is the active parent */
            if (theParent == null) {
                /* Record it */
                myActive = myItem;
            }

            /* Create the totals JMenuItem and add it to the popUp */
            TransactionCategory myTotals = theCategories.getSingularClass(TransactionCategoryClass.TOTALS);
            myItem = theCategoryMenuBuilder.addItem(myTotals);

            /* If this is the active parent */
            if (myTotals.equals(theParent)) {
                /* Record it */
                myActive = myItem;
            }

            /* Loop through the available category values */
            Iterator<TransactionCategory> myIterator = theCategories.iterator();
            while (myIterator.hasNext()) {
                TransactionCategory myCurr = myIterator.next();

                /* Ignore category if it is not a subTotal */
                TransactionCategoryClass myClass = myCurr.getCategoryTypeClass();
                if (!myClass.isSubTotal()) {
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
                TransactionCategory myCategory = theSelectButton.getValue();
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
        private final ScrollButtonCellEditor<TransactionCategoryType> theScrollEditor;

        /**
         * FullName column.
         */
        private final JDataTableColumn theFullNameColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CategoryColumnModel(final TransactionCategoryTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theScrollEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionCategoryType.class);
            theIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theIconEditor);
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButton */
            MoneyWiseIcons.buildStatusButton(theIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            theFullNameColumn = new JDataTableColumn(COLUMN_FULLNAME, WIDTH_NAME, theStringRenderer);
            declareColumn(theFullNameColumn);
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theScrollEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theIconRenderer, theIconEditor));

            /* Add listener */
            new EditorListener();
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

        /**
         * EditorListener.
         */
        private final class EditorListener
                implements TethysChangeEventListener {
            /**
             * Constructor.
             */
            private EditorListener() {
                theScrollEditor.getEventRegistrar().addChangeListener(this);
            }

            @Override
            public void processChangeEvent(final TethysChangeEvent pEvent) {
                buildCategoryTypeMenu();
            }

            /**
             * Build the category type list for the item.
             */
            private void buildCategoryTypeMenu() {
                /* Access details */
                JScrollMenuBuilder<TransactionCategoryType> myBuilder = theScrollEditor.getMenuBuilder();

                /* Record active item */
                Point myCell = theScrollEditor.getPoint();
                TransactionCategory myCategory = theCategories.get(myCell.y);

                /* Build the menu */
                theActiveCategory.buildCategoryTypeMenu(myBuilder, myCategory);
            }
        }
    }
}
