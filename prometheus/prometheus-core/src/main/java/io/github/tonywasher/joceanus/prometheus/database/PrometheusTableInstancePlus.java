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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.sql.SQLException;

/**
 * Table interface.
 *
 * @param <T> the DataType
 */
public interface PrometheusTableInstancePlus<T extends PrometheusDataItem>
        extends PrometheusTableInstance<T> {
    /**
     * Close the result set and statement.
     *
     * @throws SQLException on error
     */
    void closeStmt() throws SQLException;

    /**
     * Load items from the list into the table.
     *
     * @param pReport the report
     * @param pData   the data
     * @throws OceanusException on error
     */
    void loadItems(TethysUIThreadStatusReport pReport,
                   PrometheusDataSet pData) throws OceanusException;

    /**
     * Insert new items from the list.
     *
     * @param pReport the report
     * @param pData   the data
     * @param pBatch  the batch control
     * @throws OceanusException on error
     */
    void insertItems(TethysUIThreadStatusReport pReport,
                     PrometheusDataSet pData,
                     PrometheusBatchControl pBatch) throws OceanusException;

    /**
     * Update items from the list.
     *
     * @param pReport the report
     * @param pBatch  the batch control
     * @throws OceanusException on error
     */
    void updateItems(TethysUIThreadStatusReport pReport,
                     PrometheusBatchControl pBatch) throws OceanusException;

    /**
     * Delete items from the list.
     *
     * @param pReport the report
     * @param pBatch  the batch control
     * @throws OceanusException on error
     */
    void deleteItems(TethysUIThreadStatusReport pReport,
                     PrometheusBatchControl pBatch) throws OceanusException;

    /**
     * Create the table.
     *
     * @throws OceanusException on error
     */
    void createTable() throws OceanusException;

    /**
     * Drop the table.
     *
     * @throws OceanusException on error
     */
    void dropTable() throws OceanusException;

    /**
     * Truncate the table.
     *
     * @throws OceanusException on error
     */
    void purgeTable() throws OceanusException;
}
