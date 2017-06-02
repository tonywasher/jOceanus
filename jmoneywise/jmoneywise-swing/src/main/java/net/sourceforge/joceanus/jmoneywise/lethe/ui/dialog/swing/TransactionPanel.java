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
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
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
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.TransactionFilters;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.lethe.date.swing.TethysSwingDateButton;
import net.sourceforge.joceanus.jtethys.lethe.date.swing.TethysSwingDateConfig;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollListButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollListButton.JScrollListMenuBuilder;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollListButton.ToggleState;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Panel to display/edit/create a Transaction.
 */
public class TransactionPanel
        extends MoneyWiseDataItemPanel<Transaction> {
    /**
     * The Field Set.
     */
    private final transient MetisFieldSet<Transaction> theFieldSet;

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
     * Date Button.
     */
    private final TethysSwingDateButton theDateButton;

    /**
     * Account Button Field.
     */
    private final JScrollButton<TransactionAsset> theAccountButton;

    /**
     * Partner Button Field.
     */
    private final JScrollButton<TransactionAsset> thePartnerButton;

    /**
     * Category Button Field.
     */
    private final JScrollButton<TransactionCategory> theCategoryButton;

    /**
     * ThirdParty Button Field.
     */
    private final JScrollButton<Deposit> theThirdPartyButton;

    /**
     * TransactionTag Button Field.
     */
    private final JScrollListButton<TransactionTag> theTagButton;

    /**
     * Analysis selection panel.
     */
    private final MoneyWiseAnalysisSelect<JComponent, Icon> theAnalysisSelect;

    /**
     * The Tag Menu Builder.
     */
    private final JScrollListMenuBuilder<TransactionTag> theTagMenuBuilder;

    /**
     * Direction Button Field.
     */
    private final ComplexIconButtonState<AssetDirection, Boolean> theDirectionState;

    /**
     * Reconciled Button Field.
     */
    private final ComplexIconButtonState<Boolean, Boolean> theReconciledState;

    /**
     * TransactionBuilder.
     */
    private final TransactionBuilder theBuilder;

    /**
     * The Account Menu Builder.
     */
    private final JScrollMenuBuilder<TransactionAsset> theAccountMenuBuilder;

    /**
     * The Partner Menu Builder.
     */
    private final JScrollMenuBuilder<TransactionAsset> thePartnerMenuBuilder;

    /**
     * The Category Menu Builder.
     */
    private final JScrollMenuBuilder<TransactionCategory> theCategoryMenuBuilder;

    /**
     * The ThirdParty Menu Builder.
     */
    private final JScrollMenuBuilder<Deposit> theThirdPartyMenuBuilder;

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

        /* Create the text fields */
        theDateButton = new TethysSwingDateButton();

        /* Create the buttons */
        theAccountButton = new JScrollButton<>();
        thePartnerButton = new JScrollButton<>();
        theCategoryButton = new JScrollButton<>();
        theTagButton = new JScrollListButton<>();
        theThirdPartyButton = new JScrollButton<>();

        /* Access tag menu builder */
        theTagMenuBuilder = theTagButton.getMenuBuilder();

        /* Create states */
        theDirectionState = new ComplexIconButtonState<>(Boolean.FALSE);
        theReconciledState = new ComplexIconButtonState<>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the info panel */
        JPanel myPanel = buildInfoPanel();
        myTabs.add(TAB_INFO, myPanel);

        /* Build the tax panel */
        myPanel = buildTaxPanel();
        myTabs.add(TAB_TAXES, myPanel);

        /* Build the tax panel */
        myPanel = buildSecuritiesPanel();
        myTabs.add(TAB_SECURITIES, myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listeners */
        theAccountMenuBuilder = theAccountButton.getMenuBuilder();
        theAccountMenuBuilder.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildAccountMenu(theAccountMenuBuilder, getItem()));
        thePartnerMenuBuilder = thePartnerButton.getMenuBuilder();
        thePartnerMenuBuilder.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildPartnerMenu(thePartnerMenuBuilder, getItem()));
        theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
        theCategoryMenuBuilder.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildCategoryMenu(theCategoryMenuBuilder, getItem()));
        theThirdPartyMenuBuilder = theThirdPartyButton.getMenuBuilder();
        theThirdPartyMenuBuilder.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildThirdPartyMenu(theThirdPartyMenuBuilder, getItem()));
        theTagMenuBuilder.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildTagMenu(theTagMenuBuilder, getItem()));
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Create direction button */
        JIconButton<AssetDirection> myDirectionButton = new JIconButton<>(theDirectionState);
        MoneyWiseIcons.buildDirectionButton(theDirectionState);

        /* Create reconciled button */
        JIconButton<Boolean> myReconciledButton = new JIconButton<>(theReconciledState);
        MoneyWiseIcons.buildReconciledButton(theReconciledState);

        /* Allocate fields */
        JTextField myAmount = new JTextField();

        /* restrict the fields */
        restrictField(theDateButton, Transaction.DESCLEN);
        restrictField(theAccountButton, Transaction.DESCLEN);
        restrictField(theCategoryButton, Transaction.DESCLEN);
        restrictField(myDirectionButton, Transaction.DESCLEN);
        restrictField(thePartnerButton, Transaction.DESCLEN);
        restrictField(myAmount, Transaction.DESCLEN);
        restrictField(myReconciledButton, Transaction.DESCLEN);

        /* Declare fields */
        theFieldSet.addFieldElement(Transaction.FIELD_DATE, MetisDataType.DATE, theDateButton);
        theFieldSet.addFieldElement(Transaction.FIELD_ACCOUNT, TransactionAsset.class, theAccountButton);
        theFieldSet.addFieldElement(Transaction.FIELD_CATEGORY, TransactionCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Transaction.FIELD_DIRECTION, AssetDirection.class, myDirectionButton);
        theFieldSet.addFieldElement(Transaction.FIELD_PARTNER, TransactionAsset.class, thePartnerButton);
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

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build info subPanel.
     * @return the panel
     */
    private JPanel buildInfoPanel() {
        /* Allocate fields */
        JTextField myComments = new JTextField();
        JTextField myReference = new JTextField();

        /* Allocate fields */
        JTextField myAmount = new JTextField();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myAmount, myWidth);
        restrictField(myComments, myWidth);
        restrictField(myReference, myWidth);
        restrictField(theTagButton, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT), MetisDataType.MONEY, myAmount);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), MetisDataType.STRING, myComments);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), MetisDataType.STRING, myReference);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), theTagButton);

        /* Create the Tax panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build tax subPanel.
     * @return the panel
     */
    private JPanel buildTaxPanel() {
        /* Allocate fields */
        JTextField myTaxCredit = new JTextField();
        JTextField myNatIns = new JTextField();
        JTextField myBenefit = new JTextField();
        JTextField myDonation = new JTextField();
        JTextField myYears = new JTextField();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myTaxCredit, myWidth);
        restrictField(myNatIns, myWidth);
        restrictField(myBenefit, myWidth);
        restrictField(myDonation, myWidth);
        restrictField(myYears, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), MetisDataType.MONEY, myTaxCredit);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE), MetisDataType.MONEY, myNatIns);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), MetisDataType.MONEY, myBenefit);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION), MetisDataType.MONEY, myDonation);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), MetisDataType.INTEGER, myYears);

        /* Create the Tax panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the Tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build securities subPanel.
     * @return the panel
     */
    private JPanel buildSecuritiesPanel() {
        /* Allocate fields */
        JTextField myCredUnits = new JTextField();
        JTextField myDebUnits = new JTextField();
        JTextField myDilution = new JTextField();
        JTextField myThirdPartyAmount = new JTextField();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myCredUnits, myWidth);
        restrictField(myDebUnits, myWidth);
        restrictField(myDilution, myWidth);
        restrictField(myThirdPartyAmount, myWidth);
        restrictField(theThirdPartyButton, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS), MetisDataType.UNITS, myCredUnits);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS), MetisDataType.UNITS, myDebUnits);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), MetisDataType.DILUTION, myDilution);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTY), Deposit.class, theThirdPartyButton);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTYAMOUNT), MetisDataType.MONEY, myThirdPartyAmount);

        /* Create the Tax panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTY), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTYAMOUNT), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        /* Update Date button */
        theDateButton.setEarliestDateDay(pRange != null
                                                        ? pRange.getStart()
                                                        : null);
        theDateButton.setLatestDateDay(pRange != null
                                                      ? pRange.getEnd()
                                                      : null);

        /* update the tagMenuBuilder */
        updateTagMenuBuilder(theTagMenuBuilder);
    }

    /**
     * Update tag menuBuilder.
     * @param pBuilder the menu builder
     */
    public void updateTagMenuBuilder(final JScrollListMenuBuilder<TransactionTag> pBuilder) {
        /* Access TransactionTags */
        TransactionTagList myTags = getDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);
        pBuilder.clearAvailableItems();

        /* Loop through the tags */
        Iterator<TransactionTag> myIterator = myTags.iterator();
        while (myIterator.hasNext()) {
            TransactionTag myTag = myIterator.next();

            /* If the tag is not deleted */
            if (!myTag.isDeleted()) {
                /* Add item to the tag list */
                pBuilder.setAvailableItem(myTag);
            }
        }
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

        /* Update text for tag button */
        if (bShowField) {
            theTagButton.setText(myTrans.getTagNameList());
        }

        /* Determine whether the partnerAmount field should be visible */
        MetisField myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT);
        boolean bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PARTNERAMOUNT);
        bShowField = bEditField || myTrans.getPartnerAmount() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myTrans.getPartner().getCurrency());

        /* Determine whether the taxCredit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.TAXCREDIT);
        bShowField = bEditField || myTrans.getTaxCredit() != null;
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
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.CHARITYDONATION);
        bShowField = bEditField || myTrans.getCharityDonation() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Determine whether the creditUnits field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.CREDITUNITS);
        bShowField = bEditField || myTrans.getCreditUnits() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the debit units field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DEBITUNITS);
        bShowField = bEditField || myTrans.getDebitUnits() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the dilution field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DILUTION);
        bShowField = bEditField || myTrans.getDilution() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the thirdParty field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTY);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.THIRDPARTY);
        bShowField = bEditField || myTrans.getThirdParty() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the thirdPartyAmount field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTYAMOUNT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.THIRDPARTYAMOUNT);
        bShowField = bEditField || myTrans.getThirdPartyAmount() != null;
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
        theReconciledState.setState(!bIsLocked);
        theDirectionState.setState(!bIsReconciled);
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
        TethysDateRange myRange = theBuilder.getRange();
        TethysSwingDateConfig myConfig = theDateButton.getDateConfig();
        myConfig.setEarliestDateDay(myRange.getStart());
        myConfig.setLatestDateDay(myRange.getEnd());
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
                case TRANSTAG:
                    updateTag(myTrans, pUpdate.getEvent());
                    break;
                case PARTNERAMOUNT:
                    myTrans.setPartnerAmount(pUpdate.getMoney());
                    break;
                case CREDITUNITS:
                    myTrans.setCreditUnits(pUpdate.getUnits());
                    break;
                case DEBITUNITS:
                    myTrans.setDebitUnits(pUpdate.getUnits());
                    break;
                case DILUTION:
                    myTrans.setDilution(pUpdate.getDilution());
                    break;
                case QUALIFYYEARS:
                    myTrans.setYears(pUpdate.getInteger());
                    break;
                case THIRDPARTY:
                    myTrans.setThirdParty(pUpdate.getValue(Deposit.class));
                    break;
                case THIRDPARTYAMOUNT:
                    myTrans.setThirdPartyAmount(pUpdate.getMoney());
                    break;
                case TAXCREDIT:
                    myTrans.setTaxCredit(pUpdate.getMoney());
                    break;
                case NATINSURANCE:
                    myTrans.setNatInsurance(pUpdate.getMoney());
                    break;
                case DEEMEDBENEFIT:
                    myTrans.setBenefit(pUpdate.getMoney());
                    break;
                case CHARITYDONATION:
                    myTrans.setDonation(pUpdate.getMoney());
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
            declareGoToItem(myItem.getThirdParty());
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
     * Build the account list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildAccountMenu(final JScrollMenuBuilder<TransactionAsset> pMenuBuilder,
                                 final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Add possible items */
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class), true, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.CASH, CashList.class), true, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.LOAN, LoanList.class), true, pTrans);
        buildHoldingMenu(pMenuBuilder, true, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class), true, pTrans);
    }

    /**
     * Build the partner list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildPartnerMenu(final JScrollMenuBuilder<TransactionAsset> pMenuBuilder,
                                 final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Add possible items */
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class), false, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.CASH, CashList.class), false, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.LOAN, LoanList.class), false, pTrans);
        buildHoldingMenu(pMenuBuilder, false, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class), false, pTrans);
        buildAssetMenu(pMenuBuilder, getDataList(MoneyWiseDataType.PAYEE, PayeeList.class), false, pTrans);
    }

    /**
     * Build the asset list for an item.
     * @param <T> the Asset type
     * @param pMenuBuilder the menu builder
     * @param pIsAccount is this item the account rather than partner
     * @param pList the asset list
     * @param pTrans the transaction to build for
     */
    private static <T extends AssetBase<T>> void buildAssetMenu(final JScrollMenuBuilder<TransactionAsset> pMenuBuilder,
                                                                final AssetBaseList<T> pList,
                                                                final boolean pIsAccount,
                                                                final Transaction pTrans) {
        /* Record active item */
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionCategory myCategory = pTrans.getCategory();
        TransactionAsset myCurr = pIsAccount
                                             ? myAccount
                                             : pTrans.getPartner();
        JMenuItem myActive = null;
        JScrollMenu myMenu = null;

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
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenuBuilder.addSubMenu(pList.getItemType().getItemName());
            }

            /* Create a new JMenuItem and add it to the popUp */
            JMenuItem myItem = pMenuBuilder.addItem(myMenu, myAsset);

            /* If this is the active category */
            if (myAsset.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the asset list for an item.
     * @param pMenuBuilder the menu builder
     * @param pIsAccount is this item the account rather than partner
     * @param pTrans the transaction to build for
     */
    private void buildHoldingMenu(final JScrollMenuBuilder<TransactionAsset> pMenuBuilder,
                                  final boolean pIsAccount,
                                  final Transaction pTrans) {
        /* Record active item */
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionCategory myCategory = pTrans.getCategory();
        TransactionAsset myCurr = pIsAccount
                                             ? myAccount
                                             : pTrans.getPartner();
        JMenuItem myActive = null;
        JScrollMenu myMenu = null;

        /* Access Portfolios and Holdings Map */
        MoneyWiseData myData = pTrans.getDataSet();
        PortfolioList myPortfolios = myData.getPortfolios();
        SecurityHoldingMap myMap = myData.getSecurityHoldingsMap();

        /* Loop through the Portfolios */
        Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            Portfolio myPortfolio = myPortIterator.next();
            JScrollMenu myCoreMenu = null;

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
                            myMenu = pMenuBuilder.addSubMenu(AssetType.SECURITYHOLDING.toString());
                        }
                        if (myCoreMenu == null) {
                            /* Create a new JMenu and add it to the popUp */
                            myCoreMenu = pMenuBuilder.addSubMenu(myMenu, myPortfolio.getName());
                        }

                        /* Add the item to the menu */
                        JMenuItem myItem = pMenuBuilder.addItem(myCoreMenu, myHolding, mySecurity.getName());

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
                    JScrollMenu mySubMenu = null;
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
                            /* Create a new JMenu and add it to the popUp */
                            myMenu = pMenuBuilder.addSubMenu(AssetType.SECURITYHOLDING.toString());
                        }
                        if (myCoreMenu == null) {
                            /* Create a new JMenu and add it to the popUp */
                            myCoreMenu = pMenuBuilder.addSubMenu(myMenu, myPortfolio.getName());
                        }
                        if (mySubMenu == null) {
                            /* Create a new subMenu */
                            mySubMenu = pMenuBuilder.addSubMenu(myCoreMenu, SecurityHolding.SECURITYHOLDING_NEW);
                        }

                        /* Add the item to the menu */
                        pMenuBuilder.addItem(mySubMenu, myHolding, mySecurity.getName());
                    }
                }
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the category list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildCategoryMenu(final JScrollMenuBuilder<TransactionCategory> pMenuBuilder,
                                  final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionCategory myCurr = pTrans.getCategory();
        JMenuItem myActive = null;
        JMenuItem myItem;

        /* Create a simple map for top-level categories */
        Map<String, JScrollMenu> myMap = new HashMap<>();

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
                JScrollMenu myMenu = myMap.get(myParentName);

                /* If this is a new menu */
                if (myMenu == null) {
                    /* Create a new JMenu and add it to the popUp */
                    myMenu = pMenuBuilder.addSubMenu(myParentName);
                    myMap.put(myParentName, myMenu);
                }

                /* Create a new JMenuItem and add it to the subMenu */
                myItem = pMenuBuilder.addItem(myMenu, myCategory, myCategory.getSubCategory());

            } else {
                /* Create a new JMenuItem and add it to the popUp */
                myItem = pMenuBuilder.addItem(myCategory);
            }

            /* If this is the active category */
            if (myCategory.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the ThirdParty list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildThirdPartyMenu(final JScrollMenuBuilder<Deposit> pMenuBuilder,
                                    final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        Deposit myCurr = pTrans.getThirdParty();
        JMenuItem myActive = null;

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

            /* Create a new action for the portfolio */
            JMenuItem myItem = pMenuBuilder.addItem(myDeposit);

            /* If this is the active thirdParty */
            if (myDeposit.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the active Tag list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildTagMenu(final JScrollListMenuBuilder<TransactionTag> pMenuBuilder,
                             final Transaction pTrans) {
        /* Clear the menu of selected items */
        pMenuBuilder.clearAllSelected();

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
                    pMenuBuilder.setSelectedItem(myTag);
                }
            }
        }
    }

    /**
     * Update the Tag list for an item.
     * @param pTrans the transaction to build for
     * @param pEvent the item event
     * @throws OceanusException on error
     */
    public void updateTag(final Transaction pTrans,
                          final TethysEvent<TethysUIEvent> pEvent) throws OceanusException {
        /* Determine whether this is a select or not */
        @SuppressWarnings("unchecked")
        ToggleState<TransactionTag> myState = (ToggleState<TransactionTag>) pEvent.getDetails(ToggleState.class);
        boolean bSelected = myState.isSelected();

        /* Access the TransactionTag */
        TransactionTag myTag = myState.getItem();

        /* Update transaction tag status */
        if (bSelected) {
            pTrans.setTransactionTag(myTag);
        } else {
            pTrans.clearTransactionTag(myTag);
        }
    }
}
