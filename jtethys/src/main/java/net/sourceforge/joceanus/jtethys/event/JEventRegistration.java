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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Registration structure for event listeners.
 * @param <T> The event for the registration
 */
public abstract class JEventRegistration<T> {
    /**
     * registrationType.
     */
    private final RegistrationType theType;

    /**
     * Constructor.
     * @param pType the registration type
     */
    private JEventRegistration(final RegistrationType pType) {
        theType = pType;
    }

    /**
     * Is this the required registration type?
     * @param pType the registrationType
     * @return true/false
     */
    protected boolean isRegistrationType(final RegistrationType pType) {
        return theType.equals(pType);
    }

    /**
     * Process the relevant event.
     * @param pEvent the event.
     */
    protected abstract void processEvent(final T pEvent);

    /**
     * ActionRegistration class.
     */
    protected static class ActionRegistration
            extends JEventRegistration<ActionEvent> {
        /**
         * Action listener.
         */
        private final ActionListener theListener;

        /**
         * Constructor.
         * @param pListener the listener
         */
        protected ActionRegistration(final ActionListener pListener) {
            super(RegistrationType.ACTION);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final ActionEvent pEvent) {
            theListener.actionPerformed(pEvent);
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
            ActionRegistration myReg = (ActionRegistration) o;

            /* Compare fields */
            return theListener == myReg.theListener;
        }

        @Override
        public int hashCode() {
            return theListener.hashCode();
        }
    }

    /**
     * ChangeRegistration class.
     */
    protected static class ChangeRegistration
            extends JEventRegistration<ChangeEvent> {
        /**
         * Change listener.
         */
        private final ChangeListener theListener;

        /**
         * Constructor.
         * @param pListener the listener
         */
        protected ChangeRegistration(final ChangeListener pListener) {
            super(RegistrationType.CHANGE);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final ChangeEvent pEvent) {
            theListener.stateChanged(pEvent);
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
            ChangeRegistration myReg = (ChangeRegistration) o;

            /* Compare fields */
            return theListener == myReg.theListener;
        }

        @Override
        public int hashCode() {
            return theListener.hashCode();
        }
    }

    /**
     * ItemRegistration class.
     */
    protected static class ItemRegistration
            extends JEventRegistration<ItemEvent> {
        /**
         * Item listener.
         */
        private final ItemListener theListener;

        /**
         * Constructor.
         * @param pListener the listener
         */
        protected ItemRegistration(final ItemListener pListener) {
            super(RegistrationType.ITEM);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final ItemEvent pEvent) {
            theListener.itemStateChanged(pEvent);
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
            ItemRegistration myReg = (ItemRegistration) o;

            /* Compare fields */
            return theListener == myReg.theListener;
        }

        @Override
        public int hashCode() {
            return theListener.hashCode();
        }
    }

    /**
     * Registration Type.
     */
    protected enum RegistrationType {
        /**
         * Action.
         */
        ACTION,

        /**
         * Change.
         */
        CHANGE,

        /**
         * Item.
         */
        ITEM;
    }
}
