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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;

/**
 * The Loan Bucket class.
 */
public final class MoneyWiseXAnalysisLoanBucket
        extends MoneyWiseXAnalysisAccountBucket<MoneyWiseLoan> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisLoanBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisLoanBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseXAnalysisLoanBucket::getCategory);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.LOAN_CREDITCARD, MoneyWiseXAnalysisLoanBucket::isCreditCard);
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
    private MoneyWiseXAnalysisLoanBucket(final MoneyWiseXAnalysis pAnalysis,
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
    private MoneyWiseXAnalysisLoanBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisLoanBucket pBase) {
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
    private MoneyWiseXAnalysisLoanBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisLoanBucket pBase,
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
    private MoneyWiseXAnalysisLoanBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisLoanBucket pBase,
                                         final OceanusDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        isCreditCard = pBase.isCreditCard();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisLoanBucket> getDataFieldSet() {
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
    public Long getBucketId() {
        return getAccount().getExternalId();
    }

    /**
     * LoanBucket list class.
     */
    public static final class MoneyWiseXAnalysisLoanBucketList
            extends MoneyWiseXAnalysisAccountBucketList<MoneyWiseXAnalysisLoanBucket, MoneyWiseLoan> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisLoanBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisLoanBucketList.class);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisLoanBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisLoanBucketList(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisLoanBucketList pBase,
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
        MoneyWiseXAnalysisLoanBucketList(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisLoanBucketList pBase,
                                         final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisLoanBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching LoanBucket.
         * @param pLoan the loan
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisLoanBucket getMatchingLoan(final MoneyWiseLoan pLoan) {
            /* Return the matching loan if it exists else an orphan bucket */
            final MoneyWiseXAnalysisLoanBucket myLoan = findItemById(pLoan.getIndexedId());
            return myLoan != null
                    ? myLoan
                    : new MoneyWiseXAnalysisLoanBucket(getAnalysis(), pLoan);
        }

        /**
         * Obtain the default Cash.
         * @return the bucket
         */
        public MoneyWiseXAnalysisLoanBucket getDefaultLoan() {
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
        public MoneyWiseXAnalysisLoanBucket getDefaultLoan(final MoneyWiseLoanCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<MoneyWiseXAnalysisLoanBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseXAnalysisLoanBucket myBucket = myIterator.next();

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
        protected MoneyWiseXAnalysisLoanBucket newBucket(final MoneyWiseLoan pLoan) {
            return new MoneyWiseXAnalysisLoanBucket(getAnalysis(), pLoan);
        }

        @Override
        protected MoneyWiseXAnalysisLoanBucket newBucket(final MoneyWiseXAnalysisLoanBucket pBase) {
            return new MoneyWiseXAnalysisLoanBucket(getAnalysis(), pBase);
        }

        @Override
        protected MoneyWiseXAnalysisLoanBucket newBucket(final MoneyWiseXAnalysisLoanBucket pBase,
                                                         final OceanusDate pDate) {
            return new MoneyWiseXAnalysisLoanBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected MoneyWiseXAnalysisLoanBucket newBucket(final MoneyWiseXAnalysisLoanBucket pBase,
                                                         final OceanusDateRange pRange) {
            return new MoneyWiseXAnalysisLoanBucket(getAnalysis(), pBase, pRange);
        }
    }
}
