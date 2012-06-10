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
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedField;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.EventInfoType.EventInfoTypeList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.EncryptedItem;

/**
 * EventData data type.
 * @author Tony Washer
 */
public class EventData extends EncryptedItem<EventData> {
    /**
     * The name of the object
     */
    public static final String OBJECT_NAME = "EventData";

    /**
     * The name of the object
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

    /* Field IDs */
    public static final JDataField FIELD_INFOTYPE = FIELD_DEFS.declareEqualityValueField("InfoType");
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityValueField("Event");
    public static final JDataField FIELD_VALUE = FIELD_DEFS.declareEqualityValueField("Value");

    /**
     * The active set of values
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public EventInfoType getInfoType() {
        return getInfoType(theValueSet);
    }

    public Event getEvent() {
        return getEvent(theValueSet);
    }

    public Units getUnits() {
        return getUnits(theValueSet);
    }

    public Money getMoney() {
        return getMoney(theValueSet);
    }

    public Dilution getDilution() {
        return getDilution(theValueSet);
    }

    public byte[] getValueBytes() {
        return getValueBytes(theValueSet);
    }

    private Object getValueField() {
        return getValueField(theValueSet);
    }

    public static EventInfoType getInfoType(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_INFOTYPE, EventInfoType.class);
    }

    public static Event getEvent(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_EVENT, Event.class);
    }

    public static Units getUnits(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, Units.class);
    }

    public static Money getMoney(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, Money.class);
    }

    public static Dilution getDilution(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, Dilution.class);
    }

    public static byte[] getValueBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_VALUE);
    }

    private static EncryptedField<?> getValueField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_VALUE, EncryptedField.class);
    }

    private void setValueInfoType(EventInfoType pInfoType) {
        theValueSet.setValue(FIELD_INFOTYPE, pInfoType);
    }

    private void setValueInfoType(Integer pInfoType) {
        theValueSet.setValue(FIELD_INFOTYPE, pInfoType);
    }

    private void setValueEvent(Event pEvent) {
        theValueSet.setValue(FIELD_EVENT, pEvent);
    }

    private void setValueEvent(Integer pEvent) {
        theValueSet.setValue(FIELD_EVENT, pEvent);
    }

    private void setValueUnits(Units pUnits) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pUnits);
    }

    private void setValueMoney(Money pMoney) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pMoney);
    }

    private void setValueDilution(Dilution pDilution) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pDilution);
    }

    private void setValueUnits(byte[] pUnits) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pUnits, Units.class);
    }

    private void setValueMoney(byte[] pMoney) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pMoney, Money.class);
    }

    private void setValueDilution(byte[] pDilution) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pDilution, Dilution.class);
    }

    /* Linking methods */
    @Override
    public EventData getBase() {
        return (EventData) super.getBase();
    }

    /**
     * Construct a copy of an EventInfo
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected EventData(EventDataList pList,
                        EventData pInfo) {
        /* Set standard values */
        super(pList, pInfo);
        ListStyle myOldStyle = pInfo.getStyle();

        /* Switch on the ListStyle */
        switch (getStyle()) {
            case EDIT:
                /* If this is a view creation */
                if (myOldStyle == ListStyle.CORE) {
                    /* Rate is based on the original element */
                    setBase(pInfo);
                    pList.setNewId(this);
                    break;
                }

                /* Else this is a duplication so treat as new item */
                setId(0);
                pList.setNewId(this);
                break;
            case CLONE:
                reBuildLinks(pList.getData());
            case COPY:
            case CORE:
                /* Reset Id if this is an insert from a view */
                if (myOldStyle == ListStyle.EDIT)
                    setId(0);
                pList.setNewId(this);
                break;
            case UPDATE:
                setBase(pInfo);
                setState(pInfo.getState());
                break;
        }
    }

    /* Encryption constructor */
    private EventData(EventDataList pList,
                      int uId,
                      int uControlId,
                      int uInfoTypeId,
                      int uEventId,
                      byte[] pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Record the Ids */
        setValueInfoType(uInfoTypeId);
        setValueEvent(uEventId);

        /* Store the controlId */
        setControlKey(uControlId);

        /* Look up the EventType */
        FinanceData myData = pList.getData();
        EventInfoType myType = myData.getInfoTypes().searchFor(uInfoTypeId);
        if (myType == null) {
            throw new JDataException(ExceptionClass.DATA, this, "Invalid EventInfoType Id");
        }
        setValueInfoType(myType);

        /* Look up the Event */
        Event myEvent = myData.getEvents().searchFor(uEventId);
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

        /* Allocate the id */
        pList.setNewId(this);
    }

    @Override
    public void deRegister() {
        /* Access the EventInfoSet and register this value */
        EventInfoSet mySet = getEvent().getInfoSet();
        mySet.deRegisterData(this);
    }

    /* Edit constructor */
    private EventData(EventDataList pList,
                      EventInfoType pType,
                      Event pEvent) {
        /* Initialise the item */
        super(pList, 0);

        /* Record the Detail */
        setValueInfoType(pType);
        setValueEvent(pEvent);

        /* Allocate the id */
        pList.setNewId(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The EventData to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is an EventData */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as an EventData */
        EventData myThat = (EventData) pThat;

        /* Compare the Events */
        iDiff = getEvent().compareTo(myThat.getEvent());
        if (iDiff != 0)
            return iDiff;

        /* Compare the Info Types */
        iDiff = getInfoType().compareTo(myThat.getInfoType());
        if (iDiff != 0)
            return iDiff;

        /* Compare the IDs */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;
        return 0;
    }

    /**
     * Rebuild Links to partner data
     * @param pData the DataSet
     */
    protected void reBuildLinks(FinanceData pData) {
        /* Update the Encryption details */
        super.reBuildLinks(pData);

        /* Access Events and InfoTypes */
        EventList myEvents = pData.getEvents();
        EventInfoTypeList myTypes = pData.getInfoTypes();

        /* Update to use the local copy of the Types */
        EventInfoType myType = getInfoType();
        EventInfoType myNewType = myTypes.searchFor(myType.getId());
        setValueInfoType(myNewType);

        /* Update to use the local copy of the Events */
        Event myEvent = getEvent();
        Event myNewEvt = myEvents.searchFor(myEvent.getId());
        setValueEvent(myNewEvt);
    }

    /**
     * Validate the Event Data
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
        } else if (!myType.getEnabled())
            addError("EventInfoType must be enabled", FIELD_INFOTYPE);
        else {
            /* Switch on Info Class */
            switch (myType.getInfoClass()) {
                case TaxCredit:
                case NatInsurance:
                case Benefit:
                    Money myMoney = getMoney();
                    if (myMoney == null)
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    else if (!myMoney.isPositive())
                        addError(myType.getName() + " must be positive", FIELD_VALUE);
                    break;
                case Dilution:
                    Dilution myDilution = getDilution();
                    if (myDilution == null)
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    else if (myDilution.outOfRange())
                        addError("Dilution factor value is outside allowed range (0-1)", FIELD_VALUE);
                    break;
                case CreditUnits:
                case DebitUnits:
                    Units myUnits = getUnits();
                    if (myUnits == null)
                        addError(myType.getName() + " must be non-null", FIELD_VALUE);
                    else if (!myUnits.isPositive())
                        addError(myType.getName() + " must be positive", FIELD_VALUE);
                    break;
            }
        }

        /* Set validation flag */
        if (!hasErrors())
            setValidEdit();
    }

    /**
     * Format an Event Data
     * @param pData the data to format
     * @return the formatted data
     */
    public static String format(EventData pData) {
        /* If we have null, return it */
        if ((pData == null) || (pData.getValueField() == null))
            return "null";

        /* Switch on type of Data */
        switch (pData.getInfoType().getInfoClass()) {
            case TaxCredit:
            case NatInsurance:
            case Benefit:
                return Money.format(pData.getMoney());
            case CreditUnits:
            case DebitUnits:
                return Units.format(pData.getUnits());
            case Dilution:
                return Dilution.format(pData.getDilution());
            default:
                return "null";
        }
    }

    /**
     * Set Money
     * @param pValue the Value
     * @throws JDataException
     */
    protected void setMoney(Money pValue) throws JDataException {
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
     * Set Units
     * @param pValue the Value
     * @throws JDataException
     */
    protected void setUnits(Units pValue) throws JDataException {
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
     * Set Dilution
     * @param pValue the Value
     * @throws JDataException
     */
    protected void setDilution(Dilution pValue) throws JDataException {
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

    /* List class */
    public static class EventDataList extends EncryptedList<EventDataList, EventData> {
        /* Access Extra Variables correctly */
        @Override
        public FinanceData getData() {
            return (FinanceData) super.getData();
        }

        /**
         * Construct an empty CORE rate list
         * @param pData the DataSet for the list
         */
        protected EventDataList(FinanceData pData) {
            super(EventDataList.class, EventData.class, pData);
        }

        /**
         * Construct an empty list
         * @param pData the DataSet for the list
         * @param pStyle the required style
         */
        protected EventDataList(FinanceData pData,
                                ListStyle pStyle) {
            super(EventDataList.class, EventData.class, pData);
            setStyle(pStyle);
            setGeneration(pData.getGeneration());
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private EventDataList(EventDataList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private EventDataList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            EventDataList myList = new EventDataList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public EventDataList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public EventDataList getEditList() {
            return getExtractList(ListStyle.EDIT);
        }

        @Override
        public EventDataList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public EventDataList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            EventDataList myList = new EventDataList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        /**
         * Construct a difference Info list
         * @param pOld the old Info list
         * @return the difference list
         */
        @Override
        protected EventDataList getDifferences(EventDataList pOld) {
            /* Build an empty Difference List */
            EventDataList myList = new EventDataList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Obtain the type of the item
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Allow an EventData to be added
         * @param uId
         * @param uControlId
         * @param uInfoTypeId
         * @param uEventId
         * @param pValue
         * @throws JDataException
         */
        public void addItem(int uId,
                            int uControlId,
                            int uInfoTypeId,
                            int uEventId,
                            byte[] pValue) throws JDataException {
            EventData myInfo;

            /* Create the info */
            myInfo = new EventData(this, uId, uControlId, uInfoTypeId, uEventId, pValue);

            /* Check that this DataId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, myInfo, "Duplicate DataId");

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, "Failed validation");

            /* Add to the list */
            add(myInfo);
        }

        /**
         * Add new item type (into edit session)
         * @param pType the Item Type
         * @param pEvent the Event
         * @return the new item
         */
        protected EventData addNewItem(EventInfoType pType,
                                       Event pEvent) {
            /* Create the new Data */
            EventData myData = new EventData(this, pType, pEvent);

            /* Add it to the list and return */
            add(myData);
            return myData;
        }

        @Override
        public EventData addNewItem(DataItem<?> pElement) {
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
