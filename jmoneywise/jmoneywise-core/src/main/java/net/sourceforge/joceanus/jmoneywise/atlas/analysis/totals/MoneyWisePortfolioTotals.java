/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWisePortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.analysis.data.MoneyWiseSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Portfolio Totals.
 */
public class MoneyWisePortfolioTotals
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWisePortfolioTotals> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioTotals.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWisePortfolioTotals::getTotals);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWisePortfolioTotals::getInitial);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_PORTFOLIOS, MoneyWisePortfolioTotals::getPortfolios);
    }

    /**
     * The top-level totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theTotals;

    /**
     * The initial totals.
     */
    private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theInitial;

    /**
     * The categories.
     */
    private final List<MoneyWiseConsolidatedPortfolio> thePortfolios;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWisePortfolioTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create fields */
        final AssetCurrency myCurrency = pAnalysis.getReportingCurrency();
        theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, myCurrency);
        thePortfolios = new ArrayList<>();

        /* Loop through the portfolio buckets */
        final Map<Integer, MoneyWiseConsolidatedPortfolio> myMap = new HashMap<>();
        for (MoneyWisePortfolioBucket myBucket : pAnalysis.getPortfolios().values()) {
            /* Create portfolio summary */
            final Portfolio myPortfolio = myBucket.getOwner();
            myMap.put(myPortfolio.getId(), new MoneyWiseConsolidatedPortfolio(myBucket, myCurrency));
        }

        /* Loop through the holding buckets */
        for (MoneyWiseSecurityBucket myBucket : pAnalysis.getHoldings().values()) {
            /* Add to portfolio summary */
            final Portfolio myPortfolio = myBucket.getPortfolio();
            final MoneyWiseConsolidatedPortfolio mySummary = myMap.computeIfAbsent(myPortfolio.getId(),
                    i -> new MoneyWiseConsolidatedPortfolio(myPortfolio, myCurrency));
            mySummary.addBucket(myBucket);
        }

        /* Loop through the portfolio summaries */
        for (MoneyWiseConsolidatedPortfolio myPortfolio : myMap.values()) {
            /* Adjust totals */
            MoneyWiseAssetTotals.addToTotals(theTotals, myPortfolio.getTotals());
            MoneyWiseAssetTotals.addToTotals(theInitial, myPortfolio.getInitial());

            /* Sort the holdings and add to list */
            myPortfolio.sortTheList();
            thePortfolios.add(myPortfolio);
        }

        /* Sort the portfolios */
        thePortfolios.sort(Comparator.comparing(MoneyWiseConsolidatedPortfolio::getPortfolio));
    }

    /**
     * Obtain the totals.
     * @return the totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getTotals() {
        return theTotals;
    }

    /**
     * Obtain the initial totals.
     * @return the initial totals
     */
    public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getInitial() {
        return theInitial;
    }

    /**
     * Obtain the portfolio totals.
     * @return the totals
     */
    public List<MoneyWiseConsolidatedPortfolio> getPortfolios() {
        return thePortfolios;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Add to totals.
     * @param pTotals the totals
     * @param pDelta the delta
     */
    static void addToTotals(final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> pTotals,
                            final MoneyWiseAnalysisValues<MoneyWiseSecurityAttr> pDelta) {
        final TethysMoney myTotals = pTotals.getMoneyValue(MoneyWiseAccountAttr.VALUATION);
        final TethysMoney myDelta = pDelta.getMoneyValue(MoneyWiseSecurityAttr.VALUATION);
        myTotals.addAmount(myDelta);
    }

    /**
     * The Consolidated Portfolio Totals.
     */
    public static class MoneyWiseConsolidatedPortfolio
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseConsolidatedPortfolio> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseConsolidatedPortfolio.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_PORTFOLIO, MoneyWiseConsolidatedPortfolio::getPortfolio);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_TOTAL, MoneyWiseConsolidatedPortfolio::getTotals);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_INITIAL, MoneyWiseConsolidatedPortfolio::getInitial);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_CASH, MoneyWiseConsolidatedPortfolio::getCashBucket);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisTotalsResource.TOTALS_HOLDINGS, MoneyWiseConsolidatedPortfolio::getBuckets);
        }

        /**
         * The portfolio.
         */
        private final Portfolio thePortfolio;

        /**
         * The portfolio totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theTotals;

        /**
         * The portfolio initial totals.
         */
        private final MoneyWiseAnalysisValues<MoneyWiseAccountAttr> theInitial;

        /**
         * The portfolio bucket.
         */
        private final MoneyWisePortfolioBucket theCash;

        /**
         * The holding buckets.
         */
        private final List<MoneyWiseSecurityBucket> theBuckets;

        /**
         * Constructor.
         * @param pPortfolioCash the portfolioBucket
         * @param pCurrency the reporting currency
         */
        MoneyWiseConsolidatedPortfolio(final MoneyWisePortfolioBucket pPortfolioCash,
                                       final AssetCurrency pCurrency) {
            this(pPortfolioCash, pPortfolioCash.getOwner(), pCurrency);
        }

        /**
         * Constructor.
         * @param pPortfolio the portfolio
         * @param pCurrency the reporting currency
         */
        MoneyWiseConsolidatedPortfolio(final Portfolio pPortfolio,
                                       final AssetCurrency pCurrency) {
            this(null, pPortfolio, pCurrency);
        }

        /**
         * Constructor.
         * @param pPortfolioCash the portfolioBucket
         * @param pPortfolio the portfolio
         * @param pCurrency the reporting currency
         */
        private MoneyWiseConsolidatedPortfolio(final MoneyWisePortfolioBucket pPortfolioCash,
                                               final Portfolio pPortfolio,
                                               final AssetCurrency pCurrency) {
            theCash = pPortfolioCash;
            thePortfolio = pPortfolio;
            theTotals = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pCurrency);
            theInitial = new MoneyWiseAnalysisValues<>(MoneyWiseAccountAttr.class, pCurrency);
            theBuckets = new ArrayList<>();
        }

        /**
         * Obtain the portfolio.
         * @return the portfolio
         */
        public Portfolio getPortfolio() {
            return thePortfolio;
        }

        /**
         * Obtain the totals.
         * @return the totals
         */
        public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getTotals() {
            return theTotals;
        }

        /**
         * Obtain the initial totals.
         * @return the initial totals
         */
        public MoneyWiseAnalysisValues<MoneyWiseAccountAttr> getInitial() {
            return theInitial;
        }

        /**
         * Obtain the portfolio cashBucket.
         * @return the bucket
         */
        public MoneyWisePortfolioBucket getCashBucket() {
            return theCash;
        }

        /**
         * Obtain the portfolio buckets.
         * @return the buckets
         */
        public List<MoneyWiseSecurityBucket> getBuckets() {
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
        void addBucket(final MoneyWiseSecurityBucket pBucket) {
            addToTotals(theTotals, pBucket.getValues());
            addToTotals(theInitial, pBucket.getInitial());
            theBuckets.add(pBucket);
        }

        /**
         * Sort the list.
         */
        void sortTheList() {
            theBuckets.sort(Comparator.comparing(MoneyWiseSecurityBucket::getSecurity));
        }
    }
}
