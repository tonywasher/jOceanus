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

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisMethodInstance;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisFile;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisModule;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisPackage;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;

/**
 * Solver Interface.
 */
public interface ThemisSolverDef {
    /**
     * The solver Project definition.
     */
    interface ThemisSolverProjectDef {
        /**
         * Obtain the underlying project.
         *
         * @return the project
         */
        ThemisProject getUnderlyingProject();
    }

    /**
     * The solver Module definition.
     */
    interface ThemisSolverModuleDef {
        /**
         * Obtain the project to which this module belongs.
         *
         * @return the project
         */
        ThemisSolverProjectDef getOwningProject();

        /**
         * Obtain the underlying module.
         *
         * @return the module
         */
        ThemisModule getUnderlyingModule();
    }

    /**
     * The solver Package definition.
     */
    interface ThemisSolverPackageDef {
        /**
         * Obtain the module to which this package belongs.
         *
         * @return the module
         */
        ThemisSolverModuleDef getOwningModule();

        /**
         * Obtain the underlying package.
         *
         * @return the package
         */
        ThemisPackage getUnderlyingPackage();

        /**
         * Obtain the package name.
         *
         * @return the packageName
         */
        String getPackageName();

        /**
         * Is this a standard package?
         *
         * @return true/false
         */
        boolean isStandard();
    }

    /**
     * The solver File definition.
     */
    interface ThemisSolverFileDef {
        /**
         * Obtain the package to which this file belongs.
         *
         * @return the package
         */
        ThemisSolverPackageDef getOwningPackage();

        /**
         * Obtain the underlying file.
         *
         * @return the file
         */
        ThemisFile getUnderlyingFile();
    }

    /**
     * The solver Class definition.
     */
    interface ThemisSolverClassDef {
        /**
         * Obtain the file to which this class belongs.
         *
         * @return the file
         */
        ThemisSolverFileDef getOwningFile();

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
        ThemisClassInstance getUnderlyingClass();
    }

    /**
     * The solver Method definition.
     */
    interface ThemisSolverMethodDef {
        /**
         * Obtain the class to which this class belongs.
         *
         * @return the class
         */
        ThemisSolverClassDef getOwningClass();

        /**
         * Obtain the underlying method.
         *
         * @return the class
         */
        ThemisMethodInstance getUnderlyingMethod();
    }
}
