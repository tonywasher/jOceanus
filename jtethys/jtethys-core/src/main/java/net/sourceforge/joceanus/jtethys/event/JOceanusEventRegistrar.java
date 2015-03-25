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

import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusItemEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.ActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.ChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.ItemRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.RegistrationType;

/**
 * EventRegister implementation. This maintains a list of Action/Change/ItemListeners/ActionListeners and allows the caller to fire Action/Change/ItemEvents and
 * ActionEvents to these listeners. This is implemented to provide functionality to non-GUI components and also to enable improved control over the contents of
 * the ChangeEvents and ActionEvents that are fired.
 * <p>
 * This class is used by listeners to register to listen for events.
 */
public class JOceanusEventRegistrar {
    /**
     * Interface for event providers.
     */
    public interface JOceanusEventProvider {
        /**
         * Obtain registration object for listeners.
         * @return the registrar
         */
        JOceanusEventRegistrar getEventRegistrar();
    }

    /**
     * The Owner of the events.
     */
    private final int theOwnerId;

    /**
     * The list of registrations.
     */
    private volatile List<JOceanusEventRegistration<?>> theRegistrations = null;

    /**
     * The Next registrationId.
     */
    private int theNextRegId = 1;

    /**
     * Constructor.
     * @param pOwnerId the owner id
     */
    protected JOceanusEventRegistrar(final int pOwnerId) {
        /* Store the owner */
        theOwnerId = pOwnerId;

        /* Allocate the list */
        theRegistrations = new ArrayList<JOceanusEventRegistration<?>>();
    }

    /**
     * Obtain ownerId.
     * @return the owner Id
     */
    public int getOwnerId() {
        return theOwnerId;
    }

    /**
     * Obtain registration iterator.
     * @return the iterator
     */
    protected Iterator<JOceanusEventRegistration<?>> iterator() {
        return theRegistrations.iterator();
    }

    /**
     * Obtain reverse registration iterator.
     * @return the iterator
     */
    protected ListIterator<JOceanusEventRegistration<?>> reverseIterator() {
        /* Obtain a reference to the registrations */
        List<JOceanusEventRegistration<?>> myList = theRegistrations;

        /* Create an iterator positioned at the end of the list */
        return myList.listIterator(myList.size());
    }

    /**
     * Add change Listener to list.
     * @param pListener the listener to add
     * @return the listener id
     */
    public int addChangeListener(final JOceanusEventListener pListener) {
        /* Create the registration */
        ChangeRegistration myReg = new ChangeRegistration(pListener);

        /* Add it to the list */
        return addToListenerList(myReg);
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     * @return the listener id
     */
    public int addActionListener(final JOceanusActionEventListener pListener) {
        /* Create the registration */
        ActionRegistration myReg = new ActionRegistration(pListener);

        /* Add it to the list */
        return addToListenerList(myReg);
    }

    /**
     * Add item Listener to list.
     * @param pListener the listener to add
     * @return the listener id
     */
    public int addItemListener(final JOceanusItemEventListener pListener) {
        /* Create the registration */
        ItemRegistration myReg = new ItemRegistration(pListener);

        /* Add it to the list */
        return addToListenerList(myReg);
    }

    /**
     * Remove Change Listener.
     * @param pListenerId the listener id to remove
     */
    public void removeChangeListener(final int pListenerId) {
        removeFromListenerList(pListenerId, RegistrationType.CHANGE);
    }

    /**
     * Remove Action Listener.
     * @param pListenerId the listener id to remove
     */
    public void removeActionListener(final int pListenerId) {
        removeFromListenerList(pListenerId, RegistrationType.ACTION);
    }

    /**
     * Remove Item Listener.
     * @param pListenerId the listener id to remove
     */
    public void removeItemListener(final int pListenerId) {
        removeFromListenerList(pListenerId, RegistrationType.CHANGE);
    }

    /**
     * Add To Listener list.
     * @param pRegistration the relevant registration
     * @return the id of the new registration
     */
    private synchronized int addToListenerList(final JOceanusEventRegistration<?> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<JOceanusEventRegistration<?>> myNew = new ArrayList<JOceanusEventRegistration<?>>(theRegistrations);

        /* Set the new registration Id */
        pRegistration.setId(theNextRegId++);

        /* Adjust the list */
        myNew.add(pRegistration);

        /* Record the new list */
        theRegistrations = myNew;

        /* return the new registration id */
        return pRegistration.getId();
    }

    /**
     * Remove from listener list.
     * @param pListenerId the registration id
     * @param pType the type of the registration
     */
    private synchronized void removeFromListenerList(final int pListenerId,
                                                     final RegistrationType pType) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<JOceanusEventRegistration<?>> myNew = new ArrayList<JOceanusEventRegistration<?>>(theRegistrations);

        /* Iterate through the registrations */
        Iterator<JOceanusEventRegistration<?>> myIterator = myNew.iterator();
        while (myIterator.hasNext()) {
            JOceanusEventRegistration<?> myReg = myIterator.next();

            /* If the registration matches */
            if (myReg.isRegistrationType(pType)
                && myReg.getId() == pListenerId) {
                /* Remove the registration */
                myIterator.remove();

                /* Record the new list and return */
                theRegistrations = myNew;
                return;
            }
        }
    }
}
