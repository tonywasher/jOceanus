/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisCashBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisDepositBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisLoanBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisTransTagBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisTransTagBucket.MoneyWiseAnalysisTransTagBucketList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisCashFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisDepositFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisLoanFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPayeeFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTagFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTaxBasisFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTransCategoryFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * List of filters for a transaction within an analysis.
 */
public class MoneyWiseTransactionFilters
        implements MetisDataList<MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The list.
     */
    private final List<MoneyWiseAnalysisFilter<?, ?>> theList;

    /**
     * The transaction.
     */
    private final MoneyWiseTransaction theTrans;

    /**
     * The dateRange.
     */
    private final TethysDateRange theDateRange;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pDateRange the dateRange
     * @param pTrans the transaction
     */
    public MoneyWiseTransactionFilters(final MoneyWiseAnalysis pAnalysis,
                                       final TethysDateRange pDateRange,
                                       final MoneyWiseTransaction pTrans) {
        /* Store dateRange and transaction */
        theDateRange = pDateRange;
        theTrans = pTrans;

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
    public List<MoneyWiseAnalysisFilter<?, ?>> getUnderlyingList() {
        return theList;
    }

    /**
     * Add relevant deposit filters.
     * @param pDeposits the deposit buckets
     */
    private void analyseDeposits(final MoneyWiseAnalysisDepositBucketList pDeposits) {
        /* Loop through the deposit buckets */
        final Iterator<MoneyWiseAnalysisDepositBucket> myIterator = pDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisDepositBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisDepositFilter myFilter = new MoneyWiseAnalysisDepositFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant cash filters.
     * @param pCash the cash buckets
     */
    private void analyseCash(final MoneyWiseAnalysisCashBucketList pCash) {
        /* Loop through the cash buckets */
        final Iterator<MoneyWiseAnalysisCashBucket> myIterator = pCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisCashFilter myFilter = new MoneyWiseAnalysisCashFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant loan filters.
     * @param pLoans the loan buckets
     */
    private void analyseLoans(final MoneyWiseAnalysisLoanBucketList pLoans) {
        /* Loop through the loan buckets */
        final Iterator<MoneyWiseAnalysisLoanBucket> myIterator = pLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisLoanBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisLoanFilter myFilter = new MoneyWiseAnalysisLoanFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant portfolio filters.
     * @param pPortfolios the portfolio buckets
     */
    private void analysePortfolios(final MoneyWiseAnalysisPortfolioBucketList pPortfolios) {
        /* Loop through the portfolio buckets */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = pPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Analyse Securities */
            analyseSecurities(myBucket.getSecurities());

            /* Check for Cash bucket is relevant for the transaction */
            final MoneyWiseAnalysisPortfolioCashBucket myCash = myBucket.getPortfolioCash();
            if (myCash.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisPortfolioCashFilter myFilter = new MoneyWiseAnalysisPortfolioCashFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant security filters.
     * @param pSecurities the security buckets
     */
    private void analyseSecurities(final MoneyWiseAnalysisSecurityBucketList pSecurities) {
        /* Loop through the security buckets */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = pSecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisSecurityFilter myFilter = new MoneyWiseAnalysisSecurityFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant payee filters.
     * @param pPayees the payee buckets
     */
    private void analysePayees(final MoneyWiseAnalysisPayeeBucketList pPayees) {
        /* Loop through the payee buckets */
        final Iterator<MoneyWiseAnalysisPayeeBucket> myIterator = pPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPayeeBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisPayeeFilter myFilter = new MoneyWiseAnalysisPayeeFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant category filters.
     * @param pCategories the category buckets
     */
    private void analyseCategories(final MoneyWiseAnalysisTransCategoryBucketList pCategories) {
        /* Loop through the category buckets */
        final Iterator<MoneyWiseAnalysisTransCategoryBucket> myIterator = pCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisTransCategoryFilter myFilter = new MoneyWiseAnalysisTransCategoryFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant taxBasis filters.
     * @param pBases the taxBasis buckets
     */
    private void analyseTaxBasis(final MoneyWiseAnalysisTaxBasisBucketList pBases) {
        /* Loop through the taxBasis buckets */
        final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = pBases.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTaxBasisBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                final MoneyWiseAnalysisTaxBasisFilter myFilter = new MoneyWiseAnalysisTaxBasisFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }

    /**
     * Add relevant transaction tag filters.
     * @param pTags the trqansactionTag buckets
     */
    private void analyseTags(final MoneyWiseAnalysisTransTagBucketList pTags) {
        /* Loop through the tag buckets */
        final Iterator<MoneyWiseAnalysisTransTagBucket> myIterator = pTags.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTransTagBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.hasTransaction(theTrans)) {
                /* Add filter */
                final MoneyWiseAnalysisTagFilter myFilter = new MoneyWiseAnalysisTagFilter(myBucket);
                myFilter.setDateRange(theDateRange);
                add(myFilter);
            }
        }
    }
}
