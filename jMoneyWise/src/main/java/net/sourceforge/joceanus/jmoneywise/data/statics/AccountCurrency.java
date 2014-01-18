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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Iterator;
import java.util.Locale;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * AccountCurrency data type.
 * @author Tony Washer
 */
public class AccountCurrency
        extends StaticData<AccountCurrency, AccountCurrencyClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountCurrency.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "AccountCurrencies";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Default Field Id.
     */
    public static final JDataField FIELD_DEFAULT = FIELD_DEFS.declareEqualityValueField("Default");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Is this the default currency.
     * @return true/false
     */
    public Boolean isDefault() {
        return isDefault(getValueSet());
    }

    /**
     * Is this the default currency.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isDefault(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEFAULT, Boolean.class);
    }

    /**
     * Set default indication.
     * @param pValue the value
     */
    private void setValueDefault(final Boolean pValue) {
        getValueSet().setValue(FIELD_DEFAULT, (pValue != null)
                ? pValue
                : Boolean.FALSE);
    }

    /**
     * Return the Currency class of the AccountCurrency.
     * @return the class
     */
    public AccountCurrencyClass getCurrencyClass() {
        return super.getStaticClass();
    }

    @Override
    public AccountCurrency getBase() {
        return (AccountCurrency) super.getBase();
    }

    @Override
    public AccountCurrencyList getList() {
        return (AccountCurrencyList) super.getList();
    }

    /**
     * Return the Currency of the AccountCurrency.
     * @return the currency
     */
    public Currency getCurrency() {
        return getCurrencyClass().getCurrency();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pCurrency The Account Currency to copy
     */
    protected AccountCurrency(final AccountCurrencyList pList,
                              final AccountCurrency pCurrency) {
        super(pList, pCurrency);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pName Name of Account Currency
     * @throws JOceanusException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final String pName) throws JOceanusException {
        super(pList, pName);
        setValueDefault(Boolean.FALSE);
        setValueEnabled(Boolean.FALSE);
        setValueDesc(getCurrencyClass().getCurrency().getDisplayName());
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pClass Class of Account Currency
     * @throws JOceanusException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final AccountCurrencyClass pClass) throws JOceanusException {
        super(pList, pClass);
        setValueDefault(Boolean.FALSE);
        setValueEnabled(Boolean.FALSE);
        setValueDesc(pClass.getCurrency().getDisplayName());
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pId the id
     * @param isEnabled is the account currency enabled
     * @param pOrder the sort order
     * @param pName Name of Account Currency
     * @param pDesc Description of Account Currency
     * @param pDefault is this the default currency
     * @throws JOceanusException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final Integer pId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final String pName,
                            final String pDesc,
                            final Boolean pDefault) throws JOceanusException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
        setValueDefault(pDefault);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pId ID of Account Currency
     * @param pControlId the control id of the new item
     * @param isEnabled is the account currency enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of Account Currency
     * @param pDesc Encrypted Description of Account Currency
     * @param pDefault is this the default currency
     * @throws JOceanusException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final Integer pId,
                            final Integer pControlId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final byte[] pName,
                            final byte[] pDesc,
                            final Boolean pDefault) throws JOceanusException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
        setValueDefault(pDefault);
    }

    /**
     * Set default indication.
     * @param pDefault the new indication
     */
    private void setDefault(final Boolean pDefault) {
        setValueDefault(pDefault);
    }

    @Override
    public void validate() {
        AccountCurrencyList myList = getList();

        /* Check that default is non-null */
        if (isDefault() == null) {
            addError(ERROR_MISSING, FIELD_DEFAULT);

            /* else check various things for a default currency */
        } else if (isDefault()) {
            /* Check that default is enabled */
            if (!getEnabled()) {
                addError(ERROR_DISABLED, FIELD_DEFAULT);
            }

            /* Check for multiple defaults */
            if (myList.countDefaults() > 1) {
                addError("Multiple default currencies", FIELD_DEFAULT);
            }
        }

        /* Validate it */
        super.validate();
    }

    @Override
    public boolean applyChanges(final DataItem pData) {
        /* Can only apply changes for AccountCurrency */
        if (!(pData instanceof AccountCurrency)) {
            return false;
        }

        /* Access the data */
        AccountCurrency myData = (AccountCurrency) pData;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myData);

        /* Update the default indication if required */
        if (!isDefault().equals(myData.isDefault())) {
            setDefault(myData.isDefault());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Represents a list of {@link AccountCurrency} objects.
     */
    public static class AccountCurrencyList
            extends StaticList<AccountCurrency, AccountCurrencyClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountCurrency.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<AccountCurrencyClass> getEnumClass() {
            return AccountCurrencyClass.class;
        }

        /**
         * Construct an empty CORE account currency list.
         * @param pData the DataSet for the list
         */
        public AccountCurrencyList(final DataSet<?, ?> pData) {
            super(AccountCurrency.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountCurrencyList(final AccountCurrencyList pSource) {
            super(pSource);
        }

        @Override
        protected AccountCurrencyList getEmptyList(final ListStyle pStyle) {
            AccountCurrencyList myList = new AccountCurrencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountCurrencyList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountCurrencyList) super.cloneList(pDataSet);
        }

        @Override
        public AccountCurrency addCopyItem(final DataItem pItem) {
            /* Can only clone an AccountCurrency */
            if (!(pItem instanceof AccountCurrency)) {
                return null;
            }

            AccountCurrency myCurr = new AccountCurrency(this, (AccountCurrency) pItem);
            add(myCurr);
            return myCurr;
        }

        @Override
        public AccountCurrency addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pCurrency the Name of the account currency
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pCurrency) throws JOceanusException {
            /* Create a new Account Currency */
            AccountCurrency myCurr = new AccountCurrency(this, pCurrency);

            /* Check that this AccountCurrency has not been previously added */
            if (findItemByName(pCurrency) != null) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(myCurr.getId())) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            append(myCurr);

            /* Validate the Currency */
            myCurr.validate();

            /* Handle validation failure */
            if (myCurr.hasErrors()) {
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pId the Id of the account currency
         * @param isEnabled is the account currency enabled
         * @param pOrder the sort order
         * @param pCurrency the Name of the account currency
         * @param pDesc the Description of the account currency
         * @param pDefault is this the default currency
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pCurrency,
                                final String pDesc,
                                final Boolean pDefault) throws JOceanusException {
            /* Create a new Account Currency */
            AccountCurrency myCurr = new AccountCurrency(this, pId, isEnabled, pOrder, pCurrency, pDesc, pDefault);

            /* Check that this AccountCurrencyTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            append(myCurr);

            /* Validate the AccountCurrency */
            myCurr.validate();

            /* Handle validation failure */
            if (myCurr.hasErrors()) {
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pId the Id of the account currency
         * @param pControlId the control id of the new item
         * @param isEnabled is the account currency enabled
         * @param pOrder the sort order
         * @param pCurrency the encrypted Name of the account currency
         * @param pDesc the Encrypted Description of the account currency
         * @param pDefault is this the default currency
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pCurrency,
                                  final byte[] pDesc,
                                  final Boolean pDefault) throws JOceanusException {
            /* Create a new Account Currency */
            AccountCurrency myCurr = new AccountCurrency(this, pId, pControlId, isEnabled, pOrder, pCurrency, pDesc, pDefault);

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(pId)) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }

            /* Add the AccountCurrency to the list */
            append(myCurr);

            /* Validate the AccountCurrency */
            myCurr.validate();

            /* Handle validation failure */
            if (myCurr.hasErrors()) {
                throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (AccountCurrencyClass myClass : AccountCurrencyClass.values()) {
                /* Create new element */
                AccountCurrency myCurr = new AccountCurrency(this, myClass);

                /* Add the AccountCurrency to the list */
                append(myCurr);

                /* Validate the AccountCurrency */
                myCurr.validate();

                /* Handle validation failure */
                if (myCurr.hasErrors()) {
                    throw new JMoneyWiseDataException(myCurr, ERROR_VALIDATION);
                }
            }

            /* Initialise the default currency */
            initialiseDefault();

            /* Ensure that the list is sorted */
            reSort();
        }

        /**
         * Initialise the default currency.
         */
        public void initialiseDefault() {
            /* Determine the locale currency */
            Locale myLocale = Locale.getDefault();
            DecimalFormatSymbols mySymbols = DecimalFormatSymbols.getInstance(myLocale);
            Currency myCurrency = mySymbols.getCurrency();

            /* Find the currency in the list */
            AccountCurrency myCurr = findCurrency(myCurrency);
            if (myCurr == null) {
                /* Default to GBP if local currency not found */
                myCurr = findItemByClass(AccountCurrencyClass.GBP);
            }

            /* If we have a currency */
            if (myCurr != null) {
                /* Set it as the default */
                myCurr.setDefault(Boolean.TRUE);
                myCurr.setValueEnabled(Boolean.TRUE);
            }
        }

        /**
         * find a currency in the list.
         * @param pCurrency the currency to find
         * @return The currency
         */
        public AccountCurrency findCurrency(final Currency pCurrency) {
            /* Access the iterator */
            Iterator<AccountCurrency> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCurrency myCurr = myIterator.next();

                /* If this is a default value */
                if (pCurrency.equals(myCurr.getCurrency())) {
                    /* return the currency */
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Count the number of default currencies.
         * @return The # of default currencies
         */
        protected int countDefaults() {
            /* Access the iterator */
            Iterator<AccountCurrency> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCurrency myCurr = myIterator.next();

                /* If this is a default value */
                if (myCurr.isDefault()) {
                    /* Increment count */
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Find the default currency.
         * @return The default currency
         */
        public AccountCurrency findDefault() {
            /* Access the iterator */
            Iterator<AccountCurrency> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountCurrency myCurr = myIterator.next();

                /* If this is a default value */
                if (myCurr.isDefault()) {
                    /* Return the default */
                    return myCurr;
                }
            }

            /* Return to caller */
            return null;
        }

        /**
         * Set default currency.
         * @param pCurrency the new default currency.
         */
        public void setDefaultCurrency(final AccountCurrency pCurrency) {
            /* Find the default currency */
            AccountCurrency myCurr = findDefault();

            /* If we are changing the currency */
            if (!pCurrency.equals(myCurr)) {
                /* If we have a default value */
                if (myCurr != null) {
                    /* Clear default value */
                    myCurr.pushHistory();
                    myCurr.setDefault(Boolean.FALSE);
                }

                /* Set new currency */
                pCurrency.pushHistory();
                pCurrency.setDefault(Boolean.TRUE);
            }
        }
    }
}
