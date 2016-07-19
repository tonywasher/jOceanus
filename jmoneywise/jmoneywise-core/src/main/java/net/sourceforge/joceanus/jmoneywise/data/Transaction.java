/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
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
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
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
    public static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * EventInfoSet field Id.
     */
    private static final MetisField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

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
        MetisDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Date */
            Object myValue = pValues.getValue(FIELD_DATE);
            if (myValue instanceof TethysDate) {
                setValueDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                TethysDateFormatter myParser = myFormatter.getDateFormatter();
                setValueDate(myParser.parseDateDay((String) myValue));
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
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_DATE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : MetisFieldValue.SKIP;
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
    }

    @Override
    public TransactionInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Debit Units.
     * @return the Debit Units
     */
    public final TethysUnits getDebitUnits() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.DEBITUNITS, TethysUnits.class)
                          : null;
    }

    /**
     * Obtain Credit Units.
     * @return the Credit Units
     */
    public final TethysUnits getCreditUnits() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.CREDITUNITS, TethysUnits.class)
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
     * Obtain credit date.
     * @return the credit date
     */
    public final TethysDate getCreditDate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.CREDITDATE, TethysDate.class)
                          : null;
    }

    /**
     * Obtain National Insurance.
     * @return the NatInsurance
     */
    public final TethysMoney getNatInsurance() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.NATINSURANCE, TethysMoney.class)
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
     * Obtain Pension.
     * @return the Pension
     */
    public final TethysMoney getPension() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.PENSION, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Donation.
     * @return the Donation
     */
    public final TethysMoney getCharityDonation() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.CHARITYDONATION, TethysMoney.class)
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
    public final TethysMoney getPartnerAmount() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.PARTNERAMOUNT, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Exchange Rate.
     * @return the Donation
     */
    public final TethysRatio getExchangeRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TransactionInfoClass.XCHANGERATE, TethysRatio.class)
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
    public MetisEditState getEditState() {
        /* Pop history for self */
        MetisEditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == MetisEditState.CLEAN) && useInfoSet) {
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
    public MetisDifference fieldChanged(final MetisField pField) {
        /* Handle InfoSet fields */
        TransactionInfoClass myClass = TransactionInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : MetisDifference.IDENTICAL;
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
        int iDiff = MetisDifference.compareObject(getDate(), pThat.getDate());
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
        TethysDate myDate = getDate();
        TransactionAsset myAccount = getAccount();
        TransactionAsset myPartner = getPartner();
        TransactionCategory myCategory = getCategory();
        TethysDilution myDilution = getDilution();
        TethysUnits myDebitUnits = getDebitUnits();
        TethysUnits myCreditUnits = getCreditUnits();

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Determine date range to check for */
        TransactionList myList = getList();
        TethysDateRange myRange = myList.getValidDateRange();

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
        if ((myCreditUnits != null) && (myDebitUnits != null) && (MetisDifference.isEqual(myAccount, myPartner))) {
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

        /* Return no deposit */
        return null;
    }

    /**
     * Calculate the tax credit for a transaction.
     * @return the calculated tax credit
     */
    public final TethysMoney calculateTaxCredit() {
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
        TethysRate myRate = isInterest()
                                    ? myTax.getIntTaxRate()
                                    : myTax.getDivTaxRate();

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
     * Set a new Debit Units.
     * @param pUnits the new units
     * @throws OceanusException on error
     */
    public final void setDebitUnits(final TethysUnits pUnits) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.DEBITUNITS, pUnits);
    }

    /**
     * Set a new Credit Units.
     * @param pUnits the new units
     * @throws OceanusException on error
     */
    public final void setCreditUnits(final TethysUnits pUnits) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.CREDITUNITS, pUnits);
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
     * Set a new Credit Date.
     * @param pCreditDate the new credit date
     * @throws OceanusException on error
     */
    public final void setCreditDate(final TethysDate pCreditDate) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.CREDITDATE, pCreditDate);
    }

    /**
     * Set a new NatInsurance.
     * @param pNatIns the new insurance
     * @throws OceanusException on error
     */
    public final void setNatInsurance(final TethysMoney pNatIns) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.NATINSURANCE, pNatIns);
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
     * Set a new Pension.
     * @param pPension the new pension
     * @throws OceanusException on error
     */
    public final void setPension(final TethysMoney pPension) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.PENSION, pPension);
    }

    /**
     * Set a new Donation.
     * @param pDonation the new donation
     * @throws OceanusException on error
     */
    public final void setDonation(final TethysMoney pDonation) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.CHARITYDONATION, pDonation);
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
     * @param pValue the new exchangeRate
     * @throws OceanusException on error
     */
    public final void setXchangeRate(final TethysRate pValue) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.XCHANGERATE, pValue);
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
     * Set a new ThirdParty.
     * @param pParty the new thirdParty
     * @throws OceanusException on error
     */
    public final void setThirdParty(final Deposit pParty) throws OceanusException {
        setInfoSetValue(TransactionInfoClass.THIRDPARTY, pParty);
    }

    /**
     * Set a transaction tag.
     * @param pTag the tag
     * @throws OceanusException on error
     */
    public final void setTransactionTag(final TransactionTag pTag) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Link the value */
        theInfoSet.linkValue(TransactionInfoClass.TRANSTAG, pTag);
    }

    /**
     * Clear a transaction tag.
     * @param pTag the tag
     * @throws OceanusException on error
     */
    public final void clearTransactionTag(final TransactionTag pTag) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
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
        Transaction myTrans = (Transaction) pTrans;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Date if required */
        if (!MetisDifference.isEqual(getDate(), myTrans.getDate())) {
            setValueDate(myTrans.getDate());
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
        protected static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataList.FIELD_DEFS);

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
        public MetisFields declareFields() {
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
        public Transaction addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the transaction */
            Transaction myTrans = new Transaction(this, pValues);

            /* Check that this TransId has not been previously added */
            if (!isIdUnique(myTrans.getId())) {
                myTrans.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myTrans, ERROR_VALIDATION);
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
        public void postProcessOnLoad() throws OceanusException {
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
