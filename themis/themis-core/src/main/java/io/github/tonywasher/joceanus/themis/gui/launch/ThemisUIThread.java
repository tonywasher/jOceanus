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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThread;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
import io.github.tonywasher.joceanus.themis.parser.project.ThemisProject;
import io.github.tonywasher.joceanus.themis.solver.ThemisSolver;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsProject;

import java.io.File;

/**
 * Themis Loader Thread.
 */
public class ThemisUIThread
        implements TethysUIThread<ThemisUIData> {
    /**
     * The new data interface.
     */
    interface ThemisUIThreadData {
        /**
         * Set new Data.
         *
         * @param pData the data
         */
        void setNewData(ThemisUIData pData);
    }

    /**
     * The parent.
     */
    private final ThemisUIThreadData theParent;

    /**
     * The Data.
     */
    private final ThemisUIData theData;

    /**
     * Constructor (Event Thread).
     *
     * @param pParent   the parent
     * @param pLocation the project location
     */
    ThemisUIThread(final ThemisUIThreadData pParent,
                   final File pLocation) {
        theParent = pParent;
        theData = new ThemisUIData(pLocation);
    }

    @Override
    public String getTaskName() {
        return ThemisDataResource.TASK_DATALOAD.getValue();
    }

    @Override
    public ThemisUIData performTask(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Initialise the status window */
        pReport.initTask(getTaskName());

        /* Parse the project */
        final ThemisParser myParser = new ThemisParser(pReport, theData.getProjectDir());
        final ThemisProject myProject = myParser.getProject();
        theData.setParsedProject(myProject);

        /* Resolve references */
        final ThemisSolver mySolver = new ThemisSolver(myParser);
        final ThemisSolverProject mySolved = mySolver.getProject();
        theData.setSolvedProject(mySolved);

        /* Calculate statistics */
        final ThemisStatsProject myStats = new ThemisStatsProject(myParser);
        theData.setProjectStats(myStats);

        /* State that we have completed */
        pReport.setCompletion();

        /* Return the data */
        return theData;
    }

    @Override
    public void processResult(final ThemisUIData pResult) {
        theParent.setNewData(pResult);
    }
}
