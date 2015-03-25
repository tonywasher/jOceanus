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

import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusItemEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusItemEventListener;

/**
 * Registration structure for event listeners.
 * @param <T> The event for the registration
 */
public abstract class JOceanusEventRegistration<T extends JOceanusEvent> {
    /**
     * registrationType.
     */
    private final RegistrationType theType;

    /**
     * registration id.
     */
    private int theId;

    /**
     * Constructor.
     * @param pType the registration type
     */
    private JOceanusEventRegistration(final RegistrationType pType) {
        theType = pType;
    }

    /**
     * Obtain the registration id.
     * @return true/false
     */
    protected int getId() {
        return theId;
    }

    /**
     * Set the registration id.
     * @param pId the id
     */
    protected void setId(final int pId) {
        theId = pId;
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

    @Override
    public boolean equals(final Object o) {
        /* Handle trivial cases */
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        /* Cast as Registration */
        JOceanusEventRegistration<?> myReg = (JOceanusEventRegistration<?>) o;

        /* Compare fields */
        return theId == myReg.getId();
    }

    @Override
    public int hashCode() {
        return theId;
    }

    /**
     * ActionRegistration class.
     */
    protected static class ActionRegistration
            extends JOceanusEventRegistration<JOceanusActionEvent> {
        /**
         * Action listener.
         */
        private final JOceanusActionEventListener theListener;

        /**
         * Constructor.
         * @param pListener the listener
         */
        protected ActionRegistration(final JOceanusActionEventListener pListener) {
            super(RegistrationType.ACTION);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final JOceanusActionEvent pEvent) {
            theListener.processActionEvent(pEvent);
        }
    }

    /**
     * ChangeRegistration class.
     */
    protected static class ChangeRegistration
            extends JOceanusEventRegistration<JOceanusEvent> {
        /**
         * Change listener.
         */
        private final JOceanusEventListener theListener;

        /**
         * Constructor.
         * @param pListener the listener
         */
        protected ChangeRegistration(final JOceanusEventListener pListener) {
            super(RegistrationType.CHANGE);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final JOceanusEvent pEvent) {
            theListener.processEvent(pEvent);
        }
    }

    /**
     * ItemRegistration class.
     */
    protected static class ItemRegistration
            extends JOceanusEventRegistration<JOceanusItemEvent> {
        /**
         * Item listener.
         */
        private final JOceanusItemEventListener theListener;

        /**
         * Constructor.
         * @param pListener the listener
         */
        protected ItemRegistration(final JOceanusItemEventListener pListener) {
            super(RegistrationType.ITEM);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final JOceanusItemEvent pEvent) {
            theListener.processItemEvent(pEvent);
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
