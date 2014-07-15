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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;

/**
 * Cash Category Bucket.
 */
public final class CashCategoryBucket
        extends AccountCategoryBucket<Cash, CashCategory> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(CashCategoryBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), AccountCategoryBucket.FIELD_DEFS);

    /**
     * Cash Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASHCATEGORY.getItemName());

    /**
     * The cash category.
     */
    private final CashCategory theCategory;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
    public CashCategory getAccountCategory() {
        return theCategory;
    }

    /**
     * Constructor.
     * @param pCategory the account category
     */
    protected CashCategoryBucket(final CashCategory pCategory) {
        /* Store the category */
        theCategory = pCategory;
    }

    @Override
    public int compareTo(final AccountCategoryBucket<Cash, CashCategory> pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the AccountCategories */
        return getAccountCategory().compareTo(pThat.getAccountCategory());
    }

    /**
     * CashCategoryBucket list class.
     */
    public static final class CashCategoryBucketList
            extends OrderedIdList<Integer, CashCategoryBucket>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"));

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSize"));

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAnalysis"));

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataTotals"));

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The totals.
         */
        private final CashCategoryBucket theTotals;

        /**
         * Obtain the Totals.
         * @return the totals
         */
        public CashCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public CashCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(CashCategoryBucket.class);
            theAnalysis = pAnalysis;
            theTotals = allocateTotalsBucket();
        }

        /**
         * Allocate the Totals CategoryBucket.
         * @return the bucket
         */
        private CashCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new CashCategoryBucket(null);
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
                myItem = new CashCategoryBucket(pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Analyse cash accounts.
         * @param pCash the cash account buckets
         */
        protected void analyseCash(final CashBucketList pCash) {
            /* Loop through the buckets */
            Iterator<CashBucket> myIterator = pCash.iterator();
            while (myIterator.hasNext()) {
                /* Access bucket and category */
                CashBucket myCurr = myIterator.next();
                CashCategory myCategory = myCurr.getCategory();

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* Access category bucket and add values */
                CashCategoryBucket myBucket = getBucket(myCategory);
                myBucket.addValues(myCurr);
            }
        }

        /**
         * Produce totals for the categories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets (to avoid breaking iterator on add) */
            OrderedIdList<Integer, CashCategoryBucket> myTotals = new OrderedIdList<Integer, CashCategoryBucket>(CashCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<CashCategoryBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                CashCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                CashCategory myCategory = myCurr.getAccountCategory();
                CashCategory myParent = myCategory.getParentCategory();

                /* Calculate delta for the category */
                myCurr.calculateDelta();

                /* Access parent bucket */
                CashCategoryBucket myTotal = findItemById(myParent.getId());

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.findItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new CashCategoryBucket(myParent);
                        myTotals.add(myTotal);
                    }
                }

                /* Add the bucket to the totals */
                myTotal.addValues(myCurr);

                /* Add to totals bucket */
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                CashCategoryBucket myCurr = myIterator.next();

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