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

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Income/Expense report builder.
 */
public class IncomeExpense
        extends BasicReport<EventCategoryBucket, EventCategoryBucket> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(IncomeExpense.class.getName());

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
    protected IncomeExpense(final ReportManager pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        JDateDayRange myRange = theAnalysis.getDateRange();

        /* Obtain the totals bucket */
        EventCategoryBucket myTotals = myCategories.getTotalsBucket();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);
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

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
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

    @Override
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the category */
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        EventCategoryBucket mySource = pTable.getSource();
        EventCategory myCategory = mySource.getEventCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pTable.getParent());

        /* Loop through the Category Buckets */
        Iterator<EventCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            EventCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            EventCategory myCurr = myBucket.getEventCategory();
            if (!Difference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Income));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.Expense));
            theBuilder.makeTotalCell(myTable, myBucket.getMoneyAttribute(EventAttribute.IncomeDelta));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected void processFilter(final EventCategoryBucket pSource) {
        /* Create the new filter */
        EventFilter myFilter = new EventFilter();
        myFilter.setFilter(pSource);
    }
}
