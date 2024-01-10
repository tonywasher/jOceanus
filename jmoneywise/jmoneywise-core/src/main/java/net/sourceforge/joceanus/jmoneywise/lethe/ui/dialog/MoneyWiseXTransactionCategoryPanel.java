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
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
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
 * Dialog to display/edit/create a TransactionCategory.
 */
public class MoneyWiseXTransactionCategoryPanel
        extends MoneyWiseXItemPanel<TransactionCategory> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public MoneyWiseXTransactionCategoryPanel(final TethysUIFactory<?> pFactory,
                                              final UpdateSet pUpdateSet,
                                              final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Create a new panel */
        final PrometheusXFieldSet<TransactionCategory> myFieldSet = getFieldSet();

        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField mySubName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<TransactionCategoryType> myTypeButton = myFields.newScrollField(TransactionCategoryType.class);
        final TethysUIScrollButtonField<TransactionCategory> myParentButton = myFields.newScrollField(TransactionCategory.class);

        /* Assign the fields to the panel */
        myFieldSet.addField(MoneyWiseCategoryDataId.NAME, myName, TransactionCategory::getName);
        myFieldSet.addField(MoneyWiseCategoryDataId.SUBCAT, mySubName, TransactionCategory::getSubCategory);
        myFieldSet.addField(MoneyWiseCategoryDataId.DESC, myDesc, TransactionCategory::getDesc);
        myFieldSet.addField(MoneyWiseCategoryDataId.TRANSCATTYPE, myTypeButton, TransactionCategory::getCategoryType);
        myFieldSet.addField(MoneyWiseCategoryDataId.PARENT, myParentButton, TransactionCategory::getParentCategory);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildCategoryTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final TransactionCategory myItem = getItem();
        if (myItem != null) {
            final TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final PrometheusXFieldSet<TransactionCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final TransactionCategory myCategory = getItem();
        final TransactionCategoryType myType = myCategory.getCategoryType();
        final CategoryType myCurrType = CategoryType.determineType(myType);
        final boolean showParent = myCurrType.hasSubCatName();

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        myFieldSet.setFieldVisible(MoneyWiseCategoryDataId.DESC, bShowDesc);

        /* Set visibility */
        myFieldSet.setFieldVisible(MoneyWiseCategoryDataId.PARENT, showParent);
        myFieldSet.setFieldVisible(MoneyWiseCategoryDataId.SUBCAT, showParent);

        /* Category type cannot be changed if the item is active */
        final boolean canEdit = isEditable && !myCategory.isActive() && myCurrType.isChangeable();
        myFieldSet.setFieldEditable(MoneyWiseCategoryDataId.TRANSCATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setFieldEditable(MoneyWiseCategoryDataId.NAME, isEditable && !showParent);
    }

    @Override
    protected void updateField(final PrometheusXFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final TransactionCategory myCategory = getItem();

        /* Process updates */
        if (MoneyWiseCategoryDataId.NAME.equals(myField)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseCategoryDataId.SUBCAT.equals(myField)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (MoneyWiseCategoryDataId.PARENT.equals(myField)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(TransactionCategory.class));
        } else if (MoneyWiseCategoryDataId.DESC.equals(myField)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseCategoryDataId.DEPOSITCATTYPE.equals(myField)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(TransactionCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final TransactionCategory myItem = getItem();
        final TransactionCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final TransactionCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type list for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysUIScrollMenu<TransactionCategoryType> pMenu,
                                      final TransactionCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionCategoryType myCurr = pCategory.getCategoryType();
        final CategoryType myCurrType = CategoryType.determineType(myCurr);
        TethysUIScrollItem<TransactionCategoryType> myActive = null;

        /* Access Transaction Category types */
        final TransactionCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.TRANSTYPE, TransactionCategoryTypeList.class);

        /* Loop through the TransCategoryTypes */
        final Iterator<TransactionCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if wrong type */
            bIgnore |= !myCurrType.equals(CategoryType.determineType(myType));
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysUIScrollItem<TransactionCategoryType> myItem = pMenu.addItem(myType);

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
    private void buildParentMenu(final TethysUIScrollMenu<TransactionCategory> pMenu,
                                 final TransactionCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionCategory myCurr = pCategory.getParentCategory();
        final CategoryType myCurrType = CategoryType.determineType(pCategory);
        TethysUIScrollItem<TransactionCategory> myActive = null;

        /* Loop through the TransactionCategories */
        final TransactionCategoryList myCategories = getItem().getList();
        final Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategory myCat = myIterator.next();

            /* Ignore deleted and non-subTotal items */
            final TransactionCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isSubTotal()) {
                continue;
            }

            /* If we are interested */
            if (myCurrType.isParentMatch(myClass)) {
                /* Create a new action for the type */
                final TethysUIScrollItem<TransactionCategory> myItem = pMenu.addItem(myCat);

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
        public static CategoryType determineType(final TransactionCategory pCategory) {
            return determineType(pCategory.getCategoryType());
        }

        /**
         * Determine type.
         * @param pType the transaction category type
         * @return the category type
         */
        public static CategoryType determineType(final TransactionCategoryType pType) {
            /* Access class */
            final TransactionCategoryClass myClass = pType.getCategoryClass();

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
        public boolean isParentMatch(final TransactionCategoryClass pClass) {
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
