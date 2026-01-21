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
package io.github.tonywasher.joceanus.oceanus.event;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent.OceanusEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * EventRegister implementation. This maintains a list of
 * Action/Change/ItemListeners/ActionListeners and allows the caller to fire
 * Action/Change/ItemEvents and ActionEvents to these listeners. This is implemented to provide
 * functionality to non-GUI components and also to enable improved control over the contents of the
 * ChangeEvents and ActionEvents that are fired.
 * <p>
 * This class is used by listeners to register to listen for events.
 *
 * @param <E> The event id type
 */
public class OceanusEventRegistrar<E extends Enum<E>> {
    /**
     * Interface for event providers.
     *
     * @param <E> The event id type
     */
    @FunctionalInterface
    public interface OceanusEventProvider<E extends Enum<E>> {
        /**
         * Obtain registration object for listeners.
         *
         * @return the registrar
         */
        OceanusEventRegistrar<E> getEventRegistrar();
    }

    /**
     * The Source of the events.
     */
    private final Integer theMgrId;

    /**
     * The list of registrations.
     */
    private final AtomicReference<List<OceanusEventRegistration<E>>> theRegistrations;

    /**
     * The Next registrationId.
     */
    private final AtomicInteger theNextRegId = new AtomicInteger();

    /**
     * Constructor.
     *
     * @param pMgrId the manager id
     */
    protected OceanusEventRegistrar(final Integer pMgrId) {
        /* Store the owning manager */
        theMgrId = pMgrId;

        /* Allocate the list */
        theRegistrations = new AtomicReference<>();
        theRegistrations.set(new ArrayList<>());
    }

    /**
     * Obtain registration iterator.
     *
     * @return the iterator
     */
    protected Iterator<OceanusEventRegistration<E>> iterator() {
        return theRegistrations.get().iterator();
    }

    /**
     * Obtain reverse registration iterator.
     *
     * @return the iterator
     */
    ListIterator<OceanusEventRegistration<E>> reverseIterator() {
        /* Obtain a reference to the registrations */
        final List<OceanusEventRegistration<E>> myList = theRegistrations.get();

        /* Create an iterator positioned at the end of the list */
        return myList.listIterator(myList.size());
    }

    /**
     * Add event Listener to list.
     *
     * @param pListener the listener to add
     * @return the registration
     */
    public OceanusEventRegistration<E> addEventListener(final OceanusEventListener<E> pListener) {
        /* Create the registration */
        final OceanusEventRegistration<E> myReg = new OceanusEventRegistration<>(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add filtered event Listener to list.
     *
     * @param pEventId  the explicit event id to listen for
     * @param pListener the listener to add
     * @return the registration
     */
    public OceanusEventRegistration<E> addEventListener(final E pEventId,
                                                        final OceanusEventListener<E> pListener) {
        /* Create the registration */
        final OceanusEventRegistration<E> myReg = new OceanusEventRegistration<>(theMgrId, pEventId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Remove Event Listener.
     *
     * @param pRegistration the registration to remove
     */
    public void removeEventListener(final OceanusEventRegistration<E> pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Add To Listener list.
     *
     * @param pRegistration the relevant registration
     */
    private synchronized void addToListenerList(final OceanusEventRegistration<E> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        final List<OceanusEventRegistration<E>> myNew = new ArrayList<>(theRegistrations.get());

        /* Set the new registration Id */
        pRegistration.setRegId(theNextRegId.getAndIncrement());

        /* Adjust the list */
        myNew.add(pRegistration);

        /* Record the new list */
        theRegistrations.set(myNew);
    }

    /**
     * Remove from listener list.
     *
     * @param pRegistration the registration to remove
     */
    private synchronized void removeFromListenerList(final OceanusEventRegistration<E> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        final List<OceanusEventRegistration<E>> myNew = new ArrayList<>(theRegistrations.get());

        /* Iterate through the registrations */
        final Iterator<OceanusEventRegistration<E>> myIterator = myNew.iterator();
        while (myIterator.hasNext()) {
            final OceanusEventRegistration<E> myReg = myIterator.next();

            /* If the registration matches */
            if (myReg.equals(pRegistration)) {
                /* Remove the registration */
                myIterator.remove();

                /* Record the new list and return */
                theRegistrations.set(myNew);
                return;
            }
        }
    }
}
