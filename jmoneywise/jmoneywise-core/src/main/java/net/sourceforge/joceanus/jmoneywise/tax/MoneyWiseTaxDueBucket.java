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
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
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
     * TaxDue Field Id.
     */
    private static final MetisField FIELD_TAXDUE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_TAXDUE.getValue());

    /**
     * Tax Basis.
     */
    private final TaxBasisBucket theTaxBasis;

    /**
     * Tax Configuration.
     */
    private final MoneyWiseTaxConfig theTaxConfig;

    /**
     * Tax Bands.
     */
    private final List<MoneyWiseTaxBandBucket> theTaxBands;

    /**
     * Tax Due.
     */
    private final TethysMoney theTaxDue;

    /**
     * Constructor.
     * @param pConfig the tax configuration
     * @param pBasis the tax basis
     */
    public MoneyWiseTaxDueBucket(final TaxBasisBucket pBasis,
                                 final MoneyWiseTaxConfig pConfig) {
        /* Store parameters */
        theTaxBasis = pBasis;
        theTaxConfig = pConfig.cloneIt();

        /* Allocate the tax band list */
        theTaxBands = new ArrayList<>();

        /* Create the taxDue value */
        theTaxDue = new TethysMoney(pBasis.getMoneyValue(TaxBasisAttribute.GROSS));
        theTaxDue.setZero();
    }

    /**
     * Obtain the taxBasis.
     * @return the basis
     */
    public TaxBasisBucket getTaxBasis() {
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
     * Obtain the taxDue.
     * @return the taxDue
     */
    public TethysMoney getTaxDue() {
        return theTaxDue;
    }

    /**
     * Calculate the taxDue.
     */
    protected void calculateTaxDue() {
        /* Reset the tax Due */
        theTaxDue.setZero();

        /* Loop through the tax bands */
        Iterator<MoneyWiseTaxBandBucket> myIterator = taxBandIterator();
        while (myIterator.hasNext()) {
            MoneyWiseTaxBandBucket myBand = myIterator.next();

            /* Add the tax */
            theTaxDue.addAmount(myBand.getAmount());
        }
    }

    @Override
    public MetisFields getDataFields() {
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
        if (FIELD_TAXDUE.equals(pField)) {
            return theTaxDue;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
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
         * @param pAmount the amount
         * @param pRate the rate
         */
        protected MoneyWiseTaxBandBucket(final TethysMoney pAmount,
                                         final TethysRate pRate) {
            /* Store parameters */
            theAmount = pAmount;
            theRate = pRate;

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
            return FIELD_DEFS.getName();
        }
    }
}
