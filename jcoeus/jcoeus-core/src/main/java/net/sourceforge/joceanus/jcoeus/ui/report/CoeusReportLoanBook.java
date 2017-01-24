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

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jcoeus.data.CoeusLoan;
import net.sourceforge.joceanus.jcoeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.jcoeus.data.CoeusResource;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * LoanBook Report.
 */
public class CoeusReportLoanBook
        extends MetisReportBase<CoeusMarketSnapShot, CoeusFilter> {
    /**
     * Loan Id text.
     */
    private static final String TEXT_LOANID = CoeusResource.DATA_LOANID.getValue();

    /**
     * StartDate text.
     */
    private static final String TEXT_STARTDATE = CoeusResource.DATA_STARTDATE.getValue();

    /**
     * InitialLoan text.
     */
    private static final String TEXT_INITIALLOAN = CoeusResource.DATA_LENT.getValue();

    /**
     * BadDebtDate text.
     */
    private static final String TEXT_BADDEBTDATE = CoeusResource.DATA_BADDEBTDATE.getValue();

    /**
     * BadDebtDate text.
     */
    private static final String TEXT_BADDEBT = CoeusResource.DATA_BADDEBT.getValue();

    /**
     * LastDate text.
     */
    private static final String TEXT_LASTDATE = CoeusResource.DATA_LASTDATE.getValue();

    /**
     * BadDebtDate text.
     */
    private static final String TEXT_RECOVERED = CoeusResource.DATA_RECOVERED.getValue();

    /**
     * Outstanding text.
     */
    private static final String TEXT_OUTSTANDING = "Outstanding";

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Market.
     */
    private CoeusMarketSnapShot theMarket;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected CoeusReportLoanBook(final MetisReportManager<CoeusFilter> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final CoeusMarketSnapShot pMarket) {
        /* Store the market */
        theMarket = pMarket;

        /* Access the date and totals */
        TethysDate myDate = theMarket.getDate();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, "Loan Book for " + theMarket.getMarket().getProvider().toString(), theFormatter.formatObject(myDate));

        /* Initialise the active Loan table */
        theBuilder.makeSubTitle(myBody, "Active Loans");
        MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_LOANID);
        theBuilder.makeTitleCell(myTable, TEXT_STARTDATE);
        theBuilder.makeTitleCell(myTable, TEXT_INITIALLOAN);
        theBuilder.makeTitleCell(myTable, TEXT_LASTDATE);
        theBuilder.makeTitleCell(myTable, TEXT_OUTSTANDING);

        /* Loop through the loans */
        Iterator<CoeusLoan> myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            CoeusLoan myLoan = myIterator.next();

            /* Skip if loan is not active */
            if (!CoeusLoanStatus.ACTIVE.equals(myLoan.getStatus())) {
                continue;
            }

            /* Access details */
            String myId = myLoan.getLoanId();
            CoeusTotals myTotals = myLoan.getTotals();

            /* Create the row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myId);
            theBuilder.makeValueCell(myTable, myLoan.getStartDate());
            theBuilder.makeValueCell(myTable, myLoan.getInitialLoan());
            theBuilder.makeValueCell(myTable, myLoan.getLastDate());
            theBuilder.makeValueCell(myTable, myTotals.getLoanBook());

            /* Record the filter */
            setFilterForId(myId, myLoan);
        }

        /* If we have badDebt */
        if (theMarket.hasBadDebt()) {
            /* Initialise the badDebt Loan table */
            theBuilder.makeSubTitle(myBody, "BadDebt Loans");
            myTable = theBuilder.startTable(myBody);
            theBuilder.startTotalRow(myTable);
            theBuilder.makeTitleCell(myTable, TEXT_LOANID);
            theBuilder.makeTitleCell(myTable, TEXT_INITIALLOAN);
            theBuilder.makeTitleCell(myTable, TEXT_BADDEBTDATE);
            theBuilder.makeTitleCell(myTable, TEXT_BADDEBT);
            theBuilder.makeTitleCell(myTable, TEXT_RECOVERED);
            theBuilder.makeTitleCell(myTable, TEXT_LASTDATE);
            theBuilder.makeTitleCell(myTable, TEXT_OUTSTANDING);

            /* Loop through the loans */
            myIterator = theMarket.loanIterator();
            while (myIterator.hasNext()) {
                CoeusLoan myLoan = myIterator.next();

                /* Skip if loan is not badDebt */
                if (!CoeusLoanStatus.BADDEBT.equals(myLoan.getStatus())) {
                    continue;
                }

                /* Access details */
                String myId = myLoan.getLoanId();
                CoeusTotals myTotals = myLoan.getTotals();

                /* Create the row */
                theBuilder.startRow(myTable);
                theBuilder.makeFilterLinkCell(myTable, myId);
                theBuilder.makeValueCell(myTable, myLoan.getInitialLoan());
                theBuilder.makeValueCell(myTable, myLoan.getBadDebtDate());
                theBuilder.makeValueCell(myTable, myTotals.getBadDebt());
                theBuilder.makeValueCell(myTable, myTotals.getRecovered());
                theBuilder.makeValueCell(myTable, myLoan.getLastDate());
                theBuilder.makeValueCell(myTable, myTotals.getLosses());

                /* Record the filter */
                setFilterForId(myId, myLoan);
            }
        }

        /* Initialise the closed Loan table */
        theBuilder.makeSubTitle(myBody, "Closed Loans");
        myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_LOANID);
        theBuilder.makeTitleCell(myTable, TEXT_INITIALLOAN);
        theBuilder.makeTitleCell(myTable, TEXT_LASTDATE);

        /* Loop through the loans */
        myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            CoeusLoan myLoan = myIterator.next();

            /* Skip if loan is not rePaid */
            if (!CoeusLoanStatus.REPAID.equals(myLoan.getStatus())) {
                continue;
            }

            /* Access details */
            String myId = myLoan.getLoanId();

            /* Create the row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myId);
            theBuilder.makeValueCell(myTable, myLoan.getInitialLoan());
            theBuilder.makeValueCell(myTable, myLoan.getLastDate());

            /* Record the filter */
            setFilterForId(myId, myLoan);
        }

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public CoeusFilter processFilter(final Object pSource) {
        /* If this is a Loan */
        if (pSource instanceof CoeusLoan) {
            /* Create the new filter */
            CoeusLoan myLoan = (CoeusLoan) pSource;
            CoeusSnapShotFilter myFilter = new CoeusSnapShotFilter(theMarket);
            myFilter.setLoan(myLoan);
            return myFilter;
        }
        return null;
    }
}
