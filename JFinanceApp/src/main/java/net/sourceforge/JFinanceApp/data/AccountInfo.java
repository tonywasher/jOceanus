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

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataFormatter;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataInfo;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JRate;
import net.sourceforge.JFinanceApp.data.Account.AccountList;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType.AccountInfoTypeList;

/**
 * Representation of an information extension of an account.
 * @author Tony Washer
 */
public class AccountInfo extends DataInfo<AccountInfo, Account, AccountInfoType> implements
        Comparable<AccountInfo> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountInfo.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountInfo.class.getSimpleName(),
            DataInfo.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField("Account");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if ((FIELD_ACCOUNT.equals(pField)) && !getInfoType().isLink()) {
            return JDataFieldValue.SkipField;
        }
        if ((FIELD_VALUE.equals(pField)) && getInfoType().isLink()) {
            return JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Obtain InfoType.
     * @return the Info type
     */
    public AccountInfoType getInfoType() {
        return getInfoType(getValueSet(), AccountInfoType.class);
    }

    /**
     * Obtain Account.
     * @return the Account
     */
    public Account getAccount() {
        return getOwner(getValueSet(), Account.class);
    }

    /**
     * Obtain Date.
     * @return the Money
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain CharArray.
     * @return the CharArray
     */
    public char[] getCharArray() {
        return getCharArray(getValueSet());
    }

    /**
     * Obtain Integer.
     * @return the Integer
     */
    private Integer getInteger() {
        return getInteger(getValueSet());
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
     * Obtain Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static Account getAccount(final ValueSet pValueSet) {
        return getOwner(pValueSet, Account.class);
    }

    /**
     * Set Account.
     * @param pAccount the account
     */
    private void setValueAccount(final Account pAccount) {
        getValueSet().setValue(FIELD_ACCOUNT, pAccount);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public AccountInfo getBase() {
        return (AccountInfo) super.getBase();
    }

    /**
     * Construct a copy of a AccountInfo.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected AccountInfo(final AccountInfoList pList,
                          final AccountInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Encrypted constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uAccountId the Account id
     * @param pValue the value
     * @throws JDataException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final int uId,
                        final int uControlId,
                        final int uInfoTypeId,
                        final int uAccountId,
                        final byte[] pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, uControlId, uInfoTypeId, uAccountId);

        /* Protect against exceptions */
        try {
            /* Look up the EventType */
            FinanceData myData = getDataSet();
            AccountInfoTypeList myTypes = myData.getActInfoTypes();
            AccountInfoType myType = myTypes.findItemById(uInfoTypeId);
            if (myType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid AccountInfoType Id");
            }
            setValueInfoType(myType);

            /* Look up the Account */
            AccountList myAccounts = myData.getAccounts();
            Account myAccount = myAccounts.findItemById(uAccountId);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
            }
            setValueOwner(myAccount);

            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case INTEGER:
                    setValueInteger(pValue);
                    if (myType.isLink()) {
                        myAccount = myAccounts.findItemById(getInteger());
                        if (myAccount == null) {
                            throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
                        }
                        setValueAccount(myAccount);
                    }
                    break;
                case DATEDAY:
                    setValueDateDay(pValue);
                    break;
                case CHARARRAY:
                    setValueCharArray(pValue);
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
     * Open Text constructor.
     * @param pList the list
     * @param uId the id
     * @param pInfoType the info type
     * @param pAccount the Account
     * @param pValue the value
     * @throws JDataException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final int uId,
                        final AccountInfoType pInfoType,
                        final Account pAccount,
                        final Object pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, pInfoType, pAccount);

        /* Protect against exceptions */
        try {
            /* Switch on Info Class */
            boolean bValueOK = false;
            switch (pInfoType.getDataType()) {
                case INTEGER:
                    if (pValue instanceof Account) {
                        Account myAccount = (Account) pValue;
                        setValueInteger(myAccount.getId());
                        setValueAccount(myAccount);
                        bValueOK = true;
                    }
                    break;
                case DATEDAY:
                    if (pValue instanceof Date) {
                        setValueDateDay(new JDateDay((Date) pValue));
                        bValueOK = true;
                    }
                    break;
                case CHARARRAY:
                    if (pValue instanceof char[]) {
                        setValueCharArray((char[]) pValue);
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
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
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
        int iDiff = getAccount().compareTo(pThat.getAccount());
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

        /* Access Accounts and InfoTypes */
        FinanceData myData = getDataSet();
        AccountList myAccounts = myData.getAccounts();
        AccountInfoTypeList myTypes = myData.getActInfoTypes();

        /* Update to use the local copy of the Types */
        AccountInfoType myType = getInfoType();
        AccountInfoType myNewType = myTypes.findItemById(myType.getId());
        setValueInfoType(myNewType);

        /* Update to use the local copy of the Accounts */
        Account myAccount = getAccount();
        Account myNewAct = myAccounts.findItemById(myAccount.getId());
        setValueOwner(myNewAct);
    }

    @Override
    public String formatObject() {
        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on type of Data */
        switch (getInfoType().getDataType()) {
            case INTEGER:
                return myFormatter.formatObject(getAccount());
            case DATEDAY:
                return myFormatter.formatObject(getDate());
            case CHARARRAY:
                return myFormatter.formatObject(getCharArray());
            default:
                return "null";
        }
    }

    /**
     * Set Money.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setMoney(final JMoney pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getDataType()) {
            case MONEY:
                /* Set the value */
                setValueMoney(pValue);
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Money value");
        }
    }

    /**
     * Set Units.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void setRate(final JRate pValue) throws JDataException {
        /* Switch on Info type */
        switch (getInfoType().getDataType()) {
            case RATE:
                /* Set the value */
                setValueRate(pValue);
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, this, "Invalid Attempt to set Rate value");
        }
    }

    /**
     * AccountInfoList.
     */
    public static class AccountInfoList extends DataInfoList<AccountInfo, Account, AccountInfoType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                AccountInfoList.class.getSimpleName(), DataInfoList.FIELD_DEFS);

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
        protected AccountInfoList(final FinanceData pData) {
            super(AccountInfo.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountInfoList(final AccountInfoList pSource) {
            super(pSource);
        }

        @Override
        protected AccountInfoList getEmptyList() {
            return new AccountInfoList(this);
        }

        @Override
        public AccountInfoList cloneList(final DataSet<?> pDataSet) {
            return (AccountInfoList) super.cloneList(pDataSet);
        }

        @Override
        public AccountInfoList deriveList(final ListStyle pStyle) {
            return (AccountInfoList) super.deriveList(pStyle);
        }

        @Override
        public AccountInfoList deriveDifferences(final DataList<AccountInfo> pOld) {
            return (AccountInfoList) super.deriveDifferences(pOld);
        }

        @Override
        public AccountInfo addNewItem(final DataItem pItem) {
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

        /**
         * Allow an AccountInfo to be added.
         * @param uId the id
         * @param uControlId the control id
         * @param uInfoTypeId the info type id
         * @param uAccountId the account id
         * @param pValue the data
         * @throws JDataException on error
         */
        public void addSecureItem(final int uId,
                                  final int uControlId,
                                  final int uInfoTypeId,
                                  final int uAccountId,
                                  final byte[] pValue) throws JDataException {
            /* Create the info */
            AccountInfo myInfo = new AccountInfo(this, uId, uControlId, uInfoTypeId, uAccountId, pValue);

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
         * Add an AccountInfo to the list.
         * @param uId the Id of the info
         * @param pAccount the account
         * @param pInfoType the Name of the account info type
         * @param pValue the value of the account info
         * @throws JDataException on error
         */
        public void addOpenItem(final int uId,
                                final Account pAccount,
                                final String pInfoType,
                                final Object pValue) throws JDataException {
            /* Access the data set */
            FinanceData myData = getDataSet();

            /* Look up the Info Type */
            AccountInfoType myInfoType = myData.getActInfoTypes().findItemByName(pInfoType);
            if (myInfoType == null) {
                throw new JDataException(ExceptionClass.DATA, pAccount,
                        "Account has invalid Account Info Type [" + pInfoType + "]");
            }

            /* Create a new Account Info Type */
            AccountInfo myInfo = new AccountInfo(this, uId, myInfoType, pAccount, pValue);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                throw new JDataException(ExceptionClass.DATA, myInfo, "Duplicate AccountInfoId");
            }

            /* Add the Info to the list */
            append(myInfo);

            /* Validate the Info */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfo, "Failed validation");
            }
        }

    }
}
