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
package io.github.tonywasher.joceanus.themis.stats;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisModule;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics Project.
 */
public class ThemisStatsProject
        implements ThemisStatsElement {
    /**
     * The underlying project.
     */
    private final ThemisProject theProject;

    /**
     * The stats.
     */
    private final ThemisStats theStats;

    /**
     * The list of modules.
     */
    private final List<ThemisStatsModule> theModules;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     *
     * @param pParser the project parser
     */
    public ThemisStatsProject(final ThemisParser pParser) {
        /* Store the parameters */
        theProject = pParser.getProject();

        /* Create the stats */
        theStats = new ThemisStats();

        /* Create the Module list */
        theModules = new ArrayList<>();

        /* Protect against exceptions */
        try {
            /* Initialise the modules */
            for (ThemisModule myModule : theProject.getModules()) {
                theModules.add(new ThemisStatsModule(myModule));
            }

            /* Handle exceptions */
        } catch (OceanusException e) {
            /* Save Exception */
            theError = new ThemisIOException("Failed to parse Stats project", e);
        }
    }

    @Override
    public String getName() {
        return theProject.getName();
    }

    /**
     * Obtain the project.
     *
     * @return the project
     */
    public ThemisProject getUnderlying() {
        return theProject;
    }

    @Override
    public ThemisStats getStats() {
        return theStats;
    }

    /**
     * Obtain the modules.
     *
     * @return the modules
     */
    public List<ThemisStatsModule> getModules() {
        return theModules;
    }

    /**
     * Obtain the error.
     *
     * @return the error
     */
    public OceanusException getError() {
        return theError;
    }

    @Override
    public String toString() {
        return theProject.toString();
    }
}
