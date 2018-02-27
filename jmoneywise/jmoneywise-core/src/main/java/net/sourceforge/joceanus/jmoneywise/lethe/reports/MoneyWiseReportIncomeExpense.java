/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket.CategoryValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.TransactionCategoryFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Income/Expense report builder.
 */
public class MoneyWiseReportIncomeExpense
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.INCEXP_TITLE.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportIncomeExpense(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        final TransactionCategoryBucketList myCategories = theAnalysis.getTransCategories();
        final TethysDateRange myRange = theAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final TransactionCategoryBucket myTotals = myCategories.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_PROFIT);

        /* Loop through the SubTotal Buckets */
        final Iterator<TransactionCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            final TransactionCategoryClass myClass = myBucket.getTransactionCategoryType().getCategoryClass();
            if (!myClass.isSubTotal()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final CategoryValues myValues = myBucket.getValues();

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.INCOME));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.EXPENSE));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.PROFIT));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        final CategoryValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.INCOME));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.EXPENSE));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.PROFIT));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof TransactionCategoryBucket) {
            final TransactionCategoryBucket mySourceBucket = (TransactionCategoryBucket) mySource;
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
    protected MetisHTMLTable createDelayedCategory(final MetisHTMLTable pParent,
                                                   final TransactionCategoryBucket pSource) {
        /* Access the category */
        final TransactionCategoryBucketList myCategories = theAnalysis.getTransCategories();
        final TransactionCategory myCategory = pSource.getTransactionCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<TransactionCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final TransactionCategory myCurr = myBucket.getTransactionCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final CategoryValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.INCOME));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.EXPENSE));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TransactionAttribute.PROFIT));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public TransactionCategoryFilter processFilter(final Object pSource) {
        /* If this is an EventCategoryBucket */
        if (pSource instanceof TransactionCategoryBucket) {
            /* Create the new filter */
            return new TransactionCategoryFilter((TransactionCategoryBucket) pSource);
        }
        return null;
    }
}
