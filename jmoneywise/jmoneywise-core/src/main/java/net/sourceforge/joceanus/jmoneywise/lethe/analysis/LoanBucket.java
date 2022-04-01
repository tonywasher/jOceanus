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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Currency;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The Loan Bucket class.
 */
public final class LoanBucket
        extends AccountBucket<Loan> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<LoanBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.LOANCATEGORY, LoanBucket::getCategory);
        FIELD_DEFS.declareLocalField(AnalysisResource.LOAN_CREDITCARD, LoanBucket::isCreditCard);
    }

    /**
     * The loan category.
     */
    private final LoanCategory theCategory;

    /**
     * Is this a creditCard?
     */
    private final Boolean isCreditCard;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pLoan the loan
     */
    protected LoanBucket(final Analysis pAnalysis,
                         final Loan pLoan) {
        /* Call super-constructor */
        super(pAnalysis, pLoan);

        /* Obtain category */
        theCategory = pLoan.getCategory();

        /* Determine whether this is a credit card */
        isCreditCard = theCategory.isCategoryClass(LoanCategoryClass.CREDITCARD);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private LoanBucket(final Analysis pAnalysis,
                       final LoanBucket pBase) {
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
    private LoanBucket(final Analysis pAnalysis,
                       final LoanBucket pBase,
                       final TethysDate pDate) {
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
    private LoanBucket(final Analysis pAnalysis,
                       final LoanBucket pBase,
                       final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        isCreditCard = pBase.isCreditCard();
    }

    @Override
    public MetisFieldSet<LoanBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the loan category.
     * @return the loan category
     */
    public LoanCategory getCategory() {
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
    protected AccountValues allocateStandardValues(final Currency pCurrency) {
        return getAccount().isLoanClass(LoanCategoryClass.CREDITCARD)
                                                                      ? new CreditCardValues(pCurrency)
                                                                      : super.allocateStandardValues(pCurrency);
    }

    @Override
    protected AccountValues allocateForeignValues(final Currency pCurrency,
                                                  final Currency pReportingCurrency) {
        return getAccount().isLoanClass(LoanCategoryClass.CREDITCARD)
                                                                      ? new CreditCardValues(pCurrency, pReportingCurrency)
                                                                      : super.allocateForeignValues(pCurrency, pReportingCurrency);
    }

    @Override
    protected void adjustForDebit(final TransactionHelper pHelper) {
        /* If this is a credit card */
        if (isCreditCard) {
            /* Access the amount */
            final TethysMoney myAmount = pHelper.getDebitAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust spend */
                adjustCounter(AccountAttribute.SPEND, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pHelper);
    }

    /**
     * CreditCardValues class.
     */
    public static final class CreditCardValues
            extends AccountValues {
        /**
         * Constructor.
         * @param pCurrency the account currency
         */
        private CreditCardValues(final Currency pCurrency) {
            /* Initialise class */
            super(pCurrency);

            /* Initialise spend to zero */
            setValue(AccountAttribute.SPEND, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pCurrency the account currency
         * @param pReportingCurrency the reporting currency
         */
        private CreditCardValues(final Currency pCurrency,
                                 final Currency pReportingCurrency) {
            /* Initialise class */
            super(pCurrency, pReportingCurrency);

            /* Initialise spend to zero */
            setValue(AccountAttribute.SPEND, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        private CreditCardValues(final CreditCardValues pSource,
                                 final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected CreditCardValues getCounterSnapShot() {
            return new CreditCardValues(this, true);
        }

        @Override
        protected CreditCardValues getFullSnapShot() {
            return new CreditCardValues(this, false);
        }

        @Override
        protected void adjustToBaseValues(final AccountValues pBase) {
            /* Adjust spend values */
            adjustMoneyToBase(pBase, AccountAttribute.SPEND);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset spend values */
            TethysMoney mySpend = getMoneyValue(AccountAttribute.SPEND);
            if (mySpend.isNonZero()) {
                mySpend = new TethysMoney(mySpend);
                mySpend.setZero();
                setValue(AccountAttribute.SPEND, mySpend);
            }
        }
    }

    /**
     * LoanBucket list class.
     */
    public static class LoanBucketList
            extends AccountBucketList<LoanBucket, Loan> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<LoanBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanBucketList.class);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected LoanBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        protected LoanBucketList(final Analysis pAnalysis,
                                 final LoanBucketList pBase) {
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
        protected LoanBucketList(final Analysis pAnalysis,
                                 final LoanBucketList pBase,
                                 final TethysDate pDate) {
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
        protected LoanBucketList(final Analysis pAnalysis,
                                 final LoanBucketList pBase,
                                 final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<LoanBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching LoanBucket.
         * @param pLoan the loan
         * @return the matching bucket
         */
        public LoanBucket getMatchingLoan(final Loan pLoan) {
            /* Return the matching loan if it exists else an orphan bucket */
            final LoanBucket myLoan = findItemById(pLoan.getIndexedId());
            return myLoan != null
                                  ? myLoan
                                  : new LoanBucket(getAnalysis(), pLoan);
        }

        /**
         * Obtain the default Cash.
         * @return the bucket
         */
        public LoanBucket getDefaultLoan() {
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
        public LoanBucket getDefaultLoan(final LoanCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<LoanBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final LoanBucket myBucket = myIterator.next();

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
        protected LoanBucket newBucket(final Loan pLoan) {
            return new LoanBucket(getAnalysis(), pLoan);
        }

        @Override
        protected LoanBucket newBucket(final LoanBucket pBase) {
            return new LoanBucket(getAnalysis(), pBase);
        }

        @Override
        protected LoanBucket newBucket(final LoanBucket pBase,
                                       final TethysDate pDate) {
            return new LoanBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected LoanBucket newBucket(final LoanBucket pBase,
                                       final TethysDateRange pRange) {
            return new LoanBucket(getAnalysis(), pBase, pRange);
        }
    }
}
