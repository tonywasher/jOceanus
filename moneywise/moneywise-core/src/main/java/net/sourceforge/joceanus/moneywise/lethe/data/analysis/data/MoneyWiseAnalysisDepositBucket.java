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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.util.Currency;
import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateDataMap;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.tethys.date.TethysDate;
import net.sourceforge.joceanus.tethys.date.TethysDateRange;
import net.sourceforge.joceanus.tethys.decimal.TethysMoney;

/**
 * The Deposit Bucket class.
 */
public final class MoneyWiseAnalysisDepositBucket
        extends MoneyWiseAnalysisAccountBucket<MoneyWiseDeposit> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisDepositBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisDepositBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseAnalysisDepositBucket::getCategory);
    }

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * The deposit category.
     */
    private final MoneyWiseDepositCategory theCategory;

    /**
     * Is this a peer2peer?
     */
    private final Boolean isPeer2Peer;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pDeposit the deposit
     */
    private MoneyWiseAnalysisDepositBucket(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseDeposit pDeposit) {
        /* Call super-constructor */
        super(pAnalysis, pDeposit);

        /* Obtain category */
        theAnalysis = pAnalysis;
        theCategory = pDeposit.getCategory();

        /* Determine whether this is a peer2peer */
        isPeer2Peer = theCategory.isCategoryClass(MoneyWiseDepositCategoryClass.PEER2PEER);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private MoneyWiseAnalysisDepositBucket(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisDepositBucket pBase) {
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
    private MoneyWiseAnalysisDepositBucket(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisDepositBucket pBase,
                                           final TethysDate pDate) {
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
    private MoneyWiseAnalysisDepositBucket(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisDepositBucket pBase,
                                           final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theAnalysis = pAnalysis;
        theCategory = pBase.getCategory();
        isPeer2Peer = pBase.isPeer2Peer();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisDepositBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the deposit category.
     * @return the deposit category
     */
    public MoneyWiseDepositCategory getCategory() {
        return theCategory;
    }

    /**
     * Is this a peer2peer?
     * @return true/false
     */
    public Boolean isPeer2Peer() {
        return isPeer2Peer;
    }

    @Override
    protected MoneyWiseAnalysisAccountValues allocateStandardValues(final Currency pCurrency) {
        return getAccount().isDepositClass(MoneyWiseDepositCategoryClass.PEER2PEER)
                ? new MoneyWiseAnalysisPeer2PeerValues(pCurrency)
                : super.allocateStandardValues(pCurrency);
    }

    @Override
    protected MoneyWiseAnalysisAccountValues allocateForeignValues(final Currency pCurrency,
                                                                   final Currency pReportingCurrency) {
        return getAccount().isDepositClass(MoneyWiseDepositCategoryClass.PEER2PEER)
                ? new MoneyWiseAnalysisPeer2PeerValues(pCurrency, pReportingCurrency)
                : super.allocateForeignValues(pCurrency, pReportingCurrency);
    }

    @Override
    protected void recordRate(final TethysDate pDate) {
        /* Obtain the appropriate rate record */
        final MoneyWiseDataSet myData = theAnalysis.getData();
        final MoneyWiseDepositRateDataMap myRateMap = myData.getDepositRateDataMap();
        final MoneyWiseDeposit myDeposit = getAccount();
        final MoneyWiseDepositRate myRate = myRateMap.getRateForDate(myDeposit, pDate);
        TethysDate myDate = myDeposit.getMaturity();

        /* If we have a rate */
        if (myRate != null) {
            /* Use Rate date instead */
            if (myDate == null) {
                myDate = myRate.getDate();
            }

            /* Store the rate */
            setValue(MoneyWiseAnalysisAccountAttr.DEPOSITRATE, myRate.getRate());
        }

        /* Store the maturity */
        if (myDate != null) {
            setValue(MoneyWiseAnalysisAccountAttr.MATURITY, myDate);
        }
    }

    @Override
    public void adjustForDebit(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* If this is a peer2peer and a bad debt transaction */
        if (Boolean.TRUE.equals(isPeer2Peer)
                && isBadDebt(pHelper)) {
            /* Access the amount */
            final TethysMoney myAmount = pHelper.getDebitAmount();
            final MoneyWiseAnalysisAccountAttr myAttr = badDebtAttr(pHelper);

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust counter */
                adjustCounter(myAttr, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pHelper);
    }

    @Override
    public void adjustForCredit(final MoneyWiseAnalysisTransactionHelper pHelper) {
        /* If this is a peer2peer and a bad debt transaction */
        if (Boolean.TRUE.equals(isPeer2Peer)
                && isBadDebt(pHelper)) {
            /* Access the amount */
            TethysMoney myAmount = pHelper.getCreditAmount();
            final MoneyWiseAnalysisAccountAttr myAttr = badDebtAttr(pHelper);

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust bad debt */
                myAmount = new TethysMoney(myAmount);
                myAmount.negate();
                adjustCounter(myAttr, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForCredit(pHelper);
    }

    /**
     * Is the transaction a badDebt?
     * @param pHelper the transaction helper
     * @return true/false
     */
    boolean isBadDebt(final MoneyWiseAnalysisTransactionHelper pHelper) {
        return pHelper.isCategoryClass(MoneyWiseTransCategoryClass.BADDEBTCAPITAL)
                || pHelper.isCategoryClass(MoneyWiseTransCategoryClass.BADDEBTINTEREST);
    }

    /**
     * Obtain the badDebt attribute.
     * @param pHelper the transaction helper
     * @return the attribute
     */
    MoneyWiseAnalysisAccountAttr badDebtAttr(final MoneyWiseAnalysisTransactionHelper pHelper) {
        return pHelper.isCategoryClass(MoneyWiseTransCategoryClass.BADDEBTCAPITAL)
                ? MoneyWiseAnalysisAccountAttr.BADDEBTCAPITAL
                : MoneyWiseAnalysisAccountAttr.BADDEBTINTEREST;
    }

    /**
     * Peer2PeerValues class.
     */
    public static final class MoneyWiseAnalysisPeer2PeerValues
            extends MoneyWiseAnalysisAccountValues {
        /**
         * Constructor.
         * @param pCurrency the account currency
         */
        private MoneyWiseAnalysisPeer2PeerValues(final Currency pCurrency) {
            /* Initialise class */
            super(pCurrency);

            /* Initialise BadDebt to zero */
            setValue(MoneyWiseAnalysisAccountAttr.BADDEBTCAPITAL, new TethysMoney(pCurrency));
            setValue(MoneyWiseAnalysisAccountAttr.BADDEBTINTEREST, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pCurrency the account currency
         * @param pReportingCurrency the reporting currency
         */
        private MoneyWiseAnalysisPeer2PeerValues(final Currency pCurrency,
                                                 final Currency pReportingCurrency) {
            /* Initialise class */
            super(pCurrency, pReportingCurrency);

            /* Initialise BadDebt to zero */
            setValue(MoneyWiseAnalysisAccountAttr.BADDEBTCAPITAL, new TethysMoney(pCurrency));
            setValue(MoneyWiseAnalysisAccountAttr.BADDEBTINTEREST, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        private MoneyWiseAnalysisPeer2PeerValues(final MoneyWiseAnalysisPeer2PeerValues pSource,
                                                 final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected MoneyWiseAnalysisPeer2PeerValues getCounterSnapShot() {
            return new MoneyWiseAnalysisPeer2PeerValues(this, true);
        }

        @Override
        protected MoneyWiseAnalysisPeer2PeerValues getFullSnapShot() {
            return new MoneyWiseAnalysisPeer2PeerValues(this, false);
        }
    }

    /**
     * DepositBucket list class.
     */
    public static final class MoneyWiseAnalysisDepositBucketList
            extends MoneyWiseAnalysisAccountBucketList<MoneyWiseAnalysisDepositBucket, MoneyWiseDeposit> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisDepositBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisDepositBucketList.class);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseAnalysisDepositBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        MoneyWiseAnalysisDepositBucketList(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisDepositBucketList pBase) {
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
        MoneyWiseAnalysisDepositBucketList(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisDepositBucketList pBase,
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
        MoneyWiseAnalysisDepositBucketList(final MoneyWiseAnalysis pAnalysis,
                                           final MoneyWiseAnalysisDepositBucketList pBase,
                                           final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisDepositBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching DepositBucket.
         * @param pDeposit the deposit
         * @return the matching bucket
         */
        public MoneyWiseAnalysisDepositBucket getMatchingDeposit(final MoneyWiseDeposit pDeposit) {
            /* Return the matching deposit if it exists else an orphan bucket */
            final MoneyWiseAnalysisDepositBucket myDeposit = findItemById(pDeposit.getIndexedId());
            return myDeposit != null
                    ? myDeposit
                    : new MoneyWiseAnalysisDepositBucket(getAnalysis(), pDeposit);
        }

        /**
         * Obtain the default Deposit.
         * @return the bucket
         */
        public MoneyWiseAnalysisDepositBucket getDefaultDeposit() {
            /* Return the first deposit in the list if it exists */
            return isEmpty()
                    ? null
                    : getUnderlyingList().get(0);
        }

        /**
         * Obtain the default Deposit for the category.
         * @param pCategory the category
         * @return the bucket
         */
        public MoneyWiseAnalysisDepositBucket getDefaultDeposit(final MoneyWiseDepositCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<MoneyWiseAnalysisDepositBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseAnalysisDepositBucket myBucket = myIterator.next();

                    /* Return if correct category */
                    if (MetisDataDifference.isEqual(pCategory, myBucket.getCategory())) {
                        return myBucket;
                    }
                }
            }

            /* No default deposit */
            return null;
        }

        /**
         * Obtain an orphan DepositBucket for a given deposit account.
         * @param pDeposit the deposit account
         * @return the bucket
         */
        public MoneyWiseAnalysisDepositBucket getOrphanBucket(final MoneyWiseDeposit pDeposit) {
            /* Allocate an orphan bucket */
            return newBucket(pDeposit);
        }

        @Override
        protected MoneyWiseAnalysisDepositBucket newBucket(final MoneyWiseDeposit pDeposit) {
            return new MoneyWiseAnalysisDepositBucket(getAnalysis(), pDeposit);
        }

        @Override
        protected MoneyWiseAnalysisDepositBucket newBucket(final MoneyWiseAnalysisDepositBucket pBase) {
            return new MoneyWiseAnalysisDepositBucket(getAnalysis(), pBase);
        }

        @Override
        protected MoneyWiseAnalysisDepositBucket newBucket(final MoneyWiseAnalysisDepositBucket pBase,
                                                           final TethysDate pDate) {
            return new MoneyWiseAnalysisDepositBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected MoneyWiseAnalysisDepositBucket newBucket(final MoneyWiseAnalysisDepositBucket pBase,
                                                          final TethysDateRange pRange) {
            return new MoneyWiseAnalysisDepositBucket(getAnalysis(), pBase, pRange);
        }
    }
}
