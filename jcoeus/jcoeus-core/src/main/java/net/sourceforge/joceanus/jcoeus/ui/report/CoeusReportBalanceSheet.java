/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jcoeus.data.CoeusMarketSnapShot;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusSnapShotFilter;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * BalanceSheet Report.
 */
public class CoeusReportBalanceSheet
        extends MetisReportBase<CoeusMarketSnapShot, CoeusFilter> {
    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Market.
     */
    private CoeusMarketSnapShot theMarket;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected CoeusReportBalanceSheet(final MetisReportManager<CoeusFilter> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final CoeusMarketSnapShot pMarket) {
        /* Store the market */
        theMarket = pMarket;

        /* Access the date and totals */
        final TethysDate myDate = theMarket.getDate();
        final CoeusTotals myTotals = theMarket.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, "Balance Sheet for " + theMarket.getMarket().getProvider().toString(), theFormatter.formatObject(myDate));

        /* Initialise the source table */
        theBuilder.makeSubTitle(myBody, "Source");
        MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, "Total Source");
        theBuilder.makeTotalCell(myTable, myTotals.getSourceValue());

        /* Create the Invested row */
        makeTableFilterRow(myTable, CoeusTotalSet.INVESTED, myTotals.getInvested());

        /* Create the Earnings row */
        makeTableFilterRow(myTable, CoeusTotalSet.EARNINGS, myTotals.getEarnings());

        /* Create an embedded table */
        MetisHTMLTable mySubTable = theBuilder.createEmbeddedTable(myTable);

        /* Create the Interest row */
        makeTableFilterRow(mySubTable, CoeusTotalSet.INTEREST, myTotals.getInterest());

        /* Create the Fees row */
        makeTableFilterRow(mySubTable, CoeusTotalSet.FEES, myTotals.getFees());

        /* Create the Shield row */
        makeTableFilterRow(mySubTable, CoeusTotalSet.SHIELD, myTotals.getShield());

        /* Create the CashBack row */
        makeTableFilterRow(mySubTable, CoeusTotalSet.CASHBACK, myTotals.getCashBack());

        /* Create the XferPayment row */
        makeTableFilterRow(mySubTable, CoeusTotalSet.XFERPAYMENT, myTotals.getXferPayment());
        theBuilder.embedTable(mySubTable);

        /* If we have badDebt */
        if (theMarket.hasBadDebt()) {
            /* Create the Losses row */
            makeTableFilterRow(myTable, CoeusTotalSet.LOSSES, myTotals.getLosses());

            /* Create an embedded table */
            mySubTable = theBuilder.createEmbeddedTable(myTable);

            /* Create the BadDebt row */
            makeTableFilterRow(mySubTable, CoeusTotalSet.BADDEBT, myTotals.getBadDebt());

            /* Create the Recovered row */
            makeTableFilterRow(mySubTable, CoeusTotalSet.RECOVERED, myTotals.getRecovered());
            theBuilder.embedTable(mySubTable);
        }

        /* Initialise the assets table */
        theBuilder.makeSubTitle(myBody, "Assets");
        myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, "Total Assets");
        theBuilder.makeTotalCell(myTable, myTotals.getAssetValue());

        /* Create the Holding row */
        makeTableFilterRow(myTable, CoeusTotalSet.HOLDING, myTotals.getHolding());

        /* Create the LoanBook row */
        makeTableFilterRow(myTable, CoeusTotalSet.LOANBOOK, myTotals.getLoanBook());

        /* Determine the number of days in the period */
        final TethysDateRange myRange = theMarket.getDateRange();
        final long numDays = myRange.getNumDays();

        /* Create the AssetRoR row */
        TethysRatio myRoR = myTotals.getAssetRoR();
        makeTableFilterRow(myTable, CoeusTotalSet.ASSETROR, myRoR.annualise(numDays));

        /* Create the LoanRoR row */
        myRoR = myTotals.getLoanRoR();
        makeTableFilterRow(myTable, CoeusTotalSet.LOANROR, myRoR.annualise(numDays));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Make Table Filter Row.
     * @param pTable the table
     * @param pTotalSet the totalSet
     * @param pValue the value
     */
    private void makeTableFilterRow(final MetisHTMLTable pTable,
                                    final CoeusTotalSet pTotalSet,
                                    final TethysDecimal pValue) {
        /* If the value is non-zero */
        if (pValue.isNonZero()) {
            /* Create the BadDebt row */
            theBuilder.startRow(pTable);
            theBuilder.makeFilterLinkCell(pTable, pTotalSet.toString());
            theBuilder.makeTotalCell(pTable, pValue);

            /* Record the filter */
            setFilterForId(pTotalSet.toString(), pTotalSet);
        }
    }

    @Override
    public CoeusFilter processFilter(final Object pSource) {
        /* If this is a TotalSet */
        if (pSource instanceof CoeusTotalSet) {
            /* Create the new filter */
            final CoeusTotalSet myTotalSet = (CoeusTotalSet) pSource;
            final CoeusSnapShotFilter myFilter = new CoeusSnapShotFilter(theMarket);
            myFilter.setTotalSet(myTotalSet);
            return myFilter;
        }
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}
