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

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Tax Due Bucket.
 */
public class MoneyWiseTaxDueBucket
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTaxDueBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxDueBucket.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TAXBASIS, MoneyWiseTaxDueBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_NAME, MoneyWiseTaxDueBucket::getTaxConfig);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_BANDS, MoneyWiseTaxDueBucket::getTaxBands);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_INCOME, MoneyWiseTaxDueBucket::getTaxableIncome);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_TAXDUE, MoneyWiseTaxDueBucket::getTaxDue);
    }

    /**
     * Tax Basis.
     */
    private final TaxBasisClass theTaxBasis;

    /**
     * Tax Configuration.
     */
    private final MoneyWiseTaxConfig theTaxConfig;

    /**
     * Tax Bands.
     */
    private final List<MoneyWiseTaxBandBucket> theTaxBands;

    /**
     * Taxable income.
     */
    private final TethysMoney theTaxableIncome;

    /**
     * Tax Due.
     */
    private final TethysMoney theTaxDue;

    /**
     * Constructor.
     * @param pBasis the tax basis
     * @param pBands the tax bands
     * @param pConfig the tax configuration
     */
    public MoneyWiseTaxDueBucket(final TaxBasisClass pBasis,
                                 final MoneyWiseTaxBandSet pBands,
                                 final MoneyWiseTaxConfig pConfig) {
        /* Store parameters */
        theTaxBasis = pBasis;
        theTaxConfig = pConfig;

        /* Allocate the tax band list */
        theTaxBands = new ArrayList<>();

        /* Loop through the taxBands */
        for (MoneyWiseTaxBand myBand : pBands) {
            /* Ignore the band if there is zero amount */
            if (myBand.getAmount().isNonZero()) {
                /* Create the tax band bucket */
                final MoneyWiseTaxBandBucket myBucket = new MoneyWiseTaxBandBucket(myBand);
                theTaxBands.add(myBucket);
            }
        }

        /* Copy the values */
        theTaxableIncome = pBands.getZeroAmount();
        theTaxDue = pBands.getZeroAmount();
        calculateTaxDue();
    }

    /**
     * Constructor.
     * @param pBase the underlying bucket
     */
    protected MoneyWiseTaxDueBucket(final MoneyWiseTaxDueBucket pBase) {
        /* Store parameters */
        theTaxBasis = pBase.getTaxBasis();
        theTaxConfig = pBase.getTaxConfig();

        /* Allocate the tax band list */
        theTaxBands = pBase.theTaxBands;

        /* Create the values */
        theTaxableIncome = pBase.getTaxableIncome();
        theTaxDue = pBase.getTaxDue();
    }

    /**
     * Obtain the taxBasis.
     * @return the basis
     */
    public TaxBasisClass getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Obtain the taxConfig.
     * @return the configuration
     */
    public MoneyWiseTaxConfig getTaxConfig() {
        return theTaxConfig;
    }

    /**
     * Obtain the taxBands.
     * @return the taxBands
     */
    private List<MoneyWiseTaxBandBucket> getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the taxBands iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseTaxBandBucket> taxBandIterator() {
        return theTaxBands.iterator();
    }

    /**
     * Obtain the taxableIncome.
     * @return the taxableIncome
     */
    public TethysMoney getTaxableIncome() {
        return theTaxableIncome;
    }

    /**
     * Obtain the taxDue.
     * @return the taxDue
     */
    public TethysMoney getTaxDue() {
        return theTaxDue;
    }

    /**
     * Calculate the taxDue.
     */
    private void calculateTaxDue() {
        /* Loop through the tax bands */
        final Iterator<MoneyWiseTaxBandBucket> myIterator = taxBandIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTaxBandBucket myBand = myIterator.next();

            /* Add the values */
            theTaxableIncome.addAmount(myBand.getAmount());
            theTaxDue.addAmount(myBand.getTaxDue());
        }
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseTaxDueBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theTaxBasis);
        myBuilder.append("==>");
        myBuilder.append(theTaxDue);
        return myBuilder.toString();
    }

    /**
     * Tax Band Bucket.
     */
    public static class MoneyWiseTaxBandBucket
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTaxBandBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxBandBucket.class);

        /**
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_AMOUNT, MoneyWiseTaxBandBucket::getAmount);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_RATE, MoneyWiseTaxBandBucket::getRate);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_TAXDUE, MoneyWiseTaxBandBucket::getTaxDue);
        }

        /**
         * Amount in Band.
         */
        private final TethysMoney theAmount;

        /**
         * Rate for Band.
         */
        private final TethysRate theRate;

        /**
         * TaxDue for Band.
         */
        private final TethysMoney theTaxDue;

        /**
         * Constructor.
         * @param pBand the tax band
         */
        protected MoneyWiseTaxBandBucket(final MoneyWiseTaxBand pBand) {
            /* Store parameters */
            theAmount = pBand.getAmount();
            theRate = pBand.getRate();

            /* Calculate the tax due */
            theTaxDue = theAmount.valueAtRate(theRate);
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public TethysRate getRate() {
            return theRate;
        }

        /**
         * Obtain the taxDue.
         * @return the taxDue
         */
        public TethysMoney getTaxDue() {
            return theTaxDue;
        }

        @Override
        public MetisFieldSet<MoneyWiseTaxBandBucket> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return toString();
        }

        @Override
        public String toString() {
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theAmount);
            myBuilder.append('@');
            myBuilder.append(theRate);
            myBuilder.append("==>");
            myBuilder.append(theTaxDue);
            return myBuilder.toString();
        }
    }
}
