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

import java.time.Month;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.FrequencyClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;

/**
 * Pattern data type.
 * @author Tony Washer
 */
public class Schedule
        extends TransactionBase<Schedule> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SCHEDULE.getItemName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SCHEDULE.getListName();

    /**
     * Interesting TaxYear.
     */
    public static final int BASE_TAXYEAR = 2000;

    /**
     * Interesting TaxYear.
     */
    public static final int END_OF_MONTH_DAY = 5;

    /**
     * The interesting date range.
     */
    public static final JDateDayRange RANGE_SCHEDULE = new JDateDayRange(new JDateDay(BASE_TAXYEAR - 1, Month.APRIL, END_OF_MONTH_DAY + 1),
            new JDateDay(BASE_TAXYEAR, Month.APRIL, END_OF_MONTH_DAY));

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, TransactionBase.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Frequency Field Id.
     */
    public static final JDataField FIELD_FREQ = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.FREQUENCY.getItemName());

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_FREQ.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Iterator<Schedule> childIterator() {
        /* No iterator at present */
        return null;
    }

    @Override
    public Schedule getParent() {
        return (Schedule) super.getParent();
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

    @Override
    public Schedule getBase() {
        return (Schedule) super.getBase();
    }

    @Override
    public ScheduleList getList() {
        return (ScheduleList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pSchedule The Schedule
     */
    protected Schedule(final ScheduleList pList,
                       final Schedule pSchedule) {
        /* Simply initialise as Schedule */
        super(pList, pSchedule);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Schedule(final ScheduleList pList) {
        /* Initialise as Transaction */
        super(pList);
    }

    /**
     * Construct a new pattern from a Transaction.
     * @param pList the list
     * @param pTrans the transaction
     */
    public Schedule(final ScheduleList pList,
                    final Transaction pTrans) {
        /* Set standard values */
        super(pList);

        /* Adjust the date so that it is in the correct range */
        JDateDay myDate = new JDateDay(pTrans.getDate());
        while (myDate.compareTo(RANGE_SCHEDULE.getEnd()) > 0) {
            myDate.adjustYear(-1);
        }
        while (myDate.compareTo(RANGE_SCHEDULE.getStart()) < 0) {
            myDate.adjustYear(1);
        }
        setDate(myDate);

        /* Copy underlying values */
        setValueAssetPair(pTrans.getAssetPair());
        setValueDebit(pTrans.getDebit());
        setValueCredit(pTrans.getCredit());
        setCategory(pTrans.getCategory());
        setReconciled(Boolean.FALSE);
        setSplit(Boolean.FALSE);
        setValueAmount(pTrans.getAmountField());

        /* Default to monthly frequency */
        MoneyWiseData myData = getDataSet();
        FrequencyList myFrequencies = myData.getFrequencys();
        setValueFrequency(myFrequencies.findItemByClass(FrequencyClass.MONTHLY));
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private Schedule(final ScheduleList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Frequency */
        Object myValue = pValues.getValue(FIELD_FREQ);
        if (myValue instanceof Integer) {
            setValueFrequency((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueFrequency((String) myValue);
        }
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Event details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_FREQ, myData.getFrequencys());
        resolveDataLink(FIELD_PARENT, getList());
    }

    @Override
    public void validate() {
        /* Access data */
        Frequency myFrequency = getFrequency();

        /* Check that frequency is non-null */
        if (getFrequency() == null) {
            addError(ERROR_MISSING, FIELD_FREQ);
        } else if (!getFrequency().getEnabled()) {
            addError("Frequency must be enabled", FIELD_FREQ);
        } else if (myFrequency.getFrequency() == FrequencyClass.MATURITY) {
            AssetBase<?> myDebit = getDebit();
            if ((!(myDebit instanceof Deposit))
                || (!((Deposit) myDebit).isDepositClass(DepositCategoryClass.BOND))) {
                addError("Maturity only allowed for Bonds", FIELD_FREQ);
            }
        }

        /* Validate it */
        super.validate();
    }

    /**
     * Adjust date that is built from a schedule.
     * @param pTransactions the transaction list
     * @param pTaxYear the new tax year
     * @param pDate the date for the event
     * @return the new transaction
     * @throws JOceanusException on error
     */
    public Transaction nextTransaction(final TransactionList pTransactions,
                                       final TaxYear pTaxYear,
                                       final JDateDay pDate) throws JOceanusException {
        /* Access the frequency */
        FrequencyClass myFreq = getFrequency().getFrequency();
        JDateDay myDate = null;
        int iAdjust;

        /* Access the Tax Year list */
        MoneyWiseData myData = getDataSet();
        TaxYearList myList = myData.getTaxYears();

        /* If this is the first request for an event */
        if (pDate.compareTo(getDate()) == 0) {
            /* If the frequency is maturity */
            if (myFreq == FrequencyClass.MATURITY) {
                /* Access the maturity date */
                AssetBase<?> myDebit = getDebit();
                if (myDebit instanceof Deposit) {
                    myDate = ((Deposit) myDebit).getMaturity();
                }

                /* Obtain the relevant tax year */
                TaxYear myBase = myList.findTaxYearForDate(getDate());

                /* Ignore if no maturity or else not this year */
                if ((myDate == null) || (myBase == null) || (myBase.compareTo(pTaxYear) != 0)) {
                    return null;
                }
            }

            /* Calculate the difference in years */
            iAdjust = pTaxYear.getTaxYear().getYear() - RANGE_SCHEDULE.getEnd().getYear();

            /* Adjust the date to fall into the tax year */
            pDate.copyDate(getDate());
            pDate.adjustYear(iAdjust);

            /* else this is a secondary access */
        } else {
            /* switch on frequency type */
            switch (myFreq) {
            /* Weekly etc add relevant days */
                case WEEKLY:
                case FORTNIGHTLY:
                    pDate.adjustDay(myFreq.getAdjustment());
                    break;

                /* Monthly etc add relevant months */
                case MONTHLY:
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
        }

        /* Build the new linked transaction */
        Transaction myTrans = new Transaction(pTransactions, this);
        myTrans.setNewVersion();

        /* Set the date for this transaction */
        myTrans.setDate(new JDateDay(pDate));

        /* Return the new transaction */
        return myTrans;
    }

    /**
     * Set a new frequency.
     * @param pFrequency the frequency
     */
    public void setFrequency(final Frequency pFrequency) {
        setValueFrequency(pFrequency);
    }

    /**
     * Update Schedule from a schedule extract.
     * @param pSchedule the schedule extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pSchedule) {
        /* Can only update from Pattern */
        if (!(pSchedule instanceof Schedule)) {
            return false;
        }

        Schedule mySchedule = (Schedule) pSchedule;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(mySchedule);

        /* Update the frequency if required */
        if (!Difference.isEqual(getFrequency(), mySchedule.getFrequency())) {
            setFrequency(mySchedule.getFrequency());
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
    public static class ScheduleList
            extends TransactionBaseList<Schedule> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ScheduleList.class.getSimpleName(), DataList.FIELD_DEFS);

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
            return Schedule.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        public JDateDayRange getValidDateRange() {
            return RANGE_SCHEDULE;
        }

        /**
         * Construct an empty CORE pattern list.
         * @param pData the DataSet for the list
         */
        protected ScheduleList(final MoneyWiseData pData) {
            super(pData, Schedule.class, MoneyWiseDataType.SCHEDULE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ScheduleList(final ScheduleList pSource) {
            super(pSource);
        }

        @Override
        protected ScheduleList getEmptyList(final ListStyle pStyle) {
            ScheduleList myList = new ScheduleList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Construct an edit extract of a Pattern list.
         * @return the edit list
         */
        public ScheduleList deriveEditList() {
            /* Build an empty Update */
            ScheduleList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the Schedule */
            Iterator<Schedule> myIterator = listIterator();
            while (myIterator.hasNext()) {
                Schedule myCurr = myIterator.next();

                /* Copy the item */
                Schedule myItem = new Schedule(myList, myCurr);
                myList.append(myItem);
            }

            /* Sort the list */
            myList.reSort();

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pSchedule item
         * @return the newly added item
         */
        @Override
        public Schedule addCopyItem(final DataItem<?> pSchedule) {
            /* Can only clone from Schedule */
            if (!(pSchedule instanceof Schedule)) {
                throw new UnsupportedOperationException();
            }

            Schedule mySchedule = new Schedule(this, (Schedule) pSchedule);
            add(mySchedule);
            return mySchedule;
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public Schedule addNewItem() {
            Schedule mySchedule = new Schedule(this);

            /* Set the Date as the start of the range */
            mySchedule.setDate(RANGE_SCHEDULE.getStart());
            add(mySchedule);
            return mySchedule;
        }

        @Override
        public Schedule addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* If the item has children */
            if (pValues.hasChildren()) {
                /* Note that the item is split */
                pValues.addValue(FIELD_SPLIT, Boolean.TRUE);
            }

            /* Create the schedule */
            Schedule mySchedule = new Schedule(this, pValues);

            /* Check that this ScheduleId has not been previously added */
            if (!isIdUnique(mySchedule.getId())) {
                mySchedule.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(mySchedule, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(mySchedule);

            /* Loop through the children */
            if (pValues.hasChildren()) {
                /* Loop through the items */
                Iterator<DataValues<MoneyWiseDataType>> myIterator = pValues.childIterator();
                while (myIterator.hasNext()) {
                    DataValues<MoneyWiseDataType> myValues = myIterator.next();

                    /* Note that the item is split */
                    myValues.addValue(FIELD_SPLIT, Boolean.TRUE);
                    myValues.addValue(FIELD_PARENT, mySchedule);

                    /* Copy missing values from parent */
                    myValues.addValue(FIELD_DATE, pValues.getValue(FIELD_DATE));
                    if (myValues.getValue(FIELD_DEBIT) == null) {
                        myValues.addValue(FIELD_DEBIT, pValues.getValue(FIELD_DEBIT));
                    }
                    if (myValues.getValue(FIELD_CREDIT) == null) {
                        myValues.addValue(FIELD_CREDIT, pValues.getValue(FIELD_CREDIT));
                    }

                    /* Build item */
                    addValuesItem(myValues);
                }
            }

            /* Return it */
            return mySchedule;
        }

        @Override
        protected DataMapItem<Schedule, MoneyWiseDataType> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }

        @Override
        public void postProcessOnLoad() throws JOceanusException {
            /* Resolve links and sort the data */
            resolveDataSetLinks();
            reSort();

            /* Validate the schedules */
            validateOnLoad();
        }

        @Override
        protected void ensureMap() {
            /* Null operation */
        }
    }
}
