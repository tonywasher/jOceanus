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

import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;

/**
 * Market analysis.
 */
public class MarketAnalysis {
    /**
     * Market Payee Income.
     */
    private final JMoney theMarketIncome = new JMoney();

    /**
     * Market Payee Expense.
     */
    private final JMoney theMarketExpense = new JMoney();

    /**
     * CapitalGains Income.
     */
    private final JMoney theGainsIncome = new JMoney();

    /**
     * CapitalGains Expense.
     */
    private final JMoney theGainsExpense = new JMoney();

    /**
     * TaxFreeGains Income.
     */
    private final JMoney theTaxFreeIncome = new JMoney();

    /**
     * TaxFreeGains Expense.
     */
    private final JMoney theTaxFreeExpense = new JMoney();

    /**
     * MarketGrowth Income.
     */
    private final JMoney theGrowthIncome = new JMoney();

    /**
     * MarketGrowth Expense.
     */
    private final JMoney theGrowthExpense = new JMoney();

    /**
     * Process security bucket.
     * @param pBucket the security bucket.
     */
    protected void processSecurity(final SecurityBucket pBucket) {
        /* Access the security */
        Account mySecurity = pBucket.getSecurity();

        /* Access market and gains */
        SecurityValues myValues = pBucket.getValues();
        JMoney myMarket = myValues.getMoneyValue(SecurityAttribute.Market);
        JMoney myGains = myValues.getMoneyValue(SecurityAttribute.Gains);

        /* If there are gains in the period */
        if (myGains.isNonZero()) {
            if (mySecurity.getAccountCategoryClass().isCapitalGains()) {
                /* Add to Capital Gains income/expense */
                if (myGains.isPositive()) {
                    /* Adjust category and market account */
                    if (mySecurity.isTaxFree()) {
                        theTaxFreeIncome.addAmount(myGains);
                    } else {
                        theGainsIncome.addAmount(myGains);
                    }
                    theMarketIncome.addAmount(myGains);
                } else {
                    /* Adjust category and market account */
                    if (mySecurity.isTaxFree()) {
                        theTaxFreeExpense.subtractAmount(myGains);
                    } else {
                        theGainsExpense.subtractAmount(myGains);
                    }
                    theMarketExpense.subtractAmount(myGains);
                }
            } else if (mySecurity.isCategoryClass(AccountCategoryClass.LifeBond)) {
                /* If the gains are positive */
                if (myGains.isPositive()) {
                    /* Add the market income */
                    theMarketIncome.addAmount(myGains);
                }
            }
        }

        /* If there is growth in the period */
        if (myMarket.isNonZero()) {
            /* Add to MarketGrowth income/expense */
            if (myMarket.isPositive()) {
                theGrowthIncome.addAmount(myGains);
                theMarketIncome.addAmount(myGains);
            } else {
                theGrowthExpense.subtractAmount(myGains);
                theMarketExpense.subtractAmount(myGains);
            }
        }
    }

    /**
     * Propagate totals.
     * @param pAnalysis the analysis
     */
    protected void propagateTotals(final Analysis pAnalysis) {
        /* Access lists */
        PayeeBucketList myPayees = pAnalysis.getPayees();
        EventCategoryBucketList myCategories = pAnalysis.getEventCategories();

        /* If we have market income/expense */
        if ((theMarketIncome.isNonZero())
            || (theMarketExpense.isNonZero())) {
            /* Access market payee */
            PayeeBucket myMarket = myPayees.getBucket(AccountCategoryClass.Market);

            /* Adjust totals */
            myMarket.addIncome(theMarketIncome);
            myMarket.addExpense(theMarketExpense);
        }

        /* If we have marketGrowth */
        if ((theGrowthIncome.isNonZero())
            || (theGrowthExpense.isNonZero())) {
            /* Access marketGrowth category */
            EventCategoryBucket myGrowth = myCategories.getBucket(EventCategoryClass.MarketGrowth);

            /* Adjust totals */
            myGrowth.addIncome(theGrowthIncome);
            myGrowth.addExpense(theGrowthExpense);
        }

        /* If we have capitalGains */
        if ((theGainsIncome.isNonZero())
            || (theGainsExpense.isNonZero())) {
            /* Access capitalGain category */
            EventCategoryBucket myGains = myCategories.getBucket(EventCategoryClass.CapitalGain);

            /* Adjust totals */
            myGains.addIncome(theGainsIncome);
            myGains.addExpense(theGainsExpense);
        }

        /* If we have taxFreeGains */
        if ((theTaxFreeIncome.isNonZero())
            || (theTaxFreeExpense.isNonZero())) {
            /* Access taxFreeGain category */
            EventCategoryBucket myGains = myCategories.getBucket(EventCategoryClass.TaxFreeGain);

            /* Adjust totals */
            myGains.addIncome(theGainsIncome);
            myGains.addExpense(theGainsExpense);
        }
    }
}
