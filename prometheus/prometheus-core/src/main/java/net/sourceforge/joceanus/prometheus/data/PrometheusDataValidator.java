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

import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues.PrometheusInfoSetItem;

/**
 * Item Validator interface.
 * @param <T> the item type
 */
public interface PrometheusDataValidator<T extends PrometheusDataItem> {
    /**
     * Validate the item.
     * @param pItem the item
     */
    void validate(T pItem);

    /**
     * InfoSet validator.
     * @param <T> the infoSet item type
     */
    interface PrometheusInfoSetValidator<T extends PrometheusDataItem & PrometheusInfoSetItem>
            extends PrometheusDataValidator<T> {
        /**
         * Determine if an infoSet class is required.
         * @param pClass the infoSet class
         * @return the status
         */
        MetisFieldRequired isClassRequired(PrometheusDataInfoClass pClass);
    }
}
