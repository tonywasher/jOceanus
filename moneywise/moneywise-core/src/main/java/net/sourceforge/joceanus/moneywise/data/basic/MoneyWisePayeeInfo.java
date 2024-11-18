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
package net.sourceforge.joceanus.moneywise.data.basic;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a payee.
 * @author Tony Washer
 */
public class MoneyWisePayeeInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.PAYEEINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.PAYEEINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWisePayeeInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeInfo.class);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWisePayeeInfo(final MoneyWisePayeeInfoList pList,
                                 final MoneyWisePayeeInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pPayee the payee
     * @param pType the type
     */
    private MoneyWisePayeeInfo(final MoneyWisePayeeInfoList pList,
                               final MoneyWisePayee pPayee,
                               final MoneyWiseAccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pPayee);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWisePayeeInfo(final MoneyWisePayeeInfoList pList,
                               final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getPayees());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Access the PayeeInfoSet and register this data */
            final MoneyWisePayeeInfoSet mySet = getOwner().getInfoSet();
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
    public MoneyWisePayee getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWisePayee.class);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWisePayeeInfo getBase() {
        return (MoneyWisePayeeInfo) super.getBase();
    }

    @Override
    public MoneyWisePayeeInfoList getList() {
        return (MoneyWisePayeeInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the PayeeInfoSet and register this value */
        final MoneyWisePayeeInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getPayees());

        /* Access the PayeeInfoSet and register this data */
        final MoneyWisePayeeInfoSet mySet = getOwner().getInfoSet();
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
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class));
    }

    /**
     * Update payeeInfo from a payeeInfo extract.
     * @param pInfo the changed payeeInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pInfo) {
        /* Can only update from PayeeInfo */
        if (!(pInfo instanceof MoneyWisePayeeInfo)) {
            return false;
        }

        /* Access as PayeeInfo */
        final MoneyWisePayeeInfo myPayeeInfo = (MoneyWisePayeeInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myPayeeInfo.getField())) {
            setValueValue(myPayeeInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * PayeeInfoList.
     */
    public static class MoneyWisePayeeInfoList
            extends PrometheusDataInfoList<MoneyWisePayeeInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePayeeInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeInfoList.class);

        /**
         * Construct an empty CORE info list.
         * @param pData the DataSet for the list
         */
        protected MoneyWisePayeeInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWisePayeeInfo.class, pData, MoneyWiseBasicDataType.PAYEEINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWisePayeeInfoList(final MoneyWisePayeeInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWisePayeeInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWisePayeeInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final MoneyWisePayeeInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected MoneyWisePayeeInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWisePayeeInfoList myList = new MoneyWisePayeeInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWisePayeeInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a PayeeInfo */
            if (!(pItem instanceof MoneyWisePayeeInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWisePayeeInfo myInfo = new MoneyWisePayeeInfo(this, (MoneyWisePayeeInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWisePayeeInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWisePayeeInfo addNewItem(final PrometheusDataItem pOwner,
                                                final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWisePayeeInfo myInfo = new MoneyWisePayeeInfo(this, (MoneyWisePayee) pOwner, (MoneyWiseAccountInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pPayee,
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
                throw new MoneyWiseDataException(pPayee, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pPayee);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Payee Info */
            final MoneyWisePayeeInfo myInfo = new MoneyWisePayeeInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWisePayeeInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWisePayeeInfo myInfo = new MoneyWisePayeeInfo(this, pValues);

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
            /* Validate the PayeeInfo */
            validateOnLoad();

            /* Map and Validate the Payees */
            final MoneyWisePayeeList myPayees = getDataSet().getPayees();
            myPayees.mapData();
            myPayees.validateOnLoad();
        }
    }
}
