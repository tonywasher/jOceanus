/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.base.MoneyWiseXAnalysisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseXAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseXLoanBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;

/**
 * Loan Totals.
 */
public class MoneyWiseLoanTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseLoanTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseLoanTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseLoanTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseLoanTotals::getCategories);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> theTotals;

    /**
     * The top-level initial totals.
     */
    private final MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> theInitial;

    /**
     * The categories.
     */
    private final List<MoneyWiseLoanCategoryTotals> theCategories;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseLoanTotals(final MoneyWiseXAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseXAnalysisValues<>(MoneyWiseXAccountAttr.class, myCurrency);
        theInitial = new MoneyWiseXAnalysisValues<>(MoneyWiseXAccountAttr.class, myCurrency);
        theCategories = new ArrayList<>();

        /* Loop through the loan buckets */
        final Map<Integer, MoneyWiseLoanCategoryTotals> myMap = new HashMap<>();
        for (MoneyWiseXLoanBucket myBucket : pAnalysis.getLoans().values()) {
            /* Add to category totals */
            final LoanCategory myCategory = myBucket.getOwner().getCategory();
            final MoneyWiseLoanCategoryTotals myTotals = myMap.computeIfAbsent(myCategory.getId(),
                    i -> new MoneyWiseLoanCategoryTotals(myCategory, myCurrency));
            myTotals.addBucket(myBucket);
        }

        /* Loop through the loan categories */
        for (MoneyWiseLoanCategoryTotals myCategory : myMap.values()) {
            /* Adjust totals */
            MoneyWiseAssetTotals.addToTotals(theTotals, myCategory.getTotals());
            MoneyWiseAssetTotals.addToTotals(theInitial, myCategory.getInitial());

            /* Sort the buckets and add to list */
            myCategory.sortTheList();
            theCategories.add(myCategory);
        }

        /* Sort the categories */
        theCategories.sort(Comparator.comparing(MoneyWiseLoanCategoryTotals::getCategory));
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial.
     * @return the initial totals
     */
    public MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the categories.
     * @return the categories
     */
    public List<MoneyWiseLoanCategoryTotals> getCategories() {
        return theCategories;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * The Category totals.
     */
    public static class MoneyWiseLoanCategoryTotals
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseLoanCategoryTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanCategoryTotals.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORY, MoneyWiseLoanCategoryTotals::getCategory);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseLoanCategoryTotals::getTotals);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseLoanCategoryTotals::getInitial);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CATEGORIES, MoneyWiseLoanCategoryTotals::getBuckets);
        }

        /**
         * The category.
         */
        private final LoanCategory theCategory;

        /**
         * The category totals.
         */
        private final MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> theTotals;

        /**
         * The category initial totals.
         */
        private final MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> theInitial;

        /**
         * The loan buckets.
         */
        private final List<MoneyWiseXLoanBucket> theBuckets;

        /**
         * Constructor.
         * @param pCategory the category
         * @param pCurrency the reporting currency
         */
        MoneyWiseLoanCategoryTotals(final LoanCategory pCategory,
                                    final AssetCurrency pCurrency) {
            theCategory = pCategory;
            theTotals = new MoneyWiseXAnalysisValues<>(MoneyWiseXAccountAttr.class, pCurrency);
            theInitial = new MoneyWiseXAnalysisValues<>(MoneyWiseXAccountAttr.class, pCurrency);
            theBuckets = new ArrayList<>();
        }

        /**
         * Obtain the category.
         * @return the category
         */
        public LoanCategory getCategory() {
            return theCategory;
        }

        /**
         * Obtain the totals.
         * @return the totals
         */
        public MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> getTotals() {
            return theTotals;
        }

        /**
         * Obtain the initial.
         * @return the initial totals
         */
        public MoneyWiseXAnalysisValues<MoneyWiseXAccountAttr> getInitial() {
            return theInitial;
        }

        /**
         * Obtain the loan buckets.
         * @return the buckets
         */
        public List<MoneyWiseXLoanBucket> getBuckets() {
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
        void addBucket(final MoneyWiseXLoanBucket pBucket) {
            MoneyWiseAssetTotals.addToTotals(theTotals, pBucket.getValues());
            MoneyWiseAssetTotals.addToTotals(theInitial, pBucket.getInitial());
            theBuckets.add(pBucket);
        }

        /**
         * Sort the list.
         */
        void sortTheList() {
            theBuckets.sort(Comparator.comparing(MoneyWiseXAnalysisBucket::getOwner));
        }
    }
}
