/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.panel;

import java.util.List;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseCategoryDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseCategoryTable;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWiseDepositCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise DepositCategory Table.
 */
public class MoneyWiseDepositCategoryTable
        extends MoneyWiseCategoryTable<DepositCategory, DepositCategoryType> {
    /**
     * The Category dialog.
     */
    private final MoneyWiseDepositCategoryPanel theActiveCategory;

    /**
     * The edit list.
     */
    private DepositCategoryList theCategories;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseDepositCategoryTable(final MoneyWiseView pView,
                                  final UpdateSet pUpdateSet,
                                  final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, DepositCategory.class, MoneyWiseDataType.DEPOSITCATEGORY);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

        /* Create a category panel */
        theActiveCategory = new MoneyWiseDepositCategoryPanel(myGuiFactory, pUpdateSet, pError);
        declareItemPanel(theActiveCategory);

        /* Add listeners */
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected void addCategoryTypeColumn() {
        final TethysUITableManager<PrometheusDataFieldId, DepositCategory> myTable = getTable();
        myTable.declareScrollColumn(MoneyWiseCategoryDataId.DEPOSITCATTYPE, DepositCategoryType.class)
                .setMenuConfigurator(this::buildCategoryTypeMenu)
                .setCellValueFactory(DepositCategory::getCategoryType)
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(CategoryBase::setCategoryType, r, v));
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveCategory.isEditing();
    }

    @Override
    protected List<DepositCategory> getCategories() {
        return theCategories == null ? null : theCategories.getUnderlyingList();
    }

    @Override
    protected boolean isChildCategory(final DepositCategoryType pCategoryType) {
        return !pCategoryType.getDepositClass().isParentCategory();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("DepositCategories");

        /* Access list */
        final MoneyWiseData myData = (MoneyWiseData) getView().getData();
        final DepositCategoryList myBase = myData.getDepositCategories();
        theCategories = myBase.deriveEditList();
        getTable().setItems(theCategories.getUnderlyingList());
        getUpdateEntry().setDataList(theCategories);

        /* If we have a parent */
        DepositCategory myParent = getParent();
        if (myParent != null) {
            /* Update the parent via the edit list */
            myParent = theCategories.findItemById(myParent.getId());
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
     * @param pCategory the category to select
     */
    void selectCategory(final DepositCategory pCategory) {
        /* If we are changing the selection */
        final DepositCategory myCurrent = theActiveCategory.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCategory)) {
            /* Ensure the correct parent is selected */
            DepositCategory myParent = pCategory.getParentCategory();
            if (!MetisDataDifference.isEqual(getParent(), myParent)) {
                if (myParent != null) {
                    myParent = theCategories.findItemById(myParent.getId());
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
            final DepositCategory myCategory = theActiveCategory.getSelectedItem();
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
    protected void buildCategoryTypeMenu(final DepositCategory pCategory,
                                         final TethysUIScrollMenu<DepositCategoryType> pMenu) {
        /* Build the menu */
        theActiveCategory.buildCategoryTypeMenu(pMenu, pCategory);
    }

   @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Make sure that we have finished editing */
            cancelEditing();

            /* Create the new category */
            final DepositCategory myCategory = theCategories.addNewItem();
            myCategory.setDefaults(getParent());

            /* Set as new and adjust map */
            myCategory.setNewVersion();
            myCategory.adjustMapForItem();
            getUpdateSet().incrementVersion();

            /* Validate the new item and update panel */
            myCategory.validate();
            theActiveCategory.setNewItem(myCategory);

            /* Lock the table */
            setTableEnabled(false);

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new category", e);

            /* Show the error */
            setError(myError);
        }
    }

    @Override
    protected boolean isFiltered(final DepositCategory pRow) {
        final DepositCategory myParent = getParent();
        return super.isFiltered(pRow) && (myParent == null
                ? pRow.isCategoryClass(DepositCategoryClass.PARENT)
                : myParent.equals(pRow.getParentCategory()));
    }
}
