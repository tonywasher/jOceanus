/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
 * MoneyWise Core.
 */
module net.sourceforge.joceanus.jmoneywise.core {
    /* Java libraries */
    requires java.xml;

    /* jOceanus */
    requires net.sourceforge.joceanus.jprometheus.core;
    requires net.sourceforge.joceanus.jmetis.core;
    requires net.sourceforge.joceanus.jgordianknot.core;
    requires net.sourceforge.joceanus.jtethys.core;
    requires net.sourceforge.joceanus.jprometheus.sheet.api;

    /* Exports */
    exports net.sourceforge.joceanus.jmoneywise.atlas.quicken.definitions;
    exports net.sourceforge.joceanus.jmoneywise.atlas.launch;
}
