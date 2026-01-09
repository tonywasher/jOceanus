/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.tax.uk;

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseChargeableGainSlice;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Iterator;

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
        private final OceanusMoney theTotalGains;

        /**
         * The total slices.
         */
        private final OceanusMoney theTotalSlices;

        /**
         * The ratio.
         */
        private final OceanusRatio theRatio;

        /**
         * The slices taxDueBucket.
         */
        private final MoneyWiseTaxDueBucket theSliceBucket;

        /**
         * The taxRelief.
         */
        private final OceanusMoney theTaxRelief;

        /**
         * The nett taxDue.
         */
        private final OceanusMoney theNettTaxDue;

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
            final OceanusMoney myZero = new OceanusMoney(pBase.getTaxDue());
            theTotalGains = new OceanusMoney(myZero);
            theTotalSlices = new OceanusMoney(myZero);
            theTaxRelief = new OceanusMoney(myZero);
            theNettTaxDue = new OceanusMoney(myZero);

            /* Process the slices */
            for (MoneyWiseChargeableGainSlice mySlice : pSource.getGainSlices().getUnderlyingList()) {
                processSlice(mySlice);
            }

            /* Calculate the ratio */
            theRatio = new OceanusRatio(theTotalGains, theTotalSlices);

            /* Determine tax due on slices */
            theSliceBucket = buildSliceBucket();
            calculateTax();
        }

        /**
         * Obtain the total gains.
         * @return the gains
         */
        public OceanusMoney getTotalGains() {
            return theTotalGains;
        }

        /**
         * Obtain the total slices.
         * @return the slices
         */
        public OceanusMoney getTotalSlices() {
            return theTotalSlices;
        }

        /**
         * Obtain the ratio.
         * @return the ratio
         */
        public OceanusRatio getRatio() {
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
        public OceanusMoney getNettTaxDue() {
            return theNettTaxDue;
        }

        /**
         * Obtain the tax relief.
         * @return the tax relief
         */
        public OceanusMoney getTaxRelief() {
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
            final OceanusMoney myRemaining = new OceanusMoney(theTotalSlices);

            /* Calculate new tax allocation */
            final Iterator<MoneyWiseTaxBandBucket> myIterator = taxBandIterator();
            while (myRemaining.isNonZero()
                    && myIterator.hasNext()) {
                final MoneyWiseTaxBandBucket myBucket = myIterator.next();

                /* Determine amount in band */
                OceanusMoney myAmount = getAmountInBand(myBucket.getAmount(), myRemaining);
                myAmount = new OceanusMoney(myAmount);

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
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<MoneyWiseUKSlicedTaxDueBucket> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
