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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Representation of an information extension of an event.
 * @author Tony Washer
 */
public class EventInfo
        extends DataInfo<EventInfo, Event, EventInfoType, EventInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.EVENTINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.EVENTINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public EventInfoType getInfoType() {
        return getInfoType(getValueSet(), EventInfoType.class);
    }

    @Override
    public EventInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    @Override
    public Event getOwner() {
        return getOwner(getValueSet(), Event.class);
    }

    /**
     * Obtain Account.
     * @return the Account
     */
    public Account getAccount() {
        return getAccount(getValueSet());
    }

    /**
     * Obtain Event Tag.
     * @return the Event Tag
     */
    public EventTag getEventTag() {
        return getEventTag(getValueSet());
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the Money
     */
    public static EventInfoType getInfoType(final ValueSet pValueSet) {
        return getInfoType(pValueSet, EventInfoType.class);
    }

    /**
     * Obtain Linked Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static Account getAccount(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, Account.class);
    }

    /**
     * Obtain Linked EventTag.
     * @param pValueSet the valueSet
     * @return the EventTag
     */
    public static EventTag getEventTag(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, EventTag.class);
    }

    @Override
    public String getLinkName() {
        DataItem<?> myItem = getLink(DataItem.class);
        if (myItem instanceof Account) {
            return ((Account) myItem).getName();
        }
        if (myItem instanceof EventTag) {
            return ((EventTag) myItem).getName();
        }
        return null;
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public EventInfo getBase() {
        return (EventInfo) super.getBase();
    }

    @Override
    public EventInfoList getList() {
        return (EventInfoList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected EventInfo(final EventInfoList pList,
                        final EventInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
        setControlKey(pList.getControlKey());
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pEvent the event
     * @param pType the type
     */
    private EventInfo(final EventInfoList pList,
                      final Event pEvent,
                      final EventInfoType pType) {
        /* Initialise the item */
        super(pList);
        setControlKey(pList.getControlKey());

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pEvent);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private EventInfo(final EventInfoList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getEventInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getEvents());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the EventInfoSet and register this data */
            EventInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the EventInfoSet and register this value */
        EventInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The EventInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final EventInfo pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Events */
        int iDiff = getOwner().compareTo(pThat.getOwner());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the Info Types */
        iDiff = getInfoType().compareTo(pThat.getInfoType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getEventInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getEvents());

        /* Resolve any link value */
        resolveLink();

        /* Access the EventInfoSet and register this data */
        EventInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Resolve link reference.
     * @throws JOceanusException on error
     */
    private void resolveLink() throws JOceanusException {
        /* If we have a link */
        EventInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            MoneyWiseData myData = getDataSet();
            ValueSet myValues = getValueSet();
            Object myLinkId = myValues.getValue(FIELD_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case THIRDPARTY:
                    resolveDataLink(FIELD_LINK, myData.getAccounts());
                    if (myLinkId == null) {
                        setValueValue(getAccount().getId());
                    }
                    break;
                case EVENTTAG:
                    resolveDataLink(FIELD_LINK, myData.getEventClasses());
                    if (myLinkId == null) {
                        setValueValue(getEventTag().getId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update eventInfo from an eventInfo extract.
     * @param pEventInfo the changed eventInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pEventInfo) {
        /* Can only update from EventInfo */
        if (!(pEventInfo instanceof EventInfo)) {
            return false;
        }

        /* Access as EventInfo */
        EventInfo myEventInfo = (EventInfo) pEventInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!Difference.isEqual(getField(), myEventInfo.getField())) {
            setValueValue(myEventInfo.getField());
            if (getInfoType().isLink()) {
                setValueLink(myEventInfo.getLink(DataItem.class));
            }
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch info class */
        super.touchUnderlyingItems();

        /* Switch on info class */
        switch (getInfoClass()) {
            case THIRDPARTY:
                getAccount().touchItem(getOwner());
                break;
            case EVENTTAG:
                getEventTag().touchItem(getOwner());
                break;
            default:
                break;
        }
    }

    /**
     * EventInfoList.
     */
    public static class EventInfoList
            extends DataInfoList<EventInfo, Event, EventInfoType, EventInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataInfoList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return EventInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final EventInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected EventInfoList(final MoneyWiseData pData) {
            super(EventInfo.class, pData, MoneyWiseDataType.EVENTINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private EventInfoList(final EventInfoList pSource) {
            super(pSource);
        }

        @Override
        public EventInfoList getEmptyList(final ListStyle pStyle) {
            EventInfoList myList = new EventInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventInfoList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (EventInfoList) super.cloneList(pDataSet);
        }

        @Override
        public EventInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an EventInfo */
            if (!(pItem instanceof EventInfo)) {
                return null;
            }

            EventInfo myInfo = new EventInfo(this, (EventInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public EventInfo addNewItem() {
            return null;
        }

        @Override
        protected EventInfo addNewItem(final Event pOwner,
                                       final EventInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            EventInfo myInfo = new EventInfo(this, pOwner, pInfoType);
            append(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Event pEvent,
                                final EventInfoClass pInfoClass,
                                final Object pValue) throws JOceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            EventInfoType myInfoType = myData.getEventInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JMoneyWiseDataException(pEvent, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(TaxYearInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pEvent);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Event Info */
            EventInfo myInfo = new EventInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Event Info to the list */
            append(myInfo);
        }

        @Override
        public EventInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the info */
            EventInfo myInfo = new EventInfo(this, pValues);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myInfo);

            /* Return it */
            return myInfo;
        }

        /**
         * Resolve ValueLinks.
         * @throws JOceanusException on error
         */
        public void resolveValueLinks() throws JOceanusException {
            /* Loop through the Info items */
            Iterator<EventInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                EventInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink();
                }
            }
        }
    }
}
