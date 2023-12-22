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
 * Dividend Tax Scheme.
 */
public abstract class MoneyWiseXUKDividendScheme
        extends MoneyWiseXUKIncomeScheme {
    /*
     * Local Report fields.
     */
    static {
        MetisFieldSet.newFieldSet(MoneyWiseXUKDividendScheme.class);
    }

    /**
     * Constructor.
     */
    protected MoneyWiseXUKDividendScheme() {
        this(Boolean.TRUE);
    }

    /**
     * Constructor.
     * @param pReliefAvailable Is tax relief available?
     */
    protected MoneyWiseXUKDividendScheme(final Boolean pReliefAvailable) {
        super(pReliefAvailable);
    }

    /**
     * Obtain theTaxCredit rate for dividend.
     * @param pTaxYear the taxYear
     * @return the taxCredit rate
     */
    protected abstract TethysRate getTaxCreditRate(MoneyWiseXUKTaxYear pTaxYear);

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseXUKTaxConfig pConfig,
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
    protected TethysMoney getAmountInAllowance(final MoneyWiseXUKTaxConfig pConfig,
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
    protected Iterator<MoneyWiseXTaxBand> taxBandIterator(final MoneyWiseXUKTaxConfig pConfig,
                                                         final TaxBasisClass pBasis) {

        /* Create a new List */
        final List<MoneyWiseXTaxBand> myList = new ArrayList<>();

        /* Access underlying iterator */
        final Iterator<MoneyWiseXTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
        MoneyWiseXTaxBand myBand = myIterator.next();
        TethysMoney myAmount = myBand.getAmount();
        TethysRate myRate = getBaseRate();

        /* If we are a LoHigher instance */
        if (this instanceof MoneyWiseXUKDividendLoHigherRateScheme) {
            /* Access the true basic band and merge in the lower rate */
            myBand = myIterator.next();
            myAmount = new TethysMoney(myAmount);
            myAmount.addAmount(myBand.getAmount());
        }

        /* Add the basic band */
        if (myRate == null) {
            myRate = myBand.getRate();
        }
        myList.add(new MoneyWiseXTaxBand(myAmount, myRate));

        /* If we have a "higher" band */
        if (myIterator.hasNext()) {
            /* Add the higher band */
            myBand = myIterator.next();
            myRate = getHigherRate();
            if (myRate == null) {
                myRate = myBand.getRate();
            }
            myList.add(new MoneyWiseXTaxBand(myBand.getAmount(), myRate));
        }

        /* If we have an "additional" band */
        if (myIterator.hasNext()) {
            /* Add the higher band */
            myBand = myIterator.next();
            myRate = getAdditionalRate();
            if (myRate == null) {
                myRate = myBand.getRate();
            }
            myList.add(new MoneyWiseXTaxBand(myBand.getAmount(), myRate));
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
    public static class MoneyWiseXUKDividendAsIncomeScheme
            extends MoneyWiseXUKDividendScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKDividendAsIncomeScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKDividendAsIncomeScheme.class);

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseXUKTaxYear pTaxYear) {
            return pTaxYear.getTaxBands().getBasicTaxRate();
        }

        @Override
        public MetisFieldSet<MoneyWiseXUKDividendAsIncomeScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseXUKDividendBaseRateScheme
            extends MoneyWiseXUKDividendScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKDividendBaseRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKDividendBaseRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_BASE_RATE, MoneyWiseXUKDividendBaseRateScheme::getBaseRate);
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
        protected MoneyWiseXUKDividendBaseRateScheme(final TethysRate pRate,
                                                     final Boolean pReliefAvailable) {
            super(pReliefAvailable);
            theBaseRate = pRate;
        }

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseXUKDividendBaseRateScheme(final TethysRate pRate) {
            this(pRate, Boolean.TRUE);
        }

        @Override
        protected TethysRate getBaseRate() {
            return theBaseRate;
        }

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseXUKTaxYear pTaxYear) {
            return theBaseRate;
        }

        @Override
        public MetisFieldSet<? extends MoneyWiseXUKDividendBaseRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Higher Rate Scheme.
     */
    public static class MoneyWiseXUKDividendHigherRateScheme
            extends MoneyWiseXUKDividendBaseRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKDividendHigherRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKDividendHigherRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_HIGH_RATE, MoneyWiseXUKDividendHigherRateScheme::getHigherRate);
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
        protected MoneyWiseXUKDividendHigherRateScheme(final TethysRate pRate,
                                                       final TethysRate pHighRate) {
            this(pRate, pHighRate, Boolean.FALSE);
        }

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the higher rate
         * @param pReliefAvailable Is tax relief available?
         */
        protected MoneyWiseXUKDividendHigherRateScheme(final TethysRate pRate,
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
        public MetisFieldSet<? extends MoneyWiseXUKDividendHigherRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * LoHigher Rate Scheme.
     */
    public static class MoneyWiseXUKDividendLoHigherRateScheme
            extends MoneyWiseXUKDividendHigherRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKDividendLoHigherRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKDividendLoHigherRateScheme.class);

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHigherRate the higher rate
         */
        protected MoneyWiseXUKDividendLoHigherRateScheme(final TethysRate pRate,
                                                        final TethysRate pHigherRate) {
            super(pRate, pHigherRate);
        }

        @Override
        public MetisFieldSet<MoneyWiseXUKDividendLoHigherRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Additional Rate Scheme.
     */
    public static class MoneyWiseXUKDividendAdditionalRateScheme
            extends MoneyWiseXUKDividendHigherRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKDividendAdditionalRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKDividendAdditionalRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_ADDITIONAL_RATE, MoneyWiseXUKDividendHigherRateScheme::getAdditionalRate);
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
        protected MoneyWiseXUKDividendAdditionalRateScheme(final TethysRate pRate,
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
        protected MoneyWiseXUKDividendAdditionalRateScheme(final TethysRate pRate,
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
        public MetisFieldSet<MoneyWiseXUKDividendAdditionalRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
