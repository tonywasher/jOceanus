/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Currency;

import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Market analysis.
 */
public class MarketAnalysis {
    /**
     * Analysis.
     */
    private final Analysis theAnalysis;

    /**
     * Market Payee Income.
     */
    private final TethysMoney theMarketIncome;

    /**
     * Market Payee Expense.
     */
    private final TethysMoney theMarketExpense;

    /**
     * MarketGrowth Income.
     */
    private final TethysMoney theGrowthIncome;

    /**
     * MarketGrowth Expense.
     */
    private final TethysMoney theGrowthExpense;

    /**
     * CurrencyFluctuation Income.
     */
    private final TethysMoney theFluctIncome;

    /**
     * CurrencyFluctuation Expense.
     */
    private final TethysMoney theFluctExpense;

    /**
     * Constructor.
     * @param pAnalysis the analysis.
     */
    protected MarketAnalysis(final Analysis pAnalysis) {
        /* Store parameters */
        theAnalysis = pAnalysis;

        /* Determine the currency */
        final AssetCurrency myCurr = pAnalysis.getCurrency();
        final Currency myCurrency = myCurr == null
                                                   ? AccountBucket.DEFAULT_CURRENCY
                                                   : myCurr.getCurrency();

        /* Create buckets */
        theMarketIncome = new TethysMoney(myCurrency);
        theMarketExpense = new TethysMoney(myCurrency);
        theGrowthIncome = new TethysMoney(myCurrency);
        theGrowthExpense = new TethysMoney(myCurrency);
        theFluctIncome = new TethysMoney(myCurrency);
        theFluctExpense = new TethysMoney(myCurrency);
    }

    /**
     * Process account bucket.
     * @param pBucket the account bucket.
     */
    protected void processAccount(final AccountBucket<?> pBucket) {
        /* Access market and gains */
        final AccountValues myValues = pBucket.getValues();
        final TethysMoney myFluct = myValues.getMoneyValue(AccountAttribute.CURRENCYFLUCT);

        /* If there are fluctuations */
        if (myFluct != null) {
            final AccountValues myBaseValues = pBucket.getBaseValues();
            final TethysMoney myBaseFluct = myBaseValues.getMoneyValue(AccountAttribute.CURRENCYFLUCT);
            final TethysMoney myPeriodFluct = new TethysMoney(myFluct);
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
    protected void processSecurity(final SecurityBucket pBucket) {
        /* Access market and gains */
        final SecurityValues myValues = pBucket.getValues();
        final TethysMoney myMarket = myValues.getMoneyValue(SecurityAttribute.MARKETGROWTH);
        final TethysMoney myGains = myValues.getMoneyValue(SecurityAttribute.REALISEDGAINS);
        final TethysMoney myFluct = myValues.getMoneyValue(SecurityAttribute.CURRENCYFLUCT);

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
        final PayeeBucketList myPayees = theAnalysis.getPayees();
        final TaxBasisBucketList myTaxBasis = theAnalysis.getTaxBasis();
        final TransactionCategoryBucketList myCategories = theAnalysis.getTransCategories();

        /* If we have market income/expense */
        if ((theMarketIncome.isNonZero())
            || (theMarketExpense.isNonZero())) {
            /* Access market payee */
            final PayeeBucket myMarket = myPayees.getBucket(PayeeTypeClass.MARKET);

            /* Adjust totals */
            myMarket.addIncome(theMarketIncome);
            myMarket.addExpense(theMarketExpense);
        }

        /* If we have marketGrowth */
        if ((theGrowthIncome.isNonZero())
            || (theGrowthExpense.isNonZero())) {
            /* Access marketGrowth category */
            final TransactionCategoryBucket myGrowth = myCategories.getBucket(TransactionCategoryClass.MARKETGROWTH);

            /* Adjust totals */
            myGrowth.addIncome(theGrowthIncome);
            myGrowth.addExpense(theGrowthExpense);

            /* Adjust tax basis */
            myTaxBasis.adjustMarket(theGrowthIncome, theGrowthExpense);
        }

        /* If we have currencyFluctuation */
        if ((theFluctIncome.isNonZero())
            || (theFluctExpense.isNonZero())) {
            /* Access currecyFluctuation category */
            final TransactionCategoryBucket myFluct = myCategories.getBucket(TransactionCategoryClass.CURRENCYFLUCTUATION);

            /* Adjust totals */
            myFluct.addIncome(theFluctIncome);
            myFluct.addExpense(theFluctExpense);

            /* Adjust tax basis */
            myTaxBasis.adjustMarket(theFluctIncome, theFluctExpense);
        }
    }
}
