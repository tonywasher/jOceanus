/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxDueBucket.MoneyWiseXTaxBandBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxYear;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * TaxCalculation report builder.
 */
public class MoneyWiseXReportTaxCalculation
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.TAXCALC_TITLE.getValue();

    /**
     * The Income text.
     */
    private static final String TEXT_INCOME = MoneyWiseXTaxResource.TAXBANDS_INCOME.getValue();

    /**
     * The Rate text.
     */
    private static final String TEXT_RATE = MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE.getValue();

    /**
     * The TaxDue text.
     */
    private static final String TEXT_TAXDUE = MoneyWiseXTaxResource.TAXBANDS_TAXDUE.getValue();

    /**
     * The Profit text.
     */
    private static final String TEXT_PROFIT = MoneyWiseXTaxResource.TAXYEAR_PROFIT.getValue();

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
    protected MoneyWiseXReportTaxCalculation(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    /**
     * Build a web output of the taxation report.
     * @param pAnalysis the analysis
     * @return Web output
     */
    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseXTaxAnalysis myTaxAnalysis = pAnalysis.getTaxAnalysis();
        final MoneyWiseXTaxYear myYear = myTaxAnalysis.getTaxYear();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myYear.getYearEnd()));

        /* Format the header */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseDataTypeResource.TAXBASIS_NAME.getValue());
        theBuilder.makeTitleCell(myTable, TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, TEXT_TAXDUE);

        /* Loop through the Tax Due Buckets */
        final Iterator<MoneyWiseXTaxDueBucket> myTaxIterator = myTaxAnalysis.taxDueIterator();
        while (myTaxIterator.hasNext()) {
            final MoneyWiseXTaxDueBucket myBucket = myTaxIterator.next();

            /* Format the line */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getTaxBasis().toString());
            theBuilder.makeValueCell(myTable, myBucket.getTaxableIncome());
            theBuilder.makeValueCell(myTable, myBucket.getTaxDue());

            /* Format the detail */
            makeTaxReport(myTable, myBucket);
        }

        /* Access the Totals */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTaxAnalysis.getTaxableIncome());
        theBuilder.makeTotalCell(myTable, myTaxAnalysis.getTaxDue());
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, TaxBasisClass.TAXPAID.toString());
        theBuilder.makeStretchedTotalCell(myTable, myTaxAnalysis.getTaxPaid());
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, TEXT_PROFIT);
        theBuilder.makeStretchedTotalCell(myTable, myTaxAnalysis.getTaxProfit());

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a standard tax report element.
     * @param pParent the parent table
     * @param pSummary the tax summary
     */
    public void makeTaxReport(final MetisHTMLTable pParent,
                              final MoneyWiseXTaxDueBucket pSummary) {
        /* Format the detail */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, TEXT_RATE);
        theBuilder.makeTitleCell(myTable, TEXT_TAXDUE);

        /* Loop through the Transaction Detail Buckets */
        final Iterator<MoneyWiseXTaxBandBucket> myIterator = pSummary.taxBandIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXTaxBandBucket myBucket = myIterator.next();

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, myBucket.getAmount());
            theBuilder.makeValueCell(myTable, myBucket.getRate());
            theBuilder.makeValueCell(myTable, myBucket.getTaxDue());
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pSummary.getTaxBasis().toString());
    }

    @Override
    public AnalysisFilter<?, ?> processFilter(final Object pSource) {
        return null;
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }
}