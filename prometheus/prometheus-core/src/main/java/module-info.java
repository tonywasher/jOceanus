/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
/**
 * Prometheus Core.
 */
module io.github.tonywasher.joceanus.prometheus.core {
    /* Java Libraries */
    requires java.prefs;
    requires java.sql;
    requires java.xml;

    /* Oceanus */
    requires io.github.tonywasher.joceanus.oceanus;
    requires net.sourceforge.joceanus.tethys.core;
    requires net.sourceforge.joceanus.gordianknot;
    requires io.github.tonywasher.joceanus.metis;
    requires io.github.tonywasher.joceanus.prometheus.sheet.api;

    /* Exports */
    exports io.github.tonywasher.joceanus.prometheus.data;
    exports io.github.tonywasher.joceanus.prometheus.database;
    exports io.github.tonywasher.joceanus.prometheus.maps;
    exports io.github.tonywasher.joceanus.prometheus.preference;
    exports io.github.tonywasher.joceanus.prometheus.security;
    exports io.github.tonywasher.joceanus.prometheus.sheets;
    exports io.github.tonywasher.joceanus.prometheus.threads;
    exports io.github.tonywasher.joceanus.prometheus.toolkit;
    exports io.github.tonywasher.joceanus.prometheus.ui;
    exports io.github.tonywasher.joceanus.prometheus.ui.fieldset;
    exports io.github.tonywasher.joceanus.prometheus.ui.panel;
    exports io.github.tonywasher.joceanus.prometheus.validate;
    exports io.github.tonywasher.joceanus.prometheus.views;
}
