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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisHistory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeValues;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

/**
 * The Payee Bucket class.
 */
public final class MoneyWiseAnalysisPayeeBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisPayeeBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisPayeeBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisPayeeBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PAYEE, MoneyWiseAnalysisPayeeBucket::getPayee);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisPayeeBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_HISTORY, MoneyWiseAnalysisPayeeBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisPayeeAttr.class, MoneyWiseAnalysisPayeeBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * The payee.
     */
    private final MoneyWisePayee thePayee;

    /**
     * Values.
     */
    private final MoneyWiseAnalysisPayeeValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisPayeeValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseAnalysisHistory<MoneyWiseAnalysisPayeeValues, MoneyWiseAnalysisPayeeAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPayee the payee
     */
    private MoneyWiseAnalysisPayeeBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWisePayee pPayee) {
        /* Store the details */
        thePayee = pPayee;
        theAnalysis = pAnalysis;

        /* Create the history map */
        final MoneyWiseCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseAnalysisPayeeValues myValues = new MoneyWiseAnalysisPayeeValues(myCurrency);
        theHistory = new MoneyWiseAnalysisHistory<>(myValues);

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
    private MoneyWiseAnalysisPayeeBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPayeeBucket pBase,
                                         final OceanusDate pDate) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pDate);

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
    private MoneyWiseAnalysisPayeeBucket(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPayeeBucket pBase,
                                         final OceanusDateRange pRange) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisPayeeBucket> getDataFieldSet() {
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

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    MoneyWiseAnalysis getAnalysis() {
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
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseAnalysisPayeeValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseAnalysisPayeeValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisPayeeValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
        /* Obtain values for transaction */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisPayeeValues getPreviousValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                 final MoneyWiseAnalysisPayeeAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseAnalysisHistory<MoneyWiseAnalysisPayeeValues, MoneyWiseAnalysisPayeeAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    void setValue(final MoneyWiseAnalysisPayeeAttr pAttr,
                  final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisPayeeAttr pAttr) {
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
    private Object getValue(final MoneyWiseAnalysisPayeeAttr pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    void adjustCounter(final MoneyWiseAnalysisPayeeAttr pAttr,
                       final OceanusMoney pDelta) {
        OceanusMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new OceanusMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Adjust account for debit.
     * @param pTrans the transaction helper
     */
    public void adjustForDebit(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Access the class */
        final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
        final boolean isIncome = myClass.isIncome();
        OceanusMoney myIncome = null;
        OceanusMoney myExpense = null;

        /* Access amount */
        final OceanusMoney myAmount = pTrans.getLocalAmount();
        if (myAmount.isNonZero()) {
            /* If this is an income */
            if (isIncome) {
                /* Allocate the income */
                myIncome = new OceanusMoney(myAmount);

                /* else this is a refunded expense */
            } else {
                /* Update the expense */
                myExpense = new OceanusMoney(myAmount);
                myExpense.negate();
            }
        }

        /* If there is a non-zero TaxCredit */
        final OceanusMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred != null
                && myTaxCred.isNonZero()) {
            /* Adjust for Tax Credit */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new OceanusMoney(myTaxCred);
                } else {
                    myIncome.addAmount(myTaxCred);
                }
            } else {
                if (myExpense == null) {
                    myExpense = new OceanusMoney(myTaxCred);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myTaxCred);
                }
            }
        }

        /* If there is Employee National Insurance */
        OceanusMoney myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null
                && myNatIns.isNonZero()) {
            /* Adjust for National Insurance */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new OceanusMoney(myNatIns);
                } else {
                    myIncome.addAmount(myNatIns);
                }
            } else {
                if (myExpense == null) {
                    myExpense = new OceanusMoney(myNatIns);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myNatIns);
                }
            }
        }

        /* If there is Employer National Insurance */
        myNatIns = pTrans.getEmployerNatIns();
        if (myNatIns != null
                && myNatIns.isNonZero()) {
            /* Adjust for National Insurance */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new OceanusMoney(myNatIns);
                } else {
                    myIncome.addAmount(myNatIns);
                }
            } else {
                if (myExpense == null) {
                    myExpense = new OceanusMoney(myNatIns);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myNatIns);
                }
            }
        }

        /* If there is Withheld */
        final OceanusMoney myWithheld = pTrans.getWithheld();
        if (myWithheld != null
                && myWithheld.isNonZero()) {
            /* Adjust for Charity Donation */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new OceanusMoney(myWithheld);
                } else {
                    myIncome.addAmount(myWithheld);
                }
                myExpense = new OceanusMoney(myWithheld);
            } else {
                myIncome = new OceanusMoney(myWithheld);
                myIncome.negate();
                if (myExpense == null) {
                    myExpense = new OceanusMoney(myAmount);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myWithheld);
                }
            }
        }

        /* Set new values */
        if (myIncome != null) {
            adjustCounter(MoneyWiseAnalysisPayeeAttr.INCOME, myIncome);
        }
        if (myExpense != null) {
            adjustCounter(MoneyWiseAnalysisPayeeAttr.EXPENSE, myExpense);
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Adjust account for credit.
     * @param pTrans the transaction helper
     */
    public void adjustForCredit(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Analyse the transaction */
        final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
        final boolean isExpense = !myClass.isIncome();
        OceanusMoney myExpense = null;
        OceanusMoney myIncome = null;

        /* Access amount */
        final OceanusMoney myAmount = pTrans.getLocalAmount();
        if (myAmount.isNonZero()) {
            /* If this is an expense */
            if (isExpense) {
                /* Allocate the expense */
                myExpense = new OceanusMoney(myAmount);

                /* else this is a refunded income */
            } else {
                /* Update the income */
                myIncome = new OceanusMoney(myAmount);
                myIncome.negate();
            }
        }

        /* If there is a non-zero TaxCredit */
        final OceanusMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred != null && myTaxCred.isNonZero()) {
            /* Adjust for Tax Credit */
            if (isExpense) {
                if (myExpense == null) {
                    myExpense = new OceanusMoney(myTaxCred);
                } else {
                    myExpense.addAmount(myTaxCred);
                }
            } else {
                if (myIncome == null) {
                    myIncome = new OceanusMoney(myTaxCred);
                    myIncome.negate();
                } else {
                    myIncome.subtractAmount(myTaxCred);
                }
            }
        }

        /* If we have an expense */
        if (myExpense != null) {
            /* Record it */
            adjustCounter(MoneyWiseAnalysisPayeeAttr.EXPENSE, myExpense);
        }
        if (myIncome != null) {
            /* Record it */
            adjustCounter(MoneyWiseAnalysisPayeeAttr.INCOME, myIncome);
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Adjust account for tax credit.
     * @param pTrans the transaction helper
     */
    public void adjustForTaxCredit(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Access amount */
        final OceanusMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred.isNonZero()) {
            final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
            if (myClass.isExpense()) {
                /* Update the expense */
                adjustCounter(MoneyWiseAnalysisPayeeAttr.EXPENSE, myTaxCred);
            } else {
                /* Update the income */
                adjustCounter(MoneyWiseAnalysisPayeeAttr.INCOME, myTaxCred);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Adjust account for tax payments.
     * @param pTrans the transaction causing the payments
     */
    public void adjustForTaxPayments(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Determine transaction type */
        final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
        OceanusMoney myAmount = null;

        /* Adjust for Tax credit */
        final OceanusMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred != null
                && myTaxCred.isNonZero()) {
            myAmount = new OceanusMoney(myTaxCred);
        }

        /* If we have payments */
        if (myAmount != null) {
            /* Adjust correct bucket */
            if (myClass.isExpense()) {
                adjustCounter(MoneyWiseAnalysisPayeeAttr.INCOME, myAmount);
            } else {
                adjustCounter(MoneyWiseAnalysisPayeeAttr.EXPENSE, myAmount);
            }

            /* Register the transaction in the history */
            registerTransaction(pTrans);
        }
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction helper
     */
    public void registerTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans.getTransaction(), theValues);
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    public void addIncome(final OceanusMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseAnalysisPayeeAttr.INCOME, pValue);
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
            setValue(MoneyWiseAnalysisPayeeAttr.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to add
     */
    public void addExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                           final OceanusMoney pValue) {
        /* Add the expense */
        addExpense(pValue);

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    public void addExpense(final OceanusMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseAnalysisPayeeAttr.EXPENSE, pValue);
        }
    }

    /**
     * Subtract expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to subtract
     */
    public void subtractExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                                final OceanusMoney pValue) {
        /* Subtract the expense */
        subtractExpense(pValue);

        /* Register the transaction in the history */
        registerTransaction(pTrans);
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
            adjustCounter(MoneyWiseAnalysisPayeeAttr.EXPENSE, myExpense);
        }
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    void addValues(final MoneyWiseAnalysisPayeeBucket pSource) {
        /* Access source values */
        final MoneyWiseAnalysisPayeeValues mySource = pSource.getValues();

        /* Add income values */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        OceanusMoney mySrcValue = mySource.getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE);
        mySrcValue = mySource.getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE);
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
    public static final class MoneyWiseAnalysisPayeeBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisPayeeBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisPayeeBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisPayeeBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisPayeeBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisPayeeBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisPayeeBucket> theList;

        /**
         * The editSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The totals.
         */
        private final MoneyWiseAnalysisPayeeBucket theTotals;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisPayeeBucketList(final MoneyWiseAnalysis pAnalysis) {
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
        MoneyWiseAnalysisPayeeBucketList(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPayeeBucketList pBase,
                                         final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisPayeeBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisPayeeBucket myBucket = new MoneyWiseAnalysisPayeeBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
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
        MoneyWiseAnalysisPayeeBucketList(final MoneyWiseAnalysis pAnalysis,
                                         final MoneyWiseAnalysisPayeeBucketList pBase,
                                         final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisPayeeBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisPayeeBucket myBucket = new MoneyWiseAnalysisPayeeBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Adjust to the base and add to the list */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisPayeeBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisPayeeBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisPayeeBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public MoneyWiseAnalysisPayeeBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals PayeeBucket.
         * @return the bucket
         */
        private MoneyWiseAnalysisPayeeBucket allocateTotalsBucket() {
            /* Obtain the totals payee */
            return new MoneyWiseAnalysisPayeeBucket(theAnalysis, null);
        }

        /**
         * Obtain the PayeeBucket for a given payee.
         * @param pPayee the payee
         * @return the bucket
         */
        public MoneyWiseAnalysisPayeeBucket getBucket(final MoneyWiseAssetBase pPayee) {
            /* Handle null payee */
            if (pPayee == null) {
                return null;
            }

            /* Access as payee */
            final MoneyWisePayee myPayee = (MoneyWisePayee) pPayee;

            /* Locate the bucket in the list */
            MoneyWiseAnalysisPayeeBucket myItem = findItemById(myPayee.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisPayeeBucket(theAnalysis, myPayee);

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
        public MoneyWiseAnalysisPayeeBucket getBucket(final MoneyWisePayeeClass pClass) {
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
        public MoneyWiseAnalysisPayeeBucket getMatchingPayee(final MoneyWisePayee pPayee) {
            /* Return the matching payee if it exists else an orphan bucket */
            final MoneyWiseAnalysisPayeeBucket myPayee = findItemById(pPayee.getIndexedId());
            return myPayee != null
                    ? myPayee
                    : new MoneyWiseAnalysisPayeeBucket(theAnalysis, pPayee);
        }

        /**
         * Obtain the default PayeeBucket.
         * @return the default bucket
         */
        public MoneyWiseAnalysisPayeeBucket getDefaultPayee() {
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
            final Iterator<MoneyWiseAnalysisPayeeBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisPayeeBucket myCurr = myIterator.next();

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
