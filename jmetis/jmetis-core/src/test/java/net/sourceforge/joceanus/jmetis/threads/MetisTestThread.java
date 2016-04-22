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
 */
public class MetisTestThread
        implements MetisThread<Void> {

    @Override
    public String getTaskName() {
        return "TestThread";
    }

    @Override
    public Void performTask(MetisThreadManager<?, ?> pManager) throws OceanusException {
        /* Set stage */
        boolean bContinue = pManager.setNumStages(2);

        /* Perform first task */
        if (bContinue) {
            bContinue = pManager.setNewStage("First")
                        && singleTask(pManager, 500);
        }

        /* Perform second task */
        if (bContinue) {
            bContinue = pManager.setNewStage("Second")
                        && singleTask(pManager, 200);
        }

        /* No result */
        return null;
    }

    /**
     * Perform one task.
     * @param pManager the thread manager
     * @param pNumSteps the number of steps
     * @return continue true/false
     * @throws OceanusException on error
     */
    private boolean singleTask(final MetisThreadManager<?, ?> pManager,
                               final int pNumSteps) throws OceanusException {
        /* Record task details */
        if (!pManager.setNumSteps(500)) {
            return false;
        }

        /* Protect against exceptions */
        try {
            /* Loop required times */
            for (int i = 0; i < pNumSteps; i++) {
                /* Sleep awhile */
                Thread.sleep(10);

                /* Set status */
                if (!pManager.setNextStep()) {
                    return false;
                }
            }
        } catch (InterruptedException e) {
            return false;
        }

        /* Continue */
        return true;
    }

    @Override
    public void processResult(final Void pResult) throws OceanusException {
        /* Nothing to do */
    }
}
