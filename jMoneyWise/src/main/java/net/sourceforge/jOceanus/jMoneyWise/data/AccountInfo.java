/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataInfo;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;

/**
 * Representation of an information extension of an account.
 * @author Tony Washer
 */
public class AccountInfo
        extends DataInfo<AccountInfo, Account, AccountInfoType, AccountInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountInfo.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountInfo.class.getSimpleName(), DataInfo.FIELD_DEFS);

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
        if ((FIELD_ACCOUNT.equals(pField))
            && !getInfoType().isLink()) {
            return JDataFieldValue.SkipField;
        }
        if ((FIELD_VALUE.equals(pField))
            && getInfoType().isLink()) {
            return JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public AccountInfoType getInfoType() {
        return getInfoType(getValueSet(), AccountInfoType.class);
    }

    @Override
    public AccountInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    /**
     * Obtain Account.
     * @return the Account
     */
    public Account getOwnerAccount() {
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
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the InfoType
     */
    public static AccountInfoType getInfoType(final ValueSet pValueSet) {
        return getInfoType(pValueSet, AccountInfoType.class);
    }

    /**
     * Obtain Owner Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static Account getAccount(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, Account.class);
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
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uAccountId the Account id
     * @param pValue the value
     * @throws JDataException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final Integer uId,
                        final Integer uControlId,
                        final Integer uInfoTypeId,
                        final Integer uAccountId,
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
            Account myOwner = myAccounts.findItemById(uAccountId);
            if (myOwner == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
            }
            setValueOwner(myOwner);

            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case INTEGER:
                    setValueBytes(pValue, Integer.class);
                    if (myType.isLink()) {
                        Account myLink = myAccounts.findItemById(getValue(Integer.class));
                        if (myLink == null) {
                            throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
                        }
                        setValueAccount(myLink);
                    }
                    break;
                case DATEDAY:
                    setValueBytes(pValue, JDateDay.class);
                    break;
                case CHARARRAY:
                    setValueBytes(pValue, char[].class);
                    break;
                default:
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Data Type");
            }

            /* Access the AccountInfoSet and register this data */
            AccountInfoSet mySet = myOwner.getInfoSet();
            mySet.registerInfo(this);
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
     * @param pAccount the Account
     * @param pValue the value
     * @throws JDataException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final Integer uId,
                        final AccountInfoType pInfoType,
                        final Account pAccount,
                        final Object pValue) throws JDataException {
        /* Initialise the item */
        super(pList, uId, pInfoType, pAccount);

        try {
            /* Set the value */
            setValue(pValue);

            /* Access the AccountInfoSet and register this data */
            AccountInfoSet mySet = pAccount.getInfoSet();
            mySet.registerInfo(this);
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    @Override
    public void deRegister() {
        /* Access the AccountInfoSet and register this value */
        AccountInfoSet mySet = getOwnerAccount().getInfoSet();
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
        int iDiff = getOwnerAccount().compareTo(pThat.getOwnerAccount());
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
    public void relinkToDataSet() {
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
        Account myAccount = getOwnerAccount();
        Account myOwner = myAccounts.findItemById(myAccount.getId());
        setValueOwner(myOwner);

        /* If the value is a link */
        if (myType.isLink()) {
            /* Update account value */
            myAccount = getAccount();
            Account myNewAct = myAccounts.findItemById(myAccount.getId());
            setValueAccount(myNewAct);
        }

        /* Access the AccountInfoSet and register this data */
        AccountInfoSet mySet = myOwner.getInfoSet();
        mySet.registerInfo(this);
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
                return myFormatter.formatObject(getValue(JDateDay.class));
            case CHARARRAY:
                return myFormatter.formatObject(getValue(char[].class));
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
        AccountInfoType myType = getInfoType();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myType.getDataType()) {
            case INTEGER:
                if (pValue instanceof Account) {
                    Account myAccount = (Account) pValue;
                    setValueValue(myAccount.getId());
                    setValueAccount(myAccount);
                    bValueOK = true;
                }
                break;
            case DATEDAY:
                if (pValue instanceof Date) {
                    setValueValue(new JDateDay((Date) pValue));
                    bValueOK = true;
                } else if (pValue instanceof JDateDay) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                break;
            case CHARARRAY:
                if (pValue instanceof char[]) {
                    setValueValue(pValue);
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
     * Update accountInfo from an accountInfo extract.
     * @param pActInfo the changed accountInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pActInfo) {
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
                setValueAccount(myActInfo.getAccount());
            }
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * AccountInfoList.
     */
    public static class AccountInfoList
            extends DataInfoList<AccountInfo, Account, AccountInfoType, AccountInfoClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountInfoList.class.getSimpleName(), DataInfoList.FIELD_DEFS);

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
        protected AccountInfoList getEmptyList(final ListStyle pStyle) {
            AccountInfoList myList = new AccountInfoList(this);
            myList.setStyle(pStyle);
            return myList;
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
        public AccountInfo addCopyItem(final DataItem pItem) {
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

        /**
         * Allow an AccountInfo to be added.
         * @param uId the id
         * @param uControlId the control id
         * @param uInfoTypeId the info type id
         * @param uAccountId the account id
         * @param pValue the data
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Integer uInfoTypeId,
                                  final Integer uAccountId,
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
         * @param pInfoClass the Class of the account info type
         * @param pValue the value of the account info
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final Account pAccount,
                                final AccountInfoClass pInfoClass,
                                final Object pValue) throws JDataException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            FinanceData myData = getDataSet();

            /* Look up the Info Type */
            AccountInfoType myInfoType = myData.getActInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new JDataException(ExceptionClass.DATA, pAccount, "Account has invalid Account Info Class ["
                                                                        + pInfoClass
                                                                        + "]");
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
