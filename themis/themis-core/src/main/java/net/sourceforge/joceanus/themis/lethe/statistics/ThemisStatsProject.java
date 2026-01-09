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
package net.sourceforge.joceanus.themis.lethe.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisProject;

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
    private final List<ThemisStatsBase> theChildren;

    /**
     * Constructor.
     * @param pProject the project
     */
    ThemisStatsProject(final ThemisAnalysisProject pProject) {
        /* Store parameters */
        theProject = pProject;

        /* Create lists */
        theChildren = new ArrayList<>();
    }

    /**
     * Obtain the project.
     * @return the project
     */
    public ThemisAnalysisProject getProject() {
        return theProject;
    }

    @Override
    public Iterator<ThemisStatsBase> childIterator() {
        return theChildren.iterator();
    }

    @Override
    public String toString() {
        return theProject.getName();
    }

    /**
     * Add module to list.
     * @param pModule the module
     */
    void addModule(final ThemisStatsModule pModule) {
        /* Add module to list */
        theChildren.add(pModule);
        pModule.setParent(this);

        /* Adjust count of files and packages */
        adjustChildStat(pModule, ThemisStat.TNPKG);
        adjustChildStat(pModule, ThemisStat.TNFI);

        /* Adjust counts */
        addChildTotals(pModule);
    }
}
