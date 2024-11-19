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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.tethys.date.TethysDateRange;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Deposit Category Bucket.
 */
public final class MoneyWiseAnalysisDepositCategoryBucket
        extends MoneyWiseAnalysisAccountCategoryBucket<MoneyWiseDeposit, MoneyWiseDepositCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisDepositCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisDepositCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseAnalysisDepositCategoryBucket::getAccountCategory);
    }

    /**
     * The deposit category.
     */
    private final MoneyWiseDepositCategory theCategory;

    /**
     * Is the category active?
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pCurrency the currency
     * @param pCategory the account category
     */
    MoneyWiseAnalysisDepositCategoryBucket(final MoneyWiseCurrency pCurrency,
                                           final MoneyWiseDepositCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisDepositCategoryBucket> getDataFieldSet() {
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
    public MoneyWiseDepositCategory getAccountCategory() {
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
     * Update active flag for Deposit Bucket.
     * @param pBucket the Deposit bucket
     */
    void updateActive(final MoneyWiseAnalysisDepositBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Deposit Category Bucket.
     * @param pBucket the Deposit category bucket
     */
    void updateActive(final MoneyWiseAnalysisDepositCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * DepositCategoryBucket list class.
     */
    public static final class MoneyWiseAnalysisDepositCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseAnalysisDepositCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisDepositCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisDepositCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisDepositCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisDepositCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisDepositCategoryBucket> theList;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The totals.
         */
        private final MoneyWiseAnalysisDepositCategoryBucket theTotals;

        /**
         * Do we have a foreign deposit account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisDepositCategoryBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getAccountCategory().compareTo(r.getAccountCategory()));
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisDepositCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisDepositCategoryBucket> getUnderlyingList() {
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
        public MoneyWiseAnalysisDepositCategoryBucket findItemById(final Integer pId) {
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
        public MoneyWiseAnalysisDepositCategoryBucket getTotals() {
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
        private MoneyWiseAnalysisDepositCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseAnalysisDepositCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the DepositCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        public MoneyWiseAnalysisDepositCategoryBucket getBucket(final MoneyWiseDepositCategory pCategory) {
            /* Locate the bucket in the list */
            MoneyWiseAnalysisDepositCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisDepositCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse deposit accounts.
         * @param pMarket the market analysis
         * @param pDeposits the deposit account buckets
         */
        void analyseDeposits(final MoneyWiseAnalysisMarket pMarket,
                             final MoneyWiseAnalysisDepositBucketList pDeposits) {
            /* Sort the deposits */
            pDeposits.sortBuckets();

            /* Loop through the buckets */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final Iterator<MoneyWiseAnalysisDepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseAnalysisDepositBucket myCurr = myIterator.next();
                final MoneyWiseDepositCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCurr.isForeignCurrency())) {
                    myCurr.calculateFluctuations(myRange);
                    pMarket.processAccount(myCurr);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final MoneyWiseAnalysisDepositCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pDeposits the deposit account buckets
         */
        public void buildCategories(final MoneyWiseAnalysisDepositBucketList pDeposits) {
            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisDepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseAnalysisDepositBucket myCurr = myIterator.next();
                final MoneyWiseDepositCategory myCategory = myCurr.getCategory();
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
            final MetisListIndexed<MoneyWiseAnalysisDepositCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseAnalysisDepositCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisDepositCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseDepositCategory myCategory = myCurr.getAccountCategory();
                final MoneyWiseDepositCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                MoneyWiseAnalysisDepositCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseAnalysisDepositCategoryBucket(theCurrency, myParent);
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
                final MoneyWiseAnalysisDepositCategoryBucket myCurr = myIterator.next();

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
