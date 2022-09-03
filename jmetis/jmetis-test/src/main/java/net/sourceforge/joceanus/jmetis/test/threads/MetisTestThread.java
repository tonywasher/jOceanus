/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.test.threads;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadData;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;

import java.util.concurrent.TimeUnit;

/**
 * Thread class.
 */
public class MetisTestThread
        implements MetisThread<Void> {

    @Override
    public String getTaskName() {
        return "TestThread";
    }

    @Override
    public Void performTask(final MetisThreadData pThreadData) throws OceanusException {
        /* Access the Thread Manager */
        final MetisToolkit myToolkit = (MetisToolkit) pThreadData;
        final MetisThreadManager myManager = null; myToolkit.getThreadManager();

        /* Set stages */
        myManager.setNumStages(2);

        /* Perform first task */
        myManager.setNewStage("First");
        singleTask(myManager, 500);

        /* Perform second task */
        myManager.setNewStage("Second");
        singleTask(myManager, 200);

        /* No result */
        return null;
    }

    /**
     * Perform one task.
     * @param pManager the thread manager
     * @param pNumSteps the number of steps
     * @throws OceanusException on error
     */
    private static void singleTask(final MetisThreadManager pManager,
                                   final int pNumSteps) throws OceanusException {
        /* Record task details */
        pManager.setNumSteps(500);

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
