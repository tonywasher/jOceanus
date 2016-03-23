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

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a TaxYear.
 * @author Tony Washer
 */
public class TaxYearInfo
        extends DataInfo<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TAXYEARINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TAXYEARINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected TaxYearInfo(final TaxInfoList pList,
                          final TaxYearInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pTaxYear the taxYear
     * @param pType the type
     */
    private TaxYearInfo(final TaxInfoList pList,
                        final TaxYear pTaxYear,
                        final TaxYearInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pTaxYear);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private TaxYearInfo(final TaxInfoList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getTaxInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getTaxYears());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the TaxInfoSet and register this data */
            TaxYearInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (OceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public TaxYearInfoType getInfoType() {
        return getInfoType(getValueSet(), TaxYearInfoType.class);
    }

    @Override
    public TaxYearInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    @Override
    public TaxYear getOwner() {
        return getOwner(getValueSet(), TaxYear.class);
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the Money
     */
    public static TaxYearInfoType getInfoType(final MetisValueSet pValueSet) {
        return getInfoType(pValueSet, TaxYearInfoType.class);
    }

    /**
     * Obtain TaxYear.
     * @param pValueSet the valueSet
     * @return the TaxYear
     */
    public static TaxYear getTaxYear(final MetisValueSet pValueSet) {
        return getOwner(pValueSet, TaxYear.class);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public TaxYearInfo getBase() {
        return (TaxYearInfo) super.getBase();
    }

    @Override
    public TaxInfoList getList() {
        return (TaxInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the TaxInfoSet and deRegister this value */
        TaxYearInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The TaxYearInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the TaxYears */
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
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getTaxInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getTaxYears());

        /* Access the TaxInfoSet and register this data */
        TaxYearInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Update taxInfo from a taxInfo extract.
     * @param pTaxInfo the changed taxInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pTaxInfo) {
        /* Can only update from TaxYearInfo */
        if (!(pTaxInfo instanceof TaxYearInfo)) {
            return false;
        }

        /* Access as TaxInfo */
        TaxYearInfo myTaxInfo = (TaxYearInfo) pTaxInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDifference.isEqual(getField(), myTaxInfo.getField())) {
            setValueValue(myTaxInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * TaxYearInfoList.
     */
    public static class TaxInfoList
            extends DataInfoList<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataInfoList.FIELD_DEFS);

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected TaxInfoList(final MoneyWiseData pData) {
            super(TaxYearInfo.class, pData, MoneyWiseDataType.TAXYEARINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxInfoList(final TaxInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return TaxYearInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final TaxInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected TaxInfoList getEmptyList(final ListStyle pStyle) {
            TaxInfoList myList = new TaxInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxYearInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a TaxYearInfo */
            if (!(pItem instanceof TaxYearInfo)) {
                throw new UnsupportedOperationException();
            }

            TaxYearInfo myInfo = new TaxYearInfo(this, (TaxYearInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public TaxYearInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected TaxYearInfo addNewItem(final TaxYear pOwner,
                                         final TaxYearInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            TaxYearInfo myInfo = new TaxYearInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final TaxYear pTaxYear,
                                final TaxYearInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            TaxYearInfoType myInfoType = myData.getTaxInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JMoneyWiseDataException(pTaxYear, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<>(TaxYearInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pTaxYear);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Tax Info */
            TaxYearInfo myTaxInfo = new TaxYearInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myTaxInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myTaxInfo, ERROR_VALIDATION);
            }

            /* Add the TaxYear Info to the list */
            append(myTaxInfo);

            /* Validate the TaxInfo */
            myTaxInfo.validate();

            /* Handle validation failure */
            if (myTaxInfo.hasErrors()) {
                throw new JMoneyWiseDataException(myTaxInfo, ERROR_VALIDATION);
            }
        }

        @Override
        public TaxYearInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the info */
            TaxYearInfo myInfo = new TaxYearInfo(this, pValues);

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

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the TaxInfo */
            validateOnLoad();

            /* Validate the TaxYears */
            TaxYearList myYears = getDataSet().getTaxYears();
            myYears.validateOnLoad();
        }
    }
}
