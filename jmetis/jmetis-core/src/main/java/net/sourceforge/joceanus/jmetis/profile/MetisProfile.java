/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.profile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Viewable version of SLF4J Profile data.
 */
public class MetisProfile
        implements MetisDataFieldItem {
    /**
     * number of decimals for elapsed.
     */
    private static final int NUM_DECIMALS = 6;

    /**
     * Local Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisProfile.class);

    /**
     * Task Field Id.
     */
    private static final MetisDataField FIELD_TASK = FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_TASK);

    /**
     * Status Field Id.
     */
    private static final MetisDataField FIELD_STATUS = FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_STATUS);

    /**
     * Elapsed Field Id.
     */
    private static final MetisDataField FIELD_ELAPSED = FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_ELAPSED);

    /**
     * Hidden Field Id.
     */
    private static final MetisDataField FIELD_HIDDEN = FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_HIDDEN);

    /**
     * Report fields.
     */
    private final MetisDataFieldSet theFields = new MetisDataFieldSet(MetisProfile.class, FIELD_DEFS);

    /**
     * Step name.
     */
    private final String theName;

    /**
     * Status.
     */
    private ProfileStatus theStatus;

    /**
     * Start time.
     */
    private final long theStart;

    /**
     * Elapsed (in milliseconds).
     */
    private TethysDecimal theElapsed;

    /**
     * Hidden Elapsed (in milliseconds).
     */
    private TethysDecimal theHidden;

    /**
     * Current subTask.
     */
    private MetisProfile theCurrentTask;

    /**
     * List of subTasks.
     */
    private List<MetisProfile> theSubTasks;

    /**
     * Constructor.
     * @param pName the name of the step
     */
    public MetisProfile(final String pName) {
        /* Record the name and start the timer */
        theName = pName;
        theStart = System.nanoTime();
        theStatus = ProfileStatus.RUNNING;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        /* Format the profile */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theName);
        myBuilder.append(": ");
        myBuilder.append(theStatus.isRunning()
                                               ? theStatus.toString()
                                               : theElapsed.toString());
        return myBuilder.toString();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return theFields;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_TASK.equals(pField)) {
            return theName;
        }
        if (FIELD_STATUS.equals(pField)) {
            return theStatus.isRunning()
                                         ? theStatus
                                         : MetisDataFieldValue.SKIP;
        }
        if (FIELD_ELAPSED.equals(pField)) {
            return theStatus.isRunning()
                                         ? MetisDataFieldValue.SKIP
                                         : theElapsed;
        }
        if (FIELD_HIDDEN.equals(pField)) {
            return theHidden == null
                                     ? MetisDataFieldValue.SKIP
                                     : theHidden;
        }

        /* Only possible if we have subTasks */
        if (theSubTasks == null) {
            return MetisDataFieldValue.UNKNOWN;
        }

        /* return the value */
        final int iIndex = pField.getIndex();
        return (iIndex < 0
                || iIndex >= theSubTasks.size())
                                                 ? MetisDataFieldValue.UNKNOWN
                                                 : theSubTasks.get(iIndex);
    }

    /**
     * Obtain the name of the profile.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Start a new subTask.
     * @param pName the name of the subTask
     * @return the new task
     */
    public MetisProfile startTask(final String pName) {
        /* If we are currently running */
        if (theStatus.isRunning()) {
            /* Prepare for the task */
            prepareForTask();

            /* Create the new task */
            final MetisProfile myTask = new MetisProfile(pName);
            theSubTasks.add(myTask);
            theFields.declareIndexField(pName);
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
            theElapsed = new TethysDecimal(myEnd - theStart, NUM_DECIMALS);
            theHidden = theSubTasks == null
                                            ? null
                                            : calculateHidden();

            /* Mark time as stopped */
            theStatus = ProfileStatus.STOPPED;
        }
    }

    /**
     * Calculate the hidden time.
     * @return the hidden time
     */
    private TethysDecimal calculateHidden() {
        /* Initialise hidden value */
        final TethysDecimal myHidden = new TethysDecimal(theElapsed);

        /* Loop through the subTasks */
        final Iterator<MetisProfile> myIterator = theSubTasks.iterator();
        while (myIterator.hasNext()) {
            final MetisProfile myProfile = myIterator.next();

            /* Subtract child time */
            myHidden.subtractValue(myProfile.theElapsed);
        }

        /* Return calculated value */
        return myHidden;
    }

    /**
     * is the task running?
     * @return true/false.
     */
    private boolean isRunning() {
        /* return status */
        return theStatus.isRunning();
    }

    /**
     * Obtain the currently active task.
     * @return the task
     */
    public MetisProfile getActiveTask() {
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
    private enum ProfileStatus {
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
         * @return true/false
         */
        private boolean isRunning() {
            switch (this) {
                case RUNNING:
                    return true;
                case STOPPED:
                default:
                    return false;
            }
        }
    }
}
