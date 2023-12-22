/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseChargeableGainSlice;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

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
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKSlicedTaxDueBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKSlicedTaxDueBucket.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TOTALGAINS, MoneyWiseUKSlicedTaxDueBucket::getTotalGains);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TOTALSLICES, MoneyWiseUKSlicedTaxDueBucket::getTotalSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_RATIO, MoneyWiseUKSlicedTaxDueBucket::getRatio);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TAXEDSLICES, MoneyWiseUKSlicedTaxDueBucket::getTaxedSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_NETTTAXDUE, MoneyWiseUKSlicedTaxDueBucket::getNettTaxDue);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.CHARGEABLEGAIN_TAXRELIEF, MoneyWiseUKSlicedTaxDueBucket::getTaxRelief);
        }

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
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<MoneyWiseUKSlicedTaxDueBucket> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
