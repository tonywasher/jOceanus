/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.ui.report;

import java.util.EnumMap;
import java.util.Map;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.coeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.coeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;

/**
 * Report Builder.
 */
public class CoeusReportBuilder {
    /**
     * The Report Manager.
     */
    private final MetisReportManager<CoeusFilter> theManager;

    /**
     * Map of allocated snapshot reports.
     */
    private final Map<CoeusReportType, MetisReportBase<CoeusMarketSnapShot, CoeusFilter>> theSnapShotMap;

    /**
     * Map of allocated annual reports.
     */
    private final Map<CoeusReportType, MetisReportBase<CoeusMarketAnnual, CoeusFilter>> theAnnualMap;

    /**
     * Constructor.
     * @param pManager the report manager
     */
    public CoeusReportBuilder(final MetisReportManager<CoeusFilter> pManager) {
        /* Record the details */
        theManager = pManager;

        /* Allocate maps */
        theSnapShotMap = new EnumMap<>(CoeusReportType.class);
        theAnnualMap = new EnumMap<>(CoeusReportType.class);
    }

    /**
     * Build a snapshot report.
     * @param pType the report type
     * @param pSnapShot the snapshot
     * @return the Web document
     */
    public Document createReport(final CoeusReportType pType,
                                 final CoeusMarketSnapShot pSnapShot) {
        /* Access existing report */
        MetisReportBase<CoeusMarketSnapShot, CoeusFilter> myReport = theSnapShotMap.get(pType);

        /* If we have not previously allocated this report */
        if (myReport == null) {
            /* Switch on the report type */
            switch (pType) {
                case BALANCESHEET:
                    myReport = new CoeusReportBalanceSheet(theManager);
                    break;
                case LOANBOOK:
                    myReport = new CoeusReportLoanBook(theManager);
                    break;
                default:
                    return null;
            }

            /* Store allocated report */
            theSnapShotMap.put(pType, myReport);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pSnapShot);
    }

    /**
     * Build an annual report.
     * @param pType the report type
     * @param pAnnual the annual
     * @return the Web document
     */
    public Document createReport(final CoeusReportType pType,
                                 final CoeusMarketAnnual pAnnual) {
        /* Access existing report */
        MetisReportBase<CoeusMarketAnnual, CoeusFilter> myReport = theAnnualMap.get(pType);

        /* If we have not previously allocated this report */
        if (myReport == null) {
            /* Handle report type */
            if (CoeusReportType.ANNUAL.equals(pType)) {
                myReport = new CoeusReportAnnual(theManager);
            } else {
                return null;
            }

            /* Store allocated report */
            theAnnualMap.put(pType, myReport);
        }

        /* Set up the report */
        theManager.setReport(myReport);

        /* Create the report */
        return myReport.createReport(pAnnual);
    }
}
