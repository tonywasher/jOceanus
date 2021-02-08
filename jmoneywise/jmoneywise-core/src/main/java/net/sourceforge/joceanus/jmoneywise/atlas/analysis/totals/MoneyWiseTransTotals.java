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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.totals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseIncomeAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseTransactionBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;

/**
 * Transaction Totals.
 */
public class MoneyWiseTransTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTransTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseTransTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseTransTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseTransTotals::getCategories);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> theTotals;

    /**
     * The initial totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> theInitial;

    /**
     * The categories.
     */
    private final List<MoneyWiseTransCategoryTotals> theCategories;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseTransTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, myCurrency);
        theCategories = new ArrayList<>();

        /* Loop through the transaction buckets */
        final Map<Integer, MoneyWiseTransCategoryTotals> myMap = new HashMap<>();
        for (MoneyWiseTransactionBucket myBucket : pAnalysis.getTrans().values()) {
            /* Add to category totals */
            final TransactionCategory myCategory = myBucket.getOwner();
            final MoneyWiseTransCategoryTotals myTotals = myMap.computeIfAbsent(myCategory.getId(),
                    i -> new MoneyWiseTransCategoryTotals(myCategory, myCurrency));
            myTotals.setBucket(myBucket);

            /* Update the hierarchy */
            updateHierarchy(myMap, myTotals, myCurrency);
        }

        /* Loop through the transaction categories */
        for (MoneyWiseTransCategoryTotals myCategory : myMap.values()) {
            /* Ignore if not a top-level category */
            if (myCategory.getParentCategory() != null) {
                continue;
            }

            /* Consolidate the totals */
            myCategory.consolidateTotals();

            /* Adjust totals */
            MoneyWisePayeeTotals.addToTotals(theTotals, myCategory.getTotals());
            MoneyWisePayeeTotals.addToTotals(theInitial, myCategory.getInitial());

            /* add to list */
            theCategories.add(myCategory);
        }

        /* Sort the categories */
        theCategories.sort(Comparator.comparing(MoneyWiseTransCategoryTotals::getCategory));
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial totals.
     * @return the initial totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the categories.
     * @return the categories
     */
    public List<MoneyWiseTransCategoryTotals> getCategories() {
        return theCategories;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Update the hierarchy.
     * @param pMap the map
     * @param pTotals the totals
     * @param pCurrency the reporting currency
     */
    public void updateHierarchy(final Map<Integer, MoneyWiseTransCategoryTotals> pMap,
                                final MoneyWiseTransCategoryTotals pTotals,
                                final AssetCurrency pCurrency) {
        final TransactionCategory myParent = pTotals.getParentCategory();
        if (myParent != null) {
            final MoneyWiseTransCategoryTotals myTotals = pMap.computeIfAbsent(myParent.getId(),
                    i -> new MoneyWiseTransCategoryTotals(myParent, pCurrency));
            myTotals.addChild(pTotals);
        }
    }

    /**
     * The Category totals.
     */
    public static class MoneyWiseTransCategoryTotals
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransCategoryTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransCategoryTotals.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORY, MoneyWiseTransCategoryTotals::getCategory);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseTransCategoryTotals::getTotals);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseTransCategoryTotals::getInitial);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_BUCKET, MoneyWiseTransCategoryTotals::getBucket);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseTransCategoryTotals::getCategories);
        }

        /**
         * The category.
         */
        private final TransactionCategory theCategory;

        /**
         * The category totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> theTotals;

        /**
         * The category initial totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> theInitial;

        /**
         * The underlying categories.
         */
        private final List<MoneyWiseTransCategoryTotals> theCategories;

        /**
         * The explicit bucket.
         */
        private MoneyWiseTransactionBucket theBucket;

        /**
         * Constructor.
         * @param pCategory the category
         * @param pCurrency the reporting currency
         */
        MoneyWiseTransCategoryTotals(final TransactionCategory pCategory,
                                     final AssetCurrency pCurrency) {
            theCategory = pCategory;
            theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, pCurrency);
            theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseIncomeAttr.class, pCurrency);
            theCategories = new ArrayList<>();
        }

        /**
         * Obtain the category.
         * @return the category
         */
        public TransactionCategory getCategory() {
            return theCategory;
        }

        /**
         * Obtain the parent category.
         * @return the category
         */
        public TransactionCategory getParentCategory() {
            return theCategory.getParentCategory();
        }

        /**
         * Obtain the totals.
         * @return the totals
         */
        public MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> getTotals() {
            return theTotals;
        }

        /**
         * Obtain the initial totals.
         * @return the initial totals
         */
        public MoneyWiseAnalysisValues<MoneyWiseIncomeAttr> getInitial() {
            return theInitial;
        }

        /**
         * Obtain the bucket.
         * @return the bucket
         */
        public MoneyWiseTransactionBucket getBucket() {
            return theBucket;
        }

        /**
         * Obtain the category buckets.
         * @return the buckets
         */
        public List<MoneyWiseTransCategoryTotals> getCategories() {
            return theCategories;
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Set bucket.
         * @param pBucket the bucket
         */
        void setBucket(final MoneyWiseTransactionBucket pBucket) {
            theBucket = pBucket;
        }

        /**
         * Add child category.
         * @param pChild the child category
         */
        void addChild(final MoneyWiseTransCategoryTotals pChild) {
            theCategories.add(pChild);
        }

        /**
         * Consolidate the totals.
         */
        void consolidateTotals() {
            /* Loop through the underlying categories */
            for (MoneyWiseTransCategoryTotals myCategory : theCategories) {
                /* Consolidate the totals */
                myCategory.consolidateTotals();
                MoneyWisePayeeTotals.addToTotals(theTotals, myCategory.getTotals());
                MoneyWisePayeeTotals.addToTotals(theInitial, myCategory.getInitial());
            }

            /* Add in the explicit bucket if available */
            if (theBucket != null) {
                MoneyWisePayeeTotals.addToTotals(theTotals, theBucket.getValues());
                MoneyWisePayeeTotals.addToTotals(theInitial, theBucket.getInitial());
            }

            /* Sort the underlying categories */
            theCategories.sort(Comparator.comparing(MoneyWiseTransCategoryTotals::getCategory));
        }
    }
}
