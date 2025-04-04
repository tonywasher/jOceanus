/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2025 Tony Washer
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
 * Coeus Core.
 */
module net.sourceforge.joceanus.coeus.core {
    /* Java Libraries */
    requires java.xml;

    /* External Libraries */
    requires org.jsoup;

    /* Oceanus */
    requires net.sourceforge.joceanus.metis;
    requires net.sourceforge.joceanus.tethys.core;
    requires net.sourceforge.joceanus.oceanus;

    /* Exports */
    exports net.sourceforge.joceanus.coeus.ui.launch;

    /* Allow properties to be read */
    opens net.sourceforge.joceanus.coeus.ui to net.sourceforge.joceanus.metis;
}
