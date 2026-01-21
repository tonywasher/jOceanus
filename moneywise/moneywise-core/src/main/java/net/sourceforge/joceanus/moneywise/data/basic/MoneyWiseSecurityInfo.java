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
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Representation of an information extension of a security.
 *
 * @author Tony Washer
 */
public class MoneyWiseSecurityInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.SECURITYINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.SECURITYINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseSecurityInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityInfo.class);

    /**
     * Copy Constructor.
     *
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWiseSecurityInfo(final MoneyWiseSecurityInfoList pList,
                                    final MoneyWiseSecurityInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     *
     * @param pList     the list
     * @param pSecurity the security
     * @param pType     the type
     */
    private MoneyWiseSecurityInfo(final MoneyWiseSecurityInfoList pList,
                                  final MoneyWiseSecurity pSecurity,
                                  final MoneyWiseAccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pSecurity);
    }

    /**
     * Values constructor.
     *
     * @param pList   the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseSecurityInfo(final MoneyWiseSecurityInfoList pList,
                                  final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getSecurities());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Resolve any link value */
            resolveLink(null);

            /* Access the SecurityInfoSet and register this data */
            final MoneyWiseSecurityInfoSet mySet = getOwner().getInfoSet();
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
    public MoneyWiseSecurity getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWiseSecurity.class);
    }

    /**
     * Obtain Region.
     *
     * @return the Region
     */
    public MoneyWiseRegion getRegion() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, MoneyWiseRegion.class);
    }

    /**
     * Obtain Security.
     *
     * @return the Security
     */
    public MoneyWiseSecurity getSecurity() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, MoneyWiseSecurity.class);
    }

    @Override
    public String getLinkName() {
        final PrometheusDataItem myItem = getLink();
        if (myItem instanceof MoneyWiseRegion myRegion) {
            return myRegion.getName();
        }
        if (myItem instanceof MoneyWiseSecurity mySecurity) {
            return mySecurity.getName();
        }
        return null;
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseSecurityInfo getBase() {
        return (MoneyWiseSecurityInfo) super.getBase();
    }

    @Override
    public MoneyWiseSecurityInfoList getList() {
        return (MoneyWiseSecurityInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the SecurityInfoSet and register this value */
        final MoneyWiseSecurityInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getSecurities());

        /* Resolve any link value */
        resolveLink(null);

        /* Access the SecurityInfoSet and register this data */
        final MoneyWiseSecurityInfoSet mySet = getOwner().getInfoSet();
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
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, pEditSet.getDataList(MoneyWiseStaticDataType.ACCOUNTINFOTYPE, MoneyWiseAccountInfoTypeList.class));
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class));

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
        final MoneyWiseAccountInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            final MoneyWiseDataSet myData = getDataSet();
            final Object myLinkId = getValues().getValue(PrometheusDataResource.DATAINFO_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case REGION:
                    resolveDataLink(PrometheusDataResource.DATAINFO_LINK, pEditSet == null
                            ? myData.getRegions()
                            : pEditSet.getDataList(MoneyWiseBasicDataType.REGION, MoneyWiseRegionList.class));
                    if (myLinkId == null) {
                        setValueValue(getRegion().getIndexedId());
                    }
                    break;
                case UNDERLYINGSTOCK:
                    resolveDataLink(PrometheusDataResource.DATAINFO_LINK, pEditSet == null
                            ? myData.getSecurities()
                            : pEditSet.getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class));
                    if (myLinkId == null) {
                        setValueValue(getSecurity().getIndexedId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update securityInfo from a securityInfo extract.
     *
     * @param pInfo the changed securityInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pInfo) {
        /* Can only update from SecurityInfo */
        if (!(pInfo instanceof MoneyWiseSecurityInfo)) {
            return false;
        }

        /* Access as SecurityInfo */
        final MoneyWiseSecurityInfo mySecInfo = (MoneyWiseSecurityInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), mySecInfo.getField())) {
            setValueValue(mySecInfo.getField());
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
            case UNDERLYINGSTOCK:
                getSecurity().touchItem(getOwner());
                break;
            case REGION:
                getRegion().touchItem(getOwner());
                break;
            default:
                break;
        }
    }

    /**
     * SecurityInfoList.
     */
    public static class MoneyWiseSecurityInfoList
            extends PrometheusDataInfoList<MoneyWiseSecurityInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseSecurityInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityInfoList.class);

        /**
         * Construct an empty CORE info list.
         *
         * @param pData the DataSet for the list
         */
        protected MoneyWiseSecurityInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWiseSecurityInfo.class, pData, MoneyWiseBasicDataType.SECURITYINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseSecurityInfoList(final MoneyWiseSecurityInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseSecurityInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseSecurityInfo.FIELD_DEFS;
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
        protected void setBase(final MoneyWiseSecurityInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected MoneyWiseSecurityInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseSecurityInfoList myList = new MoneyWiseSecurityInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseSecurityInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a SecurityInfo */
            if (!(pItem instanceof MoneyWiseSecurityInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseSecurityInfo myInfo = new MoneyWiseSecurityInfo(this, (MoneyWiseSecurityInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWiseSecurityInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWiseSecurityInfo addNewItem(final PrometheusDataItem pOwner,
                                                   final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWiseSecurityInfo myInfo = new MoneyWiseSecurityInfo(this, (MoneyWiseSecurity) pOwner, (MoneyWiseAccountInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pSecurity,
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
                throw new MoneyWiseDataException(pSecurity, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pSecurity);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Security Info */
            final MoneyWiseSecurityInfo myInfo = new MoneyWiseSecurityInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWiseSecurityInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWiseSecurityInfo myInfo = new MoneyWiseSecurityInfo(this, pValues);

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
            /* Validate the SecurityInfo */
            validateOnLoad();

            /* Map and Validate the Securities */
            final MoneyWiseSecurityList mySecurities = getDataSet().getSecurities();
            mySecurities.mapData();
            mySecurities.validateOnLoad();
        }
    }
}
