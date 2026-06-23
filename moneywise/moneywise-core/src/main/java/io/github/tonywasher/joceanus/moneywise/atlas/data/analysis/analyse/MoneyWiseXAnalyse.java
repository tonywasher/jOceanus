/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse;

import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisSecurityBucket;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHoldingMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Analyser Interfaces.
 */
public interface MoneyWiseXAnalyse {
    /**
     * EventAnalyser interface.
     */
    interface MoneyWiseXAnalyseEventAnalyserCtl {
        /**
         * Obtain the analysis.
         *
         * @return the analysis
         */
        MoneyWiseXAnalysis getAnalysis();

        /**
         * Obtain the state.
         *
         * @return the state
         */
        MoneyWiseXAnalyseState getState();

        /**
         * Obtain the market analysis.
         *
         * @return the market
         */
        MoneyWiseXAnalyseMarketCtl getMarket();

        /**
         * Obtain the tax analysis.
         *
         * @return the tax
         */
        MoneyWiseXAnalyseTaxCtl getTax();

        /**
         * Obtain the securityHoldingMap.
         *
         * @return the map
         */
        MoneyWiseSecurityHoldingMap getSecurityHoldingMap();
    }

    /**
     * TransAnalyser interface.
     */
    interface MoneyWiseXAnalyseTransAnalyserCtl {
        /**
         * Obtain the currency.
         *
         * @return the currency
         */
        MoneyWiseCurrency getCurrency();

        /**
         * Obtain Account bucket for asset.
         *
         * @param pAsset the asset
         * @return the bucket
         */
        MoneyWiseXAnalysisAccountBucket<?> getAccountBucket(MoneyWiseAssetBase pAsset);

        /**
         * is the account a payee?
         *
         * @param pAccount the account
         * @return true/false
         */
        boolean isPayee(MoneyWiseAssetBase pAccount);

        /**
         * process debit Asset.
         *
         * @param pDebit the debit asset
         * @return the debitAmount in reporting currency
         */
        OceanusMoney processDebitAsset(MoneyWiseAssetBase pDebit);

        /**
         * process credit Asset.
         *
         * @param pCredit the credit asset
         * @return the creditAmount in reporting currency
         */
        OceanusMoney processCreditAsset(MoneyWiseAssetBase pCredit);

        /**
         * process debit payee asset.
         *
         * @param pDebit the debit asset
         */
        void processDebitPayee(MoneyWisePayee pDebit);

        /**
         * adjustForeignDebit.
         *
         * @param pExchangeRate the exchangeRate
         * @return the adjusted debitAmount
         */
        OceanusMoney adjustForeignAssetDebit(OceanusRatio pExchangeRate);

        /**
         * adjustForeignCredit.
         *
         * @param pExchangeRate the exchangeRate
         * @return the adjusted creditAmount
         */
        OceanusMoney adjustForeignAssetCredit(OceanusRatio pExchangeRate);
    }

    /**
     * Security interface.
     */
    interface MoneyWiseXAnalyseSecurityCtl {
        /**
         * Obtain the trans analyser.
         *
         * @return the transAnalyser
         */
        MoneyWiseXAnalyseTransAnalyserCtl getTransAnalyser();

        /**
         * adjust Asset Valuation.
         *
         * @param pAsset the asset
         */
        void adjustAssetValuation(MoneyWiseXAnalysisSecurityBucket pAsset);

        /**
         * Adjust for Standard Gains.
         *
         * @param pSource the source security holding
         * @param pGains  the gains
         */
        void adjustStandardGain(MoneyWiseSecurityHolding pSource,
                                OceanusMoney pGains);
    }

    /**
     * Market interface.
     */
    interface MoneyWiseXAnalyseMarketCtl {
        /**
         * Adjust totals for a marketGrowth Event.
         *
         * @param pEvent the event
         * @param pDelta the delta
         */
        void adjustTotalsForMarketGrowth(MoneyWiseXAnalysisEvent pEvent,
                                         OceanusMoney pDelta);

        /**
         * Adjust for standard gains.
         *
         * @param pGains the gains amount
         */
        void adjustForGains(OceanusMoney pGains);

        /**
         * Adjust market totals.
         *
         * @param pEvent the event
         */
        void adjustMarketTotals(MoneyWiseXAnalysisEvent pEvent);

        /**
         * Adjust totals for a currencyFluctuation Event.
         *
         * @param pEvent the event
         * @param pDelta the delta
         */
        void adjustTotalsForCurrencyFluctuation(MoneyWiseXAnalysisEvent pEvent,
                                                OceanusMoney pDelta);
    }

    /**
     * Tax interface.
     */
    interface MoneyWiseXAnalyseTaxCtl {
        /**
         * Declare the Security analyser.
         *
         * @param pSecurity the securityAnalyser
         */
        void declareSecurityAnalyser(MoneyWiseXAnalyseSecurityCtl pSecurity);

        /**
         * Adjust basis buckets.
         *
         * @param pTrans the transaction
         */
        void adjustTaxBasis(MoneyWiseXAnalyseTransCtl pTrans) throws OceanusException;

        /**
         * Record active payee bucket.
         *
         * @param pPayee the payee bucket
         */
        void recordPayeeBucket(MoneyWiseXAnalysisPayeeBucket pPayee);

        /**
         * Process an autoExpense amount.
         *
         * @param pAmount the amount
         */
        void processAutoExpense(OceanusMoney pAmount);
    }

    /**
     * Transaction interface.
     */
    interface MoneyWiseXAnalyseTransCtl {
        /**
         * Obtain the transaction.
         *
         * @return the transaction
         */
        MoneyWiseTransaction getTransaction();

        /**
         * Adjust parent/child.
         */
        void adjustParent();

        /**
         * Obtain the debit account.
         *
         * @return the debit account
         */
        MoneyWiseTransAsset getDebitAccount();

        /**
         * Obtain the credit account.
         *
         * @return the credit account
         */
        MoneyWiseTransAsset getCreditAccount();

        /**
         * Obtain the debit amount.
         *
         * @return the debit amount
         */
        OceanusMoney getDebitAmount();

        /**
         * Obtain the credit amount.
         *
         * @return the credit amount
         */
        OceanusMoney getCreditAmount();

        /**
         * Obtain the debit unitsDelta.
         *
         * @return the delta
         */
        OceanusUnits getDebitUnitsDelta();

        /**
         * Obtain the credit unitsDelta.
         *
         * @return the delta
         */
        OceanusUnits getCreditUnitsDelta();

        /**
         * Obtain the categoryClass.
         *
         * @return the categoryClass
         */
        MoneyWiseTransCategoryClass getCategoryClass();
    }
}
