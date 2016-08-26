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

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * UK TaxBands.
 */
public class MoneyWiseTaxBands {
    /**
     * Standard TaxBandSet.
     */
    private final MoneyWiseTaxBandSet theStandard;

    /**
     * Low Savings TaxBandSet.
     */
    private final MoneyWiseTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     */
    protected MoneyWiseTaxBands(final MoneyWiseTaxBandSet pStandard) {
        this(pStandard, null);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pLoSavings the loSavings taxBand
     */
    protected MoneyWiseTaxBands(final MoneyWiseTaxBandSet pStandard,
                                final MoneyWiseTaxBand pLoSavings) {
        theStandard = pStandard;
        theLoSavings = pLoSavings;
    }

    /**
     * Obtain the standard taxBands.
     * @return the taxBands
     */
    public MoneyWiseTaxBandSet getStandardSet() {
        return theStandard;
    }

    /**
     * Obtain the low savings taxBand.
     * @return the taxBands
     */
    public MoneyWiseTaxBand getLoSavings() {
        return theLoSavings;
    }

    /**
     * MoneyWiseTaxBand set.
     */
    public static class MoneyWiseTaxBandSet {
        /**
         * List of Tax Bands.
         */
        private final List<MoneyWiseTaxBand> theTaxBands;

        /**
         * Constructor.
         * @param pBands the set of tax bands
         */
        protected MoneyWiseTaxBandSet(final MoneyWiseTaxBand... pBands) {
            theTaxBands = new ArrayList<>();
            for (MoneyWiseTaxBand myBand : pBands) {
                theTaxBands.add(myBand);
            }
        }

        /**
         * Obtain an iterator.
         * @return the iterator
         */
        public Iterator<MoneyWiseTaxBand> iterator() {
            return theTaxBands.iterator();
        }
    }

    /**
     * MoneyWiseTaxBand class.
     */
    public static class MoneyWiseTaxBand {
        /**
         * Amount.
         */
        private final TethysMoney theAmount;

        /**
         * Rate.
         */
        private final TethysRate theRate;

        /**
         * Constructor.
         * @param pAmount the amount
         * @param pRate the rate
         */
        protected MoneyWiseTaxBand(final TethysMoney pAmount,
                                   final TethysRate pRate) {
            theAmount = pAmount;
            theRate = pRate;
        }

        /**
         * Constructor.
         * @param pRate the rate
         */
        protected MoneyWiseTaxBand(final TethysRate pRate) {
            this(null, pRate);
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public TethysRate getRate() {
            return theRate;
        }
    }
}
