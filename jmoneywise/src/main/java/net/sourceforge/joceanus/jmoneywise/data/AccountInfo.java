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
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
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
 * Representation of an information extension of an account.
 * @author Tony Washer
 */
public class AccountInfo
        extends DataInfo<AccountInfo, Account, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.ACCOUNTINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.ACCOUNTINFO.getListName();

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
    public Account getOwner() {
        return getOwner(getValueSet(), Account.class);
    }

    /**
     * Obtain Account.
     * @return the Account
     */
    public Account getAccount() {
        return getAccount(getValueSet());
    }

    /**
     * Obtain EventCategory.
     * @return the EventCategory
     */
    public EventCategory getEventCategory() {
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
     * Obtain Linked Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static Account getAccount(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, Account.class);
    }

    /**
     * Obtain Linked EventCategory.
     * @param pValueSet the valueSet
     * @return the EventCategory
     */
    public static EventCategory getEventCategory(final ValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, EventCategory.class);
    }

    @Override
    public String getLinkName() {
        DataItem<?> myItem = getLink(DataItem.class);
        if (myItem instanceof Account) {
            return ((Account) myItem).getName();
        }
        if (myItem instanceof EventCategory) {
            return ((EventCategory) myItem).getName();
        }
        return null;
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public AccountInfo getBase() {
        return (AccountInfo) super.getBase();
    }

    @Override
    public AccountInfoList getList() {
        return (AccountInfoList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected AccountInfo(final AccountInfoList pList,
                          final AccountInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
        setControlKey(pList.getControlKey());
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pAccount the account
     * @param pType the type
     */
    private AccountInfo(final AccountInfoList pList,
                        final Account pAccount,
                        final AccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setControlKey(pList.getControlKey());

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pAccount);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getAccounts());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the AccountInfoSet and register this data */
            AccountInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the AccountInfoSet and register this value */
        AccountInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final AccountInfo pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Accounts */
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
        resolveDataLink(FIELD_OWNER, myData.getAccounts());

        /* Resolve any link value */
        resolveLink();

        /* Access the AccountInfoSet and register this data */
        AccountInfoSet mySet = getOwner().getInfoSet();
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
                case ALIAS:
                case PARENT:
                case PORTFOLIO:
                case HOLDING:
                    resolveDataLink(FIELD_LINK, myData.getAccounts());
                    if (myLinkId == null) {
                        setValueValue(getAccount().getId());
                    }
                    break;
                case AUTOEXPENSE:
                    resolveDataLink(FIELD_LINK, myData.getEventCategories());
                    if (myLinkId == null) {
                        setValueValue(getEventCategory().getId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public String formatObject() {
        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on type of Data */
        switch (getInfoType().getDataType()) {
            case LINK:
                return myFormatter.formatObject(getLink(DataItem.class));
            default:
                return myFormatter.formatObject(getValue(Object.class));
        }
    }

    /**
     * Update accountInfo from an accountInfo extract.
     * @param pActInfo the changed accountInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pActInfo) {
        /* Can only update from AccountInfo */
        if (!(pActInfo instanceof AccountInfo)) {
            return false;
        }

        /* Access as AccountInfo */
        AccountInfo myActInfo = (AccountInfo) pActInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!Difference.isEqual(getField(), myActInfo.getField())) {
            setValueValue(myActInfo.getField());
            if (getInfoType().isLink()) {
                setValueLink(myActInfo.getLink(DataItem.class));
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
            case PARENT:
            case ALIAS:
            case PORTFOLIO:
            case HOLDING:
                getAccount().touchItem(this);
                break;
            case AUTOEXPENSE:
                getEventCategory().touchItem(this);
                break;
            default:
                break;
        }
    }

    /**
     * AccountInfoList.
     */
    public static class AccountInfoList
            extends DataInfoList<AccountInfo, Account, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
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
            return AccountInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final AccountInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected AccountInfoList(final MoneyWiseData pData) {
            super(AccountInfo.class, pData, MoneyWiseDataType.ACCOUNTINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountInfoList(final AccountInfoList pSource) {
            super(pSource);
        }

        @Override
        protected AccountInfoList getEmptyList(final ListStyle pStyle) {
            AccountInfoList myList = new AccountInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountInfoList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountInfoList) super.cloneList(pDataSet);
        }

        @Override
        public AccountInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an AccountInfo */
            if (!(pItem instanceof AccountInfo)) {
                return null;
            }

            AccountInfo myInfo = new AccountInfo(this, (AccountInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public AccountInfo addNewItem() {
            return null;
        }

        @Override
        protected AccountInfo addNewItem(final Account pOwner,
                                         final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            AccountInfo myInfo = new AccountInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Account pAccount,
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
                throw new JMoneyWiseDataException(pAccount, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(TaxYearInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pAccount);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Account Info */
            AccountInfo myInfo = new AccountInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            append(myInfo);
        }

        @Override
        public AccountInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the info */
            AccountInfo myInfo = new AccountInfo(this, pValues);

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
            Iterator<AccountInfo> myIterator = iterator();
            while (myIterator.hasNext()) {
                AccountInfo myCurr = myIterator.next();

                /* If this is an infoItem */
                if (myCurr.getInfoType().isLink()) {
                    /* Resolve the link */
                    myCurr.resolveLink();
                }
            }
        }
    }
}
