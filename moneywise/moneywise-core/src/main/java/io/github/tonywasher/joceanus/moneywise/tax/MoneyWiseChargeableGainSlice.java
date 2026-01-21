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
package io.github.tonywasher.joceanus.moneywise.tax;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataList;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataObjectFormat;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Chargeable Gains Slice record.
 */
public final class MoneyWiseChargeableGainSlice
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseChargeableGainSlice> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseChargeableGainSlice.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseChargeableGainSlice::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_GAIN, MoneyWiseChargeableGainSlice::getGain);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.TRANSINFO_QUALYEARS, MoneyWiseChargeableGainSlice::getYears);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_SLICE, MoneyWiseChargeableGainSlice::getSlice);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseChargeableGainSlice::getTransaction);
    }

    /**
     * The Date.
     */
    private final OceanusDate theDate;

    /**
     * The Gain.
     */
    private final OceanusMoney theGain;

    /**
     * The Years.
     */
    private final Integer theYears;

    /**
     * The Slice.
     */
    private final OceanusMoney theSlice;

    /**
     * The Transaction.
     */
    private final MoneyWiseTransaction theTrans;

    /**
     * Constructor.
     *
     * @param pTrans the transaction
     * @param pGain  the gain
     */
    private MoneyWiseChargeableGainSlice(final MoneyWiseTransaction pTrans,
                                         final OceanusMoney pGain) {
        /* Store the parameters */
        theDate = pTrans.getDate();
        theGain = pGain;
        theTrans = pTrans;
        theYears = Objects.requireNonNull(pTrans.getYears());

        /* Calculate slice */
        theSlice = new OceanusMoney(pGain);
        theSlice.divide(theYears);
    }

    /**
     * Constructor.
     *
     * @param pTrans the transaction
     * @param pGain  the gain
     * @param pSlice the slice
     * @param pYears the years
     */
    private MoneyWiseChargeableGainSlice(final MoneyWiseTransaction pTrans,
                                         final OceanusMoney pGain,
                                         final OceanusMoney pSlice,
                                         final Integer pYears) {
        /* Store the parameters */
        theTrans = pTrans;
        theDate = theTrans.getDate();
        theGain = pGain;
        theSlice = pSlice;
        theYears = pYears;
    }

    /**
     * Obtain the date.
     *
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain the gain.
     *
     * @return the gain
     */
    public OceanusMoney getGain() {
        return theGain;
    }

    /**
     * Obtain the years.
     *
     * @return the years
     */
    public Integer getYears() {
        return theYears;
    }

    /**
     * Obtain the slice.
     *
     * @return the slice
     */
    public OceanusMoney getSlice() {
        return theSlice;
    }

    /**
     * Obtain the transaction.
     *
     * @return the transaction
     */
    public MoneyWiseTransaction getTransaction() {
        return theTrans;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFieldSet<MoneyWiseChargeableGainSlice> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * List of Gains Slices.
     */
    public static class MoneyWiseChargeableGainSliceList
            implements MetisDataObjectFormat, MetisDataList<MoneyWiseChargeableGainSlice> {
        /**
         * Gains slices.
         */
        private final List<MoneyWiseChargeableGainSlice> theSlices;

        /**
         * Constructor.
         */
        public MoneyWiseChargeableGainSliceList() {
            theSlices = new ArrayList<>();
        }

        /**
         * Constructor.
         *
         * @param pSource the source list.
         * @param pRange  the range of events to copy
         */
        public MoneyWiseChargeableGainSliceList(final MoneyWiseChargeableGainSliceList pSource,
                                                final OceanusDateRange pRange) {
            /* Call standard constructor */
            this();

            /* Loop through the source */
            for (MoneyWiseChargeableGainSlice mySlice : pSource.theSlices) {
                /* Check the range */
                final int iDiff = pRange.compareToDate(mySlice.getDate());

                /* If we are past the range, break the loop */
                if (iDiff < 0) {
                    break;
                }

                /* If we are within the range */
                if (iDiff == 0) {
                    /* Add to the list */
                    theSlices.add(mySlice);
                }
            }
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return MoneyWiseChargeableGainSliceList.class.getSimpleName();
        }

        /**
         * Add Chargeable Transaction to List.
         *
         * @param pTrans the base transaction
         * @param pGains the gains
         */
        public void addTransaction(final MoneyWiseTransaction pTrans,
                                   final OceanusMoney pGains) {
            /* Create the chargeable event */
            final MoneyWiseChargeableGainSlice mySlice = new MoneyWiseChargeableGainSlice(pTrans, pGains);

            /* Add it to the list */
            theSlices.add(mySlice);
        }

        /**
         * Add Chargeable Transaction to List.
         *
         * @param pTrans the base transaction
         * @param pGains the gains
         * @param pSlice the slice
         * @param pYears the years
         */
        public void addTransaction(final MoneyWiseTransaction pTrans,
                                   final OceanusMoney pGains,
                                   final OceanusMoney pSlice,
                                   final Integer pYears) {
            /* Create the chargeable event */
            final MoneyWiseChargeableGainSlice mySlice = new MoneyWiseChargeableGainSlice(pTrans, pGains, pSlice, pYears);

            /* Add it to the list */
            theSlices.add(mySlice);
        }

        @Override
        public List<MoneyWiseChargeableGainSlice> getUnderlyingList() {
            return theSlices;
        }

        @Override
        public boolean isEmpty() {
            return theSlices.isEmpty();
        }
    }
}
