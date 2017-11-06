/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Capital Gains Tax Scheme.
 */
public abstract class MoneyWiseUKCapitalScheme
        extends MoneyWiseUKIncomeScheme {
    @Override
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
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
    protected TethysMoney getAmountInAllowance(final MoneyWiseUKTaxConfig pConfig,
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
    public static class MoneyWiseUKCapitalFlatRateScheme
            extends MoneyWiseUKCapitalScheme {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseUKCapitalFlatRateScheme.class, MoneyWiseUKIncomeScheme.getBaseFieldSet());

        /**
         * Rate Field Id.
         */
        private static final MetisDataField FIELD_BASERATE = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_BASE_RATE);

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseUKCapitalFlatRateScheme(final TethysRate pRate) {
            theBaseRate = pRate;
        }

        /**
         * Obtain the base rate.
         * @return the base rate
         */
        protected TethysRate getBasicRate() {
            return theBaseRate;
        }

        /**
         * Obtain the data fields.
         * @return the data fields
         */
        protected static MetisDataFieldSet getBaseFields() {
            return FIELD_DEFS;
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_BASERATE.equals(pField)) {
                return theBaseRate;
            }

            /* Pass on */
            return super.getFieldValue(pField);
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final TaxBasisClass pBasis) {
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
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseUKCapitalSplitRateScheme.class, MoneyWiseUKCapitalFlatRateScheme.getBaseFieldSet());

        /**
         * Rate Field Id.
         */
        private static final MetisDataField FIELD_HIGHRATE = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_HIGH_RATE);

        /**
         * The High Rate.
         */
        private final TethysRate theHighRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         */
        protected MoneyWiseUKCapitalSplitRateScheme(final TethysRate pRate,
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
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_HIGHRATE.equals(pField)) {
                return theHighRate;
            }

            /* Pass call on */
            return super.getFieldValue(pField);
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final TaxBasisClass pBasis) {
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
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseUKCapitalAsIncomeScheme.class, MoneyWiseUKIncomeScheme.getBaseFieldSet());

        /**
         * Obtain the data fields.
         * @return the data fields
         */
        protected static MetisDataFieldSet getBaseFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Residential Scheme.
     */
    public static class MoneyWiseUKCapitalResidentialScheme
            extends MoneyWiseUKCapitalSplitRateScheme {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseUKCapitalResidentialScheme.class, MoneyWiseUKCapitalSplitRateScheme.getBaseFieldSet());

        /**
         * Residential Field Id.
         */
        private static final MetisDataField FIELD_RESIDENTIAL = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_RESIDENTIAL);

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
        protected MoneyWiseUKCapitalResidentialScheme(final TethysRate pRate,
                                                      final TethysRate pHighRate,
                                                      final TethysRate pResRate,
                                                      final TethysRate pHighResRate) {
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
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_RESIDENTIAL.equals(pField)) {
                return theResidential;
            }

            /* Pass call on */
            return super.getFieldValue(pField);
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final TaxBasisClass pBasis) {
            /* Switch on taxBasis */
            return TaxBasisClass.RESIDENTIALGAINS.equals(pBasis)
                                                                 ? theResidential.taxBandIterator(pConfig, pBasis)
                                                                 : super.taxBandIterator(pConfig, pBasis);
        }
    }
}
