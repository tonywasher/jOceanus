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
package net.sourceforge.joceanus.jmoneywise.data.analysis.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * The Cash Bucket class.
 */
public final class MoneyWiseAnalysisCashBucket
        extends MoneyWiseAnalysisAccountBucket<MoneyWiseCash> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisCashBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisCashBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseAnalysisCashBucket::getCategory);
    }

    /**
     * The cash category.
     */
    private final MoneyWiseCashCategory theCategory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCash the cash account
     */
    protected MoneyWiseAnalysisCashBucket(final MoneyWiseAnalysis pAnalysis,
                                          final MoneyWiseCash pCash) {
        /* Call super-constructor */
        super(pAnalysis, pCash);

        /* Obtain category */
        theCategory = pCash.getCategory();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     */
    private MoneyWiseAnalysisCashBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisCashBucket pBase) {
        /* Call super-constructor */
        super(pAnalysis, pBase);

        /* Copy details from base */
        theCategory = pBase.getCategory();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private MoneyWiseAnalysisCashBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisCashBucket pBase,
                                        final TethysDate pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);

        /* Copy details from base */
        theCategory = pBase.getCategory();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private MoneyWiseAnalysisCashBucket(final MoneyWiseAnalysis pAnalysis,
                                        final MoneyWiseAnalysisCashBucket pBase,
                                        final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisCashBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the cash category.
     * @return the cash category
     */
    public MoneyWiseCashCategory getCategory() {
        return theCategory;
    }

    /**
     * CashBucket list class.
     */
    public static class MoneyWiseAnalysisCashBucketList
            extends MoneyWiseAnalysisAccountBucketList<MoneyWiseAnalysisCashBucket, MoneyWiseCash> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisCashBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisCashBucketList.class);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected MoneyWiseAnalysisCashBucketList(final MoneyWiseAnalysis pAnalysis) {
            /* Initialise class */
            super(pAnalysis);
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        protected MoneyWiseAnalysisCashBucketList(final MoneyWiseAnalysis pAnalysis,
                                                  final MoneyWiseAnalysisCashBucketList pBase) {
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
        protected MoneyWiseAnalysisCashBucketList(final MoneyWiseAnalysis pAnalysis,
                                                  final MoneyWiseAnalysisCashBucketList pBase,
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
        protected MoneyWiseAnalysisCashBucketList(final MoneyWiseAnalysis pAnalysis,
                                                  final MoneyWiseAnalysisCashBucketList pBase,
                                                  final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis);

            /* Construct list from base */
            constructFromBase(pBase, pRange);
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisCashBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the matching CashBucket.
         * @param pCash the cash
         * @return the matching bucket
         */
        public MoneyWiseAnalysisCashBucket getMatchingCash(final MoneyWiseCash pCash) {
            /* Return the matching cash if it exists else an orphan bucket */
            final MoneyWiseAnalysisCashBucket myCash = findItemById(pCash.getIndexedId());
            return myCash != null
                    ? myCash
                    : new MoneyWiseAnalysisCashBucket(getAnalysis(), pCash);
        }

        /**
         * Obtain the default Cash.
         * @return the bucket
         */
        public MoneyWiseAnalysisCashBucket getDefaultCash() {
            /* Return the first cash in the list if it exists */
            return isEmpty()
                    ? null
                    : getUnderlyingList().get(0);
        }

        /**
         * Obtain the default Cash for the category.
         * @param pCategory the category
         * @return the bucket
         */
        public MoneyWiseAnalysisCashBucket getDefaultCash(final MoneyWiseCashCategory pCategory) {
            /* If there is a category */
            if (pCategory != null) {
                /* Loop through the available account values */
                final Iterator<MoneyWiseAnalysisCashBucket> myIterator = iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseAnalysisCashBucket myBucket = myIterator.next();

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
        protected MoneyWiseAnalysisCashBucket newBucket(final MoneyWiseCash pCash) {
            return new MoneyWiseAnalysisCashBucket(getAnalysis(), pCash);
        }

        @Override
        protected MoneyWiseAnalysisCashBucket newBucket(final MoneyWiseAnalysisCashBucket pBase) {
            return new MoneyWiseAnalysisCashBucket(getAnalysis(), pBase);
        }

        @Override
        protected MoneyWiseAnalysisCashBucket newBucket(final MoneyWiseAnalysisCashBucket pBase,
                                                        final TethysDate pDate) {
            return new MoneyWiseAnalysisCashBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected MoneyWiseAnalysisCashBucket newBucket(final MoneyWiseAnalysisCashBucket pBase,
                                                        final TethysDateRange pRange) {
            return new MoneyWiseAnalysisCashBucket(getAnalysis(), pBase, pRange);
        }
    }
}
