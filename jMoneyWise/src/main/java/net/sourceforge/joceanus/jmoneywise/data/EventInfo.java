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

import java.util.Date;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Representation of an information extension of an event.
 * @author Tony Washer
 */
public class EventInfo
        extends DataInfo<EventInfo, Event, EventInfoType, EventInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventInfo.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventInfo.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataInfo.FIELD_DEFS);

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

    @Override
    public String getLinkName() {
        DataItem myItem = getLink(DataItem.class);
        if (myItem instanceof Account) {
            return ((Account) myItem).getName();
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
     * Secure constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the control id
     * @param pInfoTypeId the info id
     * @param pEventId the Event id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private EventInfo(final EventInfoList pList,
                      final Integer pId,
                      final Integer pControlId,
                      final Integer pInfoTypeId,
                      final Integer pEventId,
                      final byte[] pValue) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId, pControlId, pInfoTypeId, pEventId);

        /* Protect against exceptions */
        try {
            /* Look up the EventType */
            MoneyWiseData myData = getDataSet();
            EventInfoTypeList myTypes = myData.getEventInfoTypes();
            EventInfoType myType = myTypes.findItemById(pInfoTypeId);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_INFOTYPE);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueInfoType(myType);

            /* Look up the Event */
            EventList myEvents = myData.getEvents();
            Event myOwner = myEvents.findItemById(pEventId);
            if (myOwner == null) {
                addError(ERROR_UNKNOWN, FIELD_OWNER);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueOwner(myOwner);

            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case INTEGER:
                    setValueBytes(pValue, Integer.class);
                    if (myType.isLink()) {
                        DataItem myLink = null;
                        switch (myType.getInfoClass()) {
                            case THIRDPARTY:
                                myLink = myData.getAccounts().findItemById(getValue(Integer.class));
                                break;
                            default:
                                break;
                        }
                        if (myLink == null) {
                            addError(ERROR_UNKNOWN, FIELD_LINK);
                            throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
                        }
                        setValueLink(myLink);
                    }
                    break;
                case MONEY:
                    setValueBytes(pValue, JMoney.class);
                    break;
                case UNITS:
                    setValueBytes(pValue, JUnits.class);
                    break;
                case DILUTION:
                    setValueBytes(pValue, JDilution.class);
                    break;
                case DATEDAY:
                    setValueBytes(pValue, JDateDay.class);
                    break;
                case STRING:
                    setValueBytes(pValue, String.class);
                    break;
                default:
                    throw new JMoneyWiseLogicException(this, ERROR_BADDATATYPE);
            }

            /* Access the EventInfoSet and register this data */
            EventInfoSet mySet = myOwner.getInfoSet();
            mySet.registerInfo(this);
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pInfoType the info type
     * @param pEvent the Event
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private EventInfo(final EventInfoList pList,
                      final Integer pId,
                      final EventInfoType pInfoType,
                      final Event pEvent,
                      final Object pValue) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId, pInfoType, pEvent);

        /* Protect against exceptions */
        try {
            /* Set the value */
            setValue(pValue);

            /* Access the EventInfoSet and register this data */
            EventInfoSet mySet = pEvent.getInfoSet();
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

        /* Access Events and InfoTypes */
        MoneyWiseData myData = getDataSet();
        EventList myEvents = myData.getEvents();
        EventInfoTypeList myTypes = myData.getEventInfoTypes();

        /* Update to use the local copy of the Types */
        EventInfoType myType = getInfoType();
        EventInfoType myNewType = myTypes.findItemById(myType.getId());
        setValueInfoType(myNewType);

        /* If we are using an account */
        if (myType.isLink()) {
            Integer myId = getValue(Integer.class);
            DataItem myNewLink = null;
            switch (myType.getInfoClass()) {
                case THIRDPARTY:
                    myNewLink = myData.getAccounts().findItemById(myId);
                    break;
                default:
                    break;
            }

            /* Check link is valid */
            if (myNewLink == null) {
                addError(ERROR_UNKNOWN, FIELD_LINK);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }

            /* Update link value */
            setValueLink(myNewLink);
        }

        /* Update to use the local copy of the Events */
        Event myEvent = getOwner();
        Event myOwner = myEvents.findItemById(myEvent.getId());
        setValueOwner(myOwner);

        /* Access the TaxInfoSet and register this data */
        EventInfoSet mySet = myOwner.getInfoSet();
        mySet.registerInfo(this);
    }

    @Override
    public String formatObject() {
        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on type of Data */
        EventInfoType myType = getInfoType();
        switch (myType.getDataType()) {
            case INTEGER:
                return myFormatter.formatObject(myType.isLink()
                        ? getAccount()
                        : getValue(Integer.class));
            case MONEY:
                return myFormatter.formatObject(getValue(JMoney.class));
            case UNITS:
                return myFormatter.formatObject(getValue(JUnits.class));
            case DILUTION:
                return myFormatter.formatObject(getValue(JDilution.class));
            case DATEDAY:
                return myFormatter.formatObject(getValue(JDateDay.class));
            case STRING:
                return myFormatter.formatObject(getValue(String.class));
            default:
                return null;
        }
    }

    /**
     * Set Value.
     * @param pValue the Value
     * @throws JOceanusException on error
     */
    @Override
    protected void setValue(final Object pValue) throws JOceanusException {
        /* Access the info Type */
        EventInfoType myType = getInfoType();

        /* Access the DataSet and parser */
        MoneyWiseData myDataSet = getDataSet();
        JDataFormatter myFormatter = myDataSet.getDataFormatter();
        JDecimalParser myParser = myFormatter.getDecimalParser();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myType.getDataType()) {
            case INTEGER:
                if (myType.isLink()) {
                    if (pValue instanceof String) {
                        DataItem myLink = null;
                        String myName = (String) pValue;
                        MoneyWiseData myData = getDataSet();
                        switch (myType.getInfoClass()) {
                            case THIRDPARTY:
                                myLink = myData.getAccounts().findItemByName(myName);
                                break;
                            default:
                                break;
                        }
                        if (myLink == null) {
                            addError(ERROR_UNKNOWN, FIELD_LINK);
                            throw new JMoneyWiseDataException(this, ERROR_VALIDATION
                                                                    + " "
                                                                    + myName);
                        }
                        setValueValue(myLink.getId());
                        setValueLink(myLink);
                        bValueOK = true;
                    }
                    if (pValue instanceof DataItem) {
                        DataItem myItem = (DataItem) pValue;
                        setValueValue(myItem.getId());
                        setValueLink(myItem);
                        bValueOK = true;
                    }
                } else if (pValue instanceof Integer) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                break;
            case MONEY:
                if (pValue instanceof JMoney) {
                    setValueValue(pValue);
                    bValueOK = true;
                } else if (pValue instanceof String) {
                    setValueValue(myParser.parseMoneyValue((String) pValue));
                    bValueOK = true;
                }
                break;
            case UNITS:
                if (pValue instanceof JUnits) {
                    setValueValue(pValue);
                    bValueOK = true;
                } else if (pValue instanceof String) {
                    setValueValue(myParser.parseUnitsValue((String) pValue));
                    bValueOK = true;
                }
                break;
            case DILUTION:
                if (pValue instanceof JDilution) {
                    setValueValue(pValue);
                    bValueOK = true;
                } else if (pValue instanceof String) {
                    setValueValue(myParser.parseDilutionValue((String) pValue));
                    bValueOK = true;
                }
                break;
            case DATEDAY:
                if (pValue instanceof Date) {
                    setValueValue(new JDateDay((Date) pValue));
                    bValueOK = true;
                } else if (pValue instanceof JDateDay) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                break;
            case STRING:
                if (pValue instanceof String) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                break;
            default:
                break;
        }

        /* Reject invalid value */
        if (!bValueOK) {
            throw new JMoneyWiseLogicException(this, ERROR_BADDATATYPE);
        }
    }

    /**
     * Update eventInfo from an eventInfo extract.
     * @param pEventInfo the changed eventInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pEventInfo) {
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
            default:
                break;
        }
    }

    /**
     * EventInfoList.
     */
    public static class EventInfoList
            extends DataInfoList<EventInfo, Event, EventInfoType, EventInfoClass> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataInfoList.FIELD_DEFS);

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
            super(EventInfo.class, pData, ListStyle.CORE);
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
        public EventInfo addCopyItem(final DataItem pItem) {
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
            add(myInfo);

            /* return it */
            return myInfo;
        }

        /**
         * Allow an EventInfo to be added.
         * @param pId the id
         * @param pControlId the control id
         * @param pInfoTypeId the info type id
         * @param pEventId the event id
         * @param pValue the data
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Integer pInfoTypeId,
                                  final Integer pEventId,
                                  final byte[] pValue) throws JOceanusException {
            /* Create the info */
            EventInfo myInfo = new EventInfo(this, pId, pControlId, pInfoTypeId, pEventId, pValue);

            /* Check that this DataId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myInfo);
        }

        @Override
        public void addOpenItem(final Integer pId,
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
                throw new JMoneyWiseDataException(pEvent, ERROR_BADINFOCLASS
                                                          + " ["
                                                          + pInfoClass
                                                          + "]");
            }

            /* Create a new Event Info */
            EventInfo myInfo = new EventInfo(this, pId, myInfoType, pEvent, pValue);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Event Info to the list */
            append(myInfo);

            /* Validate the Info */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }
        }
    }
}
