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
package net.sourceforge.joceanus.jmoneywise.atlas.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.values.MoneyWiseXAnalysisValuesResource;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisTaxBasisFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Taxation Basis report builder.
 */
public class MoneyWiseXReportTaxationBasis
        extends MetisReportBase<MoneyWiseXAnalysis, MoneyWiseXAnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseXReportResource.TAXBASIS_TITLE.getValue();

    /**
     * The Net text.
     */
    private static final String TEXT_NETT = MoneyWiseXAnalysisValuesResource.TAXATTR_NETT.getValue();

    /**
     * The Gross text.
     */
    private static final String TEXT_GROSS = MoneyWiseXAnalysisValuesResource.TAXATTR_GROSS.getValue();

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
    MoneyWiseXReportTaxationBasis(final MetisReportManager<MoneyWiseXAnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final MoneyWiseXAnalysis pAnalysis) {
        /* Access the bucket lists */
        final MoneyWiseXAnalysisTaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        final TethysDateRange myRange = pAnalysis.getDateRange();

        /* Obtain the totals bucket */
        final MoneyWiseXAnalysisTaxBasisBucket myTotals = myTaxBasis.getTotals();

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
        final Iterator<MoneyWiseXAnalysisTaxBasisBucket> myIterator = myTaxBasis.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTaxBasisBucket myBucket = myIterator.next();

            /* Access the tax basis */
            final boolean hasAccounts = myBucket.hasAccounts();

            /* Access the amount */
            final MoneyWiseXAnalysisTaxBasisValues myValues = myBucket.getValues();
            final TethysMoney myGross = myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
            final TethysMoney myNett = myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT);

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
        final MoneyWiseXAnalysisTaxBasisValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseXReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        final Object mySource = pTable.getSource();
        if (mySource instanceof MoneyWiseXAnalysisTaxBasisBucket) {
            final MoneyWiseXAnalysisTaxBasisBucket mySourceBucket = (MoneyWiseXAnalysisTaxBasisBucket) mySource;
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
                                                   final MoneyWiseXAnalysisTaxBasisBucket pSource) {
        /* Create an embedded table */
        final MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Account Buckets */
        final Iterator<MoneyWiseXAnalysisTaxBasisAccountBucket> myIterator = pSource.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXAnalysisTaxBasisAccountBucket myBucket = myIterator.next();

            /* Access bucket name */
            final String myName = myBucket.getName();
            final String mySimpleName = myBucket.getSimpleName();

            /* Access values */
            final MoneyWiseXAnalysisTaxBasisValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, mySimpleName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public MoneyWiseXAnalysisTaxBasisFilter processFilter(final Object pSource) {
        if (pSource instanceof MoneyWiseXAnalysisTaxBasisBucket) {
            /* Create the new filter */
            return new MoneyWiseXAnalysisTaxBasisFilter((MoneyWiseXAnalysisTaxBasisBucket) pSource);
        }
        return null;
    }
}
