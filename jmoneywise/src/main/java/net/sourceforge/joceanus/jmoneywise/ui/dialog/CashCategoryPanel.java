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
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a CashCategory.
 */
public class CashCategoryPanel
        extends DataItemPanel<CashCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -2519622794507877466L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<CashCategory> theFieldSet;

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
        theName = new JTextField();
        theSubName = new JTextField();
        theDesc = new JTextField();

        /* Create the buttons */
        theTypeButton = new JScrollButton<CashCategoryType>();
        theParentButton = new JScrollButton<CashCategory>();

        /* restrict the fields */
        restrictField(theName, CashCategory.NAMELEN);
        restrictField(theSubName, CashCategory.NAMELEN);
        restrictField(theDesc, CashCategory.NAMELEN);
        restrictField(theTypeButton, CashCategory.NAMELEN);
        restrictField(theParentButton, CashCategory.NAMELEN);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(CashCategory.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(CashCategory.FIELD_SUBCAT, DataType.STRING, theSubName);
        theFieldSet.addFieldElement(CashCategory.FIELD_DESC, DataType.STRING, theDesc);
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
            CashCategoryList myCategories = findDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);
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
        boolean showParent = !myType.isCashCategory(CashCategoryClass.PARENT);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCategory.getDesc() != null;
        theFieldSet.setVisibility(CashCategory.FIELD_DESC, bShowDesc);

        /* Set visibility */
        theFieldSet.setVisibility(CashCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(CashCategory.FIELD_SUBCAT, showParent);

        /* If the category is active then we cannot change the category type */
        boolean canEdit = isEditable && !myCategory.isActive();
        theFieldSet.setEditable(CashCategory.FIELD_CATTYPE, canEdit);

        /* If the category is not a parent then we cannot edit the full name */
        theFieldSet.setEditable(CashCategory.FIELD_NAME, isEditable && !showParent);
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
    protected void buildGoToMenu() {
        CashCategory myItem = getItem();
        CashCategoryType myType = myItem.getCategoryType();
        CashCategory myParent = myItem.getParentCategory();
        buildGoToEvent(myType);
        buildGoToEvent(myParent);
    }

    /**
     * Category Listener.
     */
    private final class CategoryListener
            implements ChangeListener {
        /**
         * The CategoryType Menu Builder.
         */
        private final JScrollMenuBuilder<CashCategoryType> theTypeMenuBuilder;

        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<CashCategory> theParentMenuBuilder;

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
            CashCategory myCategory = getItem();
            CashCategoryType myCurr = myCategory.getCategoryType();
            JMenuItem myActive = null;

            /* Access Cash Category types */
            MoneyWiseData myData = myCategory.getDataSet();
            CashCategoryTypeList myCategoryTypes = myData.getCashCategoryTypes();

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
            CashCategory myCategory = getItem();
            CashCategoryType myCurr = myCategory.getCategoryType();
            JMenuItem myActive = null;

            /* Loop through the CashCategories */
            CashCategoryList myCategories = getItem().getList();
            Iterator<CashCategory> myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                CashCategory myCat = myIterator.next();

                /* Ignore deleted and non-parent items */
                CashCategoryClass myClass = myCat.getCategoryTypeClass();
                if (myCat.isDeleted() || !myClass.isParentCategory()) {
                    continue;
                }

                /* Create a new action for the type */
                JMenuItem myItem = theParentMenuBuilder.addItem(myCat);

                /* If this is the active type */
                if (myCat.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theParentMenuBuilder.showItem(myActive);
        }
    }
}
