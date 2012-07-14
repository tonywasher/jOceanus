/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JFinanceApp.data.Account.AccountList;
import net.sourceforge.JFinanceApp.data.Frequency.FrequencyList;
import net.sourceforge.JFinanceApp.data.StaticClass.FreqClass;
import net.sourceforge.JFinanceApp.data.TaxYear.TaxYearList;
import net.sourceforge.JFinanceApp.data.TransactionType.TransTypeList;
import net.sourceforge.JFinanceApp.views.Statement.StatementLine;

/**
 * Pattern data type.
 * @author Tony Washer
 */
public class Pattern extends Event {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = Pattern.class.getSimpleName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Interesting TaxYear.
     */
    public static final int BASE_TAXYEAR = 2000;

    /**
     * TenMonths maximum adjustment.
     */
    private static final int MAX_TENMONTHS = 9;

    /**
     * The interesting date range.
     */
    public static final DateDayRange RANGE_PATTERN = new DateDayRange(new DateDay(BASE_TAXYEAR - 1,
            Calendar.APRIL, TaxYear.END_OF_MONTH_DAY + 1), new DateDay(BASE_TAXYEAR, Calendar.APRIL,
            TaxYear.END_OF_MONTH_DAY));

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, Event.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Credit Field Id.
     */
    public static final JDataField FIELD_ISCREDIT = FIELD_DEFS.declareEqualityValueField("IsCredit");

    /**
     * Frequency Field Id.
     */
    public static final JDataField FIELD_FREQ = FIELD_DEFS.declareEqualityValueField("Frequency");

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    /**
     * Partner Field Id.
     */
    public static final JDataField FIELD_PARTNER = FIELD_DEFS.declareLocalField("Partner");

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* If the field is not an attribute handle normally */
        if (FIELD_ACCOUNT.equals(pField)) {
            return JDataFieldValue.SkipField;
        }
        if (FIELD_PARTNER.equals(pField)) {
            return JDataFieldValue.SkipField;
        }
        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Obtain Frequency.
     * @return the frequency
     */
    public Frequency getFrequency() {
        return getFrequency(getValueSet());
    }

    /**
     * Is this a credit to the account.
     * @return true/false
     */
    public boolean isCredit() {
        return isCredit(getValueSet());
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public Account getAccount() {
        return isCredit() ? getCredit() : getDebit();
    }

    /**
     * Obtain Account Type.
     * @return the type
     */
    public AccountType getActType() {
        Account myAccount = getAccount();
        return (myAccount == null) ? null : myAccount.getActType();
    }

    /**
     * Obtain Partner.
     * @return the partner
     */
    public Account getPartner() {
        return isCredit() ? getDebit() : getCredit();
    }

    /**
     * Is this a credit to account.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isCredit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ISCREDIT, Boolean.class);
    }

    /**
     * Obtain Frequency.
     * @param pValueSet the valueSet
     * @return the Frequency
     */
    public static Frequency getFrequency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_FREQ, Frequency.class);
    }

    /**
     * Set isCredit flag.
     * @param isCredit true/false
     */
    private void setValueIsCredit(final Boolean isCredit) {
        getValueSet().setValue(FIELD_ISCREDIT, isCredit);
    }

    /**
     * Set frequency value.
     * @param pValue the frequency
     */
    private void setValueFrequency(final Frequency pValue) {
        getValueSet().setValue(FIELD_FREQ, pValue);
    }

    /**
     * Set frequency id.
     * @param pId the frequency id
     */
    private void setValueFrequency(final Integer pId) {
        getValueSet().setValue(FIELD_FREQ, pId);
    }

    @Override
    public Pattern getBase() {
        return (Pattern) super.getBase();
    }

    @Override
    protected boolean requiredInfoSet() {
        return false;
    }

    /**
     * Construct a copy of a Pattern.
     * @param pList the list
     * @param pPattern The Pattern
     */
    protected Pattern(final PatternList pList,
                      final Pattern pPattern) {
        /* Simply initialise as Event */
        super(pList, (Event) pPattern);
    }

    @Override
    public boolean isLocked() {
        return getAccount().isLocked();
    }

    /**
     * Constructor.
     * @param pList the list
     */
    public Pattern(final PatternList pList) {
        /* Initialise as Event */
        super(pList);

        /* Ensure that this is a Debit from this account */
        setDebit(pList.getAccount());
    }

    /**
     * Construct a new pattern from a statement line.
     * @param pList the list
     * @param pLine the line
     */
    public Pattern(final PatternList pList,
                   final StatementLine pLine) {
        /* Set standard values */
        super(pList, pLine);

        /* Adjust the date so that it is in the correct range */
        DateDay myDate = new DateDay(getDate());
        while (myDate.compareTo(RANGE_PATTERN.getEnd()) > 0) {
            myDate.adjustYear(-1);
        }
        while (myDate.compareTo(RANGE_PATTERN.getStart()) < 0) {
            myDate.adjustYear(1);
        }
        setDate(myDate);
    }

    /**
     * Constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control Id
     * @param uAccountId the account Id
     * @param pDate the date
     * @param pDesc the description
     * @param pAmount the amount
     * @param uPartnerId the partner id
     * @param uTransId the transaction id
     * @param uFreqId the frequency id
     * @param isCredit true/false
     * @throws JDataException on error
     */
    private Pattern(final PatternList pList,
                    final int uId,
                    final int uControlId,
                    final int uAccountId,
                    final Date pDate,
                    final byte[] pDesc,
                    final byte[] pAmount,
                    final int uPartnerId,
                    final int uTransId,
                    final int uFreqId,
                    final boolean isCredit) throws JDataException {
        /* Initialise item assuming account as debit and partner as credit */
        super(pList, uId, uControlId, pDate, pDesc, (isCredit) ? uPartnerId : uAccountId,
                (isCredit) ? uAccountId : uPartnerId, uTransId, pAmount, null, null, null, null);

        /* Record the IDs */
        setValueFrequency(uFreqId);

        /* Access the Frequencies */
        FinanceData myData = getDataSet();
        FrequencyList myFrequencies = myData.getFrequencys();
        Frequency myFreq = myFrequencies.findItemById(uFreqId);
        if (myFreq == null) {
            throw new JDataException(ExceptionClass.DATA, this, "Invalid Frequency Id");
        }
        setValueFrequency(myFreq);

        /* Record the isCredit Flag */
        setValueIsCredit(isCredit);
    }

    /**
     * Standard constructor.
     * @param pList the list
     * @param uId the id
     * @param pAccount the account
     * @param pDate the date
     * @param pDesc the description
     * @param pAmount the amount
     * @param pPartner the partner
     * @param pTransType the transaction type
     * @param pFrequency the frequency
     * @param isCredit true/false
     * @throws JDataException on error
     */
    private Pattern(final PatternList pList,
                    final int uId,
                    final Account pAccount,
                    final Date pDate,
                    final String pDesc,
                    final String pAmount,
                    final Account pPartner,
                    final TransactionType pTransType,
                    final Frequency pFrequency,
                    final boolean isCredit) throws JDataException {
        /* Initialise item assuming account as debit and partner as credit */
        super(pList, uId, pDate, pDesc, (isCredit) ? pPartner : pAccount, (isCredit) ? pAccount : pPartner,
                pTransType, pAmount, null, null, null, null);

        /* Record the values */
        setValueFrequency(pFrequency);
        setValueIsCredit(isCredit);
    }

    @Override
    protected void relinkToDataSet() {
        /* Update the Event details */
        super.relinkToDataSet();

        /* Access Lists */
        FinanceData myData = getDataSet();
        FrequencyList myFrequencys = myData.getFrequencys();

        /* Update frequency to use the local copy */
        Frequency myFreq = getFrequency();
        Frequency myNewFreq = myFrequencys.findItemById(myFreq.getId());
        setValueFrequency(myNewFreq);
    }

    /**
     * Validate the pattern.
     */
    @Override
    public void validate() {
        /* Check that frequency is non-null */
        if (getFrequency() == null) {
            addError("Frequency must be non-null", FIELD_FREQ);
        } else if (!getFrequency().getEnabled()) {
            addError("Frequency must be enabled", FIELD_FREQ);
        }

        /* Validate it */
        super.validate();
    }

    /**
     * Adjust date that is built from a pattern.
     * @param pEvents the event list
     * @param pTaxYear the new tax year
     * @param pDate the date for the event
     * @return the new event
     * @throws JDataException on error
     */
    public Event nextEvent(final EventList pEvents,
                           final TaxYear pTaxYear,
                           final DateDay pDate) throws JDataException {
        /* Access the frequency */
        FreqClass myFreq = getFrequency().getFrequency();
        DateDay myDate;
        int iAdjust;

        /* Access the Tax Year list */
        FinanceData myData = getDataSet();
        TaxYearList myList = myData.getTaxYears();

        /* If this is the first request for an event */
        if (pDate.compareTo(getDate()) == 0) {
            /* If the frequency is maturity */
            if (myFreq == FreqClass.MATURITY) {
                /* Access the maturity date */
                myDate = getAccount().getMaturity();

                /* Obtain the relevant tax year */
                TaxYear myBase = myList.findTaxYearForDate(getDate());

                /* Ignore if no maturity or else not this year */
                if ((myDate == null) || (myBase == null) || (myBase.compareTo(pTaxYear) != 0)) {
                    return null;
                }
            }

            /* Calculate the difference in years */
            iAdjust = pTaxYear.getTaxYear().getYear() - RANGE_PATTERN.getEnd().getYear();

            /* Adjust the date to fall into the tax year */
            pDate.copyDate(getDate());
            pDate.adjustYear(iAdjust);

            /* else this is a secondary access */
        } else {
            /* switch on frequency type */
            switch (myFreq) {
            /* Monthly etc add relevant months */
                case MONTHLY:
                case TENMONTHS:
                case QUARTERLY:
                case HALFYEARLY:
                    pDate.adjustMonth(myFreq.getAdjustment());
                    break;

                /* EndMonthly shift to end of next month */
                case ENDOFMONTH:
                    pDate.endNextMonth();
                    break;
                /* Annual and maturity patterns only generate single event */
                case ANNUALLY:
                case MATURITY:
                default:
                    return null;
            }

            /* If we are beyond the end of the year we have finished */
            if (pDate.compareTo(pTaxYear.getTaxYear()) > 0) {
                return null;
            }

            /* If this is a ten month repeat */
            if (myFreq == FreqClass.TENMONTHS) {
                myDate = new DateDay(getDate());

                /* Calculate the difference in years */
                iAdjust = pTaxYear.getTaxYear().getYear() - RANGE_PATTERN.getEnd().getYear();

                /* Adjust the date to fall into the tax year */
                myDate.copyDate(getDate());
                myDate.adjustYear(iAdjust);

                /* Add 9 months to get to last date */
                myDate.adjustMonth(MAX_TENMONTHS);

                /* If we are beyond this date then we have finished */
                if (pDate.compareTo(myDate) > 0) {
                    return null;
                }
            }
        }

        /* Build the new linked event */
        Event myEvent = new Event(pEvents, this);

        /* Set the date for this event */
        myEvent.setDate(new DateDay(pDate));

        /* Return the new event */
        return myEvent;
    }

    /**
     * Set a new partner.
     * @param pPartner the account
     */
    public void setPartner(final Account pPartner) {
        if (isCredit()) {
            setDebit(pPartner);
        } else {
            setCredit(pPartner);
        }
    }

    /**
     * Set a new frequency.
     * @param pFrequency the frequency
     */
    public void setFrequency(final Frequency pFrequency) {
        setValueFrequency(pFrequency);
    }

    /**
     * Set a new isCredit indication.
     * @param isCredit true/false
     */
    public void setIsCredit(final boolean isCredit) {
        /* If we are changing values */
        if (isCredit != isCredit()) {
            /* Swap credit/debit values */
            Account myTemp = getCredit();
            setCredit(getDebit());
            setDebit(myTemp);
        }
        setValueIsCredit(isCredit);
    }

    /**
     * Update Pattern from a pattern extract.
     * @param pPattern the pattern extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pPattern) {
        /* Can only update from Pattern */
        if (!(pPattern instanceof Pattern)) {
            return false;
        }

        Pattern myPattern = (Pattern) pPattern;

        /* Store the current detail into history */
        pushHistory();

        /* Update the isCredit if required */
        if (isCredit() != myPattern.isCredit()) {
            setValueIsCredit(myPattern.isCredit());
        }

        /* Update the partner if required */
        if (!Difference.isEqual(getPartner(), myPattern.getPartner())) {
            setPartner(myPattern.getPartner());
        }

        /* Update the transtype if required */
        if (!Difference.isEqual(getTransType(), myPattern.getTransType())) {
            setTransType(myPattern.getTransType());
        }

        /* Update the frequency if required */
        if (!Difference.isEqual(getFrequency(), myPattern.getFrequency())) {
            setFrequency(myPattern.getFrequency());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myPattern.getDesc())) {
            setValueDesc(myPattern.getDescField());
        }

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), myPattern.getAmount())) {
            setValueAmount(myPattern.getAmountField());
        }

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), myPattern.getDate())) {
            setDate(myPattern.getDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Add an error for this item.
     * @param pError the error text
     * @param iField the associated field
     */
    @Override
    protected void addError(final String pError,
                            final JDataField iField) {
        JDataField myField = iField;
        /* Re-Map Credit/Debit field errors */
        if (iField == FIELD_CREDIT) {
            myField = isCredit() ? FIELD_ACCOUNT : FIELD_PARTNER;
        } else if (iField == FIELD_DEBIT) {
            myField = isCredit() ? FIELD_PARTNER : FIELD_ACCOUNT;
        }

        /* Call super class */
        super.addError(pError, myField);
    }

    /**
     * The list.
     */
    public static class PatternList extends EventList {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PatternList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        /**
         * Account field id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return (theAccount == null) ? JDataFieldValue.SkipField : theAccount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The Account.
         */
        private Account theAccount = null;

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE pattern list.
         * @param pData the DataSet for the list
         */
        protected PatternList(final FinanceData pData) {
            super(pData);
            setRange(RANGE_PATTERN);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PatternList(final PatternList pSource) {
            super(pSource);
            setRange(RANGE_PATTERN);
        }

        @Override
        protected PatternList getEmptyList() {
            return new PatternList(this);
        }

        @Override
        public PatternList deriveList(final ListStyle pStyle) {
            return (PatternList) super.deriveList(pStyle);
        }

        @Override
        public PatternList deriveDifferences(final EventList pSource) {
            return (PatternList) super.deriveDifferences(pSource);
        }

        /**
         * Construct an edit extract of a Pattern list.
         * @param pAccount The account to extract patterns for
         * @return the edit list
         */
        public PatternList deriveEditList(final Account pAccount) {
            /* Build an empty Update */
            PatternList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);
            myList.theAccount = pAccount;

            /* Access the list iterator */
            Iterator<Event> myIterator = listIterator();

            /* Loop through the Prices */
            while (myIterator.hasNext()) {
                Pattern myCurr = (Pattern) myIterator.next();

                /* Skip differing accounts */
                if (!pAccount.equals(myCurr.getAccount())) {
                    continue;
                }

                /* Copy the item */
                Pattern myItem = new Pattern(myList, myCurr);
                myList.addAtEnd(myItem);
            }

            /* Return the List */
            return myList;
        }

        @Override
        public boolean isLocked() {
            return (theAccount != null) && (theAccount.isLocked());
        }

        /**
         * Add a new item to the core list.
         * @param pPattern item
         * @return the newly added item
         */
        @Override
        public Pattern addNewItem(final DataItem pPattern) {
            /* Can only clone from Pattern */
            if (!(pPattern instanceof Pattern)) {
                return null;
            }

            Pattern myPattern = new Pattern(this, (Pattern) pPattern);
            add(myPattern);
            return myPattern;
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public Pattern addNewItem() {
            Pattern myPattern = new Pattern(this);

            /* Set the Date as the start of the range */
            myPattern.setDate(getRange().getStart());
            add(myPattern);
            return myPattern;
        }

        /**
         * Mark Active items.
         */
        public void markActiveItems() {
            /* Access the list iterator */
            Iterator<Event> myIterator = listIterator();

            /* Loop through the Prices */
            while (myIterator.hasNext()) {
                Pattern myCurr = (Pattern) myIterator.next();

                /* Touch the patterned account */
                myCurr.getAccount().touchItem(myCurr);

                /* Touch the patterned partner */
                myCurr.getPartner().touchItem(myCurr);

                /* Touch the patterned frequency */
                myCurr.getFrequency().touchItem(myCurr);

                /* Touch the patterned transaction type */
                myCurr.getTransType().touchItem(myCurr);
            }
        }

        /**
         * Allow a pattern to be added.
         * @param uId the id
         * @param pDate the date
         * @param pDesc the description
         * @param pAmount the amount
         * @param pAccount the account
         * @param pPartner the partner
         * @param pTransType the transaction type
         * @param pFrequency the frequency
         * @param isCredit true/false
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final Date pDate,
                            final String pDesc,
                            final String pAmount,
                            final String pAccount,
                            final String pPartner,
                            final String pTransType,
                            final String pFrequency,
                            final boolean isCredit) throws JDataException {
            /* Access the Lists */
            FinanceData myData = getDataSet();
            AccountList myAccounts = myData.getAccounts();
            TransTypeList myTranTypes = myData.getTransTypes();
            FrequencyList myFrequencies = myData.getFrequencys();

            /* Look up the Account */
            Account myAccount = myAccounts.findItemByName(pAccount);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Account [" + pAccount
                        + "]");
            }

            /* Look up the Partner */
            Account myPartner = myAccounts.findItemByName(pPartner);
            if (myPartner == null) {
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Partner [" + pPartner
                        + "]");
            }

            /* Look up the TransType */
            TransactionType myTransType = myTranTypes.findItemByName(pTransType);
            if (myTransType == null) {
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid TransType ["
                        + pTransType + "]");
            }

            /* Look up the Frequency */
            Frequency myFrequency = myFrequencies.findItemByName(pFrequency);
            if (myFrequency == null) {
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Frequency ["
                        + pFrequency + "]");
            }

            /* Create the new pattern */
            Pattern myPattern = new Pattern(this, uId, myAccount, pDate, pDesc, pAmount, myPartner,
                    myTransType, myFrequency, isCredit);

            /* Validate the pattern */
            myPattern.validate();

            /* Handle validation failure */
            if (myPattern.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myPattern, "Failed validation");
            }

            /* Add to the list */
            add(myPattern);
        }

        /**
         * Allow a pattern to be added.
         * @param uId the id
         * @param uControlId the control id
         * @param pDate the date
         * @param pDesc the description
         * @param pAmount the amount
         * @param uAccountId the account id
         * @param uPartnerId the partner id
         * @param uTransId the transaction type id
         * @param uFreqId the frequency id
         * @param isCredit true/false
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uControlId,
                            final Date pDate,
                            final byte[] pDesc,
                            final byte[] pAmount,
                            final int uAccountId,
                            final int uPartnerId,
                            final int uTransId,
                            final int uFreqId,
                            final boolean isCredit) throws JDataException {
            /* Create the new pattern */
            Pattern myPattern = new Pattern(this, uId, uControlId, uAccountId, pDate, pDesc, pAmount,
                    uPartnerId, uTransId, uFreqId, isCredit);

            /* Check that this PatternId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, "Duplicate PatternId <" + uId + ">");
            }

            /* Validate the pattern */
            myPattern.validate();

            /* Handle validation failure */
            if (myPattern.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myPattern, "Failed validation");
            }

            /* Add to the list */
            addAtEnd(myPattern);
        }
    }
}