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
 * Capital Gains Tax Scheme.
 */
public abstract class MoneyWiseCapitalScheme
        extends MoneyWiseIncomeScheme
        implements MetisDataContents {
    /**
     * Flat Rate Scheme.
     */
    public static class MoneyWiseCapitalFlatRateScheme
            extends MoneyWiseCapitalScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseCapitalFlatRateScheme.class.getSimpleName());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_BASERATE = FIELD_DEFS.declareEqualityField("BaseRate");

        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseCapitalFlatRateScheme(final TethysRate pRate) {
            theBaseRate = pRate;
        }

        /**
         * Obtain the base rate.
         * @return the base rate
         */
        protected TethysRate getBaseRate() {
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
    }

    /**
     * Split Rate Scheme.
     */
    public static class MoneyWiseCapitalSplitRateScheme
            extends MoneyWiseCapitalFlatRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseCapitalSplitRateScheme.class.getSimpleName(), MoneyWiseCapitalFlatRateScheme.getBaseFields());

        /**
         * Rate Field Id.
         */
        private static final MetisField FIELD_HIGHRATE = FIELD_DEFS.declareEqualityField("HighRate");

        /**
         * The High Rate.
         */
        private final TethysRate theHighRate;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         */
        protected MoneyWiseCapitalSplitRateScheme(final TethysRate pRate,
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
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseCapitalAsIncomeScheme
            extends MoneyWiseCapitalScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseCapitalAsIncomeScheme.class.getSimpleName());

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
    public static class MoneyWiseCapitalResidentialScheme
            extends MoneyWiseCapitalSplitRateScheme {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseCapitalResidentialScheme.class.getSimpleName(), MoneyWiseCapitalSplitRateScheme.getBaseFields());

        /**
         * Residential Field Id.
         */
        private static final MetisField FIELD_RESIDENTIAL = FIELD_DEFS.declareEqualityField("Residential");

        /**
         * The Residential Scheme.
         */
        private final MoneyWiseCapitalSplitRateScheme theResidential;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pHighRate the high rate
         * @param pResRate the base rate
         * @param pHighResRate the high rate
         */
        protected MoneyWiseCapitalResidentialScheme(final TethysRate pRate,
                                                    final TethysRate pHighRate,
                                                    final TethysRate pResRate,
                                                    final TethysRate pHighResRate) {
            super(pRate, pHighRate);
            theResidential = new MoneyWiseCapitalSplitRateScheme(pResRate, pHighResRate);
        }

        /**
         * Obtain the high rate.
         * @return the high rate
         */
        protected MoneyWiseCapitalSplitRateScheme getResidentialScheme() {
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
    }
}
