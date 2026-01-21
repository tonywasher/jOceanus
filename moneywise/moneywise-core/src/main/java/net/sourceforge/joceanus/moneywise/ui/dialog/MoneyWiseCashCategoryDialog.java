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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType.MoneyWiseCashCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import io.github.tonywasher.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

import java.util.Iterator;

/**
 * Panel to display/edit/create a CashCategory.
 */
public class MoneyWiseCashCategoryDialog
        extends MoneyWiseItemPanel<MoneyWiseCashCategory> {
    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner   the owning table
     */
    public MoneyWiseCashCategoryDialog(final TethysUIFactory<?> pFactory,
                                       final PrometheusEditSet pEditSet,
                                       final MoneyWiseBaseTable<MoneyWiseCashCategory> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Create a new panel */
        final PrometheusFieldSet<MoneyWiseCashCategory> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField mySubName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseCashCategoryType> myTypeButton = myFields.newScrollField(MoneyWiseCashCategoryType.class);
        final TethysUIScrollButtonField<MoneyWiseCashCategory> myParentButton = myFields.newScrollField(MoneyWiseCashCategory.class);

        /* Assign the fields to the panel */
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseCashCategory::getName);
        myFieldSet.addField(MoneyWiseBasicResource.CATEGORY_SUBCAT, mySubName, MoneyWiseCashCategory::getSubCategory);
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseCashCategory::getDesc);
        myFieldSet.addField(MoneyWiseStaticDataType.CASHTYPE, myTypeButton, MoneyWiseCashCategory::getCategoryType);
        myFieldSet.addField(PrometheusDataResource.DATAGROUP_PARENT, myParentButton, MoneyWiseCashCategory::getParentCategory);

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
        final MoneyWiseCashCategory myItem = getItem();
        if (myItem != null) {
            final MoneyWiseCashCategoryList myCategories = getDataList(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseCashCategoryList.class);
            setItem(myCategories.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusFieldSet<MoneyWiseCashCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final MoneyWiseCashCategory myCategory = getItem();
        final MoneyWiseCashCategoryType myType = myCategory.getCategoryType();
        final boolean isParent = myType.isCashCategory(MoneyWiseCashCategoryClass.PARENT);

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
        myFieldSet.setFieldEditable(MoneyWiseStaticDataType.CASHTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setFieldEditable(PrometheusDataResource.DATAITEM_FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseCashCategory myCategory = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_SUBCAT.equals(myField)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAGROUP_PARENT.equals(myField)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(MoneyWiseCashCategory.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseStaticDataType.CASHTYPE.equals(myField)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(MoneyWiseCashCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseCashCategory myItem = getItem();
        final MoneyWiseCashCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final MoneyWiseCashCategoryType myType = myItem.getCategoryType();
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
    public void buildCategoryTypeMenu(final TethysUIScrollMenu<MoneyWiseCashCategoryType> pMenu,
                                      final MoneyWiseCashCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCashCategoryType myCurr = pCategory.getCategoryType();
        TethysUIScrollItem<MoneyWiseCashCategoryType> myActive = null;

        /* Access Cash Category types */
        final MoneyWiseCashCategoryTypeList myCategoryTypes = getDataList(MoneyWiseStaticDataType.CASHTYPE, MoneyWiseCashCategoryTypeList.class);

        /* Loop through the CashCategoryTypes */
        final Iterator<MoneyWiseCashCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseCashCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getCashClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<MoneyWiseCashCategoryType> myItem = pMenu.addItem(myType);

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
    private static void buildParentMenu(final TethysUIScrollMenu<MoneyWiseCashCategory> pMenu,
                                        final MoneyWiseCashCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCashCategory myCurr = pCategory.getParentCategory();
        TethysUIScrollItem<MoneyWiseCashCategory> myActive = null;

        /* Loop through the CashCategories */
        final MoneyWiseCashCategoryList myCategories = pCategory.getList();
        final Iterator<MoneyWiseCashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseCashCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            final MoneyWiseCashCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<MoneyWiseCashCategory> myItem = pMenu.addItem(myCat);

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
