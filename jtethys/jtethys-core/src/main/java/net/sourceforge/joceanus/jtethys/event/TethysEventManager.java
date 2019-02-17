/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * EventManager implementation. This provides means for classes to fire events to registered
 * listeners.
 * @param <E> The event id type
 */
public class TethysEventManager<E extends Enum<E>>
        implements TethysEventProvider<E> {
    /**
     * The Next managerId.
     */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    /**
     * The Id of this manager.
     */
    private final Integer theMgrId;

    /**
     * The registrar.
     */
    private final TethysEventRegistrar<E> theRegistrar;

    /**
     * Constructor.
     */
    public TethysEventManager() {
        /* Store the owner */
        theMgrId = NEXT_ID.getAndIncrement();

        /* Allocate the registrar */
        theRegistrar = new TethysEventRegistrar<>(theMgrId);
    }

    /**
     * Obtain ownerId.
     * @return the owner Id
     */
    public Integer getManagerId() {
        return theMgrId;
    }

    @Override
    public TethysEventRegistrar<E> getEventRegistrar() {
        return theRegistrar;
    }

    /**
     * Cascade action event.
     * @param pEvent the event to cascade
     */
    public void cascadeEvent(final TethysEvent<E> pEvent) {
        if (!fireEvent(pEvent.getEventId(), pEvent.getDetails())) {
            pEvent.consume();
        }
    }

    /**
     * Fire Event to all registered listeners.
     * @param pEventId the eventId of the event
     * @return was the event consumed?
     */
    public boolean fireEvent(final E pEventId) {
        return fireEvent(pEventId, null);
    }

    /**
     * Fire Event to all registered listeners.
     * @param pEventId the eventId of the event
     * @param pDetails the details of the event
     * @return was the event left unconsumed? true/false
     */
    public boolean fireEvent(final E pEventId,
                             final Object pDetails) {
        /* Lazily create the event */
        TethysEvent<E> myEvent = null;

        /* Loop backwards through the list */
        final ListIterator<TethysEventRegistration<E>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            final TethysEventRegistration<E> myReg = myIterator.previous();

            /* Check whether the action is filtered */
            if (myReg.isFiltered(pEventId)) {
                continue;
            }

            /* If we have not yet created the action event */
            if (myEvent == null) {
                /* Create the action event */
                myEvent = new TethysEvent<>(theMgrId, pEventId, pDetails);
            }

            /* Process the event */
            myReg.processEvent(myEvent);
            if (myEvent.isConsumed()) {
                return false;
            }
        }

        /* Event was not consumed */
        return true;
    }
}
