/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-core/src/main/java/net/sourceforge/joceanus/jtethys/event/TethysEventRegistration.java $
 * $Revision: 654 $
 * $Author: Tony $
 * $Date: 2015-12-01 15:49:07 +0000 (Tue, 01 Dec 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

/**
 * Tethys User interface events.
 */
public enum TethysUIEvent {
    /**
     * New Value.
     */
    NEWVALUE,

    /**
     * Toggle Item.
     */
    TOGGLEITEM,

    /**
     * New Command.
     */
    NEWCOMMAND,

    /**
     * Prepare Dialog.
     */
    PREPAREDIALOG,

    /**
     * Prepare Command Dialog.
     */
    PREPARECMDDIALOG,

    /**
     * Build Page.
     */
    BUILDPAGE,

    /**
     * Window Closed.
     */
    WINDOWCLOSED,
}
