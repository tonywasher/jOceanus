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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Interest Tax Scheme.
 */
public abstract class MoneyWiseUKInterestScheme
        extends MoneyWiseUKIncomeScheme {
    /**
     * Obtain theTaxCredit rate for interest.
     * @param pTaxYear the taxYear
     * @return the taxCredit rate
     */
    protected abstract TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear);

    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                           final TethysMoney pAmount) {
        /* Adjust against the basic allowance */
        TethysMoney myRemaining = super.adjustAllowances(pConfig, pAmount);

        /* If we have any interest left */
        if (myRemaining.isNonZero()) {
            /* Adjust the savings allowance noting that it still counts against the taxBand */
            TethysMoney myInterest = adjustForAllowance(pConfig.getSavingsAllowance(), myRemaining);

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
            TethysMoney myRemaining = new TethysMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by savings allowance */
            TethysMoney myXtra = getAmountInBand(pConfig.getSavingsAllowance(), myRemaining);

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
                                                         final TaxBasisClass pBasis) {
        /* Obtain the loSavingsBand and the basicRate */
        MoneyWiseTaxBand myLoBand = pConfig.getLoSavingsBand();
        TethysMoney myLoAmount = myLoBand.getAmount();
        TethysRate myRate = getBaseRate();

        /* Create a new List */
        List<MoneyWiseTaxBand> myList = new ArrayList<>();

        /* Access underlying iterator and obtain first band */
        Iterator<MoneyWiseTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
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
            MoneyWiseTaxBand myBand = myIterator.next();
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
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKInterestAsIncomeScheme.class.getSimpleName(), MoneyWiseUKIncomeScheme.getBaseFields());

        @Override
        protected TethysRate getTaxCreditRate(final MoneyWiseUKTaxYear pTaxYear) {
            return pTaxYear.getTaxBands().getBasicTaxRate();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseUKInterestBaseRateScheme
            extends MoneyWiseUKInterestScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKInterestBaseRateScheme.class.getSimpleName(), MoneyWiseUKIncomeScheme.getBaseFields());

        /**
         * Base Rate Field Id.
         */
        private static final MetisField FIELD_BASERATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_BASE_RATE.getValue());

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

            /* Pass on */
            return super.getFieldValue(pField);
        }
    }

    /**
     * LoBase Rate Scheme.
     */
    public static class MoneyWiseUKInterestLoBaseRateScheme
            extends MoneyWiseUKInterestBaseRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKInterestLoBaseRateScheme.class.getSimpleName(), MoneyWiseUKInterestBaseRateScheme.getBaseFields());

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseUKInterestLoBaseRateScheme(final TethysRate pRate) {
            super(pRate);
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }
    }
}
