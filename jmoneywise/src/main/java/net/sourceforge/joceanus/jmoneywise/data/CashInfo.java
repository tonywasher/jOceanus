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

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
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
    public static AccountInfoType getInfoType(final ValueSet pValueSet) {
        return getInfoType(pValueSet, AccountInfoType.class);
    }

    /**
     * Obtain Linked Payee.
     * @param pValueSet the valueSet
     * @return the Payee
     */
    public static Payee getPayee(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, Payee.class);
    }

    /**
     * Obtain Linked EventCategory.
     * @param pValueSet the valueSet
     * @return the EventCategory
     */
    public static TransactionCategory getEventCategory(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, TransactionCategory.class);
    }

    @Override
    public String getLinkName() {
        DataItem<?> myItem = getLink(DataItem.class);
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

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected CashInfo(final CashInfoList pList,
                       final CashInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
        setControlKey(pList.getControlKey());
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
        setControlKey(pList.getControlKey());

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pCash);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private CashInfo(final CashInfoList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getCash());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the CashInfoSet and register this data */
            CashInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the CashInfoSet and register this value */
        CashInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final CashInfo pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getCash());

        /* Resolve any link value */
        resolveLink();

        /* Access the CashInfoSet and register this data */
        CashInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Resolve link reference.
     * @throws JOceanusException on error
     */
    private void resolveLink() throws JOceanusException {
        /* If we have a link */
        AccountInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            MoneyWiseData myData = getDataSet();
            ValueSet myValues = getValueSet();
            Object myLinkId = myValues.getValue(FIELD_VALUE);

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
        CashInfo myInfo = (CashInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!Difference.isEqual(getField(), myInfo.getField())) {
            setValueValue(myInfo.getField());
            if (getInfoType().isLink()) {
                setValueLink(myInfo.getLink(DataItem.class));
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

    /**
     * CashInfoList.
     */
    public static class CashInfoList
            extends DataInfoList<CashInfo, Cash, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataInfoList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
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

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected CashInfoList(final MoneyWiseData pData) {
            super(CashInfo.class, pData, MoneyWiseDataType.LOANINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private CashInfoList(final CashInfoList pSource) {
            super(pSource);
        }

        @Override
        protected CashInfoList getEmptyList(final ListStyle pStyle) {
            CashInfoList myList = new CashInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public CashInfoList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (CashInfoList) super.cloneList(pDataSet);
        }

        @Override
        public CashInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a CashInfo */
            if (!(pItem instanceof CashInfo)) {
                throw new UnsupportedOperationException();
            }

            CashInfo myInfo = new CashInfo(this, (CashInfo) pItem);
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
            CashInfo myInfo = new CashInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Cash pCash,
                                final AccountInfoClass pInfoClass,
                                final Object pValue) throws JOceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            AccountInfoType myInfoType = myData.getActInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JMoneyWiseDataException(pCash, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(DepositInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pCash);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Cash Info */
            CashInfo myInfo = new CashInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            append(myInfo);
        }

        @Override
        public CashInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the info */
            CashInfo myInfo = new CashInfo(this, pValues);

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

        /**
         * Resolve ValueLinks.
         * @throws JOceanusException on error
         */
        public void resolveValueLinks() throws JOceanusException {
            /* Loop through the Info items */
            Iterator<CashInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                CashInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink();
                }
            }
        }
    }
}
