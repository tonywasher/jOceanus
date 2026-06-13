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

package io.github.tonywasher.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorAutoCorrect;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;

/**
 * Transaction validator.
 *
 * @param <T> the itemType
 */
public interface MoneyWiseDataValidatorTrans<T extends MoneyWiseTransBase>
        extends MoneyWiseDataValidatorAutoCorrect<T> {
    /**
     * Is the account valid as the base account in a transaction?
     *
     * @param pAccount the account
     * @return true/false
     */
    boolean isValidAccount(MoneyWiseTransAsset pAccount);

    /**
     * Is the transaction valid for the base account in the transaction?.
     *
     * @param pAccount  the account
     * @param pCategory The category of the event
     * @return true/false
     */
    boolean isValidCategory(MoneyWiseTransAsset pAccount,
                            MoneyWiseTransCategory pCategory);

    /**
     * Is the direction valid for the base account and category in the transaction?.
     *
     * @param pAccount   the account
     * @param pCategory  The category of the event
     * @param pDirection the direction
     * @return true/false
     */
    boolean isValidDirection(MoneyWiseTransAsset pAccount,
                             MoneyWiseTransCategory pCategory,
                             MoneyWiseAssetDirection pDirection);

    /**
     * Is the partner valid for the base account and category in the transaction?.
     *
     * @param pAccount  the account
     * @param pCategory The category of the event
     * @param pPartner  the partner
     * @return true/false
     */
    boolean isValidPartner(MoneyWiseTransAsset pAccount,
                           MoneyWiseTransCategory pCategory,
                           MoneyWiseTransAsset pPartner);

    /**
     * Build default transaction.
     *
     * @param pKey the key to base the new transaction around (or null)
     * @return the new transaction (or null if no possible transaction)
     */
    MoneyWiseTransaction buildTransaction(Object pKey);

    /**
     * Set range.
     *
     * @param pRange the date range
     */
    void setRange(OceanusDateRange pRange);

    /**
     * Get range.
     *
     * @return the date range
     */
    OceanusDateRange getRange();
}
