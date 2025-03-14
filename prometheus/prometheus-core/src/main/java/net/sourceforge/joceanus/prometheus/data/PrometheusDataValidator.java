/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Item Validator interface.
 * @param <T> the item type
 */
public interface PrometheusDataValidator<T extends PrometheusDataItem> {
    /**
     * Validate the item.
     * @param pItem the item
     */
    void validate(PrometheusDataItem pItem);

    /**
     * Validator with Defaults.
     * @param <T> the item type
     */
    interface PrometheusDataValidatorDefaults<T extends PrometheusDataItem>
            extends PrometheusDataValidator<T> {
        /**
         * Set defaults.
         * @param pItem the item
         * @throws OceanusException on error
         */
        void setDefaults(T pItem) throws OceanusException;
    }

    /**
     * Validator with parent Defaults.
     * @param <T> the item type
     */
    interface PrometheusDataValidatorParentDefaults<T extends PrometheusDataItem>
            extends PrometheusDataValidator<T> {
        /**
         * Set defaults.
         * @param pParent the parent
         * @param pItem the item
         * @throws OceanusException on error
         */
        void setDefaults(T pParent,
                         T pItem) throws OceanusException;
    }

    /**
     * Validator with autoCorrect and Defaults.
     * @param <T> the item type
     */
    interface PrometheusDataValidatorAutoCorrect<T extends PrometheusDataItem>
            extends PrometheusDataValidatorDefaults<T> {
        /**
         * autoCorrect values after change.
         * @param pItem the item
         * @throws OceanusException on error
         */
        void autoCorrect(T pItem) throws OceanusException;
    }

    /**
     * Validator factory.
     */
    interface PrometheusDataValidatorFactory {
        /**
         * Obtain validator for listItem type.
         * @param pItemType the itemType
         * @return the validator
         */
        PrometheusDataValidator<?> newValidator(final PrometheusListKey pItemType);
    }
}
