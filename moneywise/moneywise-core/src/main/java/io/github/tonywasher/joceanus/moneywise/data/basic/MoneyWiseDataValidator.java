/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValidator;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Item Validator interface.
 */
public interface MoneyWiseDataValidator
        extends PrometheusDataValidator {
    /**
     * Set the editSet.
     *
     * @param pEditSet the editSet
     */
    void setEditSet(PrometheusEditSet pEditSet);

    /**
     * Validator with Defaults.
     *
     * @param <T> the item type
     */
    interface MoneyWiseDataValidatorDefaults<T extends PrometheusDataItem>
            extends MoneyWiseDataValidator {
        /**
         * Set defaults.
         *
         * @param pItem the item
         * @throws OceanusException on error
         */
        void setDefaults(T pItem) throws OceanusException;
    }

    /**
     * Validator with parent Defaults.
     *
     * @param <T> the item type
     */
    interface MoneyWiseDataValidatorParentDefaults<T extends PrometheusDataItem>
            extends MoneyWiseDataValidator {
        /**
         * Set defaults.
         *
         * @param pParent the parent
         * @param pItem   the item
         * @throws OceanusException on error
         */
        void setDefaults(T pParent,
                         T pItem) throws OceanusException;
    }

    /**
     * Validator with autoCorrect.
     *
     * @param <T> the item type
     */
    interface MoneyWiseDataValidatorAutoCorrect<T extends PrometheusDataItem>
            extends MoneyWiseDataValidator {
        /**
         * autoCorrect values after change.
         *
         * @param pItem the item
         * @throws OceanusException on error
         */
        void autoCorrect(T pItem) throws OceanusException;
    }
}
