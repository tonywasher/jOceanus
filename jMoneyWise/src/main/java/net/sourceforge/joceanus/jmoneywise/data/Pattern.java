/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import java.util.Calendar;
import java.util.Iterator;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.FrequencyClass;

/**
 * Pattern data type.
 * @author Tony Washer
 */
public class Pattern
        extends EventBase {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = Pattern.class.getSimpleName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Interesting TaxYear.
     */
    public static final int BASE_TAXYEAR = 2000;

    /**
     * The interesting date range.
     */
    public static final JDateDayRange RANGE_PATTERN = new JDateDayRange(new JDateDay(BASE_TAXYEAR - 1, Calendar.APRIL, TaxYear.END_OF_MONTH_DAY + 1),
            new JDateDay(BASE_TAXYEAR, Calendar.APRIL, TaxYear.END_OF_MONTH_DAY));

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EventBase.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Frequency Field Id.
     */
    public static final JDataField FIELD_FREQ = FIELD_DEFS.declareEqualityValueField("Frequency");

    @Override
    public Pattern getParent() {
        return (Pattern) super.getParent();
    }

    /**
     * Obtain Frequency.
     * @return the frequency
     */
    public Frequency getFrequency() {
        return getFrequency(getValueSet());
    }

    /**
     * Obtain FrequencyId.
     * @return the frequencyId
     */
    public Integer getFrequencyId() {
        Frequency myFreq = getFrequency();
        return (myFreq == null)
                ? null
                : myFreq.getId();
    }

    /**
     * Obtain frequencyName.
     * @return the frequencyName
     */
    public String getFrequencyName() {
        Frequency myFreq = getFrequency();
        return (myFreq == null)
                ? null
                : myFreq.getName();
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

    /**
     * Set frequency name.
     * @param pName the frequency name
     */
    private void setValueFrequency(final String pName) {
        getValueSet().setValue(FIELD_FREQ, pName);
    }

    /**
     * Obtain partner.
     * @return the partner
     */
    public boolean isCredit() {
        Account myAccount = getList().getAccount();
        return !Difference.isEqual(myAccount, getDebit());
    }

    /**
     * Obtain partner.
     * @return the partner
     */
    public Account getPartner() {
        return isCredit()
                ? getDebit()
                : getCredit();
    }

    @Override
    public Pattern getBase() {
        return (Pattern) super.getBase();
    }

    @Override
    public PatternList getList() {
        return (PatternList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPattern The Pattern
     */
    protected Pattern(final PatternList pList,
                      final Pattern pPattern) {
        /* Simply initialise as Event */
        super(pList, pPattern);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Pattern(final PatternList pList) {
        /* Initialise as Event */
        super(pList);

        /* Ensure that this is a Debit from this account */
        setDebit(pList.getAccount());
    }

    /**
     * Construct a new pattern from an Event.
     * @param pList the list
     * @param pLine the line
     */
    public Pattern(final PatternList pList,
                   final Event pLine) {
        /* Set standard values */
        super(pList, pLine);

        /* Adjust the date so that it is in the correct range */
        JDateDay myDate = new JDateDay(getDate());
        while (myDate.compareTo(RANGE_PATTERN.getEnd()) > 0) {
            myDate.adjustYear(-1);
        }
        while (myDate.compareTo(RANGE_PATTERN.getStart()) < 0) {
            myDate.adjustYear(1);
        }
        setDate(myDate);

        /* Default to monthly frequency */
        FinanceData myData = getDataSet();
        FrequencyList myFrequencies = myData.getFrequencys();
        setValueFrequency(myFrequencies.findItemByClass(FrequencyClass.Monthly));
    }

    /**
     * Secure Constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the control Id
     * @param pDate the date
     * @param pDebitId the debit Id
     * @param pCreditId the credit id
     * @param pCatId the category id
     * @param pAmount the amount
     * @param pFreqId the frequency id
     * @param pSplit is the pattern split?
     * @param pParentId the parent id
     * @throws JDataException on error
     */
    private Pattern(final PatternList pList,
                    final Integer pId,
                    final Integer pControlId,
                    final JDateDay pDate,
                    final Integer pDebitId,
                    final Integer pCreditId,
                    final Integer pCatId,
                    final byte[] pAmount,
                    final Integer pFreqId,
                    final Boolean pSplit,
                    final Integer pParentId) throws JDataException {
        /* Initialise item assuming account as debit and partner as credit */
        super(pList, pId, pControlId, pDate, pDebitId, pCreditId, pAmount, pCatId, Boolean.FALSE, pSplit, pParentId);

        /* Record the IDs */
        setValueFrequency(pFreqId);
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pDate the date
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pCategory the category
     * @param pAmount the amount
     * @param pFrequency the frequency
     * @param pSplit is the pattern split?
     * @param pParent the parent
     * @throws JDataException on error
     */
    private Pattern(final PatternList pList,
                    final Integer pId,
                    final JDateDay pDate,
                    final String pDebit,
                    final String pCredit,
                    final String pCategory,
                    final String pAmount,
                    final String pFrequency,
                    final Boolean pSplit,
                    final Pattern pParent) throws JDataException {
        /* Initialise item assuming account as debit and partner as credit */
        super(pList, pId, pDate, pDebit, pCredit, pAmount, pCategory, Boolean.FALSE, pSplit, pParent);

        /* Record the values */
        setValueFrequency(pFrequency);
    }

    @Override
    public void resolveDataSetLinks() throws JDataException {
        /* Update the Event details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        FinanceData myData = getDataSet();
        FrequencyList myFrequencies = myData.getFrequencys();
        PatternList myPatterns = getList();
        ValueSet myValues = getValueSet();

        /* Adjust Frequency */
        Object myFrequency = myValues.getValue(FIELD_FREQ);
        if (myFrequency instanceof Frequency) {
            myFrequency = ((Frequency) myFrequency).getId();
        }
        if (myFrequency instanceof Integer) {
            Frequency myFreq = myFrequencies.findItemById((Integer) myFrequency);
            if (myFreq == null) {
                addError(ERROR_UNKNOWN, FIELD_FREQ);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_VALIDATION);
            }
            setValueFrequency(myFreq);
        } else if (myFrequency instanceof String) {
            Frequency myFreq = myFrequencies.findItemByName((String) myFrequency);
            if (myFreq == null) {
                addError(ERROR_UNKNOWN, FIELD_FREQ);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_VALIDATION);
            }
            setValueFrequency(myFreq);
        }

        /* Adjust Parent */
        Object myParent = myValues.getValue(FIELD_PARENT);
        if (myParent instanceof Pattern) {
            myParent = ((Pattern) myParent).getId();
        }
        if (myParent instanceof Integer) {
            Pattern myPattern = myPatterns.findItemById((Integer) myParent);
            if (myPattern == null) {
                addError(ERROR_UNKNOWN, FIELD_PARENT);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_VALIDATION);
            }
            setValueParent(myPattern);
        }
    }

    @Override
    public void validate() {
        /* Check that frequency is non-null */
        if (getFrequency() == null) {
            addError(ERROR_MISSING, FIELD_FREQ);
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
                           final JDateDay pDate) throws JDataException {
        /* Access the frequency */
        FrequencyClass myFreq = getFrequency().getFrequency();
        JDateDay myDate;
        int iAdjust;

        /* Access the Tax Year list */
        FinanceData myData = getDataSet();
        TaxYearList myList = myData.getTaxYears();

        /* If this is the first request for an event */
        if (pDate.compareTo(getDate()) == 0) {
            /* If the frequency is maturity */
            if (myFreq == FrequencyClass.Maturity) {
                /* Access the maturity date */
                myDate = getDebit().getMaturity();

                /* Obtain the relevant tax year */
                TaxYear myBase = myList.findTaxYearForDate(getDate());

                /* Ignore if no maturity or else not this year */
                if ((myDate == null)
                    || (myBase == null)
                    || (myBase.compareTo(pTaxYear) != 0)) {
                    return null;
                }
            }

            /* Calculate the difference in years */
            iAdjust = pTaxYear.getTaxYear().getYear()
                      - RANGE_PATTERN.getEnd().getYear();

            /* Adjust the date to fall into the tax year */
            pDate.copyDate(getDate());
            pDate.adjustYear(iAdjust);

            /* else this is a secondary access */
        } else {
            /* switch on frequency type */
            switch (myFreq) {
            /* Weekly etc add relevant days */
                case Weekly:
                case Fortnightly:
                    pDate.adjustDay(myFreq.getAdjustment());
                    break;

                /* Monthly etc add relevant months */
                case Monthly:
                case Quarterly:
                case HalfYearly:
                    pDate.adjustMonth(myFreq.getAdjustment());
                    break;

                /* EndMonthly shift to end of next month */
                case EndOfMonth:
                    pDate.endNextMonth();
                    break;
                /* Annual and maturity patterns only generate single event */
                case Annually:
                case Maturity:
                default:
                    return null;
            }

            /* If we are beyond the end of the year we have finished */
            if (pDate.compareTo(pTaxYear.getTaxYear()) > 0) {
                return null;
            }
        }

        /* Build the new linked event */
        Event myEvent = new Event(pEvents, this);
        myEvent.setNewVersion();

        /* Set the date for this event */
        myEvent.setDate(new JDateDay(pDate));

        /* Return the new event */
        return myEvent;
    }

    /**
     * Set a new frequency.
     * @param pFrequency the frequency
     */
    public void setFrequency(final Frequency pFrequency) {
        setValueFrequency(pFrequency);
    }

    /**
     * Set a new partner.
     * @param pPartner the partner
     */
    public void setPartner(final Account pPartner) {
        if (isCredit()) {
            setDebit(pPartner);
        } else {
            setCredit(pPartner);
        }
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

        /* Apply basic changes */
        applyBasicChanges(myPattern);

        /* Update the frequency if required */
        if (!Difference.isEqual(getFrequency(), myPattern.getFrequency())) {
            setFrequency(myPattern.getFrequency());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch underlying items */
        super.touchUnderlyingItems();

        /* mark the frequency referred to */
        getFrequency().touchItem(this);
    }

    /**
     * The list.
     */
    public static class PatternList
            extends EventBaseList<Pattern> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PatternList.class.getSimpleName(), DataList.FIELD_DEFS);

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
                return (theAccount == null)
                        ? JDataFieldValue.SKIP
                        : theAccount;
            }
            return super.getFieldValue(pField);
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
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

        @Override
        public JDateDayRange getValidDateRange() {
            return RANGE_PATTERN;
        }

        /**
         * Construct an empty CORE pattern list.
         * @param pData the DataSet for the list
         */
        protected PatternList(final FinanceData pData) {
            super(pData, Pattern.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PatternList(final PatternList pSource) {
            super(pSource);
        }

        @Override
        protected PatternList getEmptyList(final ListStyle pStyle) {
            PatternList myList = new PatternList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PatternList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (PatternList) super.cloneList(pDataSet);
        }

        @Override
        public PatternList deriveList(final ListStyle pStyle) throws JDataException {
            return (PatternList) super.deriveList(pStyle);
        }

        @Override
        public PatternList deriveDifferences(final DataList<Pattern> pOld) {
            return (PatternList) super.deriveDifferences(pOld);
        }

        /**
         * Construct an edit extract of a Pattern list.
         * @param pAccount The account to extract patterns for
         * @return the edit list
         */
        public PatternList deriveEditList(final Account pAccount) {
            /* Build an empty Update */
            PatternList myList = getEmptyList(ListStyle.EDIT);
            myList.theAccount = pAccount;

            /* Access the list iterator */
            Iterator<Pattern> myIterator = listIterator();

            /* Loop through the Patterns */
            while (myIterator.hasNext()) {
                Pattern myCurr = myIterator.next();

                /* Skip differing accounts */
                if (!myCurr.relatesTo(pAccount)) {
                    continue;
                }

                /* Copy the item */
                Pattern myItem = new Pattern(myList, myCurr);
                myList.append(myItem);
            }

            /* Sort the list */
            myList.reSort();

            /* Return the List */
            return myList;
        }

        @Override
        public boolean isLocked() {
            return (theAccount != null)
                   && (theAccount.isLocked());
        }

        /**
         * Add a new item to the core list.
         * @param pPattern item
         * @return the newly added item
         */
        @Override
        public Pattern addCopyItem(final DataItem pPattern) {
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
            myPattern.setDate(RANGE_PATTERN.getStart());
            add(myPattern);
            return myPattern;
        }

        /**
         * Allow a pattern to be added.
         * @param pId the id
         * @param pDate the date
         * @param pDebit the debit account
         * @param pCredit the credit account
         * @param pAmount the amount
         * @param pCategory the category type
         * @param pFrequency the frequency
         * @param pSplit is the pattern split
         * @param pParent the parent
         * @return the allocated pattern
         * @throws JDataException on error
         */
        public Pattern addOpenItem(final Integer pId,
                                   final JDateDay pDate,
                                   final String pDebit,
                                   final String pCredit,
                                   final String pAmount,
                                   final String pCategory,
                                   final String pFrequency,
                                   final Boolean pSplit,
                                   final Pattern pParent) throws JDataException {
            /* Create the new pattern */
            Pattern myPattern = new Pattern(this, pId, pDate, pDebit, pCredit, pCategory, pAmount, pFrequency, pSplit, pParent);

            /* Check that this PatternId has not been previously added */
            if (!isIdUnique(pId)) {
                myPattern.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myPattern, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPattern);
            return myPattern;
        }

        /**
         * Allow a pattern to be added.
         * @param pId the id
         * @param pControlId the control id
         * @param pDate the date
         * @param pDebitId the account id
         * @param pCreditId the partner id
         * @param pCatId the category type id
         * @param pFreqId the frequency id
         * @param pAmount the amount
         * @param pSplit is the pattern split
         * @param pParentId the parent id
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final JDateDay pDate,
                                  final Integer pDebitId,
                                  final Integer pCreditId,
                                  final byte[] pAmount,
                                  final Integer pCatId,
                                  final Integer pFreqId,
                                  final Boolean pSplit,
                                  final Integer pParentId) throws JDataException {
            /* Create the new pattern */
            Pattern myPattern = new Pattern(this, pId, pControlId, pDate, pDebitId, pCreditId, pCatId, pAmount, pFreqId, pSplit, pParentId);

            /* Check that this PatternId has not been previously added */
            if (!isIdUnique(pId)) {
                myPattern.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myPattern, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPattern);
        }
    }
}
