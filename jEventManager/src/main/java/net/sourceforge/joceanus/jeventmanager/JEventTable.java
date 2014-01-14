/*******************************************************************************
 * jEventManager: Java Event Manager
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
package net.sourceforge.joceanus.jeventmanager;

import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.event.ChangeListener;

/**
 * Extension of JTable with Event Manager to provide support for Action and Change Events.
 * @author Tony Washer
 */
public class JEventTable
        extends JTable {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -3037090797611288045L;

    /**
     * The Event Manager.
     */
    private final transient JEventManager theTableManager = new JEventManager(this);

    /**
     * Add change Listener to list.
     * @param pListener the listener to add
     */
    public void addChangeListener(final ChangeListener pListener) {
        /* Add the change listener */
        theTableManager.addChangeListener(pListener);
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     */
    public void addActionListener(final ActionListener pListener) {
        /* Add the action listener */
        theTableManager.addActionListener(pListener);
    }

    /**
     * Remove Change Listener.
     * @param pListener the listener to remove
     */
    public void removeChangeListener(final ChangeListener pListener) {
        /* Remove the change listener */
        theTableManager.removeChangeListener(pListener);
    }

    /**
     * Remove Action Listener.
     * @param pListener the listener to remove
     */
    public void removeActionListener(final ActionListener pListener) {
        /* Remove the action listener */
        theTableManager.removeActionListener(pListener);
    }

    /**
     * Fire State Changed Event to all registered listeners.
     */
    protected void fireStateChanged() {
        /* Fire the standard event */
        theTableManager.fireStateChanged();
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pCommand the action command
     */
    protected void fireActionPerformed(final String pCommand) {
        /* Fire standard action performed event */
        theTableManager.fireActionPerformed(pCommand);
    }

    /**
     * Fire Action Detail Event to all registered listeners.
     * @param pSubId the SubId of the event.
     * @param pDetails the action details
     */
    protected void fireActionEvent(final int pSubId,
                                   final Object pDetails) {
        /* Fire action detail event */
        theTableManager.fireActionEvent(this, pSubId, pDetails);
    }

    /**
     * Cascade action event.
     * @param pEvent the event to cascade
     */
    protected void cascadeActionEvent(final ActionDetailEvent pEvent) {
        /* Fire action detail event */
        theTableManager.cascadeActionEvent(this, pEvent);
    }
}
