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
package io.github.tonywasher.joceanus.themis.xanalysis.solver;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.mapper.ThemisXAnalysisMapper;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.mapper.ThemisXAnalysisMapperReference;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverModule;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;

/**
 * Solver.
 */
public class ThemisXAnalysisSolver {
    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     *
     * @param pProject the project
     */
    public ThemisXAnalysisSolver(final ThemisXAnalysisSolverProject pProject) {
        /* Protect against exceptions */
        try (ThemisXAnalysisMapper myMapper = new ThemisXAnalysisMapper(pProject)) {
            /* Loop through all packages */
            for (ThemisXAnalysisSolverModule myModule : pProject.getModules()) {
                for (ThemisXAnalysisSolverPackage myPackage : myModule.getPackages().values()) {
                    /* preProcess each package */
                    myMapper.preProcessPackage(myPackage);
                }
            }

            /* Loop through all packages */
            for (ThemisXAnalysisSolverModule myModule : pProject.getModules()) {
                for (ThemisXAnalysisSolverPackage myPackage : myModule.getPackages().values()) {
                    /* Process each package */
                    myMapper.processPackage(myPackage);
                }
            }

            /* Process all references */
            new ThemisXAnalysisMapperReference().processReferences(pProject);

        } catch (OceanusException e) {
            theError = e;
        }
    }

    /**
     * Obtain the error.
     *
     * @return the error
     */
    public OceanusException getError() {
        return theError;
    }
}
