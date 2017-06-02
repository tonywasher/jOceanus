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
package net.sourceforge.joceanus.jmoneywise.lethe.reports;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.SecurityFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Portfolio (Market) report builder.
 */
public class MoneyWiseReportPortfolioView
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyWiseReportPortfolioView.class);

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.PORTFOLIO_TITLE.getValue();

    /**
     * The Cost text.
     */
    private static final String TEXT_COST = AnalysisResource.SECURITYATTR_RESIDUALCOST.getValue();

    /**
     * The Adjustment text.
     */
    private static final String TEXT_ADJUST = AnalysisResource.SECURITYATTR_GROWTHADJUST.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = AnalysisResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Gains text.
     */
    private static final String TEXT_GAINS = AnalysisResource.SECURITYATTR_REALISEDGAINS.getValue();

    /**
     * The Dividend text.
     */
    private static final String TEXT_DIVIDEND = AnalysisResource.SECURITYATTR_DIVIDEND.getValue();

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
    protected MoneyWiseReportPortfolioView(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
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
        TethysDate myDate = pAnalysis.getDateRange().getEnd();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_COST);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);
        theBuilder.makeTitleCell(myTable, TEXT_DIVIDEND);
        theBuilder.makeTitleCell(myTable, TEXT_ADJUST);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_PROFIT);

        /* Loop through the Portfolio Buckets */
        Iterator<PortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();

            /* Format the Asset */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);

            /* Handle values bucket value */
            theBuilder.makeValueCell(myTable, myBucket.getNonCashValue(false));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.DIVIDEND));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.PROFIT));
            checkPortfolioProfit(myBucket);

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        SecurityValues myValues = myTotals.getValues();

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(false));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.DIVIDEND));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(SecurityAttribute.PROFIT));
        checkPortfolioProfit(myTotals);

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
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
    private MetisHTMLTable createDelayedPortfolio(final MetisHTMLTable pParent,
                                                  final PortfolioBucket pSource) {
        /* Access the securities and portfolio */
        SecurityBucketList mySecurities = pSource.getSecurities();

        /* Create a new table */
        MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Security Buckets */
        Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();
            String myFullName = myBucket.getDecoratedName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.DIVIDEND));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.PROFIT));
            checkSecurityProfit(myBucket);

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Check portfolio profit calculation.
     * @param pBucket the portfolio bucket
     */
    private void checkPortfolioProfit(final PortfolioBucket pBucket) {
        SecurityValues myValues = pBucket.getValues();
        TethysMoney myCalcProfit = pBucket.getNonCashValue(false);
        myCalcProfit.subtractAmount(myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST));
        myCalcProfit.addAmount(myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS));
        myCalcProfit.addAmount(myValues.getMoneyValue(SecurityAttribute.DIVIDEND));
        myCalcProfit.addAmount(myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
        TethysMoney myProfit = myValues.getMoneyValue(SecurityAttribute.PROFIT);
        if (!myProfit.equals(myCalcProfit)) {
            LOGGER.error("Incorrect profit calculation for portfolio {}", pBucket.getName());
        }
    }

    /**
     * Check security portfolio profit calculation.
     * @param pBucket the security bucket
     */
    private void checkSecurityProfit(final SecurityBucket pBucket) {
        SecurityValues myValues = pBucket.getValues();
        TethysMoney myCalcProfit = new TethysMoney(myValues.getMoneyValue(SecurityAttribute.VALUATION));
        myCalcProfit.subtractAmount(myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST));
        myCalcProfit.addAmount(myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS));
        myCalcProfit.addAmount(myValues.getMoneyValue(SecurityAttribute.DIVIDEND));
        myCalcProfit.addAmount(myValues.getMoneyValue(SecurityAttribute.GROWTHADJUST));
        TethysMoney myProfit = myValues.getMoneyValue(SecurityAttribute.PROFIT);
        if (!myProfit.equals(myCalcProfit)) {
            LOGGER.error("Incorrect profit calculation for security {}", pBucket.getDecoratedName());
        }
    }

    @Override
    public SecurityFilter processFilter(final Object pSource) {
        /* If this is a SecurityBucket */
        if (pSource instanceof SecurityBucket) {
            /* Create the new filter */
            return new SecurityFilter((SecurityBucket) pSource);
        }
        return null;
    }
}
