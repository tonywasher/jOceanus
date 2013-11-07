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
package net.sourceforge.joceanus.jmoneywise.analysis;

import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jsortedlist.NestedHashMap;

/**
 * Analysis manager.
 */
public class AnalysisManager
        extends NestedHashMap<JDateDayRange, Analysis>
        implements JDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8360259174517408222L;

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * The base analysis.
     */
    private final transient Analysis theAnalysis;

    /**
     * The first date.
     */
    private final transient JDateDay theFirstDate;

    /**
     * Constructor.
     * @param pAnalysis the new analysis
     */
    protected AnalysisManager(final Analysis pAnalysis) {
        /* Store the analysis */
        theAnalysis = pAnalysis;

        /* Store the first date */
        FinanceData myData = theAnalysis.getData();
        JDateDayRange myRange = myData.getDateRange();
        theFirstDate = myRange.getStart();
    }

    /**
     * Obtain an analysis for a date.
     * @param pDate the date for the analysis.
     * @return the analysis
     */
    public Analysis getAnalysis(final JDateDay pDate) {
        /* Create the new Range */
        JDateDayRange myRange = new JDateDayRange(theFirstDate, pDate);

        /* Look for the existing analysis */
        Analysis myAnalysis = get(myRange);
        if (myAnalysis == null) {
            /* Create the new event analysis */
            myAnalysis = new Analysis(theAnalysis, pDate);
            produceTotals(myAnalysis);

            /* Put it into the map */
            put(myRange, myAnalysis);
        }

        /* return the analysis */
        return myAnalysis;
    }

    /**
     * Obtain an analysis for a range.
     * @param pRange the date range for the analysis.
     * @return the analysis
     */
    public Analysis getAnalysis(final JDateDayRange pRange) {
        /* Look for the existing analysis */
        Analysis myAnalysis = get(pRange);
        if (myAnalysis == null) {
            /* Create the new event analysis */
            myAnalysis = new Analysis(theAnalysis, pRange);
            produceTotals(myAnalysis);

            /* Put it into the map */
            put(pRange, myAnalysis);
        }

        /* return the analysis */
        return myAnalysis;
    }

    /**
     * Analyse the base analysis.
     */
    protected void analyseBase() {
        /* Produce totals for the base analysis */
        produceTotals(theAnalysis);
    }

    /**
     * Produce Totals for an analysis.
     * @param pAnalysis the analysis.
     */
    private void produceTotals(final Analysis pAnalysis) {
        /* Access the lists */
        AccountBucketList myAccounts = pAnalysis.getAccounts();
        PayeeBucketList myPayees = pAnalysis.getPayees();
        EventCategoryBucketList myEventCategories = pAnalysis.getEventCategories();
        TaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();

        /* Analyse the accounts */
        AccountCategoryBucketList myAccountCategories = pAnalysis.getAccountCategories();
        myAccountCategories.analyseAccounts(myAccounts);
        myAccountCategories.produceTotals();

        /* Analyse the securities */
        PortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        myPortfolios.analyseSecurities(pAnalysis.getSecurities());
        myAccountCategories.analysePortfolios(pAnalysis.getPortfolios());

        /* Analyse the Payees */
        myPayees.produceTotals();

        /* Analyse the EventCategories */
        myEventCategories.produceTotals();

        /* Analyse the TaxBasis */
        myTaxBasis.produceTotals();
    }
}
