/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data;

import java.time.Month;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;

/**
 * Loan Status.
 */
public enum CoeusLoanStatus {
    /**
     * Offered.
     */
    OFFERED,

    /**
     * Active.
     */
    ACTIVE,

    /**
     * PoorHealth.
     */
    POORLY,

    /**
     * BadDebt.
     */
    BADDEBT,

    /**
     * Repaid.
     */
    REPAID,

    /**
     * Rejected.
     */
    REJECTED;

    /**
     * The Date from which BadDebts are charged against interest.
     */
    private static final TethysDate BADDEBT_BOUNDARY = TethysFiscalYear.UK.endOfYear(new TethysDate(2015, Month.JANUARY, 1));

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CoeusResource.getKeyForLoanStatus(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Is the status a badDebt?
     * @return true/false
     */
    public boolean isBadDebt() {
        return this == CoeusLoanStatus.BADDEBT;
    }

    /**
     * Is this an interest badDebt?
     * @param pDate the date
     * @return true/false
     */
    public static boolean isCapitalBadDebt(final TethysDate pDate) {
        return BADDEBT_BOUNDARY.compareTo(pDate) >= 0;
    }
}
