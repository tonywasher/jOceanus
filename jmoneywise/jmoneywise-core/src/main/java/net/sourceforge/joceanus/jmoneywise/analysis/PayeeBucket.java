/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The Payee Bucket class.
 */
public final class PayeeBucket
        implements MetisDataContents, Comparable<PayeeBucket>, MetisOrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.PAYEE_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Payee Field Id.
     */
    private static final MetisField FIELD_PAYEE = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.PAYEE.getItemName());

    /**
     * Base Field Id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, PayeeAttribute> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, PayeeAttribute.class);

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
        AssetCurrency myDefault = theAnalysis.getCurrency();
        Currency myCurrency = myDefault == null
                                                ? AccountBucket.DEFAULT_CURRENCY
                                                : myDefault.getCurrency();
        PayeeValues myValues = new PayeeValues(myCurrency);
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
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_PAYEE.equals(pField)) {
            return thePayee;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        PayeeAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof TethysDecimal) {
                return ((TethysDecimal) myValue).isNonZero()
                                                             ? myValue
                                                             : MetisFieldValue.SKIP;
            }
            return myValue;
        }

        return MetisFieldValue.UNKNOWN;
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
        return (thePayee == null)
                                  ? NAME_TOTALS
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
    public Integer getOrderedId() {
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
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static PayeeAttribute getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
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

    @Override
    public int compareTo(final PayeeBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Payees */
        return getPayee().compareTo(pThat.getPayee());
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
        if (!(pThat instanceof PayeeBucket)) {
            return false;
        }

        /* Compare the Payees */
        PayeeBucket myThat = (PayeeBucket) pThat;
        if (!getPayee().equals(myThat.getPayee())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getPayee().hashCode();
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
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        boolean isIncome = myClass.isIncome();
        TethysMoney myIncome = null;
        TethysMoney myExpense = null;

        /* Access amount */
        TethysMoney myAmount = pTrans.getLocalAmount();
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
        TethysMoney myTaxCred = pTrans.getTaxCredit();
        if ((myTaxCred != null) && (myTaxCred.isNonZero())) {
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

        /* If there is National Insurance */
        TethysMoney myNatIns = pTrans.getNatInsurance();
        if ((myNatIns != null) && (myNatIns.isNonZero())) {
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

        /* If there is Charity Donation */
        TethysMoney myDonation = pTrans.getCharityDonation();
        if ((myDonation != null) && (myDonation.isNonZero())) {
            /* Adjust for Charity Donation */
            if (isIncome) {
                if (myIncome == null) {
                    myIncome = new TethysMoney(myDonation);
                } else {
                    myIncome.addAmount(myDonation);
                }
                myExpense = new TethysMoney(myDonation);
            } else {
                myIncome = new TethysMoney(myDonation);
                myIncome.negate();
                if (myExpense == null) {
                    myExpense = new TethysMoney(myAmount);
                    myExpense.negate();
                } else {
                    myExpense.subtractAmount(myDonation);
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
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        boolean isExpense = !myClass.isIncome();
        TethysMoney myExpense = null;
        TethysMoney myIncome = null;

        /* Access amount */
        TethysMoney myAmount = pTrans.getLocalAmount();
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
        TethysMoney myTaxCred = pTrans.getTaxCredit();
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
        TethysMoney myTaxCred = pTrans.getTaxCredit();
        if (myTaxCred.isNonZero()) {
            TransactionCategoryClass myClass = pTrans.getCategoryClass();
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
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        TethysMoney myAmount = null;

        /* Adjust for Tax credit */
        TethysMoney myTaxCred = pTrans.getTaxCredit();
        if ((myTaxCred != null) && (myTaxCred.isNonZero())) {
            myAmount = new TethysMoney(myTaxCred);
        }

        /* Adjust for national insurance */
        TethysMoney myNatIns = pTrans.getNatInsurance();
        if ((myNatIns != null) && (myNatIns.isNonZero())) {
            if (myAmount == null) {
                myAmount = new TethysMoney(myNatIns);
            } else {
                myAmount.addAmount(myNatIns);
            }
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
            TethysMoney myIncome = new TethysMoney(pValue);
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
            TethysMoney myExpense = new TethysMoney(pValue);
            myExpense.negate();
            adjustCounter(PayeeAttribute.EXPENSE, myExpense);
        }
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    private void addValues(final PayeeBucket pSource) {
        /* Access source values */
        PayeeValues mySource = pSource.getValues();

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
    private void calculateDelta() {
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
            setValue(PayeeAttribute.INCOME, new TethysMoney(pCurrency));
            setValue(PayeeAttribute.EXPENSE, new TethysMoney(pCurrency));
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
            setValue(PayeeAttribute.INCOME, myValue);
            setValue(PayeeAttribute.EXPENSE, new TethysMoney(myValue));
            setValue(PayeeAttribute.PROFIT, new TethysMoney(myValue));
        }

        /**
         * Calculate delta.
         */
        private void calculateDelta() {
            /* Obtain a copy of the value */
            TethysMoney myDelta = getMoneyValue(PayeeAttribute.INCOME);
            myDelta = new TethysMoney(myDelta);

            /* Subtract the expense value */
            TethysMoney myExpense = getMoneyValue(PayeeAttribute.EXPENSE);
            myDelta.subtractAmount(myExpense);

            /* Set the delta */
            setValue(PayeeAttribute.PROFIT, myDelta);
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            TethysMoney myIncome = getMoneyValue(PayeeAttribute.INCOME);
            TethysMoney myExpense = getMoneyValue(PayeeAttribute.EXPENSE);
            return (myIncome.isNonZero()) || (myExpense.isNonZero());
        }
    }

    /**
     * PayeeBucket list class.
     */
    public static class PayeeBucketList
            extends MetisOrderedIdList<Integer, PayeeBucket>
            implements MetisDataContents {

        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.PAYEE_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final MetisField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NAME_TOTALS);

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
        private final PayeeBucket theTotals;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected PayeeBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(PayeeBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
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
            super(PayeeBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<PayeeBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                PayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                PayeeBucket myBucket = new PayeeBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
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
            super(PayeeBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<PayeeBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                PayeeBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                PayeeBucket myBucket = new PayeeBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base and add to the list */
                    myBucket.adjustToBase();
                    append(myBucket);
                }
            }
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return MetisFieldValue.UNKNOWN;
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
            Payee myPayee = Payee.class.cast(pPayee);

            /* Locate the bucket in the list */
            PayeeBucket myItem = findItemById(myPayee.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new PayeeBucket(theAnalysis, myPayee);

                /* Add to the list */
                add(myItem);
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
            Payee myPayee = theData.getPayees().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myPayee);
        }

        /**
         * Obtain an orphan PayeeBucket for a given payee account.
         * @param pPayee the payee account
         * @return the bucket
         */
        public PayeeBucket getOrphanBucket(final Payee pPayee) {
            /* Allocate an orphan bucket */
            return new PayeeBucket(theAnalysis, pPayee);
        }

        /**
         * Produce totals for the Payees.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            Iterator<PayeeBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                PayeeBucket myCurr = myIterator.next();

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
