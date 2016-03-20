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
package net.sourceforge.joceanus.jmoneywise.ui.dialog.swing;

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.PayeeInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusSwingErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Panel to display/edit/create a Payee.
 */
public class PayeePanel
        extends MoneyWiseDataItemPanel<Payee> {
    /**
     * The Field Set.
     */
    private final transient MetisFieldSet<Payee> theFieldSet;

    /**
     * Payee Type Button Field.
     */
    private final JScrollButton<PayeeType> theTypeButton;

    /**
     * Closed Button Field.
     */
    private final ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * The PayeeType Menu Builder.
     */
    private final JScrollMenuBuilder<PayeeType> theTypeMenuBuilder;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public PayeePanel(final MetisFieldManager pFieldMgr,
                      final UpdateSet<MoneyWiseDataType> pUpdateSet,
                      final PrometheusSwingErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the buttons */
        theTypeButton = new JScrollButton<>();

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel();
        myTabs.add(TAB_DETAILS, myPanel);

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

        /* Create the listeners */
        theTypeMenuBuilder = theTypeButton.getMenuBuilder();
        theTypeMenuBuilder.getEventRegistrar().addEventListener(e -> buildPayeeTypeMenu(theTypeMenuBuilder, getItem()));
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the closed button state */
        JIconButton<Boolean> myClosedButton = new JIconButton<>(theClosedState);
        MoneyWiseIcons.buildLockedButton(theClosedState);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, Payee.NAMELEN);
        restrictField(myDesc, Payee.NAMELEN);
        restrictField(theTypeButton, Payee.NAMELEN);
        restrictField(myClosedButton, Payee.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Payee.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Payee.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Payee.FIELD_PAYEETYPE, PayeeType.class, theTypeButton);
        theFieldSet.addFieldElement(Payee.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Payee.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_PAYEETYPE, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        int myWidth = Payee.NAMELEN >> 1;
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);
        restrictField(myWebSite, myWidth);
        restrictField(myCustNo, myWidth);
        restrictField(myUserId, myWidth);
        restrictField(myPassWord, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), MetisDataType.CHARARRAY, myWebSite);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), MetisDataType.CHARARRAY, myCustNo);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.USERID), MetisDataType.CHARARRAY, myUserId);
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), MetisDataType.CHARARRAY, myPassWord);

        /* Create the extras panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

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
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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

        /* Build the FieldSet */
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Payee myItem = getItem();
        if (myItem != null) {
            PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            setItem(myPayees.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Payee myPayee = getItem();
        boolean bIsClosed = myPayee.isClosed();
        boolean bIsActive = myPayee.isActive();
        boolean bIsRelevant = myPayee.isRelevant();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Payee.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setEditable(Payee.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState.setState(bEditClosed);

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

        /* Payee type cannot be changed if the item is singular, or if its relevant */
        PayeeTypeClass myClass = myPayee.getPayeeTypeClass();
        boolean bIsSingular = myClass.isSingular();
        theFieldSet.setEditable(Payee.FIELD_PAYEETYPE, isEditable && !bIsSingular && !bIsRelevant);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
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
    protected void declareGoToItems(final boolean pUpdates) {
        if (!pUpdates) {
            Payee myItem = getItem();
            PayeeType myType = myItem.getPayeeType();
            declareGoToItem(myType);
        }
    }

    /**
     * Build the payeeType list for an item.
     * @param pMenuBuilder the menu builder
     * @param pPayee the payee to build for
     */
    public void buildPayeeTypeMenu(final JScrollMenuBuilder<PayeeType> pMenuBuilder,
                                   final Payee pPayee) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        PayeeType myCurr = pPayee.getPayeeType();
        PayeeList myList = pPayee.getList();
        JMenuItem myActive = null;

        /* Access PayeeTypes */
        PayeeTypeList myTypes = getDataList(MoneyWiseDataType.PAYEETYPE, PayeeTypeList.class);

        /* Loop through the PayeeTypes */
        Iterator<PayeeType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            PayeeType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* If the type is a likely candidate */
            if (!bIgnore) {
                /* Check for singular class */
                PayeeTypeClass myClass = myType.getPayeeClass();
                if (myClass.isSingular()) {
                    /* Cannot change to this type if one already exists */
                    Payee myExisting = myList.getSingularClass(myClass);
                    bIgnore = myExisting != null;
                }
            }

            /* Skip record if necessary */
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the payeeType */
            JMenuItem myItem = pMenuBuilder.addItem(myType);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }
}
