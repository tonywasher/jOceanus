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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataInfo;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdecimal.JDecimalParser;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;

/**
 * Representation of an information extension of a TaxYear.
 * @author Tony Washer
 */
public class TaxYearInfo
        extends DataInfo<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearInfo.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TaxYearInfo.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataInfo.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
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
    public static TaxYearInfoType getInfoType(final ValueSet pValueSet) {
        return getInfoType(pValueSet, TaxYearInfoType.class);
    }

    /**
     * Obtain TaxYear.
     * @param pValueSet the valueSet
     * @return the TaxYear
     */
    public static TaxYear getTaxYear(final ValueSet pValueSet) {
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

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected TaxYearInfo(final TaxInfoList pList,
                          final TaxYearInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
        setControlKey(pList.getControlKey());
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
        setControlKey(pList.getControlKey());

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pTaxYear);
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the control id
     * @param pInfoTypeId the info id
     * @param pTaxYearId the TaxYear id
     * @param pValue the value
     * @throws JDataException on error
     */
    private TaxYearInfo(final TaxInfoList pList,
                        final Integer pId,
                        final Integer pControlId,
                        final Integer pInfoTypeId,
                        final int pTaxYearId,
                        final byte[] pValue) throws JDataException {
        /* Initialise the item */
        super(pList, pId, pControlId, pInfoTypeId, pTaxYearId);

        /* Look up the EventType */
        MoneyWiseData myData = getDataSet();
        TaxYearInfoTypeList myTypes = myData.getTaxInfoTypes();
        TaxYearInfoType myType = myTypes.findItemById(pInfoTypeId);
        if (myType == null) {
            addError(ERROR_UNKNOWN, FIELD_INFOTYPE);
            throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
        }
        setValueInfoType(myType);

        /* Look up the TaxYear */
        TaxYearList myTaxYears = myData.getTaxYears();
        TaxYear myOwner = myTaxYears.findItemById(pTaxYearId);
        if (myOwner == null) {
            addError(ERROR_UNKNOWN, FIELD_OWNER);
            throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
        }
        setValueOwner(myOwner);

        /* Protect against exceptions */
        try {
            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case MONEY:
                    setValueBytes(pValue, JMoney.class);
                    break;
                case RATE:
                    setValueBytes(pValue, JRate.class);
                    break;
                default:
                    throw new JDataException(ExceptionClass.DATA, this, ERROR_BADDATATYPE);
            }

            /* Access the TaxInfoSet and register this data */
            TaxInfoSet mySet = myOwner.getInfoSet();
            mySet.registerInfo(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pInfoType the info type
     * @param pTaxYear the TaxYear
     * @param pValue the value
     * @throws JDataException on error
     */
    private TaxYearInfo(final TaxInfoList pList,
                        final Integer pId,
                        final TaxYearInfoType pInfoType,
                        final TaxYear pTaxYear,
                        final Object pValue) throws JDataException {
        /* Initialise the item */
        super(pList, pId, pInfoType, pTaxYear);

        /* Protect against exceptions */
        try {
            /* Set the value */
            setValue(pValue);

            /* Access the TaxInfoSet and register this data */
            TaxInfoSet mySet = pTaxYear.getInfoSet();
            mySet.registerInfo(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the TaxInfoSet and deRegister this value */
        TaxInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The TaxYearInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
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
    public void resolveDataSetLinks() throws JDataException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access TaxYears and InfoTypes */
        MoneyWiseData myData = getDataSet();
        TaxYearList myTaxYears = myData.getTaxYears();
        TaxYearInfoTypeList myTypes = myData.getTaxInfoTypes();

        /* Update to use the local copy of the Types */
        TaxYearInfoType myType = getInfoType();
        TaxYearInfoType myNewType = myTypes.findItemById(myType.getId());
        if (myNewType == null) {
            addError(ERROR_UNKNOWN, FIELD_INFOTYPE);
            throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
        }
        setValueInfoType(myNewType);

        /* Update to use the local copy of the TaxYears */
        TaxYear myTaxYear = getOwner();
        TaxYear myNewYear = myTaxYears.findItemById(myTaxYear.getId());
        if (myNewYear == null) {
            addError(ERROR_UNKNOWN, FIELD_OWNER);
            throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
        }
        setValueOwner(myNewYear);

        /* Access the TaxInfoSet and register this data */
        TaxInfoSet mySet = myNewYear.getInfoSet();
        mySet.registerInfo(this);
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
                return null;
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
        MoneyWiseData myDataSet = getDataSet();
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
            throw new JDataException(ExceptionClass.DATA, this, ERROR_BADDATATYPE);
        }
    }

    /**
     * Update taxInfo from a taxInfo extract.
     * @param pTaxInfo the changed taxInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pTaxInfo) {
        /* Can only update from TaxYearInfo */
        if (!(pTaxInfo instanceof TaxYearInfo)) {
            return false;
        }

        /* Access as TaxInfo */
        TaxYearInfo myTaxInfo = (TaxYearInfo) pTaxInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!Difference.isEqual(getField(), myTaxInfo.getField())) {
            setValueValue(myTaxInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * TaxYearInfoList.
     */
    public static class TaxInfoList
            extends DataInfoList<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataInfoList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
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

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected TaxInfoList(final MoneyWiseData pData) {
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
        protected TaxInfoList getEmptyList(final ListStyle pStyle) {
            TaxInfoList myList = new TaxInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxInfoList cloneList(final DataSet<?, ?> pDataSet) throws JDataException {
            return (TaxInfoList) super.cloneList(pDataSet);
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
        protected TaxYearInfo addNewItem(final TaxYear pOwner,
                                         final TaxYearInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            TaxYearInfo myInfo = new TaxYearInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        /**
         * Allow a TaxYearInfo to be added.
         * @param pId the id
         * @param pControlId the control id
         * @param pInfoTypeId the info type id
         * @param pTaxYearId the taxYear id
         * @param pValue the data
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Integer pInfoTypeId,
                                  final Integer pTaxYearId,
                                  final byte[] pValue) throws JDataException {
            /* Create the info */
            TaxYearInfo myInfo = new TaxYearInfo(this, pId, pControlId, pInfoTypeId, pTaxYearId, pValue);

            /* Check that this DataId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfo, ERROR_VALIDATION);
            }

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myInfo);
        }

        @Override
        public void addOpenItem(final Integer pId,
                                final TaxYear pTaxYear,
                                final TaxYearInfoClass pInfoClass,
                                final Object pValue) throws JDataException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            TaxYearInfoType myInfoType = myData.getTaxInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JDataException(ExceptionClass.DATA, pTaxYear, ERROR_BADINFOCLASS
                                                                        + " ["
                                                                        + pInfoClass
                                                                        + "]");
            }

            /* Create a new Tax Info */
            TaxYearInfo myTaxInfo = new TaxYearInfo(this, pId, myInfoType, pTaxYear, pValue);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myTaxInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myTaxInfo, ERROR_VALIDATION);
            }

            /* Add the TaxYear Info to the list */
            append(myTaxInfo);

            /* Validate the TaxInfo */
            myTaxInfo.validate();

            /* Handle validation failure */
            if (myTaxInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxInfo, ERROR_VALIDATION);
            }
        }
    }
}
