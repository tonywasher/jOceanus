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
package net.sourceforge.joceanus.moneywise.lethe.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeValues;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPayeeFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * CashFlow report builder.
 */
public class MoneyWiseReportCashFlow
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.CASHFLOW_TITLE.getValue();

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
    protected MoneyWiseReportCashFlow(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseAnalysisPayeeBucketList myPayees = pAnalysis.getPayees();
        final TethysDateRange myRange = pAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final MoneyWiseAnalysisPayeeBucket myTotals = myPayees.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_EXPENSE);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_PROFIT);

        /* Loop through the Payee Buckets */
        final Iterator<MoneyWiseAnalysisPayeeBucket> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPayeeBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisPayeeValues myValues = myBucket.getValues();

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.PROFIT));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Access values */
        final MoneyWiseAnalysisPayeeValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.INCOME));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.EXPENSE));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisPayeeAttr.PROFIT));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MoneyWiseAnalysisPayeeFilter processFilter(final Object pSource) {
        /* If this is an PayeeBucket */
        if (pSource instanceof MoneyWiseAnalysisPayeeBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisPayeeFilter((MoneyWiseAnalysisPayeeBucket) pSource);
        }
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}
