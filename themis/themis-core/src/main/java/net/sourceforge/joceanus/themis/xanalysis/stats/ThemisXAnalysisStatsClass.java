/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;

import java.util.List;

/**
 * Statistics Class.
 */
public class ThemisXAnalysisStatsClass {
    /**
     * The underlying class.
     */
    private final ThemisXAnalysisClassInstance theClass;

    /**
     * The stats.
     */
    private final ThemisXAnalysisStats theStats;

    /**
     * The methods.
     */
    private final List<ThemisXAnalysisStatsMethod> theMethods;

    /**
     * Constructor.
     * @param pClass the parsed class
     */
    ThemisXAnalysisStatsClass(final ThemisXAnalysisClassInstance pClass) {
        /* Store the class */
        theClass = pClass;

        /* Create the stats */
        theStats = new ThemisXAnalysisStats();

        /* Populate the methodList */
        final ThemisXAnalysisInstance myClass = (ThemisXAnalysisInstance) theClass;
        final List<ThemisXAnalysisInstance> myMethods = myClass.discoverChildren(ThemisXAnalysisMethodInstance.class::isInstance);
        theMethods = myMethods.stream().map(ThemisXAnalysisStatsMethod::new).toList();
    }

    /**
     * Obtain the class.
     * @return the class
     */
    public ThemisXAnalysisClassInstance getUnderlying() {
        return theClass;
    }

    /**
     * Obtain the stats.
     * @return the stats
     */
    public ThemisXAnalysisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the methods.
     * @return the methods
     */
    List<ThemisXAnalysisStatsMethod> getMethods() {
        return theMethods;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a StatsClass */
        if (!(pThat instanceof ThemisXAnalysisStatsClass myThat)) {
            return false;
        }

        /* Check equality of class */
        return theClass.equals(myThat.getUnderlying());
    }

    @Override
    public int hashCode() {
        return theClass.hashCode();
    }

    @Override
    public String toString() {
        return theClass.toString();
    }
}
