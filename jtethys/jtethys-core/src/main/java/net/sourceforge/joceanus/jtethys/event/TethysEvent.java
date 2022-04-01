/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.event;

/**
 * A generic event with action and item variants. For use by non-GUI components, or else GUI
 * components that do not have these features.
 * <p>
 * Additionally listener interfaces for the events
 * @param <E> the event id type
 */
public class TethysEvent<E extends Enum<E>> {
    /**
     * Interface for event consumers.
     * @param <E> Event type
     */
    @FunctionalInterface
    public interface TethysEventListener<E extends Enum<E>> {
        /**
         * Handle a TethysEvent.
         * @param pEvent the event to handle
         */
        void handleEvent(TethysEvent<E> pEvent);
    }

    /**
     * The Id of the source.
     */
    private final int theSourceId;

    /**
     * The Id of the event.
     */
    private final E theEventId;

    /**
     * The details of the event.
     */
    private final Object theDetails;

    /**
     * Is the event consumed?
     */
    private boolean isConsumed;

    /**
     * Constructor.
     * @param pSourceId the id of the source of the event
     * @param pEventId the event id
     */
    protected TethysEvent(final int pSourceId,
                          final E pEventId) {
        /* Set the details */
        this(pSourceId, pEventId, null);
    }

    /**
     * Constructor.
     * @param pSourceId the id of the source of the event
     * @param pEventId the event id
     * @param pDetails the details of the event
     */
    protected TethysEvent(final int pSourceId,
                          final E pEventId,
                          final Object pDetails) {
        /* Set the details */
        theSourceId = pSourceId;
        theEventId = pEventId;
        theDetails = pDetails;
    }

    /**
     * Obtain the sourceId.
     * @return the sourceId
     */
    public int getSourceId() {
        return theSourceId;
    }

    /**
     * Obtain the eventId.
     * @return the eventId
     */
    public E getEventId() {
        return theEventId;
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
     * @param pClazz the class of the details
     * @param <T> the details class
     * @return the details
     */
    public <T> T getDetails(final Class<T> pClazz) {
        return pClazz.cast(theDetails);
    }

    /**
     * Is the event consumed?
     * @return true/false
     */
    public boolean isConsumed() {
        return isConsumed;
    }

    /**
     * Consume the event.
     */
    public void consume() {
        isConsumed = true;
    }
}
