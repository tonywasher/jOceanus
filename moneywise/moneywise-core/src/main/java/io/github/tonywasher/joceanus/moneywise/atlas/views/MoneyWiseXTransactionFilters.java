/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.views;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket.MoneyWiseXAnalysisPayeeBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioCashBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket.MoneyWiseXAnalysisSecurityBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket.MoneyWiseXAnalysisTransTagBucketList;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisCashFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisDepositFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisLoanFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisPayeeFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisPortfolioCashFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisSecurityFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTagFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTaxBasisFilter;
import io.github.tonywasher.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTransCategoryFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * List of filters for a transaction within an analysis.
 */
public class MoneyWiseXTransactionFilters
        implements MetisDataList<MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The list.
     */
    private final List<MoneyWiseXAnalysisFilter<?, ?>> theList;

    /**
     * The event.
     */
    private final MoneyWiseXAnalysisEvent theEvent;

    /**
     * The dateRange.
     */
    private final OceanusDateRange theDateRange;

    /**
     * Constructor.
     *
     * @param pAnalysis  the analysis
     * @param pDateRange the dateRange
     * @param pEvent     the event
     */
    public MoneyWiseXTransactionFilters(final MoneyWiseXAnalysis pAnalysis,
                                        final OceanusDateRange pDateRange,
                                        final MoneyWiseXAnalysisEvent pEvent) {
        /* Store dateRange and transaction */
        theDateRange = pDateRange;
        theEvent = pEvent;

        /* Create the list */
        theList = new ArrayList<>();

        /* Analyse accounts */
        analyseDeposits(pAnalysis.getDeposits());
        analyseCash(pAnalysis.getCash());
        analyseLoans(pAnalysis.getLoans());

        /* Analyse Portfolios */
        analysePortfolios(pAnalysis.getPortfolios());

        /* Analyse Payees */
        analysePayees(pAnalysis.getPayees());

        /* Analyse Categories */
        analyseCategories(pAnalysis.getTransCategories());

        /* Analyse TaxBases */
        analyseTaxBasis(pAnalysis.getTaxBasis());

        /* Analyse Tags */
        analyseTags(pAnalysis.getTransactionTags());
    }

    @Override
    public List<MoneyWiseXAnalysisFilter<?, ?>> getUnderlyingList() {
        return theList;
    }

    /**
     * Add relevant deposit filters.
     *
     * @param pDeposits the deposit buckets
     */
    private void analyseDeposits(final MoneyWiseXAnalysisDepositBucketList pDeposits) {
        /* Loop through the deposit buckets */
        final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = pDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisDepositBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisDepositFilter myFilter = new MoneyWiseXAnalysisDepositFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant cash filters.
     *
     * @param pCash the cash buckets
     */
    private void analyseCash(final MoneyWiseXAnalysisCashBucketList pCash) {
        /* Loop through the cash buckets */
        final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = pCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisCashBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisCashFilter myFilter = new MoneyWiseXAnalysisCashFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant loan filters.
     *
     * @param pLoans the loan buckets
     */
    private void analyseLoans(final MoneyWiseXAnalysisLoanBucketList pLoans) {
        /* Loop through the loan buckets */
        final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = pLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisLoanBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisLoanFilter myFilter = new MoneyWiseXAnalysisLoanFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant portfolio filters.
     *
     * @param pPortfolios the portfolio buckets
     */
    private void analysePortfolios(final MoneyWiseXAnalysisPortfolioBucketList pPortfolios) {
        /* Loop through the portfolio buckets */
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = pPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Analyse Securities */
            analyseSecurities(myBucket.getSecurities());

            /* Check for Cash bucket is relevant for the event */
            final MoneyWiseXAnalysisPortfolioCashBucket myCash = myBucket.getPortfolioCash();
            if (myCash.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisPortfolioCashFilter myFilter = new MoneyWiseXAnalysisPortfolioCashFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant security filters.
     *
     * @param pSecurities the security buckets
     */
    private void analyseSecurities(final MoneyWiseXAnalysisSecurityBucketList pSecurities) {
        /* Loop through the security buckets */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = pSecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisSecurityFilter myFilter = new MoneyWiseXAnalysisSecurityFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant payee filters.
     *
     * @param pPayees the payee buckets
     */
    private void analysePayees(final MoneyWiseXAnalysisPayeeBucketList pPayees) {
        /* Loop through the payee buckets */
        final Iterator<MoneyWiseXAnalysisPayeeBucket> myIterator = pPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPayeeBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisPayeeFilter myFilter = new MoneyWiseXAnalysisPayeeFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant category filters.
     *
     * @param pCategories the category buckets
     */
    private void analyseCategories(final MoneyWiseXAnalysisTransCategoryBucketList pCategories) {
        /* Loop through the category buckets */
        final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = pCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisTransCategoryFilter myFilter = new MoneyWiseXAnalysisTransCategoryFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant taxBasis filters.
     *
     * @param pBases the taxBasis buckets
     */
    private void analyseTaxBasis(final MoneyWiseXAnalysisTaxBasisBucketList pBases) {
        /* Loop through the taxBasis buckets */
        final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = pBases.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.getValuesForEvent(theEvent) != null) {
                /* Add filter */
                final MoneyWiseXAnalysisTaxBasisFilter myFilter = new MoneyWiseXAnalysisTaxBasisFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant transaction tag filters.
     *
     * @param pTags the trqansactionTag buckets
     */
    private void analyseTags(final MoneyWiseXAnalysisTransTagBucketList pTags) {
        /* Loop through the tag buckets */
        final Iterator<MoneyWiseXAnalysisTransTagBucket> myIterator = pTags.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTransTagBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the event */
            if (myBucket.hasEvent(theEvent)) {
                /* Add filter */
                final MoneyWiseXAnalysisTagFilter myFilter = new MoneyWiseXAnalysisTagFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }
}
