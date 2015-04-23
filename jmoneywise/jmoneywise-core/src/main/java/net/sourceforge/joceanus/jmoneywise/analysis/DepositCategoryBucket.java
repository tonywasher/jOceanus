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

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;

/**
 * Deposit Category Bucket.
 */
public final class DepositCategoryBucket
        extends AccountCategoryBucket<Deposit, DepositCategory> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.DEPOSITCATEGORY_NAME.getValue(),
            AccountCategoryBucket.FIELD_DEFS);

    /**
     * Deposit Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.DEPOSITCATEGORY.getItemName());

    /**
     * The deposit category.
     */
    private final DepositCategory theCategory;

    /**
     * Constructor.
     * @param pCurrency the currency
     * @param pCategory the account category
     */
    protected DepositCategoryBucket(final AssetCurrency pCurrency,
                                    final DepositCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String getName() {
        return (theCategory == null)
                                    ? NAME_TOTALS
                                    : theCategory.getName();
    }

    @Override
    public Integer getOrderedId() {
        return theCategory.getId();
    }

    @Override
    public DepositCategory getAccountCategory() {
        return theCategory;
    }

    @Override
    public int compareTo(final AccountCategoryBucket<Deposit, DepositCategory> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the AccountCategories */
        return getAccountCategory().compareTo(pThat.getAccountCategory());
    }

    /**
     * DepositCategoryBucket list class.
     */
    public static final class DepositCategoryBucketList
            extends OrderedIdList<Integer, DepositCategoryBucket>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.DEPOSITCATEGORY_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The currency.
         */
        private final AssetCurrency theCurrency;

        /**
         * The totals.
         */
        private final DepositCategoryBucket theTotals;

        /**
         * Do we have a foreign deposit account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected DepositCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(DepositCategoryBucket.class);
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
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
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public DepositCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Do we have a foreign currency?
         * @return true/false
         */
        public Boolean haveForeignCurrency() {
            return haveForeignCurrency;
        }

        /**
         * Allocate the Totals CategoryBucket.
         * @return the bucket
         */
        private DepositCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new DepositCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the DepositCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        protected DepositCategoryBucket getBucket(final DepositCategory pCategory) {
            /* Locate the bucket in the list */
            DepositCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new DepositCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse deposit accounts.
         * @param pDeposits the deposit account buckets
         */
        protected void analyseDeposits(final DepositBucketList pDeposits) {
            /* Loop through the buckets */
            Iterator<DepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                DepositBucket myCurr = myIterator.next();
                DepositCategory myCategory = myCurr.getCategory();

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                DepositCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);

                /* Note foreign currency */
                if (myCurr.isForeignCurrency()) {
                    haveForeignCurrency = Boolean.TRUE;
                }
            }
        }

        /**
         * Build categories.
         * @param pDeposits the deposit account buckets
         */
        protected void buildCategories(final DepositBucketList pDeposits) {
            /* Loop through the buckets */
            Iterator<DepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                DepositBucket myCurr = myIterator.next();
                DepositCategory myCategory = myCurr.getCategory();
                getBucket(myCategory);
            }
        }

        /**
         * Produce totals for the categories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets (to avoid breaking iterator on add) */
            OrderedIdList<Integer, DepositCategoryBucket> myTotals = new OrderedIdList<Integer, DepositCategoryBucket>(DepositCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<DepositCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                DepositCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                DepositCategory myCategory = myCurr.getAccountCategory();
                DepositCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                DepositCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.findItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new DepositCategoryBucket(theCurrency, myParent);
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
                DepositCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                add(myCurr);
            }

            /* Calculate delta for the totals */
            theTotals.calculateDelta();
        }
    }
}
