/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.tax;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.date.OceanusFiscalYear;

/**
 * Tax Year cache.
 */
public abstract class MoneyWiseTaxYearCache
        implements MoneyWiseTaxFactory {
    /**
     * The Fiscal Year.
     */
    private final OceanusFiscalYear theFiscalYear;

    /**
     * Constructor.
     * @param pYear the fiscal year
     */
    protected MoneyWiseTaxYearCache(final OceanusFiscalYear pYear) {
        theFiscalYear = pYear;
    }

    /**
     * Obtain the taxYear date.
     * @param pDate the date
     * @return the date
     */
    protected OceanusDate getTaxYearDate(final OceanusDate pDate) {
        return theFiscalYear.endOfYear(pDate);
    }

    /**
     * Check whether the range matches a taxYear.
     * @param pRange the range
     * @return true/false
     */
    protected boolean checkTaxYearRange(final OceanusDateRange pRange) {
        /* Check that the range ends of a tax year boundary */
        final OceanusDate myEnd = pRange.getEnd();
        if ((myEnd == null)
                || !myEnd.equals(getTaxYearDate(myEnd))) {
            return false;
        }

        /* Check that the range starts on the correct taxYear boundary */
        final OceanusDate myStart = new OceanusDate(myEnd);
        myStart.adjustYear(-1);
        myStart.adjustDay(1);
        return myStart.equals(pRange.getStart());
    }
}
