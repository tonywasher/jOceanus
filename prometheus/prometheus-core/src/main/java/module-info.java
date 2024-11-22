/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
module net.sourceforge.joceanus.prometheus.core {
    /* Java Libraries */
    requires java.prefs;
    requires java.sql;

    /* Oceanus */
    requires net.sourceforge.joceanus.oceanus;
    requires net.sourceforge.joceanus.tethys.core;
    requires net.sourceforge.joceanus.gordianknot;
    requires net.sourceforge.joceanus.metis;
    requires net.sourceforge.joceanus.prometheus.sheet.api;

    /* Exports */
    exports net.sourceforge.joceanus.prometheus;
    exports net.sourceforge.joceanus.prometheus.toolkit;
    exports net.sourceforge.joceanus.prometheus.data;
    exports net.sourceforge.joceanus.prometheus.database;
    exports net.sourceforge.joceanus.prometheus.preference;
    exports net.sourceforge.joceanus.prometheus.security;
    exports net.sourceforge.joceanus.prometheus.sheets;
    exports net.sourceforge.joceanus.prometheus.views;
    exports net.sourceforge.joceanus.prometheus.threads;
    exports net.sourceforge.joceanus.prometheus.ui;
    exports net.sourceforge.joceanus.prometheus.ui.fieldset;
    exports net.sourceforge.joceanus.prometheus.ui.panel;
}
