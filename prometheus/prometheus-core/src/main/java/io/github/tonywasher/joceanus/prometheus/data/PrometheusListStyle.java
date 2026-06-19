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

package io.github.tonywasher.joceanus.prometheus.data;

/**
 * ListStyles.
 */
public enum PrometheusListStyle {
    /**
     * Core list holding the true version of the data.
     */
    CORE,

    /**
     * Deep Copy clone for security updates.
     */
    CLONE,

    /**
     * Shallow Copy list for comparison purposes. Only references to other items can be added to
     * the list
     */
    COPY,

    /**
     * Partial extract of the data for the purposes of editing.
     */
    EDIT,

    /**
     * List of changes to be applied to database.
     */
    UPDATE,

    /**
     * List of differences.
     */
    DIFFER;
}
