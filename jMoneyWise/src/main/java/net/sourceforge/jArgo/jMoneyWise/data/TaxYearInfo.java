/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFormatter;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataInfo;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDecimal.JDecimalParser;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JRate;
import net.sourceforge.JFinanceApp.data.TaxYearNew.TaxYearNewList;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoClass;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoType;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoType.TaxYearInfoTypeList;

/**
 * Representation of an information extension of a TaxYear.
 * @author Tony Washer
 */
public class TaxYearInfo extends DataInfo<TaxYearInfo, TaxYearNew, TaxYearInfoType> implements
        Comparable<TaxYearInfo> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearInfo.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(TaxYearInfo.class.getSimpleName(),
            DataInfo.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public TaxYearInfoType getInfoType() {
        return getInfoType(getValueSet(), TaxYearInfoType.class);
    }

    /**
     * Obtain TaxYear.
     * @return the TaxYear
     */
    public TaxYearNew getTaxYear() {
        return getOwner(getValueSet(), TaxYearNew.class);
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the Money
     */
    public static TaxYearInfoType getInfoType(final ValueSet pValueSet) {
        return getInfoType(pValueSet, TaxYearInfoType.class);
    }

    /**
     * Obtain TaxYear.
     * @param pValueSet the valueSet
     * @return the TaxYear
     */
    public static TaxYearNew getTaxYear(final ValueSet pValueSet) {
        return getOwner(pValueSet, TaxYearNew.class);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public TaxYearInfo getBase() {
        return (TaxYearInfo) super.getBase();
    }

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
                        final TaxYearNew pTaxYear,
                        final TaxYearInfoType pType) {
        /* Initialise the item */
        super(pList);

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pTaxYear);
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uTaxYearId the TaxYear id
     * @param pValue the value
     * @throws JDataException on error
     */
    private TaxYearInfo(final TaxInfoList pList,
                        final Integer uId,
                        final Integer uControlId,
                        final Integer uInfoTypeId,
                        final int uTaxYearId,
                        final byte[] pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, uControlId, uInfoTypeId, uTaxYearId);

        /* Protect against exceptions */
        try {
            /* Look up the EventType */
            FinanceData myData = getDataSet();
            TaxYearInfoTypeList myTypes = myData.getTaxInfoTypes();
            TaxYearInfoType myType = myTypes.findItemById(uInfoTypeId);
            if (myType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid TaxInfoType Id");
            }
            setValueInfoType(myType);

            /* Look up the TaxYear */
            TaxYearNewList myTaxYears = myData.getNewTaxYears();
            TaxYearNew myTaxYear = myTaxYears.findItemById(uTaxYearId);
            if (myTaxYear == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid TaxYear Id");
            }
            setValueOwner(myTaxYear);

            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case MONEY:
                    setValueBytes(pValue, JMoney.class);
                    break;
                case RATE:
                    setValueBytes(pValue, JRate.class);
                    break;
                default:
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Data Type");
            }

            /* Access the EventInfoSet and register this data */
            // EventInfoSet mySet = myEvent.getInfoSet();
            // mySet.registerData(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param uId the id
     * @param pInfoType the info type
     * @param pTaxYear the TaxYear
     * @param pValue the value
     * @throws JDataException on error
     */
    private TaxYearInfo(final TaxInfoList pList,
                        final Integer uId,
                        final TaxYearInfoType pInfoType,
                        final TaxYearNew pTaxYear,
                        final String pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, pInfoType, pTaxYear);

        /* Protect against exceptions */
        try {
            /* Set the value */
            setValue(pValue);

            /* Access the EventInfoSet and register this data */
            // EventInfoSet mySet = myEvent.getInfoSet();
            // mySet.registerData(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    // @Override
    // public void deRegister() {
    /* Access the EventInfoSet and register this value */
    // EventInfoSet mySet = getEvent().getInfoSet();
    // mySet.deRegisterData(this);
    // }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The TaxYearInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(final TaxYearInfo pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the TaxYears */
        int iDiff = getTaxYear().compareTo(pThat.getTaxYear());
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
    protected void relinkToDataSet() {
        /* Update the Encryption details */
        super.relinkToDataSet();

        /* Access TaxYears and InfoTypes */
        FinanceData myData = getDataSet();
        TaxYearNewList myTaxYears = myData.getNewTaxYears();
        TaxYearInfoTypeList myTypes = myData.getTaxInfoTypes();

        /* Update to use the local copy of the Types */
        TaxYearInfoType myType = getInfoType();
        TaxYearInfoType myNewType = myTypes.findItemById(myType.getId());
        setValueInfoType(myNewType);

        /* Update to use the local copy of the TaxYears */
        TaxYearNew myTaxYear = getTaxYear();
        TaxYearNew myNewYear = myTaxYears.findItemById(myTaxYear.getId());
        setValueOwner(myNewYear);
    }

    @Override
    public String formatObject() {
        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on type of Data */
        switch (getInfoType().getDataType()) {
            case MONEY:
                return myFormatter.formatObject(getValue(JMoney.class));
            case RATE:
                return myFormatter.formatObject(getValue(JRate.class));
            default:
                return "null";
        }
    }

    /**
     * Set Value.
     * @param pValue the Value
     * @throws JDataException on error
     */
    @Override
    protected void setValue(final Object pValue) throws JDataException {
        /* Access the info Type */
        TaxYearInfoType myType = getInfoType();

        /* Access the DataSet and parser */
        FinanceData myDataSet = getDataSet();
        JDataFormatter myFormatter = myDataSet.getDataFormatter();
        JDecimalParser myParser = myFormatter.getDecimalParser();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myType.getDataType()) {
            case MONEY:
                if (pValue instanceof JMoney) {
                    setValueValue(pValue);
                    bValueOK = true;
                } else if (pValue instanceof String) {
                    setValueValue(myParser.parseMoneyValue((String) pValue));
                    bValueOK = true;
                }
                break;
            case RATE:
                if (pValue instanceof JRate) {
                    setValueValue(pValue);
                    bValueOK = true;
                } else if (pValue instanceof String) {
                    setValueValue(myParser.parseRateValue((String) pValue));
                    bValueOK = true;
                }
                break;
            default:
                break;
        }

        /* Reject invalid value */
        if (!bValueOK) {
            throw new JDataException(ExceptionClass.DATA, this, "Invalid Data Type");
        }
    }

    /**
     * TaxYearInfoList.
     */
    public static class TaxInfoList extends DataInfoList<TaxYearInfo, TaxYearNew, TaxYearInfoType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxInfoList.class.getSimpleName(),
                DataInfoList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected TaxInfoList(final FinanceData pData) {
            super(TaxYearInfo.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxInfoList(final TaxInfoList pSource) {
            super(pSource);
        }

        @Override
        protected TaxInfoList getEmptyList() {
            return new TaxInfoList(this);
        }

        @Override
        public TaxInfoList cloneList(final DataSet<?> pDataSet) {
            return (TaxInfoList) super.cloneList(pDataSet);
        }

        @Override
        public TaxInfoList deriveList(final ListStyle pStyle) {
            return (TaxInfoList) super.deriveList(pStyle);
        }

        @Override
        public TaxInfoList deriveDifferences(final DataList<TaxYearInfo> pOld) {
            return (TaxInfoList) super.deriveDifferences(pOld);
        }

        @Override
        public TaxYearInfo addCopyItem(final DataItem pItem) {
            /* Can only clone a TaxYearInfo */
            if (!(pItem instanceof TaxYearInfo)) {
                return null;
            }

            TaxYearInfo myInfo = new TaxYearInfo(this, (TaxYearInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public TaxYearInfo addNewItem() {
            return null;
        }

        @Override
        protected TaxYearInfo addNewItem(final TaxYearNew pOwner,
                                         final TaxYearInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            TaxYearInfo myInfo = new TaxYearInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        /**
         * Allow a TaxYearInfo to be added.
         * @param uId the id
         * @param uControlId the control id
         * @param uInfoTypeId the info type id
         * @param uTaxYearId the taxYear id
         * @param pValue the data
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Integer uInfoTypeId,
                                  final Integer uTaxYearId,
                                  final byte[] pValue) throws JDataException {
            /* Create the info */
            TaxYearInfo myInfo = new TaxYearInfo(this, uId, uControlId, uInfoTypeId, uTaxYearId, pValue);

            /* Check that this DataId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myInfo, "Duplicate DataId");
            }

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, "Failed validation");
            }

            /* Add to the list */
            append(myInfo);
        }

        /**
         * Add a TaxYearInfo to the list.
         * @param uId the Id of the tax info
         * @param pTaxYear the tax Year
         * @param pInfoClass the Class of the account info type
         * @param pValue the value of the tax info
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final TaxYearNew pTaxYear,
                                final TaxYearInfoClass pInfoClass,
                                final String pValue) throws JDataException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            FinanceData myData = getDataSet();

            /* Look up the Info Type */
            TaxYearInfoType myInfoType = myData.getTaxInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JDataException(ExceptionClass.DATA, pTaxYear,
                        "TaxYear has invalid Tax Info Class [" + pInfoClass + "]");
            }

            /* Create a new Tax Info */
            TaxYearInfo myTaxInfo = new TaxYearInfo(this, uId, myInfoType, pTaxYear, pValue);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myTaxInfo.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTaxInfo, "Duplicate TaxYearInfoId");
            }

            /* Add the TaxYear Info to the list */
            append(myTaxInfo);

            /* Validate the TaxInfo */
            myTaxInfo.validate();

            /* Handle validation failure */
            if (myTaxInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxInfo, "Failed validation");
            }
        }
    }
}
