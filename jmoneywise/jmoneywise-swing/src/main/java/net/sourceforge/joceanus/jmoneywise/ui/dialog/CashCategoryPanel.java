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

import net.sourceforge.joceanus.jmetis.data.DataType;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.field.JFieldSetBase.FieldUpdate;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.ui.swing.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a CashCategory.
 */
public class CashCategoryPanel
        extends MoneyWiseDataItemPanel<CashCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -2519622794507877466L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<CashCategory> theFieldSet;

    /**
     * Category Type Button Field.
     */
    private final JScrollButton<CashCategoryType> theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JScrollButton<CashCategory> theParentButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public CashCategoryPanel(final JFieldManager pFieldMgr,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField mySubName = new JTextField();
        JTextField myDesc = new JTextField();

        /* Create the buttons */
        theTypeButton = new JScrollButton<CashCategoryType>();
        theParentButton = new JScrollButton<CashCategory>();

        /* restrict the fields */
        restrictField(myName, CashCategory.NAMELEN);
        restrictField(mySubName, CashCategory.NAMELEN);
        restrictField(myDesc, CashCategory.NAMELEN);
        restrictField(theTypeButton, CashCategory.NAMELEN);
        restrictField(theParentButton, CashCategory.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(CashCategory.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(CashCategory.FIELD_SUBCAT, DataType.STRING, mySubName);
        theFieldSet.addFieldElement(CashCategory.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(CashCategory.FIELD_CATTYPE, CashCategoryType.class, theTypeButton);
        theFieldSet.addFieldElement(CashCategory.FIELD_PARENT, CashCategory.class, theParentButton);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashCategory.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(CashCategory.FIELD_SUBCAT, myPanel);
        theFieldSet.addFieldToPanel(CashCategory.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(CashCategory.FIELD_CATTYPE, myPanel);
        theFieldSet.addFieldToPanel(CashCategory.FIELD_PARENT, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        CashCategory myItem = getItem();
        if (myItem != null) {
            CashCategoryList myCategories = getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        CashCategory myCategory = getItem();
        CashCategoryType myType = myCategory.getCategoryType();
        boolean isParent = myType.isCashCategory(CashCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        theFieldSet.setVisibility(CashCategory.FIELD_DESC, bShowDesc);

        /* Set visibility */
        theFieldSet.setVisibility(CashCategory.FIELD_PARENT, !isParent);
        theFieldSet.setVisibility(CashCategory.FIELD_SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        theFieldSet.setEditable(CashCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        theFieldSet.setEditable(CashCategory.FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        CashCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(CashCategory.FIELD_NAME)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(CashCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(CashCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(CashCategory.class));
        } else if (myField.equals(CashCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getString());
        } else if (myField.equals(CashCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(CashCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        CashCategory myItem = getItem();
        CashCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            CashCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenuBuilder the menu builder
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final JScrollMenuBuilder<CashCategoryType> pMenuBuilder,
                                      final CashCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        CashCategoryType myCurr = pCategory.getCategoryType();
        JMenuItem myActive = null;

        /* Access Cash Category types */
        CashCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.CASHTYPE, CashCategoryTypeList.class);

        /* Loop through the CashCategoryTypes */
        Iterator<CashCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            CashCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getCashClass().isParentCategory();
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
    private void buildParentMenu(final JScrollMenuBuilder<CashCategory> pMenuBuilder,
                                 final CashCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        CashCategory myCurr = pCategory.getParentCategory();
        JMenuItem myActive = null;

        /* Loop through the CashCategories */
        CashCategoryList myCategories = pCategory.getList();
        Iterator<CashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            CashCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            CashCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the type */
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
            implements JOceanusChangeEventListener {
        /**
         * The CategoryType Menu Builder.
         */
        private final JScrollMenuBuilder<CashCategoryType> theTypeMenuBuilder;

        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<CashCategory> theParentMenuBuilder;

        /**
         * ParentMenu Registration.
         */
        private final JOceanusChangeRegistration theParentMenuReg;

        /**
         * TypeMenu Registration.
         */
        private final JOceanusChangeRegistration theTypeMenuReg;

        /**
         * Constructor.
         */
        private CategoryListener() {
            /* Access the MenuBuilders */
            theTypeMenuBuilder = theTypeButton.getMenuBuilder();
            theTypeMenuReg = theTypeMenuBuilder.getEventRegistrar().addChangeListener(this);
            theParentMenuBuilder = theParentButton.getMenuBuilder();
            theParentMenuReg = theParentMenuBuilder.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* Handle menu builders */
            if (theTypeMenuReg.isRelevant(pEvent)) {
                buildCategoryTypeMenu(theTypeMenuBuilder, getItem());
            } else if (theParentMenuReg.isRelevant(pEvent)) {
                buildParentMenu(theParentMenuBuilder, getItem());
            }
        }
    }
}
