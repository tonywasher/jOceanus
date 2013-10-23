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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory.EventCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Event Category Bucket.
 */
public final class EventCategoryBucket
        implements JDataContents, Comparable<EventCategoryBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventCategoryBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Event Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * Event Type Field Id.
     */
    private static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataType"));

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBaseValues"));

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHistory"));

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, EventAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, EventAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The event category.
     */
    private final EventCategory theCategory;

    /**
     * The event category type.
     */
    private final EventCategoryType theType;

    /**
     * Values.
     */
    private final CategoryValues theValues;

    /**
     * The base values.
     */
    private final CategoryValues theBaseValues;

    /**
     * Is the bucket idle.
     */
    private final Boolean isIdle;

    /**
     * History Map.
     */
    private final BucketHistory<CategoryValues> theHistory;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_TYPE.equals(pField)) {
            return theType;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBaseValues != null)
                    ? theBaseValues
                    : JDataFieldValue.SkipField;
        }

        /* Handle Attribute fields */
        EventAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof JDecimal) {
                return ((JDecimal) myValue).isNonZero()
                        ? myValue
                        : JDataFieldValue.SkipField;
            }
            return myValue;
        }

        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theCategory.getName();
    }

    @Override
    public Integer getOrderedId() {
        return theCategory.getId();
    }

    /**
     * Obtain the event category.
     * @return the event category
     */
    public EventCategory getEventCategory() {
        return theCategory;
    }

    /**
     * Obtain the event category type.
     * @return the event category type
     */
    public EventCategoryType getEventCategoryType() {
        return theType;
    }

    /**
     * Is this bucket idle.
     * @return true/false
     */
    public Boolean isIdle() {
        return isIdle;
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    protected CategoryValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    protected CategoryValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<CategoryValues> getHistoryMap() {
        return theHistory;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final EventAttribute pAttr,
                            final JMoney pValue) {
        /* Set the value */
        theValues.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final EventAttribute pAttr) {
        /* Access value of object */
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static EventAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final EventAttribute pAttr) {
        /* Obtain the value */
        return theValues.get(pAttr);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the event category
     */
    private EventCategoryBucket(final Analysis pAnalysis,
                                final EventCategory pCategory) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theCategory = pCategory;
        theType = (pCategory == null)
                ? null
                : pCategory.getCategoryType();

        /* Create the values map */
        theValues = new CategoryValues();
        theBaseValues = new CategoryValues();
        isIdle = false;

        /* Create the history map */
        theHistory = new BucketHistory<CategoryValues>();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private EventCategoryBucket(final Analysis pAnalysis,
                                final EventCategoryBucket pBase,
                                final JDateDay pDate) {
        /* Copy details from base */
        theCategory = pBase.getEventCategory();
        theType = pBase.getEventCategoryType();
        theAnalysis = pAnalysis;

        /* Reference the underlying history */
        theHistory = pBase.getHistoryMap();

        /* Copy base values from source */
        theBaseValues = pBase.getBaseValues().getSnapShot();

        /* Obtain values for date */
        CategoryValues myValues = theHistory.getValuesForDate(pDate);

        /* Determine values */
        isIdle = (myValues == null);
        theValues = (isIdle)
                ? theBaseValues.getSnapShot()
                : myValues;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private EventCategoryBucket(final Analysis pAnalysis,
                                final EventCategoryBucket pBase,
                                final JDateDayRange pRange) {
        /* Copy details from base */
        theCategory = pBase.getEventCategory();
        theType = pBase.getEventCategoryType();
        theAnalysis = pAnalysis;

        /* Reference the underlying history */
        theHistory = pBase.getHistoryMap();

        /* Obtain values for range */
        CategoryValues[] myArray = theHistory.getValuesForRange(pRange);

        /* If no activity took place up to this date */
        if (myArray == null) {
            /* Use base values and note idleness */
            theValues = pBase.getBaseValues().getSnapShot();
            theBaseValues = theValues.getSnapShot();
            isIdle = true;

            /* else we have values */
        } else {
            /* Determine base values */
            CategoryValues myFirst = myArray[0];
            theBaseValues = (myFirst == null)
                    ? pBase.getBaseValues().getSnapShot()
                    : myFirst;

            /* Determine values */
            CategoryValues myValues = myArray[1];
            isIdle = (myValues == null);
            theValues = (isIdle)
                    ? theBaseValues.getSnapShot()
                    : myValues;
        }

        /* Adjust to base values */
        adjustToBaseValues();
    }

    @Override
    public int compareTo(final EventCategoryBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the EventCategories */
        return getEventCategory().compareTo(pThat.getEventCategory());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof EventCategoryBucket)) {
            return false;
        }

        /* Compare the Event Categories */
        EventCategoryBucket myThat = (EventCategoryBucket) pThat;
        return getEventCategory().equals(myThat.getEventCategory());
    }

    @Override
    public int hashCode() {
        return getEventCategory().hashCode();
    }

    /**
     * Obtain new Income value.
     * @return the new income value
     */
    private JMoney getNewIncome() {
        JMoney myIncome = theValues.getMoneyValue(EventAttribute.Income);
        return new JMoney(myIncome);
    }

    /**
     * Obtain new Expense value.
     * @return the new expense value
     */
    private JMoney getNewExpense() {
        JMoney myExpense = theValues.getMoneyValue(EventAttribute.Expense);
        return new JMoney(myExpense);
    }

    /**
     * Add income value.
     * @param pEvent the event causing the expense
     * @param pValue the value to add
     */
    protected void addIncome(final Event pEvent,
                             final JMoney pValue) {
        /* Add the expense */
        addIncome(pValue);

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    protected void addIncome(final JMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            JMoney myIncome = getNewIncome();
            myIncome.addAmount(pValue);
            setValue(EventAttribute.Income, myIncome);
        }
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    protected void subtractIncome(final JMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            JMoney myIncome = getNewIncome();
            myIncome.subtractAmount(pValue);
            setValue(EventAttribute.Income, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pEvent the event causing the expense
     * @param pValue the value to add
     */
    protected void addExpense(final Event pEvent,
                              final JMoney pValue) {
        /* Add the expense */
        addExpense(pValue);

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    protected void addExpense(final JMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            JMoney myExpense = getNewExpense();
            myExpense.addAmount(pValue);
            setValue(EventAttribute.Expense, myExpense);
        }
    }

    /**
     * Subtract expense value.
     * @param pEvent the event causing the expense
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final Event pEvent,
                                   final JMoney pValue) {
        /* Subtract the expense */
        subtractExpense(pValue);

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final JMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            JMoney myExpense = getNewExpense();
            myExpense.subtractAmount(pValue);
            setValue(EventAttribute.Expense, myExpense);
        }
    }

    /**
     * Add event to totals.
     * @param pEvent the event
     */
    private void adjustValues(final Event pEvent) {
        /* Analyse the event */
        AccountType myDebitType = AccountType.deriveType(pEvent.getDebit());
        AccountType myCreditType = AccountType.deriveType(pEvent.getCredit());
        TransactionType myCatTran = TransactionType.deriveType(pEvent.getCategory());
        TransactionType myActTran = myDebitType.getTransactionType(myCreditType);
        JMoney myAmount = pEvent.getAmount();

        /* If this is an expense */
        if (myCatTran.isExpense()) {
            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a recovered expense */
                if (myActTran.isIncome()) {
                    JMoney myIncome = getNewIncome();
                    myIncome.addAmount(myAmount);
                    setValue(EventAttribute.Income, myIncome);
                } else {
                    JMoney myExpense = getNewExpense();
                    myExpense.addAmount(myAmount);
                    setValue(EventAttribute.Expense, myExpense);
                }
            }

            /* else this is an income */
        } else {
            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a returned income */
                if (myActTran.isExpense()) {
                    JMoney myExpense = getNewExpense();
                    myExpense.addAmount(myAmount);
                    setValue(EventAttribute.Expense, myExpense);
                } else {
                    JMoney myIncome = getNewIncome();
                    myIncome.addAmount(myAmount);

                    /* Adjust for TaxCredit */
                    JMoney myTaxCredit = pEvent.getTaxCredit();
                    if (myTaxCredit != null) {
                        myIncome.addAmount(myTaxCredit);
                    }

                    /* Adjust for NatInsurance */
                    JMoney myNatIns = pEvent.getNatInsurance();
                    if (myNatIns != null) {
                        myIncome.addAmount(myNatIns);
                    }

                    /* Adjust for DeemedBenefit */
                    JMoney myBenefit = pEvent.getDeemedBenefit();
                    if (myBenefit != null) {
                        myIncome.addAmount(myBenefit);
                    }

                    /* Adjust for CharityDonation */
                    JMoney myDonation = pEvent.getCharityDonation();
                    if (myDonation != null) {
                        myIncome.addAmount(myDonation);
                    }

                    /* Store the value */
                    setValue(EventAttribute.Income, myIncome);
                }
            }
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust to Base values.
     */
    private void adjustToBaseValues() {
        /* Adjust income values */
        JMoney myValue = getNewIncome();
        JMoney myBaseValue = theBaseValues.getMoneyValue(EventAttribute.Income);
        myValue.subtractAmount(myBaseValue);
        theBaseValues.put(EventAttribute.Income, null);

        /* Adjust expense values */
        myValue = getNewExpense();
        myBaseValue = theBaseValues.getMoneyValue(EventAttribute.Expense);
        myValue.subtractAmount(myBaseValue);
        theBaseValues.put(EventAttribute.Expense, null);
    }

    /**
     * Calculate Income delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myDelta = getNewIncome();

        /* Subtract the expense value */
        JMoney myExpense = theValues.getMoneyValue(EventAttribute.Expense);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        setValue(EventAttribute.Delta, myDelta);
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    private void addValues(final EventCategoryBucket pSource) {
        /* Access source values */
        CategoryValues mySource = pSource.getValues();

        /* Add income values */
        JMoney myValue = theValues.getMoneyValue(EventAttribute.Income);
        JMoney mySrcValue = mySource.getMoneyValue(EventAttribute.Income);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(EventAttribute.Expense);
        mySrcValue = mySource.getMoneyValue(EventAttribute.Expense);
        myValue.addAmount(mySrcValue);
    }

    /**
     * CategoryValues class.
     */
    public final class CategoryValues
            extends BucketValues<CategoryValues, EventAttribute> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 8946533427978809288L;

        /**
         * Constructor.
         */
        private CategoryValues() {
            /* Initialise class */
            super(EventAttribute.class);

            /* Create all possible values */
            put(EventAttribute.Income, new JMoney());
            put(EventAttribute.Expense, new JMoney());
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private CategoryValues(final CategoryValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected CategoryValues getSnapShot() {
            return new CategoryValues(this);
        }

        @Override
        protected CategoryValues[] getSnapShotArray() {
            /* Allocate the array and return it */
            return new CategoryValues[2];
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myIncome = getMoneyValue(EventAttribute.Income);
            JMoney myExpense = getMoneyValue(EventAttribute.Expense);
            return ((myIncome.isNonZero()) || (myExpense.isNonZero()));
        }
    }

    /**
     * EventCategoryBucket list class.
     */
    public static final class EventCategoryBucketList
            extends OrderedIdList<Integer, EventCategoryBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"));

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName()
                   + "("
                   + size()
                   + ")";
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSize"));

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAnalysis"));

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataTotals"));

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return JDataFieldValue.UnknownField;
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The data.
         */
        private final FinanceData theData;

        /**
         * The totals.
         */
        private final EventCategoryBucket theTotals;

        /**
         * The TaxCredit.
         */
        private final EventCategoryBucket theTaxCredit;

        /**
         * The NatInsurance.
         */
        private final EventCategoryBucket theNatInsurance;

        /**
         * The DeemedBenefit.
         */
        private final EventCategoryBucket theDeemedBenefit;

        /**
         * The CharityDonation.
         */
        private final EventCategoryBucket theCharityDonation;

        /**
         * The TaxableGains.
         */
        private final EventCategoryBucket theTaxableGains;

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public EventCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public EventCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Obtain the implied buckets */
            EventCategoryList myList = theData.getEventCategories();
            theTaxCredit = getBucket(myList.getEventInfoCategory(EventInfoClass.TaxCredit));
            theNatInsurance = getBucket(myList.getEventInfoCategory(EventInfoClass.NatInsurance));
            theDeemedBenefit = getBucket(myList.getEventInfoCategory(EventInfoClass.DeemedBenefit));
            theCharityDonation = getBucket(myList.getEventInfoCategory(EventInfoClass.CharityDonation));
            theTaxableGains = getBucket(myList.getSingularClass(EventCategoryClass.TaxableGain));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        public EventCategoryBucketList(final Analysis pAnalysis,
                                       final EventCategoryBucketList pBase,
                                       final JDateDay pDate) {
            /* Initialise class */
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Don't use implied buckets */
            theTaxCredit = null;
            theNatInsurance = null;
            theDeemedBenefit = null;
            theCharityDonation = null;
            theTaxableGains = null;

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                EventCategoryBucket myBucket = new EventCategoryBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    myBucket.calculateDelta();
                    add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        public EventCategoryBucketList(final Analysis pAnalysis,
                                       final EventCategoryBucketList pBase,
                                       final JDateDayRange pRange) {
            /* Initialise class */
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Don't use implied buckets */
            theTaxCredit = null;
            theNatInsurance = null;
            theDeemedBenefit = null;
            theCharityDonation = null;
            theTaxableGains = null;

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                EventCategoryBucket myBucket = new EventCategoryBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    myBucket.calculateDelta();
                    add(myBucket);
                }
            }
        }

        /**
         * Allocate the Totals EventCategoryBucket.
         * @return the bucket
         */
        private EventCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new EventCategoryBucket(theAnalysis, null);
        }

        /**
         * Obtain the EventCategoryBucket for a given event category.
         * @param pCategory the event category
         * @return the bucket
         */
        protected EventCategoryBucket getBucket(final EventCategory pCategory) {
            /* Handle null category */
            if (pCategory == null) {
                return null;
            }

            /* Locate the bucket in the list */
            EventCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new EventCategoryBucket(theAnalysis, pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the EventCategoryBucket for a given event category class.
         * @param pClass the event category class
         * @return the bucket
         */
        protected EventCategoryBucket getBucket(final EventCategoryClass pClass) {
            /* Determine required category */
            EventCategory myCategory = theData.getEventCategories().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myCategory);
        }

        /**
         * Adjust category buckets.
         * @param pEvent the event
         * @param pCategory primary category
         */
        protected void adjustCategories(final Event pEvent,
                                        final EventCategory pCategory) {
            /* Adjust the primary category bucket */
            EventCategoryBucket myCatBucket = getBucket(pCategory);
            myCatBucket.adjustValues(pEvent);

            /* Adjust for Tax Credit */
            JMoney myTaxCredit = pEvent.getTaxCredit();
            if (myTaxCredit != null) {
                theTaxCredit.addIncome(pEvent, myTaxCredit);
            }

            /* Adjust for NatInsurance */
            JMoney myNatIns = pEvent.getNatInsurance();
            if (myNatIns != null) {
                theNatInsurance.addIncome(pEvent, myNatIns);
            }

            /* Adjust for DeemedBenefit */
            JMoney myBenefit = pEvent.getDeemedBenefit();
            if (myBenefit != null) {
                theDeemedBenefit.addIncome(pEvent, myBenefit);
            }

            /* Adjust for Charity Donation */
            JMoney myDonation = pEvent.getCharityDonation();
            if (myDonation != null) {
                theCharityDonation.addIncome(pEvent, myDonation);
            }
        }

        /**
         * Adjust for Taxable Gains.
         * @param pEvent the event
         * @param pReduction the income reduction
         */
        protected void adjustTaxableGain(final Event pEvent,
                                         final JMoney pReduction) {
            /* Adjust Taxable Gains */
            theTaxableGains.adjustValues(pEvent);
            theTaxableGains.subtractIncome(pReduction);

            /* Adjust for Tax Credit */
            JMoney myTaxCredit = pEvent.getTaxCredit();
            if (myTaxCredit != null) {
                theTaxCredit.addIncome(pEvent, myTaxCredit);
            }
        }

        /**
         * Produce totals for the EventCategories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets */
            OrderedIdList<Integer, EventCategoryBucket> myTotals = new OrderedIdList<Integer, EventCategoryBucket>(EventCategoryBucket.class);

            /* Determine if this is a ranged analysis */
            boolean isRanged = theAnalysis.isRangedAnalysis();

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                EventCategory myCategory = myCurr.getEventCategory();
                EventCategory myParent = myCategory.getParentCategory();

                /* Access parent bucket */
                EventCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.findItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new EventCategoryBucket(theAnalysis, myParent);
                        myTotals.add(myTotal);
                    }
                }

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                if (isRanged) {
                    myCurr.calculateDelta();
                }

                /* Add it to the list */
                add(myCurr);
            }

            /* Calculate delta for the totals */
            if ((isRanged)
                && (theTotals != null)) {
                theTotals.calculateDelta();
            }
        }
    }

    /**
     * EventAttribute enumeration.
     */
    public enum EventAttribute {
        /**
         * Income.
         */
        Income,

        /**
         * Expense.
         */
        Expense,

        /**
         * IncomeDelta.
         */
        Delta;
    }
}
