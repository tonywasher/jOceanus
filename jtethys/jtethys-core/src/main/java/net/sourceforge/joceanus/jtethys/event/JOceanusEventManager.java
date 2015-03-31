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

import java.util.ListIterator;

import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusItemEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusItemRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.RegistrationType;

/**
 * EventManager implementation. This provides means for classes to fire events to registered listeners.
 */
public class JOceanusEventManager
        implements JOceanusEventProvider {
    /**
     * Default action Id.
     */
    private static final int DEFAULT_ACTION_ID = 100;

    /**
     * The Next ownerId.
     */
    private static int theNextOwnerId = 1;

    /**
     * The Id of this manager.
     */
    private final int theOwnerId;

    /**
     * The registrar.
     */
    private final JOceanusEventRegistrar theRegistrar;

    /**
     * Constructor.
     */
    public JOceanusEventManager() {
        /* Store the owner */
        theOwnerId = getNextOwnerId();

        /* Allocate the registrar */
        theRegistrar = new JOceanusEventRegistrar(theOwnerId);
    }

    /**
     * Obtain next owner id.
     * @return the id of the new owner
     */
    private synchronized int getNextOwnerId() {
        /* return the new owner id */
        return theNextOwnerId++;
    }

    /**
     * Obtain ownerId.
     * @return the owner Id
     */
    public int getOwnerId() {
        return theOwnerId;
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theRegistrar;
    }

    /**
     * Fire State Changed Event to all registered listeners.
     */
    public void fireStateChanged() {
        /* Lazily create the event */
        JOceanusChangeEvent myEvent = null;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<JOceanusEventRegistration<?>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            JOceanusEventRegistration<?> myReg = myIterator.previous();

            /* If this is a change registration */
            if (myReg.isRegistrationType(RegistrationType.CHANGE)) {
                /* If we have not yet created the change event */
                if (myEvent == null) {
                    /* Create the change event */
                    myEvent = new JOceanusChangeEvent(theOwnerId);
                }

                /* Fire the event */
                JOceanusChangeRegistration myChange = (JOceanusChangeRegistration) myReg;
                myChange.processEvent(myEvent);
            }
        }
    }

    /**
     * Cascade action event.
     * @param pEvent the event to cascade
     */
    public void cascadeActionEvent(final JOceanusActionEvent pEvent) {
        /* Fire the event */
        fireActionEvent(pEvent.getActionId(), pEvent.getDetails());
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     */
    public void fireActionEvent() {
        fireActionEvent(DEFAULT_ACTION_ID, null);
    }

    /**
     * Fire Action Event to all registered listeners.
     * @param pDetails the details of the event
     */
    public void fireActionEvent(final Object pDetails) {
        fireActionEvent(DEFAULT_ACTION_ID, pDetails);
    }

    /**
     * Fire Action Event to all registered listeners.
     * @param pActionId the actionId of the event
     */
    public void fireActionEvent(final int pActionId) {
        fireActionEvent(pActionId, null);
    }

    /**
     * Fire Action Event to all registered listeners.
     * @param pActionId the actionId of the event
     * @param pDetails the details of the event
     */
    public void fireActionEvent(final int pActionId,
                                final Object pDetails) {
        /* Lazily create the event */
        JOceanusActionEvent myEvent = null;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<JOceanusEventRegistration<?>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            JOceanusEventRegistration<?> myReg = myIterator.previous();

            /* If this is an action registration */
            if (myReg.isRegistrationType(RegistrationType.ACTION)) {
                /* If we have not yet created the item event */
                if (myEvent == null) {
                    /* Create the action event */
                    myEvent = new JOceanusActionEvent(theOwnerId, pActionId, pDetails);
                }

                /* Fire the event */
                JOceanusActionRegistration myAction = (JOceanusActionRegistration) myReg;
                myAction.processEvent(myEvent);
            }
        }
    }

    /**
     * Create an action event.
     * @param pActionId the actionId of the event
     * @param pDetails the details of the event
     * @return the action event
     */
    public JOceanusActionEvent createActionEvent(final int pActionId,
                                                 final Object pDetails) {
        return new JOceanusActionEvent(theOwnerId, pActionId, pDetails);
    }

    /**
     * Fire Item Changed Event to all registered listeners.
     * @param pItem the item that has changed selection
     * @param pSelected is the item now selected?
     */
    public void fireItemStateChanged(final Object pItem,
                                     final boolean pSelected) {
        fireItemStateChanged(DEFAULT_ACTION_ID, pItem, pSelected);
    }

    /**
     * Fire Item Changed Event to all registered listeners.
     * @param pActionId the actionId of the event
     * @param pItem the item that has changed selection
     * @param pSelected is the item now selected?
     */
    public void fireItemStateChanged(final int pActionId,
                                     final Object pItem,
                                     final boolean pSelected) {
        /* Lazily create the event */
        JOceanusItemEvent myEvent = null;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<JOceanusEventRegistration<?>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            JOceanusEventRegistration<?> myReg = myIterator.previous();

            /* If this is an item registration */
            if (myReg.isRegistrationType(RegistrationType.ITEM)) {
                /* If we have not yet created the item event */
                if (myEvent == null) {
                    /* Create the item event */
                    myEvent = new JOceanusItemEvent(theOwnerId, pActionId, pItem, pSelected);
                }

                /* Fire the event */
                JOceanusItemRegistration myChange = (JOceanusItemRegistration) myReg;
                myChange.processEvent(myEvent);
            }
        }
    }
}
