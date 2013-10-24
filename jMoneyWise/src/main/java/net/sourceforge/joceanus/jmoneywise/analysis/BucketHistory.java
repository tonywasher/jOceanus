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

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Constructor.
     */
    protected BucketHistory() {
    }

    /**
     * Register the event.
     * @param pEvent the event to register.
     * @param pValues the values
     */
    protected void registerEvent(final Event pEvent,
                                 final T pValues) {
        /* Allocate the event and add to map */
        BucketSnapShot<T> myEvent = new BucketSnapShot<T>(pEvent, pValues);
        put(pEvent.getId(), myEvent);
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

    /**
     * Obtain values for date.
     * <p>
     * If no events are found prior to the date then null is returned.
     * @param pDate the date
     * @return the values (or null)
     */
    public T getValuesForDate(final JDateDay pDate) {
        /* Record latest event */
        BucketSnapShot<T> myLatest = null;

        /* Loop through the map */
        Iterator<BucketSnapShot<T>> myIterator = values().iterator();
        while (myIterator.hasNext()) {
            BucketSnapShot<T> myEvent = myIterator.next();

            /* If we have passed the Date, break the loop */
            if (pDate.compareTo(myEvent.getDate()) > 0) {
                break;
            }

            /* Store latest value */
            myLatest = myEvent;
        }

        /* Return the values */
        return (myLatest == null)
                ? null
                : myLatest.getSnapShot().getSnapShot();
    }

    /**
     * Obtain values for date range.
     * <p>
     * This method returns an array of length 2 containing the values at the start and at the end of the range.
     * <ul>
     * <li>If no events are found prior to the end of the range, then null is returned.
     * <li>If no events are found prior to the start of the range then the first element is null.
     * <li>If no events are found within the range then the second element is null.
     * </ul>
     * @param pRange the date range
     * @return the values (or null)
     */
    public T[] getValuesForRange(final JDateDayRange pRange) {
        /* Record first event */
        BucketSnapShot<T> myFirst = null;
        BucketSnapShot<T> myLatest = null;

        /* Loop through the map */
        Iterator<BucketSnapShot<T>> myIterator = values().iterator();
        while (myIterator.hasNext()) {
            BucketSnapShot<T> myEvent = myIterator.next();

            /* If we are past the initial Date */
            int iRange = pRange.compareTo(myEvent.getDate());
            if (iRange >= 0) {
                /* If we are within the range */
                if (iRange == 0) {
                    /* Note that this counts as latest */
                    myLatest = myEvent;
                }
                break;
            }

            /* Store first value */
            myFirst = myEvent;
        }

        /* Continue the loop */
        while (myIterator.hasNext()) {
            BucketSnapShot<T> myEvent = myIterator.next();

            /* If we are past the range, break the loop */
            if (pRange.compareTo(myEvent.getDate()) > 0) {
                break;
            }

            /* Store latest value */
            myLatest = myEvent;
        }

        /* If we have a latest event */
        if (myLatest != null) {
            T mySnapShot = myLatest.getSnapShot();
            T[] myArray = mySnapShot.getSnapShotArray();

            /* Store the values */
            myArray[0] = (myFirst == null)
                    ? null
                    : myFirst.getSnapShot().getSnapShot();
            myArray[1] = mySnapShot.getSnapShot();

            /* Return the values */
            return myArray;

            /* If we have a first event */
        } else if (myFirst != null) {
            T mySnapShot = myFirst.getSnapShot();
            T[] myArray = mySnapShot.getSnapShotArray();

            /* Store the values */
            myArray[0] = mySnapShot.getSnapShot();
            myArray[1] = null;

            /* Return the values */
            return myArray;
        }

        /* return null */
        return null;
    }
}
