/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.moneywise.ui.base.MoneyWiseCategoryTable;
import io.github.tonywasher.joceanus.moneywise.ui.dialog.MoneyWiseTransCategoryDialog;
import io.github.tonywasher.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableManager;

import java.util.List;

/**
 * MoneyWise TransCategory Table.
 */
public class MoneyWiseTransCategoryTable
        extends MoneyWiseCategoryTable<MoneyWiseTransCategory, MoneyWiseTransCategoryType> {
    /**
     * The Category dialog.
     */
    private final MoneyWiseTransCategoryDialog theActiveCategory;

    /**
     * The edit list.
     */
    private MoneyWiseTransCategoryList theCategories;

    /**
     * Constructor.
     *
     * @param pView    the view
     * @param pEditSet the editSet
     * @param pError   the error panel
     */
    MoneyWiseTransCategoryTable(final MoneyWiseView pView,
                                final PrometheusEditSet pEditSet,
                                final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseTransCategory.class, MoneyWiseBasicDataType.TRANSCATEGORY);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a category panel */
        theActiveCategory = new MoneyWiseTransCategoryDialog(myGuiFactory, pEditSet, this);
        declareItemPanel(theActiveCategory);

        /* Add listeners */
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected void addCategoryTypeColumn() {
        final TethysUITableManager<MetisDataFieldId, MoneyWiseTransCategory> myTable = getTable();
        myTable.declareScrollColumn(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseTransCategoryType.class)
                .setMenuConfigurator(this::buildCategoryTypeMenu)
                .setCellValueFactory(MoneyWiseTransCategory::getCategoryType)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseCategoryBase::setCategoryType, r, v));
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveCategory.isEditing();
    }

    @Override
    protected List<MoneyWiseTransCategory> getCategories() {
        return theCategories == null ? null : theCategories.getUnderlyingList();
    }

    @Override
    protected boolean isChildCategory(final MoneyWiseTransCategoryType pCategoryType) {
        return !pCategoryType.getCategoryClass().isSubTotal();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("TransactionCategories");

        /* Access list */
        final MoneyWiseDataSet myData = getView().getData();
        final MoneyWiseTransCategoryList myBase = myData.getTransCategories();
        theCategories = myBase.deriveEditList(getEditSet());
        getTable().setItems(theCategories.getUnderlyingList());

        /* If we have a parent */
        MoneyWiseTransCategory myParent = getParent();
        if (myParent != null) {
            /* Update the parent via the edit list */
            myParent = theCategories.findItemById(myParent.getIndexedId());
            updateParent(myParent);
        }

        /* Notify panel of refresh */
        theActiveCategory.refreshData();
        restoreSelected();

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveCategory.setEditable(false);
    }

    /**
     * Select category.
     *
     * @param pCategory the category to select
     */
    void selectCategory(final MoneyWiseTransCategory pCategory) {
        /* If we are changing the selection */
        final MoneyWiseTransCategory myCurrent = theActiveCategory.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCategory)) {
            /* Ensure the correct parent is selected */
            MoneyWiseTransCategory myParent = pCategory.getParentCategory();
            if (!MetisDataDifference.isEqual(getParent(), myParent)) {
                if (myParent != null) {
                    myParent = theCategories.findItemById(myParent.getIndexedId());
                }
                selectParent(myParent);
            }

            /* Select the row and ensure that it is visible */
            getTable().selectRow(pCategory);
        }
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCategory.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            super.handleRewind();
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
            setEnabled(true);
            final MoneyWiseTransCategory myCategory = theActiveCategory.getSelectedItem();
            updateTableData();
            if (myCategory != null) {
                getTable().selectRow(myCategory);
            } else {
                restoreSelected();
            }
        } else {
            getTable().cancelEditing();
        }

        /* Note changes */
        notifyChanges();
    }

    @Override
    protected void buildCategoryTypeMenu(final MoneyWiseTransCategory pCategory,
                                         final TethysUIScrollMenu<MoneyWiseTransCategoryType> pMenu) {
        /* Build the menu */
        theActiveCategory.buildCategoryTypeMenu(pMenu, pCategory);
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create a new profile */
            final OceanusProfile myTask = getView().getNewProfile("addNewItem");

            /* Create the new category */
            myTask.startTask("buildItem");
            final MoneyWiseTransCategory myCategory = theCategories.addNewItem();
            myCategory.setDefaults(getParent());

            /* Set as new and adjust map */
            myTask.startTask("incrementVersion");
            myCategory.setNewVersion();
            myCategory.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item */
            myTask.startTask("validate");

            /* update panel */
            myTask.startTask("setItem");
            theActiveCategory.setNewItem(myCategory);

            /* Lock the table */
            setTableEnabled(false);
            myTask.end();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new category", e);

            /* Show the error */
            setError(myError);
        }
    }

    @Override
    protected boolean isFiltered(final MoneyWiseTransCategory pRow) {
        final MoneyWiseTransCategory myParent = getParent();
        return super.isFiltered(pRow) && (myParent == null
                ? pRow.getParentCategory() == null
                || pRow.getParentCategory().isCategoryClass(MoneyWiseTransCategoryClass.TOTALS)
                : myParent.equals(pRow.getParentCategory()));
    }
}
