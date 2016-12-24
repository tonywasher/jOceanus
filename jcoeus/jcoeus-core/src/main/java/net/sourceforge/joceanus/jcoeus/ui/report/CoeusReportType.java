/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.report;

/**
 * Coeus Report Types.
 */
public enum CoeusReportType {
    /**
     * BalanceSheet Report.
     */
    BALANCESHEET,

    /**
     * LoanBook Report.
     */
    LOANBOOK,

    /**
     * Annual Report.
     */
    ANNUAL;

    /**
     * Report Name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CoeusReportResource.getKeyForReportType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Use annual date?
     * @return true/false
     */
    public boolean useAnnualDate() {
        return this == CoeusReportType.ANNUAL;
    }
}
