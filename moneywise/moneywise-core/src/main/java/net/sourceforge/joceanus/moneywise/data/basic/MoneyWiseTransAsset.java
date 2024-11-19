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
package net.sourceforge.joceanus.moneywise.data.basic;

import java.util.Currency;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

/**
 * Interface for objects (Deposits, SecurityHoldings etc.) used in a transaction.
 */
public interface MoneyWiseTransAsset
        extends MetisDataNamedItem {
    /**
     * Obtain the asset id.
     * @return the id
     */
    Long getExternalId();

    @Override
    String getName();

    /**
     * Is the asset closed?
     * @return true/false
     */
    Boolean isClosed();

    /**
     * Obtain Asset Type.
     * @return the Asset type
     */
    MoneyWiseAssetType getAssetType();

    /**
     * Obtain the parent.
     * @return the parent
     */
    MoneyWiseAssetBase getParent();

    /**
     * Is the Asset taxFree?
     * @return true/false
     */
    boolean isTaxFree();

    /**
     * Is the Asset gross?
     * @return true/false
     */
    boolean isGross();

    /**
     * Is the Asset foreign?
     * @return true/false
     */
    boolean isForeign();

    /**
     * Touch underlying item.
     * @param pItem the object that is touching the item
     */
    void touchItem(PrometheusDataItem pItem);

    /**
     * Is the account capital?
     * @return true/false
     */
    boolean isCapital();

    /**
     * Is the account shares?
     * @return true/false
     */
    boolean isShares();

    /**
     * Is the account autoExpense?
     * @return true/false
     */
    boolean isAutoExpense();

    /**
     * Is the asset hidden?
     * @return true/false
     */
    boolean isHidden();

    /**
     * Obtain the currency of the Asset.
     * @return the currency
     */
    MoneyWiseCurrency getAssetCurrency();

    /**
     * Obtain the currency of the Asset.
     * @return the currency
     */
    Currency getCurrency();
}
