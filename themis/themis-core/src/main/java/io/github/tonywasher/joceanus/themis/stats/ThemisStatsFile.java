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

import io.github.tonywasher.joceanus.themis.parser.proj.ThemisFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics File.
 */
public class ThemisStatsFile
        implements ThemisStatsElement {
    /**
     * The underlying file.
     */
    private final ThemisFile theFile;

    /**
     * The stats.
     */
    private final ThemisStats theStats;

    /**
     * The files.
     */
    private final List<ThemisStatsClass> theClasses;

    /**
     * Constructor.
     *
     * @param pFile the parsed file
     */
    ThemisStatsFile(final ThemisFile pFile) {
        /* Store the file */
        theFile = pFile;

        /* Create the stats */
        theStats = new ThemisStats();

        /* Populate the classList */
        theClasses = new ArrayList<>();

        /* Create a statsParser */
        final ThemisStatsParser myParser = new ThemisStatsParser();
        myParser.parseElement(this, theFile.getContents());
    }

    @Override
    public String getName() {
        return theFile.getName();
    }

    /**
     * Obtain the file.
     *
     * @return the file
     */
    public ThemisFile getUnderlying() {
        return theFile;
    }

    @Override
    public ThemisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the class map.
     *
     * @return the class map
     */
    public List<ThemisStatsClass> getClasses() {
        return theClasses;
    }

    @Override
    public void addClass(final ThemisStatsElement pElement) {
        theClasses.add((ThemisStatsClass) pElement);
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
        if (!(pThat instanceof ThemisStatsFile myThat)) {
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
