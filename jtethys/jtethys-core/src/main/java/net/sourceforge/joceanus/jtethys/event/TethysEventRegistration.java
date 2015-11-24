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

import net.sourceforge.joceanus.jtethys.event.TethysEvent.JOceanusItemEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysItemEvent;

/**
 * Registration structure for event listeners.
 * @param <T> The event for the registration
 */
public abstract class TethysEventRegistration<T extends TethysEvent> {
    /**
     * registrationType.
     */
    private final RegistrationType theType;

    /**
     * manager id.
     */
    private final Integer theMgrId;

    /**
     * registration id.
     */
    private Integer theRegId;

    /**
     * Constructor.
     * @param pMgrId the manager Id
     * @param pType the registration type
     */
    private TethysEventRegistration(final Integer pMgrId,
                                    final RegistrationType pType) {
        theMgrId = pMgrId;
        theType = pType;
    }

    /**
     * Is the event aimed at this registration.
     * @param pEvent the event
     * @return true/false
     */
    public boolean isRelevant(final TethysEvent pEvent) {
        return theMgrId == pEvent.getSourceId();
    }

    /**
     * Obtain the registration id.
     * @return the id
     */
    protected Integer getRegId() {
        return theRegId;
    }

    /**
     * Set the registration id.
     * @param pId the id
     */
    protected void setRegId(final Integer pId) {
        theRegId = pId;
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
        TethysEventRegistration<?> myReg = (TethysEventRegistration<?>) o;

        /* Compare fields */
        return theRegId == myReg.getRegId();
    }

    @Override
    public int hashCode() {
        return theRegId;
    }

    /**
     * ActionRegistration class.
     */
    public static class TethysActionRegistration
            extends TethysEventRegistration<TethysActionEvent> {
        /**
         * Action listener.
         */
        private final TethysActionEventListener theListener;

        /**
         * Action Id.
         */
        private final int theActionId;

        /**
         * Constructor.
         * @param pMgrId the manager Id
         * @param pListener the listener
         */
        protected TethysActionRegistration(final Integer pMgrId,
                                           final TethysActionEventListener pListener) {
            this(pMgrId, TethysEventManager.ACTIONID_ANY, pListener);
        }

        /**
         * Constructor.
         * @param pMgrId the manager Id
         * @param pActionId the actionId to filter on
         * @param pListener the listener
         */
        protected TethysActionRegistration(final Integer pMgrId,
                                           final int pActionId,
                                           final TethysActionEventListener pListener) {
            super(pMgrId, RegistrationType.ACTION);
            theActionId = pActionId;
            theListener = pListener;
        }

        @Override
        protected void processEvent(final TethysActionEvent pEvent) {
            theListener.processActionEvent(pEvent);
        }

        /**
         * Is the event filtered?
         * @param pActionId the action Id
         * @return true/false
         */
        protected boolean isFiltered(final int pActionId) {
            return theActionId != TethysEventManager.ACTIONID_ANY
                   && theActionId != pActionId;
        }
    }

    /**
     * ChangeRegistration class.
     */
    public static class TethysChangeRegistration
            extends TethysEventRegistration<TethysChangeEvent> {
        /**
         * Change listener.
         */
        private final TethysChangeEventListener theListener;

        /**
         * Constructor.
         * @param pMgrId the manager Id
         * @param pListener the listener
         */
        protected TethysChangeRegistration(final Integer pMgrId,
                                           final TethysChangeEventListener pListener) {
            super(pMgrId, RegistrationType.CHANGE);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final TethysChangeEvent pEvent) {
            theListener.processChangeEvent(pEvent);
        }
    }

    /**
     * ItemRegistration class.
     */
    public static class TethysItemRegistration
            extends TethysEventRegistration<TethysItemEvent> {
        /**
         * Item listener.
         */
        private final JOceanusItemEventListener theListener;

        /**
         * Constructor.
         * @param pMgrId the manager Id
         * @param pListener the listener
         */
        protected TethysItemRegistration(final Integer pMgrId,
                                         final JOceanusItemEventListener pListener) {
            super(pMgrId, RegistrationType.ITEM);
            theListener = pListener;
        }

        @Override
        protected void processEvent(final TethysItemEvent pEvent) {
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
