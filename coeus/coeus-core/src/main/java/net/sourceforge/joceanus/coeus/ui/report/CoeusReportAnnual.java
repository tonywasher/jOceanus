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

import java.time.Month;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.coeus.data.CoeusHistory;
import net.sourceforge.joceanus.coeus.data.CoeusMarketAnnual;
import net.sourceforge.joceanus.coeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.coeus.data.CoeusTotals;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter;
import net.sourceforge.joceanus.coeus.ui.CoeusFilter.CoeusAnnualFilter;
import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

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
    private final TethysUIDataFormatter theFormatter;

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
        TethysDate myDate = theMarket.getDateRange().getEnd();
        final boolean hasFees = theMarket.hasFees();
        final boolean hasCashBack = theMarket.hasCashBack();
        final boolean hasShield = theMarket.hasShield();
        final boolean hasBadDebt = theMarket.hasBadDebt();
        final boolean needTaxableEarnings = hasFees || hasCashBack || hasBadDebt;

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, "TaxBook for " + theMarket.getMarket().getProvider().toString(), theFormatter.formatObject(myDate));

        /* Initialise the tax table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, "Date");
        theBuilder.makeTitleCell(myTable, CoeusTotalSet.INTEREST.toString());

        /* Handle optional parts */
        if (hasFees) {
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.FEES.toString());
        }
        if (hasShield) {
            theBuilder.makeTitleCell(myTable, CoeusTotalSet.SHIELD.toString());
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
        theBuilder.makeTitleCell(myTable, CoeusTotalSet.ASSETROR.toString());

        /* Loop through the months */
        final Iterator<CoeusHistory> myIterator = theMarket.monthlyIterator();
        while (myIterator.hasNext()) {
            final CoeusHistory myHistory = myIterator.next();
            final CoeusTotals myTotals = myHistory.getTotals();
            final TethysDateRange myRange = myHistory.getDateRange();
            myDate = myRange.getEnd();
            final Month myMonth = myDate.getMonthValue();

            /* Determine the number of days in the period */
            final long numDays = myRange.getNumDays();

            /* Create the row */
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, myDate.toString());
            makeTableFilterCell(myTable, CoeusTotalSet.INTEREST, myMonth, myTotals.getInterest());

            /* Handle optional parts */
            if (hasFees) {
                makeTableFilterCell(myTable, CoeusTotalSet.FEES, myMonth, myTotals.getFees());
            }
            if (hasShield) {
                makeTableFilterCell(myTable, CoeusTotalSet.SHIELD, myMonth, myTotals.getShield());
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

            /* Create RoR cell */
            final TethysRatio myRoR = myTotals.getAssetRoR();
            makeTableFilterCell(myTable, CoeusTotalSet.ASSETROR, myMonth, myRoR.annualise(numDays));
        }

        /* Create the totals row */
        final CoeusTotals myTotals = theMarket.getHistory().getTotals();
        theBuilder.startRow(myTable);
        theBuilder.makeTotalCell(myTable, "Totals");
        makeTableFilterCell(myTable, CoeusTotalSet.INTEREST, myTotals.getInterest());

        /* Handle optional parts */
        if (hasFees) {
            makeTableFilterCell(myTable, CoeusTotalSet.FEES, myTotals.getFees());
        }
        if (hasShield) {
            makeTableFilterCell(myTable, CoeusTotalSet.SHIELD, myTotals.getShield());
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

        /* Determine the number of days in the period */
        final TethysDateRange myRange = theMarket.getDateRange();
        final long numDays = myRange.getNumDays();

        /* Create RoR cell */
        final TethysRatio myRoR = myTotals.getAssetRoR();
        makeTableFilterCell(myTable, CoeusTotalSet.ASSETROR, myRoR.annualise(numDays));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Make Table Link Cell.
     * @param pTable the table
     * @param pTotalSet the totalSet
     * @param pValue the value
     */
    private void makeTableFilterCell(final MetisHTMLTable pTable,
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
    private void makeTableFilterCell(final MetisHTMLTable pTable,
                                     final CoeusTotalSet pTotalSet,
                                     final Month pMonth,
                                     final TethysDecimal pValue) {
        /* Create the filter definition */
        final CoeusFilterDefinition myDef = new CoeusFilterDefinition(pTotalSet, pMonth);
        final String myId = myDef.getFilterId();

        /* Create the LinkCell */
        theBuilder.makeFilterLinkCell(pTable, myId, pValue);
        setFilterForId(myId, myDef);
    }

    @Override
    public CoeusFilter processFilter(final Object pSource) {
        /* If this is a Loan */
        if (pSource instanceof CoeusFilterDefinition) {
            /* Create the new filter */
            final CoeusFilterDefinition myDef = (CoeusFilterDefinition) pSource;
            final CoeusAnnualFilter myFilter = new CoeusAnnualFilter(theMarket, theMarket.getDateRange().getEnd());
            myFilter.setMonth(myDef.getMonth());
            myFilter.setTotalSet(myDef.getTotalSet());
            return myFilter;
        }
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
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
        CoeusFilterDefinition(final CoeusTotalSet pTotalSet,
                              final Month pMonth) {
            theTotalSet = pTotalSet;
            theMonth = pMonth;
        }

        /**
         * Obtain totalSet.
         * @return the totalSet
         */
        CoeusTotalSet getTotalSet() {
            return theTotalSet;
        }

        /**
         * Obtain month.
         * @return the month
         */
        Month getMonth() {
            return theMonth;
        }

        /**
         * Obtain filter id.
         * @return the filterId
         */
        String getFilterId() {
            final StringBuilder myBuilder = new StringBuilder("total");
            myBuilder.append(theTotalSet.toString());
            if (theMonth != null) {
                myBuilder.append(theMonth.toString());
            }
            return myBuilder.toString();
        }
    }
}
