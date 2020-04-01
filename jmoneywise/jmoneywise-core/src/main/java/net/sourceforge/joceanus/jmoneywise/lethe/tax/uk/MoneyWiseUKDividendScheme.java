/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Dividend Tax Scheme.
 */
public abstract class MoneyWiseUKDividendScheme
        extends MoneyWiseUKIncomeScheme {
    /*
     * Local Report fields.
     */
    static {
        MetisFieldSet.newFieldSet(MoneyWiseUKDividendScheme.class);
    }

    /**
     * Constructor.
     */
    protected MoneyWiseUKDividendScheme() {
        this(Boolean.TRUE);
    }

    /**
     * Constructor.
     * @param pReliefAvailable Is tax relief available?
     */
    protected MoneyWiseUKDividendScheme(final Boolean pReliefAvailable) {
        super(pReliefAvailable);
    }

    /**
     * Obtain theTaxCredit rate for dividend.
     * @param pTaxYear the taxYear
     * @return the taxCredit rate
     */
    protected abstract TethysRate getTaxCreditRate(MoneyWiseUKTaxYear pTaxYear);

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                           final TethysMoney pAmount) {
        /* Adjust against the basic allowance */
        final TethysMoney myRemaining = super.adjustAllowances(pConfig, pAmount);

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
                                               final TethysMoney pAmount) {
        /* Obtain the amount covered by the basic allowance */
        TethysMoney myAmount = super.getAmountInAllowance(pConfig, pAmount);

        /* If we have income left over */
        if (myAmount.compareTo(pAmount) < 0) {
            /* Calculate remaining amount */
            final TethysMoney myRemaining = new TethysMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by dividend allowance */
            final TethysMoney myXtra = getAmountInBand(pConfig.getDividendAllowance(), myRemaining);

            /* Determine the total amount covered by the allowance */
            myAmount = new TethysMoney(myAmount);
            myAmount.addAmount(myXtra);
        }

        /* return the amount */
        return myAmount;
    }

    /**
     * Obtain the base rate.
     * @return the base rate
     */
    protected TethysRate getBaseRate() {
        return null;
    }

    /**
     * Obtain the higher rate.
     * @return the higher rate
     */
    protected TethysRate getHigherRate() {
        return null;
    }

    /**
     * Obtain the additional rate.
     * @return the additional rate
     */
    protected TethysRate getAdditionalRate() {
        return null;
    }

    @Override
    protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                         final TaxBasisClass pBasis) {

        /* Create a new List */
        final List<MoneyWiseTaxBand> myList = new ArrayList<>();

        /* Access underlying iterator */
        final Iterator<MoneyWiseTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
        MoneyWiseTaxBand myBand = myIterator.next();
        TethysMoney myAmount = myBand.getAmount();
        TethysRate myRate = getBaseRate();

        /* If we are a LoHigher instance */
        if (this instanceof MoneyWiseUKDividendLoHigherRateScheme) {
            /* Access the true basic band and merge in the lower rate */
            myBand = myIterator.next();
            myAmount = new TethysMoney(myAmount);
            myAmount.addAmount(myBand.getAmount());
        }

        /* Add the basic band */
        if (myRate == null) {
            myRate = myBand.getRate();
        }
        myList.add(new MoneyWiseTaxBand(myAmount, myRate));

        /* If we have a "higher" band */
        if (myIterator.hasNext()) {
            /* Add the higher band */
            myBand = myIterator.next();
            myRate = getHigherRate();
            if (myRate == null) {
                myRate = myBand.getRate();
            }
            myList.add(new MoneyWiseTaxBand(myBand.getAmount(), myRate));
        }

        /* If we have an "additional" band */
        if (myIterator.hasNext()) {
            /* Add the higher band */
            myBand = myIterator.next();
            myRate = getAdditionalRate();
            if (myRate == null) {
                myRate = myBand.getRate();
            }
            myList.add(new MoneyWiseTaxBand(myBand.getAmount(), myRate));
        }

        /* Loop through remaining tax bands */
        while (myIterator.hasNext()) {
            myBand = myIterator.next();
            myList.add(myBand);
        }

        /* Return the iterator */
        return myList.iterator();
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseUKDividendAsIncomeScheme
            extends MoneyWiseUKDividendScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKDividendAsIncomeScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKDividendAsIncomeScheme.class);

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
            return pTaxYear.getTaxBands().getBasicTaxRate();
        }

        @Override
        public MetisFieldSet<MoneyWiseUKDividendAsIncomeScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseUKDividendBaseRateScheme
            extends MoneyWiseUKDividendScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKDividendBaseRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKDividendBaseRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_BASE_RATE, MoneyWiseUKDividendBaseRateScheme::getBaseRate);
        }

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pReliefAvailable Is tax relief available?
         */
        protected MoneyWiseUKDividendBaseRateScheme(final TethysRate pRate,
                                                    final Boolean pReliefAvailable) {
            super(pReliefAvailable);
            theBaseRate = pRate;
        }

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseUKDividendBaseRateScheme(final TethysRate pRate) {
            this(pRate, Boolean.TRUE);
        }

        @Override
        protected TethysRate getBaseRate() {
            return theBaseRate;
        }

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
            return theBaseRate;
        }

        @Override
        public MetisFieldSet<? extends MoneyWiseUKDividendBaseRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Higher Rate Scheme.
     */
    public static class MoneyWiseUKDividendHigherRateScheme
            extends MoneyWiseUKDividendBaseRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKDividendHigherRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKDividendHigherRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_HIGH_RATE, MoneyWiseUKDividendHigherRateScheme::getHigherRate);
        }

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
            this(pRate, pHighRate, Boolean.FALSE);
        }

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the higher rate
         * @param pReliefAvailable Is tax relief available?
         */
        protected MoneyWiseUKDividendHigherRateScheme(final TethysRate pRate,
                                                      final TethysRate pHighRate,
                                                      final Boolean pReliefAvailable) {
            super(pRate, pReliefAvailable);
            theHighRate = pHighRate;
        }

        @Override
        protected TethysRate getHigherRate() {
            return theHighRate;
        }

        @Override
        public MetisFieldSet<? extends MoneyWiseUKDividendHigherRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * LoHigher Rate Scheme.
     */
    public static class MoneyWiseUKDividendLoHigherRateScheme
            extends MoneyWiseUKDividendHigherRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKDividendLoHigherRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKDividendLoHigherRateScheme.class);

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHigherRate the higher rate
         */
        protected MoneyWiseUKDividendLoHigherRateScheme(final TethysRate pRate,
                                                        final TethysRate pHigherRate) {
            super(pRate, pHigherRate);
        }

        @Override
        public MetisFieldSet<MoneyWiseUKDividendLoHigherRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Additional Rate Scheme.
     */
    public static class MoneyWiseUKDividendAdditionalRateScheme
            extends MoneyWiseUKDividendHigherRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKDividendAdditionalRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKDividendAdditionalRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_ADDITIONAL_RATE, MoneyWiseUKDividendHigherRateScheme::getAdditionalRate);
        }

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
            this(pRate, pHighRate, pAddRate, Boolean.FALSE);
        }

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the higher rate
         * @param pAddRate the additional rate
         * @param pReliefAvailable Is tax relief available?
         */
        protected MoneyWiseUKDividendAdditionalRateScheme(final TethysRate pRate,
                                                          final TethysRate pHighRate,
                                                          final TethysRate pAddRate,
                                                          final Boolean pReliefAvailable) {
            super(pRate, pHighRate, pReliefAvailable);
            theAdditionalRate = pAddRate;
        }

        @Override
        protected TethysRate getAdditionalRate() {
            return theAdditionalRate;
        }

        @Override
        public MetisFieldSet<MoneyWiseUKDividendAdditionalRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
