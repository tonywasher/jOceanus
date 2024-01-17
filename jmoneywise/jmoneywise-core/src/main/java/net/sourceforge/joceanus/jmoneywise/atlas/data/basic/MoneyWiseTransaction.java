/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.basic;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataMapItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues.PrometheusInfoItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues.PrometheusInfoSetItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * New version of Event DataItem utilising EventInfo.
 * @author Tony Washer
 */
public class MoneyWiseTransaction
        extends MoneyWiseTransBase
        implements PrometheusInfoSetItem {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.TRANSACTION.getItemName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.TRANSACTION.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldVersionedSet<MoneyWiseTransaction> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MoneyWiseTransaction.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareDateField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        FIELD_DEFS.declareDerivedVersionedField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_TAXYEAR);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME, MoneyWiseTransaction::getInfoSet);
    }

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Circular update Error Text.
     */
    private static final String ERROR_CIRCULAR = MoneyWiseBasicResource.TRANSACTION_ERROR_CIRCLE.getValue();

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
    private final MoneyWiseTransInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the transaction list
     * @param pTransaction The Transaction to copy
     */
    public MoneyWiseTransaction(final MoneyWiseTransactionList pList,
                                final MoneyWiseTransaction pTransaction) {
        /* Set standard values */
        super(pList, pTransaction);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new MoneyWiseTransInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
                theInfoSet.cloneDataInfoSet(pTransaction.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new MoneyWiseTransInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
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
    public MoneyWiseTransaction(final MoneyWiseTransactionList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new MoneyWiseTransInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseTransaction(final MoneyWiseTransactionList pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            final Object myValue = pValues.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
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
        theInfoSet = new MoneyWiseTransInfoSet(this, pList.getTransInfoTypes(), pList.getTransactionInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisDataFieldId pField) {
        /* Determine whether fields should be included */
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(pField)) {
            return true;
        }
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_TAXYEAR.equals(pField)) {
            return false;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public TethysDate getDate() {
        return getValues().getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, TethysDate.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final TethysDate pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, pValue);
        final MoneyWiseTaxFactory myFactory = getDataSet().getTaxFactory();
        setValueTaxYear(myFactory.findTaxYearForDate(pValue));
    }

    /**
     * Obtain TaxYear.
     * @return the taxYear
     */
    public MoneyWiseTaxCredit getTaxYear() {
        return getValues().getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_TAXYEAR, MoneyWiseTaxCredit.class);
    }

    /**
     * Set taxYear value.
     * @param pValue the value
     */
    private void setValueTaxYear(final MoneyWiseTaxCredit pValue) {
        getValues().setUncheckedValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_TAXYEAR, pValue);
    }

    @Override
    public MoneyWiseTransInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Account Delta Units.
     * @return the Account Delta Units
     */
    public final TethysUnits getAccountDeltaUnits() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, TethysUnits.class)
                : null;
    }

    /**
     * Obtain Partner Delta Units.
     * @return the Partner Delta Units
     */
    public final TethysUnits getPartnerDeltaUnits() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, TethysUnits.class)
                : null;
    }

    /**
     * Obtain Tax Credit.
     * @return the Tax Credit
     */
    public final TethysMoney getTaxCredit() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.TAXCREDIT, TethysMoney.class)
                : null;
    }

    /**
     * Obtain Price.
     * @return the Price
     */
    public final TethysPrice getPrice() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.PRICE, TethysPrice.class)
                : null;
    }

    /**
     * Obtain Commission.
     * @return the Commission
     */
    public final TethysMoney getCommission() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.COMMISSION, TethysMoney.class)
                : null;
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public final TethysRatio getDilution() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.DILUTION, TethysRatio.class)
                : null;
    }

    /**
     * Obtain Qualifying Years.
     * @return the Years
     */
    public final Integer getYears() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.QUALIFYYEARS, Integer.class)
                : null;
    }

    /**
     * Obtain Employer National Insurance.
     * @return the NatInsurance
     */
    public final TethysMoney getEmployerNatIns() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.EMPLOYERNATINS, TethysMoney.class)
                : null;
    }

    /**
     * Obtain Employee National Insurance.
     * @return the NatInsurance
     */
    public final TethysMoney getEmployeeNatIns() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.EMPLOYEENATINS, TethysMoney.class)
                : null;
    }

    /**
     * Obtain Deemed Benefit.
     * @return the Benefit
     */
    public final TethysMoney getDeemedBenefit() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.DEEMEDBENEFIT, TethysMoney.class)
                : null;
    }

    /**
     * Obtain Withheld amount.
     * @return the Withheld
     */
    public final TethysMoney getWithheld() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.WITHHELD, TethysMoney.class)
                : null;
    }

    /**
     * Obtain Reference.
     * @return the Reference
     */
    public final String getReference() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.REFERENCE, String.class)
                : null;
    }

    /**
     * Obtain Comments.
     * @return the Comments
     */
    public final String getComments() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.COMMENTS, String.class)
                : null;
    }

    /**
     * Obtain ReturnedCash Account.
     * @return the ReturnedCash
     */
    public final MoneyWiseTransAsset getReturnedCashAccount() {
        return hasInfoSet
                ? theInfoSet.getTransAsset(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT)
                : null;
    }

    /**
     * Obtain Partner Amount.
     * @return the Partner Amount
     */
    public final TethysMoney getPartnerAmount() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.PARTNERAMOUNT, TethysMoney.class)
                : null;
    }

    /**
     * Obtain ExchangeRate.
     * @return the ExchangeRate
     */
    public final TethysRatio getExchangeRate() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.XCHANGERATE, TethysRatio.class)
                : null;
    }

    /**
     * Obtain ReturnedCash.
     * @return the ReturnedCash
     */
    public final TethysMoney getReturnedCash() {
        return hasInfoSet
                ? theInfoSet.getValue(MoneyWiseTransInfoClass.RETURNEDCASH, TethysMoney.class)
                : null;
    }

    /**
     * Obtain the Transaction tagList.
     * @return the list of transaction tags
     */
    @SuppressWarnings("unchecked")
    public List<MoneyWiseTransTag> getTransactionTags() {
        return hasInfoSet
                ? (List<MoneyWiseTransTag>) theInfoSet.getListValue(MoneyWiseTransInfoClass.TRANSTAG)
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
    public MetisDataDifference fieldChanged(final MetisDataFieldId pField) {
        /* Handle InfoSet fields */
        final MoneyWiseTransInfoClass myClass = MoneyWiseTransInfoSet.getClassForField(pField);
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
        final MoneyWiseTransAsset myReturnedCash = getReturnedCashAccount();
        return myReturnedCash != null && myReturnedCash.isClosed();
    }

    @Override
    public MoneyWiseTransaction getBase() {
        return (MoneyWiseTransaction) super.getBase();
    }

    @Override
    public MoneyWiseTransactionList getList() {
        return (MoneyWiseTransactionList) super.getList();
    }

    /**
     * Compare this event to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Handle the trivial cases */
        final MoneyWiseTransaction myThat = (MoneyWiseTransaction) pThat;

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                    ? -1
                    : 1;
        }

        /* If the dates differ */
        final int iDiff = MetisDataDifference.compareObject(getDate(), myThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying details */
        return super.compareValues(pThat);
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
    public MetisFieldRequired isClassRequired(final MoneyWiseTransInfoClass pClass) {
        /* Check the class */
        return theInfoSet.isClassRequired(pClass);
    }

    /**
     * Validate the transaction.
     */
    @Override
    public void validate() {
        final TethysDate myDate = getDate();
        final MoneyWiseTransAsset myAccount = getAccount();
        final MoneyWiseTransAsset myPartner = getPartner();
        final MoneyWiseTransCategory myCategory = getCategory();
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
            addError(ERROR_MISSING, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
            /* The date must be in-range */
        } else if (myRange.compareToDate(myDate) != 0) {
            addError(ERROR_RANGE, MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        }

        /* Perform underlying checks */
        super.validate();

        /* Cannot have PartnerUnits if securities are identical */
        if (myAccountUnits != null
                && myPartnerUnits != null
                && MetisDataDifference.isEqual(myAccount, myPartner)) {
            addError(ERROR_CIRCULAR, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.PARTNERDELTAUNITS));
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
    protected MoneyWiseDeposit getDefaultReturnedCashAccount() {
        /* loop through the deposits */
        final MoneyWiseDepositList myDeposits = getDataSet().getDeposits();
        final Iterator<MoneyWiseDeposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDeposit myDeposit = myIterator.next();

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
        setInfoSetValue(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, pUnits);
    }

    /**
     * Set a new PartnerDeltaUnits.
     * @param pUnits the new units
     * @throws OceanusException on error
     */
    public final void setPartnerDeltaUnits(final TethysUnits pUnits) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, pUnits);
    }

    /**
     * Set a new TaxCredit.
     * @param pCredit the new credit
     * @throws OceanusException on error
     */
    public final void setTaxCredit(final TethysMoney pCredit) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.TAXCREDIT, pCredit);
    }

    /**
     * Set a new Price.
     * @param pPrice the new price
     * @throws OceanusException on error
     */
    public final void setPrice(final TethysPrice pPrice) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.PRICE, pPrice);
    }

    /**
     * Set a new Commission.
     * @param pCommission the new commission
     * @throws OceanusException on error
     */
    public final void setCommission(final TethysMoney pCommission) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.COMMISSION, pCommission);
    }

    /**
     * Set a new Dilution.
     * @param pDilution the new dilution
     * @throws OceanusException on error
     */
    public final void setDilution(final TethysRatio pDilution) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.DILUTION, pDilution);
    }

    /**
     * Set a new Qualifying Years.
     * @param pYears the new years
     * @throws OceanusException on error
     */
    public final void setYears(final Integer pYears) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.QUALIFYYEARS, pYears);
    }

    /**
     * Set a new Employer NatInsurance.
     * @param pNatIns the new insurance
     * @throws OceanusException on error
     */
    public final void setEmployerNatIns(final TethysMoney pNatIns) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.EMPLOYERNATINS, pNatIns);
    }

    /**
     * Set a new Employee NatInsurance.
     * @param pNatIns the new insurance
     * @throws OceanusException on error
     */
    public final void setEmployeeNatIns(final TethysMoney pNatIns) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.EMPLOYEENATINS, pNatIns);
    }

    /**
     * Set a new Benefit.
     * @param pBenefit the new benefit
     * @throws OceanusException on error
     */
    public final void setBenefit(final TethysMoney pBenefit) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.DEEMEDBENEFIT, pBenefit);
    }

    /**
     * Set a new Withheld.
     * @param pWithheld the new withheld
     * @throws OceanusException on error
     */
    public final void setWithheld(final TethysMoney pWithheld) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.WITHHELD, pWithheld);
    }

    /**
     * Set a new Partner Amount.
     * @param pValue the new partner amount
     * @throws OceanusException on error
     */
    public final void setPartnerAmount(final TethysMoney pValue) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.PARTNERAMOUNT, pValue);
    }

    /**
     * Set a new ExchangeRate.
     * @param pRate the new rate
     * @throws OceanusException on error
     */
    public final void setExchangeRate(final TethysRatio pRate) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.XCHANGERATE, pRate);
    }

    /**
     * Set a new ReturnedCash.
     * @param pValue the new returned cash
     * @throws OceanusException on error
     */
    public final void setReturnedCash(final TethysMoney pValue) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.RETURNEDCASH, pValue);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws OceanusException on error
     */
    public final void setReference(final String pReference) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.REFERENCE, pReference);
    }

    /**
     * Set new Comments.
     * @param pComments the new comments
     * @throws OceanusException on error
     */
    public final void setComments(final String pComments) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.COMMENTS, pComments);
    }

    /**
     * Set a new ReturnedCasah Account.
     * @param pAccount the new returned cash account
     * @throws OceanusException on error
     */
    public final void setReturnedCashAccount(final MoneyWiseTransAsset pAccount) throws OceanusException {
        setInfoSetValue(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, pAccount);
    }

    /**
     * Set the transaction tags.
     * @param pTags the tags
     * @throws OceanusException on error
     */
    public final void setTransactionTags(final List<MoneyWiseTransTag> pTags) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Link the value */
        theInfoSet.setListValue(MoneyWiseTransInfoClass.TRANSTAG, pTags);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    private void setInfoSetValue(final MoneyWiseTransInfoClass pInfoClass,
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
    public boolean applyChanges(final PrometheusDataItem pTrans) {
        /* Can only update from a transaction */
        if (!(pTrans instanceof MoneyWiseTransaction)) {
            return false;
        }
        final MoneyWiseTransaction myTrans = (MoneyWiseTransaction) pTrans;

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
            theInfoSet.wipeInfo(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS);
        }
        if (myPartDelta != null && myActDelta == null) {
            theInfoSet.wipeInfo(MoneyWiseTransInfoClass.PARTNERDELTAUNITS);
        }
    }

    /**
     * The Transaction List class.
     */
    public static class MoneyWiseTransactionList
            extends MoneyWiseTransBaseList<MoneyWiseTransaction> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransactionList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransactionList.class);

        /**
         * The TransactionInfo List.
         */
        private MoneyWiseTransInfoList theInfoList;

        /**
         * The TransactionInfoType list.
         */
        private MoneyWiseTransInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseTransactionList(final MoneyWiseDataSet pData) {
            super(pData, MoneyWiseTransaction.class, MoneyWiseBasicDataType.TRANSACTION);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected MoneyWiseTransactionList(final MoneyWiseTransactionList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTransactionList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTransaction.FIELD_DEFS;
        }

        /**
         * Obtain the transactionInfoList.
         * @return the transaction info list
         */
        public MoneyWiseTransInfoList getTransactionInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getTransactionInfo();
            }
            return theInfoList;
        }

        /**
         * Set the transactionInfoTypeList.
         * @param pInfoList the info type list
         */
        protected void setTransactionInfo(final MoneyWiseTransInfoList pInfoList) {
            theInfoList = pInfoList;
        }

        /**
         * Obtain the transactionInfoTypeList.
         * @return the transaction info type list
         */
        public MoneyWiseTransInfoTypeList getTransInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getTransInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Set the transactionInfoTypeList.
         * @param pInfoTypeList the info type list
         */
        protected void setTransInfoTypes(final MoneyWiseTransInfoTypeList pInfoTypeList) {
            theInfoTypeList = pInfoTypeList;
        }

        @Override
        protected MoneyWiseTransactionList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTransactionList myList = new MoneyWiseTransactionList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the list.
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public MoneyWiseTransaction addCopyItem(final PrometheusDataItem pItem) {
            if (pItem instanceof MoneyWiseTransaction) {
                final MoneyWiseTransaction myTrans = new MoneyWiseTransaction(this, (MoneyWiseTransaction) pItem);
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
        public MoneyWiseTransaction addNewItem() {
            /* Create a new Transaction */
            final MoneyWiseTransaction myTrans = new MoneyWiseTransaction(this);

            /* Set the Date as the start of the range */
            TethysDate myDate = new TethysDate();
            final TethysDateRange myRange = getDataSet().getDateRange();
            if (myRange.compareToDate(myDate) != 0) {
                myDate = myRange.getStart();
            }
            myTrans.setDate(myDate);

            /* Add to list and return */
            add(myTrans);
            return myTrans;
        }

        @Override
        public MoneyWiseTransaction addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the transaction */
            final MoneyWiseTransaction myTrans = new MoneyWiseTransaction(this, pValues);

            /* Check that this TransId has not been previously added */
            if (!isIdUnique(myTrans.getIndexedId())) {
                myTrans.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myTrans, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myTrans);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<PrometheusInfoItem> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final PrometheusInfoItem myItem = myIterator.next();

                    /* Build info */
                    final PrometheusDataValues myValues = myItem.getValues(myTrans);
                    getTransactionInfo().addValuesItem(myValues);
                }
            }

            /* Return it */
            return myTrans;
        }

        @Override
        protected PrometheusDataMapItem allocateDataMap() {
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
