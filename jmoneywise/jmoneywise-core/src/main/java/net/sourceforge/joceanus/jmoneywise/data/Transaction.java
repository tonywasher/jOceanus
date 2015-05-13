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

import net.sourceforge.joceanus.jmetis.data.DataState;
import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.EditState;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
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
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Reconciled Field Id.
     */
    public static final JDataField FIELD_RECONCILED = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_RECONCILED.getValue());

    /**
     * EventInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Circular update Error Text.
     */
    private static final String ERROR_CIRCULAR = MoneyWiseDataResource.TRANSACTION_ERROR_CIRCLE.getValue();

    /**
     * Debit and Dilution for DeMerger.
     */
    private static final String ERROR_DEBITDILUTION = MoneyWiseDataResource.TRANSACTION_ERROR_DEBITDILUTION.getValue();

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
                theInfoSet = new TransactionInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
                theInfoSet.cloneDataInfoSet(pTransaction.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new TransactionInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
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
     * Edit constructor.
     * @param pList the list
     */
    public Transaction(final TransactionList pList) {
        super(pList);
        setValueReconciled(Boolean.FALSE);

        /* Build InfoSet */
        theInfoSet = new TransactionInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
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

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof JDateDay) {
                setValueDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
            }

            /* Store the reconciled flag */
            myValue = pValues.getValue(FIELD_RECONCILED);
            if (myValue instanceof Boolean) {
                setValueReconciled((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueReconciled(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }

        /* Create the InfoSet */
        theInfoSet = new TransactionInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DATE.equals(pField)) {
            return true;
        }
        if (FIELD_RECONCILED.equals(pField)) {
            return isReconciled();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
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
     * Obtain Date.
     * @return the date
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Reconciled State.
     * @return the reconciled state
     */
    public Boolean isReconciled() {
        return isReconciled(getValueSet());
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain Reconciled State.
     * @param pValueSet the valueSet
     * @return the Reconciled State
     */
    public static Boolean isReconciled(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RECONCILED, Boolean.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Set reconciled state.
     * @param pValue the value
     */
    protected final void setValueReconciled(final Boolean pValue) {
        getValueSet().setValue(FIELD_RECONCILED, pValue);
    }

    @Override
    public TransactionInfoSet getInfoSet() {
        return theInfoSet;
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
     * Obtain Partner Amount.
     * @return the Partner Amount
     */
    public final JMoney getPartnerAmount() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.PARTNERAMOUNT, JMoney.class)
                         : null;
    }

    /**
     * Obtain Exchange Rate.
     * @return the Donation
     */
    public final JRatio getExchangeRate() {
        return hasInfoSet
                         ? theInfoSet.getValue(TransactionInfoClass.XCHANGERATE, JRatio.class)
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
     * Compare this event to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final Transaction pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                             ? -1
                             : 1;
        }

        /* If the dates differ */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying details */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Transaction details */
        super.resolveDataSetLinks();

        /* Adjust Reconciled */
        ValueSet myValues = getValueSet();
        Object myReconciled = myValues.getValue(FIELD_RECONCILED);
        if (myReconciled == null) {
            setValueReconciled(Boolean.FALSE);
        }

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
        JDateDay myDate = getDate();
        TransactionAsset myAccount = getAccount();
        TransactionAsset myPartner = getPartner();
        TransactionCategory myCategory = getCategory();
        JDilution myDilution = getDilution();
        JUnits myDebitUnits = getDebitUnits();
        JUnits myCreditUnits = getCreditUnits();

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Determine date range to check for */
        TransactionList myList = getList();
        JDateDayRange myRange = myList.getValidDateRange();

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);
            /* The date must be in-range */
        } else if (myRange.compareTo(myDate) != 0) {
            addError(ERROR_RANGE, FIELD_DATE);
        }

        /* Perform underlying checks */
        super.validate();

        /* Cannot have Credit and Debit if securities are identical */
        if ((myCreditUnits != null) && (myDebitUnits != null) && (Difference.isEqual(myAccount, myPartner))) {
            addError(ERROR_CIRCULAR, TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS));
        }

        /* Must have either dilution or debit units for deMerger */
        if (isCategoryClass(TransactionCategoryClass.STOCKDEMERGER)
            && ((myDebitUnits == null) == (myDilution == null))) {
            addError(ERROR_DEBITDILUTION, TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION));
            addError(ERROR_DEBITDILUTION, TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS));
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
     * Obtain default deposit for ThirdParty.
     * @return the default thirdParty
     */
    protected Deposit getDefaultThirdParty() {
        /* loop through the deposits */
        DepositList myDeposits = getDataSet().getDeposits();
        Iterator<Deposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            Deposit myDeposit = myIterator.next();

            /* Use if not deleted or closed */
            if (!myDeposit.isDeleted() && !myDeposit.isClosed()) {
                return myDeposit;
            }
        }

        /* Return no category */
        return null;
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
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
        setValueDate((pDate == null)
                                    ? null
                                    : new JDateDay(pDate));
    }

    /**
     * Set a reconciled indication.
     * @param pReconciled the reconciled state
     */
    public void setReconciled(final Boolean pReconciled) {
        setValueReconciled(pReconciled);
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
     * Set a new Partner Amount.
     * @param pValue the new partner amount
     * @throws JOceanusException on error
     */
    public final void setPartnerAmount(final JMoney pValue) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.PARTNERAMOUNT, pValue);
    }

    /**
     * Set a new ExchangeRate.
     * @param pValue the new exchangeRate
     * @throws JOceanusException on error
     */
    public final void setXchangeRate(final JRate pValue) throws JOceanusException {
        setInfoSetValue(TransactionInfoClass.XCHANGERATE, pValue);
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

        /* Update the Date if required */
        if (!Difference.isEqual(getDate(), myTrans.getDate())) {
            setValueDate(myTrans.getDate());
        }

        /* Update the reconciled state if required */
        if (!Difference.isEqual(isReconciled(), myTrans.isReconciled())) {
            setValueReconciled(myTrans.isReconciled());
        }

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

        /**
         * The TransactionInfo List.
         */
        private TransactionInfoList theInfoList = null;

        /**
         * The TransactionInfoType list.
         */
        private TransactionInfoTypeList theInfoTypeList = null;

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
         * Set the transactionInfoTypeList.
         * @param pInfoList the info type list
         */
        protected void setTransactionInfo(final TransactionInfoList pInfoList) {
            theInfoList = pInfoList;
        }

        /**
         * Obtain the transactionInfoTypeList.
         * @return the transaction info type list
         */
        public TransactionInfoTypeList getTransInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getTransInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Set the transactionInfoTypeList.
         * @param pInfoTypeList the info type list
         */
        protected void setTransInfoTypes(final TransactionInfoTypeList pInfoTypeList) {
            theInfoTypeList = pInfoTypeList;
        }

        @Override
        protected TransactionList getEmptyList(final ListStyle pStyle) {
            TransactionList myList = new TransactionList(this);
            myList.setStyle(pStyle);
            return myList;
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
