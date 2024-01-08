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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseCategoryDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseXItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSet;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Panel to display/edit/create a CashCategory.
 */
public class MoneyWiseCashCategoryPanel
        extends MoneyWiseXItemPanel<CashCategory> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public MoneyWiseCashCategoryPanel(final TethysUIFactory<?> pFactory,
                                      final UpdateSet pUpdateSet,
                                      final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Create a new panel */
        final PrometheusXFieldSet<CashCategory> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField mySubName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<CashCategoryType> myTypeButton = myFields.newScrollField(CashCategoryType.class);
        final TethysUIScrollButtonField<CashCategory> myParentButton = myFields.newScrollField(CashCategory.class);

        /* Assign the fields to the panel */
        myFieldSet.addField(MoneyWiseCategoryDataId.NAME, myName, CashCategory::getName);
        myFieldSet.addField(MoneyWiseCategoryDataId.SUBCAT, mySubName, CashCategory::getSubCategory);
        myFieldSet.addField(MoneyWiseCategoryDataId.DESC, myDesc, CashCategory::getDesc);
        myFieldSet.addField(MoneyWiseCategoryDataId.CASHCATTYPE, myTypeButton, CashCategory::getCategoryType);
        myFieldSet.addField(MoneyWiseCategoryDataId.PARENT, myParentButton, CashCategory::getParentCategory);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildCategoryTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final CashCategory myItem = getItem();
        if (myItem != null) {
            final CashCategoryList myCategories = getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusXFieldSet<CashCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final CashCategory myCategory = getItem();
        final CashCategoryType myType = myCategory.getCategoryType();
        final boolean isParent = myType.isCashCategory(CashCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        myFieldSet.setFieldVisible(MoneyWiseCategoryDataId.DESC, bShowDesc);

        /* Set visibility */
        myFieldSet.setFieldVisible(MoneyWiseCategoryDataId.PARENT, !isParent);
        myFieldSet.setFieldVisible(MoneyWiseCategoryDataId.SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        myFieldSet.setFieldEditable(MoneyWiseCategoryDataId.CASHCATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setFieldEditable(MoneyWiseCategoryDataId.NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final PrometheusXFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final CashCategory myCategory = getItem();

        /* Process updates */
        if (MoneyWiseCategoryDataId.NAME.equals(myField)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseCategoryDataId.SUBCAT.equals(myField)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseCategoryDataId.PARENT.equals(myField)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(CashCategory.class));
        } else if (MoneyWiseCategoryDataId.DESC.equals(myField)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseCategoryDataId.CASHCATTYPE.equals(myField)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(CashCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final CashCategory myItem = getItem();
        final CashCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final CashCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysUIScrollMenu<CashCategoryType> pMenu,
                                      final CashCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final CashCategoryType myCurr = pCategory.getCategoryType();
        TethysUIScrollItem<CashCategoryType> myActive = null;

        /* Access Cash Category types */
        final CashCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.CASHTYPE, CashCategoryTypeList.class);

        /* Loop through the CashCategoryTypes */
        final Iterator<CashCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final CashCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getCashClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<CashCategoryType> myItem = pMenu.addItem(myType);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
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
     * Build the parent menu for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    private static void buildParentMenu(final TethysUIScrollMenu<CashCategory> pMenu,
                                        final CashCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final CashCategory myCurr = pCategory.getParentCategory();
        TethysUIScrollItem<CashCategory> myActive = null;

        /* Loop through the CashCategories */
        final CashCategoryList myCategories = pCategory.getList();
        final Iterator<CashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final CashCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            final CashCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<CashCategory> myItem = pMenu.addItem(myCat);

            /* If this is the active parent */
            if (myCat.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }
}
