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

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.PayeeInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
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
    private final transient ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public PayeePanel(final JFieldManager pFieldMgr,
                      final UpdateSet<MoneyWiseDataType> pUpdateSet,
                      final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theName = new JTextField();
        theDesc = new JTextField();

        /* Create the buttons */
        theTypeButton = new JScrollButton<PayeeType>();

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel();
        myTabs.add("Details", myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel();
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new PayeeListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the closed button state */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildOptionButton(theClosedState);

        /* restrict the fields */
        restrictField(theName, Payee.NAMELEN);
        restrictField(theDesc, Payee.NAMELEN);
        restrictField(theTypeButton, Payee.NAMELEN);
        restrictField(myClosedButton, Payee.NAMELEN);

        theFieldSet.addFieldElement(Payee.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Payee.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Payee.FIELD_PAYEETYPE, PayeeType.class, theTypeButton);
        theFieldSet.addFieldElement(Payee.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Payee.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_PAYEETYPE, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_CLOSED, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @return the panel
     */
    private JPanel buildXtrasPanel() {
        /* Allocate fields */
        JTextField mySortCode = new JTextField();
        JTextField myAccount = new JTextField();
        JTextField myReference = new JTextField();
        JTextField myWebSite = new JTextField();
        JTextField myCustNo = new JTextField();
        JTextField myUserId = new JTextField();
        JTextField myPassWord = new JTextField();

        /* Restrict the fields */
        int myWidth = Deposit.NAMELEN >> 1;
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);
        restrictField(myWebSite, myWidth);
        restrictField(myCustNo, myWidth);
        restrictField(myUserId, myWidth);
        restrictField(myPassWord, myWidth);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), DataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), DataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), DataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), DataType.CHARARRAY, myWebSite);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), DataType.CHARARRAY, myCustNo);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.USERID), DataType.CHARARRAY, myUserId);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), DataType.CHARARRAY, myPassWord);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), myPanel);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), myPanel);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.USERID), myPanel);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Notes subPanel.
     * @return the panel
     */
    private JPanel buildNotesPanel() {
        /* Allocate fields */
        JTextArea myNotes = new JTextArea();
        JScrollPane myScroll = new JScrollPane(myNotes);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Payee myItem = getItem();
        if (myItem != null) {
            PayeeList myPayees = findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            setItem(myPayees.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Payee myPayee = getItem();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = myPayee.isClosed() || !myPayee.isRelevant();
        theFieldSet.setVisibility(Payee.FIELD_CLOSED, bShowClosed);
        theClosedState.setState(bShowClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myPayee.getDesc() != null;
        theFieldSet.setVisibility(Payee.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowSortCode = isEditable || myPayee.getSortCode() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        boolean bShowAccount = isEditable || myPayee.getAccount() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        boolean bShowReference = isEditable || myPayee.getReference() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        boolean bShowWebSite = isEditable || myPayee.getWebSite() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), bShowWebSite);
        boolean bShowCustNo = isEditable || myPayee.getCustNo() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), bShowCustNo);
        boolean bShowUserId = isEditable || myPayee.getUserId() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.USERID), bShowUserId);
        boolean bShowPasswd = isEditable || myPayee.getPassword() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), bShowPasswd);
        boolean bShowNotes = isEditable || myPayee.getNotes() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Payee type cannot be changed if the item is active */
        boolean bIsActive = myPayee.isActive();
        theFieldSet.setEditable(Payee.FIELD_PAYEETYPE, !bIsActive);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Payee myPayee = getItem();

        /* Process updates */
        if (myField.equals(Payee.FIELD_NAME)) {
            /* Update the Name */
            myPayee.setName(pUpdate.getString());
        } else if (myField.equals(Payee.FIELD_DESC)) {
            /* Update the Description */
            myPayee.setDescription(pUpdate.getString());
        } else if (myField.equals(Payee.FIELD_PAYEETYPE)) {
            /* Update the Payee Type */
            myPayee.setPayeeType(pUpdate.getValue(PayeeType.class));
        } else if (myField.equals(Payee.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myPayee.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (PayeeInfoSet.getClassForField(myField)) {
                case SORTCODE:
                    myPayee.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myPayee.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myPayee.setReference(pUpdate.getCharArray());
                    break;
                case WEBSITE:
                    myPayee.setWebSite(pUpdate.getCharArray());
                    break;
                case CUSTOMERNO:
                    myPayee.setCustNo(pUpdate.getCharArray());
                    break;
                case USERID:
                    myPayee.setUserId(pUpdate.getCharArray());
                    break;
                case PASSWORD:
                    myPayee.setPassword(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myPayee.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void buildGoToMenu() {
        Payee myItem = getItem();
        PayeeType myType = myItem.getPayeeType();
        buildGoToEvent(myType);
    }

    /**
     * Payee Listener.
     */
    private final class PayeeListener
            implements ChangeListener {
        /**
         * The PayeeType Menu Builder.
         */
        private final JScrollMenuBuilder<PayeeType> theTypeMenuBuilder;

        /**
         * Constructor.
         */
        private PayeeListener() {
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
