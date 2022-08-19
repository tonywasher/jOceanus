/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisLetheFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStringEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * Panel to display/edit/create a DepositCategory.
 */
public class DepositCategoryPanel
        extends MoneyWiseItemPanel<DepositCategory> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public DepositCategoryPanel(final TethysGuiFactory pFactory,
                                final MetisSwingFieldManager pFieldMgr,
                                final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(DataItem.NAMELEN);

        /* Create the text fields */
        final TethysStringEditField myName = pFactory.newStringField();
        final TethysStringEditField mySubName = pFactory.newStringField();
        final TethysStringEditField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysScrollButtonManager<DepositCategoryType> myTypeButton = pFactory.newScrollButton();
        final TethysScrollButtonManager<DepositCategory> myParentButton = pFactory.newScrollButton();

        /* Assign the fields to the panel */
        myPanel.addField(CategoryBase.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(CategoryBase.FIELD_SUBCAT, MetisDataType.STRING, mySubName);
        myPanel.addField(CategoryBase.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(DepositCategory.FIELD_CATTYPE, DepositCategoryType.class, myTypeButton);
        myPanel.addField(CategoryBase.FIELD_PARENT, DepositCategory.class, myParentButton);

        /* Define the panel */
        defineMainPanel(myPanel);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildCategoryTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final DepositCategory myItem = getItem();
        if (myItem != null) {
            final DepositCategoryList myCategories = getDataList(MoneyWiseDataType.DEPOSITCATEGORY, DepositCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final MetisSwingFieldSet<DepositCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final DepositCategory myCategory = getItem();
        final DepositCategoryType myType = myCategory.getCategoryType();
        final boolean isParent = myType.isDepositCategory(DepositCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        myFieldSet.setVisibility(CategoryBase.FIELD_DESC, bShowDesc);

        /* Set visibility */
        myFieldSet.setVisibility(CategoryBase.FIELD_PARENT, !isParent);
        myFieldSet.setVisibility(CategoryBase.FIELD_SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        myFieldSet.setEditable(DepositCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setEditable(CategoryBase.FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final MetisLetheFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisLetheField myField = pUpdate.getField();
        final DepositCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(CategoryBase.FIELD_NAME)) {
            /* Update the SUBCATEGORY(!!) Name */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(CategoryBase.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getString());
        } else if (myField.equals(CategoryBase.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(DepositCategory.class));
        } else if (myField.equals(CategoryBase.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getString());
        } else if (myField.equals(DepositCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(DepositCategoryType.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final DepositCategory myItem = getItem();
        final DepositCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final DepositCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysScrollMenu<DepositCategoryType> pMenu,
                                      final DepositCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategoryType myCurr = pCategory.getCategoryType();
        TethysScrollMenuItem<DepositCategoryType> myActive = null;

        /* Access Deposit Category types */
        final DepositCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.DEPOSITTYPE, DepositCategoryTypeList.class);

        /* Loop through the DepositCategoryTypes */
        final Iterator<DepositCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final DepositCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getDepositClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysScrollMenuItem<DepositCategoryType> myItem = pMenu.addItem(myType);

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
    private static void buildParentMenu(final TethysScrollMenu<DepositCategory> pMenu,
                                        final DepositCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategory myCurr = pCategory.getParentCategory();
        TethysScrollMenuItem<DepositCategory> myActive = null;

        /* Loop through the DepositCategories */
        final DepositCategoryList myCategories = pCategory.getList();
        final Iterator<DepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final DepositCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            final DepositCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the parent */
            final TethysScrollMenuItem<DepositCategory> myItem = pMenu.addItem(myCat);

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
