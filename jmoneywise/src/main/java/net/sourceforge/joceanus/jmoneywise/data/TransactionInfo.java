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
public class TransactionInfo
        extends DataInfo<TransactionInfo, Transaction, EventInfoType, EventInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSACTIONINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSACTIONINFO.getListName();

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
    public Transaction getOwner() {
        return getOwner(getValueSet(), Transaction.class);
    }

    /**
     * Obtain Deposit.
     * @return the Deposit
     */
    public Deposit getDeposit() {
        return getDeposit(getValueSet());
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
    public static Deposit getDeposit(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, Deposit.class);
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
        if (myItem instanceof Deposit) {
            return ((Deposit) myItem).getName();
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
    public TransactionInfo getBase() {
        return (TransactionInfo) super.getBase();
    }

    @Override
    public TransactionInfoList getList() {
        return (TransactionInfoList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected TransactionInfo(final TransactionInfoList pList,
                              final TransactionInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
        setControlKey(pList.getControlKey());
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pTransaction the transaction
     * @param pType the type
     */
    private TransactionInfo(final TransactionInfoList pList,
                            final Transaction pTransaction,
                            final EventInfoType pType) {
        /* Initialise the item */
        super(pList);
        setControlKey(pList.getControlKey());

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pTransaction);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private TransactionInfo(final TransactionInfoList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getEventInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getTransactions());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the TransactionInfoSet and register this data */
            TransactionInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the TransactionInfoSet and register this value */
        TransactionInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The EventInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final TransactionInfo pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Transactions */
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
        resolveDataLink(FIELD_OWNER, myData.getTransactions());

        /* Resolve any link value */
        resolveLink();

        /* Access the TransactionInfoSet and register this data */
        TransactionInfoSet mySet = getOwner().getInfoSet();
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
                    resolveDataLink(FIELD_LINK, myData.getDeposits());
                    if (myLinkId == null) {
                        setValueValue(getDeposit().getId());
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
                getDeposit().touchItem(getOwner());
                break;
            case EVENTTAG:
                getEventTag().touchItem(getOwner());
                break;
            default:
                break;
        }
    }

    /**
     * TransactionInfoList.
     */
    public static class TransactionInfoList
            extends DataInfoList<TransactionInfo, Transaction, EventInfoType, EventInfoClass, MoneyWiseDataType> {
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
            return TransactionInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final TransactionInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected TransactionInfoList(final MoneyWiseData pData) {
            super(TransactionInfo.class, pData, MoneyWiseDataType.TRANSACTIONINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TransactionInfoList(final TransactionInfoList pSource) {
            super(pSource);
        }

        @Override
        public TransactionInfoList getEmptyList(final ListStyle pStyle) {
            TransactionInfoList myList = new TransactionInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TransactionInfoList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (TransactionInfoList) super.cloneList(pDataSet);
        }

        @Override
        public TransactionInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a TransactionInfo */
            if (!(pItem instanceof TransactionInfo)) {
                throw new UnsupportedOperationException();
            }

            TransactionInfo myInfo = new TransactionInfo(this, (TransactionInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public TransactionInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected TransactionInfo addNewItem(final Transaction pOwner,
                                             final EventInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            TransactionInfo myInfo = new TransactionInfo(this, pOwner, pInfoType);
            append(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Transaction pTransaction,
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
                throw new JMoneyWiseDataException(pTransaction, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(TransactionInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pTransaction);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Transaction Info */
            TransactionInfo myInfo = new TransactionInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Event Info to the list */
            append(myInfo);
        }

        @Override
        public TransactionInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the info */
            TransactionInfo myInfo = new TransactionInfo(this, pValues);

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
            Iterator<TransactionInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                TransactionInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink();
                }
            }
        }
    }
}
