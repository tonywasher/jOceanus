/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
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
 * Representation of an information extension of an account.
 * @author Tony Washer
 */
public class MoneyWiseDepositInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.DEPOSITINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.DEPOSITINFO.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseDepositInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositInfo.class);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWiseDepositInfo(final MoneyWiseDepositInfoList pList,
                                   final MoneyWiseDepositInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pDeposit the deposit
     * @param pType the type
     */
    private MoneyWiseDepositInfo(final MoneyWiseDepositInfoList pList,
                                 final MoneyWiseDeposit pDeposit,
                                 final MoneyWiseAccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pDeposit);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseDepositInfo(final MoneyWiseDepositInfoList pList,
                                 final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getDeposits());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Access the DepositInfoSet and register this data */
            final MoneyWiseDepositInfoSet mySet = getOwner().getInfoSet();
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
    public MoneyWiseDeposit getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWiseDeposit.class);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseDepositInfo getBase() {
        return (MoneyWiseDepositInfo) super.getBase();
    }

    @Override
    public MoneyWiseDepositInfoList getList() {
        return (MoneyWiseDepositInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the DepositInfoSet and register this value */
        final MoneyWiseDepositInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getDeposits());

        /* Access the DepositInfoSet and register this data */
        final MoneyWiseDepositInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Resolve editSet links.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    public void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Resolve data links */
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class));
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class));
    }

    /**
     * Update depositInfo from a depositInfo extract.
     * @param pInfo the changed depositInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pInfo) {
        /* Can only update from DepositInfo */
        if (!(pInfo instanceof MoneyWiseDepositInfo)) {
            return false;
        }

        /* Access as DepositInfo */
        final MoneyWiseDepositInfo myDepInfo = (MoneyWiseDepositInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myDepInfo.getField())) {
            setValueValue(myDepInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * DepositInfoList.
     */
    public static class MoneyWiseDepositInfoList
            extends PrometheusDataInfoList<MoneyWiseDepositInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositInfoList.class);

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseDepositInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWiseDepositInfo.class, pData, MoneyWiseBasicDataType.DEPOSITINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseDepositInfoList(final MoneyWiseDepositInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseDepositInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final MoneyWiseDepositInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected MoneyWiseDepositInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseDepositInfoList myList = new MoneyWiseDepositInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseDepositInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a DepositInfo */
            if (!(pItem instanceof MoneyWiseDepositInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseDepositInfo myInfo = new MoneyWiseDepositInfo(this, (MoneyWiseDepositInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWiseDepositInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWiseDepositInfo addNewItem(final PrometheusDataItem pOwner,
                                                  final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWiseDepositInfo myInfo = new MoneyWiseDepositInfo(this, (MoneyWiseDeposit) pOwner, (MoneyWiseAccountInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pDeposit,
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
                throw new MoneyWiseDataException(pDeposit, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseDepositInfo.OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pDeposit);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Deposit Info */
            final MoneyWiseDepositInfo myInfo = new MoneyWiseDepositInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWiseDepositInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWiseDepositInfo myInfo = new MoneyWiseDepositInfo(this, pValues);

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

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the DepositInfo */
            validateOnLoad();

            /* Map and Validate the Deposits */
            final MoneyWiseDepositList myDeposits = getDataSet().getDeposits();
            myDeposits.mapData();
            myDeposits.validateOnLoad();
        }
    }
}
