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
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a LoanCategory.
 */
public class LoanCategoryPanel
        extends DataItemPanel<LoanCategory>
        implements JFieldButtonPopUp {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6589528251463766615L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<LoanCategory> theFieldSet;

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
    public LoanCategoryPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(LoanCategory.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel mySubNameLabel = new JLabel(LoanCategory.FIELD_SUBCAT.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(LoanCategory.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel myTypeLabel = new JLabel(LoanCategory.FIELD_CATTYPE.getName() + ":", SwingConstants.TRAILING);
        JLabel myParLabel = new JLabel(LoanCategory.FIELD_PARENT.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(LoanCategory.NAMELEN);
        theSubName = new JTextField(LoanCategory.NAMELEN);
        theDesc = new JTextField(LoanCategory.DESCLEN);

        /* Create the buttons */
        theTypeButton = new JButton();
        theParentButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(LoanCategory.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(LoanCategory.FIELD_SUBCAT, DataType.STRING, mySubNameLabel, theSubName);
        theFieldSet.addFieldElement(LoanCategory.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(LoanCategory.FIELD_CATTYPE, this, LoanCategoryType.class, myTypeLabel, theTypeButton);
        theFieldSet.addFieldElement(LoanCategory.FIELD_PARENT, this, LoanCategory.class, myParLabel, theParentButton);

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
        LoanCategoryType myCurr = getItem().getCategoryType();
        boolean showParent = !myCurr.isLoanCategory(LoanCategoryClass.PARENT);

        /* Set visibility */
        theFieldSet.setVisibility(LoanCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(LoanCategory.FIELD_NAME, showParent);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(LoanCategory.FIELD_PARENT)) {
            /* Build the parent menu */
            return getParentPopUpMenu(pActionSrc);
        } else if (pField.equals(LoanCategory.FIELD_CATTYPE)) {
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
        LoanCategoryList myList = getItem().getList();
        Iterator<LoanCategory> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            LoanCategory myCategory = myIterator.next();

            /* Ignore deleted and non-parent items */
            LoanCategoryClass myClass = myCategory.getCategoryTypeClass();
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
        LoanCategory myCategory = getItem();
        LoanCategoryType myCurr = myCategory.getCategoryType();
        boolean isParent = myCurr.isLoanCategory(LoanCategoryClass.PARENT);
        JMenuItem myActive = null;

        /* Access Loan Category types */
        MoneyWiseData myData = myCategory.getDataSet();
        LoanCategoryTypeList myCategoryTypes = myData.getLoanCategoryTypes();

        /* Loop through the LoanCategoryTypes */
        Iterator<LoanCategoryType> myIterator = myCategoryTypes.iterator();
        while (myIterator.hasNext()) {
            LoanCategoryType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* Ignore category if wrong type */
            bIgnore |= isParent != myType.isLoanCategory(LoanCategoryClass.PARENT);
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
        LoanCategory myCategory = getItem();

        /* Process updates */
        if (myField.equals(LoanCategory.FIELD_NAME)) {
            /* Update the Name */
            myCategory.setCategoryName(pUpdate.getValue(String.class));
        } else if (myField.equals(LoanCategory.FIELD_SUBCAT)) {
            /* Update the SubCategory */
            myCategory.setSubCategoryName(pUpdate.getValue(String.class));
        } else if (myField.equals(LoanCategory.FIELD_PARENT)) {
            /* Update the Parent */
            myCategory.setParentCategory(pUpdate.getValue(LoanCategory.class));
        } else if (myField.equals(LoanCategory.FIELD_DESC)) {
            /* Update the Description */
            myCategory.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(LoanCategory.FIELD_CATTYPE)) {
            /* Update the Category Type */
            myCategory.setCategoryType(pUpdate.getValue(LoanCategoryType.class));
        }
    }
}
