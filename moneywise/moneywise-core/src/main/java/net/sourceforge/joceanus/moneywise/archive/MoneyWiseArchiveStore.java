/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.archive;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;

import java.util.ListIterator;

/**
 * Date Check interface.
 */
public interface MoneyWiseArchiveStore {
    /**
     * Check whether date is in range.
     * @param pDate the date to check
     * @return in range true/false
     */
    boolean checkDate(OceanusDate pDate);

    /**
     * Get the reverse iterator.
     * @return the iterator
     */
    ListIterator<MoneyWiseArchiveYear> reverseIterator();

    /**
     * Declare asset.
     * @param pAsset the asset to declare.
     * @throws OceanusException on error
     */
    void declareAsset(MoneyWiseAssetBase pAsset) throws OceanusException;

    /**
     * Declare category.
     * @param pCategory the category to declare.
     * @throws OceanusException on error
     */
    void declareCategory(MoneyWiseTransCategory pCategory) throws OceanusException;

    /**
     * Declare security holding.
     * @param pSecurity the security.
     * @param pPortfolio the portfolio
     * @throws OceanusException on error
     */
    void declareSecurityHolding(MoneyWiseSecurity pSecurity,
                                String pPortfolio) throws OceanusException;

    /**
     * Declare security holding.
     * @param pName the security holding name
     * @param pAlias the alias name.
     * @param pPortfolio the portfolio
     * @throws OceanusException on error
     */
    void declareAliasHolding(String pName,
                             String pAlias,
                             String pPortfolio) throws OceanusException;

    /**
     * Resolve security holdings.
     * @param pData the dataSet
     */
    void resolveSecurityHoldings(MoneyWiseDataSet pData);
}
