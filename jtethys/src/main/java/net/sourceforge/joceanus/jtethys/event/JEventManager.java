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

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jtethys.event.JEventRegistration.ActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JEventRegistration.ChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.JEventRegistration.ItemRegistration;
import net.sourceforge.joceanus.jtethys.event.JEventRegistration.RegistrationType;

/**
 * EventManager implementation. This maintains a list of ChangeListeners/ActionListeners and allows the caller to fire ChangeEvents and ActionEvents to these
 * lists This is implemented to provide functionality to non-Swing components and also to enable improved control over the contents of the ChangeEvents and
 * ActionEvents that are fired
 */
public class JEventManager {
    /**
     * The Owner of the events.
     */
    private final Object theOwner;

    /**
     * The list of registrations.
     */
    private volatile List<JEventRegistration<?>> theRegistrations = null;

    /**
     * Constructor.
     * @param pOwner the owner object
     */
    public JEventManager(final Object pOwner) {
        /* Store the owner */
        theOwner = pOwner;

        /* Allocate the list */
        theRegistrations = new ArrayList<JEventRegistration<?>>();
    }

    /**
     * Add change Listener to list.
     * @param pListener the listener to add
     */
    public void addChangeListener(final ChangeListener pListener) {
        /* Create the registration */
        JEventRegistration<ChangeEvent> myReg = new ChangeRegistration(pListener);

        /* Add it to the list */
        adjustListenerList(myReg, true);
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     */
    public void addActionListener(final ActionListener pListener) {
        /* Create the registration */
        JEventRegistration<ActionEvent> myReg = new ActionRegistration(pListener);

        /* Add it to the list */
        adjustListenerList(myReg, true);
    }

    /**
     * Add item Listener to list.
     * @param pListener the listener to add
     */
    public void addItemListener(final ItemListener pListener) {
        /* Create the registration */
        JEventRegistration<ItemEvent> myReg = new ItemRegistration(pListener);

        /* Add it to the list */
        adjustListenerList(myReg, true);
    }

    /**
     * Remove Change Listener.
     * @param pListener the listener to remove
     */
    public void removeChangeListener(final ChangeListener pListener) {
        /* Create the registration */
        JEventRegistration<ChangeEvent> myReg = new ChangeRegistration(pListener);

        /* Remove it from the list */
        adjustListenerList(myReg, false);
    }

    /**
     * Remove Action Listener.
     * @param pListener the listener to remove
     */
    public void removeActionListener(final ActionListener pListener) {
        /* Create the registration */
        JEventRegistration<ActionEvent> myReg = new ActionRegistration(pListener);

        /* Remove it from the list */
        adjustListenerList(myReg, false);
    }

    /**
     * Remove Item Listener.
     * @param pListener the listener to remove
     */
    public void removeItemListener(final ItemListener pListener) {
        /* Create the registration */
        JEventRegistration<ItemEvent> myReg = new ItemRegistration(pListener);

        /* Remove it from the list */
        adjustListenerList(myReg, false);
    }

    /**
     * Adjust listener list.
     * @param pRegistration the relevant registration
     * @param isMember should this registration be in the list
     */
    private synchronized void adjustListenerList(final JEventRegistration<?> pRegistration,
                                                 final boolean isMember) {
        /* If the listener is already in the correct state, return */
        if (theRegistrations.contains(pRegistration) == isMember) {
            return;
        }

        /* Create a new list to avoid affecting any currently firing iterations */
        List<JEventRegistration<?>> myNew = new ArrayList<JEventRegistration<?>>(theRegistrations);

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
        ChangeEvent myEvent = null;

        /* Obtain a reference to the registrations */
        List<JEventRegistration<?>> myList = theRegistrations;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<JEventRegistration<?>> myIterator = myList.listIterator(myList.size());
        while (myIterator.hasPrevious()) {
            /* Access the registration */
            JEventRegistration<?> myReg = myIterator.previous();

            /* If this is a change registration */
            if (myReg.isRegistrationType(RegistrationType.CHANGE)) {
                /* If we have not yet created the change event */
                if (myEvent == null) {
                    /* Create the change event */
                    myEvent = new ChangeEvent(pOwner);
                }

                /* Fire the event */
                ChangeRegistration myChange = (ChangeRegistration) myReg;
                myChange.processEvent(myEvent);
            }
        }
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pCommand the action command
     */
    public void fireActionPerformed(final String pCommand) {
        /* Fire standard action performed event */
        fireActionEvent(theOwner, ActionEvent.ACTION_PERFORMED, pCommand);
    }

    /**
     * Cascade action event.
     * @param pOwner the owner of the event
     * @param pEvent the event to cascade
     */
    public void cascadeActionEvent(final Object pOwner,
                                   final ActionDetailEvent pEvent) {
        /* Create the action event */
        ActionEvent myEvent = new ActionDetailEvent(pOwner, pEvent);

        /* Fire the event */
        fireActionEvent(myEvent);
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pOwner the owner of the event
     * @param uSubId the id of the action
     * @param pDetails the action details
     */
    public void fireActionEvent(final Object pOwner,
                                final int uSubId,
                                final Object pDetails) {
        /* Create the action event */
        ActionEvent myEvent = new ActionDetailEvent(pOwner, ActionEvent.ACTION_PERFORMED, uSubId, pDetails);

        /* Fire the event */
        fireActionEvent(myEvent);
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pEvent the event
     */
    private void fireActionEvent(final ActionEvent pEvent) {
        /* Obtain a reference to the registrations */
        List<JEventRegistration<?>> myList = theRegistrations;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<JEventRegistration<?>> myIterator = myList.listIterator(myList.size());
        while (myIterator.hasPrevious()) {
            /* Access the registration */
            JEventRegistration<?> myReg = myIterator.previous();

            /* If this is an action registration */
            if (myReg.isRegistrationType(RegistrationType.ACTION)) {
                /* Fire the event */
                ActionRegistration myAction = (ActionRegistration) myReg;
                myAction.processEvent(pEvent);
            }
        }
    }

    /**
     * Fire Item Changed Event to all registered listeners.
     * @param pOwner the owner of the event
     * @param pItem the item that has changed selection
     * @param pSelected is the item now selected?
     */
    public void fireItemStateChanged(final ItemSelectable pOwner,
                                     final Object pItem,
                                     final boolean pSelected) {
        /* Create the item event */
        ItemEvent myEvent = new ItemEvent(pOwner, ItemEvent.ITEM_STATE_CHANGED, pItem, pSelected
                                                                                                ? ItemEvent.SELECTED
                                                                                                : ItemEvent.DESELECTED);

        /* Obtain a reference to the registrations */
        List<JEventRegistration<?>> myList = theRegistrations;

        /* Loop backwards through the list to notify most recently registered first */
        ListIterator<JEventRegistration<?>> myIterator = myList.listIterator(myList.size());
        while (myIterator.hasPrevious()) {
            /* Access the registration */
            JEventRegistration<?> myReg = myIterator.previous();

            /* If this is an item registration */
            if (myReg.isRegistrationType(RegistrationType.ITEM)) {
                /* Fire the event */
                ItemRegistration myItem = (ItemRegistration) myReg;
                myItem.processEvent(myEvent);
            }
        }
    }
}
