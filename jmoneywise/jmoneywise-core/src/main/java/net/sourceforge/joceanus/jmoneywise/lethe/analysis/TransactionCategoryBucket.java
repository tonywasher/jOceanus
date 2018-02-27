/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Transaction Category Bucket.
 */
public final class TransactionCategoryBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<TransactionCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionCategoryBucket.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, TransactionCategoryBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryBucket::getTransactionCategory);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSTYPE, TransactionCategoryBucket::getTransactionCategoryType);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES, TransactionCategoryBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY, TransactionCategoryBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(TransactionAttribute.class, TransactionCategoryBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The transaction category.
     */
    private final TransactionCategory theCategory;

    /**
     * The transaction category type.
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
    private final BucketHistory<CategoryValues, TransactionAttribute> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the transaction category
     */
    private TransactionCategoryBucket(final Analysis pAnalysis,
                                      final TransactionCategory pCategory) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theCategory = pCategory;
        theType = (pCategory == null)
                                      ? null
                                      : pCategory.getCategoryType();

        /* Create the history map */
        final AssetCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                                                      ? AccountBucket.DEFAULT_CURRENCY
                                                      : myDefault.getCurrency();
        final CategoryValues myValues = new CategoryValues(myCurrency);
        theHistory = new BucketHistory<>(myValues);

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
    private TransactionCategoryBucket(final Analysis pAnalysis,
                                      final TransactionCategoryBucket pBase,
                                      final TethysDate pDate) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pDate);

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
    private TransactionCategoryBucket(final Analysis pAnalysis,
                                      final TransactionCategoryBucket pBase,
                                      final TethysDateRange pRange) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<TransactionCategoryBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
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
        return theCategory.getId();
    }

    /**
     * Obtain the transaction category.
     * @return the transaction category
     */
    public TransactionCategory getTransactionCategory() {
        return theCategory;
    }

    /**
     * Obtain the transaction category type.
     * @return the transaction category type
     */
    public TransactionCategoryType getTransactionCategoryType() {
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
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public CategoryValues getPreviousValuesForTransaction(final Transaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final Transaction pTrans,
                                                final TransactionAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<CategoryValues, TransactionAttribute> getHistoryMap() {
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
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final TransactionAttribute pAttr,
                            final TethysMoney pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final TransactionAttribute pAttr) {
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
    private Object getValue(final TransactionAttribute pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final TransactionAttribute pAttr,
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
    protected void addIncome(final TransactionHelper pTrans,
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
    protected void addIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(TransactionAttribute.INCOME, pValue);
        }
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    protected void subtractIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            final TethysMoney myIncome = new TethysMoney(pValue);
            myIncome.negate();
            adjustCounter(TransactionAttribute.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pTrans the transaction helper
     * @param pValue the value to add
     */
    protected void addExpense(final TransactionHelper pTrans,
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
    protected void addExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(TransactionAttribute.EXPENSE, pValue);
        }
    }

    /**
     * Subtract expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final TransactionHelper pTrans,
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
    protected void subtractExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            final TethysMoney myExpense = new TethysMoney(pValue);
            myExpense.negate();
            adjustCounter(TransactionAttribute.EXPENSE, myExpense);
        }
    }

    /**
     * Add transaction to totals.
     * @param pTrans the transaction helper
     * @return isIncome true/false
     */
    protected boolean adjustValues(final TransactionHelper pTrans) {
        /* Analyse the event */
        final TransactionCategoryClass myClass = pTrans.getCategoryClass();
        AssetDirection myDir = pTrans.getDirection();
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
                    adjustCounter(TransactionAttribute.INCOME, myAmount);
                    isExpense = false;

                    /* else its standard expense */
                } else {
                    /* Add as expense */
                    adjustCounter(TransactionAttribute.EXPENSE, myAmount);
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
                    adjustCounter(TransactionAttribute.EXPENSE, myAmount);

                    /* else standard income */
                } else {
                    /* Add as income */
                    adjustCounter(TransactionAttribute.INCOME, myAmount);
                    isExpense = false;
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
    protected void calculateDelta() {
        /* Calculate delta for the values */
        theValues.calculateDelta();
    }

    /**
     * Adjust to base.
     */
    protected void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    protected void addValues(final TransactionCategoryBucket pSource) {
        /* Access source values */
        final CategoryValues mySource = pSource.getValues();

        /* Add income values */
        TethysMoney myValue = theValues.getMoneyValue(TransactionAttribute.INCOME);
        TethysMoney mySrcValue = mySource.getMoneyValue(TransactionAttribute.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(TransactionAttribute.EXPENSE);
        mySrcValue = mySource.getMoneyValue(TransactionAttribute.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction helper
     */
    protected void registerTransaction(final TransactionHelper pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans.getTransaction(), theValues);
    }

    /**
     * CategoryValues class.
     */
    public static final class CategoryValues
            extends BucketValues<CategoryValues, TransactionAttribute> {
        /**
         * Constructor.
         * @param pCurrency the reporting currency
         */
        private CategoryValues(final Currency pCurrency) {
            /* Initialise class */
            super(TransactionAttribute.class);

            /* Create all possible values */
            super.setValue(TransactionAttribute.INCOME, new TethysMoney(pCurrency));
            super.setValue(TransactionAttribute.EXPENSE, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        private CategoryValues(final CategoryValues pSource,
                               final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected CategoryValues getCounterSnapShot() {
            return new CategoryValues(this, true);
        }

        @Override
        protected CategoryValues getFullSnapShot() {
            return new CategoryValues(this, false);
        }

        @Override
        protected void adjustToBaseValues(final CategoryValues pBase) {
            /* Adjust income/expense values */
            adjustMoneyToBase(pBase, TransactionAttribute.INCOME);
            adjustMoneyToBase(pBase, TransactionAttribute.EXPENSE);
            calculateDelta();
        }

        /**
         * Calculate delta.
         */
        private void calculateDelta() {
            /* Obtain a copy of the value */
            TethysMoney myDelta = getMoneyValue(TransactionAttribute.INCOME);
            myDelta = new TethysMoney(myDelta);

            /* Subtract the expense value */
            final TethysMoney myExpense = getMoneyValue(TransactionAttribute.EXPENSE);
            myDelta.subtractAmount(myExpense);

            /* Set the delta */
            super.setValue(TransactionAttribute.PROFIT, myDelta);
        }

        @Override
        protected void resetBaseValues() {
            /* Create a zero value in the correct currency */
            TethysMoney myValue = getMoneyValue(TransactionAttribute.INCOME);
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Reset Income and expense values */
            super.setValue(TransactionAttribute.INCOME, myValue);
            super.setValue(TransactionAttribute.EXPENSE, new TethysMoney(myValue));
            super.setValue(TransactionAttribute.PROFIT, new TethysMoney(myValue));
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            final TethysMoney myIncome = getMoneyValue(TransactionAttribute.INCOME);
            final TethysMoney myExpense = getMoneyValue(TransactionAttribute.EXPENSE);
            return (myIncome.isNonZero()) || (myExpense.isNonZero());
        }
    }

    /**
     * TransactionCategoryBucket list class.
     */
    public static final class TransactionCategoryBucketList
            implements MetisFieldItem, MetisDataList<TransactionCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<TransactionCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionCategoryBucketList.class);

        /**
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, TransactionCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS, TransactionCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<TransactionCategoryBucket> theList;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

        /**
         * The totals.
         */
        private final TransactionCategoryBucket theTotals;

        /**
         * The TaxBasis.
         */
        private final TaxBasisBucketList theTaxBasis;

        /**
         * The TaxCredit.
         */
        private final TransactionCategoryBucket theTaxCredit;

        /**
         * The TaxRelief.
         */
        private final TransactionCategoryBucket theTaxRelief;

        /**
         * The EmployeeNatIns.
         */
        private final TransactionCategoryBucket theEmployeeNatIns;

        /**
         * The EmployerNatIns.
         */
        private final TransactionCategoryBucket theEmployerNatIns;

        /**
         * The DeemedBenefit.
         */
        private final TransactionCategoryBucket theDeemedBenefit;

        /**
         * The Withheld.
         */
        private final TransactionCategoryBucket theWithheld;

        /**
         * The CapitalGains.
         */
        private final TransactionCategoryBucket theCapitalGains;

        /**
         * The TaxFreeGains.
         */
        private final TransactionCategoryBucket theTaxFreeGains;

        /**
         * The ChargeableGains.
         */
        private final TransactionCategoryBucket theChargeableGains;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected TransactionCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();

            /* Access taxBasis list */
            theTaxBasis = theAnalysis.getTaxBasis();

            /* Obtain the implied buckets */
            final TransactionCategoryList myList = theData.getTransCategories();
            theTaxCredit = getBucket(myList.getEventInfoCategory(TransactionInfoClass.TAXCREDIT));
            theEmployeeNatIns = getBucket(myList.getEventInfoCategory(TransactionInfoClass.EMPLOYEENATINS));
            theEmployerNatIns = getBucket(myList.getEventInfoCategory(TransactionInfoClass.EMPLOYERNATINS));
            theDeemedBenefit = getBucket(myList.getEventInfoCategory(TransactionInfoClass.DEEMEDBENEFIT));
            theWithheld = getBucket(myList.getEventInfoCategory(TransactionInfoClass.WITHHELD));
            theTaxRelief = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXRELIEF));
            theChargeableGains = getBucket(myList.getSingularClass(TransactionCategoryClass.CHARGEABLEGAIN));
            theTaxFreeGains = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXFREEGAIN));
            theCapitalGains = getBucket(myList.getSingularClass(TransactionCategoryClass.CAPITALGAIN));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected TransactionCategoryBucketList(final Analysis pAnalysis,
                                                final TransactionCategoryBucketList pBase,
                                                final TethysDate pDate) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
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
            final Iterator<TransactionCategoryBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TransactionCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final TransactionCategoryBucket myBucket = new TransactionCategoryBucket(pAnalysis, myCurr, pDate);

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
        protected TransactionCategoryBucketList(final Analysis pAnalysis,
                                                final TransactionCategoryBucketList pBase,
                                                final TethysDateRange pRange) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
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
            final Iterator<TransactionCategoryBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TransactionCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final TransactionCategoryBucket myBucket = new TransactionCategoryBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<TransactionCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<TransactionCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public TransactionCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public TransactionCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals TransactionCategoryBucket.
         * @return the bucket
         */
        private TransactionCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new TransactionCategoryBucket(theAnalysis, null);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction category.
         * @param pCategory the transaction category
         * @return the bucket
         */
        protected TransactionCategoryBucket getBucket(final TransactionCategory pCategory) {
            /* Handle null category */
            if (pCategory == null) {
                return null;
            }

            /* Locate the bucket in the list */
            TransactionCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TransactionCategoryBucket(theAnalysis, pCategory);

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
        protected TransactionCategoryBucket getBucket(final TransactionCategoryClass pClass) {
            /* Determine required category */
            final TransactionCategory myCategory = theData.getTransCategories().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myCategory);
        }

        /**
         * Obtain the matching CategoryBucket.
         * @param pCategory the category
         * @return the matching bucket
         */
        public TransactionCategoryBucket getMatchingCategory(final TransactionCategory pCategory) {
            /* Return the matching category if it exists else an orphan bucket */
            final TransactionCategoryBucket myCategory = findItemById(pCategory.getIndexedId());
            return myCategory != null
                                      ? myCategory
                                      : new TransactionCategoryBucket(theAnalysis, pCategory);
        }

        /**
         * Obtain the default CategoryBucket.
         * @return the default bucket
         */
        public TransactionCategoryBucket getDefaultCategory() {
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
        protected void adjustCategories(final TransactionHelper pTrans,
                                        final TransactionCategory pCategory) {
            /* Adjust the primary category bucket */
            final TransactionCategoryBucket myCatBucket = getBucket(pCategory);

            /* Adjust for Tax Credit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                if (pCategory.isCategoryClass(TransactionCategoryClass.LOANINTERESTCHARGED)) {
                    theTaxRelief.addIncome(pTrans, myTaxCredit);
                    myTaxCredit = new TethysMoney(myTaxCredit);
                    myTaxCredit.negate();
                    theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myTaxCredit);
                } else {
                    theTaxCredit.addExpense(pTrans, myTaxCredit);
                    theTaxBasis.adjustGrossValue(pTrans, TaxBasisClass.TAXPAID, myTaxCredit);
                }
            }

            /* Adjust for EmployeeNatInsurance */
            final boolean isPension = pCategory.isCategoryClass(TransactionCategoryClass.PENSIONCONTRIB);
            TethysMoney myNatIns = pTrans.getEmployeeNatIns();
            if (myNatIns != null && myNatIns.isNonZero()) {
                if (!isPension) {
                    theEmployeeNatIns.addIncome(pTrans, myNatIns);
                } else {
                    myCatBucket.addIncome(myNatIns);
                }
                myNatIns = new TethysMoney(myNatIns);
                myNatIns.negate();
                theTaxBasis.adjustNettValue(pTrans, TaxBasisClass.VIRTUAL, myNatIns);
            }

            /* Adjust for EmployerNatInsurance */
            myNatIns = pTrans.getEmployerNatIns();
            if (myNatIns != null) {
                if (!isPension) {
                    theEmployerNatIns.addIncome(pTrans, myNatIns);
                } else {
                    myCatBucket.addIncome(myNatIns);
                }
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXFREE, myNatIns);
            }

            /* Adjust for DeemedBenefit */
            final TethysMoney myBenefit = pTrans.getDeemedBenefit();
            if (myBenefit != null) {
                theDeemedBenefit.addIncome(pTrans, myBenefit);
                theWithheld.addExpense(pTrans, myBenefit);
                theTaxBasis.adjustGrossValue(pTrans, TaxBasisClass.VIRTUAL, myBenefit);
            }

            /* Adjust for Withheld */
            final TethysMoney myWithheld = pTrans.getWithheld();
            if (myWithheld != null) {
                theWithheld.addExpense(pTrans, myWithheld);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myWithheld);
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
        protected void adjustStandardGain(final TransactionHelper pTrans,
                                          final SecurityHolding pSource,
                                          final TethysMoney pGains) {
            /* Access security and portfolio */
            final Security mySecurity = pSource.getSecurity();
            final Portfolio myPortfolio = pSource.getPortfolio();

            final boolean bTaxFreeGains = myPortfolio.isTaxFree()
                                          || !mySecurity.getSecurityTypeClass().isCapitalGains();
            final TransactionCategoryBucket myCategory = bTaxFreeGains
                                                                       ? theTaxFreeGains
                                                                       : theCapitalGains;
            final TaxBasisClass myTaxBasis = bTaxFreeGains
                                                           ? TaxBasisClass.TAXFREE
                                                           : TaxBasisClass.CAPITALGAINS;

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
        protected void adjustChargeableGain(final TransactionHelper pTrans,
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
                theTaxBasis.adjustGrossValue(pTrans, TaxBasisClass.TAXPAID, myTaxCredit);
                myTaxCredit = new TethysMoney(myTaxCredit);
                myTaxCredit.negate();
                theTaxBasis.adjustGrossValue(pTrans, TaxBasisClass.VIRTUAL, myTaxCredit);
            }

            /* Adjust tax basis */
            theTaxBasis.adjustValue(pTrans, TaxBasisClass.CHARGEABLEGAINS, myGains);
        }

        /**
         * Produce totals for the TransactionCategories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets */
            final MetisListIndexed<TransactionCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<TransactionCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TransactionCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final TransactionCategory myCategory = myCurr.getTransactionCategory();
                final TransactionCategory myParent = myCategory.getParentCategory();

                /* Access parent bucket */
                TransactionCategoryBucket myTotal = findItemById(myParent.getId());

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new TransactionCategoryBucket(theAnalysis, myParent);
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
                final TransactionCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                theList.add(myCurr);
            }

            /* Sort the list */
            Collections.sort(theList.getUnderlyingList(), (l, r) -> l.getTransactionCategory().compareTo(r.getTransactionCategory()));

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
