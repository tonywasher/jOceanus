/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmetis.profile;

import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;

/**
 * Program definition.
 */
public class MetisState {
    /**
     * Profile.
     */
    private final TethysGuiFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public MetisState(final TethysGuiFactory pFactory) {
        /* Store the factory */
        theFactory = pFactory;
    }

    /**
     * Obtain the profile.
     * @return the profile
     */
    public TethysGuiFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the program definition.
     * @return the definition
     */
    public TethysProgram getProgramDefinitions() {
        return theFactory.getProgramDefinitions();
    }
}
