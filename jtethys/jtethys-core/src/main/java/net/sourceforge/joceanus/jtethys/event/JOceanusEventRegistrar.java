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
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusItemEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusItemRegistration;

/**
 * EventRegister implementation. This maintains a list of
 * Action/Change/ItemListeners/ActionListeners and allows the caller to fire
 * Action/Change/ItemEvents and ActionEvents to these listeners. This is implemented to provide
 * functionality to non-GUI components and also to enable improved control over the contents of the
 * ChangeEvents and ActionEvents that are fired.
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
     * The Source of the events.
     */
    private final Integer theMgrId;

    /**
     * The list of registrations.
     */
    private volatile List<JOceanusEventRegistration<?>> theRegistrations = null;

    /**
     * The Next registrationId.
     */
    private Integer theNextRegId = 1;

    /**
     * Constructor.
     * @param pMgrId the manager id
     */
    protected JOceanusEventRegistrar(final Integer pMgrId) {
        /* Store the owning manager */
        theMgrId = pMgrId;

        /* Allocate the list */
        theRegistrations = new ArrayList<JOceanusEventRegistration<?>>();
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
     * @return the registration
     */
    public JOceanusChangeRegistration addChangeListener(final JOceanusChangeEventListener pListener) {
        /* Create the registration */
        JOceanusChangeRegistration myReg = new JOceanusChangeRegistration(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     * @return the registration
     */
    public JOceanusActionRegistration addActionListener(final JOceanusActionEventListener pListener) {
        /* Create the registration */
        JOceanusActionRegistration myReg = new JOceanusActionRegistration(theMgrId, pListener);

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
    public JOceanusActionRegistration addFilteredActionListener(final int pActionId,
                                                                final JOceanusActionEventListener pListener) {
        /* Create the registration */
        JOceanusActionRegistration myReg = new JOceanusActionRegistration(theMgrId, pActionId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Add item Listener to list.
     * @param pListener the listener to add
     * @return the registration
     */
    public JOceanusItemRegistration addItemListener(final JOceanusItemEventListener pListener) {
        /* Create the registration */
        JOceanusItemRegistration myReg = new JOceanusItemRegistration(theMgrId, pListener);

        /* Add it to the list */
        addToListenerList(myReg);
        return myReg;
    }

    /**
     * Remove Change Listener.
     * @param pRegistration the registration to remove
     */
    public void removeChangeListener(final JOceanusChangeRegistration pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Remove Action Listener.
     * @param pRegistration the registration to remove
     */
    public void removeActionListener(final JOceanusActionRegistration pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Remove Item Listener.
     * @param pRegistration the registration to remove
     */
    public void removeItemListener(final JOceanusItemRegistration pRegistration) {
        removeFromListenerList(pRegistration);
    }

    /**
     * Add To Listener list.
     * @param pRegistration the relevant registration
     * @return the id of the new registration
     */
    private synchronized Integer addToListenerList(final JOceanusEventRegistration<?> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<JOceanusEventRegistration<?>> myNew = new ArrayList<JOceanusEventRegistration<?>>(theRegistrations);

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
    private synchronized void removeFromListenerList(final JOceanusEventRegistration<?> pRegistration) {
        /* Create a new list to avoid affecting any currently firing iterations */
        List<JOceanusEventRegistration<?>> myNew = new ArrayList<JOceanusEventRegistration<?>>(theRegistrations);

        /* Iterate through the registrations */
        Iterator<JOceanusEventRegistration<?>> myIterator = myNew.iterator();
        while (myIterator.hasNext()) {
            JOceanusEventRegistration<?> myReg = myIterator.next();

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
