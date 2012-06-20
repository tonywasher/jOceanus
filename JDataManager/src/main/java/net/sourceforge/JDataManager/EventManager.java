/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * EventManager implementation. This maintains a list of ChangeListeners/ActionListeners and allows the caller
 * to fire ChangeEvents and ActionEvents to these lists This is implemented to provide functionality to
 * non-Swing components and also to enable improved control over the contents of the ChangeEvents and
 * ActionEvents that are fired
 */
public class EventManager {
    /**
     * Hash Prime.
     */
    public static final int HASH_PRIME = 19;

    /**
     * The Owner of the events.
     */
    private final Object theOwner;

    /**
     * The list of registrations.
     */
    private volatile List<Registration> theRegistrations = null;

    /**
     * Registration enumeration.
     */
    private enum RegistrationType {
        /**
         * Change Listener.
         */
        Change,

        /**
         * Action Listener.
         */
        Action;
    }

    /**
     * Registration class.
     */
    private final class Registration {
        /**
         * Registration Type.
         */
        private final RegistrationType theType;

        /**
         * Listener.
         */
        private final EventListener theListener;

        /**
         * Constructor.
         * @param pType the Registration Type
         * @param pListener the Listener
         */
        private Registration(final RegistrationType pType,
                             final EventListener pListener) {
            /* Store value */
            theType = pType;
            theListener = pListener;
        }

        @Override
        public boolean equals(final Object o) {
            /* Handle trivial cases */
            if (o == null) {
                return false;
            }
            if (o.getClass() != this.getClass()) {
                return false;
            }

            /* Cast as Registration */
            Registration myReg = (Registration) o;

            /* Compare fields */
            return ((theType == myReg.theType) && (theListener == myReg.theListener));
        }

        @Override
        public int hashCode() {
            /* Calculate hashCode and return it */
            int myHash = HASH_PRIME * (theType.ordinal() + 1);
            myHash += theListener.hashCode();
            return myHash;
        }
    }

    /**
     * Constructor.
     * @param pOwner the owner object
     */
    public EventManager(final Object pOwner) {
        /* Store the owner */
        theOwner = pOwner;

        /* Allocate the list */
        theRegistrations = new ArrayList<Registration>();
    }

    /**
     * Add change Listener to list.
     * @param pListener the listener to add
     */
    public void addChangeListener(final ChangeListener pListener) {
        /* Create the registration */
        Registration myReg = new Registration(RegistrationType.Change, pListener);

        /* Add it to the list */
        adjustListenerList(myReg, true);
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     */
    public void addActionListener(final ActionListener pListener) {
        /* Create the registration */
        Registration myReg = new Registration(RegistrationType.Action, pListener);

        /* Add it to the list */
        adjustListenerList(myReg, true);
    }

    /**
     * Remove Change Listener.
     * @param pListener the listener to remove
     */
    public void removeChangeListener(final ChangeListener pListener) {
        /* Create the registration */
        Registration myReg = new Registration(RegistrationType.Change, pListener);

        /* Remove it from the list */
        adjustListenerList(myReg, false);
    }

    /**
     * Remove Action Listener.
     * @param pListener the listener to remove
     */
    public void removeActionListener(final ActionListener pListener) {
        /* Create the registration */
        Registration myReg = new Registration(RegistrationType.Action, pListener);

        /* Remove it from the list */
        adjustListenerList(myReg, false);
    }

    /**
     * Adjust listener list.
     * @param pRegistration the relevant registration
     * @param isMember should this registration be in the list
     */
    private synchronized void adjustListenerList(final Registration pRegistration,
                                                 final boolean isMember) {
        /* If the listener is already in the correct state, return */
        if (theRegistrations.contains(pRegistration) == isMember) {
            return;
        }

        /* Create a new list to avoid affecting any current fire iterations */
        List<Registration> myNew = new ArrayList<Registration>(theRegistrations);

        /* Adjust the list */
        if (isMember) {
            myNew.add(pRegistration);
        } else {
            myNew.remove(pRegistration);
        }

        /* Record the new list */
        theRegistrations = myNew;
    }

    /**
     * Fire State Changed Event to all registered listeners.
     */
    public void fireStateChanged() {
        /* Fire the standard event */
        fireStateChanged(theOwner);
    }

    /**
     * Fire State Changed Event to all registered listeners.
     * @param pOwner the owner of the event
     */
    public void fireStateChanged(final Object pOwner) {
        /* Create the change event */
        ChangeEvent myEvent = new ChangeEvent(pOwner);

        /* Obtain a reference to the registrations */
        List<Registration> myList = theRegistrations;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<Registration> myIterator = myList.listIterator(myList.size());
        while (myIterator.hasPrevious()) {
            /* Access the registration */
            Registration myReg = myIterator.previous();

            /* Ignore if not a Change registration */
            if (myReg.theType != RegistrationType.Change) {
                continue;
            }

            /* Fire the event */
            ChangeListener myListener = (ChangeListener) myReg.theListener;
            myListener.stateChanged(myEvent);
        }
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pCommand the action command
     */
    public void fireActionPerformed(final String pCommand) {
        /* Fire standard action performed event */
        fireActionPerformed(theOwner, ActionEvent.ACTION_PERFORMED, pCommand);
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pOwner the owner of the event
     * @param uId the id of the action
     * @param pCommand the action command
     */
    public void fireActionPerformed(final Object pOwner,
                                    final int uId,
                                    final String pCommand) {
        /* Create the action event */
        ActionEvent myEvent = new ActionEvent(pOwner, uId, pCommand);

        /* Obtain a reference to the registrations */
        List<Registration> myList = theRegistrations;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<Registration> myIterator = myList.listIterator(myList.size());
        while (myIterator.hasPrevious()) {
            /* Access the registration */
            Registration myReg = myIterator.previous();

            /* Ignore if not an Action registration */
            if (myReg.theType != RegistrationType.Action) {
                continue;
            }

            /* Fire the event */
            ActionListener myListener = (ActionListener) myReg.theListener;
            myListener.actionPerformed(myEvent);
        }
    }
}