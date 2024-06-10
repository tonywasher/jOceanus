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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse;

import java.util.Currency;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXTaxBasisAdjust;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Process NatInsurance.
 */
public class MoneyWiseXAnalysisMarket {
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
    private final TethysMoney theMarketIncome;

    /**
     * The market expense.
     */
    private final TethysMoney theMarketExpense;

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
     * @param pAnalysis the analysis
     */
    MoneyWiseXAnalysisMarket(final MoneyWiseXAnalysis pAnalysis) {
        /* Obtain the market bucket */
        theMarketBucket = pAnalysis.getPayees().getBucket(MoneyWisePayeeClass.MARKET);

        /* Obtain the category buckets */
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = pAnalysis.getTransCategories();
        theCurrencyFluctuationBucket = myCategories.getBucket(MoneyWiseTransCategoryClass.CURRENCYFLUCTUATION);
        theMarketGrowthBucket = myCategories.getBucket(MoneyWiseTransCategoryClass.MARKETGROWTH);

        /* Obtain the tax basis bucket */
        theTaxBasisBucket = pAnalysis.getTaxBasis().getBucket(MoneyWiseTaxClass.MARKET);

        /* Allocate the market counters */
        final Currency myCurrency = pAnalysis.getCurrency().getCurrency();
        theMarketIncome = new TethysMoney(myCurrency);
        theMarketExpense = new TethysMoney(myCurrency);
    }

    /**
     * Adjust totals for a marketGrowth Event.
     * @param pEvent the event
     * @param pDelta the delta
     */
    void adjustTotalsForMarketGrowth(final MoneyWiseXAnalysisEvent pEvent,
                                     final TethysMoney pDelta) {
        /* Adjust marketGrowth for delta */
        theMarketGrowthBucket.adjustForDelta(pDelta);
        if (!isGrowth) {
            theMarketGrowthBucket.registerEvent(pEvent);
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
                                            final TethysMoney pDelta) {
        /* Adjust currencyFluctuation for delta */
        theCurrencyFluctuationBucket.adjustForDelta(pDelta);
        if (!isFluctuation) {
            theCurrencyFluctuationBucket.registerEvent(pEvent);
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
            theMarketBucket.registerEvent(pEvent);

            /* Adjust taxBasisTotals */
            theMarketIncome.subtractAmount(theMarketExpense);
            theTaxBasisBucket.adjustValue(theMarketIncome, MoneyWiseXTaxBasisAdjust.STANDARD);
            theTaxBasisBucket.registerEvent(pEvent);

            /* Reset totals */
            theMarketIncome.setZero();
            theMarketExpense.setZero();
            isGrowth = false;
            isFluctuation = false;
        }
    }
}
