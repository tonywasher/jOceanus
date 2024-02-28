/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.basic;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a cash account.
 * @author Tony Washer
 */
public class MoneyWiseCashInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.CASHINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.CASHINFO.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseCashInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashInfo.class);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWiseCashInfo(final MoneyWiseCashInfoList pList,
                                final MoneyWiseCashInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pCash the cash
     * @param pType the type
     */
    private MoneyWiseCashInfo(final MoneyWiseCashInfoList pList,
                              final MoneyWiseCash pCash,
                              final MoneyWiseAccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pCash);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseCashInfo(final MoneyWiseCashInfoList pList,
                              final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getCash());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Resolve any link value */
            resolveLink(null);

            /* Access the CashInfoSet and register this data */
            final MoneyWiseCashInfoSet mySet = getOwner().getInfoSet();
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
    public MoneyWiseAccountInfoType getInfoType() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_TYPE, MoneyWiseAccountInfoType.class);
    }

    @Override
    public MoneyWiseAccountInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    @Override
    public MoneyWiseCash getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWiseCash.class);
    }

    /**
     * Obtain Payee.
     * @return the Payee
     */
    public MoneyWisePayee getPayee() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, MoneyWisePayee.class);
    }

    /**
     * Obtain EventCategory.
     * @return the EventCategory
     */
    public MoneyWiseTransCategory getEventCategory() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, MoneyWiseTransCategory.class);
    }

    @Override
    public String getLinkName() {
        final PrometheusDataItem myItem = getLink();
        if (myItem instanceof MoneyWisePayee) {
            return ((MoneyWisePayee) myItem).getName();
        }
        if (myItem instanceof MoneyWiseTransCategory) {
            return ((MoneyWiseTransCategory) myItem).getName();
        }
        return null;
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseCashInfo getBase() {
        return (MoneyWiseCashInfo) super.getBase();
    }

    @Override
    public MoneyWiseCashInfoList getList() {
        return (MoneyWiseCashInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the CashInfoSet and register this value */
        final MoneyWiseCashInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getCash());

        /* Resolve any link value */
        resolveLink(null);

        /* Access the CashInfoSet and register this data */
        final MoneyWiseCashInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * resolve editSet links.
     * @param pEditSet the edit set
     * @throws OceanusException on error
     */
    public void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Resolve data links */
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class));
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class));

        /* Resolve any link value */
        resolveLink(pEditSet);
    }

    /**
     * Resolve link reference.
     * @param pEditSet the edit set
     * @throws OceanusException on error
     */
    private void resolveLink(final PrometheusEditSet pEditSet) throws OceanusException {
        /* If we have a link */
        final MoneyWiseAccountInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            final MoneyWiseDataSet myData = getDataSet();
            final Object myLinkId = getValues().getValue(PrometheusDataResource.DATAINFO_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case AUTOPAYEE:
                    resolveDataLink(PrometheusDataResource.DATAINFO_LINK, pEditSet == null
                            ? myData.getPayees()
                            : pEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class));
                    if (myLinkId == null) {
                        setValueValue(getPayee().getIndexedId());
                    }
                    break;
                case AUTOEXPENSE:
                    resolveDataLink(PrometheusDataResource.DATAINFO_LINK, pEditSet == null
                            ? myData.getTransCategories()
                            : pEditSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class));
                    if (myLinkId == null) {
                        setValueValue(getEventCategory().getIndexedId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update loanInfo from a loanInfo extract.
     * @param pInfo the changed loanInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pInfo) {
        /* Can only update from CashInfo */
        if (!(pInfo instanceof MoneyWiseCashInfo)) {
            return false;
        }

        /* Access as CashInfo */
        final MoneyWiseCashInfo myInfo = (MoneyWiseCashInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myInfo.getField())) {
            setValueValue(myInfo.getField());
            if (getInfoType().isLink()) {
                setValueLink(myInfo.getLink());
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
            case AUTOPAYEE:
                getPayee().touchItem(this);
                break;
            case AUTOEXPENSE:
                getEventCategory().touchItem(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void touchOnUpdate() {
        /* Switch on info class */
        switch (getInfoClass()) {
            case AUTOPAYEE:
                getPayee().touchItem(this);
                break;
            default:
                break;
        }
    }

    /**
     * CashInfoList.
     */
    public static class MoneyWiseCashInfoList
            extends PrometheusDataInfoList<MoneyWiseCashInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCashInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashInfoList.class);

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseCashInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWiseCashInfo.class, pData, MoneyWiseBasicDataType.CASHINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseCashInfoList(final MoneyWiseCashInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseCashInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseCashInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final MoneyWiseCashInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected MoneyWiseCashInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseCashInfoList myList = new MoneyWiseCashInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseCashInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a CashInfo */
            if (!(pItem instanceof MoneyWiseCashInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseCashInfo myInfo = new MoneyWiseCashInfo(this, (MoneyWiseCashInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWiseCashInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWiseCashInfo addNewItem(final PrometheusDataItem pOwner,
                                               final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWiseCashInfo myInfo = new MoneyWiseCashInfo(this, (MoneyWiseCash) pOwner, (MoneyWiseAccountInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pCash,
                                final PrometheusDataInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            final MoneyWiseDataSet myData = getDataSet();

            /* Look up the Info Type */
            final MoneyWiseAccountInfoType myInfoType = myData.getActInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new MoneyWiseDataException(pCash, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseCashInfo.OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pCash);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Cash Info */
            final MoneyWiseCashInfo myInfo = new MoneyWiseCashInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWiseCashInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWiseCashInfo myInfo = new MoneyWiseCashInfo(this, pValues);

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
         * @throws OceanusException on error
         */
        public void resolveValueLinks() throws OceanusException {
            /* Loop through the Info items */
            final Iterator<MoneyWiseCashInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseCashInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink(null);
                }
            }
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the CashInfo */
            validateOnLoad();

            /* Map and Validate the Cash */
            final MoneyWiseCashList myCash = getDataSet().getCash();
            myCash.mapData();
            myCash.validateOnLoad();
        }
    }
}
