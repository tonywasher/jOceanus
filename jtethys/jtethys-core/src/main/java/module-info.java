/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
module jtethys.core {
    /* External libraries */
    requires org.jsoup;

    /* Exports */
    exports net.sourceforge.joceanus.jtethys;
    exports net.sourceforge.joceanus.jtethys.date;
    exports net.sourceforge.joceanus.jtethys.decimal;
    exports net.sourceforge.joceanus.jtethys.event;
    exports net.sourceforge.joceanus.jtethys.help;
    exports net.sourceforge.joceanus.jtethys.logger;
    exports net.sourceforge.joceanus.jtethys.resource;
    exports net.sourceforge.joceanus.jtethys.ui;
}
