/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeValues;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.PayeeFilter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CashFlow report builder.
 */
public class CashFlow
        extends BasicReport {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = ReportResource.CASHFLOW_TITLE.getValue();

    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final JDataFormatter theFormatter;

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
        PayeeBucketList myPayees = pAnalysis.getPayees();
        JDateDayRange myRange = pAnalysis.getDateRange();

        /* Obtain the totals bucket */
        PayeeBucket myTotals = myPayees.getTotals();

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

        /* Loop through the Payee Buckets */
        Iterator<PayeeBucket> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            PayeeBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            PayeeValues myValues = myBucket.getValues();

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(PayeeAttribute.INCOME));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(PayeeAttribute.EXPENSE));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(PayeeAttribute.DELTA));

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Access values */
        PayeeValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(PayeeAttribute.INCOME));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(PayeeAttribute.EXPENSE));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(PayeeAttribute.DELTA));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    protected PayeeFilter processFilter(final Object pSource) {
        /* If this is an PayeeBucket */
        if (pSource instanceof PayeeBucket) {
            /* Create the new filter */
            return new PayeeFilter((PayeeBucket) pSource);
        }
        return null;
    }
}
