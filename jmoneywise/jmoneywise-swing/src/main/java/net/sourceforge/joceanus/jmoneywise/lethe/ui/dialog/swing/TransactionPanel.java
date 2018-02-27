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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
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
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.TransactionFilters;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Panel to display/edit/create a Transaction.
 */
public class TransactionPanel
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
     * Analysis selection panel.
     */
    private final MoneyWiseAnalysisSelect<JComponent, Icon> theAnalysisSelect;

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
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pBuilder the transaction builder
     * @param pAnalysisSelect the analysis selection panel
     * @param pError the error panel
     */
    public TransactionPanel(final TethysSwingGuiFactory pFactory,
                            final MetisSwingFieldManager pFieldMgr,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final TransactionBuilder pBuilder,
                            final MoneyWiseAnalysisSelect<JComponent, Icon> pAnalysisSelect,
                            final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);
        theAnalysisSelect = pAnalysisSelect;
        theBuilder = pBuilder;

        /* Build the main panel */
        final MoneyWiseDataPanel myPanel = buildMainPanel(pFactory);

        /* Build the info panel */
        buildInfoPanel(pFactory);

        /* Build the tax panel */
        buildTaxPanel(pFactory);

        /* Build the securities panel */
        buildSecuritiesPanel(pFactory);

        /* Define the panel */
        defineMainPanel(myPanel);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private MoneyWiseDataPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Transaction.DESCLEN);

        /* Allocate fields */
        final TethysSwingStringTextField myAmount = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingDateButtonManager myDateButton = pFactory.newDateButton();
        final TethysSwingScrollButtonManager<TransactionAsset> myAccountButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<TransactionAsset> myPartnerButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<TransactionCategory> myCategoryButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myReconciledButton = pFactory.newIconButton();
        final TethysSwingIconButtonManager<AssetDirection> myDirectionButton = pFactory.newIconButton();

        /* Assign the fields to the panel */
        myPanel.addField(Transaction.FIELD_DATE, myDateButton);
        myPanel.addField(Transaction.FIELD_ACCOUNT, TransactionAsset.class, myAccountButton);
        myPanel.addField(Transaction.FIELD_CATEGORY, TransactionCategory.class, myCategoryButton);
        myPanel.addField(Transaction.FIELD_DIRECTION, AssetDirection.class, myDirectionButton);
        myPanel.addField(Transaction.FIELD_PARTNER, TransactionAsset.class, myPartnerButton);
        myPanel.addField(Transaction.FIELD_AMOUNT, MetisDataType.MONEY, myAmount);
        myPanel.addField(Transaction.FIELD_RECONCILED, Boolean.class, myReconciledButton);

        /* Layout the panel */
        myPanel.compactPanel();

        /* Configure the menuBuilders */
        myDateButton.setDateConfigurator(this::handleDateConfig);
        myAccountButton.setMenuConfigurator(c -> buildAccountMenu(c, getItem()));
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myPartnerButton.setMenuConfigurator(c -> buildPartnerMenu(c, getItem()));
        final Map<Boolean, TethysIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton();
        myReconciledButton.setIconMapSet(() -> myRecMapSets.get(theReconciledState));
        final Map<Boolean, TethysIconMapSet<AssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton();
        myDirectionButton.setIconMapSet(() -> myDirMapSets.get(theDirectionState));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build info subPanel.
     * @param pFactory the GUI factory
     */
    private void buildInfoPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_INFO, Transaction.DESCLEN >> 1);

        /* Allocate fields */
        final TethysSwingStringTextField myAmount = pFactory.newStringField();
        final TethysSwingStringTextField myComments = pFactory.newStringField();
        final TethysSwingStringTextField myReference = pFactory.newStringField();
        final TethysSwingStringTextField myRate = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingListButtonManager<TransactionTag> myTagButton = pFactory.newListButton();

        /* Assign the fields to the panel */
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT), MetisDataType.MONEY, myAmount);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), MetisDataType.STRING, myComments);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), MetisDataType.STRING, myReference);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), myTagButton);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE), MetisDataType.RATIO, myRate);

        /* Layout the panel */
        myTab.compactPanel();

        /* Configure the tag button */
        myTagButton.setSelectables(this::buildTransactionTags);
    }

    /**
     * Build tax subPanel.
     * @param pFactory the GUI factory
     */
    private void buildTaxPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_TAXES, Transaction.DESCLEN >> 1);

        /* Allocate fields */
        final TethysSwingStringTextField myTaxCredit = pFactory.newStringField();
        final TethysSwingStringTextField myEeNatIns = pFactory.newStringField();
        final TethysSwingStringTextField myErNatIns = pFactory.newStringField();
        final TethysSwingStringTextField myBenefit = pFactory.newStringField();
        final TethysSwingStringTextField myWithheld = pFactory.newStringField();
        final TethysSwingStringTextField myYears = pFactory.newStringField();

        /* Assign the fields to the panel */
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), MetisDataType.MONEY, myTaxCredit);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYEENATINS), MetisDataType.MONEY, myEeNatIns);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYERNATINS), MetisDataType.MONEY, myErNatIns);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), MetisDataType.MONEY, myBenefit);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD), MetisDataType.MONEY, myWithheld);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), MetisDataType.INTEGER, myYears);

        /* Layout the panel */
        myTab.compactPanel();
    }

    /**
     * Build securities subPanel.
     * @param pFactory the GUI factory
     */
    private void buildSecuritiesPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_SECURITIES, Transaction.DESCLEN >> 1);

        /* Allocate fields */
        final TethysSwingStringTextField myAccountUnits = pFactory.newStringField();
        final TethysSwingStringTextField myPartnerUnits = pFactory.newStringField();
        final TethysSwingStringTextField myCommission = pFactory.newStringField();
        final TethysSwingStringTextField myPrice = pFactory.newStringField();
        final TethysSwingStringTextField myDilution = pFactory.newStringField();
        final TethysSwingStringTextField myReturnedCash = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<TransactionAsset> myReturnedAccountButton = pFactory.newScrollButton();

        /* Assign the fields to the panel */
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS), MetisDataType.UNITS, myAccountUnits);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS), MetisDataType.UNITS, myPartnerUnits);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PRICE), MetisDataType.PRICE, myPrice);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMISSION), MetisDataType.MONEY, myCommission);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), MetisDataType.DILUTION, myDilution);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT), TransactionAsset.class, myReturnedAccountButton);
        myTab.addField(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH), MetisDataType.MONEY, myReturnedCash);

        /* Layout the panel */
        myTab.compactPanel();

        /* Configure the menuBuilders */
        myReturnedAccountButton.setMenuConfigurator(c -> buildReturnedAccountMenu(c, getItem()));
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
        return !getItem().isReconciled();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final MetisSwingFieldSet<Transaction> myFieldSet = getFieldSet();

        /* Access the item */
        final Transaction myTrans = getItem();
        final boolean bIsReconciled = myTrans.isReconciled();
        final boolean bIsLocked = myTrans.isLocked();
        final Currency myCurrency = myTrans.getAccount().getCurrency();

        /* Determine whether the comments field should be visible */
        boolean bShowField = isEditable || myTrans.getComments() != null;
        myFieldSet.setVisibility(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), bShowField);

        /* Determine whether the reference field should be visible */
        bShowField = isEditable || myTrans.getReference() != null;
        myFieldSet.setVisibility(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), bShowField);

        /* Determine whether the tags field should be visible */
        bShowField = isEditable || myTrans.getTransactionTags() != null;
        myFieldSet.setVisibility(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), bShowField);

        /* Determine whether the partnerAmount field should be visible */
        MetisField myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT);
        boolean bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERAMOUNT);
        bShowField = bEditField || myTrans.getPartnerAmount() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myTrans.getPartner().getCurrency());

        /* Determine whether the exchangeRate field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.XCHANGERATE);
        bShowField = bEditField || myTrans.getExchangeRate() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the taxCredit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.TAXCREDIT);
        bShowField = bEditField || myTrans.getTaxCredit() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the EeNatIns field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYEENATINS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.EMPLOYEENATINS);
        bShowField = bEditField || myTrans.getEmployeeNatIns() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the ErnatIns field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYERNATINS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.EMPLOYERNATINS);
        bShowField = bEditField || myTrans.getEmployerNatIns() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the benefit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DEEMEDBENEFIT);
        bShowField = bEditField || myTrans.getDeemedBenefit() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the donation field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.WITHHELD);
        bShowField = bEditField || myTrans.getWithheld() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the account units field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.ACCOUNTDELTAUNITS);
        bShowField = bEditField || myTrans.getAccountDeltaUnits() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the partnerDeltaUnits field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERDELTAUNITS);
        bShowField = bEditField || myTrans.getPartnerDeltaUnits() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the price field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PRICE);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PRICE);
        bShowField = bEditField || myTrans.getPrice() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the commission field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMISSION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.COMMISSION);
        bShowField = bEditField || myTrans.getCommission() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the dilution field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DILUTION);
        bShowField = bEditField || myTrans.getDilution() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the returnedAccount field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.RETURNEDCASHACCOUNT);
        bShowField = bEditField || myTrans.getReturnedCashAccount() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the returnedCash field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.RETURNEDCASH);
        bShowField = bEditField || myTrans.getReturnedCash() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the years field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.QUALIFYYEARS);
        bShowField = bEditField || myTrans.getYears() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the reconciled field should be visible */
        final boolean bShowReconciled = isEditable || bIsReconciled;
        theReconciledState = !bIsLocked;
        theDirectionState = !bIsReconciled;
        myFieldSet.setVisibility(Transaction.FIELD_RECONCILED, bShowReconciled);
        myFieldSet.setEditable(Transaction.FIELD_RECONCILED, isEditable && !bIsLocked);

        /* Determine basic editing */
        final boolean canEdit = isEditable && !bIsReconciled;
        final boolean needsNullAmount = myTrans.needsNullAmount();
        myFieldSet.setEditable(Transaction.FIELD_DIRECTION, canEdit && myTrans.canSwitchDirection());
        myFieldSet.setEditable(Transaction.FIELD_ACCOUNT, canEdit);
        myFieldSet.setEditable(Transaction.FIELD_PARTNER, canEdit);
        myFieldSet.setEditable(Transaction.FIELD_CATEGORY, canEdit);
        myFieldSet.setEditable(Transaction.FIELD_DATE, canEdit);
        myFieldSet.setEditable(Transaction.FIELD_AMOUNT, canEdit && !needsNullAmount);
        myFieldSet.setVisibility(Transaction.FIELD_AMOUNT, !needsNullAmount);
        myFieldSet.setAssumedCurrency(Transaction.FIELD_AMOUNT, myCurrency);

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
        if (pTrans.isReconciled()) {
            /* Only allow editing of metaData */
            return myInfoSet.isMetaData(pField);
        }

        /* Check whether the field is available */
        final MetisFieldRequired isRequired = myInfoSet.isClassRequired(pField);
        return !isRequired.equals(MetisFieldRequired.NOTALLOWED);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
        final Transaction myTrans = getItem();

        /* Process updates */
        if (myField.equals(Transaction.FIELD_DATE)) {
            /* Update the Date */
            myTrans.setDate(pUpdate.getDate());
        } else if (myField.equals(Transaction.FIELD_AMOUNT)) {
            /* Update the Amount */
            myTrans.setAmount(pUpdate.getMoney());
            theBuilder.autoCorrect(myTrans);
        } else if (myField.equals(Transaction.FIELD_ACCOUNT)) {
            /* Update the Account */
            myTrans.setAccount(resolveAsset(pUpdate.getValue(TransactionAsset.class)));
            theBuilder.autoCorrect(myTrans);
        } else if (myField.equals(Transaction.FIELD_DIRECTION)) {
            /* Update the Direction */
            myTrans.switchDirection();
            theBuilder.autoCorrect(myTrans);
        } else if (myField.equals(Transaction.FIELD_PARTNER)) {
            /* Update the Partner */
            myTrans.setPartner(resolveAsset(pUpdate.getValue(TransactionAsset.class)));
            theBuilder.autoCorrect(myTrans);
        } else if (myField.equals(Transaction.FIELD_CATEGORY)) {
            /* Update the Category */
            myTrans.setCategory(pUpdate.getValue(TransactionCategory.class));
            theBuilder.autoCorrect(myTrans);
        } else if (myField.equals(Transaction.FIELD_RECONCILED)) {
            /* Update the Reconciled indication */
            myTrans.setReconciled(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (TransactionInfoSet.getClassForField(myField)) {
                case COMMENTS:
                    myTrans.setComments(pUpdate.getString());
                    break;
                case REFERENCE:
                    myTrans.setReference(pUpdate.getString());
                    break;
                case TRANSTAG:
                    myTrans.setTransactionTags((List<TransactionTag>) pUpdate.getValue(List.class));
                    break;
                case PARTNERAMOUNT:
                    myTrans.setPartnerAmount(pUpdate.getMoney());
                    break;
                case XCHANGERATE:
                    myTrans.setExchangeRate(pUpdate.getRatio());
                    break;
                case ACCOUNTDELTAUNITS:
                    myTrans.setAccountDeltaUnits(pUpdate.getUnits());
                    break;
                case PARTNERDELTAUNITS:
                    myTrans.setPartnerDeltaUnits(pUpdate.getUnits());
                    break;
                case PRICE:
                    myTrans.setPrice(pUpdate.getPrice());
                    break;
                case COMMISSION:
                    myTrans.setCommission(pUpdate.getMoney());
                    break;
                case DILUTION:
                    myTrans.setDilution(pUpdate.getDilution());
                    break;
                case QUALIFYYEARS:
                    myTrans.setYears(pUpdate.getInteger());
                    break;
                case RETURNEDCASHACCOUNT:
                    myTrans.setReturnedCashAccount(pUpdate.getValue(TransactionAsset.class));
                    theBuilder.autoCorrect(myTrans);
                    break;
                case RETURNEDCASH:
                    myTrans.setReturnedCash(pUpdate.getMoney());
                    break;
                case TAXCREDIT:
                    myTrans.setTaxCredit(pUpdate.getMoney());
                    break;
                case EMPLOYEENATINS:
                    myTrans.setEmployeeNatIns(pUpdate.getMoney());
                    break;
                case EMPLOYERNATINS:
                    myTrans.setEmployerNatIns(pUpdate.getMoney());
                    break;
                case DEEMEDBENEFIT:
                    myTrans.setBenefit(pUpdate.getMoney());
                    break;
                case WITHHELD:
                    myTrans.setWithheld(pUpdate.getMoney());
                    break;
                default:
                    break;
            }
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
        } else if (pAsset instanceof AssetBase<?>) {
            declareGoToItem((AssetBase<?>) pAsset);
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
    public void buildAccountMenu(final TethysScrollMenu<TransactionAsset, Icon> pMenu,
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
    public void buildPartnerMenu(final TethysScrollMenu<TransactionAsset, Icon> pMenu,
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
    private static <T extends AssetBase<T>> void buildAssetMenu(final TethysScrollMenu<TransactionAsset, Icon> pMenu,
                                                                final AssetBaseList<T> pList,
                                                                final boolean pIsAccount,
                                                                final Transaction pTrans) {
        /* Record active item */
        final TransactionAsset myAccount = pTrans.getAccount();
        final TransactionCategory myCategory = pTrans.getCategory();
        final TransactionAsset myCurr = pIsAccount
                                                   ? myAccount
                                                   : pTrans.getPartner();
        TethysScrollMenuItem<TransactionAsset> myActive = null;
        TethysScrollSubMenu<TransactionAsset, Icon> myMenu = null;

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
            final TethysScrollMenuItem<TransactionAsset> myItem = myMenu.getSubMenu().addItem(myAsset);

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
    private static void buildHoldingMenu(final TethysScrollMenu<TransactionAsset, Icon> pMenu,
                                         final boolean pIsAccount,
                                         final Transaction pTrans) {
        /* Record active item */
        final TransactionAsset myAccount = pTrans.getAccount();
        final TransactionCategory myCategory = pTrans.getCategory();
        final TransactionAsset myCurr = pIsAccount
                                                   ? myAccount
                                                   : pTrans.getPartner();
        TethysScrollMenuItem<TransactionAsset> myActive = null;
        TethysScrollSubMenu<TransactionAsset, Icon> myMenu = null;

        /* Access Portfolios and Holdings Map */
        final MoneyWiseData myData = pTrans.getDataSet();
        final PortfolioList myPortfolios = myData.getPortfolios();
        final SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        final Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final Portfolio myPortfolio = myPortIterator.next();
            TethysScrollSubMenu<TransactionAsset, Icon> myCoreMenu = null;

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
                        final TethysScrollMenuItem<TransactionAsset> myItem = myCoreMenu.getSubMenu().addItem(myHolding, mySecurity.getName());

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
                    TethysScrollSubMenu<TransactionAsset, Icon> mySubMenu = null;
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
    public void buildCategoryMenu(final TethysScrollMenu<TransactionCategory, Icon> pMenu,
                                  final Transaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionAsset myAccount = pTrans.getAccount();
        final TransactionCategory myCurr = pTrans.getCategory();
        TethysScrollMenuItem<TransactionCategory> myActive = null;
        TethysScrollMenuItem<TransactionCategory> myItem;

        /* Create a simple map for top-level categories */
        final Map<String, TethysScrollSubMenu<TransactionCategory, Icon>> myMap = new HashMap<>();

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
                TethysScrollSubMenu<TransactionCategory, Icon> myMenu = myMap.get(myParentName);

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
    public void buildReturnedAccountMenu(final TethysScrollMenu<TransactionAsset, Icon> pMenu,
                                         final Transaction pTrans) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionAsset myCurr = pTrans.getReturnedCashAccount();
        TethysScrollMenuItem<TransactionAsset> myActive = null;

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
            final TethysScrollMenuItem<TransactionAsset> myItem = pMenu.addItem(myDeposit);

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

        /* Loop through the TransactionTagss */
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
