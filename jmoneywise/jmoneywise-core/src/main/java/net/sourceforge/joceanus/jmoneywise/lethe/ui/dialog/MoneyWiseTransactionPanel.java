/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseTransDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase.AssetBaseList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionValidator;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.TransactionFilters;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDilutionEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIntegerEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIPriceEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRatioEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIUnitsEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;

/**
 * Panel to display/edit/create a Transaction.
 */
public class MoneyWiseTransactionPanel
        extends MoneyWiseItemPanel<Transaction> {
    /**
     * Info Tab Title.
     */
    private static final String TAB_INFO = MoneyWiseUIResource.TRANSPANEL_TAB_INFO.getValue();

    /**
     * Tax Tab Title.
     */
    private static final String TAB_TAXES = MoneyWiseUIResource.TRANSPANEL_TAB_TAXES.getValue();

    /**
     * Securities Tab Title.
     */
    private static final String TAB_SECURITIES = MoneyWiseUIResource.TRANSPANEL_TAB_SECURITIES.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<Transaction> theFieldSet;

    /**
     * Analysis selection panel.
     */
    private final MoneyWiseAnalysisSelect theAnalysisSelect;

    /**
     * TransactionBuilder.
     */
    private final TransactionBuilder theBuilder;

    /**
     * dateRange.
     */
    private TethysDateRange theRange;

    /**
     * reconciledState.
     */
    private Boolean theReconciledState = Boolean.FALSE;

    /**
     * directionState.
     */
    private Boolean theDirectionState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pBuilder the transaction builder
     * @param pAnalysisSelect the analysis selection panel
     * @param pError the error panel
     */
    public MoneyWiseTransactionPanel(final TethysUIFactory<?> pFactory,
                                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                     final TransactionBuilder pBuilder,
                                     final MoneyWiseAnalysisSelect pAnalysisSelect,
                                     final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);
        theAnalysisSelect = pAnalysisSelect;
        theBuilder = pBuilder;

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the info panel */
        buildInfoPanel(pFactory);

        /* Build the tax panel */
        buildTaxPanel(pFactory);

        /* Build the securities panel */
        buildSecuritiesPanel(pFactory);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     */
    private void buildMainPanel(final TethysUIFactory<?> pFactory) {
        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIMoneyEditField myAmount = myFields.newMoneyField();

        /* Create the buttons */
        final TethysUIDateButtonField myDateButton = myFields.newDateField();
        final TethysUIScrollButtonField<TransactionAsset> myAccountButton = myFields.newScrollField(TransactionAsset.class);
        final TethysUIScrollButtonField<TransactionAsset> myPartnerButton = myFields.newScrollField(TransactionAsset.class);
        final TethysUIScrollButtonField<TransactionCategory> myCategoryButton = myFields.newScrollField(TransactionCategory.class);
        final TethysUIIconButtonField<Boolean> myReconciledButton = myFields.newIconField(Boolean.class);
        final TethysUIIconButtonField<AssetDirection> myDirectionButton = myFields.newIconField(AssetDirection.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransDataId.DATE, myDateButton, Transaction::getDate);
        theFieldSet.addField(MoneyWiseTransDataId.ACCOUNT, myAccountButton, Transaction::getAccount);
        theFieldSet.addField(MoneyWiseTransDataId.CATEGORY, myCategoryButton, Transaction::getCategory);
        theFieldSet.addField(MoneyWiseTransDataId.DIRECTION, myDirectionButton, Transaction::getDirection);
        theFieldSet.addField(MoneyWiseTransDataId.PARTNER, myPartnerButton, Transaction::getPartner);
        theFieldSet.addField(MoneyWiseTransDataId.AMOUNT, myAmount, Transaction::getAmount);
        theFieldSet.addField(MoneyWiseTransDataId.RECONCILED, myReconciledButton, Transaction::isReconciled);

        /* Configure the menuBuilders */
        myDateButton.setDateConfigurator(this::handleDateConfig);
        myAccountButton.setMenuConfigurator(c -> buildAccountMenu(c, getItem()));
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myPartnerButton.setMenuConfigurator(c -> buildPartnerMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton(pFactory);
        myReconciledButton.setIconMapSet(() -> myRecMapSets.get(theReconciledState));
        final Map<Boolean, TethysUIIconMapSet<AssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton(pFactory);
        myDirectionButton.setIconMapSet(() -> myDirMapSets.get(theDirectionState));
        myAmount.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
    }

    /**
     * Build info subPanel.
     * @param pFactory the GUI factory
     */
    private void buildInfoPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_INFO);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIMoneyEditField myAmount = myFields.newMoneyField();
        final TethysUIStringEditField myComments = myFields.newStringField();
        final TethysUIStringEditField myReference = myFields.newStringField();
        final TethysUIRatioEditField myRate = myFields.newRatioField();

        /* Create the buttons */
        final TethysUIListButtonField<TransactionTag> myTagButton = myFields.newListField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransDataId.PARTNERAMOUNT, myAmount, Transaction::getPartnerAmount);
        theFieldSet.addField(MoneyWiseTransDataId.COMMENTS, myComments, Transaction::getComments);
        theFieldSet.addField(MoneyWiseTransDataId.REFERENCE, myReference, Transaction::getReference);
        theFieldSet.addField(MoneyWiseTransDataId.TAG, myTagButton, Transaction::getTransactionTags);
        theFieldSet.addField(MoneyWiseTransDataId.XCHANGERATE, myRate, Transaction::getExchangeRate);

        /* Configure the tag button */
        myTagButton.setSelectables(this::buildTransactionTags);

        /* Set currency */
        myAmount.setDeemedCurrency(() -> getItem().getPartner().getCurrency());
    }

    /**
     * Build tax subPanel.
     * @param pFactory the GUI factory
     */
    private void buildTaxPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_TAXES);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIMoneyEditField myTaxCredit = myFields.newMoneyField();
        final TethysUIMoneyEditField myEeNatIns = myFields.newMoneyField();
        final TethysUIMoneyEditField myErNatIns = myFields.newMoneyField();
        final TethysUIMoneyEditField myBenefit = myFields.newMoneyField();
        final TethysUIMoneyEditField myWithheld = myFields.newMoneyField();
        final TethysUIIntegerEditField myYears = myFields.newIntegerField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransDataId.TAXCREDIT, myTaxCredit, Transaction::getTaxCredit);
        theFieldSet.addField(MoneyWiseTransDataId.EMPLOYEENATINS, myEeNatIns, Transaction::getEmployeeNatIns);
        theFieldSet.addField(MoneyWiseTransDataId.EMPLOYERNATINS, myErNatIns, Transaction::getEmployerNatIns);
        theFieldSet.addField(MoneyWiseTransDataId.DEEMEDBENEFIT, myBenefit, Transaction::getDeemedBenefit);
        theFieldSet.addField(MoneyWiseTransDataId.WITHHELD, myWithheld, Transaction::getWithheld);
        theFieldSet.addField(MoneyWiseTransDataId.QUALIFYYEARS, myYears, Transaction::getYears);

        /* Set currency */
        myTaxCredit.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myEeNatIns.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myErNatIns.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myBenefit.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myWithheld.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
    }

    /**
     * Build securities subPanel.
     * @param pFactory the GUI factory
     */
    private void buildSecuritiesPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_SECURITIES);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIUnitsEditField myAccountUnits = myFields.newUnitsField();
        final TethysUIUnitsEditField myPartnerUnits = myFields.newUnitsField();
        final TethysUIMoneyEditField myCommission = myFields.newMoneyField();
        final TethysUIPriceEditField myPrice = myFields.newPriceField();
        final TethysUIDilutionEditField myDilution = myFields.newDilutionField();
        final TethysUIMoneyEditField myReturnedCash = myFields.newMoneyField();

        /* Create the buttons */
        final TethysUIScrollButtonField<TransactionAsset> myReturnedAccountButton = myFields.newScrollField(TransactionAsset.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransDataId.ACCOUNTDELTAUNITS, myAccountUnits, Transaction::getAccountDeltaUnits);
        theFieldSet.addField(MoneyWiseTransDataId.PARTNERDELTAUNITS, myPartnerUnits, Transaction::getPartnerDeltaUnits);
        theFieldSet.addField(MoneyWiseTransDataId.PRICE, myPrice, Transaction::getPrice);
        theFieldSet.addField(MoneyWiseTransDataId.COMMISSION, myCommission, Transaction::getCommission);
        theFieldSet.addField(MoneyWiseTransDataId.DILUTION, myDilution, Transaction::getDilution);
        theFieldSet.addField(MoneyWiseTransDataId.RETURNEDCASHACCOUNT, myReturnedAccountButton, Transaction::getReturnedCashAccount);
        theFieldSet.addField(MoneyWiseTransDataId.RETURNEDCASH, myReturnedCash, Transaction::getReturnedCash);

        /* Configure the menuBuilders */
        myReturnedAccountButton.setMenuConfigurator(c -> buildReturnedAccountMenu(c, getItem()));

        /* Set currency */
        myCommission.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myPrice.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myReturnedCash.setDeemedCurrency(() -> getItem().getReturnedCashAccount().getCurrency());
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Transaction myItem = getItem();
        if (myItem != null) {
            final TransactionList myTrans = getDataList(MoneyWiseDataType.TRANSACTION, TransactionList.class);
            setItem(myTrans.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    /**
     * Update editors.
     * @param pRange the date range.
     */
    public void updateEditors(final TethysDateRange pRange) {
        /* Update the range */
        theRange = pRange;
    }

    /**
     * Handle dateConfig.
     * @param pConfig the dateConfig
     */
    private void handleDateConfig(final TethysDateConfig pConfig) {
        /* Update Date button */
        pConfig.setEarliestDate(theRange != null
                ? theRange.getStart()
                : null);
        pConfig.setLatestDate(theRange != null
                ? theRange.getEnd()
                : null);
    }

    @Override
    public boolean isDeletable() {
        return getItem() != null && !getItem().isReconciled();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final Transaction myTrans = getItem();
        final boolean bIsReconciled = myTrans.isReconciled();
        final boolean bIsLocked = myTrans.isLocked();

        /* Determine whether the comments field should be visible */
        boolean bShowField = isEditable || myTrans.getComments() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.COMMENTS, bShowField);

        /* Determine whether the reference field should be visible */
        bShowField = isEditable || myTrans.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.REFERENCE, bShowField);

        /* Determine whether the tags field should be visible */
        bShowField = isEditable || myTrans.getTransactionTags() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.TAG, bShowField);

        /* Determine whether the partnerAmount field should be visible */
        boolean bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERAMOUNT);
        bShowField = bEditField || myTrans.getPartnerAmount() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.PARTNERAMOUNT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.PARTNERAMOUNT, bEditField);

        /* Determine whether the exchangeRate field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.XCHANGERATE);
        bShowField = bEditField || myTrans.getExchangeRate() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.XCHANGERATE, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.XCHANGERATE, bEditField);

        /* Determine whether the taxCredit field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.TAXCREDIT);
        bShowField = bEditField || myTrans.getTaxCredit() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.TAXCREDIT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.TAXCREDIT, bEditField);

        /* Determine whether the EeNatIns field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.EMPLOYEENATINS);
        bShowField = bEditField || myTrans.getEmployeeNatIns() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.EMPLOYEENATINS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.EMPLOYEENATINS, bEditField);

        /* Determine whether the ErnatIns field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.EMPLOYERNATINS);
        bShowField = bEditField || myTrans.getEmployerNatIns() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.EMPLOYERNATINS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.EMPLOYERNATINS, bEditField);

        /* Determine whether the benefit field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DEEMEDBENEFIT);
        bShowField = bEditField || myTrans.getDeemedBenefit() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.DEEMEDBENEFIT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.DEEMEDBENEFIT, bEditField);

        /* Determine whether the donation field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.WITHHELD);
        bShowField = bEditField || myTrans.getWithheld() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.WITHHELD, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.WITHHELD, bEditField);

        /* Determine whether the account units field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.ACCOUNTDELTAUNITS);
        bShowField = bEditField || myTrans.getAccountDeltaUnits() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.ACCOUNTDELTAUNITS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.ACCOUNTDELTAUNITS, bEditField);

        /* Determine whether the partnerDeltaUnits field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERDELTAUNITS);
        bShowField = bEditField || myTrans.getPartnerDeltaUnits() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.PARTNERDELTAUNITS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.PARTNERDELTAUNITS, bEditField);

        /* Determine whether the price field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PRICE);
        bShowField = bEditField || myTrans.getPrice() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.PRICE, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.PRICE, bEditField);

        /* Determine whether the commission field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.COMMISSION);
        bShowField = bEditField || myTrans.getCommission() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.COMMISSION, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.COMMISSION, bEditField);

        /* Determine whether the dilution field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DILUTION);
        bShowField = bEditField || myTrans.getDilution() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.DILUTION, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.DILUTION, bEditField);

        /* Determine whether the returnedAccount field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.RETURNEDCASHACCOUNT);
        bShowField = bEditField || myTrans.getReturnedCashAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.RETURNEDCASHACCOUNT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.RETURNEDCASHACCOUNT, bEditField);

        /* Determine whether the returnedCash field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.RETURNEDCASH);
        bShowField = bEditField || myTrans.getReturnedCash() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.RETURNEDCASH, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.RETURNEDCASH, bEditField);

        /* Determine whether the years field should be visible */
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.QUALIFYYEARS);
        bShowField = bEditField || myTrans.getYears() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.QUALIFYYEARS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.QUALIFYYEARS, bEditField);

        /* Determine whether the reconciled field should be visible */
        final boolean bShowReconciled = isEditable || bIsReconciled;
        theReconciledState = bIsLocked;
        theDirectionState = bIsReconciled;
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.RECONCILED, bShowReconciled);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.RECONCILED, isEditable && !bIsLocked);

        /* Determine basic editing */
        final boolean canEdit = isEditable && !bIsReconciled;
        final boolean needsNullAmount = myTrans.needsNullAmount();
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.DIRECTION, canEdit && myTrans.canSwitchDirection());
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.ACCOUNT, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.PARTNER, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.CATEGORY, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.DATE, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseTransDataId.AMOUNT, canEdit && !needsNullAmount);
        theFieldSet.setFieldVisible(MoneyWiseTransDataId.AMOUNT, !needsNullAmount);

        /* Set the range for the dateButton */
        theRange = theBuilder.getRange();
    }

    /**
     * Is the field editable?
     * @param pTrans the transaction
     * @param pField the field class
     * @return true/false
     */
    public static boolean isEditableField(final Transaction pTrans,
                                          final TransactionInfoClass pField) {
        /* Access the infoSet */
        final TransactionInfoSet myInfoSet = pTrans.getInfoSet();

        /* If the transaction is reconciled */
        if (Boolean.TRUE.equals(pTrans.isReconciled())) {
            /* Only allow editing of metaData */
            return myInfoSet.isMetaData(pField);
        }

        /* Check whether the field is available */
        final MetisFieldRequired isRequired = myInfoSet.isClassRequired(pField);
        return !isRequired.equals(MetisFieldRequired.NOTALLOWED);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Transaction myTrans = getItem();

        /* Process updates */
        if (MoneyWiseTransDataId.DATE.equals(myField)) {
            /* Update the Date */
            myTrans.setDate(pUpdate.getValue(TethysDate.class));
        } else if (MoneyWiseTransDataId.AMOUNT.equals(myField)) {
            /* Update the Amount */
            myTrans.setAmount(pUpdate.getValue(TethysMoney.class));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransDataId.ACCOUNT.equals(myField)) {
            /* Update the Account */
            myTrans.setAccount(resolveAsset(pUpdate.getValue(TransactionAsset.class)));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransDataId.DIRECTION.equals(myField)) {
            /* Update the Direction */
            myTrans.switchDirection();
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransDataId.PARTNER.equals(myField)) {
            /* Update the Partner */
            myTrans.setPartner(resolveAsset(pUpdate.getValue(TransactionAsset.class)));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransDataId.CATEGORY.equals(myField)) {
            /* Update the Category */
            myTrans.setCategory(pUpdate.getValue(TransactionCategory.class));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransDataId.RECONCILED.equals(myField)) {
            /* Update the Reconciled indication */
            myTrans.setReconciled(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseTransDataId.COMMENTS.equals(myField)) {
            /* Update the Comments */
            myTrans.setComments(pUpdate.getValue(String.class));
        } else if (MoneyWiseTransDataId.REFERENCE.equals(myField)) {
            /* Update the Reference */
            myTrans.setReference(pUpdate.getValue(String.class));
        } else if (MoneyWiseTransDataId.TAG.equals(myField)) {
            /* Update the Tag indication */
            myTrans.setTransactionTags(pUpdate.getValue(List.class));
        } else if (MoneyWiseTransDataId.PARTNERAMOUNT.equals(myField)) {
            /* Update the PartnerAmount */
            myTrans.setPartnerAmount(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.XCHANGERATE.equals(myField)) {
            /* Update the ExchangeRate */
            myTrans.setExchangeRate(pUpdate.getValue(TethysRatio.class));
        } else if (MoneyWiseTransDataId.ACCOUNTDELTAUNITS.equals(myField)) {
            /* Update the AccountDeltaUnits */
            myTrans.setAccountDeltaUnits(pUpdate.getValue(TethysUnits.class));
        } else if (MoneyWiseTransDataId.PARTNERDELTAUNITS.equals(myField)) {
            /* Update the PartnerDeltaUnits */
            myTrans.setPartnerDeltaUnits(pUpdate.getValue(TethysUnits.class));
        } else if (MoneyWiseTransDataId.PRICE.equals(myField)) {
            /* Update the Price */
            myTrans.setPrice(pUpdate.getValue(TethysPrice.class));
        } else if (MoneyWiseTransDataId.COMMISSION.equals(myField)) {
            /* Update the Commission */
            myTrans.setCommission(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.DILUTION.equals(myField)) {
            /* Update the Dilution */
            myTrans.setDilution(pUpdate.getValue(TethysDilution.class));
        } else if (MoneyWiseTransDataId.QUALIFYYEARS.equals(myField)) {
            /* Update the QualifyYears */
            myTrans.setYears(pUpdate.getValue(Integer.class));
        } else if (MoneyWiseTransDataId.RETURNEDCASHACCOUNT.equals(myField)) {
            /* Update the ReturnedCashAccount */
            myTrans.setReturnedCashAccount(pUpdate.getValue(TransactionAsset.class));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransDataId.RETURNEDCASH.equals(myField)) {
            /* Update the ReturnedCash */
            myTrans.setReturnedCash(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.TAXCREDIT.equals(myField)) {
            /* Update the TaxCredit */
            myTrans.setTaxCredit(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.EMPLOYEENATINS.equals(myField)) {
            /* Update the EmployeeNatIns */
            myTrans.setEmployeeNatIns(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.EMPLOYERNATINS.equals(myField)) {
            /* Update the EmployerNayIns */
            myTrans.setEmployerNatIns(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.DEEMEDBENEFIT.equals(myField)) {
            /* Update the Benefit */
            myTrans.setBenefit(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseTransDataId.WITHHELD.equals(myField)) {
            /* Update the Withheld */
            myTrans.setWithheld(pUpdate.getValue(TethysMoney.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        /* Access the item */
        final Transaction myItem = getItem();

        /* Access the analysis and the relevant filters */
        final Analysis myAnalysis = theAnalysisSelect.getAnalysis();
        final TransactionFilters myFilters = new TransactionFilters(myAnalysis, myItem);

        /* Remove the current filter */
        final AnalysisFilter<?, ?> myCurrent = theAnalysisSelect.getFilter();
        myFilters.remove(myCurrent);

        /* Loop through the filters */
        final Iterator<AnalysisFilter<?, ?>> myIterator = myFilters.iterator();
        while (myIterator.hasNext()) {
            final AnalysisFilter<?, ?> myFilter = myIterator.next();

            /* declare it */
            declareGoToFilter(myFilter);
        }

        /* If we have not had updates */
        if (!pUpdates) {
            /* Allow GoTo different panels */
            buildAssetGoTo(myItem.getAccount());
            buildAssetGoTo(myItem.getPartner());
            declareGoToItem(myItem.getCategory());
            buildAssetGoTo(myItem.getReturnedCashAccount());
        }
    }

    /**
     * Handle goto declarations for TransactionAssets.
     * @param pAsset the asset
     */
    private void buildAssetGoTo(final TransactionAsset pAsset) {
        if (pAsset instanceof SecurityHolding) {
            /* Build menu Items for Portfolio and Security */
            final SecurityHolding myHolding = (SecurityHolding) pAsset;
            declareGoToItem(myHolding.getPortfolio());
            declareGoToItem(myHolding.getSecurity());
        } else if (pAsset instanceof AssetBase<?, ?>) {
            declareGoToItem((AssetBase<?, ?>) pAsset);
        }
    }

    /**
     * Resolve Asset.
     * @param pAsset the asset to resolve
     * @return the resolved asset
     */
    public static TransactionAsset resolveAsset(final TransactionAsset pAsset) {
        /* If this is a security holding */
        if (pAsset instanceof SecurityHolding) {
            /* declare holding via map */
            final SecurityHolding myHolding = (SecurityHolding) pAsset;
            final Portfolio myPortfolio = myHolding.getPortfolio();
            final Security mySecurity = myHolding.getSecurity();
            final MoneyWiseData myData = myPortfolio.getDataSet();
            final SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();
            return myMap.declareHolding(myPortfolio, mySecurity);
        }

        /* Just return the asset */
        return pAsset;
    }

    /**
     * Build the account menu for an item.
     * @param pMenu the menu
     * @param pTrans the transaction to build for
     */
    public void buildAccountMenu(final TethysUIScrollMenu<TransactionAsset> pMenu,
                                 final Transaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Add possible items */
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class), true, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.CASH, CashList.class), true, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.LOAN, LoanList.class), true, pTrans);
        buildHoldingMenu(pMenu, true, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class), true, pTrans);
    }

    /**
     * Build the partner menu for an item.
     * @param pMenu the menu
     * @param pTrans the transaction to build for
     */
    public void buildPartnerMenu(final TethysUIScrollMenu<TransactionAsset> pMenu,
                                 final Transaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Add possible items */
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.CASH, CashList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.LOAN, LoanList.class), false, pTrans);
        buildHoldingMenu(pMenu, false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseDataType.PAYEE, PayeeList.class), false, pTrans);
    }

    /**
     * Build the asset menu for an item.
     * @param <T> the Asset type
     * @param pMenu the menu
     * @param pIsAccount is this item the account rather than partner
     * @param pList the asset list
     * @param pTrans the transaction to build for
     */
    private static <T extends AssetBase<T, ?>> void buildAssetMenu(final TethysUIScrollMenu<TransactionAsset> pMenu,
                                                                   final AssetBaseList<T, ?> pList,
                                                                   final boolean pIsAccount,
                                                                   final Transaction pTrans) {
        /* Record active item */
        final TransactionAsset myAccount = pTrans.getAccount();
        final TransactionCategory myCategory = pTrans.getCategory();
        final TransactionAsset myCurr = pIsAccount
                ? myAccount
                : pTrans.getPartner();
        TethysUIScrollItem<TransactionAsset> myActive = null;
        TethysUIScrollSubMenu<TransactionAsset> myMenu = null;

        /* Loop through the available values */
        final Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final T myAsset = myIterator.next();

            /* Only process non-deleted/non-closed items */
            boolean bIgnore = myAsset.isDeleted() || myAsset.isClosed();

            /* Check whether the asset is allowable for the owner */
            bIgnore |= !(pIsAccount
                    ? TransactionValidator.isValidAccount(myAsset)
                    : TransactionValidator.isValidPartner(myAccount, myCategory, myAsset));
            if (bIgnore) {
                continue;
            }

            /* If this the first item */
            if (myMenu == null) {
                /* Create a new subMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(pList.getItemType().getItemName());
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<TransactionAsset> myItem = myMenu.getSubMenu().addItem(myAsset);

            /* If this is the active category */
            if (myAsset.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the holding asset menu for an item.
     * @param pMenu the menu
     * @param pIsAccount is this item the account rather than partner
     * @param pTrans the transaction to build for
     */
    private static void buildHoldingMenu(final TethysUIScrollMenu<TransactionAsset> pMenu,
                                         final boolean pIsAccount,
                                         final Transaction pTrans) {
        /* Record active item */
        final TransactionAsset myAccount = pTrans.getAccount();
        final TransactionCategory myCategory = pTrans.getCategory();
        final TransactionAsset myCurr = pIsAccount
                ? myAccount
                : pTrans.getPartner();
        TethysUIScrollItem<TransactionAsset> myActive = null;
        TethysUIScrollSubMenu<TransactionAsset> myMenu = null;

        /* Access Portfolios and Holdings Map */
        final MoneyWiseData myData = pTrans.getDataSet();
        final PortfolioList myPortfolios = myData.getPortfolios();
        final SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        final Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final Portfolio myPortfolio = myPortIterator.next();
            TethysUIScrollSubMenu<TransactionAsset> myCoreMenu = null;

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted()
                    || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing and new holdings */
            final Iterator<SecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            final Iterator<SecurityHolding> myNewIterator = myMap.newIterator(myPortfolio);
            if ((myExistIterator != null) || (myNewIterator != null)) {
                /* If there are existing elements */
                if (myExistIterator != null) {
                    /* Loop through them */
                    while (myExistIterator.hasNext()) {
                        final SecurityHolding myHolding = myExistIterator.next();
                        final Security mySecurity = myHolding.getSecurity();

                        /* Check whether the asset is allowable for the owner */
                        final boolean bIgnore = !(pIsAccount
                                ? TransactionValidator.isValidAccount(myHolding)
                                : TransactionValidator.isValidPartner(myAccount, myCategory, myHolding));
                        if (bIgnore) {
                            continue;
                        }

                        /* Ensure that hierarchy is created */
                        if (myMenu == null) {
                            /* Create a new JMenu and add it to the popUp */
                            myMenu = pMenu.addSubMenu(AssetType.SECURITYHOLDING.toString());
                        }
                        if (myCoreMenu == null) {
                            /* Create a new JMenu and add it to the popUp */
                            myCoreMenu = myMenu.getSubMenu().addSubMenu(myPortfolio.getName());
                        }

                        /* Add the item to the menu */
                        final TethysUIScrollItem<TransactionAsset> myItem = myCoreMenu.getSubMenu().addItem(myHolding, mySecurity.getName());

                        /* If this is the active holding */
                        if (mySecurity.equals(myCurr)) {
                            /* Record it */
                            myActive = myItem;
                        }
                    }
                }

                /* If there are new elements */
                if (myNewIterator != null) {
                    /* Loop through them */
                    TethysUIScrollSubMenu<TransactionAsset> mySubMenu = null;
                    while (myNewIterator.hasNext()) {
                        final SecurityHolding myHolding = myNewIterator.next();
                        final Security mySecurity = myHolding.getSecurity();

                        /* Check whether the asset is allowable for the owner */
                        final boolean bIgnore = !(pIsAccount
                                ? TransactionValidator.isValidAccount(myHolding)
                                : TransactionValidator.isValidPartner(myAccount, myCategory, myHolding));
                        if (bIgnore) {
                            continue;
                        }

                        /* Ensure that hierarchy is created */
                        if (myMenu == null) {
                            /* Create a new subMenu and add it to the popUp */
                            myMenu = pMenu.addSubMenu(AssetType.SECURITYHOLDING.toString());
                        }
                        if (myCoreMenu == null) {
                            /* Create a new subMenu and add it to the popUp */
                            myCoreMenu = myMenu.getSubMenu().addSubMenu(myPortfolio.getName());
                        }
                        if (mySubMenu == null) {
                            /* Create a new subMenu */
                            mySubMenu = myCoreMenu.getSubMenu().addSubMenu(SecurityHolding.SECURITYHOLDING_NEW);
                        }

                        /* Add the item to the menu */
                        mySubMenu.getSubMenu().addItem(myHolding, mySecurity.getName());
                    }
                }
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the category menu for an item.
     * @param pMenu the menu
     * @param pTrans the transaction to build for
     */
    public void buildCategoryMenu(final TethysUIScrollMenu<TransactionCategory> pMenu,
                                  final Transaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionAsset myAccount = pTrans.getAccount();
        final TransactionCategory myCurr = pTrans.getCategory();
        TethysUIScrollItem<TransactionCategory> myActive = null;
        TethysUIScrollItem<TransactionCategory> myItem;

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<TransactionCategory>> myMap = new HashMap<>();

        /* Access Categories */
        final TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Loop through the available category values */
        final Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            final TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
            boolean bIgnore = myCategory.isDeleted() || myClass.canParentCategory();

            /* Check whether the category is allowable for the owner */
            bIgnore |= !TransactionValidator.isValidCategory(myAccount, myCategory);
            if (bIgnore) {
                continue;
            }

            /* Determine parent */
            final TransactionCategory myParent = myCategory.getParentCategory();

            /* If we have a parent */
            if (myParent != null) {
                final String myParentName = myParent.getName();
                TethysUIScrollSubMenu<TransactionCategory> myMenu = myMap.get(myParentName);

                /* If this is a new subMenu */
                if (myMenu == null) {
                    /* Create a new subMenu and add it to the popUp */
                    myMenu = pMenu.addSubMenu(myParentName);
                    myMap.put(myParentName, myMenu);
                }

                /* Create a new MenuItem and add it to the subMenu */
                myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

            } else {
                /* Create a new MenuItem and add it to the popUp */
                myItem = pMenu.addItem(myCategory);
            }

            /* If this is the active category */
            if (myCategory.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the ReturnedAccount menu for an item.
     * @param pMenu the menu
     * @param pTrans the transaction to build for
     */
    public void buildReturnedAccountMenu(final TethysUIScrollMenu<TransactionAsset> pMenu,
                                         final Transaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionAsset myCurr = pTrans.getReturnedCashAccount();
        TethysUIScrollItem<TransactionAsset> myActive = null;

        /* Access Deposits */
        final DepositList myDeposits = getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);

        /* Loop through the Portfolios */
        final Iterator<Deposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final Deposit myDeposit = myIterator.next();

            /* Ignore deleted or closed */
            final boolean bIgnore = myDeposit.isDeleted() || myDeposit.isClosed();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the account */
            final TethysUIScrollItem<TransactionAsset> myItem = pMenu.addItem(myDeposit);

            /* If this is the active returned account */
            if (myDeposit.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the possible TransactionTag list.
     * @return the transaction tag iterator
     */
    public Iterator<TransactionTag> buildTransactionTags() {
        /* Create a list */
        final List<TransactionTag> myList = new ArrayList<>();

        /* Loop through the TransactionTags */
        final Iterator<TransactionTag> myIterator = getItem().getDataSet().getTransactionTags().iterator();
        while (myIterator.hasNext()) {
            final TransactionTag myTag = myIterator.next();

            /* Add to list if available */
            if (!myTag.isDeleted()) {
                myList.add(myTag);
            }
        }

        /* Return the iterator */
        return myList.iterator();
    }
}
