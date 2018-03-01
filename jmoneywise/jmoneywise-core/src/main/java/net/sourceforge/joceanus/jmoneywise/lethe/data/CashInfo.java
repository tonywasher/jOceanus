/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a cash account.
 * @author Tony Washer
 */
public class CashInfo
        extends DataInfo<CashInfo, Cash, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.CASHINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.CASHINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected CashInfo(final CashInfoList pList,
                       final CashInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pCash the cash
     * @param pType the type
     */
    private CashInfo(final CashInfoList pList,
                     final Cash pCash,
                     final AccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pCash);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private CashInfo(final CashInfoList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getCash());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Resolve any link value */
            resolveLink();

            /* Access the CashInfoSet and register this data */
            final CashInfoSet mySet = getOwner().getInfoSet();
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
    public Cash getOwner() {
        return getOwner(getValueSet(), Cash.class);
    }

    /**
     * Obtain Payee.
     * @return the Payee
     */
    public Payee getPayee() {
        return getPayee(getValueSet());
    }

    /**
     * Obtain EventCategory.
     * @return the EventCategory
     */
    public TransactionCategory getEventCategory() {
        return getEventCategory(getValueSet());
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the InfoType
     */
    public static AccountInfoType getInfoType(final MetisValueSet pValueSet) {
        return getInfoType(pValueSet, AccountInfoType.class);
    }

    /**
     * Obtain Linked Payee.
     * @param pValueSet the valueSet
     * @return the Payee
     */
    public static Payee getPayee(final MetisValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_LINK, Payee.class);
    }

    /**
     * Obtain Linked EventCategory.
     * @param pValueSet the valueSet
     * @return the EventCategory
     */
    public static TransactionCategory getEventCategory(final MetisValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_LINK, TransactionCategory.class);
    }

    @Override
    public String getLinkName() {
        final DataItem<?> myItem = getLink();
        if (myItem instanceof Payee) {
            return ((Payee) myItem).getName();
        }
        if (myItem instanceof TransactionCategory) {
            return ((TransactionCategory) myItem).getName();
        }
        return null;
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public CashInfo getBase() {
        return (CashInfo) super.getBase();
    }

    @Override
    public CashInfoList getList() {
        return (CashInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the CashInfoSet and register this value */
        final CashInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<CashInfo, Cash, AccountInfoType, AccountInfoClass, MoneyWiseDataType> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Cash */
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
        resolveDataLink(FIELD_OWNER, myData.getCash());

        /* Resolve any link value */
        resolveLink();

        /* Access the CashInfoSet and register this data */
        final CashInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Resolve link reference.
     * @throws OceanusException on error
     */
    private void resolveLink() throws OceanusException {
        /* If we have a link */
        final AccountInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            final MoneyWiseData myData = getDataSet();
            final MetisValueSet myValues = getValueSet();
            final Object myLinkId = myValues.getValue(FIELD_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case AUTOPAYEE:
                    resolveDataLink(FIELD_LINK, myData.getPayees());
                    if (myLinkId == null) {
                        setValueValue(getPayee().getId());
                    }
                    break;
                case AUTOEXPENSE:
                    resolveDataLink(FIELD_LINK, myData.getTransCategories());
                    if (myLinkId == null) {
                        setValueValue(getEventCategory().getId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update loanInfo from a loanInfo extract.
     * @param pInfo the changed loanInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pInfo) {
        /* Can only update from CashInfo */
        if (!(pInfo instanceof CashInfo)) {
            return false;
        }

        /* Access as CashInfo */
        final CashInfo myInfo = (CashInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), myInfo.getField())) {
            setValueValue(myInfo.getField());
            if (getInfoType().isLink()) {
                setValueLink(myInfo.getLink());
            }
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
            case AUTOPAYEE:
                getPayee().touchItem(this);
                break;
            case AUTOEXPENSE:
                getEventCategory().touchItem(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void touchOnUpdate() {
        /* Switch on info class */
        switch (getInfoClass()) {
            case AUTOPAYEE:
                getPayee().touchItem(this);
                break;
            default:
                break;
        }
    }

    /**
     * CashInfoList.
     */
    public static class CashInfoList
            extends DataInfoList<CashInfo, Cash, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<CashInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(CashInfoList.class);

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected CashInfoList(final MoneyWiseData pData) {
            super(CashInfo.class, pData, MoneyWiseDataType.CASHINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private CashInfoList(final CashInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<CashInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return CashInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final CashInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected CashInfoList getEmptyList(final ListStyle pStyle) {
            final CashInfoList myList = new CashInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public CashInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a CashInfo */
            if (!(pItem instanceof CashInfo)) {
                throw new UnsupportedOperationException();
            }

            final CashInfo myInfo = new CashInfo(this, (CashInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public CashInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected CashInfo addNewItem(final Cash pOwner,
                                      final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            final CashInfo myInfo = new CashInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Cash pCash,
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
                throw new MoneyWiseDataException(pCash, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final DataValues<MoneyWiseDataType> myValues = new DataValues<>(DepositInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pCash);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Cash Info */
            final CashInfo myInfo = new CashInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public CashInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the info */
            final CashInfo myInfo = new CashInfo(this, pValues);

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

        /**
         * Resolve ValueLinks.
         * @throws OceanusException on error
         */
        public void resolveValueLinks() throws OceanusException {
            /* Loop through the Info items */
            final Iterator<CashInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                final CashInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink();
                }
            }
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the CashInfo */
            validateOnLoad();

            /* Map and Validate the Cash */
            final CashList myCash = getDataSet().getCash();
            myCash.mapData();
            myCash.validateOnLoad();
        }
    }
}
