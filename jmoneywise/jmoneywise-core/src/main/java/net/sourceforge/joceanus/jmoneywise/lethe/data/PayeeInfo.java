/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a payee.
 * @author Tony Washer
 */
public class PayeeInfo
        extends DataInfo<PayeeInfo, Payee, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PAYEEINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PAYEEINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected PayeeInfo(final PayeeInfoList pList,
                        final PayeeInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pPayee the payee
     * @param pType the type
     */
    private PayeeInfo(final PayeeInfoList pList,
                      final Payee pPayee,
                      final AccountInfoType pType) {
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
    private PayeeInfo(final PayeeInfoList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getPayees());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the PayeeInfoSet and register this data */
            final PayeeInfoSet mySet = getOwner().getInfoSet();
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
    public Payee getOwner() {
        return getOwner(getValueSet(), Payee.class);
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
    public PayeeInfo getBase() {
        return (PayeeInfo) super.getBase();
    }

    @Override
    public PayeeInfoList getList() {
        return (PayeeInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the PayeeInfoSet and register this value */
        final PayeeInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<PayeeInfo, Payee, AccountInfoType, AccountInfoClass, MoneyWiseDataType> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Payees */
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
        resolveDataLink(FIELD_OWNER, myData.getPayees());

        /* Access the PayeeInfoSet and register this data */
        final PayeeInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Update payeeInfo from a payeeInfo extract.
     * @param pInfo the changed payeeInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pInfo) {
        /* Can only update from PayeeInfo */
        if (!(pInfo instanceof PayeeInfo)) {
            return false;
        }

        /* Access as PayeeInfo */
        final PayeeInfo myPayeeInfo = (PayeeInfo) pInfo;

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
    public static class PayeeInfoList
            extends DataInfoList<PayeeInfo, Payee, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PayeeInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(PayeeInfoList.class);

        /**
         * Construct an empty CORE info list.
         * @param pData the DataSet for the list
         */
        protected PayeeInfoList(final MoneyWiseData pData) {
            super(PayeeInfo.class, pData, MoneyWiseDataType.PAYEEINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PayeeInfoList(final PayeeInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PayeeInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return PayeeInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final PayeeInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected PayeeInfoList getEmptyList(final ListStyle pStyle) {
            final PayeeInfoList myList = new PayeeInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PayeeInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a PayeeInfo */
            if (!(pItem instanceof PayeeInfo)) {
                throw new UnsupportedOperationException();
            }

            final PayeeInfo myInfo = new PayeeInfo(this, (PayeeInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public PayeeInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected PayeeInfo addNewItem(final Payee pOwner,
                                       final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            final PayeeInfo myInfo = new PayeeInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Payee pPayee,
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
                throw new MoneyWiseDataException(pPayee, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final DataValues<MoneyWiseDataType> myValues = new DataValues<>(PayeeInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pPayee);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Payee Info */
            final PayeeInfo myInfo = new PayeeInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public PayeeInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the info */
            final PayeeInfo myInfo = new PayeeInfo(this, pValues);

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
            /* Validate the PayeeInfo */
            validateOnLoad();

            /* Map and Validate the Payees */
            final PayeeList myPayees = getDataSet().getPayees();
            myPayees.mapData();
            myPayees.validateOnLoad();
        }
    }
}
