/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.ValueSet;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.EventInfoType.EventInfoTypeList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;

/**
 * EventValue data type.
 * @author Tony Washer
 */
public class EventValue extends DataItem implements Comparable<EventValue> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventValue.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * InfoType Field Id.
     */
    public static final JDataField FIELD_INFOTYPE = FIELD_DEFS.declareEqualityValueField("InfoType");

    /**
     * Event Field Id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityValueField("Event");

    /**
     * Value Field Id.
     */
    public static final JDataField FIELD_VALUE = FIELD_DEFS.declareEqualityValueField("Value");

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareDerivedValueField("Account");

    /**
     * The active set of values.
     */
    private ValueSet theValueSet;

    @Override
    public void declareValues(final ValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /**
     * Obtain InfoType.
     * @return the Info type
     */
    public EventInfoType getInfoType() {
        return getInfoType(theValueSet);
    }

    /**
     * Obtain Event.
     * @return the Event
     */
    public Event getEvent() {
        return getEvent(theValueSet);
    }

    /**
     * Obtain Value.
     * @return the Value
     */
    public Integer getValue() {
        return getValue(theValueSet);
    }

    /**
     * Obtain Account.
     * @return the Account
     */
    public Account getAccount() {
        return getAccount(theValueSet);
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the Info types
     */
    public static EventInfoType getInfoType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_INFOTYPE, EventInfoType.class);
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
     * Obtain Value.
     * @param pValueSet the valueSet
     * @return the Value
     */
    public static Integer getValue(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_VALUE, Integer.class);
    }

    /**
     * Obtain Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static Account getAccount(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, Account.class);
    }

    /**
     * Set InfoType.
     * @param pValue the info Type
     */
    private void setValueInfoType(final EventInfoType pValue) {
        theValueSet.setValue(FIELD_INFOTYPE, pValue);
    }

    /**
     * Set InfoType Id.
     * @param pId the info Type id
     */
    private void setValueInfoType(final Integer pId) {
        theValueSet.setValue(FIELD_INFOTYPE, pId);
    }

    /**
     * Set Event.
     * @param pValue the event
     */
    private void setValueEvent(final Event pValue) {
        theValueSet.setValue(FIELD_EVENT, pValue);
    }

    /**
     * Set Event id.
     * @param pId the event id
     */
    private void setValueEvent(final Integer pId) {
        theValueSet.setValue(FIELD_EVENT, pId);
    }

    /**
     * Set Value.
     * @param pValue the value
     */
    private void setValueValue(final Integer pValue) {
        theValueSet.setValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Account.
     * @param pValue the account
     */
    private void setValueAccount(final Account pValue) {
        theValueSet.setValue(FIELD_ACCOUNT, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public EventValue getBase() {
        return (EventValue) super.getBase();
    }

    /**
     * Construct a copy of an EventInfo.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected EventValue(final EventValueList pList,
                         final EventValue pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Encryption constructor.
     * @param pList the list
     * @param uId the id
     * @param uInfoTypeId the infoType Id
     * @param uEventId the Event Id
     * @param pValue the value
     * @throws JDataException on error
     */
    private EventValue(final EventValueList pList,
                       final int uId,
                       final int uInfoTypeId,
                       final int uEventId,
                       final Integer pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Ids */
            setValueInfoType(uInfoTypeId);
            setValueEvent(uEventId);

            /* Look up the EventType */
            FinanceData myData = getDataSet();
            EventInfoTypeList myTypes = myData.getInfoTypes();
            EventInfoType myType = myTypes.findItemById(uInfoTypeId);
            if (myType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid EventInfoType Id");
            }
            setValueInfoType(myType);

            /* Look up the Event */
            EventList myEvents = myData.getEvents();
            Event myEvent = myEvents.findItemById(uEventId);
            if (myEvent == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Event Id");
            }
            setValueEvent(myEvent);

            /* Switch on Info Class */
            switch (myType.getInfoClass()) {
                case QualifyYears:
                case XferDelay:
                    setValueValue(pValue);
                    break;
                // case ThirdParty:
                case CashAccount:
                    /* Look up the Account */
                    setValueValue(pValue);
                    AccountList myAccounts = myData.getAccounts();
                    Account myAccount = myAccounts.findItemById(pValue);
                    if (myAccount == null) {
                        throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
                    }
                    setValueAccount(myAccount);
                    break;
                default:
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Event Type");
            }

            /* Access the EventInfoSet and register this value */
            EventInfoSet mySet = myEvent.getInfoSet();
            mySet.registerValue(this);
            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pType the type
     * @param pEvent the event
     */
    private EventValue(final EventValueList pList,
                       final EventInfoType pType,
                       final Event pEvent) {
        /* Initialise the item */
        super(pList, 0);

        /* Record the Detail */
        setValueInfoType(pType);
        setValueEvent(pEvent);
    }

    @Override
    public void deRegister() {
        /* Access the EventInfoSet and register this value */
        EventInfoSet mySet = getEvent().getInfoSet();
        mySet.deRegisterValue(this);
    }

    @Override
    public int compareTo(final EventValue pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Events */
        int iDiff = getEvent().compareTo(pThat.getEvent());
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
    protected void relinkToDataSet() {
        /* Access Events and InfoTypes */
        FinanceData myData = getDataSet();
        EventList myEvents = myData.getEvents();
        EventInfoTypeList myTypes = myData.getInfoTypes();

        /* Update to use the local copy of the Types */
        EventInfoType myType = getInfoType();
        EventInfoType myNewType = myTypes.findItemById(myType.getId());
        setValueInfoType(myNewType);

        /* Update to use the local copy of the Events */
        Event myEvent = getEvent();
        Event myNewEvt = myEvents.findItemById(myEvent.getId());
        setValueEvent(myNewEvt);
    }

    /**
     * Validate the Event Info.
     */
    @Override
    public void validate() {
        EventInfoType myType = getInfoType();
        Event myEvent = getEvent();

        /* Event must be non-null */
        if (myEvent == null) {
            addError("Event must be non-null", FIELD_EVENT);
        }

        /* InfoType must be non-null */
        if (myType == null) {
            addError("EventInfoType must be non-null", FIELD_INFOTYPE);
        } else if (!myType.getEnabled()) {
            addError("EventInfoType must be enabled", FIELD_INFOTYPE);
        } else {
            /* Switch on Info Class */
            switch (myType.getInfoClass()) {
                case QualifyYears:
                case XferDelay:
                    if (getValue() == null) {
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    } else if (getValue() <= 0) {
                        addError(myType.getName() + " must be positive", FIELD_VALUE);
                    }
                    break;
                // case ThirdParty:
                case CashAccount:
                    Account myAccount = getAccount();
                    if (myAccount == null) {
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    } else if (!myAccount.isMoney()) {
                        addError(myType.getName() + " must be money account", FIELD_VALUE);
                    }
                    break;
                default:
                    addError("Invalid Event Type", FIELD_INFOTYPE);
                    break;
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    @Override
    public String formatObject() {
        Integer myValue = getValue();

        /* If we have null, return it */
        if (myValue == null) {
            return "null";
        }

        /* Switch on type of Value */
        switch (getInfoType().getInfoClass()) {
        // case ThirdParty:
            case CashAccount:
                return JDataObject.formatField(getAccount());
            case QualifyYears:
            case XferDelay:
                return JDataObject.formatField(myValue);
            default:
                return "null";
        }
    }

    /**
     * Set Value.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setValue(final Integer pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getInfoClass()) {
            case QualifyYears:
            case XferDelay:
                setValueValue(pValue);
                setValueAccount(null);
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Integer value");
        }
    }

    /**
     * Set Account.
     * @param pValue the Account
     * @throws JDataException on error
     */
    protected void setAccount(final Account pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getInfoClass()) {
        // case ThirdParty:
            case CashAccount:
                setValueAccount(pValue);
                setValueValue((pValue == null) ? null : pValue.getId());
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Account value");
        }
    }

    /**
     * List class for EventValues.
     */
    public static class EventValueList extends DataList<EventValueList, EventValue> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventValueList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The DataSet.
         */
        private FinanceData theData = null;

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        @Override
        protected EventValueList getEmptyList() {
            return new EventValueList(this);
        }

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected EventValueList(final FinanceData pData) {
            super(EventValueList.class, EventValue.class, pData, ListStyle.CORE);
        }

        /**
         * Construct an empty list.
         * @param pData the DataSet for the list
         * @param pStyle the required style
         */
        protected EventValueList(final FinanceData pData,
                                 final ListStyle pStyle) {
            super(EventValueList.class, EventValue.class, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private EventValueList(final EventValueList pSource) {
            super(pSource);
            theData = pSource.theData;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Allow an EventInfo to be loaded.
         * @param uId the id
         * @param uInfoTypeId the infoType
         * @param uEventId the event id
         * @param pValue the value
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uInfoTypeId,
                            final int uEventId,
                            final Integer pValue) throws JDataException {
            /* Create the info */
            EventValue myInfo = new EventValue(this, uId, uInfoTypeId, uEventId, pValue);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myInfo, "Duplicate ValueId");
            }

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, "Failed validation");
            }

            /* Add to the list */
            add(myInfo);
        }

        /**
         * Add new item type (into edit session).
         * @param pType the Item Type
         * @param pEvent the Event
         * @return the value
         */
        protected EventValue addNewItem(final EventInfoType pType,
                                        final Event pEvent) {
            /* Create the new Value */
            EventValue myValue = new EventValue(this, pType, pEvent);

            /* Add it to the list and return */
            addAtEnd(myValue);
            return myValue;
        }

        @Override
        public EventValue addNewItem(final DataItem pElement) {
            /* Can only clone an EventValue */
            if (!(pElement instanceof EventValue)) {
                return null;
            }

            /* Create the new item */
            EventValue mySource = (EventValue) pElement;
            EventValue myValue = new EventValue(this, mySource);

            /* Add to list and return */
            add(myValue);
            return myValue;
        }

        @Override
        public EventValue addNewItem() {
            return null;
        }
    }
}
