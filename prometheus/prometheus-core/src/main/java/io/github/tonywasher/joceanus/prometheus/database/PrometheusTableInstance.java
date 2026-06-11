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

package io.github.tonywasher.joceanus.prometheus.database;

import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataList;

import java.util.List;

/**
 * Table interface.
 *
 * @param <T> the DataType
 */
public interface PrometheusTableInstance<T extends PrometheusDataItem> {
    /**
     * Obtain the list of items.
     *
     * @return the list of items
     */
    PrometheusDataList<T> getList();

    /**
     * Access the table name.
     *
     * @return the table name
     */
    String getTableName();

    /**
     * Access the table definition.
     *
     * @return the table definition
     */
    PrometheusTableControl getDefinition();

    /**
     * Resolve references.
     *
     * @param pTables the list of defined tables
     */
    void resolveReferences(List<PrometheusTableInstance<?>> pTables);
}
