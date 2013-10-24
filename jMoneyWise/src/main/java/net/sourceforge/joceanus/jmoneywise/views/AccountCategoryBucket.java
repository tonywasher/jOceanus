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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.views.AccountBucket.AccountAttribute;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * Account Category Bucket.
 */
public final class AccountCategoryBucket
        implements JDataContents, Comparable<AccountCategoryBucket>, OrderedIdItem<Integer> {

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountCategoryBucket.class.getSimpleName());

    /**
     * Account Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(AccountCategory.class.getSimpleName());

    /**
     * Category Type Field Id.
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
    private final AccountCategoryBucket theBase;

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
        return theCategory.getName();
    }

    @Override
    public Integer getOrderedId() {
        return theCategory.getId();
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
    public AccountCategoryBucket getBase() {
        return theBase;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    private FinanceData getDataSet() {
        return theData;
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
                                final JMoney pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    private void setAttribute(final AccountAttribute pAttr,
                              final Integer pValue) {
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
    private <X extends JDecimal> X getAttribute(final AccountAttribute pAttr,
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
     * Obtain an attribute value from the base.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getBaseAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return (theBase == null)
                ? null
                : theBase.getAttribute(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getBaseMoneyAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return JMoney.class.cast(getBaseAttribute(pAttr));
    }

    /**
     * Obtain an integer attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public Integer getIntegerAttribute(final AccountAttribute pAttr) {
        /* Obtain the attribute */
        return Integer.class.cast(getAttribute(pAttr));
    }

    /**
     * Constructor.
     * @param pData the dataSet
     * @param pCategory the account category
     */
    private AccountCategoryBucket(final FinanceData pData,
                                  final AccountCategory pCategory) {
        /* Store the category */
        theCategory = pCategory;
        theType = determineCategoryType(theCategory);
        theData = pData;
        theBase = null;

        /* Create the attribute map */
        theAttributes = new EnumMap<AccountAttribute, Object>(AccountAttribute.class);

        /* Create all possible values */
        setAttribute(AccountAttribute.Income, new JMoney());
        setAttribute(AccountAttribute.Expense, new JMoney());
        setAttribute(AccountAttribute.Valuation, new JMoney());
        setAttribute(AccountAttribute.MarketValue, new JMoney());
        setAttribute(AccountAttribute.Spend, new JMoney());
        setAttribute(AccountAttribute.Cost, new JMoney());
        setAttribute(AccountAttribute.Gained, new JMoney());
        setAttribute(AccountAttribute.Profit, new JMoney());
        setAttribute(AccountAttribute.ValueDelta, new JMoney());
        setAttribute(AccountAttribute.IncomeDelta, new JMoney());
        setAttribute(AccountAttribute.Children, Integer.valueOf(0));
    }

    /**
     * Constructor.
     * @param pBase the underlying bucket
     */
    private AccountCategoryBucket(final AccountCategoryBucket pBase) {
        /* Copy details from base */
        theCategory = pBase.getAccountCategory();
        theType = pBase.getCategoryType();
        theData = pBase.getDataSet();
        theBase = pBase;

        /* Create a new attribute map */
        theAttributes = new EnumMap<AccountAttribute, Object>(AccountAttribute.class);

        /* Clone the underlying map */
        cloneMap(theBase.getAttributes(), theAttributes);
    }

    @Override
    public int compareTo(final AccountCategoryBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the AccountCategories */
        return getAccountCategory().compareTo(pThat.getAccountCategory());
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
        if (!(pThat instanceof AccountCategoryBucket)) {
            return false;
        }

        /* Compare the Account Categories */
        AccountCategoryBucket myThat = (AccountCategoryBucket) pThat;
        return getAccountCategory().equals(myThat.getAccountCategory());
    }

    @Override
    public int hashCode() {
        return getAccountCategory().hashCode();
    }

    /**
     * Determine category type.
     * @param pCategory the category
     * @return the category type
     */
    protected static CategoryType determineCategoryType(final AccountCategory pCategory) {
        /* Access class */
        AccountCategoryClass myClass = pCategory.getCategoryTypeClass();

        /* If we have do not have value */
        if (myClass.isNonAsset()) {
            /* If this is a credit card account */
            if (myClass == AccountCategoryClass.Totals) {
                return CategoryType.Total;
            }

            /* This is a payee */
            return (myClass.isSubTotal())
                    ? CategoryType.SubTotal
                    : CategoryType.Payee;
        }

        /* If this is a credit card account */
        if (myClass == AccountCategoryClass.CreditCard) {
            return CategoryType.CreditCard;
        }

        /* Return asset or money */
        if (myClass.hasUnits()) {
            return CategoryType.Priced;
        }

        return CategoryType.Money;
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
                case Cost:
                case Gained:
                case Profit:
                    pTarget.put(myAttr, new JMoney(JMoney.class.cast(myObject)));
                    break;
                case Valuation:
                case MarketValue:
                case Income:
                case Expense:
                case Spend:
                case IncomeDelta:
                case ValueDelta:
                    pTarget.put(myAttr, new JMoney());
                    break;
                case Children:
                    pTarget.put(myAttr, Integer.valueOf(0));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final AccountCategoryBucket pBucket) {
        /* Add underlying attributes */
        addValues(pBucket.getAttributes());
    }

    /**
     * Add bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final AccountBucket pBucket) {
        /* Add underlying attributes */
        addValues(pBucket.getAttributes());

        /* Increment child count */
        Integer myCount = getIntegerAttribute(AccountAttribute.Children);
        if ((pBucket.getCategoryType() != CategoryType.Priced)
            || (pBucket.isRelevant())) {
            setAttribute(AccountAttribute.Children, myCount + 1);
        }
    }

    /**
     * Add bucket to totals.
     * @param pAttributes the underlying attributes
     */
    private void addValues(final Map<AccountAttribute, Object> pAttributes) {
        /* For each entry in the source map */
        for (Map.Entry<AccountAttribute, Object> myEntry : pAttributes.entrySet()) {
            /* Access key and object */
            AccountAttribute myAttr = myEntry.getKey();
            Object myObject = myEntry.getValue();

            /* Switch on the Attribute */
            switch (myAttr) {
                case Valuation:
                case MarketValue:
                case Cost:
                case Gained:
                case Income:
                case Expense:
                case Spend:
                case Profit:
                case ValueDelta:
                case IncomeDelta:
                    JMoney myMoney = getMoneyAttribute(myAttr);
                    myMoney.addAmount(JMoney.class.cast(myObject));
                    break;
                case Gains:
                case Invested:
                case Dividend:
                case Units:
                case Rate:
                case Maturity:
                case Price:
                default:
                    break;
            }
        }
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        /* For each entry in the source map */
        for (Map.Entry<AccountAttribute, Object> myEntry : theAttributes.entrySet()) {
            /* Access the value */
            Object myValue = myEntry.getValue();

            /* Active if we have a non-zero decimal */
            if (myValue instanceof JDecimal) {
                JDecimal myDecimal = (JDecimal) myValue;
                if (myDecimal.isNonZero()) {
                    return true;
                }
            }
        }

        /* Inactive */
        return false;
    }

    /**
     * Is the bucket relevant? That is to say is either this bucket or it's base active?
     * @return true/false
     */
    protected boolean isRelevant() {
        /* Relevant if this value is non-zero or if this is the totals */
        if (isActive()
            || (theType == CategoryType.Total)) {
            return true;
        }

        /* Relevant if the previous value is non-zero */
        return (theBase != null)
               && (theBase.isActive());
    }

    /**
     * AccountCategoryBucket list class.
     */
    public static class AccountCategoryBucketList
            extends OrderedIdList<Integer, AccountCategoryBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountCategoryBucketList.class.getSimpleName());

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
         * The data.
         */
        private final FinanceData theData;

        /**
         * The totals.
         */
        private final AccountCategoryBucket theTotals;

        /**
         * Obtain the Totals AccountCategoryBucket.
         * @return the bucket
         */
        public AccountCategoryBucket getTotalsBucket() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public AccountCategoryBucketList(final Analysis pAnalysis) {
            super(AccountCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
        }

        /**
         * Construct a secondary List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        public AccountCategoryBucketList(final Analysis pAnalysis,
                                         final AccountCategoryBucketList pBase) {
            super(AccountCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();

            /* Access the iterator */
            Iterator<AccountCategoryBucket> myIterator = pBase.listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                AccountCategoryBucket myCurr = myIterator.next();

                /* If the bucket is active */
                if (myCurr.isActive()) {
                    /* Add a derived bucket to the list */
                    AccountCategoryBucket myBucket = new AccountCategoryBucket(myCurr);
                    add(myBucket);
                }
            }

            /* Access the totals bucket */
            theTotals = allocateTotalsBucket();
        }

        /**
         * Obtain the AccountCategoryBucket for a given account category.
         * @param pCategory the account category
         * @return the bucket
         */
        protected AccountCategoryBucket getBucket(final AccountCategory pCategory) {
            /* Locate the bucket in the list */
            AccountCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new AccountCategoryBucket(theData, pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Allocate the Totals AccountCategoryBucket.
         * @return the bucket
         */
        private AccountCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            AccountCategory myTotals = theData.getAccountCategories().getSingularClass(AccountCategoryClass.Totals);
            return getBucket(myTotals);
        }

        /**
         * Produce totals for the categories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets */
            OrderedIdList<Integer, AccountCategoryBucket> myTotals = new OrderedIdList<Integer, AccountCategoryBucket>(AccountCategoryBucket.class);

            /* Access the iterator */
            Iterator<AccountCategoryBucket> myIterator = listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                AccountCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                AccountCategory myCategory = myCurr.getAccountCategory();
                AccountCategory myParent = myCategory.getParentCategory();

                /* If we have a parent category */
                if (myParent != null) {
                    /* Access parent bucket */
                    AccountCategoryBucket myTotal = findItemById(myParent.getId());

                    /* If the bucket does not exist */
                    if (myTotal == null) {
                        /* Look for bucket in the new list */
                        myTotal = myTotals.findItemById(myParent.getId());

                        /* If the bucket is completely new */
                        if (myTotal == null) {
                            /* Create the new bucket and add to new list */
                            myTotal = new AccountCategoryBucket(theData, myParent);
                            myTotals.add(myTotal);
                        }
                    }

                    /* Add the bucket to the totals */
                    myTotal.addValues(myCurr);
                }

                /* Remove the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    myIterator.remove();
                }
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                AccountCategoryBucket myCurr = myIterator.next();

                /* Ignore the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    continue;
                }

                /* Obtain category and parent category */
                AccountCategory myCategory = myCurr.getAccountCategory();
                AccountCategory myParent = myCategory.getParentCategory();

                /* If we have a parent category */
                if (myParent != null) {
                    /* Add the bucket to the totals */
                    findItemById(myParent.getId()).addValues(myCurr);
                }

                /* Add it to the list */
                add(myCurr);
            }
        }
    }

    /**
     * Category type.
     */
    public enum CategoryType {
        /**
         * Money.
         */
        Money,

        /**
         * CreditCard.
         */
        CreditCard,

        /**
         * Priced.
         */
        Priced,

        /**
         * Payee.
         */
        Payee,

        /**
         * SubTotal.
         */
        SubTotal,

        /**
         * Total.
         */
        Total;

        /**
         * Does this account type have balances?
         * @return true/false
         */
        public boolean hasBalances() {
            switch (this) {
                case Money:
                case CreditCard:
                case Priced:
                    return true;
                default:
                    return false;
            }
        }
    }
}
