/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountCategoryBucket.AccountCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmetis.list.NestedHashMap;

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
     * The logger.
     */
    private final transient Logger theLogger;

    /**
     * Is the analysis manager idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theFirstDate == null;
    }

    /**
     * Obtain the base analysis.
     * @return the base analysis
     */
    public Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Constructor.
     * @param pAnalysis the new analysis
     * @param pLogger the logger
     */
    protected AnalysisManager(final Analysis pAnalysis,
                              final Logger pLogger) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theLogger = pLogger;

        /* Store the first date */
        MoneyWiseData myData = theAnalysis.getData();
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

            /* Check the totals */
            checkTotals(myAnalysis);

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

            /* Check the totals */
            checkTotals(myAnalysis);

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

    /**
     * Check totals for an analysis.
     * @param pAnalysis the analysis to check
     */
    private void checkTotals(final Analysis pAnalysis) {
        /* Obtain Totals bucket */
        AccountBucketList myAccount = pAnalysis.getAccounts();
        AccountCategoryBucket myActCat = pAnalysis.getAccountCategories().getTotals();
        PayeeBucket myPayee = pAnalysis.getPayees().getTotals();
        EventCategoryBucket myEvent = pAnalysis.getEventCategories().getTotals();
        TaxBasisBucket myTax = pAnalysis.getTaxBasis().getTotals();

        /* Handle null data */
        if (myActCat == null) {
            return;
        }

        /* Access totals */
        JMoney myActTotal = myActCat.getValues().getMoneyValue(AccountAttribute.DELTA);
        JMoney myPayTotal = myPayee.getValues().getMoneyValue(PayeeAttribute.DELTA);
        JMoney myEvtTotal = myEvent.getValues().getMoneyValue(EventAttribute.DELTA);
        JMoney myTaxTotal = myTax.getValues().getMoneyValue(TaxBasisAttribute.GROSS);

        /* Adjust for Hidden Base Totals */
        JMoney myHiddenBase = myAccount.getHiddenBaseTotal();
        if (myHiddenBase != null) {
            myActTotal = new JMoney(myActTotal);
            myActTotal.subtractAmount(myHiddenBase);
        }

        /* Check identities */
        if (!myActTotal.equals(myPayTotal)) {
            theLogger.log(Level.SEVERE, "Payee total mismatch");
        }
        if (!myActTotal.equals(myEvtTotal)) {
            theLogger.log(Level.SEVERE, "EventCategory total mismatch");
        }
        if (!myActTotal.equals(myTaxTotal)) {
            theLogger.log(Level.SEVERE, "TaxBasis total mismatch");
        }
    }
}
