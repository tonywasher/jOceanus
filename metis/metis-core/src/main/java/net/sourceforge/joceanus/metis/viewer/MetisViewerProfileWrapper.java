/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.viewer;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile.TethysProfileStatus;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Profile wrapper.
 */
public class MetisViewerProfileWrapper
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MetisViewerProfileWrapper> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisViewerProfileWrapper.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_TASK, MetisViewerProfileWrapper::getName);
        FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_STATUS, MetisViewerProfileWrapper::getStatus);
        FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_ELAPSED, MetisViewerProfileWrapper::getElapsed);
        FIELD_DEFS.declareLocalField(MetisDataResource.PROFILE_HIDDEN, MetisViewerProfileWrapper::getHidden);
    }

    /**
     * Report fields.
     */
    private final MetisFieldSet<MetisViewerProfileWrapper> theFields = MetisFieldSet.newFieldSet(this);

    /**
     * The wrapped profile.
     */
    private final OceanusProfile theWrapped;

    /**
     * Create a new Metis Profile Wrapper for an underlying TethysProfile.
     * @param pProfile the underlying profile
     */
    public MetisViewerProfileWrapper(final OceanusProfile pProfile) {
        /* Store details */
        theWrapped = pProfile;

        /* Loop through the subtasks */
        final Iterator<OceanusProfile> myIterator = theWrapped.subTaskIterator();
        while (myIterator.hasNext()) {
            final OceanusProfile mySubTask = myIterator.next();
            theFields.declareLocalField(mySubTask.getName(), p -> mySubTask);
        }
    }

    @Override
    public MetisFieldSet<MetisViewerProfileWrapper> getDataFieldSet() {
        return theFields;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        /* Format the profile */
        return getName()
                + ": "
                + (theWrapped.isRunning()
                        ? getStatus()
                        : getElapsed());
    }

    /**
     * Obtain the name of the profile.
     * @return the name
     */
    private String getName() {
        return theWrapped.getName();
    }

    /**
     * Obtain the status of the profile.
     * @return the status
     */
    private TethysProfileStatus getStatus() {
        return theWrapped.isRunning()
                ? theWrapped.getStatus()
                : null;
    }

    /**
     * Obtain the elapsed time of the profile.
     * @return the elapsedTime
     */
    private OceanusDecimal getElapsed() {
        return theWrapped.isRunning()
                ? null
                : theWrapped.getElapsed();
    }

    /**
     * Obtain the hidden time of the profile.
     * @return the hiddenTime
     */
    private OceanusDecimal getHidden() {
        return theWrapped.getHidden();
    }
}
