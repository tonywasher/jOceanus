/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of an account.
 * @author Tony Washer
 */
public class DepositInfo
        extends DataInfo<DepositInfo, Deposit, AccountInfoType, AccountInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSITINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSITINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected DepositInfo(final DepositInfoList pList,
                          final DepositInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pDeposit the deposit
     * @param pType the type
     */
    private DepositInfo(final DepositInfoList pList,
                        final Deposit pDeposit,
                        final AccountInfoType pType) {
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
    private DepositInfo(final DepositInfoList pList,
                        final DataValues pValues) throws OceanusException {
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

            /* Access the DepositInfoSet and register this data */
            final DepositInfoSet mySet = getOwner().getInfoSet();
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
    public Deposit getOwner() {
        return getOwner(getValueSet(), Deposit.class);
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
    public DepositInfo getBase() {
        return (DepositInfo) super.getBase();
    }

    @Override
    public DepositInfoList getList() {
        return (DepositInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the DepositInfoSet and register this value */
        final DepositInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<DepositInfo, Deposit, AccountInfoType, AccountInfoClass> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Deposits */
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
        resolveDataLink(FIELD_OWNER, myData.getDeposits());

        /* Access the DepositInfoSet and register this data */
        final DepositInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Update depositInfo from a depositInfo extract.
     * @param pInfo the changed depositInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pInfo) {
        /* Can only update from DepositInfo */
        if (!(pInfo instanceof DepositInfo)) {
            return false;
        }

        /* Access as DepositInfo */
        final DepositInfo myDepInfo = (DepositInfo) pInfo;

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
    public static class DepositInfoList
            extends DataInfoList<DepositInfo, Deposit, AccountInfoType, AccountInfoClass> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositInfoList.class);

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected DepositInfoList(final MoneyWiseData pData) {
            super(DepositInfo.class, pData, MoneyWiseDataType.DEPOSITINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DepositInfoList(final DepositInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<DepositInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return DepositInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final DepositInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected DepositInfoList getEmptyList(final ListStyle pStyle) {
            final DepositInfoList myList = new DepositInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DepositInfo addCopyItem(final DataItem pItem) {
            /* Can only clone a DepositInfo */
            if (!(pItem instanceof DepositInfo)) {
                throw new UnsupportedOperationException();
            }

            final DepositInfo myInfo = new DepositInfo(this, (DepositInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public DepositInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected DepositInfo addNewItem(final Deposit pOwner,
                                         final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            final DepositInfo myInfo = new DepositInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Deposit pDeposit,
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
                throw new MoneyWiseDataException(pDeposit, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final DataValues myValues = new DataValues(DepositInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pDeposit);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Deposit Info */
            final DepositInfo myInfo = new DepositInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public DepositInfo addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the info */
            final DepositInfo myInfo = new DepositInfo(this, pValues);

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
            /* Validate the DepositInfo */
            validateOnLoad();

            /* Map and Validate the Deposits */
            final DepositList myDeposits = getDataSet().getDeposits();
            myDeposits.mapData();
            myDeposits.validateOnLoad();
        }
    }
}
