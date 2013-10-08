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
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AccountBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.EventFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CashFlow report builder.
 */
public class CashFlow
        extends BasicReport<AccountBucket, AccountBucket> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(CashFlow.class.getName());

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = NLS_BUNDLE.getString("ReportTitle");

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
    protected CashFlow(final ReportManager pManager) {
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

        /* Obtain the totals bucket */
        AccountCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_PROFIT);

        /* Create the bucket iterator */
        Iterator<AccountBucket> myIterator = myList.iterator();

        /* Loop through the Detail Buckets */
        while (myIterator.hasNext()) {
            AccountBucket myBucket = myIterator.next();

            /* Skip bucket if this is not a payee account */
            if (myBucket.getCategoryType() != CategoryType.Payee) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Income));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.Expense));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(AccountAttribute.IncomeDelta));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Income));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Expense));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.IncomeDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    protected void processFilter(final AccountBucket pSource) {
        /* Create the new filter */
        EventFilter myFilter = new EventFilter();
        myFilter.setFilter(pSource);
    }
}
