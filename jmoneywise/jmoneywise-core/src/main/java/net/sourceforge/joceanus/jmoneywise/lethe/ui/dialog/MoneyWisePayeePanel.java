/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseXIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseXItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSet;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Panel to display/edit/create a Payee.
 */
public class MoneyWisePayeePanel
        extends MoneyWiseXItemPanel<Payee> {
    /**
     * The fieldSet.
     */
    private final PrometheusXFieldSet<Payee> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public MoneyWisePayeePanel(final TethysUIFactory<?> pFactory,
                               final UpdateSet pUpdateSet,
                               final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the account panel */
        buildAccountPanel(pFactory);

        /* Build the web panel */
        buildWebPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     */
    private void buildMainPanel(final TethysUIFactory<?> pFactory) {
        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<AssetCategory> myTypeButton = myFields.newScrollField(AssetCategory.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.NAME, myName, Payee::getName);
        theFieldSet.addField(MoneyWiseAssetDataId.DESC, myDesc, Payee::getDesc);
        theFieldSet.addField(MoneyWiseAssetDataId.CATEGORY, myTypeButton, Payee::getCategory);
        theFieldSet.addField(MoneyWiseAssetDataId.CLOSED, myClosedButton, Payee::isClosed);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildPayeeTypeMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseXIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));
    }

    /**
     * Build account subPanel.
     * @param pFactory the GUI factory
     */
    private void buildAccountPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_ACCOUNT);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayEditField mySortCode = myFields.newCharArrayField();
        final TethysUICharArrayEditField myAccount = myFields.newCharArrayField();
        final TethysUICharArrayEditField myReference = myFields.newCharArrayField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEESORTCODE, mySortCode, Payee::getSortCode);
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEEACCOUNT, myAccount, Payee::getAccount);
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEEREFERENCE, myReference, Payee::getReference);
    }

    /**
     * Build web subPanel.
     * @param pFactory the GUI factory
     */
    private void buildWebPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_WEB);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayEditField myWebSite = myFields.newCharArrayField();
        final TethysUICharArrayEditField myCustNo = myFields.newCharArrayField();
        final TethysUICharArrayEditField myUserId = myFields.newCharArrayField();
        final TethysUICharArrayEditField myPassWord = myFields.newCharArrayField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEEWEBSITE, myWebSite, Payee::getWebSite);
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEECUSTNO, myCustNo, Payee::getCustNo);
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEEUSERID, myUserId, Payee::getUserId);
        theFieldSet.addField(MoneyWiseAssetDataId.PAYEEPASSWORD, myPassWord, Payee::getPassword);
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysUIFactory<?> pFactory) {
        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayTextAreaField myNotes = myFields.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAssetDataId.PAYEENOTES, myNotes, Payee::getNotes);
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
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myPayee.getDesc() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myPayee.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEESORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myPayee.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEEACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myPayee.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEEREFERENCE, bShowReference);
        final boolean bShowWebSite = isEditable || myPayee.getWebSite() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEEWEBSITE, bShowWebSite);
        final boolean bShowCustNo = isEditable || myPayee.getCustNo() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEECUSTNO, bShowCustNo);
        final boolean bShowUserId = isEditable || myPayee.getUserId() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEEUSERID, bShowUserId);
        final boolean bShowPasswd = isEditable || myPayee.getPassword() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEEPASSWORD, bShowPasswd);
        final boolean bShowNotes = isEditable || myPayee.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PAYEENOTES, bShowNotes);

        /* Payee type cannot be changed if the item is singular, or if its relevant */
        final PayeeTypeClass myClass = myPayee.getCategoryClass();
        final boolean bIsSingular = myClass.isSingular();
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CATEGORY, isEditable && !bIsSingular && !bIsRelevant);
    }

    @Override
    protected void updateField(final PrometheusXFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Payee myPayee = getItem();

        /* Process updates */
        if (MoneyWiseAssetDataId.NAME.equals(myField)) {
            /* Update the Name */
            myPayee.setName(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.DESC.equals(myField)) {
            /* Update the Description */
            myPayee.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.CATEGORY.equals(myField)) {
            /* Update the Payee Type */
            myPayee.setCategory(pUpdate.getValue(PayeeType.class));
        } else if (MoneyWiseAssetDataId.CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myPayee.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAssetDataId.PAYEESORTCODE.equals(myField)) {
            /* Update the SortCode */
            myPayee.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEEACCOUNT.equals(myField)) {
            /* Update the Account */
            myPayee.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEEREFERENCE.equals(myField)) {
            /* Update the Reference */
            myPayee.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEEWEBSITE.equals(myField)) {
            /* Update the WebSite */
            myPayee.setWebSite(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEECUSTNO.equals(myField)) {
            /* Update the Customer# */
            myPayee.setCustNo(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEEUSERID.equals(myField)) {
            /* Update the UserId */
            myPayee.setUserId(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEEPASSWORD.equals(myField)) {
            /* Update the Password */
            myPayee.setPassword(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PAYEENOTES.equals(myField)) {
            /* Update the Notes */
            myPayee.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        if (!pUpdates) {
            final Payee myItem = getItem();
            final PayeeType myType = myItem.getCategory();
            declareGoToItem(myType);
        }
    }

    /**
     * Build the payeeType menu for an item.
     * @param pMenu the menu
     * @param pPayee the payee to build for
     */
    public void buildPayeeTypeMenu(final TethysUIScrollMenu<AssetCategory> pMenu,
                                   final Payee pPayee) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final PayeeType myCurr = pPayee.getCategory();
        final PayeeList myList = pPayee.getList();
        TethysUIScrollItem<AssetCategory> myActive = null;

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
            final TethysUIScrollItem<AssetCategory> myItem = pMenu.addItem(myType);

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
