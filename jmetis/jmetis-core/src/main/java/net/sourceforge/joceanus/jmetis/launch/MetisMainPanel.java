/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmetis.launch;

import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;

/**
 * Main GUI program class interface.
 */
public interface MetisMainPanel {
    /**
     * Obtain the main component.
     * @return the component
     */
    TethysComponent getComponent();

    /**
     * Obtain menuBar.
     * @return the menuBar (or null)
     */
    default TethysMenuBarManager getMenuBar() {
        return null;
    }

    /**
     * Handle application close.
     * @return allow close (true/false)
     */
    default boolean handleAppClose() {
        return true;
    }
}
