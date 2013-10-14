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

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jMoneyWise.analysis.AccountCategoryBucket.CategoryType;
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
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBucket.class.getSimpleName());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(Analysis.class.getSimpleName());

    /**
     * Account Field Id.
     */
    private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(Account.class.getSimpleName());

    /**
     * Account Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(AccountCategory.class.getSimpleName());

    /**
     * Account Type Field Id.
     */
    private static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(CategoryType.class.getSimpleName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

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
     * The category type.
     */
    private final CategoryType theType;

    /**
     * The dataSet.
     */
    private final FinanceData theData;

    /**
     * The base.
     */
    private final AccountBucket theBase;

    /**
     * Attribute Map.
     */
    private final Map<AccountAttribute, Object> theAttributes;

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
        if (FIELD_TYPE.equals(pField)) {
            return theType;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase != null)
                    ? theBase
                    : JDataFieldValue.SkipField;
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
     * Obtain the category type.
     * @return the category type
     */
    public CategoryType getCategoryType() {
        return theType;
    }

    /**
     * Obtain the base.
     * @return the base
     */
    public AccountBucket getBase() {
        return theBase;
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
     * Obtain the attribute map.
     * @return the attribute map
     */
    protected Map<AccountAttribute, Object> getAttributes() {
        return theAttributes;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final AccountAttribute pAttr,
                                final Object pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final AccountAttribute pAttr) {
        /* Access value of object */
        Object myValue = getAttribute(pAttr);

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
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X> X getAttribute(final AccountAttribute pAttr,
                               final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getMoneyAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JMoney.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JRate getRateAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JRate.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JDateDay getDateAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JDateDay.class);
    }

    /**
     * Obtain an attribute value from the base.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X extends JDecimal> X getBaseAttribute(final AccountAttribute pAttr,
                                                    final Class<X> pClass) {
        /* Obtain the attribute */
        return (theBase == null)
                ? null
                : theBase.getAttribute(pAttr, pClass);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getBaseMoneyAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return getBaseAttribute(pAttr, JMoney.class);
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
        theBase = null;

        /* Obtain category, allowing for autoExpense */
        if (theAccount.getAutoExpense() != null) {
            theCategory = theData.getAccountCategories().getSingularClass(AccountCategoryClass.Payee);
        } else {
            theCategory = theAccount.getAccountCategory();
        }

        /* Determine type based on category */
        theType = AccountCategoryBucket.determineCategoryType(theCategory);

        /* Create the attribute map and savePoint */
        theAttributes = new EnumMap<AccountAttribute, Object>(AccountAttribute.class);

        /* Initialise valuation to zero */
        setAttribute(AccountAttribute.Valuation, new JMoney());

        /* Initialise spend to zero */
        setAttribute(AccountAttribute.Spend, new JMoney());
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private AccountBucket(final Analysis pAnalysis,
                          final AccountBucket pBase) {
        /* Copy details from base */
        theAccount = pBase.getAccount();
        theCategory = pBase.getAccountCategory();
        theType = pBase.getCategoryType();
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        theBase = pBase;

        /* Create a new attribute map and save point */
        theAttributes = new EnumMap<AccountAttribute, Object>(AccountAttribute.class);

        /* Clone the underlying map */
        cloneMap(theBase.getAttributes(), theAttributes);
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
    protected void setOpenBalance(final JMoney pBalance) {
        JMoney myValuation = getMoneyAttribute(AccountAttribute.Valuation);
        myValuation.addAmount(pBalance);
    }

    /**
     * Adjust account for debit.
     * @param pEvent the event causing the debit
     */
    protected void adjustForDebit(final Event pEvent) {
        JMoney myValuation = getMoneyAttribute(AccountAttribute.Valuation);
        myValuation.subtractAmount(pEvent.getAmount());
        if (theType == CategoryType.CreditCard) {
            JMoney mySpend = getMoneyAttribute(AccountAttribute.Spend);
            mySpend.addAmount(pEvent.getAmount());
        }
    }

    /**
     * Adjust account for credit.
     * @param pEvent the event causing the credit
     */
    protected void adjustForCredit(final Event pEvent) {
        JMoney myValuation = getMoneyAttribute(AccountAttribute.Valuation);
        myValuation.addAmount(pEvent.getAmount());
    }

    /**
     * Copy a map.
     * @param pSource the source map
     * @param pTarget the target map
     */
    protected void copyMap(final Map<AccountAttribute, Object> pSource,
                           final Map<AccountAttribute, Object> pTarget) {
        /* Clear the target map */
        pTarget.clear();

        /* For each entry in the source map */
        for (Map.Entry<AccountAttribute, Object> myEntry : pSource.entrySet()) {
            /* Access key and object */
            AccountAttribute myAttr = myEntry.getKey();
            Object myObject = myEntry.getValue();

            /* Create copy of object in map */
            if (myObject instanceof JMoney) {
                pTarget.put(myAttr, new JMoney((JMoney) myObject));
            } else if (myObject instanceof JRate) {
                pTarget.put(myAttr, new JRate((JRate) myObject));
            } else if (myObject instanceof JDateDay) {
                pTarget.put(myAttr, new JDateDay((JDateDay) myObject));
            }
        }
    }

    /**
     * Clone a map.
     * @param pSource the source map
     * @param pTarget the target map
     */
    private void cloneMap(final Map<AccountAttribute, Object> pSource,
                          final Map<AccountAttribute, Object> pTarget) {
        /* For each entry in the source map */
        for (Map.Entry<AccountAttribute, Object> myEntry : pSource.entrySet()) {
            /* Access key and object */
            AccountAttribute myAttr = myEntry.getKey();
            Object myObject = myEntry.getValue();

            /* Switch on the Attribute */
            switch (myAttr) {
                case Valuation:
                    pTarget.put(myAttr, new JMoney((JMoney) myObject));
                    break;
                case Spend:
                    pTarget.put(myAttr, new JMoney());
                    break;
                case Rate:
                case Maturity:
                    pTarget.put(myAttr, myObject);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * record the rate of the account at a given date.
     * @param pDate the date of valuation
     */
    protected void recordRate(final JDateDay pDate) {
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
            setAttribute(AccountAttribute.Rate, myRate.getRate());
        }

        /* Store the maturity */
        setAttribute(AccountAttribute.Maturity, myDate);
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myValue = new JMoney(getMoneyAttribute(AccountAttribute.Valuation));

        /* Subtract any base value */
        if (theBase != null) {
            myValue.subtractAmount(getBaseMoneyAttribute(AccountAttribute.Valuation));
        }

        /* Set the delta */
        setAttribute(AccountAttribute.Delta, myValue);
    }

    /**
     * analyse bucket.
     * @param pDate the date for analysis
     */
    protected void analyseBucket(final JDateDay pDate) {
        recordRate(pDate);
        calculateDelta();
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        JMoney myValuation = getMoneyAttribute(AccountAttribute.Valuation);
        return (myValuation != null)
               && (myValuation.isNonZero());
    }

    /**
     * Is the bucket relevant? That is to say is either this bucket or it's base active?
     * @return true/false
     */
    public boolean isRelevant() {
        /* Relevant if this value or the previous value is non-zero */
        if (isActive()) {
            return true;
        }
        return (theBase != null)
               && (theBase.isActive());
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
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBucketList.class.getSimpleName());

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
        public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Analysis field Id.
         */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

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
            super(AccountBucket.class);
            theAnalysis = pAnalysis;
        }

        /**
         * Construct a secondary List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        public AccountBucketList(final Analysis pAnalysis,
                                 final AccountBucketList pBase) {
            super(AccountBucket.class);
            theAnalysis = pAnalysis;

            /* Access the iterator */
            Iterator<AccountBucket> myIterator = pBase.listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                AccountBucket myCurr = myIterator.next();

                /* Ignore payees */
                if (myCurr.getCategoryType() == CategoryType.Payee) {
                    continue;
                }

                /* Add a derived bucket to the list */
                AccountBucket myBucket = new AccountBucket(pAnalysis, myCurr);
                add(myBucket);
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
        Spend,

        /**
         * Children.
         */
        Children;
    }
}
