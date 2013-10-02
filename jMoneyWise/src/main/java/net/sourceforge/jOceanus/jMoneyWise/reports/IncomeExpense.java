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
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.TableControl;
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

        /* Initialise the table */
        TableControl myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_PROFIT);

        /* Loop through the SubTotal Buckets */
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            EventCategoryClass myClass = myBucket.getEventCategoryType().getCategoryClass();
            if (!myClass.isSubTotal()) {
                continue;
            }

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Add the subSection */
            createSubSection(myTable, myBucket);
        }

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(EventAttribute.Income));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(EventAttribute.Expense));
        theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(EventAttribute.IncomeDelta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void createSubSection(final TableControl pParent,
                                  final EventCategoryBucket pCategory) {
        /* Access the category */
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        EventCategory myCategory = pCategory.getEventCategory();

        /* Create an embedded table */
        TableControl myTable = theBuilder.startEmbeddedTable(pParent, myCategory.getName(), false);

        /* Loop through the Category Buckets */
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            EventCategory myCurr = myBucket.getEventCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myBucket.getName(), myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Record the selection */
            theManager.setFilterForId(myBucket.getName(), myBucket);
        }
    }
}
