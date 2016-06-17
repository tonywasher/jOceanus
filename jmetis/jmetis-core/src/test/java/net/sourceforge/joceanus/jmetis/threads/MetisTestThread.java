/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Thread class.
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public class MetisTestThread<N, I>
        implements MetisThread<Void, N, I> {

    @Override
    public String getTaskName() {
        return "TestThread";
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the Thread Manager */
        MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();

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
    private void singleTask(final MetisThreadManager<N, I> pManager,
                            final int pNumSteps) throws OceanusException {
        /* Record task details */
        pManager.setNumSteps(500);

        /* Protect against exceptions */
        try {
            /* Loop required times */
            for (int i = 0; i < pNumSteps; i++) {
                /* Sleep awhile */
                Thread.sleep(10);

                /* Set status */
                pManager.setNextStep();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
