/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.atlas.ui;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Objects;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.LoanCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysOnCellCommit;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise LoanCategory Table.
 */
public class MoneyWiseLoanCategoryTable
        implements TethysEventProvider<PrometheusDataEvent>, TethysComponent {
    /**
     * Filter Prompt.
     */
    private static final String TITLE_FILTER = MoneyWiseUIResource.CATEGORY_PROMPT_FILTER.getValue();

    /**
     * Filter Parents Title.
     */
    private static final String FILTER_PARENTS = MoneyWiseUIResource.CATEGORY_FILTER_PARENT.getValue();

    /**
     * The view.
     */
    private final MoneyWiseView theView;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The UpdateSet associated with the table.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The UpdateEntry.
     */
    private final UpdateEntry<LoanCategory, MoneyWiseDataType> theUpdateEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The underlying table.
     */
    private final TethysSwingTableManager<MetisLetheField, LoanCategory> theTable;

    /**
     * The Category dialog.
     */
    private final LoanCategoryPanel theActiveCategory;

    /**
     * The select button.
     */
    private final TethysScrollButtonManager<LoanCategory> theSelectButton;

    /**
     * The edit list.
     */
    private LoanCategoryList theCategories;

    /**
     * Active parent.
     */
    private LoanCategory theParent;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseLoanCategoryTable(final MoneyWiseView pView,
                                  final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                  final MetisErrorPanel pError) {
        /* Store parameters */
        theView = pView;
        theError = pError;

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) theView.getToolkit()).getFieldManager();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Build the Update set */
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerType(MoneyWiseDataType.LOANCATEGORY);

        /* Create the panel */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        thePanel = myGuiFactory.newBorderPane();

        /* Create the table */
        theTable = myGuiFactory.newTable();
        thePanel.setCentre(theTable);

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create the filter components */
        final TethysLabel myPrompt = myGuiFactory.newLabel(TITLE_FILTER);
        theSelectButton = myGuiFactory.newScrollButton();
        theSelectButton.setValue(null, FILTER_PARENTS);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myPrompt);
        theFilterPanel.addNode(theSelectButton);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Create a category panel */
        theActiveCategory = new LoanCategoryPanel(myGuiFactory, myFieldMgr, theUpdateSet, theError);
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(TethysSwingNode.getComponent(theActiveCategory), BorderLayout.CENTER);
        thePanel.setSouth(myPanel);

        /* Set table configuration */
        theTable.setOnCommitError(this::setError)
                .setOnValidateError(this::showValidateError)
                .setOnCellEditState(this::handleEditState)
                .setDisabled(LoanCategory::isDisabled)
                .setChanged(this::isFieldChanged)
                .setError(this::isFieldInError)
                .setComparator(LoanCategory::compareTo)
                .setFilter(this::isFiltered)
                .setEditable(true)
                .setOnSelect(theActiveCategory::setItem);

        /* Create the short name column */
        theTable.declareStringColumn(LoanCategory.FIELD_SUBCAT)
                .setValidator(this::isValidName)
                .setCellValueFactory(this::getShortName)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(LoanCategory::setSubCategoryName, r, v));

        /* Create the full name column */
        theTable.declareStringColumn(LoanCategory.FIELD_NAME)
                .setCellValueFactory(LoanCategory::getName)
                .setEditable(false);

        /* Create the category type column */
        theTable.declareScrollColumn(LoanCategory.FIELD_CATTYPE, LoanCategoryType.class)
                .setMenuConfigurator(this::buildCategoryTypeMenu)
                .setCellValueFactory(LoanCategory::getCategoryType)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(LoanCategory::setCategoryType, r, v));

        /* Create the description column */
        theTable.declareStringColumn(LoanCategory.FIELD_DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(LoanCategory::getDesc)
                .setEditable(true)
                .setOnCommit((r, v) -> updateField(LoanCategory::setDescription, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        theTable.declareIconColumn(LoanCategory.FIELD_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
        theSelectButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleParentSelection());
        theSelectButton.setMenuConfigurator(e -> buildSelectMenu());
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveCategory.isEditing();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Obtain the short name.
     * @return the name
     */
    private String getShortName(final LoanCategory pCategory) {
        final String myName = pCategory.getSubCategory();
        return myName == null ? pCategory.getName() : myName;
    }

    /**
     * Refresh data.
     */
    void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("LoanCategories");

        /* Access list */
        final MoneyWiseData myData = theView.getData();
        final LoanCategoryList myBase = myData.getDataList(LoanCategoryList.class);
        theCategories = (LoanCategoryList) myBase.deriveList(ListStyle.EDIT);
        theCategories.mapData();
        theTable.setItems(theCategories.getUnderlyingList());
        theUpdateEntry.setDataList(theCategories);

        /* If we have a parent */
        if (theParent != null) {
            /* Update the parent via the edit list */
            theParent = theCategories.findItemById(theParent.getId());
            theSelectButton.setValue(theParent);
        }

        /* Notify panel of refresh */
        theActiveCategory.refreshData();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Cancel editing.
     */
    void cancelEditing() {
        theTable.cancelEditing();
        theActiveCategory.setEditable(false);
    }

    /**
     * Does the panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    /**
     * Does the panel have a session?
     * @return true/false
     */
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
    }

    /**
     * Does the panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        theTable.requestFocus();

        /* Set the required focus */
        pEntry.setFocus(theUpdateEntry.getName());
    }

    /**
     * Select category.
     * @param pCategory the category to select
     */
    void selectCategory(final LoanCategory pCategory) {
        /* If we are changing the selection */
        final LoanCategory myCurrent = theActiveCategory.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCategory)) {
            /* Ensure the correct parent is selected */
            LoanCategory myParent = pCategory.getParentCategory();
            if (!MetisDataDifference.isEqual(theParent, myParent)) {
                if (myParent != null) {
                    myParent = theCategories.findItemById(myParent.getId());
                }
                selectParent(myParent);
            }

            /* Select the row and ensure that it is visible */
            theTable.selectRowWithScroll(pCategory);
            theActiveCategory.setItem(pCategory);
        }
    }

    /**
     * Select parent.
     * @param pParent the parent category
     */
    private void selectParent(final LoanCategory pParent) {
        /* If the parent is being changed */
        if (!MetisDataDifference.isEqual(pParent, theParent)) {
            /* Store new value */
            theParent = pParent;

            /* Update select button */
            if (pParent == null) {
                theSelectButton.setValue(null, FILTER_PARENTS);
            } else {
                theSelectButton.setValue(pParent);
            }

            /* Notify table of change */
            theTable.fireTableDataChanged();
        }
    }

    /**
     * Handle parent selection.
     */
    private void handleParentSelection() {
        selectParent(theSelectButton.getValue());
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     */
    private void deleteRow(final LoanCategory pRow,
                           final Object pValue) {
        pRow.setDeleted(true);
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCategory.isEditing()) {
            /* Handle the reWind */
            theTable.fireTableDataChanged();
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
            theTable.fireTableDataChanged();
            selectCategory(theActiveCategory.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Build Select menu.
     */
    private void buildSelectMenu() {
        /* Clear the menu */
        final TethysScrollMenu<LoanCategory> myCategoryMenu = theSelectButton.getMenu();
        myCategoryMenu.removeAllItems();

        /* Cope if we have no categories */
        if (theCategories == null) {
            return;
        }

        /* Record active item */
        TethysScrollMenuItem<LoanCategory> myActive = null;

        /* Create the no filter MenuItem and add it to the popUp */
        TethysScrollMenuItem<LoanCategory> myItem = myCategoryMenu.addItem(null, FILTER_PARENTS);

        /* If this is the active parent */
        if (theParent == null) {
            /* Record it */
            myActive = myItem;
        }

        /* Loop through the available category values */
        final Iterator<LoanCategory> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final LoanCategory myCurr = myIterator.next();
            final LoanCategoryType myType = myCurr.getCategoryType();

            /* Ignore deleted */
            boolean bIgnore = myCurr.isDeleted();

            /* Ignore category if it is not a parent */
            bIgnore |= !myType.getLoanClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            myItem = myCategoryMenu.addItem(myCurr);

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
     * Build the category type list for the item.
     * @param pCategory the item
     * @param pMenu the menu to build
     */
    private void buildCategoryTypeMenu(final LoanCategory pCategory,
                                       final TethysScrollMenu<LoanCategoryType> pMenu) {
        /* Build the menu */
        theActiveCategory.buildCategoryTypeMenu(pMenu, pCategory);
    }

    /**
     * New item.
     */
    private void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new category */
            final LoanCategory myCategory = theCategories.addNewItem();
            myCategory.setDefaults(theParent);

            /* Set as new and adjust map */
            myCategory.setNewVersion();
            myCategory.adjustMapForItem();
            theUpdateSet.incrementVersion();

            /* Validate the new item and update panel */
            myCategory.validate();
            theActiveCategory.setNewItem(myCategory);

            /* Lock the table */
            setEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new category", e);

            /* Show the error */
            setError(myError);
        }
    }

    /**
     * Update value.
     * @param <V> the value type
     * @param pOnCommit the update function
     * @param pRow the row to update
     * @param pValue the value
     * @throws OceanusException on error
     */
    private <V> void updateField(final TethysOnCellCommit<LoanCategory, V> pOnCommit,
                                 final LoanCategory pRow,
                                 final V pValue) throws OceanusException {
        /* Push history */
        pRow.pushHistory();

        /* Protect against Exceptions */
        try {
            /* Set the item value */
            pOnCommit.commitCell(pRow, pValue);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Reset values */
            pRow.popHistory();

            /* Throw the error */
            throw new PrometheusDataException("Failed to update field", e);
        }

        /* Check for changes */
        if (pRow.checkForHistory()) {
            /* Increment data version */
            theUpdateSet.incrementVersion();

            /* Update components to reflect changes */
            theTable.fireTableDataChanged();
            notifyChanges();
        }
    }

    /**
     * Set the error.
     * @param pError the error
     */
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Notify that there have been changes to this list.
     */
    protected void notifyChanges() {
        /* Adjust enable of the table */
        theTable.setEnabled(!theActiveCategory.isEditing());

        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * is field in error?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldInError(final MetisLetheField pField,
                                   final LoanCategory pItem) {
        return pItem.getFieldErrors(pField) != null;
    }

    /**
     * is field changed?
     *
     * @param pField the field
     * @param pItem  the item
     * @return true/false
     */
    private boolean isFieldChanged(final MetisLetheField pField,
                                   final LoanCategory pItem) {
        return pItem.fieldChanged(pField).isDifferent();
    }

    /**
     * isFiltered?
     * @param pRow the row
     * @return true/false
     */
    private boolean isFiltered(final LoanCategory pRow) {
        return !pRow.isDeleted() && (theParent == null
                ? pRow.isCategoryClass(LoanCategoryClass.PARENT)
                : theParent.equals(pRow.getParentCategory()));
    }

    /**
     * is Valid name?
     * @param pNewName the new name
     * @param pRow the row
     * @return error message or null
     */
    private String isValidName(final String pNewName,
                               final LoanCategory pRow) {
        /* Reject null name */
        if (pNewName == null) {
            return "Null Name not allowed";
        }

        /* Reject invalid name */
        if (!DataItem.validString(pNewName, ":")) {
            return "Invalid characters in name";
        }

        /* Reject name that is too long */
        if (DataItem.byteLength(pNewName) > LoanCategory.NAMELEN) {
            return "Name too long";
        }

        /* Loop through the existing values */
        for (LoanCategory myValue : theCategories.getUnderlyingList()) {
            /* Ignore self, deleted and non-siblings */
            if (myValue.isDeleted()
                    || myValue.equals(pRow)
                    ||!Objects.equals(myValue.getParentCategory(), theParent)) {
                continue;
            }

            /* Check for duplicate */
            if (pNewName.equals(myValue.getName())) {
                return "Duplicate name";
            }
        }

        /* Valid name */
        return null;
    }

    /**
     * is Valid description?
     * @param pNewDesc the new description
     * @param pRow the row
     * @return error message or null
     */
    private String isValidDesc(final String pNewDesc,
                               final LoanCategory pRow) {
        /* Reject description that is too long */
        if (pNewDesc != null
                && DataItem.byteLength(pNewDesc) > LoanCategory.DESCLEN) {
            return "Description too long";
        }

        /* Valid description */
        return null;
    }

    /**
     * Show validation error TODO use panel.
     * @param pError the error message
     */
    private void showValidateError(final String pError) {
        System.out.println(Objects.requireNonNullElse(pError, "Error cleared"));
    }

    /**
     * Check edit state.
     * @param pState the new state
     */
    private void handleEditState(final Boolean pState) {
        notifyChanges();
    }
}
