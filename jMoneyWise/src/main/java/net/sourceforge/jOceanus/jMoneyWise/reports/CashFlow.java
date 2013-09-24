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
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CashFlow report builder.
 */
public class CashFlow
        implements MoneyWiseReport {
    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Report Manager.
     */
    private final ReportManager theManager;

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
    protected CashFlow(final ReportManager pManager) {
        /* Store values */
        theManager = pManager;

        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        AccountBucketList myList = theAnalysis.getAccounts();
        AccountCategoryBucketList myCategories = theAnalysis.getAccountCategories();
        JDateDayRange myRange = theAnalysis.getDateRange();
        StringBuilder myBuffer = new StringBuilder();

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Cash Flow Report for ");
        myBuffer.append(theFormatter.formatObject(myRange));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, ReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myRow, ReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myRow, ReportBuilder.TEXT_PROFIT);
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Create the bucket iterator */
        boolean isOdd = true;
        Iterator<AccountBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip bucket if this is not a payee account */
            if (myBucket.getCategoryType() != CategoryType.Payee) {
                continue;
            }

            /* Format the detail */
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateRow(myTBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Income));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.Expense));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(AccountAttribute.IncomeDelta));

            /* Record the selection */
            theManager.setSelectionForId(myBucket.getName(), myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the total */
        myRow = theBuilder.startTotalRow(myTBody, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Income));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.Expense));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(AccountAttribute.IncomeDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }
}
