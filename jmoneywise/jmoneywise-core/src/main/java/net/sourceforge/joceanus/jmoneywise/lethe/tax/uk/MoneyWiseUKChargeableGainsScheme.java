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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseChargeableGainSlice;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * Taxable Gains Scheme.
 */
public class MoneyWiseUKChargeableGainsScheme
        extends MoneyWiseUKIncomeScheme {
    /**
     * Constructor.
     */
    protected MoneyWiseUKChargeableGainsScheme() {
        super(Boolean.FALSE);
    }

    /**
     * Chargeable Gains Sliced Tax Due.
     */
    public static class MoneyWiseUKSlicedTaxDueBucket
            extends MoneyWiseTaxDueBucket {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseUKSlicedTaxDueBucket.class, MoneyWiseTaxDueBucket.getBaseFieldSet());

        /**
         * Total Gains Field Id.
         */
        private static final MetisDataField FIELD_TOTALGAIN = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TOTALGAINS);

        /**
         * Total Slices Field Id.
         */
        private static final MetisDataField FIELD_TOTALSLICES = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TOTALGAINS);

        /**
         * Ratio Field Id.
         */
        private static final MetisDataField FIELD_RATIO = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_RATIO);

        /**
         * TaxedSlices Field Id.
         */
        private static final MetisDataField FIELD_TAXEDSLICES = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TAXEDSLICES);

        /**
         * NettTaxDue Field Id.
         */
        private static final MetisDataField FIELD_NETTTAXDUE = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_NETTTAXDUE);

        /**
         * TaxRelief Field Id.
         */
        private static final MetisDataField FIELD_TAXRELIEF = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TAXRELIEF);

        /**
         * The total gains.
         */
        private final TethysMoney theTotalGains;

        /**
         * The total slices.
         */
        private final TethysMoney theTotalSlices;

        /**
         * The ratio.
         */
        private final TethysRatio theRatio;

        /**
         * The slices taxDueBucket.
         */
        private final MoneyWiseTaxDueBucket theSliceBucket;

        /**
         * The taxRelief.
         */
        private final TethysMoney theTaxRelief;

        /**
         * The nett taxDue.
         */
        private final TethysMoney theNettTaxDue;

        /**
         * Constructor.
         * @param pBase the underlying bucket
         * @param pSource the tax source
         */
        protected MoneyWiseUKSlicedTaxDueBucket(final MoneyWiseTaxDueBucket pBase,
                                                final MoneyWiseTaxSource pSource) {
            /* Initialise underlying bucket */
            super(pBase);

            /* Create the totals */
            final TethysMoney myZero = new TethysMoney(pBase.getTaxDue());
            theTotalGains = new TethysMoney(myZero);
            theTotalSlices = new TethysMoney(myZero);
            theTaxRelief = new TethysMoney(myZero);
            theNettTaxDue = new TethysMoney(myZero);

            /* Process the slices */
            final Iterator<MoneyWiseChargeableGainSlice> myIterator = pSource.getGainSlices().getUnderlyingList().iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseChargeableGainSlice mySlice = myIterator.next();
                processSlice(mySlice);
            }

            /* Calculate the ratio */
            theRatio = new TethysRatio(theTotalGains, theTotalSlices);

            /* Determine tax due on slices */
            theSliceBucket = buildSliceBucket();
            calculateTax();
        }

        /**
         * Obtain the total gains.
         * @return the gains
         */
        public TethysMoney getTotalGains() {
            return theTotalGains;
        }

        /**
         * Obtain the total slices.
         * @return the slices
         */
        public TethysMoney getTotalSlices() {
            return theTotalSlices;
        }

        /**
         * Obtain the ratio.
         * @return the ratio
         */
        public TethysRatio getRatio() {
            return theRatio;
        }

        /**
         * Obtain the tax due bucket for slices.
         * @return the tax due on slices
         */
        public MoneyWiseTaxDueBucket getSliceBucket() {
            return theSliceBucket;
        }

        /**
         * Obtain the taxed slices.
         * @return the taxed slices
         */
        public MoneyWiseTaxDueBucket getTaxedSlices() {
            return theSliceBucket;
        }

        /**
         * Obtain the tax due.
         * @return the tax due
         */
        public TethysMoney getNettTaxDue() {
            return theNettTaxDue;
        }

        /**
         * Obtain the tax relief.
         * @return the tax relief
         */
        public TethysMoney getTaxRelief() {
            return theTaxRelief;
        }

        /**
         * Process slice.
         * @param pSlice the slice
         */
        private void processSlice(final MoneyWiseChargeableGainSlice pSlice) {
            /* Adjust totals */
            theTotalGains.addAmount(pSlice.getGain());
            theTotalSlices.addAmount(pSlice.getSlice());
        }

        /**
         * build slice bucket.
         * @return the slice bucket
         */
        private MoneyWiseTaxDueBucket buildSliceBucket() {
            /* Create a new taxBand set */
            final MoneyWiseTaxBandSet myTaxBands = new MoneyWiseTaxBandSet();
            final TethysMoney myRemaining = new TethysMoney(theTotalSlices);

            /* Calculate new tax allocation */
            final Iterator<MoneyWiseTaxBandBucket> myIterator = taxBandIterator();
            while (myRemaining.isNonZero()
                   && myIterator.hasNext()) {
                final MoneyWiseTaxBandBucket myBucket = myIterator.next();

                /* Determine amount in band */
                TethysMoney myAmount = MoneyWiseUKIncomeScheme.getAmountInBand(myBucket.getAmount(), myRemaining);
                myAmount = new TethysMoney(myAmount);

                /* allocate band and adjust */
                myTaxBands.addTaxBand(new MoneyWiseTaxBand(myAmount, myBucket.getRate()));
                myRemaining.subtractAmount(myAmount);
            }

            /* Create the new tax bucket */
            return new MoneyWiseTaxDueBucket(getTaxBasis(), myTaxBands, getTaxConfig());
        }

        /**
         * calculate the tax.
         */
        private void calculateTax() {
            /* Calculate tax due */
            theNettTaxDue.add(theSliceBucket.getTaxDue().valueAtRatio(theRatio));

            /* Calculate tax relief */
            theTaxRelief.addAmount(getTaxDue());
            theTaxRelief.subtractAmount(theNettTaxDue);
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_TOTALGAIN.equals(pField)) {
                return theTotalGains;
            }
            if (FIELD_TOTALSLICES.equals(pField)) {
                return theTotalSlices;
            }
            if (FIELD_RATIO.equals(pField)) {
                return theRatio;
            }
            if (FIELD_TAXEDSLICES.equals(pField)) {
                return theSliceBucket;
            }
            if (FIELD_TAXRELIEF.equals(pField)) {
                return theTaxRelief;
            }
            if (FIELD_NETTTAXDUE.equals(pField)) {
                return theNettTaxDue;
            }

            /* Not recognised */
            return MetisDataFieldValue.UNKNOWN;
        }
    }
}
