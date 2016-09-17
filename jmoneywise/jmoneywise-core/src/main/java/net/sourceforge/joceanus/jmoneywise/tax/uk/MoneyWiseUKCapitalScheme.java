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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Capital Gains Tax Scheme.
 */
public abstract class MoneyWiseUKCapitalScheme
        extends MoneyWiseUKIncomeScheme
        implements MetisDataContents {
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
            TethysMoney myRemaining = new TethysMoney(pAmount);
            myRemaining.subtractAmount(myAmount);

            /* Calculate the amount covered by basic allowance */
            TethysMoney myXtra = super.getAmountInAllowance(pConfig, myRemaining);

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
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKCapitalFlatRateScheme.class.getSimpleName());

        /**
         * Rate Field Id.
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

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final TaxBasisClass pBasis) {
            /* Create a new List */
            List<MoneyWiseTaxBand> myList = new ArrayList<>();

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
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKCapitalSplitRateScheme.class.getSimpleName(), MoneyWiseUKCapitalFlatRateScheme.getBaseFields());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_HIGHRATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_HIGH_RATE.getValue());

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
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_HIGHRATE.equals(pField)) {
                return theHighRate;
            }

            /* Pass call on */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                             final TaxBasisClass pBasis) {
            /* Create a new List */
            List<MoneyWiseTaxBand> myList = new ArrayList<>();

            /* Access underlying iterator and obtain first band */
            Iterator<MoneyWiseTaxBand> myIterator = super.taxBandIterator(pConfig, pBasis);
            MoneyWiseTaxBand myFirstBand = myIterator.next();

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
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKCapitalAsIncomeScheme.class.getSimpleName());

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
            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
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
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKCapitalResidentialScheme.class.getSimpleName(), MoneyWiseUKCapitalSplitRateScheme.getBaseFields());

        /**
         * Residential Field Id.
         */
        private static final MetisField FIELD_RESIDENTIAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.SCHEME_RESIDENTIAL.getValue());

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
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_RESIDENTIAL.equals(pField)) {
                return theResidential;
            }

            /* Pass call on */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
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
