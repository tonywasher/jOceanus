/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
module net.sourceforge.joceanus.jprometheus.core {
    /* Java Libraries */
    requires java.sql;

    /* jOceanus */
    requires net.sourceforge.joceanus.jtethys.core;
    requires net.sourceforge.joceanus.jgordianknot.core;
    requires net.sourceforge.joceanus.jmetis.core;
    requires net.sourceforge.joceanus.jprometheus.sheet.api;

    /* Exports */
    exports net.sourceforge.joceanus.jprometheus;
    exports net.sourceforge.joceanus.jprometheus.atlas.data;
    exports net.sourceforge.joceanus.jprometheus.atlas.preference;
    exports net.sourceforge.joceanus.jprometheus.ui.fieldset;
    exports net.sourceforge.joceanus.jprometheus.ui.panel;
    exports net.sourceforge.joceanus.jprometheus.lethe;
    exports net.sourceforge.joceanus.jprometheus.lethe.data;
    exports net.sourceforge.joceanus.jprometheus.lethe.database;
    exports net.sourceforge.joceanus.jprometheus.lethe.sheets;
    exports net.sourceforge.joceanus.jprometheus.threads;
    exports net.sourceforge.joceanus.jprometheus.ui;
    exports net.sourceforge.joceanus.jprometheus.lethe.views;
}
