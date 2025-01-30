/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Deposit Category Bucket.
 */
public final class MoneyWiseXAnalysisDepositCategoryBucket
        extends MoneyWiseXAnalysisAccountCategoryBucket<MoneyWiseDeposit, MoneyWiseDepositCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisDepositCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisDepositCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseXAnalysisDepositCategoryBucket::getAccountCategory);
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
    MoneyWiseXAnalysisDepositCategoryBucket(final MoneyWiseCurrency pCurrency,
                                            final MoneyWiseDepositCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisDepositCategoryBucket> getDataFieldSet() {
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
    void updateActive(final MoneyWiseXAnalysisDepositBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Deposit Category Bucket.
     * @param pBucket the Deposit category bucket
     */
    void updateActive(final MoneyWiseXAnalysisDepositCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * DepositCategoryBucket list class.
     */
    public static final class MoneyWiseXAnalysisDepositCategoryBucketList
            implements MetisFieldItem, MetisDataList<MoneyWiseXAnalysisDepositCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisDepositCategoryBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisDepositCategoryBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisDepositCategoryBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS, MoneyWiseXAnalysisDepositCategoryBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseXAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseXAnalysisDepositCategoryBucket> theList;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The totals.
         */
        private final MoneyWiseXAnalysisDepositCategoryBucket theTotals;

        /**
         * Do we have a foreign deposit account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisDepositCategoryBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator(Comparator.comparing(MoneyWiseXAnalysisDepositCategoryBucket::getAccountCategory));
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisDepositCategoryBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseXAnalysisDepositCategoryBucket> getUnderlyingList() {
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
        public MoneyWiseXAnalysisDepositCategoryBucket findItemById(final Integer pId) {
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
        public MoneyWiseXAnalysisDepositCategoryBucket getTotals() {
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
        private MoneyWiseXAnalysisDepositCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseXAnalysisDepositCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the DepositCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        public MoneyWiseXAnalysisDepositCategoryBucket getBucket(final MoneyWiseDepositCategory pCategory) {
            /* Locate the bucket in the list */
            MoneyWiseXAnalysisDepositCategoryBucket myItem = findItemById(pCategory.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseXAnalysisDepositCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse deposit accounts.
         * @param pDeposits the deposit account buckets
         */
        public void analyseDeposits(final MoneyWiseXAnalysisDepositBucketList pDeposits) {
            /* Sort the deposits */
            pDeposits.sortBuckets();

            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseXAnalysisDepositBucket myCurr = myIterator.next();
                final MoneyWiseDepositCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (Boolean.TRUE.equals(myCurr.isForeignCurrency())) {
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final MoneyWiseXAnalysisDepositCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pDeposits the deposit account buckets
         */
        public void buildCategories(final MoneyWiseXAnalysisDepositBucketList pDeposits) {
            /* Loop through the buckets */
            final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final MoneyWiseXAnalysisDepositBucket myCurr = myIterator.next();
                final MoneyWiseDepositCategory myCategory = myCurr.getCategory();
                getBucket(myCategory);

                /* Access parent category */
                getBucket(myCategory.getParentCategory());
            }
        }

        /**
         * Produce totals for the categories.
         */
        public void produceTotals() {
            /* Create a list of new buckets (to avoid breaking iterator on add) */
            final MetisListIndexed<MoneyWiseXAnalysisDepositCategoryBucket> myTotals = new MetisListIndexed<>();

            /* Loop through the buckets */
            Iterator<MoneyWiseXAnalysisDepositCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXAnalysisDepositCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final MoneyWiseDepositCategory myCategory = myCurr.getAccountCategory();
                final MoneyWiseDepositCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                MoneyWiseXAnalysisDepositCategoryBucket myTotal = findItemById(myParent.getIndexedId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getIndexedId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new MoneyWiseXAnalysisDepositCategoryBucket(theCurrency, myParent);
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
                final MoneyWiseXAnalysisDepositCategoryBucket myCurr = myIterator.next();

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
