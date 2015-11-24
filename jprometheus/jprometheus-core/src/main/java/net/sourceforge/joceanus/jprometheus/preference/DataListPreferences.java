/*******************************************************************************
 * jPrometheus: Application Framework
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.preference;

import net.sourceforge.joceanus.jmetis.preference.PreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DataList preferences.
 * @author Tony Washer
 */
public class DataListPreferences
        extends PreferenceSet {
    /**
     * Registry name for Granularity.
     */
    public static final String NAME_GRANULARITY = "Granularity";

    /**
     * Display name for Granularity.
     */
    private static final String DISPLAY_GRANULARITY = "List Index Granularity";

    /**
     * Default Granularity.
     */
    private static final int DEFAULT_GRANULARITY = 5;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public DataListPreferences() throws OceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the properties */
        defineIntegerPreference(NAME_GRANULARITY, DEFAULT_GRANULARITY);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_GRANULARITY)) {
            return DISPLAY_GRANULARITY;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
