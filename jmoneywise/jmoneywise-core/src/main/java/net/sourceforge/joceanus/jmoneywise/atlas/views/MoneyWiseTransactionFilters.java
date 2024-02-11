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
package net.sourceforge.joceanus.jmoneywise.atlas.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransTagBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransTagBucket.MoneyWiseAnalysisTransTagBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisCashFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisDepositFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisLoanFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPayeeFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTagFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTaxBasisFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTransCategoryFilter;

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
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     */
    public MoneyWiseTransactionFilters(final MoneyWiseAnalysis pAnalysis,
                                       final MoneyWiseTransaction pTrans) {
        /* Store transaction */
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
                add(new MoneyWiseAnalysisDepositFilter(myBucket));
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
                add(new MoneyWiseAnalysisCashFilter(myBucket));
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
                add(new MoneyWiseAnalysisLoanFilter(myBucket));
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
                add(new MoneyWiseAnalysisPortfolioCashFilter(myBucket));
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
                add(new MoneyWiseAnalysisSecurityFilter(myBucket));
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
                add(new MoneyWiseAnalysisPayeeFilter(myBucket));
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
                add(new MoneyWiseAnalysisTransCategoryFilter(myBucket));
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
                add(new MoneyWiseAnalysisTaxBasisFilter(myBucket));
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
                add(new MoneyWiseAnalysisTagFilter(myBucket));
            }
        }
    }
}