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

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;

/**
 * Project statistics.
 */
public class ThemisStatsProject
        extends ThemisStatsBase {
    /**
     * The project.
     */
    private final ThemisAnalysisProject theProject;

    /**
     * The module list.
     */
    private final List<ThemisStatsModule> theModules;

    /**
     * Constructor.
     * @param pProject the project
     */
    ThemisStatsProject(final ThemisAnalysisProject pProject) {
        /* Store parameters */
        theProject = pProject;

        /* Create lists */
        theModules = new ArrayList<>();
    }

    /**
     * Obtain the project.
     * @return the project
     */
    public ThemisAnalysisProject getProject() {
        return theProject;
    }

    /**
     * Obtain module iterator.
     * @return the iterator
     */
    Iterator<ThemisStatsModule> moduleIterator() {
        return theModules.iterator();
    }

    /**
     * Add module to list.
     * @param pModule the module
     */
    void addModule(final ThemisStatsModule pModule) {
        /* Add module to list */
        theModules.add(pModule);

        /* Adjust count of files and packages */
        adjustStat(ThemisSMStat.TNPKG, pModule.getStat(ThemisSMStat.NPKG));
        adjustStat(ThemisSMStat.TNFI, pModule.getStat(ThemisSMStat.TNFI));

        /* Adjust counts */
        adjustStat(ThemisSMStat.TNCL, pModule.getStat(ThemisSMStat.TNCL));
        adjustStat(ThemisSMStat.TNIN, pModule.getStat(ThemisSMStat.TNIN));
        adjustStat(ThemisSMStat.TNEN, pModule.getStat(ThemisSMStat.TNEN));
        adjustStat(ThemisSMStat.TNM, pModule.getStat(ThemisSMStat.TNM));
        adjustStat(ThemisSMStat.TNOS, pModule.getStat(ThemisSMStat.TNOS));
        adjustStat(ThemisSMStat.TLOC, pModule.getStat(ThemisSMStat.TLOC));
        adjustStat(ThemisSMStat.TLLOC, pModule.getStat(ThemisSMStat.TLLOC));
        adjustStat(ThemisSMStat.TCLOC, pModule.getStat(ThemisSMStat.TCLOC));
        adjustStat(ThemisSMStat.TDLOC, pModule.getStat(ThemisSMStat.TDLOC));
    }
}
