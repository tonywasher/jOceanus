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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Dividend Tax Scheme.
 */
public abstract class MoneyWiseUKDividendScheme
        extends MoneyWiseUKIncomeScheme
        implements MetisDataContents {
    /**
     * Is tax relief available?
     * @return true/false
     */
    public Boolean taxReliefAvailable() {
        return Boolean.TRUE;
    }

    /**
     * Obtain theTaxCredit rate for interest.
     * @param pTaxYear the taxYear
     * @return the taxCredit rate
     */
    protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
        return null;
    }

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                           final TaxBasisClass pBasis,
                                           final TethysMoney pAmount) {
        /* Adjust against the basic allowance */
        TethysMoney myRemaining = super.adjustAllowances(pConfig, pBasis, pAmount);

        /* If we have any dividends left */
        if (myRemaining.isNonZero()) {
            /* Adjust the dividend allowance noting that it still counts against the taxBand */
            adjustForAllowance(pConfig.getDividendAllowance(), myRemaining);
        }

        /* Return unallocated income */
        return myRemaining;
    }

    @Override
    protected TethysMoney getAmountInAllowance(final MoneyWiseUKTaxConfig pConfig,
                                               final TaxBasisClass pBasis,
                                               final TethysMoney pAmount) {
        /* Obtain the amount covered by the basic allowance */
        TethysMoney myAmount = super.getAmountInAllowance(pConfig, pBasis, pAmount);

        /* If we have income left over */
        if (myAmount.compareTo(pAmount) < 0) {
            /* Calculate remaining amount */
            TethysMoney myRemaining = new TethysMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by dividend allowance */
            TethysMoney myXtra = getAmountInBand(pConfig.getDividendAllowance(), myRemaining);

            /* Determine the total amount covered by the allowance */
            myAmount = new TethysMoney(myAmount);
            myAmount.addAmount(myXtra);
        }

        /* return the amount */
        return myAmount;
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseUKDividendAsIncomeScheme
            extends MoneyWiseUKDividendScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKDividendAsIncomeScheme.class.getSimpleName());

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
            return pTaxYear.getTaxBands().getTaxCreditRate();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseDividendUKBaseRateScheme
            extends MoneyWiseUKDividendScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDividendUKBaseRateScheme.class.getSimpleName());

        /**
         * Base Rate Field Id.
         */
        private static final MetisField FIELD_BASERATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_BASE_RATE.getValue());

        /**
         * Relief Available Field Id.
         */
        private static final MetisField FIELD_RELIEF = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_RELIEF_AVAILABLE.getValue());

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Tax Relief available.
         */
        private final Boolean reliefAvailable;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pReliefAvailable Is tax relief available?
         */
        protected MoneyWiseDividendUKBaseRateScheme(final TethysRate pRate,
                                                    final Boolean pReliefAvailable) {
            theBaseRate = pRate;
            reliefAvailable = pReliefAvailable;
        }

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseDividendUKBaseRateScheme(final TethysRate pRate) {
            this(pRate, Boolean.TRUE);
        }

        /**
         * Obtain the base rate.
         * @return the base rate
         */
        protected TethysRate getBaseRate() {
            return theBaseRate;
        }

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
            return theBaseRate;
        }

        @Override
        public Boolean taxReliefAvailable() {
            return reliefAvailable;
        }

        /**
         * Obtain the data fields.
         * @return the data fields
         */
        protected static MetisFields getBaseFields() {
            return FIELD_DEFS;
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_BASERATE.equals(pField)) {
                return theBaseRate;
            }
            if (FIELD_RELIEF.equals(pField)) {
                return reliefAvailable;
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }
    }

    /**
     * Higher Rate Scheme.
     */
    public static class MoneyWiseUKDividendHigherRateScheme
            extends MoneyWiseDividendUKBaseRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKDividendHigherRateScheme.class.getSimpleName(), MoneyWiseDividendUKBaseRateScheme.getBaseFields());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_HIGHRATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_HIGH_RATE.getValue());

        /**
         * The Higher Rate.
         */
        private final TethysRate theHighRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the higher rate
         */
        protected MoneyWiseUKDividendHigherRateScheme(final TethysRate pRate,
                                                      final TethysRate pHighRate) {
            super(pRate, Boolean.FALSE);
            theHighRate = pHighRate;
        }

        /**
         * Obtain the high rate.
         * @return the high rate
         */
        protected TethysRate getHighRate() {
            return theHighRate;
        }

        @Override
        public Boolean taxReliefAvailable() {
            return Boolean.FALSE;
        }

        /**
         * Obtain the data fields.
         * @return the data fields
         */
        protected static MetisFields getBaseFields() {
            return FIELD_DEFS;
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_HIGHRATE.equals(pField)) {
                return theHighRate;
            }

            /* Pass on */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }
    }

    /**
     * Additional Rate Scheme.
     */
    public static class MoneyWiseUKDividendAdditionalRateScheme
            extends MoneyWiseUKDividendHigherRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKDividendAdditionalRateScheme.class.getSimpleName(), MoneyWiseUKDividendHigherRateScheme.getBaseFields());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_ADDRATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_ADDITIONAL_RATE.getValue());

        /**
         * The Additional Rate.
         */
        private final TethysRate theAdditionalRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the higher rate
         * @param pAddRate the additional rate
         */
        protected MoneyWiseUKDividendAdditionalRateScheme(final TethysRate pRate,
                                                          final TethysRate pHighRate,
                                                          final TethysRate pAddRate) {
            super(pRate, pHighRate);
            theAdditionalRate = pAddRate;
        }

        /**
         * Obtain the additional rate.
         * @return the additional rate
         */
        protected TethysRate getAdditionalRate() {
            return theAdditionalRate;
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_ADDRATE.equals(pField)) {
                return theAdditionalRate;
            }

            /* Pass on */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }
    }
}