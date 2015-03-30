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

/**
 * A generic event with action and item variants. For use by non-GUI components, or else GUI components that do not have these features.
 * <p>
 * Additionally listener interfaces for the events
 */
public abstract class JOceanusEvent {
    /**
     * Interface for event consumers.
     */
    public interface JOceanusChangeEventListener {
        /**
         * Process a JOceanusEvent.
         * @param pEvent the event to process
         */
        void processChangeEvent(final JOceanusChangeEvent pEvent);
    }

    /**
     * Interface for action event consumers.
     */
    public interface JOceanusActionEventListener {
        /**
         * Process a JOceanusActionEvent.
         * @param pEvent the event to process
         */
        void processActionEvent(final JOceanusActionEvent pEvent);
    }

    /**
     * Interface for item event consumers.
     */
    public interface JOceanusItemEventListener {
        /**
         * Process a JOceanusItemEvent.
         * @param pEvent the event to process
         */
        void processItemEvent(final JOceanusItemEvent pEvent);
    }

    /**
     * The Id of the source.
     */
    private final int theSourceId;

    /**
     * Constructor.
     * @param pSourceId the id of the source of the event
     */
    protected JOceanusEvent(final int pSourceId) {
        /* Set the details */
        theSourceId = pSourceId;
    }

    /**
     * Obtain the sourceId.
     * @return the sourceId
     */
    public int getSourceId() {
        return theSourceId;
    }

    /**
     * A generic action event.
     */
    public static class JOceanusChangeEvent
            extends JOceanusEvent {
        /**
         * Constructor.
         * @param pSourceId the id of the source of the event
         */
        protected JOceanusChangeEvent(final int pSourceId) {
            /* Set the details */
            super(pSourceId);
        }
    }

    /**
     * A generic action event.
     */
    public static class JOceanusActionEvent
            extends JOceanusEvent {
        /**
         * The Id of the event.
         */
        private final int theActionId;

        /**
         * The details of the event.
         */
        private final Object theDetails;

        /**
         * Constructor.
         * @param pSourceId the id of the source of the event
         * @param pActionId the id of the event
         */
        protected JOceanusActionEvent(final int pSourceId,
                                      final int pActionId) {
            /* Set the details */
            this(pSourceId, pActionId, null);
        }

        /**
         * Constructor.
         * @param pSourceId the id of the source of the event
         * @param pActionId the id of the event
         * @param pDetails the details of the event
         */
        protected JOceanusActionEvent(final int pSourceId,
                                      final int pActionId,
                                      final Object pDetails) {
            /* Set the details */
            super(pSourceId);
            theActionId = pActionId;
            theDetails = pDetails;
        }

        /**
         * Obtain the actionId.
         * @return the actionId
         */
        public int getActionId() {
            return theActionId;
        }

        /**
         * Obtain the details.
         * @return the details
         */
        public Object getDetails() {
            return theDetails;
        }

        /**
         * Obtain the details.
         * @param pClass the class of the details
         * @param <T> the details class
         * @return the details
         */
        public <T> T getDetails(final Class<T> pClass) {
            return pClass.cast(theDetails);
        }
    }

    /**
     * A generic item event.
     */
    public static class JOceanusItemEvent
            extends JOceanusActionEvent {
        /**
         * The Selection state.
         */
        private final boolean isSelected;

        /**
         * Constructor.
         * @param pSourceId the id of the source of the event
         * @param pActionId the id of the event
         * @param pItem the item that has changed selection
         * @param pSelected is the item now selected?
         */
        protected JOceanusItemEvent(final int pSourceId,
                                    final int pActionId,
                                    final Object pItem,
                                    final boolean pSelected) {
            /* Set the details */
            super(pSourceId, pActionId, pItem);
            isSelected = pSelected;
        }

        /**
         * Obtain the selected state.
         * @return the selected state
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Obtain the item.
         * @return the item
         */
        public Object getItem() {
            return getDetails();
        }

        /**
         * Obtain the item.
         * @param pClass the class of the item
         * @param <T> the item class
         * @return the item
         */
        public <T> T getItem(final Class<T> pClass) {
            return getDetails(pClass);
        }
    }
}
