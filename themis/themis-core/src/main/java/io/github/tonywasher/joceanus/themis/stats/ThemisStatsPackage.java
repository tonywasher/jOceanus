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
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Package.
 */
public class ThemisStatsPackage
        implements ThemisStatsElement {
    /**
     * The underlying package.
     */
    private final ThemisPackage thePackage;

    /**
     * The stats.
     */
    private final ThemisStats theStats;

    /**
     * The files.
     */
    private final List<ThemisStatsFile> theFiles;

    /**
     * Constructor.
     *
     * @param pPackage the parsed package
     */
    ThemisStatsPackage(final ThemisPackage pPackage) {
        /* Store the package */
        thePackage = pPackage;

        /* Create the stats */
        theStats = new ThemisStats();

        /* Create the List */
        theFiles = new ArrayList<>();

        /* Populate the fileList */
        for (ThemisFile myFile : thePackage.getFiles()) {
            theFiles.add(new ThemisStatsFile(myFile));
        }
    }

    @Override
    public String getName() {
        return thePackage.getPackage();
    }

    /**
     * Obtain the package.
     *
     * @return the package
     */
    public ThemisPackage getUnderlying() {
        return thePackage;
    }

    @Override
    public ThemisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the package name.
     *
     * @return the package name
     */
    public String getPackageName() {
        return thePackage.getPackage();
    }

    /**
     * Obtain the class map.
     *
     * @return the class map
     */
    public List<ThemisStatsFile> getFiles() {
        return theFiles;
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

        /* Make sure that the object is a StatsPackage */
        if (!(pThat instanceof ThemisStatsPackage myThat)) {
            return false;
        }

        /* Check name of package */
        return getPackageName().equals(myThat.getPackageName());
    }

    @Override
    public int hashCode() {
        return getPackageName().hashCode();
    }

    @Override
    public String toString() {
        return thePackage.toString();
    }
}
