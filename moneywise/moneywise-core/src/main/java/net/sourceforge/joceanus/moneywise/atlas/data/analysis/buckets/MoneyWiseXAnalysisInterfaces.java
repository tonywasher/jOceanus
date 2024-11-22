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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;

/**
 * Analysis Interfaces.
 */
public abstract class MoneyWiseXAnalysisInterfaces {
    /**
     * Standard register-able bucket.
     */
    public interface MoneyWiseXAnalysisBucketRegister {
        /**
         * Obtain the bucket Id.
         * @return the id
         */
        Long getBucketId();

        /**
         * Register the event.
         * @param pEvent the event
         */
        void registerEvent(MoneyWiseXAnalysisEvent pEvent);
    }

    /**
     * Standard foreign bucket.
     */
    public interface MoneyWiseXAnalysisBucketForeign
            extends MoneyWiseXAnalysisBucketRegister {
        /**
         * Record exchangeRate.
         */
        void recordExchangeRate();

        /**
         * Adjust the valuation.
         */
        void adjustValuation();

        /**
         * Obtain the delta valuation.
         * @return the delta
         */
        OceanusMoney getDeltaValuation();

        /**
         * Obtain currency for bucket.
         * @return the currency
         */
        MoneyWiseCurrency getCurrency();
    }

    /**
     * Standard security bucket.
     */
    public interface MoneyWiseXAnalysisBucketPriced
            extends MoneyWiseXAnalysisBucketForeign {
        /**
         * Record securityPrice.
         */
        void recordSecurityPrice();

        /**
         * Is this bucket a StockOption?
         * @return true/false
         */
        boolean isStockOption();

        /**
         * Obtain security for bucket.
         * @return the security
         */
        MoneyWiseSecurity getSecurity();
    }

    /**
     * Analysis Cursor.
     */
    public interface MoneyWiseXAnalysisCursor {
        /**
         * Obtain the current price for a security.
         *
         * @param pSecurity the security
         * @return the price
         */
        OceanusPrice getCurrentPrice(MoneyWiseSecurity pSecurity);

        /**
         * Obtain the current rate for a currency.
         *
         * @param pCurrency the currency
         * @return the rate
         */
        OceanusRatio getCurrentXchgRate(MoneyWiseCurrency pCurrency);

        /**
         * Obtain the depositRate for a deposit.
         *
         * @param pDeposit the deposit
         * @return the rate
         */
        OceanusRate getCurrentDepositRate(MoneyWiseDeposit pDeposit);

        /**
         * Register a securityBucket for price updates.
         *
         * @param pBucket the bucket
         */
        void registerForPriceUpdates(MoneyWiseXAnalysisBucketPriced pBucket);

        /**
         * Register an account/securityBucket for xchgRate updates.
         *
         * @param pBucket the bucket
         */
        void registerForXchgRateUpdates(MoneyWiseXAnalysisBucketForeign pBucket);

        /**
         * Register a buckets interest in the current event.
         *
         * @param pBucket the bucket
         */
        void registerBucketInterest(MoneyWiseXAnalysisBucketRegister pBucket);

        /**
         * Register interested buckets for an event.
         *
         * @param pEvent the event
         */
        void registerInterestedBucketsForEvent(MoneyWiseXAnalysisEvent pEvent);
    }
}
