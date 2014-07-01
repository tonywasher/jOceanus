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
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Payee.
 */
public class PayeePanel
        extends DataItemPanel<Payee>
        implements JFieldButtonPopUp {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -2683728681317279179L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Payee> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * Payee Type Button Field.
     */
    private final JButton theTypeButton;

    /**
     * Closed Button Field.
     */
    private final JButton theClosedButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public PayeePanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(Payee.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(Payee.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel myTypeLabel = new JLabel(Payee.FIELD_PAYEETYPE.getName() + ":", SwingConstants.TRAILING);
        JLabel myClosedLabel = new JLabel(Payee.FIELD_CLOSED.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(Payee.NAMELEN);
        theDesc = new JTextField(Payee.DESCLEN);

        /* Create the buttons */
        theTypeButton = new JButton();
        theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Payee.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(Payee.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(Payee.FIELD_PAYEETYPE, this, PayeeType.class, myTypeLabel, theTypeButton);
        theFieldSet.addFieldElement(Payee.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        add(myNameLabel);
        add(theName);
        add(myDescLabel);
        add(theDesc);
        add(myTypeLabel);
        add(theTypeButton);
        add(myClosedLabel);
        add(theClosedButton);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Payee.FIELD_CLOSED, false);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(Payee.FIELD_PAYEETYPE)) {
            /* Build the category type menu */
            return getPayeeTypePopUpMenu(pActionSrc);
        }

        /* return no menu */
        return null;
    }

    /**
     * Build the payee type menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getPayeeTypePopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the type of the payee */
        Payee myPayee = getItem();
        PayeeType myCurr = myPayee.getPayeeType();
        JMenuItem myActive = null;

        /* Access Payee types */
        MoneyWiseData myData = myPayee.getDataSet();
        PayeeTypeList myPayeeTypes = myData.getPayeeTypes();

        /* Loop through the PayeeTypes */
        Iterator<PayeeType> myIterator = myPayeeTypes.iterator();
        while (myIterator.hasNext()) {
            PayeeType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
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
        Payee myPayee = getItem();

        /* Process updates */
        if (myField.equals(Payee.FIELD_NAME)) {
            /* Update the Name */
            myPayee.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(Payee.FIELD_DESC)) {
            /* Update the Description */
            myPayee.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(Payee.FIELD_PAYEETYPE)) {
            /* Update the Payee Type */
            myPayee.setPayeeType(pUpdate.getValue(PayeeType.class));
        }
    }
}
