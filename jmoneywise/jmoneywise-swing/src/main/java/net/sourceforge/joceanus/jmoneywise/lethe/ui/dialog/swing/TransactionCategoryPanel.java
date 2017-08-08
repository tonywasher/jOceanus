/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisEosFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisEosFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Dialog to display/edit/create a TransactionCategory.
 */
public class TransactionCategoryPanel
        extends MoneyWiseEosItemPanel<TransactionCategory> {
    /**
     * The Field Set.
     */
    private final MetisEosFieldSet<TransactionCategory> theFieldSet;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionCategoryPanel(final TethysSwingGuiFactory pFactory,
                                    final MetisEosFieldManager pFieldMgr,
                                    final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                    final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        TethysSwingStringTextField myName = pFactory.newStringField();
        TethysSwingStringTextField mySubName = pFactory.newStringField();
        TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        TethysSwingScrollButtonManager<TransactionCategoryType> myTypeButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<TransactionCategory> myParentButton = pFactory.newScrollButton();

        /* restrict the fields */
        restrictField(myName, TransactionCategory.NAMELEN);
        restrictField(mySubName, TransactionCategory.NAMELEN);
        restrictField(myDesc, TransactionCategory.NAMELEN);
        restrictField(myTypeButton, TransactionCategory.NAMELEN);
        restrictField(myParentButton, TransactionCategory.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionCategory.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_SUBCAT, MetisDataType.STRING, mySubName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_CATTYPE, TransactionCategoryType.class, myTypeButton);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_PARENT, TransactionCategory.class, myParentButton);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_SUBCAT, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_CATTYPE, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_PARENT, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildCategoryTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        TransactionCategory myItem = getItem();
        if (myItem != null) {
            TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        TransactionCategory myCategory = getItem();
        TransactionCategoryType myType = myCategory.getCategoryType();
        CategoryType myCurrType = CategoryType.determineType(myType);
        boolean showParent = myCurrType.hasSubCatName();

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        theFieldSet.setVisibility(TransactionCategory.FIELD_DESC, bShowDesc);

        /* Set visibility */
        theFieldSet.setVisibility(TransactionCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(TransactionCategory.FIELD_SUBCAT, showParent);

        /* Category type cannot be changed if the item is active */
        boolean canEdit = isEditable && !myCategory.isActive() && myCurrType.isChangeable();
        theFieldSet.setEditable(TransactionCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        theFieldSet.setEditable(TransactionCategory.FIELD_NAME, isEditable && !showParent);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
        TransactionCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(TransactionCategory.FIELD_NAME)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(TransactionCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(TransactionCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(TransactionCategory.class));
        } else if (myField.equals(TransactionCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getString());
        } else if (myField.equals(TransactionCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(TransactionCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        TransactionCategory myItem = getItem();
        TransactionCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            TransactionCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type list for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysScrollMenu<TransactionCategoryType, Icon> pMenu,
                                      final TransactionCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        TransactionCategoryType myCurr = pCategory.getCategoryType();
        CategoryType myCurrType = CategoryType.determineType(myCurr);
        TethysScrollMenuItem<TransactionCategoryType> myActive = null;

        /* Access Transaction Category types */
        TransactionCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.TRANSTYPE, TransactionCategoryTypeList.class);

        /* Loop through the TransCategoryTypes */
        Iterator<TransactionCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            TransactionCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if wrong type */
            bIgnore |= !myCurrType.equals(CategoryType.determineType(myType));
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            TethysScrollMenuItem<TransactionCategoryType> myItem = pMenu.addItem(myType);

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
    private void buildParentMenu(final TethysScrollMenu<TransactionCategory, Icon> pMenu,
                                 final TransactionCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        TransactionCategory myCurr = pCategory.getParentCategory();
        CategoryType myCurrType = CategoryType.determineType(pCategory);
        TethysScrollMenuItem<TransactionCategory> myActive = null;

        /* Loop through the TransactionCategories */
        TransactionCategoryList myCategories = getItem().getList();
        Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCat = myIterator.next();

            /* Ignore deleted and non-subTotal items */
            TransactionCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isSubTotal()) {
                continue;
            }

            /* If we are interested */
            if (myCurrType.isParentMatch(myClass)) {
                /* Create a new action for the type */
                TethysScrollMenuItem<TransactionCategory> myItem = pMenu.addItem(myCat);

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
    public enum CategoryType {
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
            TransactionCategoryClass myClass = pType.getCategoryClass();

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
