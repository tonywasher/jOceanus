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
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQActionType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPortfolioEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurity;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

public class MoneyWiseXQIFPortfolioAdjust {
    /**
     * The Portfolio Helper.
     */
    private final MoneyWiseXQIFPortfolioHelper thePortHelper;

    /**
     * The QIF Register.
     */
    private final MoneyWiseXQIFRegister theRegister;

    /**
     * Constructor.
     *
     * @param pHelper the portfolio helper
     */
    MoneyWiseXQIFPortfolioAdjust(final MoneyWiseXQIFPortfolioHelper pHelper) {
        thePortHelper = pHelper;
        theRegister = thePortHelper.getHelper().getRegister();
    }

    /**
     * Process Stock Split.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    void processStockSplit(final MoneyWiseSecurityHolding pHolding,
                           final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);

        /* Obtain number of units after this event */
        final OceanusUnits myTotalUnits = thePortHelper.getUnitsForHoldingEvent(pHolding, pTrans);

        /* Access the delta units */
        final OceanusUnits myDeltaUnits = pTrans.getAccountDeltaUnits();

        /* Obtain number of units before event */
        final OceanusUnits myBaseUnits = new OceanusUnits(myTotalUnits);
        myBaseUnits.subtractUnits(myDeltaUnits);

        /* Obtain split ratio */
        final OceanusRatio mySplit = new OceanusRatio(myTotalUnits, myBaseUnits);
        mySplit.multiply(OceanusDecimal.RADIX_TEN);

        /* Create a stock split event */
        final MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, MoneyWiseQActionType.STKSPLIT);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(mySplit);

        /* Add to the event list */
        myQPortfolio.addEvent(myEvent);
    }

    /**
     * Process stock adjustment.
     *
     * @param pHolding the security holding
     * @param pTrans   the transaction
     */
    void processSecurityAdjust(final MoneyWiseSecurityHolding pHolding,
                               final MoneyWiseXAnalysisEvent pTrans) {
        /* Access Portfolio Account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        final MoneyWiseXQIFAccountEvents myQPortfolio = theRegister.registerAccount(myPortfolio);

        /* Access Transaction details */
        final MoneyWiseXQIFSecurity myQSecurity = theRegister.registerSecurity(mySecurity);

        /* Access the delta units */
        OceanusUnits myUnits = pTrans.getAccountDeltaUnits();
        final boolean isCredit = myUnits.isPositive();
        if (!isCredit) {
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
        }

        /* Create a share movement event */
        final MoneyWiseXQIFPortfolioEvent myEvent = new MoneyWiseXQIFPortfolioEvent(theRegister, pTrans, isCredit
                ? MoneyWiseQActionType.SHRSIN
                : MoneyWiseQActionType.SHRSOUT);
        myEvent.recordSecurity(myQSecurity);
        myEvent.recordQuantity(myUnits);

        /* Add to event list */
        myQPortfolio.addEvent(myEvent);
    }
}
