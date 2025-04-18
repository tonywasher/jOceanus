/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.util.Currency;
import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisCreditCardValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * The Loan Bucket class.
 */
public final class MoneyWiseAnalysisLoanBucket
        extends MoneyWiseAnalysisAccountBucket<MoneyWiseLoan> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisLoanBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisLoanBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseAnalysisLoanBucket::getCategory);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.LOAN_CREDITCARD, MoneyWiseAnalysisLoanBucket::isCreditCard);
    }

    /**
     * The loan category.
     */
    private final MoneyWiseLoanCategory theCategory;

    /**
     * Is this a creditCard?
     */
    private final Boolean isCreditCard;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pLoan the loan
     */
    private MoneyWiseAnalysisLoanBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseLoan pLoan) {
        /* Call super-constructor */
        super(pAnalysis, pLoan);

        /* Obtain category */
        theCategory = pLoan.getCategory();

        /* Determine whether this is a credit card */
        isCreditCard = theCategory.isCategoryClass(MoneyWiseLoanCategoryClass.CREDITCARD);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private MoneyWiseAnalysisLoanBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisLoanBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        isCreditCard = pBase.isCreditCard();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseAnalysisLoanBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisLoanBucket pBase,
                                        final OceanusDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        isCreditCard = pBase.isCreditCard();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private MoneyWiseAnalysisLoanBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisLoanBucket pBase,
                                        final OceanusDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        isCreditCard = pBase.isCreditCard();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisLoanBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the loan category.
     * @return the loan category
     */
    public MoneyWiseLoanCategory getCategory() {
        return theCategory;
    }

    /**
     * Is this a creditCard?
     * @return true/false
     */
    public Boolean isCreditCard() {
        return isCreditCard;
    }

    @Override
    protected MoneyWiseAnalysisAccountValues allocateStandardValues(final Currency pCurrency) {
        return getAccount().isLoanClass(MoneyWiseLoanCategoryClass.CREDITCARD)
                ? new MoneyWiseAnalysisCreditCardValues(pCurrency)
                : super.allocateStandardValues(pCurrency);
    }

    @Override
    protected MoneyWiseAnalysisAccountValues allocateForeignValues(final Currency pCurrency,
                                                                   final Currency pReportingCurrency) {
        return getAccount().isLoanClass(MoneyWiseLoanCategoryClass.CREDITCARD)
                ? new MoneyWiseAnalysisCreditCardValues(pCurrency, pReportingCurrency)
                : super.allocateForeignValues(pCurrency, pReportingCurrency);
    }

    @Override
    public void adjustForDebit(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* If this is a credit card */
        if (Boolean.TRUE.equals(isCreditCard)) {
            /* Access the amount */
            final OceanusMoney myAmount = pHelper.getDebitAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust spend */
                adjustCounter(MoneyWiseAnalysisAccountAttr.SPEND, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pHelper);
    }

    /**
     * LoanBucket list class.
     */
    public static final class MoneyWiseAnalysisLoanBucketList
            extends MoneyWiseAnalysisAccountBucketList<MoneyWiseAnalysisLoanBucket, MoneyWiseLoan> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisLoanBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisLoanBucketList.class);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisLoanBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        MoneyWiseAnalysisLoanBucketList(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisLoanBucketList pBase) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase);
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseAnalysisLoanBucketList(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisLoanBucketList pBase,
                                        final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pDate);
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        MoneyWiseAnalysisLoanBucketList(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisLoanBucketList pBase,
                                        final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisLoanBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching LoanBucket.
         * @param pLoan the loan
         * @return the matching bucket
         */
        public MoneyWiseAnalysisLoanBucket getMatchingLoan(final MoneyWiseLoan pLoan) {
            /* Return the matching loan if it exists else an orphan bucket */
            final MoneyWiseAnalysisLoanBucket myLoan = findItemById(pLoan.getIndexedId());
            return myLoan != null
                    ? myLoan
                    : new MoneyWiseAnalysisLoanBucket(getAnalysis(), pLoan);
        }

        /**
         * Obtain the default Cash.
         * @return the bucket
         */
        public MoneyWiseAnalysisLoanBucket getDefaultLoan() {
            /* Return the first loan in the list if it exists */
            return isEmpty()
                    ? null
                    : getUnderlyingList().get(0);
        }

        /**
         * Obtain the default Loan for the category.
         * @param pCategory the category
         * @return the bucket
         */
        public MoneyWiseAnalysisLoanBucket getDefaultLoan(final MoneyWiseLoanCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<MoneyWiseAnalysisLoanBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseAnalysisLoanBucket myBucket = myIterator.next();

                    /* Return if correct category */
                    if (MetisDataDifference.isEqual(pCategory, myBucket.getCategory())) {
                        return myBucket;
                    }
                }
            }

            /* No default loan */
            return null;
        }

        @Override
        protected MoneyWiseAnalysisLoanBucket newBucket(final MoneyWiseLoan pLoan) {
            return new MoneyWiseAnalysisLoanBucket(getAnalysis(), pLoan);
        }

        @Override
        protected MoneyWiseAnalysisLoanBucket newBucket(final MoneyWiseAnalysisLoanBucket pBase) {
            return new MoneyWiseAnalysisLoanBucket(getAnalysis(), pBase);
        }

        @Override
        protected MoneyWiseAnalysisLoanBucket newBucket(final MoneyWiseAnalysisLoanBucket pBase,
                                                        final OceanusDate pDate) {
            return new MoneyWiseAnalysisLoanBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected MoneyWiseAnalysisLoanBucket newBucket(final MoneyWiseAnalysisLoanBucket pBase,
                                                        final OceanusDateRange pRange) {
            return new MoneyWiseAnalysisLoanBucket(getAnalysis(), pBase, pRange);
        }
    }
}
