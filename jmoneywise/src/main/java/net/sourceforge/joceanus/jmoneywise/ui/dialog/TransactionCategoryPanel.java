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
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.ui.TransactionCategoryTable.CategoryType;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Dialog to display/edit/create a TransactionCategory.
 */
public class TransactionCategoryPanel
        extends DataItemPanel<TransactionCategory>
        implements JFieldButtonPopUp {
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
    private final JButton theTypeButton;

    /**
     * Parent Button Field.
     */
    private final JButton theParentButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public TransactionCategoryPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(TransactionCategory.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel mySubNameLabel = new JLabel(TransactionCategory.FIELD_SUBCAT.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(TransactionCategory.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel myTypeLabel = new JLabel(TransactionCategory.FIELD_CATTYPE.getName() + ":", SwingConstants.TRAILING);
        JLabel myParLabel = new JLabel(TransactionCategory.FIELD_PARENT.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(TransactionCategory.NAMELEN);
        theSubName = new JTextField(TransactionCategory.NAMELEN);
        theDesc = new JTextField(TransactionCategory.DESCLEN);

        /* Create the buttons */
        theTypeButton = new JButton();
        theParentButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(TransactionCategory.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_SUBCAT, DataType.STRING, mySubNameLabel, theSubName);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_CATTYPE, this, TransactionCategoryType.class, myTypeLabel, theTypeButton);
        theFieldSet.addFieldElement(TransactionCategory.FIELD_PARENT, this, TransactionCategory.class, myParLabel, theParentButton);

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
        TransactionCategoryType myCurr = getItem().getCategoryType();
        CategoryType myCurrType = CategoryType.determineType(myCurr);
        boolean showParent = myCurrType.equals(CategoryType.SUBTOTAL);

        /* Set visibility */
        theFieldSet.setVisibility(TransactionCategory.FIELD_PARENT, showParent);
        theFieldSet.setVisibility(TransactionCategory.FIELD_NAME, showParent);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(TransactionCategory.FIELD_PARENT)) {
            /* Build the parent menu */
            return getParentPopUpMenu(pActionSrc);
        } else if (pField.equals(TransactionCategory.FIELD_CATTYPE)) {
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

        /* Determine which parents we are looking at */
        TransactionCategory myCategory = getItem();
        Boolean isExpense = myCategory.getCategoryTypeClass().isExpense();

        /* Loop through the categories */
        TransactionCategoryList myList = myCategory.getList();
        Iterator<TransactionCategory> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCat = myIterator.next();

            /* Ignore deleted and non-subTotal items */
            TransactionCategoryClass myClass = myCat.getCategoryTypeClass();
            if (myCategory.isDeleted() || !myClass.isSubTotal()) {
                continue;
            }

            /* If we are interested */
            if (myClass.isExpense() == isExpense) {
                /* Add the menu item */
                Action myAction = pActionSrc.getNewAction(myCat);
                JMenuItem myItem = new JMenuItem(myAction);
                myMenu.addMenuItem(myItem);
            }
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
        TransactionCategory myCategory = getItem();
        TransactionCategoryType myCurr = myCategory.getCategoryType();
        CategoryType myCurrType = CategoryType.determineType(myCurr);
        JMenuItem myActive = null;

        /* Access Transaction Category types */
        MoneyWiseData myData = myCategory.getDataSet();
        TransactionCategoryTypeList myCategoryTypes = myData.getTransCategoryTypes();

        /* Loop through the TransactionCategoryTypes */
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
}
