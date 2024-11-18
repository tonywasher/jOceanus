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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Loan Category Bucket.
 */
public final class MoneyWiseXAnalysisLoanCategoryBucket
        extends MoneyWiseXAnalysisAccountCategoryBucket<MoneyWiseLoan, MoneyWiseLoanCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisLoanCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisLoanCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseXAnalysisLoanCategoryBucket::getAccountCategory);
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
    MoneyWiseXAnalysisLoanCategoryBucket(final MoneyWiseCurrency pCurrency,
                                         final MoneyWiseLoanCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisLoanCategoryBucket> getDataFieldSet() {
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
    void updateActive(final MoneyWiseXAnalysisLoanBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Loan Category Bucket.
     * @param pBucket the Loan category bucket
     */
    void updateActive(final MoneyWiseXAnalysisLoanCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * LoanCategoryBucket list class.
     */
    public static final class MoneyWiseXAnalysisLoanCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisLoanCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisLoanCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisLoanCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisLoanCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisLoanCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisLoanCategoryBucket> theList;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The totals.
         */
        private final MoneyWiseXAnalysisLoanCategoryBucket theTotals;

        /**
         * Do we have a foreign loan account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisLoanCategoryBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccountCategory().compareTo(r.getAccountCategory()));
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisLoanCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisLoanCategoryBucket> getUnderlyingList() {
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
        public MoneyWiseXAnalysisLoanCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
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
        public MoneyWiseXAnalysisLoanCategoryBucket getTotals() {
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
        private MoneyWiseXAnalysisLoanCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseXAnalysisLoanCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the CashCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        MoneyWiseXAnalysisLoanCategoryBucket getBucket(final MoneyWiseLoanCategory pCategory) {
            /* Locate the bucket in the list */
            MoneyWiseXAnalysisLoanCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisLoanCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse loan accounts.
         * @param pLoans the loan account buckets
         */
        void analyseLoans(final MoneyWiseXAnalysisLoanBucketList pLoans) {
            /* Sort the loans */
            pLoans.sortBuckets();

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseXAnalysisLoanBucket myCurr = myIterator.next();
                final MoneyWiseLoanCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCurr.isForeignCurrency())) {
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final MoneyWiseXAnalysisLoanCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pLoans the loan account buckets
         */
        public void buildCategories(final MoneyWiseXAnalysisLoanBucketList pLoans) {
            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseXAnalysisLoanBucket myCurr = myIterator.next();
                final MoneyWiseLoanCategory myCategory = myCurr.getCategory();
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
            final MetisListIndexed<MoneyWiseXAnalysisLoanCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseXAnalysisLoanCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisLoanCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseLoanCategory myCategory = myCurr.getAccountCategory();
                final MoneyWiseLoanCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                MoneyWiseXAnalysisLoanCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseXAnalysisLoanCategoryBucket(theCurrency, myParent);
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
                final MoneyWiseXAnalysisLoanCategoryBucket myCurr = myIterator.next();

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
