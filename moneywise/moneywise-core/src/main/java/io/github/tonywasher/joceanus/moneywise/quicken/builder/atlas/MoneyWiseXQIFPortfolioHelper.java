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

package io.github.tonywasher.joceanus.moneywise.quicken.builder.atlas;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Portfolio Builder Helper.
 */
public interface MoneyWiseXQIFPortfolioHelper {
    /**
     * Obtain the base helper.
     *
     * @return the helper.
     */

    MoneyWiseXQIFHelper getHelper();

    /**
     * Obtain latest price for a security.
     *
     * @param pSecurity the security
     * @param pDate     the date
     * @return the price
     */
    OceanusPrice getPriceForDate(MoneyWiseSecurity pSecurity,
                                 OceanusDate pDate);

    /**
     * Obtain delta cost for a security holding.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     * @return the delta cost
     */
    OceanusMoney getDeltaCostForHolding(MoneyWiseSecurityHolding pHolding,
                                        MoneyWiseXAnalysisEvent pTrans);

    /**
     * Obtain resulting units for a security holding event.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     * @return the units
     */
    OceanusUnits getUnitsForHoldingEvent(MoneyWiseSecurityHolding pHolding,
                                         MoneyWiseXAnalysisEvent pTrans);

    /**
     * Obtain base units for a security holding event.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     * @return the units
     */
    OceanusUnits getBaseUnitsForHolding(MoneyWiseSecurityHolding pHolding,
                                        MoneyWiseXAnalysisEvent pTrans);

    /**
     * Obtain portfolio cash value.
     *
     * @param pPortfolio the portfolio
     * @param pTrans     the transaction
     * @return the cash value (or null if none)
     */
    OceanusMoney getPortfolioCashValue(MoneyWisePortfolio pPortfolio,
                                       MoneyWiseXAnalysisEvent pTrans);
}
