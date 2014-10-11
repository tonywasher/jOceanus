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
import java.awt.event.ItemEvent;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollListButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollListButton.JScrollListMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Transaction.
 */
public class TransactionPanel
        extends MoneyWiseDataItemPanel<Transaction> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5109090549472976745L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Transaction> theFieldSet;

    /**
     * Info Tab Title.
     */
    private static final String TAB_INFO = MoneyWiseUIControlResource.TRANSPANEL_TAB_INFO.getValue();

    /**
     * Tax Tab Title.
     */
    private static final String TAB_TAXES = MoneyWiseUIControlResource.TRANSPANEL_TAB_TAXES.getValue();

    /**
     * Securities Tab Title.
     */
    private static final String TAB_SECURITIES = MoneyWiseUIControlResource.TRANSPANEL_TAB_SECURITIES.getValue();

    /**
     * Date Button.
     */
    private final JDateDayButton theDateButton;

    /**
     * Debit Button Field.
     */
    private final JScrollButton<Object> theDebitButton;

    /**
     * Credit Button Field.
     */
    private final JScrollButton<Object> theCreditButton;

    /**
     * Category Button Field.
     */
    private final JScrollButton<TransactionCategory> theCategoryButton;

    /**
     * Portfolio Button Field.
     */
    private final JScrollButton<Portfolio> thePortfolioButton;

    /**
     * ThirdParty Button Field.
     */
    private final JScrollButton<Deposit> theThirdPartyButton;

    /**
     * TransactionTag Button Field.
     */
    private final JScrollListButton<TransactionTag> theTagButton;

    /**
     * The Tag Menu Builder.
     */
    private final transient JScrollListMenuBuilder<TransactionTag> theTagMenuBuilder;

    /**
     * Reconciled Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theReconciledState;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TransactionPanel(final JFieldManager pFieldMgr,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theDateButton = new JDateDayButton();

        /* Create the buttons */
        theDebitButton = new JScrollButton<Object>();
        theCreditButton = new JScrollButton<Object>();
        theCategoryButton = new JScrollButton<TransactionCategory>();
        theTagButton = new JScrollListButton<TransactionTag>();
        thePortfolioButton = new JScrollButton<Portfolio>();
        theThirdPartyButton = new JScrollButton<Deposit>();

        /* Access tag menu builder */
        theTagMenuBuilder = theTagButton.getMenuBuilder();

        /* Set closed button */
        theReconciledState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

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

        /* Create the listener */
        new TransactionListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Set states */
        JIconButton<Boolean> myReconciledButton = new JIconButton<Boolean>(theReconciledState);
        MoneyWiseIcons.buildReconciledButton(theReconciledState);

        /* Allocate fields */
        JTextField myAmount = new JTextField();

        /* restrict the fields */
        restrictField(theDateButton, Transaction.DESCLEN);
        restrictField(theDebitButton, Transaction.DESCLEN);
        restrictField(theCreditButton, Transaction.DESCLEN);
        restrictField(theCategoryButton, Transaction.DESCLEN);
        restrictField(myAmount, Transaction.DESCLEN);
        restrictField(myReconciledButton, Transaction.DESCLEN);

        /* Declare fields */
        theFieldSet.addFieldElement(Transaction.FIELD_DATE, DataType.DATEDAY, theDateButton);
        theFieldSet.addFieldElement(Transaction.FIELD_DEBIT, Object.class, theDebitButton);
        theFieldSet.addFieldElement(Transaction.FIELD_CREDIT, Object.class, theCreditButton);
        theFieldSet.addFieldElement(Transaction.FIELD_CATEGORY, TransactionCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Transaction.FIELD_AMOUNT, DataType.MONEY, myAmount);
        theFieldSet.addFieldElement(Transaction.FIELD_RECONCILED, Boolean.class, myReconciledButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Transaction.FIELD_DATE, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_DEBIT, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_CREDIT, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_AMOUNT, myPanel);
        theFieldSet.addFieldToPanel(Transaction.FIELD_RECONCILED, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myComments, myWidth);
        restrictField(myReference, myWidth);
        restrictField(theTagButton, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), DataType.STRING, myComments);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), DataType.STRING, myReference);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), TransactionTag.class, theTagButton);

        /* Create the Tax panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(myTaxCredit, myWidth);
        restrictField(myNatIns, myWidth);
        restrictField(myBenefit, myWidth);
        restrictField(myDonation, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), DataType.MONEY, myTaxCredit);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE), DataType.MONEY, myNatIns);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), DataType.MONEY, myBenefit);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION), DataType.MONEY, myDonation);

        /* Create the Tax panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the Tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        JTextField myYears = new JTextField();

        /* Restrict the fields */
        int myWidth = Transaction.DESCLEN >> 1;
        restrictField(thePortfolioButton, myWidth);
        restrictField(myCredUnits, myWidth);
        restrictField(myDebUnits, myWidth);
        restrictField(myDilution, myWidth);
        restrictField(theThirdPartyButton, myWidth);
        restrictField(myYears, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PORTFOLIO), Portfolio.class, thePortfolioButton);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS), DataType.UNITS, myCredUnits);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS), DataType.UNITS, myDebUnits);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), DataType.DILUTION, myDilution);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTY), Deposit.class, theThirdPartyButton);
        theFieldSet.addFieldElement(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), DataType.INTEGER, myYears);

        /* Create the Tax panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the tax panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PORTFOLIO), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTY), myPanel);
        theFieldSet.addFieldToPanel(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Transaction myItem = getItem();
        if (myItem != null) {
            TransactionList myTrans = findDataList(MoneyWiseDataType.TRANSACTION, TransactionList.class);
            setItem(myTrans.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    /**
     * Update editors.
     * @param pRange the date range.
     */
    public void updateEditors(final JDateDayRange pRange) {
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
        TransactionTagList myTags = findDataList(MoneyWiseDataType.TRANSTAG, TransactionTagList.class);
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
    protected boolean isDeletable() {
        return !getItem().isReconciled();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Transaction myTrans = getItem();
        boolean bIsReconciled = myTrans.isReconciled();
        boolean bIsLocked = myTrans.isLocked();

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

        /* Determine whether the taxCredit field should be visible */
        JDataField myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT);
        boolean bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.TAXCREDIT);
        bShowField = bEditField || myTrans.getTaxCredit() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the natIns field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.NATINSURANCE);
        bShowField = bEditField || myTrans.getNatInsurance() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the benefit field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.DEEMEDBENEFIT);
        bShowField = bEditField || myTrans.getDeemedBenefit() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the donation field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.CHARITYDONATION);
        bShowField = bEditField || myTrans.getCharityDonation() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the portfolio field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.PORTFOLIO);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.PORTFOLIO);
        bShowField = bEditField || myTrans.getPortfolio() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

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

        /* Determine whether the years field should be visible */
        myField = TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS);
        bEditField = isEditable && isEditableField(myTrans, TransactionInfoClass.QUALIFYYEARS);
        bShowField = bEditField || myTrans.getYears() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the reconciled field should be visible */
        boolean bShowReconciled = isEditable || bIsReconciled;
        theReconciledState.setState(!bIsLocked);
        theFieldSet.setVisibility(Transaction.FIELD_RECONCILED, bShowReconciled);
        theFieldSet.setEditable(Transaction.FIELD_RECONCILED, isEditable && !bIsLocked);
        theFieldSet.setEditable(Transaction.FIELD_AMOUNT, isEditable && !bIsReconciled);
        theFieldSet.setEditable(Transaction.FIELD_DEBIT, isEditable && !bIsReconciled);
        theFieldSet.setEditable(Transaction.FIELD_CREDIT, isEditable && !bIsReconciled);
        theFieldSet.setEditable(Transaction.FIELD_CATEGORY, isEditable && !bIsReconciled);
        theFieldSet.setEditable(Transaction.FIELD_DATE, isEditable && !bIsReconciled);
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
        JDataFieldRequired isRequired = myInfoSet.isClassRequired(pField);
        return !isRequired.equals(JDataFieldRequired.NOTALLOWED);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Transaction myTrans = getItem();

        /* Process updates */
        if (myField.equals(Transaction.FIELD_DATE)) {
            /* Update the Date */
            myTrans.setDate(pUpdate.getDateDay());
        } else if (myField.equals(Transaction.FIELD_AMOUNT)) {
            /* Update the Amount */
            myTrans.setAmount(pUpdate.getMoney());
        } else if (myField.equals(Transaction.FIELD_DEBIT)) {
            /* Update the Debit */
            myTrans.setDebit(pUpdate.getValue(AssetBase.class));
        } else if (myField.equals(Transaction.FIELD_CREDIT)) {
            /* Update the Credit */
            myTrans.setCredit(pUpdate.getValue(AssetBase.class));
        } else if (myField.equals(Transaction.FIELD_CATEGORY)) {
            /* Update the Category */
            myTrans.setCategory(pUpdate.getValue(TransactionCategory.class));
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
                    updateTag(myTrans, pUpdate.getItemEvent());
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
                case PORTFOLIO:
                    myTrans.setPortfolio(pUpdate.getValue(Portfolio.class));
                    break;
                case THIRDPARTY:
                    myTrans.setThirdParty(pUpdate.getValue(Deposit.class));
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
    protected void buildGoToMenu() {
        Transaction myItem = getItem();
        if (!getUpdateSet().hasUpdates()) {
            buildGoToEvent(myItem.getDebit());
            buildGoToEvent(myItem.getCredit());
            buildGoToEvent(myItem.getCategory());
            Portfolio myPortfolio = myItem.getPortfolio();
            if (myPortfolio != null) {
                buildGoToEvent(myPortfolio);
            }
            Deposit myThirdParty = myItem.getThirdParty();
            if (myThirdParty != null) {
                buildGoToEvent(myThirdParty);
            }
        }
    }

    /**
     * Build the debit list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildDebitMenu(final JScrollMenuBuilder<Object> pMenuBuilder,
                               final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();
    }

    /**
     * Build the credit list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildCreditMenu(final JScrollMenuBuilder<Object> pMenuBuilder,
                                final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();
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
    }

    /**
     * Build the portfolio list for an item.
     * @param pMenuBuilder the menu builder
     * @param pTrans the transaction to build for
     */
    public void buildPortfolioMenu(final JScrollMenuBuilder<Portfolio> pMenuBuilder,
                                   final Transaction pTrans) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        Portfolio myCurr = pTrans.getPortfolio();
        JMenuItem myActive = null;

        /* Access Portfolios */
        PortfolioList myPortfolios = findDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);

        /* Loop through the Portfolios */
        Iterator<Portfolio> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            Portfolio myPortfolio = myIterator.next();

            /* Ignore deleted or closed */
            boolean bIgnore = myPortfolio.isDeleted() || myPortfolio.isClosed();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the portfolio */
            JMenuItem myItem = pMenuBuilder.addItem(myPortfolio);

            /* If this is the active portfolio */
            if (myPortfolio.equals(myCurr)) {
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
        DepositList myDeposits = findDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);

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
     * @throws JOceanusException on error
     */
    public void updateTag(final Transaction pTrans,
                          final ItemEvent pEvent) throws JOceanusException {
        /* Determine whether this is a select or not */
        boolean bSelected = pEvent.getStateChange() == ItemEvent.SELECTED;

        /* Access the TransactionTag */
        Object myItem = pEvent.getItem();
        if (myItem instanceof TransactionTag) {
            TransactionTag myTag = (TransactionTag) myItem;

            /* Update transaction tag status */
            if (bSelected) {
                pTrans.setTransactionTag(myTag);
            } else {
                pTrans.clearTransactionTag(myTag);
            }
        }
    }

    /**
     * Transaction Listener.
     */
    private final class TransactionListener
            implements ChangeListener {
        /**
         * The Debit Menu Builder.
         */
        private final JScrollMenuBuilder<Object> theDebitMenuBuilder;

        /**
         * The Credit Menu Builder.
         */
        private final JScrollMenuBuilder<Object> theCreditMenuBuilder;

        /**
         * The Category Menu Builder.
         */
        private final JScrollMenuBuilder<TransactionCategory> theCategoryMenuBuilder;

        /**
         * The Portfolio Menu Builder.
         */
        private final JScrollMenuBuilder<Portfolio> thePortMenuBuilder;

        /**
         * The ThirdParty Menu Builder.
         */
        private final JScrollMenuBuilder<Deposit> theThirdPartyMenuBuilder;

        /**
         * Constructor.
         */
        private TransactionListener() {
            /* Access the MenuBuilders */
            theDebitMenuBuilder = theDebitButton.getMenuBuilder();
            theDebitMenuBuilder.addChangeListener(this);
            theCreditMenuBuilder = theCreditButton.getMenuBuilder();
            theCreditMenuBuilder.addChangeListener(this);
            theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            thePortMenuBuilder = thePortfolioButton.getMenuBuilder();
            thePortMenuBuilder.addChangeListener(this);
            theThirdPartyMenuBuilder = theThirdPartyButton.getMenuBuilder();
            theThirdPartyMenuBuilder.addChangeListener(this);
            theTagMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theDebitMenuBuilder.equals(o)) {
                buildDebitMenu(theDebitMenuBuilder, getItem());
            } else if (theCreditMenuBuilder.equals(o)) {
                buildCreditMenu(theCreditMenuBuilder, getItem());
            } else if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu(theCategoryMenuBuilder, getItem());
            } else if (thePortMenuBuilder.equals(o)) {
                buildPortfolioMenu(thePortMenuBuilder, getItem());
            } else if (theThirdPartyMenuBuilder.equals(o)) {
                buildThirdPartyMenu(theThirdPartyMenuBuilder, getItem());
            } else if (theTagMenuBuilder.equals(o)) {
                buildTagMenu(theTagMenuBuilder, getItem());
            }
        }
    }
}
