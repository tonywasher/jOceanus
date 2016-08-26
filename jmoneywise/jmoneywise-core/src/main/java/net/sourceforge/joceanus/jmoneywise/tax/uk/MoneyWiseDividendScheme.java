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

import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Dividend Tax Scheme.
 */
public abstract class MoneyWiseDividendScheme
        extends MoneyWiseIncomeScheme {
    /**
     * Is tax relief available?
     * @return true/false
     */
    public boolean taxReliefAvailable() {
        return true;
    }

    /**
     * As Income Scheme.
     */
    public static class MoneyWiseDividendAsIncomeScheme
            extends MoneyWiseDividendScheme {
    }

    /**
     * Base Rate Scheme.
     */
    public static class MoneyWiseDividendBaseRateScheme
            extends MoneyWiseDividendScheme {
        /**
         * The Base Rate.
         */
        private final TethysRate theBaseRate;

        /**
         * Tax Relief available.
         */
        private final boolean reliefAvailable;

        /**
         * Constructor.
         * @param pRate the base rate
         * @param pReliefAvailable Is tax relief available?
         */
        protected MoneyWiseDividendBaseRateScheme(final TethysRate pRate,
                                                  final boolean pReliefAvailable) {
            theBaseRate = pRate;
            reliefAvailable = pReliefAvailable;
        }

        /**
         * Constructor.
         * @param pRate the base rate
         */
        protected MoneyWiseDividendBaseRateScheme(final TethysRate pRate) {
            this(pRate, true);
        }

        /**
         * Obtain the base rate.
         * @return the base rate
         */
        protected TethysRate getBaseRate() {
            return theBaseRate;
        }

        @Override
        public boolean taxReliefAvailable() {
            return reliefAvailable;
        }
    }

    /**
     * Higher Rate Scheme.
     */
    public static class MoneyWiseDividendHigherRateScheme
            extends MoneyWiseDividendBaseRateScheme {
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
            super(pRate, false);
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
        public boolean taxReliefAvailable() {
            return false;
        }
    }

    /**
     * Additional Rate Scheme.
     */
    public static class MoneyWiseDividendAdditionalRateScheme
            extends MoneyWiseDividendHigherRateScheme {
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
    }
}
