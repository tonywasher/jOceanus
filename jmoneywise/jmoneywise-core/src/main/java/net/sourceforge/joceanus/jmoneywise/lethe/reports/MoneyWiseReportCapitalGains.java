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
import java.util.List;

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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * CapitalGains report builder.
 */
public class MoneyWiseReportCapitalGains
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.CAPITALGAINS_TITLE.getValue();

    /**
     * The Cost text.
     */
    private static final String TEXT_COST = AnalysisResource.SECURITYATTR_RESIDUALCOST.getValue();

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = AnalysisResource.ACCOUNTATTR_VALUATION.getValue();

    /**
     * The Gains text.
     */
    private static final String TEXT_GAINS = AnalysisResource.SECURITYATTR_REALISEDGAINS.getValue();

    /**
     * HTML builder.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The Formatter.
     */
    private List<Transaction> theSecurities;

    /**
     * The EndDate.
     */
    private TethysDate theEndDate;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected MoneyWiseReportCapitalGains(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    protected MetisDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the builder.
     * @return the builder
     */
    protected MetisReportHTMLBuilder getBuilder() {
        return theBuilder;
    }

    /**
     * Obtain the security iterator.
     * @return the iterator
     */
    protected Iterator<Transaction> transactionIterator() {
        return theSecurities.iterator();
    }

    /**
     * Obtain the endDate.
     * @return the endDate
     */
    protected TethysDate getEndDate() {
        return theEndDate;
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        PortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        theSecurities = pAnalysis.getSecurities();
        theEndDate = pAnalysis.getDateRange().getEnd();

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
        if (mySource instanceof SecurityBucket) {
            SecurityBucket mySourceBucket = (SecurityBucket) mySource;
            return createDelayedGains(pTable.getParent(), mySourceBucket);
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
            myFullName = myFullName.replace(':', '-');

            /* Access values */
            SecurityValues myValues = myBucket.getValues();

            /* Create the detail row */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myFullName, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.VALUATION));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS));

            /* Record the delayed table */
            setDelayedTable(myFullName, myTable, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    /**
     * Create a delayed gains table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    private MetisHTMLTable createDelayedGains(final MetisHTMLTable pParent,
                                              final SecurityBucket pSource) {
        /* Create a gains analysis */
        MoneyWiseReportGainsAnalysis myGains = new MoneyWiseReportGainsAnalysis(this, pParent, pSource);

        /* Return the formatted table */
        return myGains.getTable();
    }

    @Override
    public AnalysisFilter<?, ?> processFilter(final Object pSource) {
        return null;
    }
}
