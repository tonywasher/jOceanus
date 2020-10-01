/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMPackage;

/**
 * Package statistics.
 */
public class ThemisStatsPackage
        extends ThemisStatsBase {
    /**
     * The package.
     */
    private final ThemisAnalysisPackage thePackage;

    /**
     * The sourceMeter package Stats.
     */
    private final ThemisSMPackage theSMPackage;

    /**
     * The file list.
     */
    private final List<ThemisStatsFile> theFiles;

    /**
     * Constructor.
     * @param pPackage the package
     * @param pSourceMeter the sourceMeter stats
     */
    ThemisStatsPackage(final ThemisAnalysisPackage pPackage,
                       final ThemisSMPackage pSourceMeter) {
        /* Store parameters */
        thePackage = pPackage;
        theSMPackage = pSourceMeter;

        /* Create lists */
        theFiles = new ArrayList<>();
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public ThemisAnalysisPackage getPackage() {
        return thePackage;
    }

    /**
     * Obtain the sourceMeter stats.
     * @return the stats
     */
    public ThemisSMPackage getSourceMeter() {
        return theSMPackage;
    }

    /**
     * Obtain file iterator.
     * @return the iterator
     */
    Iterator<ThemisStatsFile> fileIterator() {
        return theFiles.iterator();
    }

    /**
     * Add file to list.
     * @param pFile the file
     */
    void addFile(final ThemisStatsFile pFile) {
        theFiles.add(pFile);
    }
}
