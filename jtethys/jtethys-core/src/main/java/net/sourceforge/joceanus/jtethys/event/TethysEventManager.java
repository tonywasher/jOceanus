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

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysItemEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.RegistrationType;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysActionRegistration;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysItemRegistration;

/**
 * EventManager implementation. This provides means for classes to fire events to registered
 * listeners.
 */
public class TethysEventManager
        implements TethysEventProvider {
    /**
     * Default action Id.
     */
    public static final int ACTIONID_ANY = -1;

    /**
     * The Next managerId.
     */
    private static Integer theNextMgrId = 1;

    /**
     * The Id of this manager.
     */
    private final Integer theMgrId;

    /**
     * The registrar.
     */
    private final TethysEventRegistrar theRegistrar;

    /**
     * Constructor.
     */
    public TethysEventManager() {
        /* Store the owner */
        theMgrId = getNextManagerId();

        /* Allocate the registrar */
        theRegistrar = new TethysEventRegistrar(theMgrId);
    }

    /**
     * Obtain next owner id.
     * @return the id of the new owner
     */
    private static synchronized Integer getNextManagerId() {
        /* return the new manager id */
        Integer myId = theNextMgrId;
        theNextMgrId = theNextMgrId + 1;
        return myId;
    }

    /**
     * Obtain ownerId.
     * @return the owner Id
     */
    public Integer getManagerId() {
        return theMgrId;
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theRegistrar;
    }

    /**
     * Fire State Changed Event to all registered listeners.
     */
    public void fireStateChanged() {
        /* Lazily create the event */
        TethysChangeEvent myEvent = null;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<TethysEventRegistration<?>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            TethysEventRegistration<?> myReg = myIterator.previous();

            /* If this is a change registration */
            if (myReg.isRegistrationType(RegistrationType.CHANGE)) {
                /* If we have not yet created the change event */
                if (myEvent == null) {
                    /* Create the change event */
                    myEvent = new TethysChangeEvent(theMgrId);
                }

                /* Fire the event */
                TethysChangeRegistration myChange = (TethysChangeRegistration) myReg;
                myChange.processEvent(myEvent);
            }
        }
    }

    /**
     * Cascade action event.
     * @param pEvent the event to cascade
     */
    public void cascadeActionEvent(final TethysActionEvent pEvent) {
        /* Fire the event */
        fireActionEvent(pEvent.getActionId(), pEvent.getDetails());
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     */
    public void fireActionEvent() {
        fireActionEvent(ACTIONID_ANY, null);
    }

    /**
     * Fire Action Event to all registered listeners.
     * @param pDetails the details of the event
     */
    public void fireActionEvent(final Object pDetails) {
        fireActionEvent(ACTIONID_ANY, pDetails);
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
        TethysActionEvent myEvent = null;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<TethysEventRegistration<?>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            TethysEventRegistration<?> myReg = myIterator.previous();

            /* If this is an action registration */
            if (myReg.isRegistrationType(RegistrationType.ACTION)) {
                /* Check whether the action is filtered */
                TethysActionRegistration myAction = (TethysActionRegistration) myReg;
                if (myAction.isFiltered(pActionId)) {
                    continue;
                }

                /* If we have not yet created the action event */
                if (myEvent == null) {
                    /* Create the action event */
                    myEvent = new TethysActionEvent(theMgrId, pActionId, pDetails);
                }

                /* Fire the event */
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
    public TethysActionEvent createActionEvent(final int pActionId,
                                               final Object pDetails) {
        return new TethysActionEvent(theMgrId, pActionId, pDetails);
    }

    /**
     * Fire Item Changed Event to all registered listeners.
     * @param pItem the item that has changed selection
     * @param pSelected is the item now selected?
     */
    public void fireItemStateChanged(final Object pItem,
                                     final boolean pSelected) {
        fireItemStateChanged(ACTIONID_ANY, pItem, pSelected);
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
        TethysItemEvent myEvent = null;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<TethysEventRegistration<?>> myIterator = theRegistrar.reverseIterator();
        while (myIterator.hasPrevious()) {
            TethysEventRegistration<?> myReg = myIterator.previous();

            /* If this is an item registration */
            if (myReg.isRegistrationType(RegistrationType.ITEM)) {
                /* If we have not yet created the item event */
                if (myEvent == null) {
                    /* Create the item event */
                    myEvent = new TethysItemEvent(theMgrId, pActionId, pItem, pSelected);
                }

                /* Fire the event */
                TethysItemRegistration myChange = (TethysItemRegistration) myReg;
                myChange.processEvent(myEvent);
            }
        }
    }
}
