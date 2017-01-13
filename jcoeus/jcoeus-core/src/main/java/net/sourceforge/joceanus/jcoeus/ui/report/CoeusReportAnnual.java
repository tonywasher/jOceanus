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

import java.time.Month;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jcoeus.data.CoeusHistory;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter;
import net.sourceforge.joceanus.jcoeus.ui.CoeusFilter.CoeusAnnualFilter;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * TaxBook Report.
 */
public class CoeusReportAnnual
        extends MetisReportBase<CoeusMarketAnnual, CoeusFilter> {
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
    private CoeusMarketAnnual theMarket;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected CoeusReportAnnual(final MetisReportManager<CoeusFilter> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final CoeusMarketAnnual pMarket) {
        /* Store the market */
        theMarket = pMarket;

        /* Access the date and totals */
        TethysDate myDate = theMarket.getDate();
        boolean hasFees = theMarket.hasFees();
        boolean hasCashBack = theMarket.hasCashBack();
        boolean hasBadDebt = theMarket.hasBadDebt();
        boolean needTaxableEarnings = hasFees || hasCashBack || hasBadDebt;

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, "TaxBook for " + theMarket.getMarket().getProvider().toString(), theFormatter.formatObject(myDate));

        /* Initialise the tax table */
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, "Date");
        theBuilder.makeTitleCell(myTable, CoeusTotalSet.INTEREST.toString());

        /* Handle optional parts */
        if (hasFees) {
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.FEES.toString());
        }
        if (hasCashBack) {
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.CASHBACK.toString());
        }
        if (hasBadDebt) {
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.NETTINTEREST.toString());
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.BADDEBTINTEREST.toString());
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.BADDEBTCAPITAL.toString());
        }
        if (needTaxableEarnings) {
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.TAXABLEEARNINGS.toString());
        }

        /* Loop through the months */
        Iterator<CoeusHistory> myIterator = theMarket.monthlyIterator();
        while (myIterator.hasNext()) {
            CoeusHistory myHistory = myIterator.next();
            CoeusTotals myTotals = myHistory.getTotals();
            myDate = myHistory.getDate();
            Month myMonth = myDate.getMonthValue();

            /* Create the row */
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, myDate.toString());
            makeTableFilterCell(myTable, CoeusTotalSet.INTEREST, myMonth, myTotals.getInterest());

            /* Handle optional parts */
            if (hasFees) {
                makeTableFilterCell(myTable, CoeusTotalSet.FEES, myMonth, myTotals.getFees());
            }
            if (hasCashBack) {
                makeTableFilterCell(myTable, CoeusTotalSet.CASHBACK, myMonth, myTotals.getCashBack());
            }
            if (hasBadDebt) {
                makeTableFilterCell(myTable, CoeusTotalSet.NETTINTEREST, myMonth, myTotals.getNettInterest());
                makeTableFilterCell(myTable, CoeusTotalSet.BADDEBTINTEREST, myMonth, myTotals.getBadDebtInterest());
                makeTableFilterCell(myTable, CoeusTotalSet.BADDEBTCAPITAL, myMonth, myTotals.getBadDebtCapital());
            }
            if (needTaxableEarnings) {
                makeTableFilterCell(myTable, CoeusTotalSet.TAXABLEEARNINGS, myMonth, myTotals.getTaxableEarnings());
            }
        }

        /* Create the row */
        CoeusTotals myTotals = theMarket.getHistory().getTotals();
        theBuilder.startRow(myTable);
        theBuilder.makeTotalCell(myTable, "Totals");
        makeTableFilterCell(myTable, CoeusTotalSet.INTEREST, myTotals.getInterest());

        /* Handle optional parts */
        if (hasFees) {
            makeTableFilterCell(myTable, CoeusTotalSet.FEES, myTotals.getFees());
        }
        if (hasCashBack) {
            makeTableFilterCell(myTable, CoeusTotalSet.CASHBACK, myTotals.getCashBack());
        }
        if (hasBadDebt) {
            makeTableFilterCell(myTable, CoeusTotalSet.NETTINTEREST, myTotals.getNettInterest());
            makeTableFilterCell(myTable, CoeusTotalSet.BADDEBTINTEREST, myTotals.getBadDebtInterest());
            makeTableFilterCell(myTable, CoeusTotalSet.BADDEBTCAPITAL, myTotals.getBadDebtCapital());
        }
        if (needTaxableEarnings) {
            makeTableFilterCell(myTable, CoeusTotalSet.TAXABLEEARNINGS, myTotals.getTaxableEarnings());
        }

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Make Table Link Cell.
     * @param pTable the table
     * @param pTotalSet the totalSet
     * @param pValue the value
     */
    private void makeTableFilterCell(final HTMLTable pTable,
                                     final CoeusTotalSet pTotalSet,
                                     final TethysDecimal pValue) {
        makeTableFilterCell(pTable, pTotalSet, null, pValue);
    }

    /**
     * Make Table Link Cell.
     * @param pTable the table
     * @param pTotalSet the totalSet
     * @param pMonth the month
     * @param pValue the value
     */
    private void makeTableFilterCell(final HTMLTable pTable,
                                     final CoeusTotalSet pTotalSet,
                                     final Month pMonth,
                                     final TethysDecimal pValue) {
        /* Create the filter definition */
        CoeusFilterDefinition myDef = new CoeusFilterDefinition(pTotalSet, pMonth);
        String myId = myDef.getFilterId();

        /* Create the LinkCell */
        theBuilder.makeFilterLinkCell(pTable, myId, pValue);
        setFilterForId(myId, myDef);
    }

    @Override
    public CoeusFilter processFilter(final Object pSource) {
        /* If this is a Loan */
        if (pSource instanceof CoeusFilterDefinition) {
            /* Create the new filter */
            CoeusFilterDefinition myDef = (CoeusFilterDefinition) pSource;
            CoeusAnnualFilter myFilter = new CoeusAnnualFilter(theMarket, theMarket.getDate());
            myFilter.setMonth(myDef.theMonth);
            myFilter.setTotalSet(myDef.theTotalSet);
            return myFilter;
        }
        return null;
    }

    /**
     * Filter Definition.
     */
    private static final class CoeusFilterDefinition {
        /**
         * The TotalSet.
         */
        private final CoeusTotalSet theTotalSet;

        /**
         * The month.
         */
        private final Month theMonth;

        /**
         * Constructor.
         * @param pTotalSet the totalSet
         * @param pMonth the month
         */
        private CoeusFilterDefinition(final CoeusTotalSet pTotalSet,
                                      final Month pMonth) {
            theTotalSet = pTotalSet;
            theMonth = pMonth;
        }

        /**
         * Obtain filter id.
         * @return the filterId
         */
        private String getFilterId() {
            StringBuilder myBuilder = new StringBuilder("total");
            myBuilder.append(theTotalSet.toString());
            if (theMonth != null) {
                myBuilder.append(theMonth.toString());
            }
            return myBuilder.toString();
        }
    }
}
