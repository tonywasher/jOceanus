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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

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
     * Is this a creditCard.
     */
    private final Boolean isCreditCard;

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
     * Is this a creditCard.
     * @return true/false
     */
    public Boolean isCreditCard() {
        return isCreditCard;
    }

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
     * @param pDate the date for the bucket
     */
    private LoanBucket(final Analysis pAnalysis,
                       final LoanBucket pBase,
                       final JDateDay pDate) {
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
                       final JDateDayRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        isCreditCard = pBase.isCreditCard();
    }

    /**
     * Obtain new Spend value.
     * @return the new spend value
     */
    private JMoney getNewSpend() {
        JMoney mySpend = getValues().getMoneyValue(AccountAttribute.SPEND);
        return new JMoney(mySpend);
    }

    @Override
    protected void adjustForDebit(final Transaction pTrans) {
        /* If this is a credit card */
        if (isCreditCard) {
            /* Access the amount */
            JMoney myAmount = pTrans.getAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust spend */
                JMoney mySpend = getNewSpend();
                mySpend.addAmount(myAmount);
                setValue(AccountAttribute.SPEND, mySpend);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pTrans);
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

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected LoanBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(LoanBucket.class, pAnalysis);
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected LoanBucketList(final Analysis pAnalysis,
                                 final LoanBucketList pBase,
                                 final JDateDay pDate) {
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
                                 final JDateDayRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        protected LoanBucket newBucket(final Loan pLoan) {
            return new LoanBucket(getAnalysis(), pLoan);
        }

        @Override
        protected LoanBucket newBucket(final LoanBucket pBase,
                                       final JDateDay pDate) {
            return new LoanBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected LoanBucket newBucket(final LoanBucket pBase,
                                       final JDateDayRange pRange) {
            return new LoanBucket(getAnalysis(), pBase, pRange);
        }
    }
}
