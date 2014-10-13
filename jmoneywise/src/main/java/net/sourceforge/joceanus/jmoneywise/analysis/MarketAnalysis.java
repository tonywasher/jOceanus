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

import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

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
        Security mySecurity = pBucket.getSecurity();

        /* Access market and gains */
        SecurityValues myValues = pBucket.getValues();
        JMoney myMarket = myValues.getMoneyValue(SecurityAttribute.MARKET);
        JMoney myGains = myValues.getMoneyValue(SecurityAttribute.GAINS);

        /* If there are gains in the period */
        if (myGains.isNonZero()) {
            if (mySecurity.getSecurityTypeClass().isCapitalGains()) {
                /* Subtract them from the market movement */
                myMarket.subtractAmount(myGains);

                /* Add to Capital Gains income/expense */
                if (myGains.isPositive()) {
                    /* Adjust market account */
                    theMarketIncome.addAmount(myGains);
                } else {
                    /* Adjust market account */
                    theMarketExpense.subtractAmount(myGains);
                }
            } else if (mySecurity.isSecurityClass(SecurityTypeClass.LIFEBOND)) {
                /* Subtract them from the market movement */
                myMarket.subtractAmount(myGains);

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
                theGrowthIncome.addAmount(myMarket);
                theMarketIncome.addAmount(myMarket);
            } else {
                theGrowthExpense.subtractAmount(myMarket);
                theMarketExpense.subtractAmount(myMarket);
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
        TaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        EventCategoryBucketList myCategories = pAnalysis.getEventCategories();

        /* If we have market income/expense */
        if ((theMarketIncome.isNonZero())
            || (theMarketExpense.isNonZero())) {
            /* Access market payee */
            PayeeBucket myMarket = myPayees.getBucket(PayeeTypeClass.MARKET);

            /* Adjust totals */
            myMarket.addIncome(theMarketIncome);
            myMarket.addExpense(theMarketExpense);
        }

        /* If we have marketGrowth */
        if ((theGrowthIncome.isNonZero())
            || (theGrowthExpense.isNonZero())) {
            /* Access marketGrowth category */
            EventCategoryBucket myGrowth = myCategories.getBucket(TransactionCategoryClass.MARKETGROWTH);

            /* Adjust totals */
            myGrowth.addIncome(theGrowthIncome);
            myGrowth.addExpense(theGrowthExpense);

            /* Adjust tax basis */
            myTaxBasis.adjustMarket(theGrowthIncome, theGrowthExpense);
        }
    }
}
