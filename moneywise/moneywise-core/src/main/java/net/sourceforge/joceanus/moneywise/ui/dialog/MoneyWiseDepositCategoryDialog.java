/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.dialog;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType.MoneyWiseDepositCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

/**
 * Panel to display/edit/create a DepositCategory.
 */
public class MoneyWiseDepositCategoryDialog
        extends MoneyWiseItemPanel<MoneyWiseDepositCategory> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    public MoneyWiseDepositCategoryDialog(final TethysUIFactory<?> pFactory,
                                          final PrometheusEditSet pEditSet,
                                          final MoneyWiseBaseTable<MoneyWiseDepositCategory> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Create a new panel */
        final PrometheusFieldSet<MoneyWiseDepositCategory> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField mySubName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseDepositCategoryType> myTypeButton = myFields.newScrollField(MoneyWiseDepositCategoryType.class);
        final TethysUIScrollButtonField<MoneyWiseDepositCategory> myParentButton = myFields.newScrollField(MoneyWiseDepositCategory.class);

        /* Assign the fields to the panel */
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseDepositCategory::getName);
        myFieldSet.addField(MoneyWiseBasicResource.CATEGORY_SUBCAT, mySubName, MoneyWiseDepositCategory::getSubCategory);
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseDepositCategory::getDesc);
        myFieldSet.addField(MoneyWiseStaticDataType.DEPOSITTYPE, myTypeButton, MoneyWiseDepositCategory::getCategoryType);
        myFieldSet.addField(PrometheusDataResource.DATAGROUP_PARENT, myParentButton, MoneyWiseDepositCategory::getParentCategory);

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
        final MoneyWiseDepositCategory myItem = getItem();
        if (myItem != null) {
            final MoneyWiseDepositCategoryList myCategories = getDataList(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseDepositCategoryList.class);
            setItem(myCategories.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusFieldSet<MoneyWiseDepositCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final MoneyWiseDepositCategory myCategory = getItem();
        final MoneyWiseDepositCategoryType myType = myCategory.getCategoryType();
        final boolean isParent = myType.isDepositCategory(MoneyWiseDepositCategoryClass.PARENT);

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
        myFieldSet.setFieldEditable(MoneyWiseStaticDataType.DEPOSITTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setFieldEditable(PrometheusDataResource.DATAITEM_FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseDepositCategory myCategory = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_SUBCAT.equals(myField)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAGROUP_PARENT.equals(myField)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(MoneyWiseDepositCategory.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseStaticDataType.DEPOSITTYPE.equals(myField)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(MoneyWiseDepositCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseDepositCategory myItem = getItem();
        final MoneyWiseDepositCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final MoneyWiseDepositCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysUIScrollMenu<MoneyWiseDepositCategoryType> pMenu,
                                      final MoneyWiseDepositCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseDepositCategoryType myCurr = pCategory.getCategoryType();
        TethysUIScrollItem<MoneyWiseDepositCategoryType> myActive = null;

        /* Access Deposit Category types */
        final MoneyWiseDepositCategoryTypeList myCategoryTypes = getDataList(MoneyWiseStaticDataType.DEPOSITTYPE, MoneyWiseDepositCategoryTypeList.class);

        /* Loop through the DepositCategoryTypes */
        final Iterator<MoneyWiseDepositCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDepositCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getDepositClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<MoneyWiseDepositCategoryType> myItem = pMenu.addItem(myType);

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
    private static void buildParentMenu(final TethysUIScrollMenu<MoneyWiseDepositCategory> pMenu,
                                        final MoneyWiseDepositCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseDepositCategory myCurr = pCategory.getParentCategory();
        TethysUIScrollItem<MoneyWiseDepositCategory> myActive = null;

        /* Loop through the DepositCategories */
        final MoneyWiseDepositCategoryList myCategories = pCategory.getList();
        final Iterator<MoneyWiseDepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDepositCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            final MoneyWiseDepositCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the parent */
            final TethysUIScrollItem<MoneyWiseDepositCategory> myItem = pMenu.addItem(myCat);

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
