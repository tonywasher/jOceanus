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
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Chargeable Gains Slice record.
 */
public class MoneyWiseChargeableGainSlice
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseChargeableGainSlice> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseChargeableGainSlice.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseChargeableGainSlice::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_GAIN, MoneyWiseChargeableGainSlice::getGain);
        FIELD_DEFS.declareLocalField(StaticDataResource.TRANSINFO_QUALYEARS, MoneyWiseChargeableGainSlice::getYears);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_SLICE, MoneyWiseChargeableGainSlice::getSlice);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSACTION, MoneyWiseChargeableGainSlice::getTransaction);
    }

    /**
     * The Date.
     */
    private final TethysDate theDate;

    /**
     * The Gain.
     */
    private final TethysMoney theGain;

    /**
     * The Years.
     */
    private final Integer theYears;

    /**
     * The Slice.
     */
    private final TethysMoney theSlice;

    /**
     * The Transaction.
     */
    private final Transaction theTrans;

    /**
     * Constructor.
     * @param pTrans the transaction
     * @param pGain the gain
     */
    public MoneyWiseChargeableGainSlice(final Transaction pTrans,
                                        final TethysMoney pGain) {
        /* Store the parameters */
        theDate = pTrans.getDate();
        theGain = pGain;
        theTrans = pTrans;
        theYears = pTrans.getYears();

        /* Calculate slice */
        theSlice = new TethysMoney(pGain);
        theSlice.divide(theYears);
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain the gain.
     * @return the gain
     */
    public TethysMoney getGain() {
        return theGain;
    }

    /**
     * Obtain the years.
     * @return the years
     */
    public Integer getYears() {
        return theYears;
    }

    /**
     * Obtain the slice.
     * @return the slice
     */
    public TethysMoney getSlice() {
        return theSlice;
    }

    /**
     * Obtain the transaction.
     * @return the transaction
     */
    public Transaction getTransaction() {
        return theTrans;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
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
         * @param pSource the source list.
         * @param pRange the range of events to copy
         */
        public MoneyWiseChargeableGainSliceList(final MoneyWiseChargeableGainSliceList pSource,
                                                final TethysDateRange pRange) {
            /* Call standard constructor */
            this();

            /* Loop through the source */
            final Iterator<MoneyWiseChargeableGainSlice> myIterator = pSource.theSlices.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseChargeableGainSlice mySlice = myIterator.next();

                /* Check the range */
                final int iDiff = pRange.compareTo(mySlice.getDate());

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
        public String formatObject(final MetisDataFormatter pFormatter) {
            return MoneyWiseChargeableGainSliceList.class.getSimpleName();
        }

        /**
         * Add Chargeable Transaction to List.
         * @param pTrans the base transaction
         * @param pGains the gains
         */
        public void addTransaction(final Transaction pTrans,
                                   final TethysMoney pGains) {
            /* Create the chargeable event */
            final MoneyWiseChargeableGainSlice mySlice = new MoneyWiseChargeableGainSlice(pTrans, pGains);

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
