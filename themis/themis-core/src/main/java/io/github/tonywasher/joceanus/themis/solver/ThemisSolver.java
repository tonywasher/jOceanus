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
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
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
            /* Obtain the active profile */
            OceanusProfile myTask = myReport.getActiveTask();
            myTask = myTask.startTask(ThemisDataResource.TASK_SOLVER);

            /* Loop through all modules */
            myReport.initTask(ThemisDataResource.TASK_SOLVERPREPROCESS);
            myReport.setNumStages(myModules.size());
            OceanusProfile mySubTask = myTask.startTask(ThemisDataResource.TASK_SOLVERPREPROCESS);
            for (ThemisSolverModule myModule : myModules) {
                /* Update reporter and profile */
                final Map<String, ThemisSolverPackage> myPackages = myModule.getPackages();
                final String myName = myModule.getUnderlyingModule().getName();
                myReport.setNewStage(myName);
                myReport.setNumSteps(myPackages.size());
                mySubTask.startTask(myName);

                /* Loop through all packages */
                for (ThemisSolverPackage myPackage : myPackages.values()) {
                    /* preProcess each package */
                    myReport.setNextStep();
                    myMapper.preProcessPackage(myPackage);
                }
            }

            /* Loop through all modules */
            myReport.initTask(ThemisDataResource.TASK_SOLVERPROCESS);
            myReport.setNumStages(myModules.size());
            mySubTask = myTask.startTask(ThemisDataResource.TASK_SOLVERPROCESS);
            for (ThemisSolverModule myModule : myModules) {
                /* Update reporter and profile */
                final String myName = myModule.getUnderlyingModule().getName();
                myReport.setNewStage(myName);
                final Map<String, ThemisSolverPackage> myPackages = myModule.getPackages();
                myReport.setNumSteps(myPackages.size());
                mySubTask.startTask(myName);

                /* Loop through all packages */
                for (ThemisSolverPackage myPackage : myPackages.values()) {
                    /* Process each package */
                    myReport.setNextStep();
                    myMapper.processPackage(myPackage);
                }
            }

            /* Resolve all references */
            myTask.startTask(ThemisDataResource.TASK_RESOLVING);
            new ThemisMapperReference().processReferences(theProject);
            myTask.end();
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
