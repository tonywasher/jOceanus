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
import net.sourceforge.joceanus.jmoneywise.analysis.ChargeableEvent;
import net.sourceforge.joceanus.jmoneywise.analysis.ChargeableEvent.ChargeableEventList;
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

        /* If we need a tax slice report */
        if (myList.hasGainsSlices()) {
            makeTaxSliceReport(myBody);
        }

        /* Format the tax parameters */
        makeTaxParameters(myBody, myYear);

        /* Format the header */
        theBuilder.makeSubTitle(myBody, "New Taxation");
        myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseDataTypeResource.TAXBASIS_NAME.getValue());
        theBuilder.makeTitleCell(myTable, TEXT_INCOME);
        theBuilder.makeTitleCell(myTable, TEXT_TAXDUE);

        /* Loop through the Tax Calculation Buckets */
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
     * Build a web output of the taxation parameters.
     * @param pBody the report body
     * @param pYear the taxYear
     */
    public void makeTaxParameters(final Element pBody,
                                  final TaxYear pYear) {
        /* Obtain details as to the tax regime */
        boolean hasAdditionalBand = pYear.hasAdditionalTaxBand();
        boolean hasResidentialGains = pYear.hasResidentialCapitalGains();
        boolean hasAgeAllowance = pYear.hasAgeRelatedAllowance();
        boolean hasSavingsAllowance = pYear.hasSavingsAllowance();

        /* Access the bucket lists */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Format the tax parameters */
        theBuilder.makeTitle(pBody, "Taxation Parameters");

        /* Format the allowances */
        theBuilder.makeSubTitle(pBody, "Allowances");
        HTMLTable myTable = theBuilder.startTable(pBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, "Name");
        theBuilder.makeTitleCell(myTable, "Value");
        theBuilder.startRow(myTable);
        theBuilder.makeValueCell(myTable, "PersonalAllowance");
        theBuilder.makeValueCell(myTable, pYear.getAllowance());
        if (hasAgeAllowance) {
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "Age 65-74 PersonalAllowance");
            theBuilder.makeValueCell(myTable, pYear.getLoAgeAllow());
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "Age 75+ PersonalAllowance");
            theBuilder.makeValueCell(myTable, pYear.getHiAgeAllow());
        }
        theBuilder.startRow(myTable);
        theBuilder.makeValueCell(myTable, "RentalAllowance");
        theBuilder.makeValueCell(myTable, pYear.getRentalAllowance());
        if (hasSavingsAllowance) {
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "SavingsAllowance");
            theBuilder.makeValueCell(myTable, pYear.getSavingsAllow());
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "HiSavingsAllowance");
            theBuilder.makeValueCell(myTable, pYear.getHiSavingsAllow());
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "DividendAllowance");
            theBuilder.makeValueCell(myTable, pYear.getDividendAllow());
        }
        theBuilder.startRow(myTable);
        theBuilder.makeValueCell(myTable, "CapitalAllowance");
        theBuilder.makeValueCell(myTable, pYear.getCapitalAllow());
        if (hasAdditionalBand) {
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "Income Limit for PersonalAllowance");
            theBuilder.makeValueCell(myTable, pYear.getAddAllowLimit());
        }
        if (hasAgeAllowance) {
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, "Income Limit for AgeAllowance");
            theBuilder.makeValueCell(myTable, pYear.getAgeAllowLimit());
        }

        /* Format the Rates */
        theBuilder.makeSubTitle(pBody, "TaxRates");
        myTable = theBuilder.startTable(pBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, "IncomeType");
        theBuilder.makeTitleCell(myTable, "LoRate");
        theBuilder.makeTitleCell(myTable, "BasicRate");
        theBuilder.makeTitleCell(myTable, "HiRate");
        if (hasAdditionalBand) {
            theBuilder.makeTitleCell(myTable, "AdditionalRate");
        }
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Salary/Rental");
        theBuilder.makeValueCell(myTable, pYear.hasLoSalaryBand()
                                                                  ? pYear.getLoTaxRate()
                                                                  : null);
        theBuilder.makeValueCell(myTable, pYear.getBasicTaxRate());
        theBuilder.makeValueCell(myTable, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myTable, pYear.getAddTaxRate());
        }
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Interest");
        theBuilder.makeValueCell(myTable, pYear.getLoTaxRate());
        theBuilder.makeValueCell(myTable, pYear.getIntTaxRate());
        theBuilder.makeValueCell(myTable, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myTable, pYear.getAddTaxRate());
        }
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Dividends");
        theBuilder.makeValueCell(myTable);
        theBuilder.makeValueCell(myTable, pYear.getDivTaxRate());
        theBuilder.makeValueCell(myTable, pYear.getHiDivTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myTable, pYear.getAddDivTaxRate());
        }
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "TaxableGains");
        theBuilder.makeValueCell(myTable);
        theBuilder.makeValueCell(myTable, pYear.getBasicTaxRate());
        theBuilder.makeValueCell(myTable, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myTable, pYear.getAddTaxRate());
        }
        if (hasResidentialGains) {
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, "ResidentialGains");
            theBuilder.makeValueCell(myTable);
            theBuilder.makeValueCell(myTable, pYear.getResidentTaxRate());
            theBuilder.makeValueCell(myTable, pYear.getHiResidentTaxRate());
            if (hasAdditionalBand) {
                theBuilder.makeValueCell(myTable);
            }
        }
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "CapitalGains");
        theBuilder.makeValueCell(myTable);
        theBuilder.makeValueCell(myTable, pYear.getCapTaxRate());
        theBuilder.makeValueCell(myTable, pYear.getHiCapTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myTable);
        }

        /* Format the tax bands */
        theBuilder.makeSubTitle(pBody, "TaxBands");
        myTable = theBuilder.startTable(pBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, "Name");
        theBuilder.makeTitleCell(myTable, "Value");
        if (hasAgeAllowance) {
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, "Age for Tax Year");
            theBuilder.makeValueCell(myTable, myList.getAge());
        }

        /* Access the original allowance */
        TaxCalcBucket myTax = myList.getBucket(TaxCategoryClass.ORIGINALALLOWANCE);
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Personal Allowance");
        theBuilder.makeValueCell(myTable, myTax.getMoneyValue(TaxAttribute.AMOUNT));

        /* if we have adjusted the allowance */
        if (myList.hasReducedAllow()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.GROSSINCOME);
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, "Gross Taxable Income");
            theBuilder.makeValueCell(myTable, myTax.getMoneyValue(TaxAttribute.AMOUNT));

            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.ADJUSTEDALLOWANCE);
            theBuilder.makeTitleCell(myTable, "Adjusted Allowance");
            theBuilder.makeValueCell(myTable, myTax.getMoneyValue(TaxAttribute.AMOUNT));
        }

        /* Access the Low Tax Band */
        if (pYear.getLoBand().isNonZero()) {
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, "Low Tax Band");
            theBuilder.makeValueCell(myTable, pYear.getLoBand());
        }

        /* Access the Basic Tax Band */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, "Basic Tax Band");
        theBuilder.makeValueCell(myTable, pYear.getBasicBand());

        /* If we have a high tax band */
        if (pYear.hasAdditionalTaxBand()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.HITAXBAND);
            theBuilder.startRow(myTable);
            theBuilder.makeTitleCell(myTable, "High Tax Band");
            theBuilder.makeValueCell(myTable, myTax.getMoneyValue(TaxAttribute.AMOUNT));
        }
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

    /**
     * Build a tax slice report.
     * @param pBody the document body
     */
    public void makeTaxSliceReport(final Element pBody) {
        /* Access the bucket lists */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();
        ChargeableEventList myCharges = theAnalysis.getCharges();

        /* Format the detail */
        theBuilder.makeSubTitle(pBody, "Chargeable Events");
        HTMLTable myTable = theBuilder.startTable(pBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable, "Date");
        theBuilder.makeTitleCell(myTable, "Description");
        theBuilder.makeTitleCell(myTable, "Amount");
        theBuilder.makeTitleCell(myTable, "Tax Credit");
        theBuilder.makeTitleCell(myTable, "Years");
        theBuilder.makeTitleCell(myTable, "Slice");
        theBuilder.makeTitleCell(myTable, "Taxation");

        /* Loop through the Charges */
        Iterator<ChargeableEvent> myIterator = theAnalysis.getCharges().iterator();
        while (myIterator.hasNext()) {
            ChargeableEvent myCharge = myIterator.next();

            /* Format the detail */
            String myDate = theFormatter.formatObject(myCharge.getDate());
            theBuilder.startRow(myTable);
            theBuilder.makeValueCell(myTable, myDate);
            theBuilder.makeValueCell(myTable, myCharge.getComments());
            theBuilder.makeValueCell(myTable, myCharge.getAmount());
            theBuilder.makeValueCell(myTable, myCharge.getTaxCredit());
            theBuilder.makeValueCell(myTable, myCharge.getYears());
            theBuilder.makeValueCell(myTable, myCharge.getSlice());
            theBuilder.makeValueCell(myTable, myCharge.getTaxation());
        }

        /* Format the totals */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTotalCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTotalCell(myTable, myCharges.getGainsTotal());
        theBuilder.makeTotalCell(myTable);
        theBuilder.makeTotalCell(myTable, myCharges.getSliceTotal());
        theBuilder.makeTotalCell(myTable, myCharges.getTaxTotal());

        /* Access the Summary Tax Due Slice */
        TaxCalcBucket myTax = myList.getBucket(TaxCategoryClass.TAXDUESLICE);

        /* Add the Slice taxation details */
        makeTaxReport(myTable, myTax);
    }
}
