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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.field.JFieldComponent.JFieldButtonAction;
import net.sourceforge.joceanus.jmetis.field.JFieldComponent.JFieldButtonPopUp;
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
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a DepositCategory.
 */
public class DepositCategoryPanel
        extends DataItemPanel<DepositCategory>
        implements JFieldButtonPopUp {
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
    private final JButton theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JButton theParentButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public DepositCategoryPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(DepositCategory.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel mySubNameLabel = new JLabel(DepositCategory.FIELD_SUBCAT.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(DepositCategory.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel myTypeLabel = new JLabel(DepositCategory.FIELD_CATTYPE.getName() + ":", SwingConstants.TRAILING);
        JLabel myParLabel = new JLabel(DepositCategory.FIELD_PARENT.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(DepositCategory.NAMELEN);
        theSubName = new JTextField(DepositCategory.NAMELEN);
        theDesc = new JTextField(DepositCategory.DESCLEN);

        /* Create the buttons */
        theTypeButton = new JButton();
        theParentButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(DepositCategory.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(DepositCategory.FIELD_SUBCAT, DataType.STRING, mySubNameLabel, theSubName);
        theFieldSet.addFieldElement(DepositCategory.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(DepositCategory.FIELD_CATTYPE, this, DepositCategoryType.class, myTypeLabel, theTypeButton);
        theFieldSet.addFieldElement(DepositCategory.FIELD_PARENT, this, DepositCategory.class, myParLabel, theParentButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        add(myNameLabel);
        add(theName);
        add(mySubNameLabel);
        add(theSubName);
        add(myDescLabel);
        add(theDesc);
        add(myTypeLabel);
        add(theTypeButton);
        add(myParLabel);
        add(theParentButton);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Determine whether parent/full-name fields are visible */
        DepositCategoryType myCurr = getItem().getCategoryType();
        boolean showParent = !myCurr.isDepositCategory(DepositCategoryClass.PARENT);

        /* Set visibility */
        theFieldSet.setVisibility(DepositCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(DepositCategory.FIELD_NAME, showParent);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(DepositCategory.FIELD_PARENT)) {
            /* Build the parent menu */
            return getParentPopUpMenu(pActionSrc);
        } else if (pField.equals(DepositCategory.FIELD_CATTYPE)) {
            /* Build the category type menu */
            return getCategoryTypePopUpMenu(pActionSrc);
        }

        /* return no menu */
        return null;
    }

    /**
     * Build the parent menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getParentPopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Loop through the categories */
        DepositCategoryList myList = getItem().getList();
        Iterator<DepositCategory> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            DepositCategory myCategory = myIterator.next();

            /* Ignore deleted and non-parent items */
            DepositCategoryClass myClass = myCategory.getCategoryTypeClass();
            if (myCategory.isDeleted() || !myClass.isParentCategory()) {
                continue;
            }

            /* Add the menu item */
            Action myAction = pActionSrc.getNewAction(myCategory);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);
        }

        /* Return the menu */
        return myMenu;
    }

    /**
     * Build the category type menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getCategoryTypePopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the type of the category */
        DepositCategory myCategory = getItem();
        DepositCategoryType myCurr = myCategory.getCategoryType();
        boolean isParent = myCurr.isDepositCategory(DepositCategoryClass.PARENT);
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

            /* Ignore category if wrong type */
            bIgnore |= isParent != myType.isDepositCategory(DepositCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            Action myAction = pActionSrc.getNewAction(myType);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        myMenu.showItem(myActive);

        /* Return the menu */
        return myMenu;
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
}
