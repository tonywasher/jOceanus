/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate.DepositRateDataMap;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The Deposit Bucket class.
 */
public final class DepositBucket
        extends AccountBucket<Deposit> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.DEPOSIT_NAME.getValue(), AccountBucket.FIELD_DEFS);

    /**
     * Deposit Category Field Id.
     */
    private static final MetisField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.DEPOSITCATEGORY.getItemName());

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The deposit category.
     */
    private final DepositCategory theCategory;

    /**
     * Is this a peer2peer?
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
    private DepositBucket(final Analysis pAnalysis,
                          final DepositBucket pBase,
                          final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theAnalysis = pAnalysis;
        theCategory = pBase.getCategory();
        isPeer2Peer = pBase.isPeer2Peer();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
     * Is this a peer2peer?
     * @return true/false
     */
    public Boolean isPeer2Peer() {
        return isPeer2Peer;
    }

    @Override
    protected AccountValues allocateStandardValues(final Currency pCurrency) {
        return getAccount().isDepositClass(DepositCategoryClass.PEER2PEER)
                                                                           ? new Peer2PeerValues(pCurrency)
                                                                           : super.allocateStandardValues(pCurrency);
    }

    @Override
    protected AccountValues allocateForeignValues(final Currency pCurrency,
                                                  final Currency pReportingCurrency) {
        return getAccount().isDepositClass(DepositCategoryClass.PEER2PEER)
                                                                           ? new Peer2PeerValues(pCurrency, pReportingCurrency)
                                                                           : super.allocateForeignValues(pCurrency, pReportingCurrency);
    }

    @Override
    protected void recordRate(final TethysDate pDate) {
        /* Obtain the appropriate rate record */
        MoneyWiseData myData = theAnalysis.getData();
        DepositRateDataMap myRateMap = myData.getDepositRateDataMap();
        Deposit myDeposit = getAccount();
        DepositRate myRate = myRateMap.getRateForDate(myDeposit, pDate);
        TethysDate myDate = myDeposit.getMaturity();

        /* If we have a rate */
        if (myRate != null) {
            /* Use Rate date instead */
            if (myDate == null) {
                myDate = myRate.getDate();
            }

            /* Store the rate */
            setValue(AccountAttribute.DEPOSITRATE, myRate.getRate());
        }

        /* Store the maturity */
        if (myDate != null) {
            setValue(AccountAttribute.MATURITY, myDate);
        }
    }

    @Override
    protected void adjustForDebit(final TransactionHelper pHelper) {
        /* If this is a peer2peer and a bad debt transaction */
        if (isPeer2Peer
            && pHelper.isCategoryClass(TransactionCategoryClass.BADDEBT)) {
            /* Access the amount */
            TethysMoney myAmount = pHelper.getDebitAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust counter */
                adjustCounter(AccountAttribute.BADDEBT, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForDebit(pHelper);
    }

    @Override
    protected void adjustForCredit(final TransactionHelper pHelper) {
        /* If this is a peer2peer and a bad debt transaction */
        if (isPeer2Peer
            && pHelper.isCategoryClass(TransactionCategoryClass.BADDEBT)) {
            /* Access the amount */
            TethysMoney myAmount = pHelper.getCreditAmount();

            /* If we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* Adjust bad debt */
                myAmount = new TethysMoney(myAmount);
                myAmount.negate();
                adjustCounter(AccountAttribute.BADDEBT, myAmount);
            }
        }

        /* Pass call on */
        super.adjustForCredit(pHelper);
    }

    /**
     * Peer2PeerValues class.
     */
    public static final class Peer2PeerValues
            extends AccountValues {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5567633911931855603L;

        /**
         * Constructor.
         * @param pCurrency the account currency
         */
        private Peer2PeerValues(final Currency pCurrency) {
            /* Initialise class */
            super(pCurrency);

            /* Initialise BadDebt to zero */
            put(AccountAttribute.BADDEBT, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pCurrency the account currency
         * @param pReportingCurrency the reporting currency
         */
        private Peer2PeerValues(final Currency pCurrency,
                                final Currency pReportingCurrency) {
            /* Initialise class */
            super(pCurrency, pReportingCurrency);

            /* Initialise BadDebt to zero */
            put(AccountAttribute.BADDEBT, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private Peer2PeerValues(final Peer2PeerValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected Peer2PeerValues getSnapShot() {
            return new Peer2PeerValues(this);
        }
    }

    /**
     * DepositBucket list class.
     */
    public static class DepositBucketList
            extends AccountBucketList<DepositBucket, Deposit> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.DEPOSIT_LIST.getValue(), AccountBucketList.FIELD_DEFS);

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
        protected DepositBucketList(final Analysis pAnalysis,
                                    final DepositBucketList pBase,
                                    final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Obtain an orphan DepositBucket for a given deposit account.
         * @param pDeposit the deposit account
         * @return the bucket
         */
        public DepositBucket getOrphanBucket(final Deposit pDeposit) {
            /* Allocate an orphan bucket */
            return newBucket(pDeposit);
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
                                          final TethysDate pDate) {
            return new DepositBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected DepositBucket newBucket(final DepositBucket pBase,
                                          final TethysDateRange pRange) {
            return new DepositBucket(getAnalysis(), pBase, pRange);
        }
    }
}
