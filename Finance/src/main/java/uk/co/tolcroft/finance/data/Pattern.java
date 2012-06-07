/*******************************************************************************
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
package uk.co.tolcroft.finance.data;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Frequency.FrequencyList;
import uk.co.tolcroft.finance.data.StaticClass.FreqClass;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.finance.views.Statement.StatementLine;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;

public class Pattern extends Event {
    /**
     * The name of the object
     */
    public static final String OBJECT_NAME = Pattern.class.getSimpleName();

    /**
     * The name of the object
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * The interesting date range
     */
    public final static DateDayRange thePatternRange = new DateDayRange(new DateDay(1999, Calendar.APRIL, 6),
            new DateDay(2000, Calendar.APRIL, 5));

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, Event.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /* Field IDs */
    public static final JDataField FIELD_ISCREDIT = FIELD_DEFS.declareEqualityValueField("IsCredit");
    public static final JDataField FIELD_FREQ = FIELD_DEFS.declareEqualityValueField("Frequency");
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");
    public static final JDataField FIELD_PARTNER = FIELD_DEFS.declareLocalField("Partner");

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* If the field is not an attribute handle normally */
        if (pField == FIELD_ACCOUNT) {
            return JDataObject.FIELD_SKIP;
        }
        if (pField == FIELD_PARTNER) {
            return JDataObject.FIELD_SKIP;
        }
        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * The active set of values
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public Frequency getFrequency() {
        return getFrequency(theValueSet);
    }

    public boolean isCredit() {
        return isCredit(theValueSet);
    }

    public Account getAccount() {
        return isCredit() ? getCredit() : getDebit();
    }

    public AccountType getActType() {
        Account myAccount = getAccount();
        return (myAccount == null) ? null : myAccount.getActType();
    }

    public Account getPartner() {
        return isCredit() ? getCredit() : getDebit();
    }

    public static Boolean isCredit(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ISCREDIT, Boolean.class);
    }

    public static Frequency getFrequency(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_FREQ, Frequency.class);
    }

    private void setValueIsCredit(Boolean isCredit) {
        theValueSet.setValue(FIELD_ISCREDIT, isCredit);
    }

    private void setValueFrequency(Frequency pFreq) {
        theValueSet.setValue(FIELD_FREQ, pFreq);
    }

    private void setValueFrequency(Integer pId) {
        theValueSet.setValue(FIELD_FREQ, pId);
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
     * Construct a copy of a Pattern
     * @param pList the list
     * @param pPattern The Pattern
     */
    protected Pattern(PatternList pList,
                      Pattern pPattern) {
        /* Simply initialise as Event */
        super(pList, (Event) pPattern);
    }

    /* Is this list locked */
    @Override
    public boolean isLocked() {
        return getAccount().isLocked();
    }

    /* Standard constructor for a newly inserted pattern */
    public Pattern(PatternList pList) {
        /* Initialise as Event */
        super(pList);

        /* Ensure that this is a Debit from this account */
        setDebit(pList.getAccount());
    }

    /* Construct a new pattern from a statement line */
    public Pattern(PatternList pList,
                   StatementLine pLine) {
        /* Set standard values */
        super(pList, pLine);
        pList.setNewId(this);

        /* Adjust the date so that it is in the correct range */
        DateDay myDate = new DateDay(getDate());
        while (myDate.compareTo(thePatternRange.getEnd()) > 0)
            myDate.adjustYear(-1);
        while (myDate.compareTo(thePatternRange.getStart()) < 0)
            myDate.adjustYear(1);
        setDate(myDate);
    }

    /* Standard constructor */
    private Pattern(PatternList pList,
                    int uId,
                    int uControlId,
                    int uAccountId,
                    Date pDate,
                    byte[] pDesc,
                    byte[] pAmount,
                    int uPartnerId,
                    int uTransId,
                    int uFreqId,
                    boolean isCredit) throws JDataException {
        /* Initialise item assuming account as debit and partner as credit */
        super(pList, uId, uControlId, pDate, pDesc, uAccountId, uPartnerId, uTransId, pAmount, null, null,
                null, null);

        /* Local variables */
        Frequency myFreq;

        /* Record the IDs */
        setValueFrequency(uFreqId);

        /* Access the Frequencys */
        FinanceData myData = pList.getData();
        myFreq = myData.getFrequencys().searchFor(uFreqId);
        if (myFreq == null)
            throw new JDataException(ExceptionClass.DATA, this, "Invalid Frequency Id");
        setValueFrequency(myFreq);

        /* Record the isCredit Flag */
        setValueIsCredit(isCredit);
    }

    /* Standard constructor */
    private Pattern(PatternList pList,
                    int uId,
                    Account pAccount,
                    Date pDate,
                    String pDesc,
                    String pAmount,
                    Account pPartner,
                    TransactionType pTransType,
                    Frequency pFrequency,
                    boolean isCredit) throws JDataException {
        /* Initialise item assuming account as debit and partner as credit */
        super(pList, uId, pDate, pDesc, pAccount, pPartner, pTransType, pAmount, null, null, null, null);

        /* Record the values */
        setValueFrequency(pFrequency);
        setValueIsCredit(isCredit);
    }

    /**
     * Compare this pattern to another to establish sort order.
     * @param pThat The Pattern to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is a Pattern */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a Pattern */
        Pattern myThat = (Pattern) pThat;

        /* If the date differs */
        if (this.getDate() != myThat.getDate()) {
            /* Handle null dates */
            if (this.getDate() == null)
                return 1;
            if (myThat.getDate() == null)
                return -1;

            /* Compare the dates */
            iDiff = getDate().compareTo(myThat.getDate());
            if (iDiff != 0)
                return iDiff;
        }

        /* Compare the accounts */
        iDiff = getAccount().compareTo(myThat.getAccount());
        if (iDiff != 0)
            return iDiff;

        /* If the descriptions differ */
        if (this.getDesc() != myThat.getDesc()) {
            /* Handle null descriptions */
            if (this.getDesc() == null)
                return 1;
            if (myThat.getDesc() == null)
                return -1;

            /* Compare the descriptions */
            iDiff = getDesc().compareTo(myThat.getDesc());
            if (iDiff < 0)
                return -1;
            if (iDiff > 0)
                return 1;
        }

        /* If the transaction types differ */
        if (this.getTransType() != myThat.getTransType()) {
            /* Handle null transaction types */
            if (this.getTransType() == null)
                return 1;
            if (myThat.getTransType() == null)
                return -1;

            /* Compare the transaction types */
            iDiff = getTransType().compareTo(myThat.getTransType());
            if (iDiff != 0)
                return iDiff;
        }

        /* Compare the IDs */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;
        return 0;
    }

    /**
     * Rebuild Links to partner data
     * @param pData the DataSet
     */
    @Override
    protected void reBuildLinks(FinanceData pData) {
        /* Update the Event details */
        super.reBuildLinks(pData);

        /* Access Lists */
        FrequencyList myFrequencys = pData.getFrequencys();

        /* Update frequency to use the local copy */
        Frequency myFreq = getFrequency();
        Frequency myNewFreq = myFrequencys.searchFor(myFreq.getId());
        setValueFrequency(myNewFreq);
    }

    /**
     * Validate the pattern
     */
    @Override
    public void validate() {
        /* Check that frequency is non-null */
        if (getFrequency() == null)
            addError("Frequency must be non-null", FIELD_FREQ);
        else if (!getFrequency().getEnabled())
            addError("Frequency must be enabled", FIELD_FREQ);

        /* Validate it */
        super.validate();
    }

    /**
     * Adjust date that is built from a pattern
     * @param pEvents the event list
     * @param pTaxYear the new tax year
     * @param pDate the data for the event
     * @return the new event
     * @throws JDataException
     */
    public Event nextEvent(EventList pEvents,
                           TaxYear pTaxYear,
                           DateDay pDate) throws JDataException {
        Event myEvent;
        TaxYear myBase;
        DateDay myDate;
        FreqClass myFreq;
        int iAdjust;
        TaxYearList myList;

        /* Access the frequency */
        myFreq = getFrequency().getFrequency();

        /* Access the Tax Year list */
        FinanceData myData = pEvents.getData();
        myList = myData.getTaxYears();

        /* If this is the first request for an event */
        if (pDate.compareTo(getDate()) == 0) {
            /* If the frequency is maturity */
            if (myFreq == FreqClass.MATURITY) {
                /* Access the maturity date */
                myDate = getAccount().getMaturity();

                /* Obtain the relevant tax year */
                myBase = myList.searchFor(getDate());

                /* Ignore if no maturity or else not this year */
                if ((myDate == null) || (myBase == null) || (myBase.compareTo(pTaxYear) != 0))
                    return null;
            }

            /* Calculate the difference in years */
            iAdjust = pTaxYear.getTaxYear().getYear() - thePatternRange.getEnd().getYear();

            /* Adjust the date to fall into the tax year */
            pDate.copyDate(getDate());
            pDate.adjustYear(iAdjust);
        }

        /* else this is a secondary access */
        else {
            /* switch on frequency type */
            switch (myFreq) {
            /* Annual and maturity patterns only generate single event */
                case ANNUALLY:
                case MATURITY:
                    return null;

                    /* Monthly and TenMonthly add one month */
                case MONTHLY:
                case TENMONTHS:
                    pDate.adjustMonth(1);
                    break;

                /* Quarterly add three months */
                case QUARTERLY:
                    pDate.adjustMonth(3);
                    break;

                /* HalfYearly add six months */
                case HALFYEARLY:
                    pDate.adjustMonth(6);
                    break;

                /* EndMonthly shift to end of next month */
                case ENDOFMONTH:
                    pDate.endNextMonth();
                    break;
            }

            /* If we are beyond the end of the year we have finished */
            if (pDate.compareTo(pTaxYear.getTaxYear()) > 0)
                return null;

            /* If this is a ten month repeat */
            if (myFreq == FreqClass.TENMONTHS) {
                myDate = new DateDay(getDate());

                /* Calculate the difference in years */
                iAdjust = pTaxYear.getTaxYear().getYear() - thePatternRange.getEnd().getYear();

                /* Adjust the date to fall into the tax year */
                myDate.copyDate(getDate());
                myDate.adjustYear(iAdjust);

                /* Add 9 months to get to last date */
                myDate.adjustMonth(9);

                /* If we are beyond this date then we have finished */
                if (pDate.compareTo(myDate) > 0)
                    return null;
            }
        }

        /* Build the new linked event */
        myEvent = new Event(pEvents, this);

        /* Set the date for this event */
        myEvent.setDate(new DateDay(pDate));

        /* Return the new event */
        return myEvent;
    }

    /**
     * Set a new partner
     * 
     * @param pPartner the account
     */
    public void setPartner(Account pPartner) {
        if (isCredit())
            setDebit(pPartner);
        else
            setCredit(pPartner);
    }

    /**
     * Set a new frequency
     * 
     * @param pFrequency the frequency
     */
    public void setFrequency(Frequency pFrequency) {
        setValueFrequency(pFrequency);
    }

    /**
     * Set a new isCredit indication
     * 
     * @param isCredit
     */
    public void setIsCredit(boolean isCredit) {
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
     * Update Pattern from a pattern extract
     * @param pPattern the pattern extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(DataItem<?> pPattern) {
        Pattern myPattern = (Pattern) pPattern;
        boolean bChanged = false;

        /* Store the current detail into history */
        pushHistory();

        /* Update the isCredit if required */
        if (isCredit() != myPattern.isCredit())
            setValueIsCredit(myPattern.isCredit());

        /* Update the partner if required */
        if (!Difference.isEqual(getPartner(), myPattern.getPartner()))
            setPartner(myPattern.getPartner());

        /* Update the transtype if required */
        if (!Difference.isEqual(getTransType(), myPattern.getTransType()))
            setTransType(myPattern.getTransType());

        /* Update the frequency if required */
        if (!Difference.isEqual(getFrequency(), myPattern.getFrequency()))
            setFrequency(myPattern.getFrequency());

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myPattern.getDesc()))
            setValueDesc(myPattern.getDescField());

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), myPattern.getAmount()))
            setValueAmount(myPattern.getAmountField());

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), myPattern.getDate()))
            setDate(myPattern.getDate());

        /* Check for changes */
        if (checkForHistory()) {
            /* Mark as changed */
            setState(DataState.CHANGED);
            bChanged = true;
        }

        /* Return to caller */
        return bChanged;
    }

    /**
     * Add an error for this item
     * @param pError the error text
     * @param iField the associated field
     */
    @Override
    protected void addError(String pError,
                            JDataField iField) {
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

    public static class PatternList extends EventList {
        /* Local values */
        private Account theAccount = null;

        /* Access Extra Variables correctly */
        @Override
        public FinanceData getData() {
            return (FinanceData) super.getData();
        }

        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE pattern list
         * @param pData the DataSet for the list
         */
        protected PatternList(FinanceData pData) {
            super(pData);
            setRange(thePatternRange);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private PatternList(PatternList pSource) {
            super(pSource);
            setRange(thePatternRange);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the style
         * @return the update Extract
         */
        private PatternList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            PatternList myList = new PatternList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public PatternList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public PatternList getEditList() {
            return null;
        }

        @Override
        public PatternList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public PatternList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            PatternList myList = new PatternList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        /**
         * Construct a difference Pattern list
         * @param pOld the old Pattern list
         * @return the difference list
         */
        protected PatternList getDifferences(PatternList pOld) {
            /* Build an empty Difference List */
            PatternList myList = new PatternList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Construct an edit extract of a Pattern list
         * @param pAccount The account to extract patterns for
         * @return the edit list
         */
        public PatternList getEditList(Account pAccount) {
            /* Build an empty Update */
            PatternList myList = new PatternList(this);

            /* Make this list the correct style */
            myList.setStyle(ListStyle.EDIT);

            /* Local variables */
            Event myCurr;
            Pattern myItem;
            DataListIterator<Event> myIterator;

            /* Store the account */
            myList.theAccount = pAccount;

            /* Access the list iterator */
            myIterator = listIterator(true);

            /* Loop through the Prices */
            while ((myCurr = myIterator.next()) != null) {
                /* Check the account */
                myItem = (Pattern) myCurr;
                int myResult = pAccount.compareTo(myItem.getAccount());

                /* Skip differing accounts */
                if (myResult != 0)
                    continue;

                /* Copy the item */
                myItem = new Pattern(myList, myItem);
                myList.add(myItem);
            }

            /* Return the List */
            return myList;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return (theAccount != null) && (theAccount.isLocked());
        }

        /**
         * Add a new item to the core list
         * @param pPattern item
         * @return the newly added item
         */
        @Override
        public Pattern addNewItem(DataItem<?> pPattern) {
            Pattern myPattern = new Pattern(this, (Pattern) pPattern);
            add(myPattern);
            return myPattern;
        }

        /**
         * Add a new item to the edit list
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
         * Obtain the type of the item
         * @return the type of the item
         */
        @Override
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Mark Active items
         */
        public void markActiveItems() {
            DataListIterator<Event> myIterator;
            Event myCurr;
            Pattern myItem;

            /* Access the list iterator */
            myIterator = listIterator();

            /* Loop through the Prices */
            while ((myCurr = myIterator.next()) != null) {
                /* * Access as a pattern */
                myItem = (Pattern) myCurr;

                /* Touch the patterned account */
                myItem.getAccount().touchItem(myCurr);

                /* Touch the patterned partner */
                myItem.getPartner().touchItem(myCurr);

                /* Touch the patterned frequency */
                myItem.getFrequency().touchItem(myCurr);

                /* Touch the patterned transaction type */
                myItem.getTransType().touchItem(myCurr);
            }
        }

        /**
         * Allow a pattern to be added
         * @param uId
         * @param pDate
         * @param pDesc
         * @param pAmount
         * @param pAccount
         * @param pPartner
         * @param pTransType
         * @param pFrequency
         * @param isCredit
         * @throws JDataException
         */
        public void addItem(int uId,
                            Date pDate,
                            String pDesc,
                            String pAmount,
                            String pAccount,
                            String pPartner,
                            String pTransType,
                            String pFrequency,
                            boolean isCredit) throws JDataException {
            TransTypeList myTranTypes;
            FrequencyList myFrequencies;
            Account.AccountList myAccounts;
            Account myAccount;
            Account myPartner;
            TransactionType myTransType;
            Frequency myFrequency;
            Pattern myPattern;
            FinanceData myData;

            /* Access the Lists */
            myData = getData();
            myAccounts = myData.getAccounts();
            myTranTypes = myData.getTransTypes();
            myFrequencies = myData.getFrequencys();

            /* Look up the Account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Account [" + pAccount
                        + "]");

            /* Look up the Partner */
            myPartner = myAccounts.searchFor(pPartner);
            if (myPartner == null)
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Partner [" + pPartner
                        + "]");

            /* Look up the TransType */
            myTransType = myTranTypes.searchFor(pTransType);
            if (myTransType == null)
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid TransType ["
                        + pTransType + "]");

            /* Look up the Frequency */
            myFrequency = myFrequencies.searchFor(pFrequency);
            if (myFrequency == null)
                throw new JDataException(ExceptionClass.DATA, "Pattern on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Frequency ["
                        + pFrequency + "]");

            /* Create the new pattern */
            myPattern = new Pattern(this, uId, myAccount, pDate, pDesc, pAmount, myPartner, myTransType,
                    myFrequency, isCredit);

            /* Validate the pattern */
            myPattern.validate();

            /* Handle validation failure */
            if (myPattern.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myPattern, "Failed validation");

            /* Add to the list */
            add(myPattern);
        }

        /**
         * Allow a pattern to be added
         * @param uId
         * @param uControlId
         * @param pDate
         * @param pDesc
         * @param pAmount
         * @param uAccountId
         * @param uPartnerId
         * @param uTransId
         * @param uFreqId
         * @param isCredit
         * @throws JDataException
         */
        public void addItem(int uId,
                            int uControlId,
                            Date pDate,
                            byte[] pDesc,
                            byte[] pAmount,
                            int uAccountId,
                            int uPartnerId,
                            int uTransId,
                            int uFreqId,
                            boolean isCredit) throws JDataException {
            Pattern myPattern;

            /* Create the new pattern */
            myPattern = new Pattern(this, uId, uControlId, uAccountId, pDate, pDesc, pAmount, uPartnerId,
                    uTransId, uFreqId, isCredit);

            /* Check that this PatternId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, "Duplicate PatternId <" + uId + ">");

            /* Validate the pattern */
            myPattern.validate();

            /* Handle validation failure */
            if (myPattern.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myPattern, "Failed validation");

            /* Add to the list */
            add(myPattern);
        }
    }
}
