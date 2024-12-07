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

import net.sourceforge.joceanus.coeus.data.CoeusLoan;
import net.sourceforge.joceanus.coeus.data.CoeusLoanStatus;
import net.sourceforge.joceanus.coeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.coeus.data.CoeusResource;
import net.sourceforge.joceanus.coeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

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
    private final OceanusDataFormatter theFormatter;

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
        final OceanusDate myDate = theMarket.getDate();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, "Loan Book for " + theMarket.getMarket().getProvider().toString(), theFormatter.formatObject(myDate));

        /* report on active loans */
        reportOnActiveLoans(myBody);

        /* If we have badDebt */
        if (theMarket.hasBadDebt()) {
            /* report on badDebt */
            reportOnBadDebt(myBody);
        }

        /* report on closed loans */
        reportOnClosedLoans(myBody);

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * create active loans report segment.
     * @param pBody the body element
     */
    public void reportOnActiveLoans(final Element pBody) {
        /* Initialise the active Loan table */
        theBuilder.makeSubTitle(pBody, "Active Loans");
        final MetisHTMLTable myTable = theBuilder.startTable(pBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_LOANID);
        theBuilder.makeTitleCell(myTable, TEXT_STARTDATE);
        theBuilder.makeTitleCell(myTable, TEXT_INITIALLOAN);
        theBuilder.makeTitleCell(myTable, TEXT_LASTDATE);
        theBuilder.makeTitleCell(myTable, TEXT_OUTSTANDING);

        /* Loop through the loans */
        final Iterator<CoeusLoan> myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();

            /* Skip if loan is not active */
            if (!CoeusLoanStatus.ACTIVE.equals(myLoan.getStatus())) {
                continue;
            }

            /* Access details */
            final String myId = myLoan.getLoanId();
            final CoeusTotals myTotals = myLoan.getTotals();

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
    }

    /**
     * create badDebt loans report segment.
     * @param pBody the body element
     */
    public void reportOnBadDebt(final Element pBody) {
        /* Initialise the badDebt Loan table */
        theBuilder.makeSubTitle(pBody, "BadDebt Loans");
        final MetisHTMLTable myTable = theBuilder.startTable(pBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_LOANID);
        theBuilder.makeTitleCell(myTable, TEXT_INITIALLOAN);
        theBuilder.makeTitleCell(myTable, TEXT_BADDEBTDATE);
        theBuilder.makeTitleCell(myTable, TEXT_BADDEBT);
        theBuilder.makeTitleCell(myTable, TEXT_RECOVERED);
        theBuilder.makeTitleCell(myTable, TEXT_LASTDATE);
        theBuilder.makeTitleCell(myTable, TEXT_OUTSTANDING);

        /* Loop through the loans */
        final Iterator<CoeusLoan> myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();

            /* Skip if loan is not badDebt */
            if (!CoeusLoanStatus.BADDEBT.equals(myLoan.getStatus())) {
                continue;
            }

            /* Access details */
            final String myId = myLoan.getLoanId();
            final CoeusTotals myTotals = myLoan.getTotals();

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

    /**
     * create closed loans report segment.
     * @param pBody the body element
     */
    public void reportOnClosedLoans(final Element pBody) {
        /* Initialise the closed Loan table */
        theBuilder.makeSubTitle(pBody, "Closed Loans");
        final MetisHTMLTable myTable = theBuilder.startTable(pBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_LOANID);
        theBuilder.makeTitleCell(myTable, TEXT_INITIALLOAN);
        theBuilder.makeTitleCell(myTable, TEXT_LASTDATE);

        /* Loop through the loans */
        final Iterator<CoeusLoan> myIterator = theMarket.loanIterator();
        while (myIterator.hasNext()) {
            final CoeusLoan myLoan = myIterator.next();

            /* Skip if loan is not rePaid */
            if (!CoeusLoanStatus.REPAID.equals(myLoan.getStatus())) {
                continue;
            }

            /* Access details */
            final String myId = myLoan.getLoanId();

            /* Create the row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myId);
            theBuilder.makeValueCell(myTable, myLoan.getInitialLoan());
            theBuilder.makeValueCell(myTable, myLoan.getLastDate());

            /* Record the filter */
            setFilterForId(myId, myLoan);
        }
    }

    @Override
    public CoeusFilter processFilter(final Object pSource) {
        /* If this is a Loan */
        if (pSource instanceof CoeusLoan) {
            /* Create the new filter */
            final CoeusLoan myLoan = (CoeusLoan) pSource;
            final CoeusSnapShotFilter myFilter = new CoeusSnapShotFilter(theMarket);
            myFilter.setLoan(myLoan);
            myFilter.setTotalSet(CoeusTotalSet.LOANBOOK);
            return myFilter;
        }
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}
