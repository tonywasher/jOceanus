/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.panel;

import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseCategoryDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseCategoryTable;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.MoneyWiseCashCategoryPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;

/**
 * MoneyWise CashCategory Table.
 */
public class MoneyWiseCashCategoryTable
        extends MoneyWiseCategoryTable<CashCategory, CashCategoryType, CashCategoryClass> {
    /**
     * The Category dialog.
     */
    private final MoneyWiseCashCategoryPanel theActiveCategory;

    /**
     * The edit list.
     */
    private CashCategoryList theCategories;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    MoneyWiseCashCategoryTable(final MoneyWiseView pView,
                               final UpdateSet<MoneyWiseDataType> pUpdateSet,
                               final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, CashCategory.class, MoneyWiseDataType.CASHCATEGORY);

        /* Access Gui factory */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
        final TethysTableManager<PrometheusDataFieldId, CashCategory> myTable = getTable();

        /* Create a category panel */
        theActiveCategory = new MoneyWiseCashCategoryPanel(myGuiFactory, pUpdateSet, pError);
        declareItemPanel(theActiveCategory);

        /* Set table configuration */
        myTable.setOnSelect(theActiveCategory::setItem);

        /* Add listeners */
        theActiveCategory.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
    }

    @Override
    protected void addCategoryTypeColumn() {
        final TethysTableManager<PrometheusDataFieldId, CashCategory> myTable = getTable();
        myTable.declareScrollColumn(MoneyWiseCategoryDataId.CASHCATTYPE, CashCategoryType.class)
                .setMenuConfigurator(this::buildCategoryTypeMenu)
                .setCellValueFactory(CategoryBase::getCategoryType)
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
    protected List<CashCategory> getCategories() {
        return theCategories == null ? null : theCategories.getUnderlyingList();
    }

    @Override
    protected boolean isChildCategory(final CashCategoryType pCategoryType) {
        return !pCategoryType.getCashClass().isParentCategory();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("CashCategories");

        /* Access list */
        final MoneyWiseData myData = getView().getData();
        final CashCategoryList myBase = myData.getCashCategories();
        theCategories = myBase.deriveEditList();
        getTable().setItems(theCategories.getUnderlyingList());
        getUpdateEntry().setDataList(theCategories);

        /* If we have a parent */
        CashCategory myParent = getParent();
        if (myParent != null) {
            /* Update the parent via the edit list */
            myParent = theCategories.findItemById(myParent.getId());
            updateParent(myParent);
        }

        /* Notify panel of refresh */
        theActiveCategory.refreshData();

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
    void selectCategory(final CashCategory pCategory) {
        /* If we are changing the selection */
        final CashCategory myCurrent = theActiveCategory.getSelectedItem();
        if (!MetisDataDifference.isEqual(myCurrent, pCategory)) {
            /* Ensure the correct parent is selected */
            CashCategory myParent = pCategory.getParentCategory();
            if (!MetisDataDifference.isEqual(getParent(), myParent)) {
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

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveCategory.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
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
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectCategory(theActiveCategory.getSelectedItem());
        }

        /* Note changes */
        notifyChanges();
    }

    @Override
    protected void buildCategoryTypeMenu(final CashCategory pCategory,
                                         final TethysScrollMenu<CashCategoryType> pMenu) {
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
            final CashCategory myCategory = theCategories.addNewItem();
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
    protected boolean isFiltered(final CashCategory pRow) {
        final CashCategory myParent = getParent();
        return super.isFiltered(pRow) && (myParent == null
                ? pRow.isCategoryClass(CashCategoryClass.PARENT)
                : myParent.equals(pRow.getParentCategory()));
    }
}
