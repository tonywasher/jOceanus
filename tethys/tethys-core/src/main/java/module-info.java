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
module net.sourceforge.joceanus.tethys.core {
    /* External libraries */
    requires org.jsoup;

    /* Oceanus */
    requires net.sourceforge.joceanus.oceanus;

    /* New exports */
    exports net.sourceforge.joceanus.tethys.api.base;
    exports net.sourceforge.joceanus.tethys.api.button;
    exports net.sourceforge.joceanus.tethys.api.chart;
    exports net.sourceforge.joceanus.tethys.api.control;
    exports net.sourceforge.joceanus.tethys.api.dialog;
    exports net.sourceforge.joceanus.tethys.api.factory;
    exports net.sourceforge.joceanus.tethys.api.field;
    exports net.sourceforge.joceanus.tethys.api.menu;
    exports net.sourceforge.joceanus.tethys.api.pane;
    exports net.sourceforge.joceanus.tethys.api.table;
    exports net.sourceforge.joceanus.tethys.api.thread;
    exports net.sourceforge.joceanus.tethys.helper;

    /* Restricted exports */
    exports net.sourceforge.joceanus.tethys.core.base to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.button to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.chart to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.control to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.dialog to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.factory to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.field to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.menu to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.pane to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.table to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.core.thread to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
}
