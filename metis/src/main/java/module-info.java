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
module net.sourceforge.joceanus.metis {
    /* Java libraries */
    requires java.prefs;
    requires java.xml;
    requires java.net.http;

    /* External libraries */
    requires org.json;

    /* jOceanus */
    requires net.sourceforge.joceanus.oceanus;
    requires net.sourceforge.joceanus.tethys.core;

    /* Exports */
    exports net.sourceforge.joceanus.metis.data;
    exports net.sourceforge.joceanus.metis.field;
    exports net.sourceforge.joceanus.metis.help;
    exports net.sourceforge.joceanus.metis.http;
    exports net.sourceforge.joceanus.metis.lethe.list;
    exports net.sourceforge.joceanus.metis.parser;
    exports net.sourceforge.joceanus.metis.preference;
    exports net.sourceforge.joceanus.metis.profile;
    exports net.sourceforge.joceanus.metis.report;
    exports net.sourceforge.joceanus.metis.toolkit;
    exports net.sourceforge.joceanus.metis.viewer;
    exports net.sourceforge.joceanus.metis.ui;
    exports net.sourceforge.joceanus.metis.list;
}
