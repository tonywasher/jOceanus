/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Capital Gains Tax Scheme.
 */
public abstract class MoneyWiseUKCapitalScheme
        extends MoneyWiseUKIncomeScheme {
    /*
     * Local Report fields.
     */
    static {
        MetisFieldSet.newFieldSet(MoneyWiseUKCapitalScheme.class);
    }

    /**
     * Constructor.
     */
    protected MoneyWiseUKCapitalScheme() {
    }

    @Override
    protected OceanusMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                            final OceanusMoney pAmount) {
        /* Adjust against the capital allowance */
        OceanusMoney myRemaining = adjustForAllowance(pConfig.getCapitalAllowance(), pAmount);

        /* If we have any gains left */
        if (myRemaining.isNonZero()) {
            /* Adjust the basic allowance */
            myRemaining = super.adjustAllowances(pConfig, myRemaining);
        }

        /* Return unallocated income */
        return myRemaining;
    }

    @Override
    protected OceanusMoney getAmountInAllowance(final MoneyWiseUKTaxConfig pConfig,
                                                final OceanusMoney pAmount) {
        /* Obtain the amount covered by the capital allowance */
        OceanusMoney myAmount = getAmountInBand(pConfig.getCapitalAllowance(), pAmount);

        /* If we have income left over */
        if (myAmount.compareTo(pAmount) < 0) {
            /* Calculate remaining amount */
            final OceanusMoney myRemaining = new OceanusMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by basic allowance */
            final OceanusMoney myXtra = super.getAmountInAllowance(pConfig, myRemaining);

            /* Determine the total amount covered by the allowance */
            myAmount = new OceanusMoney(myAmount);
            myAmount.addAmount(myXtra);
        }

        /* return the amount */
        return myAmount;
    }

    /**
     * Flat Rate Scheme.
     */
    public static class MoneyWiseUKCapitalFlatRateScheme
            extends MoneyWiseUKCapitalScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKCapitalFlatRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKCapitalFlatRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_BASE_RATE, MoneyWiseUKCapitalFlatRateScheme::getBasicRate);
        }

        /**
         * The Base Rate.
         */
        private final OceanusRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseUKCapitalFlatRateScheme(final OceanusRate pRate) {
            theBaseRate = pRate;
        }

        /**
         * Obtain the base rate.
         * @return the base rate
         */
        protected OceanusRate getBasicRate() {
            return theBaseRate;
        }

        @Override
        public MetisFieldSet<? extends MoneyWiseUKCapitalFlatRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final MoneyWiseTaxClass pBasis) {
            /* Create a new List */
            final List<MoneyWiseTaxBand> myList = new ArrayList<>();

            /* Add the single band */
            myList.add(new MoneyWiseTaxBand(getBasicRate()));

            /* Return the iterator */
            return myList.iterator();
        }
    }

    /**
     * Split Rate Scheme.
     */
    public static class MoneyWiseUKCapitalSplitRateScheme
            extends MoneyWiseUKCapitalFlatRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKCapitalSplitRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKCapitalSplitRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_HIGH_RATE, MoneyWiseUKCapitalSplitRateScheme::getHighRate);
        }

        /**
         * The High Rate.
         */
        private final OceanusRate theHighRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         */
        protected MoneyWiseUKCapitalSplitRateScheme(final OceanusRate pRate,
                                                    final OceanusRate pHighRate) {
            super(pRate);
            theHighRate = pHighRate;
        }

        /**
         * Obtain the high rate.
         * @return the high rate
         */
        protected OceanusRate getHighRate() {
            return theHighRate;
        }

        @Override
        public MetisFieldSet<? extends MoneyWiseUKCapitalSplitRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final MoneyWiseTaxClass pBasis) {
            /* Create a new List */
            final List<MoneyWiseTaxBand> myList = new ArrayList<>();

            /* Access underlying iterator and obtain first band */
            final Iterator<MoneyWiseTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
            final MoneyWiseTaxBand myFirstBand = myIterator.next();

            /* Add the two bands */
            myList.add(new MoneyWiseTaxBand(myFirstBand.getAmount(), getBasicRate()));
            myList.add(new MoneyWiseTaxBand(getHighRate()));

            /* Return the iterator */
            return myList.iterator();
        }
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseUKCapitalAsIncomeScheme
            extends MoneyWiseUKCapitalScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKCapitalAsIncomeScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKCapitalAsIncomeScheme.class);

        /**
         * Constructor.
         */
        public MoneyWiseUKCapitalAsIncomeScheme() {
        }

        @Override
        public MetisFieldSet<MoneyWiseUKCapitalAsIncomeScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Residential Scheme.
     */
    public static class MoneyWiseUKCapitalResidentialScheme
            extends MoneyWiseUKCapitalSplitRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKCapitalResidentialScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKCapitalResidentialScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_RESIDENTIAL, MoneyWiseUKCapitalResidentialScheme::getResidentialScheme);
        }

        /**
         * The Residential Scheme.
         */
        private final MoneyWiseUKCapitalSplitRateScheme theResidential;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         * @param pResRate the base rate
         * @param pHighResRate the high rate
         */
        protected MoneyWiseUKCapitalResidentialScheme(final OceanusRate pRate,
                                                      final OceanusRate pHighRate,
                                                      final OceanusRate pResRate,
                                                      final OceanusRate pHighResRate) {
            super(pRate, pHighRate);
            theResidential = new MoneyWiseUKCapitalSplitRateScheme(pResRate, pHighResRate);
        }

        /**
         * Obtain the high rate.
         * @return the high rate
         */
        protected MoneyWiseUKCapitalSplitRateScheme getResidentialScheme() {
            return theResidential;
        }

        @Override
        public MetisFieldSet<MoneyWiseUKCapitalResidentialScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final MoneyWiseTaxClass pBasis) {
            /* Switch on taxBasis */
            return MoneyWiseTaxClass.RESIDENTIALGAINS.equals(pBasis)
                    ? theResidential.taxBandIterator(pConfig, pBasis)
                    : super.taxBandIterator(pConfig, pBasis);
        }
    }
}
