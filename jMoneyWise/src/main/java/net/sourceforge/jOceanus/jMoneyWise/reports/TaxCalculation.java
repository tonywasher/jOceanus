/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.reports;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategorySection;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxCategoryBucketList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TaxCalculation report builder.
 */
public class TaxCalculation
        implements MoneyWiseReport {
    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected TaxCalculation(final ReportManager pManager) {
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
        StringBuffer myBuffer = new StringBuffer();

        /* Obtain the TaxYear parameters */
        FinanceData myData = pAnalysis.getData();
        JDateDay myDate = pAnalysis.getDateRange().getEnd();
        TaxYear myYear = myData.getTaxYears().findTaxYearForDate(myDate);

        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        TaxCategoryBucket myTax;

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Taxation Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange()));
        theBuilder.makeTitle(myBody, myBuffer.toString());
        myBuffer.setLength(0);

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;
        myColumns++;

        /* Format the header */
        theBuilder.makeSubTitle(myBody, "Taxation Summary");
        Element myTable = theBuilder.startTable(myBody);
        Element myTHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myTHdr, "Class");
        theBuilder.makeTitleCell(myRow, "Total Income");
        theBuilder.makeTitleCell(myRow, "Taxation Due");
        Element myTBody = theBuilder.startTableBody(myTable);

        /* Access the tax bucket iterator */
        Iterator<TaxCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Tax Summary Buckets */
        while (myIterator.hasNext()) {
            TaxCategoryBucket myBucket = myIterator.next();

            /* Skip the non-summary elements */
            if (myBucket.getCategorySection() != TaxCategorySection.TAXSUMM) {
                continue;
            }

            /* Format the line */
            myRow = (isOdd)
                    ? theBuilder.startCategoryRow(myTBody, myBucket.getName())
                    : theBuilder.startAlternateCatRow(myTBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Amount));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Taxation));

            /* Format the detail */
            makeTaxReport(myTBody, myColumns, myBucket);

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Access the Total taxation bucket */
        myTax = myList.getBucket(TaxCategoryClass.TotalTaxationDue);
        myRow = theBuilder.startTotalRow(myTBody, myTax.getName());
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Taxation));

        /* Access the Tax Paid bucket */
        myTax = myList.getBucket(TaxCategoryClass.TaxPaid);
        myRow = theBuilder.startTotalRow(myTBody, myTax.getName());
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));

        /* Access the Tax Profit bucket */
        myTax = myList.getBucket(TaxCategoryClass.TaxProfitLoss);
        myRow = theBuilder.startTotalRow(myTBody, myTax.getName());
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        theBuilder.makeTotalCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Taxation));

        /* If we need a tax slice report */
        if (myList.hasGainsSlices()) {
            makeTaxSliceReport(myBody, myColumns);
        }

        /* Format the tax parameters */
        makeTaxParameters(myBody, myYear);

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
        boolean hasAdditionalBand = pYear.hasAdditionalTaxBand();

        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();

        /* Format the tax parameters */
        theBuilder.makeTitle(pBody, "Taxation Parameters");

        /* Format the allowances */
        theBuilder.makeSubTitle(pBody, "Allowances");
        Element myTable = theBuilder.startTable(pBody);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startDetailTitleRow(myHdr, "Name");
        theBuilder.makeTitleCell(myRow, "Value");
        Element myBody = theBuilder.startTableBody(myTable);
        myRow = theBuilder.startAlternateRow(myBody, "PersonalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getAllowance());
        myRow = theBuilder.startDetailRow(myBody, "Age 65-74 PersonalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getLoAgeAllow());
        myRow = theBuilder.startAlternateRow(myBody, "Age 75+ PersonalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getHiAgeAllow());
        myRow = theBuilder.startDetailRow(myBody, "RentalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getRentalAllowance());
        myRow = theBuilder.startAlternateRow(myBody, "CapitalAllowance");
        theBuilder.makeValueCell(myRow, pYear.getCapitalAllow());
        myRow = theBuilder.startDetailRow(myBody, "Income Limit for AgeAllowance");
        theBuilder.makeValueCell(myRow, pYear.getAgeAllowLimit());
        if (hasAdditionalBand) {
            myRow = theBuilder.startAlternateRow(myBody, "Income Limit for PersonalAllowance");
            theBuilder.makeValueCell(myRow, pYear.getAddAllowLimit());
        }

        /* Format the Rates */
        theBuilder.makeSubTitle(pBody, "TaxRates");
        myTable = theBuilder.startTable(pBody);
        myHdr = theBuilder.startTableHeader(myTable);
        myRow = theBuilder.startDetailTitleRow(myHdr, "IncomeType");
        theBuilder.makeTitleCell(myRow, "LoRate");
        theBuilder.makeTitleCell(myRow, "BasicRate");
        theBuilder.makeTitleCell(myRow, "HiRate");
        if (hasAdditionalBand) {
            theBuilder.makeTitleCell(myRow, "AdditionalRate");
        }
        myBody = theBuilder.startTableBody(myTable);
        myRow = theBuilder.startAlternateRow(myBody, "Salary/Rental");
        theBuilder.makeValueCell(myRow, pYear.hasLoSalaryBand()
                ? pYear.getLoTaxRate()
                : null);
        theBuilder.makeValueCell(myRow, pYear.getBasicTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddTaxRate());
        }
        myRow = theBuilder.startDetailRow(myBody, "Interest");
        theBuilder.makeValueCell(myRow, pYear.getLoTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getIntTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddTaxRate());
        }
        myRow = theBuilder.startAlternateRow(myBody, "Dividends");
        theBuilder.makeValueCell(myRow);
        theBuilder.makeValueCell(myRow, pYear.getDivTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiDivTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddDivTaxRate());
        }
        myRow = theBuilder.startDetailRow(myBody, "TaxableGains");
        theBuilder.makeValueCell(myRow);
        theBuilder.makeValueCell(myRow, pYear.getBasicTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow, pYear.getAddTaxRate());
        }
        myRow = theBuilder.startAlternateRow(myBody, "CapitalGains");
        theBuilder.makeValueCell(myRow);
        theBuilder.makeValueCell(myRow, pYear.getCapTaxRate());
        theBuilder.makeValueCell(myRow, pYear.getHiCapTaxRate());
        if (hasAdditionalBand) {
            theBuilder.makeValueCell(myRow);
        }

        /* Format the tax bands */
        theBuilder.makeSubTitle(pBody, "TaxBands");
        myTable = theBuilder.startTable(pBody);
        myHdr = theBuilder.startTableHeader(myTable);
        myRow = theBuilder.startDetailTitleRow(myHdr, "Name");
        theBuilder.makeTitleCell(myRow, "Value");
        myBody = theBuilder.startTableBody(myTable);
        myRow = theBuilder.startAlternateRow(myRow, "Age for Tax Year");
        theBuilder.makeValueCell(myRow, myList.getAge());

        /* Access the original allowance */
        TaxCategoryBucket myTax = myList.getBucket(TaxCategoryClass.OriginalAllowance);
        myRow = theBuilder.startDetailRow(myBody, "Personal Allowance");
        theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));

        /* if we have adjusted the allowance */
        if (myList.hasReducedAllow()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.GrossIncome);
            myRow = theBuilder.startAlternateRow(myBody, "Gross Taxable Income");
            theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));

            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.AdjustedAllowance);
            myRow = theBuilder.startDetailRow(myBody, "Adjusted Allowance");
            theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        }

        /* Access the Low Tax Band */
        boolean isOdd = true;
        if (pYear.getLoBand().isNonZero()) {
            myRow = theBuilder.startAlternateRow(myBody, "Low Tax Band");
            theBuilder.makeValueCell(myRow, pYear.getLoBand());
            isOdd = false;
        }

        /* Access the Basic Tax Band */
        myRow = (!isOdd)
                ? theBuilder.startDetailRow(myBody, "Basic Tax Band")
                : theBuilder.startAlternateRow(myBody, "Basic Tax Band");
        theBuilder.makeValueCell(myRow, pYear.getBasicBand());

        /* If we have a high tax band */
        if (pYear.hasAdditionalTaxBand()) {
            /* Access the gross income */
            myTax = myList.getBucket(TaxCategoryClass.HiTaxBand);
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, "High Tax Band")
                    : theBuilder.startAlternateRow(myBody, "High Tax Band");
            theBuilder.makeValueCell(myRow, myTax.getMoneyAttribute(TaxAttribute.Amount));
        }
    }

    /**
     * Build a standard tax report element.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     * @param pSummary the tax summary
     */
    public void makeTaxReport(final Element pBody,
                              final Integer pNumColumns,
                              final TaxCategoryBucket pSummary) {
        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();

        /* Format the detail */
        Element myTable = theBuilder.startEmbeddedTable(pBody, pSummary.getName(), pNumColumns, true);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startDetailTitleRow(myHdr, "Class");
        theBuilder.makeTitleCell(myRow, "Income");
        theBuilder.makeTitleCell(myRow, "Rate");
        theBuilder.makeTitleCell(myRow, "Taxation Due");
        Element myBody = theBuilder.startTableBody(myTable);

        /* Access the tax bucket iterator */
        Iterator<TaxCategoryBucket> myIterator = myList.iterator();
        boolean isOdd = true;

        /* Loop through the Transaction Detail Buckets */
        while (myIterator.hasNext()) {
            TaxCategoryBucket myBucket = myIterator.next();

            /* Skip non-detail buckets */
            if (myBucket.getCategorySection() != TaxCategorySection.TAXDETAIL) {
                continue;
            }

            /* Skip record if incorrect parent */
            if (!pSummary.equals(myBucket.getParent())) {
                continue;
            }

            /* Format the detail */
            myRow = (!isOdd)
                    ? theBuilder.startDetailRow(myBody, myBucket.getName())
                    : theBuilder.startAlternateRow(myBody, myBucket.getName());
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Amount));
            theBuilder.makeValueCell(myRow, myBucket.getRateAttribute(TaxAttribute.Rate));
            theBuilder.makeValueCell(myRow, myBucket.getMoneyAttribute(TaxAttribute.Taxation));

            /* Flip row type */
            isOdd = !isOdd;
        }
    }

    /**
     * Build a tax slice report.
     * @param pBody the table body
     * @param pNumColumns the number of table columns
     */
    public void makeTaxSliceReport(final Element pBody,
                                   final Integer pNumColumns) {
        /* Access the bucket lists */
        TaxCategoryBucketList myList = theAnalysis.getTaxCategories();
        ChargeableEventList myCharges = theAnalysis.getCharges();

        /* Determine number of columns */
        int myColumns = 1;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;
        myColumns++;

        /* Format the detail */
        Element myTable = theBuilder.startEmbeddedTable(pBody, "ChargeableEvents", pNumColumns, false);
        Element myHdr = theBuilder.startTableHeader(myTable);
        Element myRow = theBuilder.startTotalRow(myHdr, "Date");
        theBuilder.makeTitleCell(myRow, "Description");
        theBuilder.makeTitleCell(myRow, "Amount");
        theBuilder.makeTitleCell(myRow, "Tax Credit");
        theBuilder.makeTitleCell(myRow, "Years");
        theBuilder.makeTitleCell(myRow, "Slice");
        theBuilder.makeTitleCell(myRow, "Taxation");
        Element myBody = theBuilder.startTableBody(myTable);

        /* Create the list iterator */
        Iterator<ChargeableEvent> myIterator = theAnalysis.getCharges().iterator();
        boolean isOdd = true;

        /* Loop through the Charges */
        while (myIterator.hasNext()) {
            ChargeableEvent myCharge = myIterator.next();

            /* Format the detail */
            String myDate = theFormatter.formatObject(myCharge.getDate());
            myRow = (isOdd)
                    ? theBuilder.startDetailRow(myBody, myDate)
                    : theBuilder.startAlternateRow(myBody, myDate);
            theBuilder.makeValueCell(myRow, myCharge.getComments());
            theBuilder.makeValueCell(myRow, myCharge.getAmount());
            theBuilder.makeValueCell(myRow, myCharge.getTaxCredit());
            theBuilder.makeValueCell(myRow, myCharge.getYears());
            theBuilder.makeValueCell(myRow, myCharge.getSlice());
            theBuilder.makeValueCell(myRow, myCharge.getTaxation());

            /* Flip row type */
            isOdd = !isOdd;
        }

        /* Format the totals */
        myRow = theBuilder.startTotalRow(myBody, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow, myCharges.getGainsTotal());
        theBuilder.makeTotalCell(myRow);
        theBuilder.makeTotalCell(myRow, myCharges.getSliceTotal());
        theBuilder.makeTotalCell(myRow, myCharges.getTaxTotal());

        /* Access the Summary Tax Due Slice */
        TaxCategoryBucket myTax = myList.getBucket(TaxCategoryClass.TaxDueSlice);

        /* Add the Slice taxation details */
        makeTaxReport(myBody, myColumns, myTax);
    }
}
