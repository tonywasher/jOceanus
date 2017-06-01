/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * The Cash Bucket class.
 */
public final class CashBucket
        extends AccountBucket<Cash> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.CASH_NAME.getValue(), AccountBucket.FIELD_DEFS);

    /**
     * Cash Category Field Id.
     */
    private static final MetisField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.CASHCATEGORY.getItemName());

    /**
     * The cash category.
     */
    private final CashCategory theCategory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCash the cash account
     */
    protected CashBucket(final Analysis pAnalysis,
                         final Cash pCash) {
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
    private CashBucket(final Analysis pAnalysis,
                       final CashBucket pBase) {
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
    private CashBucket(final Analysis pAnalysis,
                       final CashBucket pBase,
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
    private CashBucket(final Analysis pAnalysis,
                       final CashBucket pBase,
                       final TethysDateRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);

        /* Copy details from base */
        theCategory = pBase.getCategory();
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
     * Obtain the cash category.
     * @return the cash category
     */
    public CashCategory getCategory() {
        return theCategory;
    }

    /**
     * CashBucket list class.
     */
    public static class CashBucketList
            extends AccountBucketList<CashBucket, Cash> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.CASH_LIST.getValue(), AccountBucketList.FIELD_DEFS);

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected CashBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(CashBucket.class, pAnalysis);
        }

        /**
         * Construct a view List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         */
        protected CashBucketList(final Analysis pAnalysis,
                                 final CashBucketList pBase) {
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
        protected CashBucketList(final Analysis pAnalysis,
                                 final CashBucketList pBase,
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
        protected CashBucketList(final Analysis pAnalysis,
                                 final CashBucketList pBase,
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
         * Obtain an orphan CashBucket for a given cash account.
         * @param pCash the cash account
         * @return the bucket
         */
        public CashBucket getOrphanBucket(final Cash pCash) {
            /* Allocate an orphan bucket */
            return newBucket(pCash);
        }

        @Override
        protected CashBucket newBucket(final Cash pCash) {
            return new CashBucket(getAnalysis(), pCash);
        }

        @Override
        protected CashBucket newBucket(final CashBucket pBase) {
            return new CashBucket(getAnalysis(), pBase);
        }

        @Override
        protected CashBucket newBucket(final CashBucket pBase,
                                       final TethysDate pDate) {
            return new CashBucket(getAnalysis(), pBase, pDate);
        }

        @Override
        protected CashBucket newBucket(final CashBucket pBase,
                                       final TethysDateRange pRange) {
            return new CashBucket(getAnalysis(), pBase, pRange);
        }
    }
}
