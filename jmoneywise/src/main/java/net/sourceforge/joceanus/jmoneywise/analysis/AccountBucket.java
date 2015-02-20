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
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * The Account Bucket class.
 * @param <T> the account data type
 */
public abstract class AccountBucket<T extends AssetBase<T>>
        implements JDataContents, Comparable<AccountBucket<T>>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBucket.class.getSimpleName());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Account Field Id.
     */
    private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(AnalysisResource.BUCKET_ACCOUNT.getValue());

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
    private static final Map<JDataField, AccountAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The account.
     */
    private final T theAccount;

    /**
     * The dataSet.
     */
    private final MoneyWiseData theData;

    /**
     * Values.
     */
    private final AccountValues theValues;

    /**
     * The base values.
     */
    private final AccountValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<AccountValues, AccountAttribute> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    protected AccountBucket(final Analysis pAnalysis,
                            final T pAccount) {
        /* Store the details */
        theAccount = pAccount;
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Create the history map */
        theHistory = new BucketHistory<AccountValues, AccountAttribute>(new AccountValues());

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
    protected AccountBucket(final Analysis pAnalysis,
                            final AccountBucket<T> pBase,
                            final JDateDay pDate) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<AccountValues, AccountAttribute>(pBase.getHistoryMap(), pDate);

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
    protected AccountBucket(final Analysis pAnalysis,
                            final AccountBucket<T> pBase,
                            final JDateDayRange pRange) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<AccountValues, AccountAttribute>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        AccountAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof JDecimal) {
                return ((JDecimal) myValue).isNonZero()
                                                       ? myValue
                                                       : JDataFieldValue.SKIP;
            }
            return myValue;
        }

        return JDataFieldValue.UNKNOWN;
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
        return theAccount.getName();
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public T getAccount() {
        return theAccount;
    }

    @Override
    public Integer getOrderedId() {
        return theAccount.getId();
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
    public JDateDayRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public AccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public AccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public AccountValues getValuesForTransaction(final Transaction pTrans) {
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
                                           final AccountAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<AccountValues, AccountAttribute> getHistoryMap() {
        return theHistory;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final AccountAttribute pAttr,
                            final Object pValue) {
        /* Set the value */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final AccountAttribute pAttr) {
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
    private static AccountAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final AccountAttribute pAttr) {
        /* Obtain the attribute value */
        return theValues.get(pAttr);
    }

    @Override
    public int compareTo(final AccountBucket<T> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Accounts */
        return getAccount().compareTo(pThat.getAccount());
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
        if (!(pThat instanceof AccountBucket)) {
            return false;
        }

        /* Compare the Accounts */
        AccountBucket<?> myThat = (AccountBucket<?>) pThat;
        if (!getAccount().equals(myThat.getAccount())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }

    /**
     * Obtain new Valuation value.
     * @return the new valuation value
     */
    protected JMoney getNewValuation() {
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.VALUATION);
        return new JMoney(myValue);
    }

    /**
     * Adjust account for debit.
     * @param pTrans the transaction causing the debit
     */
    protected void adjustForDebit(final Transaction pTrans) {
        /* Access event amount */
        JMoney myAmount = pTrans.getAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* Adjust valuation */
            JMoney myValuation = getNewValuation();
            myValuation.subtractAmount(myAmount);
            setValue(AccountAttribute.VALUATION, myValuation);
        }

        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * Adjust account for credit.
     * @param pTrans the transaction causing the credit
     */
    protected void adjustForCredit(final Transaction pTrans) {
        /* Access event amount */
        JMoney myAmount = pTrans.getAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* Adjust valuation */
            JMoney myValuation = getNewValuation();
            myValuation.addAmount(pTrans.getAmount());
            setValue(AccountAttribute.VALUATION, myValuation);
        }

        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction
     */
    protected void registerTransaction(final Transaction pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * Calculate delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        JMoney myBase = theBaseValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(AccountAttribute.DELTA, myValue);

        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * record the rate of the account at a given date.
     * @param pDate the date of valuation
     */
    protected void recordRate(final JDateDay pDate) {
    }

    /**
     * AccountValues class.
     */
    public static class AccountValues
            extends BucketValues<AccountValues, AccountAttribute> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = -5253865468417987410L;

        /**
         * Constructor.
         */
        protected AccountValues() {
            /* Initialise class */
            super(AccountAttribute.class);

            /* Initialise valuation and spend to zero */
            put(AccountAttribute.VALUATION, new JMoney());
            put(AccountAttribute.SPEND, new JMoney());
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private AccountValues(final AccountValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected AccountValues getSnapShot() {
            return new AccountValues(this);
        }

        @Override
        protected void adjustToBaseValues(final AccountValues pBase) {
            /* Adjust spend values */
            adjustMoneyToBase(pBase, AccountAttribute.SPEND);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset spend values */
            put(AccountAttribute.SPEND, new JMoney());
        }

        /**
         * Are the values active?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myValuation = getMoneyValue(AccountAttribute.VALUATION);
            return (myValuation != null) && (myValuation.isNonZero());
        }
    }

    /**
     * AccountBucket list class.
     * @param <B> the account bucket data type
     * @param <T> the account data type
     */
    public abstract static class AccountBucketList<B extends AccountBucket<T>, T extends AssetBase<T>>
            extends OrderedIdList<Integer, B>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBucketList.class.getSimpleName());

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The hidden base total.
         */
        private final JMoney theHiddenBaseTotal;

        /**
         * Construct a top-level List.
         * @param pClass the bucket class
         * @param pAnalysis the analysis
         */
        protected AccountBucketList(final Class<B> pClass,
                                    final Analysis pAnalysis) {
            /* Initialise class */
            super(pClass);
            theAnalysis = pAnalysis;
            theHiddenBaseTotal = new JMoney();
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the hidden base totals.
         * <p>
         * These base totals are missing because we discarded them for a dated analysis. However they are needed to ensure that totals balance.
         * @return the hidden base totals
         */
        protected JMoney getHiddenBaseTotal() {
            return theHiddenBaseTotal;
        }

        /**
         * Construct a dated List.
         * @param pBase the base list
         * @param pDate the Date
         */
        protected void constructFromBase(final AccountBucketList<B, T> pBase,
                                         final JDateDay pDate) {
            /* Loop through the buckets */
            Iterator<B> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();

                /* Access the bucket for this date */
                B myBucket = newBucket(myCurr, pDate);

                /* If the bucket is active */
                if (myBucket.isActive()) {
                    /* Record the rate (if required) and add to list */
                    myBucket.recordRate(pDate);
                    append(myBucket);

                    /* Else for inactive buckets */
                } else {
                    /* Record any base value (since we are discarding it) */
                    AccountValues myBaseValues = myBucket.getBaseValues();
                    JMoney myValue = myBaseValues.getMoneyValue(AccountAttribute.VALUATION);
                    theHiddenBaseTotal.addAmount(myValue);
                }
            }
        }

        /**
         * Construct a dated bucket.
         * @param pBase the base bucket
         * @param pDate the Date
         * @return the new bucket
         */
        protected abstract B newBucket(final B pBase,
                                       final JDateDay pDate);

        /**
         * Construct a ranged List.
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected void constructFromBase(final AccountBucketList<B, T> pBase,
                                         final JDateDayRange pRange) {
            /* Loop through the buckets */
            Iterator<B> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();

                /* Access the bucket for this range */
                B myBucket = newBucket(myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive() || !myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
                }
            }
        }

        /**
         * Construct a ranged bucket.
         * @param pBase the base bucket
         * @param pRange the Range
         * @return the new bucket
         */
        protected abstract B newBucket(final B pBase,
                                       final JDateDayRange pRange);

        /**
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected B getBucket(final T pAccount) {
            /* Locate the bucket in the list */
            B myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = newBucket(pAccount);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Construct a standard bucket.
         * @param pAccount the Account
         * @return the new bucket
         */
        protected abstract B newBucket(final T pAccount);

        /**
         * Mark active accounts.
         * @throws JOceanusException on error
         */
        protected void markActiveAccounts() throws JOceanusException {
            /* Loop through the buckets */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myCurr = myIterator.next();
                T myAccount = myCurr.getAccount();

                /* If we are active */
                if (myCurr.isActive()) {
                    /* Set the account as relevant */
                    myAccount.setRelevant();
                }

                /* If we are closed */
                if (myAccount.isClosed()) {
                    /* Ensure that we have correct closed/maturity dates */
                    myAccount.adjustClosed();

                    /* If we are Relevant */
                    if (myAccount.isRelevant() && theAnalysis.getData().checkClosedAccounts()) {
                        /* throw exception */
                        throw new JMoneyWiseDataException(myCurr, "Illegally closed account");
                    }
                }
            }
        }
    }
}
