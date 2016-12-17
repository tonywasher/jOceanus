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
package net.sourceforge.joceanus.jmoneywise.tax;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Tax Year cache.
 */
public abstract class MoneyWiseTaxYearCache {
    /**
     * The Fiscal Year.
     */
    private final TethysFiscalYear theFiscalYear;

    /**
     * Constructor.
     * @param pYear the fiscal year
     */
    public MoneyWiseTaxYearCache(final TethysFiscalYear pYear) {
        theFiscalYear = pYear;
    }

    /**
     * Obtain the taxYear for the period.
     * @param pRange the range
     * @return the taxYear or null if not a taxYear period
     */
    public abstract MoneyWiseTaxYear getTaxYearForRange(TethysDateRange pRange);

    /**
     * Obtain the taxYear for the date.
     * @param pDate the date
     * @return the taxYear
     */
    public abstract MoneyWiseTaxYear getTaxYearForDate(TethysDate pDate);

    /**
     * Obtain the taxYear date.
     * @param pDate the date
     * @return the date
     */
    protected TethysDate getTaxYearDate(final TethysDate pDate) {
        return theFiscalYear.endOfYear(pDate);
    }

    /**
     * Check whether the range matches a taxYear.
     * @param pRange the range
     * @return true/false
     */
    protected boolean checkTaxYearRange(final TethysDateRange pRange) {
        /* Check that the range ends of a tax year boundary */
        TethysDate myEnd = pRange.getEnd();
        if ((myEnd == null)
            || !myEnd.equals(getTaxYearDate(myEnd))) {
            return false;
        }

        /* Check that the range starts on the correct taxYear boundary */
        TethysDate myStart = new TethysDate(myEnd);
        myStart.adjustYear(-1);
        myStart.adjustDay(1);
        return myStart.equals(pRange.getStart());
    }
}
