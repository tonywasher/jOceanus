/*
 * Prometheus: Application Framework
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

package io.github.tonywasher.joceanus.prometheus.ui.fieldset;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;

/**
 * FieldSet Control.
 *
 * @param <T> the item type
 */
public interface PrometheusFieldSetControl<T> {
    /**
     * Register a field.
     *
     * @param pFieldId the field.
     * @param pPanel   the panel.
     */
    void registerField(MetisDataFieldId pFieldId,
                       PrometheusFieldSetPanel<T> pPanel);

    /**
     * Notify listeners of new data.
     *
     * @param pFieldId  the fieldId.
     * @param pNewValue the new value
     */
    void newData(MetisDataFieldId pFieldId,
                 Object pNewValue);

    /**
     * Is the cell changed?
     *
     * @param pItem  the item
     * @param pField the field id
     * @return true/false
     */
    boolean isChanged(T pItem,
                      MetisDataFieldId pField);
}
