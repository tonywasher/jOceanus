/**
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.views;

import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionTagBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionTagBucket.TransactionTagBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.CashFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.DepositFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.LoanFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.PortfolioCashFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TagFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TransactionCategoryFilter;

/**
 * List of filters for a transaction within an analysis.
 */
public class TransactionFilters
        extends ArrayList<AnalysisFilter<?, ?>> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7093262525444372656L;

    /**
     * The transaction.
     */
    private final transient Transaction theTrans;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTrans the transaction
     */
    public TransactionFilters(final Analysis pAnalysis,
                              final Transaction pTrans) {
        /* Store transaction */
        theTrans = pTrans;

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

    /**
     * Add relevant deposit filters.
     * @param pDeposits the deposit buckets
     */
    private void analyseDeposits(final DepositBucketList pDeposits) {
        /* Loop through the deposit buckets */
        Iterator<DepositBucket> myIterator = pDeposits.iterator();
        while (myIterator.hasNext()) {
            DepositBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new DepositFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant cash filters.
     * @param pCash the cash buckets
     */
    private void analyseCash(final CashBucketList pCash) {
        /* Loop through the cash buckets */
        Iterator<CashBucket> myIterator = pCash.iterator();
        while (myIterator.hasNext()) {
            CashBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new CashFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant loan filters.
     * @param pLoans the loan buckets
     */
    private void analyseLoans(final LoanBucketList pLoans) {
        /* Loop through the loan buckets */
        Iterator<LoanBucket> myIterator = pLoans.iterator();
        while (myIterator.hasNext()) {
            LoanBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new LoanFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant portfolio filters.
     * @param pPortfolios the portfolio buckets
     */
    private void analysePortfolios(final PortfolioBucketList pPortfolios) {
        /* Loop through the portfolio buckets */
        Iterator<PortfolioBucket> myIterator = pPortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Analyse Securities */
            analyseSecurities(myBucket.getSecurities());

            /* Check for Cash bucket is relevant for the transaction */
            PortfolioCashBucket myCash = myBucket.getPortfolioCash();
            if (myCash.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new PortfolioCashFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant security filters.
     * @param pSecurities the security buckets
     */
    private void analyseSecurities(final SecurityBucketList pSecurities) {
        /* Loop through the security buckets */
        Iterator<SecurityBucket> myIterator = pSecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new SecurityFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant payee filters.
     * @param pPayees the payee buckets
     */
    private void analysePayees(final PayeeBucketList pPayees) {
        /* Loop through the payee buckets */
        Iterator<PayeeBucket> myIterator = pPayees.iterator();
        while (myIterator.hasNext()) {
            PayeeBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new PayeeFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant category filters.
     * @param pCategories the category buckets
     */
    private void analyseCategories(final TransactionCategoryBucketList pCategories) {
        /* Loop through the category buckets */
        Iterator<TransactionCategoryBucket> myIterator = pCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategoryBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new TransactionCategoryFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant taxBasis filters.
     * @param pBases the taxBasis buckets
     */
    private void analyseTaxBasis(final TaxBasisBucketList pBases) {
        /* Loop through the taxBasis buckets */
        Iterator<TaxBasisBucket> myIterator = pBases.iterator();
        while (myIterator.hasNext()) {
            TaxBasisBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.getValuesForTransaction(theTrans) != null) {
                /* Add filter */
                add(new TaxBasisFilter(myBucket));
            }
        }
    }

    /**
     * Add relevant transaction tag filters.
     * @param pTags the trqansactionTag buckets
     */
    private void analyseTags(final TransactionTagBucketList pTags) {
        /* Loop through the tag buckets */
        Iterator<TransactionTagBucket> myIterator = pTags.iterator();
        while (myIterator.hasNext()) {
            TransactionTagBucket myBucket = myIterator.next();

            /* If this bucket is relevant for the transaction */
            if (myBucket.hasTransaction(theTrans)) {
                /* Add filter */
                add(new TagFilter(myBucket));
            }
        }
    }
}