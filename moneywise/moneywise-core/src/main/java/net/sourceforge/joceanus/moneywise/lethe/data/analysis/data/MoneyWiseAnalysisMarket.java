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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import java.util.Currency;

import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * Market analysis.
 */
public class MoneyWiseAnalysisMarket {
    /**
     * Analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * Market Payee Income.
     */
    private final OceanusMoney theMarketIncome;

    /**
     * Market Payee Expense.
     */
    private final OceanusMoney theMarketExpense;

    /**
     * MarketGrowth Income.
     */
    private final OceanusMoney theGrowthIncome;

    /**
     * MarketGrowth Expense.
     */
    private final OceanusMoney theGrowthExpense;

    /**
     * CurrencyFluctuation Income.
     */
    private final OceanusMoney theFluctIncome;

    /**
     * CurrencyFluctuation Expense.
     */
    private final OceanusMoney theFluctExpense;

    /**
     * Constructor.
     * @param pAnalysis the analysis.
     */
    protected MoneyWiseAnalysisMarket(final MoneyWiseAnalysis pAnalysis) {
        /* Store parameters */
        theAnalysis = pAnalysis;

        /* Determine the currency */
        final MoneyWiseCurrency myCurr = pAnalysis.getCurrency();
        final Currency myCurrency = myCurr == null
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : myCurr.getCurrency();

        /* Create buckets */
        theMarketIncome = new OceanusMoney(myCurrency);
        theMarketExpense = new OceanusMoney(myCurrency);
        theGrowthIncome = new OceanusMoney(myCurrency);
        theGrowthExpense = new OceanusMoney(myCurrency);
        theFluctIncome = new OceanusMoney(myCurrency);
        theFluctExpense = new OceanusMoney(myCurrency);
    }

    /**
     * Process account bucket.
     * @param pBucket the account bucket.
     */
    protected void processAccount(final MoneyWiseAnalysisAccountBucket<?> pBucket) {
        /* Access market and gains */
        final MoneyWiseAnalysisAccountValues myValues = pBucket.getValues();
        final OceanusMoney myFluct = myValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT);

        /* If there are fluctuations */
        if (myFluct != null) {
            final MoneyWiseAnalysisAccountValues myBaseValues = pBucket.getBaseValues();
            final OceanusMoney myBaseFluct = myBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.CURRENCYFLUCT);
            final OceanusMoney myPeriodFluct = new OceanusMoney(myFluct);
            myPeriodFluct.subtractAmount(myBaseFluct);

            /* Add to CurrencyFluctuation income/expense */
            if (myPeriodFluct.isPositive()) {
                theFluctIncome.addAmount(myPeriodFluct);
                theMarketIncome.addAmount(myPeriodFluct);
            } else {
                theFluctExpense.subtractAmount(myPeriodFluct);
                theMarketExpense.subtractAmount(myPeriodFluct);
            }
        }
    }

    /**
     * Process security bucket.
     * @param pBucket the security bucket.
     */
    protected void processSecurity(final MoneyWiseAnalysisSecurityBucket pBucket) {
        /* Access market and gains */
        final MoneyWiseAnalysisSecurityValues myValues = pBucket.getValues();
        final OceanusMoney myMarket = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.MARKETGROWTH);
        final OceanusMoney myGains = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        final OceanusMoney myFluct = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.CURRENCYFLUCT);

        /* If there are gains in the period */
        if (myGains.isNonZero()) {
            /* Subtract them from the market movement */
            myMarket.subtractAmount(myGains);

            /* These gains have been allocated separately */
            if (myGains.isPositive()) {
                /* Adjust market account */
                theMarketIncome.addAmount(myGains);
            } else {
                /* Adjust market account */
                theMarketExpense.subtractAmount(myGains);
            }
        }

        /* If there is growth in the period */
        if (myMarket.isNonZero()) {
            /* Add to MarketGrowth income/expense */
            if (myMarket.isPositive()) {
                theGrowthIncome.addAmount(myMarket);
                theMarketIncome.addAmount(myMarket);
            } else {
                theGrowthExpense.subtractAmount(myMarket);
                theMarketExpense.subtractAmount(myMarket);
            }
        }

        /* If there are fluctuations in the period */
        if (myFluct != null && myFluct.isNonZero()) {
            /* Add to CurrencyFluctuation income/expense */
            if (myFluct.isPositive()) {
                theFluctIncome.addAmount(myFluct);
                theMarketIncome.addAmount(myFluct);
            } else {
                theFluctExpense.subtractAmount(myFluct);
                theMarketExpense.subtractAmount(myFluct);
            }
        }
    }

    /**
     * Propagate totals.
     */
    protected void propagateTotals() {
        /* Access lists */
        final MoneyWiseAnalysisPayeeBucketList myPayees = theAnalysis.getPayees();
        final MoneyWiseAnalysisTaxBasisBucketList myTaxBasis = theAnalysis.getTaxBasis();
        final MoneyWiseAnalysisTransCategoryBucketList myCategories = theAnalysis.getTransCategories();

        /* If we have market income/expense */
        if (theMarketIncome.isNonZero()
                || theMarketExpense.isNonZero()) {
            /* Access market payee */
            final MoneyWiseAnalysisPayeeBucket myMarket = myPayees.getBucket(MoneyWisePayeeClass.MARKET);

            /* Adjust totals */
            myMarket.addIncome(theMarketIncome);
            myMarket.addExpense(theMarketExpense);
        }

        /* If we have marketGrowth */
        if (theGrowthIncome.isNonZero()
                || theGrowthExpense.isNonZero()) {
            /* Access marketGrowth category */
            final MoneyWiseAnalysisTransCategoryBucket myGrowth = myCategories.getBucket(MoneyWiseTransCategoryClass.MARKETGROWTH);

            /* Adjust totals */
            myGrowth.addIncome(theGrowthIncome);
            myGrowth.addExpense(theGrowthExpense);

            /* Adjust tax basis */
            myTaxBasis.adjustMarket(theGrowthIncome, theGrowthExpense);
        }

        /* If we have currencyFluctuation */
        if (theFluctIncome.isNonZero()
                || theFluctExpense.isNonZero()) {
            /* Access currecyFluctuation category */
            final MoneyWiseAnalysisTransCategoryBucket myFluct = myCategories.getBucket(MoneyWiseTransCategoryClass.CURRENCYFLUCTUATION);

            /* Adjust totals */
            myFluct.addIncome(theFluctIncome);
            myFluct.addExpense(theFluctExpense);

            /* Adjust tax basis */
            myTaxBasis.adjustMarket(theFluctIncome, theFluctExpense);
        }
    }
}
