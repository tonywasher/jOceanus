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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Schedule.ScheduleList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * New version of Event DataItem utilising EventInfo.
 * @author Tony Washer
 */
public class Transaction
        extends TransactionBase<Transaction>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSACTION.getItemName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSACTION.getListName();

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, TransactionBase.FIELD_DEFS);

    /**
     * EventInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Early Date Error Text.
     */
    private static final String ERROR_BADDATE = MoneyWiseDataResource.TRANSACTION_ERROR_BADPRICEDATE.getValue();

    /**
     * Circular update Error Text.
     */
    private static final String ERROR_CIRCULAR = MoneyWiseDataResource.TRANSACTION_ERROR_CIRCLE.getValue();

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet
                             : JDataFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        TransactionInfoClass myClass = TransactionInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * TransactionInfoSet.
     */
    private final TransactionInfoSet theInfoSet;

    @Override
    public TransactionInfoSet getInfoSet() {
        return theInfoSet;
    }

    @Override
    public Transaction getParent() {
        return (Transaction) super.getParent();
    }

    @Override
    public Iterator<Transaction> childIterator() {
        /* No iterator if we are not a group or else we are a child */
        if (!isSplit() || isChild()) {
            return null;
        }

        return getList().getGroup(this).iterator();
    }

    /**
     * Obtain Debit Units.
     * @return the Debit Units
     */
    public final JUnits getDebitUnits() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.DEBITUNITS, JUnits.class)
                         : null;
    }

    /**
     * Obtain Credit Units.
     * @return the Credit Units
     */
    public final JUnits getCreditUnits() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.CREDITUNITS, JUnits.class)
                         : null;
    }

    /**
     * Obtain Tax Credit.
     * @return the Tax Credit
     */
    public final JMoney getTaxCredit() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.TAXCREDIT, JMoney.class)
                         : null;
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public final JDilution getDilution() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.DILUTION, JDilution.class)
                         : null;
    }

    /**
     * Obtain Qualifying Years.
     * @return the Years
     */
    public final Integer getYears() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.QUALIFYYEARS, Integer.class)
                         : null;
    }

    /**
     * Obtain credit date.
     * @return the credit date
     */
    public final JDateDay getCreditDate() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.CREDITDATE, JDateDay.class)
                         : null;
    }

    /**
     * Obtain National Insurance.
     * @return the NatInsurance
     */
    public final JMoney getNatInsurance() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.NATINSURANCE, JMoney.class)
                         : null;
    }

    /**
     * Obtain Deemed Benefit.
     * @return the Benefit
     */
    public final JMoney getDeemedBenefit() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.DEEMEDBENEFIT, JMoney.class)
                         : null;
    }

    /**
     * Obtain Pension.
     * @return the Pension
     */
    public final JMoney getPension() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.PENSION, JMoney.class)
                         : null;
    }

    /**
     * Obtain Donation.
     * @return the Donation
     */
    public final JMoney getCharityDonation() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.CHARITYDONATION, JMoney.class)
                         : null;
    }

    /**
     * Obtain Reference.
     * @return the Reference
     */
    public final String getReference() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.REFERENCE, String.class)
                         : null;
    }

    /**
     * Obtain Comments.
     * @return the Comments
     */
    public final String getComments() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.COMMENTS, String.class)
                         : null;
    }

    /**
     * Obtain ThirdParty.
     * @return the ThirdParty
     */
    public final Deposit getThirdParty() {
        return hasInfoSet
                         ? theInfoSet.getDeposit(TransactionInfoClass.THIRDPARTY)
                         : null;
    }

    /**
     * Obtain Portfolio.
     * @return the Portfolio
     */
    public final Portfolio getPortfolio() {
        return hasInfoSet
                         ? theInfoSet.getPortfolio(TransactionInfoClass.PORTFOLIO)
                         : null;
    }

    /**
     * Obtain Credit Amount.
     * @return the Credit Amount
     */
    public final JMoney getCreditAmount() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.CREDITAMOUNT, JMoney.class)
                         : null;
    }

    /**
     * Obtain the nameList for Transaction tags.
     * @return the list (or null if no tags)
     */
    public String getTagNameList() {
        return hasInfoSet
                         ? theInfoSet.getNameList(TransactionInfoClass.TRANSTAG)
                         : null;
    }

    /**
     * Obtain the iterator for Transaction tags.
     * @return the iterator (or null if no tags)
     */
    public Iterator<TransactionInfo> tagIterator() {
        return hasInfoSet
                         ? theInfoSet.linkIterator(TransactionInfoClass.TRANSTAG)
                         : null;
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN) && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public EditState getEditState() {
        /* Pop history for self */
        EditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == EditState.CLEAN) && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getEditState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public boolean hasHistory() {
        /* Check for history for self */
        boolean hasHistory = super.hasHistory();

        /* If we should use the InfoSet */
        if ((!hasHistory) && (useInfoSet)) {
            /* Check history for infoSet */
            hasHistory = theInfoSet.hasHistory();
        }

        /* Return details */
        return hasHistory;
    }

    @Override
    public void pushHistory() {
        /* Push history for self */
        super.pushHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Push history for infoSet */
            theInfoSet.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Pop history for self */
        super.popHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Pop history for infoSet */
            theInfoSet.popHistory();
        }
    }

    @Override
    public boolean checkForHistory() {
        /* Check for history for self */
        boolean bChanges = super.checkForHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Check for history for infoSet */
            bChanges |= theInfoSet.checkForHistory();
        }

        /* return result */
        return bChanges;
    }

    @Override
    public Difference fieldChanged(final JDataField pField) {
        /* Handle InfoSet fields */
        TransactionInfoClass myClass = TransactionInfoSet.getClassForField(pField);
        if (myClass != null) {
            return (useInfoSet)
                               ? theInfoSet.fieldChanged(myClass)
                               : Difference.IDENTICAL;
        }

        /* Check super fields */
        return super.fieldChanged(pField);
    }

    @Override
    public void setDeleted(final boolean bDeleted) {
        /* Pass call to infoSet if required */
        if (useInfoSet) {
            theInfoSet.setDeleted(bDeleted);
        }

        /* Pass call onwards */
        super.setDeleted(bDeleted);
    }

    @Override
    public boolean isLocked() {
        /* Check credit/debit */
        if (super.isLocked()) {
            return true;
        }

        /* Check Portfolio */
        Portfolio myPortfolio = getPortfolio();
        if ((myPortfolio != null) && myPortfolio.isClosed()) {
            return true;
        }

        /* Check ThirdParty */
        Deposit myThirdParty = getThirdParty();
        return (myThirdParty != null) && myThirdParty.isClosed();
    }

    @Override
    public Transaction getBase() {
        return (Transaction) super.getBase();
    }

    @Override
    public TransactionList getList() {
        return (TransactionList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the transaction list
     * @param pTransaction The Transaction to copy
     */
    public Transaction(final TransactionList pList,
                       final Transaction pTransaction) {
        /* Set standard values */
        super(pList, pTransaction);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new TransactionInfoSet(this, pList.getEventInfoTypes(), pList.getTransactionInfo());
                theInfoSet.cloneDataInfoSet(pTransaction.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new TransactionInfoSet(this, pList.getEventInfoTypes(), pList.getTransactionInfo());
                hasInfoSet = true;
                useInfoSet = false;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                useInfoSet = false;
                break;
        }
    }

    /**
     * Construct a new transaction from a Schedule.
     * @param pList the list to build into
     * @param pSchedule The schedule to copy
     * @throws JOceanusException on error
     */
    protected Transaction(final TransactionList pList,
                          final Schedule pSchedule) throws JOceanusException {
        /* Set standard values */
        super(pList);

        /* Copy underlying values */
        setDate(pSchedule.getDate());
        setValueAssetPair(pSchedule.getAssetPair());
        setValueAccount(pSchedule.getAccount());
        setValuePartner(pSchedule.getPartner());
        setCategory(pSchedule.getCategory());
        setReconciled(Boolean.FALSE);
        setSplit(Boolean.FALSE);
        setValueAmount(pSchedule.getAmountField());

        /* Set up info set */
        theInfoSet = new TransactionInfoSet(this, pList.getEventInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = true;

        /* If we need a tax Credit */
        if (TransactionInfoSet.isTaxCreditClassRequired(getAccount(),
                null, getCategory().getCategoryTypeClass()) == JDataFieldRequired.MUSTEXIST) {
            /* Calculate the tax credit */
            setTaxCredit(calculateTaxCredit());
        }
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    public Transaction(final TransactionList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new TransactionInfoSet(this, pList.getEventInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private Transaction(final TransactionList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new TransactionInfoSet(this, pList.getEventInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Transaction details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        resolveDataLink(FIELD_PARENT, getList());

        /* Sort linkSets */
        if (hasInfoSet) {
            theInfoSet.sortLinkSets();
        }
    }

    /**
     * Determine if an infoSet class is required.
     * @param pClass the infoSet class
     * @return the status
     */
    public JDataFieldRequired isClassRequired(final TransactionInfoClass pClass) {
        /* Check the class */
        return theInfoSet.isClassRequired(pClass);
    }

    /**
     * Validate the transaction.
     */
    @Override
    public void validate() {
        TransactionAsset myAccount = getAccount();
        TransactionAsset myPartner = getPartner();
        TransactionCategory myCategory = getCategory();
        JUnits myDebitUnits = getDebitUnits();
        JUnits myCreditUnits = getCreditUnits();

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Perform underlying checks */
        super.validate();

        /* If the event has a parent */
        Transaction myParent = getParent();
        if (myParent != null) {
            /* Register child against parent */
            getList().registerChild(this);
        }

        /* Check for valid account security */
        if ((myAccount != null) && (myAccount instanceof Security)) {
            /* If the date of this transaction is prior to the first price */
            SecurityPrice myPrice = ((Security) myAccount).getInitialPrice();
            if ((myPrice != null) && (getDate().compareTo(myPrice.getDate()) < 0)) {
                addError(ERROR_BADDATE, FIELD_ACCOUNT);
            }
        }

        /* Check for valid partner security */
        if ((myPartner != null) && (myPartner instanceof Security) && (!Difference.isEqual(myAccount, myPartner))) {
            /* If the date of this transaction is prior to the first price */
            SecurityPrice myPrice = ((Security) myPartner).getInitialPrice();
            if ((myPrice != null) && (getDate().compareTo(myPrice.getDate()) < 0)) {
                addError(ERROR_BADDATE, FIELD_PARTNER);
            }
        }

        /* Cannot have Credit and Debit if securities are identical */
        if ((myCreditUnits != null) && (myDebitUnits != null) && (Difference.isEqual(myAccount, myPartner))) {
            addError(ERROR_CIRCULAR, TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS));
        }

        /* If we have a category and an infoSet */
        if ((myCategory != null) && (theInfoSet != null)) {
            /* Validate the InfoSet */
            theInfoSet.validate();
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Calculate the tax credit for a transaction.
     * @return the calculated tax credit
     */
    public final JMoney calculateTaxCredit() {
        MoneyWiseData myData = getDataSet();
        TaxYearList myList = myData.getTaxYears();

        /* Ignore unless tax credit is null/zero */
        if ((getTaxCredit() != null) && (getTaxCredit().isNonZero())) {
            return getTaxCredit();
        }

        /* Ignore unless category is interest/dividend */
        if ((getCategory() == null) || ((!isInterest()) && (!isDividend()))) {
            return getTaxCredit();
        }

        /* Access the relevant tax year */
        TaxYear myTax = myList.findTaxYearForDate(getDate());

        /* Determine the tax credit rate */
        JRate myRate = (isInterest())
                                     ? myTax.getIntTaxRate()
                                     : myTax.getDivTaxRate();

        /* Calculate the tax credit */
        return getAmount().taxCreditAtRate(myRate);
    }

    /**
     * Set a new Debit Units.
     * @param pUnits the new units
     * @throws JOceanusException on error
     */
    public final void setDebitUnits(final JUnits pUnits) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.DEBITUNITS, pUnits);
    }

    /**
     * Set a new Credit Units.
     * @param pUnits the new units
     * @throws JOceanusException on error
     */
    public final void setCreditUnits(final JUnits pUnits) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.CREDITUNITS, pUnits);
    }

    /**
     * Set a new TaxCredit.
     * @param pCredit the new credit
     * @throws JOceanusException on error
     */
    public final void setTaxCredit(final JMoney pCredit) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.TAXCREDIT, pCredit);
    }

    /**
     * Set a new Dilution.
     * @param pDilution the new dilution
     * @throws JOceanusException on error
     */
    public final void setDilution(final JDilution pDilution) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.DILUTION, pDilution);
    }

    /**
     * Set a new Qualifying Years.
     * @param pYears the new years
     * @throws JOceanusException on error
     */
    public final void setYears(final Integer pYears) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.QUALIFYYEARS, pYears);
    }

    /**
     * Set a new Credit Date.
     * @param pCreditDate the new credit date
     * @throws JOceanusException on error
     */
    public final void setCreditDate(final JDateDay pCreditDate) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.CREDITDATE, pCreditDate);
    }

    /**
     * Set a new NatInsurance.
     * @param pNatIns the new insurance
     * @throws JOceanusException on error
     */
    public final void setNatInsurance(final JMoney pNatIns) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.NATINSURANCE, pNatIns);
    }

    /**
     * Set a new Benefit.
     * @param pBenefit the new benefit
     * @throws JOceanusException on error
     */
    public final void setBenefit(final JMoney pBenefit) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.DEEMEDBENEFIT, pBenefit);
    }

    /**
     * Set a new Pension.
     * @param pPension the new pension
     * @throws JOceanusException on error
     */
    public final void setPension(final JMoney pPension) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.PENSION, pPension);
    }

    /**
     * Set a new Donation.
     * @param pDonation the new donation
     * @throws JOceanusException on error
     */
    public final void setDonation(final JMoney pDonation) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.CHARITYDONATION, pDonation);
    }

    /**
     * Set a new Credit Amount.
     * @param pValue the new credit amount
     * @throws JOceanusException on error
     */
    public final void setCreditAmount(final JMoney pValue) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.CREDITAMOUNT, pValue);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws JOceanusException on error
     */
    public final void setReference(final String pReference) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.REFERENCE, pReference);
    }

    /**
     * Set new Comments.
     * @param pComments the new comments
     * @throws JOceanusException on error
     */
    public final void setComments(final String pComments) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.COMMENTS, pComments);
    }

    /**
     * Set a new ThirdParty.
     * @param pParty the new thirdParty
     * @throws JOceanusException on error
     */
    public final void setThirdParty(final Deposit pParty) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.THIRDPARTY, pParty);
    }

    /**
     * Set a new Portfolio.
     * @param pPortfolio the new portfolio
     * @throws JOceanusException on error
     */
    public final void setPortfolio(final Portfolio pPortfolio) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.PORTFOLIO, pPortfolio);
    }

    /**
     * Set a transaction tag.
     * @param pTag the tag
     * @throws JOceanusException on error
     */
    public final void setTransactionTag(final TransactionTag pTag) throws JOceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Link the value */
        theInfoSet.linkValue(TransactionInfoClass.TRANSTAG, pTag);
    }

    /**
     * Clear a transaction tag.
     * @param pTag the tag
     * @throws JOceanusException on error
     */
    public final void clearTransactionTag(final TransactionTag pTag) throws JOceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Link the value */
        theInfoSet.clearValue(TransactionInfoClass.TRANSTAG, pTag);
    }

    /**
     * Does the transaction have this transaction tag.
     * @param pTag the tag
     * @return true/false
     */
    public final boolean hasTransactionTag(final TransactionTag pTag) {
        /* Access iterator and return false if we have none */
        Iterator<TransactionInfo> myIterator = tagIterator();
        if (myIterator == null) {
            return false;
        }

        /* Loop through the tags */
        while (myIterator.hasNext()) {
            TransactionInfo myInfo = myIterator.next();

            /* Check for presence */
            if (!myInfo.isDeleted()
                && pTag.equals(myInfo.getTransactionTag())) {
                return true;
            }
        }

        /* We do not have the tag */
        return false;
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    private void setInfoSetValue(final TransactionInfoClass pInfoClass,
                                 final Object pValue) throws JOceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch underlying items */
        super.touchUnderlyingItems();

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    /**
     * Update base transaction from an edited transaction.
     * @param pTrans the edited transaction
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pTrans) {
        /* Can only update from a transaction */
        if (!(pTrans instanceof Transaction)) {
            return false;
        }
        Transaction myTrans = (Transaction) pTrans;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myTrans);

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Transaction List class.
     */
    public static class TransactionList
            extends TransactionBaseList<Transaction> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return Transaction.FIELD_DEFS;
        }

        /**
         * EventGroupList field Id.
         */
        private static final JDataField FIELD_EVENTGROUPS = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.TRANSACTION_GROUPS.getValue());

        /**
         * EventGroupMap.
         */
        private final Map<Integer, TransactionGroup> theGroups = new LinkedHashMap<Integer, TransactionGroup>();

        /**
         * The TransactionInfo List.
         */
        private TransactionInfoList theInfoList = null;

        /**
         * The EventInfoType list.
         */
        private TransactionInfoTypeList theInfoTypeList = null;

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_EVENTGROUPS.equals(pField)) {
                return theGroups.isEmpty()
                                          ? JDataFieldValue.SKIP
                                          : theGroups;
            }

            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain the transactionInfoList.
         * @return the transaction info list
         */
        public TransactionInfoList getTransactionInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getTransactionInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the eventInfoTypeList.
         * @return the event info type list
         */
        public TransactionInfoTypeList getEventInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getTransInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public TransactionList(final MoneyWiseData pData) {
            super(pData, Transaction.class, MoneyWiseDataType.TRANSACTION);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected TransactionList(final TransactionList pSource) {
            super(pSource);
        }

        @Override
        protected TransactionList getEmptyList(final ListStyle pStyle) {
            TransactionList myList = new TransactionList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Get an EditList for a range.
         * @return the edit list
         */
        public TransactionList getViewList() {
            /* Build an empty List */
            TransactionList myList = getEmptyList(ListStyle.COPY);
            myList.setStyle(ListStyle.COPY);

            /* Return it */
            return myList;
        }

        /**
         * Get an EditList for a range.
         * @param pRange the range
         * @return the edit list
         */
        public TransactionList deriveEditList(final JDateDayRange pRange) {
            /* Build an empty List */
            TransactionList myList = getEmptyList(ListStyle.EDIT);
            myList.setRange(pRange);

            /* Store InfoType list */
            myList.theInfoTypeList = theInfoTypeList;

            /* Create info List */
            TransactionInfoList myTransInfo = getTransactionInfo();
            myList.theInfoList = myTransInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the Transactions extracting relevant elements */
            Iterator<Transaction> myIterator = iterator();
            while (myIterator.hasNext()) {
                Transaction myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Check the range */
                int myResult = pRange.compareTo(myCurr.getDate());

                /* Handle out of range */
                if (myResult > 0) {
                    continue;
                } else if (myResult < 0) {
                    break;
                }

                /* Build the new linked transaction and add it to the list */
                Transaction myTrans = new Transaction(myList, myCurr);
                myList.append(myTrans);

                /* If this is a child transaction */
                if (myTrans.isChild()) {
                    /* Register child against parent (in this edit list) */
                    myList.registerChild(myTrans);
                }
            }

            /* Return the List */
            return myList;
        }

        /**
         * Get an EditList for a new TaxYear.
         * @param pTaxYear the new TaxYear
         * @return the edit list
         * @throws JOceanusException on error
         */
        public TransactionList deriveEditList(final TaxYear pTaxYear) throws JOceanusException {
            /* Build an empty List */
            TransactionList myList = getEmptyList(ListStyle.EDIT);
            myList.setRange(pTaxYear.getDateRange());

            /* Store InfoType list */
            myList.theInfoTypeList = theInfoTypeList;

            /* Create info List */
            TransactionInfoList myTransInfo = getTransactionInfo();
            myList.theInfoList = myTransInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the Schedules */
            ScheduleList mySchedules = getDataSet().getSchedules();
            Iterator<Schedule> myIterator = mySchedules.iterator();
            while (myIterator.hasNext()) {
                Schedule myCurr = myIterator.next();

                /* Ignore deleted patterns */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Access a copy of the base date */
                // JDateDay myDate = new JDateDay(myCurr.getDate());

                /* Loop while we have an event to add */
                // for (;;) {
                /* Access next event and break loop if no more */
                // Event myEvent = myCurr.nextEvent(myList, pTaxYear, myDate);
                // if (myEvent == null) {
                // break;
                // }

                /* Add it to the extract */
                // myList.append(myEvent);

                /* If this is a child event */
                // if (myEvent.isChild()) {
                // /* Register child against parent (in this edit list) */
                // myList.registerChild(myEvent);
                // }
                // }
            }

            /* Sort the list */
            myList.reSort();

            /* Return the List */
            return myList;
        }

        /**
         * Register child into event group.
         * @param pChild the child to register
         */
        public void registerChild(final Transaction pChild) {
            /* Access parent */
            Transaction myParent = pChild.getParent();
            Integer myId = myParent.getId();
            myParent = findItemById(myId);

            /* Access TransactionGroup */
            TransactionGroup myGroup = theGroups.get(myId);
            if (myGroup == null) {
                myGroup = new TransactionGroup(myParent);
                theGroups.put(myId, myGroup);
            }

            /* Register the child */
            myGroup.registerChild(pChild);
        }

        /**
         * Obtain the group for a parent.
         * @param pParent the parent event
         * @return the group
         */
        public TransactionGroup getGroup(final Transaction pParent) {
            return theGroups.get(pParent.getId());
        }

        /**
         * Reset groups.
         */
        public void resetGroups() {
            theGroups.clear();
        }

        /**
         * Validate groups.
         * @return the error list (or null if no errors)
         */
        public DataErrorList<Transaction> validateGroups() {
            /* Note error list */
            DataErrorList<Transaction> myErrorList = null;

            /* Loop through the groups */
            Iterator<TransactionGroup> myIterator = theGroups.values().iterator();
            while (myIterator.hasNext()) {
                TransactionGroup myGroup = myIterator.next();

                /* Validate the group */
                DataErrorList<Transaction> myErrors = myGroup.validate();

                /* If we have any errors */
                if (myErrors != null) {
                    /* If this is the first error */
                    if (myErrorList == null) {
                        /* Record as error list */
                        myErrorList = myErrors;
                    } else {
                        /* Add to the error list */
                        myErrorList.addAll(myErrors);
                    }
                }
            }

            /* Return the error list */
            return myErrorList;
        }

        /**
         * Add a new item to the list.
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public Transaction addCopyItem(final DataItem<?> pItem) {
            if (pItem instanceof Transaction) {
                Transaction myTrans = new Transaction(this, (Transaction) pItem);
                add(myTrans);
                return myTrans;
            }
            throw new UnsupportedOperationException();
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public Transaction addNewItem() {
            /* Create a new Transaction */
            Transaction myTrans = new Transaction(this);

            /* Set the Date as the start of the range */
            myTrans.setDate(getValidDateRange().getStart());

            /* Add to list and return */
            add(myTrans);
            return myTrans;
        }

        @Override
        public Transaction addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* If the item has children */
            if (pValues.hasChildren()) {
                /* Note that the item is split */
                pValues.addValue(FIELD_SPLIT, Boolean.TRUE);
            }

            /* Create the transaction */
            Transaction myTrans = new Transaction(this, pValues);

            /* Check that this TransId has not been previously added */
            if (!isIdUnique(myTrans.getId())) {
                myTrans.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myTrans, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myTrans);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myTrans);
                    getTransactionInfo().addValuesItem(myValues);
                }
            }

            /* Loop through the children */
            if (pValues.hasChildren()) {
                /* Loop through the items */
                Iterator<DataValues<MoneyWiseDataType>> myIterator = pValues.childIterator();
                while (myIterator.hasNext()) {
                    DataValues<MoneyWiseDataType> myValues = myIterator.next();

                    /* Note that the item is split */
                    myValues.addValue(FIELD_SPLIT, Boolean.TRUE);
                    myValues.addValue(FIELD_PARENT, myTrans);

                    /* Copy missing values from parent */
                    myValues.addValue(FIELD_DATE, pValues.getValue(FIELD_DATE));
                    myValues.addValue(FIELD_ACCOUNT, pValues.getValue(FIELD_ACCOUNT));

                    /* Build item */
                    addValuesItem(myValues);
                }
            }

            /* Return it */
            return myTrans;
        }

        @Override
        protected DataMapItem<Transaction, MoneyWiseDataType> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }

        @Override
        public void postProcessOnLoad() throws JOceanusException {
            /* Resolve links and sort the data */
            resolveDataSetLinks();
            reSort();
        }

        @Override
        public void prepareForAnalysis() {
            /* Null operation */
        }
    }
}
