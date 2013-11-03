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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Portfolio (Market) report builder.
 */
public class PortfolioAlt
        extends BasicReportAlt<SecurityBucket, Object> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Portfolio.class.getName());

    /**
     * The Title text.
     */
    private static final String TEXT_TITLE = NLS_BUNDLE.getString("ReportTitle");

    /**
     * The Cost text.
     */
    private static final String TEXT_COST = NLS_BUNDLE.getString("ReportCost");

    /**
     * The Value text.
     */
    private static final String TEXT_VALUE = NLS_BUNDLE.getString("ReportValue");

    /**
     * The Gains text.
     */
    private static final String TEXT_GAINS = NLS_BUNDLE.getString("ReportGains");

    /**
     * The Date text.
     */
    // private static final String TEXT_DATE = NLS_BUNDLE.getString("ReportDate");

    /**
     * The Category text.
     */
    // private static final String TEXT_CAT = NLS_BUNDLE.getString("ReportCat");

    /**
     * The Delta Units text.
     */
    // private static final String TEXT_DUNIT = NLS_BUNDLE.getString("ReportDUnits");

    /**
     * The Delta Cost text.
     */
    // private static final String TEXT_DCOST = NLS_BUNDLE.getString("ReportDCost");

    /**
     * The Delta Gains text.
     */
    // private static final String TEXT_DGAIN = NLS_BUNDLE.getString("ReportDGains");

    /**
     * The Dividend text.
     */
    private static final String TEXT_DIVIDEND = NLS_BUNDLE.getString("ReportDividend");

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
    protected PortfolioAlt(final ReportManagerAlt pManager) {
        /* Access underlying utilities */
        theBuilder = pManager.getBuilder();
        theFormatter = theBuilder.getDataFormatter();
    }

    @Override
    public Document createReport(final Analysis pAnalysis) {
        /* Access the bucket lists */
        theAnalysis = pAnalysis;
        SecurityBucketList mySecurities = theAnalysis.getSecurities();
        // PortfolioBucketList myPortfolios = theAnalysis.getPortfolios();

        /* Access the totals */
        // PortfolioBucket myTotals = myPortfolios..getTotalsBucket();
        JDateDay myDate = theAnalysis.getDateRange().getEnd();

        /* Start the report */
        Element myBody = theBuilder.startReport();
        theBuilder.makeTitle(myBody, TEXT_TITLE, theFormatter.formatObject(myDate));

        /* Initialise the table */
        HTMLTable myTable = theBuilder.startTable(myBody);
        theBuilder.startHdrRow(myTable);
        theBuilder.makeTitleCell(myTable);
        theBuilder.makeTitleCell(myTable, TEXT_COST);
        theBuilder.makeTitleCell(myTable, TEXT_VALUE);
        theBuilder.makeTitleCell(myTable, TEXT_GAINS);
        theBuilder.makeTitleCell(myTable, TEXT_DIVIDEND);
        theBuilder.makeTitleCell(myTable, ReportBuilder.TEXT_PROFIT);

        /* Loop through the Security Buckets */
        Iterator<SecurityBucket> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* Access bucket name */
            String myName = myBucket.getName();

            /* Access values */
            SecurityValues myValues = myBucket.getValues();

            /* Format the Asset */
            theBuilder.startRow(myTable);
            theBuilder.makeDelayLinkCell(myTable, myName);
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Cost));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Valuation));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Gains));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Dividend));
            theBuilder.makeValueCell(myTable, myValues.getMoneyValue(SecurityAttribute.Profit));

            /* Note the delayed subTable */
            // setDelayedTable(myName, myTable, myBucket);
        }

        /* Create the total row */
        // theBuilder.startTotalRow(myTable);
        // theBuilder.makeTotalCell(myTable, ReportBuilder.TEXT_TOTAL);
        // theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Cost));
        // theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.MarketValue));
        // theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Gained));
        // theBuilder.makeTotalCell(myTable, myTotals.getMoneyAttribute(AccountAttribute.Profit));

        /* Return the document */
        return theBuilder.getDocument();
    }
}
