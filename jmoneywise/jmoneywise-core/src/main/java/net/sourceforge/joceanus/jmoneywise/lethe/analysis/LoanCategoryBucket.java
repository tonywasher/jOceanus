/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Loan Category Bucket.
 */
public final class LoanCategoryBucket
        extends AccountCategoryBucket<Loan, LoanCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<LoanCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.LOANCATEGORY, LoanCategoryBucket::getAccountCategory);
    }

    /**
     * The loan category.
     */
    private final LoanCategory theCategory;

    /**
     * Is the category active?
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pCurrency the currency
     * @param pCategory the account category
     */
    protected LoanCategoryBucket(final AssetCurrency pCurrency,
                                 final LoanCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<LoanCategoryBucket> getDataFieldSet() {
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
        return theCategory.getId();
    }

    @Override
    public LoanCategory getAccountCategory() {
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
     * Update active flag for Loan Bucket.
     * @param pBucket the Loan bucket
     */
    protected void updateActive(final LoanBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Loan Category Bucket.
     * @param pBucket the Loan category bucket
     */
    protected void updateActive(final LoanCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * LoanCategoryBucket list class.
     */
    public static final class LoanCategoryBucketList
            implements MetisFieldItem, MetisDataList<LoanCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<LoanCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, LoanCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS, LoanCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<LoanCategoryBucket> theList;

        /**
         * The currency.
         */
        private final AssetCurrency theCurrency;

        /**
         * The totals.
         */
        private final LoanCategoryBucket theTotals;

        /**
         * Do we have a foreign loan account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected LoanCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccountCategory().compareTo(r.getAccountCategory()));
        }

        @Override
        public MetisFieldSet<LoanCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<LoanCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public LoanCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        /**
         * Obtain the Analysis.
         * @return the analysis
         */
        public Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public LoanCategoryBucket getTotals() {
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
        private LoanCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new LoanCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the CashCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        protected LoanCategoryBucket getBucket(final LoanCategory pCategory) {
            /* Locate the bucket in the list */
            LoanCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new LoanCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse loan accounts.
         * @param pMarket the market analysis
         * @param pLoans the loan account buckets
         */
        protected void analyseLoans(final MarketAnalysis pMarket,
                                    final LoanBucketList pLoans) {
            /* Sort the loans */
            pLoans.sortBuckets();

            /* Loop through the buckets */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final Iterator<LoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final LoanBucket myCurr = myIterator.next();
                final LoanCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (myCurr.isForeignCurrency()) {
                    myCurr.calculateFluctuations(myRange);
                    pMarket.processAccount(myCurr);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final LoanCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pLoans the loan account buckets
         */
        protected void buildCategories(final LoanBucketList pLoans) {
            /* Loop through the buckets */
            final Iterator<LoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final LoanBucket myCurr = myIterator.next();
                final LoanCategory myCategory = myCurr.getCategory();
                getBucket(myCategory);

                /* Access parent category */
                getBucket(myCategory.getParentCategory());
            }
        }

        /**
         * Produce totals for the categories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets (to avoid breaking iterator on add) */
            final MetisListIndexed<LoanCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<LoanCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final LoanCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final LoanCategory myCategory = myCurr.getAccountCategory();
                final LoanCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                LoanCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new LoanCategoryBucket(theCurrency, myParent);
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
                final LoanCategoryBucket myCurr = myIterator.next();

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
