/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;

/**
 * A list of filter-able events.
 */
public class FilteredEvents
        extends EventList {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(EventFilter.class.getSimpleName(), EventList.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Accounts Field Id.
     */
    public static final JDataField FIELD_FILTER = FIELD_DEFS.declareLocalField("Filter");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_FILTER.equals(pField)) {
            return theFilter;
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Event filter.
     */
    private final EventFilter theFilter;

    /**
     * Obtain the filter.
     * @return the filter
     */
    public EventFilter getFilter() {
        return theFilter;
    }

    /**
     * Constructor.
     * @param pFilter the event filter
     * @param pSource the source event list
     * @param pRange the date range
     */
    public FilteredEvents(final EventFilter pFilter,
                          final EventList pSource,
                          final JDateDayRange pRange) {
        /* Call super-constructor and set style/range */
        super(pSource.getDataSet());
        setStyle(ListStyle.EDIT);
        setRange(pRange);

        /* Record filter and reset lists */
        theFilter = pFilter;
        theFilter.resetLists();

        /* Store InfoType list */
        setEventInfoTypes(getEventInfoTypes());

        /* Create info List */
        EventInfoList myEventInfo = getEventInfo();
        setEventInfos(myEventInfo.getEmptyList(ListStyle.EDIT));

        /* Loop through the source events extracting relevant elements */
        Iterator<Event> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            Event myCurr = myIterator.next();

            /* Ignore deleted events */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* Check the range */
            int myResult = pRange.compareTo(myCurr.getDate());

            /* Handle out of range */
            if (myResult > 0) {
                continue;
            } else if (myResult == -1) {
                break;
            }

            /* Build the new linked event and add it to the list */
            Event myEvent = new Event(this, myCurr);
            append(myEvent);

            /* Register the event with the filter */
            theFilter.registerEvent(myEvent);

            /* If this is a child event */
            if (myEvent.isChild()) {
                /* Register child against parent (in this edit list) */
                registerChild(myEvent);
            }
        }
    }
}
