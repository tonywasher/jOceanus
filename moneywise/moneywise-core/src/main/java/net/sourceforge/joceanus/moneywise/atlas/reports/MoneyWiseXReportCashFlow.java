/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket.MoneyWiseXAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeValues;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisPayeeFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * CashFlow report builder.
 */
public class MoneyWiseXReportCashFlow
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.CASHFLOW_TITLE.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    MoneyWiseXReportCashFlow(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseXAnalysisPayeeBucketList myPayees = pAnalysis.getPayees();
        final TethysDateRange myRange = pAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final MoneyWiseXAnalysisPayeeBucket myTotals = myPayees.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_PROFIT);

        /* Loop through the Payee Buckets */
        final Iterator<MoneyWiseXAnalysisPayeeBucket> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPayeeBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisPayeeValues myValues = myBucket.getValues();

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.PROFIT));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Access values */
        final MoneyWiseXAnalysisPayeeValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisPayeeAttr.PROFIT));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MoneyWiseXAnalysisPayeeFilter processFilter(final Object pSource) {
        /* If this is an PayeeBucket */
        if (pSource instanceof MoneyWiseXAnalysisPayeeBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisPayeeFilter((MoneyWiseXAnalysisPayeeBucket) pSource);
        }
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}
