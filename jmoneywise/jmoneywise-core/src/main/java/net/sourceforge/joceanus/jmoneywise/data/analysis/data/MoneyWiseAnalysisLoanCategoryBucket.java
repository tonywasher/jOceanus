/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.analysis.data;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Loan Category Bucket.
 */
public final class MoneyWiseAnalysisLoanCategoryBucket
        extends MoneyWiseAnalysisAccountCategoryBucket<MoneyWiseLoan, MoneyWiseLoanCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisLoanCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisLoanCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseAnalysisLoanCategoryBucket::getAccountCategory);
    }

    /**
     * The loan category.
     */
    private final MoneyWiseLoanCategory theCategory;

    /**
     * Is the category active?
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pCurrency the currency
     * @param pCategory the account category
     */
    protected MoneyWiseAnalysisLoanCategoryBucket(final MoneyWiseCurrency pCurrency,
                                                  final MoneyWiseLoanCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisLoanCategoryBucket> getDataFieldSet() {
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
    public MoneyWiseLoanCategory getAccountCategory() {
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
    protected void updateActive(final MoneyWiseAnalysisLoanBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Loan Category Bucket.
     * @param pBucket the Loan category bucket
     */
    protected void updateActive(final MoneyWiseAnalysisLoanCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * LoanCategoryBucket list class.
     */
    public static final class MoneyWiseAnalysisLoanCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisLoanCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisLoanCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisLoanCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisLoanCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisLoanCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisLoanCategoryBucket> theList;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The totals.
         */
        private final MoneyWiseAnalysisLoanCategoryBucket theTotals;

        /**
         * Do we have a foreign loan account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected MoneyWiseAnalysisLoanCategoryBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccountCategory().compareTo(r.getAccountCategory()));
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisLoanCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisLoanCategoryBucket> getUnderlyingList() {
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
        public MoneyWiseAnalysisLoanCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
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
        public MoneyWiseAnalysisLoanCategoryBucket getTotals() {
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
        private MoneyWiseAnalysisLoanCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseAnalysisLoanCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the CashCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        protected MoneyWiseAnalysisLoanCategoryBucket getBucket(final MoneyWiseLoanCategory pCategory) {
            /* Locate the bucket in the list */
            MoneyWiseAnalysisLoanCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisLoanCategoryBucket(theCurrency, pCategory);

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
        protected void analyseLoans(final MoneyWiseAnalysisMarket pMarket,
                                    final MoneyWiseAnalysisLoanBucketList pLoans) {
            /* Sort the loans */
            pLoans.sortBuckets();

            /* Loop through the buckets */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final Iterator<MoneyWiseAnalysisLoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseAnalysisLoanBucket myCurr = myIterator.next();
                final MoneyWiseLoanCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (myCurr.isForeignCurrency()) {
                    myCurr.calculateFluctuations(myRange);
                    pMarket.processAccount(myCurr);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final MoneyWiseAnalysisLoanCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pLoans the loan account buckets
         */
        public void buildCategories(final MoneyWiseAnalysisLoanBucketList pLoans) {
            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisLoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseAnalysisLoanBucket myCurr = myIterator.next();
                final MoneyWiseLoanCategory myCategory = myCurr.getCategory();
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
            final MetisListIndexed<MoneyWiseAnalysisLoanCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseAnalysisLoanCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisLoanCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseLoanCategory myCategory = myCurr.getAccountCategory();
                final MoneyWiseLoanCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                MoneyWiseAnalysisLoanCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseAnalysisLoanCategoryBucket(theCurrency, myParent);
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
                final MoneyWiseAnalysisLoanCategoryBucket myCurr = myIterator.next();

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
