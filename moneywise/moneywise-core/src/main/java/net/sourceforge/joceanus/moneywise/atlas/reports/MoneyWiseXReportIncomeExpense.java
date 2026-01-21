/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.atlas.reports;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.report.MetisReportBase;
import io.github.tonywasher.joceanus.metis.report.MetisReportHTMLBuilder;
import io.github.tonywasher.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import io.github.tonywasher.joceanus.metis.report.MetisReportManager;
import io.github.tonywasher.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransValues;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTransCategoryFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * Income/Expense report builder.
 */
public class MoneyWiseXReportIncomeExpense
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.INCEXP_TITLE.getValue();

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
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     *
     * @param pManager the Report Manager
     */
    MoneyWiseXReportIncomeExpense(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the bucket list */
        theAnalysis = pAnalysis;
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = theAnalysis.getTransCategories();
        final OceanusDateRange myRange = theAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final MoneyWiseXAnalysisTransCategoryBucket myTotals = myCategories.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_PROFIT);

        /* Loop through the SubTotal Buckets */
        final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* Only process subTotal items */
            final MoneyWiseTransCategoryClass myClass = myBucket.getTransactionCategoryType().getCategoryClass();
            if (!myClass.isSubTotal()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisTransValues myValues = myBucket.getValues();

            /* Format the Category Total */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.PROFIT));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        final MoneyWiseXAnalysisTransValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.PROFIT));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseXAnalysisTransCategoryBucket mySourceBucket) {
            return createDelayedCategory(pTable.getParent(), mySourceBucket);
        }

        /* Return the null table */
        return null;
    }

    /**
     * Create a delayed category table.
     *
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    protected MetisHTMLTable createDelayedCategory(final MetisHTMLTable pParent,
                                                   final MoneyWiseXAnalysisTransCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = theAnalysis.getTransCategories();
        final MoneyWiseTransCategory myCategory = pSource.getTransactionCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseXAnalysisTransCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTransCategoryBucket myBucket = myIterator.next();

            /* Skip record if incorrect category */
            final MoneyWiseTransCategory myCurr = myBucket.getTransactionCategory();
            if (!MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisTransValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTransAttr.PROFIT));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseXAnalysisTransCategoryFilter processFilter(final Object pSource) {
        /* If this is an EventCategoryBucket */
        if (pSource instanceof MoneyWiseXAnalysisTransCategoryBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisTransCategoryFilter(mySource);
        }
        return null;
    }
}
