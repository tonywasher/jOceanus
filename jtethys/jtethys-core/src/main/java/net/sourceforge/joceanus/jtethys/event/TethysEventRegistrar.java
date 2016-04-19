/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysEventListener;

/**
 * EventRegister implementation. This maintains a list of
 * Action/Change/ItemListeners/ActionListeners and allows the caller to fire
 * Action/Change/ItemEvents and ActionEvents to these listeners. This is implemented to provide
 * functionality to non-GUI components and also to enable improved control over the contents of the
 * ChangeEvents and ActionEvents that are fired.
 * <p>
 * This class is used by listeners to register to listen for events.
 * @param <E> The event id type
 */
public class TethysEventRegistrar<E extends Enum<E>> {
    /**
     * Interface for event providers.
     * @param <E> The event id type
     */
    @FunctionalInterface
    public interface TethysEventProvider<E extends Enum<E>> {
        /**
         * Obtain registration object for listeners.
         * @return the registrar
         */
        TethysEventRegistrar<E> getEventRegistrar();
    }

    /**
     * The Source of the events.
     */
    private final Integer theMgrId;

    /**
     * The list of registrations.
     */
    private volatile List<TethysEventRegistration<E>> theRegistrations = null;

    /**
     * The Next registrationId.
     */
    private AtomicInteger theNextRegId = new AtomicInteger();

    /**
     * Constructor.
     * @param pMgrId the manager id
     */
    protected TethysEventRegistrar(final Integer pMgrId) {
        /* Store the owning manager */
        theMgrId = pMgrId;

        /* Allocate the list */
        theRegistrations = new ArrayList<>();
    }

    /**
     * Obtain registration iterator.
     * @return the iterator
     */
    protected Iterator<TethysEventRegistration<E>> iterator() {
        return theRegistrations.iterator();
    }

    /**
     * Obtain reverse registration iterator.
     * @return the iterator
     */
    protected ListIterator<TethysEventRegistration<E>> reverseIterator() {
        /* Obtain a reference to the registrations */
        List<TethysEventRegistration<E>> myList = theRegistrations;

        /* Create an iterator positioned at the end of the list */
        return myList.listIterator(myList.size());
    }

    /**
     * Add event Listener to list.
     * @param pListener the listener to add
     * @return the registration
     */
    public TethysEventRegistration<E> addEventListener(final TethysEventListener<E> pListener) {
        /* Create the registration */
        TethysEventRegistration<E> myReg = new TethysEventRegistration<>(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add filtered event Listener to list.
     * @param pEventId the explicit event id to listen for
     * @param pListener the listener to add
     * @return the registration
     */
    public TethysEventRegistration<E> addEventListener(final E pEventId,
                                                       final TethysEventListener<E> pListener) {
        /* Create the registration */
        TethysEventRegistration<E> myReg = new TethysEventRegistration<>(theMgrId, pEventId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Remove Event Listener.
     * @param pRegistration the registration to remove
     */
    public void removeEventListener(final TethysEventRegistration<E> pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Add To Listener list.
     * @param pRegistration the relevant registration
     * @return the id of the new registration
     */
    private synchronized Integer addToListenerList(final TethysEventRegistration<E> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<TethysEventRegistration<E>> myNew = new ArrayList<>(theRegistrations);

        /* Set the new registration Id */
        pRegistration.setRegId(theNextRegId.getAndIncrement());

        /* Adjust the list */
        myNew.add(pRegistration);

        /* Record the new list */
        theRegistrations = myNew;

        /* return the new registration id */
        return pRegistration.getRegId();
    }

    /**
     * Remove from listener list.
     * @param pRegistration the registration to remove
     */
    private synchronized void removeFromListenerList(final TethysEventRegistration<E> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<TethysEventRegistration<E>> myNew = new ArrayList<>(theRegistrations);

        /* Iterate through the registrations */
        Iterator<TethysEventRegistration<E>> myIterator = myNew.iterator();
        while (myIterator.hasNext()) {
            TethysEventRegistration<E> myReg = myIterator.next();

            /* If the registration matches */
            if (myReg.equals(pRegistration)) {
                /* Remove the registration */
                myIterator.remove();

                /* Record the new list and return */
                theRegistrations = myNew;
                return;
            }
        }
    }
}