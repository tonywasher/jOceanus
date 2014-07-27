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
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.ui.TransactionCategoryTable.CategoryType;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Dialog to display/edit/create a TransactionCategory.
 */
public class TransactionCategoryPanel
        extends DataItemPanel<TransactionCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -9025803624405965777L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<TransactionCategory> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * SubName Text Field.
     */
    private final JTextField theSubName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

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
     */
    public TransactionCategoryPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField(TransactionCategory.NAMELEN);
        theSubName = new JTextField(TransactionCategory.NAMELEN);
        theDesc = new JTextField(TransactionCategory.DESCLEN);

        /* Create the buttons */
        theTypeButton = new JScrollButton<TransactionCategoryType>();
        theParentButton = new JScrollButton<TransactionCategory>();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionCategory.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_SUBCAT, DataType.STRING, theSubName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_CATTYPE, TransactionCategoryType.class, theTypeButton);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_PARENT, TransactionCategory.class, theParentButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_SUBCAT, this);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_CATTYPE, this);
        theFieldSet.addFieldToPanel(TransactionCategory.FIELD_PARENT, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        TransactionCategoryType myCurr = getItem().getCategoryType();
        CategoryType myCurrType = CategoryType.determineType(myCurr);
        boolean showParent = myCurrType.equals(CategoryType.SUBTOTAL);

        /* Set visibility */
        theFieldSet.setVisibility(TransactionCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(TransactionCategory.FIELD_NAME, showParent);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        TransactionCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(TransactionCategory.FIELD_NAME)) {
            /* Update the Name */
            myCategory.setCategoryName(pUpdate.getValue(String.class));
        } else if (myField.equals(TransactionCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (myField.equals(TransactionCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(TransactionCategory.class));
        } else if (myField.equals(TransactionCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(TransactionCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(TransactionCategoryType.class));
        }
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
                buildCategoryTypeMenu();
            } else if (theParentMenuBuilder.equals(o)) {
                buildParentMenu();
            }
        }

        /**
         * Build the category type list for the item.
         */
        private void buildCategoryTypeMenu() {
            /* Clear the menu */
            theTypeMenuBuilder.clearMenu();

            /* Record active item */
            TransactionCategory myCategory = getItem();
            TransactionCategoryType myCurr = myCategory.getCategoryType();
            CategoryType myCurrType = CategoryType.determineType(myCurr);
            JMenuItem myActive = null;

            /* Access Transaction Category types */
            MoneyWiseData myData = myCategory.getDataSet();
            TransactionCategoryTypeList myCategoryTypes = myData.getTransCategoryTypes();

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
                JMenuItem myItem = theTypeMenuBuilder.addItem(myType);

                /* If this is the active type */
                if (myType.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theTypeMenuBuilder.showItem(myActive);
        }

        /**
         * Build the parent list for the item.
         */
        private void buildParentMenu() {
            /* Clear the menu */
            theParentMenuBuilder.clearMenu();

            /* Record active item */
            TransactionCategory myCategory = getItem();
            TransactionCategory myCurr = myCategory.getParentCategory();
            Boolean isExpense = myCategory.getCategoryTypeClass().isExpense();
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
                if (myClass.isExpense() == isExpense) {
                    /* Create a new action for the type */
                    JMenuItem myItem = theParentMenuBuilder.addItem(myCat);

                    /* If this is the active type */
                    if (myCat.equals(myCurr)) {
                        /* Record it */
                        myActive = myItem;
                    }
                }
            }

            /* Ensure active item is visible */
            theParentMenuBuilder.showItem(myActive);
        }
    }
}
