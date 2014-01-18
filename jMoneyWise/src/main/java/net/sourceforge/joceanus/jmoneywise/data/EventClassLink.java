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
package net.sourceforge.joceanus.jmoneywise.data;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventClass.EventClassList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Event Class Link for an event.
 */
public class EventClassLink
        extends DataItem
        implements Comparable<EventClassLink> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventClassLink.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ExchangeRate.class.getSimpleName(), DataItem.FIELD_DEFS);

    /**
     * Event Field Id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityValueField("Event");

    /**
     * EventClass Field Id.
     */
    public static final JDataField FIELD_CLASS = FIELD_DEFS.declareEqualityValueField("EventClass");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * Obtain Event.
     * @return the event
     */
    public Event getEvent() {
        return getEvent(getValueSet());
    }

    /**
     * Obtain eventId.
     * @return the eventId
     */
    public Integer getEventId() {
        Event myEvent = getEvent();
        return (myEvent == null)
                ? null
                : myEvent.getId();
    }

    /**
     * Obtain EventClass.
     * @return the class
     */
    public EventClass getEventClass() {
        return getEventClass(getValueSet());
    }

    /**
     * Obtain tagId.
     * @return the tagId
     */
    public Integer getEventClassId() {
        EventClass myClass = getEventClass();
        return (myClass == null)
                ? null
                : myClass.getId();
    }

    /**
     * Obtain Event.
     * @param pValueSet the valueSet
     * @return the Event
     */
    public static Event getEvent(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_EVENT, Event.class);
    }

    /**
     * Obtain EventClass.
     * @param pValueSet the valueSet
     * @return the class
     */
    public static EventClass getEventClass(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLASS, EventClass.class);
    }

    /**
     * Set event value.
     * @param pValue the value
     */
    private void setValueEvent(final Event pValue) {
        getValueSet().setValue(FIELD_EVENT, pValue);
    }

    /**
     * Set event integer value.
     * @param pValue the value
     */
    private void setValueEvent(final Integer pValue) {
        getValueSet().setValue(FIELD_EVENT, pValue);
    }

    /**
     * Set eventClass value.
     * @param pValue the value
     */
    private void setValueEventClass(final EventClass pValue) {
        getValueSet().setValue(FIELD_CLASS, pValue);
    }

    /**
     * Set eventClass integer value.
     * @param pValue the value
     */
    private void setValueEventClass(final Integer pValue) {
        getValueSet().setValue(FIELD_CLASS, pValue);
    }

    /**
     * Set eventClass string value.
     * @param pValue the value
     */
    private void setValueEventClass(final String pValue) {
        getValueSet().setValue(FIELD_CLASS, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public EventClassLink getBase() {
        return (EventClassLink) super.getBase();
    }

    @Override
    public EventClassLinkList getList() {
        return (EventClassLinkList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pTagLink The TagLink to copy
     */
    protected EventClassLink(final EventClassLinkList pList,
                             final EventClassLink pTagLink) {
        /* Set standard values */
        super(pList, pTagLink);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the TagLink id
     * @param pEventId the id of the event
     * @param pClassId the id of the eventClass
     */
    protected EventClassLink(final EventClassLinkList pList,
                             final Integer pId,
                             final Integer pEventId,
                             final Integer pClassId) {
        /* Initialise the item */
        super(pList, pId);

        /* Store the IDs */
        setValueEvent(pEventId);
        setValueEventClass(pClassId);
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pEvent the Event to link
     * @param pClass the name of the event class
     */
    protected EventClassLink(final EventClassLinkList pList,
                             final Integer pId,
                             final Event pEvent,
                             final String pClass) {
        /* Initialise the item */
        super(pList, pId);

        /* Store the links */
        setValueEvent(pEvent);
        setValueEventClass(pClass);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public EventClassLink(final EventClassLinkList pList) {
        super(pList, 0);
    }

    @Override
    public int compareTo(final EventClassLink pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the event */
        int iDiff = Difference.compareObject(getEvent(), pThat.getEvent());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the class */
        iDiff = Difference.compareObject(getEventClass(), pThat.getEventClass());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        EventList myEvents = myData.getEvents();
        EventClassList myTags = myData.getEventClasses();
        ValueSet myValues = getValueSet();

        /* Adjust event */
        Object myCurr = myValues.getValue(FIELD_EVENT);
        if (myCurr instanceof Event) {
            myCurr = ((Event) myCurr).getId();
        }
        if (myCurr instanceof Integer) {
            Event myEvent = myEvents.findItemById((Integer) myCurr);
            if (myEvent == null) {
                addError(ERROR_UNKNOWN, FIELD_EVENT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueEvent(myEvent);
        }

        /* Adjust class */
        myCurr = myValues.getValue(FIELD_CLASS);
        if (myCurr instanceof EventClass) {
            myCurr = ((EventClass) myCurr).getId();
        }
        if (myCurr instanceof Integer) {
            EventClass myClass = myTags.findItemById((Integer) myCurr);
            if (myClass == null) {
                addError(ERROR_UNKNOWN, FIELD_CLASS);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueEventClass(myClass);
        } else if (myCurr instanceof String) {
            EventClass myClass = myTags.findItemByName((String) myCurr);
            if (myClass == null) {
                addError(ERROR_UNKNOWN, FIELD_CLASS);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueEventClass(myClass);
        }
    }

    /**
     * Set a new event.
     * @param pEvent the new event
     */
    public void setEvent(final Event pEvent) {
        setValueEvent(pEvent);
    }

    /**
     * Set a new eventClass.
     * @param pClass the eventClass
     */
    public void setEventClass(final EventClass pClass) {
        setValueEventClass(pClass);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the items referred to */
        getEvent().touchItem(this);
        getEventClass().touchItem(this);
    }

    /**
     * Update base rate from an edited link.
     * @param pLink the edited link
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pLink) {
        /* Can only update from an event tag link */
        if (!(pLink instanceof EventClassLink)) {
            return false;
        }

        EventClassLink myLink = (EventClassLink) pLink;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Event if required */
        if (!Difference.isEqual(getEvent(), myLink.getEvent())) {
            setValueEvent(myLink.getEvent());
        }

        /* Update the class if required */
        if (!Difference.isEqual(getEventClass(), myLink.getEventClass())) {
            setValueEventClass(myLink.getEventClass());
        }
        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The EventClassLink List class.
     */
    public static class EventClassLinkList
            extends DataList<EventClassLink> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventClassLinkList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE EventClassLink list.
         * @param pData the DataSet for the list
         */
        protected EventClassLinkList(final MoneyWiseData pData) {
            super(EventClassLink.class, pData, ListStyle.CORE);
        }

        @Override
        protected EventClassLinkList getEmptyList(final ListStyle pStyle) {
            EventClassLinkList myList = new EventClassLinkList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventClassLinkList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (EventClassLinkList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventClassLinkList(final EventClassLinkList pSource) {
            super(pSource);
        }

        /**
         * Add a new item to the core list.
         * @param pLink item
         * @return the newly added item
         */
        @Override
        public EventClassLink addCopyItem(final DataItem pLink) {
            /* Can only clone an EventClassLink */
            if (!(pLink instanceof EventClassLink)) {
                return null;
            }

            EventClassLink myLink = new EventClassLink(this, (EventClassLink) pLink);
            add(myLink);
            return myLink;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public EventClassLink addNewItem() {
            EventClassLink myLink = new EventClassLink(this);
            add(myLink);
            return myLink;
        }

        /**
         * Allow a link to be added.
         * @param pId the id
         * @param pEvent the event
         * @param pClass the eventClass name
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Event pEvent,
                                final String pClass) throws JOceanusException {
            /* Create the link */
            EventClassLink myLink = new EventClassLink(this, pId, pEvent, pClass);

            /* Check that this LinkId has not been previously added */
            if (!isIdUnique(pId)) {
                myLink.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myLink, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myLink);
        }

        /**
         * Load a secure Category.
         * @param pId the id
         * @param pEventId the event id
         * @param pClassId the class id
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pEventId,
                                  final Integer pClassId) throws JOceanusException {
            /* Create the link */
            EventClassLink myLink = new EventClassLink(this, pId, pEventId, pClassId);

            /* Check that this LinkId has not been previously added */
            if (!isIdUnique(pId)) {
                myLink.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myLink, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myLink);
        }
    }
}
