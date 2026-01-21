/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataDifference;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag.MoneyWiseTransTagList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Representation of an information extension of an event.
 *
 * @author Tony Washer
 */
public class MoneyWiseTransInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.TRANSACTIONINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.TRANSACTIONINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTransInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransInfo.class);

    /**
     * Copy Constructor.
     *
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWiseTransInfo(final MoneyWiseTransInfoList pList,
                                 final MoneyWiseTransInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     *
     * @param pList        the list
     * @param pTransaction the transaction
     * @param pType        the type
     */
    private MoneyWiseTransInfo(final MoneyWiseTransInfoList pList,
                               final MoneyWiseTransaction pTransaction,
                               final MoneyWiseTransInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pTransaction);
    }

    /**
     * Values constructor.
     *
     * @param pList   the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseTransInfo(final MoneyWiseTransInfoList pList,
                               final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getTransInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getTransactions());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Resolve any link value */
            resolveLink(null);

            /* Access the TransactionInfoSet and register this data */
            final MoneyWiseTransInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MoneyWiseTransInfoType getInfoType() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_TYPE, MoneyWiseTransInfoType.class);
    }

    @Override
    public MoneyWiseTransInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    @Override
    public MoneyWiseTransaction getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWiseTransaction.class);
    }

    /**
     * Obtain Deposit.
     *
     * @return the Deposit
     */
    public MoneyWiseTransAsset getTransAsset() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, MoneyWiseTransAsset.class);
    }

    /**
     * Obtain Transaction Tag.
     *
     * @return the Transaction Tag
     */
    public MoneyWiseTransTag getTransactionTag() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, MoneyWiseTransTag.class);
    }

    @Override
    public String getLinkName() {
        final PrometheusDataItem myItem = getLink();
        if (myItem instanceof MoneyWiseDeposit myDeposit) {
            return myDeposit.getName();
        }
        if (myItem instanceof MoneyWisePortfolio myPortfolio) {
            return myPortfolio.getName();
        }
        if (myItem instanceof MoneyWiseTransTag myTag) {
            return myTag.getName();
        }
        return null;
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseTransInfo getBase() {
        return (MoneyWiseTransInfo) super.getBase();
    }

    @Override
    public MoneyWiseTransInfoList getList() {
        return (MoneyWiseTransInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the TransactionInfoSet and register this value */
        final MoneyWiseTransInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void rewindInfoLinkSet() {
        /* Access the TransactionInfoSet and reWind this value */
        final MoneyWiseTransInfoSet mySet = getOwner().getInfoSet();
        mySet.rewindInfoLinkSet(this);
    }

    /**
     * Compare this data to another to establish sort order.
     *
     * @param pThat The EventInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareValues(final PrometheusDataItem pThat) {
        /* Access item */
        final MoneyWiseTransInfo myThat = (MoneyWiseTransInfo) pThat;

        /* Compare basic details */
        int iDiff = super.compareValues(pThat);
        if (iDiff != 0) {
            return iDiff;
        }

        /* If this is a linkSet */
        final MoneyWiseTransInfoType myType = myThat.getInfoType();
        if (myType.getInfoClass().isLinkSet()) {
            /* Compare names */
            iDiff = MetisDataDifference.compareObject(getLinkName(), myThat.getLinkName());
        }
        return iDiff;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getTransInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getTransactions());

        /* Resolve any link value */
        resolveLink(null);

        /* Access the TransactionInfoSet and register this data */
        final MoneyWiseTransInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * resolve editSet links.
     *
     * @param pEditSet the edit set
     * @throws OceanusException on error
     */
    public void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, pEditSet.getDataList(MoneyWiseStaticDataType.TRANSINFOTYPE, MoneyWiseTransInfoTypeList.class));
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class));

        /* Resolve any link value */
        resolveLink(pEditSet);
    }

    /**
     * Resolve link reference.
     *
     * @param pEditSet the edit set
     * @throws OceanusException on error
     */
    private void resolveLink(final PrometheusEditSet pEditSet) throws OceanusException {
        /* If we have a link */
        final MoneyWiseTransInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            final MoneyWiseDataSet myData = getDataSet();
            final Object myLinkId = getValues().getValue(PrometheusDataResource.DATAINFO_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case RETURNEDCASHACCOUNT:
                    getOwner().resolveTransactionAsset(pEditSet == null ? getDataSet() : pEditSet, this, PrometheusDataResource.DATAINFO_LINK);
                    if (myLinkId == null) {
                        setValueValue(getTransAsset().getExternalId());
                    }
                    break;
                case TRANSTAG:
                    resolveDataLink(PrometheusDataResource.DATAINFO_LINK, pEditSet == null
                            ? myData.getTransactionTags()
                            : pEditSet.getDataList(MoneyWiseBasicDataType.TRANSTAG, MoneyWiseTransTagList.class));
                    if (myLinkId == null) {
                        setValueValue(getTransactionTag().getIndexedId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update transactionInfo from a transactionInfo extract.
     *
     * @param pTransInfo the changed transInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pTransInfo) {
        /* Can only update from TransactionInfo */
        if (!(pTransInfo instanceof MoneyWiseTransInfo)) {
            return false;
        }

        /* Access as TransactionInfo */
        final MoneyWiseTransInfo myTransInfo = (MoneyWiseTransInfo) pTransInfo;

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
                getTransAsset().touchItem(getOwner());
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
    public static class MoneyWiseTransInfoList
            extends PrometheusDataInfoList<MoneyWiseTransInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransInfoList.class);

        /**
         * Construct an empty CORE list.
         *
         * @param pData the DataSet for the list
         */
        protected MoneyWiseTransInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWiseTransInfo.class, pData, MoneyWiseBasicDataType.TRANSACTIONINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseTransInfoList(final MoneyWiseTransInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTransInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTransInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         *
         * @param pBase the base list
         */
        protected void setBase(final MoneyWiseTransInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        public MoneyWiseTransInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTransInfoList myList = new MoneyWiseTransInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseTransInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a TransactionInfo */
            if (!(pItem instanceof MoneyWiseTransInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseTransInfo myInfo = new MoneyWiseTransInfo(this, (MoneyWiseTransInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWiseTransInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWiseTransInfo addNewItem(final PrometheusDataItem pOwner,
                                                final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWiseTransInfo myInfo = new MoneyWiseTransInfo(this, (MoneyWiseTransaction) pOwner, (MoneyWiseTransInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pTransaction,
                                final PrometheusDataInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            final MoneyWiseDataSet myData = getDataSet();

            /* Look up the Info Type */
            final MoneyWiseTransInfoType myInfoType = myData.getTransInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new MoneyWiseDataException(pTransaction, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pTransaction);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Transaction Info */
            final MoneyWiseTransInfo myInfo = new MoneyWiseTransInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getIndexedId())) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Event Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWiseTransInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWiseTransInfo myInfo = new MoneyWiseTransInfo(this, pValues);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myInfo.getIndexedId())) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myInfo);

            /* Return it */
            return myInfo;
        }

        /**
         * Resolve ValueLinks.
         *
         * @throws OceanusException on error
         */
        public void resolveValueLinks() throws OceanusException {
            /* Loop through the Info items */
            final Iterator<MoneyWiseTransInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseTransInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink(null);
                }
            }
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the TransactionInfo */
            validateOnLoad();

            /* Validate the Transactions */
            final MoneyWiseTransactionList myTrans = getDataSet().getTransactions();
            myTrans.validateOnLoad();
        }
    }
}
