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
package net.sourceforge.JFinanceApp.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataFormatter;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.data.EncryptedItem;
import net.sourceforge.JDecimal.JDilution;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JUnits;
import net.sourceforge.JFinanceApp.data.Event.EventList;
import net.sourceforge.JFinanceApp.data.EventInfoType.EventInfoTypeList;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDilution;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedUnits;
import net.sourceforge.JGordianKnot.EncryptedValueSet;

/**
 * EventData data type.
 * @author Tony Washer
 */
public class EventData extends EncryptedItem implements Comparable<EventData> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = EventData.class.getSimpleName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

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
     * Obtain InfoType.
     * @return the Info type
     */
    public EventInfoType getInfoType() {
        return getInfoType(getValueSet());
    }

    /**
     * Obtain Event.
     * @return the Event
     */
    public Event getEvent() {
        return getEvent(getValueSet());
    }

    /**
     * Obtain Units.
     * @return the Units
     */
    public JUnits getUnits() {
        return getUnits(getValueSet());
    }

    /**
     * Obtain Money.
     * @return the Money
     */
    public JMoney getMoney() {
        return getMoney(getValueSet());
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public JDilution getDilution() {
        return getDilution(getValueSet());
    }

    /**
     * Obtain Encrypted Bytes.
     * @return the Bytes
     */
    public byte[] getValueBytes() {
        return getValueBytes(getValueSet());
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
     * Obtain Units.
     * @param pValueSet the valueSet
     * @return the Units
     */
    public static JUnits getUnits(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedUnits)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JUnits.class);
    }

    /**
     * Obtain Money.
     * @param pValueSet the valueSet
     * @return the Money
     */
    public static JMoney getMoney(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedMoney)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JMoney.class);
    }

    /**
     * Obtain Dilution.
     * @param pValueSet the valueSet
     * @return the Dilution
     */
    public static JDilution getDilution(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedDilution)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JDilution.class);
    }

    /**
     * Obtain Encrypted Bytes.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getValueBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_VALUE);
    }

    /**
     * Set InfoType.
     * @param pValue the info Type
     */
    private void setValueInfoType(final EventInfoType pValue) {
        getValueSet().setValue(FIELD_INFOTYPE, pValue);
    }

    /**
     * Set InfoType Id.
     * @param pId the info Type id
     */
    private void setValueInfoType(final Integer pId) {
        getValueSet().setValue(FIELD_INFOTYPE, pId);
    }

    /**
     * Set Event.
     * @param pValue the event
     */
    private void setValueEvent(final Event pValue) {
        getValueSet().setValue(FIELD_EVENT, pValue);
    }

    /**
     * Set Event id.
     * @param pId the event id
     */
    private void setValueEvent(final Integer pId) {
        getValueSet().setValue(FIELD_EVENT, pId);
    }

    /**
     * Set Units.
     * @param pValue the units
     * @throws JDataException on error
     */
    private void setValueUnits(final JUnits pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Money.
     * @param pValue the money
     * @throws JDataException on error
     */
    private void setValueMoney(final JMoney pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Dilution.
     * @param pValue the dilution
     * @throws JDataException on error
     */
    private void setValueDilution(final JDilution pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Units.
     * @param pBytes the units
     * @throws JDataException on error
     */
    private void setValueUnits(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JUnits.class);
    }

    /**
     * Set Money.
     * @param pBytes the money
     * @throws JDataException on error
     */
    private void setValueMoney(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JMoney.class);
    }

    /**
     * Set Dilution.
     * @param pBytes the dilution
     * @throws JDataException on error
     */
    private void setValueDilution(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JDilution.class);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public EventData getBase() {
        return (EventData) super.getBase();
    }

    /**
     * Construct a copy of an EventInfo.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected EventData(final EventDataList pList,
                        final EventData pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Encrypted constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uEventId the event id
     * @param pValue the value
     * @throws JDataException on error
     */
    private EventData(final EventDataList pList,
                      final int uId,
                      final int uControlId,
                      final int uInfoTypeId,
                      final int uEventId,
                      final byte[] pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Ids */
            setValueInfoType(uInfoTypeId);
            setValueEvent(uEventId);

            /* Store the controlId */
            setControlKey(uControlId);

            /* Look up the EventType */
            FinanceData myData = pList.getDataSet();
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
                case TaxCredit:
                case NatInsurance:
                case Benefit:
                    setValueMoney(pValue);
                    break;
                case Dilution:
                    setValueDilution(pValue);
                    break;
                case CreditUnits:
                case DebitUnits:
                    setValueUnits(pValue);
                    break;
                default:
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Event Type");
            }

            /* Access the EventInfoSet and register this data */
            EventInfoSet mySet = myEvent.getInfoSet();
            mySet.registerData(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the EventInfoSet and register this value */
        EventInfoSet mySet = getEvent().getInfoSet();
        mySet.deRegisterData(this);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pType the type
     * @param pEvent the event
     */
    private EventData(final EventDataList pList,
                      final EventInfoType pType,
                      final Event pEvent) {
        /* Initialise the item */
        super(pList, 0);

        /* Record the Detail */
        setValueInfoType(pType);
        setValueEvent(pEvent);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The EventData to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(final EventData pThat) {
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
        /* Update the Encryption details */
        super.relinkToDataSet();

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
     * Validate the Event Data.
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
                case TaxCredit:
                case NatInsurance:
                case Benefit:
                    JMoney myMoney = getMoney();
                    if (myMoney == null) {
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    } else if (!myMoney.isPositive()) {
                        addError(myType.getName() + " must be positive", FIELD_VALUE);
                    }
                    break;
                case Dilution:
                    JDilution myDilution = getDilution();
                    if (myDilution == null) {
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    } else if (myDilution.outOfRange()) {
                        addError("Dilution factor value is outside allowed range (0-1)", FIELD_VALUE);
                    }
                    break;
                case CreditUnits:
                case DebitUnits:
                    JUnits myUnits = getUnits();
                    if (myUnits == null) {
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    } else if (!myUnits.isPositive()) {
                        addError(myType.getName() + " must be positive", FIELD_VALUE);
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
        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on type of Data */
        switch (getInfoType().getInfoClass()) {
            case TaxCredit:
            case NatInsurance:
            case Benefit:
                return myFormatter.formatObject(getMoney());
            case CreditUnits:
            case DebitUnits:
                return myFormatter.formatObject(getUnits());
            case Dilution:
                return myFormatter.formatObject(getDilution());
            default:
                return "null";
        }
    }

    /**
     * Set Money.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setMoney(final JMoney pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getInfoClass()) {
            case TaxCredit:
            case NatInsurance:
            case Benefit:
                /* Set the value */
                setValueMoney(pValue);
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Money value");
        }
    }

    /**
     * Set Units.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setUnits(final JUnits pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getInfoClass()) {
            case CreditUnits:
            case DebitUnits:
                /* Set the value */
                setValueUnits(pValue);
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Units value");
        }
    }

    /**
     * Set Dilution.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setDilution(final JDilution pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getInfoClass()) {
            case Dilution:
                /* Set value */
                setValueDilution(pValue);
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Dilution value");
        }
    }

    /**
     * List class for EventData.
     */
    public static class EventDataList extends EncryptedList<EventData> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventDataList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE rate list.
         * @param pData the DataSet for the list
         */
        protected EventDataList(final FinanceData pData) {
            super(EventData.class, pData);
        }

        /**
         * Construct an empty list.
         * @param pData the DataSet for the list
         * @param pStyle the required style
         */
        protected EventDataList(final FinanceData pData,
                                final ListStyle pStyle) {
            super(EventData.class, pData);
            setStyle(pStyle);
            setGeneration(pData.getGeneration());
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private EventDataList(final EventDataList pSource) {
            super(pSource);
        }

        @Override
        protected EventDataList getEmptyList() {
            return new EventDataList(this);
        }

        @Override
        public EventDataList cloneList(final DataSet<?> pDataSet) {
            return (EventDataList) super.cloneList(pDataSet);
        }

        @Override
        public EventDataList deriveList(final ListStyle pStyle) {
            return (EventDataList) super.deriveList(pStyle);
        }

        @Override
        public EventDataList deriveDifferences(final DataList<EventData> pOld) {
            return (EventDataList) super.deriveDifferences(pOld);
        }

        /**
         * Allow an EventData to be added.
         * @param uId the id
         * @param uControlId the control id
         * @param uInfoTypeId the info type id
         * @param uEventId the event id
         * @param pValue the data
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uControlId,
                            final int uInfoTypeId,
                            final int uEventId,
                            final byte[] pValue) throws JDataException {
            /* Create the info */
            EventData myInfo = new EventData(this, uId, uControlId, uInfoTypeId, uEventId, pValue);

            /* Check that this DataId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myInfo, "Duplicate DataId");
            }

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, "Failed validation");
            }

            /* Add to the list */
            addAtEnd(myInfo);
        }

        /**
         * Add new item type (into edit session).
         * @param pType the Item Type
         * @param pEvent the Event
         * @return the new item
         */
        protected EventData addNewItem(final EventInfoType pType,
                                       final Event pEvent) {
            /* Create the new Data */
            EventData myData = new EventData(this, pType, pEvent);

            /* Add it to the list and return */
            add(myData);
            return myData;
        }

        @Override
        public EventData addNewItem(final DataItem pElement) {
            /* Can only clone an EventData */
            if (!(pElement instanceof EventData)) {
                return null;
            }

            /* Create the new item */
            EventData mySource = (EventData) pElement;
            EventData myInfo = new EventData(this, mySource);

            /* Add to list and return */
            add(myInfo);
            return myInfo;
        }

        @Override
        public EventData addNewItem() {
            return null;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }
    }
}
