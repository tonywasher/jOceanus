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

package io.github.tonywasher.joceanus.moneywise.quicken.file;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;

/**
 * Register interface.
 */
public interface MoneyWiseQIFRegister {
    /**
     * Obtain the file type.
     *
     * @return the file type
     */
    MoneyWiseQIFType getFileType();

    /**
     * Obtain account.
     *
     * @param pName the name of the account
     * @return the account
     */
    MoneyWiseQIFAccount getAccount(String pName);

    /**
     * Obtain category.
     *
     * @param pName the name of the category
     * @return the category
     */
    MoneyWiseQIFEventCategory getCategory(String pName);

    /**
     * Obtain class.
     *
     * @param pName the name of the class
     * @return the class
     */
    MoneyWiseQIFClass getClass(String pName);

    /**
     * Obtain security.
     *
     * @param pName the name of the security
     * @return the security
     */
    MoneyWiseQIFSecurity getSecurity(String pName);

    /**
     * Obtain security by Symbol.
     *
     * @param pSymbol the symbol of the security
     * @return the security
     */
    MoneyWiseQIFSecurity getSecurityBySymbol(String pSymbol);

    /**
     * Register account.
     *
     * @param pAccount the account
     * @return the QIFAccount representation
     */
    MoneyWiseQIFAccountEvents registerAccount(MoneyWiseTransAsset pAccount);

    /**
     * Register holding account.
     *
     * @param pPortfolio the portfolio
     * @return the QIFAccount representation
     */
    MoneyWiseQIFAccountEvents registerHoldingAccount(MoneyWisePortfolio pPortfolio);

    /**
     * Register security.
     *
     * @param pSecurity the security
     * @return the QIFSecurity representation
     */
    MoneyWiseQIFSecurity registerSecurity(MoneyWiseSecurity pSecurity);

    /**
     * Register payee.
     *
     * @param pPayee the payee
     * @return the QIFPayee representation
     */
    MoneyWiseQIFPayee registerPayee(MoneyWisePayee pPayee);

    /**
     * Register payee.
     *
     * @param pPayee the payee
     * @return the QIFPayee representation
     */
    MoneyWiseQIFPayee registerPayee(String pPayee);

    /**
     * Register category.
     *
     * @param pCategory the category
     * @return the QIFEventCategory representation
     */
    MoneyWiseQIFEventCategory registerCategory(MoneyWiseTransCategory pCategory);

    /**
     * Register class.
     *
     * @param pClass the class
     * @return the QIFClass representation
     */
    MoneyWiseQIFClass registerClass(MoneyWiseTransTag pClass);

    /**
     * Register class.
     *
     * @param pClass the class
     */
    void registerClass(MoneyWiseQIFClass pClass);
}
