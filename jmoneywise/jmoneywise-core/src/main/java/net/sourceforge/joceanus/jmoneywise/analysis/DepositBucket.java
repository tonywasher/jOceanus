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

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate.DepositRateDataMap;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * The Deposit Bucket class.
 */
public final class DepositBucket
        extends AccountBucket<Deposit> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.DEPOSIT_NAME.getValue(), AccountBucket.FIELD_DEFS);

    /**
     * Deposit Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.DEPOSITCATEGORY.getItemName());

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The deposit category.
     */
    private final DepositCategory theCategory;

    /**
     * Is this a peer2peer.
     */
    private final Boolean isPeer2Peer;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pDeposit the deposit
     */
    protected DepositBucket(final Analysis pAnalysis,
                            final Deposit pDeposit) {
        /* Call super-constructor */
        super(pAnalysis, pDeposit);

        /* Obtain category */
        theAnalysis = pAnalysis;
        theCategory = pDeposit.getCategory();

        /* Determine whether this is a peer2peer */
        isPeer2Peer = theCategory.isCategoryClass(DepositCategoryClass.PEER2PEER);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private DepositBucket(final Analysis pAnalysis,
                          final DepositBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);

        /* Copy details from base */
        theAnalysis = pAnalysis;
        theCategory = pBase.getCategory();
        isPeer2Peer = pBase.isPeer2Peer();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private DepositBucket(final Analysis pAnalysis,
                          final DepositBucket pBase,
                          final JDateDay pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);

        /* Obtain category */
        theAnalysis = pAnalysis;
        theCategory = pBase.getCategory();
        isPeer2Peer = pBase.isPeer2Peer();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private DepositBucket(final Analysis pAnalysis,
                          final DepositBucket pBase,
                          final JDateDayRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theAnalysis = pAnalysis;
        theCategory = pBase.getCategory();
        isPeer2Peer = pBase.isPeer2Peer();
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
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the deposit category.
     * @return the deposit category
     */
    public DepositCategory getCategory() {
        return theCategory;
    }

    /**
     * Is this a peer2peer.
     * @return true/false
     */
    public Boolean isPeer2Peer() {
        return isPeer2Peer;
    }

    /**
     * Set opening balance.
     * @param pBalance the opening balance
     */
    protected void setOpeningBalance(final JMoney pBalance) {
        JMoney myValue = getNewValuation();
        JMoney myBaseValue = getBaseValues().getMoneyValue(AccountAttribute.VALUATION);
        myValue.addAmount(pBalance);
        myBaseValue.addAmount(pBalance);
        setValue(AccountAttribute.VALUATION, myValue);
    }

    @Override
    protected void recordRate(final JDateDay pDate) {
        /* Obtain the appropriate rate record */
        MoneyWiseData myData = theAnalysis.getData();
        DepositRateDataMap myRateMap = myData.getDepositRateDataMap();
        Deposit myDeposit = getAccount();
        DepositRate myRate = myRateMap.getRateForDate(myDeposit, pDate);
        JDateDay myDate = myDeposit.getMaturity();

        /* If we have a rate */
        if (myRate != null) {
            /* Use Rate date instead */
            if (myDate == null) {
                myDate = myRate.getDate();
            }

            /* Store the rate */
            setValue(AccountAttribute.RATE, myRate.getRate());
        }

        /* Store the maturity */
        setValue(AccountAttribute.MATURITY, myDate);
    }

    /**
     * Obtain new BadDebt value.
     * @return the new badDebt value
     */
    private JMoney getNewBadDebt() {
        JMoney myBadDebt = getValues().getMoneyValue(AccountAttribute.BADDEBT);
        return new JMoney(myBadDebt);
    }

    @Override
    protected void adjustForDebit(final Transaction pTrans) {
        /* If this is a peer2peer and a bad debt transaction */
        if ((isPeer2Peer)
            && pTrans.getCategory().isCategoryClass(TransactionCategoryClass.BADDEBT)) {
            /* Access the amount */
            JMoney myAmount = pTrans.getAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust spend */
                JMoney myBadDebt = getNewBadDebt();
                myBadDebt.addAmount(myAmount);
                setValue(AccountAttribute.BADDEBT, myBadDebt);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pTrans);
    }

    @Override
    protected void adjustForCredit(final Transaction pTrans) {
        /* If this is a peer2peer and a bad debt transaction */
        if ((isPeer2Peer)
            && pTrans.getCategory().isCategoryClass(TransactionCategoryClass.BADDEBT)) {
            /* Access the amount */
            JMoney myAmount = pTrans.getAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust spend */
                JMoney myBadDebt = getNewBadDebt();
                myBadDebt.subtractAmount(myAmount);
                setValue(AccountAttribute.BADDEBT, myBadDebt);
            }
        }

        /* Pass call on */
        super.adjustForCredit(pTrans);
    }

    /**
     * DepositBucket list class.
     */
    public static class DepositBucketList
            extends AccountBucketList<DepositBucket, Deposit> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.DEPOSIT_LIST.getValue(), AccountBucketList.FIELD_DEFS);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected DepositBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(DepositBucket.class, pAnalysis);
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        protected DepositBucketList(final Analysis pAnalysis,
                                    final DepositBucketList pBase) {
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
        protected DepositBucketList(final Analysis pAnalysis,
                                    final DepositBucketList pBase,
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
        protected DepositBucketList(final Analysis pAnalysis,
                                    final DepositBucketList pBase,
                                    final JDateDayRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        protected DepositBucket newBucket(final Deposit pDeposit) {
            return new DepositBucket(getAnalysis(), pDeposit);
        }

        @Override
        protected DepositBucket newBucket(final DepositBucket pBase) {
            return new DepositBucket(getAnalysis(), pBase);
        }

        @Override
        protected DepositBucket newBucket(final DepositBucket pBase,
                                          final JDateDay pDate) {
            return new DepositBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected DepositBucket newBucket(final DepositBucket pBase,
                                          final JDateDayRange pRange) {
            return new DepositBucket(getAnalysis(), pBase, pRange);
        }
    }
}
