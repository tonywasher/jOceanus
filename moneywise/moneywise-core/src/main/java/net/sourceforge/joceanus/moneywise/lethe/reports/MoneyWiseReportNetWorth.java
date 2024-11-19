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

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket.MoneyWiseAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket.MoneyWiseAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket.MoneyWiseAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisValuesResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisCashFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisDepositFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisLoanFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisPortfolioCashFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * NetWorth report builder.
 */
public class MoneyWiseReportNetWorth
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.NETWORTH_TITLE.getValue();

    /**
     * The Asset text.
     */
    private static final String TEXT_ASSET = MoneyWiseReportResource.NETWORTH_ASSET.getValue();

    /**
     * The Units text.
     */
    private static final String TEXT_UNITS = MoneyWiseBasicResource.MONEYWISEDATA_FIELD_UNITS.getValue();

    /**
     * The Price text.
     */
    private static final String TEXT_PRICE = MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE.getValue();

    /**
     * The ForeignValue text.
     */
    private static final String TEXT_FOREIGNVALUE = MoneyWiseAnalysisValuesResource.ACCOUNTATTR_FOREIGNVALUE.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = MoneyWiseAnalysisValuesResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Account text.
     */
    private static final String TEXT_ACCOUNT = MoneyWiseAnalysisDataResource.BUCKET_ACCOUNT.getValue();

    /**
     * The Rate text.
     */
    private static final String TEXT_RATE = MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE.getValue();

    /**
     * The Maturity text.
     */
    private static final String TEXT_MATURITY = MoneyWiseAnalysisValuesResource.ACCOUNTATTR_MATURITY.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private MoneyWiseAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportNetWorth(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        final MoneyWiseAnalysisDepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        final MoneyWiseAnalysisCashCategoryBucketList myCash = theAnalysis.getCashCategories();
        final MoneyWiseAnalysisLoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();
        final OceanusDate myDate = theAnalysis.getDateRange().getEnd();

        /* Create the totals */
        final OceanusMoney myTotal = new OceanusMoney();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);

        /* If we have deposits */
        if (!myDeposits.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseAnalysisDepositCategoryBucket> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisDepositCategoryBucket myBucket = myIterator.next();

                /* Only process active subTotal items */
                if (!myBucket.isActive()
                        || !myBucket.getAccountCategory().isCategoryClass(MoneyWiseDepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseAnalysisDepositCategoryBucket myTotals = myDeposits.getTotals();
            final MoneyWiseAnalysisAccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
        }

        /* If we have cash */
        if (!myCash.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseAnalysisCashCategoryBucket> myIterator = myCash.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisCashCategoryBucket myBucket = myIterator.next();

                /* Only process active subTotal items */
                if (!myBucket.isActive()
                        || !myBucket.getAccountCategory().isCategoryClass(MoneyWiseCashCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseAnalysisCashCategoryBucket myTotals = myCash.getTotals();
            final MoneyWiseAnalysisAccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
        }

        /* If we have portfolios */
        if (!myPortfolios.isEmpty()) {
            /* Access totals */
            final MoneyWiseAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();
            final MoneyWiseAnalysisSecurityValues myValues = myTotals.getValues();
            final OceanusMoney myValuation = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);

            /* Format the Portfolios Total */
            theBuilder.startRow(myTable);
            theBuilder.makeTableLinkCell(myTable, MoneyWiseBasicDataType.PORTFOLIO.getListName());
            theBuilder.makeTotalCell(myTable, myValuation);

            /* Make the portfolio report */
            makePortfolioReport(myTable);

            /* Add to running totals */
            myTotal.addAmount(myValuation);
        }

        /* If we have loans */
        if (!myLoans.isEmpty()) {
            /* Loop through the SubTotal Buckets */
            final Iterator<MoneyWiseAnalysisLoanCategoryBucket> myIterator = myLoans.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisLoanCategoryBucket myBucket = myIterator.next();

                /* Only process active subTotal items */
                if (!myBucket.isActive()
                        || !myBucket.getAccountCategory().isCategoryClass(MoneyWiseLoanCategoryClass.PARENT)) {
                    continue;
                }

                /* Access values */
                final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

                /* Format the Category Total */
                theBuilder.startRow(myTable);
                theBuilder.makeTableLinkCell(myTable, myBucket.getName());
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));

                /* Add the category report */
                makeCategoryReport(myTable, myBucket);
            }

            /* Access totals */
            final MoneyWiseAnalysisLoanCategoryBucket myTotals = myLoans.getTotals();
            final MoneyWiseAnalysisAccountValues myValues = myTotals.getValues();

            /* Add to running totals */
            myTotal.addAmount(myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
        }

        /* Build the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotal);

        /* Return the document */
        return theBuilder.getDocument();
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final MetisHTMLTable pParent,
                                    final MoneyWiseAnalysisDepositCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseAnalysisDepositCategoryBucketList myCategories = theAnalysis.getDepositCategories();
        final MoneyWiseDepositCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisDepositCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisDepositCategoryBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            final MoneyWiseDepositCategory myCurr = myBucket.getAccountCategory();
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final MetisHTMLTable pParent,
                                    final MoneyWiseAnalysisCashCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseAnalysisCashCategoryBucketList myCategories = theAnalysis.getCashCategories();
        final MoneyWiseCashCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisCashCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashCategoryBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            final MoneyWiseCashCategory myCurr = myBucket.getAccountCategory();
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a category report.
     * @param pParent the table parent
     * @param pCategory the category bucket
     */
    private void makeCategoryReport(final MetisHTMLTable pParent,
                                    final MoneyWiseAnalysisLoanCategoryBucket pCategory) {
        /* Access the category */
        final MoneyWiseAnalysisLoanCategoryBucketList myCategories = theAnalysis.getLoanCategories();
        final MoneyWiseLoanCategory myCategory = pCategory.getAccountCategory();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Category Buckets */
        final Iterator<MoneyWiseAnalysisLoanCategoryBucket> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisLoanCategoryBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            final MoneyWiseLoanCategory myCurr = myBucket.getAccountCategory();
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myCurr.getParentCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName, myCurr.getSubCategory());
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, pCategory.getName());
    }

    /**
     * Build a portfolio report.
     * @param pParent the table parent
     */
    private void makePortfolioReport(final MetisHTMLTable pParent) {
        /* Access the portfolios */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = theAnalysis.getPortfolios();

        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Skip inactive portfolios */
            if (!myBucket.isActive()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Embed the table correctly */
        theBuilder.embedTable(myTable, MoneyWiseBasicDataType.PORTFOLIO.getListName());
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseAnalysisDepositCategoryBucket) {
            final MoneyWiseAnalysisDepositCategoryBucket mySourceBucket = (MoneyWiseAnalysisDepositCategoryBucket) mySource;
            return createDelayedDeposit(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseAnalysisCashCategoryBucket) {
            final MoneyWiseAnalysisCashCategoryBucket mySourceBucket = (MoneyWiseAnalysisCashCategoryBucket) mySource;
            return createDelayedCash(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseAnalysisLoanCategoryBucket) {
            final MoneyWiseAnalysisLoanCategoryBucket mySourceBucket = (MoneyWiseAnalysisLoanCategoryBucket) mySource;
            return createDelayedLoan(pTable.getParent(), mySourceBucket);
        } else if (mySource instanceof MoneyWiseAnalysisPortfolioBucket) {
            final MoneyWiseAnalysisPortfolioBucket mySourceBucket = (MoneyWiseAnalysisPortfolioBucket) mySource;
            return createDelayedPortfolio(pTable.getParent(), mySourceBucket);
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
    private MetisHTMLTable createDelayedDeposit(final MetisHTMLTable pParent,
                                                final MoneyWiseAnalysisDepositCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseAnalysisDepositBucketList myAccounts = theAnalysis.getDeposits();
        final MoneyWiseDepositCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        theBuilder.makeTitleCell(myTable, TEXT_RATE);
        theBuilder.makeTitleCell(myTable, TEXT_MATURITY);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Deposit Buckets */
        final Iterator<MoneyWiseAnalysisDepositBucket> myIterator = myAccounts.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisDepositBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getRateValue(MoneyWiseAnalysisAccountAttr.DEPOSITRATE));
            theBuilder.makeValueCell(myTable, myValues.getDateValue(MoneyWiseAnalysisAccountAttr.MATURITY));

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedCash(final MetisHTMLTable pParent,
                                             final MoneyWiseAnalysisCashCategoryBucket pSource) {
        /* Access the category and class */
        final MoneyWiseAnalysisCashBucketList myCash = theAnalysis.getCash();
        final MoneyWiseCashCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Cash Buckets */
        final Iterator<MoneyWiseAnalysisCashBucket> myIterator = myCash.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisCashBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed category table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedLoan(final MetisHTMLTable pParent,
                                             final MoneyWiseAnalysisLoanCategoryBucket pSource) {
        /* Access the category */
        final MoneyWiseAnalysisLoanBucketList myLoans = theAnalysis.getLoans();
        final MoneyWiseLoanCategory myCategory = pSource.getAccountCategory();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ACCOUNT);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* Loop through the Loan Buckets */
        final Iterator<MoneyWiseAnalysisLoanBucket> myIterator = myLoans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisLoanBucket myBucket = myIterator.next();

            /* Skip record if inactive or incorrect category */
            if (!myBucket.isActive()
                    || !MetisDataDifference.isEqual(myBucket.getCategory(), myCategory)) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed portfolio table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedPortfolio(final MetisHTMLTable pParent,
                                                  final MoneyWiseAnalysisPortfolioBucket pSource) {
        /* Access the securities */
        final MoneyWiseAnalysisPortfolioCashBucket myCash = pSource.getPortfolioCash();
        final MoneyWiseAnalysisSecurityBucketList mySecurities = pSource.getSecurities();
        final boolean isForeign = pSource.hasForeignCurrency();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Build the headers */
        theBuilder.startRow(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_ASSET);
        theBuilder.makeTitleCell(myTable, TEXT_UNITS);
        theBuilder.makeTitleCell(myTable, TEXT_PRICE);
        if (isForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_FOREIGNVALUE);
        }
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);

        /* If the portfolio cash is active */
        if (myCash.isActive()) {
            /* Access values */
            final MoneyWiseAnalysisAccountValues myValues = myCash.getValues();

            /* Access bucket name */
            final String myName = pSource.getName();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, MoneyWiseReportBalanceSheet.TEXT_CASH);
            theBuilder.makeValueCell(myTable);
            theBuilder.makeValueCell(myTable);

            /* Handle foreign accounts */
            if (isForeign) {
                if (myCash.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myName, pSource);
        }

        /* Loop through the Security Buckets */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

            /* Skip inactive securities */
            if (!myBucket.isActive()) {
                continue;
            }

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS));
            theBuilder.makeValueCell(myTable, myValues.getPriceValue(MoneyWiseAnalysisSecurityAttr.PRICE));

            /* Handle foreign securities */
            if (isForeign) {
                if (myBucket.isForeignCurrency()) {
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE));
                    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                } else {
                    theBuilder.makeStretchedValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
                }
            } else {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            }

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseAnalysisFilter<?, ?> processFilter(final Object pSource) {
        /* If this is a DepositBucket */
        if (pSource instanceof MoneyWiseAnalysisDepositBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisDepositFilter((MoneyWiseAnalysisDepositBucket) pSource);
        }
        /* If this is a CashBucket */
        if (pSource instanceof MoneyWiseAnalysisCashBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisCashFilter((MoneyWiseAnalysisCashBucket) pSource);
        }
        /* If this is a LoanBucket */
        if (pSource instanceof MoneyWiseAnalysisLoanBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisLoanFilter((MoneyWiseAnalysisLoanBucket) pSource);
        }
        /* If this is a SecurityBucket */
        if (pSource instanceof MoneyWiseAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisSecurityFilter((MoneyWiseAnalysisSecurityBucket) pSource);
        }
        /* If this is a PortfolioBucket */
        if (pSource instanceof MoneyWiseAnalysisPortfolioBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisPortfolioCashFilter((MoneyWiseAnalysisPortfolioBucket) pSource);
        }
        return null;
    }
}
