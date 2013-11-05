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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.CategoryValues;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Income/Expense report builder.
 */
public class IncomeExpenseAlt
        extends BasicReportAlt {
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
    protected IncomeExpenseAlt(final ReportManagerAlt pManager) {
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
        EventCategoryBucket myTotals = myCategories.getTotals();

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

            /* Access values */
            CategoryValues myValues = myBucket.getValues();

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Income));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Expense));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Delta));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        CategoryValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Income));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Expense));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Delta));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        Object mySource = pTable.getSource();
        if (mySource instanceof EventCategoryBucket) {
            EventCategoryBucket mySourceBucket = (EventCategoryBucket) mySource;
            return createDelayedCategory(pTable.getParent(), mySourceBucket);
        }

        /* Return the null table */
        return null;
    }

    /**
     * Create a delayed category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    protected HTMLTable createDelayedCategory(final HTMLTable pParent,
                                              final EventCategoryBucket pSource) {
        /* Access the category */
        EventCategoryBucketList myCategories = theAnalysis.getEventCategories();
        EventCategory myCategory = pSource.getEventCategory();

        /* Create an embedded table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

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

            /* Access values */
            CategoryValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Income));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Expense));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(EventAttribute.Delta));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    protected void processFilter(final Object pSource) {
        /* Create the new filter */
        // EventFilter myFilter = new EventFilter(theAnalysis.getData());
        // myFilter.setFilter(pSource);
    }
}
