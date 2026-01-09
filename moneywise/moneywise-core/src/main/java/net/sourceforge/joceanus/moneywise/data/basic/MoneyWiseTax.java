/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.basic;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;

/**
 * Tax related interfaces.
 */
public class MoneyWiseTax {
    /**
     * Tax Credit decisions.
     */
    public interface MoneyWiseTaxCredit {
        /**
         * Obtain the yearEnd.
         * @return the tax year end
         */
        OceanusDate getYearEnd();

        /**
         * Is a taxCredit required for interest/dividend?
         * @return true/false
         */
        boolean isTaxCreditRequired();

        /**
         * Obtain the taxCredit rate for interest.
         * @return the rate
         */
        OceanusRate getTaxCreditRateForInterest();

        /**
         * Obtain the taxCredit rate for dividend.
         * @return the rate
         */
        OceanusRate getTaxCreditRateForDividend();
    }

    /**
     * Tax Year Factory.
     */
    public interface MoneyWiseTaxFactory {
        /**
         * Obtain the taxYear for the period.
         * @param pRange the range
         * @return the taxYear or null if not a taxYear period
         */
        MoneyWiseTaxCredit findTaxYearForRange(OceanusDateRange pRange);

        /**
         * Obtain the taxYear for the date.
         * @param pDate the date
         * @return the taxYear
         */
        MoneyWiseTaxCredit findTaxYearForDate(OceanusDate pDate);

        /**
         * Obtain the range of supported dates.
         * @return the date range
         */
        OceanusDateRange getDateRange();
    }
}
