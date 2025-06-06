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
package net.sourceforge.joceanus.themis.xanalysis.dsm;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisModule;
import net.sourceforge.joceanus.themis.xanalysis.proj.ThemisXAnalysisProject;

import java.util.ArrayList;
import java.util.List;

/**
 * DSM Project.
 */
public class ThemisXAnalysisDSMProject {
    /**
     * The underlying project.
     */
    private final ThemisXAnalysisProject theProject;

    /**
     * The list of modules.
     */
    private final List<ThemisXAnalysisDSMModule> theModules;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pProject the parsed project
     */
    public ThemisXAnalysisDSMProject(final ThemisXAnalysisProject pProject) {
        /* Store the parameters */
        theProject = pProject;

        /* Create the Module list */
        theModules = new ArrayList<>();

        /* Protect against exceptions */
        try {
            /* Initialise the modules */
            for (ThemisXAnalysisModule myModule : theProject.getModules()) {
                theModules.add(new ThemisXAnalysisDSMModule(myModule));
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException("Failed to parse DSM project", e);
        }
    }

    /**
     * Obtain the project.
     * @return the project
     */
    public ThemisXAnalysisProject getProject() {
        return theProject;
    }

    /**
     * Obtain the modules.
     * @return the modules
     */
    public List<ThemisXAnalysisDSMModule> getModules() {
        return theModules;
    }

    /**
     * Obtain the error.
     * @return the error
     */
    public OceanusException getError() {
        return theError;
    }

    @Override
    public String toString() {
        return theProject.toString();
    }
}
