/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;

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
     * AccountCategory Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField("AccountCategory");

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField("isClosed");

    /**
     * isTaxFree Field Id.
     */
    public static final JDataField FIELD_TAXFREE = FIELD_DEFS.declareEqualityValueField("isTaxFree");

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
     * Obtain Account Category.
     * @return the category
     */
    public AccountCategory getAccountCategory() {
        return getAccountCategory(getValueSet());
    }

    /**
     * Obtain AccountCategoryId.
     * @return the actCategoryId
     */
    public Integer getAccountCategoryId() {
        AccountCategory myCategory = getAccountCategory();
        return (myCategory == null)
                ? null
                : myCategory.getId();
    }

    /**
     * Obtain AccountCategoryName.
     * @return the actCategoryName
     */
    public String getAccountCategoryName() {
        AccountCategory myCategory = getAccountCategory();
        return (myCategory == null)
                ? null
                : myCategory.getName();
    }

    /**
     * Obtain AccountCategoryClass.
     * @return the actCategoryClass
     */
    public AccountCategoryClass getAccountCategoryClass() {
        AccountCategory myCategory = getAccountCategory();
        return (myCategory == null)
                ? null
                : myCategory.getCategoryTypeClass();
    }

    /**
     * Is the account closed.
     * @return true/false
     */
    public Boolean isClosed() {
        return isClosed(getValueSet());
    }

    /**
     * Is the account taxFree.
     * @return true/false
     */
    public Boolean isTaxFree() {
        return isTaxFree(getValueSet());
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
     * Obtain AccountCategory.
     * @param pValueSet the valueSet
     * @return the AccountCategory
     */
    public static AccountCategory getAccountCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, AccountCategory.class);
    }

    /**
     * Is the account closed.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isClosed(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSED, Boolean.class);
    }

    /**
     * Is the account taxFree.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isTaxFree(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXFREE, Boolean.class);
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
     * Set account category value.
     * @param pValue the value
     */
    private void setValueCategory(final AccountCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set account type name.
     * @param pValue the value
     */
    private void setValueCategory(final String pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set close indication.
     * @param pValue the value
     */
    private void setValueClosed(final Boolean pValue) {
        getValueSet().setValue(FIELD_CLOSED, (pValue != null)
                ? pValue
                : Boolean.FALSE);
    }

    /**
     * Set taxFree indication.
     * @param pValue the value
     */
    private void setValueTaxFree(final Boolean pValue) {
        getValueSet().setValue(FIELD_TAXFREE, (pValue != null)
                ? pValue
                : Boolean.FALSE);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
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
     * @param uCategoryId the Account category id
     * @param pDesc the Encrypted Description of the account
     * @param isClosed is the account closed?
     * @param isTaxFree is the account taxFree?
     * @throws JDataException on error
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final Integer uId,
                          final Integer uControlId,
                          final byte[] pName,
                          final Integer uCategoryId,
                          final byte[] pDesc,
                          final Boolean isClosed,
                          final Boolean isTaxFree) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueCategory(uCategoryId);

            /* Set ControlId */
            setControlKey(uControlId);

            /* Set the closed and tax free indications */
            setValueClosed(isClosed);
            setValueTaxFree(isTaxFree);

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
     * @param pCategory the Account category
     * @param pDesc the description
     * @param isClosed is the account closed?
     * @param isTaxFree is the account taxFree?
     * @throws JDataException on error
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final Integer uId,
                          final String sName,
                          final String pCategory,
                          final String pDesc,
                          final Boolean isClosed,
                          final Boolean isTaxFree) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the category */
            setValueCategory(pCategory);

            /* Record the encrypted values */
            setValueName(sName);
            setValueDesc(pDesc);

            /* Set the closed and tax free indications */
            setValueClosed(isClosed);
            setValueTaxFree(isTaxFree);

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

        /* Check the categories */
        int iDiff = Difference.compareObject(getAccountCategory(), pThat.getAccountCategory());
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
    public void resolveDataSetLinks() throws JDataException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        FinanceData myData = getDataSet();
        AccountCategoryList myCategories = myData.getAccountCategories();
        ValueSet myValues = getValueSet();

        /* Adjust Category */
        Object myCategory = myValues.getValue(FIELD_CATEGORY);
        if (myCategory instanceof AccountCategory) {
            myCategory = ((AccountCategory) myCategory).getId();
        }
        if (myCategory instanceof Integer) {
            AccountCategory myCat = myCategories.findItemById((Integer) myCategory);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_CATEGORY);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_VALIDATION);
            }
            setValueCategory(myCat);
        } else if (myCategory instanceof String) {
            AccountCategory myCat = myCategories.findItemByName((String) myCategory);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_CATEGORY);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_VALIDATION);
            }
            setValueCategory(myCat);
        }
    }

    /**
     * Determines whether an account has units.
     * @return priced true/false
     */
    public boolean hasUnits() {
        /* Check for units */
        AccountCategory myCat = getAccountCategory();
        return (myCat != null)
               && (myCat.getCategoryTypeClass().hasUnits());
    }

    /**
     * Determines whether an account has value.
     * @return money true/false
     */
    public boolean hasValue() {
        /* Check for units */
        AccountCategory myCat = getAccountCategory();
        return (myCat != null)
               && (myCat.getCategoryTypeClass().hasValue());
    }

    /**
     * Determines whether an account is a loan.
     * @return debt true/false
     */
    public boolean isLoan() {
        /* Check for units */
        AccountCategory myCat = getAccountCategory();
        return (myCat != null)
               && (myCat.getCategoryTypeClass().isLoan());
    }

    /**
     * Determines whether an account is savings.
     * @return savings true/false
     */
    public boolean isSavings() {
        /* Check for units */
        AccountCategory myCat = getAccountCategory();
        return (myCat != null)
               && (myCat.getCategoryTypeClass().isSavings());
    }

    /**
     * Is this account category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final AccountCategoryClass pClass) {
        /* Check for match */
        return (getAccountCategoryClass() == pClass);
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        AccountCategory myCategory = getAccountCategory();
        String myName = getName();
        String myDesc = getDesc();
        AccountBaseList<?> myList = (AccountBaseList<?>) getList();

        /* AccountCategoryType must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
        }

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* The description must not be too long */
        if ((myDesc != null)
            && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
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
     * Set a new account category.
     * @param pCategory the new category
     */
    public void setAccountCategory(final AccountCategory pCategory) {
        setValueCategory(pCategory);
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
     * Set a new closed indication.
     * @param isClosed the new closed indication
     */
    public void setClosed(final Boolean isClosed) {
        setValueClosed(isClosed);
    }

    /**
     * Set a new taxFree indication.
     * @param isTaxFree the new taxFree indication
     */
    public void setTaxFree(final Boolean isTaxFree) {
        setValueTaxFree(isTaxFree);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying account category */
        getAccountCategory().touchItem(this);
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

        /* Update the account category if required */
        if (!Difference.isEqual(getAccountCategory(), myAccount.getAccountCategory())) {
            setValueCategory(myAccount.getAccountCategory());
        }

        /* Update the closed indication if required */
        if (!Difference.isEqual(isClosed(), myAccount.isClosed())) {
            setValueClosed(myAccount.isClosed());
        }

        /* Update the taxFree indication if required */
        if (!Difference.isEqual(isTaxFree(), myAccount.isTaxFree())) {
            setValueTaxFree(myAccount.isTaxFree());
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
         * Obtain the first account for the specified class.
         * @param pClass the account category class
         * @return the account
         */
        public T getSingularClass(final AccountCategoryClass pClass) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (myCurr.getAccountCategoryClass() == pClass) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }
    }
}
