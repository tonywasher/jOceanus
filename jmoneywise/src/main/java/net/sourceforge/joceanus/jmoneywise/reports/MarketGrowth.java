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

import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * MarketGrowth report builder.
 */
public class MarketGrowth
        extends BasicReport {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketGrowth.class);

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = ReportResource.MARKETGROWTH_TITLE.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = AnalysisResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Invested text.
     */
    private static final String TEXT_INVEST = AnalysisResource.SECURITYATTR_INVESTED.getValue();

    /**
     * The Adjustment text.
     */
    private static final String TEXT_ADJUST = AnalysisResource.SECURITYATTR_GROWTHADJUST.getValue();

    /**
     * The Adjustment text.
     */
    private static final String TEXT_GAINS = AnalysisResource.SECURITYATTR_GAINS.getValue();

    /**
     * The Base text.
     */
    private static final String TEXT_BASE = ReportResource.MARKETGROWTH_BASE.getValue();

    /**
     * The Growth text.
     */
    private static final String TEXT_GROWTH = AnalysisResource.SECURITYATTR_MARKET.getValue();

    /**
     * The Profit text.
     */
    private static final String TEXT_PROFIT = AnalysisResource.SECURITYATTR_PROFIT.getValue();

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
    protected MarketGrowth(final ReportManager pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        PortfolioBucketList myPortfolios = pAnalysis.getPortfolios();

        /* Access the totals */
        PortfolioBucket myTotals = myPortfolios.getTotals();
        JDateDayRange myRange = pAnalysis.getDateRange();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_BASE);
        theBuilder.makeTitleCell(myTable, TEXT_INVEST);
        theBuilder.makeTitleCell(myTable, TEXT_ADJUST);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);
        theBuilder.makeTitleCell(myTable, TEXT_GROWTH);
        theBuilder.makeTitleCell(myTable, TEXT_PROFIT);

        /* Loop through the Portfolio Buckets */
        Iterator<PortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();

            /* Only declare the entry if we have securities */
            if (myBucket.securityIterator().hasNext()) {
                /* Format the Asset */
                theBuilder.startRow(myTable);
                theBuilder.makeDelayLinkCell(myTable, myName);
                theBuilder.makeTotalCell(myTable, myBucket.getNonCashValue(false));
                theBuilder.makeTotalCell(myTable, myBucket.getNonCashValue(true));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.INVESTED));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.GAINS));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.MARKET));
                theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.MARKETPROFIT));
                checkPortfolioGrowth(myBucket);

                /* Note the delayed subTable */
                setDelayedTable(myName, myTable, myBucket);
            }
        }

        /* Access values */
        SecurityValues myValues = myTotals.getValues();

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(false));
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(true));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.INVESTED));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.GAINS));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.MARKET));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.MARKETPROFIT));
        checkPortfolioGrowth(myTotals);

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        Object mySource = pTable.getSource();
        if (mySource instanceof PortfolioBucket) {
            PortfolioBucket mySourceBucket = (PortfolioBucket) mySource;
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
    private HTMLTable createDelayedPortfolio(final HTMLTable pParent,
                                             final PortfolioBucket pSource) {
        /* Access the securities */
        SecurityBucketList mySecurities = pSource.getSecurities();

        /* Create a new table */
        HTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Security Buckets */
        Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();
            SecurityValues myBaseValues = myBucket.getBaseValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.INVESTED));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.GAINS));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.MARKET));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.MARKETPROFIT));
            checkSecurityGrowth(myBucket);

            /* Record the filter */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Check portfolio growth calculation.
     * @param pBucket the portfolio bucket
     */
    private void checkPortfolioGrowth(final PortfolioBucket pBucket) {
        /* Check market profit */
        SecurityValues myValues = pBucket.getValues();
        JMoney myAdjust = myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST);
        JMoney myCalcGrowth = pBucket.getNonCashValue(false);
        myCalcGrowth.subtractAmount(pBucket.getNonCashValue(true));
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(SecurityAttribute.INVESTED));
        myCalcGrowth.addAmount(myAdjust);
        JMoney myProfit = myValues.getMoneyValue(SecurityAttribute.MARKETPROFIT);
        if (!myProfit.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect profit calculation for security {} of {}", pBucket.getName(), myCalcGrowth);
        }

        /* Check market growth */
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(SecurityAttribute.GAINS));
        myCalcGrowth.subtractAmount(myAdjust);
        JMoney myGrowth = myValues.getMoneyValue(SecurityAttribute.MARKET);
        if (!myGrowth.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect growth calculation for portfolio {} of {}", pBucket.getName(), myCalcGrowth);
        }
    }

    /**
     * Check security portfolio profit calculation.
     * @param pBucket the security bucket
     */
    private void checkSecurityGrowth(final SecurityBucket pBucket) {
        /* Check market profit */
        SecurityValues myValues = pBucket.getValues();
        SecurityValues myBaseValues = pBucket.getBaseValues();
        JMoney myAdjust = myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST);
        JMoney myCalcGrowth = new JMoney(myValues.getMoneyValue(SecurityAttribute.VALUATION));
        myCalcGrowth.subtractAmount(myBaseValues.getMoneyValue(SecurityAttribute.VALUATION));
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(SecurityAttribute.INVESTED));
        myCalcGrowth.addAmount(myAdjust);
        JMoney myProfit = myValues.getMoneyValue(SecurityAttribute.MARKETPROFIT);
        if (!myProfit.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect profit calculation for security {} of {}", pBucket.getDecoratedName(), myCalcGrowth);
        }

        /* Check market growth */
        myCalcGrowth.subtractAmount(myValues.getMoneyValue(SecurityAttribute.GAINS));
        myCalcGrowth.subtractAmount(myAdjust);
        JMoney myGrowth = myValues.getMoneyValue(SecurityAttribute.MARKET);
        if (!myGrowth.equals(myCalcGrowth)) {
            LOGGER.error("Incorrect growth calculation for security {} of {}", pBucket.getDecoratedName(), myCalcGrowth);
        }
    }

    @Override
    protected SecurityFilter processFilter(final Object pSource) {
        /* If this is a SecurityBucket */
        if (pSource instanceof SecurityBucket) {
            /* Create the new filter */
            return new SecurityFilter((SecurityBucket) pSource);
        }
        return null;
    }
}
