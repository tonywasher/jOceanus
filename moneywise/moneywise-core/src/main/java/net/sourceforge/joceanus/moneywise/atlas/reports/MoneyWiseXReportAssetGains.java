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

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import net.sourceforge.joceanus.tethys.date.TethysDate;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * AssetGains report builder.
 */
public class MoneyWiseXReportAssetGains
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.ASSETGAINS_TITLE.getValue();

    /**
     * The Cost text.
     */
    private static final String TEXT_COST = MoneyWiseXAnalysisValuesResource.SECURITYATTR_RESIDUALCOST.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = MoneyWiseXAnalysisValuesResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Gains text.
     */
    private static final String TEXT_GAINS = MoneyWiseXAnalysisValuesResource.SECURITYATTR_REALISEDGAINS.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    MoneyWiseXReportAssetGains(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        final TethysDate myDate = pAnalysis.getDateRange().getEnd();

        /* Access the totals */
        final MoneyWiseXAnalysisPortfolioBucket myTotals = myPortfolios.getTotals();

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
        final Iterator<MoneyWiseXAnalysisPortfolioBucket> myIterator = myPortfolios.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisPortfolioBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();

            /* Access values */
            final MoneyWiseXAnalysisSecurityValues myValues = myBucket.getValues();

            /* Format the Asset */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);

            /* Handle values bucket value */
            theBuilder.makeValueCell(myTable, myBucket.getNonCashValue(false));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));

            /* Note the delayed subTable */
            setDelayedTable(myName, myTable, myBucket);
        }

        /* Access values */
        final MoneyWiseXAnalysisSecurityValues myValues = myTotals.getValues();

        /* Create the total row */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myTotals.getNonCashValue(false));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));

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
        /* Access the securities and portfolio */
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

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.REALISEDGAINS));

            /* Record the filter */
            setFilterForId(myFullName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseXAnalysisFilter<?, ?> processFilter(final Object pSource) {
        if (pSource instanceof MoneyWiseXAnalysisSecurityBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisSecurityFilter((MoneyWiseXAnalysisSecurityBucket) pSource);
        }
        return null;
    }
}
