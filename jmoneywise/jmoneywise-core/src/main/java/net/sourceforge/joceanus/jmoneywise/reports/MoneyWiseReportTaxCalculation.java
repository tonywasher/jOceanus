/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataTypeResource;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCalcBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCalcBucket.TaxAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCalcBucket.TaxCalcBucketList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategorySection;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket.MoneyWiseTaxBandBucket;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
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
     * Data Analysis.
     */
    private Analysis theAnalysis;

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
        /* Store the analysis */
        theAnalysis = pAnalysis;

        /* Access the bucket lists */
        MoneyWiseTaxAnalysis myTaxAnalysis = theAnalysis.getTaxAnalysis();
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();
        TaxBasisBucketList myBasis = theAnalysis.getTaxBasis();
        TaxYear myYear = myList.getTaxYear();
        TaxCalcBucket myTax;

        /* Make sure that the tax is calculated */
        myList.calculateTax();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myYear.getTaxYear()));

        /* Format the header */
        theBuilder.makeSubTitle(myBody, "Taxation Summary");
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, "Class");
        theBuilder.makeTitleCell(myTable, "Total Income");
        theBuilder.makeTitleCell(myTable, "Taxation Due");

        /* Loop through the Tax Calculation Buckets */
        Iterator<TaxCalcBucket> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            TaxCalcBucket myBucket = myIterator.next();

            /* Skip the non-summary elements */
            if (myBucket.getCategorySection() != TaxCategorySection.TAXSUMM) {
                continue;
            }

            /* Format the line */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, myBucket.getName());
            theBuilder.makeValueCell(myTable, myBucket.getMoneyValue(TaxAttribute.AMOUNT));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyValue(TaxAttribute.TAXATION));

            /* Format the detail */
            makeTaxReport(myTable, myBucket);
        }

        /* Access the Total taxation bucket */
        myTax = myList.getBucket(TaxCategoryClass.TOTALTAXATIONDUE);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, myTax.getName());
        theBuilder.makeTotalCell(myTable, myTax.getMoneyValue(TaxAttribute.AMOUNT));
        theBuilder.makeTotalCell(myTable, myTax.getMoneyValue(TaxAttribute.TAXATION));

        /* Access the Tax Paid bucket */
        TaxBasisBucket myTaxPaid = myBasis.getBucket(TaxBasisClass.TAXPAID);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, myTaxPaid.getName());
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTotalCell(myTable, myTaxPaid.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the Tax Profit bucket */
        myTax = myList.getBucket(TaxCategoryClass.TAXPROFITLOSS);
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, myTax.getName());
        theBuilder.makeTotalCell(myTable, myTax.getMoneyValue(TaxAttribute.AMOUNT));
        theBuilder.makeTotalCell(myTable, myTax.getMoneyValue(TaxAttribute.TAXATION));

        /* Format the header */
        theBuilder.makeSubTitle(myBody, "New Taxation");
        myTable = theBuilder.startTable(myBody);
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
    public void makeTaxReport(final HTMLTable pParent,
                              final TaxCalcBucket pSummary) {
        /* Access the bucket lists */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Format the detail */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Class");
        theBuilder.makeTitleCell(myTable, "Income");
        theBuilder.makeTitleCell(myTable, "Rate");
        theBuilder.makeTitleCell(myTable, "Taxation Due");

        /* Loop through the Transaction Detail Buckets */
        Iterator<TaxCalcBucket> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            TaxCalcBucket myBucket = myIterator.next();

            /* Skip non-detail buckets */
            if (myBucket.getCategorySection() != TaxCategorySection.TAXDETAIL) {
                continue;
            }

            /* Skip record if incorrect parent */
            if (!pSummary.equals(myBucket.getParent())) {
                continue;
            }

            /* Format the detail */
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, myBucket.getName());
            theBuilder.makeValueCell(myTable, myBucket.getMoneyValue(TaxAttribute.AMOUNT));
            theBuilder.makeValueCell(myTable, myBucket.getRateValue(TaxAttribute.RATE));
            theBuilder.makeValueCell(myTable, myBucket.getMoneyValue(TaxAttribute.TAXATION));
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pSummary.getName());
    }

    /**
     * Build a standard tax report element.
     * @param pParent the parent table
     * @param pSummary the tax summary
     */
    public void makeTaxReport(final HTMLTable pParent,
                              final MoneyWiseTaxDueBucket pSummary) {
        /* Format the detail */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);
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
