/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.test.data.xdoc;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * XDoc Report Asset Builder.
 */
public class MoneyWiseDataXDocAssets {
    /**
     * Report.
     */
    private final MoneyWiseDataXDocReport theReport;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The parent payees.
     */
    private final List<MoneyWisePayee> theParents;

    /**
     * The deposit builder.
     */
    private final MoneyWiseDataXDocDeposit theDeposits;

    /**
     * The cash builder.
     */
    private final MoneyWiseDataXDocCash theCash;

    /**
     * The loan builder.
     */
    private final MoneyWiseDataXDocLoan theLoans;

    /**
     * The portfolio builder.
     */
    private final MoneyWiseDataXDocPortfolio thePortfolios;

    /**
     * The securities builder.
     */
    private final MoneyWiseDataXDocSecurity theSecurities;

    /**
     * Constructor.
     *
     * @param pReport the report
     * @param pTest   the test case
     */
    MoneyWiseDataXDocAssets(final MoneyWiseDataXDocReport pReport,
                            final MoneyWiseDataTestCase pTest) {
        /* Store parameters */
        theReport = pReport;
        theAnalysis = pTest.getAnalysis();
        theParents = new ArrayList<>();

        /* Create submodules */
        theDeposits = new MoneyWiseDataXDocDeposit(pReport, pTest, theParents);
        theCash = new MoneyWiseDataXDocCash(pReport, pTest);
        theLoans = new MoneyWiseDataXDocLoan(pReport, pTest, theParents);
        theSecurities = new MoneyWiseDataXDocSecurity(pReport, pTest, theParents);
        thePortfolios = new MoneyWiseDataXDocPortfolio(pReport, pTest, theParents, theSecurities);
    }

    /**
     * Obtain parent payees.
     *
     * @return the parents list
     */
    List<MoneyWisePayee> getParentPayees() {
        return theParents;
    }

    /**
     * create asset details.
     */
    void createAssetDefinitions() {
        theDeposits.createDepositDefinitions();
        theCash.createCashDefinitions();
        theLoans.createLoanDefinitions();
        thePortfolios.createPortfolioDefinitions();
        theSecurities.createSecurityDefinitions();
    }

    /**
     * create asset analysis table.
     */
    void createAssetAnalysis() {
        /* Create asset headers */
        final int myNumCols = createAssetHeaders();

        /* Loop through the events */
        final MoneyWiseXAnalysisEventList myEvents = theAnalysis.getEvents();
        final Iterator<MoneyWiseXAnalysisEvent> myEvtIterator = myEvents.iterator();
        while (myEvtIterator.hasNext()) {
            final MoneyWiseXAnalysisEvent myEvent = myEvtIterator.next();

            /* Only process events/prices/XchgRates/openingBalance */
            final MoneyWiseXAnalysisEventType myType = myEvent.getEventType();
            switch (myEvent.getEventType()) {
                case SECURITYPRICE:
                case XCHANGERATE:
                case TRANSACTION:
                case OPENINGBALANCE:
                    break;
                default:
                    continue;
            }

            /* Create the new row */
            theReport.newRow();
            theReport.newCell();
            theReport.setCellValue(myEvent.getDate());

            /* Create the detail */
            boolean nonEmpty = theDeposits.updateDepositAssetRow(myEvent);
            nonEmpty |= theCash.updateCashAssetRow(myEvent);
            nonEmpty |= theLoans.updateLoanAssetRow(myEvent);
            nonEmpty |= thePortfolios.updatePortfolioAssetRow(myEvent);
            if (nonEmpty) {
                theReport.addRowToTable();
            }
        }

        /* Create the totals row */
        theReport.newRow();
        theReport.newCell();
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_PROFIT);
        theReport.newBoldSpanCell(myNumCols);
        theReport.setCellValue(getValueDelta());
        theReport.addRowToTable();

        /* Create the holdings history */
        theSecurities.createHoldingHistory();
    }

    /**
     * create asset analysis headers.
     *
     * @return the number of columns
     */
    int createAssetHeaders() {
        /* Create detail and table */
        theReport.newOpenDetail(MoneyWiseDataXDocBuilder.GRP_ANALYSIS, "AssetAnalysis");
        theReport.newTable();

        /* Determine whether there are foreign assets */
        final boolean haveForeign = haveForeignAssets();

        /* Add the date header */
        theReport.newRow();
        if (haveForeign) {
            theReport.newRowSpanHeader(2);
        } else {
            theReport.newHeader();
        }
        theReport.setCellValue(MoneyWiseDataXDocBuilder.HDR_DATE);

        /* Add main headers */
        int myNumColumns = theDeposits.createMainDepositHeaders(haveForeign);
        myNumColumns += theCash.createMainCashHeaders(haveForeign);
        myNumColumns += theLoans.createMainLoanHeaders(haveForeign);
        myNumColumns += thePortfolios.createMainPortfolioHeaders(haveForeign);
        theReport.addRowToTable();

        /* If we have foreign assets */
        if (haveForeign) {
            /* Add currency headers */
            theReport.newRow();
            theDeposits.createForeignDepositHeaders();
            theCash.createForeignCashHeaders();
            theLoans.createForeignLoanHeaders();
            thePortfolios.createForeignPortfolioHeaders();
            theReport.addRowToTable();
        }

        /* Return the number of columns */
        return myNumColumns;
    }

    /**
     * Are there any foreign assets?
     *
     * @return true/false
     */
    private boolean haveForeignAssets() {
        return theDeposits.haveForeignAssets()
                || theCash.haveForeignAssets()
                || theLoans.haveForeignAssets()
                || thePortfolios.haveForeignAssets();
    }

    /**
     * Obtain value delta.
     *
     * @return the value delta
     */
    private OceanusMoney getValueDelta() {
        /* Create running total */
        final OceanusMoney myTotal = new OceanusMoney(theAnalysis.getCurrency().getCurrency());
        myTotal.addAmount(theAnalysis.getDepositCategories().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        myTotal.addAmount(theAnalysis.getCashCategories().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        myTotal.addAmount(theAnalysis.getLoanCategories().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        myTotal.addAmount(theAnalysis.getPortfolios().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA));
        return myTotal;
    }
}
