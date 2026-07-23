/*******************************************************************************
 * MoneyWise: Finance Application
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
 * MoneyWise Core.
 */
@SuppressWarnings("module")
module io.github.tonywasher.joceanus.moneywise.core {
    /* Java libraries */
    requires java.xml;

    /* Oceanus */
    requires io.github.tonywasher.joceanus.prometheus.core;
    requires io.github.tonywasher.joceanus.metis;
    requires io.github.tonywasher.joceanus.gordianknot;
    requires io.github.tonywasher.joceanus.tethys.api;
    requires io.github.tonywasher.joceanus.oceanus;
    requires io.github.tonywasher.joceanus.prometheus.sheet.api;

    /* Exports */
    exports io.github.tonywasher.joceanus.moneywise.quicken.definitions to io.github.tonywasher.joceanus.metis;
    exports io.github.tonywasher.joceanus.moneywise.tax.uk to io.github.tonywasher.joceanus.metis, io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.launch;

    /* Test exports */
    exports io.github.tonywasher.joceanus.moneywise.archive to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.atlas.analyse to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.atlas.base to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.atlas.values to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.lethe.analyse to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.lethe.data to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.analysis.lethe.values to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.data.basic to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.data.builder to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.data.statics to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.data.validate to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.database to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.exc to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.quicken.builder.atlas to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.sheets to io.github.tonywasher.joceanus.moneywise.core.test;
    exports io.github.tonywasher.joceanus.moneywise.views to io.github.tonywasher.joceanus.moneywise.core.test;
}
