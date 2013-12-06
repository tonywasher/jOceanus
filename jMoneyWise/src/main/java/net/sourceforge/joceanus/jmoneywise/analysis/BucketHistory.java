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
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.data.Event;

/**
 * History for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class BucketHistory<T extends BucketValues<T, E>, E extends Enum<E> & BucketAttribute>
        extends LinkedHashMap<Integer, BucketSnapShot<T, E>>
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

    /**
     * Last values.
     */
    private transient T theLastValues = null;

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
    protected BucketHistory(final BucketHistory<T, E> pHistory,
                            final JDateDay pDate) {
        /* Copy the base values */
        theBaseValues = pHistory.getBaseValues().getSnapShot();

        /* Record latest event */
        BucketSnapShot<T, E> myLatest = null;

        /* Loop through the map */
        Iterator<Map.Entry<Integer, BucketSnapShot<T, E>>> myIterator = pHistory.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T, E>> myEntry = myIterator.next();
            BucketSnapShot<T, E> myEvent = myEntry.getValue();

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
    protected BucketHistory(final BucketHistory<T, E> pHistory,
                            final JDateDayRange pRange) {
        /* Record first and last events */
        BucketSnapShot<T, E> myFirst = null;
        BucketSnapShot<T, E> myLatest = null;

        /* Loop through the map */
        Iterator<Map.Entry<Integer, BucketSnapShot<T, E>>> myIterator = pHistory.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T, E>> myEntry = myIterator.next();
            BucketSnapShot<T, E> myEvent = myEntry.getValue();

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
        theLastValues = theBaseValues;

        /* If we broke the loop because we found an event */
        if (myLatest != null) {
            /* Add to the map */
            BucketSnapShot<T, E> myNewEvent = new BucketSnapShot<T, E>(myLatest, theBaseValues, theLastValues);
            put(myLatest.getId(), myNewEvent);
            theLastValues = myNewEvent.getSnapShot();
        }

        /* Continue the loop */
        while (myIterator.hasNext()) {
            Map.Entry<Integer, BucketSnapShot<T, E>> myEntry = myIterator.next();
            BucketSnapShot<T, E> myEvent = myEntry.getValue();

            /* If we are past the range, break the loop */
            if (pRange.compareTo(myEvent.getDate()) < 0) {
                break;
            }

            /* Add to the map */
            BucketSnapShot<T, E> myNewEvent = new BucketSnapShot<T, E>(myEvent, theBaseValues, theLastValues);
            put(myEntry.getKey(), myNewEvent);
            theLastValues = myNewEvent.getNewSnapShot();

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
        BucketSnapShot<T, E> myEvent = new BucketSnapShot<T, E>(pEvent, pValues, theLastValues);
        put(pEvent.getId(), myEvent);
        theLastValues = myEvent.getSnapShot();

        /* Return the values */
        return theLastValues;
    }

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values (or null)
     */
    public T getValuesForEvent(final Event pEvent) {
        /* Locate the event in the map */
        BucketSnapShot<T, E> myEvent = get(pEvent.getId());
        return (myEvent == null)
                ? null
                : myEvent.getSnapShot();
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JDecimal getDeltaValue(final Event pEvent,
                                  final E pAttr) {
        /* Locate the event in the map */
        BucketSnapShot<T, E> myEvent = get(pEvent.getId());
        return (myEvent == null)
                ? null
                : myEvent.getDeltaValue(pAttr);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JMoney getDeltaMoneyValue(final Event pEvent,
                                     final E pAttr) {
        /* Locate the event in the map */
        BucketSnapShot<T, E> myEvent = get(pEvent.getId());
        return (myEvent == null)
                ? null
                : myEvent.getDeltaMoneyValue(pAttr);
    }

    /**
     * Obtain delta for event.
     * @param pEvent the event
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JUnits getDeltaUnitsValue(final Event pEvent,
                                     final E pAttr) {
        /* Locate the event in the map */
        BucketSnapShot<T, E> myEvent = get(pEvent.getId());
        return (myEvent == null)
                ? null
                : myEvent.getDeltaUnitsValue(pAttr);
    }
}
