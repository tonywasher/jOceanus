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

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a portfolio.
 * @author Tony Washer
 */
public class MoneyWisePortfolioInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.PORTFOLIOINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.PORTFOLIOINFO.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWisePortfolioInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioInfo.class);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWisePortfolioInfo(final MoneyWisePortfolioInfoList pList,
                                     final MoneyWisePortfolioInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pPortfolio the portfolio
     * @param pType the type
     */
    private MoneyWisePortfolioInfo(final MoneyWisePortfolioInfoList pList,
                                   final MoneyWisePortfolio pPortfolio,
                                   final MoneyWiseAccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pPortfolio);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWisePortfolioInfo(final MoneyWisePortfolioInfoList pList,
                                   final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getPortfolios());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Access the PortfolioInfoSet and register this data */
            final MoneyWisePortfolioInfoSet mySet = getOwner().getInfoSet();
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
    public MoneyWisePortfolio getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWisePortfolio.class);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWisePortfolioInfo getBase() {
        return (MoneyWisePortfolioInfo) super.getBase();
    }

    @Override
    public MoneyWisePortfolioInfoList getList() {
        return (MoneyWisePortfolioInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the PortfolioInfoSet and register this value */
        final MoneyWisePortfolioInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getPortfolios());

        /* Access the PortfolioInfoSet and register this data */
        final MoneyWisePortfolioInfoSet mySet = getOwner().getInfoSet();
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
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class));
    }

    /**
     * Update portfolioInfo from a portfolioInfo extract.
     * @param pInfo the changed portfolioInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pInfo) {
        /* Can only update from PortfolioInfo */
        if (!(pInfo instanceof MoneyWisePortfolioInfo)) {
            return false;
        }

        /* Access as PortfolioInfo */
        final MoneyWisePortfolioInfo myPortInfo = (MoneyWisePortfolioInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myPortInfo.getField())) {
            setValueValue(myPortInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * PortfolioInfoList.
     */
    public static class MoneyWisePortfolioInfoList
            extends PrometheusDataInfoList<MoneyWisePortfolioInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePortfolioInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioInfoList.class);

        /**
         * Construct an empty CORE info list.
         * @param pData the DataSet for the list
         */
        protected MoneyWisePortfolioInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWisePortfolioInfo.class, pData, MoneyWiseBasicDataType.PORTFOLIOINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWisePortfolioInfoList(final MoneyWisePortfolioInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWisePortfolioInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWisePortfolioInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final MoneyWisePortfolioInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected MoneyWisePortfolioInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWisePortfolioInfoList myList = new MoneyWisePortfolioInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWisePortfolioInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a PortfolioInfo */
            if (!(pItem instanceof MoneyWisePortfolioInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWisePortfolioInfo myInfo = new MoneyWisePortfolioInfo(this, (MoneyWisePortfolioInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWisePortfolioInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWisePortfolioInfo addNewItem(final PrometheusDataItem pOwner,
                                                    final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWisePortfolioInfo myInfo = new MoneyWisePortfolioInfo(this, (MoneyWisePortfolio) pOwner, (MoneyWiseAccountInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pPortfolio,
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
                throw new MoneyWiseDataException(pPortfolio, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pPortfolio);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Portfolio Info */
            final MoneyWisePortfolioInfo myInfo = new MoneyWisePortfolioInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWisePortfolioInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWisePortfolioInfo myInfo = new MoneyWisePortfolioInfo(this, pValues);

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
            /* Validate the PortfolioInfo */
            validateOnLoad();

            /* Map and Validate the Portfolios */
            final MoneyWisePortfolioList myPortfolios = getDataSet().getPortfolios();
            myPortfolios.mapData();
            myPortfolios.validateOnLoad();
        }
    }
}
