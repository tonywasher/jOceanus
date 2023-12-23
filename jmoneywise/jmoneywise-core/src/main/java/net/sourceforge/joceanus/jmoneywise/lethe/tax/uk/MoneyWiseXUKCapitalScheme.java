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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Capital Gains Tax Scheme.
 */
public abstract class MoneyWiseXUKCapitalScheme
        extends MoneyWiseXUKIncomeScheme {
    /*
     * Local Report fields.
     */
    static {
        MetisFieldSet.newFieldSet(MoneyWiseXUKCapitalScheme.class);
    }

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseXUKTaxConfig pConfig,
                                           final TethysMoney pAmount) {
        /* Adjust against the capital allowance */
        TethysMoney myRemaining = adjustForAllowance(pConfig.getCapitalAllowance(), pAmount);

        /* If we have any gains left */
        if (myRemaining.isNonZero()) {
            /* Adjust the basic allowance */
            myRemaining = super.adjustAllowances(pConfig, myRemaining);
        }

        /* Return unallocated income */
        return myRemaining;
    }

    @Override
    protected TethysMoney getAmountInAllowance(final MoneyWiseXUKTaxConfig pConfig,
                                               final TethysMoney pAmount) {
        /* Obtain the amount covered by the capital allowance */
        TethysMoney myAmount = getAmountInBand(pConfig.getCapitalAllowance(), pAmount);

        /* If we have income left over */
        if (myAmount.compareTo(pAmount) < 0) {
            /* Calculate remaining amount */
            final TethysMoney myRemaining = new TethysMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by basic allowance */
            final TethysMoney myXtra = super.getAmountInAllowance(pConfig, myRemaining);

            /* Determine the total amount covered by the allowance */
            myAmount = new TethysMoney(myAmount);
            myAmount.addAmount(myXtra);
        }

        /* return the amount */
        return myAmount;
    }

    /**
     * Flat Rate Scheme.
     */
    public static class MoneyWiseXUKCapitalFlatRateScheme
            extends MoneyWiseXUKCapitalScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKCapitalFlatRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKCapitalFlatRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_BASE_RATE, MoneyWiseXUKCapitalFlatRateScheme::getBasicRate);
        }

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseXUKCapitalFlatRateScheme(final TethysRate pRate) {
            theBaseRate = pRate;
        }

        /**
         * Obtain the base rate.
         * @return the base rate
         */
        protected TethysRate getBasicRate() {
            return theBaseRate;
        }

        @Override
        public MetisFieldSet<? extends MoneyWiseXUKCapitalFlatRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected Iterator<MoneyWiseXTaxBand> taxBandIterator(final MoneyWiseXUKTaxConfig pConfig,
                                                              final TaxBasisClass pBasis) {
            /* Create a new List */
            final List<MoneyWiseXTaxBand> myList = new ArrayList<>();

            /* Add the single band */
            myList.add(new MoneyWiseXTaxBand(getBasicRate()));

            /* Return the iterator */
            return myList.iterator();
        }
    }

    /**
     * Split Rate Scheme.
     */
    public static class MoneyWiseXUKCapitalSplitRateScheme
            extends MoneyWiseXUKCapitalFlatRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKCapitalSplitRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKCapitalSplitRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_HIGH_RATE, MoneyWiseXUKCapitalSplitRateScheme::getHighRate);
        }

        /**
         * The High Rate.
         */
        private final TethysRate theHighRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         */
        protected MoneyWiseXUKCapitalSplitRateScheme(final TethysRate pRate,
                                                     final TethysRate pHighRate) {
            super(pRate);
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
        public MetisFieldSet<? extends MoneyWiseXUKCapitalSplitRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected Iterator<MoneyWiseXTaxBand> taxBandIterator(final MoneyWiseXUKTaxConfig pConfig,
                                                             final TaxBasisClass pBasis) {
            /* Create a new List */
            final List<MoneyWiseXTaxBand> myList = new ArrayList<>();

            /* Access underlying iterator and obtain first band */
            final Iterator<MoneyWiseXTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
            final MoneyWiseXTaxBand myFirstBand = myIterator.next();

            /* Add the two bands */
            myList.add(new MoneyWiseXTaxBand(myFirstBand.getAmount(), getBasicRate()));
            myList.add(new MoneyWiseXTaxBand(getHighRate()));

            /* Return the iterator */
            return myList.iterator();
        }
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseXUKCapitalAsIncomeScheme
            extends MoneyWiseXUKCapitalScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKCapitalAsIncomeScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKCapitalAsIncomeScheme.class);

        @Override
        public MetisFieldSet<MoneyWiseXUKCapitalAsIncomeScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Residential Scheme.
     */
    public static class MoneyWiseXUKCapitalResidentialScheme
            extends MoneyWiseXUKCapitalSplitRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKCapitalResidentialScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKCapitalResidentialScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_RESIDENTIAL, MoneyWiseXUKCapitalResidentialScheme::getResidentialScheme);
        }

        /**
         * The Residential Scheme.
         */
        private final MoneyWiseXUKCapitalSplitRateScheme theResidential;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         * @param pResRate the base rate
         * @param pHighResRate the high rate
         */
        protected MoneyWiseXUKCapitalResidentialScheme(final TethysRate pRate,
                                                       final TethysRate pHighRate,
                                                        final TethysRate pResRate,
                                                      final TethysRate pHighResRate) {
            super(pRate, pHighRate);
            theResidential = new MoneyWiseXUKCapitalSplitRateScheme(pResRate, pHighResRate);
        }

        /**
         * Obtain the high rate.
         * @return the high rate
         */
        protected MoneyWiseXUKCapitalSplitRateScheme getResidentialScheme() {
            return theResidential;
        }

        @Override
        public MetisFieldSet<MoneyWiseXUKCapitalResidentialScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected Iterator<MoneyWiseXTaxBand> taxBandIterator(final MoneyWiseXUKTaxConfig pConfig,
                                                              final TaxBasisClass pBasis) {
            /* Switch on taxBasis */
            return TaxBasisClass.RESIDENTIALGAINS.equals(pBasis)
                                                                 ? theResidential.taxBandIterator(pConfig, pBasis)
                                                                 : super.taxBandIterator(pConfig, pBasis);
        }
    }
}
