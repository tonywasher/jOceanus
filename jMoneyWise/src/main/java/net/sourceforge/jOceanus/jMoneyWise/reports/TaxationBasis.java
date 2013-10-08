/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.reports;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.EventFilter;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxCategoryBucketList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Taxation Basis report builder.
 */
public class TaxationBasis
        extends BasicReport<TaxCategoryBucket, TaxCategoryBucket> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TaxationBasis.class.getName());

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = NLS_BUNDLE.getString("ReportTitle");

    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Data Analysis.
     */
    private Analysis theAnalysis;

    /**
     * Constructor.
     * @param pManager the Report Manager
     */
    protected TaxationBasis(final ReportManager pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        TaxCategoryBucketList myTax = theAnalysis.getTaxCategories();
        JDateDayRange myRange = theAnalysis.getDateRange();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myRange));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);

        /* Loop through the Category Summary Buckets */
        Iterator<TaxCategoryBucket> myTaxIterator = myTax.iterator();
        while (myTaxIterator.hasNext()) {
            TaxCategoryBucket myBucket = myTaxIterator.next();

            /* Skip the non-summary elements */
            switch (myBucket.getCategorySection()) {
                case CATSUMM:
                case CATTOTAL:
                    /* Access the amount */
                    JMoney myAmount = myBucket.getMoneyAttribute(TaxAttribute.Amount);

                    /* If we have a non-zero value */
                    if (myAmount.isNonZero()) {
                        /* Access bucket name */
                        String myName = myBucket.getName();

                        /* Format the detail */
                        theBuilder.startRow(myTable);
                        theBuilder.makeFilterLinkCell(myTable, myName);
                        theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(TaxAttribute.Amount));

                        /* Record the filter */
                        setFilterForId(myName, myBucket);
                    }
                    break;
                default:
                    break;
            }
        }

        /* Return the document */
        return theBuilder.getDocument();
    }

    @Override
    protected void processFilter(final TaxCategoryBucket pSource) {
        /* Create the new filter */
        EventFilter myFilter = new EventFilter();
        myFilter.setFilter(pSource);
    }
}
