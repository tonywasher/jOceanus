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
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusFiscalYear;

/**
 * Simple class to define an archive year.
 */
public final class MoneyWiseArchiveYear {
    /**
     * Year boundary.
     */
    private static final int YEAR_BDY = 60;

    /**
     * Year constant.
     */
    private static final int YEAR_1900 = 1900;

    /**
     * Year constant.
     */
    private static final int YEAR_2000 = 2000;

    /**
     * The date.
     */
    private final OceanusDate theDate;

    /**
     * The range name.
     */
    private final String theRangeName;

    /**
     * Constructor.
     * @param pName the range name
     */
    MoneyWiseArchiveYear(final String pName) {
        /* Store parameters */
        theRangeName = pName;

        /* Isolate the year part */
        final int myLen = pName.length();
        int myYear = Integer.parseInt(pName.substring(myLen - 2));

        /* Calculate the actual year */
        if (myYear < YEAR_BDY) {
            myYear += YEAR_2000;
        } else {
            myYear += YEAR_1900;
        }

        /* Create the date */
        final OceanusFiscalYear myFiscal = OceanusFiscalYear.UK;
        theDate = new OceanusDate(myYear, myFiscal.getFirstMonth(), myFiscal.getFirstDay());
        theDate.adjustDay(-1);
    }

    /**
     * Get the date.
     * @return the date
     */
    OceanusDate getDate() {
        return theDate;
    }

    /**
     * Get the range name.
     * @return the name
     */
    String getRangeName() {
        return theRangeName;
    }
}
