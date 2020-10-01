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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisFile;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMFile;

/**
 * File statistics.
 */
public class ThemisStatsFile
        extends ThemisStatsBase
        implements ThemisStatsOwner {
    /**
     * The file.
     */
    private final ThemisAnalysisFile theFile;

    /**
     * The sourceMeter file Stats.
     */
    private final ThemisSMFile theSMFile;

    /**
     * The class list.
     */
    private final List<ThemisStatsClass> theClasses;

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
        theClasses = new ArrayList<>();
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
    public Iterator<ThemisStatsClass> classIterator() {
        return theClasses.iterator();
    }

    @Override
    public void addClass(final ThemisStatsClass pClass) {
        theClasses.add(pClass);
    }

    @Override
    public Iterator<ThemisStatsMethod> methodIterator() {
        return Collections.emptyIterator();
    }

    @Override
    public void addMethod(final ThemisStatsMethod pMethod) {
    }
}
