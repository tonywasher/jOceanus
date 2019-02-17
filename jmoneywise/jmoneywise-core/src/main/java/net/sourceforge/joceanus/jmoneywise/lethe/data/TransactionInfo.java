/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of an event.
 * @author Tony Washer
 */
public class TransactionInfo
        extends DataInfo<TransactionInfo, Transaction, TransactionInfoType, TransactionInfoClass, MoneyWiseDataType> {
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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected TransactionInfo(final TransactionInfoList pList,
                              final TransactionInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pTransaction the transaction
     * @param pType the type
     */
    private TransactionInfo(final TransactionInfoList pList,
                            final Transaction pTransaction,
                            final TransactionInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pTransaction);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private TransactionInfo(final TransactionInfoList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getTransInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getTransactions());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Resolve any link value */
            resolveLink();

            /* Access the TransactionInfoSet and register this data */
            final TransactionInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public TransactionInfoType getInfoType() {
        return getInfoType(getValueSet(), TransactionInfoType.class);
    }

    @Override
    public TransactionInfoClass getInfoClass() {
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
     * Obtain Transaction Tag.
     * @return the Transaction Tag
     */
    public TransactionTag getTransactionTag() {
        return getTransactionTag(getValueSet());
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the Money
     */
    public static TransactionInfoType getInfoType(final MetisValueSet pValueSet) {
        return getInfoType(pValueSet, TransactionInfoType.class);
    }

    /**
     * Obtain Linked Deposit.
     * @param pValueSet the valueSet
     * @return the Deposit
     */
    public static Deposit getDeposit(final MetisValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_LINK, Deposit.class);
    }

    /**
     * Obtain Linked TransactionTag.
     * @param pValueSet the valueSet
     * @return the TransactionTag
     */
    public static TransactionTag getTransactionTag(final MetisValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_LINK, TransactionTag.class);
    }

    @Override
    public String getLinkName() {
        final DataItem<?> myItem = getLink();
        if (myItem instanceof Deposit) {
            return ((Deposit) myItem).getName();
        }
        if (myItem instanceof Portfolio) {
            return ((Portfolio) myItem).getName();
        }
        if (myItem instanceof TransactionTag) {
            return ((TransactionTag) myItem).getName();
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

    @Override
    public void deRegister() {
        /* Access the TransactionInfoSet and register this value */
        final TransactionInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void rewindInfoLinkSet() {
        /* Access the TransactionInfoSet and reWind this value */
        final TransactionInfoSet mySet = getOwner().getInfoSet();
        mySet.rewindInfoLinkSet(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The EventInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<TransactionInfo, Transaction, TransactionInfoType, TransactionInfoClass, MoneyWiseDataType> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
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
        final TransactionInfoType myType = getInfoType();
        iDiff = myType.compareTo(pThat.getInfoType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* If this is a linkSet */
        if (myType.getInfoClass().isLinkSet()) {
            /* Compare names */
            iDiff = MetisDataDifference.compareObject(getLinkName(), pThat.getLinkName());
            if (iDiff != 0) {
                return iDiff;
            }
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getTransInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getTransactions());

        /* Resolve any link value */
        resolveLink();

        /* Access the TransactionInfoSet and register this data */
        final TransactionInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Resolve link reference.
     * @throws OceanusException on error
     */
    private void resolveLink() throws OceanusException {
        /* If we have a link */
        final TransactionInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            final MoneyWiseData myData = getDataSet();
            final MetisValueSet myValues = getValueSet();
            final Object myLinkId = myValues.getValue(FIELD_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case RETURNEDCASHACCOUNT:
                    resolveDataLink(FIELD_LINK, myData.getDeposits());
                    if (myLinkId == null) {
                        setValueValue(getDeposit().getId());
                    }
                    break;
                case TRANSTAG:
                    resolveDataLink(FIELD_LINK, myData.getTransactionTags());
                    if (myLinkId == null) {
                        setValueValue(getTransactionTag().getId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update transactionInfo from a transactionInfo extract.
     * @param pTransInfo the changed transInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pTransInfo) {
        /* Can only update from TransactionInfo */
        if (!(pTransInfo instanceof TransactionInfo)) {
            return false;
        }

        /* Access as TransactionInfo */
        final TransactionInfo myTransInfo = (TransactionInfo) pTransInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myTransInfo.getField())) {
            setValueValue(myTransInfo.getField());
            if (getInfoType().isLink()) {
                setValueLink(myTransInfo.getLink());
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
            case RETURNEDCASHACCOUNT:
                getDeposit().touchItem(getOwner());
                break;
            case TRANSTAG:
                getTransactionTag().touchItem(getOwner());
                break;
            default:
                break;
        }
    }

    /**
     * TransactionInfoList.
     */
    public static class TransactionInfoList
            extends DataInfoList<TransactionInfo, Transaction, TransactionInfoType, TransactionInfoClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<TransactionInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionInfoList.class);

        /**
         * Construct an empty CORE list.
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
        public MetisFieldSet<TransactionInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
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

        @Override
        public TransactionInfoList getEmptyList(final ListStyle pStyle) {
            final TransactionInfoList myList = new TransactionInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TransactionInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a TransactionInfo */
            if (!(pItem instanceof TransactionInfo)) {
                throw new UnsupportedOperationException();
            }

            final TransactionInfo myInfo = new TransactionInfo(this, (TransactionInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public TransactionInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected TransactionInfo addNewItem(final Transaction pOwner,
                                             final TransactionInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            final TransactionInfo myInfo = new TransactionInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Transaction pTransaction,
                                final TransactionInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            final MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            final TransactionInfoType myInfoType = myData.getTransInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new MoneyWiseDataException(pTransaction, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final DataValues<MoneyWiseDataType> myValues = new DataValues<>(TransactionInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pTransaction);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Transaction Info */
            final TransactionInfo myInfo = new TransactionInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Event Info to the list */
            add(myInfo);
        }

        @Override
        public TransactionInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the info */
            final TransactionInfo myInfo = new TransactionInfo(this, pValues);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myInfo);

            /* Return it */
            return myInfo;
        }

        /**
         * Resolve ValueLinks.
         * @throws OceanusException on error
         */
        public void resolveValueLinks() throws OceanusException {
            /* Loop through the Info items */
            final Iterator<TransactionInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TransactionInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink();
                }
            }
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the TransactionInfo */
            validateOnLoad();

            /* Validate the Transactions */
            final TransactionList myTrans = getDataSet().getTransactions();
            myTrans.validateOnLoad();
        }
    }
}
