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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXChargeableGainSlice;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxSource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Taxable Gains Scheme.
 */
public class MoneyWiseXUKChargeableGainsScheme
        extends MoneyWiseXUKIncomeScheme {
    /**
     * Constructor.
     */
    protected MoneyWiseXUKChargeableGainsScheme() {
        super(Boolean.FALSE);
    }

    /**
     * Chargeable Gains Sliced Tax Due.
     */
    public static class MoneyWiseXUKSlicedTaxDueBucket
            extends MoneyWiseXTaxDueBucket {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKSlicedTaxDueBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKSlicedTaxDueBucket.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.CHARGEABLEGAIN_TOTALGAINS, MoneyWiseXUKSlicedTaxDueBucket::getTotalGains);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.CHARGEABLEGAIN_TOTALSLICES, MoneyWiseXUKSlicedTaxDueBucket::getTotalSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.CHARGEABLEGAIN_RATIO, MoneyWiseXUKSlicedTaxDueBucket::getRatio);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.CHARGEABLEGAIN_TAXEDSLICES, MoneyWiseXUKSlicedTaxDueBucket::getTaxedSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.CHARGEABLEGAIN_NETTTAXDUE, MoneyWiseXUKSlicedTaxDueBucket::getNettTaxDue);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.CHARGEABLEGAIN_TAXRELIEF, MoneyWiseXUKSlicedTaxDueBucket::getTaxRelief);
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
        private final MoneyWiseXTaxDueBucket theSliceBucket;

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
        protected MoneyWiseXUKSlicedTaxDueBucket(final MoneyWiseXTaxDueBucket pBase,
                                                 final MoneyWiseXTaxSource pSource) {
            /* Initialise underlying bucket */
            super(pBase);

            /* Create the totals */
            final TethysMoney myZero = new TethysMoney(pBase.getTaxDue());
            theTotalGains = new TethysMoney(myZero);
            theTotalSlices = new TethysMoney(myZero);
            theTaxRelief = new TethysMoney(myZero);
            theNettTaxDue = new TethysMoney(myZero);

            /* Process the slices */
            final Iterator<MoneyWiseXChargeableGainSlice> myIterator = pSource.getGainSlices().getUnderlyingList().iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseXChargeableGainSlice mySlice = myIterator.next();
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
        public MoneyWiseXTaxDueBucket getTaxedSlices() {
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
        private void processSlice(final MoneyWiseXChargeableGainSlice pSlice) {
            /* Adjust totals */
            theTotalGains.addAmount(pSlice.getGain());
            theTotalSlices.addAmount(pSlice.getSlice());
        }

        /**
         * build slice bucket.
         * @return the slice bucket
         */
        private MoneyWiseXTaxDueBucket buildSliceBucket() {
            /* Create a new taxBand set */
            final MoneyWiseXTaxBandSet myTaxBands = new MoneyWiseXTaxBandSet();
            final TethysMoney myRemaining = new TethysMoney(theTotalSlices);

            /* Calculate new tax allocation */
            final Iterator<MoneyWiseXTaxBandBucket> myIterator = taxBandIterator();
            while (myRemaining.isNonZero()
                   && myIterator.hasNext()) {
                final MoneyWiseXTaxBandBucket myBucket = myIterator.next();

                /* Determine amount in band */
                TethysMoney myAmount = MoneyWiseXUKIncomeScheme.getAmountInBand(myBucket.getAmount(), myRemaining);
                myAmount = new TethysMoney(myAmount);

                /* allocate band and adjust */
                myTaxBands.addTaxBand(new MoneyWiseXTaxBand(myAmount, myBucket.getRate()));
                myRemaining.subtractAmount(myAmount);
            }

            /* Create the new tax bucket */
            return new MoneyWiseXTaxDueBucket(getTaxBasis(), myTaxBands, getTaxConfig());
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
        public MetisFieldSet<MoneyWiseXUKSlicedTaxDueBucket> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
