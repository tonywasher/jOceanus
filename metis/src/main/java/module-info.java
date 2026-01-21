/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2019 Tony Washer
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
 * Metis.
 */
module io.github.tonywasher.joceanus.metis {
    /* Java libraries */
    requires java.prefs;
    requires java.xml;
    requires java.net.http;

    /* External libraries */
    requires org.json;

    /* jOceanus */
    requires io.github.tonywasher.joceanus.oceanus;
    requires io.github.tonywasher.joceanus.tethys.core;

    /* Exports */
    exports io.github.tonywasher.joceanus.metis.data;
    exports io.github.tonywasher.joceanus.metis.field;
    exports io.github.tonywasher.joceanus.metis.help;
    exports io.github.tonywasher.joceanus.metis.http;
    exports io.github.tonywasher.joceanus.metis.parser;
    exports io.github.tonywasher.joceanus.metis.preference;
    exports io.github.tonywasher.joceanus.metis.profile;
    exports io.github.tonywasher.joceanus.metis.report;
    exports io.github.tonywasher.joceanus.metis.toolkit;
    exports io.github.tonywasher.joceanus.metis.viewer;
    exports io.github.tonywasher.joceanus.metis.ui;
    exports io.github.tonywasher.joceanus.metis.list;
}
