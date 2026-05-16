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
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.solver.mapper.ThemisMapper;
import io.github.tonywasher.joceanus.themis.solver.mapper.ThemisMapperReference;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverModule;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverPackage;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;

import java.util.List;
import java.util.Map;

/**
 * Solver.
 */
public class ThemisSolver {
    /**
     * The SolverProject.
     */
    private final ThemisSolverProject theProject;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @throws OceanusException on error
     */
    public ThemisSolver(final ThemisParser pParser) throws OceanusException {
        /* Create SolverProject */
        theProject = new ThemisSolverProject(pParser);
        final List<ThemisSolverModule> myModules = theProject.getModules();

        /* Obtain the reporter */
        final TethysUIThreadStatusReport myReport = pParser.getReporter();

        /* Protect against exceptions */
        try (ThemisMapper myMapper = new ThemisMapper(theProject)) {
            /* Loop through all packages */
            myReport.initTask("Solver preProcess");
            myReport.setNumStages(myModules.size());
            for (ThemisSolverModule myModule : myModules) {
                myReport.setNewStage(myModule.getUnderlyingModule().getName());
                final Map<String, ThemisSolverPackage> myPackages = myModule.getPackages();
                myReport.setNumSteps(myPackages.size());
                for (ThemisSolverPackage myPackage : myPackages.values()) {
                    /* preProcess each package */
                    myReport.setNextStep();
                    myMapper.preProcessPackage(myPackage);
                }
            }

            /* Loop through all packages */
            myReport.initTask("Solver process");
            myReport.setNumStages(myModules.size());
            for (ThemisSolverModule myModule : myModules) {
                myReport.setNewStage(myModule.getUnderlyingModule().getName());
                final Map<String, ThemisSolverPackage> myPackages = myModule.getPackages();
                myReport.setNumSteps(myPackages.size());
                for (ThemisSolverPackage myPackage : myPackages.values()) {
                    /* Process each package */
                    myReport.setNextStep();
                    myMapper.processPackage(myPackage);
                }
            }

            /* Process all references */
            new ThemisMapperReference().processReferences(theProject);
        }
    }

    /**
     * Obtain the project.
     *
     * @return the project
     */
    public ThemisSolverProject getProject() {
        return theProject;
    }
}
