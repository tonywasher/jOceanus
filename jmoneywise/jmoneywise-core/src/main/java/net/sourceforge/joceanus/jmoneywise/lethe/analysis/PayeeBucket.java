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

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.eos.list.MetisEosListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The Payee Bucket class.
 */
public final class PayeeBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<PayeeBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(PayeeBucket.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, PayeeBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.PAYEE, PayeeBucket::getPayee);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES, PayeeBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY, PayeeBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(PayeeAttribute.class, PayeeBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisFieldId NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The payee.
     */
    private final Payee thePayee;

    /**
     * The dataSet.
     */
    private final MoneyWiseData theData;

    /**
     * Values.
     */
    private final PayeeValues theValues;

    /**
     * The base values.
     */
    private final PayeeValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<PayeeValues, PayeeAttribute> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPayee the payee
     */
    private PayeeBucket(final Analysis pAnalysis,
                        final Payee pPayee) {
        /* Store the details */
        thePayee = pPayee;
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Create the history map */
        final AssetCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                                                      ? AccountBucket.DEFAULT_CURRENCY
                                                      : myDefault.getCurrency();
        final PayeeValues myValues = new PayeeValues(myCurrency);
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
    private PayeeBucket(final Analysis pAnalysis,
                        final PayeeBucket pBase,
                        final TethysDate pDate) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

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
    private PayeeBucket(final Analysis pAnalysis,
                        final PayeeBucket pBase,
                        final TethysDateRange pRange) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<PayeeBucket> getDataFieldSet() {
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
        return thePayee == null
                                ? NAME_TOTALS.getId()
                                : thePayee.getName();
    }

    /**
     * Obtain the payee.
     * @return the payee account
     */
    public Payee getPayee() {
        return thePayee;
    }

    @Override
    public Integer getIndexedId() {
        return thePayee.getId();
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    protected MoneyWiseData getDataSet() {
        return theData;
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
     * Obtain the value map.
     * @return the value map
     */
    public PayeeValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public PayeeValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public PayeeValues getValuesForTransaction(final Transaction pTrans) {
        /* Obtain values for transaction */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public PayeeValues getPreviousValuesForTransaction(final Transaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final Transaction pTrans,
                                                final PayeeAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<PayeeValues, PayeeAttribute> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final PayeeAttribute pAttr,
                            final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final PayeeAttribute pAttr) {
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
    private Object getValue(final PayeeAttribute pAttr) {
        /* Obtain the value */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final PayeeAttribute pAttr,
                                 final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Adjust account for debit.
     * @param pTrans the transaction helper
     */
    protected void adjustForDebit(final TransactionHelper pTrans) {
        /* Access the class */
        final TransactionCategoryClass myClass = pTrans.getCategoryClass();
        final boolean isIncome = myClass.isIncome();
        TethysMoney myIncome = null;
        TethysMoney myExpense = null;

        /* Access amount */
        final TethysMoney myAmount = pTrans.getLocalAmount();
        if (myAmount.isNonZero()) {
            /* If this is an income */
            if (isIncome) {
                /* Allocate the income */
                myIncome = new TethysMoney(myAmount);

                /* else this is a refunded expense */
            } else {
                /* Update the expense */
                myExpense = new TethysMoney(myAmount);
                myExpense.negate();
            }
        }

        /* If there is a non-zero TaxCredit */
        final TethysMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred != null
            && myTaxCred.isNonZero()) {
            /* Adjust for Tax Credit */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new TethysMoney(myTaxCred);
                } else {
                    myIncome.addAmount(myTaxCred);
                }
            } else {
                if (myExpense == null) {
                    myExpense = new TethysMoney(myTaxCred);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myTaxCred);
                }
            }
        }

        /* If there is Employee National Insurance */
        TethysMoney myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null
            && myNatIns.isNonZero()) {
            /* Adjust for National Insurance */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new TethysMoney(myNatIns);
                } else {
                    myIncome.addAmount(myNatIns);
                }
            } else {
                if (myExpense == null) {
                    myExpense = new TethysMoney(myNatIns);
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
                    myIncome = new TethysMoney(myNatIns);
                } else {
                    myIncome.addAmount(myNatIns);
                }
            } else {
                if (myExpense == null) {
                    myExpense = new TethysMoney(myNatIns);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myNatIns);
                }
            }
        }

        /* If there is Withheld */
        final TethysMoney myWithheld = pTrans.getWithheld();
        if (myWithheld != null
            && myWithheld.isNonZero()) {
            /* Adjust for Charity Donation */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new TethysMoney(myWithheld);
                } else {
                    myIncome.addAmount(myWithheld);
                }
                myExpense = new TethysMoney(myWithheld);
            } else {
                myIncome = new TethysMoney(myWithheld);
                myIncome.negate();
                if (myExpense == null) {
                    myExpense = new TethysMoney(myAmount);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myWithheld);
                }
            }
        }

        /* Set new values */
        if (myIncome != null) {
            adjustCounter(PayeeAttribute.INCOME, myIncome);
        }
        if (myExpense != null) {
            adjustCounter(PayeeAttribute.EXPENSE, myExpense);
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Adjust account for credit.
     * @param pTrans the transaction helper
     */
    protected void adjustForCredit(final TransactionHelper pTrans) {
        /* Analyse the transaction */
        final TransactionCategoryClass myClass = pTrans.getCategoryClass();
        final boolean isExpense = !myClass.isIncome();
        TethysMoney myExpense = null;
        TethysMoney myIncome = null;

        /* Access amount */
        final TethysMoney myAmount = pTrans.getLocalAmount();
        if (myAmount.isNonZero()) {
            /* If this is an expense */
            if (isExpense) {
                /* Allocate the expense */
                myExpense = new TethysMoney(myAmount);

                /* else this is a refunded income */
            } else {
                /* Update the income */
                myIncome = new TethysMoney(myAmount);
                myIncome.negate();
            }
        }

        /* If there is a non-zero TaxCredit */
        final TethysMoney myTaxCred = pTrans.getTaxCredit();
        if ((myTaxCred != null) && (myTaxCred.isNonZero())) {
            /* Adjust for Tax Credit */
            if (isExpense) {
                if (myExpense == null) {
                    myExpense = new TethysMoney(myTaxCred);
                } else {
                    myExpense.addAmount(myTaxCred);
                }
            } else {
                if (myIncome == null) {
                    myIncome = new TethysMoney(myTaxCred);
                    myIncome.negate();
                } else {
                    myIncome.subtractAmount(myTaxCred);
                }
            }
        }

        /* If we have an expense */
        if (myExpense != null) {
            /* Record it */
            adjustCounter(PayeeAttribute.EXPENSE, myExpense);
        }
        if (myIncome != null) {
            /* Record it */
            adjustCounter(PayeeAttribute.INCOME, myIncome);
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Adjust account for tax credit.
     * @param pTrans the transaction helper
     */
    protected void adjustForTaxCredit(final TransactionHelper pTrans) {
        /* Access amount */
        final TethysMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred.isNonZero()) {
            final TransactionCategoryClass myClass = pTrans.getCategoryClass();
            if (myClass.isExpense()) {
                /* Update the expense */
                adjustCounter(PayeeAttribute.EXPENSE, myTaxCred);
            } else {
                /* Update the income */
                adjustCounter(PayeeAttribute.INCOME, myTaxCred);
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Adjust account for tax payments.
     * @param pTrans the transaction causing the payments
     */
    protected void adjustForTaxPayments(final TransactionHelper pTrans) {
        /* Determine transaction type */
        final TransactionCategoryClass myClass = pTrans.getCategoryClass();
        TethysMoney myAmount = null;

        /* Adjust for Tax credit */
        final TethysMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred != null
            && myTaxCred.isNonZero()) {
            myAmount = new TethysMoney(myTaxCred);
        }

        /* If we have payments */
        if (myAmount != null) {
            /* Adjust correct bucket */
            if (myClass.isExpense()) {
                adjustCounter(PayeeAttribute.INCOME, myAmount);
            } else {
                adjustCounter(PayeeAttribute.EXPENSE, myAmount);
            }

            /* Register the transaction in the history */
            registerTransaction(pTrans);
        }
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
     * Add income value.
     * @param pValue the value to add
     */
    protected void addIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(PayeeAttribute.INCOME, pValue);
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
            setValue(PayeeAttribute.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pTrans the transaction causing the expense
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
            adjustCounter(PayeeAttribute.EXPENSE, pValue);
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

        /* Register the transaction in the history */
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
            adjustCounter(PayeeAttribute.EXPENSE, myExpense);
        }
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    protected void addValues(final PayeeBucket pSource) {
        /* Access source values */
        final PayeeValues mySource = pSource.getValues();

        /* Add income values */
        TethysMoney myValue = theValues.getMoneyValue(PayeeAttribute.INCOME);
        TethysMoney mySrcValue = mySource.getMoneyValue(PayeeAttribute.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(PayeeAttribute.EXPENSE);
        mySrcValue = mySource.getMoneyValue(PayeeAttribute.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Calculate delta.
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
     * PayeeValues class.
     */
    public static final class PayeeValues
            extends BucketValues<PayeeValues, PayeeAttribute> {
        /**
         * Constructor.
         * @param pCurrency the reporting currency
         */
        private PayeeValues(final Currency pCurrency) {
            /* Initialise class */
            super(PayeeAttribute.class);

            /* Initialise income/expense to zero */
            super.setValue(PayeeAttribute.INCOME, new TethysMoney(pCurrency));
            super.setValue(PayeeAttribute.EXPENSE, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        private PayeeValues(final PayeeValues pSource,
                            final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected PayeeValues getCounterSnapShot() {
            return new PayeeValues(this, true);
        }

        @Override
        protected PayeeValues getFullSnapShot() {
            return new PayeeValues(this, false);
        }

        @Override
        protected void adjustToBaseValues(final PayeeValues pBase) {
            /* Adjust income/expense values */
            adjustMoneyToBase(pBase, PayeeAttribute.INCOME);
            adjustMoneyToBase(pBase, PayeeAttribute.EXPENSE);
            calculateDelta();
        }

        @Override
        protected void resetBaseValues() {
            /* Create a zero value in the correct currency */
            TethysMoney myValue = getMoneyValue(PayeeAttribute.INCOME);
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Reset Income and expense values */
            super.setValue(PayeeAttribute.INCOME, myValue);
            super.setValue(PayeeAttribute.EXPENSE, new TethysMoney(myValue));
            super.setValue(PayeeAttribute.PROFIT, new TethysMoney(myValue));
        }

        /**
         * Calculate delta.
         */
        private void calculateDelta() {
            /* Obtain a copy of the value */
            TethysMoney myDelta = getMoneyValue(PayeeAttribute.INCOME);
            myDelta = new TethysMoney(myDelta);

            /* Subtract the expense value */
            final TethysMoney myExpense = getMoneyValue(PayeeAttribute.EXPENSE);
            myDelta.subtractAmount(myExpense);

            /* Set the delta */
            super.setValue(PayeeAttribute.PROFIT, myDelta);
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            final TethysMoney myIncome = getMoneyValue(PayeeAttribute.INCOME);
            final TethysMoney myExpense = getMoneyValue(PayeeAttribute.EXPENSE);
            return (myIncome.isNonZero()) || (myExpense.isNonZero());
        }
    }

    /**
     * PayeeBucket list class.
     */
    public static class PayeeBucketList
            implements MetisFieldItem, MetisDataList<PayeeBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<PayeeBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(PayeeBucketList.class);

        /**
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, PayeeBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS, PayeeBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisEosListIndexed<PayeeBucket> theList;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

        /**
         * The totals.
         */
        private final PayeeBucket theTotals;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected PayeeBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
            theList = new MetisEosListIndexed<>();
            theList.setComparator((l, r) -> l.getPayee().compareTo(r.getPayee()));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected PayeeBucketList(final Analysis pAnalysis,
                                  final PayeeBucketList pBase,
                                  final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<PayeeBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final PayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final PayeeBucket myBucket = new PayeeBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    theList.addToList(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected PayeeBucketList(final Analysis pAnalysis,
                                  final PayeeBucketList pBase,
                                  final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Loop through the buckets */
            final Iterator<PayeeBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final PayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final PayeeBucket myBucket = new PayeeBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base and add to the list */
                    myBucket.adjustToBase();
                    theList.addToList(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<PayeeBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<PayeeBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public PayeeBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public PayeeBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals PayeeBucket.
         * @return the bucket
         */
        private PayeeBucket allocateTotalsBucket() {
            /* Obtain the totals payee */
            return new PayeeBucket(theAnalysis, null);
        }

        /**
         * Obtain the PayeeBucket for a given payee.
         * @param pPayee the payee
         * @return the bucket
         */
        protected PayeeBucket getBucket(final AssetBase<?> pPayee) {
            /* Handle null payee */
            if (pPayee == null) {
                return null;
            }

            /* Access as payee */
            final Payee myPayee = Payee.class.cast(pPayee);

            /* Locate the bucket in the list */
            PayeeBucket myItem = findItemById(myPayee.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new PayeeBucket(theAnalysis, myPayee);

                /* Add to the list */
                theList.addToList(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the PayeeBucket for a given payee class.
         * @param pClass the account category class
         * @return the bucket
         */
        protected PayeeBucket getBucket(final PayeeTypeClass pClass) {
            /* Determine required payee */
            final Payee myPayee = theData.getPayees().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myPayee);
        }

        /**
         * Obtain the matching PayeeBucket.
         * @param pPayee the payee
         * @return the matching bucket
         */
        public PayeeBucket getMatchingPayee(final Payee pPayee) {
            /* Return the matching payee if it exists else an orphan bucket */
            final PayeeBucket myPayee = findItemById(pPayee.getOrderedId());
            return myPayee != null
                                   ? myPayee
                                   : new PayeeBucket(theAnalysis, pPayee);
        }

        /**
         * Obtain the default PayeeBucket.
         * @return the default bucket
         */
        public PayeeBucket getDefaultPayee() {
            /* Return the first payee in the list if it exists */
            return isEmpty()
                             ? null
                             : theList.getUnderlyingList().get(0);
        }

        /**
         * Produce totals for the Payees.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            final Iterator<PayeeBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PayeeBucket myCurr = myIterator.next();

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
