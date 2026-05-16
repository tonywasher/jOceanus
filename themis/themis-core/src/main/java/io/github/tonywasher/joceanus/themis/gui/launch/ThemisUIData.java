/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.gui.launch;

import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsProject;

import java.io.File;

/**
 * Themis Data.
 */
public class ThemisUIData {
    /**
     * The Location.
     */
    private final File theProjectDir;

    /**
     * The Parsed project.
     */
    private ThemisProject theParsedProject;

    /**
     * The Solved project.
     */
    private ThemisSolverProject theSolvedProject;

    /**
     * The Project Stats.
     */
    private ThemisStatsProject theProjectStats;

    /**
     * Constructor.
     *
     * @param pProjectDir the projectDirectory.
     */
    ThemisUIData(final File pProjectDir) {
        theProjectDir = pProjectDir;
    }

    /**
     * Obtain the projectDir.
     *
     * @return the projectDir
     */
    File getProjectDir() {
        return theProjectDir;
    }

    /**
     * Store the parsed project.
     *
     * @param pProject the project
     */
    void setParsedProject(final ThemisProject pProject) {
        theParsedProject = pProject;
    }

    /**
     * Obtain the parsed project.
     *
     * @return the project
     */
    ThemisProject getParsedProject() {
        return theParsedProject;
    }

    /**
     * Store the solved project.
     *
     * @param pProject the project
     */
    void setSolvedProject(final ThemisSolverProject pProject) {
        theSolvedProject = pProject;
    }

    /**
     * Obtain the solved project.
     *
     * @return the project
     */
    ThemisSolverProject getSolvedProject() {
        return theSolvedProject;
    }

    /**
     * Store the project stats.
     *
     * @param pStats the project stats
     */
    void setProjectStats(final ThemisStatsProject pStats) {
        theProjectStats = pStats;
    }

    /**
     * Obtain the project stats.
     *
     * @return the project
     */
    ThemisStatsProject getProjectStats() {
        return theProjectStats;
    }
}
