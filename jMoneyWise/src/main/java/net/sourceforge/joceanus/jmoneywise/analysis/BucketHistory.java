/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.data.Event;

/**
 * History for a bucket.
 * @param <T> the values
 */
public class BucketHistory<T extends BucketValues<T, ?>>
        extends LinkedHashMap<Integer, BucketSnapShot<T>>
        implements JDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1327128217723296123L;

    /**
     * values.
     */
    private final T theValues;

    /**
     * Base values.
     */
    private final T theBaseValues;

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Are there any entries in the map?
     * @return true/false
     */
    protected boolean isIdle() {
        return isEmpty();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    protected T getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    protected T getBaseValues() {
        return theBaseValues;
    }

    /**
     * Constructor.
     * @param pValues the initial values
     */
    protected BucketHistory(final T pValues) {
        /* Store the values */
        theValues = pValues;

        /* Create base as a snapshot */
        theBaseValues = theValues.getSnapShot();
    }

    /**
     * Constructor.
     * @param pHistory the base history
     * @param pDate the date for history cut-off
     */
    protected BucketHistory(final BucketHistory<T> pHistory,
                            final JDateDay pDate) {
        /* Copy the base values */
        theBaseValues = pHistory.getBaseValues().getSnapShot();

        /* Record latest event */
        BucketSnapShot<T> myLatest = null;

        /* Loop through the map */
        Iterator<Map.Entry<Integer, BucketSnapShot<T>>> myIterator = pHistory.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T>> myEntry = myIterator.next();
            BucketSnapShot<T> myEvent = myEntry.getValue();

            /* If we have passed the Date, break the loop */
            if (pDate.compareTo(myEvent.getDate()) < 0) {
                break;
            }

            /* The event is relevant so add to the map */
            put(myEntry.getKey(), myEvent);

            /* Store latest value */
            myLatest = myEvent;
        }

        /* If we have no entries */
        if (myLatest == null) {
            /* Values are identical to base values */
            theValues = theBaseValues.getSnapShot();
        } else {
            /* Take a snapShot of the latest values */
            theValues = myLatest.getNewSnapShot();
        }
    }

    /**
     * Constructor.
     * @param pHistory the base history
     * @param pRange the date range for history cut-off
     */
    protected BucketHistory(final BucketHistory<T> pHistory,
                            final JDateDayRange pRange) {
        /* Record first and last events */
        BucketSnapShot<T> myFirst = null;
        BucketSnapShot<T> myLatest = null;

        /* Loop through the map */
        Iterator<Map.Entry<Integer, BucketSnapShot<T>>> myIterator = pHistory.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T>> myEntry = myIterator.next();
            BucketSnapShot<T> myEvent = myEntry.getValue();

            /* If we are past the initial Date */
            int iRange = pRange.compareTo(myEvent.getDate());
            if (iRange <= 0) {
                /* If we are within the range */
                if (iRange == 0) {
                    /* Note that this counts as latest */
                    myLatest = myEvent;
                }

                /* Break the loop */
                break;
            }

            /* Store first value */
            myFirst = myEvent;
        }

        /* Determine the base values */
        theBaseValues = (myFirst == null)
                ? pHistory.getBaseValues().getSnapShot()
                : myFirst.getNewSnapShot();

        /* If we broke the loop because we found an event */
        if (myLatest != null) {
            /* Add to the map */
            BucketSnapShot<T> myNewEvent = new BucketSnapShot<T>(myLatest, theBaseValues);
            put(myLatest.getId(), myNewEvent);
        }

        /* Continue the loop */
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T>> myEntry = myIterator.next();
            BucketSnapShot<T> myEvent = myEntry.getValue();

            /* If we are past the range, break the loop */
            if (pRange.compareTo(myEvent.getDate()) < 0) {
                break;
            }

            /* Add to the map */
            BucketSnapShot<T> myNewEvent = new BucketSnapShot<T>(myEvent, theBaseValues);
            put(myEntry.getKey(), myNewEvent);

            /* Store latest value */
            myLatest = myEvent;
        }

        /* Store the values */
        theValues = (myLatest != null)
                ? myLatest.getNewSnapShot()
                : theBaseValues.getSnapShot();
    }

    /**
     * Register the event.
     * @param pEvent the event to register.
     * @param pValues the values
     * @return the snapShot values
     */
    protected T registerEvent(final Event pEvent,
                              final T pValues) {
        /* Allocate the event and add to map */
        BucketSnapShot<T> myEvent = new BucketSnapShot<T>(pEvent, pValues);
        put(pEvent.getId(), myEvent);

        /* Return the values */
        return myEvent.getSnapShot();
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public T getValuesForEvent(final Event pEvent) {
        /* Locate the event in the map */
        BucketSnapShot<T> myEvent = get(pEvent.getId());
        return (myEvent == null)
                ? null
                : myEvent.getSnapShot();
    }
}
