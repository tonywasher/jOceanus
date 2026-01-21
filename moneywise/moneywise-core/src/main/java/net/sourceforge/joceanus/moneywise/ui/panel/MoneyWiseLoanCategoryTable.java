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
package net.sourceforge.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCategoryBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseCategoryTable;
import net.sourceforge.joceanus.moneywise.ui.dialog.MoneyWiseLoanCategoryDialog;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

import java.util.List;

/**
 * MoneyWise LoanCategory Table.
 */
public class MoneyWiseLoanCategoryTable
        extends MoneyWiseCategoryTable<MoneyWiseLoanCategory, MoneyWiseLoanCategoryType> {
    /**
     * The Category dialog.
     */
    private final MoneyWiseLoanCategoryDialog theActiveCategory;

    /**
     * The edit list.
     */
    private MoneyWiseLoanCategoryList theCategories;

    /**
     * Constructor.
     *
     * @param pView    the view
     * @param pEditSet the editSet
     * @param pError   the error panel
     */
    MoneyWiseLoanCategoryTable(final MoneyWiseView pView,
                               final PrometheusEditSet pEditSet,
                               final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseLoanCategory.class, MoneyWiseBasicDataType.LOANCATEGORY);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a category panel */
        theActiveCategory = new MoneyWiseLoanCategoryDialog(myGuiFactory, pEditSet, this);
        declareItemPanel(theActiveCategory);

        /* Add listeners */
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected void addCategoryTypeColumn() {
        final TethysUITableManager<MetisDataFieldId, MoneyWiseLoanCategory> myTable = getTable();
        myTable.declareScrollColumn(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseLoanCategoryType.class)
                .setMenuConfigurator(this::buildCategoryTypeMenu)
                .setCellValueFactory(MoneyWiseLoanCategory::getCategoryType)
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
    protected List<MoneyWiseLoanCategory> getCategories() {
        return theCategories == null ? null : theCategories.getUnderlyingList();
    }

    @Override
    protected boolean isChildCategory(final MoneyWiseLoanCategoryType pCategoryType) {
        return !pCategoryType.getLoanClass().isParentCategory();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("LoanCategories");

        /* Access list */
        final MoneyWiseDataSet myData = getView().getData();
        final MoneyWiseLoanCategoryList myBase = myData.getLoanCategories();
        theCategories = myBase.deriveEditList(getEditSet());
        getTable().setItems(theCategories.getUnderlyingList());

        /* If we have a parent */
        MoneyWiseLoanCategory myParent = getParent();
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
    void selectCategory(final MoneyWiseLoanCategory pCategory) {
        /* If we are changing the selection */
        final MoneyWiseLoanCategory myCurrent = theActiveCategory.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCategory)) {
            /* Ensure the correct parent is selected */
            MoneyWiseLoanCategory myParent = pCategory.getParentCategory();
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
            final MoneyWiseLoanCategory myCategory = theActiveCategory.getSelectedItem();
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
    protected void buildCategoryTypeMenu(final MoneyWiseLoanCategory pCategory,
                                         final TethysUIScrollMenu<MoneyWiseLoanCategoryType> pMenu) {
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
            final MoneyWiseLoanCategory myCategory = theCategories.addNewItem();
            myCategory.setDefaults(getParent());

            /* Set as new and adjust map */
            myTask.startTask("incrementVersion");
            myCategory.setNewVersion();
            myCategory.adjustMapForItem();
            getEditSet().incrementVersion();

            /* Validate the new item */
            myTask.startTask("validate");
            myCategory.validate();

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
    protected boolean isFiltered(final MoneyWiseLoanCategory pRow) {
        final MoneyWiseLoanCategory myParent = getParent();
        return super.isFiltered(pRow) && (myParent == null
                ? pRow.isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)
                : myParent.equals(pRow.getParentCategory()));
    }
}
