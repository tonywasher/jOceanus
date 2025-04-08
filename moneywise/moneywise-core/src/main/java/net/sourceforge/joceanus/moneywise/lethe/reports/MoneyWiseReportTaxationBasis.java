/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisAccountBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisValuesResource;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisTaxBasisFilter;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;

/**
 * Taxation Basis report builder.
 */
public class MoneyWiseReportTaxationBasis
        extends MetisReportBase<MoneyWiseAnalysis, MoneyWiseAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.TAXBASIS_TITLE.getValue();

    /**
     * The Net text.
     */
    private static final String TEXT_NETT = MoneyWiseAnalysisValuesResource.TAXATTR_NETT.getValue();

    /**
     * The Gross text.
     */
    private static final String TEXT_GROSS = MoneyWiseAnalysisValuesResource.TAXATTR_GROSS.getValue();

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
    protected MoneyWiseReportTaxationBasis(final MetisReportManager<MoneyWiseAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseAnalysisTaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        final OceanusDateRange myRange = pAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final MoneyWiseAnalysisTaxBasisBucket myTotals = myTaxBasis.getTotals();

        /* Start the report */
        final Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        final MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_NETT);
        theBuilder.makeTitleCell(myTable, TEXT_GROSS);

        /* Loop through the TaxBasis Buckets */
        final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = myTaxBasis.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTaxBasisBucket myBucket = myIterator.next();

            /* Access the tax basis */
            final boolean hasAccounts = myBucket.hasAccounts();

            /* Access the amount */
            final MoneyWiseAnalysisTaxBasisValues myValues = myBucket.getValues();
            final OceanusMoney myGross = myValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
            final OceanusMoney myNett = myValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT);

            /* If we have a non-zero value */
            if (myGross.isNonZero()) {
                /* Access bucket name */
                final String myName = myBucket.getName();

                /* Format the detail */
                theBuilder.startRow(myTable);
                if (hasAccounts) {
                    theBuilder.makeDelayLinkCell(myTable, myName);
                } else {
                    theBuilder.makeFilterLinkCell(myTable, myName);
                }
                theBuilder.makeValueCell(myTable, myNett);
                theBuilder.makeValueCell(myTable, myGross);

                /* Create links */
                if (hasAccounts) {
                    setDelayedTable(myName, myTable, myBucket);
                } else {
                    setFilterForId(myName, myBucket);
                }
            }
        }

        /* Access values */
        final MoneyWiseAnalysisTaxBasisValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseAnalysisTaxBasisBucket mySourceBucket) {
            return createDelayedAccounts(pTable.getParent(), mySourceBucket);
        }

        /* Return the null table */
        return null;
    }

    /**
     * Create a delayed accounts table.
     * @param pParent the parent table
     * @param pSource the source bucket
     * @return the new document fragment
     */
    protected MetisHTMLTable createDelayedAccounts(final MetisHTMLTable pParent,
                                                   final MoneyWiseAnalysisTaxBasisBucket pSource) {
        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Account Buckets */
        final Iterator<MoneyWiseAnalysisTaxBasisAccountBucket> myIterator = pSource.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisTaxBasisAccountBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();
            final String mySimpleName = myBucket.getSimpleName();

            /* Access values */
            final MoneyWiseAnalysisTaxBasisValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, mySimpleName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseAnalysisFilter<?, MoneyWiseAnalysisTaxBasisAttr> processFilter(final Object pSource) {
        if (pSource instanceof MoneyWiseAnalysisTaxBasisBucket mySource) {
            /* Create the new filter */
            return new MoneyWiseAnalysisTaxBasisFilter(mySource);
        }
        return null;
    }
}
