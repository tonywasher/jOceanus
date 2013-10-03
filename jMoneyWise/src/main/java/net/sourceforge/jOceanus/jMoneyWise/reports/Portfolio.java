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
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.HTMLTable;
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
        extends BasicReport<AccountBucket, Object> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Portfolio.class.getName());

    /**
     * The Cost text.
     */
    private static final String TEXT_COST = NLS_BUNDLE.getString("ReportCost");

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = NLS_BUNDLE.getString("ReportValue");

    /**
     * The Gains text.
     */
    private static final String TEXT_GAINS = NLS_BUNDLE.getString("ReportGains");

    /**
     * The Date text.
     */
    private static final String TEXT_DATE = NLS_BUNDLE.getString("ReportDate");

    /**
     * The Category text.
     */
    private static final String TEXT_CAT = NLS_BUNDLE.getString("ReportCat");

    /**
     * The Delta Units text.
     */
    private static final String TEXT_DUNIT = NLS_BUNDLE.getString("ReportDUnits");

    /**
     * The Delta Cost text.
     */
    private static final String TEXT_DCOST = NLS_BUNDLE.getString("ReportDCost");

    /**
     * The Delta Gains text.
     */
    private static final String TEXT_DGAIN = NLS_BUNDLE.getString("ReportDGains");

    /**
     * The Dividend text.
     */
    private static final String TEXT_DIV = NLS_BUNDLE.getString("ReportDividend");

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
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_COST);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_PROFIT);

        /* Loop through the Account Buckets */
        Iterator<AccountBucket> myIterator = myAccounts.iterator();
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Only process priced asset types */
            if (myBucket.getCategoryType() != CategoryType.Priced) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Format the Asset */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Cost));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.MarketValue));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Gained));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Profit));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
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

    @Override
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the investment analyses */
        AccountBucket myAsset = pTable.getSource();
        InvestmentAnalysisList myList = myAsset.getInvestmentAnalyses();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pTable.getParent());

        /* Format the header */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_DATE);
        theBuilder.makeTitleCell(myTable, TEXT_CAT);
        theBuilder.makeTitleCell(myTable, TEXT_DUNIT);
        theBuilder.makeTitleCell(myTable, TEXT_DCOST);
        theBuilder.makeTitleCell(myTable, TEXT_DGAIN);
        theBuilder.makeTitleCell(myTable, TEXT_DIV);

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

        /* Return the table */
        return myTable;
    }
}
