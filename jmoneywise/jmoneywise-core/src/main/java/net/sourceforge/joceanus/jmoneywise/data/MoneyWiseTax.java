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
package net.sourceforge.joceanus.jmoneywise.data;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

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
        TethysDate getYearEnd();

        /**
         * Is a taxCredit required for interest/dividend?
         * @return true/false
         */
        boolean isTaxCreditRequired();

        /**
         * Obtain the taxCredit rate for interest.
         * @return the rate
         */
        TethysRate getTaxCreditRateForInterest();

        /**
         * Obtain the taxCredit rate for dividend.
         * @return the rate
         */
        TethysRate getTaxCreditRateForDividend();
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
        MoneyWiseTaxCredit findTaxYearForRange(TethysDateRange pRange);

        /**
         * Obtain the taxYear for the date.
         * @param pDate the date
         * @return the taxYear
         */
        MoneyWiseTaxCredit findTaxYearForDate(TethysDate pDate);

        /**
         * Obtain the range of supported dates.
         * @return the date range
         */
        TethysDateRange getDateRange();
    }
}
