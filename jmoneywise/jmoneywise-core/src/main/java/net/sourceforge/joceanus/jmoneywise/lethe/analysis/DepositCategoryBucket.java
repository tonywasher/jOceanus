/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Deposit Category Bucket.
 */
public final class DepositCategoryBucket
        extends AccountCategoryBucket<Deposit, DepositCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.DEPOSITCATEGORY_NAME.getValue(),
            AccountCategoryBucket.FIELD_DEFS);

    /**
     * Deposit Category Field Id.
     */
    private static final MetisField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.DEPOSITCATEGORY.getItemName());

    /**
     * The deposit category.
     */
    private final DepositCategory theCategory;

    /**
     * Is the category active?
     */
    private boolean isActive;

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
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return isActive;
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
     * Update active flag for Deposit Bucket.
     * @param pBucket the Deposit bucket
     */
    protected void updateActive(final DepositBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Deposit Category Bucket.
     * @param pBucket the Deposit category bucket
     */
    protected void updateActive(final DepositCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * DepositCategoryBucket list class.
     */
    public static final class DepositCategoryBucketList
            implements MetisDataContents, MetisDataList<DepositCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.DEPOSITCATEGORY_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final MetisField FIELD_TOTALS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisOrderedIdList<Integer, DepositCategoryBucket> theList;

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
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisOrderedIdList<>(DepositCategoryBucket.class);
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public List<DepositCategoryBucket> getUnderlyingList() {
            return theList;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public DepositCategoryBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.findItemById(pId);
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
        protected void analyseDeposits(final MarketAnalysis pMarket,
                                       final DepositBucketList pDeposits) {
            /* Loop through the buckets */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final Iterator<DepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final DepositBucket myCurr = myIterator.next();
                final DepositCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (myCurr.isForeignCurrency()) {
                    myCurr.calculateFluctuations(myRange);
                    pMarket.processAccount(myCurr);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final DepositCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pDeposits the deposit account buckets
         */
        protected void buildCategories(final DepositBucketList pDeposits) {
            /* Loop through the buckets */
            final Iterator<DepositBucket> myIterator = pDeposits.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final DepositBucket myCurr = myIterator.next();
                final DepositCategory myCategory = myCurr.getCategory();
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
            final MetisOrderedIdList<Integer, DepositCategoryBucket> myTotals = new MetisOrderedIdList<>(DepositCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<DepositCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final DepositCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final DepositCategory myCategory = myCurr.getAccountCategory();
                final DepositCategory myParent = myCategory.getParentCategory();

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
                myTotal.updateActive(myCurr);

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                final DepositCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                theList.add(myCurr);
            }

            /* Calculate delta for the totals */
            theTotals.calculateDelta();
        }
    }
}
