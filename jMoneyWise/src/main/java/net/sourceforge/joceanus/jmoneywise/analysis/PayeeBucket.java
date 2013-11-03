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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * The Payee Bucket class.
 */
public final class PayeeBucket
        implements JDataContents, Comparable<PayeeBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PayeeBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Payee Field Id.
     */
    private static final JDataField FIELD_PAYEE = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataPayee"));

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
    private static final Map<JDataField, PayeeAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, PayeeAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The payee.
     */
    private final Account thePayee;

    /**
     * The dataSet.
     */
    private final FinanceData theData;

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
    private final BucketHistory<PayeeValues> theHistory;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
                ? null
                : thePayee.getName();
    }

    /**
     * Obtain the payee.
     * @return the payee account
     */
    public Account getPayee() {
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
    protected FinanceData getDataSet() {
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
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<PayeeValues> getHistoryMap() {
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
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static PayeeAttribute getClassForField(final JDataField pField) {
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
        return theValues.get(pAttr);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPayee the payee
     */
    private PayeeBucket(final Analysis pAnalysis,
                        final Account pPayee) {
        /* Store the details */
        thePayee = pPayee;
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Create the history map */
        theHistory = new BucketHistory<PayeeValues>(new PayeeValues());

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
                        final JDateDay pDate) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<PayeeValues>(pBase.getHistoryMap(), pDate);

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
                        final JDateDayRange pRange) {
        /* Copy details from base */
        thePayee = pBase.getPayee();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<PayeeValues>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public int compareTo(final PayeeBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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

        /* Compare the Accounts */
        PayeeBucket myThat = (PayeeBucket) pThat;
        return getPayee().equals(myThat.getPayee());
    }

    @Override
    public int hashCode() {
        return getPayee().hashCode();
    }

    /**
     * Obtain new Income value.
     * @return the new income value
     */
    private JMoney getNewIncome() {
        JMoney myIncome = theValues.getMoneyValue(PayeeAttribute.Income);
        return new JMoney(myIncome);
    }

    /**
     * Obtain new Expense value.
     * @return the new expense value
     */
    private JMoney getNewExpense() {
        JMoney myExpense = theValues.getMoneyValue(PayeeAttribute.Expense);
        return new JMoney(myExpense);
    }

    /**
     * Adjust account for debit.
     * @param pEvent the event causing the debit
     */
    protected void adjustForDebit(final Event pEvent) {
        /* Analyse the event */
        TransactionType myCatTran = TransactionType.deriveType(pEvent.getCategory());
        JMoney myIncome = null;
        JMoney myExpense = null;

        /* Access amount */
        JMoney myAmount = pEvent.getAmount();
        if (myAmount.isNonZero()) {
            /* If this is a refunded expense */
            if (myCatTran.isExpense()) {
                /* Update the expense */
                myExpense = getNewExpense();
                myExpense.subtractAmount(pEvent.getAmount());

                /* else this is an income */
            } else {
                /* Update the income */
                myIncome = getNewIncome();
                myIncome.addAmount(pEvent.getAmount());
            }
        }

        /* If there is a non-zero TaxCredit */
        JMoney myTaxCred = pEvent.getTaxCredit();
        if ((myTaxCred != null)
            && (myTaxCred.isNonZero())) {
            /* Adjust for Tax Credit */
            if (myIncome == null) {
                myIncome = getNewIncome();
            }
            myIncome.addAmount(myTaxCred);
        }

        /* If there is National Insurance */
        JMoney myNatIns = pEvent.getNatInsurance();
        if ((myNatIns != null)
            && (myNatIns.isNonZero())) {
            /* Adjust for National Insurance */
            if (myIncome == null) {
                myIncome = getNewIncome();
            }
            myIncome.addAmount(myNatIns);
        }

        /* If there is Charity Donation */
        JMoney myDonation = pEvent.getCharityDonation();
        if (myDonation != null) {
            /* Adjust for Charity Donation */
            if (myIncome == null) {
                myIncome = getNewIncome();
            }
            if (myExpense == null) {
                myExpense = getNewExpense();
            }
            myIncome.addAmount(myDonation);
            myExpense.addAmount(myDonation);
        }

        /* Set new values */
        if (myIncome != null) {
            setValue(PayeeAttribute.Income, myIncome);
        }
        if (myExpense != null) {
            setValue(PayeeAttribute.Expense, myExpense);
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust account for credit.
     * @param pEvent the event causing the credit
     */
    protected void adjustForCredit(final Event pEvent) {
        /* Access amount */
        JMoney myAmount = pEvent.getAmount();
        if (myAmount.isNonZero()) {
            /* Update the expense */
            JMoney myExpense = getNewExpense();
            myExpense.addAmount(myAmount);
            setValue(PayeeAttribute.Expense, myExpense);
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust account for credit.
     * @param pEvent the event causing the credit
     */
    protected void adjustForTaxCredit(final Event pEvent) {
        /* Access amount */
        JMoney myAmount = pEvent.getTaxCredit();
        if (myAmount.isNonZero()) {
            /* Update the expense */
            JMoney myIncome = getNewIncome();
            myIncome.addAmount(myAmount);
            setValue(PayeeAttribute.Income, myIncome);
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust account for tax payments.
     * @param pEvent the event causing the payments
     */
    protected void adjustForTaxPayments(final Event pEvent) {
        JMoney myExpense = null;

        /* Adjust for Tax credit */
        JMoney myTaxCred = pEvent.getTaxCredit();
        if ((myTaxCred != null)
            && (myTaxCred.isNonZero())) {
            myExpense = getNewExpense();
            myExpense.addAmount(myTaxCred);
        }

        /* Adjust for national insurance */
        JMoney myNatIns = pEvent.getNatInsurance();
        if (myNatIns != null) {
            if (myExpense == null) {
                myExpense = getNewExpense();
            }
            myExpense.addAmount(myNatIns);
        }

        /* Set new values */
        if (myExpense != null) {
            setValue(PayeeAttribute.Expense, myExpense);
        }

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
            setValue(PayeeAttribute.Income, myIncome);
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
            setValue(PayeeAttribute.Income, myIncome);
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
            setValue(PayeeAttribute.Expense, myExpense);
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
            setValue(PayeeAttribute.Expense, myExpense);
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
        JMoney myValue = theValues.getMoneyValue(PayeeAttribute.Income);
        JMoney mySrcValue = mySource.getMoneyValue(PayeeAttribute.Income);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(PayeeAttribute.Expense);
        mySrcValue = mySource.getMoneyValue(PayeeAttribute.Expense);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myDelta = getNewIncome();

        /* Subtract the expense value */
        JMoney myExpense = theValues.getMoneyValue(PayeeAttribute.Expense);
        myDelta.subtractAmount(myExpense);

        /* Set the delta */
        setValue(PayeeAttribute.Delta, myDelta);
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
         * SerialId.
         */
        private static final long serialVersionUID = -8908601440676932099L;

        /**
         * Constructor.
         */
        private PayeeValues() {
            /* Initialise class */
            super(PayeeAttribute.class);

            /* Initialise income/expense to zero */
            put(PayeeAttribute.Income, new JMoney());
            put(PayeeAttribute.Expense, new JMoney());
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private PayeeValues(final PayeeValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected PayeeValues getSnapShot() {
            return new PayeeValues(this);
        }

        @Override
        protected void adjustToBaseValues(final PayeeValues pBase) {
            /* Adjust income values */
            JMoney myValue = getMoneyValue(PayeeAttribute.Income);
            myValue = new JMoney(myValue);
            JMoney myBaseValue = pBase.getMoneyValue(PayeeAttribute.Income);
            myValue.subtractAmount(myBaseValue);
            put(PayeeAttribute.Income, myValue);

            /* Adjust expense values */
            myValue = getMoneyValue(PayeeAttribute.Expense);
            myValue = new JMoney(myValue);
            myBaseValue = pBase.getMoneyValue(PayeeAttribute.Expense);
            myValue.subtractAmount(myBaseValue);
            put(PayeeAttribute.Expense, myValue);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset Income and expense values */
            put(PayeeAttribute.Income, new JMoney());
            put(PayeeAttribute.Expense, new JMoney());
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myIncome = getMoneyValue(PayeeAttribute.Income);
            JMoney myExpense = getMoneyValue(PayeeAttribute.Expense);
            return ((myIncome.isNonZero()) || (myExpense.isNonZero()));
        }
    }

    /**
     * PayeeBucket list class.
     */
    public static class PayeeBucketList
            extends OrderedIdList<Integer, PayeeBucket>
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
        private final PayeeBucket theTotals;

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public PayeeBucket getTotals() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public PayeeBucketList(final Analysis pAnalysis) {
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
        public PayeeBucketList(final Analysis pAnalysis,
                               final PayeeBucketList pBase,
                               final JDateDay pDate) {
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
        public PayeeBucketList(final Analysis pAnalysis,
                               final PayeeBucketList pBase,
                               final JDateDayRange pRange) {
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

        /**
         * Allocate the Totals PayeeBucket.
         * @return the bucket
         */
        private PayeeBucket allocateTotalsBucket() {
            /* Obtain the totals payee */
            return new PayeeBucket(theAnalysis, null);
        }

        /**
         * Obtain the PayeeBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected PayeeBucket getBucket(final Account pAccount) {
            /* Handle null payee */
            if (pAccount == null) {
                return null;
            }

            /* Locate the bucket in the list */
            PayeeBucket myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new PayeeBucket(theAnalysis, pAccount);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the PayeeBucket for a given accountCategory class.
         * @param pClass the account category class
         * @return the bucket
         */
        protected PayeeBucket getBucket(final AccountCategoryClass pClass) {
            /* Determine required category */
            Account myAccount = theData.getAccounts().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myAccount);
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

    /**
     * PayeeAttribute enumeration.
     */
    public enum PayeeAttribute {
        /**
         * Income.
         */
        Income,

        /**
         * Expense.
         */
        Expense,

        /**
         * Delta.
         */
        Delta;
    }
}
