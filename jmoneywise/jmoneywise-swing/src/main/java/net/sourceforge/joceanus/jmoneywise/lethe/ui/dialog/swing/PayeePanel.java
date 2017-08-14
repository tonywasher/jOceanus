/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTextArea;

/**
 * Panel to display/edit/create a Payee.
 */
public class PayeePanel
        extends MoneyWiseItemPanel<Payee> {
    /**
     * The Field Set.
     */
    private final MetisFieldSet<Payee> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public PayeePanel(final TethysSwingGuiFactory pFactory,
                      final MetisFieldManager pFieldMgr,
                      final UpdateSet<MoneyWiseDataType> pUpdateSet,
                      final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        final JPanel myMainPanel = buildMainPanel(pFactory);

        /* Create a tabbedPane */
        final JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel(pFactory);
        myTabs.add(TAB_DETAILS, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel(pFactory);
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<PayeeType> myTypeButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* restrict the fields */
        restrictField(myName, Payee.NAMELEN);
        restrictField(myDesc, Payee.NAMELEN);
        restrictField(myTypeButton, Payee.NAMELEN);
        restrictField(myClosedButton, Payee.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Payee.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Payee.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Payee.FIELD_PAYEETYPE, PayeeType.class, myTypeButton);
        theFieldSet.addFieldElement(Payee.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Payee.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_PAYEETYPE, myPanel);
        theFieldSet.addFieldToPanel(Payee.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildPayeeTypeMenu(c, getItem()));
        final Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildXtrasPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        final TethysSwingStringTextField mySortCode = pFactory.newStringField();
        final TethysSwingStringTextField myAccount = pFactory.newStringField();
        final TethysSwingStringTextField myReference = pFactory.newStringField();
        final TethysSwingStringTextField myWebSite = pFactory.newStringField();
        final TethysSwingStringTextField myCustNo = pFactory.newStringField();
        final TethysSwingStringTextField myUserId = pFactory.newStringField();
        final TethysSwingStringTextField myPassWord = pFactory.newStringField();

        /* Restrict the fields */
        final int myWidth = Payee.NAMELEN >> 1;
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
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the extras panel */
        final SpringLayout mySpring = new SpringLayout();
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
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildNotesPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        final TethysSwingTextArea myNotes = pFactory.newTextArea();
        final TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Payee myItem = getItem();
        if (myItem != null) {
            final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            setItem(myPayees.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final Payee myPayee = getItem();
        final boolean bIsClosed = myPayee.isClosed();
        final boolean bIsActive = myPayee.isActive();
        final boolean bIsRelevant = myPayee.isRelevant();

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Payee.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setEditable(Payee.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myPayee.getDesc() != null;
        theFieldSet.setVisibility(Payee.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myPayee.getSortCode() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        final boolean bShowAccount = isEditable || myPayee.getAccount() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        final boolean bShowReference = isEditable || myPayee.getReference() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        final boolean bShowWebSite = isEditable || myPayee.getWebSite() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), bShowWebSite);
        final boolean bShowCustNo = isEditable || myPayee.getCustNo() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), bShowCustNo);
        final boolean bShowUserId = isEditable || myPayee.getUserId() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.USERID), bShowUserId);
        final boolean bShowPasswd = isEditable || myPayee.getPassword() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), bShowPasswd);
        final boolean bShowNotes = isEditable || myPayee.getNotes() != null;
        theFieldSet.setVisibility(PayeeInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Payee type cannot be changed if the item is singular, or if its relevant */
        final PayeeTypeClass myClass = myPayee.getPayeeTypeClass();
        final boolean bIsSingular = myClass.isSingular();
        theFieldSet.setEditable(Payee.FIELD_PAYEETYPE, isEditable && !bIsSingular && !bIsRelevant);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
        final Payee myPayee = getItem();

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
            final Payee myItem = getItem();
            final PayeeType myType = myItem.getPayeeType();
            declareGoToItem(myType);
        }
    }

    /**
     * Build the payeeType menu for an item.
     * @param pMenu the menu
     * @param pPayee the payee to build for
     */
    public void buildPayeeTypeMenu(final TethysScrollMenu<PayeeType, Icon> pMenu,
                                   final Payee pPayee) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final PayeeType myCurr = pPayee.getPayeeType();
        final PayeeList myList = pPayee.getList();
        TethysScrollMenuItem<PayeeType> myActive = null;

        /* Access PayeeTypes */
        final PayeeTypeList myTypes = getDataList(MoneyWiseDataType.PAYEETYPE, PayeeTypeList.class);

        /* Loop through the PayeeTypes */
        final Iterator<PayeeType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final PayeeType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* If the type is a likely candidate */
            if (!bIgnore) {
                /* Check for singular class */
                final PayeeTypeClass myClass = myType.getPayeeClass();
                if (myClass.isSingular()) {
                    /* Cannot change to this type if one already exists */
                    final Payee myExisting = myList.getSingularClass(myClass);
                    bIgnore = myExisting != null;
                }
            }

            /* Skip record if necessary */
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the payeeType */
            final TethysScrollMenuItem<PayeeType> myItem = pMenu.addItem(myType);

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
}
