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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate.AccountRateList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

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
     * Is the bucket idle.
     */
    private final Boolean isIdle;

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
     * Is this bucket idle.
     * @return true/false
     */
    public Boolean isIdle() {
        return isIdle;
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
    protected AccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    protected AccountValues getBaseValues() {
        return theBaseValues;
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
                : JDataFieldValue.SkipField;
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

        /* Create the value maps */
        theValues = new AccountValues();
        theBaseValues = new AccountValues();
        isIdle = false;

        /* Create the history map */
        theHistory = new BucketHistory<AccountValues>();
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

        /* Reference the underlying history */
        theHistory = pBase.getHistoryMap();

        /* Copy base values from source */
        theBaseValues = pBase.getBaseValues().getSnapShot();

        /* Obtain values for date */
        AccountValues myValues = theHistory.getValuesForDate(pDate);

        /* Determine values */
        isIdle = (myValues == null);
        theValues = (isIdle)
                ? theBaseValues.getSnapShot()
                : myValues;
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

        /* Reference the underlying history */
        theHistory = pBase.getHistoryMap();

        /* Obtain values for range */
        AccountValues[] myArray = theHistory.getValuesForRange(pRange);

        /* If no activity took place up to this date */
        if (myArray == null) {
            /* Use base values and note idleness */
            theValues = pBase.getBaseValues().getSnapShot();
            theBaseValues = theValues.getSnapShot();
            isIdle = true;

            /* else we have values */
        } else {
            /* Determine base values */
            AccountValues myFirst = myArray[0];
            theBaseValues = (myFirst == null)
                    ? pBase.getBaseValues().getSnapShot()
                    : myFirst;

            /* Determine values */
            AccountValues myValues = myArray[1];
            isIdle = (myValues == null);
            theValues = (isIdle)
                    ? theBaseValues.getSnapShot()
                    : myValues;
        }

        /* If this is a creditCard */
        if (isCreditCard) {
            /* Adjust to base values */
            adjustToBaseValues();
        }
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
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.Valuation);
        JMoney myBaseValue = theBaseValues.getMoneyValue(AccountAttribute.Valuation);
        myValue.addAmount(pBalance);
        myBaseValue.addAmount(pBalance);
    }

    /**
     * Obtain new Valuation value.
     * @return the new valuation value
     */
    private JMoney getNewValuation() {
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.Valuation);
        return new JMoney(myValue);
    }

    /**
     * Obtain new Spend value.
     * @return the new spend value
     */
    private JMoney getNewSpend() {
        JMoney mySpend = theValues.getMoneyValue(AccountAttribute.Spend);
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
            setValue(AccountAttribute.Valuation, myValuation);

            /* If this is a credit card */
            if (isCreditCard) {
                /* Adjust spend */
                JMoney mySpend = getNewSpend();
                mySpend.addAmount(myAmount);
                setValue(AccountAttribute.Spend, mySpend);
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
            setValue(AccountAttribute.Valuation, myValuation);
        }

        /* Register the event in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust to Base values.
     */
    private void adjustToBaseValues() {
        /* Adjust spend values */
        JMoney myValue = getNewSpend();
        JMoney myBaseValue = theBaseValues.getMoneyValue(AccountAttribute.Spend);
        myValue.subtractAmount(myBaseValue);
        theBaseValues.put(AccountAttribute.Spend, null);
    }

    /**
     * record the rate of the account at a given date.
     * @param pDate the date of valuation
     */
    private void recordRate(final JDateDay pDate) {
        /* Obtain the appropriate rate record */
        AccountRateList myRates = theData.getRates();
        AccountRate myRate = myRates.getLatestRate(theAccount, pDate);
        JDateDay myDate = theAccount.getMaturity();

        /* If we have a rate */
        if (myRate != null) {
            /* Use Rate date instead */
            if (myDate == null) {
                myDate = myRate.getDate();
            }

            /* Store the rate */
            setValue(AccountAttribute.Rate, myRate.getRate());
        }

        /* Store the maturity */
        setValue(AccountAttribute.Maturity, myDate);
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.Valuation);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        JMoney myBase = theBaseValues.getMoneyValue(AccountAttribute.Valuation);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(AccountAttribute.Delta, myValue);
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
            put(AccountAttribute.Valuation, new JMoney());
            put(AccountAttribute.Spend, new JMoney());
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
        protected AccountValues[] getSnapShotArray() {
            /* Allocate the array and return it */
            return new AccountValues[2];
        }

        /**
         * Are the values active?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myValuation = getMoneyValue(AccountAttribute.Valuation);
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
            return JDataFieldValue.UnknownField;
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
                    /* Calculate the delta and add to the list */
                    myBucket.calculateDelta();
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

    /**
     * AccountAttribute enumeration.
     */
    public enum AccountAttribute {
        /**
         * Valuation.
         */
        Valuation,

        /**
         * Rate.
         */
        Rate,

        /**
         * Valuation Delta.
         */
        Delta,

        /**
         * Maturity.
         */
        Maturity,

        /**
         * Spend.
         */
        Spend;
    }
}