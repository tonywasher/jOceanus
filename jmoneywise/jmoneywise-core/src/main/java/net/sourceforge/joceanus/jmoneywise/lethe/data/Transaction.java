/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

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
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, TransactionBase.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final MetisLetheField FIELD_DATE = FIELD_DEFS.declareComparisonValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue(), MetisDataType.DATE);

    /**
     * TaxYear Field Id.
     */
    public static final MetisLetheField FIELD_TAXYEAR = FIELD_DEFS.declareDerivedValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_TAXYEAR.getValue());

    /**
     * EventInfoSet field Id.
     */
    private static final MetisLetheField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Circular update Error Text.
     */
    private static final String ERROR_CIRCULAR = MoneyWiseDataResource.TRANSACTION_ERROR_CIRCLE.getValue();

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

        /* Build InfoSet */
        theInfoSet = new TransactionInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values
     * @throws OceanusException on error
     */
    private Transaction(final TransactionList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            final Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof TethysDate) {
                setValueDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                final TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDate((String) myValue));
            }
            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }

        /* Create the InfoSet */
        theInfoSet = new TransactionInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DATE.equals(pField)) {
            return true;
        }
        if (FIELD_TAXYEAR.equals(pField)) {
            return false;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Object getFieldValue(final MetisLetheField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : MetisDataFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        final TransactionInfoClass myClass = TransactionInfoSet.getClassForField(pField);
        if (theInfoSet != null
            && myClass != null) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public TethysDate getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static TethysDate getDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, TethysDate.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
        final MoneyWiseTaxFactory myFactory = getDataSet().getTaxFactory();
        setValueTaxYear(myFactory.findTaxYearForDate(pValue));
    }

    /**
     * Obtain TaxYear.
     * @return the taxYear
     */
    public MoneyWiseTaxCredit getTaxYear() {
        return getTaxYear(getValueSet());
    }

    /**
     * Obtain TaxYear.
     * @param pValueSet the valueSet
     * @return the TaxYear
     */
    public static MoneyWiseTaxCredit getTaxYear(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXYEAR, MoneyWiseTaxCredit.class);
    }

    /**
     * Set taxYear value.
     * @param pValue the value
     */
    private void setValueTaxYear(final MoneyWiseTaxCredit pValue) {
        getValueSet().setValue(FIELD_TAXYEAR, pValue);
    }

    @Override
    public TransactionInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Account Delta Units.
     * @return the Account Delta Units
     */
    public final TethysUnits getAccountDeltaUnits() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.ACCOUNTDELTAUNITS, TethysUnits.class)
                          : null;
    }

    /**
     * Obtain Partner Delta Units.
     * @return the Partner Delta Units
     */
    public final TethysUnits getPartnerDeltaUnits() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.PARTNERDELTAUNITS, TethysUnits.class)
                          : null;
    }

    /**
     * Obtain Tax Credit.
     * @return the Tax Credit
     */
    public final TethysMoney getTaxCredit() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.TAXCREDIT, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Price.
     * @return the Price
     */
    public final TethysPrice getPrice() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.PRICE, TethysPrice.class)
                          : null;
    }

    /**
     * Obtain Commission.
     * @return the Commission
     */
    public final TethysMoney getCommission() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.COMMISSION, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public final TethysDilution getDilution() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.DILUTION, TethysDilution.class)
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
     * Obtain Employer National Insurance.
     * @return the NatInsurance
     */
    public final TethysMoney getEmployerNatIns() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.EMPLOYERNATINS, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Employee National Insurance.
     * @return the NatInsurance
     */
    public final TethysMoney getEmployeeNatIns() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.EMPLOYEENATINS, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Deemed Benefit.
     * @return the Benefit
     */
    public final TethysMoney getDeemedBenefit() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.DEEMEDBENEFIT, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Withheld amount.
     * @return the Withheld
     */
    public final TethysMoney getWithheld() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.WITHHELD, TethysMoney.class)
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
     * Obtain ReturnedCash Account.
     * @return the ReturnedCash
     */
    public final TransactionAsset getReturnedCashAccount() {
        return hasInfoSet
                          ? theInfoSet.getDeposit(TransactionInfoClass.RETURNEDCASHACCOUNT)
                          : null;
    }

    /**
     * Obtain Partner Amount.
     * @return the Partner Amount
     */
    public final TethysMoney getPartnerAmount() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.PARTNERAMOUNT, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain ExchangeRate.
     * @return the ExchangeRate
     */
    public final TethysRatio getExchangeRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.XCHANGERATE, TethysRatio.class)
                          : null;
    }

    /**
     * Obtain ReturnedCash.
     * @return the ReturnedCash
     */
    public final TethysMoney getReturnedCash() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.RETURNEDCASH, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain the Transaction tagList.
     * @return the list of transaction tags
     */
    @SuppressWarnings("unchecked")
    public List<TransactionTag> getTransactionTags() {
        return hasInfoSet
                          ? (List<TransactionTag>) theInfoSet.getListValue(TransactionInfoClass.TRANSTAG)
                          : null;
    }

    @Override
    public MetisDataState getState() {
        /* Pop history for self */
        MetisDataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == MetisDataState.CLEAN) && useInfoSet) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public MetisDataEditState getEditState() {
        /* Pop history for self */
        MetisDataEditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if (myState == MetisDataEditState.CLEAN
            && useInfoSet) {
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
        if (!hasHistory && useInfoSet) {
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
    public MetisDataDifference fieldChanged(final MetisLetheField pField) {
        /* Handle InfoSet fields */
        final TransactionInfoClass myClass = TransactionInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : MetisDataDifference.IDENTICAL;
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

        /* Check ReturnedCash Account */
        final TransactionAsset myReturnedCash = getReturnedCashAccount();
        return (myReturnedCash != null) && myReturnedCash.isClosed();
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
        final int iDiff = MetisDataDifference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying details */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Transaction details */
        super.resolveDataSetLinks();

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
    public MetisFieldRequired isClassRequired(final TransactionInfoClass pClass) {
        /* Check the class */
        return theInfoSet.isClassRequired(pClass);
    }

    /**
     * Validate the transaction.
     */
    @Override
    public void validate() {
        final TethysDate myDate = getDate();
        final TransactionAsset myAccount = getAccount();
        final TransactionAsset myPartner = getPartner();
        final TransactionCategory myCategory = getCategory();
        final TethysUnits myAccountUnits = getAccountDeltaUnits();
        final TethysUnits myPartnerUnits = getPartnerDeltaUnits();

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Determine date range to check for */
        final TethysDateRange myRange = getDataSet().getDateRange();

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);
            /* The date must be in-range */
        } else if (myRange.compareToDate(myDate) != 0) {
            addError(ERROR_RANGE, FIELD_DATE);
        }

        /* Perform underlying checks */
        super.validate();

        /* Cannot have PartnerUnits if securities are identical */
        if (myAccountUnits != null
            && myPartnerUnits != null
            && MetisDataDifference.isEqual(myAccount, myPartner)) {
            addError(ERROR_CIRCULAR, TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS));
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
     * Obtain default deposit for ReturnedCashAccount.
     * @return the default returnedCashAccount
     */
    protected Deposit getDefaultReturnedCashAccount() {
        /* loop through the deposits */
        final DepositList myDeposits = getDataSet().getDeposits();
        final Iterator<Deposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final Deposit myDeposit = myIterator.next();

            /* Use if not deleted or closed */
            if (!myDeposit.isDeleted() && !myDeposit.isClosed()) {
                return myDeposit;
            }
        }

        /* Return no deposit */
        return null;
    }

    /**
     * Calculate the tax credit for a transaction.
     * @return the calculated tax credit
     */
    public final TethysMoney calculateTaxCredit() {
        final MoneyWiseTaxCredit myTax = getTaxYear();
        final TethysMoney myCredit = getTaxCredit();

        /* Ignore unless tax credit is null/zero */
        if (myCredit != null
            && myCredit.isNonZero()) {
            return myCredit;
        }

        /* Ignore unless category is interest/dividend */
        if ((getCategory() == null)
            || (!isInterest()
                && !isDividend())) {
            return myCredit;
        }

        /* Determine the tax credit rate */
        final TethysRate myRate = isInterest()
                                               ? myTax.getTaxCreditRateForInterest()
                                               : myTax.getTaxCreditRateForDividend();

        /* Calculate the tax credit */
        return getAmount().taxCreditAtRate(myRate);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final TethysDate pDate) {
        setValueDate((pDate == null)
                                     ? null
                                     : new TethysDate(pDate));
    }

    /**
     * Set a new AccountDeltaUnits.
     * @param pUnits the new units
     * @throws OceanusException on error
     */
    public final void setAccountDeltaUnits(final TethysUnits pUnits) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.ACCOUNTDELTAUNITS, pUnits);
    }

    /**
     * Set a new PartnerDeltaUnits.
     * @param pUnits the new units
     * @throws OceanusException on error
     */
    public final void setPartnerDeltaUnits(final TethysUnits pUnits) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.PARTNERDELTAUNITS, pUnits);
    }

    /**
     * Set a new TaxCredit.
     * @param pCredit the new credit
     * @throws OceanusException on error
     */
    public final void setTaxCredit(final TethysMoney pCredit) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.TAXCREDIT, pCredit);
    }

    /**
     * Set a new Price.
     * @param pPrice the new price
     * @throws OceanusException on error
     */
    public final void setPrice(final TethysPrice pPrice) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.PRICE, pPrice);
    }

    /**
     * Set a new Commission.
     * @param pCommission the new commission
     * @throws OceanusException on error
     */
    public final void setCommission(final TethysMoney pCommission) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.COMMISSION, pCommission);
    }

    /**
     * Set a new Dilution.
     * @param pDilution the new dilution
     * @throws OceanusException on error
     */
    public final void setDilution(final TethysDilution pDilution) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.DILUTION, pDilution);
    }

    /**
     * Set a new Qualifying Years.
     * @param pYears the new years
     * @throws OceanusException on error
     */
    public final void setYears(final Integer pYears) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.QUALIFYYEARS, pYears);
    }

    /**
     * Set a new Employer NatInsurance.
     * @param pNatIns the new insurance
     * @throws OceanusException on error
     */
    public final void setEmployerNatIns(final TethysMoney pNatIns) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.EMPLOYERNATINS, pNatIns);
    }

    /**
     * Set a new Employee NatInsurance.
     * @param pNatIns the new insurance
     * @throws OceanusException on error
     */
    public final void setEmployeeNatIns(final TethysMoney pNatIns) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.EMPLOYEENATINS, pNatIns);
    }

    /**
     * Set a new Benefit.
     * @param pBenefit the new benefit
     * @throws OceanusException on error
     */
    public final void setBenefit(final TethysMoney pBenefit) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.DEEMEDBENEFIT, pBenefit);
    }

    /**
     * Set a new Withheld.
     * @param pWithheld the new withheld
     * @throws OceanusException on error
     */
    public final void setWithheld(final TethysMoney pWithheld) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.WITHHELD, pWithheld);
    }

    /**
     * Set a new Partner Amount.
     * @param pValue the new partner amount
     * @throws OceanusException on error
     */
    public final void setPartnerAmount(final TethysMoney pValue) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.PARTNERAMOUNT, pValue);
    }

    /**
     * Set a new ExchangeRate.
     * @param pRate the new rate
     * @throws OceanusException on error
     */
    public final void setExchangeRate(final TethysRatio pRate) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.XCHANGERATE, pRate);
    }

    /**
     * Set a new ReturnedCash.
     * @param pValue the new returned cash
     * @throws OceanusException on error
     */
    public final void setReturnedCash(final TethysMoney pValue) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.RETURNEDCASH, pValue);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws OceanusException on error
     */
    public final void setReference(final String pReference) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.REFERENCE, pReference);
    }

    /**
     * Set new Comments.
     * @param pComments the new comments
     * @throws OceanusException on error
     */
    public final void setComments(final String pComments) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.COMMENTS, pComments);
    }

    /**
     * Set a new ReturnedCasah Account.
     * @param pAccount the new returned cash account
     * @throws OceanusException on error
     */
    public final void setReturnedCashAccount(final TransactionAsset pAccount) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.RETURNEDCASHACCOUNT, pAccount);
    }

    /**
     * Set the transaction tags.
     * @param pTags the tags
     * @throws OceanusException on error
     */
    public final void setTransactionTags(final List<TransactionTag> pTags) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Link the value */
        theInfoSet.setListValue(TransactionInfoClass.TRANSTAG, pTags);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    private void setInfoSetValue(final TransactionInfoClass pInfoClass,
                                 final Object pValue) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
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
        final Transaction myTrans = (Transaction) pTrans;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Date if required */
        if (!MetisDataDifference.isEqual(getDate(), myTrans.getDate())) {
            setValueDate(myTrans.getDate());
        }

        /* Apply basic changes */
        applyBasicChanges(myTrans);

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void flipAssets() throws OceanusException {
        /* Handle underlying values */
        super.flipAssets();

        /* Flip deltas */
        final TethysUnits myActDelta = getAccountDeltaUnits();
        final TethysUnits myPartDelta = getPartnerDeltaUnits();
        setAccountDeltaUnits(myPartDelta);
        setPartnerDeltaUnits(myActDelta);

        /* If we need to wipe memory of AccountDelta/PartnerDelta */
        if (myActDelta != null && myPartDelta == null) {
            theInfoSet.wipeInfo(TransactionInfoClass.ACCOUNTDELTAUNITS);
        }
        if (myPartDelta != null && myActDelta == null) {
            theInfoSet.wipeInfo(TransactionInfoClass.PARTNERDELTAUNITS);
        }
    }

    /**
     * The Transaction List class.
     */
    public static class TransactionList
            extends TransactionBaseList<Transaction> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<TransactionList> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionList.class);

        /**
         * The TransactionInfo List.
         */
        private TransactionInfoList theInfoList;

        /**
         * The TransactionInfoType list.
         */
        private TransactionInfoTypeList theInfoTypeList;

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
        public MetisFieldSet<TransactionList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
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
            final TransactionList myList = new TransactionList(this);
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
                final Transaction myTrans = new Transaction(this, (Transaction) pItem);
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
            final Transaction myTrans = new Transaction(this);

            /* Set the Date as the start of the range */
            TethysDate myDate = new TethysDate();
            final TethysDateRange myRange = getDataSet().getDateRange();
            if (myRange.compareToDate(myDate) != 0) {
                myDate = myRange.getStart();
            }
            myTrans.setDate(myDate);

            /* Create a default pair */
            myTrans.setValueAssetPair(getAssetPairManager().getDefaultPair());

            /* Add to list and return */
            add(myTrans);
            return myTrans;
        }

        @Override
        public Transaction addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the transaction */
            final Transaction myTrans = new Transaction(this, pValues);

            /* Check that this TransId has not been previously added */
            if (!isIdUnique(myTrans.getId())) {
                myTrans.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myTrans, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myTrans);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    final DataValues<MoneyWiseDataType> myValues = myItem.getValues(myTrans);
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
        public void postProcessOnLoad() throws OceanusException {
            /* Resolve links and sort the data */
            super.resolveDataSetLinks();
            reSort();
        }

        @Override
        public void prepareForAnalysis() {
            /* Null operation */
        }
    }
}
