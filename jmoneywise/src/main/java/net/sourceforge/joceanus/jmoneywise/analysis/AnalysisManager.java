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

import net.sourceforge.joceanus.jmetis.list.NestedHashMap;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

import org.slf4j.Logger;

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
        JDateDayRange myRange = new JDateDayRange(null, pDate);

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
        /* Analyse the deposits */
        DepositBucketList myDeposits = pAnalysis.getDeposits();
        DepositCategoryBucketList myDepositCategories = pAnalysis.getDepositCategories();
        myDepositCategories.analyseDeposits(myDeposits);
        myDepositCategories.produceTotals();

        /* Analyse the cash */
        CashBucketList myCash = pAnalysis.getCash();
        CashCategoryBucketList myCashCategories = pAnalysis.getCashCategories();
        myCashCategories.analyseCash(myCash);
        myCashCategories.produceTotals();

        /* Analyse the loans */
        LoanBucketList myLoans = pAnalysis.getLoans();
        LoanCategoryBucketList myLoanCategories = pAnalysis.getLoanCategories();
        myLoanCategories.analyseLoans(myLoans);
        myLoanCategories.produceTotals();

        /* Analyse the securities */
        PortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        myPortfolios.analyseSecurities();

        /* Analyse the Payees */
        PayeeBucketList myPayees = pAnalysis.getPayees();
        myPayees.produceTotals();

        /* Analyse the EventCategories */
        EventCategoryBucketList myEventCategories = pAnalysis.getEventCategories();
        myEventCategories.produceTotals();

        /* Analyse the TaxBasis */
        TaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        myTaxBasis.produceTotals();
    }

    /**
     * Check totals for an analysis.
     * @param pAnalysis the analysis to check
     */
    private void checkTotals(final Analysis pAnalysis) {
        /* Obtain Totals bucket */
        DepositBucketList myDeposit = pAnalysis.getDeposits();
        DepositCategoryBucket myDepCat = pAnalysis.getDepositCategories().getTotals();
        CashCategoryBucket myCashCat = pAnalysis.getCashCategories().getTotals();
        LoanCategoryBucket myLoanCat = pAnalysis.getLoanCategories().getTotals();
        PortfolioBucket myPort = pAnalysis.getPortfolios().getTotals();
        PayeeBucket myPayee = pAnalysis.getPayees().getTotals();
        EventCategoryBucket myEvent = pAnalysis.getEventCategories().getTotals();
        TaxBasisBucket myTax = pAnalysis.getTaxBasis().getTotals();

        /* Handle null data */
        if (myDepCat == null) {
            return;
        }

        /* Access totals */
        JMoney myDepTotal = myDepCat.getValues().getMoneyValue(AccountAttribute.DELTA);
        JMoney myCashTotal = myCashCat.getValues().getMoneyValue(AccountAttribute.DELTA);
        JMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(AccountAttribute.DELTA);
        JMoney myPortTotal = myPort.getValues().getMoneyValue(SecurityAttribute.DELTA);
        JMoney myPayTotal = myPayee.getValues().getMoneyValue(PayeeAttribute.DELTA);
        JMoney myEvtTotal = myEvent.getValues().getMoneyValue(EventAttribute.DELTA);
        JMoney myTaxTotal = myTax.getValues().getMoneyValue(TaxBasisAttribute.GROSS);

        /* Create a copy */
        myDepTotal = new JMoney(myDepTotal);

        /* Adjust for Hidden Base Totals */
        JMoney myHiddenBase = myDeposit.getHiddenBaseTotal();
        if (myHiddenBase != null) {
            myDepTotal = new JMoney(myDepTotal);
            myDepTotal.subtractAmount(myHiddenBase);
        }

        /* Add sub-accounts */
        myDepTotal.addAmount(myCashTotal);
        myDepTotal.addAmount(myLoanTotal);
        myDepTotal.addAmount(myPortTotal);

        /* Check identities */
        if (!myDepTotal.equals(myPayTotal)) {
            theLogger.error("Payee total mismatch");
        }
        if (!myDepTotal.equals(myEvtTotal)) {
            theLogger.error("EventCategory total mismatch");
        }
        if (!myDepTotal.equals(myTaxTotal)) {
            theLogger.error("TaxBasis total mismatch");
        }
    }
}
