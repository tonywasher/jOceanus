/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
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
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Representation of an information extension of a loan.
 * @author Tony Washer
 */
public class MoneyWiseLoanInfo
        extends PrometheusDataInfoItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseBasicDataType.LOANINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseBasicDataType.LOANINFO.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseLoanInfo> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanInfo.class);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected MoneyWiseLoanInfo(final MoneyWiseLoanInfoList pList,
                                final MoneyWiseLoanInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pLoan the loan
     * @param pType the type
     */
    private MoneyWiseLoanInfo(final MoneyWiseLoanInfoList pList,
                              final MoneyWiseLoan pLoan,
                              final MoneyWiseAccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pLoan);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private MoneyWiseLoanInfo(final MoneyWiseLoanInfoList pList,
                              final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseDataSet myData = getDataSet();
            resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
            resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getLoans());

            /* Set the value */
            setValue(pValues.getValue(PrometheusDataResource.DATAINFO_VALUE));

            /* Access the LoanInfoSet and register this data */
            final MoneyWiseLoanInfoSet mySet = getOwner().getInfoSet();
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
    public MoneyWiseLoan getOwner() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_OWNER, MoneyWiseLoan.class);
    }

    @Override
    public MoneyWiseDataSet getDataSet() {
        return (MoneyWiseDataSet) super.getDataSet();
    }

    @Override
    public MoneyWiseLoanInfo getBase() {
        return (MoneyWiseLoanInfo) super.getBase();
    }

    @Override
    public MoneyWiseLoanInfoList getList() {
        return (MoneyWiseLoanInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the LoanInfoSet and register this value */
        final MoneyWiseLoanInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseDataSet myData = getDataSet();
        resolveDataLink(PrometheusDataResource.DATAINFO_TYPE, myData.getActInfoTypes());
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, myData.getLoans());

        /* Access the LoanInfoSet and register this data */
        final MoneyWiseLoanInfoSet mySet = getOwner().getInfoSet();
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
        resolveDataLink(PrometheusDataResource.DATAINFO_OWNER, pEditSet.getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class));
    }

    /**
     * Update depositInfo from a depositInfo extract.
     * @param pInfo the changed depositInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final PrometheusDataItem pInfo) {
        /* Can only update from LoanInfo */
        if (!(pInfo instanceof MoneyWiseLoanInfo)) {
            return false;
        }

        /* Access as LoanInfo */
        final MoneyWiseLoanInfo myLoanInfo = (MoneyWiseLoanInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myLoanInfo.getField())) {
            setValueValue(myLoanInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * LoanInfoList.
     */
    public static class MoneyWiseLoanInfoList
            extends PrometheusDataInfoList<MoneyWiseLoanInfo> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseLoanInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanInfoList.class);

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected MoneyWiseLoanInfoList(final MoneyWiseDataSet pData) {
            super(MoneyWiseLoanInfo.class, pData, MoneyWiseBasicDataType.LOANINFO, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseLoanInfoList(final MoneyWiseLoanInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseLoanInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseLoanInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final MoneyWiseLoanInfoList pBase) {
            /* Set the style and base */
            setStyle(PrometheusListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected MoneyWiseLoanInfoList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseLoanInfoList myList = new MoneyWiseLoanInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseLoanInfo addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a LoanInfo */
            if (!(pItem instanceof MoneyWiseLoanInfo)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseLoanInfo myInfo = new MoneyWiseLoanInfo(this, (MoneyWiseLoanInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public MoneyWiseLoanInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected MoneyWiseLoanInfo addNewItem(final PrometheusDataItem pOwner,
                                               final PrometheusStaticDataItem pInfoType) {
            /* Allocate the new entry and add to list */
            final MoneyWiseLoanInfo myInfo = new MoneyWiseLoanInfo(this, (MoneyWiseLoan) pOwner, (MoneyWiseAccountInfoType) pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final PrometheusDataItem pLoan,
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
                throw new MoneyWiseDataException(pLoan, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final PrometheusDataValues myValues = new PrometheusDataValues(OBJECT_NAME);
            myValues.addValue(MetisDataResource.DATA_ID, pId);
            myValues.addValue(PrometheusDataResource.DATAINFO_TYPE, myInfoType);
            myValues.addValue(PrometheusDataResource.DATAINFO_OWNER, pLoan);
            myValues.addValue(PrometheusDataResource.DATAINFO_VALUE, pValue);

            /* Create a new Loan Info */
            final MoneyWiseLoanInfo myInfo = new MoneyWiseLoanInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public MoneyWiseLoanInfo addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the info */
            final MoneyWiseLoanInfo myInfo = new MoneyWiseLoanInfo(this, pValues);

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
            /* Validate the LoanInfo */
            validateOnLoad();

            /* Map and Validate the Loans */
            final MoneyWiseLoanList myLoans = getDataSet().getLoans();
            myLoans.mapData();
            myLoans.validateOnLoad();
        }
    }
}
