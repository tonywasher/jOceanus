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
package net.sourceforge.joceanus.themis.xanalysis.solver.proj;

import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisModule;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverModuleDef;
import net.sourceforge.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverDef.ThemisXAnalysisSolverProjectDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver Module.
 */
public class ThemisXAnalysisSolverModule
        implements ThemisXAnalysisSolverModuleDef {
    /**
     * The owning project.
     */
    private final ThemisXAnalysisSolverProjectDef theProject;

    /**
     * The underlying module.
     */
    private final ThemisXAnalysisModule theModule;

    /**
     * The list of packages.
     */
    private final List<ThemisXAnalysisSolverPackage> thePackages;

    /**
     * Constructor.
     * @param pProject the owning project
     * @param pModule the parsed module
     */
    ThemisXAnalysisSolverModule(final ThemisXAnalysisSolverProjectDef pProject,
                                final ThemisXAnalysisModule pModule) {
        /* Store the parameters */
        theProject = pProject;
        theModule = pModule;

        /* Initialise the packages */
        thePackages = new ArrayList<>();
        for (ThemisXAnalysisPackage myPackage : theModule.getPackages()) {
            final ThemisXAnalysisSolverPackage mySolverPackage = new ThemisXAnalysisSolverPackage(this, myPackage);
            thePackages.add(mySolverPackage);
        }
    }

    @Override
    public ThemisXAnalysisSolverProjectDef getOwningProject() {
        return theProject;
    }

    @Override
    public ThemisXAnalysisModule getUnderlyingModule() {
        return theModule;
    }

    /**
     * Obtain the packages.
     * @return the packages
     */
    public List<ThemisXAnalysisSolverPackage> getPackages() {
        return thePackages;
    }

    @Override
    public String toString() {
        return theModule.toString();
    }
}
