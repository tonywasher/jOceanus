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

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysItemEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysActionRegistration;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysItemRegistration;

/**
 * EventRegister implementation. This maintains a list of
 * Action/Change/ItemListeners/ActionListeners and allows the caller to fire
 * Action/Change/ItemEvents and ActionEvents to these listeners. This is implemented to provide
 * functionality to non-GUI components and also to enable improved control over the contents of the
 * ChangeEvents and ActionEvents that are fired.
 * <p>
 * This class is used by listeners to register to listen for events.
 */
public class TethysEventRegistrar {
    /**
     * Interface for event providers.
     */
    @FunctionalInterface
    public interface TethysEventProvider {
        /**
         * Obtain registration object for listeners.
         * @return the registrar
         */
        TethysEventRegistrar getEventRegistrar();
    }

    /**
     * The Source of the events.
     */
    private final Integer theMgrId;

    /**
     * The list of registrations.
     */
    private volatile List<TethysEventRegistration<?>> theRegistrations = null;

    /**
     * The Next registrationId.
     */
    private Integer theNextRegId = 1;

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
     * Obtain next owner id.
     * @return the id of the new owner
     */
    private synchronized int getNextRegistrationId() {
        /* return the new registration id */
        Integer myId = theNextRegId;
        theNextRegId = theNextRegId + 1;
        return myId;
    }

    /**
     * Obtain registration iterator.
     * @return the iterator
     */
    protected Iterator<TethysEventRegistration<?>> iterator() {
        return theRegistrations.iterator();
    }

    /**
     * Obtain reverse registration iterator.
     * @return the iterator
     */
    protected ListIterator<TethysEventRegistration<?>> reverseIterator() {
        /* Obtain a reference to the registrations */
        List<TethysEventRegistration<?>> myList = theRegistrations;

        /* Create an iterator positioned at the end of the list */
        return myList.listIterator(myList.size());
    }

    /**
     * Add change Listener to list.
     * @param pListener the listener to add
     * @return the registration
     */
    public TethysChangeRegistration addChangeListener(final TethysChangeEventListener pListener) {
        /* Create the registration */
        TethysChangeRegistration myReg = new TethysChangeRegistration(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     * @return the registration
     */
    public TethysActionRegistration addActionListener(final TethysActionEventListener pListener) {
        /* Create the registration */
        TethysActionRegistration myReg = new TethysActionRegistration(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add filtered action Listener to list.
     * @param pActionId the explicit action id to listen for
     * @param pListener the listener to add
     * @return the registration
     */
    public TethysActionRegistration addFilteredActionListener(final int pActionId,
                                                              final TethysActionEventListener pListener) {
        /* Create the registration */
        TethysActionRegistration myReg = new TethysActionRegistration(theMgrId, pActionId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add item Listener to list.
     * @param pListener the listener to add
     * @return the registration
     */
    public TethysItemRegistration addItemListener(final TethysItemEventListener pListener) {
        /* Create the registration */
        TethysItemRegistration myReg = new TethysItemRegistration(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Remove Change Listener.
     * @param pRegistration the registration to remove
     */
    public void removeChangeListener(final TethysChangeRegistration pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Remove Action Listener.
     * @param pRegistration the registration to remove
     */
    public void removeActionListener(final TethysActionRegistration pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Remove Item Listener.
     * @param pRegistration the registration to remove
     */
    public void removeItemListener(final TethysItemRegistration pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Add To Listener list.
     * @param pRegistration the relevant registration
     * @return the id of the new registration
     */
    private synchronized Integer addToListenerList(final TethysEventRegistration<?> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<TethysEventRegistration<?>> myNew = new ArrayList<>(theRegistrations);

        /* Set the new registration Id */
        pRegistration.setRegId(getNextRegistrationId());

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
    private synchronized void removeFromListenerList(final TethysEventRegistration<?> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<TethysEventRegistration<?>> myNew = new ArrayList<>(theRegistrations);

        /* Iterate through the registrations */
        Iterator<TethysEventRegistration<?>> myIterator = myNew.iterator();
        while (myIterator.hasNext()) {
            TethysEventRegistration<?> myReg = myIterator.next();

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
