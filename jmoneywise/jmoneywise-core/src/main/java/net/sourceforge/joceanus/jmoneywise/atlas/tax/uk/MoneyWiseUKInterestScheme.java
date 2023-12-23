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
package net.sourceforge.joceanus.jmoneywise.atlas.tax.uk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Interest Tax Scheme.
 */
public abstract class MoneyWiseUKInterestScheme
        extends MoneyWiseUKIncomeScheme {
    /*
     * Local Report fields.
     */
    static {
        MetisFieldSet.newFieldSet(MoneyWiseUKInterestScheme.class);
    }

    /**
     * Obtain theTaxCredit rate for interest.
     * @param pTaxYear the taxYear
     * @return the taxCredit rate
     */
    protected abstract TethysRate getTaxCreditRate(MoneyWiseUKTaxYear pTaxYear);

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                           final TethysMoney pAmount) {
        /* Adjust against the basic allowance */
        final TethysMoney myRemaining = super.adjustAllowances(pConfig, pAmount);

        /* If we have any interest left */
        if (myRemaining.isNonZero()) {
            /* Adjust the savings allowance noting that it still counts against the taxBand */
            final TethysMoney myInterest = adjustForAllowance(pConfig.getSavingsAllowance(), myRemaining);

            /* Adjust any loSavings band noting that it still counts against the taxBand */
            adjustForAllowance(pConfig.getLoSavingsBand().getAmount(), myInterest);
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

            /* Calculate the amount covered by savings allowance */
            final TethysMoney myXtra = getAmountInBand(pConfig.getSavingsAllowance(), myRemaining);

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

    @Override
    protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                         final MoneyWiseTaxClass pBasis) {
        /* Obtain the loSavingsBand and the basicRate */
        final MoneyWiseTaxBand myLoBand = pConfig.getLoSavingsBand();
        final TethysMoney myLoAmount = myLoBand.getAmount();
        TethysRate myRate = getBaseRate();

        /* Create a new List */
        final List<MoneyWiseTaxBand> myList = new ArrayList<>();

        /* Access underlying iterator and obtain first band */
        final Iterator<MoneyWiseTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
        MoneyWiseTaxBand myBasicBand = myIterator.next();
        TethysMoney myAmount = myBasicBand.getAmount();

        /* If we are a LoBase instance */
        if (this instanceof MoneyWiseUKInterestLoBaseRateScheme) {
            /* Add the first band as is */
            myList.add(myBasicBand);

            /* Access the true basic band */
            myBasicBand = myIterator.next();
            myAmount = myBasicBand.getAmount();

            /* Add low band if required */
        } else if (myLoAmount.isNonZero()) {
            myList.add(myLoBand);
            myAmount = new TethysMoney(myAmount);
            myAmount.subtract(myLoAmount);
        }

        /* Add the basic band */
        if (myRate == null) {
            myRate = myBasicBand.getRate();
        }
        myList.add(new MoneyWiseTaxBand(myAmount, myRate));

        /* Loop through remaining tax bands */
        while (myIterator.hasNext()) {
            final MoneyWiseTaxBand myBand = myIterator.next();
            myList.add(myBand);
        }

        /* Return the iterator */
        return myList.iterator();
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseUKInterestAsIncomeScheme
            extends MoneyWiseUKInterestScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKInterestAsIncomeScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKInterestAsIncomeScheme.class);

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
            return pTaxYear.getTaxBands().getBasicTaxRate();
        }

        @Override
        public MetisFieldSet<MoneyWiseUKInterestAsIncomeScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseUKInterestBaseRateScheme
            extends MoneyWiseUKInterestScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKInterestBaseRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKInterestBaseRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_BASE_RATE, MoneyWiseUKInterestBaseRateScheme::getBaseRate);
        }

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseUKInterestBaseRateScheme(final TethysRate pRate) {
            theBaseRate = pRate;
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
        public MetisFieldSet<? extends MoneyWiseUKInterestBaseRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * LoBase Rate Scheme.
     */
    public static class MoneyWiseUKInterestLoBaseRateScheme
            extends MoneyWiseUKInterestBaseRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseUKInterestLoBaseRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKInterestLoBaseRateScheme.class);

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseUKInterestLoBaseRateScheme(final TethysRate pRate) {
            super(pRate);
        }

        @Override
        public MetisFieldSet<MoneyWiseUKInterestLoBaseRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
