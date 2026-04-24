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
package io.github.tonywasher.joceanus.themis.solver;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.solver.mapper.ThemisMapper;
import io.github.tonywasher.joceanus.themis.solver.mapper.ThemisMapperReference;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverModule;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;

/**
 * Solver.
 */
public class ThemisSolver {
    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     *
     * @param pProject the project
     */
    public ThemisSolver(final ThemisSolverProject pProject) {
        /* Protect against exceptions */
        try (ThemisMapper myMapper = new ThemisMapper(pProject)) {
            /* Loop through all packages */
            for (ThemisSolverModule myModule : pProject.getModules()) {
                for (ThemisSolverPackage myPackage : myModule.getPackages().values()) {
                    /* preProcess each package */
                    myMapper.preProcessPackage(myPackage);
                }
            }

            /* Loop through all packages */
            for (ThemisSolverModule myModule : pProject.getModules()) {
                for (ThemisSolverPackage myPackage : myModule.getPackages().values()) {
                    /* Process each package */
                    myMapper.processPackage(myPackage);
                }
            }

            /* Process all references */
            new ThemisMapperReference().processReferences(pProject);

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
