/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Tethys Core.
 */
module io.github.tonywasher.joceanus.tethys.core {
    /* External libraries */
    requires org.jsoup;

    /* Oceanus */
    requires io.github.tonywasher.joceanus.oceanus;

    /* New exports */
    exports io.github.tonywasher.joceanus.tethys.api.base;
    exports io.github.tonywasher.joceanus.tethys.api.button;
    exports io.github.tonywasher.joceanus.tethys.api.chart;
    exports io.github.tonywasher.joceanus.tethys.api.control;
    exports io.github.tonywasher.joceanus.tethys.api.dialog;
    exports io.github.tonywasher.joceanus.tethys.api.factory;
    exports io.github.tonywasher.joceanus.tethys.api.field;
    exports io.github.tonywasher.joceanus.tethys.api.menu;
    exports io.github.tonywasher.joceanus.tethys.api.pane;
    exports io.github.tonywasher.joceanus.tethys.api.table;
    exports io.github.tonywasher.joceanus.tethys.api.thread;
    exports io.github.tonywasher.joceanus.tethys.helper;

    /* Restricted exports */
    exports io.github.tonywasher.joceanus.tethys.core.base to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.button to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.chart to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.control to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.dialog to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.factory to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.field to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.menu to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.pane to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.table to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
    exports io.github.tonywasher.joceanus.tethys.core.thread to io.github.tonywasher.joceanus.tethys.javafx, io.github.tonywasher.joceanus.tethys.swing;
}
