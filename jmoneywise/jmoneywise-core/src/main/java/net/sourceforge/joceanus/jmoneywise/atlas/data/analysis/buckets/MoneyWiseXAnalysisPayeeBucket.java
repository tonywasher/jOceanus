/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketRegister;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeValues;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * The Payee Bucket class.
 */
public final class MoneyWiseXAnalysisPayeeBucket
        implements MetisFieldTableItem, MoneyWiseXAnalysisBucketRegister {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisPayeeBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisPayeeBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisPayeeBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PAYEE, MoneyWiseXAnalysisPayeeBucket::getPayee);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisPayeeBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_HISTORY, MoneyWiseXAnalysisPayeeBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisPayeeAttr.class, MoneyWiseXAnalysisPayeeBucket::getAttributeValue);
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
     * The payee.
     */
    private final MoneyWisePayee thePayee;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisPayeeValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisPayeeValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisPayeeValues, MoneyWiseXAnalysisPayeeAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPayee the payee
     */
    private MoneyWiseXAnalysisPayeeBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWisePayee pPayee) {
        /* Store the details */
        thePayee = pPayee;
        theAnalysis = pAnalysis;

        /* Create the history map */
        final MoneyWiseCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                ? MoneyWiseXAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseXAnalysisPayeeValues myValues = new MoneyWiseXAnalysisPayeeValues(myCurrency);
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
    private MoneyWiseXAnalysisPayeeBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPayeeBucket pBase,
                                          final TethysDate pDate) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
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
    private MoneyWiseXAnalysisPayeeBucket(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPayeeBucket pBase,
                                          final TethysDateRange pRange) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisPayeeBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return thePayee == null
                ? NAME_TOTALS.getId()
                : thePayee.getName();
    }

    /**
     * Obtain the payee.
     * @return the payee account
     */
    public MoneyWisePayee getPayee() {
        return thePayee;
    }

    @Override
    public Integer getIndexedId() {
        return thePayee.getIndexedId();
    }

    @Override
    public Long getBucketId() {
        return thePayee.getExternalId();
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theHistory.isIdle();
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
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseXAnalysisPayeeValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseXAnalysisPayeeValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisPayeeValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Obtain values for transaction */
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain previous values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisPayeeValues getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getPreviousValuesForEvent(pEvent);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                          final MoneyWiseXAnalysisPayeeAttr pAttr) {
        /* Obtain delta for event */
        return theHistory.getDeltaValue(pEvent, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisPayeeValues, MoneyWiseXAnalysisPayeeAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    void setValue(final MoneyWiseXAnalysisPayeeAttr pAttr,
                  final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisPayeeAttr pAttr) {
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
    private Object getValue(final MoneyWiseXAnalysisPayeeAttr pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    void adjustCounter(final MoneyWiseXAnalysisPayeeAttr pAttr,
                       final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    @Override
    public void registerEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    public void addIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseXAnalysisPayeeAttr.INCOME, pValue);
        }
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    public void subtractIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            final TethysMoney myIncome = new TethysMoney(pValue);
            myIncome.negate();
            setValue(MoneyWiseXAnalysisPayeeAttr.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    public void addExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseXAnalysisPayeeAttr.EXPENSE, pValue);
        }
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    public void subtractExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            final TethysMoney myExpense = new TethysMoney(pValue);
            myExpense.negate();
            adjustCounter(MoneyWiseXAnalysisPayeeAttr.EXPENSE, myExpense);
        }
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    void addValues(final MoneyWiseXAnalysisPayeeBucket pSource) {
        /* Access source values */
        final MoneyWiseXAnalysisPayeeValues mySource = pSource.getValues();

        /* Add income values */
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME);
        TethysMoney mySrcValue = mySource.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE);
        mySrcValue = mySource.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Calculate delta.
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
     * PayeeBucket list class.
     */
    public static final class MoneyWiseXAnalysisPayeeBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisPayeeBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisPayeeBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisPayeeBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisPayeeBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisPayeeBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisPayeeBucket> theList;

        /**
         * The editSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The totals.
         */
        private final MoneyWiseXAnalysisPayeeBucket theTotals;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisPayeeBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getPayee().compareTo(r.getPayee()));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisPayeeBucketList(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPayeeBucketList pBase,
                                          final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisPayeeBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseXAnalysisPayeeBucket myBucket = new MoneyWiseXAnalysisPayeeBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
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
        MoneyWiseXAnalysisPayeeBucketList(final MoneyWiseXAnalysis pAnalysis,
                                          final MoneyWiseXAnalysisPayeeBucketList pBase,
                                          final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisPayeeBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseXAnalysisPayeeBucket myBucket = new MoneyWiseXAnalysisPayeeBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base and add to the list */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisPayeeBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisPayeeBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisPayeeBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public MoneyWiseXAnalysisPayeeBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals PayeeBucket.
         * @return the bucket
         */
        private MoneyWiseXAnalysisPayeeBucket allocateTotalsBucket() {
            /* Obtain the totals payee */
            return new MoneyWiseXAnalysisPayeeBucket(theAnalysis, null);
        }

        /**
         * Obtain the PayeeBucket for a given payee.
         * @param pPayee the payee
         * @return the bucket
         */
        public MoneyWiseXAnalysisPayeeBucket getBucket(final MoneyWiseAssetBase pPayee) {
            /* Handle null payee */
            if (pPayee == null) {
                return null;
            }

            /* Access as payee */
            final MoneyWisePayee myPayee = (MoneyWisePayee) pPayee;

            /* Locate the bucket in the list */
            MoneyWiseXAnalysisPayeeBucket myItem = findItemById(myPayee.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisPayeeBucket(theAnalysis, myPayee);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the PayeeBucket for a given payee class.
         * @param pClass the account category class
         * @return the bucket
         */
        public MoneyWiseXAnalysisPayeeBucket getBucket(final MoneyWisePayeeClass pClass) {
            /* Determine required payee */
            final MoneyWisePayee myPayee = theEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class).getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myPayee);
        }

        /**
         * Obtain the matching PayeeBucket.
         * @param pPayee the payee
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisPayeeBucket getMatchingPayee(final MoneyWisePayee pPayee) {
            /* Return the matching payee if it exists else an orphan bucket */
            final MoneyWiseXAnalysisPayeeBucket myPayee = findItemById(pPayee.getIndexedId());
            return myPayee != null
                    ? myPayee
                    : new MoneyWiseXAnalysisPayeeBucket(theAnalysis, pPayee);
        }

        /**
         * Obtain the default PayeeBucket.
         * @return the default bucket
         */
        public MoneyWiseXAnalysisPayeeBucket getDefaultPayee() {
            /* Return the first payee in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Produce totals for the Payees.
         */
        void produceTotals() {
            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisPayeeBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisPayeeBucket myCurr = myIterator.next();

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Sort the payees */
            theList.sortList();

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
