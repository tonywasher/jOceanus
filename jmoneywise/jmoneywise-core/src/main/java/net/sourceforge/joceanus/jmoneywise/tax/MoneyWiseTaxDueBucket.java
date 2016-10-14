/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.tax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Tax Due Bucket.
 */
public class MoneyWiseTaxDueBucket
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxDueBucket.class.getSimpleName());

    /**
     * TaxBasis Field Id.
     */
    private static final MetisField FIELD_TAXBASIS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getItemName());

    /**
     * TaxConfig Field Id.
     */
    private static final MetisField FIELD_TAXCONFIG = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_NAME.getValue());

    /**
     * TaxBands Field Id.
     */
    private static final MetisField FIELD_TAXBANDS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_BANDS.getValue());

    /**
     * Taxable Income Field Id.
     */
    private static final MetisField FIELD_INCOME = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_INCOME.getValue());

    /**
     * TaxDue Field Id.
     */
    private static final MetisField FIELD_TAXDUE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_TAXDUE.getValue());

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
                MoneyWiseTaxBandBucket myBucket = new MoneyWiseTaxBandBucket(myBand);
                theTaxBands.add(myBucket);
            }
        }

        /* Create the values */
        theTaxableIncome = pBands.getZeroAmount();
        theTaxDue = pBands.getZeroAmount();
        calculateTaxDue();
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
        Iterator<MoneyWiseTaxBandBucket> myIterator = taxBandIterator();
        while (myIterator.hasNext()) {
            MoneyWiseTaxBandBucket myBand = myIterator.next();

            /* Add the values */
            theTaxableIncome.addAmount(myBand.getAmount());
            theTaxDue.addAmount(myBand.getTaxDue());
        }
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_TAXBASIS.equals(pField)) {
            return theTaxBasis;
        }
        if (FIELD_TAXCONFIG.equals(pField)) {
            return theTaxConfig;
        }
        if (FIELD_TAXBANDS.equals(pField)) {
            return theTaxBands;
        }
        if (FIELD_INCOME.equals(pField)) {
            return theTaxableIncome;
        }
        if (FIELD_TAXDUE.equals(pField)) {
            return theTaxDue;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theTaxBasis);
        myBuilder.append("==>");
        myBuilder.append(theTaxDue);
        return myBuilder.toString();
    }

    /**
     * Tax Band Bucket.
     */
    public static class MoneyWiseTaxBandBucket
            implements MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxBandBucket.class.getSimpleName());

        /**
         * Amount Field Id.
         */
        private static final MetisField FIELD_AMOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_AMOUNT.getValue());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_RATE.getValue());

        /**
         * TaxDue Field Id.
         */
        private static final MetisField FIELD_TAXDUE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_TAXDUE.getValue());

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
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_RATE.equals(pField)) {
                return theRate;
            }
            if (FIELD_TAXDUE.equals(pField)) {
                return theTaxDue;
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return toString();
        }

        @Override
        public String toString() {
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theAmount);
            myBuilder.append('@');
            myBuilder.append(theRate);
            myBuilder.append("==>");
            myBuilder.append(theTaxDue);
            return myBuilder.toString();
        }
    }
}
