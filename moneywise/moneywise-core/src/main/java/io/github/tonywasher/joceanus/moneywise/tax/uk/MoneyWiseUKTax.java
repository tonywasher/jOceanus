/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.moneywise.tax.uk;

import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseTaxAnalysis.MoneyWiseTaxYearCtl;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import io.github.tonywasher.joceanus.moneywise.tax.MoneyWiseTaxConfig;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;

/**
 * UK TaxYear Interface.
 */
public interface MoneyWiseUKTax {
    /**
     * UK TaxYear Interface.
     */
    interface MoneyWiseUKTaxYearCtl
            extends MoneyWiseTaxYearCtl {
        /**
         * Obtain the Standard taxBands.
         *
         * @return the tax bands
         */
        MoneyWiseUKTaxBandsCtl getTaxBands();


        /**
         * Obtain the Allowances.
         *
         * @return the allowances
         */
        MoneyWiseUKBasicAllowanceCtl getAllowances();
    }

    /**
     * UK TaxBands Interface.
     */
    interface MoneyWiseUKTaxBandsCtl {
        /**
         * Obtain the basic rate of income tax.
         *
         * @return the rate
         */
        OceanusRate getBasicTaxRate();
    }

    /**
     * UK TaxBands Interface.
     */
    interface MoneyWiseUKTaxConfigCtl
            extends MoneyWiseTaxConfig {
        /**
         * Obtain the tax year.
         *
         * @return the tax year
         */
        MoneyWiseUKTaxYearCtl getTaxYear();

        /**
         * Obtain the client birthday.
         *
         * @return the birthday
         */
        OceanusDate getBirthday();

        /**
         * Obtain the client age.
         *
         * @return the gross taxable
         */
        Integer getClientAge();

        /**
         * Obtain the allowance.
         *
         * @return the allowance
         */
        OceanusMoney getAllowance();

        /**
         * Obtain the gross taxable income.
         *
         * @return the gross taxable
         */
        OceanusMoney getGrossTaxable();

        /**
         * Obtain the gross preSavings income.
         *
         * @return the gross preSavings
         */
        OceanusMoney getGrossPreSavings();

        /**
         * Set whether we have an age related allowance?
         *
         * @param pFlag true/false
         */
        void setHasAgeRelatedAllowance(boolean pFlag);
    }

    /**
     * UK basic Allowance Interface.
     */
    interface MoneyWiseUKBasicAllowanceCtl {
        /**
         * Obtain the allowance.
         *
         * @return the Allowance
         */
        OceanusMoney getAllowance();

        /**
         * Obtain the rental allowance.
         *
         * @return the Allowance
         */
        OceanusMoney getRentalAllowance();

        /**
         * Obtain the capital allowance.
         *
         * @return the Allowance
         */
        OceanusMoney getCapitalAllowance();

        /**
         * Calculate the allowance.
         *
         * @param pConfig the tax configuration
         * @return the calculated allowance
         */
        OceanusMoney calculateBasicAllowance(MoneyWiseUKTaxConfigCtl pConfig);

        /**
         * Calculate the loSavings band.
         *
         * @param pConfig    the tax configuration
         * @param pLoSavings the low savings band
         * @return the loSavings band
         */
        MoneyWiseTaxBand calculateLoSavingsBand(MoneyWiseUKTaxConfigCtl pConfig,
                                                MoneyWiseTaxBand pLoSavings);

        /**
         * Calculate the savings allowance.
         *
         * @param pConfig the tax configuration
         * @return the savings allowance
         */
        OceanusMoney calculateSavingsAllowance(MoneyWiseUKTaxConfigCtl pConfig);

        /**
         * Calculate the dividend allowance.
         *
         * @return the dividend allowance
         */
        OceanusMoney calculateDividendAllowance();
    }
}
