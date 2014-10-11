/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Dialog to display/edit/create a TransactionCategory.
 */
public class TransactionCategoryPanel
        extends MoneyWiseDataItemPanel<TransactionCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -9025803624405965777L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<TransactionCategory> theFieldSet;

    /**
     * Category Type Button Field.
     */
    private final JScrollButton<TransactionCategoryType> theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JScrollButton<TransactionCategory> theParentButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionCategoryPanel(final JFieldManager pFieldMgr,
                                    final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                    final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField mySubName = new JTextField();
        JTextField myDesc = new JTextField();

        /* Create the buttons */
        theTypeButton = new JScrollButton<TransactionCategoryType>();
        theParentButton = new JScrollButton<TransactionCategory>();

        /* restrict the fields */
        restrictField(myName, TransactionCategory.NAMELEN);
        restrictField(mySubName, TransactionCategory.NAMELEN);
        restrictField(myDesc, TransactionCategory.NAMELEN);
        restrictField(theTypeButton, TransactionCategory.NAMELEN);
        restrictField(theParentButton, TransactionCategory.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionCategory.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_SUBCAT, DataType.STRING, mySubName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_CATTYPE, TransactionCategoryType.class, theTypeButton);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_PARENT, TransactionCategory.class, theParentButton);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_SUBCAT, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_CATTYPE, myPanel);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_PARENT, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        TransactionCategory myItem = getItem();
        if (myItem != null) {
            TransactionCategoryList myCategories = findDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);
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
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
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
    protected void buildGoToMenu() {
        TransactionCategory myItem = getItem();
        TransactionCategoryType myType = myItem.getCategoryType();
        TransactionCategory myParent = myItem.getParentCategory();
        if (!getUpdateSet().hasUpdates()) {
            buildGoToEvent(myType);
        }
        buildGoToEvent(myParent);
    }

    /**
     * Build the category type list for an item.
     * @param pMenuBuilder the menu builder
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final JScrollMenuBuilder<TransactionCategoryType> pMenuBuilder,
                                      final TransactionCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        TransactionCategoryType myCurr = pCategory.getCategoryType();
        CategoryType myCurrType = CategoryType.determineType(myCurr);
        JMenuItem myActive = null;

        /* Access Transaction Category types */
        TransactionCategoryTypeList myCategoryTypes = findDataList(MoneyWiseDataType.TRANSTYPE, TransactionCategoryTypeList.class);

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
            JMenuItem myItem = pMenuBuilder.addItem(myType);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the parent list for the item.
     * @param pMenuBuilder the menu builder
     * @param pCategory the category to build for
     */
    private void buildParentMenu(final JScrollMenuBuilder<TransactionCategory> pMenuBuilder,
                                 final TransactionCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        TransactionCategory myCurr = pCategory.getParentCategory();
        CategoryType myCurrType = CategoryType.determineType(pCategory);
        JMenuItem myActive = null;

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
                JMenuItem myItem = pMenuBuilder.addItem(myCat);

                /* If this is the active parent */
                if (myCat.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Category Listener.
     */
    private final class CategoryListener
            implements ChangeListener {
        /**
         * The CategoryType Menu Builder.
         */
        private final JScrollMenuBuilder<TransactionCategoryType> theTypeMenuBuilder;

        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<TransactionCategory> theParentMenuBuilder;

        /**
         * Constructor.
         */
        private CategoryListener() {
            /* Access the MenuBuilders */
            theTypeMenuBuilder = theTypeButton.getMenuBuilder();
            theTypeMenuBuilder.addChangeListener(this);
            theParentMenuBuilder = theParentButton.getMenuBuilder();
            theParentMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theTypeMenuBuilder.equals(o)) {
                buildCategoryTypeMenu(theTypeMenuBuilder, getItem());
            } else if (theParentMenuBuilder.equals(o)) {
                buildParentMenu(theParentMenuBuilder, getItem());
            }
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
         * StockXfer.
         */
        STOCKXFER,

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
                return myClass.isStockTransfer()
                                                ? STOCKXFER
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
                case STOCKXFER:
                    return pClass.isStockTransfer();
                default:
                    return false;
            }
        }
    }
}
