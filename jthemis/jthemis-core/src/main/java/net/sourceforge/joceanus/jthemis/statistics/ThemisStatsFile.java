/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
import java.util.Map;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisPackage;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMFile;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;

/**
 * File statistics.
 */
public class ThemisStatsFile
        extends ThemisStatsBase {
    /**
     * The file.
     */
    private final ThemisAnalysisFile theFile;

    /**
     * The sourceMeter file Stats.
     */
    private final ThemisSMFile theSMFile;

    /**
     * The child list.
     */
    private final List<ThemisStatsBase> theChildren;

    /**
     * Constructor.
     * @param pFile the file
     * @param pSourceMeter the sourceMeter stats
     */
    ThemisStatsFile(final ThemisAnalysisFile pFile,
                    final ThemisSMFile pSourceMeter) {
        /* Store parameters */
        theFile = pFile;
        theSMFile = pSourceMeter;

        /* Create lists */
        theChildren = new ArrayList<>();
    }

    /**
     * Obtain the file.
     * @return the file
     */
    public ThemisAnalysisFile getFile() {
        return theFile;
    }

    @Override
    public ThemisSMFile getSourceMeter() {
        return theSMFile;
    }

    @Override
    public Map<ThemisSMStat, Integer> getSourceMeterStats() {
        return theSMFile == null ? null : theSMFile.getStatistics();
    }

    @Override
    public Iterator<ThemisStatsBase> childIterator() {
        return theChildren.iterator();
    }

    @Override
    public String toString() {
        return theFile.getName() + ThemisAnalysisPackage.SFX_JAVA;
    }

    @Override
    public void addClass(final ThemisStatsClass pClass) {
        /* Add class to list */
        theChildren.add(pClass);
        pClass.setParent(this);

        /* Adjust counts */
        pClass.addStatsToTotals();
        addChildTotals(pClass);
    }

    @Override
    public void addMethod(final ThemisStatsMethod pMethod) {
        /* Methods cannot be a direct child of a file */
    }
}