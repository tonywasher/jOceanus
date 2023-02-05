/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataNamedItem;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;

/**
 * Interface for objects (Deposits, SecurityHoldings etc.) used in a transaction.
 */
public interface TransactionAsset
        extends MetisDataNamedItem, Comparable<TransactionAsset> {
    /**
     * Obtain the asset id.
     * @return the id
     */
    Integer getId();

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
    AssetType getAssetType();

    /**
     * Obtain the parent.
     * @return the parent
     */
    AssetBase getParent();

    /**
     * Is the Asset taxFree?
     * @return true/false
     */
    Boolean isTaxFree();

    /**
     * Is the Asset gross?
     * @return true/false
     */
    Boolean isGross();

    /**
     * Is the Asset foreign?
     * @return true/false
     */
    Boolean isForeign();

    /**
     * Touch underlying item.
     * @param pItem the object that is touching the item
     */
    void touchItem(DataItem pItem);

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
    AssetCurrency getAssetCurrency();

    /**
     * Obtain the currency of the Asset.
     * @return the currency
     */
    Currency getCurrency();
}
