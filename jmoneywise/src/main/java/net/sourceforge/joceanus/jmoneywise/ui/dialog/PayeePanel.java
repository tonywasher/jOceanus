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
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Payee.
 */
public class PayeePanel
        extends DataItemPanel<Payee> {
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
    private final JScrollButton<PayeeType> theTypeButton;

    /**
     * Closed Button Field.
     */
    // private final JButton theClosedButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public PayeePanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField(Payee.NAMELEN);
        theDesc = new JTextField(Payee.DESCLEN);

        /* Create the buttons */
        theTypeButton = new JScrollButton<PayeeType>();
        // theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Payee.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Payee.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Payee.FIELD_PAYEETYPE, PayeeType.class, theTypeButton);
        // theFieldSet.addFieldElement(Payee.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(Payee.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(Payee.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(Payee.FIELD_PAYEETYPE, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create the listener */
        new AccountListener();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Payee.FIELD_CLOSED, false);
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

    /**
     * Account Listener.
     */
    private final class AccountListener
            implements ChangeListener {
        /**
         * The PayeeType Menu Builder.
         */
        private final JScrollMenuBuilder<PayeeType> theTypeMenuBuilder;

        /**
         * Constructor.
         */
        private AccountListener() {
            /* Access the MenuBuilders */
            theTypeMenuBuilder = theTypeButton.getMenuBuilder();
            theTypeMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theTypeMenuBuilder.equals(o)) {
                buildPayeeTypeMenu();
            }
        }

        /**
         * Build the payeeType list for the item.
         */
        private void buildPayeeTypeMenu() {
            /* Clear the menu */
            theTypeMenuBuilder.clearMenu();

            /* Record active item */
            Payee myPayee = getItem();
            PayeeType myCurr = myPayee.getPayeeType();
            JMenuItem myActive = null;

            /* Access PayeeTypes */
            MoneyWiseData myData = myPayee.getDataSet();
            PayeeTypeList myTypes = myData.getPayeeTypes();

            /* Loop through the AccountCurrencies */
            Iterator<PayeeType> myIterator = myTypes.iterator();
            while (myIterator.hasNext()) {
                PayeeType myType = myIterator.next();

                /* Ignore deleted or disabled */
                boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
                if (bIgnore) {
                    continue;
                }

                /* Create a new action for the payeeType */
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
    }
}
