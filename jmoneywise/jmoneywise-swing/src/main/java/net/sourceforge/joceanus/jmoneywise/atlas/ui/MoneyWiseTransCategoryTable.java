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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.TransactionCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;

/**
 * MoneyWise TransCategory Table.
 */
public class MoneyWiseTransCategoryTable
        extends MoneyWiseBaseTable<TransactionCategory> {
    /**
     * Filter Prompt.
     */
    private static final String TITLE_FILTER = MoneyWiseUIResource.CATEGORY_PROMPT_FILTER.getValue();

    /**
     * Filter Parents Title.
     */
    private static final String FILTER_PARENTS = MoneyWiseUIResource.CATEGORY_FILTER_PARENT.getValue();

    /**
     * The filter panel.
     */
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The Category dialog.
     */
    private final TransactionCategoryPanel theActiveCategory;

    /**
     * The select button.
     */
    private final TethysScrollButtonManager<TransactionCategory> theSelectButton;

    /**
     * The edit list.
     */
    private TransactionCategoryList theCategories;

    /**
     * Active parent.
     */
    private TransactionCategory theParent;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseTransCategoryTable(final MoneyWiseView pView,
                                final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.TRANSCATEGORY);

        /* Access field manager */
        MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();

        /* Access Gui factory */
        final TethysSwingGuiFactory myGuiFactory = (TethysSwingGuiFactory) pView.getGuiFactory();
        final TethysSwingTableManager<MetisLetheField, TransactionCategory> myTable = getTable();

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
        theActiveCategory = new TransactionCategoryPanel(myGuiFactory, myFieldMgr, pUpdateSet, pError);
        declareItemPanel(theActiveCategory);

        /* Set table configuration */
        myTable.setDisabled(TransactionCategory::isDisabled)
               .setComparator(TransactionCategory::compareTo)
               .setOnSelect(theActiveCategory::setItem);

        /* Create the short name column */
        myTable.declareStringColumn(TransactionCategory.FIELD_SUBCAT)
               .setValidator(this::isValidName)
               .setCellValueFactory(this::getShortName)
               .setEditable(true)
               .setOnCommit((r, v) -> updateField(TransactionCategory::setSubCategoryName, r, v));

        /* Create the full name column */
        myTable.declareStringColumn(TransactionCategory.FIELD_NAME)
               .setCellValueFactory(TransactionCategory::getName)
               .setEditable(false);

        /* Create the category type column */
        myTable.declareScrollColumn(TransactionCategory.FIELD_CATTYPE, TransactionCategoryType.class)
               .setMenuConfigurator(this::buildCategoryTypeMenu)
               .setCellValueFactory(TransactionCategory::getCategoryType)
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setOnCommit((r, v) -> updateField(TransactionCategory::setCategoryType, r, v));

        /* Create the description column */
        myTable.declareStringColumn(TransactionCategory.FIELD_DESC)
               .setValidator(this::isValidDesc)
               .setCellValueFactory(TransactionCategory::getDesc)
               .setEditable(true)
               .setOnCommit((r, v) -> updateField(TransactionCategory::setDescription, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(TransactionCategory.FIELD_TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theSelectButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleParentSelection());
        theSelectButton.setMenuConfigurator(e -> buildSelectMenu());
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveCategory.isEditing();
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
    private String getShortName(final TransactionCategory pCategory) {
        final String myName = pCategory.getSubCategory();
        return myName == null ? pCategory.getName() : myName;
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("TransactionCategories");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final TransactionCategoryList myBase = myData.getDataList(TransactionCategoryList.class);
        theCategories = (TransactionCategoryList) myBase.deriveList(ListStyle.EDIT);
        theCategories.mapData();
        getTable().setItems(theCategories.getUnderlyingList());
        getUpdateEntry().setDataList(theCategories);

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

    @Override
    protected void cancelEditing() {
        super.cancelEditing();
        theActiveCategory.setEditable(false);
    }

    /**
     * Select category.
     * @param pCategory the category to select
     */
    void selectCategory(final TransactionCategory pCategory) {
        /* If we are changing the selection */
        final TransactionCategory myCurrent = theActiveCategory.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCategory)) {
            /* Ensure the correct parent is selected */
            TransactionCategory myParent = pCategory.getParentCategory();
            if (!MetisDataDifference.isEqual(theParent, myParent)) {
                if (myParent != null) {
                    myParent = theCategories.findItemById(myParent.getId());
                }
                selectParent(myParent);
            }

            /* Select the row and ensure that it is visible */
            getTable().selectRowWithScroll(pCategory);
            theActiveCategory.setItem(pCategory);
        }
    }

    /**
     * Select parent.
     * @param pParent the parent category
     */
    private void selectParent(final TransactionCategory pParent) {
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
            getTable().fireTableDataChanged();
        }
    }

    /**
     * Handle parent selection.
     */
    private void handleParentSelection() {
        selectParent(theSelectButton.getValue());
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCategory.isEditing()) {
            /* Handle the reWind */
            getTable().fireTableDataChanged();
            selectCategory(theActiveCategory.getSelectedItem());
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
            getTable().fireTableDataChanged();
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
        final TethysScrollMenu<TransactionCategory> myCategoryMenu = theSelectButton.getMenu();
        myCategoryMenu.removeAllItems();

        /* Cope if we have no categories */
        if (theCategories == null) {
            return;
        }

        /* Record active item */
        TethysScrollMenuItem<TransactionCategory> myActive = null;

        /* Create the no filter MenuItem and add it to the popUp */
        TethysScrollMenuItem<TransactionCategory> myItem = myCategoryMenu.addItem(null, FILTER_PARENTS);

        /* If this is the active parent */
        if (theParent == null) {
            /* Record it */
            myActive = myItem;
        }

        /* Loop through the available category values */
        final Iterator<TransactionCategory> myIterator = theCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategory myCurr = myIterator.next();
            final TransactionCategoryType myType = myCurr.getCategoryType();

            /* Ignore deleted */
            boolean bIgnore = myCurr.isDeleted();

            /* Ignore category if it is not a parent */
            bIgnore |= !myType.getCategoryClass().isSubTotal();
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
    private void buildCategoryTypeMenu(final TransactionCategory pCategory,
                                       final TethysScrollMenu<TransactionCategoryType> pMenu) {
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
            final TransactionCategory myCategory = theCategories.addNewItem();
            myCategory.setDefaults(theParent);

            /* Set as new and adjust map */
            myCategory.setNewVersion();
            myCategory.adjustMapForItem();
            getUpdateSet().incrementVersion();

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

    @Override
    protected boolean isFiltered(final TransactionCategory pRow) {
        return super.isFiltered(pRow) && (theParent == null
                ? pRow.getParentCategory() == null
                : theParent.equals(pRow.getParentCategory()));
    }

    @Override
    protected String getInvalidNameChars() {
        return ":";
    }
}
