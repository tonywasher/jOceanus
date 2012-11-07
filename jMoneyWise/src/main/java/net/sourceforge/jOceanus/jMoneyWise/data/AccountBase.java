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
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType.AccountTypeList;

/**
 * Account data type.
 * @author Tony Washer
 */
public abstract class AccountBase
        extends EncryptedItem
        implements Comparable<AccountBase> {
    /**
     * Account Name length.
     */
    public static final int NAMELEN = 30;

    /**
     * Account Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField("Name");

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");

    /**
     * AccountType Field Id.
     */
    public static final JDataField FIELD_TYPE = FIELD_DEFS.declareEqualityValueField("AccountType");

    /**
     * Close Field Id.
     */
    public static final JDataField FIELD_CLOSE = FIELD_DEFS.declareEqualityValueField("CloseDate");

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    /**
     * Obtain Name.
     * @return the name
     */
    public String getName() {
        return getName(getValueSet());
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getNameBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private EncryptedString getNameField() {
        return getNameField(getValueSet());
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private EncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Obtain Account Type.
     * @return the type
     */
    public AccountType getActType() {
        return getAccountType(getValueSet());
    }

    /**
     * Obtain ActTypeId.
     * @return the actTypeId
     */
    public Integer getActTypeId() {
        AccountType myType = getActType();
        return (myType == null) ? null : myType.getId();
    }

    /**
     * Obtain ActTypeName.
     * @return the actTypeName
     */
    public String getActTypeName() {
        AccountType myType = getActType();
        return (myType == null) ? null : myType.getName();
    }

    /**
     * Obtain Order.
     * @return the order
     */
    public int getOrder() {
        return getOrder(getValueSet());
    }

    /**
     * Obtain Close.
     * @return the date
     */
    public JDateDay getClose() {
        return getClose(getValueSet());
    }

    /**
     * Obtain Name.
     * @param pValueSet the valueSet
     * @return the Name
     */
    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Obtain AccountType.
     * @param pValueSet the valueSet
     * @return the AccountType
     */
    public static AccountType getAccountType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TYPE, AccountType.class);
    }

    /**
     * Obtain Order.
     * @param pValueSet the valueSet
     * @return the Order
     */
    public static Integer getOrder(final ValueSet pValueSet) {
        Object myType = pValueSet.getValue(FIELD_TYPE);
        if (myType instanceof AccountType) {
            return ((AccountType) myType).getOrder();
        }
        return null;
    }

    /**
     * Obtain Close date.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static JDateDay getClose(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSE, JDateDay.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueName(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueName(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueDesc(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set account type value.
     * @param pValue the value
     */
    private void setValueType(final AccountType pValue) {
        getValueSet().setValue(FIELD_TYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_TYPE, pValue);
    }

    /**
     * Set close value.
     * @param pValue the value
     */
    private void setValueClose(final JDateDay pValue) {
        getValueSet().setValue(FIELD_CLOSE, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    /**
     * Is the account priced?
     * @return true/false
     */
    public boolean isPriced() {
        return getActType().isPriced();
    }

    /**
     * Is the account the market?
     * @return true/false
     */
    protected boolean isMarket() {
        return getActType().isMarket();
    }

    /**
     * Is the account external?
     * @return true/false
     */
    public boolean isExternal() {
        return getActType().isExternal();
    }

    /**
     * Is the account special?
     * @return true/false
     */
    protected boolean isSpecial() {
        return getActType().isSpecial();
    }

    /**
     * Is the account internal?
     * @return true/false
     */
    protected boolean isInternal() {
        return getActType().isInternal();
    }

    /**
     * Is the account inheritance?
     * @return true/false
     */
    protected boolean isInheritance() {
        return getActType().isInheritance();
    }

    /**
     * Is the account the taxman?
     * @return true/false
     */
    protected boolean isTaxMan() {
        return getActType().isTaxMan();
    }

    /**
     * Is the account money?
     * @return true/false
     */
    public boolean isMoney() {
        return getActType().isMoney();
    }

    /**
     * Is the account cash?
     * @return true/false
     */
    protected boolean isCash() {
        return getActType().isCash();
    }

    /**
     * Is the account writeOff?
     * @return true/false
     */
    protected boolean isWriteOff() {
        return getActType().isWriteOff();
    }

    /**
     * Is the account and endowment?
     * @return true/false
     */
    protected boolean isEndowment() {
        return getActType().isEndowment();
    }

    /**
     * Is the account an owner?
     * @return true/false
     */
    public boolean isOwner() {
        return getActType().isOwner();
    }

    /**
     * Is the account taxFree?
     * @return true/false
     */
    public boolean isTaxFree() {
        return getActType().isTaxFree();
    }

    /**
     * Is the account a UnitTrust?
     * @return true/false
     */
    public boolean isUnitTrust() {
        return getActType().isUnitTrust();
    }

    /**
     * Is the account a debt?
     * @return true/false
     */
    public boolean isDebt() {
        return getActType().isDebt();
    }

    /**
     * Is the account a child?
     * @return true/false
     */
    public boolean isChild() {
        return getActType().isChild();
    }

    /**
     * Is the account a bond?
     * @return true/false
     */
    public boolean isBond() {
        return getActType().isBond();
    }

    /**
     * Is the account benefit?
     * @return true/false
     */
    public boolean isBenefit() {
        return getActType().isBenefit();
    }

    /**
     * Is the account a LifeBond?
     * @return true/false
     */
    public boolean isLifeBond() {
        return getActType().isLifeBond();
    }

    /**
     * Is the account capital?
     * @return true/false
     */
    public boolean isCapital() {
        return getActType().isCapital();
    }

    /**
     * Is the account capitalGains?
     * @return true/false
     */
    public boolean isCapitalGains() {
        return getActType().isCapitalGains();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pAccount The Account to copy
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final AccountBase pAccount) {
        /* Set standard values */
        super(pList, pAccount);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param uId the Account id
     * @param uControlId the control id
     * @param pName the Encrypted Name of the account
     * @param uAcTypeId the Account type id
     * @param pDesc the Encrypted Description of the account
     * @param pClose the Close date for the account
     * @throws JDataException on error
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final Integer uId,
                          final Integer uControlId,
                          final byte[] pName,
                          final Integer uAcTypeId,
                          final byte[] pDesc,
                          final Date pClose) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(uAcTypeId);

            /* Set ControlId */
            setControlKey(uControlId);

            /* Look up the Account Type */
            FinanceData myData = getDataSet();
            AccountTypeList myTypes = myData.getAccountTypes();
            AccountType myActType = myTypes.findItemById(uAcTypeId);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Type Id");
            }
            setValueType(myActType);

            /* Parse the closed date if it exists */
            if (pClose != null) {
                setValueClose(new JDateDay(pClose));
            }

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param uId the id
     * @param sName the Name of the account
     * @param uAcTypeId the Account type id
     * @param pDesc the description
     * @param pClose the Close date for the account
     * @throws JDataException on error
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final Integer uId,
                          final String sName,
                          final Integer uAcTypeId,
                          final String pDesc,
                          final Date pClose) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(uAcTypeId);

            /* Record the encrypted values */
            setValueName(sName);
            setValueDesc(pDesc);

            /* Look up the Account Type */
            FinanceData myData = getDataSet();
            AccountTypeList myTypes = myData.getAccountTypes();
            AccountType myActType = myTypes.findItemById(uAcTypeId);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Type Id");
            }
            setValueType(myActType);

            /* Parse the closed date if it exists */
            if (pClose != null) {
                setValueClose(new JDateDay(pClose));
            }

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public AccountBase(final AccountBaseList<? extends AccountBase> pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final AccountBase pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the order */
        int iDiff = (getOrder() - pThat.getOrder());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the names */
        iDiff = Difference.compareObject(getName(), pThat.getName());
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

        /* Access Account types */
        FinanceData myData = getDataSet();
        AccountTypeList myTypes = myData.getAccountTypes();

        /* Update to use the local copy of the AccountTypes */
        AccountType myType = getActType();
        AccountType myNewType = myTypes.findItemById(myType.getId());
        setValueType(myNewType);
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        AccountType myType = getActType();
        String myName = getName();
        String myDesc = getDesc();
        AccountBaseList<?> myList = (AccountBaseList<?>) getList();

        /* AccountType must be non-null */
        if (myType == null) {
            addError("AccountType must be non-null", FIELD_TYPE);
        } else if (!myType.getEnabled()) {
            addError("AccountType must be enabled", FIELD_TYPE);
        }

        /* Name must be non-null */
        if (myName == null) {
            addError("Name must be non-null", FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError("Name is too long", FIELD_NAME);
            }

            if (myList.countInstances(myName) > 1) {
                addError("Name must be unique", FIELD_NAME);
            }
        }

        /* The description must not be too long */
        if ((myDesc != null)
            && (myDesc.length() > DESCLEN)) {
            addError("Description is too long", FIELD_DESC);
        }
    }

    /**
     * Set a new account name.
     * @param pName the new name
     * @throws JDataException on error
     */
    public void setAccountName(final String pName) throws JDataException {
        setValueName(pName);
    }

    /**
     * Set a new account type.
     * @param pType the new type
     */
    public void setActType(final AccountType pType) {
        setValueType(pType);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JDataException on error
     */
    public void setDescription(final String pDesc) throws JDataException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new close date.
     * @param pDate the new date
     */
    public void setClose(final JDateDay pDate) {
        setValueClose(pDate);
    }

    /**
     * Mark active items.
     */
    protected void markActiveItems() {
        /* mark the account type referred to */
        getActType().touchItem(this);
    }

    /**
     * Update base account from an edited account.
     * @param pAccount the edited account
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pAccount) {
        /* Can only update from an account */
        if (!(pAccount instanceof AccountBase)) {
            return false;
        }

        AccountBase myAccount = (AccountBase) pAccount;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myAccount.getName())) {
            setValueName(myAccount.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myAccount.getDesc())) {
            setValueDesc(myAccount.getDescField());
        }

        /* Update the account type if required */
        if (!Difference.isEqual(getActType(), myAccount.getActType())) {
            setValueType(myAccount.getActType());
        }

        /* Update the close if required */
        if (!Difference.isEqual(getClose(), myAccount.getClose())) {
            setValueClose(myAccount.getClose());
        }
        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Account List class.
     * @param <T> the dataType
     */
    public abstract static class AccountBaseList<T extends AccountBase>
            extends EncryptedList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBaseList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Account list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         */
        protected AccountBaseList(final FinanceData pData,
                                  final Class<T> pClass) {
            super(pClass, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected AccountBaseList(final AccountBaseList<T> pSource) {
            super(pSource);
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The Item if present (or null)
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Search for a particular item by Name.
         * @param pName Name of item
         * @return The Item if present (or null)
         */
        public T findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Get the market account from the list.
         * @return the Market account
         */
        public T getMarket() {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (myCurr.isMarket()) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Get the TaxMan account from the list.
         * @return the TaxMan account
         */
        public T getTaxMan() {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (myCurr.isTaxMan()) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }
    }
}
