/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCashCategory;

import java.util.Iterator;

/**
 * The Cash Bucket class.
 */
public final class MoneyWiseXAnalysisCashBucket
        extends MoneyWiseXAnalysisAccountBucket<MoneyWiseCash> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisCashBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisCashBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseXAnalysisCashBucket::getCategory);
    }

    /**
     * The cash category.
     */
    private final MoneyWiseCashCategory theCategory;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pCash     the cash account
     */
    private MoneyWiseXAnalysisCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseCash pCash) {
        /* Call super-constructor */
        super(pAnalysis, pCash);

        /* Obtain category */
        theCategory = pCash.getCategory();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     */
    private MoneyWiseXAnalysisCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisCashBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);

        /* Copy details from base */
        theCategory = pBase.getCategory();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    private MoneyWiseXAnalysisCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisCashBucket pBase,
                                         final OceanusDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);

        /* Copy details from base */
        theCategory = pBase.getCategory();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    private MoneyWiseXAnalysisCashBucket(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisCashBucket pBase,
                                         final OceanusDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisCashBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the cash category.
     *
     * @return the cash category
     */
    public MoneyWiseCashCategory getCategory() {
        return theCategory;
    }

    @Override
    public Long getBucketId() {
        return getAccount().getExternalId();
    }

    /**
     * CashBucket list class.
     */
    public static final class MoneyWiseXAnalysisCashBucketList
            extends MoneyWiseXAnalysisAccountBucketList<MoneyWiseXAnalysisCashBucket, MoneyWiseCash> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXAnalysisCashBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisCashBucketList.class);

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         */
        MoneyWiseXAnalysisCashBucketList(final MoneyWiseXAnalysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a dated List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pDate     the Date
         */
        MoneyWiseXAnalysisCashBucketList(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisCashBucketList pBase,
                                         final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pDate);
        }

        /**
         * Construct a ranged List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pRange    the Date Range
         */
        MoneyWiseXAnalysisCashBucketList(final MoneyWiseXAnalysis pAnalysis,
                                         final MoneyWiseXAnalysisCashBucketList pBase,
                                         final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<MoneyWiseXAnalysisCashBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching CashBucket.
         *
         * @param pCash the cash
         * @return the matching bucket
         */
        public MoneyWiseXAnalysisCashBucket getMatchingCash(final MoneyWiseCash pCash) {
            /* Return the matching cash if it exists else an orphan bucket */
            final MoneyWiseXAnalysisCashBucket myCash = findItemById(pCash.getIndexedId());
            return myCash != null
                    ? myCash
                    : new MoneyWiseXAnalysisCashBucket(getAnalysis(), pCash);
        }

        /**
         * Obtain the default Cash.
         *
         * @return the bucket
         */
        public MoneyWiseXAnalysisCashBucket getDefaultCash() {
            /* Return the first cash in the list if it exists */
            return isEmpty()
                    ? null
                    : getUnderlyingList().get(0);
        }

        /**
         * Obtain the default Cash for the category.
         *
         * @param pCategory the category
         * @return the bucket
         */
        public MoneyWiseXAnalysisCashBucket getDefaultCash(final MoneyWiseCashCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<MoneyWiseXAnalysisCashBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseXAnalysisCashBucket myBucket = myIterator.next();

                    /* Return if correct category */
                    if (MetisDataDifference.isEqual(pCategory, myBucket.getCategory())) {
                        return myBucket;
                    }
                }
            }

            /* No default cash */
            return null;
        }

        @Override
        protected MoneyWiseXAnalysisCashBucket newBucket(final MoneyWiseCash pCash) {
            return new MoneyWiseXAnalysisCashBucket(getAnalysis(), pCash);
        }

        @Override
        protected MoneyWiseXAnalysisCashBucket newBucket(final MoneyWiseXAnalysisCashBucket pBase) {
            return new MoneyWiseXAnalysisCashBucket(getAnalysis(), pBase);
        }

        @Override
        protected MoneyWiseXAnalysisCashBucket newBucket(final MoneyWiseXAnalysisCashBucket pBase,
                                                         final OceanusDate pDate) {
            return new MoneyWiseXAnalysisCashBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected MoneyWiseXAnalysisCashBucket newBucket(final MoneyWiseXAnalysisCashBucket pBase,
                                                         final OceanusDateRange pRange) {
            return new MoneyWiseXAnalysisCashBucket(getAnalysis(), pBase, pRange);
        }
    }
}
