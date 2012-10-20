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
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataInfo;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDecimal.JDecimalParser;
import net.sourceforge.JDecimal.JDilution;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JUnits;
import net.sourceforge.JFinanceApp.data.AccountNew.AccountNewList;
import net.sourceforge.JFinanceApp.data.EventNew.EventNewList;
import net.sourceforge.JFinanceApp.data.statics.EventInfoClass;
import net.sourceforge.JFinanceApp.data.statics.EventInfoType;
import net.sourceforge.JFinanceApp.data.statics.EventInfoType.EventInfoTypeList;

/**
 * Representation of an information extension of an event.
 * @author Tony Washer
 */
public class EventInfo extends DataInfo<EventInfo, EventNew, EventInfoType> implements Comparable<EventInfo> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventInfo.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventInfo.class.getSimpleName(),
            DataInfo.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField("Account");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if ((FIELD_ACCOUNT.equals(pField)) && !getInfoType().isLink()) {
            return JDataFieldValue.SkipField;
        }
        if ((FIELD_VALUE.equals(pField)) && getInfoType().isLink()) {
            return JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public EventInfoType getInfoType() {
        return getInfoType(getValueSet(), EventInfoType.class);
    }

    /**
     * Obtain Event.
     * @return the Event
     */
    public EventNew getEvent() {
        return getOwner(getValueSet(), EventNew.class);
    }

    /**
     * Obtain Account.
     * @return the Account
     */
    public AccountNew getAccount() {
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
     * Obtain Event.
     * @param pValueSet the valueSet
     * @return the Event
     */
    public static EventNew getEvent(final ValueSet pValueSet) {
        return getOwner(pValueSet, EventNew.class);
    }

    /**
     * Obtain Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static AccountNew getAccount(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, AccountNew.class);
    }

    /**
     * Set Account.
     * @param pAccount the account
     */
    private void setValueAccount(final AccountNew pAccount) {
        getValueSet().setValue(FIELD_ACCOUNT, pAccount);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public EventInfo getBase() {
        return (EventInfo) super.getBase();
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
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pEvent the event
     * @param pType the type
     */
    private EventInfo(final EventInfoList pList,
                      final EventNew pEvent,
                      final EventInfoType pType) {
        /* Initialise the item */
        super(pList);

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pEvent);
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uEventId the Event id
     * @param pValue the value
     * @throws JDataException on error
     */
    private EventInfo(final EventInfoList pList,
                      final Integer uId,
                      final Integer uControlId,
                      final Integer uInfoTypeId,
                      final Integer uEventId,
                      final byte[] pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, uControlId, uInfoTypeId, uEventId);

        /* Protect against exceptions */
        try {
            /* Look up the EventType */
            FinanceData myData = getDataSet();
            EventInfoTypeList myTypes = myData.getEventInfoTypes();
            EventInfoType myType = myTypes.findItemById(uInfoTypeId);
            if (myType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid EventInfoType Id");
            }
            setValueInfoType(myType);

            /* Look up the Event */
            EventNewList myEvents = myData.getNewEvents();
            EventNew myEvent = myEvents.findItemById(uEventId);
            if (myEvent == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Event Id");
            }
            setValueOwner(myEvent);

            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case INTEGER:
                    setValueBytes(pValue, Integer.class);
                    if (myType.isLink()) {
                        AccountNewList myAccounts = myData.getNewAccounts();
                        AccountNew myAccount = myAccounts.findItemById(getValue(Integer.class));
                        if (myAccount == null) {
                            throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
                        }
                        setValueAccount(myAccount);
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
                default:
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Data Type");
            }

            /* Access the EventInfoSet and register this data */
            // EventInfoSet mySet = myEvent.getInfoSet();
            // mySet.registerData(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param uId the id
     * @param pInfoType the info type
     * @param pEvent the Event
     * @param pValue the value
     * @throws JDataException on error
     */
    private EventInfo(final EventInfoList pList,
                      final Integer uId,
                      final EventInfoType pInfoType,
                      final EventNew pEvent,
                      final Object pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, pInfoType, pEvent);

        /* Protect against exceptions */
        try {
            /* Set the value */
            setValue(pValue);

            /* Access the EventInfoSet and register this data */
            // EventInfoSet mySet = myEvent.getInfoSet();
            // mySet.registerData(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    // @Override
    // public void deRegister() {
    /* Access the EventInfoSet and register this value */
    // EventInfoSet mySet = getEvent().getInfoSet();
    // mySet.deRegisterData(this);
    // }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The EventInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
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
        EventNewList myEvents = myData.getNewEvents();
        EventInfoTypeList myTypes = myData.getEventInfoTypes();

        /* Update to use the local copy of the Types */
        EventInfoType myType = getInfoType();
        EventInfoType myNewType = myTypes.findItemById(myType.getId());
        setValueInfoType(myNewType);

        /* If we are using an account */
        if (myType.isLink()) {
            /* Update to use the local copy of the accounts */
            AccountNewList myAccounts = myData.getNewAccounts();
            AccountNew myAccount = getAccount();
            AccountNew myNewAct = myAccounts.findItemById(myAccount.getId());
            setValueAccount(myNewAct);
        }

        /* Update to use the local copy of the Events */
        EventNew myEvent = getEvent();
        EventNew myNewEvent = myEvents.findItemById(myEvent.getId());
        setValueOwner(myNewEvent);
    }

    @Override
    public String formatObject() {
        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on type of Data */
        EventInfoType myType = getInfoType();
        switch (myType.getDataType()) {
            case INTEGER:
                return myFormatter.formatObject(myType.isLink() ? getAccount() : getValue(Integer.class));
            case MONEY:
                return myFormatter.formatObject(getValue(JMoney.class));
            case UNITS:
                return myFormatter.formatObject(getValue(JUnits.class));
            case DILUTION:
                return myFormatter.formatObject(getValue(JDilution.class));
            default:
                return "null";
        }
    }

    /**
     * Set Value.
     * @param pValue the Value
     * @throws JDataException on error
     */
    @Override
    protected void setValue(final Object pValue) throws JDataException {
        /* Access the info Type */
        EventInfoType myType = getInfoType();

        /* Access the DataSet and parser */
        FinanceData myDataSet = getDataSet();
        JDataFormatter myFormatter = myDataSet.getDataFormatter();
        JDecimalParser myParser = myFormatter.getDecimalParser();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myType.getDataType()) {
            case INTEGER:
                if ((pValue instanceof Integer) && (!myType.isLink())) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                if ((pValue instanceof AccountNew) && (myType.isLink())) {
                    AccountNew myAccount = (AccountNew) pValue;
                    setValueValue(myAccount.getId());
                    setValueAccount(myAccount);
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
            default:
                break;
        }

        /* Reject invalid value */
        if (!bValueOK) {
            throw new JDataException(ExceptionClass.DATA, this, "Invalid Data Type");
        }
    }

    /**
     * EventInfoList.
     */
    public static class EventInfoList extends DataInfoList<EventInfo, EventNew, EventInfoType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventInfoList.class.getSimpleName(),
                DataInfoList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected EventInfoList(final FinanceData pData) {
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
        protected EventInfoList getEmptyList() {
            return new EventInfoList(this);
        }

        @Override
        public EventInfoList cloneList(final DataSet<?> pDataSet) {
            return (EventInfoList) super.cloneList(pDataSet);
        }

        @Override
        public EventInfoList deriveList(final ListStyle pStyle) {
            return (EventInfoList) super.deriveList(pStyle);
        }

        @Override
        public EventInfoList deriveDifferences(final DataList<EventInfo> pOld) {
            return (EventInfoList) super.deriveDifferences(pOld);
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
        protected EventInfo addNewItem(final EventNew pOwner,
                                       final EventInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            EventInfo myInfo = new EventInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        /**
         * Allow an EventInfo to be added.
         * @param uId the id
         * @param uControlId the control id
         * @param uInfoTypeId the info type id
         * @param uEventId the event id
         * @param pValue the data
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Integer uInfoTypeId,
                                  final Integer uEventId,
                                  final byte[] pValue) throws JDataException {
            /* Create the info */
            EventInfo myInfo = new EventInfo(this, uId, uControlId, uInfoTypeId, uEventId, pValue);

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
            append(myInfo);
        }

        /**
         * Add an EventInfo to the list.
         * @param uId the Id of the info
         * @param pEvent the event
         * @param pInfoClass the Class of the event info type
         * @param pValue the value of the event info
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final EventNew pEvent,
                                final EventInfoClass pInfoClass,
                                final Object pValue) throws JDataException {
            /* Access the data set */
            FinanceData myData = getDataSet();

            /* Look up the Info Type */
            EventInfoType myInfoType = myData.getEventInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JDataException(ExceptionClass.DATA, pEvent, "Event has invalid Event Info Class ["
                        + pInfoClass + "]");
            }

            /* Create a new Event Info */
            EventInfo myInfo = new EventInfo(this, uId, myInfoType, pEvent, pValue);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                throw new JDataException(ExceptionClass.DATA, myInfo, "Duplicate EventInfoId");
            }

            /* Add the Event Info to the list */
            append(myInfo);

            /* Validate the Info */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, "Failed validation");
            }
        }

    }
}
