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

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;

import java.util.List;
import java.util.Map;

/**
 * Table Control interface.
 */
public interface PrometheusTableControl {
    /**
     * Obtain the driver.
     *
     * @return the driver
     */
    PrometheusJDBCDriver getDriver();

    /**
     * Obtain the column map.
     *
     * @return the map
     */
    Map<MetisDataFieldId, PrometheusColumnControl> getMap();

    /**
     * Sort List.
     *
     * @return the sort list
     */
    List<PrometheusColumnControl> getSortList();

    /**
     * Note that we have a sort on reference.
     */
    void setSortOnReference();

    /**
     * Build the Join string for the list of columns.
     *
     * @param pChar   the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    String getJoinString(char pChar,
                         Integer pOffset);

    /**
     * Build the Order string for the list of columns.
     *
     * @param pChar   the character for this table
     * @param pOffset the join offset
     * @return the SQL string
     */
    String getOrderString(char pChar,
                          Integer pOffset);

    /**
     * Add quote if necessary.
     *
     * @param pBuilder the builder
     */
    void addQuoteIfAllowed(StringBuilder pBuilder);
}
