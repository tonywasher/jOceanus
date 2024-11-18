/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType.MoneyWiseTransCategoryTypeList;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Dialog to display/edit/create a TransactionCategory.
 */
public class MoneyWiseTransCategoryPanel
        extends MoneyWiseItemPanel<MoneyWiseTransCategory> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    public MoneyWiseTransCategoryPanel(final TethysUIFactory<?> pFactory,
                                       final PrometheusEditSet pEditSet,
                                       final MoneyWiseBaseTable<MoneyWiseTransCategory> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Create a new panel */
        final PrometheusFieldSet<MoneyWiseTransCategory> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField mySubName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseTransCategoryType> myTypeButton = myFields.newScrollField(MoneyWiseTransCategoryType.class);
        final TethysUIScrollButtonField<MoneyWiseTransCategory> myParentButton = myFields.newScrollField(MoneyWiseTransCategory.class);

        /* Assign the fields to the panel */
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseTransCategory::getName);
        myFieldSet.addField(MoneyWiseBasicResource.CATEGORY_SUBCAT, mySubName, MoneyWiseTransCategory::getSubCategory);
        myFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseTransCategory::getDesc);
        myFieldSet.addField(MoneyWiseStaticDataType.TRANSTYPE, myTypeButton, MoneyWiseTransCategory::getCategoryType);
        myFieldSet.addField(PrometheusDataResource.DATAGROUP_PARENT, myParentButton, MoneyWiseTransCategory::getParentCategory);

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
        final MoneyWiseTransCategory myItem = getItem();
        if (myItem != null) {
            final MoneyWiseTransCategoryList myCategories = getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);
            setItem(myCategories.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusFieldSet<MoneyWiseTransCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final MoneyWiseTransCategory myCategory = getItem();
        final MoneyWiseTransCategoryType myType = myCategory.getCategoryType();
        final CategoryType myCurrType = CategoryType.determineType(myType);
        final boolean showParent = myCurrType.hasSubCatName();

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        myFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Set visibility */
        myFieldSet.setFieldVisible(PrometheusDataResource.DATAGROUP_PARENT, showParent);
        myFieldSet.setFieldVisible(MoneyWiseBasicResource.CATEGORY_SUBCAT, showParent);

        /* Category type cannot be changed if the item is active */
        final boolean canEdit = isEditable && !myCategory.isActive() && myCurrType.isChangeable();
        myFieldSet.setFieldEditable(MoneyWiseStaticDataType.TRANSTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setFieldEditable(PrometheusDataResource.DATAITEM_FIELD_NAME, isEditable && !showParent);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseTransCategory myCategory = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_SUBCAT.equals(myField)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAGROUP_PARENT.equals(myField)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(MoneyWiseTransCategory.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseStaticDataType.TRANSTYPE.equals(myField)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(MoneyWiseTransCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseTransCategory myItem = getItem();
        final MoneyWiseTransCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final MoneyWiseTransCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type list for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysUIScrollMenu<MoneyWiseTransCategoryType> pMenu,
                                      final MoneyWiseTransCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseTransCategoryType myCurr = pCategory.getCategoryType();
        final CategoryType myCurrType = CategoryType.determineType(myCurr);
        TethysUIScrollItem<MoneyWiseTransCategoryType> myActive = null;

        /* Access Transaction Category types */
        final MoneyWiseTransCategoryTypeList myCategoryTypes = getDataList(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseTransCategoryTypeList.class);

        /* Loop through the TransCategoryTypes */
        final Iterator<MoneyWiseTransCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if wrong type */
            bIgnore |= !myCurrType.equals(CategoryType.determineType(myType));
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<MoneyWiseTransCategoryType> myItem = pMenu.addItem(myType);

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
     * Build the parent menu for the item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    private void buildParentMenu(final TethysUIScrollMenu<MoneyWiseTransCategory> pMenu,
                                 final MoneyWiseTransCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseTransCategory myCurr = pCategory.getParentCategory();
        final CategoryType myCurrType = CategoryType.determineType(pCategory);
        TethysUIScrollItem<MoneyWiseTransCategory> myActive = null;

        /* Loop through the TransactionCategories */
        final MoneyWiseTransCategoryList myCategories = getItem().getList();
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCat = myIterator.next();

            /* Ignore deleted and non-subTotal items */
            final MoneyWiseTransCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isSubTotal()) {
                continue;
            }

            /* If we are interested */
            if (myCurrType.isParentMatch(myClass)) {
                /* Create a new action for the type */
                final TethysUIScrollItem<MoneyWiseTransCategory> myItem = pMenu.addItem(myCat);

                /* If this is the active parent */
                if (myCat.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Category Type.
     */
    private enum CategoryType {
        /**
         * Income.
         */
        INCOME,

        /**
         * Expense.
         */
        EXPENSE,

        /**
         * Totals.
         */
        TOTALS,

        /**
         * SubTotal.
         */
        SUBTOTAL,

        /**
         * Singular.
         */
        SINGULAR,

        /**
         * SecurityXfer.
         */
        SECURITYXFER,

        /**
         * Transfer.
         */
        XFER;

        /**
         * Determine type.
         * @param pCategory the transaction category
         * @return the category type
         */
        public static CategoryType determineType(final MoneyWiseTransCategory pCategory) {
            return determineType(pCategory.getCategoryType());
        }

        /**
         * Determine type.
         * @param pType the transaction category type
         * @return the category type
         */
        public static CategoryType determineType(final MoneyWiseTransCategoryType pType) {
            /* Access class */
            final MoneyWiseTransCategoryClass myClass = pType.getCategoryClass();

            /* Handle Totals */
            if (myClass.isTotals()) {
                return TOTALS;
            }

            /* Handle SubTotals */
            if (myClass.isSubTotal()) {
                return SUBTOTAL;
            }

            /* Handle Singular */
            if (myClass.isSingular()) {
                return SINGULAR;
            }

            /* Handle Income */
            if (myClass.isIncome()) {
                return INCOME;
            }

            /* Handle Transfer */
            if (myClass.isTransfer()) {
                return myClass.isSecurityTransfer()
                        ? SECURITYXFER
                        : XFER;
            }

            /* Must be expense */
            return EXPENSE;
        }

        /**
         * Is this type changeable?
         * @return true/false
         */
        public boolean isChangeable() {
            switch (this) {
                case TOTALS:
                case XFER:
                case SINGULAR:
                    return false;
                default:
                    return true;
            }
        }

        /**
         * Is this type changeable?
         * @return true/false
         */
        public boolean hasSubCatName() {
            switch (this) {
                case TOTALS:
                case SUBTOTAL:
                    return false;
                default:
                    return true;
            }
        }

        /**
         * is this parent class a match?
         * @param pClass the parent class
         * @return true/false
         */
        public boolean isParentMatch(final MoneyWiseTransCategoryClass pClass) {
            switch (this) {
                case INCOME:
                    return pClass.isIncome();
                case EXPENSE:
                    return pClass.isExpense();
                case SECURITYXFER:
                    return pClass.isSecurityTransfer();
                default:
                    return false;
            }
        }
    }
}
