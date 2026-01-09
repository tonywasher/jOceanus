/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.metis.profile;

import net.sourceforge.joceanus.tethys.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;

/**
 * Program definition.
 */
public class MetisState {
    /**
     * Profile.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public MetisState(final TethysUIFactory<?> pFactory) {
        /* Store the factory */
        theFactory = pFactory;
    }

    /**
     * Obtain the profile.
     * @return the profile
     */
    public TethysUIFactory<?> getFactory() {
        return theFactory;
    }

    /**
     * Obtain the program definition.
     * @return the definition
     */
    public TethysUIProgram getProgramDefinitions() {
        return theFactory.getProgramDefinitions();
    }
}
