/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.reports;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis.InvestmentAnalysisList;
import net.sourceforge.jOceanus.jMoneyWise.views.InvestmentAnalysis.InvestmentAttribute;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Portfolio (Market) report builder.
 */
public class Portfolio
        implements MoneyWiseReport {
    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected Portfolio(final ReportManager pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        AccountBucketList myAccounts = theAnalysis.getAccounts();
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        StringBuilder myBuffer = new StringBuilder();

        /* Access the totals */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Market Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange().getEnd()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, "Cost");
        theBuilder.makeTitleCell(myRow, "Valuation");
        theBuilder.makeTitleCell(myRow, "Gains");
        theBuilder.makeTitleCell(myRow, "Profit");
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Access the iterator */
        boolean isOdd = true;
        Iterator<AccountBucket> myIterator = myAccounts.iterator();

        /* Loop through the Account Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Only process priced asset types */
            if (myBucket.getCategoryType() != CategoryType.Priced) {
                continue;
            }

            /* Format the Asset */
            myRow = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Cost));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.MarketValue));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Gained));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Profit));

            /* Flip row type */
            isOdd = !isOdd;

            /* Format the detail */
            makeCapitalEventReport(myTBody, myColumns, myBucket);
        }

        /* Create the total row */
        myRow = theBuilder.startTotalRow(myTBody, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Cost));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.MarketValue));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Gained));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Profit));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a capital event report element.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pAsset the asset to report on
     */
    public void makeCapitalEventReport(final Element pBody,
                                       final Integer pNumColumns,
                                       final AccountBucket pAsset) {
        /* Access the investment analyses */
        InvestmentAnalysisList myList = pAsset.getInvestmentAnalyses();

        /* Create an embedded table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, pAsset.getName(), pNumColumns, false);

        /* Format the header */
        Element myBody = theBuilder.startTableBody(myTable);
        Element myRow = theBuilder.startDetailTitleRow(myBody, "Date");
        theBuilder.makeTitleCell(myRow, "Category");
        theBuilder.makeTitleCell(myRow, "DeltaUnits");
        theBuilder.makeTitleCell(myRow, "DeltaCost");
        theBuilder.makeTitleCell(myRow, "DeltaGains");
        theBuilder.makeTitleCell(myRow, "Dividend");

        /* Access the iterator */
        Iterator<InvestmentAnalysis> myIterator = myList.iterator();
        boolean isOdd = false;

        /* Loop through the Analyses */
        while (myIterator.hasNext()) {
            InvestmentAnalysis myAnalysis = myIterator.next();

            /* Skip record if this is not based on an event (at present) */
            if (myAnalysis.getEvent() == null) {
                continue;
            }

            /* Format the detail */
            String myDate = theFormatter.formatObject(myAnalysis.getDate());
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myDate)
                    : theBuilder.startAlternateRow(myBody, myDate);
            theBuilder.makeValueCell(myRow, myAnalysis.getCategory());
            theBuilder.makeValueCell(myRow, myAnalysis.getUnitsAttribute(InvestmentAttribute.DeltaUnits));
            theBuilder.makeValueCell(myRow, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaCost));
            theBuilder.makeValueCell(myRow, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaGains));
            theBuilder.makeValueCell(myRow, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaDividend));

            /* Flip row type */
            isOdd = !isOdd;
        }
    }
}
