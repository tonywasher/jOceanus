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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * The Deposit Bucket class.
 */
public final class MoneyWiseXAnalysisDepositBucket
        extends MoneyWiseXAnalysisAccountBucket<MoneyWiseDeposit> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisDepositBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisDepositBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseXAnalysisDepositBucket::getCategory);
    }

    /**
     * The deposit category.
     */
    private final MoneyWiseDepositCategory theCategory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pDeposit the deposit
     */
    private MoneyWiseXAnalysisDepositBucket(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseDeposit pDeposit) {
        /* Call super-constructor */
        super(pAnalysis, pDeposit);

        /* Obtain category */
        theCategory = pDeposit.getCategory();
        recordMaturity();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private MoneyWiseXAnalysisDepositBucket(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseXAnalysisDepositBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);

        /* Copy details from base */
        theCategory = pBase.getCategory();
        recordMaturity();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseXAnalysisDepositBucket(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseXAnalysisDepositBucket pBase,
                                            final TethysDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);

        /* Obtain category */
        theCategory = pBase.getCategory();
        recordMaturity();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private MoneyWiseXAnalysisDepositBucket(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseXAnalysisDepositBucket pBase,
                                            final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();

        /* Record initial depositRate and Maturity */
        recordDepositRate();
        recordMaturity();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisDepositBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the deposit category.
     * @return the deposit category
     */
    public MoneyWiseDepositCategory getCategory() {
        return theCategory;
    }

    @Override
    public Long getBucketId() {
        return getAccount().getExternalId();
    }

    /**
     * DepositBucket list class.
     */
    public static final class MoneyWiseXAnalysisDepositBucketList
            extends MoneyWiseXAnalysisAccountBucketList<MoneyWiseXAnalysisDepositBucket, MoneyWiseDeposit> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisDepositBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisDepositBucketList.class);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisDepositBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        MoneyWiseXAnalysisDepositBucketList(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseXAnalysisDepositBucketList pBase,
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
        MoneyWiseXAnalysisDepositBucketList(final MoneyWiseXAnalysis pAnalysis,
                                            final MoneyWiseXAnalysisDepositBucketList pBase,
                                            final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisDepositBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching DepositBucket.
         * @param pDeposit the deposit
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisDepositBucket getMatchingDeposit(final MoneyWiseDeposit pDeposit) {
            /* Return the matching deposit if it exists else an orphan bucket */
            final MoneyWiseXAnalysisDepositBucket myDeposit = findItemById(pDeposit.getIndexedId());
            return myDeposit != null
                    ? myDeposit
                    : new MoneyWiseXAnalysisDepositBucket(getAnalysis(), pDeposit);
        }

        /**
         * Obtain the default Deposit.
         * @return the bucket
         */
        public MoneyWiseXAnalysisDepositBucket getDefaultDeposit() {
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
        public MoneyWiseXAnalysisDepositBucket getDefaultDeposit(final MoneyWiseDepositCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<MoneyWiseXAnalysisDepositBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseXAnalysisDepositBucket myBucket = myIterator.next();

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
        public MoneyWiseXAnalysisDepositBucket getOrphanBucket(final MoneyWiseDeposit pDeposit) {
            /* Allocate an orphan bucket */
            return newBucket(pDeposit);
        }

        @Override
        protected MoneyWiseXAnalysisDepositBucket newBucket(final MoneyWiseDeposit pDeposit) {
            return new MoneyWiseXAnalysisDepositBucket(getAnalysis(), pDeposit);
        }

        @Override
        protected MoneyWiseXAnalysisDepositBucket newBucket(final MoneyWiseXAnalysisDepositBucket pBase) {
            return new MoneyWiseXAnalysisDepositBucket(getAnalysis(), pBase);
        }

        @Override
        protected MoneyWiseXAnalysisDepositBucket newBucket(final MoneyWiseXAnalysisDepositBucket pBase,
                                                            final TethysDate pDate) {
            return new MoneyWiseXAnalysisDepositBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected MoneyWiseXAnalysisDepositBucket newBucket(final MoneyWiseXAnalysisDepositBucket pBase,
                                                            final TethysDateRange pRange) {
            return new MoneyWiseXAnalysisDepositBucket(getAnalysis(), pBase, pRange);
        }
    }
}
