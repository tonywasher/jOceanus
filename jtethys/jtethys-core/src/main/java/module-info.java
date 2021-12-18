/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
module net.sourceforge.joceanus.jtethys.core {
    /* External libraries */
    requires org.jsoup;

    /* Exports */
    exports net.sourceforge.joceanus.jtethys;
    exports net.sourceforge.joceanus.jtethys.date;
    exports net.sourceforge.joceanus.jtethys.decimal;
    exports net.sourceforge.joceanus.jtethys.event;
    exports net.sourceforge.joceanus.jtethys.jar;
    exports net.sourceforge.joceanus.jtethys.logger;
    exports net.sourceforge.joceanus.jtethys.resource;
    exports net.sourceforge.joceanus.jtethys.ui;

    /* New exports */
    exports net.sourceforge.joceanus.jtethys.ui.api.base;
    exports net.sourceforge.joceanus.jtethys.ui.api.control;
    exports net.sourceforge.joceanus.jtethys.ui.api.dialog;
    exports net.sourceforge.joceanus.jtethys.ui.api.pane;

    /* Restricted exports */
    exports net.sourceforge.joceanus.jtethys.ui.core.base to net.sourceforge.joceanus.jtethys.javafx, net.sourceforge.joceanus.jtethys.swing;
    exports net.sourceforge.joceanus.jtethys.ui.core.control to net.sourceforge.joceanus.jtethys.javafx, net.sourceforge.joceanus.jtethys.swing;
    exports net.sourceforge.joceanus.jtethys.ui.core.dialog to net.sourceforge.joceanus.jtethys.javafx, net.sourceforge.joceanus.jtethys.swing;
    exports net.sourceforge.joceanus.jtethys.ui.core.pane to net.sourceforge.joceanus.jtethys.javafx, net.sourceforge.joceanus.jtethys.swing;
}
