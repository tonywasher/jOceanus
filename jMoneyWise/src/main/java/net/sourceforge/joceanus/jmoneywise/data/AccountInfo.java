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

import java.util.Date;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataInfo;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
    public static final String LIST_NAME = OBJECT_NAME;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountInfo.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataInfo.FIELD_DEFS);

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
        DataItem myItem = getLink(DataItem.class);
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
     * Secure constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the control id
     * @param pInfoTypeId the info id
     * @param pAccountId the Account id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final Integer pId,
                        final Integer pControlId,
                        final Integer pInfoTypeId,
                        final Integer pAccountId,
                        final byte[] pValue) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId, pControlId, pInfoTypeId, pAccountId);

        /* Protect against exceptions */
        try {
            /* Look up the InfoType */
            MoneyWiseData myData = getDataSet();
            AccountInfoTypeList myTypes = myData.getActInfoTypes();
            AccountInfoType myType = myTypes.findItemById(pInfoTypeId);
            if (myType == null) {
                addError(ERROR_UNKNOWN, FIELD_INFOTYPE);
                throw new JMoneyWiseDataException(this, ERROR_VALIDATION);
            }
            setValueInfoType(myType);

            /* Look up the Account */
            AccountList myAccounts = myData.getAccounts();
            Account myOwner = myAccounts.findItemById(pAccountId);
            if (myOwner == null) {
                addError(ERROR_UNKNOWN, FIELD_OWNER);
                throw new JMoneyWiseDataException(this, ERROR_VALIDATION);
            }
            setValueOwner(myOwner);

            /* Switch on Info Class */
            switch (myType.getDataType()) {
                case INTEGER:
                    setValueBytes(pValue, Integer.class);
                    if (myType.isLink()) {
                        DataItem myLink = null;
                        switch (myType.getInfoClass()) {
                            case ALIAS:
                            case PARENT:
                            case PORTFOLIO:
                            case HOLDING:
                                myLink = myAccounts.findItemById(getValue(Integer.class));
                                break;
                            case AUTOEXPENSE:
                                myLink = myData.getEventCategories().findItemById(getValue(Integer.class));
                                break;
                            default:
                                break;
                        }
                        if (myLink == null) {
                            addError(ERROR_UNKNOWN, FIELD_LINK);
                            throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
                        }
                        setValueLink(myLink);
                    }
                    break;
                case DATEDAY:
                    setValueBytes(pValue, JDateDay.class);
                    break;
                case CHARARRAY:
                    setValueBytes(pValue, char[].class);
                    break;
                case STRING:
                    setValueBytes(pValue, String.class);
                    break;
                case MONEY:
                    setValueBytes(pValue, JMoney.class);
                    break;
                default:
                    throw new JMoneyWiseLogicException(this, ERROR_BADDATATYPE);
            }

            /* Access the AccountInfoSet and register this data */
            AccountInfoSet mySet = myOwner.getInfoSet();
            mySet.registerInfo(this);
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pInfoType the info type
     * @param pAccount the Account
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private AccountInfo(final AccountInfoList pList,
                        final Integer pId,
                        final AccountInfoType pInfoType,
                        final Account pAccount,
                        final Object pValue) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId, pInfoType, pAccount);

        try {
            /* Set the value */
            setValue(pValue);

            /* Access the AccountInfoSet and register this data */
            AccountInfoSet mySet = pAccount.getInfoSet();
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

        /* Access Accounts and InfoTypes */
        MoneyWiseData myData = getDataSet();
        AccountList myAccounts = myData.getAccounts();
        AccountInfoTypeList myTypes = myData.getActInfoTypes();

        /* Update to use the local copy of the Types */
        AccountInfoType myType = getInfoType();
        AccountInfoType myNewType = myTypes.findItemById(myType.getId());
        if (myNewType == null) {
            addError(ERROR_UNKNOWN, FIELD_INFOTYPE);
            throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
        }
        setValueInfoType(myNewType);

        /* Update to use the local copy of the Accounts */
        Account myAccount = getOwner();
        Account myOwner = myAccounts.findItemById(myAccount.getId());
        if (myOwner == null) {
            addError(ERROR_UNKNOWN, FIELD_OWNER);
            throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
        }
        setValueOwner(myOwner);

        /* If the value is a link */
        if (myType.isLink()) {
            Integer myId = getValue(Integer.class);
            DataItem myNewLink = null;
            switch (myType.getInfoClass()) {
                case ALIAS:
                case PARENT:
                case PORTFOLIO:
                case HOLDING:
                    myNewLink = myAccounts.findItemById(myId);
                    break;
                case AUTOEXPENSE:
                    myNewLink = myData.getEventCategories().findItemById(myId);
                    break;
                default:
                    break;
            }

            /* Check link is valid */
            if (myNewLink == null) {
                addError(ERROR_UNKNOWN, FIELD_LINK);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }

            /* Update link value */
            setValueLink(myNewLink);
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
                return myFormatter.formatObject(getLink(DataItem.class));
            case DATEDAY:
                return myFormatter.formatObject(getValue(JDateDay.class));
            case STRING:
                return myFormatter.formatObject(getValue(String.class));
            case MONEY:
                return myFormatter.formatObject(getValue(JMoney.class));
            case CHARARRAY:
                return myFormatter.formatObject(getValue(char[].class));
            default:
                return null;
        }
    }

    /**
     * Set Value.
     * @param pValue the Value
     * @throws JOceanusException on error
     */
    @Override
    protected void setValue(final Object pValue) throws JOceanusException {
        /* Access the info Type */
        AccountInfoType myType = getInfoType();

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myType.getDataType()) {
            case INTEGER:
                if (myType.isLink()) {
                    if (pValue instanceof String) {
                        DataItem myLink = null;
                        String myName = (String) pValue;
                        MoneyWiseData myData = getDataSet();
                        switch (myType.getInfoClass()) {
                            case ALIAS:
                            case PARENT:
                            case PORTFOLIO:
                            case HOLDING:
                                myLink = myData.getAccounts().findItemByName(myName);
                                break;
                            case AUTOEXPENSE:
                                myLink = myData.getEventCategories().findItemByName(myName);
                                break;
                            default:
                                break;
                        }
                        if (myLink == null) {
                            addError(ERROR_UNKNOWN, FIELD_LINK);
                            throw new JMoneyWiseDataException(this, ERROR_VALIDATION
                                                                    + " "
                                                                    + myName);
                        }
                        setValueValue(myLink.getId());
                        setValueLink(myLink);
                        bValueOK = true;
                    }
                    if (pValue instanceof DataItem) {
                        DataItem myItem = (DataItem) pValue;
                        setValueValue(myItem.getId());
                        setValueLink(myItem);
                        bValueOK = true;
                    }
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
            case STRING:
                if (pValue instanceof String) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                break;
            case MONEY:
                if (pValue instanceof String) {
                    JMoney myValue = myFormatter.parseValue((String) pValue, JMoney.class);
                    setValueValue(myValue);
                    bValueOK = true;
                }
                if (pValue instanceof JMoney) {
                    setValueValue(pValue);
                    bValueOK = true;
                }
                break;
            default:
                break;
        }

        /* Reject invalid value */
        if (!bValueOK) {
            throw new JMoneyWiseDataException(this, ERROR_BADDATATYPE);
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
            extends DataInfoList<AccountInfo, Account, AccountInfoType, AccountInfoClass> {
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
        public AccountInfoList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountInfoList) super.cloneList(pDataSet);
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
         * @param pId the id
         * @param pControlId the control id
         * @param pInfoTypeId the info type id
         * @param pAccountId the account id
         * @param pValue the data
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Integer pInfoTypeId,
                                  final Integer pAccountId,
                                  final byte[] pValue) throws JOceanusException {
            /* Create the info */
            AccountInfo myInfo = new AccountInfo(this, pId, pControlId, pInfoTypeId, pAccountId, pValue);

            /* Check that this DataId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Validate the information */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myInfo);
        }

        @Override
        public void addOpenItem(final Integer pId,
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
                throw new JMoneyWiseDataException(pAccount, ERROR_BADINFOCLASS
                                                            + " ["
                                                            + pInfoClass
                                                            + "]");
            }

            /* Create a new Account Info Type */
            AccountInfo myInfo = new AccountInfo(this, pId, myInfoType, pAccount, pValue);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            append(myInfo);

            /* Validate the Info */
            myInfo.validate();

            /* Handle validation failure */
            if (myInfo.hasErrors()) {
                throw new JMoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }
        }
    }
}
