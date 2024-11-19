/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import java.util.Currency;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * Process NatInsurance.
 */
public class MoneyWiseXAnalysisMarket {
    /**
     * The analysis state.
     */
    private final MoneyWiseXAnalysisState theState;

    /**
     * The market payee bucket.
     */
    private final MoneyWiseXAnalysisPayeeBucket theMarketBucket;

    /**
     * The market growth category bucket.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theMarketGrowthBucket;

    /**
     * The currency fluctuation category bucket.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theCurrencyFluctuationBucket;

    /**
     * The market tax basis bucket.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theTaxBasisBucket;

    /**
     * The market income.
     */
    private final OceanusMoney theMarketIncome;

    /**
     * The market expense.
     */
    private final OceanusMoney theMarketExpense;

    /**
     * Is there currency fluctuation?
     */
    private boolean isFluctuation;

    /**
     * Is there growth?
     */
    private boolean isGrowth;

    /**
     * Constructor.
     * @param pAnalyser the analyser
     */
    MoneyWiseXAnalysisMarket(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        /* Store the state */
        theState = pAnalyser.getState();

        /* Obtain the market bucket */
        final MoneyWiseXAnalysis myAnalysis = pAnalyser.getAnalysis();
        theMarketBucket = myAnalysis.getPayees().getBucket(MoneyWisePayeeClass.MARKET);

        /* Obtain the category buckets */
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = myAnalysis.getTransCategories();
        theCurrencyFluctuationBucket = myCategories.getBucket(MoneyWiseTransCategoryClass.CURRENCYFLUCTUATION);
        theMarketGrowthBucket = myCategories.getBucket(MoneyWiseTransCategoryClass.MARKETGROWTH);

        /* Obtain the tax basis bucket */
        theTaxBasisBucket = myAnalysis.getTaxBasis().getBucket(MoneyWiseTaxClass.MARKET);

        /* Allocate the market counters */
        final Currency myCurrency = myAnalysis.getCurrency().getCurrency();
        theMarketIncome = new OceanusMoney(myCurrency);
        theMarketExpense = new OceanusMoney(myCurrency);
    }

    /**
     * Adjust totals for a marketGrowth Event.
     * @param pEvent the event
     * @param pDelta the delta
     */
    void adjustTotalsForMarketGrowth(final MoneyWiseXAnalysisEvent pEvent,
                                     final OceanusMoney pDelta) {
        /* Adjust marketGrowth for delta */
        theMarketGrowthBucket.adjustForDelta(pDelta);
        if (!isGrowth) {
            theState.registerBucketInterest(theMarketGrowthBucket);
            isGrowth = true;
        }

        /* Adjust income expense */
        if (pDelta.isPositive()) {
            theMarketIncome.addAmount(pDelta);
        } else {
            theMarketExpense.subtractAmount(pDelta);
        }
    }

    /**
     * Adjust totals for a currencyFluctuation Event.
     * @param pEvent the event
     * @param pDelta the delta
     */
    void adjustTotalsForCurrencyFluctuation(final MoneyWiseXAnalysisEvent pEvent,
                                            final OceanusMoney pDelta) {
        /* Adjust currencyFluctuation for delta */
        theCurrencyFluctuationBucket.adjustForDelta(pDelta);
        if (!isFluctuation) {
            theState.registerBucketInterest(theCurrencyFluctuationBucket);
            isFluctuation = true;
        }

        /* Adjust income expense */
        if (pDelta.isPositive()) {
            theMarketIncome.addAmount(pDelta);
        } else {
            theMarketExpense.subtractAmount(pDelta);
        }
    }

    /**
     * Adjust market totals.
     * @param pEvent the event
     */
    void adjustMarketTotals(final MoneyWiseXAnalysisEvent pEvent) {
        /* If we are active */
        if (isGrowth || isFluctuation) {
            /* Adjust marketTotals */
            theMarketBucket.addIncome(theMarketIncome);
            theMarketBucket.addExpense(theMarketExpense);
            theState.registerBucketInterest(theMarketBucket);

            /* Adjust taxBasisTotals */
            theMarketIncome.subtractAmount(theMarketExpense);
            theTaxBasisBucket.adjustGrossAndNett(theMarketIncome);
            theState.registerBucketInterest(theTaxBasisBucket);

            /* Reset totals */
            theMarketIncome.setZero();
            theMarketExpense.setZero();
            isGrowth = false;
            isFluctuation = false;
        }
    }
}
