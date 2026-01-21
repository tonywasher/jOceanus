/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.ui.dialog;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType.MoneyWisePayeeTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

import java.util.Iterator;
import java.util.Map;

/**
 * Panel to display/edit/create a Payee.
 */
public class MoneyWisePayeeDialog
        extends MoneyWiseAssetDialog<MoneyWisePayee> {
    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWisePayee> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner   the owning table
     */
    public MoneyWisePayeeDialog(final TethysUIFactory<?> pFactory,
                                final PrometheusEditSet pEditSet,
                                final MoneyWiseAssetTable<MoneyWisePayee> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.setReporter(pOwner::showValidateError);

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
     *
     * @param pFactory the GUI factory
     */
    private void buildMainPanel(final TethysUIFactory<?> pFactory) {
        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseAssetCategory> myTypeButton = myFields.newScrollField(MoneyWiseAssetCategory.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWisePayee::getName);
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWisePayee::getDesc);
        theFieldSet.addField(MoneyWiseBasicResource.CATEGORY_NAME, myTypeButton, MoneyWisePayee::getCategory);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_CLOSED, myClosedButton, MoneyWisePayee::isClosed);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildPayeeTypeMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Configure validation checks */
        myName.setValidator(this::isValidName);
        myDesc.setValidator(this::isValidDesc);
    }

    /**
     * Build account subPanel.
     *
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
        theFieldSet.addField(MoneyWiseAccountInfoClass.SORTCODE, mySortCode, MoneyWisePayee::getSortCode);
        theFieldSet.addField(MoneyWiseAccountInfoClass.ACCOUNT, myAccount, MoneyWisePayee::getAccount);
        theFieldSet.addField(MoneyWiseAccountInfoClass.REFERENCE, myReference, MoneyWisePayee::getReference);

        /* Configure validation checks */
        mySortCode.setValidator(this::isValidSortCode);
        myAccount.setValidator(this::isValidAccount);
        myReference.setValidator(this::isValidReference);
    }

    /**
     * Build web subPanel.
     *
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
        theFieldSet.addField(MoneyWiseAccountInfoClass.WEBSITE, myWebSite, MoneyWisePayee::getWebSite);
        theFieldSet.addField(MoneyWiseAccountInfoClass.CUSTOMERNO, myCustNo, MoneyWisePayee::getCustNo);
        theFieldSet.addField(MoneyWiseAccountInfoClass.USERID, myUserId, MoneyWisePayee::getUserId);
        theFieldSet.addField(MoneyWiseAccountInfoClass.PASSWORD, myPassWord, MoneyWisePayee::getPassword);

        /* Configure validation checks */
        myWebSite.setValidator(this::isValidWebSite);
        myCustNo.setValidator(this::isValidCustNo);
        myUserId.setValidator(this::isValidUserId);
        myPassWord.setValidator(this::isValidPassword);
    }

    /**
     * Build Notes subPanel.
     *
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysUIFactory<?> pFactory) {
        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayTextAreaField myNotes = myFields.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAccountInfoClass.NOTES, myNotes, MoneyWisePayee::getNotes);

        /* Configure validation checks */
        myNotes.setValidator(this::isValidNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWisePayee myItem = getItem();
        if (myItem != null) {
            final MoneyWisePayeeList myPayees = getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
            setItem(myPayees.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final MoneyWisePayee myPayee = getItem();
        final boolean bIsClosed = myPayee.isClosed();
        final boolean bIsActive = myPayee.isActive();
        final boolean bIsRelevant = myPayee.isRelevant();

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.ASSET_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myPayee.getDesc() != null;
        theFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myPayee.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.SORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myPayee.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.ACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myPayee.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.REFERENCE, bShowReference);
        final boolean bShowWebSite = isEditable || myPayee.getWebSite() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.WEBSITE, bShowWebSite);
        final boolean bShowCustNo = isEditable || myPayee.getCustNo() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.CUSTOMERNO, bShowCustNo);
        final boolean bShowUserId = isEditable || myPayee.getUserId() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.USERID, bShowUserId);
        final boolean bShowPasswd = isEditable || myPayee.getPassword() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.PASSWORD, bShowPasswd);
        final boolean bShowNotes = isEditable || myPayee.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.NOTES, bShowNotes);

        /* Payee type cannot be changed if the item is singular, or if its relevant */
        final MoneyWisePayeeClass myClass = myPayee.getCategoryClass();
        final boolean bIsSingular = myClass.isSingular();
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.CATEGORY_NAME, isEditable && !bIsSingular && !bIsRelevant);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWisePayee myPayee = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            myPayee.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myPayee.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_NAME.equals(myField)) {
            /* Update the Payee Type */
            myPayee.setCategory(pUpdate.getValue(MoneyWisePayeeType.class));
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myPayee.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAccountInfoClass.SORTCODE.equals(myField)) {
            /* Update the SortCode */
            myPayee.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.ACCOUNT.equals(myField)) {
            /* Update the Account */
            myPayee.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.REFERENCE.equals(myField)) {
            /* Update the Reference */
            myPayee.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.WEBSITE.equals(myField)) {
            /* Update the WebSite */
            myPayee.setWebSite(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.CUSTOMERNO.equals(myField)) {
            /* Update the Customer# */
            myPayee.setCustNo(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.USERID.equals(myField)) {
            /* Update the UserId */
            myPayee.setUserId(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.PASSWORD.equals(myField)) {
            /* Update the Password */
            myPayee.setPassword(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.NOTES.equals(myField)) {
            /* Update the Notes */
            myPayee.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        if (!pUpdates) {
            final MoneyWisePayee myItem = getItem();
            final MoneyWisePayeeType myType = myItem.getCategory();
            declareGoToItem(myType);
        }
    }

    /**
     * Build the payeeType menu for an item.
     *
     * @param pMenu  the menu
     * @param pPayee the payee to build for
     */
    public void buildPayeeTypeMenu(final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu,
                                   final MoneyWisePayee pPayee) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWisePayeeType myCurr = pPayee.getCategory();
        final MoneyWisePayeeList myList = pPayee.getList();
        TethysUIScrollItem<MoneyWiseAssetCategory> myActive = null;

        /* Access PayeeTypes */
        final MoneyWisePayeeTypeList myTypes = getDataList(MoneyWiseStaticDataType.PAYEETYPE, MoneyWisePayeeTypeList.class);

        /* Loop through the PayeeTypes */
        final Iterator<MoneyWisePayeeType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayeeType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();

            /* If the type is a likely candidate */
            if (!bIgnore) {
                /* Check for singular class */
                final MoneyWisePayeeClass myClass = myType.getPayeeClass();
                if (myClass.isSingular()) {
                    /* Cannot change to this type if one already exists */
                    final MoneyWisePayee myExisting = myList.getSingularClass(myClass);
                    bIgnore = myExisting != null;
                }
            }

            /* Skip record if necessary */
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the payeeType */
            final TethysUIScrollItem<MoneyWiseAssetCategory> myItem = pMenu.addItem(myType);

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
