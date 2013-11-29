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

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisMaps.AccountRateMap;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * The Account Bucket class.
 */
public final class AccountBucket
        implements JDataContents, Comparable<AccountBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Account Field Id.
     */
    private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataAccount"));

    /**
     * Account Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * IsCreditCard Field Id.
     */
    private static final JDataField FIELD_ISCREDIT = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCredit"));

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
    private static final Map<JDataField, AccountAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The account.
     */
    private final Account theAccount;

    /**
     * The account category.
     */
    private final AccountCategory theCategory;

    /**
     * Is this a creditCard.
     */
    private final Boolean isCreditCard;

    /**
     * The dataSet.
     */
    private final FinanceData theData;

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
    private final BucketHistory<AccountValues> theHistory;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_ISCREDIT.equals(pField)) {
            return isCreditCard;
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
    public Account getAccount() {
        return theAccount;
    }

    @Override
    public Integer getOrderedId() {
        return theAccount.getId();
    }

    /**
     * Obtain the account category.
     * @return the account category
     */
    public AccountCategory getAccountCategory() {
        return theCategory;
    }

    /**
     * Is this a creditCard.
     * @return true/false
     */
    public Boolean isCreditCard() {
        return isCreditCard;
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
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public AccountValues getValuesForEvent(final Event pEvent) {
        /* Obtain values for event */
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain delta values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public AccountValues getDeltaForEvent(final Event pEvent) {
        /* Obtain values for event */
        return theHistory.getDeltaForEvent(pEvent);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<AccountValues> getHistoryMap() {
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

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    private AccountBucket(final Analysis pAnalysis,
                          final Account pAccount) {
        /* Store the details */
        theAccount = pAccount;
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Obtain category */
        theCategory = theAccount.getAccountCategory();

        /* Determine whether this is a credit card */
        isCreditCard = (theCategory.getCategoryTypeClass() == AccountCategoryClass.CreditCard);

        /* Create the history map */
        theHistory = new BucketHistory<AccountValues>(new AccountValues());

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
    private AccountBucket(final Analysis pAnalysis,
                          final AccountBucket pBase,
                          final JDateDay pDate) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theCategory = pBase.getAccountCategory();
        isCreditCard = pBase.isCreditCard();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<AccountValues>(pBase.getHistoryMap(), pDate);

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
    private AccountBucket(final Analysis pAnalysis,
                          final AccountBucket pBase,
                          final JDateDayRange pRange) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theCategory = pBase.getAccountCategory();
        isCreditCard = pBase.isCreditCard();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();

        /* Access the relevant history */
        theHistory = new BucketHistory<AccountValues>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public int compareTo(final AccountBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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
        AccountBucket myThat = (AccountBucket) pThat;
        return getAccount().equals(myThat.getAccount());
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }

    /**
     * Set opening balance.
     * @param pBalance the opening balance
     */
    protected void setOpeningBalance(final JMoney pBalance) {
        JMoney myValue = getNewValuation();
        JMoney myBaseValue = theBaseValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue.addAmount(pBalance);
        myBaseValue.addAmount(pBalance);
        setValue(AccountAttribute.VALUATION, myValue);
    }

    /**
     * Obtain new Valuation value.
     * @return the new valuation value
     */
    private JMoney getNewValuation() {
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.VALUATION);
        return new JMoney(myValue);
    }

    /**
     * Obtain new Spend value.
     * @return the new spend value
     */
    private JMoney getNewSpend() {
        JMoney mySpend = theValues.getMoneyValue(AccountAttribute.SPEND);
        return new JMoney(mySpend);
    }

    /**
     * Adjust account for debit.
     * @param pEvent the event causing the debit
     */
    protected void adjustForDebit(final Event pEvent) {
        /* Access event amount */
        JMoney myAmount = pEvent.getAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* Adjust valuation */
            JMoney myValuation = getNewValuation();
            myValuation.subtractAmount(myAmount);
            setValue(AccountAttribute.VALUATION, myValuation);

            /* If this is a credit card */
            if (isCreditCard) {
                /* Adjust spend */
                JMoney mySpend = getNewSpend();
                mySpend.addAmount(myAmount);
                setValue(AccountAttribute.SPEND, mySpend);
            }
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust account for credit.
     * @param pEvent the event causing the credit
     */
    protected void adjustForCredit(final Event pEvent) {
        /* Access event amount */
        JMoney myAmount = pEvent.getAmount();

        /* If we have a non-zero amount */
        if (myAmount.isNonZero()) {
            /* Adjust valuation */
            JMoney myValuation = getNewValuation();
            myValuation.addAmount(pEvent.getAmount());
            setValue(AccountAttribute.VALUATION, myValuation);
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Register the event.
     * @param pEvent the event
     */
    protected void registerEvent(final Event pEvent) {
        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * record the rate of the account at a given date.
     * @param pDate the date of valuation
     */
    private void recordRate(final JDateDay pDate) {
        /* Obtain the appropriate rate record */
        AccountRateMap myRates = theAnalysis.getRates();
        AccountRate myRate = myRates.getRateForDate(theAccount, pDate);
        JDateDay myDate = theAccount.getMaturity();

        /* If we have a rate */
        if (myRate != null) {
            /* Use Rate date instead */
            if (myDate == null) {
                myDate = myRate.getDate();
            }

            /* Store the rate */
            setValue(AccountAttribute.RATE, myRate.getRate());
        }

        /* Store the maturity */
        setValue(AccountAttribute.MATURITY, myDate);
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
     * AccountValues class.
     */
    public static class AccountValues
            extends BucketValues<AccountValues, AccountAttribute> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = -6075471778802035084L;

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
            return (myValuation != null)
                   && (myValuation.isNonZero());
        }
    }

    /**
     * AccountBucket list class.
     */
    public static class AccountBucketList
            extends OrderedIdList<Integer, AccountBucket>
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
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public AccountBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(AccountBucket.class);
            theAnalysis = pAnalysis;
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        public AccountBucketList(final Analysis pAnalysis,
                                 final AccountBucketList pBase,
                                 final JDateDay pDate) {
            /* Initialise class */
            super(AccountBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<AccountBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                AccountBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                AccountBucket myBucket = new AccountBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is active */
                if (myBucket.isActive()) {
                    /* Record the rate and add to list */
                    myBucket.recordRate(pDate);
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
        public AccountBucketList(final Analysis pAnalysis,
                                 final AccountBucketList pBase,
                                 final JDateDayRange pRange) {
            /* Initialise class */
            super(AccountBucket.class);
            theAnalysis = pAnalysis;

            /* Loop through the buckets */
            Iterator<AccountBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                AccountBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                AccountBucket myBucket = new AccountBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle or active */
                if (myBucket.isActive()
                    || !myBucket.isIdle()) {
                    /* Add to the list */
                    append(myBucket);
                }
            }
        }

        /**
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected AccountBucket getBucket(final Account pAccount) {
            /* Locate the bucket in the list */
            AccountBucket myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new AccountBucket(theAnalysis, pAccount);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }
    }
}
