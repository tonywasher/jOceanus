/*******************************************************************************
 * Themis: Java Project Framework
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
package net.sourceforge.joceanus.themis.xanalysis.stats;

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;

/**
 * Statistics Method.
 */
public class ThemisXAnalysisStatsMethod {
    /**
     * The underlying package.
     */
    private final ThemisXAnalysisMethodInstance theMethod;

    /**
     * The stats.
     */
    private final ThemisXAnalysisStats theStats;

    /**
     * Constructor.
     * @param pMethod the parsed method
     */
    ThemisXAnalysisStatsMethod(final ThemisXAnalysisInstance pMethod) {
        /* Store the method */
        theMethod = (ThemisXAnalysisMethodInstance) pMethod;

        /* Create the stats */
        theStats = new ThemisXAnalysisStats();
    }

    /**
     * Obtain the method.
     * @return the method
     */
    public ThemisXAnalysisMethodInstance getUnderlying() {
        return theMethod;
    }

    /**
     * Obtain the stats.
     * @return the stats
     */
    public ThemisXAnalysisStats getStats() {
        return theStats;
    }

    @Override
    public String toString() {
        return theMethod.toString();
    }
}
