/*
 * Tethys: GUI Utilities
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
package io.github.tonywasher.joceanus.tethys.test.ui;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThread;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Thread class.
 */
public class TethysTestThread
        implements TethysUIThread<Void> {

    @Override
    public String getTaskName() {
        return "TestThread";
    }

    @Override
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Set stages */
        pManager.setNumStages(2);

        /* Perform first task */
        pManager.setNewStage("First");
        singleTask(pManager, 500);

        /* Perform second task */
        pManager.setNewStage("Second");
        singleTask(pManager, 200);

        /* No result */
        return null;
    }

    /**
     * Perform one task.
     *
     * @param pManager  the thread manager
     * @param pNumSteps the number of steps
     * @throws OceanusException on error
     */
    private static void singleTask(final TethysUIThreadManager pManager,
                                   final int pNumSteps) throws OceanusException {
        /* Record task details */
        pManager.setNumSteps(pNumSteps);

        /* Protect against exceptions */
        try {
            /* Loop required times */
            for (int i = 0; i < pNumSteps; i++) {
                /* Sleep awhile */
                TimeUnit.MILLISECONDS.sleep(10);

                /* Set status */
                pManager.setNextStep();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
