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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataFormat;
import net.sourceforge.joceanus.jmetis.list.MetisNestedHashMap;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis manager.
 */
public class AnalysisManager
        extends MetisNestedHashMap<TethysDateRange, Analysis>
        implements MetisDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8360259174517408222L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisManager.class);

    /**
     * The base analysis.
     */
    private final transient Analysis theAnalysis;

    /**
     * The first date.
     */
    private final transient TethysDate theFirstDate;

    /**
     * Do we have active securities?
     */
    private Boolean haveActiveSecurities = Boolean.FALSE;

    /**
     * Do we have a foreign account?
     */
    private Boolean haveForeignCurrency = Boolean.FALSE;

    /**
     * Constructor.
     * @param pAnalysis the new analysis
     */
    public AnalysisManager(final Analysis pAnalysis) {
        /* Store the parameters */
        theAnalysis = pAnalysis;

        /* Store the first date */
        MoneyWiseData myData = theAnalysis.getData();
        TethysDateRange myRange = myData.getDateRange();
        theFirstDate = myRange.getStart();
    }

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

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
     * Do we have a foreign currency?
     * @return true/false
     */
    public Boolean haveForeignCurrency() {
        return haveForeignCurrency;
    }

    /**
     * Do we have active securities?
     * @return true/false
     */
    public Boolean haveActiveSecurities() {
        return haveActiveSecurities;
    }

    /**
     * Obtain an analysis for a date.
     * @param pDate the date for the analysis.
     * @return the analysis
     */
    public Analysis getAnalysis(final TethysDate pDate) {
        /* Create the new Range */
        TethysDateRange myRange = new TethysDateRange(null, pDate);

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
    public Analysis getAnalysis(final TethysDateRange pRange) {
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
    public void analyseBase() {
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
        haveForeignCurrency = myDepositCategories.haveForeignCurrency();

        /* Analyse the cash */
        CashBucketList myCash = pAnalysis.getCash();
        CashCategoryBucketList myCashCategories = pAnalysis.getCashCategories();
        myCashCategories.analyseCash(myCash);
        myCashCategories.produceTotals();
        haveForeignCurrency |= myCashCategories.haveForeignCurrency();

        /* Analyse the loans */
        LoanBucketList myLoans = pAnalysis.getLoans();
        LoanCategoryBucketList myLoanCategories = pAnalysis.getLoanCategories();
        myLoanCategories.analyseLoans(myLoans);
        myLoanCategories.produceTotals();
        haveForeignCurrency |= myLoanCategories.haveForeignCurrency();

        /* Analyse the securities */
        PortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        myPortfolios.analyseSecurities();
        haveForeignCurrency |= myPortfolios.haveForeignCurrency();
        haveActiveSecurities = myPortfolios.haveActiveSecurities();

        /* Analyse the Payees */
        PayeeBucketList myPayees = pAnalysis.getPayees();
        myPayees.produceTotals();

        /* Analyse the TransactionCategories */
        TransactionCategoryBucketList myTransCategories = pAnalysis.getTransCategories();
        myTransCategories.produceTotals();

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
        DepositCategoryBucket myDepCat = pAnalysis.getDepositCategories().getTotals();
        CashCategoryBucket myCashCat = pAnalysis.getCashCategories().getTotals();
        LoanCategoryBucket myLoanCat = pAnalysis.getLoanCategories().getTotals();
        PortfolioBucket myPort = pAnalysis.getPortfolios().getTotals();
        PayeeBucket myPayee = pAnalysis.getPayees().getTotals();
        TransactionCategoryBucket myTrans = pAnalysis.getTransCategories().getTotals();
        TaxBasisBucket myTax = pAnalysis.getTaxBasis().getTotals();

        /* Handle null data */
        if (myDepCat == null) {
            return;
        }

        /* Access totals */
        TethysMoney myDepTotal = myDepCat.getValues().getMoneyValue(AccountAttribute.VALUEDELTA);
        TethysMoney myCashTotal = myCashCat.getValues().getMoneyValue(AccountAttribute.VALUEDELTA);
        TethysMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(AccountAttribute.VALUEDELTA);
        TethysMoney myPortTotal = myPort.getValues().getMoneyValue(SecurityAttribute.VALUEDELTA);
        TethysMoney myPayTotal = myPayee.getValues().getMoneyValue(PayeeAttribute.PROFIT);
        TethysMoney myEvtTotal = myTrans.getValues().getMoneyValue(TransactionAttribute.PROFIT);
        TethysMoney myTaxTotal = myTax.getValues().getMoneyValue(TaxBasisAttribute.GROSS);

        /* Create a copy */
        myDepTotal = new TethysMoney(myDepTotal);

        /* Add sub-accounts */
        myDepTotal.addAmount(myCashTotal);
        myDepTotal.addAmount(myLoanTotal);
        myDepTotal.addAmount(myPortTotal);

        /* Check identities */
        if (!myDepTotal.equals(myPayTotal)) {
            LOGGER.error("Payee total mismatch");
        }
        if (!myDepTotal.equals(myEvtTotal)) {
            LOGGER.error("TransactionCategory total mismatch");
        }
        if (!myDepTotal.equals(myTaxTotal)) {
            LOGGER.error("TaxBasis total mismatch");
        }
    }
}
