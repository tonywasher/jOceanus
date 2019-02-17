/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
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
 * Coeus javaFX.
 */
module net.sourceforge.joceanus.jcoeus.javafx {
    /* Java Libraries */
    requires javafx.graphics;

    /* jOceanus */
    requires net.sourceforge.joceanus.jcoeus.core;
    requires net.sourceforge.joceanus.jmetis.core;
    requires net.sourceforge.joceanus.jtethys.core;
    requires net.sourceforge.joceanus.jmetis.javafx;
    requires net.sourceforge.joceanus.jtethys.javafx;

    /* Exports */
    exports net.sourceforge.joceanus.jcoeus.ui.javafx to javafx.graphics;
}
