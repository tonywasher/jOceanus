/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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

    /* Exports */
    exports net.sourceforge.joceanus.tethys;
    exports net.sourceforge.joceanus.tethys.date;
    exports net.sourceforge.joceanus.tethys.decimal;
    exports net.sourceforge.joceanus.tethys.event;
    exports net.sourceforge.joceanus.tethys.jar;
    exports net.sourceforge.joceanus.tethys.logger;
    exports net.sourceforge.joceanus.tethys.profile;
    exports net.sourceforge.joceanus.tethys.resource;

    /* New exports */
    exports net.sourceforge.joceanus.tethys.ui.api.base;
    exports net.sourceforge.joceanus.tethys.ui.api.button;
    exports net.sourceforge.joceanus.tethys.ui.api.chart;
    exports net.sourceforge.joceanus.tethys.ui.api.control;
    exports net.sourceforge.joceanus.tethys.ui.api.dialog;
    exports net.sourceforge.joceanus.tethys.ui.api.factory;
    exports net.sourceforge.joceanus.tethys.ui.api.field;
    exports net.sourceforge.joceanus.tethys.ui.api.menu;
    exports net.sourceforge.joceanus.tethys.ui.api.pane;
    exports net.sourceforge.joceanus.tethys.ui.api.table;
    exports net.sourceforge.joceanus.tethys.ui.api.thread;
    exports net.sourceforge.joceanus.tethys.ui.helper;

    /* Restricted exports */
    exports net.sourceforge.joceanus.tethys.ui.core.base to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.button to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.chart to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.control to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.dialog to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.factory to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.field to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.menu to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.pane to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.table to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
    exports net.sourceforge.joceanus.tethys.ui.core.thread to net.sourceforge.joceanus.tethys.javafx, net.sourceforge.joceanus.tethys.swing;
}
