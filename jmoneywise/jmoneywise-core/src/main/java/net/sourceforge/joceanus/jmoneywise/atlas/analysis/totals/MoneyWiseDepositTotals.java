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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseDepositBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;

/**
 * Deposit Totals.
 */
public class MoneyWiseDepositTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseDepositTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseDepositTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseDepositTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseDepositTotals::getCategories);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theTotals;

    /**
     * The top-level initial totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theInitial;

    /**
     * The categories.
     */
    private final List<MoneyWiseDepositCategoryTotals> theCategories;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseDepositTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theCategories = new ArrayList<>();

        /* Loop through the deposit buckets */
        final Map<Integer, MoneyWiseDepositCategoryTotals> myMap = new HashMap<>();
        for (MoneyWiseDepositBucket myBucket : pAnalysis.getDeposits().values()) {
            /* Add to category totals */
            final DepositCategory myCategory = myBucket.getOwner().getCategory();
            final MoneyWiseDepositCategoryTotals myTotals = myMap.computeIfAbsent(myCategory.getId(),
                    i -> new MoneyWiseDepositCategoryTotals(myCategory, myCurrency));
            myTotals.addBucket(myBucket);
        }

        /* Loop through the deposit categories */
        for (MoneyWiseDepositCategoryTotals myCategory : myMap.values()) {
            /* Adjust totals */
            MoneyWiseAssetTotals.addToTotals(theTotals, myCategory.getTotals());
            MoneyWiseAssetTotals.addToTotals(theInitial, myCategory.getInitial());

            /* Sort the buckets and add to list */
            myCategory.sortTheList();
            theCategories.add(myCategory);
        }

        /* Sort the categories */
        theCategories.sort(Comparator.comparing(MoneyWiseDepositCategoryTotals::getCategory));
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial.
     * @return the initial totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the categories.
     * @return the categories
     */
    public List<MoneyWiseDepositCategoryTotals> getCategories() {
        return theCategories;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * The Category totals.
     */
    public static class MoneyWiseDepositCategoryTotals
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositCategoryTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositCategoryTotals.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORY, MoneyWiseDepositCategoryTotals::getCategory);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseDepositCategoryTotals::getTotals);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseDepositCategoryTotals::getInitial);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseDepositCategoryTotals::getBuckets);
        }

        /**
         * The category.
         */
        private final DepositCategory theCategory;

        /**
         * The category totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theTotals;

        /**
         * The category initial totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theInitial;

        /**
         * The deposit buckets.
         */
        private final List<MoneyWiseDepositBucket> theBuckets;

        /**
         * Constructor.
         * @param pCategory the category
         * @param pCurrency the reporting currency
         */
        MoneyWiseDepositCategoryTotals(final DepositCategory pCategory,
                                       final AssetCurrency pCurrency) {
            theCategory = pCategory;
            theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pCurrency);
            theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pCurrency);
            theBuckets = new ArrayList<>();
        }

        /**
         * Obtain the category.
         * @return the category
         */
        public DepositCategory getCategory() {
            return theCategory;
        }

        /**
         * Obtain the totals.
         * @return the totals
         */
        public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getTotals() {
            return theTotals;
        }

        /**
         * Obtain the initial.
         * @return the initial totals
         */
        public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getInitial() {
            return theInitial;
        }

        /**
         * Obtain the deposit buckets.
         * @return the buckets
         */
        public List<MoneyWiseDepositBucket> getBuckets() {
            return theBuckets;
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Add bucket.
         * @param pBucket the bucket
         */
        void addBucket(final MoneyWiseDepositBucket pBucket) {
            MoneyWiseAssetTotals.addToTotals(theTotals, pBucket.getValues());
            MoneyWiseAssetTotals.addToTotals(theInitial, pBucket.getInitial());
            theBuckets.add(pBucket);
        }

        /**
         * Sort the list.
         */
        void sortTheList() {
            theBuckets.sort(Comparator.comparing(MoneyWiseAnalysisBucket::getOwner));
        }
    }
}
