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
package net.sourceforge.joceanus.moneywise.ui.dialog;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType.MoneyWiseLoanCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import io.github.tonywasher.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIFieldFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollItem;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;

import java.util.Iterator;

/**
 * Panel to display/edit/create a LoanCategory.
 */
public class MoneyWiseLoanCategoryDialog
        extends MoneyWiseItemPanel<MoneyWiseLoanCategory> {
    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner   the owning table
     */
    public MoneyWiseLoanCategoryDialog(final TethysUIFactory<?> pFactory,
                                       final PrometheusEditSet pEditSet,
                                       final MoneyWiseBaseTable<MoneyWiseLoanCategory> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Create a new panel */
        final PrometheusFieldSet<MoneyWiseLoanCategory> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField mySubName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseLoanCategoryType> myTypeButton = myFields.newScrollField(MoneyWiseLoanCategoryType.class);
        final TethysUIScrollButtonField<MoneyWiseLoanCategory> myParentButton = myFields.newScrollField(MoneyWiseLoanCategory.class);

        /* Assign the fields to the panel */
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseLoanCategory::getName);
        myFieldSet.addField(MoneyWiseBasicResource.CATEGORY_SUBCAT, mySubName, MoneyWiseLoanCategory::getSubCategory);
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseLoanCategory::getDesc);
        myFieldSet.addField(MoneyWiseStaticDataType.LOANTYPE, myTypeButton, MoneyWiseLoanCategory::getCategoryType);
        myFieldSet.addField(PrometheusDataResource.DATAGROUP_PARENT, myParentButton, MoneyWiseLoanCategory::getParentCategory);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildCategoryTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));

        /* Configure name checks */
        mySubName.setValidator(this::isValidName);
        mySubName.setReporter(pOwner::showValidateError);

        /* Configure description checks */
        myDesc.setValidator(this::isValidDesc);
        myDesc.setReporter(pOwner::showValidateError);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseLoanCategory myItem = getItem();
        if (myItem != null) {
            final MoneyWiseLoanCategoryList myCategories = getDataList(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseLoanCategoryList.class);
            setItem(myCategories.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusFieldSet<MoneyWiseLoanCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final MoneyWiseLoanCategory myCategory = getItem();
        final MoneyWiseLoanCategoryType myType = myCategory.getCategoryType();
        final boolean isParent = myType.isLoanCategory(MoneyWiseLoanCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        myFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Set visibility */
        myFieldSet.setFieldVisible(PrometheusDataResource.DATAGROUP_PARENT, !isParent);
        myFieldSet.setFieldVisible(MoneyWiseBasicResource.CATEGORY_SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        myFieldSet.setFieldEditable(MoneyWiseStaticDataType.LOANTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setFieldEditable(PrometheusDataResource.DATAITEM_FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseLoanCategory myCategory = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_SUBCAT.equals(myField)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAGROUP_PARENT.equals(myField)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(MoneyWiseLoanCategory.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseStaticDataType.LOANTYPE.equals(myField)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(MoneyWiseLoanCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseLoanCategory myItem = getItem();
        final MoneyWiseLoanCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final MoneyWiseLoanCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     *
     * @param pMenu     the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysUIScrollMenu<MoneyWiseLoanCategoryType> pMenu,
                                      final MoneyWiseLoanCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseLoanCategoryType myCurr = pCategory.getCategoryType();
        TethysUIScrollItem<MoneyWiseLoanCategoryType> myActive = null;

        /* Access Loan Category types */
        final MoneyWiseLoanCategoryTypeList myCategoryTypes = getDataList(MoneyWiseStaticDataType.LOANTYPE, MoneyWiseLoanCategoryTypeList.class);

        /* Loop through the LoanCategoryTypes */
        final Iterator<MoneyWiseLoanCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseLoanCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getLoanClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<MoneyWiseLoanCategoryType> myItem = pMenu.addItem(myType);

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
     *
     * @param pMenu     the menu
     * @param pCategory the category to build for
     */
    private static void buildParentMenu(final TethysUIScrollMenu<MoneyWiseLoanCategory> pMenu,
                                        final MoneyWiseLoanCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseLoanCategory myCurr = pCategory.getParentCategory();
        TethysUIScrollItem<MoneyWiseLoanCategory> myActive = null;

        /* Loop through the LoanCategories */
        final MoneyWiseLoanCategoryList myCategories = pCategory.getList();
        final Iterator<MoneyWiseLoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseLoanCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            final MoneyWiseLoanCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the parent */
            final TethysUIScrollItem<MoneyWiseLoanCategory> myItem = pMenu.addItem(myCat);

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
