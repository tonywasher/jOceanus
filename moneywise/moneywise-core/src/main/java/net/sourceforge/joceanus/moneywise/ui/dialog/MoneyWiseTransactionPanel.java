/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase.MoneyWiseAssetBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransDefaults;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransValidator;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.moneywise.lethe.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseTransactionFilters;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateConfig;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIntegerEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIPriceEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIRatioEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIUnitsEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;

/**
 * Panel to display/edit/create a Transaction.
 */
public class MoneyWiseTransactionPanel
        extends MoneyWiseItemPanel<MoneyWiseTransaction> {
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
     * Returned Tab Title.
     */
    private static final String TAB_RETURNED = MoneyWiseUIResource.TRANSPANEL_TAB_RETURNED.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWiseTransaction> theFieldSet;

    /**
     * Analysis selection panel.
     */
    private final MoneyWiseAnalysisSelect theAnalysisSelect;

    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransDefaults theBuilder;

    /**
     * dateRange.
     */
    private OceanusDateRange theRange;

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
     * @param pEditSet the edit set
     * @param pBuilder the transaction builder
     * @param pAnalysisSelect the analysis selection panel
     * @param pOwner the owning table
     */
    public MoneyWiseTransactionPanel(final TethysUIFactory<?> pFactory,
                                     final PrometheusEditSet pEditSet,
                                     final MoneyWiseTransDefaults pBuilder,
                                     final MoneyWiseAnalysisSelect pAnalysisSelect,
                                     final MoneyWiseBaseTable<MoneyWiseTransaction> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);
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

        /* Build the returned panel */
        buildReturnedPanel(pFactory);
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
        final TethysUIScrollButtonField<MoneyWiseTransAsset> myAccountButton = myFields.newScrollField(MoneyWiseTransAsset.class);
        final TethysUIScrollButtonField<MoneyWiseTransAsset> myPartnerButton = myFields.newScrollField(MoneyWiseTransAsset.class);
        final TethysUIScrollButtonField<MoneyWiseTransCategory> myCategoryButton = myFields.newScrollField(MoneyWiseTransCategory.class);
        final TethysUIIconButtonField<Boolean> myReconciledButton = myFields.newIconField(Boolean.class);
        final TethysUIIconButtonField<MoneyWiseAssetDirection> myDirectionButton = myFields.newIconField(MoneyWiseAssetDirection.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, myDateButton, MoneyWiseTransaction::getDate);
        theFieldSet.addField(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, myAccountButton, MoneyWiseTransaction::getAccount);
        theFieldSet.addField(MoneyWiseBasicDataType.TRANSCATEGORY, myCategoryButton, MoneyWiseTransaction::getCategory);
        theFieldSet.addField(MoneyWiseBasicResource.TRANSACTION_DIRECTION, myDirectionButton, MoneyWiseTransaction::getDirection);
        theFieldSet.addField(MoneyWiseBasicResource.TRANSACTION_PARTNER, myPartnerButton, MoneyWiseTransaction::getPartner);
        theFieldSet.addField(MoneyWiseBasicResource.TRANSACTION_AMOUNT, myAmount, MoneyWiseTransaction::getAmount);
        theFieldSet.addField(MoneyWiseBasicResource.TRANSACTION_RECONCILED, myReconciledButton, MoneyWiseTransaction::isReconciled);

        /* Configure the menuBuilders */
        myDateButton.setDateConfigurator(this::handleDateConfig);
        myAccountButton.setMenuConfigurator(c -> buildAccountMenu(c, getItem()));
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myPartnerButton.setMenuConfigurator(c -> buildPartnerMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton(pFactory);
        myReconciledButton.setIconMapSet(() -> myRecMapSets.get(theReconciledState));
        final Map<Boolean, TethysUIIconMapSet<MoneyWiseAssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton(pFactory);
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
        final TethysUIListButtonField<MoneyWiseTransTag> myTagButton = myFields.newListField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransInfoClass.PARTNERAMOUNT, myAmount, MoneyWiseTransaction::getPartnerAmount);
        theFieldSet.addField(MoneyWiseTransInfoClass.COMMENTS, myComments, MoneyWiseTransaction::getComments);
        theFieldSet.addField(MoneyWiseTransInfoClass.REFERENCE, myReference, MoneyWiseTransaction::getReference);
        theFieldSet.addField(MoneyWiseTransInfoClass.TRANSTAG, myTagButton, MoneyWiseTransaction::getTransactionTags);
        theFieldSet.addField(MoneyWiseTransInfoClass.XCHANGERATE, myRate, MoneyWiseTransaction::getExchangeRate);

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
        theFieldSet.addField(MoneyWiseTransInfoClass.TAXCREDIT, myTaxCredit, MoneyWiseTransaction::getTaxCredit);
        theFieldSet.addField(MoneyWiseTransInfoClass.EMPLOYEENATINS, myEeNatIns, MoneyWiseTransaction::getEmployeeNatIns);
        theFieldSet.addField(MoneyWiseTransInfoClass.EMPLOYERNATINS, myErNatIns, MoneyWiseTransaction::getEmployerNatIns);
        theFieldSet.addField(MoneyWiseTransInfoClass.DEEMEDBENEFIT, myBenefit, MoneyWiseTransaction::getDeemedBenefit);
        theFieldSet.addField(MoneyWiseTransInfoClass.WITHHELD, myWithheld, MoneyWiseTransaction::getWithheld);
        theFieldSet.addField(MoneyWiseTransInfoClass.QUALIFYYEARS, myYears, MoneyWiseTransaction::getYears);

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
        final TethysUIRatioEditField myDilution = myFields.newRatioField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, myAccountUnits, MoneyWiseTransaction::getAccountDeltaUnits);
        theFieldSet.addField(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, myPartnerUnits, MoneyWiseTransaction::getPartnerDeltaUnits);
        theFieldSet.addField(MoneyWiseTransInfoClass.PRICE, myPrice, MoneyWiseTransaction::getPrice);
        theFieldSet.addField(MoneyWiseTransInfoClass.COMMISSION, myCommission, MoneyWiseTransaction::getCommission);
        theFieldSet.addField(MoneyWiseTransInfoClass.DILUTION, myDilution, MoneyWiseTransaction::getDilution);

        /* Set currency */
        myCommission.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
        myPrice.setDeemedCurrency(() -> getItem().getAccount().getCurrency());
    }

    /**
     * Build returned subPanel.
     * @param pFactory the GUI factory
     */
    private void buildReturnedPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_RETURNED);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIMoneyEditField myReturnedCash = myFields.newMoneyField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseTransAsset> myReturnedAccountButton = myFields.newScrollField(MoneyWiseTransAsset.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, myReturnedAccountButton, MoneyWiseTransaction::getReturnedCashAccount);
        theFieldSet.addField(MoneyWiseTransInfoClass.RETURNEDCASH, myReturnedCash, MoneyWiseTransaction::getReturnedCash);

        /* Configure the menuBuilders */
        myReturnedAccountButton.setMenuConfigurator(c -> buildReturnedAccountMenu(c, getItem()));

        /* Set currency */
        myReturnedCash.setDeemedCurrency(() -> getItem().getReturnedCashAccount().getCurrency());
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseTransaction myItem = getItem();
        if (myItem != null) {
            final MoneyWiseTransactionList myTrans = getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class);
            setItem(myTrans.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    /**
     * Update editors.
     * @param pRange the date range.
     */
    public void updateEditors(final OceanusDateRange pRange) {
        /* Update the range */
        theRange = pRange;
    }

    /**
     * Handle dateConfig.
     * @param pConfig the dateConfig
     */
    private void handleDateConfig(final OceanusDateConfig pConfig) {
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
        final MoneyWiseTransaction myTrans = getItem();
        final boolean bIsReconciled = myTrans.isReconciled();
        final boolean bIsLocked = myTrans.isLocked();

        /* Determine whether the comments field should be visible */
        boolean bShowField = isEditable || myTrans.getComments() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.COMMENTS, bShowField);

        /* Determine whether the reference field should be visible */
        bShowField = isEditable || myTrans.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.REFERENCE, bShowField);

        /* Determine whether the tags field should be visible */
        bShowField = isEditable || myTrans.getTransactionTags() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.TRANSTAG, bShowField);

        /* Determine whether the partnerAmount field should be visible */
        boolean bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.PARTNERAMOUNT);
        bShowField = bEditField || myTrans.getPartnerAmount() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.PARTNERAMOUNT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.PARTNERAMOUNT, bEditField);

        /* Determine whether the exchangeRate field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.XCHANGERATE);
        bShowField = bEditField || myTrans.getExchangeRate() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.XCHANGERATE, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.XCHANGERATE, bEditField);

        /* Determine whether the taxCredit field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.TAXCREDIT);
        bShowField = bEditField || myTrans.getTaxCredit() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.TAXCREDIT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.TAXCREDIT, bEditField);

        /* Determine whether the EeNatIns field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.EMPLOYEENATINS);
        bShowField = bEditField || myTrans.getEmployeeNatIns() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.EMPLOYEENATINS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.EMPLOYEENATINS, bEditField);

        /* Determine whether the ErnatIns field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.EMPLOYERNATINS);
        bShowField = bEditField || myTrans.getEmployerNatIns() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.EMPLOYERNATINS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.EMPLOYERNATINS, bEditField);

        /* Determine whether the benefit field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.DEEMEDBENEFIT);
        bShowField = bEditField || myTrans.getDeemedBenefit() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.DEEMEDBENEFIT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.DEEMEDBENEFIT, bEditField);

        /* Determine whether the donation field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.WITHHELD);
        bShowField = bEditField || myTrans.getWithheld() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.WITHHELD, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.WITHHELD, bEditField);

        /* Determine whether the account units field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS);
        bShowField = bEditField || myTrans.getAccountDeltaUnits() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, bEditField);

        /* Determine whether the partnerDeltaUnits field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.PARTNERDELTAUNITS);
        bShowField = bEditField || myTrans.getPartnerDeltaUnits() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, bEditField);

        /* Determine whether the price field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.PRICE);
        bShowField = bEditField || myTrans.getPrice() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.PRICE, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.PRICE, bEditField);

        /* Determine whether the commission field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.COMMISSION);
        bShowField = bEditField || myTrans.getCommission() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.COMMISSION, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.COMMISSION, bEditField);

        /* Determine whether the dilution field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.DILUTION);
        bShowField = bEditField || myTrans.getDilution() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.DILUTION, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.DILUTION, bEditField);

        /* Determine whether the returnedAccount field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT);
        bShowField = bEditField || myTrans.getReturnedCashAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, bEditField);

        /* Determine whether the returnedCash field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.RETURNEDCASH);
        bShowField = bEditField || myTrans.getReturnedCash() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.RETURNEDCASH, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.RETURNEDCASH, bEditField);

        /* Determine whether the years field should be visible */
        bEditField = isEditable && isEditableField(myTrans, MoneyWiseTransInfoClass.QUALIFYYEARS);
        bShowField = bEditField || myTrans.getYears() != null;
        theFieldSet.setFieldVisible(MoneyWiseTransInfoClass.QUALIFYYEARS, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseTransInfoClass.QUALIFYYEARS, bEditField);

        /* Determine whether the reconciled field should be visible */
        final boolean bShowReconciled = isEditable || bIsReconciled;
        theReconciledState = bIsLocked;
        theDirectionState = bIsReconciled;
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.TRANSACTION_RECONCILED, bShowReconciled);
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.TRANSACTION_RECONCILED, isEditable && !bIsLocked);

        /* Determine basic editing */
        final boolean canEdit = isEditable && !bIsReconciled;
        final boolean needsNullAmount = myTrans.needsNullAmount();
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.TRANSACTION_DIRECTION, canEdit && myTrans.canSwitchDirection());
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.TRANSACTION_PARTNER, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseBasicDataType.TRANSCATEGORY, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.TRANSACTION_AMOUNT, canEdit && !needsNullAmount);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.TRANSACTION_AMOUNT, !needsNullAmount);

        /* Set the range for the dateButton */
        theRange = theBuilder.getRange();
    }

    /**
     * Is the field editable?
     * @param pTrans the transaction
     * @param pField the field class
     * @return true/false
     */
    public static boolean isEditableField(final MoneyWiseTransaction pTrans,
                                          final MoneyWiseTransInfoClass pField) {
        /* Access the infoSet */
        final MoneyWiseTransInfoSet myInfoSet = pTrans.getInfoSet();

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
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseTransaction myTrans = getItem();

        /* Process updates */
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(myField)) {
            /* Update the Date */
            myTrans.setDate(pUpdate.getValue(OceanusDate.class));
        } else if (MoneyWiseBasicResource.TRANSACTION_AMOUNT.equals(myField)) {
            /* Update the Amount */
            myTrans.setAmount(pUpdate.getValue(OceanusMoney.class));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseBasicResource.TRANSACTION_ACCOUNT.equals(myField)) {
            /* Update the Account */
            myTrans.setAccount(resolveAsset(pUpdate.getValue(MoneyWiseTransAsset.class)));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseBasicResource.TRANSACTION_DIRECTION.equals(myField)) {
            /* Update the Direction */
            myTrans.switchDirection();
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseBasicResource.TRANSACTION_PARTNER.equals(myField)) {
            /* Update the Partner */
            myTrans.setPartner(resolveAsset(pUpdate.getValue(MoneyWiseTransAsset.class)));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseBasicDataType.TRANSCATEGORY.equals(myField)) {
            /* Update the Category */
            myTrans.setCategory(pUpdate.getValue(MoneyWiseTransCategory.class));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseBasicResource.TRANSACTION_RECONCILED.equals(myField)) {
            /* Update the Reconciled indication */
            myTrans.setReconciled(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseTransInfoClass.COMMENTS.equals(myField)) {
            /* Update the Comments */
            myTrans.setComments(pUpdate.getValue(String.class));
        } else if (MoneyWiseTransInfoClass.REFERENCE.equals(myField)) {
            /* Update the Reference */
            myTrans.setReference(pUpdate.getValue(String.class));
        } else if (MoneyWiseTransInfoClass.TRANSTAG.equals(myField)) {
            /* Update the Tag indication */
            myTrans.setTransactionTags(pUpdate.getValue(List.class));
        } else if (MoneyWiseTransInfoClass.PARTNERAMOUNT.equals(myField)) {
            /* Update the PartnerAmount */
            myTrans.setPartnerAmount(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.XCHANGERATE.equals(myField)) {
            /* Update the ExchangeRate */
            myTrans.setExchangeRate(pUpdate.getValue(OceanusRatio.class));
        } else if (MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS.equals(myField)) {
            /* Update the AccountDeltaUnits */
            myTrans.setAccountDeltaUnits(pUpdate.getValue(OceanusUnits.class));
        } else if (MoneyWiseTransInfoClass.PARTNERDELTAUNITS.equals(myField)) {
            /* Update the PartnerDeltaUnits */
            myTrans.setPartnerDeltaUnits(pUpdate.getValue(OceanusUnits.class));
        } else if (MoneyWiseTransInfoClass.PRICE.equals(myField)) {
            /* Update the Price */
            myTrans.setPrice(pUpdate.getValue(OceanusPrice.class));
        } else if (MoneyWiseTransInfoClass.COMMISSION.equals(myField)) {
            /* Update the Commission */
            myTrans.setCommission(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.DILUTION.equals(myField)) {
            /* Update the Dilution */
            myTrans.setDilution(pUpdate.getValue(OceanusRatio.class));
        } else if (MoneyWiseTransInfoClass.QUALIFYYEARS.equals(myField)) {
            /* Update the QualifyYears */
            myTrans.setYears(pUpdate.getValue(Integer.class));
        } else if (MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT.equals(myField)) {
            /* Update the ReturnedCashAccount */
            myTrans.setReturnedCashAccount(pUpdate.getValue(MoneyWiseTransAsset.class));
            theBuilder.autoCorrect(myTrans);
        } else if (MoneyWiseTransInfoClass.RETURNEDCASH.equals(myField)) {
            /* Update the ReturnedCash */
            myTrans.setReturnedCash(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.TAXCREDIT.equals(myField)) {
            /* Update the TaxCredit */
            myTrans.setTaxCredit(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.EMPLOYEENATINS.equals(myField)) {
            /* Update the EmployeeNatIns */
            myTrans.setEmployeeNatIns(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.EMPLOYERNATINS.equals(myField)) {
            /* Update the EmployerNayIns */
            myTrans.setEmployerNatIns(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.DEEMEDBENEFIT.equals(myField)) {
            /* Update the Benefit */
            myTrans.setBenefit(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseTransInfoClass.WITHHELD.equals(myField)) {
            /* Update the Withheld */
            myTrans.setWithheld(pUpdate.getValue(OceanusMoney.class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        /* Access the item */
        final MoneyWiseTransaction myItem = getItem();

        /* Access the analysis and the relevant filters */
        final MoneyWiseAnalysis myAnalysis = theAnalysisSelect.getAnalysis();
        final OceanusDateRange myDateRange = theAnalysisSelect.getRange();
        final MoneyWiseTransactionFilters myFilters = new MoneyWiseTransactionFilters(myAnalysis, myDateRange, myItem);

        /* Remove the current filter */
        final MoneyWiseAnalysisFilter<?, ?> myCurrent = theAnalysisSelect.getFilter();
        myFilters.remove(myCurrent);

        /* Loop through the filters */
        final Iterator<MoneyWiseAnalysisFilter<?, ?>> myIterator = myFilters.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisFilter<?, ?> myFilter = myIterator.next();

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
    private void buildAssetGoTo(final MoneyWiseTransAsset pAsset) {
        if (pAsset instanceof MoneyWiseSecurityHolding) {
            /* Build menu Items for Portfolio and Security */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pAsset;
            declareGoToItem(myHolding.getPortfolio());
            declareGoToItem(myHolding.getSecurity());
        } else if (pAsset instanceof MoneyWiseAssetBase) {
            declareGoToItem((MoneyWiseAssetBase) pAsset);
        }
    }

    /**
     * Resolve Asset.
     * @param pAsset the asset to resolve
     * @return the resolved asset
     */
    public static MoneyWiseTransAsset resolveAsset(final MoneyWiseTransAsset pAsset) {
        /* If this is a security holding */
        if (pAsset instanceof MoneyWiseSecurityHolding) {
            /* declare holding via map */
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pAsset;
            final MoneyWisePortfolio myPortfolio = myHolding.getPortfolio();
            final MoneyWiseSecurity mySecurity = myHolding.getSecurity();
            final MoneyWiseDataSet myData = myPortfolio.getDataSet();
            final MoneyWiseSecurityHoldingMap myMap = myData.getPortfolios().getSecurityHoldingsMap();
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
    public void buildAccountMenu(final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu,
                                 final MoneyWiseTransaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Add possible items */
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class), true, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class), true, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class), true, pTrans);
        buildHoldingMenu(pMenu, true, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class), true, pTrans);
    }

    /**
     * Build the partner menu for an item.
     * @param pMenu the menu
     * @param pTrans the transaction to build for
     */
    public void buildPartnerMenu(final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu,
                                 final MoneyWiseTransaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Add possible items */
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class), false, pTrans);
        buildHoldingMenu(pMenu, false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class), false, pTrans);
    }

    /**
     * Build the asset menu for an item.
     * @param <T> the Asset type
     * @param pMenu the menu
     * @param pIsAccount is this item the account rather than partner
     * @param pList the asset list
     * @param pTrans the transaction to build for
     */
    private static <T extends MoneyWiseAssetBase> void buildAssetMenu(final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu,
                                                                      final MoneyWiseAssetBaseList<T> pList,
                                                                      final boolean pIsAccount,
                                                                      final MoneyWiseTransaction pTrans) {
        /* Record active item */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final MoneyWiseTransCategory myCategory = pTrans.getCategory();
        final MoneyWiseTransAsset myCurr = pIsAccount
                ? myAccount
                : pTrans.getPartner();
        TethysUIScrollItem<MoneyWiseTransAsset> myActive = null;
        TethysUIScrollSubMenu<MoneyWiseTransAsset> myMenu = null;

        /* Loop through the available values */
        final Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final T myAsset = myIterator.next();

            /* Only process non-deleted/non-closed items */
            boolean bIgnore = myAsset.isDeleted() || myAsset.isClosed();

            /* Check whether the asset is allowable for the owner */
            bIgnore |= !(pIsAccount
                    ? MoneyWiseTransValidator.isValidAccount(myAsset)
                    : MoneyWiseTransValidator.isValidPartner(myAccount, myCategory, myAsset));
            if (bIgnore) {
                continue;
            }

            /* If this the first item */
            if (myMenu == null) {
                /* Create a new subMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(pList.getItemType().getItemName());
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseTransAsset> myItem = myMenu.getSubMenu().addItem(myAsset);

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
    private static void buildHoldingMenu(final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu,
                                         final boolean pIsAccount,
                                         final MoneyWiseTransaction pTrans) {
        /* Record active item */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final MoneyWiseTransCategory myCategory = pTrans.getCategory();
        final MoneyWiseTransAsset myCurr = pIsAccount
                ? myAccount
                : pTrans.getPartner();
        TethysUIScrollItem<MoneyWiseTransAsset> myActive = null;
        TethysUIScrollSubMenu<MoneyWiseTransAsset> myMenu = null;

        /* Access Portfolios and Holdings Map */
        final MoneyWiseDataSet myData = pTrans.getDataSet();
        final MoneyWisePortfolioList myPortfolios = myData.getPortfolios();
        final MoneyWiseSecurityHoldingMap myMap = myPortfolios.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        final Iterator<MoneyWisePortfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWisePortfolio myPortfolio = myPortIterator.next();
            TethysUIScrollSubMenu<MoneyWiseTransAsset> myCoreMenu = null;

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted()
                    || Boolean.TRUE.equals(myPortfolio.isClosed())) {
                continue;
            }

            /* Look for existing and new holdings */
            final Iterator<MoneyWiseSecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            final Iterator<MoneyWiseSecurityHolding> myNewIterator = myMap.newIterator(myPortfolio);
            if ((myExistIterator != null) || (myNewIterator != null)) {
                /* If there are existing elements */
                if (myExistIterator != null) {
                    /* Loop through them */
                    while (myExistIterator.hasNext()) {
                        final MoneyWiseSecurityHolding myHolding = myExistIterator.next();
                        final MoneyWiseSecurity mySecurity = myHolding.getSecurity();

                        /* Check whether the asset is allowable for the owner */
                        final boolean bIgnore = !(pIsAccount
                                ? MoneyWiseTransValidator.isValidAccount(myHolding)
                                : MoneyWiseTransValidator.isValidPartner(myAccount, myCategory, myHolding));
                        if (bIgnore) {
                            continue;
                        }

                        /* Ensure that hierarchy is created */
                        if (myMenu == null) {
                            /* Create a new JMenu and add it to the popUp */
                            myMenu = pMenu.addSubMenu(MoneyWiseAssetType.SECURITYHOLDING.toString());
                        }
                        if (myCoreMenu == null) {
                            /* Create a new JMenu and add it to the popUp */
                            myCoreMenu = myMenu.getSubMenu().addSubMenu(myPortfolio.getName());
                        }

                        /* Add the item to the menu */
                        final TethysUIScrollItem<MoneyWiseTransAsset> myItem = myCoreMenu.getSubMenu().addItem(myHolding, mySecurity.getName());

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
                    TethysUIScrollSubMenu<MoneyWiseTransAsset> mySubMenu = null;
                    while (myNewIterator.hasNext()) {
                        final MoneyWiseSecurityHolding myHolding = myNewIterator.next();
                        final MoneyWiseSecurity mySecurity = myHolding.getSecurity();

                        /* Check whether the asset is allowable for the owner */
                        final boolean bIgnore = !(pIsAccount
                                ? MoneyWiseTransValidator.isValidAccount(myHolding)
                                : MoneyWiseTransValidator.isValidPartner(myAccount, myCategory, myHolding));
                        if (bIgnore) {
                            continue;
                        }

                        /* Ensure that hierarchy is created */
                        if (myMenu == null) {
                            /* Create a new subMenu and add it to the popUp */
                            myMenu = pMenu.addSubMenu(MoneyWiseAssetType.SECURITYHOLDING.toString());
                        }
                        if (myCoreMenu == null) {
                            /* Create a new subMenu and add it to the popUp */
                            myCoreMenu = myMenu.getSubMenu().addSubMenu(myPortfolio.getName());
                        }
                        if (mySubMenu == null) {
                            /* Create a new subMenu */
                            mySubMenu = myCoreMenu.getSubMenu().addSubMenu(MoneyWiseSecurityHolding.SECURITYHOLDING_NEW);
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
    public void buildCategoryMenu(final TethysUIScrollMenu<MoneyWiseTransCategory> pMenu,
                                  final MoneyWiseTransaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final MoneyWiseTransCategory myCurr = pTrans.getCategory();
        TethysUIScrollItem<MoneyWiseTransCategory> myActive = null;
        TethysUIScrollItem<MoneyWiseTransCategory> myItem;

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<MoneyWiseTransCategory>> myMap = new HashMap<>();

        /* Access Categories */
        final MoneyWiseTransCategoryList myCategories = getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);

        /* Loop through the available category values */
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            final MoneyWiseTransCategoryClass myClass = myCategory.getCategoryTypeClass();
            boolean bIgnore = myCategory.isDeleted() || myClass.canParentCategory();

            /* Check whether the category is allowable for the owner */
            bIgnore |= !MoneyWiseTransValidator.isValidCategory(myAccount, myCategory);
            if (bIgnore) {
                continue;
            }

            /* Determine parent */
            final MoneyWiseTransCategory myParent = myCategory.getParentCategory();

            /* If we have a parent */
            if (myParent != null) {
                final String myParentName = myParent.getName();
                final TethysUIScrollSubMenu<MoneyWiseTransCategory> myMenu = myMap.computeIfAbsent(myParentName, pMenu::addSubMenu);

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
    public void buildReturnedAccountMenu(final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu,
                                         final MoneyWiseTransaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Add possible items */
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class), false, pTrans);
        buildAssetMenu(pMenu, getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class), false, pTrans);
    }

    /**
     * Build the possible TransactionTag list.
     * @return the transaction tag iterator
     */
    public Iterator<MoneyWiseTransTag> buildTransactionTags() {
        /* Create a list */
        final List<MoneyWiseTransTag> myList = new ArrayList<>();

        /* Loop through the TransactionTags */
        final Iterator<MoneyWiseTransTag> myIterator = getItem().getDataSet().getTransactionTags().iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransTag myTag = myIterator.next();

            /* Add to list if available */
            if (!myTag.isDeleted()) {
                myList.add(myTag);
            }
        }

        /* Return the iterator */
        return myList.iterator();
    }
}
