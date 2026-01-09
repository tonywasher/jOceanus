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
package net.sourceforge.joceanus.themis.xanalysis.solver;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverModule;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;
import net.sourceforge.joceanus.themis.xanalysis.solver.reflect.ThemisXAnalysisReflectJar;

/**
 * Solver.
 */
public class ThemisXAnalysisSolver {
    /**
     * The state.
     */
    private final ThemisXAnalysisSolverProjectState theState;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pProject the project
     */
    public ThemisXAnalysisSolver(final ThemisXAnalysisSolverProject pProject) {
        /* Create the state */
        theState = new ThemisXAnalysisSolverProjectState(pProject);

        /* Process external classes */
        try (ThemisXAnalysisReflectJar myJar = new ThemisXAnalysisReflectJar(pProject.getProjectParser())) {
            /* Process javaLang and other external classes */
            myJar.processExternalClasses(theState.getExternalClassMap());
        } catch (OceanusException e) {
            theError = e;
        }

        /* If we have no error */
        if (theError == null) {
            /* Loop through all packages */
            for (ThemisXAnalysisSolverModule myModule : pProject.getModules()) {
                for (ThemisXAnalysisSolverPackage myPackage : myModule.getPackages()) {
                    /* Process each package */
                    theState.processPackage(myPackage);
                }
            }
        }
     }

    /**
     * Obtain the error.
     * @return the error
     */
    public OceanusException getError() {
        return theError;
    }
}
