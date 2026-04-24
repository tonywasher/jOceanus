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
package io.github.tonywasher.joceanus.themis.solver.proj;

import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisModule;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverDef.ThemisSolverProjectDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Project.
 */
public class ThemisSolverProject
        implements ThemisSolverProjectDef {
    /**
     * The project parser.
     */
    private final ThemisParser theParser;

    /**
     * The underlying project.
     */
    private final ThemisProject theProject;

    /**
     * The list of modules.
     */
    private final List<ThemisSolverModule> theModules;

    /**
     * Constructor.
     *
     * @param pParser the analysis parser
     */
    public ThemisSolverProject(final ThemisParser pParser) {
        /* Store the parameters */
        theParser = pParser;
        theProject = theParser.getProject();

        /* Create the Module list and parser */
        theModules = new ArrayList<>();

        /* Initialise the modules */
        for (ThemisModule myModule : theProject.getModules()) {
            theModules.add(new ThemisSolverModule(this, myModule));
        }
    }

    /**
     * Obtain the project parser.
     *
     * @return the parser
     */
    public ThemisParser getProjectParser() {
        return theParser;
    }

    @Override
    public ThemisProject getUnderlyingProject() {
        return theProject;
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    public List<ThemisSolverModule> getModules() {
        return theModules;
    }

    @Override
    public String toString() {
        return theProject.toString();
    }
}
