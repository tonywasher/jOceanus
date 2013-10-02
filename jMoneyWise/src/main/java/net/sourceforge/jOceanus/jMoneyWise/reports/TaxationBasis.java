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

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.reports.HTMLBuilder.TableControl;
import net.sourceforge.jOceanus.jMoneyWise.views.Analysis;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxAttribute;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxCategoryBucket.TaxCategoryBucketList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Taxation Basis report builder.
 */
public class TaxationBasis
        implements MoneyWiseReport {
    /**
     * HTML builder.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Report Manager.
     */
    private final ReportManager theManager;

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
        /* Store values */
        theManager = pManager;

        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        TaxCategoryBucketList myTax = theAnalysis.getTaxCategories();
        StringBuilder myBuffer = new StringBuilder();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        myBuffer.append("Taxation Basis Report for ");
        myBuffer.append(theFormatter.formatObject(theAnalysis.getDateRange()));
        theBuilder.makeTitle(myBody, myBuffer.toString());

        /* Initialise the table */
        TableControl myTable = theBuilder.startTable(myBody);

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
                        /* Format the detail */
                        theBuilder.startRow(myTable);
                        theBuilder.makeFilterLinkCell(myTable, myBucket.getName());
                        theBuilder.makeValueCell(myTable, myBucket.getMoneyAttribute(TaxAttribute.Amount));

                        /* Record the selection */
                        theManager.setFilterForId(myBucket.getName(), myBucket);
                    }
                    break;
                default:
                    break;
            }
        }

        /* Return the document */
        return theBuilder.getDocument();
    }
}
