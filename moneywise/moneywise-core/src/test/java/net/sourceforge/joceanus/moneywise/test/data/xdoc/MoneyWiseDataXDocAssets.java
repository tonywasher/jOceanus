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
package net.sourceforge.joceanus.moneywise.test.data.xdoc;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEventType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestCase;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

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
        theCash = new MoneyWiseDataXDocCash(pReport, pTest, theParents);
        theLoans = new MoneyWiseDataXDocLoan(pReport, pTest, theParents);
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
            nonEmpty |= theLoans.updateDepositAssetRow(myEvent);
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
    }

    /**
     * create asset analysis headers.
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
        theReport.addRowToTable();

        /* If we have foreign assets */
        if (haveForeign) {
            /* Add currency headers */
            theReport.newRow();
            theDeposits.createForeignDepositHeaders();
            theCash.createForeignCashHeaders();
            theLoans.createForeignLoanHeaders();
            theReport.addRowToTable();
        }

        /* Return the number of columns */
        return myNumColumns;
    }

    /**
     * Are there any foreign assets?
     * @return true/false
     */
    private boolean haveForeignAssets() {
        return theDeposits.haveForeignAssets()
                || theCash.haveForeignAssets()
                || theLoans.haveForeignAssets();
    }

    /**
     * Obtain value delta
     * @return the value delta
     */
    private OceanusMoney getValueDelta() {
        /* Create running total */
        final OceanusMoney myTotal = new OceanusMoney(theAnalysis.getCurrency().getCurrency());
        myTotal.addAmount(theAnalysis.getDepositCategories().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        myTotal.addAmount(theAnalysis.getCashCategories().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        myTotal.addAmount(theAnalysis.getLoanCategories().getTotals().getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA));
        return myTotal;
    }
}
