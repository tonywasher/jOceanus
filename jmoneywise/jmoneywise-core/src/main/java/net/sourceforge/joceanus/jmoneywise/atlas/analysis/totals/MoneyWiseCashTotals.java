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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseCashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;

/**
 * Cash Totals.
 */
public class MoneyWiseCashTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseCashTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseCashTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseCashTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseCashTotals::getCategories);
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
    private final List<MoneyWiseCashCategoryTotals> theCategories;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseCashTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theCategories = new ArrayList<>();

        /* Loop through the cash buckets */
        final Map<Integer, MoneyWiseCashCategoryTotals> myMap = new HashMap<>();
        for (MoneyWiseCashBucket myBucket : pAnalysis.getCash().values()) {
            /* Add to category totals */
            final CashCategory myCategory = myBucket.getOwner().getCategory();
            final MoneyWiseCashCategoryTotals myTotals = myMap.computeIfAbsent(myCategory.getId(),
                    i -> new MoneyWiseCashCategoryTotals(myCategory, myCurrency));
            myTotals.addBucket(myBucket);
        }

        /* Loop through the cash categories */
        for (MoneyWiseCashCategoryTotals myCategory : myMap.values()) {
            /* Adjust totals */
            MoneyWiseAssetTotals.addToTotals(theTotals, myCategory.getTotals());
            MoneyWiseAssetTotals.addToTotals(theInitial, myCategory.getInitial());

            /* Sort the accounts and add to list */
            myCategory.sortTheList();
            theCategories.add(myCategory);
        }

        /* Sort the categories */
        theCategories.sort(Comparator.comparing(MoneyWiseCashCategoryTotals::getCategory));
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
    public List<MoneyWiseCashCategoryTotals> getCategories() {
        return theCategories;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * The Category totals.
     */
    public static class MoneyWiseCashCategoryTotals
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCashCategoryTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashCategoryTotals.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORY, MoneyWiseCashCategoryTotals::getCategory);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseCashCategoryTotals::getTotals);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseCashCategoryTotals::getInitial);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseCashCategoryTotals::getBuckets);
        }

        /**
         * The category.
         */
        private final CashCategory theCategory;

        /**
         * The category totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theTotals;

        /**
         * The category initial totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theInitial;

        /**
         * The cash buckets.
         */
        private final List<MoneyWiseCashBucket> theBuckets;

        /**
         * Constructor.
         * @param pCategory the category
         * @param pCurrency the reporting currency
         */
        MoneyWiseCashCategoryTotals(final CashCategory pCategory,
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
        public CashCategory getCategory() {
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
         * Obtain the cash buckets.
         * @return the buckets
         */
        public List<MoneyWiseCashBucket> getBuckets() {
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
        void addBucket(final MoneyWiseCashBucket pBucket) {
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
