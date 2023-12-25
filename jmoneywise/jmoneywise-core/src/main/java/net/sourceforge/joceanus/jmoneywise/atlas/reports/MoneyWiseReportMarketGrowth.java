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
package net.sourceforge.joceanus.jmoneywise.atlas.reports;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * MarketGrowth report builder.
 */
public class MoneyWiseReportMarketGrowth
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseReportMarketGrowth.class);

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.MARKETGROWTH_TITLE.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = MoneyWiseAnalysisDataResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Invested text.
     */
    private static final String TEXT_INVEST = MoneyWiseAnalysisDataResource.SECURITYATTR_INVESTED.getValue();

    /**
     * The Adjustment text.
     */
    private static final String TEXT_ADJUST = MoneyWiseAnalysisDataResource.SECURITYATTR_GROWTHADJUST.getValue();

    /**
     * The Adjustment text.
     */
    private static final String TEXT_GAINS = MoneyWiseAnalysisDataResource.SECURITYATTR_REALISEDGAINS.getValue();

    /**
     * The Base text.
     */
    private static final String TEXT_BASE = MoneyWiseReportResource.MARKETGROWTH_BASE.getValue();

    /**
     * The Growth text.
     */
    private static final String TEXT_GROWTH = MoneyWiseAnalysisDataResource.SECURITYATTR_MARKETGROWTH.getValue();

    /**
     * The CurrenctFluctuation text.
     */
    private static final String TEXT_CURRENCY = MoneyWiseAnalysisDataResource.ACCOUNTATTR_CURRENCYFLUCT.getValue();

    /**
     * The Profit text.
     */
    private static final String TEXT_PROFIT = MoneyWiseAnalysisDataResource.SECURITYATTR_PROFIT.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Do we have foreign assets?
     */
    private boolean hasForeign;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportMarketGrowth(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        hasForeign = myPortfolios.haveForeignCurrency();

        /* Access the totals */
        final MoneyWiseAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();
        final TethysDateRange myRange = pAnalysis.getDateRange();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_BASE);
        theBuilder.makeTitleCell(myTable, TEXT_INVEST);
        theBuilder.makeTitleCell(myTable, TEXT_ADJUST);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);
        theBuilder.makeTitleCell(myTable, TEXT_GROWTH);
        if (hasForeign) {
            theBuilder.makeTitleCell(myTable, TEXT_CURRENCY);
        }
        theBuilder.makeTitleCell(myTable, TEXT_PROFIT);

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();

            /* Only declare the entry if we have securities */
            if (myBucket.securityIterator().hasNext()) {
                /* Format the Asset */
                theBuilder.startRow(myTable);
                theBuilder.makeDelayLinkCell(myTable, myName);
                theBuilder.makeTotalCell(myTable, myBucket.getNonCashValue(false));
                theBuilder.makeTotalCell(myTable, myBucket.getNonCashValue(true));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH));
                if (hasForeign) {
                    theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT));
                }
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT));
                checkPortfolioGrowth(myBucket);

                /* Note the delayed subTable */
                setDelayedTable(myName, myTable, myBucket);
            }
        }

        /* Access values */
        final MoneyWiseAnalysisSecurityValues myValues = myTotals.getValues();

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(false));
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(true));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH));
        if (hasForeign) {
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT));
        }
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT));
        checkPortfolioGrowth(myTotals);

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseAnalysisPortfolioBucket) {
            final MoneyWiseAnalysisPortfolioBucket mySourceBucket = (MoneyWiseAnalysisPortfolioBucket) mySource;
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
                                                  final MoneyWiseAnalysisPortfolioBucket pSource) {
        /* Access the securities */
        final MoneyWiseAnalysisSecurityBucketList mySecurities = pSource.getSecurities();

        /* Create a new table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Security Buckets */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getSecurityName();
            String myFullName = myBucket.getDecoratedName();
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();
            final MoneyWiseAnalysisSecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH));
            if (hasForeign) {
                theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT));
            }
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT));
            checkSecurityGrowth(myBucket);

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Check portfolio growth calculation.
     * @param pBucket the portfolio bucket
     */
    private static void checkPortfolioGrowth(final MoneyWiseAnalysisPortfolioBucket pBucket) {
        /* Check market profit */
        final MoneyWiseAnalysisSecurityValues myValues = pBucket.getValues();
        final TethysMoney myAdjust = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST);
        final TethysMoney myCalcGrowth = pBucket.getNonCashValue(false);
        myCalcGrowth.subtractAmount(pBucket.getNonCashValue(true));
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));
        myCalcGrowth.addAmount(myAdjust);
        final TethysMoney myProfit = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT);
        if (!myProfit.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect profit calculation for security <%s> of <%s>", pBucket.getName(), myCalcGrowth);
        }

        /* Check market growth */
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));
        myCalcGrowth.subtractAmount(myAdjust);
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT));
        final TethysMoney myGrowth = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH);
        if (!myGrowth.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect growth calculation for portfolio <%s> of <%s>", pBucket.getName(), myCalcGrowth);
        }
    }

    /**
     * Check security portfolio profit calculation.
     * @param pBucket the security bucket
     */
    private static void checkSecurityGrowth(final MoneyWiseAnalysisSecurityBucket pBucket) {
        /* Check market profit */
        final MoneyWiseAnalysisSecurityValues myValues = pBucket.getValues();
        final MoneyWiseAnalysisSecurityValues myBaseValues = pBucket.getBaseValues();
        final TethysMoney myAdjust = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST);
        final TethysMoney myCalcGrowth = new TethysMoney(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
        myCalcGrowth.subtractAmount(myBaseValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED));
        myCalcGrowth.addAmount(myAdjust);
        final TethysMoney myProfit = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETPROFIT);
        if (!myProfit.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect profit calculation for security <%s> of <%s>", pBucket.getDecoratedName(), myCalcGrowth);
        }

        /* Check market growth */
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));
        myCalcGrowth.subtractAmount(myAdjust);
        final TethysMoney myFluct = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT);
        if (myFluct != null) {
            myCalcGrowth.subtractAmount(myFluct);
        }
        final TethysMoney myGrowth = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH);
        if (!myGrowth.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect growth calculation for security <%s> of <%s>", pBucket.getDecoratedName(), myCalcGrowth);
        }
    }

    @Override
    public MoneyWiseAnalysisSecurityFilter processFilter(final Object pSource) {
        /* If this is a SecurityBucket */
        if (pSource instanceof MoneyWiseAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisSecurityFilter((MoneyWiseAnalysisSecurityBucket) pSource);
        }
        return null;
    }
}
