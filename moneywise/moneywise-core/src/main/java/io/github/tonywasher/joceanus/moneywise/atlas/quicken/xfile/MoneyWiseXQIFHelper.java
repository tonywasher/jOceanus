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

package io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile;

import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;

import java.util.List;

/**
 * Builder Helper.
 */
public interface MoneyWiseXQIFHelper {
    /**
     * Obtain the register.
     *
     * @return the register
     */
    MoneyWiseXQIFRegister getRegister();

    /**
     * Obtain classes for transaction.
     *
     * @param pTrans the transaction
     * @return the class list (or null)
     */
    List<MoneyWiseXQIFClass> getTransactionClasses(MoneyWiseXAnalysisEvent pTrans);

    /**
     * Obtain the tax category.
     *
     * @return the category
     */
    MoneyWiseXQIFEventCategory getTaxCategory();

    /**
     * Obtain the tax payee.
     *
     * @return the payee
     */
    MoneyWiseXQIFPayee getTaxMan();

    /**
     * Build xferFrom payee line.
     *
     * @param pPartner the Transfer Partner
     * @return the line
     */
    String buildXferFromPayee(MoneyWiseTransAsset pPartner);

    /**
     * Build xferFrom payee line.
     *
     * @param pPartner the Transfer Partner
     * @return the line
     */
    String buildXferToPayee(MoneyWiseTransAsset pPartner);
}
