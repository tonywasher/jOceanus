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
package net.sourceforge.joceanus.themis.xanalysis.stats;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisModule;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Module.
 */
public class ThemisXAnalysisStatsModule {
    /**
     * The underlying module.
     */
    private final ThemisXAnalysisModule theModule;

    /**
     * The stats.
     */
    private final ThemisXAnalysisStats theStats;

    /**
     * The list of packages.
     */
    private final List<ThemisXAnalysisStatsPackage> thePackages;

    /**
     * Constructor.
     *
     * @param pModule the parsed module
     * @throws OceanusException on error
     */
    ThemisXAnalysisStatsModule(final ThemisXAnalysisModule pModule) throws OceanusException {
        /* Store the parameters */
        theModule = pModule;

        /* Create the stats */
        theStats = new ThemisXAnalysisStats();

        /* Create the list and map */
        thePackages = new ArrayList<>();

        /* Initialise the packages */
        for (ThemisXAnalysisPackage myPackage : theModule.getPackages()) {
            final ThemisXAnalysisStatsPackage myStatsPackage = new ThemisXAnalysisStatsPackage(myPackage);
            thePackages.add(myStatsPackage);
        }
    }

    /**
     * Obtain the module.
     *
     * @return the module
     */
    public ThemisXAnalysisModule getUnderlying() {
        return theModule;
    }

    /**
     * Obtain the stats.
     *
     * @return the stats
     */
    public ThemisXAnalysisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the packages.
     *
     * @return the packages
     */
    public List<ThemisXAnalysisStatsPackage> getPackages() {
        return thePackages;
    }

    @Override
    public String toString() {
        return theModule.toString();
    }
}
