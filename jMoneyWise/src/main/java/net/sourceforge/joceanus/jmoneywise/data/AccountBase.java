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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountBase.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataAccountName"));

    /**
     * AccountCategory Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClosed"));

    /**
     * isTaxFree Field Id.
     */
    public static final JDataField FIELD_TAXFREE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataTaxFree"));

    /**
     * isGrossInterest Field Id.
     */
    public static final JDataField FIELD_GROSS = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataGross"));

    /**
     * Currency Field Id.
     */
    public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCurrency"));

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
     * Is the account gross interest.
     * @return true/false
     */
    public Boolean isGrossInterest() {
        return isGrossInterest(getValueSet());
    }

    /**
     * Obtain Account Category.
     * @return the category
     */
    public AccountCurrency getAccountCurrency() {
        return getAccountCurrency(getValueSet());
    }

    /**
     * Obtain AccountCategoryId.
     * @return the actCategoryId
     */
    public Integer getAccountCurrencyId() {
        AccountCurrency myCurrency = getAccountCurrency();
        return (myCurrency == null)
                ? null
                : myCurrency.getId();
    }

    /**
     * Obtain AccountCategoryName.
     * @return the actCategoryName
     */
    public String getAccountCurrencyName() {
        AccountCurrency myCurrency = getAccountCurrency();
        return (myCurrency == null)
                ? null
                : myCurrency.getName();
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
     * Is the account grossInterest.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isGrossInterest(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_GROSS, Boolean.class);
    }

    /**
     * Obtain AccountCurrency.
     * @param pValueSet the valueSet
     * @return the AccountCurrency
     */
    public static AccountCurrency getAccountCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AccountCurrency.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueName(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws JOceanusException {
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
     * Set account category value.
     * @param pValue the value
     */
    private void setValueCategory(final AccountCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set account category id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set account category name.
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

    /**
     * Set gross interest indication.
     * @param pValue the value
     */
    private void setValueGrossInterest(final Boolean pValue) {
        getValueSet().setValue(FIELD_GROSS, (pValue != null)
                ? pValue
                : Boolean.FALSE);
    }

    /**
     * Set account currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AccountCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set account currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set account currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public AccountBaseList<?> getList() {
        return (AccountBaseList<?>) super.getList();
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
     * @param isClosed is the account closed?
     * @param isTaxFree is the account taxFree?
     * @param isGross is the account grossInterest?
     * @param uCurrencyId the Account currency id
     * @throws JOceanusException on error
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final Integer uId,
                          final Integer uControlId,
                          final byte[] pName,
                          final Integer uCategoryId,
                          final Boolean isClosed,
                          final Boolean isTaxFree,
                          final Boolean isGross,
                          final Integer uCurrencyId) throws JOceanusException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueCategory(uCategoryId);
            setValueCurrency(uCurrencyId);

            /* Set ControlId */
            setControlKey(uControlId);

            /* Set the flags */
            setValueClosed(isClosed);
            setValueTaxFree(isTaxFree);
            setValueGrossInterest(isGross);

            /* Record the encrypted values */
            setValueName(pName);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param uId the id
     * @param sName the Name of the account
     * @param pCategory the Account category
     * @param isClosed is the account closed?
     * @param isTaxFree is the account taxFree?
     * @param isGross is the account grossInterest?
     * @param pCurrency the Account currency
     * @throws JOceanusException on error
     */
    protected AccountBase(final AccountBaseList<? extends AccountBase> pList,
                          final Integer uId,
                          final String sName,
                          final String pCategory,
                          final Boolean isClosed,
                          final Boolean isTaxFree,
                          final Boolean isGross,
                          final String pCurrency) throws JOceanusException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the category */
            setValueCategory(pCategory);

            /* Store the currency */
            setValueCurrency(pCurrency);

            /* Record the encrypted values */
            setValueName(sName);

            /* Set the closed and tax free indications */
            setValueClosed(isClosed);
            setValueTaxFree(isTaxFree);
            setValueGrossInterest(isGross);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
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
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        AccountCategoryList myCategories = myData.getAccountCategories();
        AccountCurrencyList myCurrencies = myData.getAccountCurrencies();
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
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCategory(myCat);
        } else if (myCategory instanceof String) {
            AccountCategory myCat = myCategories.findItemByName((String) myCategory);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_CATEGORY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCategory(myCat);
            if (myCat.getCategoryTypeClass().isNonAsset()) {
                setValueCurrency((AccountCurrency) null);
            }
        }

        /* Adjust Currency */
        Object myCurrency = myValues.getValue(FIELD_CURRENCY);
        if (myCurrency instanceof AccountCurrency) {
            myCurrency = ((AccountCurrency) myCurrency).getId();
        }
        if (myCurrency instanceof Integer) {
            AccountCurrency myCurr = myCurrencies.findItemById((Integer) myCurrency);
            if (myCurr == null) {
                addError(ERROR_UNKNOWN, FIELD_CURRENCY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCurrency(myCurr);
        } else if (myCurrency instanceof String) {
            AccountCurrency myCurr = myCurrencies.findItemByName((String) myCurrency);
            if (myCurr == null) {
                addError(ERROR_UNKNOWN, FIELD_CURRENCY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCurrency(myCurr);
        }
    }

    /**
     * Determines whether an account is an asset.
     * @return true/false
     */
    public boolean isAsset() {
        /* Check for asset */
        AccountCategory myCat = getAccountCategory();
        return (myCat != null)
               && (myCat.getCategoryTypeClass().isAsset());
    }

    /**
     * Determines whether an account is a non-asset.
     * @return true/false
     */
    public boolean isNonAsset() {
        /* Check for non asset */
        AccountCategory myCat = getAccountCategory();
        return (myCat != null)
               && (myCat.getCategoryTypeClass().isNonAsset());
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
        return getAccountCategoryClass() == pClass;
    }

    /**
     * Obtain detailed category.
     * @param pCategory current category
     * @return detailed category
     */
    public EventCategory getDetailedCategory(final EventCategory pCategory) {
        /* Access category list */
        EventCategoryList myCategories = getDataSet().getEventCategories();

        /* Switch on category type */
        switch (pCategory.getCategoryTypeClass()) {
            case INTEREST:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(EventCategoryClass.TAXFREEINTEREST);
                }
                return myCategories.getSingularClass((isGrossInterest())
                        ? EventCategoryClass.GROSSINTEREST
                        : EventCategoryClass.TAXEDINTEREST);
            case DIVIDEND:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(EventCategoryClass.TAXFREEDIVIDEND);
                }
                return myCategories.getSingularClass(isCategoryClass(AccountCategoryClass.UNITTRUST)
                        ? EventCategoryClass.UNITTRUSTDIVIDEND
                        : EventCategoryClass.SHAREDIVIDEND);
            default:
                return pCategory;
        }
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        AccountCategory myCategory = getAccountCategory();
        AccountCurrency myCurrency = getAccountCurrency();
        String myName = getName();
        AccountBaseList<?> myList = getList();

        /* AccountCategoryType must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
        }

        /* AccountCurrency must be non-null (for valued assets) and enabled */
        if (myCurrency == null) {
            if (hasUnits()
                || hasValue()) {
                addError(ERROR_MISSING, FIELD_CURRENCY);
            }
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
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
    }

    /**
     * Set a new account name.
     * @param pName the new name
     * @throws JOceanusException on error
     */
    public void setAccountName(final String pName) throws JOceanusException {
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

    /**
     * Set a new account currency.
     * @param pCurrency the new currency
     */
    public void setAccountCurrency(final AccountCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying account category */
        getAccountCategory().touchItem(this);

        /* touch the underlying account currency */
        AccountCurrency myCurrency = getAccountCurrency();
        if (myCurrency != null) {
            myCurrency.touchItem(this);
        }
    }

    /**
     * Update base account from an edited account.
     * @param pAccount the edited account
     */
    protected void applyBasicChanges(final AccountBase pAccount) {
        if (!Difference.isEqual(getName(), pAccount.getName())) {
            setValueName(pAccount.getNameField());
        }

        /* Update the account category if required */
        if (!Difference.isEqual(getAccountCategory(), pAccount.getAccountCategory())) {
            setValueCategory(pAccount.getAccountCategory());
        }

        /* Update the closed indication if required */
        if (!Difference.isEqual(isClosed(), pAccount.isClosed())) {
            setValueClosed(pAccount.isClosed());
        }

        /* Update the taxFree indication if required */
        if (!Difference.isEqual(isTaxFree(), pAccount.isTaxFree())) {
            setValueTaxFree(pAccount.isTaxFree());
        }

        /* Update the account currency if required */
        if (!Difference.isEqual(getAccountCurrency(), pAccount.getAccountCurrency())) {
            setValueCurrency(pAccount.getAccountCurrency());
        }
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
        protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Account list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         */
        protected AccountBaseList(final MoneyWiseData pData,
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
