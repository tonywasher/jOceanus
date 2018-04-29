/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a loan.
 * @author Tony Washer
 */
public class LoanInfo
        extends DataInfo<LoanInfo, Loan, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.LOANINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.LOANINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected LoanInfo(final LoanInfoList pList,
                       final LoanInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pLoan the loan
     * @param pType the type
     */
    private LoanInfo(final LoanInfoList pList,
                     final Loan pLoan,
                     final AccountInfoType pType) {
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
    private LoanInfo(final LoanInfoList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getDeposits());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the LoanInfoSet and register this data */
            final LoanInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public AccountInfoType getInfoType() {
        return getInfoType(getValueSet(), AccountInfoType.class);
    }

    @Override
    public AccountInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    @Override
    public Loan getOwner() {
        return getOwner(getValueSet(), Loan.class);
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the InfoType
     */
    public static AccountInfoType getInfoType(final MetisValueSet pValueSet) {
        return getInfoType(pValueSet, AccountInfoType.class);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public LoanInfo getBase() {
        return (LoanInfo) super.getBase();
    }

    @Override
    public LoanInfoList getList() {
        return (LoanInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the LoanInfoSet and register this value */
        final LoanInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<LoanInfo, Loan, AccountInfoType, AccountInfoClass, MoneyWiseDataType> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Loans */
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
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getLoans());

        /* Access the LoanInfoSet and register this data */
        final LoanInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Update depositInfo from a depositInfo extract.
     * @param pInfo the changed depositInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pInfo) {
        /* Can only update from LoanInfo */
        if (!(pInfo instanceof LoanInfo)) {
            return false;
        }

        /* Access as LoanInfo */
        final LoanInfo myLoanInfo = (LoanInfo) pInfo;

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
    public static class LoanInfoList
            extends DataInfoList<LoanInfo, Loan, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<LoanInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(LoanInfoList.class);

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         */
        protected LoanInfoList(final MoneyWiseData pData) {
            super(LoanInfo.class, pData, MoneyWiseDataType.LOANINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private LoanInfoList(final LoanInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<LoanInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return LoanInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final LoanInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected LoanInfoList getEmptyList(final ListStyle pStyle) {
            final LoanInfoList myList = new LoanInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public LoanInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a LoanInfo */
            if (!(pItem instanceof LoanInfo)) {
                throw new UnsupportedOperationException();
            }

            final LoanInfo myInfo = new LoanInfo(this, (LoanInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public LoanInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected LoanInfo addNewItem(final Loan pOwner,
                                      final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            final LoanInfo myInfo = new LoanInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Loan pLoan,
                                final AccountInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            final MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            final AccountInfoType myInfoType = myData.getActInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new MoneyWiseDataException(pLoan, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(DepositInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pLoan);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Loan Info */
            final LoanInfo myInfo = new LoanInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public LoanInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the info */
            final LoanInfo myInfo = new LoanInfo(this, pValues);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
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
            final LoanList myLoans = getDataSet().getLoans();
            myLoans.mapData();
            myLoans.validateOnLoad();
        }
    }
}
