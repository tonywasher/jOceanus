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

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryBucketList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Income/Expense report builder.
 */
public class IncomeExpense
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
    protected IncomeExpense(final ReportManager pManager) {
        /* Store values */
        theManager = pManager;

        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        StringBuilder myBuffer = new StringBuilder();

        /* Obtain the totals bucket */
        EventCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Income/Expense Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;

        /* Initialise the table */
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr);
        theBuilder.makeTitleCell(myRow, ReportBuilder.TEXT_INCOME);
        myColumns++;
        theBuilder.makeTitleCell(myRow, ReportBuilder.TEXT_EXPENSE);
        myColumns++;
        theBuilder.makeTitleCell(myRow, ReportBuilder.TEXT_PROFIT);
        myColumns++;
        Element myTotal = theBuilder.startTableBody(myTable);
        myTable = theBuilder.startEmbeddedTable(myTotal, ReportBuilder.TEXT_TOTAL, myColumns, true);
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Loop through the SubTotal Buckets */
        boolean isOdd = true;
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            EventCategoryClass myClass = myBucket.getEventCategoryType().getCategoryClass();
            if (!myClass.isSubTotal()) {
                continue;
            }

            /* Format the Category Total */
            Element myCategory = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myCategory, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Flip row type */
            isOdd = !isOdd;

            /* Add the subSection */
            createSubSection(myTBody, myColumns, myBucket);
        }

        /* Format the total */
        myRow = theBuilder.startTotalRow(myTotal, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(EventAttribute.Income));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(EventAttribute.Expense));
        theBuilder.makeTotalCell(myRow, myTotals.getMoneyAttribute(EventAttribute.IncomeDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pCategory the category bucket
     */
    private void createSubSection(final Element pBody,
                                  final Integer pNumColumns,
                                  final EventCategoryBucket pCategory) {
        /* Access the category */
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        EventCategory myCategory = pCategory.getEventCategory();

        /* Create an embedded table */
        Element myTable = theBuilder.startEmbeddedTable(pBody, myCategory.getName(), pNumColumns, false);
        Element myBody = theBuilder.startTableBody(myTable);

        /* Loop through the Category Buckets */
        boolean isOdd = true;
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            EventCategory myCurr = myBucket.getEventCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Create the SubCategory row */
            Element myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myCurr.getSubCategory(), myBucket.getName())
                    : theBuilder.startAlternateRow(myBody, myCurr.getSubCategory(), myBucket.getName());
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myRow, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Record the selection */
            theManager.setSelectionForId(myBucket.getName(), myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }
    }
}
