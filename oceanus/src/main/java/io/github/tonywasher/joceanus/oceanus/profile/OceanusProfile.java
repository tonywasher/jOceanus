/*
 * Oceanus: Java Utilities
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
package io.github.tonywasher.joceanus.oceanus.profile;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Profile data.
 */
public class OceanusProfile {
    /**
     * number of decimals for elapsed.
     */
    private static final int NUM_DECIMALS = 6;

    /**
     * Step name.
     */
    private final String theName;

    /**
     * Status.
     */
    private OceanusProfileStatus theStatus;

    /**
     * Start time.
     */
    private final long theStart;

    /**
     * Elapsed (in milliseconds).
     */
    private OceanusDecimal theElapsed;

    /**
     * Hidden Elapsed (in milliseconds).
     */
    private OceanusDecimal theHidden;

    /**
     * Current subTask.
     */
    private OceanusProfile theCurrentTask;

    /**
     * List of subTasks.
     */
    private List<OceanusProfile> theSubTasks;

    /**
     * Constructor.
     *
     * @param pName the name of the step
     */
    public OceanusProfile(final String pName) {
        /* Record the name and start the timer */
        theName = pName;
        theStart = System.nanoTime();
        theStatus = OceanusProfileStatus.RUNNING;
    }

    /**
     * Obtain the name of the profile.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the status of the profile.
     *
     * @return the status
     */
    public OceanusProfileStatus getStatus() {
        return theStatus.isRunning()
                ? theStatus
                : null;
    }

    /**
     * Obtain the elapsed time of the profile.
     *
     * @return the elapsedTime
     */
    public OceanusDecimal getElapsed() {
        return theStatus.isRunning()
                ? null
                : theElapsed;
    }

    /**
     * Obtain the hidden time of the profile.
     *
     * @return the hiddenTime
     */
    public OceanusDecimal getHidden() {
        return theHidden;
    }

    /**
     * Obtain the subtask iterator.
     *
     * @return the iterator
     */
    public Iterator<OceanusProfile> subTaskIterator() {
        return theSubTasks == null ? Collections.emptyIterator() : theSubTasks.iterator();
    }

    /**
     * Start a new subTask.
     *
     * @param pName the name of the subTask
     * @return the new task
     */
    public OceanusProfile startTask(final String pName) {
        /* If we are currently running */
        if (theStatus.isRunning()) {
            /* Prepare for the task */
            prepareForTask();

            /* Loop through the subTasks */
            for (OceanusProfile myProfile : theSubTasks) {
                /* Check for duplicate name */
                if (pName.equals(myProfile.getName())) {
                    throw new IllegalArgumentException("Duplicate Task - " + pName);
                }
            }

            /* Create the new task */
            final OceanusProfile myTask = new OceanusProfile(pName);
            theSubTasks.add(myTask);
            theCurrentTask = myTask;
        }

        /* Return the current task */
        return theCurrentTask;
    }

    /**
     * Prepare for the task.
     */
    private void prepareForTask() {
        /* End any subTask */
        endSubTask();

        /* If we do not currently have a subTask list */
        if (theSubTasks == null) {
            /* Create the list */
            theSubTasks = new ArrayList<>();
        }
    }

    /**
     * End any subTask.
     */
    private void endSubTask() {
        /* If we have a subTask */
        if (theCurrentTask != null) {
            /* End the current task */
            theCurrentTask.end();
            theCurrentTask = null;
        }
    }

    /**
     * End the task.
     */
    public void end() {
        /* If we are currently running */
        if (theStatus.isRunning()) {
            /* End any subTasks */
            endSubTask();

            /* Stop the task and calculate the elapsed time */
            final long myEnd = System.nanoTime();
            theElapsed = new OceanusDecimal(myEnd - theStart, NUM_DECIMALS);
            theHidden = theSubTasks == null
                    ? null
                    : calculateHidden();

            /* Mark time as stopped */
            theStatus = OceanusProfileStatus.STOPPED;
        }
    }

    /**
     * Calculate the hidden time.
     *
     * @return the hidden time
     */
    private OceanusDecimal calculateHidden() {
        /* Initialise hidden value */
        final OceanusDecimal myHidden = new OceanusDecimal(theElapsed);

        /* Loop through the subTasks */
        for (OceanusProfile myProfile : theSubTasks) {
            /* Subtract child time */
            myHidden.subtractValue(myProfile.theElapsed);
        }

        /* Return calculated value */
        return myHidden;
    }

    /**
     * is the task running?
     *
     * @return true/false.
     */
    public boolean isRunning() {
        /* return status */
        return theStatus.isRunning();
    }

    /**
     * Obtain the currently active task.
     *
     * @return the task
     */
    public OceanusProfile getActiveTask() {
        /* If we are not currently running */
        if (!isRunning()) {
            return null;
        }

        /* Return self is no active and running subTask else ask subTask */
        return theCurrentTask == null
                || !theCurrentTask.isRunning()
                ? this
                : theCurrentTask.getActiveTask();
    }

    /**
     * Status of timer.
     */
    public enum OceanusProfileStatus {
        /**
         * Running.
         */
        RUNNING,

        /**
         * Stopped.
         */
        STOPPED;

        /**
         * is the timer running?
         *
         * @return true/false
         */
        private boolean isRunning() {
            return this == RUNNING;
        }
    }
}

