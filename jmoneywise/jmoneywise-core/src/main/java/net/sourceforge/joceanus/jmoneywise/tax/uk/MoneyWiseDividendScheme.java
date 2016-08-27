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
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Dividend Tax Scheme.
 */
public abstract class MoneyWiseDividendScheme
        extends MoneyWiseIncomeScheme
        implements MetisDataContents {
    /**
     * Is tax relief available?
     * @return true/false
     */
    public Boolean taxReliefAvailable() {
        return Boolean.TRUE;
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseDividendAsIncomeScheme
            extends MoneyWiseDividendScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDividendAsIncomeScheme.class.getSimpleName());

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
    public static class MoneyWiseDividendBaseRateScheme
            extends MoneyWiseDividendScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDividendBaseRateScheme.class.getSimpleName());

        /**
         * Base Rate Field Id.
         */
        private static final MetisField FIELD_BASERATE = FIELD_DEFS.declareEqualityField("BaseRate");

        /**
         * Relief Available Field Id.
         */
        private static final MetisField FIELD_RELIEF = FIELD_DEFS.declareEqualityField("ReliefAvailable");

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
        protected MoneyWiseDividendBaseRateScheme(final TethysRate pRate,
                                                  final Boolean pReliefAvailable) {
            theBaseRate = pRate;
            reliefAvailable = pReliefAvailable;
        }

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseDividendBaseRateScheme(final TethysRate pRate) {
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
    public static class MoneyWiseDividendHigherRateScheme
            extends MoneyWiseDividendBaseRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDividendHigherRateScheme.class.getSimpleName(), MoneyWiseDividendBaseRateScheme.getBaseFields());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_HIGHRATE = FIELD_DEFS.declareEqualityField("HighRate");

        /**
         * The Higher Rate.
         */
        private final TethysRate theHighRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the higher rate
         */
        protected MoneyWiseDividendHigherRateScheme(final TethysRate pRate,
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

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }
    }

    /**
     * Additional Rate Scheme.
     */
    public static class MoneyWiseDividendAdditionalRateScheme
            extends MoneyWiseDividendHigherRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDividendAdditionalRateScheme.class.getSimpleName(), MoneyWiseDividendHigherRateScheme.getBaseFields());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_ADDRATE = FIELD_DEFS.declareEqualityField("AdditionalRate");

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
        protected MoneyWiseDividendAdditionalRateScheme(final TethysRate pRate,
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

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }
    }
}
