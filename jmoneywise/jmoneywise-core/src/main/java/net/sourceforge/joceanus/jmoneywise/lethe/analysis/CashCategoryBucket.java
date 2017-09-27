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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Cash Category Bucket.
 */
public final class CashCategoryBucket
        extends AccountCategoryBucket<Cash, CashCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CashCategoryBucket.class, AccountCategoryBucket.getBaseFieldSet());

    /**
     * Cash Category Field Id.
     */
    private static final MetisDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASHCATEGORY.getItemName());

    /**
     * The cash category.
     */
    private final CashCategory theCategory;

    /**
     * Is the category active?
     */
    private boolean isActive;

    /**
     * Constructor.
     * @param pCurrency the currency
     * @param pCategory the account category
     */
    protected CashCategoryBucket(final AssetCurrency pCurrency,
                                 final CashCategory pCategory) {
        super(pCurrency);
        theCategory = pCategory;
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public String getName() {
        return theCategory == null
                                   ? NAME_TOTALS
                                   : theCategory.getName();
    }

    @Override
    public Integer getIndexedId() {
        return theCategory.getId();
    }

    @Override
    public CashCategory getAccountCategory() {
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
    public int compareTo(final AccountCategoryBucket<Cash, CashCategory> pThat) {
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
     * Update active flag for Cash Bucket.
     * @param pBucket the Cash bucket
     */
    protected void updateActive(final CashBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Cash Category Bucket.
     * @param pBucket the Cash category bucket
     */
    protected void updateActive(final CashCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * CashCategoryBucket list class.
     */
    public static final class CashCategoryBucketList
            implements MetisDataFieldItem, MetisDataList<CashCategoryBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(CashCategoryBucketList.class);

        /**
         * Analysis field Id.
         */
        private static final MetisDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final MetisDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisIndexedList<CashCategoryBucket> theList;

        /**
         * The currency.
         */
        private final AssetCurrency theCurrency;

        /**
         * The totals.
         */
        private final CashCategoryBucket theTotals;

        /**
         * Do we have a foreign cash account?
         */
        private Boolean haveForeignCurrency = Boolean.FALSE;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected CashCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
            theList = new MetisIndexedList<>();
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<CashCategoryBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return MetisDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public CashCategoryBucket findItemById(final Integer pId) {
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
         * Obtain the Totals.
         * @return the totals
         */
        public CashCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals CategoryBucket.
         * @return the bucket
         */
        private CashCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new CashCategoryBucket(theCurrency, null);
        }

        /**
         * Obtain the CashCategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        protected CashCategoryBucket getBucket(final CashCategory pCategory) {
            /* Locate the bucket in the list */
            CashCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new CashCategoryBucket(theCurrency, pCategory);

                /* Add to the list */
                theList.addToList(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse cash accounts.
         * @param pMarket the market analysis
         * @param pCash the cash account buckets
         */
        protected void analyseCash(final MarketAnalysis pMarket,
                                   final CashBucketList pCash) {
            /* Sort the cash */
            Collections.sort(pCash.getUnderlyingList());

            /* Loop through the buckets */
            final TethysDateRange myRange = theAnalysis.getDateRange();
            final Iterator<CashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final CashBucket myCurr = myIterator.next();
                final CashCategory myCategory = myCurr.getCategory();

                /* Handle foreign asset */
                if (myCurr.isForeignCurrency()) {
                    myCurr.calculateFluctuations(myRange);
                    pMarket.processAccount(myCurr);
                    haveForeignCurrency = Boolean.TRUE;
                }

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                final CashCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);
            }
        }

        /**
         * Build categories.
         * @param pCash the cash account buckets
         */
        protected void buildCategories(final CashBucketList pCash) {
            /* Loop through the buckets */
            final Iterator<CashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                final CashBucket myCurr = myIterator.next();
                final CashCategory myCategory = myCurr.getCategory();
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
            final MetisIndexedList<CashCategoryBucket> myTotals = new MetisIndexedList<>();

            /* Loop through the buckets */
            Iterator<CashCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final CashCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                final CashCategory myCategory = myCurr.getAccountCategory();
                final CashCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                CashCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.getItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new CashCategoryBucket(theCurrency, myParent);
                        myTotals.addToList(myTotal);
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
                final CashCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                theList.addToList(myCurr);
            }

            /* Sort the list */
            Collections.sort(theList.getUnderlyingList());

            /* Calculate delta for the totals */
            theTotals.calculateDelta();
        }
    }
}
