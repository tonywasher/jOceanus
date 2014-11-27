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
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a LoanCategory.
 */
public class LoanCategoryPanel
        extends MoneyWiseDataItemPanel<LoanCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6589528251463766615L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<LoanCategory> theFieldSet;

    /**
     * Category Type Button Field.
     */
    private final JScrollButton<LoanCategoryType> theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JScrollButton<LoanCategory> theParentButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanCategoryPanel(final JFieldManager pFieldMgr,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField mySubName = new JTextField();
        JTextField myDesc = new JTextField();

        /* Create the buttons */
        theTypeButton = new JScrollButton<LoanCategoryType>();
        theParentButton = new JScrollButton<LoanCategory>();

        /* restrict the fields */
        restrictField(myName, LoanCategory.NAMELEN);
        restrictField(mySubName, LoanCategory.NAMELEN);
        restrictField(myDesc, LoanCategory.NAMELEN);
        restrictField(theTypeButton, LoanCategory.NAMELEN);
        restrictField(theParentButton, LoanCategory.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(LoanCategory.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(LoanCategory.FIELD_SUBCAT, DataType.STRING, mySubName);
        theFieldSet.addFieldElement(LoanCategory.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(LoanCategory.FIELD_CATTYPE, LoanCategoryType.class, theTypeButton);
        theFieldSet.addFieldElement(LoanCategory.FIELD_PARENT, LoanCategory.class, theParentButton);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(LoanCategory.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(LoanCategory.FIELD_SUBCAT, myPanel);
        theFieldSet.addFieldToPanel(LoanCategory.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(LoanCategory.FIELD_CATTYPE, myPanel);
        theFieldSet.addFieldToPanel(LoanCategory.FIELD_PARENT, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        LoanCategory myItem = getItem();
        if (myItem != null) {
            LoanCategoryList myCategories = getDataList(MoneyWiseDataType.LOANCATEGORY, LoanCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        LoanCategory myCategory = getItem();
        LoanCategoryType myType = myCategory.getCategoryType();
        boolean isParent = myType.isLoanCategory(LoanCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        theFieldSet.setVisibility(LoanCategory.FIELD_DESC, bShowDesc);

        /* Set visibility */
        theFieldSet.setVisibility(LoanCategory.FIELD_PARENT, !isParent);
        theFieldSet.setVisibility(LoanCategory.FIELD_SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        theFieldSet.setEditable(LoanCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        theFieldSet.setEditable(LoanCategory.FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        LoanCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(LoanCategory.FIELD_NAME)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(LoanCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(LoanCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(LoanCategory.class));
        } else if (myField.equals(LoanCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getString());
        } else if (myField.equals(LoanCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(LoanCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        LoanCategory myItem = getItem();
        LoanCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            LoanCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenuBuilder the menu builder
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final JScrollMenuBuilder<LoanCategoryType> pMenuBuilder,
                                      final LoanCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        LoanCategoryType myCurr = pCategory.getCategoryType();
        JMenuItem myActive = null;

        /* Access Loan Category types */
        LoanCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.LOANTYPE, LoanCategoryTypeList.class);

        /* Loop through the LoanCategoryTypes */
        Iterator<LoanCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            LoanCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getLoanClass().isParentCategory();
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
     * Build the parent menu for an item.
     * @param pMenuBuilder the menu builder
     * @param pCategory the category to build for
     */
    private void buildParentMenu(final JScrollMenuBuilder<LoanCategory> pMenuBuilder,
                                 final LoanCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        LoanCategory myCurr = pCategory.getParentCategory();
        JMenuItem myActive = null;

        /* Loop through the LoanCategories */
        LoanCategoryList myCategories = pCategory.getList();
        Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            LoanCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            LoanCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the parent */
            JMenuItem myItem = pMenuBuilder.addItem(myCat);

            /* If this is the active parent */
            if (myCat.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
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
        private final JScrollMenuBuilder<LoanCategoryType> theTypeMenuBuilder;

        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<LoanCategory> theParentMenuBuilder;

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
}
