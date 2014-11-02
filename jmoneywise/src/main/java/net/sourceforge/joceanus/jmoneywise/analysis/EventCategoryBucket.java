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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Event Category Bucket.
 */
public final class EventCategoryBucket
        implements JDataContents, Comparable<EventCategoryBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.EVENTCATEGORY_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Event Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSCATEGORY.getItemName());

    /**
     * Event Type Field Id.
     */
    private static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSTYPE.getItemName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, EventAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, EventAttribute.class);

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The event category.
     */
    private final TransactionCategory theCategory;

    /**
     * The event category type.
     */
    private final TransactionCategoryType theType;

    /**
     * Values.
     */
    private final CategoryValues theValues;

    /**
     * The base values.
     */
    private final CategoryValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<CategoryValues, EventAttribute> theHistory;

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
                                          : JDataFieldValue.SKIP;
        }

        /* Handle Attribute fields */
        EventAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof JDecimal) {
                return ((JDecimal) myValue).isNonZero()
                                                       ? myValue
                                                       : JDataFieldValue.SKIP;
            }
            return myValue;
        }

        return JDataFieldValue.SKIP;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return (theCategory == null)
                                    ? NAME_TOTALS
                                    : theCategory.getName();
    }

    @Override
    public Integer getOrderedId() {
        return theCategory.getId();
    }

    /**
     * Obtain the event category.
     * @return the event category
     */
    public TransactionCategory getEventCategory() {
        return theCategory;
    }

    /**
     * Obtain the event category type.
     * @return the event category type
     */
    public TransactionCategoryType getEventCategoryType() {
        return theType;
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public CategoryValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public CategoryValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public CategoryValues getValuesForTransaction(final Transaction pTrans) {
        /* Obtain values for transaction */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JDecimal getDeltaForTransaction(final Transaction pTrans,
                                           final EventAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<CategoryValues, EventAttribute> getHistoryMap() {
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
     * Obtain date range.
     * @return the range
     */
    public JDateDayRange getDateRange() {
        return theAnalysis.getDateRange();
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
                                : JDataFieldValue.SKIP;
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
                                final TransactionCategory pCategory) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theCategory = pCategory;
        theType = (pCategory == null)
                                     ? null
                                     : pCategory.getCategoryType();

        /* Create the history map */
        theHistory = new BucketHistory<CategoryValues, EventAttribute>(new CategoryValues());

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
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

        /* Access the relevant history */
        theHistory = new BucketHistory<CategoryValues, EventAttribute>(pBase.getHistoryMap(), pDate);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
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

        /* Access the relevant history */
        theHistory = new BucketHistory<CategoryValues, EventAttribute>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public int compareTo(final EventCategoryBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
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
        if (!getEventCategory().equals(myThat.getEventCategory())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
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
        JMoney myIncome = theValues.getMoneyValue(EventAttribute.INCOME);
        return new JMoney(myIncome);
    }

    /**
     * Obtain new Expense value.
     * @return the new expense value
     */
    private JMoney getNewExpense() {
        JMoney myExpense = theValues.getMoneyValue(EventAttribute.EXPENSE);
        return new JMoney(myExpense);
    }

    /**
     * Add income value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to add
     */
    protected void addIncome(final Transaction pTrans,
                             final JMoney pValue) {
        /* Add the expense */
        addIncome(pValue);

        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
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
            setValue(EventAttribute.INCOME, myIncome);
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
            setValue(EventAttribute.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to add
     */
    protected void addExpense(final Transaction pTrans,
                              final JMoney pValue) {
        /* Add the expense */
        addExpense(pValue);

        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
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
            setValue(EventAttribute.EXPENSE, myExpense);
        }
    }

    /**
     * Subtract expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final Transaction pTrans,
                                   final JMoney pValue) {
        /* Subtract the expense */
        subtractExpense(pValue);

        /* Register the event in the history */
        theHistory.registerTransaction(pTrans, theValues);
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
            setValue(EventAttribute.EXPENSE, myExpense);
        }
    }

    /**
     * Add transaction to totals.
     * @param pTrans the transaction
     * @return isIncome true/false
     */
    private boolean adjustValues(final Transaction pTrans) {
        /* Analyse the event */
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        AssetDirection myDir = pTrans.getDirection();
        boolean isExpense = myClass.isExpense();
        JMoney myAmount = new JMoney(pTrans.getAmount());

        /* If this is an expense */
        if (isExpense) {
            /* Adjust for TaxCredit */
            JMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                myAmount.addAmount(myTaxCredit);
            }

            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a recovered expense */
                if (myDir.isFrom()) {
                    /* Add as income */
                    JMoney myIncome = getNewIncome();
                    myIncome.addAmount(myAmount);
                    setValue(EventAttribute.INCOME, myIncome);
                    isExpense = false;

                    /* else its standard expense */
                } else {
                    /* Add as expense */
                    JMoney myExpense = getNewExpense();
                    myExpense.addAmount(myAmount);
                    setValue(EventAttribute.EXPENSE, myExpense);
                }
            }

            /* else this is an income */
        } else {
            /* Adjust for TaxCredit */
            JMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                myAmount.addAmount(myTaxCredit);
            }

            /* Adjust for NatInsurance */
            JMoney myNatIns = pTrans.getNatInsurance();
            if (myNatIns != null) {
                myAmount.addAmount(myNatIns);
            }

            /* Adjust for DeemedBenefit */
            JMoney myBenefit = pTrans.getDeemedBenefit();
            if (myBenefit != null) {
                myAmount.addAmount(myBenefit);
            }

            /* Adjust for CharityDonation */
            JMoney myDonation = pTrans.getCharityDonation();
            if (myDonation != null) {
                myAmount.addAmount(myDonation);
            }

            /* If we need to switch direction */
            if (myClass.isSwitchDirection()) {
                /* switch the direction */
                myDir = myDir.reverse();
            }

            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a returned income */
                if (myDir.isTo()) {
                    /* Add as expense */
                    JMoney myExpense = getNewExpense();
                    myExpense.addAmount(myAmount);
                    setValue(EventAttribute.EXPENSE, myExpense);

                    /* else standard income */
                } else {
                    /* Add as income */
                    JMoney myIncome = getNewIncome();
                    myIncome.addAmount(myAmount);

                    /* Store the value */
                    setValue(EventAttribute.INCOME, myIncome);
                    isExpense = false;
                }
            }
        }

        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);

        /* Return the income flag */
        return !isExpense;
    }

    /**
     * Calculate Income delta.
     */
    protected void calculateDelta() {
        /* Calculate delta for the values */
        theValues.calculateDelta();
    }

    /**
     * Adjust to base.
     */
    private void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    private void addValues(final EventCategoryBucket pSource) {
        /* Access source values */
        CategoryValues mySource = pSource.getValues();

        /* Add income values */
        JMoney myValue = theValues.getMoneyValue(EventAttribute.INCOME);
        JMoney mySrcValue = mySource.getMoneyValue(EventAttribute.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(EventAttribute.EXPENSE);
        mySrcValue = mySource.getMoneyValue(EventAttribute.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    /**
     * CategoryValues class.
     */
    public static final class CategoryValues
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
            put(EventAttribute.INCOME, new JMoney());
            put(EventAttribute.EXPENSE, new JMoney());
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
        protected void adjustToBaseValues(final CategoryValues pBase) {
            /* Adjust income/expense values */
            adjustMoneyToBase(pBase, EventAttribute.INCOME);
            adjustMoneyToBase(pBase, EventAttribute.EXPENSE);
            calculateDelta();
        }

        /**
         * Calculate delta.
         */
        private void calculateDelta() {
            /* Obtain a copy of the value */
            JMoney myDelta = getMoneyValue(EventAttribute.INCOME);
            myDelta = new JMoney(myDelta);

            /* Subtract the expense value */
            JMoney myExpense = getMoneyValue(EventAttribute.EXPENSE);
            myDelta.subtractAmount(myExpense);

            /* Set the delta */
            put(EventAttribute.DELTA, myDelta);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset Income and expense values */
            put(EventAttribute.INCOME, new JMoney());
            put(EventAttribute.EXPENSE, new JMoney());
            put(EventAttribute.DELTA, new JMoney());
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myIncome = getMoneyValue(EventAttribute.INCOME);
            JMoney myExpense = getMoneyValue(EventAttribute.EXPENSE);
            return (myIncome.isNonZero()) || (myExpense.isNonZero());
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
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.EVENTCATEGORY_LIST.getValue());

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NAME_TOTALS);

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
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

        /**
         * The totals.
         */
        private final EventCategoryBucket theTotals;

        /**
         * The TaxBasis.
         */
        private final TaxBasisBucketList theTaxBasis;

        /**
         * The TaxCredit.
         */
        private final EventCategoryBucket theTaxCredit;

        /**
         * The TaxRelief.
         */
        private final EventCategoryBucket theTaxRelief;

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
         * The CapitalGains.
         */
        private final EventCategoryBucket theCapitalGains;

        /**
         * The TaxFreeGains.
         */
        private final EventCategoryBucket theTaxFreeGains;

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
        protected EventCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Access taxBasis list */
            theTaxBasis = theAnalysis.getTaxBasis();

            /* Obtain the implied buckets */
            TransactionCategoryList myList = theData.getTransCategories();
            theTaxCredit = getBucket(myList.getEventInfoCategory(TransactionInfoClass.TAXCREDIT));
            theNatInsurance = getBucket(myList.getEventInfoCategory(TransactionInfoClass.NATINSURANCE));
            theDeemedBenefit = getBucket(myList.getEventInfoCategory(TransactionInfoClass.DEEMEDBENEFIT));
            theCharityDonation = getBucket(myList.getEventInfoCategory(TransactionInfoClass.CHARITYDONATION));
            theTaxRelief = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXRELIEF));
            theTaxableGains = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXABLEGAIN));
            theTaxFreeGains = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXFREEGAIN));
            theCapitalGains = getBucket(myList.getSingularClass(TransactionCategoryClass.CAPITALGAIN));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected EventCategoryBucketList(final Analysis pAnalysis,
                                          final EventCategoryBucketList pBase,
                                          final JDateDay pDate) {
            /* Initialise class */
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Don't use implied buckets */
            theTaxBasis = null;
            theTaxCredit = null;
            theNatInsurance = null;
            theDeemedBenefit = null;
            theCharityDonation = null;
            theTaxRelief = null;
            theTaxableGains = null;
            theTaxFreeGains = null;
            theCapitalGains = null;

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                EventCategoryBucket myBucket = new EventCategoryBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
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
        protected EventCategoryBucketList(final Analysis pAnalysis,
                                          final EventCategoryBucketList pBase,
                                          final JDateDayRange pRange) {
            /* Initialise class */
            super(EventCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Don't use implied buckets */
            theTaxBasis = null;
            theTaxCredit = null;
            theNatInsurance = null;
            theDeemedBenefit = null;
            theCharityDonation = null;
            theTaxRelief = null;
            theTaxableGains = null;
            theTaxFreeGains = null;
            theCapitalGains = null;

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                EventCategoryBucket myBucket = new EventCategoryBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
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
        protected EventCategoryBucket getBucket(final TransactionCategory pCategory) {
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
        protected EventCategoryBucket getBucket(final TransactionCategoryClass pClass) {
            /* Determine required category */
            TransactionCategory myCategory = theData.getTransCategories().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myCategory);
        }

        /**
         * Adjust category buckets.
         * @param pTrans the transaction
         * @param pCategory primary category
         */
        protected void adjustCategories(final Transaction pTrans,
                                        final TransactionCategory pCategory) {
            /* Adjust the primary category bucket */
            EventCategoryBucket myCatBucket = getBucket(pCategory);
            myCatBucket.adjustValues(pTrans);

            /* Adjust tax basis */
            theTaxBasis.adjustBasis(pTrans, pCategory);

            /* Adjust for Tax Credit */
            JMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                if (pCategory.isCategoryClass(TransactionCategoryClass.LOANINTERESTCHARGED)) {
                    theTaxRelief.addIncome(pTrans, myTaxCredit);
                    myTaxCredit = new JMoney(myTaxCredit);
                    myTaxCredit.negate();
                    theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myTaxCredit);
                } else {
                    theTaxCredit.addExpense(pTrans, myTaxCredit);
                    theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXPAID, myTaxCredit);
                }
            }

            /* Adjust for NatInsurance */
            JMoney myNatIns = pTrans.getNatInsurance();
            if (myNatIns != null) {
                theNatInsurance.addExpense(pTrans, myNatIns);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myNatIns);
            }

            /* Adjust for DeemedBenefit */
            JMoney myBenefit = pTrans.getDeemedBenefit();
            if (myBenefit != null) {
                theDeemedBenefit.addExpense(pTrans, myBenefit);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myBenefit);
            }

            /* Adjust for Charity Donation */
            JMoney myDonation = pTrans.getCharityDonation();
            if (myDonation != null) {
                theCharityDonation.addExpense(pTrans, myDonation);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.EXPENSE, myDonation);
            }
        }

        /**
         * Adjust for Standard Gains.
         * @param pTrans the transaction
         * @param pSource the source security holding
         * @param pGains the gains
         */
        protected void adjustStandardGain(final Transaction pTrans,
                                          final SecurityHolding pSource,
                                          final JMoney pGains) {
            /* Access security and portfolio */
            Security mySecurity = pSource.getSecurity();
            Portfolio myPortfolio = pSource.getPortfolio();

            /* If this is subject to capital gains */
            if (mySecurity.getSecurityTypeClass().isCapitalGains()) {
                /* Add to Capital Gains income/expense */
                if (pGains.isPositive()) {
                    /* Adjust category */
                    if (myPortfolio.isTaxFree()) {
                        theTaxFreeGains.addIncome(pTrans, pGains);
                        theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXFREE, pGains);
                    } else {
                        theCapitalGains.addIncome(pTrans, pGains);
                        theTaxBasis.adjustValue(pTrans, TaxBasisClass.GROSSCAPITALGAINS, pGains);
                    }
                } else {
                    /* Adjust category */
                    if (myPortfolio.isTaxFree()) {
                        theTaxFreeGains.subtractExpense(pTrans, pGains);
                        theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXFREE, pGains);
                    } else {
                        theCapitalGains.subtractExpense(pTrans, pGains);
                        theTaxBasis.adjustValue(pTrans, TaxBasisClass.GROSSCAPITALGAINS, pGains);
                    }
                }
            }
        }

        /**
         * Adjust for Taxable Gains.
         * @param pTrans the transaction
         * @param pReduction the income reduction
         */
        protected void adjustTaxableGain(final Transaction pTrans,
                                         final JMoney pReduction) {
            /* Adjust Taxable Gains */
            theTaxableGains.subtractIncome(pReduction);
            theTaxableGains.adjustValues(pTrans);

            /* Obtain normalised value */
            JMoney myGains = new JMoney(pTrans.getAmount());
            myGains.subtractAmount(pReduction);

            /* Adjust for Tax Credit */
            JMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                theTaxCredit.addExpense(pTrans, myTaxCredit);
                myGains.addAmount(myTaxCredit);

                /* Adjust tax basis */
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXPAID, myTaxCredit);
            }

            /* Adjust tax basis */
            theTaxBasis.adjustValue(pTrans, TaxBasisClass.GROSSTAXABLEGAINS, myGains);
        }

        /**
         * Produce totals for the EventCategories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets */
            OrderedIdList<Integer, EventCategoryBucket> myTotals = new OrderedIdList<Integer, EventCategoryBucket>(EventCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<EventCategoryBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                TransactionCategory myCategory = myCurr.getEventCategory();
                TransactionCategory myParent = myCategory.getParentCategory();

                /* Access parent bucket */
                EventCategoryBucket myTotal = findItemById(myParent.getId());

                /* Calculate the delta */
                myCurr.calculateDelta();

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
                myTotal.addValues(myCurr);
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                EventCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                add(myCurr);
            }

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
