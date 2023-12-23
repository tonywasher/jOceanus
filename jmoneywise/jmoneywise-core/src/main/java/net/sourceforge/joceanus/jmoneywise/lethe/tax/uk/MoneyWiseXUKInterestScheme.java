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
 * Interest Tax Scheme.
 */
public abstract class MoneyWiseXUKInterestScheme
        extends MoneyWiseXUKIncomeScheme {
    /*
     * Local Report fields.
     */
    static {
        MetisFieldSet.newFieldSet(MoneyWiseXUKInterestScheme.class);
    }

    /**
     * Obtain theTaxCredit rate for interest.
     * @param pTaxYear the taxYear
     * @return the taxCredit rate
     */
    protected abstract TethysRate getTaxCreditRate(MoneyWiseXUKTaxYear pTaxYear);

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseXUKTaxConfig pConfig,
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
    protected TethysMoney getAmountInAllowance(final MoneyWiseXUKTaxConfig pConfig,
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
    protected Iterator<MoneyWiseXTaxBand> taxBandIterator(final MoneyWiseXUKTaxConfig pConfig,
                                                          final TaxBasisClass pBasis) {
        /* Obtain the loSavingsBand and the basicRate */
        final MoneyWiseXTaxBand myLoBand = pConfig.getLoSavingsBand();
        final TethysMoney myLoAmount = myLoBand.getAmount();
        TethysRate myRate = getBaseRate();

        /* Create a new List */
        final List<MoneyWiseXTaxBand> myList = new ArrayList<>();

        /* Access underlying iterator and obtain first band */
        final Iterator<MoneyWiseXTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
        MoneyWiseXTaxBand myBasicBand = myIterator.next();
        TethysMoney myAmount = myBasicBand.getAmount();

        /* If we are a LoBase instance */
        if (this instanceof MoneyWiseXUKInterestLoBaseRateScheme) {
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
        myList.add(new MoneyWiseXTaxBand(myAmount, myRate));

        /* Loop through remaining tax bands */
        while (myIterator.hasNext()) {
            final MoneyWiseXTaxBand myBand = myIterator.next();
            myList.add(myBand);
        }

        /* Return the iterator */
        return myList.iterator();
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseXUKInterestAsIncomeScheme
            extends MoneyWiseXUKInterestScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKInterestAsIncomeScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKInterestAsIncomeScheme.class);

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseXUKTaxYear pTaxYear) {
            return pTaxYear.getTaxBands().getBasicTaxRate();
        }

        @Override
        public MetisFieldSet<MoneyWiseXUKInterestAsIncomeScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseXUKInterestBaseRateScheme
            extends MoneyWiseXUKInterestScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKInterestBaseRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKInterestBaseRateScheme.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.SCHEME_BASE_RATE, MoneyWiseXUKInterestBaseRateScheme::getBaseRate);
        }

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseXUKInterestBaseRateScheme(final TethysRate pRate) {
            theBaseRate = pRate;
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
        public MetisFieldSet<? extends MoneyWiseXUKInterestBaseRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * LoBase Rate Scheme.
     */
    public static class MoneyWiseXUKInterestLoBaseRateScheme
            extends MoneyWiseXUKInterestBaseRateScheme {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXUKInterestLoBaseRateScheme> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKInterestLoBaseRateScheme.class);

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseXUKInterestLoBaseRateScheme(final TethysRate pRate) {
            super(pRate);
        }

        @Override
        public MetisFieldSet<MoneyWiseXUKInterestLoBaseRateScheme> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
