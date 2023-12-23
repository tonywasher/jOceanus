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
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Tax Due Bucket.
 */
public class MoneyWiseXTaxDueBucket
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXTaxDueBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXTaxDueBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TAXBASIS, MoneyWiseXTaxDueBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_NAME, MoneyWiseXTaxDueBucket::getTaxConfig);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_BANDS, MoneyWiseXTaxDueBucket::getTaxBands);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_INCOME, MoneyWiseXTaxDueBucket::getTaxableIncome);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_TAXDUE, MoneyWiseXTaxDueBucket::getTaxDue);
    }

    /**
     * Tax Basis.
     */
    private final TaxBasisClass theTaxBasis;

    /**
     * Tax Configuration.
     */
    private final MoneyWiseXTaxConfig theTaxConfig;

    /**
     * Tax Bands.
     */
    private final List<MoneyWiseXTaxBandBucket> theTaxBands;

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
    public MoneyWiseXTaxDueBucket(final TaxBasisClass pBasis,
                                  final MoneyWiseXTaxBandSet pBands,
                                  final MoneyWiseXTaxConfig pConfig) {
        /* Store parameters */
        theTaxBasis = pBasis;
        theTaxConfig = pConfig;

        /* Allocate the tax band list */
        theTaxBands = new ArrayList<>();

        /* Loop through the taxBands */
        for (MoneyWiseXTaxBand myBand : pBands) {
            /* Ignore the band if there is zero amount */
            if (myBand.getAmount().isNonZero()) {
                /* Create the tax band bucket */
                final MoneyWiseXTaxBandBucket myBucket = new MoneyWiseXTaxBandBucket(myBand);
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
    protected MoneyWiseXTaxDueBucket(final MoneyWiseXTaxDueBucket pBase) {
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
    public MoneyWiseXTaxConfig getTaxConfig() {
        return theTaxConfig;
    }

    /**
     * Obtain the taxBands.
     * @return the taxBands
     */
    private List<MoneyWiseXTaxBandBucket> getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the taxBands iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseXTaxBandBucket> taxBandIterator() {
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
        final Iterator<MoneyWiseXTaxBandBucket> myIterator = taxBandIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXTaxBandBucket myBand = myIterator.next();

            /* Add the values */
            theTaxableIncome.addAmount(myBand.getAmount());
            theTaxDue.addAmount(myBand.getTaxDue());
        }
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseXTaxDueBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
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
    public static class MoneyWiseXTaxBandBucket
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXTaxBandBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXTaxBandBucket.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_AMOUNT, MoneyWiseXTaxBandBucket::getAmount);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_RATE, MoneyWiseXTaxBandBucket::getRate);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_TAXDUE, MoneyWiseXTaxBandBucket::getTaxDue);
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
        protected MoneyWiseXTaxBandBucket(final MoneyWiseXTaxBand pBand) {
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
        public MetisFieldSet<MoneyWiseXTaxBandBucket> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
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
