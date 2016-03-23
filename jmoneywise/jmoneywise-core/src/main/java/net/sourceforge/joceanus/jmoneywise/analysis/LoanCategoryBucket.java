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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;

/**
 * Loan Category Bucket.
 */
public final class LoanCategoryBucket
        extends AccountCategoryBucket<Loan, LoanCategory> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.LOANCATEGORY_NAME.getValue(), AccountCategoryBucket.FIELD_DEFS);

    /**
     * Loan Category Field Id.
     */
    private static final MetisField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.LOANCATEGORY.getItemName());

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

    @Override
    public int compareTo(final AccountCategoryBucket<Loan, LoanCategory> pThat) {
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
     * Update active flag for Loan Bucket.
     * @param pBucket the Loan bucket
     */
    private void updateActive(final LoanBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * Update active flag for Loan Category Bucket.
     * @param pBucket the Loan category bucket
     */
    private void updateActive(final LoanCategoryBucket pBucket) {
        isActive |= pBucket.isActive();
    }

    /**
     * LoanCategoryBucket list class.
     */
    public static final class LoanCategoryBucketList
            extends MetisOrderedIdList<Integer, LoanCategoryBucket>
            implements MetisDataContents {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.LOANCATEGORY_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

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
            super(LoanCategoryBucket.class);
            theAnalysis = pAnalysis;
            theCurrency = theAnalysis.getCurrency();
            theTotals = allocateTotalsBucket();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
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
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse loan accounts.
         * @param pLoans the loan account buckets
         */
        protected void analyseLoans(final LoanBucketList pLoans) {
            /* Loop through the buckets */
            Iterator<LoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                LoanBucket myCurr = myIterator.next();
                LoanCategory myCategory = myCurr.getCategory();

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                LoanCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
                myBucket.updateActive(myCurr);

                /* Note foreign currency */
                if (myCurr.isForeignCurrency()) {
                    haveForeignCurrency = Boolean.TRUE;
                }
            }
        }

        /**
         * Build categories.
         * @param pLoans the loan account buckets
         */
        protected void buildCategories(final LoanBucketList pLoans) {
            /* Loop through the buckets */
            Iterator<LoanBucket> myIterator = pLoans.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                LoanBucket myCurr = myIterator.next();
                LoanCategory myCategory = myCurr.getCategory();
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
            MetisOrderedIdList<Integer, LoanCategoryBucket> myTotals = new MetisOrderedIdList<>(LoanCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<LoanCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                LoanCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                LoanCategory myCategory = myCurr.getAccountCategory();
                LoanCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                LoanCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.findItemById(myParent.getId());

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
                LoanCategoryBucket myCurr = myIterator.next();

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
