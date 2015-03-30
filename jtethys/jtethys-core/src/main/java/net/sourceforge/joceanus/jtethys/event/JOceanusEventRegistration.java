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
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
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
     * owner id.
     */
    private int theOwnerId;

    /**
     * registration id.
     */
    private int theId;

    /**
     * Constructor.
     * @param pOwnerId the owner Id
     * @param pType the registration type
     */
    private JOceanusEventRegistration(final int pOwnerId,
                                      final RegistrationType pType) {
        theOwnerId = pOwnerId;
        theType = pType;
    }

    /**
     * Is the event aimed at this registration.
     * @param pEvent the event
     * @return true/false
     */
    public boolean isRelevant(final JOceanusEvent pEvent) {
        return theOwnerId == pEvent.getSourceId();
    }

    /**
     * Obtain the registration id.
     * @return the id
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
    public static class JOceanusActionRegistration
            extends JOceanusEventRegistration<JOceanusActionEvent> {
        /**
         * Action listener.
         */
        private final JOceanusActionEventListener theListener;

        /**
         * Constructor.
         * @param pOwnerId the owner Id
         * @param pListener the listener
         */
        protected JOceanusActionRegistration(final int pOwnerId,
                                             final JOceanusActionEventListener pListener) {
            super(pOwnerId, RegistrationType.ACTION);
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
    public static class JOceanusChangeRegistration
            extends JOceanusEventRegistration<JOceanusChangeEvent> {
        /**
         * Change listener.
         */
        private final JOceanusChangeEventListener theListener;

        /**
         * Constructor.
         * @param pOwnerId the owner Id
         * @param pListener the listener
         */
        protected JOceanusChangeRegistration(final int pOwnerId,
                                             final JOceanusChangeEventListener pListener) {
            super(pOwnerId, RegistrationType.CHANGE);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final JOceanusChangeEvent pEvent) {
            theListener.processChangeEvent(pEvent);
        }
    }

    /**
     * ItemRegistration class.
     */
    public static class JOceanusItemRegistration
            extends JOceanusEventRegistration<JOceanusItemEvent> {
        /**
         * Item listener.
         */
        private final JOceanusItemEventListener theListener;

        /**
         * Constructor.
         * @param pOwnerId the owner Id
         * @param pListener the listener
         */
        protected JOceanusItemRegistration(final int pOwnerId,
                                           final JOceanusItemEventListener pListener) {
            super(pOwnerId, RegistrationType.ITEM);
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
