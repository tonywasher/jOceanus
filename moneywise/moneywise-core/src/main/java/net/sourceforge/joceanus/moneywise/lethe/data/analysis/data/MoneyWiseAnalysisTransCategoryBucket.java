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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.util.Comparator;
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
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisHistory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisCategoryValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Transaction Category Bucket.
 */
public final class MoneyWiseAnalysisTransCategoryBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTransCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTransCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTransCategoryBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseAnalysisTransCategoryBucket::getTransactionCategory);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TRANSTYPE, MoneyWiseAnalysisTransCategoryBucket::getTransactionCategoryType);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisTransCategoryBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_HISTORY, MoneyWiseAnalysisTransCategoryBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisTransAttr.class, MoneyWiseAnalysisTransCategoryBucket::getAttributeValue);
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
    private final MoneyWiseAnalysisCategoryValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisCategoryValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseAnalysisHistory<MoneyWiseAnalysisCategoryValues, MoneyWiseAnalysisTransAttr> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the transaction category
     */
    private MoneyWiseAnalysisTransCategoryBucket(final MoneyWiseAnalysis pAnalysis,
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
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseAnalysisCategoryValues myValues = new MoneyWiseAnalysisCategoryValues(myCurrency);
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
    private MoneyWiseAnalysisTransCategoryBucket(final MoneyWiseAnalysis pAnalysis,
                                                 final MoneyWiseAnalysisTransCategoryBucket pBase,
                                                 final TethysDate pDate) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
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
    private MoneyWiseAnalysisTransCategoryBucket(final MoneyWiseAnalysis pAnalysis,
                                                 final MoneyWiseAnalysisTransCategoryBucket pBase,
                                                 final TethysDateRange pRange) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisTransCategoryBucket> getDataFieldSet() {
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
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public MoneyWiseAnalysisCategoryValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public MoneyWiseAnalysisCategoryValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisCategoryValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
        /* Obtain values for transaction */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisCategoryValues getPreviousValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                final MoneyWiseAnalysisTransAttr pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private MoneyWiseAnalysisHistory<MoneyWiseAnalysisCategoryValues, MoneyWiseAnalysisTransAttr> getHistoryMap() {
        return theHistory;
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
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    void setValue(final MoneyWiseAnalysisTransAttr pAttr,
                  final TethysMoney pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisTransAttr pAttr) {
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
    private Object getValue(final MoneyWiseAnalysisTransAttr pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    void adjustCounter(final MoneyWiseAnalysisTransAttr pAttr,
                       final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Add income value.
     * @param pTrans the transaction helper
     * @param pValue the value to add
     */
    public void addIncome(final MoneyWiseAnalysisTransactionHelper pTrans,
                          final TethysMoney pValue) {
        /* Add the expense */
        addIncome(pValue);

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    public void addIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseAnalysisTransAttr.INCOME, pValue);
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
            adjustCounter(MoneyWiseAnalysisTransAttr.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pTrans the transaction helper
     * @param pValue the value to add
     */
    public void addExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                           final TethysMoney pValue) {
        /* Add the expense */
        addExpense(pValue);

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    public void addExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(MoneyWiseAnalysisTransAttr.EXPENSE, pValue);
        }
    }

    /**
     * Subtract expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to subtract
     */
    public void subtractExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                                final TethysMoney pValue) {
        /* Subtract the expense */
        subtractExpense(pValue);

        /* Register the event in the history */
        registerTransaction(pTrans);
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
            adjustCounter(MoneyWiseAnalysisTransAttr.EXPENSE, myExpense);
        }
    }

    /**
     * Add transaction to totals.
     * @param pTrans the transaction helper
     * @return isIncome true/false
     */
    boolean adjustValues(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Analyse the event */
        final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
        MoneyWiseAssetDirection myDir = pTrans.getDirection();
        boolean isExpense = myClass.isExpense();
        final TethysMoney myAmount = new TethysMoney(pTrans.getLocalAmount());

        /* If this is an expense */
        if (isExpense) {
            /* Adjust for TaxCredit */
            final TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                myAmount.addAmount(myTaxCredit);
            }

            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a recovered expense */
                if (myDir.isFrom()) {
                    /* Add as income */
                    adjustCounter(MoneyWiseAnalysisTransAttr.INCOME, myAmount);
                    isExpense = false;

                    /* else its standard expense */
                } else {
                    /* Add as expense */
                    adjustCounter(MoneyWiseAnalysisTransAttr.EXPENSE, myAmount);
                }
            }

            /* else this is an income */
        } else {
            /* Adjust for TaxCredit */
            final TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                myAmount.addAmount(myTaxCredit);
            }

            /* Adjust for Withheld */
            final TethysMoney myWithheld = pTrans.getWithheld();
            if (myWithheld != null) {
                myAmount.addAmount(myWithheld);
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
                    adjustCounter(MoneyWiseAnalysisTransAttr.EXPENSE, myAmount);

                    /* else standard income */
                } else {
                    /* Add as income */
                    adjustCounter(MoneyWiseAnalysisTransAttr.INCOME, myAmount);
                }
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);

        /* Return the income flag */
        return !isExpense;
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
    void addValues(final MoneyWiseAnalysisTransCategoryBucket pSource) {
        /* Access source values */
        final MoneyWiseAnalysisCategoryValues mySource = pSource.getValues();

        /* Add income values */
        TethysMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME);
        TethysMoney mySrcValue = mySource.getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE);
        mySrcValue = mySource.getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction helper
     */
    void registerTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans.getTransaction(), theValues);
    }

    /**
     * TransactionCategoryBucket list class.
     */
    public static final class MoneyWiseAnalysisTransCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisTransCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisTransCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTransCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTransCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisTransCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisTransCategoryBucket> theList;

        /**
         * The editSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The totals.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theTotals;

        /**
         * The TaxBasis.
         */
        private final MoneyWiseAnalysisTaxBasisBucketList theTaxBasis;

        /**
         * The TaxCredit.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theTaxCredit;

        /**
         * The TaxRelief.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theTaxRelief;

        /**
         * The EmployeeNatIns.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theEmployeeNatIns;

        /**
         * The EmployerNatIns.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theEmployerNatIns;

        /**
         * The DeemedBenefit.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theDeemedBenefit;

        /**
         * The Withheld.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theWithheld;

        /**
         * The CapitalGains.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theCapitalGains;

        /**
         * The TaxFreeGains.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theTaxFreeGains;

        /**
         * The ChargeableGains.
         */
        private final MoneyWiseAnalysisTransCategoryBucket theChargeableGains;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisTransCategoryBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();

            /* Access taxBasis list */
            theTaxBasis = theAnalysis.getTaxBasis();

            /* Obtain the implied buckets */
            theTaxCredit = getEventInfoBucket(MoneyWiseTransInfoClass.TAXCREDIT);
            theEmployeeNatIns = getEventInfoBucket(MoneyWiseTransInfoClass.EMPLOYEENATINS);
            theEmployerNatIns = getEventInfoBucket(MoneyWiseTransInfoClass.EMPLOYERNATINS);
            theDeemedBenefit = getEventInfoBucket(MoneyWiseTransInfoClass.DEEMEDBENEFIT);
            theWithheld = getEventInfoBucket(MoneyWiseTransInfoClass.WITHHELD);
            theTaxRelief = getEventSingularBucket(MoneyWiseTransCategoryClass.TAXRELIEF);
            theChargeableGains = getEventSingularBucket(MoneyWiseTransCategoryClass.CHARGEABLEGAIN);
            theTaxFreeGains = getEventSingularBucket(MoneyWiseTransCategoryClass.TAXFREEGAIN);
            theCapitalGains = getEventSingularBucket(MoneyWiseTransCategoryClass.CAPITALGAIN);
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseAnalysisTransCategoryBucketList(final MoneyWiseAnalysis pAnalysis,
                                                 final MoneyWiseAnalysisTransCategoryBucketList pBase,
                                                 final TethysDate pDate) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();

            /* Don't use implied buckets */
            theTaxBasis = null;
            theTaxCredit = null;
            theEmployeeNatIns = null;
            theEmployerNatIns = null;
            theDeemedBenefit = null;
            theWithheld = null;
            theTaxRelief = null;
            theChargeableGains = null;
            theTaxFreeGains = null;
            theCapitalGains = null;

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTransCategoryBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisTransCategoryBucket myBucket = new MoneyWiseAnalysisTransCategoryBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
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
        MoneyWiseAnalysisTransCategoryBucketList(final MoneyWiseAnalysis pAnalysis,
                                                 final MoneyWiseAnalysisTransCategoryBucketList pBase,
                                                 final TethysDateRange pRange) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();

            /* Don't use implied buckets */
            theTaxBasis = null;
            theTaxCredit = null;
            theEmployeeNatIns = null;
            theEmployerNatIns = null;
            theDeemedBenefit = null;
            theWithheld = null;
            theTaxRelief = null;
            theChargeableGains = null;
            theTaxFreeGains = null;
            theCapitalGains = null;

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTransCategoryBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisTransCategoryBucket myBucket = new MoneyWiseAnalysisTransCategoryBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisTransCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisTransCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisTransCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public MoneyWiseAnalysisTransCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals TransactionCategoryBucket.
         * @return the bucket
         */
        private MoneyWiseAnalysisTransCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseAnalysisTransCategoryBucket(theAnalysis, null);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction infoClass.
         * @param pClass the transaction infoClass
         * @return the bucket
         */
        MoneyWiseAnalysisTransCategoryBucket getEventInfoBucket(final MoneyWiseTransInfoClass pClass) {
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
        MoneyWiseAnalysisTransCategoryBucket getEventSingularBucket(final MoneyWiseTransCategoryClass pClass) {
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
        public MoneyWiseAnalysisTransCategoryBucket getBucket(final MoneyWiseTransCategory pCategory) {
            /* Handle null category */
            if (pCategory == null) {
                throw new IllegalArgumentException();
            }

            /* Locate the bucket in the list */
            MoneyWiseAnalysisTransCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisTransCategoryBucket(theAnalysis, pCategory);

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
        public MoneyWiseAnalysisTransCategoryBucket getBucket(final MoneyWiseTransCategoryClass pClass) {
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
        public MoneyWiseAnalysisTransCategoryBucket getMatchingCategory(final MoneyWiseTransCategory pCategory) {
            /* Return the matching category if it exists else an orphan bucket */
            final MoneyWiseAnalysisTransCategoryBucket myCategory = findItemById(pCategory.getIndexedId());
            return myCategory != null
                    ? myCategory
                    : new MoneyWiseAnalysisTransCategoryBucket(theAnalysis, pCategory);
        }

        /**
         * Obtain the default CategoryBucket.
         * @return the default bucket
         */
        public MoneyWiseAnalysisTransCategoryBucket getDefaultCategory() {
            /* Return the first category in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Adjust category buckets.
         * @param pTrans the transaction helper
         * @param pCategory primary category
         */
        public void adjustCategories(final MoneyWiseAnalysisTransactionHelper pTrans,
                                     final MoneyWiseTransCategory pCategory) {
            /* Adjust the primary category bucket */
            final MoneyWiseAnalysisTransCategoryBucket myCatBucket = getBucket(pCategory);

            /* Adjust for Tax Credit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                if (pCategory.isCategoryClass(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED)) {
                    theTaxRelief.addIncome(pTrans, myTaxCredit);
                    myTaxCredit = new TethysMoney(myTaxCredit);
                    myTaxCredit.negate();
                    theTaxBasis.adjustValue(pTrans, MoneyWiseTaxClass.VIRTUAL, myTaxCredit);
                } else {
                    theTaxCredit.addExpense(pTrans, myTaxCredit);
                    theTaxBasis.adjustGrossValue(pTrans, MoneyWiseTaxClass.TAXPAID, myTaxCredit);
                }
            }

            /* Adjust for EmployeeNatInsurance */
            final boolean isPension = pCategory.isCategoryClass(MoneyWiseTransCategoryClass.PENSIONCONTRIB);
            TethysMoney myNatIns = pTrans.getEmployeeNatIns();
            if (myNatIns != null && myNatIns.isNonZero()) {
                if (!isPension) {
                    theEmployeeNatIns.addIncome(pTrans, myNatIns);
                } else {
                    myCatBucket.addIncome(myNatIns);
                }
                myNatIns = new TethysMoney(myNatIns);
                myNatIns.negate();
                theTaxBasis.adjustNettValue(pTrans, MoneyWiseTaxClass.VIRTUAL, myNatIns);
            }

            /* Adjust for EmployerNatInsurance */
            myNatIns = pTrans.getEmployerNatIns();
            if (myNatIns != null) {
                if (!isPension) {
                    theEmployerNatIns.addIncome(pTrans, myNatIns);
                } else {
                    myCatBucket.addIncome(myNatIns);
                }
                theTaxBasis.adjustValue(pTrans, MoneyWiseTaxClass.TAXFREE, myNatIns);
            }

            /* Adjust for DeemedBenefit */
            final TethysMoney myBenefit = pTrans.getDeemedBenefit();
            if (myBenefit != null) {
                theDeemedBenefit.addIncome(pTrans, myBenefit);
                theWithheld.addExpense(pTrans, myBenefit);
                theTaxBasis.adjustGrossValue(pTrans, MoneyWiseTaxClass.VIRTUAL, myBenefit);
            }

            /* Adjust for Withheld */
            final TethysMoney myWithheld = pTrans.getWithheld();
            if (myWithheld != null) {
                theWithheld.addExpense(pTrans, myWithheld);
                theTaxBasis.adjustValue(pTrans, MoneyWiseTaxClass.VIRTUAL, myWithheld);
            }

            /* Adjust the category bucket */
            myCatBucket.adjustValues(pTrans);

            /* Adjust tax basis */
            theTaxBasis.adjustBasis(pTrans, pCategory);
        }

        /**
         * Adjust for Standard Gains.
         * @param pTrans the transaction helper
         * @param pSource the source security holding
         * @param pGains the gains
         */
        public void adjustStandardGain(final MoneyWiseAnalysisTransactionHelper pTrans,
                                       final MoneyWiseSecurityHolding pSource,
                                       final TethysMoney pGains) {
            /* Access security and portfolio */
            final MoneyWiseSecurity mySecurity = pSource.getSecurity();
            final MoneyWisePortfolio myPortfolio = pSource.getPortfolio();

            final boolean bTaxFreeGains = myPortfolio.isTaxFree()
                    || !mySecurity.getCategoryClass().isCapitalGains();
            final MoneyWiseAnalysisTransCategoryBucket myCategory = bTaxFreeGains
                    ? theTaxFreeGains
                    : theCapitalGains;
            final MoneyWiseTaxClass myTaxBasis = bTaxFreeGains
                    ? MoneyWiseTaxClass.TAXFREE
                    : MoneyWiseTaxClass.CAPITALGAINS;

            /* Add to Capital Gains income/expense */
            if (pGains.isPositive()) {
                myCategory.addIncome(pTrans, pGains);
            } else {
                myCategory.subtractExpense(pTrans, pGains);
            }
            theTaxBasis.adjustValue(pTrans, myTaxBasis, pGains);
        }

        /**
         * Adjust for Chargeable Gains.
         * @param pTrans the transaction helper
         * @param pReduction the income reduction
         */
        public void adjustChargeableGain(final MoneyWiseAnalysisTransactionHelper pTrans,
                                         final TethysMoney pReduction) {
            /* Adjust Taxable Gains */
            theChargeableGains.subtractIncome(pReduction);
            theChargeableGains.adjustValues(pTrans);

            /* Obtain normalised value */
            final TethysMoney myGains = new TethysMoney(pTrans.getLocalAmount());
            myGains.subtractAmount(pReduction);

            /* Adjust for Tax Credit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                /* Adjust the taxCredit category */
                theTaxCredit.addExpense(pTrans, myTaxCredit);

                /* Adjust tax basis */
                theTaxBasis.adjustGrossValue(pTrans, MoneyWiseTaxClass.TAXPAID, myTaxCredit);
                myTaxCredit = new TethysMoney(myTaxCredit);
                myTaxCredit.negate();
                theTaxBasis.adjustGrossValue(pTrans, MoneyWiseTaxClass.VIRTUAL, myTaxCredit);
            }

            /* Adjust tax basis */
            theTaxBasis.adjustValue(pTrans, MoneyWiseTaxClass.CHARGEABLEGAINS, myGains);
        }

        /**
         * Produce totals for the TransactionCategories.
         */
        void produceTotals() {
            /* Create a list of new buckets */
            final MetisListIndexed<MoneyWiseAnalysisTransCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseAnalysisTransCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseTransCategory myCategory = myCurr.getTransactionCategory();
                final MoneyWiseTransCategory myParent = myCategory.getParentCategory();

                /* Access parent bucket */
                MoneyWiseAnalysisTransCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseAnalysisTransCategoryBucket(theAnalysis, myParent);
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
                final MoneyWiseAnalysisTransCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                theList.add(myCurr);
            }

            /* Sort the list */
            theList.getUnderlyingList().sort(Comparator.comparing(MoneyWiseAnalysisTransCategoryBucket::getTransactionCategory));

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
