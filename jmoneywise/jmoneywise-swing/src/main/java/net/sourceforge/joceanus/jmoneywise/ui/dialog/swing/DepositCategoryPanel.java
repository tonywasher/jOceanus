/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog.swing;

import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Panel to display/edit/create a DepositCategory.
 */
public class DepositCategoryPanel
        extends MoneyWiseDataItemPanel<DepositCategory> {
    /**
     * The Field Set.
     */
    private final MetisFieldSet<DepositCategory> theFieldSet;

    /**
     * Category Type Button Field.
     */
    private final JScrollButton<DepositCategoryType> theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JScrollButton<DepositCategory> theParentButton;

    /**
     * The CategoryType Menu Builder.
     */
    private final JScrollMenuBuilder<DepositCategoryType> theTypeMenuBuilder;

    /**
     * The Parent Menu Builder.
     */
    private final JScrollMenuBuilder<DepositCategory> theParentMenuBuilder;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public DepositCategoryPanel(final TethysSwingGuiFactory pFactory,
                                final MetisFieldManager pFieldMgr,
                                final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField mySubName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, DepositCategory.NAMELEN);
        restrictField(mySubName, DepositCategory.NAMELEN);
        restrictField(myDesc, DepositCategory.NAMELEN);

        /* Create the buttons */
        theTypeButton = new JScrollButton<>();
        theParentButton = new JScrollButton<>();

        /* restrict the fields */
        restrictField(myName, DepositCategory.NAMELEN);
        restrictField(mySubName, DepositCategory.NAMELEN);
        restrictField(myDesc, DepositCategory.NAMELEN);
        restrictField(theTypeButton, DepositCategory.NAMELEN);
        restrictField(theParentButton, DepositCategory.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(DepositCategory.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(DepositCategory.FIELD_SUBCAT, MetisDataType.STRING, mySubName);
        theFieldSet.addFieldElement(DepositCategory.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(DepositCategory.FIELD_CATTYPE, DepositCategoryType.class, theTypeButton);
        theFieldSet.addFieldElement(DepositCategory.FIELD_PARENT, DepositCategory.class, theParentButton);

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_SUBCAT, myPanel);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_CATTYPE, myPanel);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_PARENT, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Layout the panel */
        layoutPanel();

        /* Create the listeners */
        theTypeMenuBuilder = theTypeButton.getMenuBuilder();
        theTypeMenuBuilder.getEventRegistrar().addEventListener(e -> buildCategoryTypeMenu(theTypeMenuBuilder, getItem()));
        theParentMenuBuilder = theParentButton.getMenuBuilder();
        theParentMenuBuilder.getEventRegistrar().addEventListener(e -> buildParentMenu(theParentMenuBuilder, getItem()));
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        DepositCategory myItem = getItem();
        if (myItem != null) {
            DepositCategoryList myCategories = getDataList(MoneyWiseDataType.DEPOSITCATEGORY, DepositCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        DepositCategory myCategory = getItem();
        DepositCategoryType myType = myCategory.getCategoryType();
        boolean isParent = myType.isDepositCategory(DepositCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        theFieldSet.setVisibility(DepositCategory.FIELD_DESC, bShowDesc);

        /* Set visibility */
        theFieldSet.setVisibility(DepositCategory.FIELD_PARENT, !isParent);
        theFieldSet.setVisibility(DepositCategory.FIELD_SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        theFieldSet.setEditable(DepositCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        theFieldSet.setEditable(DepositCategory.FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
        DepositCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(DepositCategory.FIELD_NAME)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(DepositCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(DepositCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(DepositCategory.class));
        } else if (myField.equals(DepositCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getString());
        } else if (myField.equals(DepositCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(DepositCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        DepositCategory myItem = getItem();
        DepositCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            DepositCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenuBuilder the menu builder
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final JScrollMenuBuilder<DepositCategoryType> pMenuBuilder,
                                      final DepositCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        DepositCategoryType myCurr = pCategory.getCategoryType();
        JMenuItem myActive = null;

        /* Access Deposit Category types */
        DepositCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.DEPOSITTYPE, DepositCategoryTypeList.class);

        /* Loop through the DepositCategoryTypes */
        Iterator<DepositCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            DepositCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getDepositClass().isParentCategory();
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
    private static void buildParentMenu(final JScrollMenuBuilder<DepositCategory> pMenuBuilder,
                                        final DepositCategory pCategory) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        DepositCategory myCurr = pCategory.getParentCategory();
        JMenuItem myActive = null;

        /* Loop through the DepositCategories */
        DepositCategoryList myCategories = pCategory.getList();
        Iterator<DepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            DepositCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            DepositCategoryClass myClass = myCat.getCategoryTypeClass();
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
}
