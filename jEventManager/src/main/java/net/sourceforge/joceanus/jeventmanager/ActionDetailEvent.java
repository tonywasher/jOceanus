/*******************************************************************************
 * jEventManager: Java Event Manager
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jeventmanager;

import java.awt.event.ActionEvent;

/**
 * The extended action event. This allows the definition of multiple ActionIds by providing an additional subId field. The original Action Event is restricted
 * to a single Id <b>ACTION_PERFORMED</b>. In addition, rather than using the command string, this is set to NULL and a user defined object is available to
 * provide details about the event that do not have to be parsed from a string.
 */
public class ActionDetailEvent
        extends ActionEvent {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -556484648311704973L;

    /**
     * The first available Event SubId.
     */
    private static final int ACTION_RANGE = 100;

    /**
     * The first available Event SubId.
     */
    public static final int ACTION_FIRST = ACTION_PERFORMED;

    /**
     * The last available Event SubId.
     */
    public static final int ACTION_LAST = ACTION_PERFORMED
                                          + ACTION_RANGE
                                          - 1;

    /**
     * The subId of the event.
     */
    private final int theSubId;

    /**
     * The details of the event.
     */
    private final Object theDetails;

    /**
     * Obtain the subId.
     * @return the subId
     */
    public int getSubId() {
        return theSubId;
    }

    /**
     * Obtain the details.
     * @return the details
     */
    public Object getDetails() {
        return theDetails;
    }

    /**
     * Constructor.
     * @param pSource the source of the event
     * @param pId the id of the event
     * @param pSubId the subId for the event
     * @param pDetails the details of the event
     */
    public ActionDetailEvent(final Object pSource,
                             final int pId,
                             final int pSubId,
                             final Object pDetails) {
        /* Call super-constructor */
        super(pSource, pId, (pDetails instanceof String)
                ? ((String) pDetails)
                : null);

        /* Set the details */
        theSubId = pSubId;
        theDetails = pDetails;
    }

    /**
     * Constructor.
     * @param pSource the source of the event
     * @param pEvent the source event
     */
    public ActionDetailEvent(final Object pSource,
                             final ActionDetailEvent pEvent) {
        /* Call super-constructor */
        super(pSource, pEvent.getID(), pEvent.getActionCommand());

        /* Set the details */
        theSubId = pEvent.getSubId();
        theDetails = pEvent.getDetails();
    }
}
