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

import net.sourceforge.joceanus.metis.report.MetisReportBase;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.metis.report.MetisReportManager;
import net.sourceforge.joceanus.metis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisValuesResource;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisSecurityFilter;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * AssetGains report builder.
 */
public class MoneyWiseReportAssetGains
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.ASSETGAINS_TITLE.getValue();

    /**
     * The Cost text.
     */
    private static final String TEXT_COST = MoneyWiseAnalysisValuesResource.SECURITYATTR_RESIDUALCOST.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = MoneyWiseAnalysisValuesResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Gains text.
     */
    private static final String TEXT_GAINS = MoneyWiseAnalysisValuesResource.SECURITYATTR_REALISEDGAINS.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportAssetGains(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        final OceanusDate myDate = pAnalysis.getDateRange().getEnd();

        /* Access the totals */
        final MoneyWiseAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_COST);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);

        /* Loop through the Portfolio Buckets */
        final Iterator<MoneyWiseAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseAnalysisSecurityValues myValues = myBucket.getValues();

            /* Format the Asset */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);

            /* Handle values bucket value */
            theBuilder.makeValueCell(myTable, myBucket.getNonCashValue(false));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        final MoneyWiseAnalysisSecurityValues myValues = myTotals.getValues();

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(false));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));

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
        /* Access the securities and portfolio */
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

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS));

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseAnalysisFilter<?, ?> processFilter(final Object pSource) {
        if (pSource instanceof MoneyWiseAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseAnalysisSecurityFilter((MoneyWiseAnalysisSecurityBucket) pSource);
        }
        return null;
    }
}
