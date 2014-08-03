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

import java.awt.Dimension;
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
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a DepositCategory.
 */
public class DepositCategoryPanel
        extends DataItemPanel<DepositCategory> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5714727248904523307L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<DepositCategory> theFieldSet;

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
    private final JScrollButton<DepositCategoryType> theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JScrollButton<DepositCategory> theParentButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public DepositCategoryPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField();
        theSubName = new JTextField();
        theDesc = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(DepositCategory.DESCLEN * CHAR_WIDTH, FIELD_HEIGHT);

        /* restrict the field */
        theName.setMaximumSize(myDims);
        theSubName.setMaximumSize(myDims);
        theDesc.setMaximumSize(myDims);

        /* Create the buttons */
        theTypeButton = new JScrollButton<DepositCategoryType>();
        theParentButton = new JScrollButton<DepositCategory>();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(DepositCategory.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(DepositCategory.FIELD_SUBCAT, DataType.STRING, theSubName);
        theFieldSet.addFieldElement(DepositCategory.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(DepositCategory.FIELD_CATTYPE, DepositCategoryType.class, theTypeButton);
        theFieldSet.addFieldElement(DepositCategory.FIELD_PARENT, DepositCategory.class, theParentButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_SUBCAT, this);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_CATTYPE, this);
        theFieldSet.addFieldToPanel(DepositCategory.FIELD_PARENT, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create the listener */
        new CategoryListener();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        DepositCategoryType myCurr = getItem().getCategoryType();
        boolean showParent = !myCurr.isDepositCategory(DepositCategoryClass.PARENT);

        /* Set visibility */
        theFieldSet.setVisibility(DepositCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(DepositCategory.FIELD_SUBCAT, showParent);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        DepositCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(DepositCategory.FIELD_NAME)) {
            /* Update the Name */
            myCategory.setCategoryName(pUpdate.getValue(String.class));
        } else if (myField.equals(DepositCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (myField.equals(DepositCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(DepositCategory.class));
        } else if (myField.equals(DepositCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(DepositCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(DepositCategoryType.class));
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
        private final JScrollMenuBuilder<DepositCategoryType> theTypeMenuBuilder;

        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<DepositCategory> theParentMenuBuilder;

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
            DepositCategory myCategory = getItem();
            DepositCategoryType myCurr = myCategory.getCategoryType();
            JMenuItem myActive = null;

            /* Access Deposit Category types */
            MoneyWiseData myData = myCategory.getDataSet();
            DepositCategoryTypeList myCategoryTypes = myData.getDepositCategoryTypes();

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
            DepositCategory myCategory = getItem();
            DepositCategoryType myCurr = myCategory.getCategoryType();
            JMenuItem myActive = null;

            /* Loop through the DepositCategories */
            DepositCategoryList myCategories = getItem().getList();
            Iterator<DepositCategory> myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                DepositCategory myCat = myIterator.next();

                /* Ignore deleted and non-parent items */
                DepositCategoryClass myClass = myCat.getCategoryTypeClass();
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
