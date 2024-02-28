/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.event;

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysEventListener;

/**
 * Registration structure for event listeners.
 * @param <E> The event id type
 */
public class TethysEventRegistration<E extends Enum<E>> {
    /**
     * manager id.
     */
    private final Integer theMgrId;

    /**
     * Event listener.
     */
    private final TethysEventListener<E> theListener;

    /**
     * Action Id.
     */
    private final E theEventId;

    /**
     * registration id.
     */
    private Integer theRegId;

    /**
     * Constructor.
     * @param pMgrId the manager Id
     * @param pListener the listener
     */
    protected TethysEventRegistration(final Integer pMgrId,
                                      final TethysEventListener<E> pListener) {
        this(pMgrId, null, pListener);
    }

    /**
     * Constructor.
     * @param pMgrId the manager Id
     * @param pEventId the eventId to filter on
     * @param pListener the listener
     */
    protected TethysEventRegistration(final Integer pMgrId,
                                      final E pEventId,
                                      final TethysEventListener<E> pListener) {
        theMgrId = pMgrId;
        theEventId = pEventId;
        theListener = pListener;
    }

    /**
     * Is the event aimed at this registration.
     * @param pEvent the event
     * @return true/false
     */
    public boolean isRelevant(final TethysEvent<E> pEvent) {
        return theMgrId == pEvent.getSourceId();
    }

    /**
     * Obtain the registration id.
     * @return the id
     */
    protected Integer getRegId() {
        return theRegId;
    }

    /**
     * Set the registration id.
     * @param pId the id
     */
    protected void setRegId(final Integer pId) {
        theRegId = pId;
    }

    /**
     * Process the relevant event.
     * @param pEvent the event.
     */
    protected void processEvent(final TethysEvent<E> pEvent) {
        theListener.handleEvent(pEvent);
    }

    /**
     * Is the event filtered?
     * @param pEventId the event Id
     * @return true/false
     */
    protected boolean isFiltered(final E pEventId) {
        return theEventId != null
               && !theEventId.equals(pEventId);
    }

    @Override
    public boolean equals(final Object o) {
        /* Handle trivial cases */
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        /* Cast as Registration */
        final TethysEventRegistration<?> myReg = (TethysEventRegistration<?>) o;

        /* Compare fields */
        return theRegId.equals(myReg.getRegId());
    }

    @Override
    public int hashCode() {
        return theRegId;
    }
}
