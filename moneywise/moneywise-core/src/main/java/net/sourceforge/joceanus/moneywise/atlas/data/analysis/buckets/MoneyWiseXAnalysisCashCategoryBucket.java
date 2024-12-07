/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Iterator;
import java.util.List;

/**
 * Cash Category Bucket.
 */
public final class MoneyWiseXAnalysisCashCategoryBucket
        extends MoneyWiseXAnalysisAccountCategoryBucket<MoneyWiseCash, MoneyWiseCashCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisCashCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisCashCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseXAnalysisCashCategoryBucket::getAccountCategory);
    }

    /**
     * The cash category.
     */
    private final MoneyWiseCashCategory theCategory;

    /**
     * Is the category active?
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pCurrency the currency
     * @param pCategory the account category
     */
    MoneyWiseXAnalysisCashCategoryBucket(final MoneyWiseCurrency pCurrency,
                                         final MoneyWiseCashCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisCashCategoryBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String getName() {
        return theCategory == null
                ? NAME_TOTALS.getId()
                : theCategory.getName();
    }

    @Override
    public Integer getIndexedId() {
        return theCategory.getIndexedId();
    }

    @Override
    public MoneyWiseCashCategory getAccountCategory() {
        return theCategory;
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Update active flag for Cash Bucket.
     * @param pBucket the Cash bucket
     */
    void updateActive(final MoneyWiseXAnalysisCashBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Cash Category Bucket.
     * @param pBucket the Cash category bucket
     */
    void updateActive(final MoneyWiseXAnalysisCashCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * CashCategoryBucket list class.
     */
    public static final class MoneyWiseXAnalysisCashCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisCashCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisCashCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisCashCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisCashCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisCashCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisCashCategoryBucket> theList;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The totals.
         */
        private final MoneyWiseXAnalysisCashCategoryBucket theTotals;

        /**
         * Do we have a foreign cash account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisCashCategoryBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccountCategory().compareTo(r.getAccountCategory()));
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisCashCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisCashCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseXAnalysisCashCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Do we have a foreign currency?
         * @return true/false
         */
        public Boolean haveForeignCurrency() {
            return haveForeignCurrency;
        }

        /**
         * Obtain the Analysis.
         * @return the analysis
         */
        public MoneyWiseXAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public MoneyWiseXAnalysisCashCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals CategoryBucket.
         * @return the bucket
         */
        private MoneyWiseXAnalysisCashCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseXAnalysisCashCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the CashCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        MoneyWiseXAnalysisCashCategoryBucket getBucket(final MoneyWiseCashCategory pCategory) {
            /* Locate the bucket in the list */
            MoneyWiseXAnalysisCashCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisCashCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse cash accounts.
         * @param pCash the cash account buckets
         */
        void analyseCash(final MoneyWiseXAnalysisCashBucketList pCash) {
            /* Sort the cash */
            pCash.sortBuckets();

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseXAnalysisCashBucket myCurr = myIterator.next();
                final MoneyWiseCashCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCurr.isForeignCurrency())) {
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final MoneyWiseXAnalysisCashCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pCash the cash account buckets
         */
        public void buildCategories(final MoneyWiseXAnalysisCashBucketList pCash) {
            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseXAnalysisCashBucket myCurr = myIterator.next();
                final MoneyWiseCashCategory myCategory = myCurr.getCategory();
                getBucket(myCategory);

                /* Access parent category */
                getBucket(myCategory.getParentCategory());
            }
        }

        /**
         * Produce totals for the categories.
         */
        void produceTotals() {
            /* Create a list of new buckets (to avoid breaking iterator on add) */
            final MetisListIndexed<MoneyWiseXAnalysisCashCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseXAnalysisCashCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisCashCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseCashCategory myCategory = myCurr.getAccountCategory();
                final MoneyWiseCashCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                MoneyWiseXAnalysisCashCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseXAnalysisCashCategoryBucket(theCurrency, myParent);
                        myTotals.add(myTotal);
                    }
                }

                /* Add the bucket to the totals */
                myTotal.addValues(myCurr);
                myTotal.updateActive(myCurr);

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisCashCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                theList.add(myCurr);
            }

            /* Sort the list */
            theList.sortList();

            /* Calculate delta for the totals */
            theTotals.calculateDelta();
        }
    }
}
