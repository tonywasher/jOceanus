/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jtethys.dateday.TethysDate;
import net.sourceforge.joceanus.jtethys.dateday.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The Loan Bucket class.
 */
public final class LoanBucket
        extends AccountBucket<Loan> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.LOAN_NAME.getValue(), AccountBucket.FIELD_DEFS);

    /**
     * Loan Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.LOANCATEGORY.getItemName());

    /**
     * IsCreditCard Field Id.
     */
    private static final JDataField FIELD_ISCREDIT = FIELD_DEFS.declareLocalField(AnalysisResource.LOAN_CREDITCARD.getValue());

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
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_ISCREDIT.equals(pField)) {
            return isCreditCard;
        }
        return super.getFieldValue(pField);
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
    protected void adjustForDebit(final TransactionHelper pTrans) {
        /* If this is a credit card */
        if (isCreditCard) {
            /* Access the amount */
            TethysMoney myAmount = pTrans.getDebitAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust spend */
                adjustCounter(AccountAttribute.SPEND, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pTrans);
    }

    /**
     * CreditCardValues class.
     */
    public static final class CreditCardValues
            extends AccountValues {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -3340482670174694323L;

        /**
         * Constructor.
         * @param pCurrency the account currency
         */
        private CreditCardValues(final Currency pCurrency) {
            /* Initialise class */
            super(pCurrency);

            /* Initialise spend to zero */
            put(AccountAttribute.SPEND, new TethysMoney(pCurrency));
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
            put(AccountAttribute.SPEND, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private CreditCardValues(final CreditCardValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected CreditCardValues getSnapShot() {
            return new CreditCardValues(this);
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
                put(AccountAttribute.SPEND, mySpend);
            }
        }
    }

    /**
     * LoanBucket list class.
     */
    public static class LoanBucketList
            extends AccountBucketList<LoanBucket, Loan>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.LOAN_LIST.getValue(), AccountBucketList.FIELD_DEFS);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected LoanBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(LoanBucket.class, pAnalysis);
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
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Obtain an orphan LoanBucket for a given loan account.
         * @param pLoan the loan account
         * @return the bucket
         */
        public LoanBucket getOrphanBucket(final Loan pLoan) {
            /* Allocate an orphan bucket */
            return newBucket(pLoan);
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
