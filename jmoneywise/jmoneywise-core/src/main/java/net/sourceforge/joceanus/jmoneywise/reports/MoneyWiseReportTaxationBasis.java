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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportBase;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportManager;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportReferenceManager.DelayedTable;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.TaxBasisFilter;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Taxation Basis report builder.
 */
public class MoneyWiseReportTaxationBasis
        extends MetisReportBase<Analysis, AnalysisFilter<?, ?>> {
    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = MoneyWiseReportResource.TAXBASIS_TITLE.getValue();

    /**
     * The Net text.
     */
    private static final String TEXT_NETT = AnalysisResource.TAXATTR_NETT.getValue();

    /**
     * The Gross text.
     */
    private static final String TEXT_GROSS = AnalysisResource.TAXATTR_GROSS.getValue();

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
    protected MoneyWiseReportTaxationBasis(final MetisReportManager<AnalysisFilter<?, ?>> pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        TaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        TethysDateRange myRange = pAnalysis.getDateRange();

        /* Obtain the totals bucket */
        TaxBasisBucket myTotals = myTaxBasis.getTotals();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        MetisHTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_NETT);
        theBuilder.makeTitleCell(myTable, TEXT_GROSS);

        /* Loop through the TaxBasis Buckets */
        Iterator<TaxBasisBucket> myIterator = myTaxBasis.iterator();
        while (myIterator.hasNext()) {
            TaxBasisBucket myBucket = myIterator.next();

            /* Access the tax basis */
            boolean hasAccounts = myBucket.hasAccounts();

            /* Access the amount */
            TaxBasisValues myValues = myBucket.getValues();
            TethysMoney myGross = myValues.getMoneyValue(TaxBasisAttribute.GROSS);
            TethysMoney myNett = myValues.getMoneyValue(TaxBasisAttribute.NETT);

            /* If we have a non-zero value */
            if (myGross.isNonZero()) {
                /* Access bucket name */
                String myName = myBucket.getName();

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
        TaxBasisValues myValues = myTotals.getValues();

        /* Format the total */
        theBuilder.startTotalRow(myTable);
        theBuilder.makeTitleCell(myTable, MoneyWiseReportBuilder.TEXT_TOTAL);
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TaxBasisAttribute.NETT));
        theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        /* Access the source */
        Object mySource = pTable.getSource();
        if (mySource instanceof TaxBasisBucket) {
            TaxBasisBucket mySourceBucket = (TaxBasisBucket) mySource;
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
                                                   final TaxBasisBucket pSource) {
        /* Create an embedded table */
        MetisHTMLTable myTable = theBuilder.createEmbeddedTable(pParent);

        /* Loop through the Account Buckets */
        Iterator<TaxBasisAccountBucket> myIterator = pSource.accountIterator();
        while (myIterator.hasNext()) {
            TaxBasisAccountBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();
            String mySimpleName = myBucket.getSimpleName();

            /* Access values */
            TaxBasisValues myValues = myBucket.getValues();

            /* Create the SubCategory row */
            theBuilder.startRow(myTable);
            theBuilder.makeFilterLinkCell(myTable, myName, mySimpleName);
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TaxBasisAttribute.NETT));
            theBuilder.makeTotalCell(myTable, myValues.getMoneyValue(TaxBasisAttribute.GROSS));

            /* Record the selection */
            setFilterForId(myName, myBucket);
        }

        /* Return the table */
        return myTable;
    }

    @Override
    public AnalysisFilter<?, TaxBasisAttribute> processFilter(final Object pSource) {
        if (pSource instanceof TaxBasisBucket) {
            /* Create the new filter */
            return new TaxBasisFilter((TaxBasisBucket) pSource);
        }
        return null;
    }
}
