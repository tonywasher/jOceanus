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
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.TableControl;
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

        /* Initialise the table */
        TableControl myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, "Cost");
        theBuilder.makeTitleCell(myTable, "Valuation");
        theBuilder.makeTitleCell(myTable, "Gains");
        theBuilder.makeTitleCell(myTable, "Profit");

        /* Loop through the Account Buckets */
        Iterator<AccountBucket> myIterator = myAccounts.iterator();
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Only process priced asset types */
            if (myBucket.getCategoryType() != CategoryType.Priced) {
                continue;
            }

            /* Format the Asset */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Cost));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.MarketValue));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Gained));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Profit));

            /* Format the detail */
            makeCapitalEventReport(myTable, myBucket);
        }

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Cost));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.MarketValue));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Gained));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Profit));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a capital event report element.
     * @param pParent the parent table
     * @param pAsset the asset to report on
     */
    public void makeCapitalEventReport(final TableControl pParent,
                                       final AccountBucket pAsset) {
        /* Access the investment analyses */
        InvestmentAnalysisList myList = pAsset.getInvestmentAnalyses();

        /* Create an embedded table */
        TableControl myTable = theBuilder.startEmbeddedTable(pParent, pAsset.getName(), false);

        /* Format the header */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Date");
        theBuilder.makeTitleCell(myTable, "Category");
        theBuilder.makeTitleCell(myTable, "DeltaUnits");
        theBuilder.makeTitleCell(myTable, "DeltaCost");
        theBuilder.makeTitleCell(myTable, "DeltaGains");
        theBuilder.makeTitleCell(myTable, "Dividend");

        /* Loop through the Analyses */
        Iterator<InvestmentAnalysis> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            InvestmentAnalysis myAnalysis = myIterator.next();

            /* Skip record if this is not based on an event (at present) */
            if (myAnalysis.getEvent() == null) {
                continue;
            }

            /* Format the detail */
            String myDate = theFormatter.formatObject(myAnalysis.getDate());
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, myDate);
            theBuilder.makeValueCell(myTable, myAnalysis.getCategory());
            theBuilder.makeValueCell(myTable, myAnalysis.getUnitsAttribute(InvestmentAttribute.DeltaUnits));
            theBuilder.makeValueCell(myTable, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaCost));
            theBuilder.makeValueCell(myTable, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaGains));
            theBuilder.makeValueCell(myTable, myAnalysis.getMoneyAttribute(InvestmentAttribute.DeltaDividend));
        }
    }
}
