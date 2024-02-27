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
 * Metis Core.
 */
module net.sourceforge.joceanus.jmetis.core {
    /* Java libraries */
    requires java.prefs;
    requires java.xml;
    requires java.net.http;

    /* External libraries */
    requires org.json;

    /* jOceanus */
    requires net.sourceforge.joceanus.jtethys.core;

    /* Exports */
    exports net.sourceforge.joceanus.jmetis;
    exports net.sourceforge.joceanus.jmetis.data;
    exports net.sourceforge.joceanus.jmetis.field;
    exports net.sourceforge.joceanus.jmetis.help;
    exports net.sourceforge.joceanus.jmetis.http;
    exports net.sourceforge.joceanus.jmetis.list;
    exports net.sourceforge.joceanus.jmetis.parser;
    exports net.sourceforge.joceanus.jmetis.preference;
    exports net.sourceforge.joceanus.jmetis.profile;
    exports net.sourceforge.joceanus.jmetis.report;
    exports net.sourceforge.joceanus.jmetis.toolkit;
    exports net.sourceforge.joceanus.jmetis.viewer;
    exports net.sourceforge.joceanus.jmetis.ui;
}
