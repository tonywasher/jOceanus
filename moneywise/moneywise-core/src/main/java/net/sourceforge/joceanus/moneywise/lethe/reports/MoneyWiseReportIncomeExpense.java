/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.lethe.reports;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisCategoryValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTransCategoryFilter;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * Income/Expense report builder.
 */
public class MoneyWiseReportIncomeExpense
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
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
    private final OceanusDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private MoneyWiseAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportIncomeExpense(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        final MoneyWiseAnalysisTransCategoryBucketList myCategories = theAnalysis.getTransCategories();
        final OceanusDateRange myRange = theAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final MoneyWiseAnalysisTransCategoryBucket myTotals = myCategories.getTotals();

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
        final Iterator<MoneyWiseAnalysisTransCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            final MoneyWiseTransCategoryClass myClass = myBucket.getTransactionCategoryType().getCategoryClass();
            if (!myClass.isSubTotal()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisCategoryValues myValues = myBucket.getValues();

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        final MoneyWiseAnalysisCategoryValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseAnalysisTransCategoryBucket) {
            final MoneyWiseAnalysisTransCategoryBucket mySourceBucket = (MoneyWiseAnalysisTransCategoryBucket) mySource;
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
                                                   final MoneyWiseAnalysisTransCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseAnalysisTransCategoryBucketList myCategories = theAnalysis.getTransCategories();
        final MoneyWiseTransCategory myCategory = pSource.getTransactionCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisTransCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final MoneyWiseTransCategory myCurr = myBucket.getTransactionCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisCategoryValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.INCOME));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.EXPENSE));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseAnalysisTransCategoryFilter processFilter(final Object pSource) {
        /* If this is an EventCategoryBucket */
        if (pSource instanceof MoneyWiseAnalysisTransCategoryBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisTransCategoryFilter((MoneyWiseAnalysisTransCategoryBucket) pSource);
        }
        return null;
    }
}
