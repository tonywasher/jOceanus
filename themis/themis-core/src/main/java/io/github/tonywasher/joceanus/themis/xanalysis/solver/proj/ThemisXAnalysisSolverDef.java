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
package io.github.tonywasher.joceanus.themis.xanalysis.solver.proj;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisModule;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;

/**
 * Solver Interface.
 */
public interface ThemisXAnalysisSolverDef {
    /**
     * The solver Project definition.
     */
    interface ThemisXAnalysisSolverProjectDef {
        /**
         * Obtain the underlying project.
         *
         * @return the project
         */
        ThemisXAnalysisProject getUnderlyingProject();
    }

    /**
     * The solver Module definition.
     */
    interface ThemisXAnalysisSolverModuleDef {
        /**
         * Obtain the project to which this module belongs.
         *
         * @return the project
         */
        ThemisXAnalysisSolverProjectDef getOwningProject();

        /**
         * Obtain the underlying module.
         *
         * @return the module
         */
        ThemisXAnalysisModule getUnderlyingModule();
    }

    /**
     * The solver Package definition.
     */
    interface ThemisXAnalysisSolverPackageDef {
        /**
         * Obtain the module to which this package belongs.
         *
         * @return the module
         */
        ThemisXAnalysisSolverModuleDef getOwningModule();

        /**
         * Obtain the underlying package.
         *
         * @return the package
         */
        ThemisXAnalysisPackage getUnderlyingPackage();
    }

    /**
     * The solver File definition.
     */
    interface ThemisXAnalysisSolverFileDef {
        /**
         * Obtain the package to which this file belongs.
         *
         * @return the package
         */
        ThemisXAnalysisSolverPackageDef getOwningPackage();

        /**
         * Obtain the underlying file.
         *
         * @return the file
         */
        ThemisXAnalysisFile getUnderlyingFile();
    }

    /**
     * The solver Class definition.
     */
    interface ThemisXAnalysisSolverClassDef {
        /**
         * Obtain the file to which this class belongs.
         *
         * @return the file
         */
        ThemisXAnalysisSolverFileDef getOwningFile();

        /**
         * Obtain the full className.
         *
         * @return the full class name
         */
        String getFullName();

        /**
         * Obtain the underlying class.
         *
         * @return the class
         */
        ThemisXAnalysisClassInstance getUnderlyingClass();
    }

    /**
     * The solver Method definition.
     */
    interface ThemisXAnalysisSolverMethodDef {
        /**
         * Obtain the class to which this class belongs.
         *
         * @return the class
         */
        ThemisXAnalysisSolverClassDef getOwningClass();

        /**
         * Obtain the underlying method.
         *
         * @return the class
         */
        ThemisXAnalysisMethodInstance getUnderlyingMethod();
    }
}
