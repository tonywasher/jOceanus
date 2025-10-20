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
package net.sourceforge.joceanus.themis.xanalysis.solver.proj;

import net.sourceforge.joceanus.themis.xanalysis.parser.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisModule;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverProjectDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Project.
 */
public class ThemisXAnalysisSolverProject
        implements ThemisXAnalysisSolverProjectDef {
    /**
     * The project parser.
     */
    private final ThemisXAnalysisParser theParser;

    /**
     * The underlying project.
     */
    private final ThemisXAnalysisProject theProject;

    /**
     * The list of modules.
     */
    private final List<ThemisXAnalysisSolverModule> theModules;

    /**
     * Constructor.
     * @param pParser the analysis parser
     */
    public ThemisXAnalysisSolverProject(final ThemisXAnalysisParser pParser) {
        /* Store the parameters */
        theParser = pParser;
        theProject = theParser.getProject();

        /* Create the Module list and parser */
        theModules = new ArrayList<>();

        /* Initialise the modules */
        for (ThemisXAnalysisModule myModule : theProject.getModules()) {
            theModules.add(new ThemisXAnalysisSolverModule(this, myModule));
        }
    }

    /**
     * Obtain the project parser.
     * @return the parser
     */
    public ThemisXAnalysisParser getProjectParser() {
        return theParser;
    }

    @Override
    public ThemisXAnalysisProject getUnderlyingProject() {
        return theProject;
    }

    /**
     * Obtain the modules.
     * @return the modules
     */
    public List<ThemisXAnalysisSolverModule> getModules() {
        return theModules;
    }

    @Override
    public String toString() {
        return theProject.toString();
    }
}
