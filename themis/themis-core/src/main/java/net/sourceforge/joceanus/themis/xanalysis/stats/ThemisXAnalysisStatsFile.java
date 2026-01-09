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

import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;

import java.util.List;

/**
 * Statistics File.
 */
public class ThemisXAnalysisStatsFile {
    /**
     * The underlying file.
     */
    private final ThemisXAnalysisFile theFile;

    /**
     * The stats.
     */
    private final ThemisXAnalysisStats theStats;

    /**
     * The files.
     */
    private final List<ThemisXAnalysisStatsClass> theClasses;

    /**
     * Constructor.
     * @param pFile the parsed file
     */
    ThemisXAnalysisStatsFile(final ThemisXAnalysisFile pFile) {
        /* Store the file */
        theFile = pFile;

        /* Create the stats */
        theStats = new ThemisXAnalysisStats();

        /* Populate the classList */
        theClasses = theFile.getClasses().stream().map(ThemisXAnalysisStatsClass::new).toList();
    }

    /**
     * Obtain the file.
     * @return the file
     */
    public ThemisXAnalysisFile getUnderlying() {
        return theFile;
    }

    /**
     * Obtain the stats.
     * @return the stats
     */
    public ThemisXAnalysisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the class map.
     * @return the class map
     */
    List<ThemisXAnalysisStatsClass> getClasses() {
        return theClasses;
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

        /* Make sure that the object is a StatsFile */
        if (!(pThat instanceof ThemisXAnalysisStatsFile myThat)) {
            return false;
        }

        /* Check that file is the same */
        return theFile.equals(myThat.getUnderlying());
    }

    @Override
    public int hashCode() {
        return theFile.hashCode();
    }

    @Override
    public String toString() {
        return theFile.toString();
    }
}
