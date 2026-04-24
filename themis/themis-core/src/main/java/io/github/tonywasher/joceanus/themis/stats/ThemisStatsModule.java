/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.themis.stats;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisModule;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Module.
 */
public class ThemisStatsModule
        implements ThemisStatsElement {
    /**
     * The underlying module.
     */
    private final ThemisModule theModule;

    /**
     * The stats.
     */
    private final ThemisStats theStats;

    /**
     * The list of packages.
     */
    private final List<ThemisStatsPackage> thePackages;

    /**
     * Constructor.
     *
     * @param pModule the parsed module
     * @throws OceanusException on error
     */
    ThemisStatsModule(final ThemisModule pModule) throws OceanusException {
        /* Store the parameters */
        theModule = pModule;

        /* Create the stats */
        theStats = new ThemisStats();

        /* Create the list and map */
        thePackages = new ArrayList<>();

        /* Initialise the packages */
        for (ThemisPackage myPackage : theModule.getPackages()) {
            /* Only deal with non-placeholders */
            if (!myPackage.isPlaceHolder()) {
                final ThemisStatsPackage myStatsPackage = new ThemisStatsPackage(myPackage);
                thePackages.add(myStatsPackage);
            }
        }
    }

    @Override
    public String getName() {
        return theModule.getName();
    }

    /**
     * Obtain the module.
     *
     * @return the module
     */
    public ThemisModule getUnderlying() {
        return theModule;
    }

    @Override
    public ThemisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the packages.
     *
     * @return the packages
     */
    public List<ThemisStatsPackage> getPackages() {
        return thePackages;
    }

    @Override
    public String toString() {
        return theModule.toString();
    }
}
