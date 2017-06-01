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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket.MoneyWiseTaxBandBucket;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxYear;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;

/**
 * TaxCalculation report builder.
 */
public class MoneyWiseReportTaxCalculation
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.TAXCALC_TITLE.getValue();

    /**
     * The Income text.
     */
    private static final String TEXT_INCOME = MoneyWiseTaxResource.TAXBANDS_INCOME.getValue();

    /**
     * The Rate text.
     */
    private static final String TEXT_RATE = MoneyWiseDataResource.MONEYWISEDATA_FIELD_RATE.getValue();

    /**
     * The TaxDue text.
     */
    private static final String TEXT_TAXDUE = MoneyWiseTaxResource.TAXBANDS_TAXDUE.getValue();

    /**
     * The Profit text.
     */
    private static final String TEXT_PROFIT = MoneyWiseTaxResource.TAXYEAR_PROFIT.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportTaxCalculation(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
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
        MoneyWiseTaxAnalysis myTaxAnalysis = pAnalysis.getTaxAnalysis();
        MoneyWiseTaxYear myYear = myTaxAnalysis.getTaxYear();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myYear.getYearEnd()));

        /* Format the header */
        MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseDataTypeResource.TAXBASIS_NAME.getValue());
        theBuilder.makeTitleCell(myTable, TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, TEXT_TAXDUE);

        /* Loop through the Tax Due Buckets */
        Iterator<MoneyWiseTaxDueBucket> myTaxIterator = myTaxAnalysis.taxDueIterator();
        while (myTaxIterator.hasNext()) {
            MoneyWiseTaxDueBucket myBucket = myTaxIterator.next();

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
        theBuilder.makeTotalCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
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
                              final MoneyWiseTaxDueBucket pSummary) {
        /* Format the detail */
        MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, TEXT_RATE);
        theBuilder.makeTitleCell(myTable, TEXT_TAXDUE);

        /* Loop through the Transaction Detail Buckets */
        Iterator<MoneyWiseTaxBandBucket> myIterator = pSummary.taxBandIterator();
        while (myIterator.hasNext()) {
            MoneyWiseTaxBandBucket myBucket = myIterator.next();

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, myBucket.getAmount());
            theBuilder.makeValueCell(myTable, myBucket.getRate());
            theBuilder.makeValueCell(myTable, myBucket.getTaxDue());
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pSummary.getTaxBasis().toString());
    }
}
