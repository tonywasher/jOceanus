/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.api.base;

import java.util.Map;

import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

/**
 * A set of keyed values.
 */
public interface TethysUIValueSet
        extends OceanusEventProvider<TethysUIEvent> {
    /**
     * Apply new Mappings.
     * @param pMappings the colour mappings
     */
    void applyColorMapping(Map<String, String> pMappings);

    /**
     * Get value for key.
     * @param pKey the value key
     * @return the relevant value
     */
    String getValueForKey(String pKey);

    /**
     * Get default value for key.
     * @param pKey the value key
     * @return the relevant value
     */
    String getDefaultValueForKey(String pKey);

    /**
     * Resolve values.
     * @param pSource the source string
     * @return the resolved string
     */
    String resolveValues(String pSource);
}
