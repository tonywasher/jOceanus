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
package net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Cash Category Bucket.
 */
public final class MoneyWiseAnalysisCashCategoryBucket
        extends MoneyWiseAnalysisAccountCategoryBucket<MoneyWiseCash, MoneyWiseCashCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisCashCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisCashCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseAnalysisCashCategoryBucket::getAccountCategory);
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
    MoneyWiseAnalysisCashCategoryBucket(final MoneyWiseCurrency pCurrency,
                                        final MoneyWiseCashCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisCashCategoryBucket> getDataFieldSet() {
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
    void updateActive(final MoneyWiseAnalysisCashBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Cash Category Bucket.
     * @param pBucket the Cash category bucket
     */
    void updateActive(final MoneyWiseAnalysisCashCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * CashCategoryBucket list class.
     */
    public static final class MoneyWiseAnalysisCashCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisCashCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisCashCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisCashCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisCashCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisCashCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisCashCategoryBucket> theList;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The totals.
         */
        private final MoneyWiseAnalysisCashCategoryBucket theTotals;

        /**
         * Do we have a foreign cash account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisCashCategoryBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccountCategory().compareTo(r.getAccountCategory()));
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisCashCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisCashCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisCashCategoryBucket findItemById(final Integer pId) {
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
        public MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public MoneyWiseAnalysisCashCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals CategoryBucket.
         * @return the bucket
         */
        private MoneyWiseAnalysisCashCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseAnalysisCashCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the CashCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        MoneyWiseAnalysisCashCategoryBucket getBucket(final MoneyWiseCashCategory pCategory) {
            /* Locate the bucket in the list */
            MoneyWiseAnalysisCashCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisCashCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse cash accounts.
         * @param pMarket the market analysis
         * @param pCash the cash account buckets
         */
        void analyseCash(final MoneyWiseAnalysisMarket pMarket,
                         final MoneyWiseAnalysisCashBucketList pCash) {
            /* Sort the cash */
            pCash.sortBuckets();

            /* Loop through the buckets */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final Iterator<MoneyWiseAnalysisCashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseAnalysisCashBucket myCurr = myIterator.next();
                final MoneyWiseCashCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCurr.isForeignCurrency())) {
                    myCurr.calculateFluctuations(myRange);
                    pMarket.processAccount(myCurr);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final MoneyWiseAnalysisCashCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pCash the cash account buckets
         */
        public void buildCategories(final MoneyWiseAnalysisCashBucketList pCash) {
            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisCashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseAnalysisCashBucket myCurr = myIterator.next();
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
            final MetisListIndexed<MoneyWiseAnalysisCashCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseAnalysisCashCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisCashCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseCashCategory myCategory = myCurr.getAccountCategory();
                final MoneyWiseCashCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                MoneyWiseAnalysisCashCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseAnalysisCashCategoryBucket(theCurrency, myParent);
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
                final MoneyWiseAnalysisCashCategoryBucket myCurr = myIterator.next();

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
