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

import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket.MoneyWiseXAnalysisSecurityBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisValuesResource;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisSecurityFilter;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * MarketGrowth report builder.
 */
public class MoneyWiseXReportMarketGrowth
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseXReportMarketGrowth.class);

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.MARKETGROWTH_TITLE.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Adjustment text.
     */
    private static final String TEXT_GAINS = MoneyWiseXAnalysisValuesResource.SECURITYATTR_REALISEDGAINS.getValue();

    /**
     * The Base text.
     */
    private static final String TEXT_BASE = MoneyWiseXReportResource.MARKETGROWTH_BASE.getValue();

    /**
     * The Profit text.
     */
    private static final String TEXT_PROFIT = MoneyWiseXAnalysisValuesResource.SECURITYATTR_PROFIT.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Do we have foreign assets?
     */
    private boolean hasForeign;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    MoneyWiseXReportMarketGrowth(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        hasForeign = myPortfolios.haveForeignCurrency();

        /* Access the totals */
        final MoneyWiseXAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();
        final OceanusDateRange myRange = pAnalysis.getDateRange();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_BASE);
        //theBuilder.makeTitleCell(myTable, TEXT_INVEST);
        //theBuilder.makeTitleCell(myTable, TEXT_ADJUST);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);
        //theBuilder.makeTitleCell(myTable, TEXT_GROWTH);
        //if (hasForeign) {
        //    theBuilder.makeTitleCell(myTable, TEXT_CURRENCY);
        //}
        theBuilder.makeTitleCell(myTable, TEXT_PROFIT);

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();

            /* Only declare the entry if we have securities */
            if (myBucket.securityIterator().hasNext()) {
                /* Format the Asset */
                theBuilder.startRow(myTable);
                theBuilder.makeDelayLinkCell(myTable, myName);
                theBuilder.makeTotalCell(myTable, myBucket.getNonCashValue(false));
                theBuilder.makeTotalCell(myTable, myBucket.getNonCashValue(true));
                //theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));
                //theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETGROWTH));
                //if (hasForeign) {
                //    theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CURRENCYFLUCT));
                //}
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT));
                //checkPortfolioGrowth(myBucket);

                /* Note the delayed subTable */
                setDelayedTable(myName, myTable, myBucket);
            }
        }

        /* Access values */
        final MoneyWiseXAnalysisSecurityValues myValues = myTotals.getValues();

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(false));
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(true));
        //theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));
        //theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETGROWTH));
        //if (hasForeign) {
        //    theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CURRENCYFLUCT));
        //}
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT));
        //checkPortfolioGrowth(myTotals);

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseXAnalysisPortfolioBucket) {
            final MoneyWiseXAnalysisPortfolioBucket mySourceBucket = (MoneyWiseXAnalysisPortfolioBucket) mySource;
            return createDelayedPortfolio(pTable.getParent(), mySourceBucket);
        }

        /* Return the null table */
        return null;
    }

    /**
     * Create a delayed portfolio table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedPortfolio(final MetisHTMLTable pParent,
                                                  final MoneyWiseXAnalysisPortfolioBucket pSource) {
        /* Access the securities */
        final MoneyWiseXAnalysisSecurityBucketList mySecurities = pSource.getSecurities();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Security Buckets */
        final Iterator<MoneyWiseXAnalysisSecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisSecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();
            final MoneyWiseXAnalysisSecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            //theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));
            //theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETGROWTH));
            //if (hasForeign) {
            //    theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CURRENCYFLUCT));
            //}
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT));
            //checkSecurityGrowth(myBucket);

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    ///**
    // * Check portfolio growth calculation.
    // * @param pBucket the portfolio bucket
    // */
    //private static void checkPortfolioGrowth(final MoneyWiseXAnalysisPortfolioBucket pBucket) {
        /* Check market profit */
        //final MoneyWiseXAnalysisSecurityValues myValues = pBucket.getValues();
        //final TethysMoney myAdjust = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST);
        //final TethysMoney myCalcGrowth = pBucket.getNonCashValue(false);
        //myCalcGrowth.subtractAmount(pBucket.getNonCashValue(true));
        //myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED));
        //myCalcGrowth.addAmount(myAdjust);
        //final TethysMoney myProfit = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT);
        //if (!myProfit.equals(myCalcGrowth)) {
        //    LOGGER.error("Incorrect profit calculation for security <%s> of <%s>", pBucket.getName(), myCalcGrowth);
        //}

        /* Check market growth */
        //myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));
        //myCalcGrowth.subtractAmount(myAdjust);
        //myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CURRENCYFLUCT));
        //final TethysMoney myGrowth = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETGROWTH);
        //if (!myGrowth.equals(myCalcGrowth)) {
        //    LOGGER.error("Incorrect growth calculation for portfolio <%s> of <%s>", pBucket.getName(), myCalcGrowth);
        //}
    //}

    ///**
    // * Check security portfolio profit calculation.
    // * @param pBucket the security bucket
    //*/
    //private static void checkSecurityGrowth(final MoneyWiseXAnalysisSecurityBucket pBucket) {
        /* Check market profit */
        //final MoneyWiseXAnalysisSecurityValues myValues = pBucket.getValues();
        //final MoneyWiseXAnalysisSecurityValues myBaseValues = pBucket.getBaseValues();
        //final TethysMoney myAdjust = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.GAINSADJUST);
        //final TethysMoney myCalcGrowth = new TethysMoney(myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
        //myCalcGrowth.subtractAmount(myBaseValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
        //myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.INVESTED));
        //myCalcGrowth.addAmount(myAdjust);
        //final TethysMoney myProfit = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETPROFIT);
        //if (!myProfit.equals(myCalcGrowth)) {
        //    LOGGER.error("Incorrect profit calculation for security <%s> of <%s>", pBucket.getDecoratedName(), myCalcGrowth);
        //}

        /* Check market growth */
        //myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));
        //myCalcGrowth.subtractAmount(myAdjust);
        //final TethysMoney myFluct = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.CURRENCYFLUCT);
        //if (myFluct != null) {
        //    myCalcGrowth.subtractAmount(myFluct);
        //}
        //final TethysMoney myGrowth = myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.MARKETGROWTH);
        //if (!myGrowth.equals(myCalcGrowth)) {
        //    LOGGER.error("Incorrect growth calculation for security <%s> of <%s>", pBucket.getDecoratedName(), myCalcGrowth);
        //}
    //}

    @Override
    public MoneyWiseXAnalysisSecurityFilter processFilter(final Object pSource) {
        /* If this is a SecurityBucket */
        if (pSource instanceof MoneyWiseXAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisSecurityFilter((MoneyWiseXAnalysisSecurityBucket) pSource);
        }
        return null;
    }
}
