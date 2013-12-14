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
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * Account Category Bucket.
 */
public final class AccountCategoryBucket
        implements JDataContents, Comparable<AccountCategoryBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountCategoryBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Account Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * Category Type Field Id.
     */
    private static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataType"));

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBaseValues"));

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
     * Values.
     */
    private final AccountValues theValues;

    /**
     * The base values.
     */
    private final AccountValues theBaseValues;

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
     * Obtain the values.
     * @return the values
     */
    public AccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public AccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final AccountAttribute pAttr,
                            final JMoney pValue) {
        /* Set the value into the list */
        theValues.put(pAttr, pValue);
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
        /* Obtain the attribute */
        return theValues.get(pAttr);
    }

    /**
     * Constructor.
     * @param pCategory the account category
     */
    private AccountCategoryBucket(final AccountCategory pCategory) {
        /* Store the category */
        theCategory = pCategory;
        theType = determineCategoryType(theCategory);

        /* Create the value maps */
        theValues = new AccountValues();
        theBaseValues = new AccountValues();
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
            /* If this is a totals account */
            if (myClass == AccountCategoryClass.TOTALS) {
                return CategoryType.Total;
            }
            if (myClass == AccountCategoryClass.PORTFOLIO) {
                return CategoryType.Portfolio;
            }

            /* This is a payee */
            return (myClass.isSubTotal())
                    ? CategoryType.SubTotal
                    : CategoryType.Payee;
        }

        /* Return creditCard/Money */
        return (myClass == AccountCategoryClass.CREDITCARD)
                ? CategoryType.CreditCard
                : CategoryType.Money;
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myValue = theValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue = new JMoney(myValue);

        /* Subtract any base value */
        JMoney myBase = theBaseValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(AccountAttribute.DELTA, myValue);
    }

    /**
     * Add account category bucket to totals.
     * @param pBucket the underlying bucket
     */
    private void addValues(final AccountCategoryBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add account bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final AccountBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add portfolio bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final PortfolioBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final AccountValues pTotals,
                                  final AccountValues pSource) {
        /* Add base values */
        JMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        JMoney mySrcValue = pSource.getMoneyValue(AccountAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final AccountValues pTotals,
                                  final SecurityValues pSource) {
        /* Add base values */
        JMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        JMoney mySrcValue = pSource.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * AccountCategoryBucket list class.
     */
    public static final class AccountCategoryBucketList
            extends OrderedIdList<Integer, AccountCategoryBucket>
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
            return JDataFieldValue.UNKNOWN;
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
         * Obtain the Totals.
         * @return the totals
         */
        public AccountCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public AccountCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(AccountCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
        }

        /**
         * Allocate the Totals AccountCategoryBucket.
         * @return the bucket
         */
        private AccountCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            AccountCategory myTotals = theData.getAccountCategories().getSingularClass(AccountCategoryClass.TOTALS);
            return (myTotals == null)
                    ? null
                    : new AccountCategoryBucket(myTotals);
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
                myItem = new AccountCategoryBucket(pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse accounts.
         * @param pAccounts the account buckets
         */
        protected void analyseAccounts(final AccountBucketList pAccounts) {
            /* Loop through the buckets */
            Iterator<AccountBucket> myIterator = pAccounts.listIterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                AccountBucket myCurr = myIterator.next();
                AccountCategory myCategory = myCurr.getAccountCategory();

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                AccountCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
            }
        }

        /**
         * Analyse portfolios.
         * @param pPortfolios the portfolio buckets
         */
        protected void analysePortfolios(final PortfolioBucketList pPortfolios) {
            /* Skip if there are no portfolios */
            if (pPortfolios.isEmpty()) {
                /* Calculate delta first */
                if (theTotals != null) {
                    theTotals.calculateDelta();
                }
                return;
            }

            /* Obtain the portfolios category */
            AccountCategory myTotal = theData.getAccountCategories().getSingularClass(AccountCategoryClass.PORTFOLIO);
            AccountCategoryBucket myTotals = getBucket(myTotal);

            /* Loop through the buckets */
            Iterator<PortfolioBucket> myIterator = pPortfolios.listIterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                PortfolioBucket myCurr = myIterator.next();

                /* Calculate delta for the portfolio */
                myCurr.calculateDelta();

                /* Add values */
                myTotals.addValues(myCurr);
                theTotals.addValues(myCurr);
            }

            /* Re-calculate delta for the totals */
            myTotals.calculateDelta();
            theTotals.calculateDelta();
        }

        /**
         * Produce totals for the categories.
         * <p>
         * Note that portfolios will be added in by a later call to analysePortfolios.
         */
        protected void produceTotals() {
            /* Create a list of new buckets (to avoid breaking iterator on add) */
            OrderedIdList<Integer, AccountCategoryBucket> myTotals = new OrderedIdList<Integer, AccountCategoryBucket>(AccountCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<AccountCategoryBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                AccountCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                AccountCategory myCategory = myCurr.getAccountCategory();
                AccountCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                AccountCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.findItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new AccountCategoryBucket(myParent);
                        myTotals.add(myTotal);
                    }
                }

                /* Add the bucket to the totals */
                myTotal.addValues(myCurr);

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                AccountCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

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
         * Portfolio.
         */
        Portfolio,

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
         * Is this a subTotal category?
         * @return true/false
         */
        public boolean isSubTotal() {
            switch (this) {
                case SubTotal:
                case Portfolio:
                    return true;
                default:
                    return false;
            }
        }
    }
}
