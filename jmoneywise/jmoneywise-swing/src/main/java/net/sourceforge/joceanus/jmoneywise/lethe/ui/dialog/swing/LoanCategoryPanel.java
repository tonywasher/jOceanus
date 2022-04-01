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
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Panel to display/edit/create a LoanCategory.
 */
public class LoanCategoryPanel
        extends MoneyWiseItemPanel<LoanCategory> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanCategoryPanel(final TethysSwingGuiFactory pFactory,
                             final MetisSwingFieldManager pFieldMgr,
                             final UpdateSet<MoneyWiseDataType> pUpdateSet,
                             final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(LoanCategory.NAMELEN);

        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField mySubName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<LoanCategoryType> myTypeButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<LoanCategory> myParentButton = pFactory.newScrollButton();

        /* Assign the fields to the panel */
        myPanel.addField(LoanCategory.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(LoanCategory.FIELD_SUBCAT, MetisDataType.STRING, mySubName);
        myPanel.addField(LoanCategory.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(LoanCategory.FIELD_CATTYPE, LoanCategoryType.class, myTypeButton);
        myPanel.addField(LoanCategory.FIELD_PARENT, LoanCategory.class, myParentButton);

        /* Define the panel */
        defineMainPanel(myPanel);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildCategoryTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final LoanCategory myItem = getItem();
        if (myItem != null) {
            final LoanCategoryList myCategories = getDataList(MoneyWiseDataType.LOANCATEGORY, LoanCategoryList.class);
            setItem(myCategories.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final MetisSwingFieldSet<LoanCategory> myFieldSet = getFieldSet();

        /* Determine whether parent/full-name fields are visible */
        final LoanCategory myCategory = getItem();
        final LoanCategoryType myType = myCategory.getCategoryType();
        final boolean isParent = myType.isLoanCategory(LoanCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        myFieldSet.setVisibility(LoanCategory.FIELD_DESC, bShowDesc);

        /* Set visibility */
        myFieldSet.setVisibility(LoanCategory.FIELD_PARENT, !isParent);
        myFieldSet.setVisibility(LoanCategory.FIELD_SUBCAT, !isParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();

        /* We cannot change a parent category type */
        canEdit &= !isParent;
        myFieldSet.setEditable(LoanCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        myFieldSet.setEditable(LoanCategory.FIELD_NAME, isEditable && isParent);
    }

    @Override
    protected void updateField(final MetisLetheFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisLetheField myField = pUpdate.getField();
        final LoanCategory myCategory = getItem();

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
        final LoanCategory myItem = getItem();
        final LoanCategory myParent = myItem.getParentCategory();
        if (!pUpdates) {
            final LoanCategoryType myType = myItem.getCategoryType();
            declareGoToItem(myType);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type menu for an item.
     * @param pMenu the menu
     * @param pCategory the category to build for
     */
    public void buildCategoryTypeMenu(final TethysScrollMenu<LoanCategoryType> pMenu,
                                      final LoanCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final LoanCategoryType myCurr = pCategory.getCategoryType();
        TethysScrollMenuItem<LoanCategoryType> myActive = null;

        /* Access Loan Category types */
        final LoanCategoryTypeList myCategoryTypes = getDataList(MoneyWiseDataType.LOANTYPE, LoanCategoryTypeList.class);

        /* Loop through the LoanCategoryTypes */
        final Iterator<LoanCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            final LoanCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if it is a parent */
            bIgnore |= myType.getLoanClass().isParentCategory();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            final TethysScrollMenuItem<LoanCategoryType> myItem = pMenu.addItem(myType);

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
    private static void buildParentMenu(final TethysScrollMenu<LoanCategory> pMenu,
                                        final LoanCategory pCategory) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final LoanCategory myCurr = pCategory.getParentCategory();
        TethysScrollMenuItem<LoanCategory> myActive = null;

        /* Loop through the LoanCategories */
        final LoanCategoryList myCategories = pCategory.getList();
        final Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final LoanCategory myCat = myIterator.next();

            /* Ignore deleted and non-parent items */
            final LoanCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCat.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Create a new action for the parent */
            final TethysScrollMenuItem<LoanCategory> myItem = pMenu.addItem(myCat);

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
