/*******************************************************************************
ipo * jMoneyWise: Finance Application
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
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag.TransactionTagList;
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
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Panel to display/edit/create a Transaction.
 */
public class TransactionPanel
        extends MoneyWiseItemPanel<Transaction> {
    /**
     * The Field Set.
     */
    private final MetisFieldSet<Transaction> theFieldSet;

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
                            final MetisFieldManager pFieldMgr,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final TransactionBuilder pBuilder,
                            final MoneyWiseAnalysisSelect<JComponent, Icon> pAnalysisSelect,
                            final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);
        theAnalysisSelect = pAnalysisSelect;
        theBuilder = pBuilder;

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel(pFactory);

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the info panel */
        JPanel myPanel = buildInfoPanel(pFactory);
        myTabs.add(TAB_INFO, myPanel);

        /* Build the tax panel */
        myPanel = buildTaxPanel(pFactory);
        myTabs.add(TAB_TAXES, myPanel);

        /* Build the tax panel */
        myPanel = buildSecuritiesPanel(pFactory);
        myTabs.add(TAB_SECURITIES, myPanel);

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
        /* Allocate fields */
        TethysSwingStringTextField myAmount = pFactory.newStringField();

        /* Create the buttons */
        TethysSwingDateButtonManager myDateButton = pFactory.newDateButton();
        TethysSwingScrollButtonManager<TransactionAsset> myAccountButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<TransactionAsset> myPartnerButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<TransactionCategory> myCategoryButton = pFactory.newScrollButton();
        TethysSwingIconButtonManager<Boolean> myReconciledButton = pFactory.newIconButton();
        TethysSwingIconButtonManager<AssetDirection> myDirectionButton = pFactory.newIconButton();

        /* restrict the fields */
        restrictField(myDateButton, Transaction.DESCLEN);
        restrictField(myAccountButton, Transaction.DESCLEN);
        restrictField(myCategoryButton, Transaction.DESCLEN);
        restrictField(myDirectionButton, Transaction.DESCLEN);
        restrictField(myPartnerButton, Transaction.DESCLEN);
        restrictField(myAmount, Transaction.DESCLEN);
        restrictField(myReconciledButton, Transaction.DESCLEN);

        /* Declare fields */
        theFieldSet.addFieldElement(Transaction.FIELD_DATE, myDateButton);
        theFieldSet.addFieldElement(Transaction.FIELD_ACCOUNT, TransactionAsset.class, myAccountButton);
        theFieldSet.addFieldElement(Transaction.FIELD_CATEGORY, TransactionCategory.class, myCategoryButton);
        theFieldSet.addFieldElement(Transaction.FIELD_DIRECTION, AssetDirection.class, myDirectionButton);
        theFieldSet.addFieldElement(Transaction.FIELD_PARTNER, TransactionAsset.class, myPartnerButton);
        theFieldSet.addFieldElement(Transaction.FIELD_AMOUNT, MetisDataType.MONEY, myAmount);
        theFieldSet.addFieldElement(Transaction.FIELD_RECONCILED, Boolean.class, myReconciledButton);

        /* Create the main panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Transaction.FIELD_DATE, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_ACCOUNT, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_DIRECTION, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_PARTNER, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_AMOUNT, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_RECONCILED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myDateButton.setDateConfigurator(this::handleDateConfig);
        myAccountButton.setMenuConfigurator(c -> buildAccountMenu(c, getItem()));
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myPartnerButton.setMenuConfigurator(c -> buildPartnerMenu(c, getItem()));
        Map<Boolean, TethysIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton();
        myReconciledButton.setIconMapSet(() -> myRecMapSets.get(theReconciledState));
        Map<Boolean, TethysIconMapSet<AssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton();
        myDirectionButton.setIconMapSet(() -> myDirMapSets.get(theDirectionState));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build info subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildInfoPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        TethysSwingStringTextField myAmount = pFactory.newStringField();
        TethysSwingStringTextField myRate = pFactory.newStringField();
        TethysSwingStringTextField myComments = pFactory.newStringField();
        TethysSwingStringTextField myReference = pFactory.newStringField();

        /* Create the buttons */
        // TethysSwingListButtonManager<TransactionTag> myTagButton = pFactory.newListButton();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myAmount, myWidth);
        restrictField(myRate, myWidth);
        restrictField(myComments, myWidth);
        restrictField(myReference, myWidth);
        // restrictField(myTagButton, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT), MetisDataType.MONEY, myAmount);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE), MetisDataType.RATIO, myRate);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), MetisDataType.STRING, myComments);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), MetisDataType.STRING, myReference);
        // theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG),
        // myTagButton);

        /* Create the Info panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the info panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), myPanel);
        // theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG),
        // myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build tax subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildTaxPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        TethysSwingStringTextField myTaxCredit = pFactory.newStringField();
        TethysSwingStringTextField myForeignTaxCredit = pFactory.newStringField();
        TethysSwingStringTextField myNatIns = pFactory.newStringField();
        TethysSwingStringTextField myBenefit = pFactory.newStringField();
        TethysSwingStringTextField myWithheld = pFactory.newStringField();
        TethysSwingStringTextField myYears = pFactory.newStringField();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myTaxCredit, myWidth);
        restrictField(myForeignTaxCredit, myWidth);
        restrictField(myNatIns, myWidth);
        restrictField(myBenefit, myWidth);
        restrictField(myWithheld, myWidth);
        restrictField(myYears, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), MetisDataType.MONEY, myTaxCredit);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE), MetisDataType.MONEY, myNatIns);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), MetisDataType.MONEY, myBenefit);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD), MetisDataType.MONEY, myWithheld);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), MetisDataType.INTEGER, myYears);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.FOREIGNTAXCREDIT), MetisDataType.MONEY, myForeignTaxCredit);

        /* Create the Tax panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the Tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.FOREIGNTAXCREDIT), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build securities subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildSecuritiesPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        TethysSwingStringTextField myAccountUnits = pFactory.newStringField();
        TethysSwingStringTextField myPartnerUnits = pFactory.newStringField();
        TethysSwingStringTextField myCommission = pFactory.newStringField();
        TethysSwingStringTextField myPrice = pFactory.newStringField();
        TethysSwingStringTextField myDilution = pFactory.newStringField();
        TethysSwingStringTextField myReturnedCash = pFactory.newStringField();

        /* Create the buttons */
        TethysSwingScrollButtonManager<TransactionAsset> myReturnedAccountButton = pFactory.newScrollButton();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myAccountUnits, myWidth);
        restrictField(myPartnerUnits, myWidth);
        restrictField(myPrice, myWidth);
        restrictField(myCommission, myWidth);
        restrictField(myDilution, myWidth);
        restrictField(myReturnedCash, myWidth);
        restrictField(myReturnedAccountButton, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS), MetisDataType.UNITS, myAccountUnits);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS), MetisDataType.UNITS, myPartnerUnits);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PRICE), MetisDataType.PRICE, myPrice);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMISSION), MetisDataType.MONEY, myCommission);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), MetisDataType.DILUTION, myDilution);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT), TransactionAsset.class, myReturnedAccountButton);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH), MetisDataType.MONEY, myReturnedCash);

        /* Create the Tax panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PRICE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMISSION), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myReturnedAccountButton.setMenuConfigurator(c -> buildReturnedAccountMenu(c, getItem()));

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Transaction myItem = getItem();
        if (myItem != null) {
            TransactionList myTrans = getDataList(MoneyWiseDataType.TRANSACTION, TransactionList.class);
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
        /* Access the item */
        Transaction myTrans = getItem();
        boolean bIsReconciled = myTrans.isReconciled();
        boolean bIsLocked = myTrans.isLocked();
        Currency myCurrency = myTrans.getAccount().getCurrency();

        /* Determine whether the comments field should be visible */
        boolean bShowField = isEditable || myTrans.getComments() != null;
        theFieldSet.setVisibility(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), bShowField);

        /* Determine whether the reference field should be visible */
        bShowField = isEditable || myTrans.getReference() != null;
        theFieldSet.setVisibility(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), bShowField);

        /* Determine whether the tags field should be visible */
        bShowField = isEditable || myTrans.tagIterator() != null;
        theFieldSet.setVisibility(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), bShowField);

        /* Determine whether the partnerAmount field should be visible */
        MetisField myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT);
        boolean bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERAMOUNT);
        bShowField = bEditField || myTrans.getPartnerAmount() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myTrans.getPartner().getCurrency());

        /* Determine whether the exchangeRate field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.XCHANGERATE);
        bShowField = bEditField || myTrans.getExchangeRate() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the taxCredit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.TAXCREDIT);
        bShowField = bEditField || myTrans.getTaxCredit() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the taxCredit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.FOREIGNTAXCREDIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.FOREIGNTAXCREDIT);
        bShowField = bEditField || myTrans.getForeignTaxCredit() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the natIns field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.NATINSURANCE);
        bShowField = bEditField || myTrans.getNatInsurance() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the benefit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DEEMEDBENEFIT);
        bShowField = bEditField || myTrans.getDeemedBenefit() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the donation field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.WITHHELD);
        bShowField = bEditField || myTrans.getWithheld() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the account units field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.ACCOUNTDELTAUNITS);
        bShowField = bEditField || myTrans.getAccountDeltaUnits() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the partnerDeltaUnits field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERDELTAUNITS);
        bShowField = bEditField || myTrans.getPartnerDeltaUnits() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the price field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PRICE);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PRICE);
        bShowField = bEditField || myTrans.getPrice() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the commission field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMISSION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.COMMISSION);
        bShowField = bEditField || myTrans.getCommission() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the dilution field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DILUTION);
        bShowField = bEditField || myTrans.getDilution() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the returnedAccount field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.RETURNEDCASHACCOUNT);
        bShowField = bEditField || myTrans.getReturnedCashAccount() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the returnedCash field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.RETURNEDCASH);
        bShowField = bEditField || myTrans.getReturnedCash() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the years field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.QUALIFYYEARS);
        bShowField = bEditField || myTrans.getYears() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the reconciled field should be visible */
        boolean bShowReconciled = isEditable || bIsReconciled;
        theReconciledState = !bIsLocked;
        theDirectionState = !bIsReconciled;
        theFieldSet.setVisibility(Transaction.FIELD_RECONCILED, bShowReconciled);
        theFieldSet.setEditable(Transaction.FIELD_RECONCILED, isEditable && !bIsLocked);

        /* Determine basic editing */
        boolean canEdit = isEditable && !bIsReconciled;
        boolean needsNullAmount = myTrans.needsNullAmount();
        theFieldSet.setEditable(Transaction.FIELD_DIRECTION, canEdit && myTrans.canSwitchDirection());
        theFieldSet.setEditable(Transaction.FIELD_ACCOUNT, canEdit);
        theFieldSet.setEditable(Transaction.FIELD_PARTNER, canEdit);
        theFieldSet.setEditable(Transaction.FIELD_CATEGORY, canEdit);
        theFieldSet.setEditable(Transaction.FIELD_DATE, canEdit);
        theFieldSet.setEditable(Transaction.FIELD_AMOUNT, canEdit && !needsNullAmount);
        theFieldSet.setVisibility(Transaction.FIELD_AMOUNT, !needsNullAmount);
        theFieldSet.setAssumedCurrency(Transaction.FIELD_AMOUNT, myCurrency);

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
        TransactionInfoSet myInfoSet = pTrans.getInfoSet();

        /* If the transaction is reconciled */
        if (pTrans.isReconciled()) {
            /* Only allow editing of metaData */
            return myInfoSet.isMetaData(pField);
        }

        /* Check whether the field is available */
        MetisFieldRequired isRequired = myInfoSet.isClassRequired(pField);
        return !isRequired.equals(MetisFieldRequired.NOTALLOWED);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
        Transaction myTrans = getItem();

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
                // case TRANSTAG:
                // updateTag(myTrans, pUpdate.getEvent());
                // break;
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
                case FOREIGNTAXCREDIT:
                    myTrans.setForeignTaxCredit(pUpdate.getMoney());
                    break;
                case NATINSURANCE:
                    myTrans.setNatInsurance(pUpdate.getMoney());
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
        Transaction myItem = getItem();

        /* Access the analysis and the relevant filters */
        Analysis myAnalysis = theAnalysisSelect.getAnalysis();
        TransactionFilters myFilters = new TransactionFilters(myAnalysis, myItem);

        /* Remove the current filter */
        AnalysisFilter<?, ?> myCurrent = theAnalysisSelect.getFilter();
        myFilters.remove(myCurrent);

        /* Loop through the filters */
        Iterator<AnalysisFilter<?, ?>> myIterator = myFilters.iterator();
        while (myIterator.hasNext()) {
            AnalysisFilter<?, ?> myFilter = myIterator.next();

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
            SecurityHolding myHolding = (SecurityHolding) pAsset;
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
            SecurityHolding myHolding = (SecurityHolding) pAsset;
            Portfolio myPortfolio = myHolding.getPortfolio();
            Security mySecurity = myHolding.getSecurity();
            MoneyWiseData myData = myPortfolio.getDataSet();
            SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();
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
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionCategory myCategory = pTrans.getCategory();
        TransactionAsset myCurr = pIsAccount
                                             ? myAccount
                                             : pTrans.getPartner();
        TethysScrollMenuItem<TransactionAsset> myActive = null;
        TethysScrollSubMenu<TransactionAsset, Icon> myMenu = null;

        /* Loop through the available values */
        Iterator<T> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            T myAsset = myIterator.next();

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
            TethysScrollMenuItem<TransactionAsset> myItem = myMenu.getSubMenu().addItem(myAsset);

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
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionCategory myCategory = pTrans.getCategory();
        TransactionAsset myCurr = pIsAccount
                                             ? myAccount
                                             : pTrans.getPartner();
        TethysScrollMenuItem<TransactionAsset> myActive = null;
        TethysScrollSubMenu<TransactionAsset, Icon> myMenu = null;

        /* Access Portfolios and Holdings Map */
        MoneyWiseData myData = pTrans.getDataSet();
        PortfolioList myPortfolios = myData.getPortfolios();
        SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            Portfolio myPortfolio = myPortIterator.next();
            TethysScrollSubMenu<TransactionAsset, Icon> myCoreMenu = null;

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted() || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing and new holdings */
            Iterator<SecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            Iterator<SecurityHolding> myNewIterator = myMap.newIterator(myPortfolio);
            if ((myExistIterator != null) || (myNewIterator != null)) {
                /* If there are existing elements */
                if (myExistIterator != null) {
                    /* Loop through them */
                    while (myExistIterator.hasNext()) {
                        SecurityHolding myHolding = myExistIterator.next();
                        Security mySecurity = myHolding.getSecurity();

                        /* Check whether the asset is allowable for the owner */
                        boolean bIgnore = !(pIsAccount
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
                        TethysScrollMenuItem<TransactionAsset> myItem = myCoreMenu.getSubMenu().addItem(myHolding, mySecurity.getName());

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
                        SecurityHolding myHolding = myNewIterator.next();
                        Security mySecurity = myHolding.getSecurity();

                        /* Check whether the asset is allowable for the owner */
                        boolean bIgnore = !(pIsAccount
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
                        myCoreMenu.getSubMenu().addItem(myHolding, mySecurity.getName());
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
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionCategory myCurr = pTrans.getCategory();
        TethysScrollMenuItem<TransactionCategory> myActive = null;
        TethysScrollMenuItem<TransactionCategory> myItem;

        /* Create a simple map for top-level categories */
        Map<String, TethysScrollSubMenu<TransactionCategory, Icon>> myMap = new HashMap<>();

        /* Access Categories */
        TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Loop through the available category values */
        Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCategory = myIterator.next();

            /* Only process non-deleted low-level items */
            TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
            boolean bIgnore = myCategory.isDeleted() || myClass.canParentCategory();

            /* Check whether the category is allowable for the owner */
            bIgnore |= !TransactionValidator.isValidCategory(myAccount, myCategory);
            if (bIgnore) {
                continue;
            }

            /* Determine parent */
            TransactionCategory myParent = myCategory.getParentCategory();

            /* If we have a parent */
            if (myParent != null) {
                String myParentName = myParent.getName();
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
        TransactionAsset myCurr = pTrans.getReturnedCashAccount();
        TethysScrollMenuItem<TransactionAsset> myActive = null;

        /* Access Deposits */
        DepositList myDeposits = getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);

        /* Loop through the Portfolios */
        Iterator<Deposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            Deposit myDeposit = myIterator.next();

            /* Ignore deleted or closed */
            boolean bIgnore = myDeposit.isDeleted() || myDeposit.isClosed();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the account */
            TethysScrollMenuItem<TransactionAsset> myItem = pMenu.addItem(myDeposit);

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
     * Create TagItemList.
     * @param pTrans the transaction to build for
     * @return the itemList
     */
    public TethysItemList<TransactionTag> buildTagList(final Transaction pTrans) {
        /* Create a new itemList */
        TethysItemList<TransactionTag> myList = new TethysItemList<>();

        /* Access TransactionTags */
        TransactionTagList myTags = getDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);

        /* Loop through the tags */
        Iterator<TransactionTag> myTagIterator = myTags.iterator();
        while (myTagIterator.hasNext()) {
            TransactionTag myTag = myTagIterator.next();

            /* If the tag is not deleted */
            if (!myTag.isDeleted()) {
                /* Add item to the tag list */
                myList.setSelectableItem(myTag);
            }
        }

        /* Access tag iterator */
        Iterator<TransactionInfo> myIterator = pTrans.tagIterator();
        if (myIterator != null) {
            /* Loop through the TransactionTags */
            while (myIterator.hasNext()) {
                TransactionInfo myInfo = myIterator.next();

                /* If the item is not deleted */
                if (!myInfo.isDeleted()) {
                    /* Access the tag and set as active */
                    TransactionTag myTag = myInfo.getTransactionTag();
                    myList.selectItem(myTag);
                }
            }
        }

        /* return the item list */
        return myList;
    }
}
