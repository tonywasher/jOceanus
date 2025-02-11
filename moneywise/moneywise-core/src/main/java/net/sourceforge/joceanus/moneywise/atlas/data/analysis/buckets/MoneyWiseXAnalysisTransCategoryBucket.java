/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketRegister;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

/**
 * Transaction Category Bucket.
 */
public final class MoneyWiseXAnalysisTransCategoryBucket
        implements MetisFieldTableItem, MoneyWiseXAnalysisBucketRegister {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisTransCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTransCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTransCategoryBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseXAnalysisTransCategoryBucket::getTransactionCategory);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseXAnalysisTransCategoryBucket::getTransactionCategoryType);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisTransCategoryBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_HISTORY, MoneyWiseXAnalysisTransCategoryBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisTransAttr.class, MoneyWiseXAnalysisTransCategoryBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The transaction category.
     */
    private final MoneyWiseTransCategory theCategory;

    /**
     * The transaction category type.
     */
    private final MoneyWiseTransCategoryType theType;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisTransValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisTransValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisTransValues, MoneyWiseXAnalysisTransAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the transaction category
     */
    private MoneyWiseXAnalysisTransCategoryBucket(final MoneyWiseXAnalysis pAnalysis,
                                                  final MoneyWiseTransCategory pCategory) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theCategory = pCategory;
        theType = pCategory == null
                ? null
                : pCategory.getCategoryType();

        /* Create the history map */
        final MoneyWiseCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                ? MoneyWiseXAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseXAnalysisTransValues myValues = new MoneyWiseXAnalysisTransValues(myCurrency);
        theHistory = new MoneyWiseXAnalysisHistory<>(myValues);

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
    private MoneyWiseXAnalysisTransCategoryBucket(final MoneyWiseXAnalysis pAnalysis,
                                                  final MoneyWiseXAnalysisTransCategoryBucket pBase,
                                                  final OceanusDate pDate) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pDate);

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
    private MoneyWiseXAnalysisTransCategoryBucket(final MoneyWiseXAnalysis pAnalysis,
                                                  final MoneyWiseXAnalysisTransCategoryBucket pBase,
                                                  final OceanusDateRange pRange) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisTransCategoryBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Long getBucketId() {
        return MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.TRANSACTIONCATEGORY, getIndexedId());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theCategory == null
                ? NAME_TOTALS.getId()
                : theCategory.getName();
    }

    @Override
    public Integer getIndexedId() {
        return theCategory.getIndexedId();
    }

    /**
     * Obtain the transaction category.
     * @return the transaction category
     */
    public MoneyWiseTransCategory getTransactionCategory() {
        return theCategory;
    }

    /**
     * Obtain the transaction category type.
     * @return the transaction category type
     */
    public MoneyWiseTransCategoryType getTransactionCategoryType() {
        return theType;
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseXAnalysisTransValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseXAnalysisTransValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisTransValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Obtain values for event */
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain previous values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisTransValues getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getPreviousValuesForEvent(pEvent);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                           final MoneyWiseXAnalysisTransAttr pAttr) {
        /* Obtain delta for event */
        return theHistory.getDeltaValue(pEvent, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisTransValues, MoneyWiseXAnalysisTransAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public OceanusDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    void setValue(final MoneyWiseXAnalysisTransAttr pAttr,
                  final OceanusMoney pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisTransAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final MoneyWiseXAnalysisTransAttr pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    void adjustCounter(final MoneyWiseXAnalysisTransAttr pAttr,
                       final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    public void addIncome(final OceanusMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseXAnalysisTransAttr.INCOME, pValue);
        }
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    public void subtractIncome(final OceanusMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            final OceanusMoney myIncome = new OceanusMoney(pValue);
            myIncome.negate();
            adjustCounter(MoneyWiseXAnalysisTransAttr.INCOME, myIncome);
        }
    }

    /**
     * Adjust account for delta.
     * @param pDelta the delta
     */
    public void adjustForDelta(final OceanusMoney pDelta) {
        if (pDelta.isPositive()) {
            addIncome(pDelta);
        } else {
            subtractExpense(pDelta);
        }
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    public void addExpense(final OceanusMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseXAnalysisTransAttr.EXPENSE, pValue);
        }
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    public void subtractExpense(final OceanusMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            final OceanusMoney myExpense = new OceanusMoney(pValue);
            myExpense.negate();
            adjustCounter(MoneyWiseXAnalysisTransAttr.EXPENSE, myExpense);
        }
    }

    /**
     * Calculate Income delta.
     */
    void calculateDelta() {
        /* Calculate delta for the values */
        theValues.calculateDelta();
    }

    /**
     * Adjust to base.
     */
    void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    void addValues(final MoneyWiseXAnalysisTransCategoryBucket pSource) {
        /* Access source values */
        final MoneyWiseXAnalysisTransValues mySource = pSource.getValues();

        /* Add income values */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME);
        OceanusMoney mySrcValue = mySource.getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE);
        mySrcValue = mySource.getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    @Override
    public void registerEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * TransactionCategoryBucket list class.
     */
    public static final class MoneyWiseXAnalysisTransCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisTransCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisTransCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTransCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTransCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisTransCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisTransCategoryBucket> theList;

        /**
         * The editSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The totals.
         */
        private final MoneyWiseXAnalysisTransCategoryBucket theTotals;

         /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisTransCategoryBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisTransCategoryBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                  final MoneyWiseXAnalysisTransCategoryBucketList pBase,
                                                  final OceanusDate pDate) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisTransCategoryBucket myBucket = new MoneyWiseXAnalysisTransCategoryBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        MoneyWiseXAnalysisTransCategoryBucketList(final MoneyWiseXAnalysis pAnalysis,
                                                  final MoneyWiseXAnalysisTransCategoryBucketList pBase,
                                                  final OceanusDateRange pRange) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisTransCategoryBucket myBucket = new MoneyWiseXAnalysisTransCategoryBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisTransCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisTransCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisTransCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals TransactionCategoryBucket.
         * @return the bucket
         */
        private MoneyWiseXAnalysisTransCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseXAnalysisTransCategoryBucket(theAnalysis, null);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction infoClass.
         * @param pClass the transaction infoClass
         * @return the bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getEventInfoBucket(final MoneyWiseTransInfoClass pClass) {
            /* Determine category */
            final MoneyWiseTransCategoryList myList = theEditSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);
            final MoneyWiseTransCategory myCategory = myList.getEventInfoCategory(pClass);

            /* Access bucket */
            return myCategory == null
                    ? null
                    : getBucket(myCategory);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transactionClass.
         * @param pClass the transaction infoClass
         * @return the bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getEventSingularBucket(final MoneyWiseTransCategoryClass pClass) {
            /* Determine category */
            final MoneyWiseTransCategoryList myList = theEditSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);
            final MoneyWiseTransCategory myCategory = myList.getSingularClass(pClass);

            /* Access bucket */
            return myCategory == null
                    ? null
                    : getBucket(myCategory);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction category.
         * @param pCategory the transaction category
         * @return the bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getBucket(final MoneyWiseTransCategory pCategory) {
            /* Handle null category */
            if (pCategory == null) {
                throw new IllegalArgumentException();
            }

            /* Locate the bucket in the list */
            MoneyWiseXAnalysisTransCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisTransCategoryBucket(theAnalysis, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction category class.
         * @param pClass the transaction category class
         * @return the bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getBucket(final MoneyWiseTransCategoryClass pClass) {
            /* Determine required category */
            final MoneyWiseTransCategory myCategory = theEditSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class)
                    .getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myCategory);
        }

        /**
         * Obtain the matching CategoryBucket.
         * @param pCategory the category
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getMatchingCategory(final MoneyWiseTransCategory pCategory) {
            /* Return the matching category if it exists else an orphan bucket */
            final MoneyWiseXAnalysisTransCategoryBucket myCategory = findItemById(pCategory.getIndexedId());
            return myCategory != null
                    ? myCategory
                    : new MoneyWiseXAnalysisTransCategoryBucket(theAnalysis, pCategory);
        }

        /**
         * Obtain the default CategoryBucket.
         * @return the default bucket
         */
        public MoneyWiseXAnalysisTransCategoryBucket getDefaultCategory() {
            /* Return the first category in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Produce totals for the TransactionCategories.
         */
        public void produceTotals() {
            /* Create a list of new buckets */
            final MetisListIndexed<MoneyWiseXAnalysisTransCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Remove idle items */
                if (myCurr.isIdle()) {
                    myIterator.remove();
                    continue;
                }

                /* Obtain category and parent category */
                final MoneyWiseTransCategory myCategory = myCurr.getTransactionCategory();
                final MoneyWiseTransCategory myParent = myCategory.getParentCategory();

                /* Access parent bucket */
                MoneyWiseXAnalysisTransCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseXAnalysisTransCategoryBucket(theAnalysis, myParent);
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
                final MoneyWiseXAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                theList.add(myCurr);
            }

            /* Sort the list */
            theList.getUnderlyingList().sort(Comparator.comparing(MoneyWiseXAnalysisTransCategoryBucket::getTransactionCategory));

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
